package com.sp26se025.aura.controller;

import com.sp26se025.aura.dto.Requests;
import com.sp26se025.aura.model.*;
import com.sp26se025.aura.service.InMemoryStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clinics")
public class ClinicController {
    private final InMemoryStore store;

    public ClinicController(InMemoryStore store) {
        this.store = store;
    }

    @PostMapping("/register")
    public Clinic register(@RequestBody Requests.ClinicRegistrationRequest request) {
        Clinic clinic = new Clinic();
        clinic.setId("clinic-" + UUID.randomUUID());
        clinic.setName(request.name());
        clinic.setTaxCode(request.taxCode());
        clinic.setAddress(request.address());
        clinic.setVerified(false);
        return store.saveClinic(clinic);
    }

    @GetMapping("/{clinicId}/accounts")
    public List<UserAccount> accounts(@PathVariable String clinicId) {
        return store.users().stream().filter(u -> clinicId.equals(u.getClinicId())).toList();
    }

    @GetMapping("/{clinicId}/reports")
    public List<AnalysisReport> reports(@PathVariable String clinicId) {
        return store.reports().stream().filter(r -> clinicId.equals(r.getClinicId())).toList();
    }

    @GetMapping("/{clinicId}/statistics")
    public Map<String, Object> statistics(@PathVariable String clinicId) {
        List<AnalysisReport> reports = reports(clinicId);
        Map<RiskLevel, Long> distribution = reports.stream().collect(Collectors.groupingBy(AnalysisReport::getRiskLevel, Collectors.counting()));
        return Map.of("clinicId", clinicId, "imageCount", reports.size(), "riskDistribution", distribution);
    }

    @GetMapping("/{clinicId}/alerts")
    public List<AnalysisReport> alerts(@PathVariable String clinicId) {
        return reports(clinicId).stream().filter(r -> r.getRiskLevel() == RiskLevel.HIGH || r.getRiskLevel() == RiskLevel.CRITICAL).toList();
    }
}
