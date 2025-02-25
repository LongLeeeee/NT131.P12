const express = require("express");
const multer = require("multer");
const { MongoClient, GridFSBucket } = require("mongodb");
const fs = require("fs");
const path = require("path");
require("dotenv").config();

const app = express();
const upload = multer({ dest: "uploads/" });

const mongoURI = `mongodb+srv://${process.env.usernameDB}:${process.env.passwordDB}@cluster0.dnwsy.mongodb.net/myDB?retryWrites=true&w=majority&appName=Cluster0`;

let db, gfs;

// MongoDB Connection
MongoClient.connect(mongoURI, { useNewUrlParser: true, useUnifiedTopology: true })
  .then((client) => {
    db = client.db("myDB");
    gfs = new GridFSBucket(db, { bucketName: "mapFiles" });
    console.log("MongoDB connected!");
  })
  .catch((err) => {
    console.error("Failed to connect to MongoDB:", err);
  });

// Upload .map file to GridFS
app.post("/upload", upload.single("mapFile"), (req, res) => {
  if (!req.file) {
    return res.status(400).send("No file uploaded.");
  }

  // Read file from the temporary upload directory
  const tempFilePath = req.file.path;
  const originalFileName = req.file.originalname;

  if (!gfs) {
    return res.status(500).send("MongoDB connection not initialized.");
  }

  // Open a stream to GridFS
  const fileStream = fs.createReadStream(tempFilePath);
  const uploadStream = gfs.openUploadStream(originalFileName);

  // Pipe the file to GridFS
  fileStream.pipe(uploadStream);

  uploadStream.on("finish", () => {
    // Optional: Move to `maps/` if you want a local backup
    const backupDir = path.join(__dirname, "maps");
    if (!fs.existsSync(backupDir)) {
      fs.mkdirSync(backupDir);
    }

    const permanentPath = path.join(backupDir, originalFileName);
    fs.renameSync(tempFilePath, permanentPath);

    res.status(200).send("File uploaded and saved successfully.");
  });

  uploadStream.on("error", (err) => {
    console.error("Error uploading file:", err);
    res.status(500).send("File upload failed.");
  });
});

const PORT = 3000;
app.listen(PORT, () => console.log(`Server running on http://localhost:${PORT}`));
