package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String packageName;

    private Double price;

    private Integer analysisCredits;

    private String status;

    private String paymentStatus;

    private String purchaseDate;
}