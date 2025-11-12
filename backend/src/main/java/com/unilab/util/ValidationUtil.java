package com.unilab.util;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * Utility class for validation operations
 */
public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9]{10,11}$"
    );
    
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile(
        "^#[0-9A-Fa-f]{6}$"
    );
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Validate hex color code
     */
    public static boolean isValidHexColor(String color) {
        if (color == null || color.trim().isEmpty()) {
            return false;
        }
        return HEX_COLOR_PATTERN.matcher(color).matches();
    }
    
    /**
     * Validate booking time range
     */
    public static boolean isValidBookingTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return false;
        }
        return startTime.isBefore(endTime);
    }
    
    /**
     * Validate booking is not in the past
     */
    public static boolean isBookingInFuture(LocalDateTime startTime) {
        return startTime.isAfter(LocalDateTime.now());
    }
    
    /**
     * Validate capacity
     */
    public static boolean isValidCapacity(Integer capacity, Integer maxCapacity) {
        if (capacity == null || maxCapacity == null) {
            return false;
        }
        return capacity > 0 && capacity <= maxCapacity;
    }
    
    /**
     * Validate string is not null or empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validate positive number
     */
    public static boolean isPositive(Number value) {
        if (value == null) {
            return false;
        }
        return value.doubleValue() > 0;
    }
    
    /**
     * Validate non-negative number
     */
    public static boolean isNonNegative(Number value) {
        if (value == null) {
            return false;
        }
        return value.doubleValue() >= 0;
    }
    
    /**
     * Validate booking duration in hours
     */
    public static boolean isValidDuration(LocalDateTime startTime, LocalDateTime endTime, 
                                         Integer minHours, Integer maxHours) {
        if (startTime == null || endTime == null) {
            return false;
        }
        
        long hours = DateTimeUtil.calculateHoursBetween(startTime, endTime);
        
        if (minHours != null && hours < minHours) {
            return false;
        }
        
        if (maxHours != null && hours > maxHours) {
            return false;
        }
        
        return true;
    }
}
