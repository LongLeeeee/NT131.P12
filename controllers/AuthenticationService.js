const jwt = require('jsonwebtoken');

require('dotenv').config();
const secret_key = process.env.SECRET_KEY;

const Authentication = async (req, res, next) => {
    if (req.body.type == "forgot_password") {
        next();
    }
    else {
        const tokenFromUser = req.header('Authorization');
        verifyToken(req, res, tokenFromUser, next);
    }
}

const generateToken = (data) => {
    const payload = data;
    var token = jwt.sign(payload, secret_key, { expiresIn: '2h' })
    return token;
}

const verifyToken = (req, res, data, next) => {
    if (!data) {
        return res.status(401).json({
            success: false,
            error: 'Authorization header missing'
        });
    }
    try {
        var decoded = jwt.verify(data, secret_key);
        req.decoded = decoded;
        next();
    }
    catch (error) {
        if (error.name == 'TokenExpiredError') {
            return res.status(401).json({
                success: false,
                error: 'Token expired'
            });
        }
        if (error.name == 'JsonWebTokenError') {
            return res.status(403).json({
                success: false,
                error: 'Invalid token'
            });
        }
    }
}

module.exports = { Authentication, generateToken, verifyToken };
