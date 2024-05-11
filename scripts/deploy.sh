#!/bin/bash
APP_PATH=/home/ubuntu/app/inspiration
SCRIPT_PATH=/home/ubuntu/app/inspiration/source/scripts

cp ${APP_PATH}/deploy/.env ${APP_PATH}/source/.env
export $(cat ${APP_PATH}/source/.env | grep -v ^# | xargs) >/dev/null

cd ${SCRIPT_PATH}
chmod +x login_ecr.sh

$SCRIPT_PATH/login_ecr.sh ${AWS_CLI_REGION} ${AWS_CLI_ACCOUNT_ID}

docker stop ${ACTIVE}
sleep 3

docker-compose -f ${APP_PATH}/source/docker-compose.yml pull
sleep 1
docker-compose -f ${APP_PATH}/source/docker-compose.yml up
sleep 10
