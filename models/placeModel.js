const mongoose = require('mongoose')

const placeSchema = new mongoose.Schema({
    name: { type: String },
    latitude: { type: Number },
    longitude: { type: Number}
    
}, { versionKey: false });

const placeModel = mongoose.model('Place', placeSchema);
module.exports = placeModel;
