#!/bin/bash

# Crawl current connected port of WAS
TARGET_URL=localhost
APP_PATH=/home/ubuntu/app/inspiration

grep -v '^#' ${APP_PATH}/source/build/libs/.env
set -o allexport
source ${APP_PATH}/source/build/libs/.env
set +o allexport

echo "> Start health check of WAS at '${TARGET_URL}:${PORT}' ..."


for RETRY_COUNT in 1 2 3 4 5 6 7 8 9 10
do
  echo "> #${RETRY_COUNT} trying..."
  RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" ${TARGET_URL}:"${PORT}"/health)

  if [ ${RESPONSE_CODE} -eq 200 ]; then
    echo "> New WAS successfully running"
    exit 0
  elif [ ${RETRY_COUNT} -eq 10 ]; then
    echo "> Health check failed."
    exit 0
  fi
  sleep 10
done
