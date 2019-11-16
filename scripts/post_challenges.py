import json

import utm
import click

import api


@click.command()
@click.argument('challenges_path',
                type=click.Path(exists=True, dir_okay=False))
@click.option('--company',
              default='Moventis')
@click.option('--city',
              default='Lleida')
def main(challenges_path, company, city):
  print('Delete existing challenges...')
  rideal_api.delete_collection('challenges')
  
  print('Reading JSON file...')
  challenges = json.load(open(challenges_path, encoding='utf-8'))
  city = rideal_api.findBy('name', city, 'cities')['id']
  company = rideal_api.findBy('username', company, 'companies')['id']

  for c in challenges:
    c['company'] = '/companies/' + company
    c['city'] = '/cities/' + city
    rideal_api.create('challenges', c) 


if __name__ == "__main__":
    rideal_api = api.API()
    main()

