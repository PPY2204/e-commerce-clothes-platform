# Design Patterns Implementation Guide

This document explains how design patterns are implemented in the e-commerce platform.

## Table of Contents
1. [Singleton Pattern](#singleton-pattern)
2. [Repository Pattern](#repository-pattern)
3. [Factory Pattern](#factory-pattern)
4. [Strategy Pattern](#strategy-pattern)
5. [Observer Pattern](#observer-pattern)

---

## Singleton Pattern

**Purpose**: Ensure a class has only one instance and provide a global point of access to it.

### Implementation: Configuration Management

**Location**: `services/user-service/src/config/config.js`

```javascript
class Config {
  constructor() {
    if (Config.instance) {
      return Config.instance;
    }
    this.port = process.env.PORT || 3001;
    this.jwtSecret = process.env.JWT_SECRET || 'your-secret-key';
    Config.instance = this;
  }

  static getInstance() {
    if (!Config.instance) {
      Config.instance = new Config();
    }
    return Config.instance;
  }
}
```

### Benefits
- Single source of truth for configuration
- Lazy initialization
- Thread-safe access to configuration
- Prevents multiple instances with different configs

### Usage
```javascript
const Config = require('./config/config');
const config = Config.getInstance();
console.log(config.port); // 3001
```

---

## Repository Pattern

**Purpose**: Mediates between the domain and data mapping layers, acting like an in-memory collection of domain objects.

### Implementation: Data Access Layer

**Locations**: 
- `services/user-service/src/repositories/UserRepository.js`
- `services/product-service/src/repositories/ProductRepository.js`
- `services/order-service/src/repositories/OrderRepository.js`
- `services/payment-service/src/repositories/PaymentRepository.js`

```javascript
class UserRepository {
  constructor() {
    this.users = new Map();
    this.currentId = 1;
  }

  async create(userData) { /* ... */ }
  async findById(id) { /* ... */ }
  async findByEmail(email) { /* ... */ }
  async findAll() { /* ... */ }
  async update(id, userData) { /* ... */ }
  async delete(id) { /* ... */ }
}
```

### Benefits
- Separates data access logic from business logic
- Easy to swap data sources (memory → database)
- Centralized data access logic
- Testable and mockable
- Consistent interface across all services

### Usage
```javascript
const userRepository = new UserRepository();
const user = await userRepository.create({
  username: 'john',
  email: 'john@example.com'
});
```

---

## Factory Pattern

**Purpose**: Define an interface for creating objects, but let subclasses decide which class to instantiate.

### Implementation: Product Creation

**Location**: `services/product-service/src/factories/ProductFactory.js`

```javascript
class ProductFactory {
  static createProduct(type, id, data) {
    switch (type.toLowerCase()) {
      case 'tshirt':
        return new TShirt(id, data.name, data.price, ...);
      case 'pants':
        return new Pants(id, data.name, data.price, ...);
      case 'jacket':
        return new Jacket(id, data.name, data.price, ...);
      case 'dress':
        return new Dress(id, data.name, data.price, ...);
      default:
        throw new Error(`Unknown product type: ${type}`);
    }
  }
}
```

### Product Hierarchy
```
Product (Base Class)
├── TShirt
├── Pants
├── Jacket
└── Dress
```

### Benefits
- Encapsulates object creation logic
- Easy to add new product types
- Centralized validation
- Type-safe product creation
- Follows Open/Closed Principle

### Usage
```javascript
const product = ProductFactory.createProduct('tshirt', 1, {
  name: 'Blue T-Shirt',
  price: 29.99,
  size: 'M',
  color: 'Blue',
  material: 'Cotton'
});
```

---

## Strategy Pattern

**Purpose**: Define a family of algorithms, encapsulate each one, and make them interchangeable.

### Implementation: Payment Methods

**Location**: `services/payment-service/src/strategies/`

```javascript
// Base Strategy
class PaymentStrategy {
  async process(payment) { /* ... */ }
  async refund(payment) { /* ... */ }
  validate(paymentDetails) { /* ... */ }
}

// Concrete Strategies
class CreditCardStrategy extends PaymentStrategy { /* ... */ }
class PayPalStrategy extends PaymentStrategy { /* ... */ }
class CryptoStrategy extends PaymentStrategy { /* ... */ }
class BankTransferStrategy extends PaymentStrategy { /* ... */ }
```

### Context Class
```javascript
class PaymentContext {
  constructor() {
    this.strategies = {
      credit_card: new CreditCardStrategy(),
      paypal: new PayPalStrategy(),
      crypto: new CryptoStrategy(),
      bank_transfer: new BankTransferStrategy()
    };
  }

  async processPayment(payment, paymentDetails) {
    const strategy = this.setStrategy(payment.method);
    return await strategy.process(payment, paymentDetails);
  }
}
```

### Benefits
- Easy to add new payment methods
- Each payment method encapsulates its own logic
- Runtime selection of payment strategy
- Follows Single Responsibility Principle
- Eliminates conditional statements

### Usage
```javascript
const paymentContext = new PaymentContext();
const result = await paymentContext.processPayment(payment, {
  cardNumber: '4111111111111111',
  cvv: '123',
  expiryDate: '12/25'
});
```

### Supported Payment Methods
1. **Credit Card**: Traditional card payment
2. **PayPal**: Online payment service
3. **Cryptocurrency**: Blockchain-based payment
4. **Bank Transfer**: Direct bank transfer

---

## Observer Pattern

**Purpose**: Define a one-to-many dependency between objects so that when one object changes state, all its dependents are notified automatically.

### Implementation: Order Status Notifications

**Location**: `services/order-service/src/observers/`

```javascript
// Subject
class OrderSubject {
  constructor() {
    this.observers = [];
  }

  attach(observer) {
    this.observers.push(observer);
  }

  notify(order, event) {
    this.observers.forEach(observer => {
      observer.update(order, event);
    });
  }
}

// Base Observer
class OrderObserver {
  update(order, event) {
    throw new Error('update() must be implemented');
  }
}

// Concrete Observers
class EmailNotificationObserver extends OrderObserver { /* ... */ }
class InventoryObserver extends OrderObserver { /* ... */ }
class AnalyticsObserver extends OrderObserver { /* ... */ }
class ShippingObserver extends OrderObserver { /* ... */ }
```

### Observer Workflow
```
Order Status Change
       │
       ▼
   notify()
       │
       ├──► EmailNotificationObserver → Send email
       ├──► InventoryObserver → Update inventory
       ├──► AnalyticsObserver → Track metrics
       └──► ShippingObserver → Prepare shipment
```

### Benefits
- Loose coupling between order and notification systems
- Easy to add new observers
- Follows Open/Closed Principle
- Real-time event handling
- Scalable notification system

### Usage
```javascript
const orderSubject = new OrderSubject();

// Attach observers
orderSubject.attach(new EmailNotificationObserver());
orderSubject.attach(new InventoryObserver());
orderSubject.attach(new AnalyticsObserver());
orderSubject.attach(new ShippingObserver());

// Notify all observers when order is created
orderSubject.notify(order, 'created');

// Notify all observers when order status changes
orderSubject.notify(order, 'shipped');
```

### Observer Actions by Event

| Event | Email | Inventory | Analytics | Shipping |
|-------|-------|-----------|-----------|----------|
| created | ✓ Confirmation | ✓ Reserve | ✓ Track | ✗ |
| processing | ✓ Update | ✗ | ✓ Track | ✓ Prepare |
| shipped | ✓ Tracking | ✗ | ✓ Track | ✓ Update |
| delivered | ✓ Receipt | ✗ | ✓ Track | ✗ |
| cancelled | ✓ Notice | ✓ Release | ✓ Track | ✗ |

---

## Pattern Interactions

### Complete Order Flow

```
1. User creates order
   ↓
2. Repository Pattern saves order
   ↓
3. Observer Pattern notifies:
   - Email: Sends confirmation
   - Inventory: Reserves items
   - Analytics: Tracks order
   - Shipping: Prepares shipment
   ↓
4. User processes payment
   ↓
5. Strategy Pattern selects payment method
   ↓
6. Repository Pattern saves payment
   ↓
7. Observer Pattern notifies order status change
```

### Design Pattern Summary

| Pattern | Service | Purpose | Files |
|---------|---------|---------|-------|
| Singleton | User Service | Config management | `config/config.js` |
| Repository | All Services | Data access | `repositories/*.js` |
| Factory | Product Service | Product creation | `factories/ProductFactory.js` |
| Strategy | Payment Service | Payment methods | `strategies/*.js` |
| Observer | Order Service | Event notifications | `observers/*.js` |

---

## Best Practices

1. **Singleton Pattern**
   - Use for configuration, logging, caching
   - Ensure thread-safety
   - Lazy initialization when possible

2. **Repository Pattern**
   - Keep repository methods simple
   - Don't expose internal data structures
   - Use async/await for future database integration

3. **Factory Pattern**
   - Validate input before object creation
   - Return consistent object types
   - Document supported types

4. **Strategy Pattern**
   - Each strategy should be independent
   - Use composition over inheritance
   - Validate inputs in each strategy

5. **Observer Pattern**
   - Keep observers independent
   - Handle errors in individual observers
   - Use async operations when needed

---

## Extending the Patterns

### Adding a New Product Type
```javascript
// 1. Create new product class
class Shoes extends Product { /* ... */ }

// 2. Add to factory
case 'shoes':
  return new Shoes(id, data.name, ...);
```

### Adding a New Payment Method
```javascript
// 1. Create new strategy
class ApplePayStrategy extends PaymentStrategy { /* ... */ }

// 2. Register in context
this.strategies.apple_pay = new ApplePayStrategy();
```

### Adding a New Observer
```javascript
// 1. Create new observer
class SMSNotificationObserver extends OrderObserver { /* ... */ }

// 2. Attach to subject
orderSubject.attach(new SMSNotificationObserver());
```

---

## Conclusion

These design patterns provide:
- **Flexibility**: Easy to extend and modify
- **Maintainability**: Clear separation of concerns
- **Testability**: Each component can be tested independently
- **Scalability**: Can handle growing requirements
- **Reusability**: Patterns can be applied across services
