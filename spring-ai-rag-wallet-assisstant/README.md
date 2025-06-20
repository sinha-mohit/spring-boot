# Samsung Wallet Partner AskWallet Chatbot

This project is an onboarding assistant chatbot for Samsung Wallet partners. The chatbot helps partners with easy onboarding, provides information about related APIs, and offers documentation support.

## Prerequisites

- **Docker** must be installed and running on your system. [Download Docker Desktop](https://www.docker.com/products/docker-desktop/)
- **Ollama** (or another local Llama server) must be installed and running for local Llama 3.2 support. [Get Ollama](https://ollama.com/)

## Running Elasticsearch and Kibana

This project uses Elasticsearch and Kibana for search and analytics. To start these services:

1. Ensure Docker is running.
2. Run the following command in the project directory:

   ```sh
   ./start.sh
   ```

- **Elasticsearch** will be available at: http://localhost:9200
- **Kibana** will be available at: http://localhost:5601

### Security Note

- If you set `xpack.security.enabled=false` in your `docker-compose.yml` for Elasticsearch, **no password is required** for accessing Elasticsearch or Kibana. Anyone can access these services on the exposed ports.
- If you set `xpack.security.enabled=true`, you must provide usernames and passwords (using environment variables/macros as shown in the `.env` file and referenced in `docker-compose.yml`).

## Using Local Llama 3.2 (via Ollama)

1. Install Ollama from [https://ollama.com/](https://ollama.com/).
2. Start the Ollama service (if not already running):
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

## Project Overview

- **Purpose:**
  - Assist Samsung Wallet partners with onboarding.
  - Provide a chat interface to answer questions about APIs and documentation.

---

For any issues, please ensure Docker and Ollama are running and the required ports are available.
