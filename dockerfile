FROM openjdk:17.0.1-jdk-slim
WORKDIR /root
COPY ./${PROJECT_NAME}-${VERSION}.jar .

CMD java -jar -Duser.timezone=Asia/Seoul -Dspring.profiles.active=${ACTIVE} ${PROJECT_NAME}-${VERSION}.jar