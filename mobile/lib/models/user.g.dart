// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'user.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

User _$UserFromJson(Map<String, dynamic> json) => User(
      id: (json['id'] as num).toInt(),
      fullName: json['fullName'] as String,
      email: json['email'] as String,
      role: json['role'] as String,
      studentId: json['studentId'] as String?,
      faculty: json['faculty'] as String?,
      status: json['status'] as bool,
    );

Map<String, dynamic> _$UserToJson(User instance) => <String, dynamic>{
      'id': instance.id,
      'fullName': instance.fullName,
      'email': instance.email,
      'role': instance.role,
      'studentId': instance.studentId,
      'faculty': instance.faculty,
      'status': instance.status,
    };
