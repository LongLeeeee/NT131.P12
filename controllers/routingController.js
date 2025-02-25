const axios = require('axios');
class routingController{
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
    static async getRouting(req, res){
        const { start, end } = req.query; // Nhận thông tin điểm bắt đầu và điểm kết thúc từ query parameter


        if (!start || !end) {
        return res.status(400).send('Vui lòng cung cấp điểm bắt đầu và điểm kết thúc');
        }

         try {
        
        const osrmUrl = `http://router.project-osrm.org/route/v1/driving/${start};${end}?overview=full&geometries=geojson`;
        //const osrmUrl = `http://localhost:5000/route/v1/driving/${start};${end}?overview=full&geometries=geojson`;

        // Yêu cầu đến OSRM API
        const response = await axios.get(osrmUrl);

        // Trả kết quả JSON về cho ứng dụng Android
        res.json(response.data);
    } catch (error) {
        console.error(error);
        res.status(500).send('Có lỗi xảy ra khi lấy chỉ đường từ OSRM');
    }
    }
}
module.exports = routingController