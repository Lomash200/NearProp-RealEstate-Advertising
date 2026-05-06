package com.nearprop.repository;

import com.nearprop.entity.MonthlyRevenueReport;
import com.nearprop.entity.MonthlyRevenueReport.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyRevenueReportRepository extends JpaRepository<MonthlyRevenueReport, Long> {
    
    List<MonthlyRevenueReport> findByFranchiseeId(Long franchiseeId);
    
    List<MonthlyRevenueReport> findByFranchiseeDistrictId(Long franchiseeDistrictId);
    
    List<MonthlyRevenueReport> findByYearAndMonth(Integer year, Integer month);
    
    Optional<MonthlyRevenueReport> findByYearAndMonthAndFranchiseeId(Integer year, Integer month, Long franchiseeId);
    
    Optional<MonthlyRevenueReport> findByYearAndMonthAndFranchiseeDistrictId(Integer year, Integer month, Long franchiseeDistrictId);
    
    List<MonthlyRevenueReport> findByReportStatus(ReportStatus status);
    
    @Query("SELECT COUNT(r) FROM MonthlyRevenueReport r WHERE r.year = :year AND r.month = :month")
    Long countByYearAndMonth(Integer year, Integer month);
    
    @Query("SELECT r FROM MonthlyRevenueReport r WHERE r.franchiseeDistrict.id = :districtId ORDER BY r.year DESC, r.month DESC")
    List<MonthlyRevenueReport> findByFranchiseeDistrictIdOrderByDateDesc(Long districtId);
    
    @Query("SELECT r FROM MonthlyRevenueReport r WHERE r.franchisee.id = :franchiseeId ORDER BY r.year DESC, r.month DESC")
    List<MonthlyRevenueReport> findByFranchiseeIdOrderByDateDesc(Long franchiseeId);
} 