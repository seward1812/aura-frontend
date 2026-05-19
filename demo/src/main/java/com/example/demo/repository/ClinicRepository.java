package com.example.demo.repository;

import com.example.demo.entity.Clinic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Clinic [FR-22, FR-27, FR-29].
 */
@Repository
public interface ClinicRepository extends JpaRepository<Clinic, Long> {

    // [FR-22] Tìm phòng khám theo mã số thuế để xác minh tổ chức
    Optional<Clinic> findByTaxCode(String taxCode);

    /**
     * [FR-27] Truy vấn nhanh tỷ lệ sử dụng gói dịch vụ.
     * Trả về phần trăm (e.g., 75.5%)
     */
    @Query("SELECT (CAST(c.totalAnalyzed AS double) / c.currentPackageLimit) * 100 " +
           "FROM Clinic c WHERE c.id = :clinicId")
    Double calculateUsagePercentage(@Param("clinicId") Long clinicId);

    // [FR-29] Tìm các phòng khám có số lượng bệnh nhân nguy cơ cao vượt ngưỡng
    // Giúp Admin hoặc Clinic Manager theo dõi xu hướng bất thường
    List<Clinic> findByHighRiskPatientsCountGreaterThan(Integer threshold);
}