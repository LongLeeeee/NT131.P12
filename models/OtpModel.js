const mongoose = require('mongoose');

const otpSchema = new mongoose.Schema({
    email: { type: String, require: true },
    otp: { type: String, require: true },
    time: { type: Date, default: Date.now, expires: 60 },
}, { versionKey: false });

const OtpModel = mongoose.model('Otp', otpSchema);
module.exports = OtpModel;