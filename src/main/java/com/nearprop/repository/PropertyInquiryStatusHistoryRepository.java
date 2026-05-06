package com.nearprop.repository;

import com.nearprop.entity.PropertyInquiryStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyInquiryStatusHistoryRepository extends JpaRepository<PropertyInquiryStatusHistory, Long> {
} 