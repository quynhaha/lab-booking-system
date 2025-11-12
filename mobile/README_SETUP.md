# Hướng dẫn cài đặt và chạy Flutter App

## 📋 Yêu cầu

- Flutter SDK 3.4.4 trở lên
- Dart SDK 3.4.4 trở lên
- Android Studio / VS Code
- Android Emulator hoặc thiết bị Android

## 🚀 Cài đặt

### 1. Cài đặt dependencies

```bash
cd mobile
flutter pub get
```

### 2. Generate code cho models

App sử dụng `json_serializable` để generate code JSON. Chạy lệnh sau:

```bash
flutter pub run build_runner build --delete-conflicting-outputs
```

Hoặc nếu muốn watch và auto-generate:

```bash
flutter pub run build_runner watch
```

Lệnh này sẽ tạo các file `.g.dart` cho:
- `lib/models/event.g.dart`
- `lib/models/lab.g.dart`
- `lib/models/booking.g.dart`

### 3. Cấu hình Backend URL

Mặc định app đã được config cho **Android Emulator**:
- `http://10.0.2.2:8080/api`

Nếu chạy trên platform khác, chỉnh sửa trong các file:
- `lib/services/api_service.dart`
- `lib/services/lab_service.dart`
- `lib/services/booking_service.dart`

**URL cho các platform:**
| Platform | Base URL |
|----------|----------|
| Android Emulator | `http://10.0.2.2:8080/api` |
| iOS Simulator | `http://localhost:8080/api` |
| Real Device | `http://[IP_MÁY_BẠN]:8080/api` |

## 🏃 Chạy ứng dụng

### Chạy trên Android Emulator

1. Khởi động Android Emulator
2. Chạy lệnh:

```bash
flutter run
```

### Chạy trên thiết bị thật

1. Bật USB Debugging trên điện thoại
2. Kết nối điện thoại với máy tính
3. Kiểm tra thiết bị:

```bash
flutter devices
```

4. Chạy app:

```bash
flutter run
```

### Chạy với Hot Reload

Flutter hỗ trợ hot reload, sau khi app chạy:
- Nhấn `r` để reload
- Nhấn `R` để restart
- Nhấn `q` để thoát

## 📱 Chức năng của App

### 1. Màn hình chính (Home)
- Điều hướng đến các chức năng chính
- 3 card: Danh sách phòng Lab, Lịch đã đặt, Sự kiện

### 2. Danh sách phòng Lab
- Hiển thị tất cả phòng lab
- Lọc: Tất cả / Chỉ phòng khả dụng
- Xem chi tiết phòng
- Đặt lịch phòng

### 3. Chi tiết phòng Lab
- Thông tin chi tiết: tên, mã, vị trí, sức chứa
- Trạng thái: Có sẵn / Bảo trì / Không khả dụng
- Trang thiết bị
- Nút đặt lịch

### 4. Lịch đã đặt (My Bookings)
- Hiển thị danh sách lịch đã đặt
- Lọc theo trạng thái: Tất cả / Chờ duyệt / Đã duyệt / Đã hủy
- Xem chi tiết booking
- Hủy lịch (nếu có thể)

### 5. Chi tiết Booking
- Thông tin đầy đủ về lịch đặt
- Mã booking, phòng, thời gian
- Trạng thái và lịch sử duyệt
- Thông tin người duyệt (nếu có)

### 6. Sự kiện (Events)
- Xem danh sách sự kiện
- Chi tiết sự kiện

## 🔧 Cấu trúc thư mục

```
mobile/
├── lib/
│   ├── main.dart                 # Entry point
│   ├── models/                   # Data models
│   │   ├── event.dart
│   │   ├── lab.dart
│   │   └── booking.dart
│   ├── services/                 # API services
│   │   ├── api_service.dart
│   │   ├── lab_service.dart
│   │   └── booking_service.dart
│   └── screens/                  # UI screens
│       ├── home_screen.dart
│       ├── labs_screen.dart
│       ├── lab_detail_screen.dart
│       ├── bookings_screen.dart
│       ├── booking_detail_screen.dart
│       └── events_screen.dart
├── pubspec.yaml                  # Dependencies
└── README_SETUP.md              # File này
```

## 📡 API Endpoints được sử dụng

### Labs API
- `GET /api/v1/labs` - Lấy tất cả phòng lab
- `GET /api/v1/labs/available` - Lấy phòng khả dụng
- `GET /api/v1/labs/{id}` - Chi tiết phòng
- `GET /api/v1/labs/search?keyword={keyword}` - Tìm kiếm

### Bookings API
- `GET /api/v1/bookings` - Lấy tất cả bookings
- `GET /api/v1/bookings/user/{userId}` - Lấy bookings của user
- `GET /api/v1/bookings/{id}` - Chi tiết booking
- `POST /api/v1/bookings` - Tạo booking mới
- `PUT /api/v1/bookings/cancel` - Hủy booking

### Events API
- `GET /api/events` - Lấy danh sách events
- `GET /api/events/{id}` - Chi tiết event

## ⚠️ Lưu ý

1. **Backend phải chạy trước**: Đảm bảo Spring Boot backend đang chạy ở port 8080
2. **CORS đã được config**: Backend đã cho phép tất cả origins
3. **Authentication**: Hiện tại chưa implement JWT authentication đầy đủ
4. **User ID**: Đang hardcode userId = 1 cho testing

## 🐛 Troubleshooting

### Lỗi: "Failed to load labs" hoặc "No internet connection"

**Giải pháp:**
1. Kiểm tra backend đang chạy: `curl http://localhost:8080/api/v1/labs`
2. Với Android Emulator, đảm bảo dùng `10.0.2.2` thay vì `localhost`
3. Kiểm tra firewall không block port 8080

### Lỗi: "Missing generated files"

**Giải pháp:**
```bash
flutter pub run build_runner build --delete-conflicting-outputs
```

### Lỗi build

**Giải pháp:**
```bash
flutter clean
flutter pub get
flutter pub run build_runner build --delete-conflicting-outputs
flutter run
```

## 📚 Dependencies chính

- **flutter**: Framework UI
- **provider**: State management
- **go_router**: Navigation/routing
- **http**: HTTP client
- **json_annotation**: JSON serialization
- **intl**: Date/time formatting

## 🎨 UI/UX

- Material Design 3
- Responsive layout
- Pull-to-refresh
- Loading states
- Error handling
- Empty states
- Status colors:
  - 🟢 Có sẵn / Đã duyệt
  - 🟠 Chờ duyệt / Bảo trì
  - 🔴 Từ chối / Không khả dụng
  - ⚪ Đã hủy / Hoàn thành

## 📞 Hỗ trợ

Nếu gặp vấn đề, kiểm tra:
1. Backend logs
2. Flutter logs: `flutter logs`
3. Network inspector trong Android Studio

---

**Chúc bạn code vui vẻ! 🚀**


