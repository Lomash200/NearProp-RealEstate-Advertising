package com.nearprop.service;

import com.nearprop.dto.chat.ChatMessageDto;
import com.nearprop.dto.chat.ChatNotificationDto;
import com.nearprop.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatSocketService {

    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Sends a chat message to all subscribers of a chat room topic
     */
    public void sendMessage(ChatMessageDto message) {
        messagingTemplate.convertAndSend("/topic/chat/" + message.getChatRoomId(), message);
    }
    
    /**
     * Sends a private notification to a specific user
     */
    public void sendPrivateNotification(String username, ChatNotificationDto notification) {
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", notification);
    }
    
    /**
     * Notifies all users in a chat room about typing activity
     */
    public void sendTypingStatus(Long chatRoomId, String username, boolean isTyping) {
        messagingTemplate.convertAndSend(
            "/topic/chat/" + chatRoomId + "/typing", 
            new TypingStatusDto(username, isTyping)
        );
    }
    
    /**
     * Sends a message read receipt to the sender
     */
    public void sendReadReceipt(Long messageId, String senderUsername) {
        messagingTemplate.convertAndSendToUser(
            senderUsername,
            "/queue/read-receipts",
            messageId
        );
    }
    
    /**
     * DTO for typing status updates
     */
    private static class TypingStatusDto {
        private final String username;
        private final boolean typing;
        
        public TypingStatusDto(String username, boolean typing) {
            this.username = username;
            this.typing = typing;
        }
        
        public String getUsername() {
            return username;
        }
        
        public boolean isTyping() {
            return typing;
        }
    }
} 