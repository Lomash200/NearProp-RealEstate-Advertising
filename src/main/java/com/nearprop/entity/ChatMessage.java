package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @Column(nullable = false, length = 2000)
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime readAt;
    
    @Column
    private LocalDateTime deliveredAt;
    
    @Column(name = "is_admin_message")
    @Builder.Default
    private boolean isAdminMessage = false;
    
    @Column(name = "is_edited")
    @Builder.Default
    private boolean edited = false;
    
    @Column(name = "edited_at")
    private LocalDateTime editedAt;
    
    @Column(name = "is_reported")
    @Builder.Default
    private boolean reported = false;
    
    @Column(name = "is_warned")
    @Builder.Default
    private boolean warned = false;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    private ChatMessage parentMessage;
    
    @OneToMany(mappedBy = "parentMessage")
    @Builder.Default
    private List<ChatMessage> replies = new ArrayList<>();
    
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatAttachment> attachments = new ArrayList<>();
    
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MessageReport> reports = new ArrayList<>();
    
    // Helper methods
    public void markAsDelivered() {
        this.status = MessageStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }
    
    public void markAsRead() {
        this.status = MessageStatus.READ;
        this.readAt = LocalDateTime.now();
    }
    
    public void addAttachment(ChatAttachment attachment) {
        attachments.add(attachment);
        attachment.setMessage(this);
    }
    
    // Method to properly handle bidirectional relationship with ChatRoom
    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
    
    public enum MessageStatus {
        SENT,
        DELIVERED,
        READ
    }
} 