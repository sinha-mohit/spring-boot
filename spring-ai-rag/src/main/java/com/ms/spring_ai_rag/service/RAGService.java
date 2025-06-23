package com.ms.spring_ai_rag.service;

import com.ms.spring_ai_rag.model.ChatResponse;
import com.ms.spring_ai_rag.model.QueryRequest;

public interface RAGService {
    ChatResponse processQuery(QueryRequest request);
}
