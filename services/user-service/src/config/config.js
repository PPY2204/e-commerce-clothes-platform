// Singleton Pattern for Configuration
class Config {
  constructor() {
    if (Config.instance) {
      return Config.instance;
    }

    this.port = process.env.PORT || 3001;
    this.jwtSecret = process.env.JWT_SECRET || 'your-secret-key';
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
