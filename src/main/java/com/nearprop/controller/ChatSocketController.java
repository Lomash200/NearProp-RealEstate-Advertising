package com.nearprop.controller;

import com.nearprop.dto.chat.ChatMessageDto;
import com.nearprop.dto.chat.SendMessageRequest;
import com.nearprop.entity.User;
import com.nearprop.service.ChatService;
import com.nearprop.service.ChatSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatSocketController {

    private final ChatService chatService;
    private final ChatSocketService chatSocketService;
    
    @MessageMapping("/chat/{chatRoomId}/send")
    public void sendMessage(
            @DestinationVariable Long chatRoomId,
            @Payload SendMessageRequest request,
            Principal principal,
            SimpMessageHeaderAccessor headerAccessor) {
        
        Long userId = getUserIdFromPrincipal(principal);
        
        ChatMessageDto message = chatService.sendMessage(chatRoomId, request, userId);
        
        // Broadcast the message to all subscribers (clients)
        chatSocketService.sendMessage(message);
        
        log.debug("WebSocket message sent to room {}: {}", chatRoomId, message);
    }
    
    @MessageMapping("/chat/{chatRoomId}/typing")
    public void typingStatus(
            @DestinationVariable Long chatRoomId,
            @Payload Boolean isTyping,
            Principal principal) {
        
        Long userId = getUserIdFromPrincipal(principal);
        User user = chatService.getUserById(userId);
        
        chatSocketService.sendTypingStatus(chatRoomId, user.getUsername(), isTyping);
        
        log.debug("User {} typing status in room {}: {}", user.getUsername(), chatRoomId, isTyping);
    }
    
    @MessageMapping("/chat/message/{messageId}/read")
    public void markMessageAsRead(
            @DestinationVariable Long messageId,
            Principal principal) {
        
        Long userId = getUserIdFromPrincipal(principal);
        
        ChatMessageDto message = chatService.updateMessageStatus(
                messageId, 
                com.nearprop.entity.ChatMessage.MessageStatus.READ,
                userId);
        
        // Send a read receipt to the sender
        if (message.getSender() != null && !message.getSender().getId().equals(userId)) {
            User sender = chatService.getUserById(message.getSender().getId());
            String senderUsername = sender.getUsername();
            chatSocketService.sendReadReceipt(messageId, senderUsername);
        }
        
        log.debug("Message {} marked as read by user {}", messageId, userId);
    }
    
    @MessageMapping("/admin/chat/{chatRoomId}/send")
    public void sendAdminMessage(
            @DestinationVariable Long chatRoomId,
            @Payload SendMessageRequest request,
            Principal principal) {
        
        Long adminId = getUserIdFromPrincipal(principal);
        
        ChatMessageDto message = chatService.sendAdminMessage(chatRoomId, request, adminId);
        
        // Broadcast the message to all subscribers (clients)
        chatSocketService.sendMessage(message);
        
        log.debug("Admin WebSocket message sent to room {}: {}", chatRoomId, message);
    }
    
    // Helper method to extract user ID from Principal
    private Long getUserIdFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("User not authenticated");
        }
        return Long.parseLong(principal.getName());
    }
} 