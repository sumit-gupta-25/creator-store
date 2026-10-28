# CreatorStore

A RESTful backend application for managing products, user authentication and customer orders, built with **Java, Spring Boot, Spring Security, JWT and PostgreSQL**.

## Overview

CreatorStore provides backend APIs for product management, user registration and login, and order processing. It uses Spring Data JPA for database operations, JWT-based authentication for securing endpoints and role-based authorization for administrative product management.

## Features

- **User authentication:** Register and log in using email and password.
- **JWT security:** Stateless authentication using signed JSON Web Tokens.
- **Role-based access control:** Separate `USER` and `ADMIN` roles.
- **Product management:** Create, retrieve, update and delete products.
- **Pagination and sorting:** Retrieve products and orders in manageable pages.
- **Order processing:** Create orders containing multiple products.
- **Inventory management:** Validate available stock and decrease inventory when an order is created.
- **Purchase price tracking:** Store each item's price at the time of purchase.
- **Request validation:** Validate incoming request data using Jakarta Bean Validation.
- **Centralized exception handling:** Return structured error responses for common application errors.
- **API documentation:** Explore endpoints using Swagger UI.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 4.1.1 | Application framework |
| Spring Web MVC | REST API development |
| Spring Security | Authentication and authorization |
| Spring Security OAuth2 Resource Server | JWT authentication |
| Spring Data JPA | Database access |
| Hibernate | ORM and entity mapping |
| PostgreSQL | Relational database |
| Maven | Dependency and build management |
| Jakarta Bean Validation | Request validation |
| Lombok | Reducing boilerplate code |
| Springdoc OpenAPI | API documentation |

## Architecture

The application follows a layered architecture:

```text
Client / API Consumer
        |
        v
    Controllers
        |
        v
     Services
        |
        v
   Repositories
        |
        v
    PostgreSQL
```

- **Controllers:** Handle HTTP requests and responses.
- **Services:** Implement authentication, product management and order-processing logic.
- **Repositories:** Access database entities through Spring Data JPA.
- **Entities:** Represent users, products, orders and order items.
- **DTOs:** Define request and response structures.
- **Security configuration:** Defines JWT authentication and endpoint access rules.
- **Exception handler:** Provides consistent API error responses.

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/sumit-gupta-25/creator-store.git
cd creator-store
```

### 2. Create the database

```sql
CREATE DATABASE "CreatorStore";
```

### 3. Configure environment variables

Set the following variables before running the application:

```text
DATABASE_URL=jdbc:postgresql://localhost:5432/CreatorStore
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_postgresql_password
JWT_SECRET=your_base64_encoded_secret
```

Generate a secure JWT secret using:

```bash
openssl rand -base64 32
```

### 4. Run the application

```bash
mvn spring-boot:run
```

The API runs at `http://localhost:8080`.

## API Endpoints

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| GET | `/api/products` | Public |
| GET | `/api/products/{id}` | Public |
| POST | `/api/products` | Admin |
| PUT | `/api/products/{id}` | Admin |
| DELETE | `/api/products/{id}` | Admin |
| POST | `/api/orders` | Authenticated |
| GET | `/api/orders` | Authenticated |
| GET | `/api/orders/{orderId}` | Authenticated |

Protected endpoints require a JWT token in the `Authorization: Bearer <token>` header.

## API Documentation

Once the application is running, explore the APIs through [Swagger UI](http://localhost:8080/swagger-ui/index.html).

## Author

**Sumit Gupta**

- GitHub: [sumit-gupta-25](https://github.com/sumit-gupta-25)
- Repository: [CreatorStore](https://github.com/sumit-gupta-25/creator-store)
