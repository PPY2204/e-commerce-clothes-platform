// Repository Pattern for Notification data access
const Notification = require('../models/Notification');

class NotificationRepository {
  constructor() {
    this.notifications = new Map();
    this.currentId = 1;
  }

  async create(notificationData) {
    const notification = new Notification(
      this.currentId++,
      notificationData.type,
      notificationData.recipient,
      notificationData.subject,
      notificationData.message,
      notificationData.channel
    );
    this.notifications.set(notification.id, notification);
    return notification;
  }

  async findById(id) {
    return this.notifications.get(id);
  }

  async findByRecipient(recipient) {
    return Array.from(this.notifications.values()).filter(n => n.recipient === recipient);
  }

  async findByStatus(status) {
    return Array.from(this.notifications.values()).filter(n => n.status === status);
  }

  async findAll() {
    return Array.from(this.notifications.values());
  }
}

module.exports = NotificationRepository;
