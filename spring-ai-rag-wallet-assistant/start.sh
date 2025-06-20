#!/bin/bash
# Start Elasticsearch and Kibana using Docker Compose

docker-compose up -d

# Wait for Elasticsearch to be up (simple check, can be improved)
echo "Waiting for Elasticsearch to be available..."
until curl -s http://localhost:9200 >/dev/null; do
  sleep 2
done

# Delete and recreate the index with 1024 dimensions
INDEX_NAME="fri-20-jun"
echo "Deleting existing index (if any): $INDEX_NAME"
curl -s -X DELETE "http://localhost:9200/$INDEX_NAME" > /dev/null

echo "Creating index $INDEX_NAME with 1024-dim dense_vector mapping..."
curl -s -X PUT "http://localhost:9200/$INDEX_NAME" -H 'Content-Type: application/json' -d'
{
  "mappings": {
    "properties": {
      "embedding": {
        "type": "dense_vector",
        "dims": 1024,
        "index": true,
        "similarity": "cosine"
      }
    }
  }
}
' > /dev/null

echo "Index $INDEX_NAME is ready."

echo "Elasticsearch is running on http://localhost:9200"
echo "Kibana is running on http://localhost:5601"

# Clean up Gradle and dependency caches to avoid version conflicts
echo "Cleaning Gradle and dependency caches..."
./gradlew --stop
./gradlew clean
rm -rf ~/.gradle/caches/modules-2/files-2.1/co.elastic.clients/elasticsearch-java
rm -rf ~/.gradle/caches/modules-2/files-2.1/org.elasticsearch.client/elasticsearch-rest-client

# Force dependency refresh and clean build to ensure correct Elasticsearch versions
./gradlew --refresh-dependencies
./gradlew clean build
./gradlew bootRun
