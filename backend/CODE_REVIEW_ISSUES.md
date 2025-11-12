# 🐛 Code Review - Lỗi cần fix từ Backend Team

## Ngày review: 2025-10-25

---

## ❌ **LỖI 1: BookingService.java - Thiếu `.collect`**

**File**: `backend/src/main/java/com/unilab/service/BookingService.java`

**Line 53-56**:
```java
public List<BookingDto> getBookingsByUserId(Long userId) {
    return bookingRepository.findByUserId(userId).stream()
        .map(this::convertToDto)
        (Collectors.toList());  // ❌ THIẾU .collect
    }
```

**Vấn đề**: Thiếu `.collect` trước `(Collectors.toList())`

**Fix**:
```java
public List<BookingDto> getBookingsByUserId(Long userId) {
    return bookingRepository.findByUserId(userId).stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());  // ✅ THÊM .collect
}
```

**Lý do lỗi này nghiêm trọng**: 
- Code không compile được
- Đây chính là nguyên nhân lỗi 400 khi Flutter call `/api/v1/bookings/user/1`

---

## ❌ **LỖI 2: AdminDashboardController - Thiếu whitelist Security**

**File**: `backend/src/main/java/com/unilab/config/SecurityConfig.java`

**Vấn đề**: Admin dashboard endpoints bị block vì cần authentication:
```java
@RequestMapping("/api/v1/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
```

Nhưng trong SecurityConfig **CHƯA ĐƯỢC whitelist** cho development/testing.

**Hiện tượng**: 
- Admin dashboard call API → Lỗi 401 Unauthorized hoặc 403 Forbidden
- Frontend không thể test được

**Khuyến nghị**:

**Option A - Để yêu cầu authentication (Production)**: 
Không fix gì cả, để như vậy. Nhưng Flutter app phải gửi JWT token:
```dart
headers: {
  'Authorization': 'Bearer $jwtToken',
  'Content-Type': 'application/json',
}
```

**Option B - Cho phép access không cần auth (Development/Testing)**:
Thêm vào SecurityConfig:
```java
.requestMatchers(
    "/api/v1/admin/dashboard/**"  // ⚠️ CHỈ CHO DEV, XÓA KHI DEPLOY PRODUCTION
).permitAll()
```

**⚠️ LƯU Ý**: Không nên whitelist admin endpoints trong production!

---

## ⚠️ **VẤN ĐỀ 3: DashboardService - Missing Methods**

**File**: `backend/src/main/java/com/unilab/service/DashboardService.java`

**Line 80-81**: Gọi các methods không tồn tại:
```java
.limit(10)
.collect(Collectors.toList())  // ❓ Method này trả về List<Booking> hay List<BookingDto>?
```

**Vấn đề**: 
```java
bookingService.getAllBookings()  // Trả về List<BookingDto>
```
nhưng:
```java
recentActivity.setRecentBookings(...)  // Cần List<BookingDto>
```

Nếu `DashboardDto.RecentActivity.recentBookings` là `List<Booking>` thì sẽ lỗi type mismatch.

**Kiểm tra**: Xem DTO có đúng type không:
```java
// File: DashboardDto.java - cần check
public class RecentActivity {
    private List<BookingDto> recentBookings;  // ✅ Nếu là BookingDto thì OK
    // hoặc
    private List<Booking> recentBookings;  // ❌ Nếu là Booking thì SAI
}
```

---

## ⚠️ **VẤN ĐỀ 4: Recurring Booking - CHƯA THẤY CODE**

**Yêu cầu**: Backend team nói đã implement "recurring booking"

**Thực tế**: Trong code chỉ thấy:
- ✅ **Multi-lab booking**: `BookingService.createMultiLabBooking()` - OK
- ❌ **Recurring booking**: KHÔNG TÌM THẤY CODE

**Multi-lab booking** ≠ **Recurring booking**:
- **Multi-lab**: Đặt nhiều phòng cùng lúc (cùng thời gian)
- **Recurring**: Đặt lịch lặp lại theo lịch trình (daily, weekly, monthly)

**Code hiện tại chỉ có**:
```java
// BookingService.java line 127-176
public List<BookingDto> createMultiLabBooking(MultiLabBookingRequest request, Long userId) {
    // Đặt nhiều lab cùng lúc
    for (Long labId : request.getLabIds()) {
        // Create booking for each lab
    }
}
```

**THIẾU**: Recurring booking logic
```java
// VÍ DỤ CODE CẦN CÓ (CHƯA THẤY):
public List<BookingDto> createRecurringBooking(RecurringBookingRequest request, Long userId) {
    // Create bookings that repeat:
    // - Daily (every day)
    // - Weekly (every week, specific day)
    // - Monthly (specific date each month)
}
```

**Khuyến nghị**: 
1. Hỏi lại Backend team xem recurring booking đã implement chưa?
2. Nếu chưa, yêu cầu họ implement với API:
   - `POST /api/v1/bookings/recurring`
   - Request body:
```json
{
  "labId": 1,
  "title": "Weekly Lab Session",
  "startTime": "2025-10-26T09:00:00",
  "endTime": "2025-10-26T12:00:00",
  "recurrencePattern": "WEEKLY",
  "recurrenceEndDate": "2025-12-31T23:59:59",
  "daysOfWeek": ["MONDAY", "WEDNESDAY"]
}
```

---

## ✅ **ĐÚNG - Authentication Code**

**File**: `backend/src/main/java/com/unilab/api/AuthController.java`

**✅ Code trông tốt**:
- Register: OK
- Login FPT SSO: OK
- Google Login: OK
- JWT generation: OK
- Get user info (`/me`): OK

**✅ JwtService.java**: OK
- Token generation: ✅
- Token validation: ✅
- Signing key: ✅

**⚠️ Lưu ý**:
- Line 161: `googleClientId` phải được set trong `application.properties`:
```properties
app.google.clientId=YOUR_GOOGLE_CLIENT_ID_HERE
```
Nếu không set, Google login sẽ không hoạt động.

---

## ✅ **ĐÚNG - Admin Dashboard Structure**

**Controller**: AdminDashboardController.java - ✅ OK
**Service**: DashboardService.java - ✅ Logic OK (ngoại trừ vấn đề 3)

---

## 📋 **TÓM TẮT - CẦN FIX**:

| # | Vấn đề | Mức độ | File | Fix |
|---|--------|--------|------|-----|
| 1 | ❌ Thiếu `.collect` | **CRITICAL** | BookingService.java:56 | Thêm `.collect` |
| 2 | ⚠️ Admin dashboard 401/403 | **HIGH** | SecurityConfig.java | Whitelist hoặc add JWT to Flutter |
| 3 | ⚠️ Type mismatch có thể | **MEDIUM** | DashboardService.java | Check DTO types |
| 4 | ❌ Recurring booking THIẾU | **HIGH** | N/A | Implement code mới |

---

## 🔧 **HÀNH ĐỘNG**:

### 1. **Fix ngay Lỗi 1** (CRITICAL):
```bash
# Sửa BookingService.java line 56
.collect(Collectors.toList());  # Thêm .collect
```

### 2. **Kiểm tra Vấn đề 2** (Admin Dashboard):
Test API:
```bash
curl http://localhost:8080/api/v1/admin/dashboard
```

Nếu lỗi 401/403 → Cần add JWT token hoặc whitelist

### 3. **Xác nhận Vấn đề 4** (Recurring Booking):
Hỏi Backend team:
- "Recurring booking đã implement chưa?"
- "Nếu rồi, API endpoint là gì?"
- "Request/Response format như thế nào?"

---

## 📞 **GỬI CHO BACKEND TEAM**:

**Subject**: Code Review - Tìm thấy lỗi cần fix

**Nội dung**:
```
Hi Backend Team,

Đã review code các bạn gửi, tìm thấy vài vấn đề:

1. ❌ CRITICAL: BookingService.java line 56 thiếu .collect - code không compile
   → Đây là nguyên nhân lỗi 400 khi call /api/v1/bookings/user/1

2. ⚠️ Admin Dashboard bị 401/403 - cần whitelist hoặc Flutter phải gửi JWT token

3. ⚠️ Recurring Booking: Code chỉ có Multi-Lab booking, chưa thấy Recurring booking logic

Chi tiết xem file: backend/CODE_REVIEW_ISSUES.md

Thanks!
```

---

**Reviewed by**: Frontend Team  
**Date**: 2025-10-25


