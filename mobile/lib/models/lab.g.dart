// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'lab.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Lab _$LabFromJson(Map<String, dynamic> json) => Lab(
      id: (json['id'] as num).toInt(),
      name: json['name'] as String,
      description: json['description'] as String?,
      location: json['location'] as String?,
      capacity: (json['capacity'] as num).toInt(),
      status: json['status'] as String,
    );

Map<String, dynamic> _$LabToJson(Lab instance) => <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'description': instance.description,
      'location': instance.location,
      'capacity': instance.capacity,
      'status': instance.status,
    };
