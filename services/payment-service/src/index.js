const express = require('express');
const cors = require('cors');
const paymentRoutes = require('./routes/paymentRoutes');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3004;

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/payments', paymentRoutes);

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'Payment Service is running', timestamp: new Date() });
});

app.listen(PORT, () => {
  console.log(`Payment Service running on port ${PORT}`);
});
