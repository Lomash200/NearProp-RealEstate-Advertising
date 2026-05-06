package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_attachments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private ChatMessage message;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id")
    private User uploader;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
    
    @Column(nullable = false)
    private String fileName;
    
    @Column(nullable = false)
    private String fileUrl;
    
    @Column
    private String filePath;
    
    @Column(nullable = false)
    private String contentType;
    
    @Column(nullable = false)
    private Long fileSize;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttachmentType type;
    
    @Column
    private Integer width;
    
    @Column
    private Integer height;
    
    @Column
    private String thumbnailUrl;
    
    @Column
    private String thumbnailPath;
    
    // Explicitly define setMessage method for bi-directional relationship
    public void setMessage(ChatMessage message) {
        this.message = message;
    }
    
    public enum AttachmentType {
        IMAGE,
        DOCUMENT,
        VIDEO,
        AUDIO
    }
} 