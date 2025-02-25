const mongoose = require('mongoose');

const potholeSchema = new mongoose.Schema({
    username: { type: String, require: true },
    size: { type: Number, require: true },
    depth: { type: Number, require: true },
    diameter: { type: Number, require: true },
    statement: { type: String, require: true },
    location: {
        longitude: { type: String, require: true },
        latitude: { type: String, require: true },
        address: { type: String, require: true },
    }
}, { versionKey: false });

const PotHoleModel = mongoose.model('Pothole', potholeSchema);
module.exports = PotHoleModel;