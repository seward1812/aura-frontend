package com.practice.admin.repository;

import com.practice.admin.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository
        extends JpaRepository<Setting, Long> {
}