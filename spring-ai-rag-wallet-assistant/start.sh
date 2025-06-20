#!/bin/bash
# Start Elasticsearch and Kibana using Docker Compose

docker-compose up -d

echo "Elasticsearch is running on http://localhost:9200"
echo "Kibana is running on http://localhost:5601"
