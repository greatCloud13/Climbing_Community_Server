# 1단계: 빌드 스테이지 (Gradle을 사용하여 JAR 파일 생성)
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# 그래들 설정 파일을 먼저 복사하여 종속성 캐싱 활용
COPY build.gradle settings.gradle /app/
COPY src /app/src

# gradlew 실행 권한 부여 및 빌드 (테스트는 제외하여 빌드 속도 향상)
RUN gradle clean build -x test

# 2단계: 실행 스테이지 (최종 이미지 생성)
FROM eclipse-temurin:21-jdk
WORKDIR /app

# 1단계(build)에서 생성된 jar 파일만 가져오기
COPY --from=build /app/build/libs/ClimbingWith-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]