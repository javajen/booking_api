# Booking API

A production-ready REST API for appointment/room booking, built with **Spring Boot 3**, **Spring Security**, **JWT authentication**, and **PostgreSQL**.

---

## Features

- JWT-based authentication (register & login)
- Role-based access control (ADMIN vs CUSTOMER)
- Resource management (rooms, slots, services)
- Booking with **conflict detection** — no double bookings
- Booking cancellation with ownership enforcement
- Swagger UI for interactive API docs
- Docker Compose for one-command local setup

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JWT (jjwt) |
| Persistence | Spring Data JPA + PostgreSQL |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |
| DevOps | Docker + Docker Compose |

---

## Database Schema

```
users
  id, email (unique), password, full_name, role (ADMIN | CUSTOMER)

resources
  id, name, description, capacity, active

bookings
  id, user_id (FK), resource_id (FK), start_time, end_time, notes, status (CONFIRMED | CANCELLED)
```

---

## Getting Started

### Option 1: Docker Compose (Recommended)

```bash
docker-compose up --build
```

API will be available at `http://localhost:8080`

### Option 2: Run Locally

1. Start a PostgreSQL instance (default: localhost:5432, db: `booking_db`)
2. Update credentials in `src/main/resources/application.yml` if needed
3. Run:

```bash
mvn spring-boot:run
```

---

## API Endpoints

### Auth
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login, returns JWT | No |

### Resources
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/resources` | List all active resources | Yes |
| GET | `/api/resources/{id}` | Get resource by ID | Yes |
| POST | `/api/resources` | Create resource | ADMIN |
| PUT | `/api/resources/{id}` | Update resource | ADMIN |
| DELETE | `/api/resources/{id}` | Deactivate resource | ADMIN |

### Bookings
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/bookings` | Create a booking | Yes |
| GET | `/api/bookings/my` | Get my bookings | Yes |
| GET | `/api/bookings/resource/{id}` | Get bookings for a resource | Yes |
| PATCH | `/api/bookings/{id}/cancel` | Cancel a booking | Yes (owner or ADMIN) |

---

## Example Usage

### 1. Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Jane Doe","email":"jane@example.com","password":"secret123"}'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"jane@example.com","password":"secret123"}'
# Returns: {"token": "eyJhbGci..."}
```

### 3. Create a Booking
```bash
curl -X POST http://localhost:8080/api/bookings \
  -H "Authorization: Bearer <your-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "resourceId": 1,
    "startTime": "2025-06-01T09:00:00",
    "endTime": "2025-06-01T10:00:00",
    "notes": "Team standup"
  }'
```

---

## Swagger UI

Once running, visit:

```
http://localhost:8080/swagger-ui.html
```

Use the **Authorize** button to paste your JWT token and test all endpoints interactively.

---

## Key Design Decisions

- **Conflict detection** uses a JPQL range overlap query: a booking conflicts if `startTime < requested.endTime AND endTime > requested.startTime`
- **Stateless auth** — no server-side sessions; JWT carries all auth state
- **Soft delete** on resources (`active=false`) to preserve booking history
- **Role enforcement** via `@PreAuthorize` at the controller layer
