package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * [FR-22] Quản lý tài khoản phòng khám.
 * [FR-27] Theo dõi số lượng ảnh đã phân tích và mức sử dụng gói.
 */
@Entity
@Table(name = "clinics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Tên tổ chức/phòng khám

    @Column(unique = true)
    private String taxCode; // Mã số thuế để xác minh danh tính [FR-22]

    private String address;

    // --- Thông tin gói dịch vụ hiện tại ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_package_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ServicePackage currentPackage; // [FR-28] Gói dịch vụ đang sử dụng

    @Column(nullable = false)
    @Builder.Default
    private Integer totalAnalyzed = 0; // [FR-27] Tổng số ảnh đã thực hiện phân tích

    @Column(nullable = false)
    private Integer currentPackageLimit; // Copy hạn mức từ ServicePackage tại thời điểm mua để đối soát

    private LocalDateTime expiryDate; // Ngày hết hạn của gói dịch vụ hiện tại

    // --- Thống kê rủi ro (Dành cho Dashboard) ---

    @Builder.Default
    private Integer highRiskPatientsCount = 0; // [FR-29] Số lượng bệnh nhân nguy cơ cao hiện tại

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}