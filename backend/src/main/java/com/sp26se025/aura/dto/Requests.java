package com.sp26se025.aura.dto;

import com.sp26se025.aura.model.Role;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public class Requests {
    public record ProfileUpdateRequest(String fullName, String medicalInformation, String assignedDoctorId, String clinicId) {}
    public record DoctorReviewRequest(String notes, String diagnosis, String correctedFinding) {}
    public record FeedbackRequest(String reportId, String doctorId, String label, String comment) {}
    public record MessageRequest(String senderId, String receiverId, String body) {}
    public record PurchaseRequest(String accountId, String packageName, int credits, BigDecimal amount) {}
    public record ClinicRegistrationRequest(String name, String taxCode, String address) {}
    public record AccountAdminRequest(Boolean enabled, Set<Role> roles, String assignedDoctorId, String clinicId) {}
    public record AiConfigRequest(Map<String, Double> thresholds, String retrainingPolicy, String aiVersion) {}
}
