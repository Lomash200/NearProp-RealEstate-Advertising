#!/bin/bash

echo "Stopping any running application..."
pkill -f "java -jar"

echo "Compiling the application..."
./mvnw clean package -DskipTests

echo "Starting the application with improved logging..."
java -jar target/*.jar --logging.level.com.nearprop.security=DEBUG --logging.level.org.springframework.security=DEBUG &

echo "Application restarting in background. Check logs with: tail -f app.log"
