FROM openjdk:21-jdk-slim as build
LABEL maintainer="whiskels"
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} NotifierBot.jar

CMD java $JAVA_OPTS --enable-preview -jar /NotifierBot.jar
