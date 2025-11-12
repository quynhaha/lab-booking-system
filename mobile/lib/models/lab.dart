import 'package:json_annotation/json_annotation.dart';

part 'lab.g.dart';

@JsonSerializable()
class Lab {
  final int id;
  final String name;
  final String? description;
  final String? location;
  final int capacity;
  final String status;

  const Lab({
    required this.id,
    required this.name,
    this.description,
    this.location,
    required this.capacity,
    required this.status,
  });

  factory Lab.fromJson(Map<String, dynamic> json) => _$LabFromJson(json);
  Map<String, dynamic> toJson() => _$LabToJson(this);

  bool get isAvailable => status == 'AVAILABLE' || status == 'ACTIVE';
  bool get isUnderMaintenance => status == 'MAINTENANCE';

  String get statusDisplay {
    switch (status) {
      case 'AVAILABLE':
      case 'ACTIVE':
        return 'Có sẵn';
      case 'MAINTENANCE':
        return 'Bảo trì';
      case 'UNAVAILABLE':
        return 'Không khả dụng';
      default:
        return status;
    }
  }
}


