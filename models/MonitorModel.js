const mongoose = require('mongoose');

const MonitorSchema = new mongoose.Schema({
    username: { type: String, require: true },
    date: { type: String, require: true },
    uploadedPothole: { type: Number, require: true },
    hoursOfUse: { type: Number, require: true },
    distance: { type: Number, require: true },
}, { versionKey: false });

const MonitorModel = mongoose.model('Monitor', MonitorSchema);
module.exports = MonitorModel; 