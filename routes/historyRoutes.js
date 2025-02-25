const express = require('express')
const historyController = require('../controllers/historyController');
const { Authentication } = require('../controllers/AuthenticationService');
const historyRouter = express.Router();

historyRouter.get('/', Authentication, historyController.getAllHistory);

module.exports = historyRouter;