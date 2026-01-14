const ProductFactory = require('../factories/ProductFactory');
const ProductRepository = require('../repositories/ProductRepository');

const productRepository = new ProductRepository();

class ProductController {
  async createProduct(req, res) {
    try {
      const { type, ...productData } = req.body;

      if (!ProductFactory.getSupportedTypes().includes(type.toLowerCase())) {
        return res.status(400).json({
          message: 'Invalid product type',
          supportedTypes: ProductFactory.getSupportedTypes()
        });
      }

      const id = productRepository.getNextId();
      const product = ProductFactory.createProduct(type, id, productData);
      await productRepository.create(product);

      res.status(201).json({
        message: 'Product created successfully',
        product: product.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getProduct(req, res) {
    try {
      const { id } = req.params;
      const product = await productRepository.findById(parseInt(id));

      if (!product) {
        return res.status(404).json({ message: 'Product not found' });
      }

      res.json(product.toJSON());
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getAllProducts(req, res) {
    try {
      const filters = {
        category: req.query.category,
        minPrice: req.query.minPrice ? parseFloat(req.query.minPrice) : undefined,
        maxPrice: req.query.maxPrice ? parseFloat(req.query.maxPrice) : undefined,
        color: req.query.color
      };

      const products = await productRepository.findAll(filters);
      res.json(products.map(p => p.toJSON()));
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async updateProduct(req, res) {
    try {
      const { id } = req.params;
      const product = await productRepository.update(parseInt(id), req.body);

      if (!product) {
        return res.status(404).json({ message: 'Product not found' });
      }

      res.json({
        message: 'Product updated successfully',
        product: product.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async deleteProduct(req, res) {
    try {
      const { id } = req.params;
      const deleted = await productRepository.delete(parseInt(id));

      if (!deleted) {
        return res.status(404).json({ message: 'Product not found' });
      }

      res.json({ message: 'Product deleted successfully' });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }
}

module.exports = new ProductController();
