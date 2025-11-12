# 🔧 Quick Fix: Flutter Build với Path có Khoảng Trắng

## ⚠️ Vấn đề
Flutter/Gradle không xử lý tốt đường dẫn có khoảng trắng: `C:\Users\MINH ANH\...`

## ✅ Giải pháp nhanh nhất

### **Cách 1: Sử dụng Short Path (8.3 format)**

1. **Tìm short path name:**
   ```powershell
   cmd /c "dir /x C:\Users" | findstr "MINH"
   ```
   Kết quả có thể là: `MINHAN~1` hoặc tương tự

2. **Chạy Flutter với short path:**
   ```powershell
   cd C:\Users\MINHAN~1\SWD392_Group4\mobile
   flutter run
   ```

---

### **Cách 2: Tạo Junction/Symlink (Khuyến nghị)**

1. **Tạo thư mục mới không có khoảng trắng:**
   ```powershell
   mkdir C:\Projects
   ```

2. **Tạo junction từ thư mục cũ sang mới:**
   ```powershell
   # Chạy PowerShell as Administrator
   New-Item -ItemType Junction -Path "C:\Projects\SWD392_Group4" -Target "C:\Users\MINH ANH\SWD392_Group4"
   ```

3. **Làm việc từ thư mục mới:**
   ```powershell
   cd C:\Projects\SWD392_Group4\mobile
   flutter run
   ```

**Lưu ý:** Junction chỉ là link, mọi thay đổi vẫn ở thư mục gốc.

---

### **Cách 3: Di chuyển project (Vĩnh viễn)**

1. **Tạo thư mục mới:**
   ```powershell
   mkdir C:\Projects\SWD392_Group4
   ```

2. **Copy project:**
   ```powershell
   robocopy "C:\Users\MINH ANH\SWD392_Group4" "C:\Projects\SWD392_Group4" /E /COPYALL /R:3 /W:5
   ```

3. **Xóa thư mục cũ (sau khi đã test):**
   ```powershell
   Remove-Item "C:\Users\MINH ANH\SWD392_Group4" -Recurse -Force
   ```

4. **Làm việc từ thư mục mới:**
   ```powershell
   cd C:\Projects\SWD392_Group4\mobile
   flutter run
   ```

---

## 🎯 Khuyến nghị

**Cách 2 (Junction)** là tốt nhất vì:
- ✅ Không cần copy file
- ✅ Mọi thay đổi vẫn ở thư mục gốc
- ✅ Giải quyết vấn đề build ngay lập tức
- ✅ Không ảnh hưởng đến Git (nếu có)

