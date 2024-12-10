FROM amazoncorretto:21-alpine-jdk
VOLUME /tmp
COPY target/client-0.0.1-SNAPSHOT.jar client-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "client-0.0.1-SNAPSHOT.jar"]

EXPOSE 8080
