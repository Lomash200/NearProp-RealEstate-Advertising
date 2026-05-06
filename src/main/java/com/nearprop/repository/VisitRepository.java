package com.nearprop.repository;

import com.nearprop.entity.Visit;
import com.nearprop.entity.Visit.VisitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {
    Page<Visit> findByUserId(Long userId, Pageable pageable);
    
    Page<Visit> findByPropertyId(Long propertyId, Pageable pageable);
    
    Page<Visit> findByStatus(VisitStatus status, Pageable pageable);
    
    List<Visit> findByPropertyIdAndScheduledTimeBetween(Long propertyId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT v FROM Visit v WHERE v.property.id = :propertyId AND v.status IN ('PENDING', 'CONFIRMED') AND v.scheduledTime BETWEEN :start AND :end")
    List<Visit> findScheduleConflicts(Long propertyId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT v FROM Visit v WHERE v.property.owner.id = :ownerId")
    Page<Visit> findByPropertyOwnerId(Long ownerId, Pageable pageable);
    @Query("SELECT COUNT(v) FROM Visit v WHERE v.property.owner.id = :ownerId AND v.status IN ('CONFIRMED', 'PENDING')")
    int countScheduledAndPendingVisitsByOwnerId(Long ownerId);
} 