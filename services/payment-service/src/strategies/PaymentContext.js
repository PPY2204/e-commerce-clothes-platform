// Strategy Pattern - Context
const {
  CreditCardStrategy,
  PayPalStrategy,
  CryptoStrategy,
  BankTransferStrategy
} = require('./PaymentStrategies');

class PaymentContext {
  constructor() {
    this.strategies = {
      credit_card: new CreditCardStrategy(),
      paypal: new PayPalStrategy(),
      crypto: new CryptoStrategy(),
      bank_transfer: new BankTransferStrategy()
    };
  }

  setStrategy(method) {
    const strategy = this.strategies[method];
    if (!strategy) {
      throw new Error(`Payment method '${method}' not supported`);
    }
    return strategy;
  }

  getSupportedMethods() {
    return Object.keys(this.strategies);
  }

  async processPayment(payment, paymentDetails) {
    const strategy = this.setStrategy(payment.method);
    
    // Validate payment details
    const validation = strategy.validate(paymentDetails);
    if (!validation.valid) {
      throw new Error(validation.error);
    }
    
    // Process payment
    return await strategy.process(payment, paymentDetails);
  }

  async refundPayment(payment) {
    const strategy = this.setStrategy(payment.method);
    return await strategy.refund(payment);
  }
}

module.exports = PaymentContext;
