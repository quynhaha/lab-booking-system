// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'booking.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

Booking _$BookingFromJson(Map<String, dynamic> json) => Booking(
      id: (json['id'] as num?)?.toInt(),
      bookingCode: json['bookingCode'] as String?,
      userId: (json['userId'] as num?)?.toInt(),
      userEmail: json['userEmail'] as String?,
      userName: json['userName'] as String?,
      labId: (json['labId'] as num?)?.toInt(),
      labName: json['labName'] as String?,
      categoryId: (json['categoryId'] as num?)?.toInt(),
      categoryName: json['categoryName'] as String?,
      title: json['title'] as String,
      description: json['description'] as String?,
      startTime: json['startTime'] as String,
      endTime: json['endTime'] as String,
      status: json['status'] as String?,
      participantsCount: (json['participantsCount'] as num?)?.toInt(),
      isMultiLab: json['isMultiLab'] as bool?,
      parentBookingId: (json['parentBookingId'] as num?)?.toInt(),
      childBookingIds: (json['childBookingIds'] as List<dynamic>?)
          ?.map((e) => (e as num).toInt())
          .toList(),
      refundStatus: json['refundStatus'] as String?,
      refundAmount: json['refundAmount'] as String?,
      cancellationReason: json['cancellationReason'] as String?,
      cancelledByUserId: (json['cancelledByUserId'] as num?)?.toInt(),
      cancelledByUserName: json['cancelledByUserName'] as String?,
      cancelledAt: json['cancelledAt'] as String?,
      approvedByUserId: (json['approvedByUserId'] as num?)?.toInt(),
      approvedByUserName: json['approvedByUserName'] as String?,
      approvedAt: json['approvedAt'] as String?,
      createdAt: json['createdAt'] as String?,
      updatedAt: json['updatedAt'] as String?,
    );

Map<String, dynamic> _$BookingToJson(Booking instance) => <String, dynamic>{
      'id': instance.id,
      'bookingCode': instance.bookingCode,
      'userId': instance.userId,
      'userEmail': instance.userEmail,
      'userName': instance.userName,
      'labId': instance.labId,
      'labName': instance.labName,
      'categoryId': instance.categoryId,
      'categoryName': instance.categoryName,
      'title': instance.title,
      'description': instance.description,
      'startTime': instance.startTime,
      'endTime': instance.endTime,
      'status': instance.status,
      'participantsCount': instance.participantsCount,
      'isMultiLab': instance.isMultiLab,
      'parentBookingId': instance.parentBookingId,
      'childBookingIds': instance.childBookingIds,
      'refundStatus': instance.refundStatus,
      'refundAmount': instance.refundAmount,
      'cancellationReason': instance.cancellationReason,
      'cancelledByUserId': instance.cancelledByUserId,
      'cancelledByUserName': instance.cancelledByUserName,
      'cancelledAt': instance.cancelledAt,
      'approvedByUserId': instance.approvedByUserId,
      'approvedByUserName': instance.approvedByUserName,
      'approvedAt': instance.approvedAt,
      'createdAt': instance.createdAt,
      'updatedAt': instance.updatedAt,
    };
