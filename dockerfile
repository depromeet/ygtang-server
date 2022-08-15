FROM openjdk:17.0.1-jdk-slim
WORKDIR /root

CMD java -jar ${JAVA_OPTS} -Duser.timezone=Asia/Seoul -Dspring.profiles.active=${ACTIVE} ${PROJECT_NAME}-${VERSION}.jar