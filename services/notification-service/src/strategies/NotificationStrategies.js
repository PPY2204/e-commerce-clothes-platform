// Strategy Pattern for different notification channels
class NotificationStrategy {
  async send(notification) {
    throw new Error('send() must be implemented');
  }
}

class EmailNotificationStrategy extends NotificationStrategy {
  async send(notification) {
    console.log(`[EMAIL] Sending email to: ${notification.recipient}`);
    console.log(`[EMAIL] Subject: ${notification.subject}`);
    console.log(`[EMAIL] Message: ${notification.message}`);
    
    // Simulate email sending
    // In production, integrate with SendGrid, AWS SES, etc.
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          success: true,
          messageId: `email-${Date.now()}`
        });
      }, 100);
    });
  }
}

class SMSNotificationStrategy extends NotificationStrategy {
  async send(notification) {
    console.log(`[SMS] Sending SMS to: ${notification.recipient}`);
    console.log(`[SMS] Message: ${notification.message}`);
    
    // Simulate SMS sending
    // In production, integrate with Twilio, AWS SNS, etc.
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          success: true,
          messageId: `sms-${Date.now()}`
        });
      }, 100);
    });
  }
}

class NotificationContext {
  constructor() {
    this.strategies = {
      email: new EmailNotificationStrategy(),
      sms: new SMSNotificationStrategy()
    };
  }

  async sendNotification(notification) {
    const strategy = this.strategies[notification.channel];
    if (!strategy) {
      throw new Error(`Notification channel '${notification.channel}' not supported`);
    }

    try {
      const result = await strategy.send(notification);
      return result;
    } catch (error) {
      throw new Error(`Failed to send notification: ${error.message}`);
    }
  }

  getSupportedChannels() {
    return Object.keys(this.strategies);
  }
}

module.exports = NotificationContext;
