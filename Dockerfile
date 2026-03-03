# ---------- BUILD STAGE ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy entire repo (multi-module Maven safe)
COPY . .

# Build only the requested module
ARG SERVICE_NAME
RUN mvn -q -pl ${SERVICE_NAME} -am -DskipTests package

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

ARG SERVICE_NAME
COPY --from=build /workspace/${SERVICE_NAME}/target/*.jar app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]


