FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/vidyasetu-api-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", \
  "-Xmx512m", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]
