import requests
import json

import click

import utm

import api


def _to_lat_lng(x, y, zone=31, letter='T'):
  return utm.to_latlon(x, y, zone, letter)


def _create_line(l):
  city = rideal_api.findBy('name', 'Lleida', 'lines')
  city = city['id']
  
  body = dict(
    name=l['name'],
    city='/cities/' + city,
    onFreeDays=l.get('availableOnFreeDays', True),
    length=0,
    journeyMeanTime=0,
    available=True, 
    stops=l['stops']
  )
  rideal_api.create('lines', body)


def _mutate_stop(s, order):
  lat, lng = _to_lat_lng(s['location']['lat'],
                         s['location']['lng'])
  s['location']['lat'] = lat
  s['location']['lng'] = lng
  s['order'] = order
  return s

@click.argument('lines_path', 
                help='Path to file containing lines data',
                type=click.Path(exists=True, dir_okay=False))
def main(lines_path):
  rideal_api.delete_collection('lines')

  lines = json.load(open(lines_path, encoding='utf-8'))

  for l in lines:
    l['stops'] = [_mutate_stop(s, i) 
                  for i, s in enumerate(l['stops'], start=1)]
    _create_line(l)


if __name__ == "__main__":
    rideal_api = api.API()
    main()