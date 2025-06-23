package com.ms.spring_ai_rag_wallet_assisstant;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RagService {

    private final ChatClient chatClient;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String ES_URL = "http://localhost:9200/fri-20-jun/_search";
    private static final String OLLAMA_EMBEDDING_URL = "http://localhost:11434/api/embeddings";

    @Autowired
    public RagService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Calls the Ollama embedding API to get the embedding vector for a given text.
     */
    // Make this method public so it can be used by RagController
    public List<Float> getEmbeddingFromOllama(String text) {
        try {
            // Prepare request JSON
            String requestJson = objectMapper.createObjectNode()
                .put("model", "nomic-embed-text")
                .put("prompt", text)
                .toString();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            String response = restTemplate.postForObject(OLLAMA_EMBEDDING_URL, entity, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode embeddingNode = root.path("embedding");
            if (embeddingNode.isArray()) {
                List<Float> embedding = new java.util.ArrayList<>();
                for (JsonNode v : embeddingNode) {
                    embedding.add((float) v.asDouble());
                }
                return embedding;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return java.util.Collections.emptyList();
    }

    public void ingestPDF(String pdfPath) {
        List<Document> data = new PagePdfDocumentReader(pdfPath).read();
        data = new TokenTextSplitter().apply(data);
        // REST-based ingestion for each chunk
        for (Document doc : data) {
            String text = doc.getText();
            List<Float> embedding = getEmbeddingFromOllama(text);
            try {
                String json = objectMapper.createObjectNode()
                    .put("text", text)
                    .set("embedding", objectMapper.valueToTree(embedding))
                    .toString();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> entity = new HttpEntity<>(json, headers);
                restTemplate.postForObject("http://localhost:9200/fri-20-jun/_doc", entity, String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String queryLLM(String question, List<Float> queryVector) throws Exception {
        String queryJson = "{\n" +
                "  \"size\": 3,\n" +
                "  \"query\": {\n" +
                "    \"script_score\": {\n" +
                "      \"query\": { \"match_all\": {} },\n" +
                "      \"script\": {\n" +
                "        \"source\": \"cosineSimilarity(params.query_vector, 'embedding') + 1.0\",\n" +
                "        \"params\": { \"query_vector\": " + objectMapper.writeValueAsString(queryVector) + " }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(queryJson, headers);
        String response = restTemplate.postForObject(ES_URL, entity, String.class);

        JsonNode root = objectMapper.readTree(response);
        String context = root.path("hits").path("hits").findValues("_source").stream()
                .map(node -> node.path("text").asText())
                .collect(Collectors.joining(", "));

        String prompt = """
        You are a helpful assistant that helps users with their wallet-related queries. You have access to a knowledge base of PDF documents that contain information about various wallets, including their features, security measures, and how to use them effectively.
        When a user asks a question, you will search through the knowledge base to find relevant information and provide a concise answer. If the information is not available in the knowledge base, you will inform the user that you cannot answer their question at this time.
        If the user asks about a specific wallet, you will provide information about that wallet, including its features, security measures, and how to use it effectively. If the user asks a general question about wallets, you will provide a general overview of wallets, including their purpose, types, and how to use them effectively.
        If the user asks about a specific feature or security measure, you will provide information about that feature or security measure, including how it works and why it is important.

        Use the information from the DOCUMENTS section to provide accurate answers to the question in the QUESTION section.
        If Unsure, simply respond with "I am not sure about that, please check the documents for more information."
        DOCUMENTS:
        """ + context +
        """
        QUESTION:""" + question;

        String answer = chatClient.prompt().user(prompt).call().content();
        return answer;
    }
}