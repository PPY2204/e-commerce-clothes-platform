// Singleton Pattern for Configuration
class Config {
  constructor() {
    if (Config.instance) {
      return Config.instance;
    }

    this.port = process.env.PORT || 3001;
    
    // Require JWT_SECRET to be set - security critical
    if (!process.env.JWT_SECRET) {
      throw new Error('CRITICAL: JWT_SECRET environment variable must be set');
    }
    this.jwtSecret = process.env.JWT_SECRET;
    this.jwtExpiration = process.env.JWT_EXPIRATION || '24h';
    
    Config.instance = this;
  }

  static getInstance() {
    if (!Config.instance) {
      Config.instance = new Config();
    }
    return Config.instance;
  }
}

module.exports = Config;
