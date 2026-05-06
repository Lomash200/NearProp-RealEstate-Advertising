package com.nearprop.dto;

import com.nearprop.entity.PropertyInquiry;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PropertyInquiryDto {
    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private PropertyInquiry.InfoType infoType;
    private PropertyInquiry.InquiryStatus status;
    private String propertyType; // Use string for flexibility
    private Double maxPrice;
    private Integer bedrooms;
    private Integer bathrooms;
    private String minSize;
    private String state;
    private String city;
    private String area;
    private String zipCode;
    private Long districtId;
    private Double latitude;
    private Double longitude;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private List<StatusHistoryDto> statusHistory;

    @Data
    public static class StatusHistoryDto {
        private PropertyInquiry.InquiryStatus status;
        private String comment;
        private Long updatedBy;
        private LocalDateTime updatedAt;
    }
} 