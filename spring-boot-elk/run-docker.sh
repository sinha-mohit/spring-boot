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
# docker rm -f my-spring-boot-project mysql-db 2>/dev/null
# docker rmi -f my-spring-boot-project 2>/dev/null

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

echo "\n---\n"
echo "Setting up and starting the Python Kafka consumer service..."

# Check for Python 3.12
if ! command -v python3.12 &> /dev/null; then
  echo "Python 3.12 is not installed. Please install Python 3.12 to run the Kafka consumer."
  exit 1
fi

cd python-services || { echo "python-services directory not found!"; exit 1; }

# Set up virtual environment if not already present
if [ ! -d "wlt-log-env" ]; then
  python3.12 -m venv wlt-log-env
fi
source wlt-log-env/bin/activate

# Install requirements
pip install --upgrade pip > /dev/null
pip install -r requirements.txt > /dev/null

# Start the consumer in the background
nohup python kafka_log_consumer.py > kafka_consumer.log 2>&1 &
CONSUMER_PID=$!
echo "Python Kafka consumer started in background (PID: $CONSUMER_PID). Logs: python-services/kafka_consumer.log"


echo "---"
echo "To stop the Python Kafka consumer, run: pkill -f kafka_log_consumer.py"
echo "To view consumer logs: tail -f python-services/kafka_consumer.log"
