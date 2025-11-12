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

  Future<List<Event>> getEvents() async {
    try {
      final response = await _client
          .get(
            Uri.parse('$baseUrl/events'),
            headers: _getHeaders(),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = json.decode(response.body);
        return jsonData.map((json) => Event.fromJson(json)).toList();
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
