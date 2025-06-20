# Samsung Wallet Partner AskWallet Chatbot

This project is an onboarding assistant chatbot for Samsung Wallet partners. The chatbot helps partners with easy onboarding, provides information about related APIs, and offers documentation support.

---

## Prerequisites

- **Docker** must be installed and running on your system. [Download Docker Desktop](https://www.docker.com/products/docker-desktop/)
- **Ollama** (or another local Llama server) must be installed and running for local Llama 3.2 support. [Get Ollama](https://ollama.com/)
- **Java 17+** and **Gradle** (the project uses the Gradle wrapper, so no manual install is needed)

---

## Quick Start: One Command Setup

To start everything (Elasticsearch, Kibana, index setup, and the Spring Boot app) run:

```sh
./start.sh
```

This script will:
- Start Elasticsearch and Kibana using Docker Compose
- Wait for Elasticsearch to be available
- Delete and recreate the index `fri-20-jun` with a 1024-dimensional dense vector mapping (for Llama 3.2 embeddings)
- Clean build and run the Spring Boot application

- **Elasticsearch**: [http://localhost:9200](http://localhost:9200)
- **Kibana**: [http://localhost:5601](http://localhost:5601)

---

## Details: Running Each Component

### 1. Elasticsearch and Kibana

- The `start.sh` script uses Docker Compose to start both services.
- If you need to stop them:
  ```sh
  docker-compose down
  ```
- If you see container name conflicts, run:
  ```sh
  docker stop elasticsearch kibana
  docker rm elasticsearch kibana
  ```

### 2. Ollama (Llama 3.2 Embeddings)

1. Install Ollama from [https://ollama.com/](https://ollama.com/).
2. Start the Ollama service:
   ```sh
   ollama serve
   ```
3. Pull the Llama 3 model:
   ```sh
   ollama pull llama3.2
   ```
4. Run the Llama 3 model:
   ```sh
   ollama run llama3.2
   ```
5. The Spring Boot app is configured to use the local Llama 3.2 model via Ollama at `http://localhost:11434`.

---

## Troubleshooting

- **Dimension mismatch error:**
  - If you see an error like:
    > The [dense_vector] field [embedding] ... has a different number of dimensions [1024] than defined in the mapping [1536]
  - The index mapping and embedding model output dimensions do not match.
  - The `start.sh` script will always recreate the index with 1024 dimensions to match Llama 3.2.

- **Index not updating:**
  - Make sure to stop the app, delete the index, and restart using `./start.sh`.

- **Container name conflict:**
  - Stop and remove containers as shown above.

---

## Project Overview

- **Purpose:**
  - Assist Samsung Wallet partners with onboarding.
  - Provide a chat interface to answer questions about APIs and documentation.
- **Tech Stack:**
  - Spring Boot, Spring AI, Elasticsearch, Kibana, Ollama (Llama 3.2)
- **Index:**
  - Name: `fri-20-jun`
  - Vector dimension: 1024 (for Llama 3.2)

---

## Security Note

- If you set `xpack.security.enabled=false` in your `docker-compose.yml` for Elasticsearch, **no password is required** for accessing Elasticsearch or Kibana. Anyone can access these services on the exposed ports.
- If you set `xpack.security.enabled=true`, you must provide usernames and passwords (using environment variables/macros as shown in the `.env` file and referenced in `docker-compose.yml`).

---

For any issues, please ensure Docker and Ollama are running and the required ports are available. If you change embedding models, update the index mapping accordingly.
