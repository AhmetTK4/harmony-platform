FROM gradle:8.14.3-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

FROM amazoncorretto:21
WORKDIR /app
COPY --from=builder /app/build/libs/app.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
