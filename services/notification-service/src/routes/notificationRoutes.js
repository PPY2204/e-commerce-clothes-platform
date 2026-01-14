const express = require('express');
const router = express.Router();
const NotificationController = require('../controllers/NotificationController');

router.post('/', NotificationController.sendNotification);
router.get('/', NotificationController.getAllNotifications);
router.get('/channels', NotificationController.getSupportedChannels);
router.get('/:id', NotificationController.getNotification);
router.get('/recipient/:recipient', NotificationController.getNotificationsByRecipient);
router.post('/events', NotificationController.handleEvent);

module.exports = router;
