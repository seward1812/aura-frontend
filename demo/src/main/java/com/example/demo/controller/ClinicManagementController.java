package com.example.demo.controller;

import com.example.demo.entity.Clinic;
import com.example.demo.entity.User;
import com.example.demo.service.ClinicManagementService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clinics")
@CrossOrigin(origins = "*") // Ngăn chặn hoàn toàn lỗi CORS khi gọi dữ liệu từ ứng dụng React
public class ClinicManagementController {

    @Autowired
    private ClinicManagementService clinicService;

    @Autowired
    private UserService userService;

    /**
     * [FR-22] Endpoint đăng ký thông tin tổ chức phòng khám mới
     * POST http://localhost:8080/api/v1/clinics/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerClinic(@RequestBody Clinic clinic) {
        try {
            Clinic newClinic = clinicService.registerClinic(clinic);
            return ResponseEntity.ok(newClinic);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * [FR-23] Phòng khám tự tạo tài khoản con cho Bác sĩ hoặc Bệnh nhân
     * POST http://localhost:8080/api/v1/clinics/{clinicId}/accounts
     */
    @PostMapping("/{clinicId}/accounts")
    public ResponseEntity<?> createAccount(
            @PathVariable Long clinicId,
            @RequestBody User user) {
        try {
            User newUser = userService.createAccountForClinic(clinicId, user);
            return ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * [FR-23] Lấy danh sách tài khoản thuộc phòng khám lọc cụ thể theo Vai trò (role)
     * GET http://localhost:8080/api/v1/clinics/{clinicId}/accounts?role=DOCTOR
     */
    @GetMapping("/{clinicId}/accounts")
    public ResponseEntity<List<User>> getAccounts(
            @PathVariable Long clinicId,
            @RequestParam String role) {
        return ResponseEntity.ok(userService.getAccountsByClinicAndRole(clinicId, role));
    }

    /**
     * [FR-23] Tìm kiếm nhanh nhân sự phòng khám nội bộ theo ký tự tên
     * GET http://localhost:8080/api/v1/clinics/{clinicId}/accounts/search?name=Nguyen
     */
    @GetMapping("/{clinicId}/accounts/search")
    public ResponseEntity<List<User>> searchAccounts(
            @PathVariable Long clinicId,
            @RequestParam String name) {
        return ResponseEntity.ok(userService.searchClinicAccounts(clinicId, name));
    }

    /**
     * [FR-23] Khóa/Vô hiệu hóa quyền truy cập hệ thống của một tài khoản con
     * PUT http://localhost:8080/api/v1/clinics/accounts/{userId}/status?isActive=false
     */
    @PutMapping("/accounts/{userId}/status")
    public ResponseEntity<?> changeAccountStatus(
            @PathVariable Long userId,
            @RequestParam boolean isActive) {
        try {
            User updatedUser = userService.toggleAccountStatus(userId, isActive);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}