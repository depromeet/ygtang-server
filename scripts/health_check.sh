#!/bin/bash

# Crawl current connected port of WAS
TARGET_URL=localhost
APP_PATH=/home/ubuntu/app/inspiration

export $(cat ${APP_PATH}/source/.env | grep -v ^# | xargs) >/dev/null

echo "> Start health check of WAS at '${TARGET_URL}:${PORT}' ..."

for RETRY_COUNT in 1 2 3 4 5 6 7 8 9 10; do
  echo "> #${RETRY_COUNT} trying..."
  RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" ${TARGET_URL}:"${PORT}"/health)

  if [ ${RESPONSE_CODE} -eq 200 ]; then
    echo "> New WAS successfully running"
    exit 0
  elif [ ${RETRY_COUNT} -eq 10 ]; then
    echo "> Health check failed."
    exit 1
  fi
  sleep 10
done
