# Spring AI RAG

This project is a Spring Boot application demonstrating Retrieval-Augmented Generation (RAG) using AI models. It provides REST APIs to interact with AI-powered chat and document retrieval features.

**Note:** The Llama 3.2 model is running locally and used for AI responses.

**This project works as a PDF personal AI assistant, allowing you to query and chat with your own documents.**

## Features
- REST API for querying AI models
- Retrieval of information from documents (e.g., PDFs)
- Example endpoints for chat and query

## Project Structure
- `src/main/java/com/ms/spring_ai_rag/` - Main application code
  - `controller/` - REST controllers
  - `model/` - Data models
- `src/main/resources/data/` - Sample documents (e.g., `sample.pdf`)
- `src/test/java/com/ms/spring_ai_rag/` - Test cases

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Build and Run

```
./mvnw clean install
./mvnw spring-boot:run
```

The application will start on [http://localhost:8080](http://localhost:8080).

### API Endpoints
- `/api/query` - POST endpoint for submitting queries
- `/api/chat` - POST endpoint for chat interactions

### Example cURL Request

To query the PDF personal AI assistant, use the following example:

```bash
curl --location 'http://localhost:8080/rag' \
--header 'Content-Type: application/json' \
--data '{
    "query":"what does mohit do in samsung?"
}'
```

## Configuration
Edit `src/main/resources/application.properties` to configure application settings.

## License
This project is licensed under the MIT License.
