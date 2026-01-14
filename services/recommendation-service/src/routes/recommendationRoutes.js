const express = require('express');
const router = express.Router();
const RecommendationController = require('../controllers/RecommendationController');

router.get('/algorithms', RecommendationController.getSupportedAlgorithms);
router.get('/:userId', RecommendationController.getRecommendations);
router.get('/:userId/profile', RecommendationController.getUserProfile);
router.post('/:userId/profile', RecommendationController.updateUserProfile);
router.post('/events', RecommendationController.handleEvent);

module.exports = router;
