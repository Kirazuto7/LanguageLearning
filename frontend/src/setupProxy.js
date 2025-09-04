const { createProxyMiddleware } = require('http-proxy-middleware');
const express = require('express');

module.exports = function(app) {
    app.use(express.json());
    app.post('/api/logs/client', (req, res) => {
        const { level = 'info', message, context } = req.body;
        console.log(`[CLIENT:${level.toUpperCase()}] ${message}`, context || '');
        res.status(200).send('OK');
    });

    app.use(
        '/api',
        createProxyMiddleware({
            target: 'http://backend:8080',
            changeOrigin: true,
            pathRewrite: (path, req) => '/api' + path,
            onProxyReq: (proxyReq, req, res) => {
                // This will log the original path from the browser and the path being sent to the backend.
                console.log(`[HPM] /api proxy: original: ${req.originalUrl} -> rewritten: ${proxyReq.path}`);
            }
            //cookieDomainRewrite: '',
            //cookiePathRewrite: '/',
        })
    );

    app.use(
        '/graphql',
        createProxyMiddleware({
            target: 'http://backend:8080',
            changeOrigin: true,
            ws: true,
            pathRewrite: (path, req) => '/graphql' + path,
            //cookieDomainRewrite: '',
            //cookiePathRewrite: '/',
        })
    );
};