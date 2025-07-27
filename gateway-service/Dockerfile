FROM gradle:8.4-jdk17 as builder
WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon

FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/build/libs/app.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
