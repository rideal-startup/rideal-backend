import requests


class API(object):
  def __init__(self, 
               base_url: str = 'http://localhost:8080/api/',
               user: str = 'admin',
               password: str = None):
    self.base_url = base_url
    self.user = user
    self.password = password

  def _validate_response(self, res):
    assert res.status_code < 300, res.json()

  def delete_collection(self, collection: str):
    # Get existing instances ids
    res = requests.get(self.base_url + collection)
    self._validate_response(res)
    instances = res.json()
    instances = [r['id'] for r in instances['_embedded'][collection]]
  
    for i in instances:
      url = f'{self.base_url}{collection}/{i}'
      res = requests.delete(url)
      self._validate_response(res)
  
  def create(self, collection: str, body: dict):
    res = requests.post(self.base_url + collection, json=body)
    self._validate_response(res)
  
  def findBy(field: str, 
             query: str,
             collection:str):
    u_field = field[0].upper() + field[1:]
    url = f'{self.base_url}{collection}'
          f'/search/findBy{u_field}/?{field}={query}'
    res = requests.get(url)
    self._validate_response(res)
    return res.json()

