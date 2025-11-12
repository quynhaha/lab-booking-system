# Quick Start Guide - Admin Features

## Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Git

## Setup Instructions

### 1. Clone and Navigate
```bash
cd "e:\se\ki 7\SWD\SWD392_Group4"
```

### 2. Configure Database
Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/unilab
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build the Project
```bash
cd backend
mvn clean install
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 5. Access Swagger UI
Open your browser and navigate to:
```
http://localhost:8080/swagger-ui.html
```

## Testing the Features

### Step 1: Authenticate
1. Go to **Auth Controller** in Swagger UI
2. Use the login endpoint with admin credentials
3. Copy the JWT token from the response
4. Click "Authorize" button at the top of Swagger UI
5. Enter: `Bearer YOUR_JWT_TOKEN`

### Step 2: View Dashboard
**Endpoint:** `GET /api/v1/admin/dashboard`

**What you'll see:**
- Total bookings, users, labs statistics
- Lab utilization rate
- Pending approvals count
- Recent bookings and penalties
- Financial metrics

**Example Response:**
```json
{
  "stats": {
    "totalBookings": 150,
    "activeBookings": 45,
    "pendingApprovals": 8,
    "totalUsers": 320,
    "totalLabs": 5,
    "labUtilizationRate": 67.5
  },
  "recentActivity": {
    "recentBookings": [...],
    "recentPenalties": [...],
    "pendingApprovals": [...]
  }
}
```

### Step 3: Generate Reports
**Endpoint:** `GET /api/v1/admin/reports/bookings`

**Parameters:**
- `startDate`: 2024-01-01T00:00:00
- `endDate`: 2024-12-31T23:59:59

**Report Types:**
- `/bookings` - Detailed booking analysis
- `/penalties` - Penalty statistics
- `/lab-utilization` - Lab usage patterns
- `/comprehensive` - All-in-one report

### Step 4: Create Multi-Lab Booking
**Endpoint:** `POST /api/v1/bookings/multi-lab`

**Request Body:**
```json
{
  "labIds": [1, 2, 3],
  "categoryId": 1,
  "title": "Computer Science Workshop",
  "description": "Full-day workshop across multiple labs",
  "startTime": "2024-10-25T09:00:00",
  "endTime": "2024-10-25T17:00:00",
  "participantsCount": 90
}
```

**Response:**
```json
[
  {
    "id": 101,
    "bookingCode": "BK-MULTI-1",
    "labName": "CS Lab 1",
    "status": "PENDING",
    "isMultiLab": true
  },
  {
    "id": 102,
    "bookingCode": "BK-MULTI-2",
    "labName": "CS Lab 2",
    "status": "PENDING",
    "isMultiLab": true,
    "parentBookingId": 101
  }
]
```

### Step 5: Cancel Booking with Refund
**Endpoint:** `PUT /api/v1/bookings/cancel`

**Request Body:**
```json
{
  "bookingId": 101,
  "cancellationReason": "Event postponed due to speaker unavailability",
  "withRefund": true
}
```

**Response:**
```json
{
  "id": 101,
  "status": "CANCELLED",
  "refundStatus": "PENDING",
  "refundAmount": "100.00",
  "cancelledAt": "2024-10-22T14:30:00"
}
```

### Step 6: Manage Event Categories
**Create Category - `POST /api/v1/config/categories`**
```json
{
  "code": "HACKATHON",
  "name": "Hackathon",
  "description": "Coding competitions and hackathons",
  "color": "#00BCD4",
  "isActive": true,
  "maxDurationHours": 24,
  "requiresApproval": true
}
```

**List Categories - `GET /api/v1/config/categories/active`**

### Step 7: Configure Penalty Rules
**Create Rule - `POST /api/v1/penalties/rules`**
```json
{
  "code": "EQUIPMENT-THEFT",
  "name": "Equipment Theft",
  "description": "Unauthorized removal of lab equipment",
  "violationType": "THEFT",
  "penaltyAmount": "500.00",
  "penaltyType": "FIXED",
  "penaltyPoints": 100,
  "suspensionDays": 365,
  "isActive": true
}
```

### Step 8: View Penalty History
**Endpoint:** `GET /api/v1/penalties/history/user/{userId}`

**Filter by Date:** `GET /api/v1/penalties/history/filter?startDate=...&endDate=...`

### Step 9: Apply Penalty (Admin)
**Endpoint:** `POST /api/v1/penalties/apply`

**Parameters:**
- `userId`: 5
- `bookingId`: 101 (optional)
- `ruleId`: 1
- `reason`: "No-show without prior notice"

### Step 10: Manage Booking Rules
**Create Rule - `POST /api/v1/config/rules`**
```json
{
  "code": "SEMESTER-LIMIT",
  "name": "Semester Booking Limit",
  "description": "Maximum bookings per semester for students",
  "ruleType": "MAX_BOOKINGS_PER_PERIOD",
  "ruleValue": "{\"count\": 20, \"period\": \"semester\"}",
  "isActive": true,
  "priority": 5,
  "appliesTo": "STUDENT"
}
```

## Common API Patterns

### Filtering by Date Range
Most list endpoints support date filtering:
```
GET /api/v1/admin/dashboard/filter?startDate=2024-10-01T00:00:00&endDate=2024-10-31T23:59:59
```

### Status Values
**Booking Status:**
- `PENDING` - Awaiting approval
- `APPROVED` - Confirmed
- `REJECTED` - Denied
- `CANCELLED` - Cancelled by user/admin
- `COMPLETED` - Event finished

**Refund Status:**
- `NONE` - No refund applicable
- `PENDING` - Refund requested
- `PROCESSED` - Refund completed
- `REJECTED` - Refund denied

**Penalty Status:**
- `PENDING` - Not yet paid
- `PAID` - Penalty paid
- `WAIVED` - Forgiven by admin
- `APPEALED` - Under review

## Troubleshooting

### Database Connection Error
```
Error: Connection refused
```
**Solution:** Ensure PostgreSQL is running and credentials are correct

### JWT Token Expired
```
Error: 401 Unauthorized
```
**Solution:** Re-authenticate and get a new token

### Foreign Key Constraint Violation
```
Error: violates foreign key constraint
```
**Solution:** Ensure referenced entities exist (e.g., user, lab, category)

### Migration Failed
```
Error: Migration checksum mismatch
```
**Solution:**
```bash
# Clean and rebuild
mvn clean
mvn flyway:clean
mvn spring-boot:run
```

## API Quick Reference

### Admin Dashboard
- `GET /api/v1/admin/dashboard` - Full dashboard
- `GET /api/v1/admin/dashboard/filter` - Filtered by date

### Reports
- `GET /api/v1/admin/reports/bookings` - Booking report
- `GET /api/v1/admin/reports/penalties` - Penalty report
- `GET /api/v1/admin/reports/lab-utilization` - Lab usage report
- `GET /api/v1/admin/reports/comprehensive` - Complete report

### Bookings
- `GET /api/v1/bookings` - List all
- `POST /api/v1/bookings` - Create single
- `POST /api/v1/bookings/multi-lab` - Create multi-lab
- `PUT /api/v1/bookings/cancel` - Cancel with refund
- `PUT /api/v1/bookings/{id}/approve` - Approve
- `PUT /api/v1/bookings/{id}/reject` - Reject

### Penalties
- `GET /api/v1/penalties/rules` - List rules
- `POST /api/v1/penalties/rules` - Create rule
- `GET /api/v1/penalties/history` - View history
- `POST /api/v1/penalties/apply` - Apply penalty
- `PUT /api/v1/penalties/history/{id}/waive` - Waive penalty

### Configuration
- `GET /api/v1/config/categories` - List categories
- `POST /api/v1/config/categories` - Create category
- `GET /api/v1/config/rules` - List rules
- `POST /api/v1/config/rules` - Create rule

## Sample Data Available

After initial startup, you'll have:
- **5 Labs:** CS Lab 1, CS Lab 2, Networking Lab, Physics Lab, Chemistry Lab
- **6 Event Categories:** Workshop, Seminar, Exam, Research, Practical, Event
- **5 Penalty Rules:** Late Cancellation, No Show, Damage, Policy Violation, Late Return
- **5 Booking Rules:** Max Duration, Advance Booking, Max Concurrent, Admin Override, Weekend Limit
- **2 Sample Bookings:** For testing

## Next Steps

1. ✅ Test all endpoints using Swagger UI
2. ✅ Create custom event categories for your use case
3. ✅ Configure booking rules based on your policies
4. ✅ Set up penalty rules
5. ✅ Start creating real bookings
6. ✅ Generate reports to analyze usage patterns

## Support & Documentation

- **Full Documentation:** See `ADMIN_FEATURES_README.md`
- **Implementation Details:** See `IMPLEMENTATION_CHECKLIST.md`
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/v3/api-docs

## Production Deployment Checklist

Before deploying to production:
- [ ] Change default passwords and secrets
- [ ] Configure proper database credentials
- [ ] Enable HTTPS/SSL
- [ ] Set up database backups
- [ ] Configure logging and monitoring
- [ ] Test all critical paths
- [ ] Review security settings
- [ ] Document admin procedures
- [ ] Train admin users
- [ ] Set up email notifications (if implemented)
