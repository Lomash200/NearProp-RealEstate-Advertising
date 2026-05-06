package com.nearprop.repository;

import com.nearprop.entity.PropertyInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyInquiryRepository extends JpaRepository<PropertyInquiry, Long> {
    List<PropertyInquiry> findByDistrictId(Long districtId);
} 