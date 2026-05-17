package com.practice.admin.repository;

import com.practice.admin.entity.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisRepository
        extends JpaRepository<Analysis, Long> {
}