package com.nearprop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponseDto {
    private Long propertyId;
    private Long userId;
    private boolean isFavorite;
    private LocalDateTime createdAt;
} 