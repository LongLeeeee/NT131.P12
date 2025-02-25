const placeModel = require('../models/placeModel');


class placeController {

    static async Authentication(req, res, next) {
        const tokenFromUser = req.header('Authorization');
        if (!tokenFromUser) {
            return res.status(401).json({
                success: false,
                error: 'Authorization header missing'
            });
        }
        try {
            var decoded = jwt.verify(tokenFromUser, secret_key);
            next();
        }
        catch (error) {
            return res.status(403).json({
                success: false,
                error: 'Invalid token'
            });
        }
    }
    static async getPlaceByKeyName(req, res) {
        try {
            const keyword = req.query.keyword; // Lấy từ khóa từ query parameter
            let locations;
            
            if (keyword) {
                const regex = new RegExp(keyword, 'i'); // Tạo regex để tìm kiếm không phân biệt chữ hoa/thường
                locations = await placeModel.find({ name: { $regex: regex } });
            } else {
                locations = await placeModel.find({});
            }
        
            res.json(locations);
        } catch (err) {
            res.status(500).json({
                success: false,
                error: 'Internal server error',
                details: err.message
            });
        }
    }



}
module.exports = placeController
