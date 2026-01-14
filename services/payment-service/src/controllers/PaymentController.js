const PaymentRepository = require('../repositories/PaymentRepository');
const PaymentContext = require('../strategies/PaymentContext');

const paymentRepository = new PaymentRepository();
const paymentContext = new PaymentContext();

class PaymentController {
  async createPayment(req, res) {
    try {
      const { orderId, amount, method, paymentDetails } = req.body;

      // Validate payment method
      if (!paymentContext.getSupportedMethods().includes(method)) {
        return res.status(400).json({
          message: 'Invalid payment method',
          supportedMethods: paymentContext.getSupportedMethods()
        });
      }

      // Create payment record
      const payment = await paymentRepository.create({
        orderId,
        amount,
        method,
        status: 'processing'
      });

      try {
        // Process payment using appropriate strategy
        const result = await paymentContext.processPayment(payment, paymentDetails);

        if (result.success) {
          payment.complete(result.transactionId);
          await paymentRepository.update(payment.id, payment);

          res.status(201).json({
            message: 'Payment processed successfully',
            payment: payment.toJSON()
          });
        } else {
          payment.fail(result.message);
          await paymentRepository.update(payment.id, payment);

          res.status(400).json({
            message: 'Payment processing failed',
            payment: payment.toJSON()
          });
        }
      } catch (error) {
        payment.fail(error.message);
        await paymentRepository.update(payment.id, payment);

        res.status(400).json({
          message: 'Payment processing failed',
          error: error.message,
          payment: payment.toJSON()
        });
      }
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getPayment(req, res) {
    try {
      const { id } = req.params;
      const payment = await paymentRepository.findById(parseInt(id));

      if (!payment) {
        return res.status(404).json({ message: 'Payment not found' });
      }

      res.json(payment.toJSON());
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getPaymentByOrder(req, res) {
    try {
      const { orderId } = req.params;
      const payment = await paymentRepository.findByOrderId(parseInt(orderId));

      if (!payment) {
        return res.status(404).json({ message: 'Payment not found for this order' });
      }

      res.json(payment.toJSON());
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getAllPayments(req, res) {
    try {
      const payments = await paymentRepository.findAll();
      res.json(payments.map(p => p.toJSON()));
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async refundPayment(req, res) {
    try {
      const { id } = req.params;
      const payment = await paymentRepository.findById(parseInt(id));

      if (!payment) {
        return res.status(404).json({ message: 'Payment not found' });
      }

      if (payment.status !== 'completed') {
        return res.status(400).json({
          message: 'Only completed payments can be refunded',
          currentStatus: payment.status
        });
      }

      // Process refund using appropriate strategy
      const result = await paymentContext.refundPayment(payment);

      if (result.success) {
        payment.refund();
        await paymentRepository.update(payment.id, payment);

        res.json({
          message: 'Payment refunded successfully',
          payment: payment.toJSON()
        });
      } else {
        res.status(400).json({
          message: 'Refund failed',
          error: result.message
        });
      }
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getSupportedMethods(req, res) {
    try {
      res.json({
        supportedMethods: paymentContext.getSupportedMethods()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }
}

module.exports = new PaymentController();
