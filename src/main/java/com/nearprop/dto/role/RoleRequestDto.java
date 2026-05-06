package com.nearprop.dto.role;

import com.nearprop.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleRequestDto {
    @NotNull
    private Role role;
    
    private String reason;
    
    @Builder.Default
    private List<String> documentUrls = new ArrayList<>();
} 