package com.nearprop.dto.chat;

import com.nearprop.dto.UserSummaryDto;
import com.nearprop.entity.ChatMessage.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private Long id;
    private Long chatRoomId;
    private UserSummaryDto sender;
    private String content;
    private MessageStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private boolean isMine;
    
    // Admin fields
    private boolean isAdminMessage;
    private boolean edited;
    private LocalDateTime editedAt;
    private boolean reported;
    private boolean warned;
    
    @Builder.Default
    private List<ChatAttachmentDto> attachments = new ArrayList<>();

    public UserSummaryDto getSender() {
        return sender;
    }
} 