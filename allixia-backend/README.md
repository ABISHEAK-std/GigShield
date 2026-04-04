# ALLIXIA - AI-Powered Parametric Insurance Platform

## 🎯 Overview

**ALLIXIA** is an AI-powered parametric insurance platform that protects gig economy workers from income loss caused by environmental disruptions. The system automatically detects disaster events using NASA data, evaluates impact, and triggers instant payouts through blockchain—without requiring manual claims.

### Key Features
- ✅ **Zero-Touch Claims**: Automatic disaster detection and payout
- ✅ **NASA Integration**: Real-time disaster data from NASA EONET API
- ✅ **Grid-Based Mapping**: 1km x 1km geographic cells for precise impact assessment
- ✅ **Instant Payouts**: Automated claim processing and payment
- ✅ **Blockchain Audit**: Transaction transparency on Polygon network
- ✅ **JWT Authentication**: Secure user management

---

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- PostgreSQL database
- (Optional) OpenWeatherMap API key

### Setup

1. **Clone and navigate to project**
   ```bash
   cd d:\ALLIXA\allixia-backend
   ```

2. **Configure database** - Create PostgreSQL database:
   ```sql
   CREATE DATABASE allixia;
   ```

3. **Set environment variables** (or edit `application.properties`):
   ```bash
   export DB_USERNAME=your_postgres_username
   export DB_PASSWORD=your_postgres_password
   export JWT_SECRET=your_secret_key_min_256_bits
   ```

4. **Build and run**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Verify**:
   ```
   GET http://localhost:8080/api/health
   ```

---

## 📡 API Endpoints

### Health Check
```http
GET /api/health
```

### Authentication
```http
POST /api/auth/register
POST /api/auth/login
```

### Location Tracking
```http
POST   /api/location/{userId}
GET    /api/location/{userId}/history
```

### Insurance Policies
```http
POST   /api/policies
GET    /api/policies/{policyId}
GET    /api/policies/user/{userId}
GET    /api/policies/user/{userId}/active
DELETE /api/policies/{policyId}
```

### Claims
```http
GET /api/claims/{claimId}
GET /api/claims/user/{userId}
```

### Payouts
```http
GET /api/payouts/{payoutId}
GET /api/payouts/user/{userId}
```

### Demo & Testing
```http
POST /api/demo/quick-setup/{phoneNumber}
POST /api/demo/simulate-disaster
GET  /api/demo/dashboard
```

---

## 🎬 Demo Flow

### 1. Quick Setup (Create User + Policy)
```bash
POST http://localhost:8080/api/demo/quick-setup/1234567890

Response:
{
  "success": true,
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "policyNumber": "POL-1712345678901",
  "gridCell": "GRID_367_-669",
  "message": "User setup complete with active policy"
}
```

### 2. Update Worker Location
```bash
POST http://localhost:8080/api/location/{userId}
Content-Type: application/json

{
  "latitude": 40.7128,
  "longitude": -74.0060,
  "accuracy": 10.0
}
```

### 3. Simulate Disaster Event
```bash
POST http://localhost:8080/api/demo/simulate-disaster
Content-Type: application/json

{
  "latitude": 40.7128,
  "longitude": -74.0060,
  "eventType": "Severe Storm"
}
```

### 4. Check Claims (Auto-Generated)
```bash
GET http://localhost:8080/api/claims/user/{userId}

Response:
[
  {
    "id": "...",
    "claimNumber": "CLM-1712345679000",
    "policyId": "...",
    "userId": "...",
    "claimAmount": 1000.00,
    "status": "PAID",
    "autoApproved": true,
    "approvedAt": "2026-04-04T13:00:00",
    "paidAt": "2026-04-04T13:00:01"
  }
]
```

### 5. Check Payouts (Auto-Executed)
```bash
GET http://localhost:8080/api/payouts/user/{userId}

Response:
[
  {
    "id": "...",
    "claimId": "...",
    "amount": 1000.00,
    "status": "COMPLETED",
    "transactionHash": "0x123abc...",
    "blockchainConfirmed": true,
    "processedAt": "2026-04-04T13:00:01"
  }
]
```

---

## 🏗️ Architecture

### Core Components

1. **User Management** (`UserService`)
   - User registration with BCrypt password hashing
   - JWT token authentication
   - Worker status tracking

2. **Location Tracking** (`LocationService`, `GeoService`)
   - GPS coordinate logging
   - Grid-based mapping (1km x 1km cells)
   - Location history tracking

3. **Event Ingestion** (`EventIngestionService`)
   - NASA EONET API integration (scheduled polling)
   - Disaster event normalization
   - Grid cell assignment

4. **Policy Management** (`PolicyService`)
   - Policy creation and activation
   - Coverage and premium calculation
   - Policy lifecycle management

5. **Claims Processing** (`ClaimProcessingService`)
   - Automated claim detection
   - Zero-touch approval
   - Trigger-based processing

6. **Payout Engine** (`PayoutService`)
   - Mock blockchain transactions
   - Instant payout execution
   - Transaction logging

### Database Schema

```
users
├── worker_status
├── location_logs
├── grid_cells
├── insurance_policies
│   └── claims
│       ├── claim_triggers
│       ├── fraud_checks
│       └── payouts
├── disaster_events
└── weather_data
```

---

## 🔄 Automated Workflows

### 1. Event Detection & Claims (Every 60 seconds)
```
NASA EONET API → Event Ingestion → Grid Mapping → Find Affected Users → 
Create Claims → Auto-Approve → Execute Payouts
```

### 2. Claim Processing Flow
1. Disaster event detected in grid cell
2. System finds active workers in that grid (last 24 hours)
3. Validates active insurance policies
4. Auto-generates claims
5. Runs fraud detection (basic)
6. Auto-approves if fraud score is low
7. Triggers payout immediately
8. Records blockchain transaction (mock)

---

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Migration**: Flyway
- **Security**: Spring Security + JWT
- **APIs**: NASA EONET, OpenWeatherMap
- **Blockchain**: Web3j (Polygon Mumbai)
- **Build**: Maven

---

## 📊 Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`:

- **V1**: Users and worker status tables
- **V2**: Location tracking
- **V3**: Events and weather data
- **V4**: Grid cells and risk factors
- **V5**: Insurance policies
- **V6**: Claims, triggers, fraud checks, and payouts

Migrations run automatically on application startup.

---

## 🔐 Security

- **Password Encryption**: BCrypt hashing
- **Authentication**: JWT tokens (24-hour expiration)
- **API Security**: Spring Security (currently permissive for MVP)
- **Environment Variables**: Sensitive data externalized

---

## 📝 Environment Variables

Required:
- `DB_USERNAME` - PostgreSQL username
- `DB_PASSWORD` - PostgreSQL password
- `JWT_SECRET` - Secret key for JWT (min 256 bits)

Optional:
- `WEATHER_API_KEY` - OpenWeatherMap API key
- `POLICY_CONTRACT_ADDRESS` - Blockchain policy contract
- `PAYOUT_CONTRACT_ADDRESS` - Blockchain payout contract
- `WALLET_PRIVATE_KEY` - Blockchain wallet private key

---

## 🧪 Testing

### Manual Testing with cURL

**Register User:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","phone":"1234567890","email":"john@example.com","password":"password123"}'
```

**Create Policy:**
```bash
curl -X POST http://localhost:8080/api/policies \
  -H "Content-Type: application/json" \
  -d '{"userId":"USER_ID","coverageType":"WEATHER_PROTECTION","coverageAmount":1000,"premiumAmount":50}'
```

**Simulate Disaster:**
```bash
curl -X POST http://localhost:8080/api/demo/simulate-disaster \
  -H "Content-Type: application/json" \
  -d '{"latitude":40.7128,"longitude":-74.0060,"eventType":"Severe Storm"}'
```

---

## 🎯 MVP Features Implemented

- ✅ User registration and authentication
- ✅ JWT-based security
- ✅ GPS location tracking
- ✅ 1km x 1km grid mapping
- ✅ NASA EONET disaster event ingestion
- ✅ Insurance policy management
- ✅ Automated claim detection
- ✅ Zero-touch claim approval
- ✅ Instant payout execution
- ✅ Mock blockchain integration
- ✅ Demo simulation endpoints

---

## 🚧 Future Enhancements

- [ ] OpenWeatherMap API integration (weather thresholds)
- [ ] Advanced fraud detection (ML-based)
- [ ] Real blockchain smart contracts deployment
- [ ] Mobile app integration
- [ ] Premium calculation based on risk score
- [ ] Multi-currency support
- [ ] Email/SMS notifications
- [ ] Admin dashboard
- [ ] Analytics and reporting

---

## 📚 Project Structure

```
allixia-backend/
├── src/main/java/com/allixia/
│   ├── config/              # RestTemplate, etc.
│   ├── controller/          # REST API endpoints
│   ├── dto/                 # Data Transfer Objects
│   ├── entity/              # JPA Entities
│   ├── exception/           # Custom exceptions & handlers
│   ├── repository/          # Data access layer
│   ├── security/            # JWT & Spring Security config
│   ├── service/             # Business logic
│   └── AllixiaApplication.java
├── src/main/resources/
│   ├── application.properties
│   └── db/migration/        # Flyway SQL migrations
└── pom.xml
```

---

## 📞 Support

For issues or questions:
- Review the PROJECT_DETAILS.md for complete specifications
- Check application logs for error messages
- Ensure database is running and accessible
- Verify environment variables are set correctly

---

## 📄 License

This is a demo/MVP project for educational purposes.

---

**Built with ❤️ using Spring Boot and NASA Open Data**
