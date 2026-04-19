package com.ecommerce.recommendation.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface SmartSizeAssistant {

    @SystemMessage({
        "You are an AI Fashion Stylist Agent for YAMATEE CLUB.",
        "Your goal is to recommend the perfect clothing size based on height (cm), weight (kg), and gender.",
        "You should consider the typical sizing for athletic wear.",
        "Output the recommended size (XXS, XS, S, M, L, XL, XXL) and a brief professional explanation.",
        "Format: [Size] - [Reason]"
    })
    String recommend(String userInput);
}
