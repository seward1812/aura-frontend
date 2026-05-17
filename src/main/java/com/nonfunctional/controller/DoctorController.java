package com.nonfunctional.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nonfunctional.model.Doctor;
import com.nonfunctional.service.DoctorService;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping
    public ResponseEntity<String> getDoctorInfo() {
        return ResponseEntity.ok(doctorService.getDoctorInfo());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<Doctor>> getDoctorsBySpecialization(@PathVariable String specialization) {
        List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specialization);
        if (doctors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Doctor>> getAvailableDoctors() {
        List<Doctor> doctors = doctorService.getAvailableDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/specializations")
    public ResponseEntity<List<String>> getAllSpecializations() {
        List<String> specializations = doctorService.getAllSpecializations();
        return ResponseEntity.ok(specializations);
    }

    @PostMapping
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        Doctor createdDoctor = doctorService.createDoctor(doctor);
        return new ResponseEntity<>(createdDoctor, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
        Doctor updatedDoctor = doctorService.updateDoctor(id, doctorDetails);
        return ResponseEntity.ok(updatedDoctor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/toggle-availability")
    public ResponseEntity<Map<String, Object>> toggleAvailability(@PathVariable Long id) {
        doctorService.toggleAvailability(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Doctor availability toggled successfully");
        response.put("doctorId", id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Doctor>> searchDoctors(@RequestParam String query) {
        List<Doctor> allDoctors = doctorService.getAllDoctors();
        List<Doctor> results = allDoctors.stream()
                .filter(d -> d.getName().toLowerCase().contains(query.toLowerCase()) ||
                             d.getSpecialization().toLowerCase().contains(query.toLowerCase()) ||
                             d.getEmail().toLowerCase().contains(query.toLowerCase()))
                .toList();
        return ResponseEntity.ok(results);
    }
}