package com.nearprop.dto.chat;

import com.nearprop.entity.ChatMessage.MessageStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatusUpdateRequest {
    @NotNull(message = "Status is required")
    private MessageStatus status;
} 