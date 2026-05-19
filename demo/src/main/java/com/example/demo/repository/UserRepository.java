package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository
        extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByNameContaining(String name);

    // --- Bổ sung các hàm truy vấn nội bộ cho phòng khám [FR-23] ---

    List<User> findByRole(String role);
    
    // Kiểm tra trùng email khi phòng khám cấp tài khoản mới
    boolean existsByEmail(String email);

    // Lấy danh sách tài khoản theo từng phòng khám và phân loại vai trò (Bác sĩ/Bệnh nhân)
    List<User> findByClinicIdAndRole(Long clinicId, String role);

    // Tìm kiếm nhanh nhân sự/bệnh nhân trong cùng một phòng khám theo tên (không phân biệt hoa thường)
    List<User> findByClinicIdAndNameContainingIgnoreCase(Long clinicId, String name);
}