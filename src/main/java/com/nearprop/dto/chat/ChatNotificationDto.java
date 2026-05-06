package com.nearprop.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatNotificationDto {
    private Long id;
    private String type; // MESSAGE, ROOM_ADDED, MENTION, etc.
    private String title;
    private String content;
    private Long senderId;
    private String senderName;
    private Long chatRoomId;
    private Long messageId;
    private boolean read;
} 