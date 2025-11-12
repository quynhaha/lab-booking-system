# 🌐 Web Admin Dashboard Guide

## 📋 **Overview**

React Web Admin Dashboard cho hệ thống Lab Booking FPT University.

**Chỉ dành cho ADMIN** - Students & Teachers sử dụng Mobile App (Flutter).

---

## 🎯 **Features**

### ✅ **Đã hoàn thành:**

1. **🔐 Authentication**
   - Login với FPT SSO (`@fpt.edu.vn`)
   - JWT token management (auto-save to localStorage)
   - Auto-login nếu đã có token
   - Admin role verification
   - Logout functionality

2. **📊 Dashboard**
   - Overview statistics (Total Labs, Available Labs, Total Bookings, Total Capacity)
   - Real-time data từ Backend API
   - Beautiful Material Design UI
   - Responsive design (mobile, tablet, desktop)

3. **🏢 Labs Management**
   - List all labs với chi tiết đầy đủ
   - Lab status (Available, Occupied, Maintenance)
   - Lab capacity, equipment, location
   - Real-time data từ `/api/v1/labs`

### 🚧 **Coming Soon:**

- Bookings management
- Recurring schedules
- User management
- Add/Edit/Delete labs
- Analytics & Reports

---

## 🚀 **Cách chạy**

### **1. Start Backend (Docker)**

```bash
# Từ root folder
docker-compose up backend db
```

Backend sẽ chạy tại: `http://localhost:8080`

### **2. Start Frontend (React)**

```bash
# Từ root folder
cd frontend

# Install dependencies (lần đầu)
npm install

# Start dev server
npm run dev
```

Frontend sẽ chạy tại: `http://localhost:5173`

---

## 👤 **Tài khoản Admin**

**⚠️ Chỉ tài khoản ADMIN mới đăng nhập được Web Dashboard!**

### **Tài khoản test:**

```
Email: admin@fpt.edu.vn
Password: admin123
Role: ADMIN
```

**Tài khoản khác (ADMIN):**
- `admin@unilab.local` / `admin123`

### **❌ Không dùng được:**

- Student accounts (`an.nguyen@fpt.edu.vn`, etc.) → Chỉ cho Mobile App
- Teacher accounts → Chỉ cho Mobile App

---

## 🔧 **Backend API Endpoints**

### **1. Login**

```http
POST http://localhost:8080/api/auth/login/fpt-sso
Content-Type: application/json

{
  "email": "admin@fpt.edu.vn",
  "password": "admin123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "loginMethod": "FPT_SSO",
  "userType": "FPT_STUDENT",
  "message": "Login successful via FPT_SSO"
}
```

### **2. Get User Info**

```http
GET http://localhost:8080/api/auth/me
Authorization: Bearer <token>
```

**Response:**
```json
{
  "id": 1,
  "fullName": "FPT Admin",
  "email": "admin@fpt.edu.vn",
  "role": "ADMIN",
  "studentId": null,
  "faculty": null,
  "status": true
}
```

### **3. Get All Labs**

```http
GET http://localhost:8080/api/v1/labs
Authorization: Bearer <token>
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Computer Lab A",
    "building": "Engineering Building",
    "room": "Floor 2, Room 201",
    "capacity": 30,
    "description": "High-performance computers",
    "status": "AVAILABLE",
    "equipment": ["Computers", "Projector", "Whiteboard"]
  }
]
```

### **4. Dashboard Stats (Optional)**

```http
GET http://localhost:8080/api/v1/admin/dashboard
Authorization: Bearer <token>
```

---

## 🎨 **UI/UX Features**

### **Login Screen:**
- FPT University branding
- Clean, modern Material Design
- Admin credentials demo button
- Clear error/success messages
- "Admin Only" info box
- Responsive design

### **Admin Dashboard:**
- Top navigation bar với Logo
- Welcome message với user's full name
- Quick stats cards:
  - 🔵 Total Labs (blue)
  - 🟢 Available Labs (green)
  - 🟣 Total Bookings (purple)
  - 🟠 Total Capacity (orange)
- Tabs navigation (Labs, Bookings, Schedules)
- Grid layout cho labs
- Beautiful cards với:
  - Lab name, location
  - Status badge (Available/Occupied/Maintenance)
  - Description
  - Capacity
  - Equipment chips
  - Action buttons

### **Responsive Design:**
- **Desktop (> 992px):** Grid layout, full navigation
- **Tablet (600px - 992px):** Adapted grid, wrapped stats
- **Mobile (< 600px):** Single column, stacked navigation

---

## 📂 **File Structure**

```
frontend/
├── src/
│   ├── ui/
│   │   ├── App.tsx                    # Main app với authentication
│   │   ├── Login.css                  # Login screen styles
│   │   ├── AdminDashboard.tsx         # Admin dashboard component
│   │   ├── AdminDashboard.css         # Dashboard styles
│   │   └── StudentDashboard.tsx       # (Unused - for mobile)
│   └── main.tsx                       # Entry point
├── package.json
├── tsconfig.json
├── vite.config.ts
└── WEB_ADMIN_GUIDE.md                 # This file
```

---

## 🔐 **Authentication Flow**

### **1. Login Flow:**

```
User enters email/password
    ↓
POST /api/auth/login/fpt-sso
    ↓
Backend validates credentials
    ↓
Returns JWT token
    ↓
Frontend saves token to localStorage
    ↓
GET /api/auth/me (with token)
    ↓
Backend returns user info
    ↓
Check if role === 'ADMIN'
    ↓
If ADMIN → Show Dashboard
If NOT ADMIN → Show error, clear token
```

### **2. Auto-Login Flow:**

```
App loads
    ↓
Check localStorage for 'jwt_token'
    ↓
If found → GET /api/auth/me
    ↓
If valid & ADMIN → Auto login to Dashboard
If invalid → Clear token, show login
```

### **3. Logout Flow:**

```
User clicks Logout
    ↓
Clear 'jwt_token' from localStorage
    ↓
Reset app state
    ↓
Show login screen
```

---

## 🧪 **Testing**

### **Test Login:**

1. Mở browser: `http://localhost:5173`
2. Click **"Fill Admin Credentials"**
3. Click **"Sign In"**
4. ✅ Phải thấy Dashboard với:
   - Welcome message: "Welcome, FPT Admin"
   - Stats cards với numbers
   - Labs grid với data từ backend

### **Test Auto-Login:**

1. Login thành công (step trên)
2. Refresh page (F5)
3. ✅ Phải tự động vào Dashboard (không cần login lại)

### **Test Logout:**

1. Trong Dashboard, click **"Logout"**
2. ✅ Phải quay về Login screen
3. Refresh page → Vẫn ở Login screen (token đã clear)

### **Test Non-Admin Account:**

1. Thử login với `an.nguyen@fpt.edu.vn` / `fpt123`
2. ✅ Phải thấy error: "Access denied: Admin role required"

---

## 🐛 **Troubleshooting**

### **❌ "Lỗi khi đăng nhập"**

**Nguyên nhân:** Backend không chạy hoặc không kết nối được

**Giải pháp:**
```bash
# Check backend đang chạy
docker ps

# Nếu không thấy backend, start lại
docker-compose up backend db
```

### **❌ "Access denied: Admin role required"**

**Nguyên nhân:** Đang dùng student/teacher account

**Giải pháp:** Dùng tài khoản admin: `admin@fpt.edu.vn` / `admin123`

### **❌ "No labs found"**

**Nguyên nhân:** Database trống hoặc Flyway migrations chưa chạy

**Giải pháp:**
```bash
# Rebuild database
docker-compose down -v
docker-compose up backend db
```

### **❌ Page trắng / không load**

**Nguyên nhân:** Frontend build error

**Giải pháp:**
```bash
cd frontend
npm install
npm run dev
```

---

## 🎉 **Kết luận**

Web Admin Dashboard đã được tích hợp hoàn chỉnh với Backend API!

**Features:**
- ✅ Authentication với JWT
- ✅ Admin-only access
- ✅ Real-time labs data
- ✅ Beautiful responsive UI
- ✅ Auto-login
- ✅ Dashboard statistics

**Next Steps:**
- 🚧 Bookings management
- 🚧 Add/Edit/Delete labs
- 🚧 User management
- 🚧 Analytics & Reports

---

**Developed with ❤️ for FPT University Lab Booking System**






