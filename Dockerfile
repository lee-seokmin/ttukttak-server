# CI에서 ./gradlew bootJar로 만든 jar를 담아요.
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app app
COPY build/libs/app.jar app.jar
USER app
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
