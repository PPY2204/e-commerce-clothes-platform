# E-Commerce Clothes Platform

A comprehensive e-commerce platform for clothing built with **microservices architecture** and implementing various **design patterns**.

## 🏗️ Architecture

This project follows a **microservices architecture** with the following services:

- **API Gateway** (Port 3000) - Entry point for all client requests, handles routing and rate limiting
- **User Service** (Port 3001) - User authentication and management
- **Product Service** (Port 3002) - Clothing catalog management
- **Order Service** (Port 3003) - Order processing and management
- **Payment Service** (Port 3004) - Payment processing with multiple payment methods
- **Inventory Service** (Port 3005) - Stock level tracking and inventory management
- **Notification Service** (Port 3006) - Email and SMS notifications for various events
- **Recommendation Service** (Port 3007) - Personalized product recommendations using ML algorithms

## 🎨 Design Patterns Implemented

### 1. **Singleton Pattern**
- **Location**: `services/user-service/src/config/config.js`
- **Purpose**: Ensures only one instance of configuration exists across the application
- **Usage**: Managing application configuration

### 2. **Repository Pattern**
- **Location**: All services (`*Repository.js` files)
- **Purpose**: Abstracts data access logic and provides a collection-like interface
- **Usage**: 
  - `UserRepository` - User data access
  - `ProductRepository` - Product data access
  - `OrderRepository` - Order data access
  - `PaymentRepository` - Payment data access

### 3. **Factory Pattern**
- **Location**: `services/product-service/src/factories/ProductFactory.js`
- **Purpose**: Creates different types of product objects without specifying exact classes
- **Usage**: Creating different clothing types (T-Shirt, Pants, Jacket, Dress)

### 4. **Strategy Pattern**
- **Location**: `services/payment-service/src/strategies/`
- **Purpose**: Defines a family of algorithms (payment methods) and makes them interchangeable
- **Usage**: Supporting multiple payment methods:
  - Credit Card
  - PayPal
  - Cryptocurrency
  - Bank Transfer

### 5. **Observer Pattern**
- **Location**: `services/order-service/src/observers/`
- **Purpose**: Defines a one-to-many dependency for event notifications
- **Usage**: Notifying multiple systems when order status changes:
  - Email notifications
  - Inventory updates
  - Analytics tracking
  - Shipping updates

## 🚀 Getting Started

### Prerequisites
- Docker and Docker Compose
- Node.js 18+ (for local development)

### Running with Docker

1. Clone the repository:
```bash
git clone https://github.com/PPY2204/e-commerce-clothes-platform.git
cd e-commerce-clothes-platform
```

2. Start all services:
```bash
docker-compose up --build
```

3. Access the API Gateway:
```
http://localhost:3000
```

### Service Health Checks

- API Gateway: http://localhost:3000/health
- User Service: http://localhost:3001/health
- Product Service: http://localhost:3002/health
- Order Service: http://localhost:3003/health
- Payment Service: http://localhost:3004/health

## 📚 API Documentation

### User Service (`/api/users`)

#### Register User
```bash
POST /api/users/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "customer"
}
```

#### Login
```bash
POST /api/users/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

#### Get Profile (requires authentication)
```bash
GET /api/users/profile
Authorization: Bearer <token>
```

### Product Service (`/api/products`)

#### Create Product (Factory Pattern in action)
```bash
POST /api/products
Content-Type: application/json

{
  "type": "tshirt",
  "name": "Classic Cotton T-Shirt",
  "description": "Comfortable cotton t-shirt",
  "price": 29.99,
  "size": "M",
  "color": "Blue",
  "material": "100% Cotton"
}
```

#### Get All Products (with filters)
```bash
GET /api/products?category=tshirt&minPrice=20&maxPrice=50&color=Blue
```

#### Get Product by ID
```bash
GET /api/products/:id
```

### Order Service (`/api/orders`)

#### Create Order (Observer Pattern triggers notifications)
```bash
POST /api/orders
Content-Type: application/json

{
  "userId": 1,
  "items": [
    {
      "productId": 1,
      "quantity": 2,
      "price": 29.99
    }
  ]
}
```

#### Update Order Status (Observer Pattern in action)
```bash
PATCH /api/orders/:id/status
Content-Type: application/json

{
  "status": "shipped"
}
```

### Payment Service (`/api/payments`)

#### Get Supported Payment Methods
```bash
GET /api/payments/methods
```

#### Process Payment (Strategy Pattern in action)
```bash
POST /api/payments
Content-Type: application/json

# Credit Card Payment
{
  "orderId": 1,
  "amount": 59.98,
  "method": "credit_card",
  "paymentDetails": {
    "cardNumber": "4111111111111111",
    "cvv": "123",
    "expiryDate": "12/25"
  }
}

# PayPal Payment
{
  "orderId": 1,
  "amount": 59.98,
  "method": "paypal",
  "paymentDetails": {
    "email": "user@paypal.com",
    "password": "password"
  }
}
```

### Inventory Service (`/api/inventory`)

#### Create Inventory Item
```bash
POST /api/inventory
Content-Type: application/json

{
  "productId": 1,
  "quantity": 100,
  "lowStockThreshold": 10
}
```

#### Reserve Inventory (for order processing)
```bash
POST /api/inventory/:productId/reserve
Content-Type: application/json

{
  "quantity": 2
}
```

#### Get Low Stock Items
```bash
GET /api/inventory/low-stock
```

### Notification Service (`/api/notifications`)

#### Send Notification (Strategy Pattern for email/SMS)
```bash
POST /api/notifications
Content-Type: application/json

{
  "type": "order_confirmation",
  "recipient": "user@example.com",
  "subject": "Order Confirmation",
  "message": "Your order has been confirmed",
  "channel": "email"
}
```

#### Event-Driven Notification Webhook
```bash
POST /api/notifications/events
Content-Type: application/json

{
  "event": "order_shipped",
  "data": {
    "orderId": 1,
    "userEmail": "user@example.com",
    "trackingNumber": "TRACK123"
  }
}
```

### Recommendation Service (`/api/recommendations`)

#### Get Personalized Recommendations
```bash
GET /api/recommendations/:userId?algorithm=hybrid&limit=5
```

#### Update User Profile for Better Recommendations
```bash
POST /api/recommendations/:userId/profile
Content-Type: application/json

{
  "action": "purchase",
  "productId": 1,
  "category": "tshirt",
  "price": 29.99,
  "attributes": {
    "color": "Blue",
    "size": "M"
  }
}
```

#### Get Supported Recommendation Algorithms
```bash
GET /api/recommendations/algorithms
```

## 🏛️ Architecture Diagram

```
┌─────────────────┐
│   Client App    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  API Gateway    │ ◄── Rate Limiting, Routing
│   (Port 3000)   │
└────────┬────────┘
         │
    ┌────┴────┬─────────┬──────────┬──────────┬──────────┬──────────┐
    ▼         ▼         ▼          ▼          ▼          ▼          ▼
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
│ User   │ │Product │ │ Order  │ │Payment │ │Inventory │ │Notifica- │ │Recommend-│
│Service │ │Service │ │Service │ │Service │ │Service   │ │tion Svc  │ │ation Svc │
│ :3001  │ │ :3002  │ │ :3003  │ │ :3004  │ │ :3005    │ │ :3006    │ │ :3007    │
└────────┘ └────────┘ └────────┘ └────────┘ └──────────┘ └──────────┘ └──────────┘
    │         │         │          │          │            │            │
    └─────────┴─────────┴──────────┴──────────┴────────────┴────────────┘
              │
        [Future: Database Layer]
```

## 🔒 Security Features

- JWT-based authentication
- Password hashing with bcrypt
- Rate limiting on API Gateway
- CORS protection
- Input validation

## 📦 Project Structure

```
e-commerce-clothes-platform/
├── services/
│   ├── api-gateway/          # API Gateway Service
│   │   ├── src/
│   │   ├── Dockerfile
│   │   └── package.json
│   ├── user-service/         # User Management (Singleton Pattern)
│   │   ├── src/
│   │   │   ├── config/       # Configuration (Singleton)
│   │   │   ├── models/       # User models
│   │   │   ├── repositories/ # Repository Pattern
│   │   │   ├── controllers/
│   │   │   └── routes/
│   │   ├── Dockerfile
│   │   └── package.json
│   ├── product-service/      # Product Catalog (Factory Pattern)
│   │   ├── src/
│   │   │   ├── factories/    # Product Factory
│   │   │   ├── models/       # Product models
│   │   │   ├── repositories/ # Repository Pattern
│   │   │   ├── controllers/
│   │   │   └── routes/
│   │   ├── Dockerfile
│   │   └── package.json
│   ├── order-service/        # Order Management (Observer Pattern)
│   │   ├── src/
│   │   │   ├── observers/    # Observer Pattern
│   │   │   ├── models/       # Order models
│   │   │   ├── repositories/ # Repository Pattern
│   │   │   ├── controllers/
│   │   │   └── routes/
│   │   ├── Dockerfile
│   │   └── package.json
│   └── payment-service/      # Payment Processing (Strategy Pattern)
│       ├── src/
│       │   ├── strategies/   # Payment Strategies
│       │   ├── models/       # Payment models
│       │   ├── repositories/ # Repository Pattern
│       │   ├── controllers/
│       │   └── routes/
│       ├── Dockerfile
│       └── package.json
├── shared/                   # Shared utilities
│   ├── middleware/           # Authentication middleware
│   └── utils/                # Common utilities
├── docker-compose.yml        # Docker orchestration
└── README.md
```

## 🛠️ Development

### Running a Single Service Locally

```bash
cd services/user-service
npm install
npm start
```

### Environment Variables

Each service can be configured using environment variables. See `.env.example` files in each service directory.

## 🧪 Testing the System

### 1. Register a User
```bash
curl -X POST http://localhost:3000/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"pass123"}'
```

### 2. Login
```bash
curl -X POST http://localhost:3000/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john@example.com","password":"pass123"}'
```

### 3. Create a Product
```bash
curl -X POST http://localhost:3000/api/products \
  -H "Content-Type: application/json" \
  -d '{"type":"tshirt","name":"Blue T-Shirt","description":"Nice shirt","price":25.99,"size":"M","color":"Blue","material":"Cotton"}'
```

### 4. Create an Order
```bash
curl -X POST http://localhost:3000/api/orders \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"items":[{"productId":1,"quantity":2,"price":25.99}]}'
```

### 5. Process Payment
```bash
curl -X POST http://localhost:3000/api/payments \
  -H "Content-Type: application/json" \
  -d '{"orderId":1,"amount":51.98,"method":"credit_card","paymentDetails":{"cardNumber":"4111111111111111","cvv":"123","expiryDate":"12/25"}}'
```

## 🔄 Design Patterns in Action

### When You Create an Order
1. **Repository Pattern** saves the order
2. **Observer Pattern** notifies:
   - Email service (sends confirmation)
   - Inventory service (reserves items)
   - Analytics service (tracks metrics)
   - Shipping service (prepares shipment)

### When You Create a Product
1. **Factory Pattern** creates the appropriate product type (T-Shirt, Pants, etc.)
2. **Repository Pattern** persists the data

### When You Process a Payment
1. **Strategy Pattern** selects the appropriate payment method
2. Each payment method has its own validation and processing logic
3. **Repository Pattern** saves the payment record

## 🚀 Future Enhancements

- [ ] Add database integration (MongoDB/PostgreSQL)
- [ ] Implement message queue (RabbitMQ/Kafka)
- [ ] Add service discovery (Consul/Eureka)
- [ ] Implement API documentation with Swagger
- [ ] Add monitoring and logging (Prometheus/Grafana)
- [ ] Implement circuit breaker pattern
- [ ] Add caching layer (Redis)
- [ ] Implement unit and integration tests

## 📝 License

MIT License

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.