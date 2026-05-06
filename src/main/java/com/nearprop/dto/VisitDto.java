package com.nearprop.dto;

import com.nearprop.entity.Visit.VisitStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitDto {
    private Long id;
    private PropertyDto property;
    private UserSummaryDto user;
    private LocalDateTime scheduledTime;
    private String notes;
    private VisitStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 