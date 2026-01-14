const express = require('express');
const router = express.Router();
const PaymentController = require('../controllers/PaymentController');

router.post('/', PaymentController.createPayment);
router.get('/', PaymentController.getAllPayments);
router.get('/methods', PaymentController.getSupportedMethods);
router.get('/:id', PaymentController.getPayment);
router.get('/order/:orderId', PaymentController.getPaymentByOrder);
router.post('/:id/refund', PaymentController.refundPayment);

module.exports = router;
