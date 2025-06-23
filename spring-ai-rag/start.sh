#!/bin/bash
# start.sh - Build and run the Spring AI RAG application, ensuring Ollama Llama 3.2 is running

set -e

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | awk -F'[\"_]' 'NR==1{print $2}')
REQUIRED_VERSION=17
if [[ "${JAVA_VERSION%%.*}" -lt "$REQUIRED_VERSION" ]]; then
  echo "Java 17 or higher is required. Current version: $JAVA_VERSION"
  exit 1
fi

# Check Maven wrapper
if [ ! -f ./mvnw ]; then
  echo "Maven wrapper (./mvnw) not found. Please ensure you are in the project root."
  exit 1
fi

# Ensure Ollama server is running on port 11434
if ! lsof -i:11434 | grep LISTEN > /dev/null; then
  echo "Ollama server is not running. Starting ollama serve in the background..."
  ollama serve &
  OLLAMA_SERVE_PID=$!
  sleep 5
  # Check if the process is still running
  if ! ps -p $OLLAMA_SERVE_PID > /dev/null; then
    echo "Failed to start ollama serve. Exiting."
    exit 1
  fi
else
  echo "Ollama server is already running."
fi

# Check if the llama3.2:latest model is available, pull if not
if ! ollama list | grep -q "llama3.2"; then
  echo "Llama 3.2 model not found. Pulling llama3.2:latest..."
  ollama pull llama3.2:latest
fi

# Build the project without running tests only if --fresh is passed as an argument
if [[ "$*" == *--fresh* ]]; then
  echo "--fresh flag detected: Building the project without running tests..."
  ./mvnw clean install -DskipTests
else
  echo "Skipping build (no --fresh flag)."
fi

# Start the Spring Boot application
./mvnw spring-boot:run
