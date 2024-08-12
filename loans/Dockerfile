FROM openjdk:17-jdk-slim

LABEL org.opencontainers.image.authors="dkvesic"

COPY target/loans-0.0.1-SNAPSHOT.jar loans-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "loans-0.0.1-SNAPSHOT.jar"]
