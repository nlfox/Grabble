# coding=utf-8
__author__ = 'nlfox'
import json
import string
import time
from functools import wraps
from hashlib import md5

from flask import request

from app import app
from models import User, Token, Letter, Collected

points = {'a': 3,
           'b': 20,
           'c': 13,
           'd': 10,
           'e': 1,
           'f': 15,
           'g': 18,
           'h': 9,
           'i': 5,
           'j': 25,
           'k': 22,
           'l': 11,
           'm': 14,
           'n': 6,
           'o': 4,
           'p': 19,
           'q': 24,
           'r': 8,
           's': 7,
           't': 2,
           'u': 12,
           'v': 21,
           'w': 17,
           'x': 23,
           'y': 16,
           'z': 26}


def token_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        inputToken = request.args.get('token', '')
        token = Token.query.filter_by(token=inputToken).first()
        if not token:
            return error('token error')
        return f(token.user, *args, **kwargs)

    return decorated_function


def error(message='sth wrong'):
    errorMsg = {
        'code': 0,
        'message': message
    }
    return json.dumps(errorMsg, ensure_ascii=False)


def info(message='ok'):
    infoMsg = {
        'code': 1,
        'message': message
    }
    return json.dumps(infoMsg, ensure_ascii=False)


def custom_info(message='ok', code=1):
    infoMsg = {
        'code': code,
        'message': message
    }
    return json.dumps(infoMsg, ensure_ascii=False)


@app.route('/test1', methods=['GET'])
def test1():
    return "OK"


@app.route('/test', methods=['GET'])
@token_required
def test(user):
    return json.dumps(user.letters)


@app.route('/letter/add', methods=['POST'])
@token_required
def addLetter(user):
    user.count += 1
    user.save()
    letter = request.form['letter']
    point = request.form['point']
    l = Letter.getLetter(letter, user)
    c = Collected.getPoint(point, user)
    if l:
        if c:
            return error("Already collected")
        else:
            l.count += 1
            l.save()
    else:
        newLetter = Letter()
        newLetter.type = letter
        newLetter.user_id = user.id
        newLetter.count = 1
        newLetter.save()
    newCollected = Collected()
    newCollected.user_id = user.id
    newCollected.name = point
    newCollected.save()
    return info("successful")


@app.route('/letter/word', methods=['POST'])
@token_required
def makeWord(user):
    word = list(request.form["word"])
    point = 0
    for i in xrange(7):
        c = word[i]
        l = Letter.getLetter(c, user)
        l.count -= 1
        if l.count < 0:
            return error("Don't have enough letter.")
        l.save()
        point += points[c]
    user.score += point
    user.save()
    return info()


@app.route('/info', methods=["GET"])
@token_required
def getInfo(user):
    return user.get_info()


@app.route('/scoreboard', methods=["GET"])
@token_required
def getScore(user):
    return json.dumps(user.get_top(10))


@app.route('/user/register', methods=['GET', 'POST'])
def register():
    if request.method == 'POST':
        psdmd5 = md5(request.form['password'])
        password = psdmd5.hexdigest()
        u = User(username=request.form['username'],
                 password=password)
        u.save()
    return error('not POST')


@app.route('/user/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        psdmd5 = md5(request.form['password'])
        password = psdmd5.hexdigest()
        user = User.query.filter_by(
            username=username
        ).first()
        if user:
            if user.password == password:
                tokenText = md5(password + str(time.time())).hexdigest()
                token = Token(user_id=user.id, token=tokenText)
                token.save()
                return info(tokenText)
            else:
                return custom_info("Password is wrong", 2)
        else:
            return error('user not exist')
    return error('not POST')


@app.route('/user/point', methods=['GET'])
@token_required
def getPoint(user):
    return info(str(user.score))
