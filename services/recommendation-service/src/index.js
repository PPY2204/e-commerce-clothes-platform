const express = require('express');
const cors = require('cors');
const recommendationRoutes = require('./routes/recommendationRoutes');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 3007;

// Middleware
app.use(cors());
app.use(express.json());

// Routes
app.use('/recommendations', recommendationRoutes);

// Health check
app.get('/health', (req, res) => {
  res.json({ status: 'Recommendation Service is running', timestamp: new Date() });
});

app.listen(PORT, () => {
  console.log(`Recommendation Service running on port ${PORT}`);
});
