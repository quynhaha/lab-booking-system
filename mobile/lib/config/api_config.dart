class ApiConfig {
  // ============================================
  // CẤU HÌNH API URL
  // ============================================
  
  // QUAN TRỌNG: Thay đổi IP này theo máy tính của bạn
  // Cách tìm IP: Chạy "ipconfig" trong Command Prompt
  // Tìm "IPv4 Address" trong phần "Wireless LAN adapter Wi-Fi"
  
  // ⚠️ THAY ĐỔI IP NÀY THEO MÁY TÍNH CỦA BẠN
  static const String _localIP = '192.168.109.157';  // <-- IP máy tính của bạn
  
  // Các môi trường khác nhau:
  static const String _emulatorIP = '10.0.2.2';     // Android Emulator
  static const String _localhostIP = 'localhost';   // iOS Simulator
  
  // Chọn môi trường (uncomment dòng phù hợp):
  static const bool _useEmulator = false;    // Android Emulator
  static const bool _useRealDevice = true;   // Điện thoại thật
  // static const bool _useSimulator = false; // iOS Simulator
  
  // Tự động chọn IP dựa trên môi trường
  static String get baseIP {
    if (_useEmulator) return _emulatorIP;
    if (_useRealDevice) return _localIP;
    return _localhostIP;
  }
  
  // Base URL hoàn chỉnh
  static const String _port = '8080';
  static String get baseUrl => 'http://$baseIP:$_port/api';
  
  // URLs cho từng service
  static String get authUrl => '$baseUrl/auth';
  static String get labsUrl => '$baseUrl/v1/labs';
  static String get bookingsUrl => '$baseUrl/v1/bookings';
  static String get eventsUrl => '$baseUrl/events';
  
  // Timeout
  static const Duration timeout = Duration(seconds: 30);
  
  // Log thông tin (để debug)
  static void printConfig() {
    print('==================================');
    print('API Configuration');
    print('==================================');
    print('Environment: ${_useEmulator ? "Emulator" : _useRealDevice ? "Real Device" : "Simulator"}');
    print('Base IP: $baseIP');
    print('Base URL: $baseUrl');
    print('==================================');
  }
}

