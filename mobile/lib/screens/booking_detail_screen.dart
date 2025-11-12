import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../models/booking.dart';

class BookingDetailScreen extends StatelessWidget {
  final Booking booking;

  const BookingDetailScreen({super.key, required this.booking});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Chi tiết lịch đặt'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header Card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(24),
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  colors: [
                    Theme.of(context).colorScheme.primaryContainer,
                    Theme.of(context).colorScheme.secondaryContainer,
                  ],
                ),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    booking.title,
                    style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: Theme.of(context).colorScheme.onPrimaryContainer,
                        ),
                  ),
                  if (booking.bookingCode != null) ...[
                    const SizedBox(height: 8),
                    Text(
                      'Mã đặt: ${booking.bookingCode}',
                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                            color: Theme.of(context).colorScheme.onPrimaryContainer,
                          ),
                    ),
                  ],
                  const SizedBox(height: 16),
                  _buildStatusChip(context),
                ],
              ),
            ),

            // Information Cards
            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  if (booking.labName != null)
                    _buildInfoCard(
                      context,
                      icon: Icons.meeting_room,
                      title: 'Phòng Lab',
                      content: booking.labName!,
                      subtitle: 'Lab ID: ${booking.labId}',
                      color: Colors.blue,
                    ),
                  const SizedBox(height: 12),
                  _buildTimeCard(
                    context,
                    icon: Icons.calendar_today,
                    title: 'Thời gian bắt đầu',
                    content: _formatDateTime(booking.startTime),
                    color: Colors.green,
                  ),
                  const SizedBox(height: 12),
                  _buildTimeCard(
                    context,
                    icon: Icons.access_time,
                    title: 'Thời gian kết thúc',
                    content: _formatDateTime(booking.endTime),
                    color: Colors.orange,
                  ),
                  if (booking.participantsCount != null) ...[
                    const SizedBox(height: 12),
                    _buildInfoCard(
                      context,
                      icon: Icons.people,
                      title: 'Số người tham gia',
                      content: '${booking.participantsCount} người',
                      color: Colors.purple,
                    ),
                  ],
                  if (booking.description != null &&
                      booking.description!.isNotEmpty) ...[
                    const SizedBox(height: 12),
                    _buildInfoCard(
                      context,
                      icon: Icons.description,
                      title: 'Mô tả',
                      content: booking.description!,
                      color: Colors.teal,
                    ),
                  ],
                  if (booking.categoryName != null) ...[
                    const SizedBox(height: 12),
                    _buildInfoCard(
                      context,
                      icon: Icons.category,
                      title: 'Danh mục',
                      content: booking.categoryName!,
                      color: Colors.indigo,
                    ),
                  ],
                  if (booking.approvedByUserName != null) ...[
                    const SizedBox(height: 12),
                    _buildInfoCard(
                      context,
                      icon: Icons.verified_user,
                      title: 'Người duyệt',
                      content: booking.approvedByUserName!,
                      subtitle: booking.approvedAt != null
                          ? _formatDateTime(booking.approvedAt!)
                          : null,
                      color: Colors.green,
                    ),
                  ],
                  if (booking.cancellationReason != null) ...[
                    const SizedBox(height: 12),
                    _buildInfoCard(
                      context,
                      icon: Icons.cancel,
                      title: 'Lý do hủy',
                      content: booking.cancellationReason!,
                      subtitle: booking.cancelledByUserName,
                      color: Colors.red,
                    ),
                  ],
                  const SizedBox(height: 24),
                  
                  // Metadata
                  Card(
                    color: Theme.of(context).colorScheme.surfaceVariant,
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        children: [
                          if (booking.createdAt != null)
                            _buildMetadataRow(
                              context,
                              'Tạo lúc',
                              _formatDateTime(booking.createdAt!),
                            ),
                          if (booking.updatedAt != null) ...[
                            const Divider(height: 16),
                            _buildMetadataRow(
                              context,
                              'Cập nhật',
                              _formatDateTime(booking.updatedAt!),
                            ),
                          ],
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildStatusChip(BuildContext context) {
    Color statusColor;
    IconData statusIcon;

    switch (booking.status) {
      case 'PENDING':
        statusColor = Colors.orange;
        statusIcon = Icons.pending;
        break;
      case 'APPROVED':
        statusColor = Colors.green;
        statusIcon = Icons.check_circle;
        break;
      case 'REJECTED':
        statusColor = Colors.red;
        statusIcon = Icons.cancel;
        break;
      case 'CANCELLED':
        statusColor = Colors.grey;
        statusIcon = Icons.block;
        break;
      case 'COMPLETED':
        statusColor = Colors.blue;
        statusIcon = Icons.done_all;
        break;
      default:
        statusColor = Colors.grey;
        statusIcon = Icons.help;
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        color: statusColor,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(
            statusIcon,
            size: 20,
            color: Colors.white,
          ),
          const SizedBox(width: 8),
          Text(
            booking.statusDisplay,
            style: const TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildInfoCard(
    BuildContext context, {
    required IconData icon,
    required String title,
    required String content,
    String? subtitle,
    required Color color,
  }) {
    return Card(
      elevation: 2,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.all(8),
              decoration: BoxDecoration(
                color: color.withOpacity(0.1),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Icon(
                icon,
                color: color,
                size: 24,
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: Theme.of(context).textTheme.titleSmall?.copyWith(
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    content,
                    style: Theme.of(context).textTheme.bodyLarge,
                  ),
                  if (subtitle != null) ...[
                    const SizedBox(height: 2),
                    Text(
                      subtitle,
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                            color: Theme.of(context).colorScheme.onSurfaceVariant,
                          ),
                    ),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTimeCard(
    BuildContext context, {
    required IconData icon,
    required String title,
    required String content,
    required Color color,
  }) {
    return Card(
      elevation: 3,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(
          color: color.withOpacity(0.3),
          width: 2,
        ),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              padding: const EdgeInsets.all(10),
              decoration: BoxDecoration(
                color: color.withOpacity(0.15),
                borderRadius: BorderRadius.circular(10),
              ),
              child: Icon(
                icon,
                color: color,
                size: 28,
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: Theme.of(context).textTheme.titleSmall?.copyWith(
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                          fontWeight: FontWeight.w500,
                        ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    content,
                    style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: color,
                          fontSize: 20,
                        ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildMetadataRow(BuildContext context, String label, String value) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          label,
          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                color: Theme.of(context).colorScheme.onSurfaceVariant,
              ),
        ),
        Text(
          value,
          style: Theme.of(context).textTheme.bodyMedium,
        ),
      ],
    );
  }

  String _formatDateTime(String dateTimeStr) {
    try {
      final dateTime = DateTime.parse(dateTimeStr);
      return DateFormat('dd/MM/yyyy HH:mm').format(dateTime);
    } catch (e) {
      return dateTimeStr;
    }
  }
}

