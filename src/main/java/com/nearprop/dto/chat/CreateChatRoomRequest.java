package com.nearprop.dto.chat;

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
public class CreateChatRoomRequest {
    @NotNull(message = "Property ID is required")
    private Long propertyId;
    
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;
    
    private Long sellerId; // Optional, if not provided will use property owner
} 