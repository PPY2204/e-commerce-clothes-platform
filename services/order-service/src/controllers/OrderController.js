const OrderRepository = require('../repositories/OrderRepository');
const OrderSubject = require('../observers/OrderSubject');
const {
  EmailNotificationObserver,
  InventoryObserver,
  AnalyticsObserver,
  ShippingObserver
} = require('../observers/OrderObservers');

const orderRepository = new OrderRepository();
const orderSubject = new OrderSubject();

// Attach observers
orderSubject.attach(new EmailNotificationObserver());
orderSubject.attach(new InventoryObserver());
orderSubject.attach(new AnalyticsObserver());
orderSubject.attach(new ShippingObserver());

class OrderController {
  async createOrder(req, res) {
    try {
      const { userId, items } = req.body;

      if (!items || items.length === 0) {
        return res.status(400).json({ message: 'Order must contain at least one item' });
      }

      // Calculate total amount
      const totalAmount = items.reduce((sum, item) => {
        return sum + (item.price * item.quantity);
      }, 0);

      const order = await orderRepository.create({
        userId,
        items,
        totalAmount
      });

      // Notify observers about order creation
      orderSubject.notify(order, 'created');

      res.status(201).json({
        message: 'Order created successfully',
        order: order.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getOrder(req, res) {
    try {
      const { id } = req.params;
      const order = await orderRepository.findById(parseInt(id));

      if (!order) {
        return res.status(404).json({ message: 'Order not found' });
      }

      res.json(order.toJSON());
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getUserOrders(req, res) {
    try {
      const { userId } = req.params;
      const orders = await orderRepository.findByUserId(parseInt(userId));
      res.json(orders.map(order => order.toJSON()));
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getAllOrders(req, res) {
    try {
      const orders = await orderRepository.findAll();
      res.json(orders.map(order => order.toJSON()));
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async updateOrderStatus(req, res) {
    try {
      const { id } = req.params;
      const { status } = req.body;

      const validStatuses = ['pending', 'processing', 'shipped', 'delivered', 'cancelled'];
      if (!validStatuses.includes(status)) {
        return res.status(400).json({
          message: 'Invalid status',
          validStatuses
        });
      }

      const order = await orderRepository.updateStatus(parseInt(id), status);

      if (!order) {
        return res.status(404).json({ message: 'Order not found' });
      }

      // Notify observers about status change
      orderSubject.notify(order, status);

      res.json({
        message: 'Order status updated successfully',
        order: order.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async cancelOrder(req, res) {
    try {
      const { id } = req.params;
      const order = await orderRepository.updateStatus(parseInt(id), 'cancelled');

      if (!order) {
        return res.status(404).json({ message: 'Order not found' });
      }

      // Notify observers about cancellation
      orderSubject.notify(order, 'cancelled');

      res.json({
        message: 'Order cancelled successfully',
        order: order.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }
}

module.exports = new OrderController();
