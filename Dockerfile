FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar payments.jar

EXPOSE 10000

ENTRYPOINT ["java", "-jar", "payments.jar"]
