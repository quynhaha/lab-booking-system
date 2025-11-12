import 'package:json_annotation/json_annotation.dart';

part 'booking.g.dart';

@JsonSerializable()
class Booking {
  final int? id;
  final String? bookingCode;
  final int? userId;
  final String? userEmail;
  final String? userName;
  final int? labId;
  final String? labName;
  final int? categoryId;
  final String? categoryName;
  final String title;
  final String? description;
  final String startTime;
  final String endTime;
  final String? status;
  final int? participantsCount;
  final bool? isMultiLab;
  final int? parentBookingId;
  final List<int>? childBookingIds;
  final String? refundStatus;
  final String? refundAmount;
  final String? cancellationReason;
  final int? cancelledByUserId;
  final String? cancelledByUserName;
  final String? cancelledAt;
  final int? approvedByUserId;
  final String? approvedByUserName;
  final String? approvedAt;
  final String? createdAt;
  final String? updatedAt;

  const Booking({
    this.id,
    this.bookingCode,
    this.userId,
    this.userEmail,
    this.userName,
    this.labId,
    this.labName,
    this.categoryId,
    this.categoryName,
    required this.title,
    this.description,
    required this.startTime,
    required this.endTime,
    this.status,
    this.participantsCount,
    this.isMultiLab,
    this.parentBookingId,
    this.childBookingIds,
    this.refundStatus,
    this.refundAmount,
    this.cancellationReason,
    this.cancelledByUserId,
    this.cancelledByUserName,
    this.cancelledAt,
    this.approvedByUserId,
    this.approvedByUserName,
    this.approvedAt,
    this.createdAt,
    this.updatedAt,
  });

  factory Booking.fromJson(Map<String, dynamic> json) => _$BookingFromJson(json);
  Map<String, dynamic> toJson() => _$BookingToJson(this);

  String get statusDisplay {
    switch (status) {
      case 'PENDING':
        return 'Chờ duyệt';
      case 'APPROVED':
        return 'Đã duyệt';
      case 'REJECTED':
        return 'Bị từ chối';
      case 'CANCELLED':
        return 'Đã hủy';
      case 'COMPLETED':
        return 'Hoàn thành';
      default:
        return status ?? 'Không rõ';
    }
  }

  bool get isPending => status == 'PENDING';
  bool get isApproved => status == 'APPROVED';
  bool get isCancelled => status == 'CANCELLED';
  bool get isRejected => status == 'REJECTED';
  bool get canBeCancelled => status == 'PENDING' || status == 'APPROVED';
}


