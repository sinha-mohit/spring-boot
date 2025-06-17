# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:24-jdk

# Set the working directory
WORKDIR /app

# Copy build files
COPY build/libs/my-spring-boot-project-0.0.1-SNAPSHOT.jar app.jar

# Expose the default port (change if needed)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]
