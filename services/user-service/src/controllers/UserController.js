const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const UserRepository = require('../repositories/UserRepository');
const Config = require('../config/config');

const userRepository = new UserRepository();
const config = Config.getInstance();

class UserController {
  async register(req, res) {
    try {
      const { username, email, password, role } = req.body;

      // Check if user already exists
      const existingUser = await userRepository.findByEmail(email);
      if (existingUser) {
        return res.status(400).json({ message: 'User already exists' });
      }

      // Hash password
      const hashedPassword = await bcrypt.hash(password, 10);

      // Create user
      const user = await userRepository.create({
        username,
        email,
        password: hashedPassword,
        role: role || 'customer'
      });

      res.status(201).json({
        message: 'User created successfully',
        user: user.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async login(req, res) {
    try {
      const { email, password } = req.body;

      // Find user
      const user = await userRepository.findByEmail(email);
      if (!user) {
        return res.status(401).json({ message: 'Invalid credentials' });
      }

      // Verify password
      const isValidPassword = await bcrypt.compare(password, user.password);
      if (!isValidPassword) {
        return res.status(401).json({ message: 'Invalid credentials' });
      }

      // Generate token
      const token = jwt.sign(
        { id: user.id, email: user.email, role: user.role },
        config.jwtSecret,
        { expiresIn: config.jwtExpiration }
      );

      res.json({
        message: 'Login successful',
        token,
        user: user.toJSON()
      });
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getProfile(req, res) {
    try {
      const user = await userRepository.findById(req.user.id);
      if (!user) {
        return res.status(404).json({ message: 'User not found' });
      }

      res.json(user.toJSON());
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }

  async getAllUsers(req, res) {
    try {
      // Check if user has admin role
      if (req.user.role !== 'admin') {
        return res.status(403).json({ message: 'Access denied. Admin privileges required.' });
      }

      const users = await userRepository.findAll();
      res.json(users.map(user => user.toJSON()));
    } catch (error) {
      res.status(500).json({ message: 'Server error', error: error.message });
    }
  }
}

module.exports = new UserController();
