'use strict';

const express = require('express');
var app = express();
app.use(function(req, res, next) {
    res.charset = "utf-8";
    next();
});
app.use(express.static('publics'));

app.listen(3000, function () {
    console.log('Application listening on port 3000!')
});