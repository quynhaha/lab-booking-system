import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import '../models/event.dart';
import '../config/api_config.dart';

class ApiService {
  static String get baseUrl => ApiConfig.baseUrl;
  static Duration get timeout => ApiConfig.timeout;

  final http.Client _client = http.Client();
  String? _token;

  // Set JWT token
  void setToken(String? token) {
    _token = token;
  }

  Future<List<Event>> getEvents({int? labId}) async {
    try {
      final uri = labId != null
          ? Uri.parse('$baseUrl/events').replace(queryParameters: {'labId': labId.toString()})
          : Uri.parse('$baseUrl/events');
      
      final response = await _client
          .get(
            uri,
            headers: _getHeaders(),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = json.decode(response.body);
        final events = jsonData.map((json) => Event.fromJson(json)).toList();
        
        // Filter by labId if provided (client-side filtering as fallback)
        if (labId != null) {
          return events.where((event) => event.labId != null && event.labId == labId).toList();
        }
        
        return events;
      } else {
        throw ApiException(
          'Failed to load events: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const ApiException('No internet connection', 0);
    } on HttpException {
      throw const ApiException('HTTP error occurred', 0);
    } catch (e) {
      throw ApiException('Unexpected error: $e', 0);
    }
  }

  // Get available events (APPROVED events for teachers to select when booking)
  Future<List<Event>> getAvailableEvents({int? labId}) async {
    try {
      final response = await _client
          .get(
            Uri.parse('$baseUrl/events/available'),
            headers: _getHeaders(),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = json.decode(response.body);
        final events = jsonData.map((json) => Event.fromJson(json)).toList();
        
        // Filter by labId if provided
        if (labId != null) {
          return events.where((event) => event.labId != null && event.labId == labId).toList();
        }
        
        return events;
      } else {
        throw ApiException(
          'Failed to load available events: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const ApiException('No internet connection', 0);
    } on HttpException {
      throw const ApiException('HTTP error occurred', 0);
    } catch (e) {
      throw ApiException('Unexpected error: $e', 0);
    }
  }

  Future<Event> getEvent(int id) async {
    try {
      final response = await _client
          .get(
            Uri.parse('$baseUrl/events/$id'),
            headers: _getHeaders(),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final Map<String, dynamic> jsonData = json.decode(response.body);
        return Event.fromJson(jsonData);
      } else {
        throw ApiException(
          'Failed to load event: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const ApiException('No internet connection', 0);
    } on HttpException {
      throw const ApiException('HTTP error occurred', 0);
    } catch (e) {
      throw ApiException('Unexpected error: $e', 0);
    }
  }

  // Join event (Student only)
  Future<void> joinEvent(int eventId) async {
    try {
      final response = await _client
          .post(
            Uri.parse('$baseUrl/events/$eventId/join'),
            headers: _getHeaders(),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        return; // Success
      } else {
        final errorData = json.decode(response.body);
        throw ApiException(
          errorData['message'] ?? 'Failed to join event',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const ApiException('No internet connection', 0);
    } on HttpException {
      throw const ApiException('HTTP error occurred', 0);
    } catch (e) {
      throw ApiException('Unexpected error: $e', 0);
    }
  }

  // Get events that student has joined
  Future<List<Event>> getMyEvents() async {
    try {
      final response = await _client
          .get(
            Uri.parse('$baseUrl/events/my-events'),
            headers: _getHeaders(),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = json.decode(response.body);
        return jsonData.map((json) => Event.fromJson(json)).toList();
      } else {
        throw ApiException(
          'Failed to load my events: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const ApiException('No internet connection', 0);
    } on HttpException {
      throw const ApiException('HTTP error occurred', 0);
    } catch (e) {
      throw ApiException('Unexpected error: $e', 0);
    }
  }

  // TODO: Implement booking functionality when backend API is ready
  // Future<Booking> bookEvent(int eventId) async {
  //   try {
  //     final response = await _client
  //         .post(
  //           Uri.parse('$baseUrl/bookings'),
  //           headers: _getHeaders(),
  //           body: json.encode({'eventId': eventId}),
  //         )
  //         .timeout(timeout);
  //
  //     if (response.statusCode == 201) {
  //       final Map<String, dynamic> jsonData = json.decode(response.body);
  //       return Booking.fromJson(jsonData);
  //     } else {
  //       throw ApiException(
  //         'Failed to book event: ${response.statusCode}',
  //         response.statusCode,
  //       );
  //     }
  //   } on SocketException {
  //     throw const ApiException('No internet connection', 0);
  //   } on HttpException {
  //     throw const ApiException('HTTP error occurred', 0);
  //   } catch (e) {
  //     throw ApiException('Unexpected error: $e', 0);
  //   }
  // }

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

class ApiException implements Exception {
  final String message;
  final int statusCode;

  const ApiException(this.message, this.statusCode);

  @override
  String toString() => message;
}
