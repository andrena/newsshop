# Stage 1: Build der gesamten Anwendung mit Gradle (Java 21)
FROM gradle:jdk21 AS build
WORKDIR /app
COPY . .
# Hier wird sowohl Frontend als auch Backend gebaut
RUN gradle bootJar --no-daemon

# Stage 2: Finales Runtime-Image mit Java 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Kopieren des gebauten JARs (enthält bereits die Frontend-Assets)
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
