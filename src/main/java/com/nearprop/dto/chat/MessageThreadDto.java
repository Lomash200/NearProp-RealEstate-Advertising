package com.nearprop.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageThreadDto {
    private ChatMessageDto message;
    private List<ChatMessageDto> replies;
} 