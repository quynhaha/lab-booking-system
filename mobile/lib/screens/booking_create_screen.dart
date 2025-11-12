import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:intl/intl.dart';
import '../models/lab.dart';
import '../models/lab_booking_slot.dart';
import '../services/booking_service.dart';
import '../providers/auth_provider.dart';

class BookingCreateScreen extends StatefulWidget {
  final Lab lab;

  const BookingCreateScreen({
    super.key,
    required this.lab,
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
  
  // Slot definitions
  static const Map<int, Map<String, String>> _slots = {
    1: {'name': 'Slot 1', 'time': '08:00 - 11:00'},
    2: {'name': 'Slot 2', 'time': '11:00 - 14:00'},
    3: {'name': 'Slot 3', 'time': '14:00 - 17:00'},
    4: {'name': 'Slot 4', 'time': '17:00 - 20:00'},
  };

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

    final labSlot = LabBookingSlot(
      labId: widget.lab.id,
      title: _titleController.text.trim(),
      description: _descriptionController.text.trim().isEmpty 
          ? null 
          : _descriptionController.text.trim(),
      participantsCount: int.parse(_participantsController.text.trim()),
      slotNumber: _selectedSlot,
      bookingDate: _formatDateForApi(_selectedDate!),
    );

    setState(() {
      _isLoading = true;
    });

    try {
      final bookingService = context.read<BookingService>();
      await bookingService.createBooking([labSlot]);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('Đặt lịch thành công!'),
            backgroundColor: Colors.green,
          ),
        );
        Navigator.of(context).pop(true); // Return true to indicate success
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





