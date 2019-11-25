import os
from pymongo import MongoClient

user = os.environ['USERNAME'].strip()
passw = os.environ['PASSWORD'].strip()
uri = f'mongodb://{user}:{passw}@ds149596.mlab.com:49596/rideal?retryWrites=false'
client = MongoClient(uri)
client.drop_database('rideal')