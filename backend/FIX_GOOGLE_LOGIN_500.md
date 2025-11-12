# 🔧 Fix Lỗi 500 - Google Login

## 🐛 Vấn Đề

Lỗi **"Đăng nhập Google thất bại: 500"** khi đăng nhập bằng Google.

---

## ✅ Nguyên Nhân

1. **Backend chưa được restart** sau khi cập nhật code
2. **Code Java có lỗi compile** (đã fix: xóa import không dùng)

---

## 🚀 Cách Fix

### **Bước 1: Restart Backend**

```bash
cd backend

# Dừng backend nếu đang chạy (Ctrl+C trong terminal đang chạy backend)

# Chạy lại backend
mvn spring-boot:run
```

**Hoặc nếu dùng Docker:**
```bash
docker-compose restart backend
# Hoặc
docker-compose down
docker-compose up backend db
```

---

### **Bước 2: Kiểm Tra Backend Đã Chạy**

Mở browser và truy cập:
- http://localhost:8080/api/auth/google
- Hoặc http://localhost:8080/swagger-ui.html

Nếu thấy Swagger UI → Backend đã chạy ✅

---

### **Bước 3: Kiểm Tra Logs Backend**

Khi test Google login, xem logs backend có lỗi gì:

**Nếu chạy bằng Maven:**
- Xem terminal đang chạy `mvn spring-boot:run`
- Tìm dòng có `ERROR` hoặc `Exception`

**Nếu chạy bằng Docker:**
```bash
docker logs swd392_group4-backend-1 --tail 50 -f
```

---

### **Bước 4: Test Lại Google Login**

1. Mở app trên emulator
2. Nhấn "Đăng nhập bằng Google"
3. Chọn tài khoản Google
4. Kiểm tra kết quả

---

## 🔍 Troubleshooting

### **Nếu vẫn lỗi 500:**

1. **Kiểm tra Google Client IDs đã config đúng chưa:**
   - File: `backend/src/main/resources/application-local.properties`
   - Phải có:
     ```properties
     app.google.clientId=454718520252-ld1bai5jfp7497qp9qosjhjv25glbb3l.apps.googleusercontent.com
     app.google.androidClientId=454718520252-5b4e29q7a8qrgu4ka162u958kr06sihe.apps.googleusercontent.com
     ```

2. **Kiểm tra backend có compile lỗi không:**
   ```bash
   cd backend
   mvn clean compile
   ```
   - Nếu có lỗi → Fix lỗi trước
   - Nếu không có lỗi → Restart backend

3. **Kiểm tra Google token có được gửi đúng không:**
   - Xem logs backend khi test login
   - Tìm dòng có `idToken` hoặc `GoogleIdToken`

---

## ✅ Checklist

- [ ] Backend đã được restart
- [ ] Backend đang chạy (check http://localhost:8080)
- [ ] Google Client IDs đã config đúng
- [ ] Code Java không có lỗi compile
- [ ] Đã đợi 5-10 phút sau khi tạo OAuth Client IDs

---

**Sau khi restart backend, test lại Google login!** 🎉

