# Quick Start Guide

This guide will help you get the e-commerce platform running quickly.

## Prerequisites

- Docker Desktop installed ([Download](https://www.docker.com/products/docker-desktop))
- Basic understanding of REST APIs
- Optional: Postman or curl for testing

## 🚀 Quick Start (5 minutes)

### Step 1: Clone and Start

```bash
# Clone the repository
git clone https://github.com/PPY2204/e-commerce-clothes-platform.git
cd e-commerce-clothes-platform

# Start all services with Docker Compose
docker-compose up --build
```

Wait for all services to start (you'll see logs from all 5 services).

### Step 2: Verify Services

Open your browser or use curl to check health:

```bash
# API Gateway
curl http://localhost:3000/health

# User Service
curl http://localhost:3001/health

# Product Service
curl http://localhost:3002/health

# Order Service
curl http://localhost:3003/health

# Payment Service
curl http://localhost:3004/health
```

All should return status: "running".

### Step 3: Test the Platform

#### 3.1 Register a User

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

#### 3.2 Login

```bash
curl -X POST http://localhost:3000/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

Save the `token` from the response!

#### 3.3 Create Products

```bash
# Create a T-Shirt (Factory Pattern in action)
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

# Create Pants
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

# Create a Jacket
curl -X POST http://localhost:3000/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "type": "jacket",
    "name": "Waterproof Jacket",
    "description": "Perfect for rainy days",
    "price": 89.99,
    "size": "L",
    "color": "Black",
    "material": "Polyester",
    "waterproof": true
  }'
```

#### 3.4 Browse Products

```bash
# Get all products
curl http://localhost:3000/api/products

# Filter by category
curl "http://localhost:3000/api/products?category=tshirt"

# Filter by price range
curl "http://localhost:3000/api/products?minPrice=20&maxPrice=50"

# Filter by color
curl "http://localhost:3000/api/products?color=Blue"
```

#### 3.5 Create an Order (Observer Pattern triggers)

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

Watch the console logs - you'll see Observer Pattern in action:
- Email notification sent
- Inventory reserved
- Analytics tracked
- Shipping prepared

#### 3.6 Process Payment (Strategy Pattern in action)

```bash
# Credit Card Payment
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

# PayPal Payment
curl -X POST http://localhost:3000/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 2,
    "amount": 89.99,
    "method": "paypal",
    "paymentDetails": {
      "email": "john@paypal.com",
      "password": "paypal123"
    }
  }'

# Cryptocurrency Payment
curl -X POST http://localhost:3000/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 3,
    "amount": 59.99,
    "method": "crypto",
    "paymentDetails": {
      "walletAddress": "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
      "cryptocurrency": "Bitcoin"
    }
  }'
```

#### 3.7 Update Order Status

```bash
# Mark as processing
curl -X PATCH http://localhost:3000/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "processing"}'

# Mark as shipped (Observer Pattern triggers shipping notifications)
curl -X PATCH http://localhost:3000/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "shipped"}'

# Mark as delivered
curl -X PATCH http://localhost:3000/api/orders/1/status \
  -H "Content-Type: application/json" \
  -d '{"status": "delivered"}'
```

## 📝 Using Postman

### Import Collection

Create a new Postman collection with these requests:

1. **User Registration** - POST `http://localhost:3000/api/users/register`
2. **User Login** - POST `http://localhost:3000/api/users/login`
3. **Create Product** - POST `http://localhost:3000/api/products`
4. **List Products** - GET `http://localhost:3000/api/products`
5. **Create Order** - POST `http://localhost:3000/api/orders`
6. **Process Payment** - POST `http://localhost:3000/api/payments`

### Environment Variables

Create a Postman environment with:
- `baseUrl`: `http://localhost:3000`
- `token`: `<set after login>`

## 🎯 Testing Design Patterns

### Test Singleton Pattern

The configuration is only created once:

```bash
# Check logs when User Service starts
docker-compose logs user-service

# You'll see config initialization only once
```

### Test Factory Pattern

Create different product types:

```bash
# Each type creates a different class instance
curl -X POST http://localhost:3000/api/products \
  -H "Content-Type: application/json" \
  -d '{"type": "tshirt", ...}'  # Creates TShirt instance

curl -X POST http://localhost:3000/api/products \
  -H "Content-Type: application/json" \
  -d '{"type": "dress", ...}'   # Creates Dress instance
```

### Test Strategy Pattern

Use different payment methods:

```bash
# Each method uses a different strategy
curl -X POST http://localhost:3000/api/payments \
  -H "Content-Type: application/json" \
  -d '{"method": "credit_card", ...}'  # Uses CreditCardStrategy

curl -X POST http://localhost:3000/api/payments \
  -H "Content-Type: application/json" \
  -d '{"method": "paypal", ...}'       # Uses PayPalStrategy
```

### Test Observer Pattern

Create an order and watch the logs:

```bash
# Watch Order Service logs
docker-compose logs -f order-service

# Create order in another terminal
curl -X POST http://localhost:3000/api/orders ...

# You'll see all observers being notified:
# [EMAIL] Sending email notification...
# [INVENTORY] Reserving inventory...
# [ANALYTICS] Recording event...
```

### Test Repository Pattern

All CRUD operations use repositories:

```bash
# Create (uses repository.create)
curl -X POST http://localhost:3000/api/products ...

# Read (uses repository.findAll)
curl http://localhost:3000/api/products

# Update (uses repository.update)
curl -X PUT http://localhost:3000/api/products/1 ...

# Delete (uses repository.delete)
curl -X DELETE http://localhost:3000/api/products/1
```

## 🔍 Viewing Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f order-service

# Last 100 lines
docker-compose logs --tail=100 payment-service
```

## 🛑 Stopping the Platform

```bash
# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

## 🔧 Local Development (Without Docker)

### Terminal 1: API Gateway
```bash
cd services/api-gateway
npm install
npm start
```

### Terminal 2: User Service
```bash
cd services/user-service
npm install
npm start
```

### Terminal 3: Product Service
```bash
cd services/product-service
npm install
npm start
```

### Terminal 4: Order Service
```bash
cd services/order-service
npm install
npm start
```

### Terminal 5: Payment Service
```bash
cd services/payment-service
npm install
npm start
```

## 📊 Monitoring

### Health Checks

```bash
# Check all services
for port in 3000 3001 3002 3003 3004; do
  echo "Port $port:"
  curl -s http://localhost:$port/health | jq
done
```

### Resource Usage

```bash
# Docker stats
docker stats

# Specific service
docker stats api-gateway
```

## ❓ Troubleshooting

### Services won't start

```bash
# Check if ports are already in use
lsof -i :3000
lsof -i :3001

# Kill processes if needed
kill -9 <PID>

# Clean Docker
docker-compose down -v
docker system prune -a
```

### Cannot connect to service

```bash
# Check Docker network
docker network ls
docker network inspect e-commerce-clothes-platform_ecommerce-network

# Restart services
docker-compose restart
```

### See errors in logs

```bash
# Check specific service logs
docker-compose logs api-gateway
docker-compose logs user-service

# Follow logs in real-time
docker-compose logs -f
```

## 🎓 Next Steps

1. Read [DESIGN_PATTERNS.md](./DESIGN_PATTERNS.md) to understand pattern implementations
2. Read [ARCHITECTURE.md](./ARCHITECTURE.md) to understand microservices design
3. Explore the code in each service
4. Try adding new features:
   - Add a new product type (Factory Pattern)
   - Add a new payment method (Strategy Pattern)
   - Add a new notification observer (Observer Pattern)

## 📚 Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Express.js Guide](https://expressjs.com/)
- [REST API Best Practices](https://restfulapi.net/)
- [Microservices Patterns](https://microservices.io/patterns/)

## 💡 Tips

1. **Use JSON viewer**: Install a browser extension for formatted JSON
2. **Save requests**: Create a Postman collection for reuse
3. **Watch logs**: Always have logs running to see pattern behavior
4. **Test incrementally**: Test each service individually first
5. **Use environment files**: Copy `.env.example` to `.env` for configuration

Happy coding! 🚀
