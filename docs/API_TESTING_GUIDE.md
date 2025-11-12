# 🚀 API Testing Guide - Admin Features

## 📋 Mục Lục
- [Khởi động ứng dụng](#khởi-động-ứng-dụng)
- [Authentication](#authentication)
- [Admin Dashboard APIs](#admin-dashboard-apis)
- [Report APIs](#report-apis)
- [Booking Management APIs](#booking-management-apis)
- [Penalty Management APIs](#penalty-management-apis)
- [Configuration APIs](#configuration-apis)
- [Lab Management APIs](#lab-management-apis)

---

## Khởi động ứng dụng

```bash
# 1. Start PostgreSQL database
docker-compose up -d postgres

# 2. Run Spring Boot application
cd backend
mvn spring-boot:run

# 3. Access Swagger UI
http://localhost:8080/swagger-ui.html
```

---

## Authentication

### Login để lấy JWT Token

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "admin123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "email": "admin@example.com",
  "role": "ADMIN"
}
```

**Sử dụng token trong các request tiếp theo:**
```bash
-H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## Admin Dashboard APIs

### 1. View Admin Dashboard (UC-27)

```bash
curl -X GET "http://localhost:8080/api/v1/admin/dashboard" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Response:**
```json
{
  "totalBookings": 150,
  "activeBookings": 45,
  "completedBookings": 100,
  "cancelledBookings": 5,
  "totalRevenue": 15000.00,
  "totalPenalties": 500.00,
  "totalRefunds": 200.00,
  "labUtilization": 75.5,
  "mostBookedLab": "Computer Lab A",
  "peakBookingHours": ["09:00-11:00", "14:00-16:00"],
  "recentBookings": [...],
  "upcomingBookings": [...]
}
```

### 2. Filter Dashboard by Date (UC-38)

```bash
curl -X GET "http://localhost:8080/api/v1/admin/dashboard/filter?startDate=2025-10-01&endDate=2025-10-31" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 3. Get Dashboard Statistics

```bash
curl -X GET "http://localhost:8080/api/v1/admin/dashboard/stats?period=MONTHLY" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Report APIs

### 1. Generate Report (UC-07)

**Generate Booking Report:**
```bash
curl -X POST "http://localhost:8080/api/v1/admin/reports/generate" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "reportType": "BOOKING",
    "startDate": "2025-10-01T00:00:00",
    "endDate": "2025-10-31T23:59:59",
    "format": "PDF",
    "includeCharts": true
  }'
```

**Generate Revenue Report:**
```bash
curl -X POST "http://localhost:8080/api/v1/admin/reports/generate" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "reportType": "REVENUE",
    "startDate": "2025-10-01T00:00:00",
    "endDate": "2025-10-31T23:59:59",
    "format": "EXCEL"
  }'
```

### 2. Download Report

```bash
curl -X GET "http://localhost:8080/api/v1/admin/reports/1/download" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output report.pdf
```

### 3. List All Reports

```bash
curl -X GET "http://localhost:8080/api/v1/admin/reports?page=0&size=20" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Booking Management APIs

### 1. Cancel Booking with Refund (UC-28)

```bash
curl -X POST "http://localhost:8080/api/v1/admin/bookings/1/cancel" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "reason": "Lab maintenance required",
    "processRefund": true,
    "notifyUser": true
  }'
```

**Response:**
```json
{
  "bookingId": 1,
  "status": "CANCELLED",
  "refundAmount": 50.00,
  "refundStatus": "APPROVED",
  "message": "Booking cancelled and refund processed"
}
```

### 2. Multi-lab Booking (UC-30)

```bash
curl -X POST "http://localhost:8080/api/v1/bookings/multi-lab" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "labIds": [1, 2, 3],
    "eventCategoryId": 1,
    "startTime": "2025-10-25T09:00:00",
    "endTime": "2025-10-25T12:00:00",
    "purpose": "Workshop event across multiple labs",
    "expectedAttendees": 100
  }'
```

### 3. Get All Bookings

```bash
curl -X GET "http://localhost:8080/api/v1/bookings?page=0&size=20" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 4. Get Booking Details

```bash
curl -X GET "http://localhost:8080/api/v1/bookings/1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Penalty Management APIs

### 1. Manage Penalty Rules (UC-29)

**Create Penalty Rule:**
```bash
curl -X POST "http://localhost:8080/api/v1/admin/penalties/rules" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "PEN-006",
    "name": "Equipment Damage",
    "description": "Penalty for damaging lab equipment",
    "violationType": "EQUIPMENT_DAMAGE",
    "penaltyAmount": "100.00",
    "penaltyType": "FINE",
    "penaltyPoints": 15,
    "suspensionDays": 0,
    "isActive": true,
    "gracePeriodHours": 0
  }'
```

**Update Penalty Rule:**
```bash
curl -X PUT "http://localhost:8080/api/v1/admin/penalties/rules/1" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Penalty Rule",
    "penaltyAmount": "75.00",
    "isActive": true
  }'
```

**Delete Penalty Rule:**
```bash
curl -X DELETE "http://localhost:8080/api/v1/admin/penalties/rules/1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. View Penalty History (UC-36)

**Get User Penalty History:**
```bash
curl -X GET "http://localhost:8080/api/v1/admin/penalties/history/user/2" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Get All Penalties:**
```bash
curl -X GET "http://localhost:8080/api/v1/admin/penalties/history?page=0&size=20" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Get Penalty Statistics:**
```bash
curl -X GET "http://localhost:8080/api/v1/admin/penalties/statistics?startDate=2025-10-01&endDate=2025-10-31" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Configuration APIs

### 1. Configure Event Categories (UC-31)

**Create Event Category:**
```bash
curl -X POST "http://localhost:8080/api/v1/admin/config/categories" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "CAT-007",
    "name": "Research Project",
    "description": "Lab booking for research projects",
    "color": "#FF5733",
    "isActive": true,
    "maxDurationHours": 48,
    "requiresApproval": true
  }'
```

**Update Event Category:**
```bash
curl -X PUT "http://localhost:8080/api/v1/admin/config/categories/1" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Category",
    "maxDurationHours": 72,
    "isActive": true
  }'
```

**Get All Categories:**
```bash
curl -X GET "http://localhost:8080/api/v1/admin/config/categories" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 2. Apply Booking Rules (UC-35)

**Create Booking Rule:**
```bash
curl -X POST "http://localhost:8080/api/v1/admin/config/rules" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "RULE-006",
    "name": "Student Max Duration",
    "description": "Maximum booking duration for students",
    "ruleType": "MAX_DURATION",
    "ruleValue": "4",
    "isActive": true,
    "priority": 10,
    "appliesTo": "STUDENT",
    "categoryId": null
  }'
```

**Update Booking Rule:**
```bash
curl -X PUT "http://localhost:8080/api/v1/admin/config/rules/1" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "ruleValue": "6",
    "isActive": true,
    "priority": 15
  }'
```

**Get All Rules:**
```bash
curl -X GET "http://localhost:8080/api/v1/admin/config/rules?active=true" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Lab Management APIs

### 1. Create Lab

```bash
curl -X POST "http://localhost:8080/api/v1/labs" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "labCode": "LAB-006",
    "name": "AI Research Lab",
    "description": "Advanced AI and Machine Learning lab",
    "location": "Building E, Floor 4",
    "capacity": 30,
    "status": "AVAILABLE",
    "facilities": "GPU servers, High-performance workstations, AI development tools"
  }'
```

### 2. Update Lab

```bash
curl -X PUT "http://localhost:8080/api/v1/labs/1" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Lab Name",
    "capacity": 35,
    "status": "AVAILABLE"
  }'
```

### 3. Update Lab Status

```bash
curl -X PATCH "http://localhost:8080/api/v1/labs/1/status?status=MAINTENANCE" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 4. Get Available Labs

```bash
curl -X GET "http://localhost:8080/api/v1/labs/available" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 5. Search Labs

```bash
curl -X GET "http://localhost:8080/api/v1/labs/search?keyword=Computer&location=Building A" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 📊 Test Scenarios

### Scenario 1: Admin generates monthly report
1. Login as admin
2. Generate booking report for October 2025
3. Download the report PDF
4. Verify report contains booking statistics

### Scenario 2: Admin cancels booking with refund
1. Login as admin
2. Find active booking
3. Cancel booking with refund
4. Verify refund is processed
5. Check user receives notification

### Scenario 3: Admin creates penalty rule
1. Login as admin
2. Create new penalty rule for equipment damage
3. Verify rule is active
4. Apply penalty to user who violated
5. Check penalty history

### Scenario 4: Admin configures booking rules
1. Login as admin
2. Create rule limiting student booking duration
3. Student tries to book beyond limit
4. Verify booking is rejected
5. Check error message

### Scenario 5: Multi-lab booking
1. Login as staff/admin
2. Create multi-lab booking for workshop
3. Verify all labs are booked
4. Check parent-child relationship
5. Cancel parent booking
6. Verify all child bookings cancelled

---

## 🔧 Troubleshooting

### Common Issues

**1. 401 Unauthorized**
- Check if token is valid and not expired
- Verify token is included in Authorization header
- Format: `Bearer YOUR_TOKEN`

**2. 403 Forbidden**
- Check user role (ADMIN required for most endpoints)
- Verify user has necessary permissions

**3. 404 Not Found**
- Verify resource ID exists
- Check URL path is correct

**4. 400 Bad Request**
- Check request body format (JSON)
- Verify all required fields are provided
- Check field validation constraints

**5. 500 Internal Server Error**
- Check server logs for details
- Verify database connection
- Check for null pointer exceptions

---

## 📝 Notes

- All timestamps are in ISO 8601 format: `yyyy-MM-ddTHH:mm:ss`
- All endpoints require authentication except login
- Admin endpoints require ADMIN role
- Pagination uses zero-based index
- Default page size is 20
- Maximum page size is 100

---

## 🎯 Sample Data

The database is pre-populated with sample data:
- 5 Labs (LAB-001 to LAB-005)
- 6 Event Categories (CAT-001 to CAT-006)
- 5 Penalty Rules (PEN-001 to PEN-005)
- 5 Booking Rules (RULE-001 to RULE-005)
- 2 Sample Bookings

You can test with these existing records or create new ones.
