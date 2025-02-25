const { UserModel, UserGoogleModel, UserFacebookModel } = require('../models/userModel');
const { OAuth2Client } = require('google-auth-library')
const OtpController = require('./OtpController');
const OtpModel = require('../models/OtpModel');
const bcrypt = require('bcrypt');
const axios = require('axios');
const { generateToken } = require('./AuthenticationService');
require('dotenv').config();

const CLIENT_ID = process.env.CLIENT_ID;
const client = new OAuth2Client(CLIENT_ID);


class UserController {

    //get all users
    static async getAllUsers(req, res) {
        try {
            const users = await UserModel.find();
            return res.status(200).json({
                success: true,
                users: users
            });
        }
        catch (error) {
            return res.status(500).json({
                success: false,
                error: 'Error fetching users'
            });
        }
    }

    //login with google
    static async googleSignIn(req, res) {
        const data = req.body;
        if (data) {
            try {
                const ticket = await client.verifyIdToken({
                    idToken: data.idToken,
                    audience: CLIENT_ID,
                });
                const newUserGoogle = new UserGoogleModel({
                    type: "google",
                    userInfo: {
                        email: ticket.getPayload().email,
                        name: ticket.getPayload().name,
                        picture: ticket.getPayload().picture,
                    }
                });
                var tempUserGoogle = await UserGoogleModel.exists({ 'userInfo.email': newUserGoogle.userInfo.email });
                if (!tempUserGoogle) {
                    tempUserGoogle = await newUserGoogle.save();
                }
                const token = generateToken({ email: newUserGoogle.email });
                res.setHeader('Authorization', token);
                return res.status(200).json({
                    success: true,
                    type: "google",
                    userInfo: {
                        _id: tempUserGoogle._id,
                        email: ticket.getPayload().email,
                        username: ticket.getPayload().name,
                        picture: ticket.getPayload().picture,
                        // isSharing: tempUserGoogle.userInfo.isSharing,
                        // isAlert: tempUserGoogle.userInfo.isAlert
                    }
                });
            }
            catch (err) {
                return res.status(404).json({
                    success: false,
                    error: 'User not found'
                });
            }
        }
        else {
            return res.status(500).json({
                success: false,
                data: 'Error login with google failed'
            });
        }
    }

    //login with facebook
    static async facebookSignIn(req, res) {
        const data = req.body;
        if (data) {
            try {
                const response = await axios.get(`https://graph.facebook.com/v11.0/me?fields=id,name,email,picture&access_token=${data.accessToken}`);
                if (response) {
                    const newUserFacebook = new UserFacebookModel({
                        type: "facebook",
                        _id: response.data.id + "",
                        userInfo: {
                            email: response.data.email,
                            name: response.data.name,
                            picture: response.data.picture.data.url,
                        }
                    });
                    const exists = await UserFacebookModel.exists({ _id: newUserFacebook._id });
                    if (!exists) {
                        await newUserFacebook.save();
                    }
                    const token = generateToken({ email: newUserFacebook._id });
                    res.setHeader('Authorization', token);
                    return res.status(200).json({
                        success: true,
                        type: "facebook",
                        userInfo: {
                            _id: exists._id,
                            email: response.data.email,
                            name: response.data.name,
                            picture: response.data.picture.data.url,
                            // isSharing: exists.userInfo.isSharing,
                            // isAlert: exists.userInfo.isAlert
                        }
                    });
                }
            }
            catch (error) {
                return res.status(404).json({
                    success: false,
                    error: 'User not found'
                });
            }
        }
        return res.status(500).json({
            success: false,
            data: 'Error login with facebook failed'
        });
    }

    //login
    static async login(req, res) {
        const data = req.body;
        try {
            const user = await UserModel.findOne({ 'userInfo.username': data.username });
            if (!user) {
                return res.status(404).json({
                    success: false,
                    error: 'User not found'
                });
            }
            const isMatch = await bcrypt.compare(data.password, user.userInfo.password);
            if (!isMatch) {
                return res.status(404).json({
                    success: false,
                    error: 'Invalid password'
                });
            }
            const token = generateToken({ username: user.userInfo.username, password: user.userInfo.password });
            res.setHeader('Authorization', token);
            return res.status(200).json({
                success: true,
                type: user.type,
                user: {
                    _id: user._id,
                    username: user.userInfo.username,
                    email: user.userInfo.email,
                    dateOfBirth: user.userInfo.dateOfBirth,
                    phoneNumber: user.userInfo.phoneNumber,
                    isSharing: user.userInfo.isSharing,
                    isAlert: user.userInfo.isAlert
                }
            });
        }
        catch (error) {
            return res.status(500).json({
                success: false,
                data: 'Error fetching users'
            });
        }
    }

    //create user
    static async createUser(req, res) {
        if (req.body) {
            try {
                const existedUser = await UserModel.findOne({ 'userInfo.username': req.body.username });
                if (existedUser) {
                    return res.status(400).json({
                        success: false,
                        err: 'User already exists'
                    });
                }
                else {
                    const salt = await bcrypt.genSalt(10);
                    const hashedPassword = await bcrypt.hash(req.body.password, salt);

                    const newUser = new UserModel({
                        type: "normal",
                        userInfo: {
                            username: req.body.username,
                            password: hashedPassword,
                            email: req.body.email,
                            dateOfBirth: null,
                            phoneNumber: null,
                            isSharing: true,
                            isAlert: true
                        }
                    });
                    const savedUser = await newUser.save();
                    const token = generateToken({ username: savedUser.userInfo.username, password: savedUser.userInfo.password });
                    res.setHeader('Authorization', token);
                    res.status(201).json({
                        success: true,
                        type: savedUser.type,
                        userInfo: {
                            _id: savedUser._id,
                            username: savedUser.userInfo.username,
                            email: savedUser.userInfo.email,
                            dateOfBirth: savedUser.userInfo.dateOfBirth,
                            phoneNumber: savedUser.userInfo.phoneNumber,
                            isSharing: savedUser.userInfo.isSharing,
                            isAlert: savedUser.userInfo.isAlert
                        },
                    });
                }
            }
            catch (error) {
                res.status(500).json({
                    success: false,
                    error: 'Error creating users'
                });
            }
        }
        else {
            return res.status(400).json({
                success: false,
                err: 'Missing required component'
            });
        }
    }

    //forgot password
    static async forgotPassword(req, res) {
        const data = req.body;
        if (!data) {
            return res.status(400).json({
                success: false,
                err: 'Missing required component'
            });
        }
        try {
            const exists = await UserModel.exists({ 'userInfo.email': data.email });
            if (exists) {
                const otp = await OtpController.generateOTP();
                const success = await OtpController.sendOTP(data.email, otp);
                if (!success) {
                    return res.status(500).json({
                        success: false,
                        err: 'Error sending OTP'
                    });
                }
                return res.status(200).json({ success: true });
            }
            return res.status(404).json({
                success: false,
                err: 'User not found'
            });
        }
        catch (error) {
            return res.status(404).json({
                success: false,
                err: 'User not found'
            });
        }
    }

    //verify OTP
    static async verifyOTP(req, res) {
        const data = req.body;
        if (data) {
            try {
                const exists = await OtpModel.findOne({ email: data.email, otp: data.otp });
                if (!exists) {
                    return res.status(404).json({
                        success: false,
                        err: 'OTP invalid or expired'
                    });
                }
                await OtpModel.deleteOne({ id_: exists._id });
                return res.status(201).json({ success: true });
            }
            catch (error) {
                return res.status(500).json({
                    success: false,
                    err: 'Error verifying OTP'
                });
            }
        }
        return res.status(400).json({
            success: false,
            err: 'Missing required component'
        });
    }

    //update user
    static async updateUser(req, res) {
        const data = req.body;
        if (data.type == "forgot_password") {
            try {
                const updateUser = await UserModel.findOne({ 'userInfo.email': data.email });
                if (updateUser && data.type == "forgot_password") {
                    const salt = await bcrypt.genSalt(10);
                    const hashedPassword = await bcrypt.hash(data.newPassword, salt);
                    updateUser.userInfo.password = hashedPassword;
                    await updateUser.save();
                    return res.status(201).json({
                        success: true,
                        type: updateUser.type,
                        newInfo: updateUser
                    });
                }
                return res.status(404).json({
                    success: false,
                    err: "User not found"
                });
            }
            catch (error) {
                return res.status(500).json({
                    success: false,
                    err: 'Error updating users'
                });
            }
        }
        else if (data.type == "change_password") {
            const newPassword = req.body.newPassword;
            try {
                const updateUser = await UserModel.findOne({ 'userInfo.username': req.decoded.username, 'userInfo.password': req.decoded.password });
                if (updateUser) {
                    const salt = await bcrypt.genSalt(10);
                    const hashedPassword = await bcrypt.hash(newPassword, salt);
                    updateUser.userInfo.password = hashedPassword;
                    await updateUser.save();
                    return res.status(201).json({
                        success: true,
                        type: updateUser.type,
                        newInfo: updateUser
                    });
                }
                return res.status(404).json({
                    success: false,
                    err: "User not found"
                });
            }
            catch (error) {
                return res.status(500).json({
                    success: false,
                    err: 'Error updating users'
                });
            }
        }
    }


    static async updateUserPreferences(req, res) {
        const { _id, isSharing, isAlert } = req.body;

        if (req.query.type_user == "normal") {
            try {
                const user = await UserModel.findById(_id); // Tìm người dùng theo ID
                if (!user) {
                    return res.status(404).json({ success: false, message: 'User not found' });
                }

                user.userInfo.isSharing = isSharing;
                user.userInfo.isAlert = isAlert;
                await user.save();

                res.status(200).json({ success: true, message: 'Preferences updated successfully' });
            } catch (err) {
                res.status(500).json({ success: false, message: 'Error updating preferences' });
            }
        }
        else if (req.query.type_user == "google") {
            try {
                const user = await UserGoogleModel.findById(_id); // Tìm người dùng theo ID
                if (!user) {
                    return res.status(404).json({ success: false, message: 'User not found' });
                }

                user.userInfo.isSharing = isSharing;
                user.userInfo.isAlert = isAlert;
                await user.save();

                res.status(200).json({ success: true, message: 'Preferences updated successfully' });
            } catch (err) {
                res.status(500).json({ success: false, message: 'Error updating preferences' });
            }
        }
        else if (req.query.type_user == "facebook") {
            try {
                const user = await UserFacebookModel.findById(id); // Tìm người dùng theo ID
                if (!user) {
                    return res.status(404).json({ success: false, message: 'User not found' });
                }

                user.userInfo.isSharing = isSharing;
                user.userInfo.isAlert = isAlert;
                await user.save();

                res.status(200).json({ success: true, message: 'Preferences updated successfully' });
            } catch (err) {
                res.status(500).json({ success: false, message: 'Error updating preferences' });
            }
        }
    }

    //delete a user
    static async deleteUser(req, res) {
        const username = req.params.username;
        try {
            const deletedUser = await UserModel.findOneAndDelete({ username: username });
            if (deletedUser) {
                return res.status(200).json({
                    success: true,
                    userInfo: deletedUser
                });
            }
            return res.status(404).json({
                success: false,
                err: 'User not found'
            });
        }
        catch (error) {
            res.status(500).json({
                success: false,
                err: 'Error creating users'
            });
        }
    }
}

module.exports = UserController;
