package com.practice.admin.repository;

import com.practice.admin.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository
        extends JpaRepository<Admin, Long> {

    Admin findByUsernameAndPassword(
            String username,
            String password
    );
}