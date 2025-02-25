const express = require('express')
const potHoleController2 = require('../controllers/potHoleController2');
const potHoleRouter2 = express.Router();
const upload = require('../temp');
const { Authentication } = require('../controllers/AuthenticationService');

potHoleRouter2.get('/', Authentication, potHoleController2.getAllPotHole);

potHoleRouter2.get('/month', Authentication, potHoleController2.getPotholeByMonth);

potHoleRouter2.post('/create', Authentication, upload.single("image"), potHoleController2.createPotHole);

potHoleRouter2.get('/ById', potHoleController2.getPotholeById);
potHoleRouter2.get('/ByIdUser', potHoleController2.getPotholesByUserId);

potHoleRouter2.get('/address', potHoleController2.getAddress);

//potHoleRouter.delete('/delete/:id', potHoleController.Authentication, potHoleController.deletePotHoleById);

//potHoleRouter.delete('/delete/', potHoleController.Authentication, potHoleController.deleteAllPotHole);

//potHoleRouter.put('/edit/:id', potHoleController.Authentication, potHoleController.updatePotHole);

module.exports = potHoleRouter2;