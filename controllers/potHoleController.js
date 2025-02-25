const { model } = require('mongoose');
const PotHoleModel = require('../models/potHoleModel')
const jwt = require('jsonwebtoken');

const secret_key = process.env.SECRET_KEY;

class PotHoleController {

    //Authentication by token
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
            console.log(decoded);
            next();
        }
        catch (error) {
            return res.status(403).json({
                success: false,
                error: 'Invalid token'
            });
        }
    }

    //get all
    static async getAllPotHole(req, res) {
        try {
            const potHoles = await PotHoleModel.find();
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

    //get a pothole
    static async getPotHoleById(req, res) {
        const id = req.param.id;
        try {
            const potHole = await PotHoleModel.findOne({ id: id });
            if (potHole) {
                return res.status(200).json({
                    success: true,
                    potHole: potHole
                });
            }
            else {
                return res.status(404).json({
                    success: false
                });
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
        if (req.body) {
            try {
                const newPotHole = new PotHoleModel({
                    username: req.body.username,
                    size: req.body.size,
                    depth: req.body.depth,
                    diameter: req.body.diameter,
                    location: {
                        longitude: req.body.location.longitude,
                        latitude: req.body.location.latitude,
                        address: req.body.location.address
                    }
                })
                const savedPotHole = await newPotHole.save();
                return res.status(201).json({
                    success: true,
                    potHoleInfor: savedPotHole
                });
            }
            catch (err) {
                return res.status(500).json({
                    success: false,
                    err: 'Error creating new pothole.'
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

    //delete all
    static async deleteAllPotHole(req, res) {
        try {
            const result = await PotHoleModel.deleteMany();
            if (result.acknowledged) {
                return res.status(200).json({
                    success: result.acknowledged,
                    total: result.total
                });
            }
            else {
                return res.status(500).json({
                    success: false,
                    err: 'Deletion unsuccessful'
                });
            }
        }
        catch (err) {
            return res.status(500).json({
                success: false,
                err: 'Error deleting pothole'
            })
        }
    }

    //delete a pothole
    static async deletePotHoleById(req, res) {
        const id = req.param.id;
        try {
            const deletePotHole = await PotHoleModel.findOneAndDelete({ id: id });
            if (deletePotHole) {
                res.status(200).json({
                    success: true,
                    potHoles: deletePotHole
                });
            }
            else {
                return res.status(404).json({
                    success: false,
                    err: 'Not Found'
                });
            }
        }
        catch (err) {
            return res.status(500).json({
                success: false,
                err: 'Error deleting pothole'
            })
        }
    }

    //update a pothole 
    static async updatePotHole(req, res) {
        const id = req.param.id;
        const data = req.body;
        if (req.body) {
            try {
                const updatePotHole = await PotHoleModel.findOne({ id: id });
                if (updatePotHole) {
                    updatePotHole.username = data.username;
                    updatePotHole.size = data.size;
                    updatePotHole.depth = data.depth;
                    updatePotHole.diameter = data.diameter;
                    updatePotHole.location = {
                        longitude: data.location.longitude,
                        latitude: data.location.latitude,
                        address: data.location.address
                    }
                    await updatePotHole.save();
                    return res.status(200).json({
                        success: true,
                        newInfo: updatePotHole
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
                    err: 'Error updating pothole.'
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
}

module.exports = PotHoleController;