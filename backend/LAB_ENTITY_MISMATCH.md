# 🚨 Lab Entity Mismatch với Database Schema

## ❌ **Vấn đề:**

**Error khi create/fetch labs:**
```
ERROR: column l1_0.id does not exist
ERROR: column "created_at" of relation "labs" does not exist
```

**Root cause:** JPA Entity `Lab.java` không khớp với database table `labs`

---

## 🔍 **Chi tiết:**

### **Database Schema (hiện tại):**
```sql
Table "public.labs"
 Column      | Type                   
-------------|------------------------
 lab_id      | integer (PK)          
 lab_name    | varchar(100)          
 capacity    | integer               
 location    | varchar(100)          
 description | varchar(255)          
 status      | varchar(20)           
```

### **JPA Entity expects:**
```java
@Entity
@Table(name = "labs")
public class Lab {
    @Id
    @GeneratedValue
    private Long id;                    // ❌ Expects "id", DB has "lab_id"
    
    private String labCode;              // ❌ Column not exists in DB
    private String name;                 // ❌ Expects "name", DB has "lab_name"
    private String description;          // ✅ OK
    private String location;             // ✅ OK
    private Integer capacity;            // ✅ OK
    private String status;               // ✅ OK
    private String facilities;           // ❌ Column not exists in DB
    private LocalDateTime createdAt;     // ❌ Column not exists in DB
    private LocalDateTime updatedAt;     // ❌ Column not exists in DB
}
```

---

## ✅ **Giải pháp 1: Fix JPA Entity Mapping**

**File:** `backend/src/main/java/com/unilab/model/Lab.java`

```java
@Entity
@Table(name = "labs")
public class Lab {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lab_id")  // ✅ Map to lab_id
    private Long id;
    
    @Column(name = "lab_name", nullable = false)  // ✅ Map to lab_name
    private String name;
    
    // Remove labCode, facilities, createdAt, updatedAt
    // Hoặc thêm columns này vào database
    
    @Column(length = 255)
    private String description;
    
    @Column(length = 100)
    private String location;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(length = 20)
    private String status;
}
```

---

## ✅ **Giải pháp 2: Update Database Migration**

**Tạo migration mới:**

```sql
-- V11__add_missing_lab_columns.sql

ALTER TABLE labs RENAME COLUMN lab_id TO id;
ALTER TABLE labs RENAME COLUMN lab_name TO name;

ALTER TABLE labs ADD COLUMN lab_code VARCHAR(50) UNIQUE;
ALTER TABLE labs ADD COLUMN facilities VARCHAR(1000);
ALTER TABLE labs ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT NOW();
ALTER TABLE labs ADD COLUMN updated_at TIMESTAMP;

-- Update existing labs with default lab_code
UPDATE labs SET lab_code = 'LAB' || LPAD(id::text, 3, '0') WHERE lab_code IS NULL;
```

**⚠️ Lưu ý:** Phải update foreign key constraints trong các bảng khác!

---

## 🎯 **Khuyến nghị:**

**Dùng Giải pháp 1** (Fix JPA Entity) vì:
- ✅ Nhanh hơn, không cần migration
- ✅ Không ảnh hưởng đến foreign keys
- ✅ Backwards compatible

**Nếu muốn dùng Giải pháp 2:**
- Phải cẩn thận với foreign keys
- Test kỹ trước khi deploy
- Backup database trước

---

## 🚀 **Quick Fix (Giải pháp 1):**

### **1. Sửa Lab.java:**

Thay đổi các annotations:
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "lab_id")
private Long id;

@Column(name = "lab_name", nullable = false)
private String name;

// Xóa hoặc comment out:
// private String labCode;
// private String facilities;
// private LocalDateTime createdAt;
// private LocalDateTime updatedAt;
```

### **2. Rebuild & restart:**

```bash
docker-compose build backend
docker-compose up backend db -d
```

### **3. Test:**

```bash
# Fetch labs
curl http://localhost:8080/api/v1/labs

# Create lab
curl -X POST http://localhost:8080/api/v1/labs \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Lab",
    "location": "Building A",
    "capacity": 20,
    "status": "AVAILABLE"
  }'
```

---

## 📊 **Impact:**

**Hiện tại:**
- ❌ Không fetch được labs list
- ❌ Không create được lab mới
- ❌ Admin dashboard không hoạt động

**Sau khi fix:**
- ✅ Fetch labs thành công
- ✅ Create lab thành công
- ✅ Admin dashboard hiển thị data

---

## 🎯 **Priority: HIGH**

Web Admin Dashboard không hoạt động được nếu không fix!

---

**Reported by:** Frontend Developer  
**Date:** 2025-10-26  
**File:** `backend/src/main/java/com/unilab/model/Lab.java`  
**Severity:** HIGH - Blocking admin dashboard features






