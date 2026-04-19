package com.ecommerce.recommendation.service;

import org.springframework.stereotype.Service;

@Service
public class SizeRecommenderService {

    /**
     * AI-based Size Recommendation Logic (Mocked with rule-base for stability)
     * Factors: BMI (Body Mass Index) + Height + Gender
     */
    public String recommendSize(double height, double weight, String gender) {
        double bmi = weight / ((height / 100) * (height / 100));
        
        if (gender.equalsIgnoreCase("male")) {
            if (bmi < 18.5) return "S";
            if (bmi < 23) return "M";
            if (bmi < 26) return "L";
            if (bmi < 30) return "XL";
            return "XXL";
        } else {
            if (bmi < 17.5) return "XS";
            if (bmi < 21) return "S";
            if (bmi < 24) return "M";
            if (bmi < 28) return "L";
            return "XL";
        }
    }
}
