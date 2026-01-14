# Complete Order Flow Example

This document demonstrates a complete end-to-end order flow through all 8 microservices in the e-commerce platform.

## Prerequisites

All services must be running:
```bash
docker compose up --build
```

## Complete Order Process Flow

### Step 1: User Registration and Authentication

#### Register a new user
```bash
curl -X POST http://localhost:3000/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "customer"
  }'
```

**Response**: User created with ID

#### Login to get JWT token
```bash
curl -X POST http://localhost:3000/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Response**: JWT token (save this for authenticated requests)

---

### Step 2: Browse Product Catalog

#### View all products
```bash
curl http://localhost:3000/api/products
```

#### Create sample products (Factory Pattern creates different types)
```bash
# Create T-Shirt
curl -X POST http://localhost:3000/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "type": "tshirt",
    "name": "Classic Blue T-Shirt",
    "description": "Comfortable cotton t-shirt",
    "price": 29.99,
    "size": "M",
    "color": "Blue",
    "material": "100% Cotton"
  }'

# Create Jeans
curl -X POST http://localhost:3000/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "type": "pants",
    "name": "Slim Fit Jeans",
    "description": "Modern slim fit jeans",
    "price": 59.99,
    "size": "32",
    "color": "Dark Blue",
    "waistSize": "32",
    "length": "32"
  }'
```

---

### Step 3: Initialize Inventory

#### Add inventory for products
```bash
# Inventory for Product ID 1 (T-Shirt)
curl -X POST http://localhost:3000/api/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "quantity": 100,
    "lowStockThreshold": 10
  }'

# Inventory for Product ID 2 (Jeans)
curl -X POST http://localhost:3000/api/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 2,
    "quantity": 50,
    "lowStockThreshold": 5
  }'
```

#### Check inventory status
```bash
curl http://localhost:3000/api/inventory/1
```

---

### Step 4: Get Personalized Recommendations

#### Update user profile with a product view
```bash
curl -X POST http://localhost:3000/api/recommendations/1/profile \
  -H "Content-Type: application/json" \
  -d '{
    "action": "view",
    "productId": 1,
    "category": "tshirt",
    "price": 29.99,
    "attributes": {
      "color": "Blue",
      "size": "M"
    }
  }'
```

#### Get recommendations (Hybrid algorithm)
```bash
curl "http://localhost:3000/api/recommendations/1?algorithm=hybrid&limit=5"
```

**Response**: Personalized product recommendations based on user behavior

---

### Step 5: Create Order (Observer Pattern Triggers)

```bash
curl -X POST http://localhost:3000/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2,
        "price": 29.99
      },
      {
        "productId": 2,
        "quantity": 1,
        "price": 59.99
      }
    ]
  }'
```

**What happens internally:**
1. Order Service creates order
2. **Observer Pattern** notifies:
   - **Email Observer**: Sends order confirmation notification
   - **Inventory Observer**: Reserves inventory (calls Inventory Service)
   - **Analytics Observer**: Records order metrics
   - **Shipping Observer**: Prepares shipment information

**Console logs show:**
```
[EMAIL] Sending email notification for order 1
[INVENTORY] Reserving inventory for order 1
[ANALYTICS] Recording event: created for order 1
```

---

### Step 6: Inventory Service Reserves Stock

The Inventory Observer automatically calls:
```bash
curl -X POST http://localhost:3000/api/inventory/1/reserve \
  -H "Content-Type: application/json" \
  -d '{"quantity": 2}'

curl -X POST http://localhost:3000/api/inventory/2/reserve \
  -H "Content-Type: application/json" \
  -d '{"quantity": 1}'
```

#### Verify inventory was reserved
```bash
curl http://localhost:3000/api/inventory/1
```

**Response shows:**
```json
{
  "productId": 1,
  "quantity": 100,
  "reservedQuantity": 2,
  "availableQuantity": 98,
  "isLowStock": false
}
```

---

### Step 7: Process Payment (Strategy Pattern)

#### Check supported payment methods
```bash
curl http://localhost:3000/api/payments/methods
```

#### Process payment with Credit Card Strategy
```bash
curl -X POST http://localhost:3000/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "amount": 119.97,
    "method": "credit_card",
    "paymentDetails": {
      "cardNumber": "4111111111111111",
      "cvv": "123",
      "expiryDate": "12/25"
    }
  }'
```

**What happens:**
1. Payment Service validates card details
2. **Strategy Pattern** selects CreditCardStrategy
3. Secure transaction ID generated using crypto.randomUUID()
4. Payment processed and recorded

**Alternative: PayPal Strategy**
```bash
curl -X POST http://localhost:3000/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "amount": 119.97,
    "method": "paypal",
    "paymentDetails": {
      "email": "john@paypal.com",
      "password": "paypal123"
    }
  }'
```

---

### Step 8: Send Payment Confirmation Notification

#### Trigger notification via webhook (event-driven)
```bash
curl -X POST http://localhost:3000/api/notifications/events \
  -H "Content-Type: application/json" \
  -d '{
    "event": "payment_completed",
    "data": {
      "orderId": 1,
      "userEmail": "john@example.com",
      "amount": 119.97
    }
  }'
```

**What happens:**
1. Notification Service receives event
2. Creates notification record
3. **Strategy Pattern** selects EmailNotificationStrategy
4. Email sent to customer

**Console shows:**
```
[EMAIL] Sending email to: john@example.com
[EMAIL] Subject: Payment Confirmation - Order #1
[EMAIL] Message: Payment of $119.97 has been processed successfully.
```

---

### Step 9: Update Order Status (Observer Pattern)

#### Mark order as processing
```bash
curl -X PATCH http://localhost:3000/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "processing"}'
```

**Observers notified:**
- Email: Processing update sent
- Analytics: Status change recorded
- Shipping: Shipment preparation begins

#### Deduct inventory (finalize reservation)
```bash
curl -X POST http://localhost:3000/api/inventory/1/deduct \
  -H "Content-Type: application/json" \
  -d '{"quantity": 2}'

curl -X POST http://localhost:3000/api/inventory/2/deduct \
  -H "Content-Type: application/json" \
  -d '{"quantity": 1}'
```

---

### Step 10: Ship Order

#### Update order status to shipped
```bash
curl -X PATCH http://localhost:3000/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "shipped"}'
```

**Observers notified:**
- Email: Shipment notification
- Shipping: Tracking information updated

#### Send shipment notification
```bash
curl -X POST http://localhost:3000/api/notifications/events \
  -H "Content-Type: application/json" \
  -d '{
    "event": "order_shipped",
    "data": {
      "orderId": 1,
      "userEmail": "john@example.com",
      "trackingNumber": "TRACK123456"
    }
  }'
```

---

### Step 11: Update Recommendations

#### Record purchase in recommendation engine
```bash
curl -X POST http://localhost:3000/api/recommendations/1/profile \
  -H "Content-Type: application/json" \
  -d '{
    "action": "purchase",
    "productId": 1,
    "category": "tshirt",
    "price": 29.99,
    "attributes": {
      "color": "Blue",
      "size": "M"
    }
  }'

curl -X POST http://localhost:3000/api/recommendations/1/profile \
  -H "Content-Type: application/json" \
  -d '{
    "action": "purchase",
    "productId": 2,
    "category": "pants",
    "price": 59.99,
    "attributes": {
      "color": "Dark Blue",
      "size": "32"
    }
  }'
```

#### Get updated recommendations
```bash
curl "http://localhost:3000/api/recommendations/1?algorithm=content_based&limit=5"
```

**Response**: Recommendations based on purchase history and preferences

---

### Step 12: Complete Order

#### Mark order as delivered
```bash
curl -X PATCH http://localhost:3000/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "delivered"}'
```

**Final notifications sent via Observer Pattern**

---

## Services Communication Flow

```
1. User → API Gateway → User Service (Auth)
                     ↓
2. User → API Gateway → Product Service (Browse)
                     ↓
3. Admin → API Gateway → Inventory Service (Stock)
                     ↓
4. User → API Gateway → Recommendation Service (Recommendations)
                     ↓
5. User → API Gateway → Order Service (Create Order)
                     ├→ Observer → Email Notification
                     ├→ Observer → Inventory (Reserve)
                     ├→ Observer → Analytics
                     └→ Observer → Shipping
                     ↓
6. System → API Gateway → Inventory Service (Reserve Stock)
                     ↓
7. User → API Gateway → Payment Service (Process Payment)
                     ├→ Strategy → Credit Card/PayPal/Crypto
                     └→ Secure Transaction ID
                     ↓
8. System → API Gateway → Notification Service (Payment Confirmation)
                     └→ Strategy → Email/SMS
                     ↓
9. System → API Gateway → Order Service (Update Status)
                     └→ Observer → Multiple Systems Notified
                     ↓
10. System → API Gateway → Inventory Service (Deduct Stock)
                     ↓
11. System → API Gateway → Notification Service (Shipment)
                     ↓
12. System → API Gateway → Recommendation Service (Update Profile)
```

## Design Patterns in Action

### 1. **Factory Pattern** (Product Service)
- Creates different product types (TShirt, Pants, Jacket, Dress)
- Runtime type selection based on `type` parameter

### 2. **Strategy Pattern** (Payment Service)
- Interchangeable payment methods (Credit Card, PayPal, Crypto, Bank Transfer)
- Each strategy has its own validation and processing logic

### 3. **Strategy Pattern** (Notification Service)
- Interchangeable notification channels (Email, SMS)
- Easy to add new channels (Push, Slack, etc.)

### 4. **Strategy Pattern** (Recommendation Service)
- Multiple ML algorithms (Collaborative, Content-Based, Trending, Hybrid)
- Runtime algorithm selection

### 5. **Observer Pattern** (Order Service)
- Order status changes trigger multiple observers
- Decoupled notification system
- Email, Inventory, Analytics, Shipping observers

### 6. **Repository Pattern** (All Services)
- Abstracts data access
- Consistent interface across services
- Easy to swap in-memory storage with databases

### 7. **Singleton Pattern** (User Service)
- Single configuration instance
- Centralized config management

## Monitoring the Flow

### Check all service health
```bash
for port in 3000 3001 3002 3003 3004 3005 3006 3007; do
  echo "Port $port:"
  curl -s http://localhost:$port/health | jq
done
```

### View service logs
```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f order-service

# See Observer Pattern in action
docker compose logs -f order-service | grep -E '\[EMAIL\]|\[INVENTORY\]|\[ANALYTICS\]|\[SHIPPING\]'

# See Strategy Pattern in action
docker compose logs -f payment-service | grep -E '\[CREDIT_CARD\]|\[PAYPAL\]|\[CRYPTO\]'
docker compose logs -f notification-service | grep -E '\[EMAIL\]|\[SMS\]'
docker compose logs -f recommendation-service | grep -E '\[COLLABORATIVE\]|\[CONTENT_BASED\]|\[TRENDING\]|\[HYBRID\]'
```

## Low Stock Alert Example

If inventory goes low:

```bash
# Create inventory with low threshold
curl -X POST http://localhost:3000/api/inventory \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 3,
    "quantity": 8,
    "lowStockThreshold": 10
  }'

# Check low stock items
curl http://localhost:3000/api/inventory/low-stock
```

**Trigger low stock notification:**
```bash
curl -X POST http://localhost:3000/api/notifications/events \
  -H "Content-Type: application/json" \
  -d '{
    "event": "low_stock_alert",
    "data": {
      "productId": 3,
      "adminEmail": "admin@example.com",
      "availableQuantity": 8
    }
  }'
```

## Summary

This complete flow demonstrates:
- **8 microservices** working together
- **5 design patterns** in action
- **Event-driven architecture** with Observer Pattern
- **Strategy Pattern** for flexible payment, notification, and recommendation algorithms
- **Repository Pattern** for clean data access
- **Factory Pattern** for product type creation
- **Singleton Pattern** for configuration management
- **Service communication** through API Gateway
- **Asynchronous notifications** via webhooks
- **Inventory management** with reservations
- **Personalized recommendations** with ML algorithms
