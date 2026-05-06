package com.nearprop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private ErrorDetails error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetails {
        private String code;
        private String message;
    }

    /**
     * Create a success response with data
     * @param message Success message
     * @param data Response data
     * @param <T> Type of data
     * @return ApiResponse with success=true and provided data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    /**
     * Create a success response without data
     * @param message Success message
     * @param <T> Type of data
     * @return ApiResponse with success=true and null data
     */
    public static <T> ApiResponse<T> success(String message) {
        return success(message, null);
    }

    /**
     * Create an error response
     * @param message Error message
     * @param <T> Type of data
     * @return ApiResponse with success=false and error details
     */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage("Error occurred");
        
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage(message);
        
        response.setError(errorDetails);
        return response;
    }

    /**
     * Create an error response with error code
     * @param message Error message
     * @param code Error code
     * @param <T> Type of data
     * @return ApiResponse with success=false and error details
     */
    public static <T> ApiResponse<T> error(String message, String code) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setSuccess(false);
        response.setMessage("Error occurred");
        
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage(message);
        errorDetails.setCode(code);
        
        response.setError(errorDetails);
        return response;
    }
}