const express = require('express')
const mapController = require('../controllers/mapController');
const mapRouter = express.Router();

mapRouter.get('/', mapController.getMap)
module.exports = mapRouter