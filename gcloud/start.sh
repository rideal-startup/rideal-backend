#!/bin/bash

source utils.sh

ZONE="europe-west1-b"

gcloud config set project rideal-startup

# TODO: Validate vars
# TODO: Exit on error

echo "Creating mongo database..."

gcloud compute instances create-with-container mongo-db \
  --container-image mongo:latest \
  --tags=db \
  --zone=$ZONE


MONGO_DB_IP=$(getIp mongo-db)

echo "MONGO DB IP: $MONGO_DB_IP"


echo "Creating Rabbit MQ broker..."

gcloud compute instances create-with-container rabbit-mq-broker \
  --container-image rabbitmq:management-alpine \
    --container-env=RABBITMQ_DEFAULT_USER=user,RABBITMQ_DEFAULT_PASS=pass \
  --tags=db \
  --zone=$ZONE

RABBIT_IP=$(getIp rabbit-mq-broker)

echo "RABBIT MQ IP: $RABBIT_IP"

