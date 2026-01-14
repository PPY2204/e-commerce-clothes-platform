const express = require('express');
const router = express.Router();
const OrderController = require('../controllers/OrderController');

router.post('/', OrderController.createOrder);
router.get('/', OrderController.getAllOrders);
router.get('/:id', OrderController.getOrder);
router.get('/user/:userId', OrderController.getUserOrders);
router.patch('/:id/status', OrderController.updateOrderStatus);
router.post('/:id/cancel', OrderController.cancelOrder);

module.exports = router;
