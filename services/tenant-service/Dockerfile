## ==========================================
## FILE: services/tenant-service/Dockerfile
## ==========================================
#
### ---------- BUILD STAGE ----------
##FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
##WORKDIR /build
##
### 1️⃣ Copy ROOT parent pom
##COPY pom.xml .
##
### 2️⃣ Copy all services (reactor)
##COPY services ./services
##
### 3️⃣ Build ONLY tenant-service (parent auto-built)
##RUN mvn -B \
##    -pl services/tenant-service \
##    -am \
##    clean package \
##    -DskipTests
#
#FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
#WORKDIR /build
#
## Disable Maven HTTP keep-alive (fixes TAG MISMATCH)
#ENV MAVEN_OPTS="-Dmaven.wagon.httpconnectionManager.ttlSeconds=25 \
#                -Dmaven.wagon.http.retryHandler.count=3 \
#                -Dhttps.protocols=TLSv1.2"
#
## Copy full repo (IMPORTANT for multi-module)
#COPY maven-settings.xml /root/.m2/settings.xml
#COPY . .
#
## Force fresh dependency download
#RUN mvn -B \
#    -Dmaven.repo.local=/tmp/m2 \
#    -Dmaven.wagon.http.ssl.insecure=true \
#    -Dmaven.wagon.http.ssl.allowall=true \
#    -DskipTests \
#    clean package
#
#
## ---------- RUNTIME STAGE ----------
#FROM eclipse-temurin:21-jre-jammy
#WORKDIR /app
#
#RUN useradd -ms /bin/bash spring
#USER spring
#
## 4️⃣ Copy the correct jar
#COPY --from=build \
#  /build/services/tenant-service/target/*jar \
#  app.jar
#
#EXPOSE 8081
#
#ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
#
#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
# =============================
# TENANT SERVICE (RUNTIME ONLY)
# =============================
#FROM eclipse-temurin:21-jre-jammy
#
#WORKDIR /app
#
## Create non-root user
#RUN useradd -ms /bin/bash spring
#USER spring
#
## Copy pre-built jar from host
##COPY services/tenant-service/target/*.jar app.jar
#COPY target/*.jar app.jar
#
## Expose service port
#EXPOSE 8081
#
## JVM tuning (optional)
#ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
#
## Run application
##ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
#ENTRYPOINT ["java","-jar","/app/app.jar"]

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/tenant-service.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]


