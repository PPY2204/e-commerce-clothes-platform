// Repository Pattern for Order data access
const Order = require('../models/Order');

class OrderRepository {
  constructor() {
    this.orders = new Map();
    this.currentId = 1;
  }

  async create(orderData) {
    const order = new Order(
      this.currentId++,
      orderData.userId,
      orderData.items,
      orderData.totalAmount
    );
    this.orders.set(order.id, order);
    return order;
  }

  async findById(id) {
    return this.orders.get(id);
  }

  async findByUserId(userId) {
    return Array.from(this.orders.values()).filter(order => order.userId === userId);
  }

  async findAll() {
    return Array.from(this.orders.values());
  }

  async update(id, orderData) {
    const order = this.orders.get(id);
    if (!order) return null;

    Object.assign(order, orderData);
    order.updatedAt = new Date();
    return order;
  }

  async updateStatus(id, status) {
    const order = this.orders.get(id);
    if (!order) return null;

    order.updateStatus(status);
    return order;
  }

  async delete(id) {
    return this.orders.delete(id);
  }
}

module.exports = OrderRepository;
