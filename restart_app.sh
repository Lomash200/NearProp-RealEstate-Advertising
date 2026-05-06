#!/bin/bash

# Kill any running Spring Boot application
echo "Stopping any running Spring Boot application..."
pkill -f "spring-boot:run" || true

# Wait a moment for the process to terminate
sleep 2

# Start the application
echo "Starting Spring Boot application..."
mvn spring-boot:run &

# Wait a moment for the application to start
sleep 5

# Check if the application is running
if pgrep -f "spring-boot:run" > /dev/null; then
    echo "Application started successfully!"
    echo "You can access it at: http://localhost:8080/api"
else
    echo "Failed to start the application. Check the logs for errors."
fi 