#!/bin/bash

ZONE="europe-west1-b"

gcloud config set project rideal-startup

gcloud compute instances delete mongo-db \
  --quiet --zone=$ZONE
