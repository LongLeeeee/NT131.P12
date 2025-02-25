const mongoose = require('mongoose')

const userSchema = new mongoose.Schema({
    type: { type: String, require: true },
    userInfo: {
        username: { type: String, require: true, unique: true },
        password: { type: String, require: true },
        email: { type: String, require: true, unique: true },
        dateOfBirth: { type: String },
        phoneNumber: { type: String, require: true },
        isSharing: { type: Boolean, default: true },
        isAlert: { type: Boolean, default: true }
    }
}, { versionKey: false });

const userGoogleSchema = new mongoose.Schema({
    type: { type: String, require: true },
    userInfo: {
        email: { type: String, require: true, unique: true },
        username: { type: String, require: true },
        picture: { type: String },
        isSharing: { type: Boolean, default: true },
        isAlert: { type: Boolean, default: true }
    }
}, { versionKey: false });

const userFacebookSchema = new mongoose.Schema({
    type: { type: String, require: true },
    _id: { type: String, require: true },
    userInfo: {
        email: { type: String, require: true, unique: true },
        name: { type: String, require: true },
        picture: { type: String },
        isSharing: { type: Boolean, default: true },
        isAlert: { type: Boolean, default: true }
    }
}, { versionKey: false });

const UserModel = mongoose.model('User', userSchema);
const UserGoogleModel = mongoose.model('UserGoogle', userGoogleSchema);
const UserFacebookModel = mongoose.model('UserFacebook', userFacebookSchema);

module.exports = { UserModel, UserGoogleModel, UserFacebookModel };