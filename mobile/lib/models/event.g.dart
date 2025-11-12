// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'event.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Event _$EventFromJson(Map<String, dynamic> json) => Event(
      id: (json['id'] as num).toInt(),
      title: json['title'] as String,
      description: json['description'] as String?,
      startTime: json['startTime'] as String?,
      endTime: json['endTime'] as String?,
      userId: (json['userId'] as num?)?.toInt(),
      userFullName: json['userFullName'] as String?,
      labId: (json['labId'] as num?)?.toInt(),
      labName: json['labName'] as String?,
      status: json['status'] as String,
      createdAt: json['createdAt'] as String?,
    );

Map<String, dynamic> _$EventToJson(Event instance) => <String, dynamic>{
      'id': instance.id,
      'title': instance.title,
      'description': instance.description,
      'startTime': instance.startTime,
      'endTime': instance.endTime,
      'userId': instance.userId,
      'userFullName': instance.userFullName,
      'labId': instance.labId,
      'labName': instance.labName,
      'status': instance.status,
      'createdAt': instance.createdAt,
    };
