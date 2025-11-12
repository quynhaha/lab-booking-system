# 🔧 Fix Flutter Build Error: Path with Spaces

## ❌ Vấn đề
Lỗi xảy ra vì đường dẫn project có khoảng trắng: `C:\Users\MINH ANH\SWD392_Group4`

```
Failed to create parent directory 'C:\Users\MINH' when creating directory 
'C:\Users\MINH\ ANH\SWD392_Group4\mobile\build\...'
```

## ✅ Giải pháp

### **Cách 1: Di chuyển project (Khuyến nghị)**

1. **Tạo thư mục mới không có khoảng trắng:**
   ```powershell
   mkdir C:\Projects\SWD392_Group4
   ```

2. **Copy toàn bộ project:**
   ```powershell
   xcopy "C:\Users\MINH ANH\SWD392_Group4" "C:\Projects\SWD392_Group4" /E /I /H
   ```

3. **Mở project từ thư mục mới:**
   ```powershell
   cd C:\Projects\SWD392_Group4\mobile
   flutter clean
   flutter pub get
   flutter run
   ```

---

### **Cách 2: Sử dụng Short Path (8.3 format)**

1. **Kiểm tra short path hiện tại:**
   ```powershell
   dir /x "C:\Users"
   ```

2. **Sử dụng short path trong terminal:**
   ```powershell
   cd C:\Users\MINHAN~1\SWD392_Group4\mobile
   flutter run
   ```

---

### **Cách 3: Cấu hình Gradle (Tạm thời)**

Thêm vào `mobile/android/gradle.properties`:

```properties
org.gradle.jvmargs=-Xmx8G -XX:MaxMetaspaceSize=4G -XX:ReservedCodeCacheSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=true
org.gradle.caching=true
org.gradle.parallel=true
org.gradle.configureondemand=true
```

Sau đó thử lại:
```powershell
cd "C:\Users\MINH ANH\SWD392_Group4\mobile"
flutter clean
flutter pub get
flutter run
```

---

## 🎯 Khuyến nghị

**Cách 1 (Di chuyển project)** là giải pháp tốt nhất vì:
- ✅ Tránh mọi vấn đề với khoảng trắng
- ✅ Tốt cho các công cụ build khác
- ✅ Tránh lỗi tương tự trong tương lai

**Lưu ý:** Nếu bạn đang dùng Git, nhớ cập nhật remote URL sau khi di chuyển:
```powershell
cd C:\Projects\SWD392_Group4
git remote -v  # Kiểm tra remote
# Nếu cần, cập nhật remote URL
```

