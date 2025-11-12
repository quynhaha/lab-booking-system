import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import '../models/lab.dart';
import '../config/api_config.dart';

class LabService {
  static String get baseUrl => ApiConfig.labsUrl;
  static const Duration timeout = Duration(seconds: 30);

  final http.Client _client = http.Client();
  String? _token;

  // Set JWT token
  void setToken(String? token) {
    _token = token;
  }

  Future<List<Lab>> getAllLabs() async {
    try {
      final response = await _client
          .get(
            Uri.parse(baseUrl),
            headers: _getHeaders(),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = json.decode(response.body);
        return jsonData.map((json) => Lab.fromJson(json)).toList();
      } else {
        throw LabServiceException(
          'Failed to load labs: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const LabServiceException('No internet connection', 0);
    } on HttpException {
      throw const LabServiceException('HTTP error occurred', 0);
    } catch (e) {
      throw LabServiceException('Unexpected error: $e', 0);
    }
  }

  Future<List<Lab>> getAvailableLabs() async {
    try {
      final response = await _client
          .get(
            Uri.parse('$baseUrl/available'),
            headers: _getHeaders(),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = json.decode(response.body);
        return jsonData.map((json) => Lab.fromJson(json)).toList();
      } else {
        throw LabServiceException(
          'Failed to load available labs: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const LabServiceException('No internet connection', 0);
    } on HttpException {
      throw const LabServiceException('HTTP error occurred', 0);
    } catch (e) {
      throw LabServiceException('Unexpected error: $e', 0);
    }
  }

  Future<Lab> getLabById(int id) async {
    try {
      final response = await _client
          .get(
            Uri.parse('$baseUrl/$id'),
            headers: _getHeaders(),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final Map<String, dynamic> jsonData = json.decode(response.body);
        return Lab.fromJson(jsonData);
      } else {
        throw LabServiceException(
          'Failed to load lab: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const LabServiceException('No internet connection', 0);
    } on HttpException {
      throw const LabServiceException('HTTP error occurred', 0);
    } catch (e) {
      throw LabServiceException('Unexpected error: $e', 0);
    }
  }

  Future<List<Lab>> searchLabs(String keyword) async {
    try {
      final response = await _client
          .get(
            Uri.parse('$baseUrl/search?keyword=$keyword'),
            headers: _getHeaders(),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final List<dynamic> jsonData = json.decode(response.body);
        return jsonData.map((json) => Lab.fromJson(json)).toList();
      } else {
        throw LabServiceException(
          'Failed to search labs: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const LabServiceException('No internet connection', 0);
    } on HttpException {
      throw const LabServiceException('HTTP error occurred', 0);
    } catch (e) {
      throw LabServiceException('Unexpected error: $e', 0);
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

class LabServiceException implements Exception {
  final String message;
  final int statusCode;

  const LabServiceException(this.message, this.statusCode);

  @override
  String toString() => message;
}

