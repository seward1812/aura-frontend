package com.sp26se025.aura.service;

import com.sp26se025.aura.dto.AuthDtos;
import com.sp26se025.aura.model.Role;
import com.sp26se025.aura.model.UserAccount;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {
    private final InMemoryStore store;

    public AuthService(InMemoryStore store) {
        this.store = store;
    }

    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        EnumSet<Role> roles = request.roles() == null || request.roles().isEmpty()
                ? EnumSet.of(Role.USER)
                : EnumSet.copyOf(request.roles());
        UserAccount user = new UserAccount("u-" + UUID.randomUUID(), request.email(), request.username(), request.password(), request.fullName(), roles);
        store.saveUser(user);
        return response(user, "Registration successful");
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        UserAccount user = store.findByUsername(request.username())
                .filter(u -> u.isEnabled() && u.getPassword().equals(request.password()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        return response(user, "Login successful");
    }

    private AuthDtos.AuthResponse response(UserAccount user, String message) {
        String tokenPayload = user.getId() + ":" + user.getUsername() + ":" + System.currentTimeMillis();
        String token = Base64.getUrlEncoder().encodeToString(tokenPayload.getBytes(StandardCharsets.UTF_8));
        return new AuthDtos.AuthResponse(token, user.getId(), user.getUsername(), user.getFullName(), Set.copyOf(user.getRoles()), message);
    }
}
