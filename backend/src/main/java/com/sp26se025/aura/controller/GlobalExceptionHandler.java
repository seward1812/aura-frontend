package com.sp26se025.aura.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<Map<String, String>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Resource not found"));
    }

    @ExceptionHandler(IOException.class)
    ResponseEntity<Map<String, String>> uploadFailed(IOException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Image analysis failed safely: " + exception.getMessage()));
    }
}
