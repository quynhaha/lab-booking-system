import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context);
    final user = authProvider.currentUser;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Lab Booking'),
        backgroundColor: const Color(0xFFFF6C00), // FPT Orange
        foregroundColor: Colors.white,
        actions: [
          // User info and logout
          PopupMenuButton<String>(
            icon: CircleAvatar(
              backgroundColor: Colors.white,
              child: Text(
                user?.fullName.substring(0, 1).toUpperCase() ?? 'U',
                style: const TextStyle(color: Color(0xFFFF6C00)),
              ),
            ),
            onSelected: (value) {
              if (value == 'logout') {
                _showLogoutDialog(context, authProvider);
              }
            },
            itemBuilder: (BuildContext context) => [
              PopupMenuItem<String>(
                enabled: false,
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      user?.fullName ?? 'User',
                      style: const TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 16,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      user?.email ?? '',
                      style: TextStyle(
                        fontSize: 12,
                        color: Theme.of(context).colorScheme.onSurfaceVariant,
                      ),
                    ),
                    if (user?.studentId != null) ...[
                      const SizedBox(height: 4),
                      Text(
                        'Mã SV: ${user!.studentId}',
                        style: TextStyle(
                          fontSize: 12,
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        ),
                      ),
                    ],
                  ],
                ),
              ),
              const PopupMenuDivider(),
              const PopupMenuItem<String>(
                value: 'logout',
                child: Row(
                  children: [
                    Icon(Icons.logout, size: 20),
                    SizedBox(width: 8),
                    Text('Đăng xuất'),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [
              const Color(0xFFFF6C00).withOpacity(0.05),
              Colors.white,
            ],
          ),
        ),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const SizedBox(height: 32),
              Text(
                'Hệ thống quản lý phòng Lab',
                style: Theme.of(context).textTheme.headlineMedium?.copyWith(
                  fontWeight: FontWeight.bold,
                  color: const Color(0xFFFF6C00),
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 16),
              Text(
                'Đặt lịch và quản lý sử dụng phòng lab tại đại học',
                style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                  color: Theme.of(context).colorScheme.onSurfaceVariant,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 48),
              _buildActionCard(
                context,
                icon: Icons.meeting_room,
                title: 'Danh sách phòng Lab',
                subtitle: 'Xem các phòng lab khả dụng',
                color: const Color(0xFFFF6C00), // FPT Orange
                onTap: () => context.go('/labs'),
              ),
              const SizedBox(height: 16),
              _buildActionCard(
                context,
                icon: Icons.book_online,
                title: 'Lịch đã đặt',
                subtitle: 'Quản lý các lịch đặt phòng',
                color: const Color(0xFFFFA500), // Light Orange
                onTap: () => context.go('/bookings'),
              ),
              const SizedBox(height: 16),
              _buildActionCard(
                context,
                icon: Icons.event,
                title: 'Sự kiện',
                subtitle: 'Xem các sự kiện sắp diễn ra',
                color: const Color(0xFFFF8C00), // Dark Orange
                onTap: () => context.go('/events'),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildActionCard(
    BuildContext context, {
    required IconData icon,
    required String title,
    required String subtitle,
    required VoidCallback onTap,
    Color? color,
  }) {
    final cardColor = color ?? Theme.of(context).colorScheme.primary;
    
    return Card(
      elevation: 4,
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Row(
            children: [
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: cardColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Icon(
                  icon,
                  size: 32,
                  color: cardColor,
                ),
              ),
              const SizedBox(width: 16),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: Theme.of(context).textTheme.titleMedium?.copyWith(
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      subtitle,
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                        color: Theme.of(context).colorScheme.onSurfaceVariant,
                      ),
                    ),
                  ],
                ),
              ),
              Icon(
                Icons.arrow_forward_ios,
                size: 16,
                color: Theme.of(context).colorScheme.onSurfaceVariant,
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _showLogoutDialog(BuildContext context, AuthProvider authProvider) {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return AlertDialog(
          title: const Text('Đăng xuất'),
          content: const Text('Bạn có chắc chắn muốn đăng xuất?'),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(context).pop(),
              child: const Text('Hủy'),
            ),
            FilledButton(
              onPressed: () async {
                Navigator.of(context).pop();
                await authProvider.logout();
                // Router will automatically redirect to login
              },
              child: const Text('Đăng xuất'),
            ),
          ],
        );
      },
    );
  }
}
