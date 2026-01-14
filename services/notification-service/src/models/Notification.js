class Notification {
  constructor(id, type, recipient, subject, message, channel = 'email') {
    this.id = id;
    this.type = type; // order_confirmation, payment_confirmation, shipment_tracking, etc.
    this.recipient = recipient; // email or phone number
    this.subject = subject;
    this.message = message;
    this.channel = channel; // email, sms
    this.status = 'pending'; // pending, sent, failed
    this.sentAt = null;
    this.createdAt = new Date();
  }

  markAsSent() {
    this.status = 'sent';
    this.sentAt = new Date();
  }

  markAsFailed(error) {
    this.status = 'failed';
    this.error = error;
  }

  toJSON() {
    return {
      id: this.id,
      type: this.type,
      recipient: this.recipient,
      subject: this.subject,
      message: this.message,
      channel: this.channel,
      status: this.status,
      sentAt: this.sentAt,
      createdAt: this.createdAt,
      error: this.error
    };
  }
}

module.exports = Notification;
