# 🐳 Lệnh Docker - Restart Services

## 🔄 Restart Backend (Khuyến nghị)

### **Cách 1: Restart chỉ backend**
```bash
docker-compose restart backend
```

### **Cách 2: Rebuild và restart backend (nếu code đã thay đổi)**
```bash
docker-compose up -d --build backend
```

### **Cách 3: Stop và start lại backend**
```bash
docker-compose stop backend
docker-compose start backend
```

---

## 🔄 Restart Tất Cả Services

### **Restart tất cả (giữ data)**
```bash
docker-compose restart
```

### **Stop và start lại tất cả**
```bash
docker-compose down
docker-compose up -d
```

### **Rebuild và restart tất cả (nếu code đã thay đổi)**
```bash
docker-compose down
docker-compose up -d --build
```

---

## 📋 Xem Logs Backend

### **Xem logs real-time**
```bash
docker-compose logs -f backend
```

### **Xem logs 50 dòng cuối**
```bash
docker-compose logs --tail 50 backend
```

### **Xem logs tất cả services**
```bash
docker-compose logs -f
```

---

## 🛑 Stop Services

### **Stop tất cả**
```bash
docker-compose stop
```

### **Stop và xóa containers (giữ data)**
```bash
docker-compose down
```

### **Stop và xóa tất cả (bao gồm data)**
```bash
docker-compose down -v
```

---

## ✅ Kiểm Tra Services Đang Chạy

```bash
docker-compose ps
```

Hoặc:
```bash
docker ps
```

---

## 🚀 Khởi Động Services

### **Chạy ở background (detached mode)**
```bash
docker-compose up -d
```

### **Chạy và xem logs**
```bash
docker-compose up
```

---

## 🔍 Troubleshooting

### **Xem logs khi có lỗi**
```bash
docker-compose logs backend | grep -i error
```

### **Vào container backend**
```bash
docker-compose exec backend sh
```

### **Kiểm tra backend có chạy không**
```bash
curl http://localhost:8080/api/auth/google
```

---

## 📝 Lưu Ý

- **`restart`**: Restart nhanh, không rebuild
- **`up --build`**: Rebuild image trước khi start (cần khi code thay đổi)
- **`down -v`**: Xóa cả volumes (mất data database)
- **`-d`**: Chạy ở background (detached mode)

