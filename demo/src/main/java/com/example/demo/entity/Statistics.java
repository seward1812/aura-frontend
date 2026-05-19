package com.example.demo.entity;

import lombok.Data;

@Data
public class Statistics {

    private Long totalAnalyses;

    private Long highRiskCases;

    private Long mediumRiskCases;

    private Long lowRiskCases;
}