const { createProxyMiddleware } = require('http-proxy-middleware');
const express = require('express');

module.exports = function(app) {
    app.post('/api/logs/client', express.json(), (req, res) => {
        const { level = 'info', message, context } = req.body;
        console.log(`[CLIENT:${level.toUpperCase()}] ${message}`, context || '');
        res.status(200).send('OK');
    });

    app.use(
        '/api',
        createProxyMiddleware({
            target: 'http://backend:8080',
            changeOrigin: true,
            pathRewrite: (path, req) => "/api" + path,
        })
    );

    app.use(
        '/graphql',
        createProxyMiddleware({
            target: 'http://backend:8080',
            changeOrigin: true,
            ws: true,
            pathRewrite: (path, req) => req.originalUrl,
        })
    );
};