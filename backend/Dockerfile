FROM amazoncorretto:21-alpine-jdk

VOLUME /tmp

COPY target/backend-1.0-SNAPSHOT.jar backend-1.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/backend-1.0-SNAPSHOT.jar"]

EXPOSE 9090