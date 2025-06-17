#!/bin/zsh

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
  echo "Docker is not installed or not in your PATH. Please install Docker Desktop for Mac."
  exit 1
fi

# Load .env variables for use in this script
set -o allexport
source .env
set +o allexport

# Build the Spring Boot JAR, skipping tests
if ! ./gradlew clean build -x test; then
  echo "Gradle build failed. Exiting."
  exit 1
fi

# Remove any existing containers and images for a clean start
docker rm -f my-spring-boot-project mysql-db 2>/dev/null
docker rmi -f my-spring-boot-project 2>/dev/null

# Build and start all services with Docker Compose
if ! docker-compose up --build -d; then
  echo "Docker Compose up failed. Exiting."
  exit 1
fi

echo "Spring Boot app and MySQL are running in Docker."
echo "App:     http://localhost:8080"
echo "MySQL:   localhost:3309 (user: $MYSQL_USER, password: $MYSQL_PASSWORD)"
echo "To stop the services, run: docker-compose down -v"
echo "To remove all containers and images, run: ./clean-docker.sh"
echo "Kibana:  http://localhost:5601"
echo "Elasticsearch: http://localhost:9200"
echo "Logstash (host port): localhost:15000 (internal: 5000, for Filebeat)"
