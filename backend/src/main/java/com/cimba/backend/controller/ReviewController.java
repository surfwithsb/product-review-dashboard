package com.cimba.backend.controller;

import com.cimba.backend.model.Review;
import com.cimba.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")   // allow React frontend to call backend
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // health check endpoint
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    // upload multiple reviews (CSV or pasted text)
    @PostMapping("/upload")
    public List<Review> uploadReviews(@RequestBody List<String> reviewTexts) {
        return reviewService.saveBulk(reviewTexts);
    }

    // dashboard summary
    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        return reviewService.getSummary();
    }
}

