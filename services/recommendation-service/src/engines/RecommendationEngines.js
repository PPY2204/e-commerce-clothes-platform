// Strategy Pattern for different recommendation algorithms
class RecommendationEngine {
  async generateRecommendations(userProfile, products, limit) {
    throw new Error('generateRecommendations() must be implemented');
  }
}

// Collaborative Filtering Engine (simulated)
class CollaborativeFilteringEngine extends RecommendationEngine {
  async generateRecommendations(userProfile, products, limit = 5) {
    console.log(`[COLLABORATIVE] Generating recommendations for user ${userProfile.userId}`);
    
    // Simulate collaborative filtering
    // In production, this would analyze similar users' behavior
    const topCategories = userProfile.getTopCategories();
    
    const recommendations = products
      .filter(p => topCategories.includes(p.category))
      .sort(() => Math.random() - 0.5) // Random sorting for simulation
      .slice(0, limit)
      .map(p => ({
        productId: p.id,
        name: p.name,
        category: p.category,
        price: p.price,
        score: Math.random(),
        reason: 'Users like you also liked this'
      }));

    return recommendations;
  }
}

// Content-Based Filtering Engine
class ContentBasedEngine extends RecommendationEngine {
  async generateRecommendations(userProfile, products, limit = 5) {
    console.log(`[CONTENT_BASED] Generating recommendations for user ${userProfile.userId}`);
    
    const topCategories = userProfile.getTopCategories();
    const topColors = userProfile.getTopColors();
    const priceRange = userProfile.preferences.priceRange;

    const recommendations = products
      .filter(p => {
        // Filter by preferred categories
        if (topCategories.length > 0 && !topCategories.includes(p.category)) {
          return false;
        }
        
        // Filter by price range (with some flexibility)
        if (priceRange.min > 0 && priceRange.max < Infinity) {
          const minPrice = priceRange.min * 0.7;
          const maxPrice = priceRange.max * 1.3;
          if (p.price < minPrice || p.price > maxPrice) {
            return false;
          }
        }
        
        return true;
      })
      .map(p => {
        // Calculate score based on attributes
        let score = 0;
        
        if (topCategories.includes(p.category)) score += 0.5;
        if (p.color && topColors.includes(p.color)) score += 0.3;
        
        // Price similarity
        if (priceRange.min > 0 && priceRange.max < Infinity) {
          const avgPrice = (priceRange.min + priceRange.max) / 2;
          const priceDiff = Math.abs(p.price - avgPrice) / avgPrice;
          score += (1 - priceDiff) * 0.2;
        }
        
        return {
          productId: p.id,
          name: p.name,
          category: p.category,
          price: p.price,
          score,
          reason: 'Based on your preferences'
        };
      })
      .sort((a, b) => b.score - a.score)
      .slice(0, limit);

    return recommendations;
  }
}

// Trending Products Engine
class TrendingEngine extends RecommendationEngine {
  async generateRecommendations(userProfile, products, limit = 5) {
    console.log(`[TRENDING] Generating trending recommendations`);
    
    // Simulate trending algorithm
    // In production, this would analyze recent popular items
    const recommendations = products
      .sort(() => Math.random() - 0.5)
      .slice(0, limit)
      .map(p => ({
        productId: p.id,
        name: p.name,
        category: p.category,
        price: p.price,
        score: Math.random(),
        reason: 'Trending now'
      }));

    return recommendations;
  }
}

// Hybrid Engine - Combines multiple strategies
class HybridEngine extends RecommendationEngine {
  constructor() {
    super();
    this.collaborativeEngine = new CollaborativeFilteringEngine();
    this.contentEngine = new ContentBasedEngine();
    this.trendingEngine = new TrendingEngine();
  }

  async generateRecommendations(userProfile, products, limit = 5) {
    console.log(`[HYBRID] Generating hybrid recommendations for user ${userProfile.userId}`);
    
    // Get recommendations from each engine
    const collaborative = await this.collaborativeEngine.generateRecommendations(
      userProfile, products, Math.ceil(limit * 0.4)
    );
    const contentBased = await this.contentEngine.generateRecommendations(
      userProfile, products, Math.ceil(limit * 0.4)
    );
    const trending = await this.trendingEngine.generateRecommendations(
      userProfile, products, Math.ceil(limit * 0.2)
    );

    // Combine and deduplicate
    const allRecommendations = [...collaborative, ...contentBased, ...trending];
    const seen = new Set();
    const unique = allRecommendations.filter(r => {
      if (seen.has(r.productId)) return false;
      seen.add(r.productId);
      return true;
    });

    return unique.slice(0, limit);
  }
}

class RecommendationContext {
  constructor() {
    this.engines = {
      collaborative: new CollaborativeFilteringEngine(),
      content_based: new ContentBasedEngine(),
      trending: new TrendingEngine(),
      hybrid: new HybridEngine()
    };
  }

  async generateRecommendations(algorithm, userProfile, products, limit) {
    const engine = this.engines[algorithm];
    if (!engine) {
      throw new Error(`Recommendation algorithm '${algorithm}' not supported`);
    }

    return await engine.generateRecommendations(userProfile, products, limit);
  }

  getSupportedAlgorithms() {
    return Object.keys(this.engines);
  }
}

module.exports = RecommendationContext;
