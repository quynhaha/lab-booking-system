package com.unilab.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for date and time operations
 */
public class DateTimeUtil {
    
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    /**
     * Check if two datetime ranges overlap
     */
    public static boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, 
                                       LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
    
    /**
     * Calculate hours between two datetime
     */
    public static long calculateHoursBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.HOURS.between(start, end);
    }
    
    /**
     * Calculate days between two datetime
     */
    public static long calculateDaysBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.DAYS.between(start, end);
    }
    
    /**
     * Check if datetime is in the past
     */
    public static boolean isPast(LocalDateTime dateTime) {
        return dateTime.isBefore(LocalDateTime.now());
    }
    
    /**
     * Check if datetime is in the future
     */
    public static boolean isFuture(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now());
    }
    
    /**
     * Get start of day
     */
    public static LocalDateTime getStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }
    
    /**
     * Get end of day
     */
    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }
    
    /**
     * Format datetime for display
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        return dateTime.format(DISPLAY_FORMATTER);
    }
    
    /**
     * Check if datetime is within grace period
     */
    public static boolean isWithinGracePeriod(LocalDateTime bookingTime, int gracePeriodHours) {
        LocalDateTime now = LocalDateTime.now();
        long hoursUntilBooking = ChronoUnit.HOURS.between(now, bookingTime);
        return hoursUntilBooking >= gracePeriodHours;
    }
    
    /**
     * Check if cancellation is late (within grace period)
     */
    public static boolean isLateCancellation(LocalDateTime bookingTime, int gracePeriodHours) {
        return !isWithinGracePeriod(bookingTime, gracePeriodHours);
    }
    
    /**
     * Add hours to datetime
     */
    public static LocalDateTime addHours(LocalDateTime dateTime, long hours) {
        return dateTime.plusHours(hours);
    }
    
    /**
     * Add days to datetime
     */
    public static LocalDateTime addDays(LocalDateTime dateTime, long days) {
        return dateTime.plusDays(days);
    }
}
