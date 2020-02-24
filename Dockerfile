FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ./*.properties ./
COPY ./*.xlsx ./
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar","--spring.config.location=./calcws.properties"]