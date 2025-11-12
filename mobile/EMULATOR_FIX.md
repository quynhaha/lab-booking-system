# 🔧 Fix Google Sign-In cho Emulator - Hướng Dẫn Nhanh

## ✅ Tin Tốt!

**Emulator và debug keystore dùng chung SHA-1!**

SHA-1 của bạn: `8C:D4:C3:BF:F3:06:EE:A8:02:B6:C0:7B:D8:41:4C:4B:6A:78:5F:23`

---

## 🚀 Các Bước Đơn Giản (10 phút)

### **Bước 1: Tạo OAuth Client ID (5 phút)**

1. **Truy cập:** https://console.cloud.google.com/
2. **Tạo/C chọn project**
3. **Enable API:**
   - Vào "APIs & Services" → "Library"
   - Tìm "Google Sign-In API" → **Enable**
4. **OAuth Consent Screen:**
   - Vào "APIs & Services" → "OAuth consent screen"
   - Chọn "External" → Điền tên app → Save
5. **Tạo Android OAuth Client:**
   - Vào "APIs & Services" → "Credentials"
   - "+ CREATE CREDENTIALS" → "OAuth client ID"
   - Chọn **Android**
   - Package: `com.unilab.lab_booking_mobile`
   - SHA-1: `8C:D4:C3:BF:F3:06:EE:A8:02:B6:C0:7B:D8:41:4C:4B:6A:78:5F:23`
   - **Copy Client ID** (dạng: `xxxxx-xxxxx.apps.googleusercontent.com`)
6. **Tạo Web OAuth Client:**
   - "+ CREATE CREDENTIALS" → "OAuth client ID"
   - Chọn **Web application**
   - **Copy Client ID**

---

### **Bước 2: Cập Nhật Code (2 phút)**

#### **File: `mobile/lib/providers/auth_provider.dart`**

Tìm dòng:
```dart
serverClientId: null,
```

Thay bằng:
```dart
serverClientId: 'YOUR_WEB_CLIENT_ID.apps.googleusercontent.com', // Dán Web Client ID
```

#### **File: `backend/src/main/resources/application-local.properties`**

Thêm:
```properties
app.google.clientId=YOUR_WEB_CLIENT_ID.apps.googleusercontent.com
```

---

### **Bước 3: Test (3 phút)**

```bash
cd mobile
flutter clean
flutter pub get
flutter run -d android
```

**Đợi 5-10 phút** sau khi tạo OAuth Client ID, sau đó test lại!

---

## ⚡ Giải Pháp Tạm Thời (Không Cần Cấu Hình)

Nếu chưa muốn cấu hình ngay, có thể:

1. **Dùng FPT SSO Login:**
   - Email: `an.nguyen@fpt.edu.vn`
   - Password: `fpt123`
   - Không cần Google Sign-In!

2. **Chạy trên Web:**
   ```bash
   flutter run -d chrome
   ```
   Google Sign-In hoạt động tốt trên web!

---

## 📝 Lưu Ý

- **SHA-1 đã đúng:** Emulator dùng chung SHA-1 với debug keystore
- **Đợi 5-10 phút:** Sau khi tạo OAuth Client ID
- **Package name:** Phải đúng `com.unilab.lab_booking_mobile`

---

**Sau khi cấu hình xong, Google Sign-In sẽ hoạt động trên emulator! 🎉**

