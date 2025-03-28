FROM openjdk:21
WORKDIR /app
COPY target/hotel-reservation-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]