// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'event.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Event _$EventFromJson(Map<String, dynamic> json) => Event(
      id: (json['id'] as num).toInt(),
      title: json['title'] as String,
      description: json['description'] as String,
      startTime: json['startTime'] as String,
      endTime: json['endTime'] as String,
      status: json['status'] as String,
      location: json['location'] as String,
      capacity: (json['capacity'] as num).toInt(),
      bookedCount: (json['bookedCount'] as num).toInt(),
    );

Map<String, dynamic> _$EventToJson(Event instance) => <String, dynamic>{
      'id': instance.id,
      'title': instance.title,
      'description': instance.description,
      'startTime': instance.startTime,
      'endTime': instance.endTime,
      'status': instance.status,
      'location': instance.location,
      'capacity': instance.capacity,
      'bookedCount': instance.bookedCount,
    };
