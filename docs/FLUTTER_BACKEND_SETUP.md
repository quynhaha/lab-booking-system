# Hướng dẫn chạy Flutter với Backend

## ✅ Đã sửa xong các vấn đề

### Các thay đổi đã thực hiện:

1. **✅ EventController** - Đã cập nhật để trả về đầy đủ 9 fields:
   - `id`, `title`, `description`, `startTime`, `endTime`, `status`, `location`, `capacity`, `bookedCount`
   - Thêm 4 mock events để test

2. **✅ SecurityConfig** - Đã cho phép truy cập không cần authentication:
   - `/api/events/**` không cần JWT token

3. **✅ CorsConfig** - Đã cho phép tất cả origins:
   - Mobile app có thể kết nối từ bất kỳ đâu

4. **✅ Flutter API Service** - Đã cập nhật base URL:
   - Android Emulator: `http://10.0.2.2:8080/api`

---

## 🚀 Cách chạy

### 1. Chạy Backend (Spring Boot)

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Hoặc dùng Docker Compose:
```bash
docker-compose up backend db
```

Backend sẽ chạy tại: `http://localhost:8080`

### 2. Kiểm tra Backend đang chạy

Mở trình duyệt và truy cập:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Test Events API**: http://localhost:8080/api/events

Bạn sẽ thấy 4 mock events:
```json
[
  {
    "id": 1,
    "title": "Java Programming Workshop",
    "description": "Learn advanced Java programming techniques...",
    "startTime": "2025-10-26T...",
    "endTime": "2025-10-26T...",
    "status": "ACTIVE",
    "location": "Computer Lab 1",
    "capacity": 30,
    "bookedCount": 15
  },
  ...
]
```

### 3. Chạy Flutter App

#### A. Trên Android Emulator:
```bash
cd mobile
flutter pub get
flutter run
```

Base URL đã được set: `http://10.0.2.2:8080/api` ✅

#### B. Trên iOS Simulator:
Sửa file `mobile/lib/services/api_service.dart`:
```dart
static const String baseUrl = 'http://localhost:8080/api';
```

Sau đó chạy:
```bash
flutter run
```

#### C. Trên thiết bị thật (Real Device):
1. Tìm IP máy tính của bạn:
   - Windows: `ipconfig` 
   - Mac/Linux: `ifconfig` hoặc `ip addr`
   
2. Sửa file `mobile/lib/services/api_service.dart`:
```dart
static const String baseUrl = 'http://192.168.1.100:8080/api'; // Thay bằng IP của bạn
```

3. Đảm bảo điện thoại và máy tính cùng mạng WiFi

4. Chạy:
```bash
flutter run
```

---

## 🧪 Test

### Test từ Terminal:

```bash
# Test GET events
curl http://localhost:8080/api/events

# Test GET event by ID
curl http://localhost:8080/api/events/1
```

### Test từ Flutter App:

1. Mở app trên emulator/device
2. Vào màn hình "Events" 
3. Sẽ thấy danh sách 4 events
4. Có thể xem chi tiết từng event

---

## 📝 Lưu ý quan trọng

### URL cho từng platform:

| Platform | Base URL | Lý do |
|----------|----------|-------|
| Android Emulator | `http://10.0.2.2:8080/api` | `10.0.2.2` = localhost của máy host |
| iOS Simulator | `http://localhost:8080/api` | iOS simulator share network với Mac |
| Real Device | `http://[IP_MÁY_BẠN]:8080/api` | Phải cùng mạng WiFi |
| Web (Flutter Web) | `http://localhost:8080/api` | Chạy trên browser |

### Requirements:

- **Backend**: Java 17, Maven, PostgreSQL
- **Flutter**: Dart SDK 3.4.4+, Flutter SDK
- **Database**: PostgreSQL 16 (hoặc dùng Docker)

---

## ⚠️ Troubleshooting

### Lỗi: "No internet connection" hoặc "Failed to load events"

**Nguyên nhân**: Flutter không kết nối được backend

**Giải pháp**:
1. Kiểm tra backend đang chạy: mở http://localhost:8080/api/events
2. Kiểm tra base URL trong `api_service.dart`
3. Với Android Emulator: phải dùng `10.0.2.2`, không phải `localhost`
4. Với Real Device: kiểm tra firewall, cùng WiFi network

### Lỗi: "Failed to load events: 401"

**Nguyên nhân**: Endpoint cần authentication

**Giải pháp**:
Kiểm tra `SecurityConfig.java` có dòng này:
```java
"/api/events/**"
```

### Lỗi: CORS blocked

**Nguyên nhân**: Backend chặn request từ mobile

**Giải pháp**:
Kiểm tra `CorsConfig.java`:
```java
config.setAllowedOriginPatterns(List.of("*"));
```

---

## 🎉 Kết quả

Nếu setup đúng, bạn sẽ thấy:
- ✅ Backend API trả về 4 events
- ✅ Flutter app hiển thị danh sách events
- ✅ Có thể xem chi tiết từng event
- ✅ Pull-to-refresh hoạt động
- ✅ Không có lỗi CORS hoặc authentication

**Happy coding! 🚀**


