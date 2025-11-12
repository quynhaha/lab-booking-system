import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import '../models/booking.dart';
import '../models/lab_booking_slot.dart';
import '../config/api_config.dart';

class BookingService {
  static String get baseUrl => ApiConfig.bookingsUrl;
  static const Duration timeout = Duration(seconds: 30);

  final http.Client _client = http.Client();
  String? _token;

  // Set JWT token
  void setToken(String? token) {
    _token = token;
  }

  Future<List<Booking>> getAllBookings() async {
    try {
      final response = await _client
          .get(
            Uri.parse(baseUrl),
            headers: _getHeaders(),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = json.decode(response.body);
        return jsonData.map((json) => Booking.fromJson(json)).toList();
      } else {
        throw BookingServiceException(
          'Failed to load bookings: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const BookingServiceException('No internet connection', 0);
    } on HttpException {
      throw const BookingServiceException('HTTP error occurred', 0);
    } catch (e) {
      throw BookingServiceException('Unexpected error: $e', 0);
    }
  }

  Future<List<Booking>> getUserBookings(int userId) async {
    try {
      final response = await _client
          .get(
            Uri.parse('$baseUrl/user/$userId'),
            headers: _getHeaders(),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = json.decode(response.body);
        return jsonData.map((json) => Booking.fromJson(json)).toList();
      } else {
        throw BookingServiceException(
          'Failed to load user bookings: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const BookingServiceException('No internet connection', 0);
    } on HttpException {
      throw const BookingServiceException('HTTP error occurred', 0);
    } catch (e) {
      throw BookingServiceException('Unexpected error: $e', 0);
    }
  }

  Future<Booking> getBookingById(int id) async {
    try {
      final response = await _client
          .get(
            Uri.parse('$baseUrl/$id'),
            headers: _getHeaders(),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final Map<String, dynamic> jsonData = json.decode(response.body);
        return Booking.fromJson(jsonData);
      } else {
        throw BookingServiceException(
          'Failed to load booking: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const BookingServiceException('No internet connection', 0);
    } on HttpException {
      throw const BookingServiceException('HTTP error occurred', 0);
    } catch (e) {
      throw BookingServiceException('Unexpected error: $e', 0);
    }
  }

  Future<List<Booking>> createBooking(List<LabBookingSlot> labSlots, {int? categoryId}) async {
    try {
      // Validate slots have required fields for API
      for (var slot in labSlots) {
        if (slot.slotNumber == null || slot.bookingDate == null) {
          throw BookingServiceException(
            'Slot must have slotNumber and bookingDate for API booking',
            400,
          );
        }
      }

      // Use toApiJson() to send correct format to backend
      final Map<String, dynamic> requestBody = {
        'labSlots': labSlots.map((slot) => slot.toApiJson()).toList(),
        if (categoryId != null) 'categoryId': categoryId,
      };

      final response = await _client
          .post(
            Uri.parse(baseUrl),
            headers: _getHeaders(),
            body: json.encode(requestBody),
          )
          .timeout(timeout);

      if (response.statusCode == 201 || response.statusCode == 200) {
        final List<dynamic> jsonData = json.decode(response.body);
        return jsonData.map((json) => Booking.fromJson(json)).toList();
      } else {
        final errorBody = response.body;
        throw BookingServiceException(
          'Failed to create booking: ${response.statusCode} - $errorBody',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const BookingServiceException('No internet connection', 0);
    } on HttpException {
      throw const BookingServiceException('HTTP error occurred', 0);
    } catch (e) {
      if (e is BookingServiceException) {
        rethrow;
      }
      throw BookingServiceException('Unexpected error: $e', 0);
    }
  }

  Future<Booking> cancelBooking(int bookingId, String reason) async {
    try {
      final response = await _client
          .put(
            Uri.parse('$baseUrl/cancel'),
            headers: _getHeaders(),
            body: json.encode({
              'bookingId': bookingId,
              'reason': reason,
            }),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final Map<String, dynamic> jsonData = json.decode(response.body);
        return Booking.fromJson(jsonData);
      } else {
        throw BookingServiceException(
          'Failed to cancel booking: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const BookingServiceException('No internet connection', 0);
    } on HttpException {
      throw const BookingServiceException('HTTP error occurred', 0);
    } catch (e) {
      throw BookingServiceException('Unexpected error: $e', 0);
    }
  }

  Map<String, String> _getHeaders() {
    final headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };
    
    // Add JWT token if available
    if (_token != null && _token!.isNotEmpty) {
      headers['Authorization'] = 'Bearer $_token';
    }
    
    return headers;
  }

  void dispose() {
    _client.close();
  }
}

class BookingServiceException implements Exception {
  final String message;
  final int statusCode;

  const BookingServiceException(this.message, this.statusCode);

  @override
  String toString() => message;
}

