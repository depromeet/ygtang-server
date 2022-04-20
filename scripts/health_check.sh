#!/bin/bash

source /home/ubuntu/app/inspiration/deploy/deploy_env.sh
# Crawl current connected port of WAS
TARGET_URL=localhost
echo ${DEPLOYMENT_ACTIVE}


echo "> Start health check of WAS at '${TARGET_URL}' ..."

for RETRY_COUNT in 1 2 3 4 5 6 7 8 9 10
do
  echo "> #${RETRY_COUNT} trying..."
  RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" ${TARGET_URL}/health)

  if [ ${RESPONSE_CODE} -eq 200 ]; then
    echo "> New WAS successfully running"
    exit 0
  elif [ ${RETRY_COUNT} -eq 10 ]; then
    echo "> Health check failed."
    exit 1
  fi
  sleep 10
done
