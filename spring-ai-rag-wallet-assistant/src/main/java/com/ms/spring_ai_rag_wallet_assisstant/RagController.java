package com.ms.spring_ai_rag_wallet_assisstant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag")
public class RagController {
    
    private final RagService ragService;
    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/ingestPDF")
    public ResponseEntity ingestPDF(String pdfPath) {
        try {
            ragService.ingestPDF(pdfPath);
            return ResponseEntity.ok("PDF ingested successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error ingesting PDF: " + e.getMessage());
        }
        
    }

    @PostMapping("/query")
    public ResponseEntity query(String question) {
        try {
            String answer = ragService.queryLLM(question);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error querying: " + e.getMessage());
        }
    }
}
