# run_new_was.sh
#
# !/bin/bash
#
ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
APP_PATH=/home/ubuntu/app/inspiration
cp ${APP_PATH}/deploy/.env ${APP_PATH}/source/build/libs/.env
cp ${APP_PATH}/source/module-web/build/libs/* ${APP_PATH}/source/build/libs

docker-compose --env-file=./build/libs/.env -f ${APP_PATH}/source/docker-compose.yml up -d --force-recreate

exit 0
