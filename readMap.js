const fs = require('fs');
const xml2js = require('xml2js');
const mongoose = require('mongoose');
require('dotenv').config();

mongoose.set('strictQuery', true);

const atlat = `mongodb+srv://${process.env.usernameDB}:${process.env.passwordDB}@cluster0.dnwsy.mongodb.net/myDB?retryWrites=true&w=majority&appName=Cluster0`;

const connect = async () => {
  try {
    await mongoose.connect(atlat, {
      useNewUrlParser: true,
      useUnifiedTopology: true,
    });
    console.log("Connected success");
  } catch (error) {
    console.log("Connected fail");
    console.log(error);
  }
};

// Định nghĩa Schema cho địa điểm
const placeSchema = new mongoose.Schema({
  name: String,
  latitude: Number,
  longitude: Number
});

// Tạo mô hình Place từ schema
const Place = mongoose.model('Place', placeSchema);

const readAndParseOsmFile = async () => {
  try {
    const osmFilePath = 'E:\\Năm 3\\Android\\server_clone\\NT131.P12\\map.osm';
    const data = await fs.promises.readFile(osmFilePath);
    xml2js.parseString(data, async (err, result) => {
      if (err) {
        console.error('Không thể phân tích file .osm:', err);
        return;
      }

      try {
        const nodes = result.osm.node;
        const places = [];

        nodes.forEach(node => {
          let name = '';
          let lat = node.$.lat;
          let lon = node.$.lon;

          // Lấy thông tin tên địa điểm từ tag
          if (node.tag) {
            node.tag.forEach(tag => {
              if (tag.$.k === 'name') {
                name = tag.$.v;
              }
            });
          }

          if (name) {
            places.push({ name, lat, lon });
          }
        });

        console.log(`${places.length} địa điểm đã được tìm thấy`);

        // Sử dụng for..of để lưu từng địa điểm vào MongoDB
        for (const placeData of places) {
          const place = new Place({
            name: placeData.name,
            latitude: parseFloat(placeData.lat),
            longitude: parseFloat(placeData.lon)
          });
          await place.save();
          console.log('Đã lưu địa điểm:', place.name);
        }

        console.log('Hoàn thành lưu tất cả các địa điểm vào MongoDB');

      } catch (err) {
        console.error('Đã xảy ra lỗi trong quá trình xử lý:', err);
      }
    });
  } catch (err) {
    console.error('Không thể đọc file .osm:', err);
  }
};

// Hàm main để thực hiện kết nối và đọc file
const main = async () => {
  await connect(); // Kết nối tới MongoDB trước
  await readAndParseOsmFile(); // Đọc và phân tích file .osm
};

// Gọi hàm main
main();
