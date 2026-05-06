package com.nearprop.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

/**
 * Utility class for generating permanent IDs for users
 */
public class IdGenerator {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final AtomicInteger USER_COUNTER = new AtomicInteger(1);
    private static final AtomicInteger SELLER_COUNTER = new AtomicInteger(1);
    private static final AtomicInteger ADVISOR_COUNTER = new AtomicInteger(1);
    private static final AtomicInteger DEVELOPER_COUNTER = new AtomicInteger(1);
    private static final AtomicInteger PROPERTY_COUNTER = new AtomicInteger(1);
    private static final AtomicInteger PAYMENT_COUNTER = new AtomicInteger(1);
    private static final AtomicInteger COUPON_COUNTER = new AtomicInteger(1);
    
    private static final String USER_PREFIX = "RANPU";
    private static final String SELLER_PREFIX = "RANPS";
    private static final String ADVISOR_PREFIX = "RANPPA";
    private static final String DEVELOPER_PREFIX = "RANPD";
    private static final String PROPERTY_PREFIX = "RANP";
    private static final String PAYMENT_PREFIX = "RANPP";
    private static final String COUPON_PREFIX = "RANPC";
    
    /**
     * Generate a permanent ID for a regular user
     * Format: RANPU(Year)(month)(date)(milliseconds)1,2,3,4,.........
     * @return The generated ID
     */
    public static String generateUserId() {
        LocalDateTime now = LocalDateTime.now();
        String dateTimeStr = now.format(DATE_TIME_FORMAT);
        return USER_PREFIX + dateTimeStr + USER_COUNTER.getAndIncrement();
    }
    
    /**
     * Generate a permanent ID for a seller
     * Format: RANPS(Year)(month)(date)(milliseconds)1,2,3,4,.........
     * @return The generated ID
     */
    public static String generateSellerId() {
        LocalDateTime now = LocalDateTime.now();
        String dateTimeStr = now.format(DATE_TIME_FORMAT);
        return SELLER_PREFIX + dateTimeStr + SELLER_COUNTER.getAndIncrement();
    }
    
    /**
     * Generate a permanent ID for a property advisor
     * Format: RANPPA(Year)(month)(date)(milliseconds)1,2,3,4,.........
     * @return The generated ID
     */
    public static String generatePropertyAdvisorId() {
        LocalDateTime now = LocalDateTime.now();
        String dateTimeStr = now.format(DATE_TIME_FORMAT);
        return ADVISOR_PREFIX + dateTimeStr + ADVISOR_COUNTER.getAndIncrement();
    }
    
    /**
     * Generate a permanent ID for a developer
     * Format: RANPD(Year)(month)(date)(milliseconds)1,2,3,4,.........
     * @return The generated ID
     */
    public static String generateDeveloperId() {
        LocalDateTime now = LocalDateTime.now();
        String dateTimeStr = now.format(DATE_TIME_FORMAT);
        return DEVELOPER_PREFIX + dateTimeStr + DEVELOPER_COUNTER.getAndIncrement();
    }
    
    /**
     * Generate a permanent ID for a property
     * Format: RANP(Year)(month)(date)(milliseconds)1,2,3,4,.........
     * @return The generated ID
     */
    public static String generatePropertyId() {
        LocalDateTime now = LocalDateTime.now();
        String dateTimeStr = now.format(DATE_TIME_FORMAT);
        return PROPERTY_PREFIX + dateTimeStr + PROPERTY_COUNTER.getAndIncrement();
    }
    
    /**
     * Generate a payment ID in the format RANPP(Year)(month)(date)(milliseconds)[sequence number]
     * @return Formatted payment ID
     */
    public static String generatePaymentId() {
        LocalDateTime now = LocalDateTime.now();
        String dateTimeStr = now.format(DATE_TIME_FORMAT);
        return PAYMENT_PREFIX + dateTimeStr + PAYMENT_COUNTER.getAndIncrement();
    }
    
    /**
     * Generate a permanent ID for a coupon
     * Format: RANPC(Year)(month)(date)(milliseconds)1,2,3,4,.........
     * @return The generated ID
     */
    public static String generateCouponId() {
        LocalDateTime now = LocalDateTime.now();
        String dateTimeStr = now.format(DATE_TIME_FORMAT);
        return COUPON_PREFIX + dateTimeStr + COUPON_COUNTER.getAndIncrement();
    }
    
    /**
     * Generate a unique ID for a property update request
     * Format: RANPUR(Year)(month)(date)(milliseconds)(sequence)
     * Example: RANPUR202506111123456
     * 
     * @return A unique property update request ID
     */
    public static String generatePropertyUpdateRequestId() {
        LocalDateTime now = LocalDateTime.now();
        String dateTimeStr = now.format(DATE_TIME_FORMAT);
        
        // In a production environment, you would use a database sequence or atomic counter
        // For simplicity, we're using a random number here
        int sequence = new Random().nextInt(1000) + 1;
        
        return "RANPUR" + dateTimeStr + sequence;
    }
} 
