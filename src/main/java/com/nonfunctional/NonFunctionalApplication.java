package com.nonfunctional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NonFunctionalApplication {
    public static void main(String[] args) {
        SpringApplication.run(NonFunctionalApplication.class, args);
        System.out.println("Web Doctor NON-FUNCTIONAL is running...");
    }
}