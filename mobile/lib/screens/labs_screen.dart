import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/lab_service.dart';
import '../models/lab.dart';
import '../models/event.dart';
import '../utils/event_selection_helper.dart';
import 'lab_detail_screen.dart';
import 'booking_create_screen.dart';

class LabsScreen extends StatefulWidget {
  const LabsScreen({super.key});

  @override
  State<LabsScreen> createState() => _LabsScreenState();
}

class _LabsScreenState extends State<LabsScreen> {
  final LabService _labService = LabService();
  List<Lab> labs = [];
  bool isLoading = true;
  String? error;
  String filter = 'all'; // 'all', 'available'
  Event? _selectedEvent; // Event passed from events screen (for teacher booking)

  @override
  void initState() {
    super.initState();
    _loadLabs();
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    // Get event from EventSelectionHelper if teacher selected one from events screen
    _selectedEvent = EventSelectionHelper.getSelectedEvent();
    
    // Show banner if event is selected
    if (_selectedEvent != null && mounted) {
      WidgetsBinding.instance.addPostFrameCallback((_) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text('Đang book lab cho sự kiện: "${_selectedEvent!.title}"'),
            duration: const Duration(seconds: 3),
            backgroundColor: Colors.blue,
          ),
        );
      });
    }
  }

  @override
  void dispose() {
    _labService.dispose();
    super.dispose();
  }

  Future<void> _loadLabs() async {
    try {
      setState(() {
        isLoading = true;
        error = null;
      });

      final fetchedLabs = filter == 'available'
          ? await _labService.getAvailableLabs()
          : await _labService.getAllLabs();

      setState(() {
        labs = fetchedLabs;
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        error = e.toString();
        isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Danh sách phòng Lab'),
        backgroundColor: const Color(0xFFFF6C00), // FPT Orange
        foregroundColor: Colors.white,
        actions: [
          PopupMenuButton<String>(
            onSelected: (value) {
              setState(() {
                filter = value;
              });
              _loadLabs();
            },
            itemBuilder: (context) => [
              const PopupMenuItem(
                value: 'all',
                child: Text('Tất cả phòng'),
              ),
              const PopupMenuItem(
                value: 'available',
                child: Text('Chỉ phòng khả dụng'),
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
              'Lỗi tải danh sách phòng',
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
              onPressed: _loadLabs,
              child: const Text('Thử lại'),
            ),
          ],
        ),
      );
    }

    if (labs.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.meeting_room,
              size: 64,
              color: Theme.of(context).colorScheme.onSurfaceVariant,
            ),
            const SizedBox(height: 16),
            Text(
              'Không có phòng lab nào',
              style: Theme.of(context).textTheme.titleLarge,
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadLabs,
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: labs.length,
        itemBuilder: (context, index) {
          final lab = labs[index];
          return _buildLabCard(lab);
        },
      ),
    );
  }

  Widget _buildLabCard(Lab lab) {
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

    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      elevation: 4,
      child: InkWell(
        onTap: () {
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (context) => LabDetailScreen(lab: lab),
            ),
          );
        },
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          lab.name,
                          style:
                              Theme.of(context).textTheme.titleLarge?.copyWith(
                                    fontWeight: FontWeight.bold,
                                  ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          'Lab ID: ${lab.id}',
                          style:
                              Theme.of(context).textTheme.bodyMedium?.copyWith(
                                    color: Theme.of(context)
                                        .colorScheme
                                        .onSurfaceVariant,
                                  ),
                        ),
                      ],
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
                          size: 16,
                          color: statusColor,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          lab.statusDisplay,
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
              const SizedBox(height: 12),
              Row(
                children: [
                  Icon(
                    Icons.location_on,
                    size: 16,
                    color: Theme.of(context).colorScheme.onSurfaceVariant,
                  ),
                  const SizedBox(width: 4),
                  Expanded(
                    child: Text(
                      lab.location ?? 'Chưa cập nhật',
                      style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                            color:
                                Theme.of(context).colorScheme.onSurfaceVariant,
                          ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  Icon(
                    Icons.people,
                    size: 16,
                    color: Theme.of(context).colorScheme.onSurfaceVariant,
                  ),
                  const SizedBox(width: 4),
                  Text(
                    'Sức chứa: ${lab.capacity} người',
                    style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                          color: Theme.of(context).colorScheme.onSurfaceVariant,
                        ),
                  ),
                ],
              ),
              if (lab.description != null && lab.description!.isNotEmpty) ...[
                const SizedBox(height: 12),
                Text(
                  lab.description!,
                  style: Theme.of(context).textTheme.bodyMedium,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                ),
              ],
              const SizedBox(height: 12),
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  TextButton.icon(
                    onPressed: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => LabDetailScreen(lab: lab),
                        ),
                      );
                    },
                    icon: const Icon(Icons.info_outline),
                    label: const Text('Chi tiết'),
                  ),
                  const SizedBox(width: 8),
                  ElevatedButton.icon(
                    onPressed: lab.isAvailable
                        ? () => _navigateToBooking(lab, _selectedEvent)
                        : null,
                    icon: const Icon(Icons.book_online),
                    label: const Text('Đặt lịch'),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _navigateToBooking(Lab lab, [Event? event]) async {
    final result = await Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => BookingCreateScreen(lab: lab, event: event),
      ),
    );

    // If booking was successful, show a message
    if (result == true && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Đã đặt lịch thành công!'),
          backgroundColor: Colors.green,
          duration: Duration(seconds: 3),
        ),
      );
    }
  }
}


