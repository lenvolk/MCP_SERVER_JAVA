# Use Eclipse Temurin JDK 17
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy the JAR file
COPY target/mcp-server-java-1.0.0.jar app.jar

# Expose port 8080
EXPOSE 8080

# Run in HTTP mode
ENTRYPOINT ["java", "-jar", "app.jar", "--http"]
