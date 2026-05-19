package com.example.demo.repository;

import com.example.demo.entity.RetinalImage;
import org.springframework.data.jpa.repository.JpaRepository;

interface RetinalImageRepository
       extends JpaRepository<RetinalImage, Long> {

}