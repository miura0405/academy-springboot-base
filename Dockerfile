FROM eclipse-temurin:21

WORKDIR /app

COPY ./spring-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
