package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.Clinic;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ClinicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClinicRepository clinicRepository;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user != null && user.getPassword().equals(password) && Boolean .TRUE.equals(user.getIsActive())) {
            return user;
        }
        return null;
    }

    public User updateUser(
            Long id,
            User updatedUser
    ) {

        Optional<User> optionalUser =
                userRepository.findById(id);

        if (optionalUser.isPresent()) {

            User user = optionalUser.get();

            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());

            if (updatedUser.getRole() != null) user.setRole(updatedUser.getRole());
            if (updatedUser.getIsActive() != null) user.setIsActive(updatedUser.getIsActive());

            return userRepository.save(user);
        }
        return null;
    }

    public List<User> searchUsers(String name) {
        return userRepository
                .findByNameContaining(name);
    }

    @Transactional
    public User createAccountForClinic(Long clinicId, User newUserData) {
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng khám có ID: " + clinicId));

        if (userRepository.existsByEmail(newUserData.getEmail())) {
            throw new RuntimeException("Email này đã được đăng ký trên hệ thống!");
        }

        User user = new User();
        user.setName(newUserData.getName());
        user.setEmail(newUserData.getEmail());
        user.setPassword(newUserData.getPassword()); // Giữ nguyên cách lưu thô giống code cũ của bạn
        user.setRole(newUserData.getRole().toUpperCase()); // "DOCTOR" hoặc "PATIENT"
        user.setClinic(clinic); // Khóa ngoại liên kết chặt chẽ với bảng Clinics
        user.setIsActive(true); // Tài khoản mới tạo mặc định mở khóa hoạt động

        return userRepository.save(user);
    }

    /**
     * [FR-23] Xem danh sách thành viên nội bộ lọc theo vai trò
     */
    public List<User> getAccountsByClinicAndRole(Long clinicId, String role) {
        return userRepository.findByClinicIdAndRole(clinicId, role.toUpperCase());
    }

    /**
     * [FR-23] Tìm kiếm nhân sự/bệnh nhân nội bộ theo tên ký tự
     */
    public List<User> searchClinicAccounts(Long clinicId, String name) {
        return userRepository.findByClinicIdAndNameContainingIgnoreCase(clinicId, name);
    }

    /**
     * [FR-23] Khóa hoặc kích hoạt lại quyền truy cập hệ thống của nhân viên/bệnh nhân
     */
    @Transactional
    public User toggleAccountStatus(Long userId, boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản người dùng tương ứng."));
        user.setIsActive(isActive);
        return userRepository.save(user);
    }
}