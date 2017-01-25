import json
from unittest import TestCase

import requests


class TestGrabble(TestCase):
    token = ""
    url = "http://127.0.0.1:5000/"

    def setUp(self):
        tok = requests.post(url=self.url + "user/login", data={
            "username": "nlfox@msn.cn",
            "password": "g1vnd3"
        }).text
        self.token = json.loads(tok)["message"]

    def post(self, url, data):
        return requests.post(self.url + url + "?token=" + self.token, data=data).text

    def get(self,url):
        return requests.get(self.url + url + "?token=" + self.token).text

    def test_addLetter(self):
        print self.post("letter/add",data={
            "letter":'b',
            "point":'Point 121'
        })

    def test_gettop(self):
        print self.get('scoreboard')

    def test_t(self):
        word = list("abcdefg")
        for i in xrange(7):
            self.post("letter/add",data={
            "letter":word[i],
            "point":'Point 1'+str(i)
        })
        print self.post("letter/word",data={"word":"abcdefg"})
        print self.get("user/point")
        print self.get("info")

