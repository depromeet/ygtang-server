# run_new_was.sh
#
# !/bin/bash
#
ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
APP_PATH=/home/ubuntu/app/inspiration

cp ${APP_PATH}/deploy/.env ${APP_PATH}/source/.env
cp ${APP_PATH}/source/module-batch/build/libs/* ${APP_PATH}/source

export $(cat ${APP_PATH}/source/.env | grep -v ^# | xargs) >/dev/null

ps=$(ps -ef | grep ${BATCH_PROJECT_NAME})
echo process info: ${ps}

get_pid=$(echo ${ps} | cut -d " " -f2)


if [ -n "${get_pid}" ]
then
    result=$(kill -9 ${get_pid})
    echo process is killed.
else
    echo running process not found.
fi

nohup java -jar -Duser.timezone=Asia/Seoul ${APP_PATH}/source/${BATCH_PROJECT_NAME}-${VERSION}.jar > ${APP_PATH}/log/${BATCH_PROJECT_NAME}.log 2>&1 &
exit 0
