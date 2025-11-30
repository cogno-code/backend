FROM eclipse-temurin:24-jdk AS runtime
WORKDIR /app

# Gradle 빌드 결과 jar 복사 (파일명은 네 프로젝트에 맞게 조정 가능)
COPY build/libs/*.jar app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
