#!/bin/bash

source utils.sh

ZONE="europe-west1-b"

gcloud config set project rideal-startup

echo "Creating mongo database..."

gcloud compute instances create-with-container mongo-db \
  --container-image mongo:latest \
  --tags=db \
  --zone=$ZONE

MONGO_DB_IP=$(getIp mongo-db)

echo "MONGO DB IP: $MONGO_DB_IP"