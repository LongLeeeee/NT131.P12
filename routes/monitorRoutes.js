const express = require('express');
const monitorController = require('../controllers/monitorController');
const monitorRouter = express.Router();

monitorRouter.get('/', monitorController.getData);
monitorRouter.post('/', monitorController.uploadData);

module.exports = monitorRouter;