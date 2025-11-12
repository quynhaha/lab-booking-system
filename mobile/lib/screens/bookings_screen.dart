import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../services/booking_service.dart';
import '../services/api_service.dart';
import '../models/booking.dart';
import '../models/event.dart';
import '../providers/auth_provider.dart';
import 'booking_detail_screen.dart';

class BookingsScreen extends StatefulWidget {
  const BookingsScreen({super.key});

  @override
  State<BookingsScreen> createState() => _BookingsScreenState();
}

class _BookingsScreenState extends State<BookingsScreen> {
  List<Booking> bookings = [];
  List<Event> joinedEvents = []; // Events that student has joined
  bool isLoading = true;
  String? error;
  String filter = 'all'; // 'all', 'pending', 'approved', 'cancelled'

  @override
  void initState() {
    super.initState();
    _loadBookings();
  }

  Future<void> _loadBookings() async {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    final bookingService = Provider.of<BookingService>(context, listen: false);
    final apiService = Provider.of<ApiService>(context, listen: false);
    final userId = authProvider.currentUser?.id;
    final isStudent = authProvider.currentUser?.isStudent == true;

    if (userId == null) {
      setState(() {
        error = 'Không tìm thấy thông tin người dùng';
        isLoading = false;
      });
      return;
    }

    try {
      setState(() {
        isLoading = true;
        error = null;
      });

      // Set token if available
      if (authProvider.token != null) {
        bookingService.setToken(authProvider.token!);
        apiService.setToken(authProvider.token!);
      }

      // Load bookings
      final fetchedBookings = await bookingService.getUserBookings(userId);

      // Load joined events if student
      List<Event> fetchedEvents = [];
      if (isStudent) {
        try {
          fetchedEvents = await apiService.getMyEvents();
        } catch (e) {
          // Ignore error if API fails, just show bookings
          debugPrint('Failed to load joined events: $e');
        }
      }

      // Filter bookings
      List<Booking> filteredBookings = fetchedBookings;
      if (filter != 'all') {
        filteredBookings = fetchedBookings
            .where((b) => b.status?.toUpperCase() == filter.toUpperCase())
            .toList();
      }

      setState(() {
        bookings = filteredBookings;
        joinedEvents = fetchedEvents;
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        error = e.toString();
        isLoading = false;
      });
    }
  }

  // Convert Event to Booking-like object for display
  Booking _eventToBooking(Event event) {
    return Booking(
      id: event.id,
      title: event.title,
      description: event.description,
      startTime: event.startTime ?? '',
      endTime: event.endTime ?? '',
      status: 'EVENT_JOINED', // Special status for joined events
      labId: event.labId,
      labName: event.labName,
      userId: event.userId,
      userName: event.userFullName, // Use userName instead of userFullName
      createdAt: event.createdAt,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Lịch đã đặt'),
        backgroundColor: const Color(0xFFFF6C00), // FPT Orange
        foregroundColor: Colors.white,
        actions: [
          PopupMenuButton<String>(
            onSelected: (value) {
              setState(() {
                filter = value;
              });
              _loadBookings();
            },
            itemBuilder: (context) => [
              const PopupMenuItem(
                value: 'all',
                child: Text('Tất cả'),
              ),
              const PopupMenuItem(
                value: 'pending',
                child: Text('Chờ duyệt'),
              ),
              const PopupMenuItem(
                value: 'approved',
                child: Text('Đã duyệt'),
              ),
              const PopupMenuItem(
                value: 'cancelled',
                child: Text('Đã hủy'),
              ),
            ],
          ),
        ],
      ),
      body: _buildBody(),
    );
  }

  Widget _buildBody() {
    if (isLoading) {
      return const Center(
        child: CircularProgressIndicator(),
      );
    }

    if (error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.error_outline,
              size: 64,
              color: Theme.of(context).colorScheme.error,
            ),
            const SizedBox(height: 16),
            Text(
              'Lỗi tải danh sách',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 8),
            Text(
              error!,
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: Theme.of(context).colorScheme.error,
                  ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _loadBookings,
              child: const Text('Thử lại'),
            ),
          ],
        ),
      );
    }

    // Combine bookings and joined events
    List<Booking> allItems = List.from(bookings);
    if (joinedEvents.isNotEmpty) {
      allItems.addAll(joinedEvents.map((e) => _eventToBooking(e)));
    }

    if (allItems.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.book_online,
              size: 64,
              color: Theme.of(context).colorScheme.onSurfaceVariant,
            ),
            const SizedBox(height: 16),
            Text(
              'Chưa có lịch đặt nào',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 8),
            Text(
              'Đặt lịch phòng lab hoặc tham gia sự kiện để bắt đầu',
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                    color: Theme.of(context).colorScheme.onSurfaceVariant,
                  ),
              textAlign: TextAlign.center,
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadBookings,
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: allItems.length,
        itemBuilder: (context, index) {
          final booking = allItems[index];
          return _buildBookingCard(booking);
        },
      ),
    );
  }

  Widget _buildBookingCard(Booking booking) {
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
      case 'EVENT_JOINED':
        statusColor = Colors.purple;
        statusIcon = Icons.event;
        break;
      default:
        statusColor = Colors.grey;
        statusIcon = Icons.help;
    }

    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      elevation: 4,
      child: InkWell(
        onTap: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => BookingDetailScreen(booking: booking),
            ),
          ).then((_) => _loadBookings()); // Reload after returning
        },
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Expanded(
                    child: Text(
                      booking.title,
                      style: Theme.of(context).textTheme.titleLarge?.copyWith(
                            fontWeight: FontWeight.bold,
                          ),
                    ),
                  ),
                  Container(
                    padding:
                        const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(
                      color: statusColor.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(16),
                      border: Border.all(color: statusColor),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(
                          statusIcon,
                          size: 14,
                          color: statusColor,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          booking.statusDisplay,
                          style: TextStyle(
                            color: statusColor,
                            fontWeight: FontWeight.bold,
                            fontSize: 12,
                          ),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              if (booking.bookingCode != null) ...[
                Text(
                  'Mã: ${booking.bookingCode}',
                  style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Theme.of(context).colorScheme.onSurfaceVariant,
                      ),
                ),
                const SizedBox(height: 8),
              ],
              if (booking.labName != null) ...[
                Row(
                  children: [
                    Icon(
                      Icons.meeting_room,
                      size: 16,
                      color: Theme.of(context).colorScheme.onSurfaceVariant,
                    ),
                    const SizedBox(width: 4),
                    Text(
                      booking.labName!,
                      style: Theme.of(context).textTheme.bodyMedium,
                    ),
                  ],
                ),
                const SizedBox(height: 4),
              ],
              Row(
                children: [
                  Icon(
                    Icons.access_time,
                    size: 16,
                    color: Theme.of(context).colorScheme.onSurfaceVariant,
                  ),
                  const SizedBox(width: 4),
                  Expanded(
                    child: Text(
                      _formatDateTime(booking.startTime),
                      style: Theme.of(context).textTheme.bodyMedium,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 4),
              Row(
                children: [
                  Icon(
                    Icons.schedule,
                    size: 16,
                    color: Theme.of(context).colorScheme.onSurfaceVariant,
                  ),
                  const SizedBox(width: 4),
                  Expanded(
                    child: Text(
                      'Đến: ${_formatDateTime(booking.endTime)}',
                      style: Theme.of(context).textTheme.bodyMedium,
                    ),
                  ),
                ],
              ),
              if (booking.canBeCancelled) ...[
                const SizedBox(height: 12),
                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    OutlinedButton.icon(
                      onPressed: () => _confirmCancelBooking(booking),
                      icon: const Icon(Icons.cancel, size: 18),
                      label: const Text('Hủy lịch'),
                      style: OutlinedButton.styleFrom(
                        foregroundColor: Colors.red,
                      ),
                    ),
                  ],
                ),
              ],
            ],
          ),
        ),
      ),
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

  void _confirmCancelBooking(Booking booking) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Xác nhận hủy lịch'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Bạn có chắc muốn hủy lịch "${booking.title}"?'),
            const SizedBox(height: 16),
            TextField(
              decoration: const InputDecoration(
                labelText: 'Lý do hủy',
                border: OutlineInputBorder(),
                hintText: 'Nhập lý do hủy lịch',
              ),
              maxLines: 3,
              onChanged: (value) {
                // Store reason
              },
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Không'),
          ),
          ElevatedButton(
            onPressed: () async {
              Navigator.pop(context);
              await _cancelBooking(booking, 'Hủy bởi người dùng');
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.red,
            ),
            child: const Text('Hủy lịch'),
          ),
        ],
      ),
    );
  }

  Future<void> _cancelBooking(Booking booking, String reason) async {
    final bookingService = Provider.of<BookingService>(context, listen: false);
    
    try {
      await bookingService.cancelBooking(booking.id!, reason);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Đã hủy lịch thành công'),
            backgroundColor: Colors.green,
          ),
        );
        _loadBookings();
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Lỗi hủy lịch: $e'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }
}
