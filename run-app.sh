#!/bin/bash

# Define colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}Starting NearProp application...${NC}"
echo "Using Java version:"
java -version

# First attempt with standard mvn command
echo -e "${GREEN}Attempting to run with 'mvn spring-boot:run'...${NC}"
mvn spring-boot:run

# If the first attempt fails, try with the Maven wrapper
if [ $? -ne 0 ]; then
    echo -e "${RED}Maven command failed, trying with Maven wrapper...${NC}"
    
    # Make sure the Maven wrapper is executable
    chmod +x ./mvnw
    
    echo -e "${GREEN}Running with './mvnw spring-boot:run'...${NC}"
    ./mvnw spring-boot:run
fi

# If we get here, both attempts failed
if [ $? -ne 0 ]; then
    echo -e "${RED}Failed to start the application with both methods.${NC}"
    echo "Please check the error messages above for more information."
    exit 1
fi
