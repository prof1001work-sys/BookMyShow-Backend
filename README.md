# BookMyShow — Production-Grade Spring Boot Modular Monolith

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Flyway](https://img.shields.io/badge/Flyway-10-red.svg)](https://flywaydb.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg)](https://www.docker.com/)
[![ArchUnit](https://img.shields.io/badge/ArchUnit-1.3.0-purple.svg)](https://www.archunit.org/)

---

## 1. Project Overview

A production-grade, highly concurrent movie ticket booking platform designed as a **Modular Monolith**. 

Rather than prematurely splitting into distributed microservices or descending into a tightly coupled layered monolith (`controller/service/repository`), this system is architected around **Domain-Driven Design (DDD)** bounded contexts. Each business module owns its domain models, application use cases, API contracts, and infrastructure adapters, as well as an isolated **PostgreSQL schema**.

---

## 2. Architecture & Module Structure

The application is structured into **13 isolated business modules** plus a **shared kernel**:

```
com.bookmyshow/
├── BookMyShowApplication.java
│
├── iam/             # Identity & Access Management (RBAC, JWT, OAuth)
│   ├── api/         # REST Controllers, Request/Response DTOs
│   ├── application/ # Application use cases & transactions
│   ├── domain/      # Domain entities, Value Objects, Repository interfaces
│   └── infrastructure/ # JPA implementations, Security adapters
│
├── master/          # Cities, regions, languages, genres, ratings
├── movie/           # Movies, formats, cast, crew, trailers
├── theatre/         # Theatres, screens, seat layouts, tiers
├── show/            # Schedules, pricing tiers, show seat inventory
├── booking/         # Booking state machine, seat reservations, history
├── payment/         # Gateways, transactions, refunds, idempotency
├── coupon/          # Promo codes, discount calculation, user limits
├── notification/    # Asynchronous email, SMS, push dispatch
├── review/          # Ratings, reviews, moderation
├── wishlist/        # User watchlists & release alerts
├── search/          # Elasticsearch synchronization & queries
├── audit/           # Audit logs & Transactional Outbox table
│
└── shared/          # Reusable cross-cutting infrastructure
    ├── audit/       # AuditableEntity base class (@Version, timestamps)
    ├── configuration/ # OpenAPI Swagger, SecurityFilterChain, WebMvc CORS
    ├── event/       # DomainEvent base contracts
    ├── exception/   # GlobalExceptionHandler, ErrorCode, BaseException
    ├── filter/      # CorrelationIdFilter (MDC tracing)
    └── response/    # ApiResponse<T> & PagedResponse<T> envelopes
```

---

## 3. Database Design: Schema-per-Module

PostgreSQL is partitioned into isolated schemas per bounded context to ensure clear data ownership and facilitate zero-downtime microservice extraction in the future:

| Schema | Purpose |
| :--- | :--- |
| `iam` | User credentials, roles, refresh tokens, audit |
| `master` | Cities, languages, genres, certifications |
| `movie` | Movies, formats, cast & crew, trailers |
| `theatre` | Theatres, screens, seat maps, amenities |
| `show` | Show schedules, pricing tiers, seat inventory |
| `booking` | Bookings, reserved seats, booking state transitions |
| `payment` | Payment transactions, idempotency ledger, refunds |
| `coupon` | Discount rules, coupon redemption history |
| `notification`| Templates, delivery logs |
| `review` | User ratings, reviews, reports |
| `wishlist` | User saved movies, notifications |
| `audit` | System audit trail & transactional outbox table |

Migrations are controlled exclusively via Flyway located in `src/main/resources/db/migration/`:
- `V1__create_iam_schema.sql`
- `V2__create_master_schema.sql`
- `V3__create_movie_schema.sql`
- `V4__create_theatre_schema.sql`
- `V5__create_show_schema.sql`
- `V6__create_booking_schema.sql`
- `V7__create_payment_schema.sql`
- `V8__create_coupon_schema.sql`
- `V9__create_notification_schema.sql`
- `V10__create_review_schema.sql`
- `V11__create_wishlist_schema.sql`
- `V12__create_audit_schema.sql`

---

## 4. Getting Started & Setup Instructions

### Prerequisites
- **Java 21** (JDK 21+)
- **Maven 3.9+**
- **Docker & Docker Compose**

### Step 1: Clone and Checkout Feature Branch
```bash
git checkout feature/phase-1-project-foundation
```

### Step 2: Start Infrastructure with Docker Compose
To launch PostgreSQL, Redis, Kafka (KRaft mode), and Elasticsearch:
```bash
docker compose up -d postgres redis kafka elasticsearch
```

### Step 3: Run the Application
```bash
# Using Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will start on `http://localhost:8080`.

### Step 4: Verify Endpoints
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **Actuator Health**: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- **Prometheus Metrics**: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)

---

## 5. Architectural Guardrails (ArchUnit)

Architecture tests in `ModularMonolithArchitectureTest.java` enforce:
1. **Module Isolation**: No module can directly access another module's `infrastructure` or repository layer.
2. **Layering Integrity**: REST controllers must reside in the `api` layer and never call domain repositories directly.
3. **Constructor Injection**: Field injection (`@Autowired` on fields) is strictly forbidden.
4. **Clean Logging**: `System.out.println` and `java.util.logging` are forbidden in favor of SLF4J MDC structured logging.

Run architecture and unit tests:
```bash
mvn test
```

---

## 6. Git Branching & Versioning Strategy

This project follows a strict Git flow structured across implementation phases:

- `main`: Production-ready releases
- `develop`: Ongoing integration branch
- `feature/phase-1-project-foundation`: Project scaffolding, Flyway, Docker, ArchUnit, Tracing (Current)
- `feature/phase-2-iam`: Authentication, JWT, Refresh Tokens, RBAC
- `feature/phase-3-master-data`: Cities, languages, genres
- `feature/phase-4-movie`: Movie lifecycle, cast, crew, trailers
- `feature/phase-5-theatre`: Theatres, screens, configurable seat layouts
- `feature/phase-6-show`: Shows, schedule conflict detection, seat inventory
- `feature/phase-7-redis-seat-locking`: High-concurrency distributed seat locking
- `feature/phase-8-booking-state-machine`: Booking lifecycle & transition validation
- `feature/phase-9-payment`: Payment gateway abstraction & idempotency
- `feature/phase-10-outbox-kafka`: Transactional outbox pattern & Kafka dispatch

---

## 7. Deep-Dive Interview Questions & Answers (Phase 1)

### Q1: Why build a Modular Monolith instead of Microservices from Day 1?
> **Answer:**
> 1. **Single Deployment & Operational Simplicity**: Avoids the operational overhead of managing distributed networks, inter-service network latency, distributed transactions (Saga/2PC), and complex service meshes during early development.
> 2. **Refactorable Boundaries**: In early product stages, domain boundaries often shift. Refactoring a boundary inside a modular monolith is a matter of moving Java packages and database schemas, whereas changing microservice boundaries requires coordinated API versioning, data migration scripts across independent databases, and contract breaking changes.
> 3. **Future Microservice Readiness**: By enforcing strict package-by-module architecture, schema-per-module, domain events, and ArchUnit rules, individual modules (like Booking or Payment) can be extracted into standalone microservices later with minimal business-logic refactoring.

### Q2: Why use Schema-per-Module instead of a Single `public` Schema?
> **Answer:**
> 1. **Data Ownership**: Each module strictly owns its tables. For example, `theatre.screens` cannot be arbitrarily altered or joined by the `booking` module.
> 2. **Physical Separation Preparedness**: When a module needs to be extracted into an independent microservice, its dedicated schema can be detached (`pg_dump -n booking`) and restored into an isolated database without untangling foreign keys from a shared public schema.
> 3. **Access Control**: Database credentials can be granted per-schema permissions (`GRANT ALL ON SCHEMA booking TO booking_user`), enforcing least-privilege security.

### Q3: How does Distributed Tracing with MDC work in Spring Boot?
> **Answer:**
> 1. **`CorrelationIdFilter`**: Intercepts every incoming HTTP request before it reaches controllers.
> 2. **Header Propagation**: Extracts `X-Correlation-ID` from request headers (or generates a new UUID) and creates a compact `traceId`.
> 3. **Thread-Local MDC**: Puts `correlationId` and `traceId` into SLF4J's `MDC` (Mapped Diagnostic Context), which binds them to the current worker thread.
> 4. **Log Enrichment**: The Logback pattern `[%X{correlationId}] [%X{traceId}]` automatically outputs the IDs on every log line emitted by services or repositories during that request.
> 5. **Clean up**: In a `finally` block, `MDC.clear()` is called to prevent context leakage across recycled Tomcat worker threads in the connection pool.

### Q4: Why forbid `@Autowired` field injection in production code?
> **Answer:**
> 1. **Immutability**: Constructor injection allows dependencies to be declared as `private final`, ensuring they cannot be modified after instantiation.
> 2. **Testability**: Classes with constructor injection can be easily instantiated in unit tests using plain `new MyService(mockRepo)` without needing reflection, Spring Runner, or `@InjectMocks`.
> 3. **Fail-Fast Detection**: Circular dependencies and missing beans fail immediately at compile time or startup time rather than producing subtle runtime `NullPointerException`s.
