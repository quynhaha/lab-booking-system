import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import 'package:go_router/go_router.dart';
import '../services/api_service.dart';
import '../services/lab_service.dart';
import '../models/event.dart';
import '../models/lab.dart';
import '../providers/auth_provider.dart';
import 'booking_create_screen.dart';

class EventsScreen extends StatefulWidget {
  const EventsScreen({super.key});

  @override
  State<EventsScreen> createState() => _EventsScreenState();
}

class _EventsScreenState extends State<EventsScreen> {
  List<Event> events = [];
  Set<int> joinedEventIds = {}; // Track which events student has joined
  bool isLoading = true;
  String? error;

  @override
  void initState() {
    super.initState();
    _loadEvents();
  }

  Future<void> _loadEvents() async {
    try {
      setState(() {
        isLoading = true;
        error = null;
      });

      final apiService = Provider.of<ApiService>(context, listen: false);
      final authProvider = Provider.of<AuthProvider>(context, listen: false);
      
      // Set token if available
      if (authProvider.token != null) {
        apiService.setToken(authProvider.token!);
      }
      
      List<Event> fetchedEvents;
      
      // Teacher: Get only APPROVED events (for booking)
      // Student: Get all events (to view and join)
      if (authProvider.currentUser?.isTeacher == true) {
        fetchedEvents = await apiService.getAvailableEvents();
      } else {
        fetchedEvents = await apiService.getEvents();
        
        // If student, also load joined events to check participation status
        if (authProvider.currentUser?.isStudent == true) {
          try {
            final joinedEvents = await apiService.getMyEvents();
            joinedEventIds = joinedEvents.map((e) => e.id).toSet();
          } catch (e) {
            // Ignore error, just don't mark any as joined
            debugPrint('Failed to load joined events: $e');
          }
        }
      }
      
      setState(() {
        events = fetchedEvents;
        isLoading = false;
      });
    } catch (e) {
      setState(() {
        error = e.toString();
        isLoading = false;
      });
    }
  }
  
  bool _isEventJoined(int eventId) {
    return joinedEventIds.contains(eventId);
  }

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    final isTeacher = authProvider.currentUser?.isTeacher == true;
    
    return Scaffold(
      appBar: AppBar(
        title: Text(isTeacher ? 'Sự kiện (Chọn để Book Lab)' : 'Sự kiện'),
        backgroundColor: const Color(0xFFFF6C00), // FPT Orange
        foregroundColor: Colors.white,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.pop(),
        ),
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
              'Error loading events',
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
              onPressed: _loadEvents,
              child: const Text('Retry'),
            ),
          ],
        ),
      );
    }

    if (events.isEmpty) {
      final authProvider = Provider.of<AuthProvider>(context, listen: false);
      final isTeacher = authProvider.currentUser?.isTeacher == true;
      
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.event_busy,
              size: 64,
              color: Theme.of(context).colorScheme.onSurfaceVariant,
            ),
            const SizedBox(height: 16),
            Text(
              isTeacher ? 'Chưa có sự kiện được duyệt' : 'Chưa có sự kiện',
              style: Theme.of(context).textTheme.titleLarge,
            ),
            const SizedBox(height: 8),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 32),
              child: Text(
                isTeacher 
                  ? 'Chỉ có các sự kiện đã được ADMIN duyệt (APPROVED) mới hiển thị ở đây để bạn có thể book lab.'
                  : 'Kiểm tra lại sau để xem các sự kiện mới.',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                  color: Theme.of(context).colorScheme.onSurfaceVariant,
                ),
                textAlign: TextAlign.center,
              ),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadEvents,
      child: ListView.builder(
        padding: const EdgeInsets.all(16),
        itemCount: events.length,
        itemBuilder: (context, index) {
          final event = events[index];
          return _buildEventCard(event);
        },
      ),
    );
  }

  Widget _buildEventCard(Event event) {
    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      elevation: 4,
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    event.title,
                    style: Theme.of(context).textTheme.titleLarge?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.primaryContainer,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Text(
                    event.status,
                    style: Theme.of(context).textTheme.labelSmall?.copyWith(
                      color: Theme.of(context).colorScheme.onPrimaryContainer,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            if (event.description != null && event.description!.isNotEmpty) ...[
              Text(
                event.description!,
                style: Theme.of(context).textTheme.bodyMedium,
              ),
              const SizedBox(height: 8),
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
                    _formatEventTime(event.startTime, event.endTime),
                    style: Theme.of(context).textTheme.bodySmall?.copyWith(
                      color: Theme.of(context).colorScheme.onSurfaceVariant,
                    ),
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ],
            ),
            if (event.labName != null) ...[
              const SizedBox(height: 8),
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
                      event.labName!,
                      style: Theme.of(context).textTheme.bodySmall?.copyWith(
                        color: Theme.of(context).colorScheme.onSurfaceVariant,
                      ),
                    ),
                  ),
                ],
              ),
            ],
            const SizedBox(height: 16),
            Builder(
              builder: (context) {
                final authProvider = Provider.of<AuthProvider>(context, listen: false);
                final isTeacher = authProvider.currentUser?.isTeacher == true;
                final isStudent = authProvider.currentUser?.isStudent == true;
                
                if (isTeacher) {
                  // Teacher: Book lab for this event
                  return SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      onPressed: () => _bookLabForEvent(event),
                      icon: const Icon(Icons.book_online),
                      label: const Text('Book Lab cho Sự kiện'),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Theme.of(context).colorScheme.primary,
                        foregroundColor: Colors.white,
                      ),
                    ),
                  );
                } else if (isStudent) {
                  // Student: View details and join event
                  final isJoined = _isEventJoined(event.id);
                  final canJoin = event.status == 'APPROVED' && !isJoined;
                  
                  return Row(
                    children: [
                      Expanded(
                        child: OutlinedButton.icon(
                          onPressed: () => _viewEventDetails(event),
                          icon: const Icon(Icons.info_outline),
                          label: const Text('Chi tiết'),
                        ),
                      ),
                      const SizedBox(width: 8),
                      Expanded(
                        child: ElevatedButton.icon(
                          onPressed: canJoin ? () => _joinEvent(event) : null,
                          icon: Icon(isJoined ? Icons.check_circle : Icons.person_add),
                          label: Text(isJoined ? 'Đã tham gia' : 'Tham gia'),
                          style: isJoined 
                            ? ElevatedButton.styleFrom(
                                backgroundColor: Colors.grey,
                                foregroundColor: Colors.white,
                              )
                            : null,
                        ),
                      ),
                    ],
                  );
                } else {
                  // Default: Just view details
                  return SizedBox(
                    width: double.infinity,
                    child: ElevatedButton.icon(
                      onPressed: () => _viewEventDetails(event),
                      icon: const Icon(Icons.info_outline),
                      label: const Text('Xem chi tiết'),
                    ),
                  );
                }
              },
            ),
          ],
        ),
      ),
    );
  }

  void _bookLabForEvent(Event event) async {
    // Teacher: Show dialog to select lab, then open booking form directly
    final lab = await _showLabSelectionDialog(event);
    if (lab != null && mounted) {
      // Navigate directly to booking screen with selected lab and event
      final result = await Navigator.of(context).push(
        MaterialPageRoute(
          builder: (context) => BookingCreateScreen(lab: lab, event: event),
        ),
      );
      
      // Refresh events list if booking was successful
      if (result == true) {
        _loadEvents();
      }
    }
  }
  
  Future<Lab?> _showLabSelectionDialog(Event event) async {
    final labService = LabService();
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    
    // Set token if available
    if (authProvider.token != null) {
      labService.setToken(authProvider.token!);
    }
    
    // Load labs - use getAllLabs to show all labs, not just available ones
    List<Lab> labs = [];
    bool isLoading = true;
    String? error;
    
    try {
      // Get all labs first, then filter available ones in UI
      labs = await labService.getAllLabs();
      isLoading = false;
    } catch (e) {
      error = e.toString();
      isLoading = false;
    }
    
    return showDialog<Lab>(
      context: context,
      builder: (context) => AlertDialog(
        title: Row(
          children: [
            const Icon(Icons.location_on, color: Color(0xFFFF6C00)),
            const SizedBox(width: 8),
            Expanded(
              child: Text(
                'Chọn Lab cho Sự kiện',
                style: Theme.of(context).textTheme.titleLarge,
              ),
            ),
          ],
        ),
        content: SizedBox(
          width: double.maxFinite,
          child: isLoading
              ? const Center(child: CircularProgressIndicator())
              : error != null
                  ? Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(Icons.error_outline, color: Colors.red, size: 48),
                        const SizedBox(height: 16),
                        Text(
                          'Lỗi tải danh sách lab',
                          style: Theme.of(context).textTheme.titleMedium,
                        ),
                        const SizedBox(height: 8),
                        Text(
                          error!,
                          style: Theme.of(context).textTheme.bodySmall?.copyWith(
                                color: Colors.red,
                              ),
                          textAlign: TextAlign.center,
                        ),
                      ],
                    )
                  : labs.isEmpty
                      ? Column(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Icon(Icons.location_off, size: 48, color: Colors.grey),
                            const SizedBox(height: 16),
                            Text(
                              'Không có lab khả dụng',
                              style: Theme.of(context).textTheme.titleMedium,
                            ),
                          ],
                        )
                      : SingleChildScrollView(
                          child: Column(
                            mainAxisSize: MainAxisSize.min,
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              // Event info
                              Container(
                                padding: const EdgeInsets.all(12),
                                decoration: BoxDecoration(
                                  color: Colors.blue.shade50,
                                  borderRadius: BorderRadius.circular(8),
                                  border: Border.all(color: Colors.blue.shade200),
                                ),
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Row(
                                      children: [
                                        Icon(Icons.event, size: 16, color: Colors.blue.shade700),
                                        const SizedBox(width: 4),
                                        Text(
                                          'Sự kiện: ${event.title}',
                                          style: TextStyle(
                                            fontWeight: FontWeight.bold,
                                            color: Colors.blue.shade900,
                                          ),
                                        ),
                                      ],
                                    ),
                                    const SizedBox(height: 4),
                                    Text(
                                      'Thời gian: ${_formatEventTime(event.startTime, event.endTime)}',
                                      style: TextStyle(
                                        fontSize: 12,
                                        color: Colors.blue.shade700,
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                              const SizedBox(height: 16),
                              Text(
                                'Chọn lab:',
                                style: Theme.of(context).textTheme.titleSmall?.copyWith(
                                      fontWeight: FontWeight.bold,
                                    ),
                              ),
                              const SizedBox(height: 8),
                              // Show all labs, but highlight available ones
                              ...labs.map((lab) {
                                return Card(
                                  margin: const EdgeInsets.only(bottom: 8),
                                  color: lab.isAvailable ? null : Colors.grey.shade100,
                                  child: ListTile(
                                    leading: Icon(
                                      lab.isAvailable ? Icons.check_circle : Icons.cancel,
                                      color: lab.isAvailable ? Colors.green : Colors.grey,
                                    ),
                                    title: Text(
                                      lab.name,
                                      style: TextStyle(
                                        fontWeight: FontWeight.bold,
                                        color: lab.isAvailable ? null : Colors.grey,
                                      ),
                                    ),
                                    subtitle: Column(
                                      crossAxisAlignment: CrossAxisAlignment.start,
                                      children: [
                                        if (lab.location != null) 
                                          Text(
                                            lab.location!,
                                            style: TextStyle(
                                              color: lab.isAvailable ? null : Colors.grey,
                                            ),
                                          ),
                                        Text(
                                          'Sức chứa: ${lab.capacity} người',
                                          style: TextStyle(
                                            color: lab.isAvailable ? null : Colors.grey,
                                          ),
                                        ),
                                        Text(
                                          lab.statusDisplay,
                                          style: TextStyle(
                                            color: lab.isAvailable ? Colors.green : Colors.grey,
                                            fontWeight: FontWeight.w500,
                                          ),
                                        ),
                                      ],
                                    ),
                                    enabled: true, // Allow clicking on all labs
                                    onTap: () {
                                      if (lab.isAvailable) {
                                        Navigator.of(context).pop(lab);
                                      } else {
                                        // Show message if lab is not available
                                        ScaffoldMessenger.of(context).showSnackBar(
                                          SnackBar(
                                            content: Text('Lab "${lab.name}" không khả dụng. Vui lòng chọn lab khác.'),
                                            backgroundColor: Colors.orange,
                                            duration: const Duration(seconds: 2),
                                          ),
                                        );
                                      }
                                    },
                                  ),
                                );
                              }),
                            ],
                          ),
                        ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Hủy'),
          ),
        ],
      ),
    );
  }
  
  String _formatEventTime(String? startTime, String? endTime) {
    if (startTime == null || endTime == null) {
      return 'Chưa có thời gian (sẽ được đặt khi book lab)';
    }
    
    try {
      final start = DateTime.parse(startTime);
      final end = DateTime.parse(endTime);
      
      final dateFormat = DateFormat('dd/MM/yyyy');
      final timeFormat = DateFormat('HH:mm');
      
      final startDate = dateFormat.format(start);
      final startTimeStr = timeFormat.format(start);
      final endDate = dateFormat.format(end);
      final endTimeStr = timeFormat.format(end);
      
      // If same day, show: "dd/MM/yyyy HH:mm - HH:mm"
      if (startDate == endDate) {
        return '$startDate $startTimeStr - $endTimeStr';
      } else {
        // Different days: "dd/MM/yyyy HH:mm - dd/MM/yyyy HH:mm"
        return '$startDate $startTimeStr - $endDate $endTimeStr';
      }
    } catch (e) {
      // Fallback to original format if parsing fails
      return '$startTime - $endTime';
    }
  }

  void _joinEvent(Event event) {
    // Student: Join/Register for this event
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Tham gia ${event.title}'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Bạn có muốn tham gia sự kiện này không?'),
            const SizedBox(height: 16),
            if (event.description != null && event.description!.isNotEmpty) ...[
              Text('Mô tả: ${event.description}'),
              const SizedBox(height: 8),
            ],
            Text('Thời gian: ${_formatEventTime(event.startTime, event.endTime)}'),
            if (event.labName != null) ...[
              const SizedBox(height: 8),
              Text('Lab: ${event.labName}'),
            ],
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Hủy'),
          ),
          ElevatedButton(
            onPressed: () async {
              Navigator.of(context).pop();
              try {
                final apiService = Provider.of<ApiService>(context, listen: false);
                final authProvider = Provider.of<AuthProvider>(context, listen: false);
                
                // Set token if available
                if (authProvider.token != null) {
                  apiService.setToken(authProvider.token!);
                }
                
                await apiService.joinEvent(event.id);
                
                // Update joined events set
                setState(() {
                  joinedEventIds.add(event.id);
                });
                
                if (mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text('Đã tham gia sự kiện "${event.title}"'),
                      backgroundColor: Colors.green,
                    ),
                  );
                }
              } catch (e) {
                if (mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(
                      content: Text('Lỗi: ${e.toString()}'),
                      backgroundColor: Colors.red,
                    ),
                  );
                }
              }
            },
            child: const Text('Xác nhận'),
          ),
        ],
      ),
    );
  }

  void _viewEventDetails(Event event) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(event.title),
        content: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              if (event.description != null && event.description!.isNotEmpty) ...[
                Text('Description: ${event.description}'),
                const SizedBox(height: 8),
              ],
              Text('Thời gian: ${_formatEventTime(event.startTime, event.endTime)}'),
              const SizedBox(height: 8),
              if (event.labName != null) ...[
                Text('Lab: ${event.labName}'),
                const SizedBox(height: 8),
              ],
              if (event.userFullName != null) ...[
                Text('Created by: ${event.userFullName}'),
                const SizedBox(height: 8),
              ],
              Text('Status: ${event.status}'),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('Close'),
          ),
        ],
      ),
    );
  }
}
