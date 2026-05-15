package com.sp26se025.aura.service;

import com.sp26se025.aura.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryStore {
    private final Map<String, UserAccount> users = new ConcurrentHashMap<>();
    private final Map<String, Clinic> clinics = new ConcurrentHashMap<>();
    private final Map<String, AnalysisReport> reports = new ConcurrentHashMap<>();
    private final Map<String, Message> messages = new ConcurrentHashMap<>();
    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    private final Map<String, String> aiSettings = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        saveUser(new UserAccount("u-demo", "patient@aura.vn", "patient", "123456", "Demo Patient", EnumSet.of(Role.USER)));
        saveUser(new UserAccount("d-demo", "doctor@aura.vn", "doctor", "123456", "Demo Doctor", EnumSet.of(Role.DOCTOR)));
        saveUser(new UserAccount("a-demo", "admin@aura.vn", "admin", "123456", "AURA Admin", EnumSet.of(Role.ADMIN)));
        UserAccount clinicAdmin = new UserAccount("c-demo", "clinic@aura.vn", "clinic", "123456", "Demo Clinic Manager", EnumSet.of(Role.CLINIC));
        clinicAdmin.setClinicId("clinic-demo");
        saveUser(clinicAdmin);
        Clinic clinic = new Clinic();
        clinic.setId("clinic-demo");
        clinic.setName("AURA Community Clinic");
        clinic.setAddress("Ho Chi Minh City, Vietnam");
        clinic.setTaxCode("SP26SE025");
        clinic.setVerified(true);
        clinics.put(clinic.getId(), clinic);
        aiSettings.put("aiVersion", "AURA-MVP-1.0");
        aiSettings.put("retrainingPolicy", "Anonymize sensitive identifiers before feedback-based retraining.");
    }

    public Collection<UserAccount> users() { return users.values(); }
    public Optional<UserAccount> user(String id) { return Optional.ofNullable(users.get(id)); }
    public Optional<UserAccount> findByUsername(String username) { return users.values().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst(); }
    public UserAccount saveUser(UserAccount user) { users.put(user.getId(), user); return user; }
    public Collection<Clinic> clinics() { return clinics.values(); }
    public Optional<Clinic> clinic(String id) { return Optional.ofNullable(clinics.get(id)); }
    public Clinic saveClinic(Clinic clinic) { clinics.put(clinic.getId(), clinic); return clinic; }
    public Collection<AnalysisReport> reports() { return reports.values(); }
    public Optional<AnalysisReport> report(String id) { return Optional.ofNullable(reports.get(id)); }
    public AnalysisReport saveReport(AnalysisReport report) { reports.put(report.getId(), report); return report; }
    public Collection<Message> messages() { return messages.values(); }
    public Message saveMessage(Message message) { messages.put(message.getId(), message); return message; }
    public Collection<Payment> payments() { return payments.values(); }
    public Payment savePayment(Payment payment) { payments.put(payment.getId(), payment); return payment; }
    public Map<String, String> aiSettings() { return aiSettings; }
}
