// Repository Pattern for Inventory data access
const InventoryItem = require('../models/InventoryItem');

class InventoryRepository {
  constructor() {
    this.inventory = new Map();
  }

  async create(productId, quantity, lowStockThreshold = 10) {
    const item = new InventoryItem(productId, quantity, 0, lowStockThreshold);
    this.inventory.set(productId, item);
    return item;
  }

  async findByProductId(productId) {
    return this.inventory.get(productId);
  }

  async findAll() {
    return Array.from(this.inventory.values());
  }

  async findLowStock() {
    return Array.from(this.inventory.values()).filter(item => item.isLowStock());
  }

  async findOutOfStock() {
    return Array.from(this.inventory.values()).filter(item => item.isOutOfStock());
  }

  async update(productId, data) {
    const item = this.inventory.get(productId);
    if (!item) return null;

    if (data.quantity !== undefined) item.quantity = data.quantity;
    if (data.lowStockThreshold !== undefined) item.lowStockThreshold = data.lowStockThreshold;
    item.lastUpdated = new Date();
    
    return item;
  }

  async delete(productId) {
    return this.inventory.delete(productId);
  }
}

module.exports = InventoryRepository;
