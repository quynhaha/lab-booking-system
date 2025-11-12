import 'package:json_annotation/json_annotation.dart';

part 'event.g.dart';

@JsonSerializable()
class Event {
  final int id;
  final String title;
  final String? description;
  final String? startTime; // Nullable - will be set when teacher books a lab
  final String? endTime; // Nullable - will be set when teacher books a lab
  final int? userId;
  final String? userFullName;
  final int? labId; // Nullable because event can be created without lab
  final String? labName;
  final String status;
  final String? createdAt;

  const Event({
    required this.id,
    required this.title,
    this.description,
    this.startTime, // Optional - will be set when teacher books a lab
    this.endTime, // Optional - will be set when teacher books a lab
    this.userId,
    this.userFullName,
    this.labId, // Optional - event can be created without lab
    this.labName,
    required this.status,
    this.createdAt,
  });

  factory Event.fromJson(Map<String, dynamic> json) => _$EventFromJson(json);

  Map<String, dynamic> toJson() => _$EventToJson(this);

  bool get isAvailable => status == 'PENDING' || status == 'ACTIVE';
}
