package com.nearprop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
    private Object errors;
    
    // Static factory methods for common responses
    public static <T> ResponseDto<T> success(T data) {
        return ResponseDto.<T>builder()
                .success(true)
                .data(data)
                .build();
    }
    
    public static <T> ResponseDto<T> success(String message, T data) {
        return ResponseDto.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }
    
    public static <T> ResponseDto<T> error(String message) {
        return ResponseDto.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
    
    public static <T> ResponseDto<T> error(String message, Object errors) {
        return ResponseDto.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }
} 