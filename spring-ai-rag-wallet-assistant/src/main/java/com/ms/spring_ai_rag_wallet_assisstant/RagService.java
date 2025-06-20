package com.ms.spring_ai_rag_wallet_assisstant;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
// import co.elastic.clients.elasticsearch.ingest.simulate.Document;
import org.springframework.ai.document.Document;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RagService {
    private final String promt = """
        You are a helpful assistant that helps users with their wallet-related queries. You have access to a knowledge base of PDF documents that contain information about various wallets, including their features, security measures, and how to use them effectively.
        When a user asks a question, you will search through the knowledge base to find relevant information and provide a concise answer. If the information is not available in the knowledge base, you will inform the user that you cannot answer their question at this time.
        If the user asks about a specific wallet, you will provide information about that wallet, including its features, security measures, and how to use it effectively. If the user asks a general question about wallets, you will provide a general overview of wallets, including their purpose, types, and how to use them effectively.
        If the user asks about a specific feature or security measure, you will provide information about that feature or security measure, including how it works and why it is important.

        Use the information from the DOCUMENTS section to procide accurate answers to the question in the QUESTION section.
        If Unsure, simple respond with "I am not sure about that, please check the documents for more information."
        DOCUMENTS:
        %s
        QUESTION: %s
        """;

    @Autowired
    private ElasticsearchVectorStore vectorStore;
    @Autowired
    private ChatClient chatClient;

    public void ingestPDF(String pdfPath) {
        List<Document> data = new PagePdfDocumentReader(pdfPath).read();

        data = new TokenTextSplitter().apply(data);
        vectorStore.doAdd(data);
    }

    public String queryLLM(String question) {
        SearchRequest searchRequest = SearchRequest.builder().query(question).topK(5).similarityThreshold(.75).build();
        List<Document> relevantDocument = vectorStore.doSimilaritySearch(searchRequest);

        String context = relevantDocument.stream()
                .map(Document::getText)
                .collect(Collectors.joining(","));


        String promt = """
        You are a helpful assistant that helps users with their wallet-related queries. You have access to a knowledge base of PDF documents that contain information about various wallets, including their features, security measures, and how to use them effectively.
        When a user asks a question, you will search through the knowledge base to find relevant information and provide a concise answer. If the information is not available in the knowledge base, you will inform the user that you cannot answer their question at this time.
        If the user asks about a specific wallet, you will provide information about that wallet, including its features, security measures, and how to use it effectively. If the user asks a general question about wallets, you will provide a general overview of wallets, including their purpose, types, and how to use them effectively.
        If the user asks about a specific feature or security measure, you will provide information about that feature or security measure, including how it works and why it is important.

        Use the information from the DOCUMENTS section to procide accurate answers to the question in the QUESTION section.
        If Unsure, simple respond with "I am not sure about that, please check the documents for more information."
        DOCUMENTS:
        """ + context +
        """
        QUESTION:""" + question;

        String answer = chatClient.prompt().user(promt).call().content();

        return answer + " Found at page " + relevantDocument.get(0).getMetadata().get(PagePdfDocumentReader.METADATA_START_PAGE_NUMBER) + " of the PDF document.";
    }
}