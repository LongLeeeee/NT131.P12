const express = require('express')
const mongoose = require('mongoose')
const cors = require('cors')
const database = require('./config/database')
require('dotenv').config();

const userRoutes = require('./routes/userRoutes')
const potHoleRoutes2 = require('./routes/potHoleRoutes2');
const placeRoutes = require('./routes/placeRoutes');
const routingRouter = require('./routes/routingRoutes');
const mapRouter = require('./routes/mapRoutes');
const historyRouter = require('./routes/historyRoutes');
const monitorRouter = require('./routes/monitorRoutes');


const app = express();
app.use(express.json());
app.use((req, res, next) => [
    res.setHeader('Access-Control-Allow-Origin', '*'),
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE'),
    res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization'),
    next()
]);
database.connect();
mongoose.set('strictQuery', true);


app.use('/api/users', userRoutes)
app.use('/uploads', express.static('uploads'))
app.use('/api/potholes', potHoleRoutes2)
app.use('/api/locations', placeRoutes)
app.use('/api/monitor', monitorRouter)
app.use('/api/history', historyRouter)
app.use('/api/route', routingRouter)
app.use('/api/download-map', mapRouter)


app.listen(process.env.PORT, () => {
    console.log(`Server is running on port ${process.env.PORT}`);
})
