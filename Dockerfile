#FROM openjdk:17-jdk-slim
#
#WORKDIR /app
#
#COPY target/*.jar payments.jar
#
#EXPOSE 10000
#
#ENTRYPOINT ["java", "-jar", "payments.jar"]


FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]