package com.ms.spring_ai_rag.controller;

import com.ms.spring_ai_rag.model.ChatResponse;
import com.ms.spring_ai_rag.model.QueryRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ms.spring_ai_rag.service.RAGService;

@RestController
public class RAGController {

    private final RAGService ragService;

    public RAGController(RAGService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/rag")
    public ChatResponse rag(@RequestBody QueryRequest request) {
        return ragService.processQuery(request);
    }

}