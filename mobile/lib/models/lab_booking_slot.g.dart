// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'lab_booking_slot.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

LabBookingSlot _$LabBookingSlotFromJson(Map<String, dynamic> json) =>
    LabBookingSlot(
      labId: (json['labId'] as num).toInt(),
      title: json['title'] as String,
      description: json['description'] as String?,
      participantsCount: (json['participantsCount'] as num).toInt(),
      startTime: json['startTime'] as String?,
      endTime: json['endTime'] as String?,
      slotNumber: (json['slotNumber'] as num?)?.toInt(),
      bookingDate: json['bookingDate'] as String?,
    );

Map<String, dynamic> _$LabBookingSlotToJson(LabBookingSlot instance) =>
    <String, dynamic>{
      'labId': instance.labId,
      'title': instance.title,
      'description': instance.description,
      'participantsCount': instance.participantsCount,
      'startTime': instance.startTime,
      'endTime': instance.endTime,
      'slotNumber': instance.slotNumber,
      'bookingDate': instance.bookingDate,
    };
