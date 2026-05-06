package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.chat.*;
import com.nearprop.entity.ChatMessage.MessageStatus;
import com.nearprop.entity.User;
import com.nearprop.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;
    
    // Chat room endpoints
    @PostMapping("/rooms")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatRoomDto> createChatRoom(
            @Valid @RequestBody CreateChatRoomRequest request,
            @AuthenticationPrincipal User currentUser) {
        ChatRoomDto chatRoom = chatService.createChatRoom(request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(chatRoom);
    }
    
    @GetMapping("/rooms/{chatRoomId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatRoomDto> getChatRoom(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal User currentUser) {
        ChatRoomDto chatRoom = chatService.getChatRoom(chatRoomId, currentUser.getId());
        return ResponseEntity.ok(chatRoom);
    }
    
    @GetMapping("/rooms")
//    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatRoomDto>> getUserChatRooms(
            @RequestParam(defaultValue = "lastMessageAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @AuthenticationPrincipal User currentUser) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<ChatRoomDto> chatRooms = chatService.getUserChatRooms(currentUser.getId());
        return ResponseEntity.ok(chatRooms);
    }
    
    @GetMapping("/property/{propertyId}/rooms")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatRoomDto>> getPropertyChatRooms(
            @PathVariable Long propertyId,
            @AuthenticationPrincipal User currentUser) {
        List<ChatRoomDto> chatRooms = chatService.getPropertyChatRooms(propertyId, currentUser.getId());
        return ResponseEntity.ok(chatRooms);
    }
    
    // Admin endpoints for chat rooms
    @GetMapping("/admin/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChatRoomDto>> getAllChatRooms(
            @RequestParam(defaultValue = "lastMessageAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @AuthenticationPrincipal User currentUser) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<ChatRoomDto> chatRooms = chatService.getAllChatRoomsWithoutPagination(sortBy, sortDirection);
        return ResponseEntity.ok(chatRooms);
    }
    
    @PostMapping("/rooms/{chatRoomId}/participants")
   // @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatRoomDto> addParticipantToChatRoom(
            @PathVariable Long chatRoomId,
            @Valid @RequestBody AddParticipantRequest request,
            @AuthenticationPrincipal User currentUser) {
        ChatRoomDto chatRoom = chatService.addParticipantToChatRoom(chatRoomId, request.getUserId(), currentUser.getId());
        return ResponseEntity.ok(chatRoom);
    }
    
    @DeleteMapping("/rooms/{chatRoomId}/participants/{userId}")
//    @PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatRoomDto> removeParticipantFromChatRoom(
            @PathVariable Long chatRoomId,
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        ChatRoomDto chatRoom = chatService.removeParticipantFromChatRoom(chatRoomId, userId, currentUser.getId());
        return ResponseEntity.ok(chatRoom);
    }
    
    @PutMapping("/rooms/{chatRoomId}/close")
   // @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatRoomDto> closeChatRoom(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal User currentUser) {
        ChatRoomDto chatRoom = chatService.updateChatRoomStatus(chatRoomId, "CLOSED", currentUser.getId());
        return ResponseEntity.ok(chatRoom);
    }
    
    @PutMapping("/rooms/{chatRoomId}/reopen")
   // @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatRoomDto> reopenChatRoom(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal User currentUser) {
        ChatRoomDto chatRoom = chatService.updateChatRoomStatus(chatRoomId, "ACTIVE", currentUser.getId());
        return ResponseEntity.ok(chatRoom);
    }
    
    // Message endpoints
    @PostMapping("/rooms/{chatRoomId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatMessageDto> sendMessage(
            @PathVariable Long chatRoomId,
            @Valid @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal User currentUser) {
        ChatMessageDto message = chatService.sendMessage(chatRoomId, request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
    
    // Admin message endpoint
    @PostMapping("/admin/rooms/{chatRoomId}/messages")
    @PreAuthorize("hasRole('ADMIN')")

    public ResponseEntity<ChatMessageDto> sendAdminMessage(
            @PathVariable Long chatRoomId,
            @Valid @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal User currentUser) {
        ChatMessageDto message = chatService.sendAdminMessage(chatRoomId, request, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }
    
    @GetMapping("/messages/{messageId}/thread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageThreadDto> getMessageThread(
            @PathVariable Long messageId,
            @AuthenticationPrincipal User currentUser) {
        MessageThreadDto thread = chatService.getMessageThread(messageId, currentUser.getId());
        return ResponseEntity.ok(thread);
    }
    
    @GetMapping("/rooms/{chatRoomId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatMessageDto>> getChatMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "false") boolean includeReplies,
            @AuthenticationPrincipal User currentUser) {
        List<ChatMessageDto> messages = chatService.getAllChatMessagesWithoutPagination(chatRoomId, currentUser.getId(), includeReplies);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/rooms/{chatRoomId}/messages/recent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatMessageDto>> getRecentMessages(
            @PathVariable Long chatRoomId,
            @RequestParam(defaultValue = "20") int limit,
            @AuthenticationPrincipal User currentUser) {
        List<ChatMessageDto> messages = chatService.getRecentMessages(chatRoomId, currentUser.getId(), limit);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/rooms/{chatRoomId}/messages/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatMessageDto>> getUnreadMessages(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal User currentUser) {
        List<ChatMessageDto> messages = chatService.getUnreadMessages(chatRoomId, currentUser.getId());
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/rooms/{chatRoomId}/messages/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> markMessagesAsRead(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal User currentUser) {
        int count = chatService.markMessagesAsRead(chatRoomId, currentUser.getId());
        
        Map<String, Object> data = new HashMap<>();
        data.put("chatRoomId", chatRoomId);
        data.put("count", count);
        
        return ResponseEntity.ok(ApiResponse.success("Messages marked as read", data));
    }
    
    @PatchMapping("/messages/{messageId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatMessageDto> updateMessageStatus(
            @PathVariable Long messageId,
            @Valid @RequestBody MessageStatusUpdateRequest request,
            @AuthenticationPrincipal User currentUser) {
        ChatMessageDto message = chatService.updateMessageStatus(messageId, request.getStatus(), currentUser.getId());
        return ResponseEntity.ok(message);
    }
    
    @PutMapping("/messages/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatMessageDto> editMessage(
            @PathVariable Long messageId,
            @Valid @RequestBody EditMessageRequest request,
            @AuthenticationPrincipal User currentUser) {
        ChatMessageDto message = chatService.editMessage(messageId, request.getContent(), currentUser.getId());
        return ResponseEntity.ok(message);
    }
    
    @DeleteMapping("/messages/{messageId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> deleteMessage(
            @PathVariable Long messageId,
            @AuthenticationPrincipal User currentUser) {
        chatService.deleteMessage(messageId, currentUser.getId());
        
        Map<String, Object> data = new HashMap<>();
        data.put("messageId", messageId);
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success("Message deleted", data));
    }
    
    // Admin message management endpoints
    @GetMapping("/admin/messages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChatMessageDto>> getAllMessages(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal User currentUser) {
        List<ChatMessageDto> messages = chatService.getAllMessagesWithoutPagination(status);
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/messages/{messageId}/report")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> reportMessage(
            @PathVariable Long messageId,
            @Valid @RequestBody ReportMessageRequest request,
            @AuthenticationPrincipal User currentUser) {
        Long reportId = chatService.reportMessage(messageId, request, currentUser.getId());
        
        Map<String, Object> data = new HashMap<>();
        data.put("reportId", reportId);
        data.put("messageId", messageId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Message reported successfully", data));
    }
    
    @PostMapping("/admin/messages/{messageId}/process-report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> processReportedMessage(
            @PathVariable Long messageId,
            @Valid @RequestBody ProcessReportRequest request,
            @AuthenticationPrincipal User currentUser) {
        chatService.processReportedMessage(messageId, request, currentUser.getId());
        
        Map<String, Object> data = new HashMap<>();
        data.put("messageId", messageId);
        data.put("action", request.getAction());
        
        return ResponseEntity.ok(ApiResponse.success("Report processed successfully", data));
    }
    
    @PostMapping(value = "/rooms/{chatRoomId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ChatAttachmentDto> uploadAttachment(
            @PathVariable Long chatRoomId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser) {
        ChatAttachmentDto attachment = chatService.uploadAttachment(chatRoomId, file, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(attachment);
    }
    
    @GetMapping("/rooms/{chatRoomId}/attachments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ChatAttachmentDto>> getChatAttachments(
            @PathVariable Long chatRoomId,
            @AuthenticationPrincipal User currentUser) {
        List<ChatAttachmentDto> attachments = chatService.getChatAttachments(chatRoomId, currentUser.getId());
        return ResponseEntity.ok(attachments);
    }
    
    @DeleteMapping("/attachments/{attachmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> deleteAttachment(
            @PathVariable Long attachmentId,
            @AuthenticationPrincipal User currentUser) {
        chatService.deleteAttachment(attachmentId, currentUser.getId());
        
        Map<String, Object> data = new HashMap<>();
        data.put("attachmentId", attachmentId);
        
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success("Attachment deleted", data));
    }
} 