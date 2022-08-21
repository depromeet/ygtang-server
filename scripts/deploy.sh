#/bin/bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
APP_PATH=/home/ubuntu/app/inspiration
SCRIPT_PATH=/home/ubuntu/app/inspiration/scripts

cp ${APP_PATH}/deploy/.env ${APP_PATH}/source/.env
export $(cat ${APP_PATH}/source/.env | grep -v ^# | xargs) >/dev/null

cd ${SCRIPT_PATH}

$SCRIPT_PATH/login_ecr.sh

docker stop ${ACTIVE}
sleep 3

docker-compose -f ${APP_PATH}/source/docker-compose.yml pull
sleep 1
docker-compose -f ${APP_PATH}/source/docker-compose.yml up -d
sleep 10


if docker ps | grep redis
then
    echo redis process is running.
else
    docker container run -d -t -p 6379:6379 redis
    echo redis process is started.
fi