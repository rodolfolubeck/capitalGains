FROM eclipse-temurin:23-jdk

WORKDIR /app

COPY target/capitalGainsNu-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
