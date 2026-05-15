package com.sp26se025.aura.controller;

import com.sp26se025.aura.model.*;
import com.sp26se025.aura.service.InMemoryStore;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {
    private final InMemoryStore store;

    public DoctorController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping("/{doctorId}/patients")
    public List<UserAccount> patients(@PathVariable String doctorId,
                                      @RequestParam(required = false) String query,
                                      @RequestParam(required = false) RiskLevel riskLevel) {
        List<String> riskyPatientIds = store.reports().stream()
                .filter(r -> riskLevel == null || riskLevel == r.getRiskLevel())
                .map(AnalysisReport::getPatientId)
                .toList();
        return store.users().stream()
                .filter(u -> u.getRoles().contains(Role.USER))
                .filter(u -> doctorId.equals(u.getAssignedDoctorId()) || "d-demo".equals(doctorId))
                .filter(u -> query == null || u.getId().contains(query) || u.getFullName().toLowerCase().contains(query.toLowerCase()))
                .filter(u -> riskLevel == null || riskyPatientIds.contains(u.getId()))
                .toList();
    }

    @GetMapping("/{doctorId}/reports")
    public List<AnalysisReport> reports(@PathVariable String doctorId) {
        return store.reports().stream()
                .filter(r -> doctorId.equals(r.getDoctorId()) || r.getDoctorId() == null || "d-demo".equals(doctorId))
                .sorted(Comparator.comparing(AnalysisReport::getCreatedAt).reversed())
                .toList();
    }

    @GetMapping("/{doctorId}/summary")
    public Map<String, Object> summary(@PathVariable String doctorId) {
        long reviewed = store.reports().stream().filter(r -> doctorId.equals(r.getDoctorId())).count();
        long highRisk = store.reports().stream().filter(r -> r.getRiskLevel() == RiskLevel.HIGH || r.getRiskLevel() == RiskLevel.CRITICAL).count();
        return Map.of("reviewedReports", reviewed, "highRiskReports", highRisk, "totalReports", store.reports().size());
    }
}
