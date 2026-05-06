package com.nearprop.dto.franchisee;

import com.nearprop.dto.SubscriptionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseeDistrictSubscriptionAnalyticsDto {
    private District district;
    private Franchisee franchisee;
    private List<Subscription> subscriptions;
    private Analytics analytics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class District {
        private Long id;
        private String name;
        private String city;
        private String state;
        private String pincode;
        private Double latitude;
        private Double longitude;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Franchisee {
        private Long id;
        private String name;
        private String email;
        private String mobileNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Property {
        private Long id;
        private String title;
        private String address;
        private String status;
        // Add more fields as needed
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Subscription {
        private Long id;
        private User user;
        private Plan plan;
        private Coupon coupon;
        private BigDecimal price;
        private BigDecimal discountAmount;
        private BigDecimal originalPrice;
        private String startDate;
        private String endDate;
        private List<Property> properties;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private Long id;
        private String name;
        private String email;
        private String mobileNumber;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Plan {
        private Long id;
        private String name;
        private String type;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Coupon {
        private Long id;
        private String code;
        private BigDecimal discountAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Analytics {
        private int totalSubscriptions;
        private BigDecimal totalAmount;
        private BigDecimal franchiseeShare;
        private BigDecimal adminShare;
        private int subscriptionsWithCoupon;
        private int subscriptionsWithoutCoupon;
        private BigDecimal totalDiscountGiven;
    }
} 