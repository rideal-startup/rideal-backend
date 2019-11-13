import requests
import json

import utm


def _to_lat_lng(x, y, zone=31, letter='T'):
  return utm.to_latlon(x, y, zone, letter)


def _get_stop(stop_name):
  res = requests.get(
      'http://localhost:8080/api/stops/'
      'search/findbyName?name=' + stop_name)
  if res.status_code == 200:
    return res.json()
  return None


def _create_line(l):
  body = dict(
    name=l['name'],
    city='/cities/1'
    availableOnFreeDays=l.get('availableOnFreeDays', True),
    lengthKm=0,
    journeyMeanTime=0,
    available=True, 
    stops=l['stops']
  )
  res = requests.post('http://localhost:8080/api/lines', json=body)
  assert res.status_code < 300
  return res.json()['id']


def _mutate_stop(s, order):
  lat, lng = _to_lat_lng(s['location']['lat'],
                         s['location']['lng'])
  s['location']['lat'] = lat
  s['location']['lng'] = lng
  s['order'] = order
  return s


def _delete_all(collection: str):
  instances = requests.get('http://localhost:8080/api/' + collection)
  instances = instances.json()
  responses = []

  for i in instances['_embedded'][collection]:
    url = API_URL + collection + '/' + i['id']
    res = requests.delete(url)
    responses.append(res)
  
  if any([r.status_code > 300 for r in responses]):
    print("Some request have failed")


_delete_all('stops')
_delete_all('lines')

lines = json.load(open('data/mock/lines.json', encoding='utf-8'))

for l in lines:
  l['stops'] = [_mutate_stop(s, i) 
                for i, s in enumerate(l['stops'], start=1)]
  _create_line(l)
