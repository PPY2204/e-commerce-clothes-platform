# Microservices Architecture Guide

## Overview

This e-commerce platform is built using a **microservices architecture**, where the application is decomposed into small, independent services that communicate with each other through well-defined APIs.

## Architecture Principles

### 1. Service Independence
Each microservice:
- Has its own codebase
- Can be deployed independently
- Has its own data storage (currently in-memory)
- Can be scaled independently
- Uses its own technology stack if needed

### 2. Single Responsibility
Each service has one clear business responsibility:
- **User Service**: Authentication and user management
- **Product Service**: Product catalog management
- **Order Service**: Order processing
- **Payment Service**: Payment processing
- **Inventory Service**: Stock level tracking and inventory management
- **Notification Service**: Email and SMS notifications
- **Recommendation Service**: Personalized product recommendations

### 3. Decentralized Data Management
Each service manages its own data:
- No direct database access between services
- Communication through APIs only
- Data consistency through eventual consistency patterns

### 4. API Gateway Pattern
Single entry point for all client requests:
- Route requests to appropriate services
- Handle cross-cutting concerns (rate limiting, CORS)
- Simplify client interaction

## Service Architecture

### API Gateway (Port 3000)

**Responsibility**: Entry point and request routing

```
┌─────────────────────────────────┐
│        API Gateway              │
│                                 │
│  - Request Routing              │
│  - Rate Limiting                │
│  - CORS Handling                │
│  - Load Balancing (future)      │
└─────────────────────────────────┘
```

**Technology Stack**:
- Express.js
- http-proxy-middleware
- express-rate-limit

**Key Features**:
- Routes `/api/users/*` → User Service
- Routes `/api/products/*` → Product Service
- Routes `/api/orders/*` → Order Service
- Routes `/api/payments/*` → Payment Service
- Routes `/api/inventory/*` → Inventory Service
- Routes `/api/notifications/*` → Notification Service
- Routes `/api/recommendations/*` → Recommendation Service
- Rate limiting: 100 requests per 15 minutes per IP

---

### User Service (Port 3001)

**Responsibility**: User authentication and management

```
┌─────────────────────────────────┐
│        User Service             │
│                                 │
│  Controllers                    │
│      ↓                          │
│  Repositories (Repository)      │
│      ↓                          │
│  Models                         │
│      ↓                          │
│  Config (Singleton)             │
└─────────────────────────────────┘
```

**Design Patterns**:
- **Singleton Pattern**: Configuration management
- **Repository Pattern**: User data access

**Endpoints**:
- `POST /users/register` - Register new user
- `POST /users/login` - Authenticate user
- `GET /users/profile` - Get user profile (authenticated)
- `GET /users` - Get all users (authenticated)

**Security**:
- Password hashing with bcrypt
- JWT token generation
- Token-based authentication

---

### Product Service (Port 3002)

**Responsibility**: Product catalog management

```
┌─────────────────────────────────┐
│      Product Service            │
│                                 │
│  Controllers                    │
│      ↓                          │
│  Factory (Factory Pattern)      │
│      ↓                          │
│  Repositories (Repository)      │
│      ↓                          │
│  Models (TShirt, Pants, etc.)   │
└─────────────────────────────────┘
```

**Design Patterns**:
- **Factory Pattern**: Product type creation
- **Repository Pattern**: Product data access

**Endpoints**:
- `POST /products` - Create product
- `GET /products` - Get all products (with filters)
- `GET /products/:id` - Get product by ID
- `PUT /products/:id` - Update product
- `DELETE /products/:id` - Delete product

**Product Types**:
- T-Shirt (size, color, material)
- Pants (size, color, waist size, length)
- Jacket (size, color, material, waterproof)
- Dress (size, color, style, length)

**Query Filters**:
- Category filter
- Price range (min/max)
- Color filter

---

### Order Service (Port 3003)

**Responsibility**: Order processing and management

```
┌─────────────────────────────────┐
│       Order Service             │
│                                 │
│  Controllers                    │
│      ↓                          │
│  Subject (Observer Pattern)     │
│      ↓                          │
│  Observers:                     │
│    - Email                      │
│    - Inventory                  │
│    - Analytics                  │
│    - Shipping                   │
│      ↓                          │
│  Repositories (Repository)      │
│      ↓                          │
│  Models                         │
└─────────────────────────────────┘
```

**Design Patterns**:
- **Observer Pattern**: Event notifications
- **Repository Pattern**: Order data access

**Endpoints**:
- `POST /orders` - Create order
- `GET /orders` - Get all orders
- `GET /orders/:id` - Get order by ID
- `GET /orders/user/:userId` - Get user orders
- `PATCH /orders/:id/status` - Update order status
- `POST /orders/:id/cancel` - Cancel order

**Order Statuses**:
- `pending` - Order created
- `processing` - Order being processed
- `shipped` - Order shipped
- `delivered` - Order delivered
- `cancelled` - Order cancelled

**Event Notifications**:
Each status change triggers notifications to:
- Email service (customer notifications)
- Inventory service (stock management)
- Analytics service (metrics tracking)
- Shipping service (logistics)

---

### Payment Service (Port 3004)

**Responsibility**: Payment processing

```
┌─────────────────────────────────┐
│      Payment Service            │
│                                 │
│  Controllers                    │
│      ↓                          │
│  Context (Strategy Pattern)     │
│      ↓                          │
│  Strategies:                    │
│    - Credit Card                │
│    - PayPal                     │
│    - Cryptocurrency             │
│    - Bank Transfer              │
│      ↓                          │
│  Repositories (Repository)      │
│      ↓                          │
│  Models                         │
└─────────────────────────────────┘
```

**Design Patterns**:
- **Strategy Pattern**: Payment method selection
- **Repository Pattern**: Payment data access

**Endpoints**:
- `POST /payments` - Process payment
- `GET /payments` - Get all payments
- `GET /payments/:id` - Get payment by ID
- `GET /payments/order/:orderId` - Get payment by order
- `POST /payments/:id/refund` - Refund payment
- `GET /payments/methods` - Get supported methods

**Payment Methods**:
1. **Credit Card**
   - Validation: Card number, CVV, expiry date
   - Integration: Stripe/similar (simulated)

2. **PayPal**
   - Validation: Email, password
   - Integration: PayPal API (simulated)

3. **Cryptocurrency**
   - Validation: Wallet address, crypto type
   - Integration: Crypto gateway (simulated)

4. **Bank Transfer**
   - Validation: Account number, routing number
   - Integration: Banking API (simulated)

**Payment Statuses**:
- `pending` - Payment initiated
- `processing` - Payment being processed
- `completed` - Payment successful
- `failed` - Payment failed
- `refunded` - Payment refunded

---

### Inventory Service (Port 3005)

**Responsibility**: Stock level tracking and inventory management

```
┌─────────────────────────────────┐
│      Inventory Service          │
│                                 │
│  Controllers                    │
│      ↓                          │
│  Repositories (Repository)      │
│      ↓                          │
│  Models (InventoryItem)         │
└─────────────────────────────────┘
```

**Design Patterns**:
- **Repository Pattern**: Inventory data access

**Endpoints**:
- `POST /inventory` - Create inventory item
- `GET /inventory` - Get all inventory
- `GET /inventory/low-stock` - Get low stock items
- `GET /inventory/out-of-stock` - Get out of stock items
- `GET /inventory/:productId` - Get inventory by product
- `PUT /inventory/:productId` - Update inventory
- `DELETE /inventory/:productId` - Delete inventory
- `POST /inventory/:productId/reserve` - Reserve inventory
- `POST /inventory/:productId/release` - Release reservation
- `POST /inventory/:productId/deduct` - Deduct from stock
- `POST /inventory/:productId/restock` - Add stock

**Key Features**:
- Track available and reserved quantities
- Low stock alerts
- Out of stock detection
- Reserve/release/deduct operations for order processing
- Automatic calculations for available quantity

**Inventory States**:
- Available: Total - Reserved
- Reserved: Temporarily held for pending orders
- Low Stock: Available ≤ threshold
- Out of Stock: Available = 0

---

### Notification Service (Port 3006)

**Responsibility**: Email and SMS notifications

```
┌─────────────────────────────────┐
│    Notification Service         │
│                                 │
│  Controllers                    │
│      ↓                          │
│  Context (Strategy Pattern)     │
│      ↓                          │
│  Strategies:                    │
│    - Email                      │
│    - SMS                        │
│      ↓                          │
│  Repositories (Repository)      │
│      ↓                          │
│  Models (Notification)          │
└─────────────────────────────────┘
```

**Design Patterns**:
- **Strategy Pattern**: Notification channel selection (Email, SMS)
- **Repository Pattern**: Notification data access

**Endpoints**:
- `POST /notifications` - Send notification
- `GET /notifications` - Get all notifications
- `GET /notifications/channels` - Get supported channels
- `GET /notifications/:id` - Get notification by ID
- `GET /notifications/recipient/:recipient` - Get by recipient
- `POST /notifications/events` - Event webhook for system events

**Notification Channels**:
1. **Email**
   - Integration: SendGrid/AWS SES (simulated)
   - For: Order confirmations, payment confirmations, shipment tracking
   
2. **SMS**
   - Integration: Twilio/AWS SNS (simulated)
   - For: Urgent notifications, OTPs, delivery updates

**Notification Types**:
- `order_confirmation` - Order placed successfully
- `payment_confirmation` - Payment processed
- `shipment_tracking` - Order shipped
- `low_stock_alert` - Admin notification for low inventory

**Event-Driven Architecture**:
The service listens for events from other services:
```javascript
POST /notifications/events
{
  "event": "order_shipped",
  "data": {
    "orderId": 1,
    "userEmail": "user@example.com",
    "trackingNumber": "TRACK123"
  }
}
```

---

### Recommendation Service (Port 3007)

**Responsibility**: Personalized product recommendations

```
┌─────────────────────────────────┐
│   Recommendation Service        │
│                                 │
│  Controllers                    │
│      ↓                          │
│  Context (Strategy Pattern)     │
│      ↓                          │
│  Engines:                       │
│    - Collaborative Filtering    │
│    - Content-Based              │
│    - Trending                   │
│    - Hybrid                     │
│      ↓                          │
│  Repositories (Repository)      │
│      ↓                          │
│  Models (UserProfile)           │
└─────────────────────────────────┘
```

**Design Patterns**:
- **Strategy Pattern**: Multiple recommendation algorithms
- **Repository Pattern**: User profile data access

**Endpoints**:
- `GET /recommendations/:userId` - Get recommendations
- `GET /recommendations/algorithms` - Get supported algorithms
- `GET /recommendations/:userId/profile` - Get user profile
- `POST /recommendations/:userId/profile` - Update user profile
- `POST /recommendations/events` - Event webhook for user actions

**Recommendation Algorithms**:

1. **Collaborative Filtering**
   - Based on similar users' behavior
   - "Users like you also liked..."
   - Analyzes purchase patterns across users

2. **Content-Based Filtering**
   - Based on user's preferences and history
   - Analyzes product attributes (category, color, price)
   - Matches to user profile

3. **Trending**
   - Currently popular products
   - Time-based popularity
   - Cross-user trending analysis

4. **Hybrid** (Default)
   - Combines all algorithms
   - Weighted scoring: 40% Collaborative, 40% Content-Based, 20% Trending
   - Best overall recommendations

**User Profile Tracking**:
- Purchase history
- View history
- Category preferences
- Color preferences
- Size preferences
- Price range preferences

**Event Integration**:
The service tracks user behavior through events:
```javascript
POST /recommendations/events
{
  "event": "order_completed",
  "data": {
    "userId": 1,
    "items": [...]
  }
}
```

---

## Inter-Service Communication

### Current Implementation: HTTP/REST

```
Client → API Gateway → Service
                ↓
            Response
```

**Advantages**:
- Simple to implement
- Easy to debug
- Standard HTTP tooling

**Limitations**:
- Synchronous communication
- Tight coupling
- No retry mechanism

### Future Enhancement: Message Queue

```
Service A → Message Queue → Service B
              (RabbitMQ/Kafka)
```

**Benefits**:
- Asynchronous communication
- Loose coupling
- Built-in retry logic
- Event sourcing capability

---

## Data Management

### Current: In-Memory Storage

Each service uses `Map` data structure:
```javascript
class Repository {
  constructor() {
    this.data = new Map();
  }
}
```

**Advantages**:
- Fast access
- No external dependencies
- Easy to set up and test

**Limitations**:
- Data lost on restart
- No persistence
- Limited scalability

### Future: Database Per Service

```
User Service → MongoDB/PostgreSQL
Product Service → MongoDB/PostgreSQL
Order Service → MongoDB/PostgreSQL
Payment Service → MongoDB/PostgreSQL
```

**Benefits**:
- Data persistence
- Scalability
- Transaction support
- Query optimization

---

## Deployment Architecture

### Current: Docker Compose

```
docker-compose.yml
├── api-gateway (container)
├── user-service (container)
├── product-service (container)
├── order-service (container)
└── payment-service (container)
```

**Network**: Bridge network (ecommerce-network)

**Advantages**:
- Easy local development
- Service isolation
- Quick setup

### Future: Kubernetes

```
Kubernetes Cluster
├── API Gateway (Deployment + Service)
├── User Service (Deployment + Service)
├── Product Service (Deployment + Service)
├── Order Service (Deployment + Service)
└── Payment Service (Deployment + Service)
```

**Benefits**:
- Auto-scaling
- Self-healing
- Load balancing
- Rolling updates
- Service discovery

---

## Scaling Strategy

### Horizontal Scaling

Each service can be scaled independently:

```
Before:
[API Gateway] → [Service]

After:
                ┌─ [Service Instance 1]
[API Gateway] ──┼─ [Service Instance 2]
                └─ [Service Instance 3]
```

### Vertical Scaling

Increase resources for specific services:
```
Small: 512MB RAM, 0.5 CPU
Medium: 1GB RAM, 1 CPU
Large: 2GB RAM, 2 CPU
```

### Auto-Scaling Rules

Based on:
- CPU utilization (> 70%)
- Memory usage (> 80%)
- Request rate (> 1000 req/min)
- Response time (> 500ms)

---

## Security Architecture

### 1. API Gateway Security
- Rate limiting
- CORS configuration
- Request validation
- DDoS protection (future)

### 2. Authentication & Authorization
```
Client → Login → JWT Token
      ↓
Protected Routes → Verify Token → Access
```

### 3. Service-to-Service Authentication (Future)
- Service mesh (Istio)
- Mutual TLS
- API keys
- OAuth 2.0

### 4. Data Security
- Password hashing (bcrypt)
- Environment variables for secrets
- HTTPS/TLS (production)
- Secrets management (Vault - future)

---

## Monitoring & Observability

### Current Implementation
- Health check endpoints
- Console logging
- Docker logs

### Future Enhancements

#### 1. Distributed Tracing
```
Request → API Gateway → Service A → Service B
            ↓            ↓            ↓
         Trace ID    Trace ID    Trace ID
            ↓            ↓            ↓
          Jaeger/Zipkin Dashboard
```

#### 2. Centralized Logging
```
Services → Log Aggregator → Elasticsearch → Kibana
           (Fluentd/Logstash)
```

#### 3. Metrics & Monitoring
```
Services → Prometheus → Grafana Dashboards
```

**Key Metrics**:
- Request rate
- Response time
- Error rate
- CPU/Memory usage
- Active connections

#### 4. Alerting
```
Metrics → Alert Rules → Notification
                          ↓
                    Email/Slack/PagerDuty
```

---

## Resilience Patterns

### 1. Circuit Breaker (Future)
Prevents cascading failures:
```
If Service B fails:
Service A → Circuit Open → Fallback Response
(stops calling Service B for X seconds)
```

### 2. Retry Pattern (Future)
Automatic retry with backoff:
```
Request fails → Wait 1s → Retry
             → Wait 2s → Retry
             → Wait 4s → Give up
```

### 3. Timeout Pattern
Set maximum wait time:
```
Request → Service (max 5s) → Timeout → Error
```

### 4. Bulkhead Pattern (Future)
Isolate resources:
```
Thread Pool A (User Service)
Thread Pool B (Product Service)
Thread Pool C (Order Service)
```

---

## API Versioning Strategy

### URL Versioning
```
/api/v1/users
/api/v2/users
```

### Header Versioning
```
Accept: application/vnd.ecommerce.v1+json
```

---

## Service Discovery (Future)

### Consul/Eureka Integration
```
Service Registration:
Service starts → Register with Consul → Health checks

Service Discovery:
Service A needs Service B → Query Consul → Get B's address
```

---

## Microservices Anti-Patterns to Avoid

1. **Distributed Monolith**: Services too tightly coupled
2. **Shared Database**: Services sharing same database
3. **Too Fine-Grained**: Too many small services
4. **Chatty Services**: Excessive inter-service calls
5. **No Service Boundaries**: Unclear responsibilities

---

## Migration Path

### Phase 1: Current (✅ Complete)
- Basic microservices structure
- In-memory data storage
- Docker containerization
- REST APIs

### Phase 2: Persistence (Next)
- Add databases to each service
- Implement data persistence
- Add database migrations

### Phase 3: Messaging (Future)
- Implement message queue
- Async communication
- Event-driven architecture

### Phase 4: Production Ready (Future)
- Kubernetes deployment
- Service mesh
- Monitoring & logging
- CI/CD pipeline

---

## Conclusion

This microservices architecture provides:
- **Scalability**: Each service scales independently
- **Flexibility**: Easy to add/modify services
- **Resilience**: Failure isolation
- **Technology Freedom**: Different tech per service
- **Team Autonomy**: Teams own their services

The architecture is designed to grow from a simple development setup to a production-ready, cloud-native application.
