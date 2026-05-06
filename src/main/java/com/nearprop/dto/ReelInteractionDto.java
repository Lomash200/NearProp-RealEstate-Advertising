package com.nearprop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nearprop.entity.ReelInteraction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReelInteractionDto {
    
    private Long id;
    
    @NotNull(message = "Reel ID is required")
    private Long reelId;
    
    private UserDto user;
    
    @NotNull(message = "Interaction type is required")
    private ReelInteraction.InteractionType type;
    
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;
    
    private LocalDateTime createdAt;
} 