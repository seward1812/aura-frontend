package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String result;

    private String riskLevel;

    private String recommendation;

    private Double confidence;

    private String annotatedImageUrl;

    private String notification;

    private String doctorConclusion;

    private Boolean doctorConfirmed;

    private String doctorNotes;

    @OneToOne(cascade = CascadeType.ALL)
    private RetinalImage image;
}