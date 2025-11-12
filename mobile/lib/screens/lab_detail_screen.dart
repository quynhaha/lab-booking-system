import 'package:flutter/material.dart';
import '../models/lab.dart';
import '../models/event.dart';
import 'booking_create_screen.dart';

class LabDetailScreen extends StatelessWidget {
  final Lab lab;

  const LabDetailScreen({super.key, required this.lab});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Chi tiết phòng Lab'),
        backgroundColor: const Color(0xFFFF6C00), // FPT Orange
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header Card
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(24),
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  colors: [
                    Color(0xFFFF6C00), // FPT Orange
                    Color(0xFFFFA500), // Light Orange
                  ],
                ),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    lab.name,
                    style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                          color: Colors.white,
                        ),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Lab ID: ${lab.id}',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          color: Colors.white,
                        ),
                  ),
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
                  _buildInfoCard(
                    context,
                    icon: Icons.location_on,
                    title: 'Vị trí',
                    content: lab.location ?? 'Chưa cập nhật',
                    color: Colors.red,
                  ),
                  const SizedBox(height: 12),
                  _buildInfoCard(
                    context,
                    icon: Icons.people,
                    title: 'Sức chứa',
                    content: '${lab.capacity} người',
                    color: Colors.blue,
                  ),
                  if (lab.description != null && lab.description!.isNotEmpty) ...[
                    const SizedBox(height: 12),
                    _buildInfoCard(
                      context,
                      icon: Icons.description,
                      title: 'Mô tả',
                      content: lab.description!,
                      color: Colors.green,
                    ),
                  ],
                  const SizedBox(height: 24),
                  
                  // Action Buttons
                  SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      onPressed: lab.isAvailable
                          ? () => _navigateToBooking(context)
                          : null,
                      icon: const Icon(Icons.book_online),
                      label: const Text('Đặt lịch phòng này'),
                      style: ElevatedButton.styleFrom(
                        padding: const EdgeInsets.symmetric(vertical: 16),
                      ),
                    ),
                  ),
                  const SizedBox(height: 12),
                  SizedBox(
                    width: double.infinity,
                    child: OutlinedButton.icon(
                      onPressed: () => _viewSchedule(context),
                      icon: const Icon(Icons.calendar_today),
                      label: const Text('Xem lịch đã đặt'),
                      style: OutlinedButton.styleFrom(
                        padding: const EdgeInsets.symmetric(vertical: 16),
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

    switch (lab.status) {
      case 'AVAILABLE':
      case 'ACTIVE':
        statusColor = Colors.green;
        statusIcon = Icons.check_circle;
        break;
      case 'MAINTENANCE':
        statusColor = Colors.orange;
        statusIcon = Icons.build_circle;
        break;
      case 'UNAVAILABLE':
        statusColor = Colors.red;
        statusIcon = Icons.cancel;
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
            lab.statusDisplay,
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
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _navigateToBooking(BuildContext context, [Event? event]) async {
    final result = await Navigator.of(context).push(
      MaterialPageRoute(
        builder: (context) => BookingCreateScreen(lab: lab, event: event),
      ),
    );

    // If booking was successful, show a message
    if (result == true && context.mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Đã đặt lịch thành công! Kiểm tra trong mục "Lịch đã đặt"'),
          backgroundColor: Colors.green,
          duration: Duration(seconds: 3),
        ),
      );
    }
  }

  void _viewSchedule(BuildContext context) {
    // TODO: Navigate to lab schedule screen
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(
        content: Text('Xem lịch phòng (chưa triển khai)'),
      ),
    );
  }
}


