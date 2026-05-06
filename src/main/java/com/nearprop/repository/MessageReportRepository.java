package com.nearprop.repository;

import com.nearprop.entity.MessageReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageReportRepository extends JpaRepository<MessageReport, Long> {
    
    List<MessageReport> findByMessageId(Long messageId);
    
    Page<MessageReport> findByStatus(String status, Pageable pageable);
    
    List<MessageReport> findByReporterId(Long reporterId);
} 