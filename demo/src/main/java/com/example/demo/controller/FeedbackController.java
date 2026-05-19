package com.example.demo.controller;

import com.example.demo.entity.Feedback;
import com.example.demo.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public Feedback addFeedback(
            @RequestBody Feedback feedback
    ) {
        return feedbackService.saveFeedback(feedback);
    }

    @GetMapping
    public List<Feedback> getAllFeedbacks() {
        return feedbackService.getAllFeedbacks();
    }
}