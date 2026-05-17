package com.nonfunctional.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.nonfunctional.exception.CustomException;
import com.nonfunctional.model.Doctor;

import jakarta.annotation.PostConstruct;

@Service
public class DoctorService {

    private final Map<Long, Doctor> doctorDatabase = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void init() {
        // Initialize with sample data
        List<Doctor> sampleDoctors = List.of(
            new Doctor(null, "Dr. John Smith", "Cardiology", "john.smith@hospital.com", "+1-555-0101", 
                      "123 Medical Center Dr", 15, true),
            new Doctor(null, "Dr. Sarah Johnson", "Neurology", "sarah.johnson@hospital.com", "+1-555-0102", 
                      "456 Brain Institute Ave", 12, true),
            new Doctor(null, "Dr. Michael Chen", "Orthopedics", "michael.chen@hospital.com", "+1-555-0103", 
                      "789 Bone Clinic Rd", 20, false),
            new Doctor(null, "Dr. Emily Davis", "Pediatrics", "emily.davis@hospital.com", "+1-555-0104", 
                      "321 Children's Hospital Ln", 8, true),
            new Doctor(null, "Dr. Robert Wilson", "Dermatology", "robert.wilson@hospital.com", "+1-555-0105", 
                      "654 Skin Care Blvd", 10, true)
        );

        sampleDoctors.forEach(doctor -> {
            doctor.setId(idGenerator.getAndIncrement());
            doctorDatabase.put(doctor.getId(), doctor);
        });
    }

    @Cacheable("doctorInfo")
    public String getDoctorInfo() {
        simulateSlowService();
        return "Web Doctor API - Non-Functional Demo. Total doctors: " + doctorDatabase.size();
    }

    @Cacheable(value = "doctors", key = "#id")
    public Optional<Doctor> getDoctorById(Long id) {
        simulateSlowService();
        if (!doctorDatabase.containsKey(id)) {
            throw new CustomException("Doctor not found with id: " + id);
        }
        return Optional.of(doctorDatabase.get(id));
    }

    @Cacheable("allDoctors")
    public List<Doctor> getAllDoctors() {
        simulateSlowService();
        return new ArrayList<>(doctorDatabase.values());
    }

    @Cacheable(value = "doctorsBySpecialization", key = "#specialization")
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        simulateSlowService();
        return doctorDatabase.values().stream()
                .filter(d -> d.getSpecialization().equalsIgnoreCase(specialization))
                .collect(Collectors.toList());
    }

    @CachePut(value = "doctors", key = "#result.id")
    public Doctor createDoctor(Doctor doctor) {
        Long newId = idGenerator.getAndIncrement();
        doctor.setId(newId);
        doctorDatabase.put(newId, doctor);
        return doctor;
    }

    @CachePut(value = "doctors", key = "#id")
    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        if (!doctorDatabase.containsKey(id)) {
            throw new CustomException("Doctor not found with id: " + id);
        }
        
        Doctor existingDoctor = doctorDatabase.get(id);
        existingDoctor.setName(doctorDetails.getName());
        existingDoctor.setSpecialization(doctorDetails.getSpecialization());
        existingDoctor.setEmail(doctorDetails.getEmail());
        existingDoctor.setPhone(doctorDetails.getPhone());
        existingDoctor.setAddress(doctorDetails.getAddress());
        existingDoctor.setExperienceYears(doctorDetails.getExperienceYears());
        existingDoctor.setAvailable(doctorDetails.isAvailable());
        existingDoctor.setUpdatedAt(java.time.LocalDateTime.now());
        
        doctorDatabase.put(id, existingDoctor);
        return existingDoctor;
    }

    @CacheEvict(value = "doctors", key = "#id")
    public void deleteDoctor(Long id) {
        if (!doctorDatabase.containsKey(id)) {
            throw new CustomException("Doctor not found with id: " + id);
        }
        doctorDatabase.remove(id);
    }

    public void toggleAvailability(Long id) {
        if (!doctorDatabase.containsKey(id)) {
            throw new CustomException("Doctor not found with id: " + id);
        }
        Doctor doctor = doctorDatabase.get(id);
        doctor.setAvailable(!doctor.isAvailable());
        doctor.setUpdatedAt(java.time.LocalDateTime.now());
    }

    public List<String> getAllSpecializations() {
        return doctorDatabase.values().stream()
                .map(Doctor::getSpecialization)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Doctor> getAvailableDoctors() {
        return doctorDatabase.values().stream()
                .filter(Doctor::isAvailable)
                .collect(Collectors.toList());
    }

    private void simulateSlowService() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service interrupted", e);
        }
    }
}