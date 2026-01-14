const express = require('express');
const cors = require('cors');
const orderRoutes = require('./routes/orderRoutes');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3003;

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/orders', orderRoutes);

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'Order Service is running', timestamp: new Date() });
});

app.listen(PORT, () => {
  console.log(`Order Service running on port ${PORT}`);
});
