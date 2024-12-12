FROM maven:3.9.2-amazoncorretto-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ ./src/
RUN mvn package -DskipTests

FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY --from=build /app/target/backend-1.0-SNAPSHOT.jar /app/backend.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app/backend.jar"]
