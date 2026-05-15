package com.sp26se025.aura.controller;

import com.sp26se025.aura.dto.Requests;
import com.sp26se025.aura.model.*;
import com.sp26se025.aura.service.InMemoryStore;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final InMemoryStore store;

    public AdminController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping("/accounts")
    public Collection<UserAccount> accounts() {
        return store.users();
    }

    @PatchMapping("/accounts/{id}")
    public UserAccount updateAccount(@PathVariable String id, @RequestBody Requests.AccountAdminRequest request) {
        UserAccount user = store.user(id).orElseThrow();
        if (request.enabled() != null) user.setEnabled(request.enabled());
        if (request.roles() != null && !request.roles().isEmpty()) user.setRoles(EnumSet.copyOf(request.roles()));
        if (request.assignedDoctorId() != null) user.setAssignedDoctorId(request.assignedDoctorId());
        if (request.clinicId() != null) user.setClinicId(request.clinicId());
        return store.saveUser(user);
    }

    @PatchMapping("/clinics/{id}/verification")
    public Clinic verifyClinic(@PathVariable String id, @RequestParam boolean verified) {
        Clinic clinic = store.clinic(id).orElseThrow();
        clinic.setVerified(verified);
        return store.saveClinic(clinic);
    }

    @PutMapping("/ai-config")
    public Map<String, String> configureAi(@RequestBody Requests.AiConfigRequest request) {
        if (request.aiVersion() != null) store.aiSettings().put("aiVersion", request.aiVersion());
        if (request.retrainingPolicy() != null) store.aiSettings().put("retrainingPolicy", request.retrainingPolicy());
        if (request.thresholds() != null) store.aiSettings().put("thresholds", request.thresholds().toString());
        return store.aiSettings();
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        long highRisk = store.reports().stream().filter(r -> r.getRiskLevel() == RiskLevel.HIGH || r.getRiskLevel() == RiskLevel.CRITICAL).count();
        return Map.of(
                "totalAccounts", store.users().size(),
                "totalClinics", store.clinics().size(),
                "totalImages", store.reports().size(),
                "highRiskReports", highRisk,
                "revenueTransactions", store.payments().size(),
                "aiSettings", store.aiSettings()
        );
    }

    @GetMapping("/analytics")
    public Map<String, Object> analytics() {
        Map<RiskLevel, Long> riskDistribution = store.reports().stream().collect(Collectors.groupingBy(AnalysisReport::getRiskLevel, Collectors.counting()));
        return Map.of("imageCount", store.reports().size(), "riskDistribution", riskDistribution, "errorRate", 0.0);
    }

    @GetMapping("/audit-logs")
    public Map<String, Object> auditLogs() {
        return Map.of("retentionPolicy", "Daily backup and centralized audit logging should be enabled in production.", "events", java.util.List.of());
    }

    @GetMapping("/notification-templates")
    public Map<String, String> notificationTemplates() {
        return Map.of(
                "analysisReady", "Your AURA retinal screening result is ready.",
                "highRisk", "High-risk retinal vascular findings require clinician review."
        );
    }
}
