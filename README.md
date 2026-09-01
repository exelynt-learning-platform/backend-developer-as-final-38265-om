# backend-developer-as-final-38265-om
# Resource Booking API

A Spring Boot REST API for managing bookable resources and reservations, with JWT-based authentication and role-based access control (ADMIN / USER).

## Tech Stack

- **Java 17**
- **Spring Boot 4.1.1**
  - Spring Web (MVC)
  - Spring Data JPA
  - Spring Security
  - Spring Validation
- **MySQL 8+** (runtime datastore)
- **H2** (in-memory database for tests)
- **JWT** (`io.jsonwebtoken` / jjwt 0.11.5) for stateless authentication
- **MapStruct 1.5.5** for DTO ↔ entity mapping
- **SpringDoc OpenAPI 3** for Swagger UI / API docs
- **Maven** (with Maven Wrapper included)

## Features

- User authentication via JWT (login endpoint issues a bearer token)
- Role-based authorization (`ADMIN`, `USER`)
- CRUD operations on resources (admin-managed)
- Reservation creation and management, scoped to the authenticated user
- Filtering, sorting, and pagination on reservation listings
- Interactive API documentation via Swagger UI

## Requirements

- Java 17 or later
- Maven (or use the included wrapper — no local Maven install needed)
- MySQL 8+ running locally, or a compatible MySQL server accessible over the network

## Getting Started

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd Booking
```

### 2. Set up the database

Create a MySQL database and user (if the configured user doesn't already have permission to create `booking_db` automatically):

```sql
CREATE DATABASE booking_db;
CREATE USER 'booking_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON booking_db.* TO 'booking_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configure environment variables

Set these environment variables as needed before starting the application. All have sensible defaults for local development except where noted.

| Variable | Default | Purpose |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://localhost:3306/booking_db?...` | MySQL JDBC URL |
| `DB_USERNAME` | `root` | Database user |
| `DB_PASSWORD` | empty | Database password |
| `JPA_DDL_AUTO` | `update` | Hibernate schema mode (`none`, `validate`, `update`, `create`, `create-drop`) |
| `JWT_SECRET` | development value | Base64-encoded JWT signing key — **override this in any non-local environment** |

**Linux / macOS:**
```bash
export DB_URL="jdbc:mysql://localhost:3306/booking_db"
export DB_USERNAME="booking_user"
export DB_PASSWORD="your_password"
export JWT_SECRET="your-base64-secret"
```

**Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/booking_db"
$env:DB_USERNAME="booking_user"
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="your-base64-secret"
```

### 4. Build and run

Using the Maven Wrapper (recommended — no local Maven install required):

```bash
./mvnw spring-boot:run       # Linux / macOS
mvnw.cmd spring-boot:run     # Windows
```

Or with a local Maven install:

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080` by default.

### 5. Explore the API

Once running, Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

## Seed Credentials

The application seeds two default accounts on startup (passwords are BCrypt-encoded):

| Role | Login username | Password |
| --- | --- | --- |
| ADMIN | `admin` | `Admin@123` |
| USER | `user` | `User@123` |

Obtain a JWT by calling `POST /auth/login` with your `username` and `password`, then include it on subsequent requests as:

```
Authorization: Bearer <token>
```

## API Endpoints

### Auth
| Method | Endpoint | Access |
| --- | --- | --- |
| POST | `/auth/login` | Public |

### Resources
| Method | Endpoint | Access |
| --- | --- | --- |
| GET | `/resources` | ADMIN, USER |
| GET | `/resources/{id}` | ADMIN, USER |
| POST | `/resources` | ADMIN |
| PUT | `/resources/{id}` | ADMIN |
| DELETE | `/resources/{id}` | ADMIN |

### Reservations
| Method | Endpoint | Access |
| --- | --- | --- |
| POST | `/reservations` | ADMIN, USER (user is always taken from the token) |
| GET | `/reservations` | ADMIN (sees all), USER (sees only owned reservations) |
| GET | `/reservations/{id}` | ADMIN (any), USER (own only) |
| PUT | `/reservations/{id}` | ADMIN |
| DELETE | `/reservations/{id}` | ADMIN |

**Query parameters for `GET /reservations`:**

| Parameter | Type | Description |
| --- | --- | --- |
| `status` | string | Filter by reservation status |
| `minPrice` | number | Minimum price filter |
| `maxPrice` | number | Maximum price filter |
| `page` | integer | Page number (0-indexed) |
| `size` | integer | Page size |
| `sortBy` | string | Field to sort by |
| `sortDirection` | string | `asc` or `desc` |

Example:
```
GET /reservations?status=CONFIRMED&minPrice=10&maxPrice=100&page=0&size=20&sortBy=startTime&sortDirection=desc
```

## Running Tests

```bash
./mvnw test
```

Tests run against an in-memory **H2** database, so no MySQL instance is required to run the test suite.

## Building a Production JAR

```bash
./mvnw clean package
java -jar target/Booking-0.0.1-SNAPSHOT.jar
```

## Building a Container Image

Spring Boot's Maven plugin can build an OCI image directly (no separate Dockerfile required):

```bash
./mvnw spring-boot:build-image
```

## Project Structure

```
Booking/
├── src/
│   ├── main/
│   │   ├── java/          # Application source (controllers, services, repositories, security, DTOs)
│   │   └── resources/     # application.properties/yml, static assets
│   └── test/               # Unit and integration tests (H2-backed)
├── mvnw, mvnw.cmd          # Maven Wrapper scripts
├── pom.xml                 # Project dependencies and build configuration
└── README.md
```

## Notes on Maven Parent Overrides

This project inherits from `spring-boot-starter-parent`. The POM contains empty overrides for elements like `<license>` and `<developers>` to prevent unwanted inheritance from the parent POM. If you switch to a different parent and want that inheritance, remove those overrides.

## License

Add your license of choice here (e.g., MIT, Apache 2.0), or state that the project is unlicensed/private.

## Contributing

Pull requests are welcome. For significant changes, please open an issue first to discuss what you'd like to change.
