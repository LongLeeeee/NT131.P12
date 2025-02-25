const express = require('express')
const potHoleController = require('../controllers/potHoleController');
const potHoleRouter = express.Router();

potHoleRouter.get('/', potHoleController.Authentication, potHoleController.getAllPotHole);

potHoleRouter.get('/:id', potHoleController.Authentication, potHoleController.getPotHoleById);

potHoleRouter.post('/create', potHoleController.Authentication, potHoleController.createPotHole);

potHoleRouter.delete('/delete/:id', potHoleController.Authentication, potHoleController.deletePotHoleById);

potHoleRouter.delete('/delete/', potHoleController.Authentication, potHoleController.deleteAllPotHole);

potHoleRouter.put('/edit/:id', potHoleController.Authentication, potHoleController.updatePotHole);

module.exports = potHoleRouter;