package com.nearprop.dto.chat;

import com.nearprop.entity.ChatAttachment.AttachmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatAttachmentDto {
    private Long id;
    private Long messageId;
    private String fileName;
    private String fileUrl;
    private String contentType;
    private Long fileSize;
    private AttachmentType type;
    private Integer width;
    private Integer height;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
} 