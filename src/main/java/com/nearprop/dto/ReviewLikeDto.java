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
public class ReviewLikeDto {
    private Long id;
    private Long reviewId;
    private UserSummaryDto user;
    private LocalDateTime createdAt;
} 