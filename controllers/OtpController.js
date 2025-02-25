const { generate } = require('otp-generator');
const OtpModel = require('../models/OtpModel');
const otp_generator = require('otp-generator');
const nodemailer = require('nodemailer');
const { text } = require('express');
require('dotenv').config();

const mailHost = "smtp.gmail.com";
const email = process.env.EMAIL_SEND_OTP;
const password = process.env.PASSWORD_EMAIL_SEND_OTP;

const transporter = nodemailer.createTransport({
    host: mailHost,
    port: 587,
    secure: false,
    auth: {
        user: email,
        pass: password,
    },
});

class OtpController {
    static async generateOTP() {
        const otp = otp_generator.generate(4, {
            digits: true,
            lowerCaseAlphabets: false,
            upperCaseAlphabets: false,
            specialChars: false,
        })
        return otp;
    }

    static async sendOTP(toEmail, code) {
        const mailOptions = {
            from: email,
            to: toEmail,
            subject: 'Your Verification Code',
            text: `Your verification code is ${code}`,
        };
        try {
            transporter.sendMail(mailOptions);
            const newOtp = new OtpModel({
                email: toEmail,
                otp: code
            });
            await newOtp.save();
            return true;
        }
        catch (error) {
            console.log(error);
            return false;
        }
    }
}

module.exports = OtpController; 
