// Strategy Pattern - Base Payment Strategy
class PaymentStrategy {
  async process(payment) {
    throw new Error('process() must be implemented');
  }

  async refund(payment) {
    throw new Error('refund() must be implemented');
  }

  validate(paymentDetails) {
    throw new Error('validate() must be implemented');
  }
}

// Credit Card Payment Strategy
class CreditCardStrategy extends PaymentStrategy {
  validate(paymentDetails) {
    const { cardNumber, cvv, expiryDate } = paymentDetails;
    
    if (!cardNumber || cardNumber.length < 13) {
      return { valid: false, error: 'Invalid card number' };
    }
    
    if (!cvv || cvv.length < 3) {
      return { valid: false, error: 'Invalid CVV' };
    }
    
    if (!expiryDate) {
      return { valid: false, error: 'Invalid expiry date' };
    }
    
    return { valid: true };
  }

  async process(payment, paymentDetails) {
    console.log(`[CREDIT_CARD] Processing payment ${payment.id} for $${payment.amount}`);
    
    // Simulate payment processing
    const transactionId = `CC-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    
    // In production, integrate with payment gateway (Stripe, etc.)
    return {
      success: true,
      transactionId,
      message: 'Credit card payment processed successfully'
    };
  }

  async refund(payment) {
    console.log(`[CREDIT_CARD] Refunding payment ${payment.id}`);
    return {
      success: true,
      message: 'Credit card refund processed successfully'
    };
  }
}

// PayPal Payment Strategy
class PayPalStrategy extends PaymentStrategy {
  validate(paymentDetails) {
    const { email, password } = paymentDetails;
    
    if (!email || !email.includes('@')) {
      return { valid: false, error: 'Invalid PayPal email' };
    }
    
    if (!password) {
      return { valid: false, error: 'PayPal password required' };
    }
    
    return { valid: true };
  }

  async process(payment, paymentDetails) {
    console.log(`[PAYPAL] Processing payment ${payment.id} for $${payment.amount}`);
    
    const transactionId = `PP-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    
    // In production, integrate with PayPal API
    return {
      success: true,
      transactionId,
      message: 'PayPal payment processed successfully'
    };
  }

  async refund(payment) {
    console.log(`[PAYPAL] Refunding payment ${payment.id}`);
    return {
      success: true,
      message: 'PayPal refund processed successfully'
    };
  }
}

// Cryptocurrency Payment Strategy
class CryptoStrategy extends PaymentStrategy {
  validate(paymentDetails) {
    const { walletAddress, cryptocurrency } = paymentDetails;
    
    if (!walletAddress || walletAddress.length < 26) {
      return { valid: false, error: 'Invalid wallet address' };
    }
    
    if (!cryptocurrency) {
      return { valid: false, error: 'Cryptocurrency type required' };
    }
    
    return { valid: true };
  }

  async process(payment, paymentDetails) {
    console.log(`[CRYPTO] Processing payment ${payment.id} for $${payment.amount}`);
    console.log(`[CRYPTO] Cryptocurrency: ${paymentDetails.cryptocurrency}`);
    
    const transactionId = `CRYPTO-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    
    // In production, integrate with crypto payment gateway
    return {
      success: true,
      transactionId,
      message: 'Cryptocurrency payment processed successfully'
    };
  }

  async refund(payment) {
    console.log(`[CRYPTO] Refunding payment ${payment.id}`);
    return {
      success: true,
      message: 'Cryptocurrency refund processed successfully'
    };
  }
}

// Bank Transfer Payment Strategy
class BankTransferStrategy extends PaymentStrategy {
  validate(paymentDetails) {
    const { accountNumber, routingNumber, bankName } = paymentDetails;
    
    if (!accountNumber || accountNumber.length < 8) {
      return { valid: false, error: 'Invalid account number' };
    }
    
    if (!routingNumber) {
      return { valid: false, error: 'Routing number required' };
    }
    
    if (!bankName) {
      return { valid: false, error: 'Bank name required' };
    }
    
    return { valid: true };
  }

  async process(payment, paymentDetails) {
    console.log(`[BANK_TRANSFER] Processing payment ${payment.id} for $${payment.amount}`);
    
    const transactionId = `BT-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    
    // In production, integrate with banking API
    return {
      success: true,
      transactionId,
      message: 'Bank transfer payment processed successfully'
    };
  }

  async refund(payment) {
    console.log(`[BANK_TRANSFER] Refunding payment ${payment.id}`);
    return {
      success: true,
      message: 'Bank transfer refund processed successfully'
    };
  }
}

module.exports = {
  PaymentStrategy,
  CreditCardStrategy,
  PayPalStrategy,
  CryptoStrategy,
  BankTransferStrategy
};
