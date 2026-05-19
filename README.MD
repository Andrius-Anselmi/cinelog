<div align="center">

# CineLog

### A production-grade REST API for movies, streaming platforms and categories.
### Built the right way — JWT security, layered architecture, zero shortcuts, full DTO encapsulation.

&nbsp;

[![Java](https://img.shields.io/badge/java-17_LTS-ED8B00?style=flat-square&labelColor=0a0e14&logo=openjdk&logoColor=ED8B00)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/spring_boot-3.x-6DB33F?style=flat-square&labelColor=0a0e14&logo=spring&logoColor=6DB33F)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/spring_security-JWT-6DB33F?style=flat-square&labelColor=0a0e14&logo=springsecurity&logoColor=6DB33F)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/postgresql-15+-4169E1?style=flat-square&labelColor=0a0e14&logo=postgresql&logoColor=4169E1)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/docker-compose-2496ED?style=flat-square&labelColor=0a0e14&logo=docker&logoColor=2496ED)](https://www.docker.com/)
[![Flyway](https://img.shields.io/badge/flyway-migrations-CC0200?style=flat-square&labelColor=0a0e14&logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Maven](https://img.shields.io/badge/maven-3.8+-C71A36?style=flat-square&labelColor=0a0e14&logo=apache-maven&logoColor=white)](https://maven.apache.org/)
[![Status](https://img.shields.io/badge/status-active_development-F0A500?style=flat-square&labelColor=0a0e14)](.)
[![License](https://img.shields.io/badge/license-MIT-b0e8ff?style=flat-square&labelColor=0a0e14)](./LICENSE)

&nbsp;

[Quick Start](#quick-start) · [Architecture](#architecture) · [API Reference](#api-reference) · [Security](#security) · [Design Decisions](#design-decisions) · [Roadmap](#roadmap)

&nbsp;

| | | |
|---|---|---|
| **4 layers** strictly separated | **JWT** stateless authentication | **12-Factor** compliant config |
| **Zero** domain model leakage | **Custom exceptions** & global handler | **Flyway** version-controlled schema |

</div>

---

> ⚠️ **Work in progress** — core API is stable and functional. Exception coverage, refactoring, and filtering features are actively being developed. See the [Roadmap](#roadmap) for what's coming next.

---

## The Problem

Building a movie catalog API is easy. Building one that holds up in production is not.

Most tutorials give you a `@RestController` that talks directly to the database, returns your JPA entity as JSON, and calls it a day. That works until it doesn't — until a lazy-loaded relationship triggers a thousand extra queries, until a refactor breaks the API contract, until someone runs the app on a new machine and the schema is out of sync, until there's no authentication and anyone can hit your endpoints.

CineLog is built around the decisions that prevent those failures: JWT-based stateless authentication, a strict four-layer architecture, DTOs that decouple the API contract from the domain model, centralized exception handling, Flyway for schema migrations, and environment-variable-driven config.

---

## Quick Start

**Prerequisites:** JDK 17+, Maven 3.8+, Docker

```bash
git clone https://github.com/Andrius-Anselmi/cinelog.git
cd cinelog
```

Start PostgreSQL via Docker:

```bash
docker compose up -d
```

Set environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/cinelog
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password
export CINELOG_API_SECRET=your_jwt_secret_here
```

Build and run:

```bash
mvn clean install
mvn spring-boot:run
```

API available at `http://localhost:8080`. No manual schema setup needed — Flyway runs migrations automatically on startup.

> **Note:** All endpoints except `/user/register` and `/user/login` require a valid JWT token.

---

## Architecture

Four layers. Each one knows nothing about the others except the interface it depends on.

```
  HTTP Request
       │
       ▼
┌─────────────────────────────────────────────┐
│  Security Layer                             │
│  JWT validation on every request            │
│  SecurityFilter + TokenService              │
└────────────────────┬────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│  Controller Layer                           │
│  Handles HTTP, status codes, routing        │
│  Returns DTOs — never domain models         │
└────────────────────┬────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│  Service Layer                              │
│  All business rules live here               │
│  Validates, orchestrates, transforms        │
│  Throws typed exceptions on failure         │
└────────────────────┬────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│  Repository Layer                           │
│  Spring Data JPA — optimized queries        │
│  Schema managed by Flyway migrations        │
└────────────────────┬────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────┐
│  PostgreSQL 15+ (Docker)                    │
│  Source of truth — never exposed directly   │
└─────────────────────────────────────────────┘
```

---

## Security

Authentication is stateless and JWT-based. No sessions, no cookies.

**Register** a user, **login** to receive a token, then include it in every request:

```
Authorization: Bearer <your_token>
```

The `SecurityFilter` intercepts every request, validates the token via `TokenService`, and sets the authentication context. Invalid or missing tokens result in `403 Forbidden`.

Passwords are hashed with **BCrypt** before being stored — plain-text passwords never touch the database.

```
POST /user/register  →  creates user, hashes password
POST /user/login     →  validates credentials, returns JWT
All other routes     →  require valid Bearer token
```

---

## Example Request

Creating a movie with categories and streaming platforms in a single request:

```bash
curl -X POST http://localhost:8080/cinelog/movie \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_token>" \
  -d '{
    "title": "Inception",
    "description": "A thief who steals secrets through dreams is given a chance to have his past erased.",
    "releaseDate": "16/07/2010",
    "rating": 8.8,
    "categories": [1, 2],
    "streamings": [1]
  }'
```

```json
{
  "id": 1,
  "title": "Inception",
  "description": "A thief who steals secrets through dreams is given a chance to have his past erased.",
  "releaseDate": "16/07/2010",
  "rating": 8.8,
  "categories": [
    { "id": 1, "name": "Sci-Fi" },
    { "id": 2, "name": "Thriller" }
  ],
  "streamings": [
    { "id": 1, "name": "Netflix" }
  ]
}
```

---

## API Reference

### Auth

| Method | Route | Description | Auth | Status |
|--------|-------|-------------|------|--------|
| `POST` | `/user/register` | Register a new user | ❌ | `201` |
| `POST` | `/user/login` | Authenticate and receive JWT | ❌ | `200` |

### Movies

| Method | Route | Description | Auth | Status |
|--------|-------|-------------|------|--------|
| `GET` | `/movie` | List all movies | ✅ | `200` |
| `GET` | `/movie/{id}` | Get movie by ID | ✅ | `200` / `404` |
| `GET` | `/movie/category/{id}` | Get movies by category | ✅ | `200` |
| `POST` | `/movie` | Create a movie | ✅ | `201` |
| `PUT` | `/movie/{id}` | Update a movie | ✅ | `200` / `404` |
| `DELETE` | `/movie/{id}` | Delete a movie | ✅ | `204` / `404` |

### Categories

| Method | Route | Description | Auth | Status |
|--------|-------|-------------|------|--------|
| `GET` | `/category` | List all categories | ✅ | `200` |
| `GET` | `/category/{id}` | Get category by ID | ✅ | `200` / `404` |
| `POST` | `/category` | Create a category | ✅ | `201` |
| `PUT` | `/category/{id}` | Update a category | ✅ | `200` / `404` |
| `DELETE` | `/category/{id}` | Delete a category | ✅ | `204` / `404` |

### Streaming Platforms

| Method | Route | Description | Auth | Status |
|--------|-------|-------------|------|--------|
| `GET` | `/streaming` | List all platforms | ✅ | `200` |
| `GET` | `/streaming/{id}` | Get platform by ID | ✅ | `200` / `404` |
| `POST` | `/streaming` | Create a platform | ✅ | `201` |
| `PUT` | `/streaming/{id}` | Update a platform | ✅ | `200` / `404` |
| `DELETE` | `/streaming/{id}` | Delete a platform | ✅ | `204` / `404` |

---

## Exception Handling

Errors are handled globally via `GlobalExceptionHandler`. No try/catch blocks scattered across controllers.

| Exception | HTTP Status | When |
|-----------|-------------|------|
| `NotFoundException` | `404 Not Found` | Resource not found by ID |
| `ValidationException` | `400 Bad Request` | Invalid or missing request fields |

Error messages are centralized in `ExceptionMessages` — one constant per message, changed in one place.

> ⚠️ Exception coverage is actively being expanded. More typed exceptions are on the way.

---

## Design Decisions

Every pattern here is a deliberate choice, not boilerplate.

**JWT Authentication** — Stateless token-based security. No server-side session state. Every request is self-contained and independently verifiable. Scales horizontally without coordination.

**DTO encapsulation** — JPA entities are never serialized into API responses. DTOs give the API a stable contract independent of the database schema. Rename a column, change a relationship — the client never knows.

**Record-based DTOs** — `MovieRequest` and `MovieResponse` are Java Records. Immutable by design, concise by nature, and perfect for data carriers that should never change after construction.

**Mapper pattern** — Conversion between entities and DTOs lives in dedicated `@UtilityClass` mappers. Controllers and services stay clean. Transformation logic stays testable and isolated.

**Global exception handling** — `@RestControllerAdvice` intercepts typed exceptions and maps them to HTTP responses. Controllers and services stay clean. Error format is consistent across the entire API.

**Bean Validation** — `@Valid` annotations enforce constraints at the controller boundary. Bad data is rejected before it reaches the service layer.

**Flyway migrations** — Schema changes are version-controlled SQL files. Every environment runs the exact same migrations in the exact same order. No more schema drift between machines.

**12-Factor config** — Database URL, credentials, and JWT secret come from environment variables. The same artifact runs in any environment without modification.

**ManyToMany via join tables** — `movie_category` and `movie_streaming` are explicit join tables with named foreign key constraints — not Hibernate-generated defaults. Full control over the schema.

**Timestamps by Hibernate** — `createdAt` and `updatedAt` are managed automatically via `@CreationTimestamp` and `@UpdateTimestamp`. No manual date handling anywhere.

---

## Database Schema

```
┌───────────────┐       ┌──────────────────┐       ┌────────────┐
│     movie     │       │  movie_category  │       │  category  │
│───────────────│       │──────────────────│       │────────────│
│ id            │──────▶│ movie_id         │◀──────│ id         │
│ title         │       │ category_id      │       │ name       │
│ description   │       └──────────────────┘       └────────────┘
│ release_date  │
│ rating        │       ┌──────────────────┐       ┌────────────┐
│ created_at    │       │ movie_streaming  │       │ streaming  │
│ updated_at    │──────▶│──────────────────│◀──────│────────────│
└───────────────┘       │ movie_id         │       │ id         │
                        │ streaming_id     │       │ name       │
                        └──────────────────┘       └────────────┘

┌──────────────┐
│    users     │
│──────────────│
│ id           │
│ name         │
│ email        │
│ password     │
└──────────────┘
```

---

## Roadmap

Features actively being developed:

- [ ] Expand custom exception coverage across all error scenarios
- [ ] Refactor service and controller layers
- [ ] Filter movies by category, streaming platform and rating
- [ ] Pagination and sorting on list endpoints
- [ ] Additional query endpoints (e.g. movies by streaming platform)

---

## Tech Stack

| Component | Technology | Why |
|-----------|-----------|-----|
| Language | Java 17 LTS | Records, pattern matching, long-term support |
| Framework | Spring Boot 3.x | Industry standard, powerful DI ecosystem |
| Security | Spring Security + JWT | Stateless, scalable authentication |
| Validation | Spring Bean Validation | Declarative constraints at the boundary |
| Persistence | Spring Data JPA + Hibernate | Clean abstraction over JDBC |
| Database | PostgreSQL 15+ | Reliable, production-proven |
| Container | Docker + Docker Compose | Reproducible environments |
| Migrations | Flyway | Version-controlled schema |
| Build | Maven 3.8+ | Predictable lifecycle |

---

## Project Structure

```
src/
└── main/
    └── java/
        └── dev/java/cinelog/
            ├── config/         # SecurityConfig, SecurityFilter, JWTuserData
            ├── controller/     # HTTP layer — routes and status codes
            ├── service/        # Business logic — rules and validation
            ├── repository/     # Data access — JPA queries
            ├── entity/         # Domain entities — source of truth
            ├── mapper/         # DTO transformation
            ├── request/        # Inbound DTOs
            ├── response/       # Outbound DTOs
            └── exception/      # Custom exceptions + GlobalExceptionHandler
    └── resources/
        └── db/migration/       # Flyway SQL migrations (V1__, V2__...)
```

---

## Contributing

Issues and PRs welcome. If you're adding a feature, open the service layer first — business logic belongs there, not in controllers or repositories.

## License

MIT — see [LICENSE](./LICENSE).

---

<div align="center">

Built by [Andrius Anselmi](https://github.com/Andrius-Anselmi) · [LinkedIn](https://www.linkedin.com/in/andrius-anselmi)

</div>