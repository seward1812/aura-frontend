package com.sp26se025.aura.service;

import com.sp26se025.aura.model.AnalysisReport;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
public class ReportExportService {
    public byte[] csv(AnalysisReport report) {
        String csv = "id,patientId,riskLevel,result,aiVersion,createdAt\n%s,%s,%s,\"%s\",%s,%s\n".formatted(
                report.getId(), report.getPatientId(), report.getRiskLevel(), report.getResult().replace("\"", "\"\""), report.getAiVersion(), report.getCreatedAt());
        return csv.getBytes(StandardCharsets.UTF_8);
    }

    public byte[] pdf(AnalysisReport report) {
        String body = "AURA Retinal Vascular Health Screening Report\n\n" +
                "Report ID: " + report.getId() + "\n" +
                "Patient ID: " + report.getPatientId() + "\n" +
                "Risk Level: " + report.getRiskLevel() + "\n" +
                "AI Version: " + report.getAiVersion() + "\n" +
                "Result: " + report.getResult() + "\n\n" +
                "Findings:\n" + String.join("\n", report.getFindings()) + "\n\n" +
                "Recommendations:\n" + String.join("\n", report.getRecommendations()) + "\n\n" +
                "Thresholds: " + report.getThresholds().entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(", ")) + "\n" +
                "Clinical decision support only; physician validation is required.\n";
        return body.getBytes(StandardCharsets.UTF_8);
    }
}
