const crypto = require('crypto');

class TransactionIdGenerator {
  /**
   * Generates a cryptographically secure transaction ID
   * @param {string} prefix - Prefix for the transaction ID (e.g., 'CC', 'PP', 'CRYPTO', 'BT')
   * @returns {string} Unique transaction ID
   */
  static generate(prefix) {
    const timestamp = Date.now();
    const randomId = crypto.randomUUID().substring(0, 8);
    return `${prefix}-${timestamp}-${randomId}`;
  }
}

module.exports = TransactionIdGenerator;
