package com.nearprop.dto.role;

import com.nearprop.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRoleRequestDto {
    @NotNull
    private Boolean approved;
    
    private String comment;
    
    private User processedBy;
} 