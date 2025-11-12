# 🚨 CRITICAL BUG: Schema Validation Error - Type Mismatch

## ❌ **Vấn đề:**

Backend không start được sau khi fix `ddl-auto=validate`, lỗi:

```
Schema-validation: wrong column type encountered in column [event_id] in table [events]; 
found [serial (Types#INTEGER)], but expecting [bigint (Types#BIGINT)]
```

**Root cause:** Mismatch giữa Flyway migrations và JPA Entity definitions!

---

## 🔍 **Chi tiết:**

### **1. Flyway Migrations dùng `SERIAL` (INTEGER):**

```sql
-- File: V5__complete_setup_with_test_data.sql (và các migrations khác)
CREATE TABLE events (
    event_id SERIAL PRIMARY KEY,  -- ← SERIAL = INTEGER (32-bit)
    ...
);
```

### **2. JPA Entity dùng `@GeneratedValue` → BIGINT:**

```java
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;  // ← Long = BIGINT (64-bit)
    ...
}
```

### **3. PostgreSQL SERIAL vs BIGSERIAL:**

| Type | PostgreSQL | Java | Bytes | Range |
|------|-----------|------|-------|-------|
| `SERIAL` | `INTEGER` | `Integer` | 4 | -2.1B to 2.1B |
| `BIGSERIAL` | `BIGINT` | `Long` | 8 | -9.2E18 to 9.2E18 |

**Khi dùng `ddl-auto=validate`:**
- Hibernate kiểm tra schema database có khớp với JPA entities không
- Phát hiện `SERIAL` (INTEGER) ≠ `BIGINT` → **CRASH!**

---

## ✅ **Giải pháp:**

### **Option 1: Sửa Flyway Migrations (KHUYẾN NGHỊ)**

Đổi `SERIAL` → `BIGSERIAL` trong TẤT CẢ migrations:

**File cần sửa:**
- `V1__initial_schema.sql` (nếu có)
- `V5__complete_setup_with_test_data.sql`
- Tất cả migration files có `SERIAL PRIMARY KEY`

**Thay đổi:**
```sql
-- TRƯỚC:
CREATE TABLE events (
    event_id SERIAL PRIMARY KEY,  -- ❌
    ...
);

-- SAU:
CREATE TABLE events (
    event_id BIGSERIAL PRIMARY KEY,  -- ✅
    ...
);
```

**Áp dụng cho tất cả tables:**
- `events` → `event_id BIGSERIAL`
- `bookings` → `booking_id BIGSERIAL`
- `labs` → `lab_id BIGSERIAL`
- `users` → `user_id BIGSERIAL`
- `roles` → `role_id BIGSERIAL`
- `equipment` → `equipment_id BIGSERIAL`
- `penalty_history` → `penalty_id BIGSERIAL`
- `recurring_schedules` → `schedule_id BIGSERIAL`

### **Option 2: Sửa JPA Entities (KHÔNG KHUYẾN NGHỊ)**

Đổi `Long` → `Integer` trong entities:

```java
// TRƯỚC:
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long eventId;  // ❌

// SAU:
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer eventId;  // ✅ (nhưng giới hạn 2.1 billion records)
```

**Lý do không khuyến nghị:**
- `Integer` chỉ chứa tối đa ~2.1 tỷ records
- `Long` an toàn hơn cho tương lai

### **Option 3: Tạm thời dùng `ddl-auto=none` (WORKAROUND)**

**File:** `docker-compose.yml` line 13

```yaml
environment:
  - SPRING_JPA_HIBERNATE_DDL_AUTO=none  # ← Tắt validation
```

**Lưu ý:** Chỉ là workaround, vẫn cần fix schema mismatch!

---

## 🚀 **Các bước fix (Option 1 - KHUYẾN NGHỊ):**

### **1. Sửa tất cả migrations:**

```bash
# Tìm tất cả SERIAL trong migrations
grep -r "SERIAL PRIMARY KEY" backend/src/main/resources/db/migration/

# Sửa thành BIGSERIAL
# Dùng editor để replace "SERIAL PRIMARY KEY" → "BIGSERIAL PRIMARY KEY"
```

### **2. Rebuild database:**

```bash
# Xóa database cũ
docker-compose down -v

# Start lại
docker-compose up backend db

# Wait 15 seconds
```

### **3. Verify:**

```bash
# Check schema
docker exec swd392_group4-db-1 psql -U lab_user -d lab_booking -c "\d events"

# Expected: event_id | bigint (not integer)
```

### **4. Test login:**

```bash
curl -X POST http://localhost:8080/api/auth/login/fpt-sso \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fpt.edu.vn","password":"admin123"}'

# Expected: HTTP 200 + JWT token
```

---

## 🎯 **Tạm thời dùng `ddl-auto=none` để test Flutter:**

**File:** `docker-compose.yml` line 13

**SAU:**
```yaml
environment:
  - SPRING_JPA_HIBERNATE_DDL_AUTO=none  # Tắt validation tạm thời
```

Rebuild:
```bash
docker-compose down -v
docker-compose up backend db -d
```

**Sau khi này, Flutter app sẽ login được!**

---

## 📊 **Impact:**

**Hiện tại:**
- ❌ Backend không start được
- ❌ Flutter app không test được authentication
- ❌ Schema mismatch tiềm ẩn vấn đề tương lai

**Sau khi fix:**
- ✅ Backend start thành công
- ✅ Schema validation pass
- ✅ Flutter app hoạt động
- ✅ Tương lai an toàn với BIGINT

---

## 🎯 **Priority: CRITICAL**

Phải fix ngay để backend có thể start!

---

## 📝 **Files cần check:**

```bash
backend/src/main/resources/db/migration/V1__initial_schema.sql
backend/src/main/resources/db/migration/V5__complete_setup_with_test_data.sql
backend/src/main/resources/db/migration/V*.sql  # Tất cả migrations
```

**Tìm và replace:**
- `SERIAL PRIMARY KEY` → `BIGSERIAL PRIMARY KEY`
- `SERIAL NOT NULL` → `BIGSERIAL NOT NULL`
- `SERIAL` → `BIGSERIAL` (tất cả các trường hợp)

---

**Reported by:** Frontend Developer (Flutter)  
**Date:** 2025-10-26  
**Files:** All Flyway migrations with SERIAL type  
**Severity:** CRITICAL - Backend không start được!

