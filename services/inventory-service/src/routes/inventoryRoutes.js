const express = require('express');
const router = express.Router();
const InventoryController = require('../controllers/InventoryController');

router.post('/', InventoryController.createInventoryItem);
router.get('/', InventoryController.getAllInventory);
router.get('/low-stock', InventoryController.getLowStockItems);
router.get('/out-of-stock', InventoryController.getOutOfStockItems);
router.get('/:productId', InventoryController.getInventoryItem);
router.put('/:productId', InventoryController.updateInventoryItem);
router.delete('/:productId', InventoryController.deleteInventoryItem);
router.post('/:productId/reserve', InventoryController.reserveInventory);
router.post('/:productId/release', InventoryController.releaseInventory);
router.post('/:productId/deduct', InventoryController.deductInventory);
router.post('/:productId/restock', InventoryController.restockInventory);

module.exports = router;
