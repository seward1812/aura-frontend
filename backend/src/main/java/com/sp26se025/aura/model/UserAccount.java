package com.sp26se025.aura.model;

import java.time.Instant;
import java.util.EnumSet;

public class UserAccount {
    private String id;
    private String email;
    private String username;
    private String password;
    private String fullName;
    private String clinicId;
    private String assignedDoctorId;
    private boolean enabled = true;
    private EnumSet<Role> roles = EnumSet.of(Role.USER);
    private int analysisCredits = 5;
    private String medicalInformation = "";
    private Instant createdAt = Instant.now();

    public UserAccount() {
    }

    public UserAccount(String id, String email, String username, String password, String fullName, EnumSet<Role> roles) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.roles = roles;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getClinicId() { return clinicId; }
    public void setClinicId(String clinicId) { this.clinicId = clinicId; }
    public String getAssignedDoctorId() { return assignedDoctorId; }
    public void setAssignedDoctorId(String assignedDoctorId) { this.assignedDoctorId = assignedDoctorId; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public EnumSet<Role> getRoles() { return roles; }
    public void setRoles(EnumSet<Role> roles) { this.roles = roles; }
    public int getAnalysisCredits() { return analysisCredits; }
    public void setAnalysisCredits(int analysisCredits) { this.analysisCredits = analysisCredits; }
    public String getMedicalInformation() { return medicalInformation; }
    public void setMedicalInformation(String medicalInformation) { this.medicalInformation = medicalInformation; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
