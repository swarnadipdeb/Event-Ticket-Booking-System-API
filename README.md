# Event Ticket Booking System API

A Spring Boot REST API backend for managing event bookings. Users can register as organizers or customers, create events with ticket availability, and book tickets to events.

**Live API:** https://event-ticket-booking-system-api-production.up.railway.app

## Tech Stack

- **Java 17** with **Spring Boot 4.0.2**
- **PostgreSQL** database
- **JPA/Hibernate** for ORM
- **Jakarta Bean Validation** for input validation
- **Maven** for build management

## Features

- User registration with roles (ORGANIZER, CUSTOMER)
- Event creation and management
- Ticket booking with availability tracking
- Optimistic locking for concurrent updates
- Centralized exception handling
- Timezone-aware timestamps (UTC)

## API Endpoints

### Events (`/api/events`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/events` | Get all events |
| GET | `/api/events/{eventId}` | Get event by ID |
| GET | `/api/events/organizer/{organizerId}` | Get events by organizer |
| POST | `/api/events` | Create new event |
| PUT | `/api/events/{eventId}` | Update event |

### Bookings (`/api/bookings`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/bookings/customer/{customerId}` | Get customer's bookings |
| POST | `/api/bookings` | Book tickets |

### Users (`/api/users`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/{id}` | Get user by ID |
| POST | `/api/users/register` | Register new user |

## Request/Response Examples

### Create Event
```json
POST /api/events
{
  "title": "Tech Conference 2026",
  "description": "Annual technology conference",
  "location": "Convention Center, San Francisco",
  "eventDate": "2026-09-15T09:00:00",
  "totalTickets": 500,
  "organizerId": 1
}
```

### Book Tickets
```json
POST /api/bookings
{
  "eventId": 1,
  "customerId": 1,
  "numberOfTickets": 5
}
```

### Register User
```json
POST /api/users/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "role": "CUSTOMER"
}
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | roundhouse.proxy.rlwy.net | Database host |
| `DB_PORT` | 36788 | Database port |
| `DB_NAME` | railway | Database name |
| `DB_USERNAME` | postgres | Database username |
| `DB_PASSWORD` | (required) | Database password |
| `PORT` | 8080 | Server port |

## Running Locally

### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL database

### Commands
```bash
# Build
./mvnw clean package -DskipTests

# Run
./mvnw spring-boot:run

# Or with JAR
java -jar target/event.booking-0.0.1-SNAPSHOT.jar
```

## Docker

```bash
# Build image
docker build -t event-booking .

# Run container
docker run -p 8080:8080 event-booking
```

## Project Structure

```
src/main/java/com/event/booking/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/             # Request/Response DTOs
├── entity/          # JPA entities
├── exception/       # Custom exceptions & handlers
├── repository/      # JPA repositories
└── service/        # Business logic
```

## Error Handling

The API returns standardized error responses:

| Status | Description |
|--------|-------------|
| 400 | Bad Request / Validation Error |
| 404 | Resource Not Found |
| 409 | Conflict (concurrent modification) |
| 500 | Internal Server Error |

## License

MIT License
