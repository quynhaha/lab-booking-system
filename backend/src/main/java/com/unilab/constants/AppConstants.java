package com.unilab.constants;

/**
 * Application-wide constants
 */
public class AppConstants {
    
    // API Version
    public static final String API_VERSION = "/api/v1";
    
    // Booking Status
    public static final String BOOKING_STATUS_PENDING = "PENDING";
    public static final String BOOKING_STATUS_CONFIRMED = "CONFIRMED";
    public static final String BOOKING_STATUS_CANCELLED = "CANCELLED";
    public static final String BOOKING_STATUS_COMPLETED = "COMPLETED";
    public static final String BOOKING_STATUS_NO_SHOW = "NO_SHOW";
    
    // Lab Status
    public static final String LAB_STATUS_AVAILABLE = "AVAILABLE";
    public static final String LAB_STATUS_MAINTENANCE = "MAINTENANCE";
    public static final String LAB_STATUS_UNAVAILABLE = "UNAVAILABLE";
    
    // Refund Status
    public static final String REFUND_STATUS_PENDING = "PENDING";
    public static final String REFUND_STATUS_APPROVED = "APPROVED";
    public static final String REFUND_STATUS_REJECTED = "REJECTED";
    public static final String REFUND_STATUS_COMPLETED = "COMPLETED";
    
    // Penalty Types
    public static final String PENALTY_TYPE_FINE = "FINE";
    public static final String PENALTY_TYPE_SUSPENSION = "SUSPENSION";
    public static final String PENALTY_TYPE_WARNING = "WARNING";
    public static final String PENALTY_TYPE_POINTS = "POINTS";
    
    // Violation Types
    public static final String VIOLATION_LATE_CANCELLATION = "LATE_CANCELLATION";
    public static final String VIOLATION_NO_SHOW = "NO_SHOW";
    public static final String VIOLATION_EQUIPMENT_DAMAGE = "EQUIPMENT_DAMAGE";
    public static final String VIOLATION_FACILITY_MISUSE = "FACILITY_MISUSE";
    public static final String VIOLATION_REPEATED_VIOLATION = "REPEATED_VIOLATION";
    
    // Penalty Severity
    public static final String SEVERITY_LOW = "LOW";
    public static final String SEVERITY_MEDIUM = "MEDIUM";
    public static final String SEVERITY_HIGH = "HIGH";
    public static final String SEVERITY_CRITICAL = "CRITICAL";
    
    // User Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_STAFF = "STAFF";
    public static final String ROLE_STUDENT = "STUDENT";
    
    // Rule Types
    public static final String RULE_MAX_ADVANCE_DAYS = "MAX_ADVANCE_DAYS";
    public static final String RULE_MIN_ADVANCE_HOURS = "MIN_ADVANCE_HOURS";
    public static final String RULE_MAX_DURATION = "MAX_DURATION";
    public static final String RULE_MIN_DURATION = "MIN_DURATION";
    public static final String RULE_MAX_CONCURRENT = "MAX_CONCURRENT";
    
    // Rule Applies To
    public static final String APPLIES_TO_ALL = "ALL";
    public static final String APPLIES_TO_STUDENT = "STUDENT";
    public static final String APPLIES_TO_STAFF = "STAFF";
    
    // Refund Calculation
    public static final double REFUND_RATE_FULL = 1.0;
    public static final double REFUND_RATE_PARTIAL = 0.5;
    public static final double REFUND_RATE_NONE = 0.0;
    
    // Grace Periods (in hours)
    public static final int DEFAULT_GRACE_PERIOD_HOURS = 24;
    public static final int MINIMUM_GRACE_PERIOD_HOURS = 2;
    
    // Booking Limits
    public static final int MAX_BOOKING_DURATION_HOURS = 168; // 7 days
    public static final int MIN_BOOKING_DURATION_HOURS = 1;
    public static final int MAX_ADVANCE_BOOKING_DAYS = 30;
    public static final int MAX_CONCURRENT_BOOKINGS = 3;
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    
    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DISPLAY_FORMAT = "dd/MM/yyyy HH:mm";
    
    // Report Types
    public static final String REPORT_TYPE_BOOKING = "BOOKING";
    public static final String REPORT_TYPE_REVENUE = "REVENUE";
    public static final String REPORT_TYPE_PENALTY = "PENALTY";
    public static final String REPORT_TYPE_LAB_USAGE = "LAB_USAGE";
    public static final String REPORT_TYPE_USER_ACTIVITY = "USER_ACTIVITY";
    
    // Success Messages
    public static final String MSG_SUCCESS = "Operation completed successfully";
    public static final String MSG_CREATED = "Resource created successfully";
    public static final String MSG_UPDATED = "Resource updated successfully";
    public static final String MSG_DELETED = "Resource deleted successfully";
    
    // Error Messages
    public static final String MSG_NOT_FOUND = "Resource not found";
    public static final String MSG_ALREADY_EXISTS = "Resource already exists";
    public static final String MSG_INVALID_INPUT = "Invalid input provided";
    public static final String MSG_UNAUTHORIZED = "Unauthorized access";
    public static final String MSG_FORBIDDEN = "Access forbidden";
    public static final String MSG_CONFLICT = "Resource conflict detected";
    
    private AppConstants() {
        // Private constructor to prevent instantiation
    }
}
