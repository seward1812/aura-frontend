package com.sp26se025.aura.dto;

import com.sp26se025.aura.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class AuthDtos {
    public record RegisterRequest(@Email String email, @NotBlank String username, @NotBlank String password, String fullName, Set<Role> roles) {}
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record AuthResponse(String token, String id, String username, String fullName, Set<Role> roles, String message) {}
}
