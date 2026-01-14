// Factory Pattern for creating different types of products
const { TShirt, Pants, Jacket, Dress } = require('../models/Product');

class ProductFactory {
  static createProduct(type, id, data) {
    switch (type.toLowerCase()) {
      case 'tshirt':
        return new TShirt(
          id,
          data.name,
          data.description,
          data.price,
          data.size,
          data.color,
          data.material
        );
      case 'pants':
        return new Pants(
          id,
          data.name,
          data.description,
          data.price,
          data.size,
          data.color,
          data.waistSize,
          data.length
        );
      case 'jacket':
        return new Jacket(
          id,
          data.name,
          data.description,
          data.price,
          data.size,
          data.color,
          data.material,
          data.waterproof
        );
      case 'dress':
        return new Dress(
          id,
          data.name,
          data.description,
          data.price,
          data.size,
          data.color,
          data.style,
          data.length
        );
      default:
        throw new Error(`Unknown product type: ${type}`);
    }
  }

  static getSupportedTypes() {
    return ['tshirt', 'pants', 'jacket', 'dress'];
  }
}

module.exports = ProductFactory;
