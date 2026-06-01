# 🏨 BookLink

A hotel booking platform built as a **Spring Boot microservices** system with an **Angular** frontend, fronted by an API gateway and deployable via Docker Compose or Kubernetes (EKS).

## 🧱 Architecture

```
                         ┌──────────────┐
                         │  Angular SPA │  :4200
                         └──────┬───────┘
                                │
                         ┌──────▼───────┐
                         │ api-gateway  │  :8080   (JWT auth, routing, CORS)
                         └──────┬───────┘
          ┌─────────────────────┼─────────────────────┐
          ▼                     ▼                     ▼
   ┌────────────┐       ┌──────────────┐      ┌────────────────┐
   │user-service│ :8081 │ hotel-service│ :8082│ booking-service│ :8083
   └─────┬──────┘       └──────┬───────┘      └───────┬────────┘
         │                     │  ▲                    │
     ┌───▼───┐            ┌────▼──┐ │ Redis        ┌───▼────┐
     │db-users│           │db-hotels│ cache        │db-bookings│
     └───────┘            └────────┘               └──────────┘

   config-server :8888  →  centralized configuration for all services
   booking-service  ──(OpenFeign + fallback)──▶  hotel-service
```

Each service owns its own PostgreSQL database (**database-per-service**). The `config-server` provides centralized configuration; `hotel-service` uses **Redis** for caching; `booking-service` calls `hotel-service` over **OpenFeign** with a circuit-breaker fallback.

## 📦 Modules

| Module | Port | Responsibility |
|---|---|---|
| `config-server` | 8888 | Spring Cloud Config — centralized configuration |
| `api-gateway` | 8080 | Spring Cloud Gateway — single entry point, JWT validation, routing, CORS |
| `user-service` | 8081 | Authentication & user management (issues JWTs) |
| `hotel-service` | 8082 | Hotels, rooms, amenities, reviews (Redis-cached) |
| `booking-service` | 8083 | Bookings; calls hotel-service via Feign |
| `booklink-frontend` | 4200 | Angular SPA (served via nginx) |
| `booklink-backend` | — | Legacy monolith (kept for reference; not part of the microservices build) |

## 🛠️ Tech Stack

- **Java 17**, **Spring Boot 3.4**, **Spring Cloud 2024.0**
- **Spring Cloud Gateway**, **Spring Cloud Config**, **OpenFeign**
- **Spring Security** + **JWT** (jjwt)
- **Spring Data JPA**, **PostgreSQL**, **Flyway** migrations
- **MapStruct**, **Lombok**
- **Redis** (caching)
- **Angular** (frontend), **nginx**
- **JUnit 5 & Mockito**
- **Docker / Docker Compose**, **Kubernetes (EKS)**
- **Prometheus & Grafana** (monitoring)

## 🚀 Running locally (Docker Compose)

Brings up databases, Redis, all services, the frontend, and monitoring:

```sh
docker compose up --build
```

| Service | URL |
|---|---|
| Frontend | http://localhost:4200 |
| API Gateway | http://localhost:8080 |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 (admin / admin) |

> ⚠️ The compose file ships with **development credentials and a sample JWT secret**. Override `JWT_SECRET`, database passwords, and other secrets before any real deployment.

## 🧩 Running services individually

Build all microservices:

```sh
mvn package
```

Run a single service (config-server first, since the others import config from it):

```sh
cd config-server && ./mvnw spring-boot:run
# then, in separate terminals:
cd user-service && ./mvnw spring-boot:run
cd hotel-service && ./mvnw spring-boot:run
cd booking-service && ./mvnw spring-boot:run
cd api-gateway && ./mvnw spring-boot:run
```

Frontend:

```sh
cd booklink-frontend
npm install
npm start        # http://localhost:4200
```

## ✅ Tests

```sh
mvn test                       # microservices
cd booklink-backend && ./mvnw test   # legacy monolith
```

## ☸️ Kubernetes / EKS

Manifests live under `k8s/` (namespace, secrets, per-service deployments, Redis, monitoring). The GitHub Actions pipeline in `.github/workflows/ci-cd.yml` builds and tests on every push, then on `main` builds Docker images, pushes them to **Amazon ECR**, and deploys to an **EKS** cluster.

```sh
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/secrets.yaml
kubectl apply -f k8s/
```

## 🔐 API routing & auth

- `/api/auth/**` — public (login / register)
- `/api/hotels/**`, `/api/rooms/**`, `/api/amenities/**`, `/api/reviews/**` — **GET** public; writes require a JWT
- `/api/users/**` — requires a JWT (admin)
- `/api/bookings/**` — requires a JWT

The gateway validates the JWT and forwards the request; each service also re-validates the token independently.
