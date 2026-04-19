package com.ecommerce.recommendation.controller;

import com.ecommerce.recommendation.service.SizeRecommenderService;
import com.ecommerce.recommendation.service.SmartSizeAssistant;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final SizeRecommenderService sizeService;
    private final SmartSizeAssistant smartAssistant;

    @GetMapping("/size")
    public Map<String, String> getSizeRecommendation(
            @RequestParam double height,
            @RequestParam double weight,
            @RequestParam String gender) {
        
        String recommendedSize = sizeService.recommendSize(height, weight, gender);
        
        return Map.of(
            "height", String.valueOf(height),
            "weight", String.valueOf(weight),
            "gender", gender,
            "recommendedSize", recommendedSize,
            "message", "Standard calculation"
        );
    }

    @GetMapping("/ai-size")
    public Map<String, String> getAiSizeRecommendation(
            @RequestParam double height,
            @RequestParam double weight,
            @RequestParam String gender) {
        
        String prompt = String.format("Height: %.1f cm, Weight: %.1f kg, Gender: %s", height, weight, gender);
        
        try {
            String recommendation = smartAssistant.recommend(prompt);
            return Map.of(
                "input", prompt,
                "recommendation", recommendation,
                "status", "AI_POWERED"
            );
        } catch (Exception e) {
            // Fallback to standard rule-based calculation
            String fallbackSize = sizeService.recommendSize(height, weight, gender);
            return Map.of(
                "input", prompt,
                "recommendation", String.format("%s - (Live AI is currently busy, using standard calculation)", fallbackSize),
                "status", "FALLBACK_MODE"
            );
        }
    }
}
