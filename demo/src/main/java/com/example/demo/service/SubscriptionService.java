package com.example.demo.service;

import com.example.demo.entity.Subscription;
import com.example.demo.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public Subscription buyPackage(
            Subscription subscription
    ) {

        subscription.setStatus("ACTIVE");

        subscription.setPaymentStatus("PAID");

        subscription.setPurchaseDate("2026-05-10");

        return subscriptionRepository.save(subscription);
    }

    public List<Subscription> getPackages() {
        return subscriptionRepository.findAll();
    }
}