import 'package:flutter/foundation.dart';
import 'package:google_sign_in/google_sign_in.dart';
import '../models/user.dart';
import '../services/auth_service.dart';

class AuthProvider with ChangeNotifier {
  final AuthService _authService = AuthService();
  final GoogleSignIn _googleSignIn = GoogleSignIn(
    scopes: ['email', 'profile'],
    // Web Client ID from Google Cloud Console (OAuth 2.0 Client ID - Web application)
    serverClientId: '454718520252-ld1bai5jfp7497qp9qosjhjv25glbb3l.apps.googleusercontent.com',
  );
  
  User? _currentUser;
  String? _token;
  bool _isLoading = false;
  String? _error;
  bool _isInitialized = false;

  // Getters
  User? get currentUser => _currentUser;
  String? get token => _token;
  bool get isLoading => _isLoading;
  String? get error => _error;
  bool get isAuthenticated => _currentUser != null && _token != null;
  bool get isInitialized => _isInitialized;

  // Initialize - Load saved token and user from SharedPreferences
  Future<void> initialize() async {
    if (_isInitialized) return;
    
    _isLoading = true;
    notifyListeners();

    try {
      _token = await _authService.getToken();
      _currentUser = await _authService.getSavedUser();
      
      // If we have a token but no user, try to fetch user info
      if (_token != null && _currentUser == null) {
        try {
          _currentUser = await _authService.fetchAndSaveUserInfo(_token!);
        } catch (e) {
          // Token might be expired, clear it
          await _authService.clearToken();
          _token = null;
        }
      }
      
      _isInitialized = true;
    } catch (e) {
      _error = 'Lỗi khởi tạo: $e';
      debugPrint('Initialize error: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Login with Google
  Future<bool> loginWithGoogle() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      // Sign in with Google
      final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
      
      if (googleUser == null) {
        // User cancelled the sign-in
        _isLoading = false;
        notifyListeners();
        return false;
      }

      // Get authentication details
      final GoogleSignInAuthentication googleAuth = await googleUser.authentication;
      
      if (googleAuth.idToken == null) {
        _error = 'Không thể lấy Google token. Vui lòng kiểm tra cấu hình Google Sign-In.';
        _isLoading = false;
        notifyListeners();
        return false;
      }

      // Send idToken to backend
      final response = await _authService.loginWithGoogle(
        idToken: googleAuth.idToken!,
      );

      _token = response.token;
      _currentUser = await _authService.getSavedUser();
      
      _isLoading = false;
      notifyListeners();
      return true;
    } on AuthException catch (e) {
      _error = e.message;
      _isLoading = false;
      notifyListeners();
      return false;
    } catch (e) {
      // Handle PlatformException for Android
      String errorMessage = 'Lỗi đăng nhập Google';
      if (e.toString().contains('ApiException: 10')) {
        errorMessage = 'Lỗi cấu hình Google Sign-In. Vui lòng:\n1. Chạy app trên web (flutter run -d chrome)\n2. Hoặc cấu hình SHA-1 fingerprint trong Google Cloud Console';
      } else if (e.toString().contains('sign_in_failed')) {
        errorMessage = 'Không thể đăng nhập Google. Vui lòng thử lại hoặc dùng đăng nhập FPT SSO.';
      } else {
        errorMessage = 'Lỗi đăng nhập Google: $e';
      }
      
      _error = errorMessage;
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  // Login with FPT SSO
  Future<bool> loginWithFptSso({
    required String email,
    required String password,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await _authService.loginWithFptSso(
        email: email,
        password: password,
      );

      _token = response.token;
      _currentUser = await _authService.getSavedUser();
      
      _isLoading = false;
      notifyListeners();
      return true;
    } on AuthException catch (e) {
      _error = e.message;
      _isLoading = false;
      notifyListeners();
      return false;
    } catch (e) {
      _error = 'Lỗi đăng nhập: $e';
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  // Register
  Future<bool> register({
    required String email,
    required String password,
    required String fullName,
    String? studentId,
    String? faculty,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final response = await _authService.register(
        email: email,
        password: password,
        fullName: fullName,
        studentId: studentId,
        faculty: faculty,
      );

      _token = response.token;
      _currentUser = await _authService.getSavedUser();
      
      _isLoading = false;
      notifyListeners();
      return true;
    } on AuthException catch (e) {
      _error = e.message;
      _isLoading = false;
      notifyListeners();
      return false;
    } catch (e) {
      _error = 'Lỗi đăng ký: $e';
      _isLoading = false;
      notifyListeners();
      return false;
    }
  }

  // Logout
  Future<void> logout() async {
    _isLoading = true;
    notifyListeners();

    try {
      // Sign out from Google
      await _googleSignIn.signOut();
      
      // Logout from backend
      await _authService.logout();
      _token = null;
      _currentUser = null;
      _error = null;
    } catch (e) {
      _error = 'Lỗi đăng xuất: $e';
      debugPrint('Logout error: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  // Refresh user info
  Future<void> refreshUser() async {
    if (_token == null) return;

    try {
      _currentUser = await _authService.fetchAndSaveUserInfo(_token!);
      notifyListeners();
    } catch (e) {
      debugPrint('Refresh user error: $e');
      // Token might be expired
      await logout();
    }
  }

  // Clear error
  void clearError() {
    _error = null;
    notifyListeners();
  }

  @override
  void dispose() {
    _authService.dispose();
    super.dispose();
  }
}


