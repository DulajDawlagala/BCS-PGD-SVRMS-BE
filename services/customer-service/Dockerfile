# ===============================
# CUSTOMER SERVICE (RUNTIME ONLY)
# ===============================
#FROM eclipse-temurin:21-jre-jammy
#
#WORKDIR /app
#
## Create non-root user
#RUN useradd -ms /bin/bash spring
#USER spring
#
## Copy pre-built jar from host
##COPY services/customer-service/target/*.jar app.jar
#COPY target/*.jar app.jar
#
## Expose service port
#EXPOSE 8082
#
## JVM tuning (optional)
#ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"
#
## Run application
##ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
#ENTRYPOINT ["java","-jar","/app/app.jar"]

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/customer-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","app.jar"]


