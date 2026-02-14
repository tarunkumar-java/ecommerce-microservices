# Use the official OpenJDK 17 image from Docker Hub
FROM eclipse-temurin:21-jdk

# Set working directory inside the container
WORKDIR /app

# Copy the compiled Java application JAR file into the container
COPY target/*.jar app.jar

# Expose the port the Spring Boot application will run on
EXPOSE 8081

# Command to run the application
CMD ["java", "-jar", "app.jar"]
