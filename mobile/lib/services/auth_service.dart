import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/login_request.dart';
import '../models/login_response.dart';
import '../models/user.dart';
import '../config/api_config.dart';

class AuthService {
  static String get baseUrl => ApiConfig.authUrl;
  static Duration get timeout => ApiConfig.timeout;

  final http.Client _client = http.Client();
  
  // Storage keys
  static const String _tokenKey = 'jwt_token';
  static const String _userKey = 'user_data';

  // ========== LOGIN WITH GOOGLE ==========
  Future<LoginResponse> loginWithGoogle({
    required String idToken,
  }) async {
    try {
      final response = await _client
          .post(
            Uri.parse('$baseUrl/google'),
            headers: {
              'Content-Type': 'application/json',
            },
            body: json.encode({'idToken': idToken}),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final loginResponse = LoginResponse.fromJson(json.decode(response.body));
        
        // Save token to SharedPreferences
        await _saveToken(loginResponse.token);
        
        // Fetch and save user info
        await fetchAndSaveUserInfo(loginResponse.token);
        
        return loginResponse;
      } else if (response.statusCode == 401) {
        throw AuthException('Token Google không hợp lệ', response.statusCode);
      } else if (response.statusCode == 403) {
        throw AuthException('Tài khoản đã bị khóa', response.statusCode);
      } else {
        throw AuthException(
          'Đăng nhập Google thất bại: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const AuthException('Không có kết nối internet', 0);
    } on HttpException {
      throw const AuthException('Lỗi HTTP', 0);
    } on FormatException {
      throw const AuthException('Lỗi định dạng dữ liệu', 0);
    } catch (e) {
      if (e is AuthException) rethrow;
      throw AuthException('Lỗi không xác định: $e', 0);
    }
  }

  // ========== LOGIN WITH FPT SSO ==========
  Future<LoginResponse> loginWithFptSso({
    required String email,
    required String password,
  }) async {
    try {
      final request = LoginRequest(email: email, password: password);
      
      final response = await _client
          .post(
            Uri.parse('$baseUrl/login/fpt-sso'),
            headers: {
              'Content-Type': 'application/json',
            },
            body: json.encode(request.toJson()),
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final loginResponse = LoginResponse.fromJson(json.decode(response.body));
        
        // Save token to SharedPreferences
        await _saveToken(loginResponse.token);
        
        // Fetch and save user info
        await fetchAndSaveUserInfo(loginResponse.token);
        
        return loginResponse;
      } else if (response.statusCode == 401) {
        throw AuthException('Email hoặc mật khẩu không đúng', response.statusCode);
      } else if (response.statusCode == 403) {
        throw AuthException('Tài khoản đã bị khóa', response.statusCode);
      } else {
        throw AuthException(
          'Đăng nhập thất bại: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const AuthException('Không có kết nối internet', 0);
    } on HttpException {
      throw const AuthException('Lỗi HTTP', 0);
    } on FormatException {
      throw const AuthException('Lỗi định dạng dữ liệu', 0);
    } catch (e) {
      if (e is AuthException) rethrow;
      throw AuthException('Lỗi không xác định: $e', 0);
    }
  }

  // ========== REGISTER ==========
  Future<LoginResponse> register({
    required String email,
    required String password,
    required String fullName,
    String? studentId,
    String? faculty,
  }) async {
    try {
      final body = {
        'email': email,
        'password': password,
        'fullName': fullName,
        if (studentId != null) 'studentId': studentId,
        if (faculty != null) 'faculty': faculty,
      };

      final response = await _client
          .post(
            Uri.parse('$baseUrl/register'),
            headers: {
              'Content-Type': 'application/json',
            },
            body: json.encode(body),
          )
          .timeout(timeout);

      if (response.statusCode == 201 || response.statusCode == 200) {
        final loginResponse = LoginResponse.fromJson(json.decode(response.body));
        
        // Save token
        await _saveToken(loginResponse.token);
        
        // Fetch and save user info
        await fetchAndSaveUserInfo(loginResponse.token);
        
        return loginResponse;
      } else if (response.statusCode == 400) {
        throw AuthException('Chỉ email @fpt.edu.vn được phép đăng ký', response.statusCode);
      } else if (response.statusCode == 409) {
        throw AuthException('Email đã tồn tại', response.statusCode);
      } else {
        throw AuthException(
          'Đăng ký thất bại: ${response.statusCode}',
          response.statusCode,
        );
      }
    } on SocketException {
      throw const AuthException('Không có kết nối internet', 0);
    } on HttpException {
      throw const AuthException('Lỗi HTTP', 0);
    } catch (e) {
      if (e is AuthException) rethrow;
      throw AuthException('Lỗi không xác định: $e', 0);
    }
  }

  // ========== GET CURRENT USER INFO ==========
  Future<User> fetchAndSaveUserInfo(String token) async {
    try {
      final response = await _client
          .get(
            Uri.parse('$baseUrl/me'),
            headers: {
              'Content-Type': 'application/json',
              'Authorization': 'Bearer $token',
            },
          )
          .timeout(timeout);

      if (response.statusCode == 200) {
        final user = User.fromJson(json.decode(response.body));
        
        // Save user data to SharedPreferences
        await _saveUser(user);
        
        return user;
      } else {
        throw AuthException(
          'Không thể lấy thông tin người dùng: ${response.statusCode}',
          response.statusCode,
        );
      }
    } catch (e) {
      if (e is AuthException) rethrow;
      throw AuthException('Lỗi không xác định: $e', 0);
    }
  }

  // ========== TOKEN MANAGEMENT ==========
  Future<void> _saveToken(String token) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_tokenKey, token);
  }

  Future<String?> getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_tokenKey);
  }

  Future<void> clearToken() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_tokenKey);
  }

  // ========== USER DATA MANAGEMENT ==========
  Future<void> _saveUser(User user) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_userKey, json.encode(user.toJson()));
  }

  Future<User?> getSavedUser() async {
    final prefs = await SharedPreferences.getInstance();
    final userJson = prefs.getString(_userKey);
    if (userJson != null) {
      return User.fromJson(json.decode(userJson));
    }
    return null;
  }

  Future<void> clearUser() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_userKey);
  }

  // ========== LOGOUT ==========
  Future<void> logout() async {
    // Call backend logout endpoint (optional)
    try {
      final token = await getToken();
      if (token != null) {
        await _client.post(
          Uri.parse('$baseUrl/logout'),
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer $token',
          },
        ).timeout(const Duration(seconds: 5));
      }
    } catch (e) {
      // Ignore logout endpoint errors
    }

    // Clear local data
    await clearToken();
    await clearUser();
  }

  // ========== CHECK AUTHENTICATION ==========
  Future<bool> isAuthenticated() async {
    final token = await getToken();
    return token != null && token.isNotEmpty;
  }

  void dispose() {
    _client.close();
  }
}

// Custom exception for authentication errors
class AuthException implements Exception {
  final String message;
  final int statusCode;

  const AuthException(this.message, this.statusCode);

  @override
  String toString() => message;
}


