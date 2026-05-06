package com.nearprop.service;

import com.nearprop.dto.CreateVisitDto;
import com.nearprop.dto.UpdateVisitStatusDto;
import com.nearprop.dto.VisitDto;
import com.nearprop.entity.Visit.VisitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitService {
    VisitDto scheduleVisit(CreateVisitDto visitDto, Long userId);
    
    VisitDto getVisit(Long visitId);
    
    VisitDto updateVisitStatus(Long visitId, UpdateVisitStatusDto updateDto, Long userId);
    
    void cancelVisit(Long visitId, Long userId);
    
    Page<VisitDto> getUserVisits(Long userId, Pageable pageable);
    
    Page<VisitDto> getPropertyVisits(Long propertyId, Pageable pageable);
    
    Page<VisitDto> getOwnerVisits(Long ownerId, Pageable pageable);
    
    Page<VisitDto> getVisitsByStatus(VisitStatus status, Pageable pageable);
    Page<VisitDto> getAllVisits(Pageable pageable);


    List<VisitDto> checkAvailability(Long propertyId, LocalDateTime startTime, LocalDateTime endTime);
    
    boolean hasScheduleConflict(Long propertyId, LocalDateTime scheduledTime);
} 