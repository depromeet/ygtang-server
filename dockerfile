FROM openjdk:17.0.1-jdk-slim
WORKDIR /root
COPY ./build/libs/${project_name}-${version}.jar .
COPY ./build/libs/.env .

CMD java -jar -Duser.timezone=Asia/Seoul -Dspring.config.location=/root/build/libs/.env -Dspring.profiles.active=${active} ${project_name}-${version}.jar