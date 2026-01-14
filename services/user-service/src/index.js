const express = require('express');
const cors = require('cors');
const userRoutes = require('./routes/userRoutes');
require('dotenv').config();

const Config = require('./config/config');
const config = Config.getInstance();

const app = express();

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/users', userRoutes);

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'User Service is running', timestamp: new Date() });
});

app.listen(config.port, () => {
  console.log(`User Service running on port ${config.port}`);
});
