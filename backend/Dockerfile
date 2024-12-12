FROM amazoncorretto:21-alpine AS build
WORKDIR /app

# Устанавливаем Maven из репозиториев Alpine
RUN apk update && apk add maven

COPY pom.xml .
# Загрузка зависимостей для оффлайн-режима
RUN mvn dependency:go-offline

COPY src/ ./src/
RUN mvn package -DskipTests

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/target/backend-1.0-SNAPSHOT.jar /app/backend.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app/backend.jar"]
