package com.unilab.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating unique codes
 */
public class CodeGenerator {
    
    private static final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Generate booking code: BK-YYYYMMDD-XXXX
     */
    public static String generateBookingCode() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = generateRandomString(4);
        return "BK-" + date + "-" + randomPart;
    }
    
    /**
     * Generate lab code: LAB-XXX
     */
    public static String generateLabCode(int sequence) {
        return String.format("LAB-%03d", sequence);
    }
    
    /**
     * Generate penalty code: PEN-XXX
     */
    public static String generatePenaltyCode(int sequence) {
        return String.format("PEN-%03d", sequence);
    }
    
    /**
     * Generate rule code: RULE-XXX
     */
    public static String generateRuleCode(int sequence) {
        return String.format("RULE-%03d", sequence);
    }
    
    /**
     * Generate category code: CAT-XXX
     */
    public static String generateCategoryCode(int sequence) {
        return String.format("CAT-%03d", sequence);
    }
    
    /**
     * Generate report code: RPT-YYYYMMDD-XXXX
     */
    public static String generateReportCode() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = generateRandomString(4);
        return "RPT-" + date + "-" + randomPart;
    }
    
    /**
     * Generate random alphanumeric string
     */
    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHA_NUMERIC.charAt(random.nextInt(ALPHA_NUMERIC.length())));
        }
        return sb.toString();
    }
    
    /**
     * Generate refund transaction code: REF-YYYYMMDD-XXXX
     */
    public static String generateRefundCode() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = generateRandomString(4);
        return "REF-" + date + "-" + randomPart;
    }
}
