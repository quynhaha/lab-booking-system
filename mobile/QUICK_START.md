# 🚀 Quick Start - Chạy App trên Android Emulator

## Bước 1: Khởi động Backend

```bash
cd backend
mvn spring-boot:run
```

Đợi đến khi thấy: `Started LabBookingApiApplication`

## Bước 2: Mở Android Studio

1. Mở Android Studio
2. Chọn **More Actions** → **Virtual Device Manager**
3. Chọn một emulator và nhấn ▶️ (Play) để khởi động

## Bước 3: Chạy Flutter App

### Cách 1: Từ Android Studio

1. Mở project `mobile` trong Android Studio
2. Chọn emulator từ dropdown trên toolbar
3. Nhấn Run (Shift+F10) hoặc nút ▶️

### Cách 2: Từ Terminal

```bash
cd mobile
flutter run
```

Hoặc chọn device cụ thể:

```bash
flutter devices
flutter run -d <device-id>
```

## Bước 4: Xem kết quả

App sẽ hiển thị màn hình chính với 3 options:
- 📱 **Danh sách phòng Lab** - Xem và đặt phòng
- 📅 **Lịch đã đặt** - Quản lý bookings
- 🎉 **Sự kiện** - Xem events

## ⚡ Hot Reload

Khi app đang chạy, bạn có thể:
- **Sửa code** → Lưu file → App tự động reload
- Nhấn `r` trong terminal để reload
- Nhấn `R` để restart hoàn toàn

## 📱 Test các chức năng

### 1. Xem danh sách phòng Lab
- Tap "Danh sách phòng Lab"
- Scroll để xem các phòng
- Filter: Menu ⋮ → Chọn "Chỉ phòng khả dụng"
- Tap vào card để xem chi tiết

### 2. Chi tiết phòng
- Xem thông tin: Tên, mã, vị trí, sức chứa
- Trạng thái: 🟢 Có sẵn / 🟠 Bảo trì / 🔴 Không khả dụng
- Nút "Đặt lịch phòng này"

### 3. Xem lịch đã đặt
- Tap "Lịch đã đặt"
- Filter theo trạng thái: Menu ⋮ → Chọn filter
- Tap vào booking để xem chi tiết
- Nút "Hủy lịch" (nếu có thể)

## ⚠️ Lưu ý

### Backend URL
App đã config sẵn cho Android Emulator:
- `http://10.0.2.2:8080/api`

### Test với dữ liệu mock
Backend có sẵn 4 mock events tại `/api/events`:
- Java Programming Workshop
- Database Design Seminar
- Mobile Development Class
- Network Security Lab

## 🐛 Troubleshooting

### App không kết nối được Backend?

**Kiểm tra:**
1. Backend đang chạy? → Mở trình duyệt: http://localhost:8080/api/events
2. Emulator đang chạy? → `flutter devices`
3. URL đúng? → File `lib/services/lab_service.dart` phải dùng `10.0.2.2`

### App bị crash hoặc lỗi?

```bash
# Xem logs
flutter logs

# Hoặc trong Android Studio
# View → Tool Windows → Run
```

### Rebuild app

```bash
flutter clean
flutter pub get
flutter run
```

## 🎯 Tính năng đã implement

✅ Xem danh sách phòng Lab  
✅ Chi tiết phòng Lab  
✅ Lọc phòng theo trạng thái  
✅ Xem lịch đã đặt  
✅ Chi tiết booking  
✅ Lọc booking theo trạng thái  
✅ Hủy lịch  
✅ Pull-to-refresh  
✅ Loading states  
✅ Error handling  
✅ Empty states  

## 📚 API được sử dụng

- `GET /api/v1/labs` - Danh sách phòng
- `GET /api/v1/labs/available` - Phòng khả dụng
- `GET /api/v1/labs/{id}` - Chi tiết phòng
- `GET /api/v1/bookings/user/{userId}` - Lịch của user
- `GET /api/v1/bookings/{id}` - Chi tiết booking
- `PUT /api/v1/bookings/cancel` - Hủy lịch
- `GET /api/events` - Danh sách events

---

**Chúc bạn test thành công! 🎉**

Nếu gặp vấn đề, xem file `README_SETUP.md` để biết thêm chi tiết.


