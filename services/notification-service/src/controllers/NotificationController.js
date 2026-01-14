const NotificationRepository = require('../repositories/NotificationRepository');
const NotificationContext = require('../strategies/NotificationStrategies');

const notificationRepository = new NotificationRepository();
const notificationContext = new NotificationContext();

class NotificationController {
  async sendNotification(req, res) {
    try {
      const { type, recipient, subject, message, channel } = req.body;

      if (!type || !recipient || !message) {
        return res.status(400).json({ 
          message: 'Type, recipient, and message are required' 
        });
      }

      // Validate channel
      const supportedChannels = notificationContext.getSupportedChannels();
      const selectedChannel = channel || 'email';
      
      if (!supportedChannels.includes(selectedChannel)) {
        return res.status(400).json({
          message: 'Invalid notification channel',
          supportedChannels
        });
      }

      // Create notification record
      const notification = await notificationRepository.create({
        type,
        recipient,
        subject: subject || `Notification: ${type}`,
        message,
        channel: selectedChannel
      });

      // Send notification using appropriate strategy
      try {
        const result = await notificationContext.sendNotification(notification);
        notification.markAsSent();
        
        res.status(201).json({
          message: 'Notification sent successfully',
          notification: notification.toJSON(),
          result
        });
      } catch (error) {
        notification.markAsFailed(error.message);
        
        res.status(500).json({
          message: 'Failed to send notification',
          notification: notification.toJSON(),
          error: error.message
        });
      }
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getNotification(req, res) {
    try {
      const { id } = req.params;
      const notification = await notificationRepository.findById(parseInt(id));

      if (!notification) {
        return res.status(404).json({ message: 'Notification not found' });
      }

      res.json(notification.toJSON());
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getNotificationsByRecipient(req, res) {
    try {
      const { recipient } = req.params;
      const notifications = await notificationRepository.findByRecipient(recipient);
      res.json(notifications.map(n => n.toJSON()));
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getAllNotifications(req, res) {
    try {
      const { status } = req.query;
      
      let notifications;
      if (status) {
        notifications = await notificationRepository.findByStatus(status);
      } else {
        notifications = await notificationRepository.findAll();
      }
      
      res.json(notifications.map(n => n.toJSON()));
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getSupportedChannels(req, res) {
    try {
      res.json({
        supportedChannels: notificationContext.getSupportedChannels()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  // Webhook for event-driven notifications
  async handleEvent(req, res) {
    try {
      const { event, data } = req.body;

      console.log(`[NOTIFICATION] Received event: ${event}`);

      let notificationData;
      switch (event) {
        case 'order_created':
          notificationData = {
            type: 'order_confirmation',
            recipient: data.userEmail,
            subject: `Order Confirmation - Order #${data.orderId}`,
            message: `Your order #${data.orderId} has been confirmed. Total: $${data.totalAmount}`,
            channel: 'email'
          };
          break;

        case 'payment_completed':
          notificationData = {
            type: 'payment_confirmation',
            recipient: data.userEmail,
            subject: `Payment Confirmation - Order #${data.orderId}`,
            message: `Payment of $${data.amount} has been processed successfully.`,
            channel: 'email'
          };
          break;

        case 'order_shipped':
          notificationData = {
            type: 'shipment_tracking',
            recipient: data.userEmail,
            subject: `Order Shipped - Order #${data.orderId}`,
            message: `Your order has been shipped. Tracking number: ${data.trackingNumber || 'N/A'}`,
            channel: 'email'
          };
          break;

        case 'low_stock_alert':
          notificationData = {
            type: 'low_stock_alert',
            recipient: data.adminEmail,
            subject: `Low Stock Alert - Product #${data.productId}`,
            message: `Product #${data.productId} is running low on stock. Available: ${data.availableQuantity}`,
            channel: 'email'
          };
          break;

        default:
          return res.status(400).json({ message: 'Unknown event type' });
      }

      // Create and send notification
      const notification = await notificationRepository.create(notificationData);
      
      try {
        const result = await notificationContext.sendNotification(notification);
        notification.markAsSent();
        
        res.json({
          message: 'Event processed and notification sent',
          notification: notification.toJSON(),
          result
        });
      } catch (error) {
        notification.markAsFailed(error.message);
        
        res.status(500).json({
          message: 'Event processed but notification failed',
          notification: notification.toJSON(),
          error: error.message
        });
      }
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }
}

module.exports = new NotificationController();
