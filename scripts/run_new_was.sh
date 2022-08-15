# run_new_was.sh
#
# !/bin/bash
#
ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
APP_PATH=/home/ubuntu/app/inspiration

cp ${APP_PATH}/deploy/.env ${APP_PATH}/source/.env
cp ${APP_PATH}/source/module-web/build/libs/* ${APP_PATH}/source

export $(cat ${APP_PATH}/source/.env | grep -v ^# | xargs) >/dev/null

docker stop ${ACTIVE}
sleep 3
docker-compose -f ${APP_PATH}/source/docker-compose.yml up -d --force-recreate
sleep 10


if docker ps | grep redis
then
    echo redis process is running.
else
    docker container run -d -t -p 6379:6379 redis
    echo redis process is started.
fi

exit 0
