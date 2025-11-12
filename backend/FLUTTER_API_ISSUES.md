# 🐛 Vấn đề Flutter App kết nối Backend

## Ngày: 2025-10-25
## Cập nhật: Test API xong - Tìm ra nguyên nhân

## Báo cáo từ Frontend Team:

### 1. ✅ **Sự kiện (Events)** - HOẠT ĐỘNG
- `/api/events` → Trả về 4 mock events
- UI có lỗi overflow text nhỏ (đã sửa ở Flutter)

### 2. ❌ **Danh sách phòng Lab** - KHÔNG CÓ DỮ LIỆU
- **Endpoint**: `GET /api/v1/labs`
- **Response**: `[]` (empty array)
- **✅ ĐÃ XÁC NHẬN**: Database rỗng, không có data labs
- **Nguyên nhân**: 
  - Migration V5 chưa chạy hoặc bị fail
  - Labs bị xóa do `spring.jpa.hibernate.ddl-auto=create-drop`

### 3. ❌ **Lịch đã đặt (Bookings)** - LỖI 400
- **Endpoint**: `GET /api/v1/bookings/user/1`
- **Response**: 
```json
{
  "status": 400,
  "message": "Name for argument of type [java.lang.Long] not specified, and parameter name information not available via reflection. Ensure that the compiler uses the '-parameters' flag."
}
```
- **✅ ĐÃ TÌM RA NGUYÊN NHÂN**: Maven compiler thiếu flag `-parameters`
- **Controller code**:
```java
@GetMapping("/user/{userId}")
public ResponseEntity<List<BookingDto>> getBookingsByUserId(@PathVariable Long userId) {
    // Spring không bind được userId vì thiếu parameter names
}
```

---

## 🔍 Yêu cầu Backend Team kiểm tra:

### A. Kiểm tra Database có dữ liệu không:

```sql
-- 1. Check labs table
SELECT * FROM labs;
-- Expected: 5 labs (Computer Lab 1, Computer Lab 2, Network Lab, Hardware Lab, Multimedia Lab)

-- 2. Check users table  
SELECT * FROM users WHERE user_id = 1;
-- Expected: Có ít nhất 1 user

-- 3. Check bookings table
SELECT * FROM bookings WHERE user_id = 1;
-- Expected: Có thể rỗng (OK), nhưng không được lỗi
```

### B. Kiểm tra Backend Logs:

Khi Flutter app gọi API, xem logs có lỗi gì:

```bash
# Xem logs của Docker container
docker logs swd392_group4-backend-1 --tail 50 -f
```

Tìm:
- ❌ **Exception**: `RuntimeException`, `NullPointerException`, etc.
- ❌ **SQL Error**: Database query failed
- ❌ **400 Bad Request** với stack trace

### C. Test trực tiếp từ Browser/Postman:

```bash
# 1. Test Labs API
curl http://localhost:8080/api/v1/labs

# Expected Response:
# [
#   {
#     "id": 1,
#     "labCode": "LAB001",
#     "name": "Computer Lab 1",
#     "location": "Building A, Room 101",
#     "capacity": 30,
#     "status": "AVAILABLE",
#     ...
#   },
#   ...
# ]

# 2. Test Bookings API
curl http://localhost:8080/api/v1/bookings/user/1

# Expected Response (nếu chưa có bookings):
# []

# Nếu lỗi 400, sẽ thấy:
# {
#   "status": 400,
#   "error": "Bad Request",
#   "message": "..."
# }
```

---

## 🔧 Giải pháp CẦN FIX NGAY:

### ✅ FIX 1: Maven Compiler thiếu `-parameters` flag (LỖI BOOKINGS 400)

**File**: `backend/pom.xml`

**Thêm `<parameters>true</parameters>` vào maven-compiler-plugin:**

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-compiler-plugin</artifactId>
  <version>3.13.0</version>
  <configuration>
    <source>${java.version}</source>
    <target>${java.version}</target>
    <parameters>true</parameters>  <!-- THÊM DÒNG NÀY -->
  </configuration>
</plugin>
```

**Sau đó rebuild:**
```bash
docker-compose down
docker-compose up --build backend db
```

### ✅ FIX 2: Database Labs rỗng (LỖI LABS EMPTY)

**Nguyên nhân**: `spring.jpa.hibernate.ddl-auto=create-drop` xóa data mỗi lần restart

**Fix Option A** - Đổi sang `update`:
```properties
# backend/src/main/resources/application.properties
spring.jpa.hibernate.ddl-auto=update
# Thay vì create-drop
```

**Fix Option B** - Re-run migrations (khuyến nghị):
```bash
docker-compose down -v  # Xóa volumes và data
docker-compose up backend db  # Chạy lại từ đầu, migrations sẽ re-run
```

**Kiểm tra lại:**
```bash
# Vào database container
docker exec -it swd392_group4-db-1 psql -U lab_user -d lab_booking

# Chạy query
SELECT COUNT(*) FROM labs;
# Expected: 5

SELECT * FROM labs;
# Expected: 5 labs (Computer Lab 1, Computer Lab 2, Network Lab, Hardware Lab, Multimedia Lab)
```

### ❌ FIX 3: KHÔNG CẦN (Lỗi 400 là do compiler flag)

~~**Tạm thời fix** trong `BookingService.java`:~~

```java
// Line 53-57
public List<BookingDto> getBookingsByUserId(Long userId) {
    // Check if user exists first
    if (!userRepository.existsById(userId)) {
        return new ArrayList<>();  // Return empty list instead of throwing error
    }
    
    return bookingRepository.findByUserId(userId).stream()
        .map(this::convertToDto)
        .collect(Collectors.toList());
}
```

### 3. **Nếu vẫn lỗi** → Check Model mapping

Có thể là mismatch giữa:
- Database column names (snake_case)
- Java field names (camelCase)

Kiểm tra `Lab` entity và `LabDto` mapping.

---

## 📋 Checklist Backend:

- [ ] Database có chạy không? (`docker ps`)
- [ ] Migrations đã apply? (Check flyway_schema_history table)
- [ ] Labs table có 5 records?
- [ ] Users table có ít nhất 1 user?
- [ ] Test `/api/v1/labs` từ browser → trả về data?
- [ ] Test `/api/v1/bookings/user/1` → trả về `[]` hoặc data?
- [ ] Backend logs không có Exception?

---

## 🔗 API Endpoints cần test:

| Endpoint | Method | Expected | Flutter sử dụng |
|----------|--------|----------|-----------------|
| `/api/events` | GET | 4 events | ✅ OK |
| `/api/v1/labs` | GET | 5 labs | ❌ Empty |
| `/api/v1/labs/available` | GET | Available labs | Chưa test |
| `/api/v1/bookings/user/{userId}` | GET | Bookings array | ❌ Error 400 |

---

## 📝 Response Format Flutter cần:

### Labs:
```json
[
  {
    "id": 1,
    "labCode": "LAB001",
    "name": "Computer Lab 1",
    "description": "...",
    "location": "Building A, Room 101",
    "capacity": 30,
    "status": "AVAILABLE",
    "facilities": "...",
    "createdAt": "2025-10-25T...",
    "updatedAt": "2025-10-25T..."
  }
]
```

### Bookings:
```json
[
  {
    "id": 1,
    "bookingCode": "BK-12345678",
    "userId": 1,
    "userEmail": "user@example.com",
    "userName": "John Doe",
    "labId": 1,
    "labName": "Computer Lab 1",
    "labCode": "LAB001",
    "title": "Lab Session",
    "description": "...",
    "startTime": "2025-10-26T09:00:00",
    "endTime": "2025-10-26T12:00:00",
    "status": "PENDING",
    "participantsCount": 10,
    "createdAt": "2025-10-25T...",
    "updatedAt": "2025-10-25T..."
  }
]
```

---

## 🆘 Liên hệ:

Nếu cần thêm thông tin hoặc logs từ Flutter app, báo Frontend Team.

**Flutter Team đã:**
- ✅ Sửa CORS backend
- ✅ Sửa Security whitelist `/api/v1/labs/**` và `/api/v1/bookings/**`
- ✅ Fix UI overflow
- ✅ Test connection thành công với `/api/events`

**Backend Team cần:**
- ❓ Kiểm tra database có data
- ❓ Kiểm tra logs khi call API
- ❓ Fix lỗi 400 hoặc seed data

---

**Cảm ơn! 🙏**

