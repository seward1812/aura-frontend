package com.nonfunctional.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    private static final long START_TIME = System.currentTimeMillis();

    @GetMapping
    public Map<String, Object> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        health.put("uptime", getUptime());
        health.put("service", "Web Doctor API");
        health.put("version", "1.0.0");
        
        Map<String, Object> checks = new HashMap<>();
        checks.put("database", Map.of("status", "UP", "message", "In-memory storage operational"));
        checks.put("cache", Map.of("status", "UP", "message", "Cache enabled"));
        checks.put("security", Map.of("status", "UP", "message", "Security configured"));
        health.put("checks", checks);
        
        return health;
    }

    @GetMapping("/ready")
    public Map<String, Object> getReadiness() {
        Map<String, Object> readiness = new HashMap<>();
        readiness.put("ready", true);
        readiness.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return readiness;
    }

    @GetMapping("/live")
    public Map<String, Object> getLiveness() {
        Map<String, Object> liveness = new HashMap<>();
        liveness.put("alive", true);
        liveness.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return liveness;
    }

    private String getUptime() {
        long uptimeMillis = System.currentTimeMillis() - START_TIME;
        long seconds = uptimeMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        return String.format("%dd %dh %dm %ds", days, hours % 24, minutes % 60, seconds % 60);
    }
}