# Hotel Booking Platform

A full-stack hotel booking web application built as a thesis project. The platform supports three user roles — guests, hotel owners, and administrators — and handles the full flow from hotel registration to room reservation.

## Tech Stack

**Backend**
- Java 8 / Spring Boot 2.6.7
- Spring Security with stateless JWT authentication
- Spring Data JPA / Hibernate (ORM)
- MySQL 8
- Cloudinary (hotel image storage)
- Maven

**Frontend**
- Angular 13
- TypeScript
- Bootstrap

**Infrastructure**
- Docker & Docker Compose (3-service setup: MySQL + Spring Boot + nginx)
- nginx as reverse proxy for the Angular SPA

## Features

**Authentication & Authorization**
- JWT-based stateless authentication
- Role-based access control (RBAC) with three roles: `ROLE_USER`, `ROLE_MODERATOR`, `ROLE_ADMIN`
- Passwords hashed with BCrypt
- Angular HTTP Interceptor automatically attaches Bearer token to every request

**Hotel Owners (Moderators)**
- Register a hotel with full details (name, location, services)
- Upload hotel images (stored on Cloudinary CDN)
- Set room types, pricing, and availability
- View reservation dashboard

**Guests (Users)**
- Browse all registered hotels
- Check room availability by date range
- Make reservations

**Admin**
- View all registered users
- Delete users

## Project Structure

```
hotel-booking-platform/
├── hotel-booking-back-end-spring-boot/   # Spring Boot API
│   └── src/main/java/com/thesis/backend/
│       ├── controllers/                  # REST endpoints
│       ├── models/                       # JPA entities (User, Hotel, Reservation...)
│       ├── repository/                   # Spring Data JPA repositories
│       ├── security/                     # JWT filter chain, Spring Security config
│       │   ├── jwt/                      # JwtUtils, AuthTokenFilter
│       │   └── services/                 # UserDetailsImpl, UserDetailsServiceImpl
│       └── payload/                      # DTOs (request/response)
│
├── hotel-booking-front-end-angular/      # Angular 13 SPA
│   └── src/app/
│       ├── _services/                    # AuthService, TokenStorageService, UserService
│       ├── _helpers/                     # AuthInterceptor
│       └── [feature components]/         # login, register, hotels-board, reservation...
│
└── docker-compose.yml                    # Runs all 3 services together
```

## Running Locally with Docker

**Prerequisites:** Docker Desktop installed and running.

**1. Clone the repository**
```bash
git clone https://github.com/your-username/hotel-booking-platform.git
cd hotel-booking-platform
```

**2. Create a `.env` file** in the project root:
```env
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=bookingapp
MYSQL_USER=bookingApp
MYSQL_PASSWORD=bookingApp
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

**3. Start all services**
```bash
docker compose up --build
```

The app will be available at **http://localhost**

| Service     | URL                     |
|-------------|-------------------------|
| Frontend    | http://localhost        |
| Backend API | http://localhost/api    |
| MySQL       | localhost:3307          |

## Running Locally without Docker

**Backend**
```bash
cd hotel-booking-back-end-spring-boot
# Configure src/main/resources/application.properties with your local MySQL
mvn spring-boot:run
# Runs on http://localhost:8080
```

**Frontend**
```bash
cd hotel-booking-front-end-angular
npm install
npm start
# Runs on http://localhost:4200
# proxy.conf.json forwards /api calls to localhost:8080
```

## API Endpoints

| Method | Endpoint                  | Auth       | Description            |
|--------|---------------------------|------------|------------------------|
| POST   | `/api/auth/signin`        | Public     | Login, returns JWT     |
| POST   | `/api/auth/signup`        | Public     | Register new user      |
| GET    | `/api/hotel/hotels`       | Public     | List all hotels        |
| GET    | `/api/hotel/{id}`         | Public     | Get hotel details      |
| POST   | `/api/hotel/`             | MODERATOR  | Register a hotel       |
| POST   | `/api/hotel/uploadImage`  | MODERATOR  | Upload hotel image     |
| GET    | `/api/admin/users`        | ADMIN      | List all users         |
| DELETE | `/api/admin/users/{id}`   | ADMIN      | Delete a user          |

## Security Architecture

Authentication flow:

```
1. POST /api/auth/signin  →  Spring validates credentials
2. Spring returns JWT token  →  Angular stores in sessionStorage
3. Every subsequent request  →  AuthInterceptor adds "Authorization: Bearer <token>"
4. Spring's AuthTokenFilter validates the token on every protected endpoint
5. @PreAuthorize("hasRole('ADMIN')") enforces method-level access control
```

## Database

Spring Boot automatically creates all tables on first run (`ddl-auto=update`). No manual schema setup needed.

Seed the `roles` table manually after first run:
```sql
INSERT INTO roles(name) VALUES('ROLE_USER');
INSERT INTO roles(name) VALUES('ROLE_MODERATOR');
INSERT INTO roles(name) VALUES('ROLE_ADMIN');
```
