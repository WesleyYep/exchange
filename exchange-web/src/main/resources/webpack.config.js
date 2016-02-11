var path = require('path');
var webpack = require('webpack');

var config = {
    entry: __dirname + '/index.js',
    output: { path: __dirname + '/static/', filename: 'bundle.js' },
    externals: {
        'Stomp': 'Stomp',
        'SockJS': 'SockJS'
    },
    module: {
        loaders: [
            {
                test: /.jsx?$/,
                loader: 'babel-loader',
                exclude: /node_modules/,
                query: {
                    presets: ['es2015', 'react']
                }
            }

        ]
    },
    modulesDirectories: ['node_modules']
};


module.exports = config;