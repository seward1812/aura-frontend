package com.example.demo.service;

import com.example.demo.entity.Clinic;
import com.example.demo.entity.ServicePackage;
import com.example.demo.repository.ClinicRepository;
import com.example.demo.repository.ServicePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BillingService {

    @Autowired
    private ClinicRepository clinicRepository;

    @Autowired
    private ServicePackageRepository packageRepository;

    public Clinic getClinicDashboardStats(Long clinicId) {
        return clinicRepository.findById(clinicId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin phòng khám với ID: " + clinicId));
    }

    public List<ServicePackage> getAllAvailablePackages() {
        return packageRepository.findByIsActiveTrue();
    }

    @Transactional
    public void incrementAnalysisUsage(Long clinicId, int amount) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        // 1. Kiểm tra ngày hết hạn gói
        if (clinic.getExpiryDate() != null && clinic.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("PACKAGE_EXPIRED");
        }

        // 2. Kiểm tra hạn mức số lượng ảnh còn lại
        if (clinic.getTotalAnalyzed() + amount > clinic.getCurrentPackageLimit()) {
            throw new RuntimeException("LIMIT_EXCEEDED");
        }

        clinic.setTotalAnalyzed(clinic.getTotalAnalyzed() + amount);
        clinicRepository.save(clinic);
    }

    @Transactional
    public Clinic upgradePackage(Long clinicId, Long packageId) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
                
        ServicePackage newPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("PACKAGE_NOT_FOUND"));

        // Cập nhật thông tin gói và hạn mức mới
        clinic.setCurrentPackage(newPackage);
        clinic.setCurrentPackageLimit(newPackage.getImageLimit());
        
        // Tính toán ngày hết hạn mới (Cộng thêm số ngày quy định của gói từ thời điểm hiện tại)
        if (newPackage.getDurationDays() != null) {
            clinic.setExpiryDate(LocalDateTime.now().plusDays(newPackage.getDurationDays()));
        }
        
        // Reset lại số lượng đã phân tích về 0 khi mua gói mới (Hoặc giữ nguyên tùy nghiệp vụ)
        clinic.setTotalAnalyzed(0); 

        return clinicRepository.save(clinic);
    }

}