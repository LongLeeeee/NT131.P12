const express = require('express')
const routingController = require('../controllers/routingController')
const routingRouter = express.Router()


routingRouter.get('/', routingController.getRouting)
module.exports = routingRouter
