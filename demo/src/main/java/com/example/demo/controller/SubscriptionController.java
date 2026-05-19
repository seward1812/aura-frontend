package com.example.demo.controller;

import com.example.demo.entity.Subscription;
import com.example.demo.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping
    public Subscription buyPackage(
            @RequestBody Subscription subscription
    ) {
        return subscriptionService.buyPackage(subscription);
    }

    @GetMapping
    public List<Subscription> getPackages() {
        return subscriptionService.getPackages();
    }
}