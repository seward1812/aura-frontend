package com.practice.admin.repository;

import com.practice.admin.entity.AIReport;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AIReportRepository
        extends JpaRepository<AIReport, Long> {
}