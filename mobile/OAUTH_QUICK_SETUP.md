# ⚡ Tạo OAuth Client ID - Hướng Dẫn Ngắn Gọn

## 🎯 Mục Tiêu
Tạo OAuth Client ID để Google Sign-In chạy trên emulator Android.

---

## 📋 Các Bước (10 phút)

### **1. Google Cloud Console (5 phút)**

1. **Truy cập:** https://console.cloud.google.com/
2. **Tạo project** → Chọn project
3. **OAuth Consent Screen (BẮT BUỘC):**
   - "APIs & Services" → "OAuth consent screen"
   - Chọn "External" → Điền:
     - App name: `Lab Booking`
     - User support email: Email của bạn
     - Developer contact: Email của bạn
   - Click "Save and Continue" (3 lần để skip các bước khác)
4. **Tạo Android OAuth Client:**
   - "APIs & Services" → "Credentials"
   - "+ CREATE CREDENTIALS" → "OAuth client ID"
   - Application type: **Android**
   - Name: `Lab Booking Android`
   - Package name: `com.unilab.lab_booking_mobile`
   - SHA-1: `8C:D4:C3:BF:F3:06:EE:A8:02:B6:C0:7B:D8:41:4C:4B:6A:78:5F:23`
   - Click "Create"
   - **Copy Android Client ID** (không cần dùng, chỉ để tham khảo)
5. **Tạo Web OAuth Client (QUAN TRỌNG):**
   - "+ CREATE CREDENTIALS" → "OAuth client ID"
   - Application type: **Web application**
   - Name: `Lab Booking Web`
   - Authorized JavaScript origins: `http://localhost:3000`, `http://localhost:5173`
   - Click "Create"
   - **Copy Web Client ID** (dạng: `xxxxx-xxxxx.apps.googleusercontent.com`)
   - **CẦN DÙNG Client ID này!**

---

### **2. Cập Nhật Code (2 phút)**

#### **File: `mobile/lib/providers/auth_provider.dart`**
```dart
serverClientId: 'YOUR_WEB_CLIENT_ID.apps.googleusercontent.com', // Dán Web Client ID
```

#### **File: `backend/src/main/resources/application-local.properties`**
```properties
app.google.clientId=YOUR_WEB_CLIENT_ID.apps.googleusercontent.com
```

---

### **3. Test (3 phút)**

```bash
cd mobile
flutter clean
flutter pub get
flutter run -d android
```

**Đợi 5-10 phút** → Test lại!

---

## ✅ Xong!

Sau khi hoàn thành, Google Sign-In sẽ hoạt động trên emulator! 🎉

