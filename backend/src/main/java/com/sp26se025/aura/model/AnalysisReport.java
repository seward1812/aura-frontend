package com.sp26se025.aura.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalysisReport {
    private String id;
    private String patientId;
    private String doctorId;
    private String clinicId;
    private String imagePath;
    private String annotatedImagePath;
    private String result;
    private RiskLevel riskLevel;
    private AnalysisStatus status = AnalysisStatus.COMPLETED;
    private List<String> findings = new ArrayList<>();
    private List<String> recommendations = new ArrayList<>();
    private Map<String, Double> metrics;
    private String aiVersion = "AURA-MVP-1.0";
    private Map<String, Double> thresholds = Map.of("highRisk", 0.72, "criticalRisk", 0.9);
    private String doctorNotes;
    private String correctedFinding;
    private Instant createdAt = Instant.now();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    public String getClinicId() { return clinicId; }
    public void setClinicId(String clinicId) { this.clinicId = clinicId; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getAnnotatedImagePath() { return annotatedImagePath; }
    public void setAnnotatedImagePath(String annotatedImagePath) { this.annotatedImagePath = annotatedImagePath; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public AnalysisStatus getStatus() { return status; }
    public void setStatus(AnalysisStatus status) { this.status = status; }
    public List<String> getFindings() { return findings; }
    public void setFindings(List<String> findings) { this.findings = findings; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
    public Map<String, Double> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Double> metrics) { this.metrics = metrics; }
    public String getAiVersion() { return aiVersion; }
    public void setAiVersion(String aiVersion) { this.aiVersion = aiVersion; }
    public Map<String, Double> getThresholds() { return thresholds; }
    public void setThresholds(Map<String, Double> thresholds) { this.thresholds = thresholds; }
    public String getDoctorNotes() { return doctorNotes; }
    public void setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; }
    public String getCorrectedFinding() { return correctedFinding; }
    public void setCorrectedFinding(String correctedFinding) { this.correctedFinding = correctedFinding; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
