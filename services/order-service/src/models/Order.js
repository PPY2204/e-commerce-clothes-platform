class Order {
  constructor(id, userId, items, totalAmount) {
    this.id = id;
    this.userId = userId;
    this.items = items; // Array of {productId, quantity, price}
    this.totalAmount = totalAmount;
    this.status = 'pending'; // pending, processing, shipped, delivered, cancelled
    this.createdAt = new Date();
    this.updatedAt = new Date();
  }

  updateStatus(newStatus) {
    this.status = newStatus;
    this.updatedAt = new Date();
  }

  toJSON() {
    return {
      id: this.id,
      userId: this.userId,
      items: this.items,
      totalAmount: this.totalAmount,
      status: this.status,
      createdAt: this.createdAt,
      updatedAt: this.updatedAt
    };
  }
}

module.exports = Order;
