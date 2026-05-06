package com.nearprop.dto;

import com.nearprop.entity.Visit.VisitStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVisitStatusDto {
    @NotNull(message = "Status must not be null")
    private VisitStatus status;
    
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
} 