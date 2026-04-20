package com.ecommerce.recommendation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class SmartSizeAssistant {

    @Value("${GEMINI_API_KEY:}")
    private String apiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";

    private final RestTemplate restTemplate = new RestTemplate();

    public String recommend(String userInput) {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new RuntimeException("GEMINI_API_KEY is not configured.");
        }

        String systemInstruction = "You are an AI Fashion Stylist Agent for YAMATEE CLUB. " +
                "Your goal is to recommend the perfect clothing size based on height (cm), weight (kg), and gender. " +
                "You should consider the typical sizing for athletic wear. " +
                "Output the recommended size (XXS, XS, S, M, L, XL, XXL) and a brief professional explanation. " +
                "Format: [Size] - [Reason]";

        // Construct Gemini REST Request body
        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", systemInstruction + "\n\nUser Input: " + userInput)
                ))
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_URL + apiKey, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Parse the nested JSON structure: candidates[0].content.parts[0].text
                List candidates = (List) response.getBody().get("candidates");
                Map firstCandidate = (Map) candidates.get(0);
                Map content = (Map) firstCandidate.get("content");
                List parts = (List) content.get("parts");
                Map firstPart = (Map) parts.get(0);
                return (String) firstPart.get("text");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage());
        }

        return "Error generating recommendation";
    }
}
