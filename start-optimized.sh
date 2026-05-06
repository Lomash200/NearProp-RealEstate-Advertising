#!/bin/bash

# NearProp Optimized Startup Script
# This script starts the application with all performance optimizations

echo "Starting NearProp with performance optimizations..."

# JVM Performance Tuning Flags
JVM_OPTS="-XX:+UseG1GC"
JVM_OPTS="$JVM_OPTS -Xms2g -Xmx2g"
JVM_OPTS="$JVM_OPTS -XX:MaxGCPauseMillis=200"
JVM_OPTS="$JVM_OPTS -XX:+UseStringDeduplication"
JVM_OPTS="$JVM_OPTS -XX:+OptimizeStringConcat"
JVM_OPTS="$JVM_OPTS -XX:+UseCompressedOops"
JVM_OPTS="$JVM_OPTS -XX:+UseCompressedClassPointers"
JVM_OPTS="$JVM_OPTS -XX:+UnlockExperimentalVMOptions"
# JVM_OPTS="$JVM_OPTS -XX:+UseZGC"   # Removed to avoid multiple GC error
JVM_OPTS="$JVM_OPTS -XX:+UnlockDiagnosticVMOptions"
JVM_OPTS="$JVM_OPTS -XX:+LogVMOutput"
JVM_OPTS="$JVM_OPTS -XX:LogFile=gc.log"

# Application Properties
APP_OPTS="--spring.profiles.active=prod"
APP_OPTS="$APP_OPTS --server.port=8080"
APP_OPTS="$APP_OPTS --spring.redis.host=localhost"
APP_OPTS="$APP_OPTS --spring.redis.port=6379"

# Check if Redis is running
echo "Checking Redis connection..."
if ! redis-cli ping > /dev/null 2>&1; then
    echo "Warning: Redis is not running. Starting Redis..."
    sudo systemctl start redis-server
    sleep 2
fi

# Check Redis connection again
if redis-cli ping > /dev/null 2>&1; then
    echo "✓ Redis is running"
else
    echo "✗ Redis is not available. Application will use local caching only."
fi

# Start the application
echo "Starting application with JVM options: $JVM_OPTS"
echo "Application options: $APP_OPTS"

java $JVM_OPTS -jar target/*.jar $APP_OPTS 