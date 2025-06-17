#!/bin/zsh

# Stop and remove all containers and networks created by docker-compose
docker-compose down -v

# Remove all Docker images (use with caution!)
docker rmi -f $(docker images -q) 2>/dev/null

echo "All Docker containers, networks, and images have been removed."
