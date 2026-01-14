const express = require('express');
const cors = require('cors');
const notificationRoutes = require('./routes/notificationRoutes');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3006;

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/notifications', notificationRoutes);

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'Notification Service is running', timestamp: new Date() });
});

app.listen(PORT, () => {
  console.log(`Notification Service running on port ${PORT}`);
});
