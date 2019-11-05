function getIp() {
  COMPUTE_ENGINE_NAME=$1
  IP=$(gcloud compute instances describe $1 --zone=${ZONE} --format='get(networkInterfaces[0].accessConfigs[0].natIP)')
  echo $IP
}