package com.practice.admin.repository;

import com.practice.admin.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository
        extends JpaRepository<User, Long> {

    // lấy doctor
    List<User> findByRole(String role);

}