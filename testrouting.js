const express = require('express');
const axios = require('axios');
const app = express();
const PORT = 3000;

// API Endpoint để nhận yêu cầu chỉ đường từ ứng dụng Android
app.get('/route', async (req, res) => {
  const { start, end } = req.query; // Nhận thông tin điểm bắt đầu và điểm kết thúc từ query parameter
  if (!start || !end) {
    return res.status(400).send('Vui lòng cung cấp điểm bắt đầu và điểm kết thúc');
  }

  try {
    // URL OSRM API công khai
    const osrmUrl = `http://router.project-osrm.org/route/v1/driving/${start};${end}?overview=full&geometries=geojson`;

    // Yêu cầu đến OSRM API
    const response = await axios.get(osrmUrl);

    // Trả kết quả JSON về cho ứng dụng Android
    res.json(response.data);
  } catch (error) {
    console.error(error);
    res.status(500).send('Có lỗi xảy ra khi lấy chỉ đường từ OSRM');
  }
});

app.listen(PORT, () => {
  console.log(`Server đang chạy trên http://localhost:${PORT}`);
});
