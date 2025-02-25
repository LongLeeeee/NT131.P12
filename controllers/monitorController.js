const MonitorModel = require('../models/MonitorModel');
require('dotenv').config();

class monitorController {
    static async getData(req, res) {
        const { _id, month, year } = req.query;
        try {
            const data = await MonitorModel.find({ 'username': _id, date: { $regex: month + ' ' + year, $options: 'i' } });

            var hoursOfUse_1stWeek = 0;
            var hoursOfUse_2ndWeek = 0;
            var hoursOfUse_3rdWeek = 0;
            var hoursOfUse_4thWeek = 0;

            for (let i = 0; i < data.length; i++) {
                var day = parseInt(data[i].date.split(' ')[0]);
                if (day < 8) {
                    hoursOfUse_1stWeek += data[i].hoursOfUse;
                } else if (day < 15) {
                    hoursOfUse_2ndWeek += data[i].hoursOfUse;
                } else if (day < 22) {
                    hoursOfUse_3rdWeek += data[i].hoursOfUse;
                } else {
                    hoursOfUse_4thWeek += data[i].hoursOfUse;
                }
            }
            if (data) {
                return res.status(200).json({
                    First_week: parseFloat((hoursOfUse_1stWeek / 3600).toFixed(2)),
                    Second_week: parseFloat((hoursOfUse_2ndWeek / 3600).toFixed(2)),
                    Third_week: parseFloat((hoursOfUse_3rdWeek / 3600).toFixed(2)),
                    Fourth_week: parseFloat((hoursOfUse_4thWeek / 3600).toFixed(2))
                });
            } else {
                return res.status(404).json({
                    success: false,
                    err: 'Not Found',
                });
            }
        } catch (err) {
            return res.status(500).json({
                success: false,
                err: err.message,
            });
        }
    }


    static async uploadData(req, res) {
        const data = req.body;
        try {
            const existed = await MonitorModel.findOne({ 'username': data.username, 'date': data.date });
            if (!existed) {
                const newData = new MonitorModel({
                    username: data.username,
                    date: data.date,
                    uploadedPothole: data.uploadedPothole,
                    hoursOfUse: data.hoursOfUse,
                    distance: data.distance
                });
                await newData.save();
            }
            else {
                existed.uploadedPothole = existed.uploadedPothole + data.uploadedPothole;
                existed.hoursOfUse = existed.hoursOfUse + data.hoursOfUse;
                existed.distance = existed.distance + data.distance;
                await existed.save();
            }
            return res.status(200).json({
                success: true,
            });
        }
        catch (err) {
            return res.status(500).json({
                success: false,
                err: 'Not Found'
            });
        }
    }
}

module.exports = monitorController;
