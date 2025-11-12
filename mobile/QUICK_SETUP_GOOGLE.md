# ⚡ Quick Setup Google Sign-In cho Android

## 🎯 Mục Tiêu
Cấu hình Google Sign-In để chạy được trên điện thoại Android.

---

## 📝 Checklist

### ✅ Bước 1: Google Cloud Console (5-10 phút)

1. **Truy cập:** https://console.cloud.google.com/
2. **Tạo/C chọn project**
3. **Enable API:**
   - Vào "APIs & Services" → "Library"
   - Tìm "Google Sign-In API" → Enable
4. **OAuth Consent Screen:**
   - Vào "APIs & Services" → "OAuth consent screen"
   - Chọn "External" → Điền thông tin → Save
5. **Tạo Android OAuth Client:**
   - Vào "APIs & Services" → "Credentials"
   - "+ CREATE CREDENTIALS" → "OAuth client ID"
   - Chọn "Android"
   - Package name: `com.unilab.lab_booking_mobile`
   - SHA-1: `8C:D4:C3:BF:F3:06:EE:A8:02:B6:C0:7B:D8:41:4C:4B:6A:78:5F:23`
   - **Copy Client ID** (dạng: `xxxxx-xxxxx.apps.googleusercontent.com`)
6. **Tạo Web OAuth Client:**
   - "+ CREATE CREDENTIALS" → "OAuth client ID"
   - Chọn "Web application"
   - Authorized origins: `http://localhost:3000`, `http://localhost:5173`
   - **Copy Client ID**

---

### ✅ Bước 2: Cập Nhật Code

#### **File 1: `mobile/lib/providers/auth_provider.dart`**

Tìm dòng:
```dart
final GoogleSignIn _googleSignIn = GoogleSignIn(
  scopes: ['email', 'profile'],
  serverClientId: null,
);
```

Thay bằng:
```dart
final GoogleSignIn _googleSignIn = GoogleSignIn(
  scopes: ['email', 'profile'],
  serverClientId: 'YOUR_WEB_CLIENT_ID.apps.googleusercontent.com', // Dán Web Client ID vào đây
);
```

#### **File 2: `backend/src/main/resources/application-local.properties`**

Thêm hoặc cập nhật:
```properties
app.google.clientId=YOUR_WEB_CLIENT_ID.apps.googleusercontent.com
```

---

### ✅ Bước 3: Test

```bash
cd mobile
flutter clean
flutter pub get
flutter run -d android
```

---

## ⚠️ Lưu Ý

1. **Đợi 5-10 phút** sau khi tạo OAuth Client ID (Google cần thời gian sync)
2. **SHA-1 fingerprint** phải chính xác (không có dấu cách, dấu `:`)
3. **Package name** phải khớp: `com.unilab.lab_booking_mobile`

---

## 🆘 Nếu Vẫn Lỗi

1. **Xóa app và cài lại:**
   ```bash
   flutter clean
   flutter pub get
   flutter run -d android
   ```

2. **Kiểm tra Google Play Services:**
   - Đảm bảo Google Play Services đã cài và cập nhật trên điện thoại

3. **Test với Gmail cá nhân:**
   - Thử đăng nhập bằng Gmail cá nhân trước
   - Nếu được → vấn đề có thể ở email FPT
   - Nếu không → vấn đề ở cấu hình

---

## 📞 Support

Nếu gặp vấn đề, kiểm tra:
- SHA-1 fingerprint đã đúng chưa
- OAuth Client ID đã copy đúng chưa
- Package name đã khớp chưa
- Đã đợi đủ thời gian sync chưa (5-10 phút)

