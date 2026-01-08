# HACCP Audit Platform

HACCP (Hazard Analysis and Critical Control Points) Audit Management Platform - A comprehensive audit management system built with Spring Boot 3.2.x, Java 17, and PostgreSQL.

## Project Structure

```
/
  backend/              # Spring Boot application
  selenium-tests/       # Python Selenium E2E tests
  docker-compose.yml    # Docker Compose configuration
  Jenkinsfile          # CI/CD pipeline
  README.md
  .gitignore
  .env.example
```

## Prerequisites

- Java 17
- Maven 3.9+
- Docker & Docker Compose
- PostgreSQL 16 (or use Docker)
- Python 3.11 (for local Selenium tests)

## Quick Start

### Using Docker Compose (Recommended)

```powershell
# Start all services
docker compose up --build

# Run Selenium tests
docker compose run --rm selenium pytest -k test_01_login_and_dashboard
```

### Local Development

#### Backend Setup

```powershell
# Navigate to backend
cd backend

# Run unit tests
.\mvnw.cmd test

# Run integration tests
.\mvnw.cmd verify -Pintegration

# Start the application
.\mvnw.cmd spring-boot:run
```

**Note:** On Windows, use `.\mvnw.cmd` instead of `./mvnw`.

#### Database Setup

Set environment variables or use `.env` file:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/haccp_audit"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="postgres"
```

#### Selenium Tests

```powershell
# Install dependencies
cd selenium-tests
pip install -r requirements.txt

# Run tests (requires backend and chrome containers running)
pytest -k test_01_login_and_dashboard
```

## Default Users

The following users are seeded via Flyway migrations:

| Username | Password | Role |
|----------|----------|------|
| admin | Admin123! | ADMIN |
| auditor | Auditor123! | AUDITOR |
| manager | Manager123! | MANAGER |

## Features

### Admin
- Manage users and roles

### Auditor
- Create and manage facilities
- Create audits (auto-generates checklist responses from active template)
- Fill checklist (PASS/FAIL/NA + comments)
- Complete audits
- Create Non-Conformities for FAIL items

### Manager
- View Non-Conformities
- Update NC status
- Create Corrective Actions
- Mark Corrective Actions as DONE
- Close Non-Conformities (only if at least one CA is DONE)

### Reports
- View audit reports with checklist results, NCs, and CAPAs
- Export reports to CSV

## Testing

### Unit Tests
```powershell
cd backend
.\mvnw.cmd test
```

### Integration Tests
```powershell
cd backend
.\mvnw.cmd verify -Pintegration
```

Integration tests use Testcontainers with PostgreSQL and only run with the `-Pintegration` profile.

### Selenium E2E Tests

Three test scenarios:

1. **test_01_login_and_dashboard**: Auditor login → dashboard visible
2. **test_02_create_facility_and_audit**: Create facility → create audit → audit detail opens
3. **test_03_fill_checklist_and_create_nc_and_capa**: Full workflow with NC and CAPA

Run with Docker Compose:
```powershell
docker compose run --rm selenium pytest -k test_01_login_and_dashboard
```

## CI/CD

The Jenkinsfile defines the following stages:

1. **Checkout (GitHub)**: Clone repository
2. **Build**: Compile backend
3. **Unit Tests**: Run unit tests, publish JUnit reports
4. **Integration Tests**: Run integration tests with `-Pintegration`, publish JUnit reports
5. **Run on Docker Containers**: Start all services, wait for backend health
6. **6.1-Selenium Scenario 1**: Run login/dashboard test
7. **6.2-Selenium Scenario 2**: Run facility/audit creation test
8. **6.3-Selenium Scenario 3**: Run full workflow test

All stages publish JUnit XML reports and archive Selenium test reports.

## Ports

- Backend: 8080
- PostgreSQL: 5432
- Selenium Chrome: 4444

## Health Check

Backend health endpoint:
```
GET http://localhost:8080/actuator/health
```

## Database Migrations

Flyway migrations are located in `backend/src/main/resources/db/migration/`:

- `V1__create_schema.sql`: Creates all tables
- `V2__seed_data.sql`: Seeds roles, users, facility, template, and checklist items

## Technology Stack

- **Backend**: Spring Boot 3.2.0, Java 17, Spring Security, Thymeleaf, JPA/Hibernate, Flyway
- **Database**: PostgreSQL 16
- **Testing**: JUnit 5, Mockito, Testcontainers, Selenium, pytest
- **CI/CD**: Jenkins, Docker, Docker Compose

## License

This project is for educational purposes.
