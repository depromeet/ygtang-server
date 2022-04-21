# run_new_was.sh
#
# !/bin/bash
#
ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
APP_PATH = /home/ubuntu/app/inspiration
cp ${APP_PATH}/deploy/.env ${APP_PATH}/source/.env
cp ${APP_PATH}/source/module-web/build/libs/* ${APP_PATH}/source/
source ${APP_PATH}/deploy/deploy_env.sh

TARGET_PORT=0

echo ${DEPLOYMENT_ACTIVE}
if [ ${DEPLOYMENT_ACTIVE} == "dev" ]; then
  docker-compose -f ${APP_PATH}/source/docker-compose.yml up -d --force-recreate
elif [ ${DEPLOYMENT_ACTIVE} == "prod" ]; then
  docker-compose -f ${APP_PATH}/source/docker-compose.yml up -d
else
  echo "> DEPLOYMENT_ACTIVE is not correct "
fi

exit 0
