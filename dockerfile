FROM openjdk:17.0.1-jdk-slim
WORKDIR /root
COPY ./build/libs/module-web-0.0.1-SNAPSHOT.jar .

CMD java -jar -Duser.timezone=Asia/Seoul -Dspring.config.location=file:.env -Dspring.profiles.active=${active} inspiration-0.0.1-SNAPSHOT.jar