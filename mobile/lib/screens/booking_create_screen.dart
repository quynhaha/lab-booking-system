import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../models/lab.dart';
import '../models/lab_booking_slot.dart';
import '../models/event.dart';
import '../services/booking_service.dart';
import '../services/api_service.dart';
import '../providers/auth_provider.dart';

class BookingCreateScreen extends StatefulWidget {
  final Lab lab;
  final Event? event; // Optional: If provided, this event will be pre-selected

  const BookingCreateScreen({
    super.key,
    required this.lab,
    this.event,
  });

  @override
  State<BookingCreateScreen> createState() => _BookingCreateScreenState();
}

class _BookingCreateScreenState extends State<BookingCreateScreen> {
  final _formKey = GlobalKey<FormState>();
  final _titleController = TextEditingController();
  final _descriptionController = TextEditingController();
  final _participantsController = TextEditingController();

  DateTime? _selectedDate;
  int? _selectedSlot; // 1-4
  bool _isLoading = false;
  
  // Event selection
  List<Event> _events = [];
  Event? _selectedEvent;
  bool _isLoadingEvents = false;
  
  // Slot definitions
  static const Map<int, Map<String, String>> _slots = {
    1: {'name': 'Slot 1', 'time': '08:00 - 11:00'},
    2: {'name': 'Slot 2', 'time': '11:00 - 14:00'},
    3: {'name': 'Slot 3', 'time': '14:00 - 17:00'},
    4: {'name': 'Slot 4', 'time': '17:00 - 20:00'},
  };

  @override
  void initState() {
    super.initState();
    // Pre-select event if provided
    if (widget.event != null) {
      _selectedEvent = widget.event;
      // Add to events list if not already there
      _events = [widget.event!];
    }
    _loadEvents();
  }

  @override
  void dispose() {
    _titleController.dispose();
    _descriptionController.dispose();
    _participantsController.dispose();
    super.dispose();
  }

  Future<void> _selectDate(BuildContext context) async {
    final DateTime? picked = await showDatePicker(
      context: context,
      initialDate: _selectedDate ?? DateTime.now().add(const Duration(days: 1)),
      firstDate: DateTime.now(),
      lastDate: DateTime.now().add(const Duration(days: 365)),
      builder: (context, child) {
        return Theme(
          data: Theme.of(context).copyWith(
            colorScheme: ColorScheme.light(
              primary: Theme.of(context).colorScheme.primary,
            ),
          ),
          child: child!,
        );
      },
    );
    if (picked != null && picked != _selectedDate) {
      setState(() {
        _selectedDate = picked;
      });
    }
  }

  void _selectSlot(int slotNumber) {
    setState(() {
      _selectedSlot = slotNumber;
    });
  }

  String _formatDate(DateTime? date) {
    if (date == null) return 'Chọn ngày';
    return DateFormat('dd/MM/yyyy').format(date);
  }

  String _formatDateForApi(DateTime date) {
    return '${date.year}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')}';
  }

  Future<void> _loadEvents() async {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    
    // Only load events if user is TEACHER (STUDENT cannot select events)
    if (authProvider.currentUser == null || !authProvider.currentUser!.isTeacher) {
      setState(() {
        _isLoadingEvents = false;
        _events = [];
      });
      return;
    }

    setState(() {
      _isLoadingEvents = true;
    });

    try {
      final apiService = Provider.of<ApiService>(context, listen: false);
      
      // Set token if available
      if (authProvider.token != null) {
        apiService.setToken(authProvider.token!);
      }
      
      // Use getAvailableEvents() to get only APPROVED events
      // Don't filter by labId - teacher can book any lab for an event
      final events = await apiService.getAvailableEvents();
      
      setState(() {
        // If we have a pre-selected event, make sure it's in the list
        if (widget.event != null) {
          final eventIds = events.map((e) => e.id).toSet();
          if (!eventIds.contains(widget.event!.id)) {
            _events = [widget.event!, ...events];
          } else {
            _events = events;
            // Re-select the event from the loaded list
            _selectedEvent = events.firstWhere((e) => e.id == widget.event!.id);
          }
        } else {
          _events = events;
        }
        _isLoadingEvents = false;
      });
    } catch (e) {
      setState(() {
        _isLoadingEvents = false;
        _events = [];
      });
      // Silently fail - events are optional
      debugPrint('Failed to load events: $e');
    }
  }

  Future<void> _submitBooking() async {
    if (!_formKey.currentState!.validate()) {
      return;
    }

    if (_selectedDate == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Vui lòng chọn ngày')),
      );
      return;
    }

    if (_selectedSlot == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Vui lòng chọn slot thời gian')),
      );
      return;
    }

    // Build description (no need to embed event ID anymore, we'll send it separately)
    String? description = _descriptionController.text.trim().isEmpty 
        ? null 
        : _descriptionController.text.trim();

    final labSlot = LabBookingSlot(
      labId: widget.lab.id,
      title: _titleController.text.trim(),
      description: description,
      participantsCount: int.parse(_participantsController.text.trim()),
      slotNumber: _selectedSlot,
      bookingDate: _formatDateForApi(_selectedDate!),
    );

    setState(() {
      _isLoading = true;
    });

    try {
      final bookingService = context.read<BookingService>();
      // Pass eventId directly to createBooking (only if TEACHER selected an event)
      final bookings = await bookingService.createBooking(
        [labSlot],
        eventId: _selectedEvent?.id,
      );

      if (mounted) {
        // If event is selected, show confirmation message
        String message = 'Đặt lịch thành công!';
        if (_selectedEvent != null) {
          message += '\nSự kiện "${_selectedEvent!.title}" đã được liên kết với đặt lịch này.';
        }
        
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(message),
            backgroundColor: Colors.green,
            duration: const Duration(seconds: 4),
          ),
        );
        Navigator.of(context).pop(true); // Return true to indicate success
      }
    } catch (e) {
      if (mounted) {
        String errorMessage = 'Lỗi: ${e.toString()}';
        // Remove "Exception: " prefix if present
        if (errorMessage.startsWith('Exception: ')) {
          errorMessage = errorMessage.substring(11);
        }
        // Remove "BookingServiceException: " prefix if present
        if (errorMessage.startsWith('BookingServiceException: ')) {
          errorMessage = errorMessage.substring(26);
        }
        
        // Show error with longer duration for conflict errors
        final isConflictError = errorMessage.toLowerCase().contains('đã được đặt') ||
                                errorMessage.toLowerCase().contains('conflict') ||
                                errorMessage.toLowerCase().contains('trùng');
        
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text(
              errorMessage,
              style: const TextStyle(fontSize: 14),
            ),
            backgroundColor: Colors.red,
            duration: Duration(seconds: isConflictError ? 6 : 4),
            action: SnackBarAction(
              label: 'Đóng',
              textColor: Colors.white,
              onPressed: () {
                ScaffoldMessenger.of(context).hideCurrentSnackBar();
              },
            ),
          ),
        );
      }
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Đặt lịch phòng Lab'),
        backgroundColor: const Color(0xFFFF6C00), // FPT Orange
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Lab Info Card
              Card(
                elevation: 2,
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        widget.lab.name,
                        style: Theme.of(context).textTheme.titleLarge?.copyWith(
                              fontWeight: FontWeight.bold,
                            ),
                      ),
                      const SizedBox(height: 8),
                      Row(
                        children: [
                          const Icon(Icons.location_on, size: 16, color: Colors.grey),
                          const SizedBox(width: 4),
                          Expanded(
                            child: Text(
                              widget.lab.location ?? 'Chưa cập nhật',
                              style: Theme.of(context).textTheme.bodyMedium,
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: 4),
                      Row(
                        children: [
                          const Icon(Icons.people, size: 16, color: Colors.grey),
                          const SizedBox(width: 4),
                          Text(
                            'Sức chứa: ${widget.lab.capacity} người',
                            style: Theme.of(context).textTheme.bodyMedium,
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 24),

              // Form Fields
              Text(
                'Thông tin đặt lịch',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
              ),
              const SizedBox(height: 16),

              // Title Field
              TextFormField(
                controller: _titleController,
                decoration: const InputDecoration(
                  labelText: 'Tiêu đề buổi học *',
                  hintText: 'VD: Thực hành Java Programming',
                  prefixIcon: Icon(Icons.title),
                  border: OutlineInputBorder(),
                ),
                textCapitalization: TextCapitalization.sentences,
                enableSuggestions: true,
                autocorrect: true,
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Vui lòng nhập tiêu đề';
                  }
                  if (value.trim().length < 3) {
                    return 'Tiêu đề phải có ít nhất 3 ký tự';
                  }
                  return null;
                },
                maxLength: 100,
              ),
              const SizedBox(height: 16),

              // Description Field
              TextFormField(
                controller: _descriptionController,
                decoration: const InputDecoration(
                  labelText: 'Mô tả (không bắt buộc)',
                  hintText: 'Mô tả chi tiết về buổi học...',
                  prefixIcon: Icon(Icons.description),
                  border: OutlineInputBorder(),
                ),
                textCapitalization: TextCapitalization.sentences,
                enableSuggestions: true,
                autocorrect: true,
                maxLines: 3,
                maxLength: 500,
              ),
              const SizedBox(height: 16),

              // Participants Count Field
              TextFormField(
                controller: _participantsController,
                decoration: const InputDecoration(
                  labelText: 'Số lượng sinh viên *',
                  hintText: 'VD: 30',
                  prefixIcon: Icon(Icons.people),
                  border: OutlineInputBorder(),
                ),
                keyboardType: TextInputType.number,
                validator: (value) {
                  if (value == null || value.trim().isEmpty) {
                    return 'Vui lòng nhập số lượng sinh viên';
                  }
                  final count = int.tryParse(value.trim());
                  if (count == null || count < 1) {
                    return 'Số lượng phải lớn hơn 0';
                  }
                  if (count > widget.lab.capacity) {
                    return 'Vượt quá sức chứa (${widget.lab.capacity} người)';
                  }
                  return null;
                },
              ),
              const SizedBox(height: 24),

              // Date Selection
              Text(
                'Chọn ngày và giờ',
                style: Theme.of(context).textTheme.titleMedium?.copyWith(
                      fontWeight: FontWeight.bold,
                    ),
              ),
              const SizedBox(height: 16),

              // Date Picker
              InkWell(
                onTap: () => _selectDate(context),
                child: InputDecorator(
                  decoration: const InputDecoration(
                    labelText: 'Ngày *',
                    prefixIcon: Icon(Icons.calendar_today),
                    border: OutlineInputBorder(),
                  ),
                  child: Text(
                    _formatDate(_selectedDate),
                    style: TextStyle(
                      color: _selectedDate == null ? Colors.grey : null,
                    ),
                  ),
                ),
              ),
              const SizedBox(height: 16),

              // Event Selection (Optional - Only for TEACHER)
              if (Provider.of<AuthProvider>(context).currentUser?.isTeacher == true) ...[
                if (widget.event != null) ...[
                  // Show info banner if event was pre-selected
                  Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: Colors.blue.shade50,
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(color: Colors.blue.shade200),
                    ),
                    child: Row(
                      children: [
                        Icon(Icons.info_outline, color: Colors.blue.shade700, size: 20),
                        const SizedBox(width: 8),
                        Expanded(
                          child: Text(
                            'Đang book lab cho sự kiện: "${widget.event!.title}"',
                            style: TextStyle(
                              color: Colors.blue.shade900,
                              fontWeight: FontWeight.w500,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 16),
                ],
                if (!_isLoadingEvents && _events.isNotEmpty) ...[
                  Text(
                    widget.event != null 
                      ? 'Sự kiện đã chọn (có thể đổi)' 
                      : 'Chọn sự kiện (không bắt buộc)',
                    style: Theme.of(context).textTheme.titleMedium?.copyWith(
                          fontWeight: FontWeight.bold,
                        ),
                  ),
                  const SizedBox(height: 8),
                DropdownButtonFormField<Event>(
                  value: _selectedEvent,
                  decoration: const InputDecoration(
                    labelText: 'Sự kiện',
                    hintText: 'Chọn sự kiện (nếu có)',
                    prefixIcon: Icon(Icons.event),
                    border: OutlineInputBorder(),
                  ),
                  items: [
                    const DropdownMenuItem<Event>(
                      value: null,
                      child: Text('Không chọn sự kiện'),
                    ),
                    ..._events.map((event) {
                      return DropdownMenuItem<Event>(
                        value: event,
                        child: Text(
                          event.title,
                          style: const TextStyle(fontWeight: FontWeight.bold),
                          overflow: TextOverflow.ellipsis,
                        ),
                      );
                    }),
                  ],
                  onChanged: (Event? value) {
                    setState(() {
                      _selectedEvent = value;
                    });
                  },
                ),
                const SizedBox(height: 24),
              ],
              ],

              // Slot Selection
              Text(
                'Chọn slot thời gian *',
                style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                      fontWeight: FontWeight.w500,
                    ),
              ),
              const SizedBox(height: 8),
              Wrap(
                spacing: 12,
                runSpacing: 12,
                children: _slots.entries.map((entry) {
                  final slotNumber = entry.key;
                  final slotInfo = entry.value;
                  final isSelected = _selectedSlot == slotNumber;
                  
                  return InkWell(
                    onTap: () => _selectSlot(slotNumber),
                    child: Container(
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                      decoration: BoxDecoration(
                        color: isSelected
                            ? Theme.of(context).colorScheme.primary
                            : Theme.of(context).colorScheme.surface,
                        border: Border.all(
                          color: isSelected
                              ? Theme.of(context).colorScheme.primary
                              : Theme.of(context).colorScheme.outline,
                          width: isSelected ? 2 : 1,
                        ),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Text(
                            slotInfo['name']!,
                            style: TextStyle(
                              fontWeight: FontWeight.bold,
                              color: isSelected
                                  ? Colors.white
                                  : Theme.of(context).colorScheme.onSurface,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            slotInfo['time']!,
                            style: TextStyle(
                              fontSize: 12,
                              color: isSelected
                                  ? Colors.white70
                                  : Theme.of(context).colorScheme.onSurface.withOpacity(0.7),
                            ),
                          ),
                        ],
                      ),
                    ),
                  );
                }).toList(),
              ),
              const SizedBox(height: 32),

              // Submit Button
              SizedBox(
                width: double.infinity,
                height: 50,
                child: ElevatedButton(
                  onPressed: _isLoading ? null : _submitBooking,
                  style: ElevatedButton.styleFrom(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                  ),
                  child: _isLoading
                      ? const SizedBox(
                          height: 20,
                          width: 20,
                          child: CircularProgressIndicator(
                            strokeWidth: 2,
                            valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                          ),
                        )
                      : const Text(
                          'Xác nhận đặt lịch',
                          style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                        ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}





