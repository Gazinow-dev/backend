# Dockerfile
FROM bellsoft/liberica-openjdk-alpine:17 as builder

WORKDIR /app

# 소스 코드를 복사하여 이미지 내에 추가
COPY . .

# 빌드 수행
RUN ./gradlew build -x test

# 제품용 이미지 생성
FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
