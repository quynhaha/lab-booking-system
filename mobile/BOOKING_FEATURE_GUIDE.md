# 📅 Hướng dẫn Chức năng Đặt lịch Phòng Lab

## ✅ Đã hoàn thành

Đã triển khai **hoàn chỉnh** chức năng đặt lịch phòng Lab cho Flutter Mobile App!

---

## 🎯 Các thành phần đã tạo

### 1. **Model mới: `LabBookingSlot`**
- File: `mobile/lib/models/lab_booking_slot.dart`
- Chứa thông tin:
  - `labId`: ID của phòng lab
  - `title`: Tiêu đề buổi học (bắt buộc)
  - `description`: Mô tả chi tiết (không bắt buộc)
  - `participantsCount`: Số lượng sinh viên
  - `startTime`: Thời gian bắt đầu (ISO 8601)
  - `endTime`: Thời gian kết thúc (ISO 8601)
- JSON serialization đã được tạo tự động bằng `build_runner`

### 2. **Service cập nhật: `BookingService`**
- File: `mobile/lib/services/booking_service.dart`
- Thêm method: `createBooking(List<LabBookingSlot> labSlots, {int? categoryId})`
- Gọi API: `POST /api/v1/bookings`
- Request body format:
  ```json
  {
    "labSlots": [
      {
        "labId": 1,
        "title": "Thực hành Java Programming",
        "description": "Mô tả...",
        "participantsCount": 30,
        "startTime": "2025-10-28T08:00:00.000",
        "endTime": "2025-10-28T10:00:00.000"
      }
    ],
    "categoryId": null
  }
  ```
- Response: Danh sách `Booking` đã được tạo

### 3. **Màn hình mới: `BookingCreateScreen`**
- File: `mobile/lib/screens/booking_create_screen.dart`
- Giao diện đẹp với Material Design 3
- Chức năng:
  ✅ Hiển thị thông tin phòng Lab (tên, vị trí, sức chứa)
  ✅ Form nhập liệu với validation:
    - Tiêu đề buổi học (bắt buộc, ít nhất 3 ký tự, tối đa 100 ký tự)
    - Mô tả (không bắt buộc, tối đa 500 ký tự)
    - Số lượng sinh viên (bắt buộc, phải > 0 và <= sức chứa phòng)
  ✅ Date Picker: Chọn ngày (từ hôm nay trở đi, tối đa 365 ngày)
  ✅ Time Picker: Chọn giờ bắt đầu và giờ kết thúc
  ✅ Validation logic:
    - Giờ kết thúc phải sau giờ bắt đầu
    - Số sinh viên không vượt quá sức chứa
  ✅ Loading state khi đang submit
  ✅ Thông báo lỗi chi tiết nếu có
  ✅ Quay về màn hình trước sau khi đặt lịch thành công

### 4. **Cập nhật: `LabDetailScreen`**
- File: `mobile/lib/screens/lab_detail_screen.dart`
- Nút "Đặt lịch phòng này" giờ đã hoạt động!
- Khi nhấn nút:
  1. Mở màn hình `BookingCreateScreen`
  2. Sau khi đặt lịch thành công, hiển thị thông báo
  3. Hướng dẫn người dùng kiểm tra trong "Lịch đã đặt"

---

## 📱 Cách sử dụng

### Bước 1: Hot Restart App
Vì có thay đổi lớn (thêm file mới), bạn cần **hot restart** app:

```bash
# Trong terminal đang chạy flutter run, nhấn:
R  # (chữ R hoa)
```

Hoặc dừng app và chạy lại:
```bash
cd D:\swdl\SWD392_Group4\mobile
flutter run
```

### Bước 2: Đăng nhập
- Mở app
- Đăng nhập với tài khoản student:
  - Email: `an.nguyen@fpt.edu.vn`
  - Password: `fpt123`

### Bước 3: Đặt lịch
1. Từ Home Screen, chọn **"Danh sách phòng Lab"**
2. Chọn một phòng lab có status = **"Có sẵn"** (màu xanh)
3. Trong màn hình chi tiết, xem thông tin phòng
4. Nhấn nút **"Đặt lịch phòng này"** (màu primary, có icon book_online)
5. Điền form:
   - **Tiêu đề**: VD: "Thực hành Lập trình Java"
   - **Mô tả**: VD: "Thực hành OOP, kế thừa, đa hình" (không bắt buộc)
   - **Số lượng sinh viên**: VD: "30" (phải <= sức chứa phòng)
   - **Ngày**: Chọn ngày trong tương lai
   - **Giờ bắt đầu**: VD: 08:00
   - **Giờ kết thúc**: VD: 10:00
6. Nhấn **"Xác nhận đặt lịch"**
7. Nếu thành công:
   - Hiển thị thông báo xanh "Đặt lịch thành công!"
   - Quay về màn hình Lab Detail
   - Hiển thị thông báo "Đã đặt lịch thành công! Kiểm tra trong mục 'Lịch đã đặt'"

### Bước 4: Kiểm tra booking
1. Quay về Home Screen
2. Chọn **"Lịch đã đặt"**
3. Xem booking mới vừa tạo trong danh sách

---

## 🔍 Validation Rules

### Tiêu đề (Title)
- ✅ Bắt buộc
- ✅ Tối thiểu: 3 ký tự
- ✅ Tối đa: 100 ký tự

### Mô tả (Description)
- ⚪ Không bắt buộc
- ✅ Tối đa: 500 ký tự

### Số lượng sinh viên (Participants Count)
- ✅ Bắt buộc
- ✅ Phải là số nguyên dương (> 0)
- ✅ Không được vượt quá sức chứa của phòng lab

### Ngày (Date)
- ✅ Bắt buộc
- ✅ Phải là ngày hôm nay hoặc trong tương lai
- ✅ Tối đa 365 ngày kể từ hôm nay

### Giờ (Time)
- ✅ Bắt buộc cả giờ bắt đầu và giờ kết thúc
- ✅ Giờ kết thúc phải sau giờ bắt đầu
- ✅ Format: HH:MM (24 giờ)

---

## 🎨 UI/UX Features

- ✅ **Lab Info Card**: Hiển thị rõ ràng thông tin phòng đang đặt
- ✅ **Material Design 3**: Giao diện hiện đại, đẹp mắt
- ✅ **Vietnamese Localization**: Tất cả text đều tiếng Việt
- ✅ **Form Validation**: Kiểm tra input real-time
- ✅ **Loading State**: Hiển thị spinner khi đang submit
- ✅ **Error Handling**: Thông báo lỗi chi tiết khi có vấn đề
- ✅ **Success Feedback**: Thông báo màu xanh khi thành công
- ✅ **Date/Time Pickers**: Native Android date/time pickers
- ✅ **Responsive Layout**: Tự động điều chỉnh theo màn hình

---

## 🐛 Troubleshooting

### 1. Lỗi "No internet connection"
- Kiểm tra backend có đang chạy không: `docker-compose ps`
- Nếu không, chạy: `docker-compose up backend db`

### 2. Lỗi 401 Unauthorized
- Token JWT đã hết hạn
- Đăng xuất và đăng nhập lại

### 3. Lỗi 400 Bad Request
- Kiểm tra format của startTime và endTime (phải là ISO 8601)
- Kiểm tra labId có tồn tại trong database không

### 4. Lỗi "Failed to create booking: 500"
- Kiểm tra backend logs: `docker-compose logs backend`
- Có thể là lỗi database hoặc business logic ở backend

### 5. Lỗi "type 'Null' is not a subtype of type 'X'"
- Chạy `flutter clean` và `flutter pub get`
- Chạy lại build_runner:
  ```bash
  flutter pub run build_runner build --delete-conflicting-outputs
  ```

---

## 📡 Backend API

### Endpoint
```
POST http://10.0.2.2:8080/api/v1/bookings
```

### Request Headers
```
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>
```

### Request Body
```json
{
  "labSlots": [
    {
      "labId": 1,
      "title": "Thực hành Java Programming",
      "description": "Thực hành OOP, kế thừa, đa hình",
      "participantsCount": 30,
      "startTime": "2025-10-28T08:00:00.000",
      "endTime": "2025-10-28T10:00:00.000"
    }
  ],
  "categoryId": null
}
```

### Response (Success - 200/201)
```json
[
  {
    "id": 123,
    "bookingCode": "BK-20251028-001",
    "labId": 1,
    "userId": 2,
    "title": "Thực hành Java Programming",
    "description": "Thực hành OOP, kế thừa, đa hình",
    "participantsCount": 30,
    "startTime": "2025-10-28T08:00:00",
    "endTime": "2025-10-28T10:00:00",
    "status": "PENDING",
    "createdAt": "2025-10-27T08:00:00"
  }
]
```

### Response (Error - 4xx/5xx)
```json
{
  "message": "Error message",
  "statusCode": 400
}
```

---

## ✅ Testing Checklist

- [ ] Hot restart app thành công
- [ ] Đăng nhập với tài khoản student thành công
- [ ] Vào "Danh sách phòng Lab" và chọn 1 phòng
- [ ] Nhấn "Đặt lịch phòng này" mở được màn hình mới
- [ ] Form hiển thị đầy đủ: tiêu đề, mô tả, số SV, ngày, giờ
- [ ] Validation hoạt động: submit form rỗng hiển thị lỗi
- [ ] Date picker mở được và chọn ngày được
- [ ] Time picker mở được và chọn giờ được
- [ ] Submit form đầy đủ và hợp lệ
- [ ] Loading spinner hiển thị trong lúc submit
- [ ] Thông báo "Đặt lịch thành công!" màu xanh hiển thị
- [ ] Quay về màn hình Lab Detail
- [ ] Vào "Lịch đã đặt" và thấy booking mới

---

## 🚀 Next Steps (Optional)

Nếu muốn cải thiện thêm:

1. **Recurring Booking**: Đặt lịch định kỳ (hàng tuần)
2. **Calendar View**: Xem lịch dưới dạng calendar
3. **Conflict Detection**: Kiểm tra trùng lịch trước khi submit
4. **Multi-Lab Booking**: Đặt nhiều phòng cùng lúc
5. **Edit/Reschedule**: Sửa hoặc dời lịch đã đặt
6. **Equipment Request**: Yêu cầu thiết bị kèm theo

---

## 📞 Support

Nếu gặp vấn đề, hãy kiểm tra:
1. Backend đang chạy: `docker-compose ps`
2. Backend logs: `docker-compose logs backend`
3. Flutter logs trong terminal
4. Lỗi linter: `flutter analyze`

---

**Chúc bạn đặt lịch thành công! 🎉**






