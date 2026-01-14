// Repository Pattern for User data access
const User = require('../models/User');

class UserRepository {
  constructor() {
    // In-memory storage (replace with database in production)
    this.users = new Map();
    this.currentId = 1;
  }

  async create(userData) {
    const user = new User(
      this.currentId++,
      userData.username,
      userData.email,
      userData.password,
      userData.role
    );
    this.users.set(user.id, user);
    return user;
  }

  async findById(id) {
    return this.users.get(id);
  }

  async findByEmail(email) {
    return Array.from(this.users.values()).find(user => user.email === email);
  }

  async findByUsername(username) {
    return Array.from(this.users.values()).find(user => user.username === username);
  }

  async findAll() {
    return Array.from(this.users.values());
  }

  async update(id, userData) {
    const user = this.users.get(id);
    if (!user) return null;
    
    Object.assign(user, userData);
    return user;
  }

  async delete(id) {
    return this.users.delete(id);
  }
}

module.exports = UserRepository;
