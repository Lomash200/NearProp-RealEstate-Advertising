package com.nearprop.dto;

import lombok.Data;

import java.util.Map;

@Data
public class FcmRequestDto {
    private String title;
    private String body;
    private Map<String, String> data;
}
