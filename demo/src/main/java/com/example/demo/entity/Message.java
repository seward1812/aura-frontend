package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;

    private String receiver;

    private String content;
}