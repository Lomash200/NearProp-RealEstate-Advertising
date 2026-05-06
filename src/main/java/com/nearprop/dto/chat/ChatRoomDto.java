package com.nearprop.dto.chat;

import com.nearprop.dto.PropertySummaryDto;
import com.nearprop.dto.UserSummaryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    private Long id;
    private String title;
    private PropertySummaryDto property;
    private UserSummaryDto buyer;
    private UserSummaryDto seller;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private ChatMessageDto lastMessage;
    private long unreadCount;
    private boolean isParticipant;
} 