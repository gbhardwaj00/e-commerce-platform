# E-Commerce Platform Backend

A secure and containerized backend for an e-commerce platform built with **Spring Boot**, **PostgreSQL**, **Flyway**, **Docker Compose**, and **Gradle**.

This project includes **JWT authentication**, **role-based authorization**, **Swagger/OpenAPI documentation**, and core e-commerce features such as product management, cart handling, and order workflows.

## Features

- User registration and login
- JWT-based authentication
- Role-based authorization
- Product management APIs
- Cart management
- Order and checkout workflows
- Pagination support
- Database migrations with Flyway
- Swagger/OpenAPI API documentation
- Fully containerized setup with Docker Compose

## Tech Stack

- Java
- Spring Boot
- Spring Security
- PostgreSQL
- Flyway
- Gradle
- Docker Compose
- Swagger / OpenAPI
- JWT Authentication

## Project Structure

    src/
    └── main/
        ├── java/com/example/ecommerceplatform/
        │   ├── auth/
        │   ├── config/
        │   ├── product/
        │   ├── cart/
        │   ├── order/
        │   ├── user/
        │   └── common/
        └── resources/
            ├── db/migration/
            └── application.yml

## Getting Started

### Prerequisites

Make sure you have installed:

- Docker Desktop
- Java 17+
- Gradle (optional if using `./gradlew`)

## Running the Project with Docker

This project is fully containerized using Docker Compose.

### 1. Clone the repository

    git clone <your-repo-url>
    cd ecommerce-platform

### 2. Start the containers

    docker compose up --build

This starts:

- PostgreSQL database
- Spring Boot application

### 3. Access the application

- App: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Docker Setup

The Docker Compose setup includes:

- `postgres` service for PostgreSQL
- `app` service for the Spring Boot backend

Flyway migrations run automatically when the application starts.

## Authentication

This project uses **JWT authentication** for securing protected endpoints.

### Auth Flow

1. User registers or logs in
2. Server validates credentials
3. JWT token is returned
4. Client sends the token in the `Authorization` header for protected requests

Example:

    Authorization: Bearer <your-jwt-token>

## API Documentation

Swagger/OpenAPI is integrated for interactive API testing.

Once the app is running, open:

`http://localhost:8080/swagger-ui/index.html`

With Swagger, you can:

- View available endpoints
- Test requests directly in the browser
- Inspect request and response models
- Authenticate and test protected APIs

## Environment Variables

The application uses environment variables for configuration.

Example values:

    DB_URL=jdbc:postgresql://postgres:5432/ecommerce
    DB_USERNAME=catalog
    DB_PASSWORD=catalog
    JWT_SECRET=your-secret-key
    JWT_EXPIRATION_MS=86400000

## Running Locally Without Docker

If you want to run the application manually instead of through Docker:

### 1. Start PostgreSQL

Make sure a PostgreSQL instance is running and create the required database.

### 2. Configure environment variables

Set database and JWT values in your local environment or `application.properties`.

### 3. Run the app

    ./gradlew bootRun

## Example Workflow

1. Register a new user
2. Log in to receive a JWT token
3. Authorize in Swagger using the token
4. Access protected endpoints
5. Create or manage products
6. Add items to cart
7. Place an order

## Security

- Passwords are securely hashed
- Protected endpoints require JWT authentication
- Authorization can restrict access by role
- Sensitive configuration is handled through environment variables

## Future Improvements

- Refresh token support
- Payment integration
- Admin dashboard
- CI/CD pipeline
- Redis
- Kafka

## Learning Goals

This project was built to practice production-style backend engineering concepts, including:

- Secure authentication and authorization
- REST API design
- Transaction handling
- Pagination
- Database migrations with Flyway
- Containerized development with Docker
- API documentation with Swagger

## Author

**Gourav Bhardwaj**
