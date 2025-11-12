# 🚨 CRITICAL BUG: Database bị xóa sau khi Flyway seed data

## ❌ **Vấn đề:**

**File:** `docker-compose.yml` - Line 13

```yaml
environment:
  - SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop  # ← LỖI Ở ĐÂY!
```

**Hiện tượng:**
- Login với `admin@fpt.edu.vn` / `admin123` → **401 Unauthorized**
- Database hoàn toàn trống (0 users, 0 labs, 0 data)
- Flyway migrations chạy thành công nhưng data bị mất ngay sau đó

---

## 🔍 **Nguyên nhân:**

`ddl-auto=create-drop` khiến Hibernate:
1. **DROP** tất cả tables sau khi Flyway seed data ✅
2. **CREATE** lại tables từ JPA entities
3. Nhưng **KHÔNG CÓ DATA** (vì Flyway không chạy lại)

**Flow hiện tại (SAI):**
```
1. Flyway migrations run ✅
   → Tạo tables
   → Seed admin user (admin@fpt.edu.vn)
   → Seed labs
   → Seed test data

2. Hibernate DDL=create-drop ❌
   → DROP all tables
   → CREATE new tables from JPA entities
   → NO DATA!

3. Result: Database TRỐNG!
```

---

## ✅ **Giải pháp:**

### **Sửa `docker-compose.yml` line 13:**

**TRƯỚC:**
```yaml
environment:
  - SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop  # ❌ SAI
```

**SAU:**
```yaml
environment:
  - SPRING_JPA_HIBERNATE_DDL_AUTO=validate  # ✅ ĐÚNG
```

**Hoặc:**
```yaml
environment:
  - SPRING_JPA_HIBERNATE_DDL_AUTO=none  # ✅ CÒN TỐT HƠN
```

---

## 🎯 **Giải thích các options:**

| Option | Hành vi | Phù hợp? |
|--------|---------|----------|
| `create-drop` | DROP → CREATE mỗi lần restart | ❌ **KHÔNG** - Mất data! |
| `create` | DROP → CREATE lần đầu | ❌ Không - Mất data khi restart |
| `update` | Update schema nếu thay đổi | ⚠️ Không nên - Conflict với Flyway |
| `validate` | Chỉ kiểm tra schema khớp với entities | ✅ **TỐT** - Flyway quản lý migrations |
| `none` | Hibernate không động vào database | ✅ **TỐT NHẤT** - Flyway quản lý 100% |

**Khuyến nghị:** Dùng `validate` hoặc `none` khi đã có Flyway!

---

## 🚀 **Cách fix:**

### **1. Sửa docker-compose.yml:**
```bash
# Sửa line 13 trong docker-compose.yml
- SPRING_JPA_HIBERNATE_DDL_AUTO=validate  # hoặc none
```

### **2. Rebuild containers:**
```bash
docker-compose down -v
docker-compose up backend db
```

### **3. Verify data:**
```bash
docker exec swd392_group4-db-1 psql -U lab_user -d lab_booking -c "SELECT email, full_name FROM users;"
```

**Expected output:**
```
       email        | full_name 
--------------------+-----------
 admin@unilab.local | Admin
 admin@fpt.edu.vn   | FPT Admin
```

### **4. Test login:**
```bash
curl -X POST http://localhost:8080/api/auth/login/fpt-sso \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fpt.edu.vn","password":"admin123"}'
```

**Expected:** HTTP 200 với JWT token

---

## 📊 **Impact:**

**Hiện tại:**
- ❌ Không login được (401)
- ❌ Flutter app không dùng được
- ❌ React Web không dùng được
- ❌ Tất cả data bị mất mỗi lần restart

**Sau khi fix:**
- ✅ Login thành công
- ✅ Flutter app hoạt động
- ✅ Data persistent sau restart
- ✅ Flyway quản lý migrations đúng cách

---

## 🎯 **Priority: CRITICAL**

Phải sửa ngay để Flutter mobile app có thể test authentication!

---

## 📝 **Test sau khi fix:**

```bash
# 1. Stop và xóa volumes cũ
docker-compose down -v

# 2. Start lại
docker-compose up backend db

# 3. Wait 15 seconds for migrations

# 4. Test login
curl -X POST http://localhost:8080/api/auth/login/fpt-sso \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fpt.edu.vn","password":"admin123"}'

# Expected: HTTP 200 + JWT token
```

---

**Reported by:** Frontend Developer (Flutter)  
**Date:** 2025-10-26  
**File:** `docker-compose.yml` line 13  
**Severity:** CRITICAL


