package com.ms.spring_ai_rag.util;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class PdfToVectorStoreUtil {
    private PdfToVectorStoreUtil() { /* Utility class: prevent instantiation */ }
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(PdfToVectorStoreUtil.class);

    public static SimpleVectorStore buildVectorStoreFromPdf(Resource resource, EmbeddingModel embeddingModel, int chunkSize, int overlap) {
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        List<Document> documents = new PagePdfDocumentReader(resource).read();
        var data = new TokenTextSplitter().apply(documents); 
        return vectorStore;
    }

    private static List<String> chunkText(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            if (end == text.length()) break;
            start += (chunkSize - overlap);
        }
        return chunks;
    }
}
