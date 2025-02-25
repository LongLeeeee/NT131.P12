const express = require('express')
const UserController = require('../controllers/userController')
const userRouter = express.Router()
const { Authentication } = require('../controllers/AuthenticationService');

//users
userRouter.get('/', UserController.getAllUsers);

userRouter.post('/google-sign-in', UserController.googleSignIn);

userRouter.post('/facebook-sign-in', UserController.facebookSignIn);

userRouter.post('/login', UserController.login);

userRouter.post('/register', UserController.createUser);

userRouter.post('/forgot-password', UserController.forgotPassword);

userRouter.post('/verify', UserController.verifyOTP);

userRouter.put('/edit', Authentication, UserController.updateUser);

userRouter.put('/update-preferences', UserController.updateUserPreferences);


module.exports = userRouter;