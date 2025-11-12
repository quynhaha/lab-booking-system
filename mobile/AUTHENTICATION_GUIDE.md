# 🔐 Flutter Mobile Authentication Guide

## ✅ **Đã hoàn thành**

Flutter mobile app đã được tích hợp đầy đủ hệ thống Authentication với Backend API!

---

## 📋 **Tính năng**

### 1. **Login với FPT SSO**
- Email: `@fpt.edu.vn` (bắt buộc)
- Password: Tối thiểu 6 ký tự
- JWT token tự động lưu vào SharedPreferences
- Auto-redirect sau login thành công

### 2. **Register (Đăng ký tài khoản mới)**
- Chỉ email `@fpt.edu.vn` được phép
- Thông tin: Họ tên, Email, Password, Mã SV (optional), Khoa (optional)
- Tự động login sau đăng ký thành công

### 3. **JWT Token Management**
- Token tự động gửi trong header `Authorization: Bearer <token>`
- Persist token qua SharedPreferences
- Auto-refresh khi app restart
- Token được inject vào tất cả API services

### 4. **Protected Routes**
- Login screen: `/login`
- Home (protected): `/`
- Labs (protected): `/labs`
- Events (protected): `/events`
- Bookings (protected): `/bookings`

Tất cả routes (trừ `/login`) yêu cầu authentication. Nếu chưa login → auto redirect đến `/login`.

### 5. **User Profile & Logout**
- Hiển thị thông tin user trong AppBar (avatar, tên, email, mã SV)
- Logout button trong menu dropdown
- Clear token và user data khi logout

---

## 🚀 **Cách sử dụng**

### **📱 App Mobile chỉ dành cho Students & Teachers**

**Tài khoản sinh viên test:**

```
Email: an.nguyen@fpt.edu.vn
Password: fpt123
Student ID: SE123456
```

**Tài khoản khác:**
- `binh.tran@fpt.edu.vn` / `fpt123` (Sinh viên IT)
- `cuong.le@fpt.edu.vn` / `fpt123` (Sinh viên CS)

**⚠️ Lưu ý:** Admin accounts (`admin@fpt.edu.vn`) chỉ dùng cho **React Web Admin Dashboard**, không dùng cho mobile app!

*(Đảm bảo backend đang chạy trên `http://localhost:8080` hoặc update IP trong các service files)*

---

## 📂 **File Structure**

```
mobile/lib/
├── models/
│   ├── user.dart                    # User model
│   ├── user.g.dart                  # Generated JSON serialization
│   ├── login_request.dart           # Login request DTO
│   ├── login_request.g.dart
│   ├── login_response.dart          # Login response DTO
│   └── login_response.g.dart
├── services/
│   ├── auth_service.dart            # Authentication API calls
│   ├── api_service.dart             # Updated với JWT token
│   ├── lab_service.dart             # Updated với JWT token
│   └── booking_service.dart         # Updated với JWT token
├── providers/
│   └── auth_provider.dart           # Auth state management (Provider pattern)
├── screens/
│   ├── login_screen.dart            # Login UI
│   ├── register_screen.dart         # Register UI
│   ├── home_screen.dart             # Updated với user info & logout
│   └── bookings_screen.dart         # Updated để dùng real user ID
└── main.dart                        # Updated với auth routing & token injection
```

---

## 🔧 **Backend API Endpoints được sử dụng**

### 1. **Login**
```
POST http://10.0.2.2:8080/api/auth/login/fpt-sso
Body:
{
  "email": "an.nguyen@fpt.edu.vn",
  "password": "fpt123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "loginMethod": "FPT_SSO",
  "userType": "FPT_STUDENT",
  "message": "Login successful via FPT_SSO"
}
```

### 2. **Register**
```
POST http://10.0.2.2:8080/api/auth/register
Body:
{
  "email": "student@fpt.edu.vn",
  "password": "password123",
  "fullName": "Nguyen Van A",
  "studentId": "SE123456",  // optional
  "faculty": "Software Engineering"  // optional
}
```

### 3. **Get User Info**
```
GET http://10.0.2.2:8080/api/auth/me
Headers:
  Authorization: Bearer <token>

Response:
{
  "id": 13,
  "fullName": "Nguyen Van An",
  "email": "an.nguyen@fpt.edu.vn",
  "role": "STUDENT",
  "studentId": "SE123456",
  "faculty": "Software Engineering",
  "status": true
}
```

### 4. **Logout**
```
POST http://10.0.2.2:8080/api/auth/logout
Headers:
  Authorization: Bearer <token>
```

---

## 🎯 **Cách JWT Token được inject vào các API**

### **Trước đây (Hardcoded):**
```dart
// BookingService - OLD
Future<List<Booking>> getUserBookings(int userId) async {
  final response = await _client.get(
    Uri.parse('$baseUrl/user/1'),  // ❌ Hardcoded ID
    headers: {
      'Content-Type': 'application/json',
      // ❌ Không có JWT token
    },
  );
}
```

### **Bây giờ (với JWT Token):**
```dart
// BookingService - NEW
Future<List<Booking>> getUserBookings(int userId) async {
  final response = await _client.get(
    Uri.parse('$baseUrl/user/$userId'),  // ✅ Real user ID
    headers: _getHeaders(),  // ✅ JWT token included
  );
}

Map<String, String> _getHeaders() {
  final headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };
  
  // Add JWT token if available
  if (_token != null && _token!.isNotEmpty) {
    headers['Authorization'] = 'Bearer $_token';  // ✅ JWT Token
  }
  
  return headers;
}
```

Token được inject tự động qua **ProxyProvider** trong `main.dart`:

```dart
ProxyProvider<AuthProvider, BookingService>(
  create: (_) => BookingService(),
  update: (_, auth, service) {
    service ??= BookingService();
    service.setToken(auth.token);  // ✅ Auto inject token
    return service;
  },
)
```

---

## 🧪 **Testing**

### **1. Chạy Backend:**
```bash
cd backend
docker-compose up backend db
```

### **2. Chạy Flutter App:**
```bash
cd mobile
flutter run
```

### **3. Test Flow:**
1. App khởi động → Kiểm tra token trong SharedPreferences
   - **Có token** → Auto login → Home screen
   - **Không có token** → Redirect đến Login screen

2. Login với `an.nguyen@fpt.edu.vn` / `fpt123` (hoặc nhấn "Dùng ngay")
   - Success → Save token → Navigate to Home
   - Error → Show error message

3. Home screen → Xem user info trong avatar menu
4. Navigate đến Bookings → Fetch bookings với real user ID
5. Logout → Clear token → Redirect to Login

---

## 🛠️ **Troubleshooting**

### **Backend không kết nối được:**

**Android Emulator:**
```dart
// services/auth_service.dart
static const String baseUrl = 'http://10.0.2.2:8080/api/auth';
```

**iOS Simulator:**
```dart
static const String baseUrl = 'http://localhost:8080/api/auth';
```

**Real Device (cùng WiFi):**
```dart
// Tìm IP máy tính (Windows: ipconfig, Mac: ifconfig)
static const String baseUrl = 'http://192.168.1.100:8080/api/auth';
```

### **Token expired:**
- Backend trả 401 → AuthProvider tự động logout
- User phải login lại

### **Register không được:**
- Kiểm tra email có `@fpt.edu.vn`
- Kiểm tra backend migration đã chạy chưa

---

## 📱 **Screenshots Guide**

### **1. Login Screen**
- Email field với validation `@fpt.edu.vn`
- Password field với show/hide
- Demo account info hiển thị
- Register link

### **2. Register Screen**
- Full name, Email, Password, Confirm Password
- Optional: Student ID, Faculty
- Auto-redirect sau register

### **3. Home Screen**
- Avatar trong AppBar (chữ cái đầu của tên)
- Dropdown menu: User info + Logout
- Navigation cards: Labs, Bookings, Events

### **4. Bookings Screen**
- Fetch bookings với real user ID từ token
- Filter: All, Pending, Approved, Cancelled

---

## ✅ **Checklist**

- ✅ Login với FPT SSO
- ✅ Register với @fpt.edu.vn
- ✅ JWT token storage (SharedPreferences)
- ✅ Token auto-inject vào tất cả API calls
- ✅ Protected routes với auto-redirect
- ✅ User profile display
- ✅ Logout functionality
- ✅ Real user ID trong bookings
- ✅ Error handling
- ✅ Loading states

---

## 🎉 **Kết luận**

Flutter mobile app đã được tích hợp hoàn chỉnh với Backend Authentication API!

Tất cả API calls đều tự động gửi JWT token, không còn hardcoded user ID nữa! 🚀


