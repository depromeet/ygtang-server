FROM openjdk:17.0.1-jdk-slim
WORKDIR /root
COPY ./build/libs/${project_name}-${version}.jar .

CMD java -jar -Duser.timezone=Asia/Seoul -Dspring.profiles.active=${active} ${project_name}-${version}.jar