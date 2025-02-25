const mongoose = require('mongoose')

const historySchema = new mongoose.Schema({
    id_user: { type: String, require: true },
    username: { type: String, require: true },
    id_pothole: {type: String, require: true },
    date: { type: String, require: true },
    time: { type: String, require: true },
    size: { type: Number, require: true },
    address: { type: String, require: true },
    status: { type: String, require: true }
}, { versionKey: false });

const HistoryModel = mongoose.model('History', historySchema);
module.exports = HistoryModel;