# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application's JAR file into the container at /app
# Assuming your build output is a JAR named 'your-app.jar' in the 'target' directory
COPY target/inventory-0.0.1.jar /app/inventory-0.0.1.jar
COPY pom.xml /app/pom.xml


# Expose the port your application listens on
EXPOSE 8084

# Define the command to run your application
ENTRYPOINT ["java", "-jar", "/app/inventory-0.0.1.jar"]

# You can also add additional commands like:
# For a more robust production image, consider a multi-stage build:
#
# Stage 1: Build the application
# FROM maven:3.8.7-openjdk-17 AS build
# WORKDIR /app
# COPY pom.xml .
# COPY src ./src
# RUN mvn clean package -DskipTests
#
# Stage 2: Create the final runtime image
# FROM openjdk:17-jdk-slim
# WORKDIR /app
# COPY --from=build /app/target/*.jar /app/your-app.jar
# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "/app/your-app.jar"]