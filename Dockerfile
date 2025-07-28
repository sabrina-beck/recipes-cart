FROM gradle:8.14-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle :app:bootJar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder app/app/build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
