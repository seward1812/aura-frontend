package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "doctor")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialization;

    // Constructor rỗng (bắt buộc)
    public Doctor() {}

    // Constructor đầy đủ
    public Doctor(Long id, String name, String specialization) {
        this.id = id;
        this.id = id;
        this.name = name;
        this.specialization = specialization;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}