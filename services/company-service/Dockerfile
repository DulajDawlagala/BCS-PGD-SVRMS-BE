FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/company-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java","-jar","app.jar"]
