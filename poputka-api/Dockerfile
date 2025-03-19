# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-23-alpine AS build

WORKDIR /usr/app

# Copy the project files
COPY pom.xml .
COPY src ./src

# Build the project
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:23-alpine

# Set the location of the jar file
ARG JAR_FILE=/usr/app/target/*.jar

# Copy the built jar file to the runner location
COPY --from=build $JAR_FILE /app/poputka-api.jar

# Expose the application port inside the container
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/poputka-api.jar"]