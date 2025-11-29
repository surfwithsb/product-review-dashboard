package com.cimba.backend.service;

import com.cimba.backend.model.Review;
import com.cimba.backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;



@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // --------- BULK SAVE ----------
    public List<Review> saveBulk(List<String> texts) {
        return texts.stream().map(this::analyzeAndSaveReview).toList();
    }

    // --------- ANALYZE + SAVE ----------
    public Review analyzeAndSaveReview(String text) {

        String prompt = """
               Analyze the following product review.
               Return STRICT JSON ONLY with EXACT keys:
              {
                "sentiment": "positive|neutral|negative",
                "theme": "<one short topic like battery, sound, price, delivery, comfort>"
               }
              Review: "%s"
              """.formatted(text);


        Map<String, Object> response = callGroq(prompt);

        String sentiment = (String) response.getOrDefault("sentiment", "neutral");
        String theme = (String) response.getOrDefault("theme", "general");

        Review review = new Review();
        review.setText(text);
        review.setSentiment(sentiment);
        review.setTheme(theme);
        review.setCreatedAt(LocalDateTime.now().toString());

        return reviewRepository.save(review);
    }


    // --------- CALL GROQ API ----------
    private Map<String, Object> callGroq(String prompt) {

        String url = "https://api.groq.com/openai/v1/chat/completions";

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.3-70b-versatile");
        body.put("messages", List.of(
                Map.of("role", "system", "content", "You analyze product reviews."),
                Map.of("role", "user", "content", prompt)
        ));

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + groqApiKey);
        headers.set("Content-Type", "application/json");

        Map<?, ?> response = restTemplate.postForObject(
                url,
                new org.springframework.http.HttpEntity<>(body, headers),
                Map.class
        );
        try {
             String content = ((Map<String, Object>) ((List<Map<String, Object>>) response.get("choices"))
            .get(0)
            .get("message"))
            .get("content").toString();

            // Parse JSON content
             ObjectMapper mapper = new ObjectMapper();
             Map<String, Object> json = mapper.readValue(content, Map.class);

             Map<String, Object> out = new HashMap<>();
            out.put("sentiment", json.getOrDefault("sentiment", "neutral"));
            out.put("theme", json.getOrDefault("theme", "general"));

           return out;
        } 
        catch (Exception e) {
              e.printStackTrace();
              return Map.of(
                 "sentiment", "neutral",
                 "theme", "general"
              );
        }

        
    }


    // --------- SUMMARY ----------
    public Map<String, Object> getSummary() {

        List<Review> all = reviewRepository.findAll();

        long positive = all.stream().filter(r -> "positive".equals(r.getSentiment())).count();
        long neutral   = all.stream().filter(r -> "neutral".equals(r.getSentiment())).count();
        long negative = all.stream().filter(r -> "negative".equals(r.getSentiment())).count();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalReviews", all.size());
        summary.put("positive", positive);
        summary.put("neutral", neutral);
        summary.put("negative", negative);
        summary.put("reviews", all);

        return summary;
    }
}


