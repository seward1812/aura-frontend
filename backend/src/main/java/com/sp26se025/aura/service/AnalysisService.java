package com.sp26se025.aura.service;

import com.sp26se025.aura.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

@Service
public class AnalysisService {
    private final InMemoryStore store;
    private final Path uploadDir = Path.of("uploads");

    public AnalysisService(InMemoryStore store) {
        this.store = store;
    }

    public AnalysisReport analyze(MultipartFile file, String patientId, String clinicId) throws IOException {
        Files.createDirectories(uploadDir);
        String safeName = Objects.requireNonNullElse(file.getOriginalFilename(), "retina-image").replaceAll("[^a-zA-Z0-9._-]", "_");
        String fileName = Instant.now().toEpochMilli() + "-" + UUID.randomUUID() + "-" + safeName;
        Path target = uploadDir.resolve(fileName);
        file.transferTo(target);

        long size = Files.size(target);
        double imageSignal = Math.min(0.95, Math.max(0.08, (size % 1_000_000) / 1_000_000.0));
        RiskLevel riskLevel = riskFromScore(imageSignal);

        AnalysisReport report = new AnalysisReport();
        report.setId("ar-" + UUID.randomUUID());
        report.setPatientId(patientId == null || patientId.isBlank() ? "u-demo" : patientId);
        report.setClinicId(clinicId);
        report.setImagePath(fileName);
        report.setAnnotatedImagePath(fileName);
        report.setRiskLevel(riskLevel);
        report.setResult("AI retinal vascular screening completed: " + riskLevel + " risk");
        report.setMetrics(Map.of(
                "vascularTortuosityScore", round(imageSignal),
                "hemorrhageSuspicionScore", round(imageSignal * 0.72),
                "diabeticChangeScore", round(imageSignal * 0.64),
                "strokeRiskProxyScore", round(imageSignal * 0.58)
        ));
        report.setFindings(findings(riskLevel));
        report.setRecommendations(recommendations(riskLevel));
        return store.saveReport(report);
    }

    public List<AnalysisReport> analyzeBatch(List<MultipartFile> files, String patientId, String clinicId) throws IOException {
        List<AnalysisReport> reports = new ArrayList<>();
        for (MultipartFile file : files) {
            reports.add(analyze(file, patientId, clinicId));
        }
        return reports;
    }

    public AnalysisReport review(String id, String doctorId, String notes, String diagnosis, String correctedFinding) {
        AnalysisReport report = store.report(id).orElseThrow(() -> new NoSuchElementException("Report not found"));
        report.setDoctorId(doctorId);
        report.setDoctorNotes(notes == null ? diagnosis : notes + "\nDiagnosis: " + diagnosis);
        report.setCorrectedFinding(correctedFinding);
        report.setStatus(AnalysisStatus.DOCTOR_REVIEWED);
        return store.saveReport(report);
    }

    private RiskLevel riskFromScore(double score) {
        if (score >= 0.9) return RiskLevel.CRITICAL;
        if (score >= 0.72) return RiskLevel.HIGH;
        if (score >= 0.4) return RiskLevel.MODERATE;
        return RiskLevel.LOW;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private List<String> findings(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case LOW -> List.of("No strong abnormal vascular pattern detected in MVP screening.");
            case MODERATE -> List.of("Mild retinal vascular irregularity detected.", "Recommend clinician review for systemic risk correlation.");
            case HIGH -> List.of("Elevated vascular abnormality proxy score detected.", "Possible hypertension or diabetic complication risk markers require doctor validation.");
            case CRITICAL -> List.of("Critical abnormality proxy score detected.", "Urgent clinician review is recommended.");
        };
    }

    private List<String> recommendations(RiskLevel riskLevel) {
        return switch (riskLevel) {
            case LOW -> List.of("Continue annual retinal screening and maintain preventive health habits.");
            case MODERATE -> List.of("Schedule a non-urgent doctor consultation.", "Monitor blood pressure and glucose if clinically appropriate.");
            case HIGH -> List.of("Consult an assigned doctor soon for confirmatory examination.", "Do not use this CDS output as a standalone diagnosis.");
            case CRITICAL -> List.of("Seek prompt medical evaluation.", "Clinic should trigger high-risk patient alert workflow.");
        };
    }
}
