const express = require('express');
const cors = require('cors');
const inventoryRoutes = require('./routes/inventoryRoutes');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3005;

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/inventory', inventoryRoutes);

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'Inventory Service is running', timestamp: new Date() });
});

app.listen(PORT, () => {
  console.log(`Inventory Service running on port ${PORT}`);
});
