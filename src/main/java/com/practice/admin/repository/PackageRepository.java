package com.practice.admin.repository;

import com.practice.admin.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository
        extends JpaRepository<PackageEntity, Long> {
}