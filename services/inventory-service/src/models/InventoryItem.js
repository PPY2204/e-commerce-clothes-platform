class InventoryItem {
  constructor(productId, quantity, reservedQuantity = 0, lowStockThreshold = 10) {
    this.productId = productId;
    this.quantity = quantity;
    this.reservedQuantity = reservedQuantity;
    this.lowStockThreshold = lowStockThreshold;
    this.lastUpdated = new Date();
  }

  get availableQuantity() {
    return this.quantity - this.reservedQuantity;
  }

  isLowStock() {
    return this.availableQuantity <= this.lowStockThreshold;
  }

  isOutOfStock() {
    return this.availableQuantity <= 0;
  }

  reserve(quantity) {
    if (this.availableQuantity >= quantity) {
      this.reservedQuantity += quantity;
      this.lastUpdated = new Date();
      return true;
    }
    return false;
  }

  release(quantity) {
    this.reservedQuantity = Math.max(0, this.reservedQuantity - quantity);
    this.lastUpdated = new Date();
  }

  deduct(quantity) {
    if (this.quantity >= quantity) {
      this.quantity -= quantity;
      this.reservedQuantity = Math.max(0, this.reservedQuantity - quantity);
      this.lastUpdated = new Date();
      return true;
    }
    return false;
  }

  restock(quantity) {
    this.quantity += quantity;
    this.lastUpdated = new Date();
  }

  toJSON() {
    return {
      productId: this.productId,
      quantity: this.quantity,
      reservedQuantity: this.reservedQuantity,
      availableQuantity: this.availableQuantity,
      lowStockThreshold: this.lowStockThreshold,
      isLowStock: this.isLowStock(),
      isOutOfStock: this.isOutOfStock(),
      lastUpdated: this.lastUpdated
    };
  }
}

module.exports = InventoryItem;
