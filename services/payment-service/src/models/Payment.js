class Payment {
  constructor(id, orderId, amount, method, status = 'pending') {
    this.id = id;
    this.orderId = orderId;
    this.amount = amount;
    this.method = method; // credit_card, paypal, crypto, bank_transfer
    this.status = status; // pending, processing, completed, failed, refunded
    this.transactionId = null;
    this.createdAt = new Date();
    this.updatedAt = new Date();
  }

  complete(transactionId) {
    this.status = 'completed';
    this.transactionId = transactionId;
    this.updatedAt = new Date();
  }

  fail(reason) {
    this.status = 'failed';
    this.failureReason = reason;
    this.updatedAt = new Date();
  }

  refund() {
    this.status = 'refunded';
    this.updatedAt = new Date();
  }

  toJSON() {
    return {
      id: this.id,
      orderId: this.orderId,
      amount: this.amount,
      method: this.method,
      status: this.status,
      transactionId: this.transactionId,
      failureReason: this.failureReason,
      createdAt: this.createdAt,
      updatedAt: this.updatedAt
    };
  }
}

module.exports = Payment;
