package com.sp26se025.aura.controller;

import com.sp26se025.aura.dto.Requests;
import com.sp26se025.aura.model.*;
import com.sp26se025.aura.service.InMemoryStore;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final InMemoryStore store;

    public UserController(InMemoryStore store) {
        this.store = store;
    }

    @GetMapping("/{id}")
    public UserAccount profile(@PathVariable String id) {
        return store.user(id).orElseThrow();
    }

    @PutMapping("/{id}")
    public UserAccount update(@PathVariable String id, @RequestBody Requests.ProfileUpdateRequest request) {
        UserAccount user = store.user(id).orElseThrow();
        if (request.fullName() != null) user.setFullName(request.fullName());
        if (request.medicalInformation() != null) user.setMedicalInformation(request.medicalInformation());
        if (request.assignedDoctorId() != null) user.setAssignedDoctorId(request.assignedDoctorId());
        if (request.clinicId() != null) user.setClinicId(request.clinicId());
        return store.saveUser(user);
    }

    @GetMapping("/{id}/payments")
    public List<Payment> payments(@PathVariable String id) {
        return store.payments().stream().filter(p -> id.equals(p.getAccountId())).sorted(Comparator.comparing(Payment::getPaidAt).reversed()).toList();
    }

    @PostMapping("/purchase")
    public Payment purchase(@RequestBody Requests.PurchaseRequest request) {
        UserAccount user = store.user(request.accountId()).orElseThrow();
        user.setAnalysisCredits(user.getAnalysisCredits() + request.credits());
        Payment payment = new Payment();
        payment.setId("pay-" + UUID.randomUUID());
        payment.setAccountId(request.accountId());
        payment.setPackageName(request.packageName());
        payment.setCredits(request.credits());
        payment.setAmount(request.amount());
        store.saveUser(user);
        return store.savePayment(payment);
    }

    @GetMapping("/{id}/credits")
    public Map<String, Integer> credits(@PathVariable String id) {
        UserAccount user = store.user(id).orElseThrow();
        return Map.of("remainingCredits", user.getAnalysisCredits());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(org.springframework.http.HttpStatus.NOT_FOUND)
    Map<String, String> notFound() {
        return Map.of("message", "Resource not found");
    }
}
