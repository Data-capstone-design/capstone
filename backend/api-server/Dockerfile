FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

COPY gradle/ gradle/
COPY gradlew .
COPY settings.gradle .
COPY build.gradle .

COPY clients/client-kafka/build.gradle clients/client-kafka/
COPY core/core-api/build.gradle core/core-api/
COPY core/core-enum/build.gradle core/core-enum/
COPY storage/db-mongo/build.gradle storage/db-mongo/

RUN ./gradlew dependencies --no-daemon

COPY clients/client-kafka/src/ clients/client-kafka/src/
COPY core/core-api/src/ core/core-api/src/
COPY core/core-enum/src/ core/core-enum/src/
COPY storage/db-mongo/src/ storage/db-mongo/src/

RUN ./gradlew :core:core-api:bootJar --no-daemon

FROM openjdk:17-jdk-slim AS runtime
WORKDIR /app

COPY --from=builder /app/core/core-api/build/libs/core-api-0.0.1.jar app.jar

EXPOSE 8080

ENV PROFILE=${PROFILE}

CMD ["java", \
      "-jar", \
      "-Dspring.profiles.active=${PROFILE}", \
      "-Duser.timezone=Asia/Seoul", \
      "app.jar"]