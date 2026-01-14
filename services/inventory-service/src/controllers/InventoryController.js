const InventoryRepository = require('../repositories/InventoryRepository');

const inventoryRepository = new InventoryRepository();

class InventoryController {
  async createInventoryItem(req, res) {
    try {
      const { productId, quantity, lowStockThreshold } = req.body;

      if (!productId || quantity === undefined) {
        return res.status(400).json({ message: 'Product ID and quantity are required' });
      }

      const existingItem = await inventoryRepository.findByProductId(productId);
      if (existingItem) {
        return res.status(400).json({ message: 'Inventory item already exists for this product' });
      }

      const item = await inventoryRepository.create(productId, quantity, lowStockThreshold);

      res.status(201).json({
        message: 'Inventory item created successfully',
        item: item.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getInventoryItem(req, res) {
    try {
      const { productId } = req.params;
      const item = await inventoryRepository.findByProductId(parseInt(productId));

      if (!item) {
        return res.status(404).json({ message: 'Inventory item not found' });
      }

      res.json(item.toJSON());
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getAllInventory(req, res) {
    try {
      const items = await inventoryRepository.findAll();
      res.json(items.map(item => item.toJSON()));
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getLowStockItems(req, res) {
    try {
      const items = await inventoryRepository.findLowStock();
      res.json(items.map(item => item.toJSON()));
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getOutOfStockItems(req, res) {
    try {
      const items = await inventoryRepository.findOutOfStock();
      res.json(items.map(item => item.toJSON()));
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async reserveInventory(req, res) {
    try {
      const { productId } = req.params;
      const { quantity } = req.body;

      if (!quantity || quantity <= 0) {
        return res.status(400).json({ message: 'Valid quantity required' });
      }

      const item = await inventoryRepository.findByProductId(parseInt(productId));
      
      if (!item) {
        return res.status(404).json({ message: 'Inventory item not found' });
      }

      const reserved = item.reserve(quantity);
      
      if (!reserved) {
        return res.status(400).json({ 
          message: 'Insufficient inventory',
          available: item.availableQuantity,
          requested: quantity
        });
      }

      res.json({
        message: 'Inventory reserved successfully',
        item: item.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async releaseInventory(req, res) {
    try {
      const { productId } = req.params;
      const { quantity } = req.body;

      if (!quantity || quantity <= 0) {
        return res.status(400).json({ message: 'Valid quantity required' });
      }

      const item = await inventoryRepository.findByProductId(parseInt(productId));
      
      if (!item) {
        return res.status(404).json({ message: 'Inventory item not found' });
      }

      item.release(quantity);

      res.json({
        message: 'Inventory released successfully',
        item: item.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async deductInventory(req, res) {
    try {
      const { productId } = req.params;
      const { quantity } = req.body;

      if (!quantity || quantity <= 0) {
        return res.status(400).json({ message: 'Valid quantity required' });
      }

      const item = await inventoryRepository.findByProductId(parseInt(productId));
      
      if (!item) {
        return res.status(404).json({ message: 'Inventory item not found' });
      }

      const deducted = item.deduct(quantity);
      
      if (!deducted) {
        return res.status(400).json({ 
          message: 'Insufficient inventory to deduct',
          available: item.quantity,
          requested: quantity
        });
      }

      res.json({
        message: 'Inventory deducted successfully',
        item: item.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async restockInventory(req, res) {
    try {
      const { productId } = req.params;
      const { quantity } = req.body;

      if (!quantity || quantity <= 0) {
        return res.status(400).json({ message: 'Valid quantity required' });
      }

      const item = await inventoryRepository.findByProductId(parseInt(productId));
      
      if (!item) {
        return res.status(404).json({ message: 'Inventory item not found' });
      }

      item.restock(quantity);

      res.json({
        message: 'Inventory restocked successfully',
        item: item.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async updateInventoryItem(req, res) {
    try {
      const { productId } = req.params;
      const item = await inventoryRepository.update(parseInt(productId), req.body);

      if (!item) {
        return res.status(404).json({ message: 'Inventory item not found' });
      }

      res.json({
        message: 'Inventory item updated successfully',
        item: item.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async deleteInventoryItem(req, res) {
    try {
      const { productId } = req.params;
      const deleted = await inventoryRepository.delete(parseInt(productId));

      if (!deleted) {
        return res.status(404).json({ message: 'Inventory item not found' });
      }

      res.json({ message: 'Inventory item deleted successfully' });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }
}

module.exports = new InventoryController();
