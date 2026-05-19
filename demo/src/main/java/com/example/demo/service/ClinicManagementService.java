package com.example.demo.service;

import com.example.demo.entity.Clinic;
import com.example.demo.repository.ClinicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ClinicManagementService {

    @Autowired
    private ClinicRepository clinicRepository;

    /**
     * [FR-22] Đăng ký thông tin hệ thống phòng khám/tổ chức mới
     * Khởi tạo gói mặc định miễn phí ban đầu để hệ thống Billing vận hành không bị lỗi NullPointer
     */
    @Transactional
    public Clinic registerClinic(Clinic clinicData) {
        if (clinicRepository.findByTaxCode(clinicData.getTaxCode()).isPresent()) {
            throw new RuntimeException("Mã số thuế này đã được đăng ký bởi một tổ chức khác!");
        }

        Clinic clinic = new Clinic();
        clinic.setName(clinicData.getName());
        clinic.setTaxCode(clinicData.getTaxCode());
        clinic.setAddress(clinicData.getAddress());
        
        // Khởi tạo trạng thái ban đầu khớp cấu hình và logic BillingService hiện tại của bạn
        clinic.setTotalAnalyzed(0);
        clinic.setCurrentPackageLimit(1000); // Cấp hạn mức dùng thử 1000 ảnh ban đầu
        clinic.setExpiryDate(LocalDateTime.now().plusDays(30)); // Thời hạn dùng thử 30 ngày
        clinic.setHighRiskPatientsCount(0); // Chỉ số Dashboard mặc định bằng 0

        return clinicRepository.save(clinic);
    }
}