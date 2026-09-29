# Resource Booking API

Spring Boot REST API for authenticated resource reservations, JWT authentication, and role-based access control.

## Prerequisites

- Java 17
- MySQL 8 (or another MySQL-compatible server)
- Maven (or use the included Maven Wrapper)

Create a database named `booking` before starting the application. The schema is managed with Hibernate `validate`, so database tables must already exist.

## Required environment variables

Set these variables before running the application:

| Variable | Purpose |
| --- | --- |
| `DB_URL` | JDBC URL, for example `jdbc:mysql://localhost:3306/booking?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC` |
| `DB_USERNAME` | Database account |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Base64-encoded secret of at least 256 bits for signing JWTs |
| `SEED_ADMIN_PASSWORD` | Password for the development seed administrator |
| `SEED_USER_PASSWORD` | Password for the development seed user |

The two seed passwords are required only with the `dev` profile. Seed accounts are created only in that profile.

## Run locally

Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `JWT_SECRET` in your shell. Generate a suitable JWT secret with `openssl rand -base64 32`. To seed development users, also set `SEED_ADMIN_PASSWORD` and `SEED_USER_PASSWORD`, then run:

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

On Windows, use `mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev`.

## API documentation

When the application is running, Swagger UI is available at `http://localhost:8080/swagger-ui/index.html`.

The Postman test guide is available at [`output/pdf/resource-booking-postman-test-guide.pdf`](output/pdf/resource-booking-postman-test-guide.pdf).
