package com.nearprop.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVisitDto {
    @NotNull(message = "Property ID must not be null")
    private Long propertyId;
    
    @NotNull(message = "Scheduled time must not be null")
    @Future(message = "Scheduled time must be in the future")
    private LocalDateTime scheduledTime;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
} 