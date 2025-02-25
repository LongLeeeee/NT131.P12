const { model } = require('mongoose');
const PotHoleModel2 = require('../models/potHoleModel2');
const historyModel = require('../models/historyModel');
const axios = require('axios');
const jwt = require('jsonwebtoken');
const { UserModel } = require('../models/userModel');

const secret_key = process.env.SECRET_KEY;

const dayOfWeek = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

function modifyDate(date) {
    if (date < 10) {
        return '0' + date;
    }
    return date;
}

function modifyMonth(month) {
    switch (month) {
        case 1:
            return 'Jan';
        case 2:
            return 'Feb';
        case 3:
            return 'Mar';
        case 4:
            return 'Apr';
        case 5:
            return 'May';
        case 6:
            return 'Jun';
        case 7:
            return 'Jul';
        case 8:
            return 'Aug';
        case 9:
            return 'Sep';
        case 10:
            return 'Oct';
        case 11:
            return 'Nov';
        case 12:
            return 'Dec';
    }
}
class PotHoleController2 {

    static async getAllPotHole(req, res) {
        try {
            const potHoles = await PotHoleModel2.find();
            if (potHoles) {
                return res.status(200).json({
                    success: true,
                    potHoles: potHoles
                });
            }
            else {
                return res.status(404).json({
                    success: false,
                    err: 'Not Found'
                })
            }
        }
        catch (err) {
            return res.status(500).json({
                success: false,
                err: 'Not Found'
            });
        }
    }
    //create new pothole
    static async createPotHole(req, res) {
        const mapPotholeinfo = JSON.parse(req.body.mapPotholeinfo);
        if (req.body) {
            var savedPotHole = new PotHoleModel2();
            try {
                const newPotHole = new PotHoleModel2({
                    id: req.query._id,
                    username: mapPotholeinfo.username,
                    level: mapPotholeinfo.level,
                    date: mapPotholeinfo.date,
                    latitude: mapPotholeinfo.latitude,
                    longitude: mapPotholeinfo.longitude,
                    isSharing: mapPotholeinfo.isSharing

                })
                savedPotHole = await newPotHole.save();
            }
            catch (error) {
                return res.status(500).json({
                    success: false,
                    err: 'Error creating new pothole.'
                })
            }
            var oldfilePath = "";
            var newfilePath = "";
            if (req.file) {
                const fileExtension = req.file.originalname.split('.').pop();
                const fileName = `${savedPotHole._id}.${fileExtension}`;
                oldfilePath = `uploads/${req.file.filename}`;
                newfilePath = `uploads/${fileName}`;

                const fs = require('fs');
                fs.rename(oldfilePath, newfilePath, (err) => {
                    if (err) {
                        console.error("Error renaming file:", err);
                        return res.status(500).json({
                            success: false,
                            err: 'Error renaming image file.'
                        });
                    }
                });
            }

            const url = `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${mapPotholeinfo.latitude}&lon=${mapPotholeinfo.longitude}`;
            try {
                const response = await axios.get(url, {
                    headers: { 'User-Agent': 'PotHoke-App' },
                });
                const date = new Date();
                if (response.data.display_name) {
                    const newHistory = new historyModel({
                        id_user: req.query._id,
                        username: mapPotholeinfo.username,
                        id_pothole: savedPotHole._id,
                        date: dayOfWeek[date.getDay()] + ', ' + modifyDate(date.getDate()) + ' ' + modifyMonth(date.getMonth() + 1) + ' ' + date.getFullYear().toString(),
                        time: date.getHours() + ':' + date.getMinutes(),
                        size: mapPotholeinfo.level,
                        address: response.data.display_name,
                        url_image: newfilePath,
                        status: 'Accepted'
                    });
                    await newHistory.save();
                }
                return res.status(201).json({
                    success: true,
                    potHoleInfor: savedPotHole
                });
            }
            catch (err) {
                return res.status(500).json({
                    success: false,
                    err: 'Error creating new history.'
                });
            }
        }
        else {
            return res.status(400).json({
                success: false,
                err: 'Missing required component'
            });
        }
    }

    static async getPotholeByMonth(req, res) {
        try {
            var _id = req.query._id;
            var month = req.query.month;
            var year = req.query.year;
            const potholes = await PotHoleModel2.find({
                'id': _id, date: { $regex: month + ' ' + year, $options: 'i' }
            })
            var smalls = 0;
            var medium = 0;
            var large = 0;
            for (const pothole of potholes) {
                if (pothole.level == 1) {
                    smalls++;
                }
                else if (pothole.level == 2) {
                    medium++;
                }
                else if (pothole.level == 3) {
                    large++;
                }
            }
            return res.status(200).json({
                small: smalls,
                medium: medium,
                large: large
            });
        }
        catch (err) {
            return res.status(500).json({
                success: false,
                err: 'Error creating new pothole.'
            });
        }
    }

    static async getAddress(req, res) {
        const latitude = req.query.latitude;
        const longitude = req.query.longitude;
        const url = `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${latitude}&lon=${longitude}`;
        try {
            const response = await axios.get(url, {
                headers: { 'User-Agent': 'PotHoke-App' },
            });
            if (response.data.display_name) {
                return res.status(200).json(response.data.display_name);
            }
            else {
                return res.status(404).json({
                    success: false,
                    err: 'Not found address'
                })
            }
        }
        catch (err) {
            return res.status(500).json({
                success: false,
                err: 'Error fetching address'
            });
        }
    }

    static async getPotholeById(req, res) {
        try {
            const potholeId = req.query._id; // Fetch _id from query parameters
            if (!potholeId) {
                return res.status(400).json({
                    success: false,
                    err: 'Missing pothole ID in request'
                });
            }

            const pothole = await PotHoleModel2.findById(potholeId);
            const username = await UserModel.findById(pothole.i)
            if (pothole) {
                return res.status(200).json({
                    success: true,
                    pothole: pothole,
                    url_image: `uploads/${pothole._id}.jpg`
                });
            } else {
                return res.status(404).json({
                    success: false,
                    err: 'Pothole not found'
                });
            }
        } catch (err) {
            return res.status(500).json({
                success: false,
                err: 'Error retrieving pothole'
            });
        }
    }
    static async getPotholesByUserId(req, res) {
        try {
            // Lấy id người dùng từ query parameters
            const userId = req.query.id;

            if (!userId) {
                return res.status(400).json({
                    success: false,
                    err: 'Missing user ID in request'
                });
            }

            // Tìm tất cả các potholes có isSharing = true và id khớp với userId
            const potholes = await PotHoleModel2.find({
                $or: [
                    { id: userId }, // Điều kiện 1: id phải khớp với userId
                    { isSharing: true } // Điều kiện 2: isSharing phải là true
                ]
            });
            console.log(potholes);

            if (potholes.length > 0) {
                return res.status(200).json({
                    success: true,
                    potholes: potholes
                });
            } else {
                return res.status(404).json({
                    success: false,
                    err: 'No potholes found for the given user ID with isSharing = true'
                });
            }
        } catch (err) {
            console.error(err);
            return res.status(500).json({
                success: false,
                err: 'Error retrieving potholes'
            });
        }
    }


}

module.exports = PotHoleController2;
