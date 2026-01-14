const UserProfileRepository = require('../repositories/UserProfileRepository');
const RecommendationContext = require('../engines/RecommendationEngines');

const userProfileRepository = new UserProfileRepository();
const recommendationContext = new RecommendationContext();

// Mock product data (in production, this would come from Product Service)
const mockProducts = [
  { id: 1, name: 'Blue T-Shirt', category: 'tshirt', price: 29.99, color: 'Blue', size: 'M' },
  { id: 2, name: 'Black Jeans', category: 'pants', price: 59.99, color: 'Black', size: '32' },
  { id: 3, name: 'Red Jacket', category: 'jacket', price: 89.99, color: 'Red', size: 'L' },
  { id: 4, name: 'White Dress', category: 'dress', price: 79.99, color: 'White', size: 'M' },
  { id: 5, name: 'Green T-Shirt', category: 'tshirt', price: 25.99, color: 'Green', size: 'L' },
  { id: 6, name: 'Blue Jeans', category: 'pants', price: 69.99, color: 'Blue', size: '34' },
  { id: 7, name: 'Black Jacket', category: 'jacket', price: 99.99, color: 'Black', size: 'XL' },
  { id: 8, name: 'Summer Dress', category: 'dress', price: 65.99, color: 'Yellow', size: 'S' }
];

class RecommendationController {
  async getRecommendations(req, res) {
    try {
      const { userId } = req.params;
      const { algorithm, limit } = req.query;

      const selectedAlgorithm = algorithm || 'hybrid';
      const resultLimit = parseInt(limit) || 5;

      // Validate algorithm
      const supportedAlgorithms = recommendationContext.getSupportedAlgorithms();
      if (!supportedAlgorithms.includes(selectedAlgorithm)) {
        return res.status(400).json({
          message: 'Invalid algorithm',
          supportedAlgorithms
        });
      }

      // Get user profile
      const userProfile = await userProfileRepository.findByUserId(parseInt(userId));

      // Generate recommendations
      const recommendations = await recommendationContext.generateRecommendations(
        selectedAlgorithm,
        userProfile,
        mockProducts,
        resultLimit
      );

      res.json({
        userId: parseInt(userId),
        algorithm: selectedAlgorithm,
        recommendations,
        profile: {
          topCategories: userProfile.getTopCategories(),
          topColors: userProfile.getTopColors(),
          purchaseCount: userProfile.purchaseHistory.length,
          viewCount: userProfile.viewHistory.length
        }
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async updateUserProfile(req, res) {
    try {
      const { userId } = req.params;
      const { action, productId, category, price, attributes } = req.body;

      if (!action || !productId) {
        return res.status(400).json({ 
          message: 'Action and productId are required' 
        });
      }

      const userProfile = await userProfileRepository.findByUserId(parseInt(userId));

      switch (action) {
        case 'purchase':
          userProfile.addPurchase(productId, category, price, attributes || {});
          break;
        case 'view':
          userProfile.addView(productId, category, price, attributes || {});
          break;
        default:
          return res.status(400).json({ 
            message: 'Invalid action. Use "purchase" or "view"' 
          });
      }

      res.json({
        message: 'User profile updated successfully',
        profile: userProfile.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getUserProfile(req, res) {
    try {
      const { userId } = req.params;
      const userProfile = await userProfileRepository.findByUserId(parseInt(userId));

      res.json(userProfile.toJSON());
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getSupportedAlgorithms(req, res) {
    try {
      res.json({
        algorithms: recommendationContext.getSupportedAlgorithms(),
        descriptions: {
          collaborative: 'Based on similar users behavior',
          content_based: 'Based on your preferences and purchase history',
          trending: 'Currently popular products',
          hybrid: 'Combination of multiple algorithms for best results'
        }
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  // Webhook to receive events from other services
  async handleEvent(req, res) {
    try {
      const { event, data } = req.body;

      console.log(`[RECOMMENDATION] Received event: ${event}`);

      switch (event) {
        case 'order_completed':
          // Update user profile with purchase data
          for (const item of data.items) {
            const userProfile = await userProfileRepository.findByUserId(data.userId);
            userProfile.addPurchase(
              item.productId,
              item.category || 'unknown',
              item.price,
              item.attributes || {}
            );
          }
          break;

        case 'product_viewed':
          // Update user profile with view data
          const userProfile = await userProfileRepository.findByUserId(data.userId);
          userProfile.addView(
            data.productId,
            data.category || 'unknown',
            data.price,
            data.attributes || {}
          );
          break;

        default:
          return res.status(400).json({ message: 'Unknown event type' });
      }

      res.json({ message: 'Event processed successfully' });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }
}

module.exports = new RecommendationController();
