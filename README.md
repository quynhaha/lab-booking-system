# Lab Booking Monorepo

A comprehensive lab booking system with web frontend, mobile app, and backend API.

## Project Structure

```
lab-booking/
├── backend/          # Spring Boot Java API
├── frontend/         # React/TypeScript Web App
├── mobile/           # Flutter Mobile App
├── docker-compose.yml
└── README.md
```

## Services

- **Backend API**: Spring Boot REST API (Port 8080)
- **Web Frontend**: React with Vite (Port 5173)
- **Mobile App**: Flutter Web App (Port 3000)
- **Database**: PostgreSQL (Port 5432)

## Quick Start

### Using Docker (Recommended)

1. **Start all services:**
   ```bash
   docker-compose up --build
   ```

2. **Access the applications:**
   - Web App: http://localhost:5173
   - Mobile App: http://localhost:3000
   - API Documentation: http://localhost:8080/swagger-ui.html
   - Database: localhost:5432

### Individual Development

#### Backend Development
```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend Development
```bash
cd frontend
npm install
npm run dev
```

#### Mobile Development
```bash
cd mobile

# Install Flutter dependencies
flutter pub get

# Generate JSON serialization code
flutter packages pub run build_runner build

# Run on web
flutter run -d chrome

# Run on Android (requires Android Studio)
flutter run -d android

# Run on iOS (requires Xcode on macOS)
flutter run -d ios
```

## Mobile App Features

- **Modern UI**: Material Design 3 with dark/light theme support
- **Event Browsing**: View available lab events
- **Booking Management**: Manage your lab reservations
- **Responsive Design**: Works on mobile and desktop browsers
- **API Integration**: Connects to the same Spring Boot backend

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.3.3
- Spring Data JPA
- PostgreSQL
- Flyway (Database migrations)
- OpenAPI/Swagger

### Web Frontend
- React 18
- TypeScript
- Vite
- Modern CSS

### Mobile App
- Flutter 3.4.4+
- Dart
- Material Design 3
- Provider (State Management)
- Go Router (Navigation)
- HTTP (API Client)

## API Endpoints

- `GET /api/events` - List all events
- `GET /api/events/{id}` - Get event details
- `GET /api/health` - Health check

## Development Notes

- All services share the same database
- API is accessible from both web and mobile apps
- Docker networking allows services to communicate
- Mobile app runs as web app for easy development and deployment
