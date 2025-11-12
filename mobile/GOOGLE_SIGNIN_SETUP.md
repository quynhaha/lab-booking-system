# 🔧 Hướng Dẫn Cấu Hình Google Sign-In cho Android

## 📋 Tổng Quan

Để Google Sign-In hoạt động trên Android, bạn cần:
1. Tạo OAuth 2.0 Client ID trong Google Cloud Console
2. Thêm SHA-1 fingerprint
3. Cấu hình trong Flutter app

---

## 🚀 Các Bước Cấu Hình

### **Bước 1: Lấy SHA-1 Fingerprint**

SHA-1 fingerprint của bạn (đã lấy được):
```
SHA1: 8C:D4:C3:BF:F3:06:EE:A8:02:B6:C0:7B:D8:41:4C:4B:6A:78:5F:23
```

**Hoặc lấy lại bằng lệnh:**
```bash
cd mobile/android
./gradlew signingReport
```

Tìm dòng `SHA1:` trong output.

---

### **Bước 2: Tạo Project trong Google Cloud Console**

1. Truy cập: https://console.cloud.google.com/
2. Đăng nhập bằng tài khoản Google
3. Tạo project mới:
   - Click "Select a project" → "New Project"
   - Tên project: `Lab Booking App` (hoặc tên khác)
   - Click "Create"

---

### **Bước 3: Enable Google Sign-In API**

1. Trong Google Cloud Console, vào **APIs & Services** → **Library**
2. Tìm "Google Sign-In API" hoặc "Google+ API"
3. Click **Enable**

---

### **Bước 4: Tạo OAuth Consent Screen**

1. Vào **APIs & Services** → **OAuth consent screen**
2. Chọn **External** (cho development)
3. Điền thông tin:
   - App name: `Lab Booking`
   - User support email: Email của bạn
   - Developer contact: Email của bạn
4. Click **Save and Continue**
5. Ở màn hình Scopes, click **Save and Continue**
6. Ở màn hình Test users, có thể bỏ qua → **Save and Continue**

---

### **Bước 5: Tạo OAuth 2.0 Client ID cho Android**

1. Vào **APIs & Services** → **Credentials**
2. Click **+ CREATE CREDENTIALS** → **OAuth client ID**
3. Chọn **Application type**: **Android**
4. Điền thông tin:
   - **Name**: `Lab Booking Android`
   - **Package name**: `com.unilab.lab_booking_mobile`
   - **SHA-1 certificate fingerprint**: 
     ```
     8C:D4:C3:BF:F3:06:EE:A8:02:B6:C0:7B:D8:41:4C:4B:6A:78:5F:23
     ```
5. Click **Create**
6. **Copy Client ID** (dạng: `xxxxx.apps.googleusercontent.com`)

---

### **Bước 6: Tạo OAuth 2.0 Client ID cho Web (nếu cần)**

1. Vào **APIs & Services** → **Credentials**
2. Click **+ CREATE CREDENTIALS** → **OAuth client ID**
3. Chọn **Application type**: **Web application**
4. Điền thông tin:
   - **Name**: `Lab Booking Web`
   - **Authorized JavaScript origins**: 
     - `http://localhost:3000`
     - `http://localhost:5173`
   - **Authorized redirect URIs**: 
     - `http://localhost:3000`
     - `http://localhost:5173`
5. Click **Create**
6. **Copy Client ID** (dạng: `xxxxx.apps.googleusercontent.com`)

---

### **Bước 7: Cấu Hình trong Flutter App**

#### **7.1. Cập nhật AuthProvider**

File: `mobile/lib/providers/auth_provider.dart`

Thay đổi:
```dart
final GoogleSignIn _googleSignIn = GoogleSignIn(
  scopes: ['email', 'profile'],
  serverClientId: 'YOUR_WEB_CLIENT_ID.apps.googleusercontent.com', // Web Client ID
);
```

#### **7.2. Cấu hình Android**

File: `mobile/android/app/build.gradle`

Thêm vào phần `android`:
```gradle
android {
    // ... existing code ...
    
    defaultConfig {
        // ... existing code ...
        // Add this line
        resValue "string", "default_web_client_id", "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"
    }
}
```

---

### **Bước 8: Cập nhật Backend (nếu cần)**

File: `backend/src/main/resources/application-local.properties`

Thêm hoặc cập nhật:
```properties
app.google.clientId=YOUR_WEB_CLIENT_ID.apps.googleusercontent.com
```

---

## ✅ Kiểm Tra

1. **Chạy app trên Android:**
   ```bash
   cd mobile
   flutter run -d android
   ```

2. **Test Google Sign-In:**
   - Nhấn "Đăng nhập bằng Google"
   - Chọn tài khoản Google
   - Phải đăng nhập thành công

---

## 🐛 Troubleshooting

### **Lỗi: "ApiException: 10"**
- Kiểm tra SHA-1 fingerprint đã đúng chưa
- Kiểm tra Package name: `com.unilab.lab_booking_mobile`
- Đợi vài phút sau khi tạo OAuth Client ID (Google cần thời gian sync)

### **Lỗi: "sign_in_failed"**
- Kiểm tra Google Sign-In API đã enable chưa
- Kiểm tra OAuth Consent Screen đã setup chưa
- Thử xóa app và cài lại

### **Không thấy tài khoản Google**
- Kiểm tra Google Play Services đã cài đặt và cập nhật chưa
- Thử thêm tài khoản Google vào Settings → Accounts trên điện thoại

---

## 📝 Lưu Ý

1. **SHA-1 fingerprint khác nhau cho Debug và Release:**
   - Debug: Dùng SHA-1 từ `signingReport` (đã có)
   - Release: Cần lấy SHA-1 từ release keystore

2. **OAuth Client ID:**
   - Android Client ID: Dùng cho Android app
   - Web Client ID: Dùng cho Flutter web và backend verification

3. **Test Users:**
   - Nếu OAuth Consent Screen ở chế độ "Testing", chỉ test users mới đăng nhập được
   - Có thể thêm email vào Test users trong OAuth Consent Screen

---

## 🎯 Tóm Tắt

1. ✅ Lấy SHA-1: `8C:D4:C3:BF:F3:06:EE:A8:02:B6:C0:7B:D8:41:4C:4B:6A:78:5F:23`
2. ✅ Tạo project trong Google Cloud Console
3. ✅ Enable Google Sign-In API
4. ✅ Tạo OAuth Consent Screen
5. ✅ Tạo Android OAuth Client ID với SHA-1
6. ✅ Tạo Web OAuth Client ID
7. ✅ Cấu hình trong Flutter app
8. ✅ Test trên Android

---

**Sau khi hoàn thành, Google Sign-In sẽ hoạt động trên điện thoại Android! 🎉**

