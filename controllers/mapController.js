const path = require('path');

class mapController{
    static async getMap(req, res){
            const mapPath = path.join(__dirname, 'maps', 'langdaihoc.map');
            res.download(mapPath, 'langdaihoc.map', (err) => {
            if (err) {
                console.error('Error while sending the map file:', err);
                res.status(500).send('Error while downloading the map file');
            }
        });
    }

}
module.exports = mapController;