FROM openjdk:17-jdk-slim
COPY target/med-reconciliation-app-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]