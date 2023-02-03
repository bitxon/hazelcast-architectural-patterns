FROM openjdk:17.0.1-jdk-slim
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
EXPOSE 8080/tcp
EXPOSE 5701/tcp
