package com.example.demo.controller;

import com.example.demo.entity.Clinic;
import com.example.demo.entity.ServicePackage;
import com.example.demo.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller quản lý gói dịch vụ và thống kê.
 */
@RestController
@RequestMapping("/api/v1/billing")
@CrossOrigin(origins = "*") // Cho phép mọi nguồn gọi API (có thể giới hạn lại thành http://localhost:3000)
public class BillingController {

    @Autowired
    private BillingService billingService;

    /**
     * Lấy dữ liệu thống kê cho Dashboard
     * Endpoint: GET /api/v1/billing/stats/{clinicId}
     */
    @GetMapping("/stats/{clinicId}")
    public ResponseEntity<Clinic> getClinicStats(@PathVariable Long clinicId) {
        try {
            Clinic stats = billingService.getClinicDashboardStats(clinicId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Lấy danh sách các gói dịch vụ có sẵn để mua
     * Endpoint: GET /api/v1/billing/packages
     */
    @GetMapping("/packages")
    public ResponseEntity<List<ServicePackage>> getAvailablePackages() {
        return ResponseEntity.ok(billingService.getAllAvailablePackages());
    }

    /**
     * Thực hiện nâng cấp hoặc gia hạn gói dịch vụ
     * Endpoint: POST /api/v1/billing/upgrade
     */
    @PostMapping(value = "/upgrade", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> upgradePackage(@RequestBody Map<String, Long> request) {
        try {
            Long clinicId = request.get("clinicId");
            Long packageId = request.get("packageId");
            
            if (clinicId == null || packageId == null) {
                return ResponseEntity.badRequest().body(Map.of("status", "MISSING_PARAMS"));
            }

            Clinic updatedClinic = billingService.upgradePackage(clinicId, packageId);
            return ResponseEntity.ok(updatedClinic);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "ERROR", "message", e.getMessage()));
        }
    }

    /**
     * Endpoint giả lập để Test việc tăng số lượng ảnh đã phân tích
     * Trong thực tế, hàm này sẽ được gọi từ AI Core Service sau khi xử lý xong ảnh.
     */
    @PostMapping(value = "/track-usage/{clinicId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> trackUsage(@PathVariable Long clinicId, @RequestParam int count) {
        try {
            billingService.incrementAnalysisUsage(clinicId, count);
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (RuntimeException e) {
            String errorStatus = e.getMessage();
            if ("LIMIT_EXCEEDED".equals(errorStatus)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("status", "LIMIT_EXCEEDED"));
            } else if ("PACKAGE_EXPIRED".equals(errorStatus)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("status", "PACKAGE_EXPIRED"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", "UNKNOWN_ERROR"));
        }
    }
}