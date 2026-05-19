package com.example.demo.repository;

import com.example.demo.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisResultRepository
        extends JpaRepository<AnalysisResult, Long> {

    Long countByRiskLevel(String riskLevel);
}