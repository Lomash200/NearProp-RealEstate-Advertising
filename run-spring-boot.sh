#!/bin/bash

# Stop any running Java processes
pkill -f "spring-boot:run" || true
sleep 2

# Clean and compile the project
echo "Cleaning and compiling the project..."
./mvnw clean compile

# Run the Spring Boot application with additional debug logging
echo "Starting Spring Boot application..."
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dlogging.level.com.nearprop=DEBUG -Dlogging.level.org.springframework.security=DEBUG"

# The application will run in the foreground
