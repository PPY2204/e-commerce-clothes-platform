const express = require('express');
const cors = require('cors');
const productRoutes = require('./routes/productRoutes');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3002;

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/products', productRoutes);

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'Product Service is running', timestamp: new Date() });
});

app.listen(PORT, () => {
  console.log(`Product Service running on port ${PORT}`);
});
