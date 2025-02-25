const HistoryModel = require('../models/historyModel');
class HistoryController {

    //get all
    static async getAllHistory(req, res) {
        try {
            const _id = req.query.id;
            const page = req.query.page || 1;
            const limit = req.query.limit;
            const skip = (page - 1) * limit;
            const totalHistory = await HistoryModel.countDocuments();
            const histories = await HistoryModel.find({ 'id_user': _id }).sort({ date: -1 }).sort({ time: -1 }).skip(skip).limit(limit);
            if (histories) {
                return res.status(200).json({
                    page: page,
                    totalHistory: totalHistory,
                    histories: histories,
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
}

module.exports = HistoryController;