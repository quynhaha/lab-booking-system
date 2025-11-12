# 📱 Cấu Hình Google Sign-In cho Android Emulator

## 🎯 Vấn Đề

Emulator có SHA-1 fingerprint **KHÁC** với debug keystore thông thường. Cần lấy SHA-1 của emulator và thêm vào Google Cloud Console.

---

## 🔍 Bước 1: Lấy SHA-1 của Emulator

### **Cách 1: Dùng keytool (Nếu emulator đã chạy)**

```bash
# Windows
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android

# Tìm dòng "SHA1:" và copy giá trị
```

### **Cách 2: Dùng Gradle (Khuyến nghị)**

1. **Chạy emulator trước:**
   ```bash
   # Mở Android Studio → AVD Manager → Start emulator
   # Hoặc
   flutter emulators --launch <emulator_name>
   ```

2. **Lấy SHA-1:**
   ```bash
   cd mobile/android
   ./gradlew signingReport
   ```

3. **Tìm SHA-1 trong output:**
   - Tìm dòng có `SHA1:` 
   - Copy giá trị (dạng: `XX:XX:XX:...`)

### **Cách 3: Kiểm tra trong Android Studio**

1. Mở Android Studio
2. Vào **Build** → **Generate Signed Bundle / APK**
3. Chọn **Android App Bundle** → Next
4. Chọn **Create new...** → Chọn keystore
5. Xem SHA-1 trong thông tin keystore

---

## 🔧 Bước 2: Thêm SHA-1 vào Google Cloud Console

1. Vào Google Cloud Console: https://console.cloud.google.com/
2. Vào **APIs & Services** → **Credentials**
3. Tìm OAuth 2.0 Client ID cho Android (đã tạo trước đó)
4. Click **Edit** (biểu tượng bút chì)
5. Trong phần **SHA-1 certificate fingerprints**, click **+ ADD**
6. Dán SHA-1 của emulator
7. Click **Save**

**Lưu ý:** Có thể thêm nhiều SHA-1 (một cho debug, một cho emulator, một cho release)

---

## ✅ Bước 3: Test Lại

1. **Xóa app trên emulator** (nếu đã cài):
   ```bash
   adb uninstall com.unilab.lab_booking_mobile
   ```

2. **Chạy lại app:**
   ```bash
   cd mobile
   flutter clean
   flutter pub get
   flutter run -d android
   ```

3. **Đợi 5-10 phút** (Google cần thời gian sync SHA-1 mới)

4. **Test Google Sign-In:**
   - Nhấn "Đăng nhập bằng Google"
   - Chọn tài khoản Google
   - Phải hoạt động!

---

## 🚀 Giải Pháp Nhanh (Không Cần Cấu Hình)

Nếu không muốn cấu hình phức tạp, có thể:

### **Option 1: Dùng Web để Test**
```bash
flutter run -d chrome
```
Google Sign-In hoạt động tốt trên web mà không cần cấu hình SHA-1.

### **Option 2: Dùng FPT SSO Login**
- Dùng email + password thay vì Google Sign-In
- Không cần cấu hình gì cả

### **Option 3: Test trên Thiết Bị Thật**
- Thiết bị thật thường dùng debug keystore giống nhau
- SHA-1 đã có sẵn: `8C:D4:C3:BF:F3:06:EE:A8:02:B6:C0:7B:D8:41:4C:4B:6A:78:5F:23`

---

## 📝 Lưu Ý Quan Trọng

1. **Mỗi emulator có thể có SHA-1 khác nhau** (nếu dùng custom keystore)
2. **Debug keystore mặc định** thường giống nhau trên mọi máy
3. **Có thể thêm nhiều SHA-1** vào cùng một OAuth Client ID
4. **Đợi 5-10 phút** sau khi thêm SHA-1 mới

---

## 🐛 Troubleshooting

### **Vẫn lỗi ApiException: 10 sau khi thêm SHA-1?**

1. **Kiểm tra SHA-1 đã đúng chưa:**
   - Không có dấu cách
   - Đúng format: `XX:XX:XX:...` (chữ hoa)

2. **Đợi đủ thời gian:**
   - Google cần 5-10 phút để sync
   - Thử lại sau 10 phút

3. **Xóa và cài lại app:**
   ```bash
   flutter clean
   flutter pub get
   flutter run -d android
   ```

4. **Kiểm tra Package name:**
   - Phải đúng: `com.unilab.lab_booking_mobile`

---

## 🎯 Tóm Tắt

1. ✅ Lấy SHA-1 của emulator
2. ✅ Thêm SHA-1 vào Google Cloud Console (cùng OAuth Client ID)
3. ✅ Đợi 5-10 phút
4. ✅ Test lại trên emulator

**Hoặc dùng giải pháp nhanh: Chạy trên web hoặc dùng FPT SSO login!**

