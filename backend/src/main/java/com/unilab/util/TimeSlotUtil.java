package com.unilab.util;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for managing time slots for booking
 * A day is divided into 4 slots:
 * - Slot 1: 08:00 - 11:00 (Morning)
 * - Slot 2: 11:00 - 14:00 (Noon)
 * - Slot 3: 14:00 - 17:00 (Afternoon)
 * - Slot 4: 17:00 - 20:00 (Evening)
 */
public class TimeSlotUtil {
    
    public static final int SLOT_1_START_HOUR = 8;
    public static final int SLOT_1_START_MINUTE = 0;
    public static final int SLOT_1_END_HOUR = 11;
    public static final int SLOT_1_END_MINUTE = 0;
    
    public static final int SLOT_2_START_HOUR = 11;
    public static final int SLOT_2_START_MINUTE = 0;
    public static final int SLOT_2_END_HOUR = 14;
    public static final int SLOT_2_END_MINUTE = 0;
    
    public static final int SLOT_3_START_HOUR = 14;
    public static final int SLOT_3_START_MINUTE = 0;
    public static final int SLOT_3_END_HOUR = 17;
    public static final int SLOT_3_END_MINUTE = 0;
    
    public static final int SLOT_4_START_HOUR = 17;
    public static final int SLOT_4_START_MINUTE = 0;
    public static final int SLOT_4_END_HOUR = 20;
    public static final int SLOT_4_END_MINUTE = 0;
    
    public static final int TOTAL_SLOTS = 4;
    
    /**
     * Get start time for a specific slot on a given date
     */
    public static LocalDateTime getSlotStartTime(int slotNumber, LocalDateTime date) {
        LocalTime slotStartTime = getSlotStartTime(slotNumber);
        return date.toLocalDate().atTime(slotStartTime);
    }
    
    /**
     * Get end time for a specific slot on a given date
     */
    public static LocalDateTime getSlotEndTime(int slotNumber, LocalDateTime date) {
        LocalTime slotEndTime = getSlotEndTime(slotNumber);
        return date.toLocalDate().atTime(slotEndTime);
    }
    
    /**
     * Get start time for a specific slot
     */
    public static LocalTime getSlotStartTime(int slotNumber) {
        switch (slotNumber) {
            case 1:
                return LocalTime.of(SLOT_1_START_HOUR, SLOT_1_START_MINUTE);
            case 2:
                return LocalTime.of(SLOT_2_START_HOUR, SLOT_2_START_MINUTE);
            case 3:
                return LocalTime.of(SLOT_3_START_HOUR, SLOT_3_START_MINUTE);
            case 4:
                return LocalTime.of(SLOT_4_START_HOUR, SLOT_4_START_MINUTE);
            default:
                throw new IllegalArgumentException("Invalid slot number: " + slotNumber + ". Must be between 1 and 4.");
        }
    }
    
    /**
     * Get end time for a specific slot
     */
    public static LocalTime getSlotEndTime(int slotNumber) {
        switch (slotNumber) {
            case 1:
                return LocalTime.of(SLOT_1_END_HOUR, SLOT_1_END_MINUTE);
            case 2:
                return LocalTime.of(SLOT_2_END_HOUR, SLOT_2_END_MINUTE);
            case 3:
                return LocalTime.of(SLOT_3_END_HOUR, SLOT_3_END_MINUTE);
            case 4:
                return LocalTime.of(SLOT_4_END_HOUR, SLOT_4_END_MINUTE);
            default:
                throw new IllegalArgumentException("Invalid slot number: " + slotNumber + ". Must be between 1 and 4.");
        }
    }
    
    /**
     * Get slot name/display name
     */
    public static String getSlotName(int slotNumber) {
        switch (slotNumber) {
            case 1:
                return "Ca 1: Sáng (08:00 - 11:00)";
            case 2:
                return "Ca 2: Trưa (11:00 - 14:00)";
            case 3:
                return "Ca 3: Chiều (14:00 - 17:00)";
            case 4:
                return "Ca 4: Tối (17:00 - 20:00)";
            default:
                return "Ca " + slotNumber;
        }
    }
    
    /**
     * Get slot number from a given time
     * Returns 0 if time doesn't match any slot
     */
    public static int getSlotNumber(LocalDateTime dateTime) {
        LocalTime time = dateTime.toLocalTime();
        LocalTime slot1Start = LocalTime.of(SLOT_1_START_HOUR, SLOT_1_START_MINUTE);
        LocalTime slot1End = LocalTime.of(SLOT_1_END_HOUR, SLOT_1_END_MINUTE);
        LocalTime slot2Start = LocalTime.of(SLOT_2_START_HOUR, SLOT_2_START_MINUTE);
        LocalTime slot2End = LocalTime.of(SLOT_2_END_HOUR, SLOT_2_END_MINUTE);
        LocalTime slot3Start = LocalTime.of(SLOT_3_START_HOUR, SLOT_3_START_MINUTE);
        LocalTime slot3End = LocalTime.of(SLOT_3_END_HOUR, SLOT_3_END_MINUTE);
        LocalTime slot4Start = LocalTime.of(SLOT_4_START_HOUR, SLOT_4_START_MINUTE);
        LocalTime slot4End = LocalTime.of(SLOT_4_END_HOUR, SLOT_4_END_MINUTE);
        
        // Check if time is within slot 1 (08:00 to 11:00)
        if ((time.equals(slot1Start) || time.isAfter(slot1Start)) && time.isBefore(slot1End)) {
            return 1;
        }
        // Check if time is within slot 2 (11:00 to 14:00)
        if ((time.equals(slot2Start) || time.isAfter(slot2Start)) && time.isBefore(slot2End)) {
            return 2;
        }
        // Check if time is within slot 3 (14:00 to 17:00)
        if ((time.equals(slot3Start) || time.isAfter(slot3Start)) && time.isBefore(slot3End)) {
            return 3;
        }
        // Check if time is within slot 4 (17:00 to 20:00)
        if ((time.equals(slot4Start) || time.isAfter(slot4Start)) && time.isBefore(slot4End)) {
            return 4;
        }
        
        return 0; // Not in any slot
    }
    
    /**
     * Check if a time range matches a specific slot exactly
     */
    public static boolean matchesSlot(LocalDateTime startTime, LocalDateTime endTime, int slotNumber) {
        LocalDateTime slotStart = getSlotStartTime(slotNumber, startTime);
        LocalDateTime slotEnd = getSlotEndTime(slotNumber, startTime);
        
        return startTime.equals(slotStart) && endTime.equals(slotEnd);
    }
    
    /**
     * Check if a time range overlaps with a specific slot
     */
    public static boolean overlapsWithSlot(LocalDateTime startTime, LocalDateTime endTime, int slotNumber) {
        LocalDateTime slotStart = getSlotStartTime(slotNumber, startTime);
        LocalDateTime slotEnd = getSlotEndTime(slotNumber, startTime);
        
        return (startTime.isBefore(slotEnd) && endTime.isAfter(slotStart));
    }
    
    /**
     * Get all slot numbers (1-4)
     */
    public static List<Integer> getAllSlotNumbers() {
        List<Integer> slots = new ArrayList<>();
        for (int i = 1; i <= TOTAL_SLOTS; i++) {
            slots.add(i);
        }
        return slots;
    }
    
    /**
     * Validate that start and end times match a valid slot
     */
    public static boolean isValidSlotBooking(LocalDateTime startTime, LocalDateTime endTime) {
        int slotNumber = getSlotNumber(startTime);
        if (slotNumber == 0) {
            return false;
        }
        return matchesSlot(startTime, endTime, slotNumber);
    }
}

