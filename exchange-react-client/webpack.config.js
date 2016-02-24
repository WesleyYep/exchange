var path = require('path');
var webpack = require('webpack');

var config = {
    entry: __dirname + '/app/index.js',
    output: { path: __dirname + '/dist/', filename: 'bundle.js' },
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
            },
            {
                test: /\.css$/,
                loader: 'style-loader!css-loader'
            },            {
                test: /\.(ttf|eot|svg|woff(2)?)(\?[a-z0-9]+)?$/,
                loader: 'file-loader'
            }

        ]
    },
    modulesDirectories: ['node_modules']
};


module.exports = config;
