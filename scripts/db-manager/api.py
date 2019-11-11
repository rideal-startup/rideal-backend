import requests
from typing import List


class API(object):
  URL = 'http://localhost:8080/api/'
  USER = 'admin'
  PASSWORD = '1234'

  def __init__(self, 
               host: str = None,
               user: str = None, 
               password: str = None):
    self.user = user or API.USER
    self.password = password or API.PASSWORD
    self.host = host or API.URL

  def get(self, path: str) -> dict:
    res = requests.get(self.host + path)
    return res.json()

  def post(self, path: str, body: dict):
    pass

  def post_all(self, path: str, body: List[dict]):
    responses = [requests.post(self.host + path, json=b) 
                 for b in body]
    if any([r.status_code > 300 for r in responses]):
      print("Some request have failed")
      print([r.content for r in responses])

  def delete_all(self, path: str):
    instances = self.get(path)
    responses = []
    for i in instances['_embedded'][path]:
      url = self.host + path + '/' + i['id']
      print(url)
      res = requests.delete(url)
      responses.append(res)
    
    if any([r.status_code > 300 for r in responses]):
      print("Some request have failed")
