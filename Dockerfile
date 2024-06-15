FROM bellsoft/liberica-openjdk-alpine:17 as builder

WORKDIR /app
COPY . .

RUN ./gradlew build -x test

FROM bellsoft/liberica-openjdk-alpine:17
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
