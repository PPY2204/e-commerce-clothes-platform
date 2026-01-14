// Observer Pattern - Base Observer
class OrderObserver {
  update(order, event) {
    throw new Error('update() must be implemented');
  }
}

// Email Notification Observer
class EmailNotificationObserver extends OrderObserver {
  update(order, event) {
    console.log(`[EMAIL] Sending email notification for order ${order.id}`);
    console.log(`[EMAIL] Event: ${event}, Status: ${order.status}`);
    // In production, integrate with email service
  }
}

// Inventory Observer
class InventoryObserver extends OrderObserver {
  update(order, event) {
    if (event === 'created') {
      console.log(`[INVENTORY] Reserving inventory for order ${order.id}`);
      // In production, call inventory service to reserve items
    } else if (event === 'cancelled') {
      console.log(`[INVENTORY] Releasing inventory for order ${order.id}`);
      // In production, call inventory service to release items
    }
  }
}

// Analytics Observer
class AnalyticsObserver extends OrderObserver {
  update(order, event) {
    console.log(`[ANALYTICS] Recording event: ${event} for order ${order.id}`);
    console.log(`[ANALYTICS] Order value: $${order.totalAmount}`);
    // In production, send to analytics service
  }
}

// Shipping Observer
class ShippingObserver extends OrderObserver {
  update(order, event) {
    if (event === 'processing') {
      console.log(`[SHIPPING] Preparing shipment for order ${order.id}`);
      // In production, integrate with shipping service
    } else if (event === 'shipped') {
      console.log(`[SHIPPING] Order ${order.id} has been shipped`);
    }
  }
}

module.exports = {
  OrderObserver,
  EmailNotificationObserver,
  InventoryObserver,
  AnalyticsObserver,
  ShippingObserver
};
