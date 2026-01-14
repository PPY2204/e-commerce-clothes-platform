// Repository Pattern for Payment data access
const Payment = require('../models/Payment');

class PaymentRepository {
  constructor() {
    this.payments = new Map();
    this.currentId = 1;
  }

  async create(paymentData) {
    const payment = new Payment(
      this.currentId++,
      paymentData.orderId,
      paymentData.amount,
      paymentData.method,
      paymentData.status
    );
    this.payments.set(payment.id, payment);
    return payment;
  }

  async findById(id) {
    return this.payments.get(id);
  }

  async findByOrderId(orderId) {
    return Array.from(this.payments.values()).find(p => p.orderId === orderId);
  }

  async findAll() {
    return Array.from(this.payments.values());
  }

  async update(id, paymentData) {
    const payment = this.payments.get(id);
    if (!payment) return null;

    Object.assign(payment, paymentData);
    payment.updatedAt = new Date();
    return payment;
  }
}

module.exports = PaymentRepository;
