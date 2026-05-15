package com.sp26se025.aura.controller;

import com.sp26se025.aura.dto.AuthDtos;
import com.sp26se025.aura.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public AuthDtos.AuthResponse register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return authService.login(request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> invalid(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(java.util.Map.of("message", exception.getMessage()));
    }
}
