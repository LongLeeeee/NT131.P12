const express = require('express')
const placeController = require('../controllers/placeController');
const placeRouter = express.Router();

placeRouter.get('/', placeController.getPlaceByKeyName);



module.exports = placeRouter