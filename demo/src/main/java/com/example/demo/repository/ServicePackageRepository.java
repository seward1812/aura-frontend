package com.example.demo.repository;

import com.example.demo.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho ServicePackage [FR-34].
 */
@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {
    
    // Tìm các gói đang hoạt động để hiển thị cho phòng khám chọn mua
    List<ServicePackage> findByIsActiveTrue();
    
    // Tìm gói theo tên (hữu ích cho việc kiểm tra dữ liệu hoặc cấu hình)
    ServicePackage findByName(String name);
}