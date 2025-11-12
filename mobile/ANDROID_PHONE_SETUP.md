# 📱 Cấu Hình Google Sign-In cho Điện Thoại Android Thật

## ✅ Tin Tốt!

**Có thể đăng nhập bằng Google trên điện thoại Android thật!**

Chỉ cần thêm **SHA-1 fingerprint** của điện thoại vào Google Cloud Console.

---

## 🔍 Bước 1: Lấy SHA-1 Fingerprint của Điện Thoại

### **Cách 1: Dùng Gradle (Khuyến nghị)**

1. **Kết nối điện thoại Android qua USB**
   - Bật **USB Debugging** trên điện thoại:
     - Settings → About phone → Tap "Build number" 7 lần
     - Settings → Developer options → Bật "USB debugging"
   - Kết nối USB và cho phép debugging

2. **Kiểm tra điện thoại đã kết nối:**
   ```bash
   cd mobile
   flutter devices
   ```
   - Phải thấy điện thoại của bạn trong danh sách

3. **Lấy SHA-1:**
   ```bash
   cd mobile/android
   ./gradlew signingReport
   ```
   - Tìm dòng `SHA1:` trong output
   - Copy giá trị (dạng: `XX:XX:XX:...`)

### **Cách 2: Dùng keytool (Nếu có keystore)**

```bash
# Windows
keytool -list -v -keystore "%USERPROFILE%\.android\debug.keystore" -alias androiddebugkey -storepass android -keypass android

# Tìm dòng "SHA1:" và copy giá trị
```

**Lưu ý:** 
- Nếu dùng **debug keystore mặc định**, SHA-1 sẽ giống với emulator: `8C:D4:C3:BF:F3:06:EE:A8:02:B6:C0:7B:D8:41:4C:4B:6A:78:5F:23`
- Nếu dùng **release keystore** hoặc keystore khác, SHA-1 sẽ khác

---

## 🔧 Bước 2: Thêm SHA-1 vào Google Cloud Console

1. **Truy cập:** https://console.cloud.google.com/
2. **Vào project của bạn** → "APIs & Services" → "Credentials"
3. **Tìm OAuth 2.0 Client ID cho Android** ("Lab Booking Android")
4. **Click Edit** (biểu tượng bút chì)
5. **Trong phần "SHA-1 certificate fingerprints":**
   - Click **"+ ADD"**
   - Dán SHA-1 của điện thoại
6. **Click "Save"**

**Lưu ý:** Có thể thêm nhiều SHA-1 (một cho emulator, một cho điện thoại thật, một cho release)

---

## ✅ Bước 3: Test

1. **Chạy app trên điện thoại:**
   ```bash
   cd mobile
   flutter run -d <device-id>
   ```
   - Hoặc chọn device từ Android Studio

2. **Test Google Sign-In:**
   - Nhấn "Đăng nhập bằng Google"
   - Chọn tài khoản Google
   - Phải đăng nhập thành công!

---

## ⚠️ Lưu Ý Quan Trọng

### **1. Debug vs Release Keystore**

- **Debug keystore** (mặc định):
  - SHA-1: `8C:D4:C3:BF:F3:06:EE:A8:02:B6:C0:7B:D8:41:4C:4B:6A:78:5F:23`
  - Dùng cho development và testing
  - Thường giống nhau trên mọi máy

- **Release keystore**:
  - SHA-1 khác nhau tùy vào keystore bạn tạo
  - Dùng cho production (khi publish app)
  - Cần lấy SHA-1 riêng

### **2. Nhiều SHA-1 trong Google Cloud Console**

Bạn có thể thêm nhiều SHA-1 vào cùng một OAuth Client ID:
- ✅ SHA-1 của emulator
- ✅ SHA-1 của điện thoại thật (debug)
- ✅ SHA-1 của release keystore

### **3. Đợi Thời Gian Sync**

Sau khi thêm SHA-1 mới:
- **Đợi 5-10 phút** để Google sync
- Sau đó test lại

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
   flutter run -d <device-id>
   ```

4. **Kiểm tra Package name:**
   - Phải đúng: `com.unilab.lab_booking_mobile`

---

## 📝 Tóm Tắt

1. ✅ Kết nối điện thoại qua USB
2. ✅ Bật USB Debugging
3. ✅ Lấy SHA-1 fingerprint
4. ✅ Thêm SHA-1 vào Google Cloud Console
5. ✅ Đợi 5-10 phút
6. ✅ Test Google Sign-In

---

**Sau khi hoàn thành, Google Sign-In sẽ hoạt động trên điện thoại Android thật! 🎉**

