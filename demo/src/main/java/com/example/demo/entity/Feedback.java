package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String doctorName;

    private String feedbackText;

    private Boolean aiCorrect;
}