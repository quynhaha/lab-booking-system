import 'package:json_annotation/json_annotation.dart';

part 'user.g.dart';

@JsonSerializable()
class User {
  final int id;
  final String fullName;
  final String email;
  final String role;
  final String? studentId;
  final String? faculty;
  final bool status;

  User({
    required this.id,
    required this.fullName,
    required this.email,
    required this.role,
    this.studentId,
    this.faculty,
    required this.status,
  });

  factory User.fromJson(Map<String, dynamic> json) => _$UserFromJson(json);
  Map<String, dynamic> toJson() => _$UserToJson(this);

  bool get isStudent => role == 'STUDENT';
  bool get isAdmin => role == 'ADMIN';
  bool get isTeacher => role == 'TEACHER';
}


