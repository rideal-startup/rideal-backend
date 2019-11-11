import os
import json
import requests
from pathlib import Path

import click

from api import API

def pprint_dict(instance_name: str, d: dict) -> str:
  del d['_links']
  out = instance_name + '('
  out += ', '.join([f'{k}={v}' for k, v in d.items()])
  out += ')'
  return out


@click.group()
def main():
  pass


@main.command()
@click.option('--data', 
              type=click.Path(dir_okay=False, exists=True),
              help='Path to collection mock data')
@click.option('--force-delete/--no-force-delete', 
              default=False,
              help='If set to true the data of the' 
                   'collection will be removed')
def create_collection(data, force_delete):
  data_path = Path(data)
  data = json.load(data_path.open())

  if force_delete:
    api.delete_all(data_path.stem)

  api.post_all(data_path.stem, data)


@main.command()
@click.argument('collection')
def read_collection(collection):
  res = api.get(collection)['_embedded'][collection]
  res = [pprint_dict(collection, d) for d in res]
  print('\n'.join(res))

if __name__ == "__main__":
  api = API()
  main()