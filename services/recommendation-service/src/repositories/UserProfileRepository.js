// Repository Pattern for User Profile data access
const UserProfile = require('../models/UserProfile');

class UserProfileRepository {
  constructor() {
    this.profiles = new Map();
  }

  async findByUserId(userId) {
    let profile = this.profiles.get(userId);
    if (!profile) {
      profile = new UserProfile(userId);
      this.profiles.set(userId, profile);
    }
    return profile;
  }

  async findAll() {
    return Array.from(this.profiles.values());
  }
}

module.exports = UserProfileRepository;
