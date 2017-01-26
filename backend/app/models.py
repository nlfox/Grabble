import json

__author__ = 'nlfox'
from app import db
from flask_migrate import Migrate


class BaseModel(db.Model):
    __tablename__ = 'base'
    __abstract__ = True

    def delete(self):
        db.session.delete(self)
        db.session.commit()

    def save(self):
        db.session.add(self)
        db.session.commit()


class User(BaseModel):
    __tablename__ = 'user'
    id = db.Column(db.Integer, index=True, primary_key=True, autoincrement=True)
    username = db.Column(db.String(60), index=True, unique=True)
    password = db.Column(db.String(40), index=True)
    letters = db.relationship("Letter", back_populates="user")
    tokens = db.relationship("Token", back_populates="user")
    score = db.Column(db.Integer, default=0)
    count = db.Column(db.Integer, default=1)
    distance = db.Column(db.Integer, default=1)
    collected = db.relationship("Collected", back_populates="user")

    def get_letters(self):
        return {i.type: i.count for i in self.letters}

    def get_collected(self):
        return {i.name: True for i in self.collected}

    def get_top(self, n):
        return [[i.username, str(i.score)] for i in db.session.query(User).order_by(User.score.desc()).limit(n)]

    def get_info(self):
        res = {
            'letter': self.get_letters(),
            'collected': self.get_collected(),
            'count': self.count,
            'distance': self.distance

        }
        return json.dumps(res)

    def is_authenticated(self):
        return True

    def get_id(self):
        return unicode(self.id)

    def __repr__(self):
        return '<User %r>' % self.username


class Token(BaseModel):
    __tablename__ = 'token'
    # id = db.Column(db.Integer, index=True, primary_key=True, autoincrement=True)
    token = db.Column(db.String(120), index=True, primary_key=True, unique=True)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    user = db.relationship("User", back_populates="tokens")

    def __repr__(self):
        return '<Token %r>' % self.token


class Letter(BaseModel):
    __tablename__ = 'letter'
    id = db.Column(db.Integer, index=True, primary_key=True, autoincrement=True)
    type = db.Column(db.String)
    count = db.Column(db.Integer)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    user = db.relationship("User", back_populates="letters")

    @staticmethod
    def getLetter(letter, user):
        return Letter.query.filter_by(
            user_id=user.id,
            type=letter
        ).first()


class Collected(BaseModel):
    __tablename__ = 'collected'
    id = db.Column(db.Integer, index=True, primary_key=True, autoincrement=True)
    name = db.Column(db.String)
    user_id = db.Column(db.Integer, db.ForeignKey('user.id'))
    user = db.relationship("User", back_populates="collected")

    @staticmethod
    def getPoint(point, user):
        return Collected.query.filter_by(
            user_id=user.id,
            name=point
        ).first()


class Achievement(BaseModel):
    __tablename__ = "achievements"
    id = db.Column(db.Integer, index=True, primary_key=True, autoincrement=True)

