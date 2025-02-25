const mongoose = require('mongoose');

const potholeSchema = new mongoose.Schema({
    id: { type: String, require: true },
    username: { type: String, require: true },
    level: { type: Number, require: true },
    date: { type: String, require: true },
    latitude: { type: Number, require: true },
    longitude: { type: Number, require: true },
    isSharing: { type: Boolean, default: true}
}, { versionKey: false });

const PotHoleModel2 = mongoose.model('Pothole', potholeSchema);
module.exports = PotHoleModel2;
