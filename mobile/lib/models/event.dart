import 'package:json_annotation/json_annotation.dart';

part 'event.g.dart';

@JsonSerializable()
class Event {
  final int id;
  final String title;
  final String description;
  final String startTime;
  final String endTime;
  final String status;
  final String location;
  final int capacity;
  final int bookedCount;

  const Event({
    required this.id,
    required this.title,
    required this.description,
    required this.startTime,
    required this.endTime,
    required this.status,
    required this.location,
    required this.capacity,
    required this.bookedCount,
  });

  factory Event.fromJson(Map<String, dynamic> json) => _$EventFromJson(json);

  Map<String, dynamic> toJson() => _$EventToJson(this);

  bool get isAvailable => status == 'ACTIVE' && bookedCount < capacity;

  int get availableSpots => capacity - bookedCount;
}
