import 'package:json_annotation/json_annotation.dart';

part 'lab_booking_slot.g.dart';

@JsonSerializable()
class LabBookingSlot {
  final int labId;
  final String title;
  final String? description;
  final int participantsCount;
  
  // For backward compatibility and UI display
  final String? startTime;  // ISO 8601 format (optional)
  final String? endTime;    // ISO 8601 format (optional)
  
  // Required for API (slot-based booking)
  final int? slotNumber;    // 1-4: Slot 1 (08:00-11:00), Slot 2 (11:00-14:00), Slot 3 (14:00-17:00), Slot 4 (17:00-20:00)
  final String? bookingDate; // Format: YYYY-MM-DD

  const LabBookingSlot({
    required this.labId,
    required this.title,
    this.description,
    required this.participantsCount,
    this.startTime,
    this.endTime,
    this.slotNumber,
    this.bookingDate,
  });

  factory LabBookingSlot.fromJson(Map<String, dynamic> json) => _$LabBookingSlotFromJson(json);
  
  // Convert to API format (with slotNumber and bookingDate)
  Map<String, dynamic> toApiJson() {
    return {
      'labId': labId,
      'title': title,
      if (description != null && description!.isNotEmpty) 'description': description,
      'participantsCount': participantsCount,
      'slotNumber': slotNumber,
      'bookingDate': bookingDate,
    };
  }
  
  // Keep original toJson for backward compatibility
  Map<String, dynamic> toJson() => _$LabBookingSlotToJson(this);
}






