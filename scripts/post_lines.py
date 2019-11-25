import json

import utm
import click

import api


def _to_lat_lng(x, y, zone=31, letter='T'):
  return utm.to_latlon(x, y, zone, letter)


def _create_line(l):
  
  body = dict(
    name=l['name'],
    company=l['company'],
    color=l['color'],
    city=l['city'],
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

@click.command()
@click.argument('lines_path',
                type=click.Path(exists=True, dir_okay=False))
@click.option('--company',
              default='Moventis')
@click.option('--city',
              default='Lleida')
def main(lines_path, company, city):
  print('Deleting existing lines...')
  rideal_api.delete_collection('lines')

  print('Reading JSON file...')
  lines = json.load(open(lines_path, encoding='utf-8'))
  city = rideal_api.findBy('name', city, 'cities')
  company = rideal_api.findBy('username', company, 'companies')

  print('Creating lines...')
  for l in lines:
    l['city'] = '/cities/' + city['id']
    l['company'] = '/companies/' + company['id']
    l['stops'] = [_mutate_stop(s, i) 
                  for i, s in enumerate(l['stops'], start=1)]
    _create_line(l)
  
  print('Done')


if __name__ == "__main__":
    rideal_api = api.API()
    main()