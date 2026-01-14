// Repository Pattern for Product data access
class ProductRepository {
  constructor() {
    this.products = new Map();
    this.currentId = 1;
  }

  async create(product) {
    this.products.set(product.id, product);
    return product;
  }

  async findById(id) {
    return this.products.get(id);
  }

  async findAll(filters = {}) {
    let products = Array.from(this.products.values());

    if (filters.category) {
      products = products.filter(p => p.category === filters.category);
    }

    if (filters.minPrice) {
      products = products.filter(p => p.price >= filters.minPrice);
    }

    if (filters.maxPrice) {
      products = products.filter(p => p.price <= filters.maxPrice);
    }

    if (filters.color) {
      products = products.filter(p => p.color === filters.color);
    }

    return products;
  }

  async update(id, productData) {
    const product = this.products.get(id);
    if (!product) return null;

    Object.assign(product, productData);
    return product;
  }

  async delete(id) {
    return this.products.delete(id);
  }

  getNextId() {
    return this.currentId++;
  }
}

module.exports = ProductRepository;
