package com.nearprop.dto.chat;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessReportRequest {
    @NotNull(message = "Action is required")
    private String action; // IGNORE, WARN, REMOVE
    
    private String note;
} 