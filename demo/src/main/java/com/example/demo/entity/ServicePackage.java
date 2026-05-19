package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * [FR-34] Quản lý các gói dịch vụ, giá cả và mô hình thanh toán.
 */
@Entity
@Table(name = "service_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Tên gói: e.g., "Basic Plan", "Professional", "Unlimited"

    @Column(nullable = false)
    private Integer imageLimit; // [FR-27] Hạn mức số lượng ảnh được phân tích (e.g., 1000, 5000)

    @Column(nullable = false)
    private BigDecimal price; // [FR-34] Giá của gói dịch vụ

    @Column(length = 500)
    private String description; // Mô tả các tính năng đi kèm của gói

    private Integer durationDays; // Thời hạn sử dụng gói (ví dụ: 30 ngày, 365 ngày)

    @Builder.Default
    private Boolean isActive = true; // Trạng thái gói có đang kinh doanh hay không
}