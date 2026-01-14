class UserProfile {
  constructor(userId) {
    this.userId = userId;
    this.purchaseHistory = [];
    this.viewHistory = [];
    this.preferences = {
      categories: {},
      priceRange: { min: 0, max: Infinity },
      colors: {},
      sizes: {}
    };
    this.lastUpdated = new Date();
  }

  addPurchase(productId, category, price, attributes) {
    this.purchaseHistory.push({
      productId,
      category,
      price,
      attributes,
      timestamp: new Date()
    });
    this.updatePreferences(category, price, attributes);
  }

  addView(productId, category, price, attributes) {
    this.viewHistory.push({
      productId,
      category,
      price,
      attributes,
      timestamp: new Date()
    });
  }

  updatePreferences(category, price, attributes) {
    // Update category preferences
    this.preferences.categories[category] = (this.preferences.categories[category] || 0) + 1;

    // Update price range
    if (price < this.preferences.priceRange.min || this.preferences.priceRange.min === 0) {
      this.preferences.priceRange.min = price;
    }
    if (price > this.preferences.priceRange.max || this.preferences.priceRange.max === Infinity) {
      this.preferences.priceRange.max = price;
    }

    // Update color preferences
    if (attributes.color) {
      this.preferences.colors[attributes.color] = (this.preferences.colors[attributes.color] || 0) + 1;
    }

    // Update size preferences
    if (attributes.size) {
      this.preferences.sizes[attributes.size] = (this.preferences.sizes[attributes.size] || 0) + 1;
    }

    this.lastUpdated = new Date();
  }

  getTopCategories(limit = 3) {
    return Object.entries(this.preferences.categories)
      .sort(([, a], [, b]) => b - a)
      .slice(0, limit)
      .map(([category]) => category);
  }

  getTopColors(limit = 3) {
    return Object.entries(this.preferences.colors)
      .sort(([, a], [, b]) => b - a)
      .slice(0, limit)
      .map(([color]) => color);
  }

  toJSON() {
    return {
      userId: this.userId,
      purchaseHistory: this.purchaseHistory,
      viewHistory: this.viewHistory,
      preferences: this.preferences,
      lastUpdated: this.lastUpdated
    };
  }
}

module.exports = UserProfile;
