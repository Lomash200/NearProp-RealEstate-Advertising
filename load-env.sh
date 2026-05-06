#!/bin/bash
# Script to load environment variables from secrets.env

if [ -f "secrets.env" ]; then
  echo "Loading environment variables from secrets.env..."
  export $(grep -v '^#' secrets.env | xargs)
  echo "Environment variables loaded successfully."
else
  echo "Error: secrets.env file not found."
  exit 1
fi

# Print confirmation of key variables (without showing full values)
echo "Database configuration loaded: ${DB_URL}"
echo "AWS configuration loaded: ${AWS_REGION}"
echo "JWT Secret loaded: ${JWT_SECRET:0:10}..."
echo "SMTP configuration loaded: ${SMTP_HOST}"
echo "Razorpay configuration loaded: ${RAZORPAY_KEY:0:10}..."

# Run the application with the loaded environment variables
echo "You can now run your application with these environment variables."
echo "Example: ./mvnw spring-boot:run" 