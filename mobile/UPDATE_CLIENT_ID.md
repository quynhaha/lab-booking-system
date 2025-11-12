# 🔧 Cập Nhật Web Client ID

## ✅ Bạn đã tạo xong OAuth Client IDs!

Bây giờ cần copy **Web Client ID** và dán vào code.

---

## 📋 Các Bước

### **1. Copy Web Client ID từ Google Cloud Console**

1. Vào Google Cloud Console → **Clients**
2. Tìm **"Lab Booking Web"** (Type: Web application)
3. Click vào **icon copy** (📋) bên cạnh Client ID
4. **Copy toàn bộ Client ID** (dạng: `454718520252-xxxxx.apps.googleusercontent.com`)

---

### **2. Cập Nhật Flutter Code**

**File: `mobile/lib/providers/auth_provider.dart`**

Tìm dòng:
```dart
serverClientId: 'YOUR_WEB_CLIENT_ID_HERE.apps.googleusercontent.com',
```

Thay bằng:
```dart
serverClientId: '454718520252-xxxxx.apps.googleusercontent.com', // Dán Web Client ID của bạn
```

---

### **3. Cập Nhật Backend Code (Optional)**

**File: `backend/src/main/resources/application-local.properties`**

Tìm dòng:
```properties
app.google.clientId=
```

Thay bằng:
```properties
app.google.clientId=454718520252-xxxxx.apps.googleusercontent.com
```

---

### **4. Test**

```bash
cd mobile
flutter clean
flutter pub get
flutter run -d android
```

**Đợi 5-10 phút** → Test Google Sign-In!

---

## ✅ Xong!

Sau khi cập nhật, Google Sign-In sẽ hoạt động trên emulator! 🎉

