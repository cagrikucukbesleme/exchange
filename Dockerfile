FROM openjdk:17-jdk-slim

WORKDIR /exchange

COPY target/exchange-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
