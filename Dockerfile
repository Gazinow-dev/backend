FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /app

# Gradle 빌드 결과물인 JAR 파일을 복사
COPY build/libs/*.jar app.jar

# 8080 포트 오픈
EXPOSE 8080
EXPOSE 5005
# JAR 파일 실행
ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "app.jar"]
