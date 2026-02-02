# E-Commerce Clothes Platform - Microservices Architecture

A modern, scalable e-commerce platform for clothing built with Spring Boot microservices architecture.

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Services](#services)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Development](#development)
- [Testing](#testing)
- [Deployment](#deployment)
- [Monitoring](#monitoring)
- [Contributing](#contributing)

## 🎯 Overview

This is a comprehensive e-commerce platform designed for clothing retail, implementing microservices architecture for scalability, maintainability, and high availability. The platform provides features including user authentication, product catalog, shopping cart, order management, payment processing, reviews, and personalized recommendations.

## 🏗️ Architecture

### Microservices

1. **API Gateway** (Port: 8080) - Entry point for all client requests
2. **Service Registry** (Port: 8761) - Service discovery using Eureka
3. **Config Server** (Port: 8888) - Centralized configuration management
4. **User Service** (Port: 8081) - User authentication and profile management
5. **Product Catalog Service** (Port: 8082) - Product management with MongoDB
6. **Shopping Cart Service** (Port: 8086) - Cart management with Redis caching
7. **Order Service** (Port: 8083) - Order processing and management
8. **Payment Service** (Port: 8084) - Payment processing
9. **Notification Service** (Port: 8085) - Email and SMS notifications
10. **Review Service** (Port: 8087) - Product reviews and ratings
11. **Recommendation Service** (Port: 8088) - Personalized product recommendations

### Architecture Diagram

```
┌─────────────┐
│   Clients   │
└──────┬──────┘
       │
       ▼
┌─────────────────┐
│  API Gateway    │ :8080
└────────┬────────┘
         │
    ┌────┴────┐
    │ Eureka  │ :8761
    │Registry │
    └────┬────┘
         │
    ┌────┴────────────────────────────────┐
    │                                     │
    ▼                                     ▼
┌─────────────┐                    ┌─────────────┐
│User Service │ :8081              │Product Svc  │ :8082
│(PostgreSQL) │                    │(MongoDB)    │
└─────────────┘                    └─────────────┘
    │                                     │
    ▼                                     ▼
┌─────────────┐                    ┌─────────────┐
│Cart Service │ :8086              │Order Svc    │ :8083
│(Redis)      │                    │(PostgreSQL) │
└─────────────┘                    └─────────────┘
```

## 🛠️ Technology Stack

### Core Technologies

- **Java**: 17 LTS
- **Spring Boot**: 3.2.2
- **Spring Cloud**: 2023.0.0
- **Maven**: 3.9+

### Databases

- **PostgreSQL**: 15.x (User, Order, Payment, Cart)
- **MongoDB**: 6.x (Product Catalog, Reviews, Recommendations)
- **Redis**: 7.x (Caching and session storage)

### Spring Cloud Components

- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- Spring Cloud Config
- Spring Cloud OpenFeign
- Spring Cloud Circuit Breaker (Resilience4j)

### Additional Tools

- **JWT**: Authentication
- **Lombok**: Reduce boilerplate
- **MapStruct**: Object mapping
- **Swagger/OpenAPI**: API documentation
- **Docker**: Containerization
- **Kafka**: Message queue

## 📦 Prerequisites

Before running this project, ensure you have:

- **Java 17 JDK** - [Download](https://adoptium.net/)
- **Maven 3.9+** - [Download](https://maven.apache.org/download.cgi)
- **Docker Desktop** - [Download](https://www.docker.com/products/docker-desktop)
- **Git** - [Download](https://git-scm.com/)
- **IDE** - IntelliJ IDEA, Eclipse, or VS Code

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd ecormerce_clothes
```

### 2. Start Infrastructure Services

Start PostgreSQL, MongoDB, Redis, and Kafka using Docker Compose:

```bash
docker-compose up -d
```

Verify all containers are running:

```bash
docker-compose ps
```

### 3. Build All Services

Build the entire project from the root directory:

```bash
mvn clean install
```

### 4. Start Services in Order

Start services in the following sequence:

#### Step 1: Start Service Registry (Eureka)

```bash
cd service-registry
mvn spring-boot:run
```

Wait for Eureka to start completely (check http://localhost:8761)

#### Step 2: Start Config Server

```bash
cd config-server
mvn spring-boot:run
```

#### Step 3: Start API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

#### Step 4: Start Microservices

In separate terminal windows, start each service:

```bash
# User Service
cd user-service
mvn spring-boot:run

# Product Catalog Service
cd product-catalog-service
mvn spring-boot:run

# Shopping Cart Service
cd shopping-cart-service
mvn spring-boot:run

# Order Service
cd order-service
mvn spring-boot:run

# Payment Service
cd payment-service
mvn spring-boot:run

# Notification Service
cd notification-service
mvn spring-boot:run

# Review Service
cd review-service
mvn spring-boot:run

# Recommendation Service
cd recommendation-service
mvn spring-boot:run
```

## 📡 Services

### Service Registry (Eureka)

- **URL**: http://localhost:8761
- **Purpose**: Service discovery and registration
- **Dashboard**: Access the Eureka dashboard to see all registered services

### API Gateway

- **URL**: http://localhost:8080
- **Purpose**: Single entry point for all client requests
- **Routes**:
  - `/api/users/**` → User Service
  - `/api/products/**` → Product Catalog Service
  - `/api/cart/**` → Shopping Cart Service
  - `/api/orders/**` → Order Service
  - `/api/payments/**` → Payment Service
  - `/api/reviews/**` → Review Service
  - `/api/recommendations/**` → Recommendation Service

### User Service

- **Port**: 8081
- **Database**: PostgreSQL (user_db)
- **Endpoints**:
  - `POST /auth/register` - Register new user
  - `POST /auth/login` - User login
  - `GET /users/{id}` - Get user by ID
  - `PUT /users/{id}` - Update user
  - `DELETE /users/{id}` - Delete user

### Product Catalog Service

- **Port**: 8082
- **Database**: MongoDB (product_db)
- **Endpoints**:
  - `POST /products` - Create product
  - `GET /products` - Get all products
  - `GET /products/{id}` - Get product by ID
  - `GET /products/category/{category}` - Get products by category
  - `GET /products/search?keyword=` - Search products
  - `PUT /products/{id}` - Update product
  - `DELETE /products/{id}` - Delete product

## 📚 API Documentation

Each service provides Swagger UI documentation:

- User Service: http://localhost:8081/swagger-ui.html
- Product Catalog Service: http://localhost:8082/swagger-ui.html
- (Add other services as needed)

## ⚙️ Configuration

### Environment Variables

Create `.env` file in the root directory:

```env
# Database Configuration
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

MONGODB_HOST=localhost
MONGODB_PORT=27017

REDIS_HOST=localhost
REDIS_PORT=6379

# JWT Configuration
JWT_SECRET=your-secret-key-should-be-at-least-256-bits-long
JWT_EXPIRATION=86400000

# Email Configuration (for Notification Service)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-password
```

### Application Profiles

Each service supports multiple profiles:

- `dev` - Development environment
- `test` - Testing environment
- `prod` - Production environment

Run with specific profile:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🔧 Development

### Project Structure

```
ecormerce-clothes/
├── common-lib/                 # Shared utilities and DTOs
├── service-registry/          # Eureka Server
├── config-server/             # Configuration Server
├── api-gateway/               # API Gateway
├── user-service/              # User management
├── product-catalog-service/   # Product management
├── shopping-cart-service/     # Shopping cart
├── order-service/             # Order management
├── payment-service/           # Payment processing
├── notification-service/      # Notifications
├── review-service/            # Reviews and ratings
├── recommendation-service/    # Recommendations
├── docker-compose.yml         # Infrastructure services
├── pom.xml                    # Parent POM
└── README.md                  # This file
```

### Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Keep methods small and focused (< 20 lines)
- Write self-documenting code
- Add comments for complex logic

### Git Workflow

```bash
# Create feature branch
git checkout -b feature/your-feature-name

# Make changes and commit
git add .
git commit -m "[SERVICE-NAME] Description of changes"

# Push to remote
git push origin feature/your-feature-name

# Create Pull Request
```

## 🧪 Testing

### Run Unit Tests

```bash
# Test all services
mvn test

# Test specific service
cd user-service
mvn test
```

### Run Integration Tests

```bash
mvn verify
```

### Test Coverage

```bash
mvn jacoco:report
```

## 🚢 Deployment

### Docker Deployment

Build Docker images for each service:

```bash
# Example for user-service
cd user-service
docker build -t ecommerce/user-service:1.0 .
```

### Kubernetes Deployment

Create Kubernetes deployment files (TODO)

## 📊 Monitoring

### Health Checks

Each service exposes health endpoints:

```bash
curl http://localhost:8081/actuator/health
```

### Metrics

Access metrics at:

```bash
curl http://localhost:8081/actuator/metrics
```

### Distributed Tracing

Access Zipkin dashboard at: http://localhost:9411

## 🔐 Security

- JWT-based authentication
- Password encryption using BCrypt
- Role-based access control (USER, ADMIN)
- HTTPS enforcement in production
- SQL injection prevention
- XSS protection

## 🐛 Troubleshooting

### Common Issues

#### 1. Service Registry Not Available

```bash
# Check if Eureka is running
curl http://localhost:8761

# Restart Eureka server
cd service-registry
mvn spring-boot:run
```

#### 2. Database Connection Error

```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Restart PostgreSQL
docker-compose restart postgres
```

#### 3. Port Already in Use

```bash
# Find process using the port
netstat -ano | findstr :8081

# Kill the process (Windows)
taskkill /PID <process-id> /F
```

## 📞 Support

For issues and questions:

- Create an issue on GitHub
- Contact: your-email@example.com

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👥 Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Netflix OSS for cloud components
- All contributors to this project

---

**Made with ❤️ for E-Commerce Platform**
