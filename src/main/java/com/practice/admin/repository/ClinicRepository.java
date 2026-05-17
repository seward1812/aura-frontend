package com.practice.admin.repository;

import com.practice.admin.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository
        extends JpaRepository<Clinic, Long> {

}