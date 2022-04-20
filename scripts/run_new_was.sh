# run_new_was.sh
#
# !/bin/bash
#
ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)

cp /home/ubuntu/app/inspiration/deploy/.env /home/ubuntu/app/inspiration/source/build/libs/.env
source /home/ubuntu/app/inspiration/deploy/deploy_env.sh

TARGET_PORT=0

echo ${DEPLOYMENT_ACTIVE}
if [ ${DEPLOYMENT_ACTIVE} == "dev" ]; then
  docker-compose -f /home/ubuntu/app/inspiration/source/docker-compose.yml up -d --force-recreate
elif [ ${DEPLOYMENT_ACTIVE} == "prod" ]; then
  docker-compose -f /home/ubuntu/app/inspiration/source/docker-compose.yml up -d
else
  echo "> DEPLOYMENT_ACTIVE is not correct "
fi

exit 0
