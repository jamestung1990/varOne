import path from 'path';
import webpack from 'webpack';
import ExtractTextPlugin from 'extract-text-webpack-plugin';

export default {
  entry: [
    'bootstrap-loader/extractStyles',
    path.resolve(__dirname, '../app/main.js')
  ],
  output: {
    path: path.resolve(__dirname, '../dist'),
    filename: 'app.js'
  },
  module: {
    preLoaders: [
      { test: /\.js$/, exclude: /node_modules/, loader: 'eslint' }
    ],
    loaders: [
      { test: /\.js$/, loaders: [ 'babel' ], exclude: /node_modules/ },
      { test: /\.scss$/, loaders: [ 'style', 'css', 'sass' ] },
      { test: /\.(woff2?|ttf|eot|svg)$/, loader: 'url?limit=10000' },
      { test: /bootstrap-sass\/assets\/javascripts\//, loader: 'imports?jQuery=jquery' },
      {
        test: /\.(jpe?g|png|gif|svg|woff|woff2|eot|ttf)(\?v=[0-9].[0-9].[0-9])?$/,
        loader: 'file?name=[sha512:hash:base64:7].[ext]',
        exclude: /node_modules\/(?!font-awesome)/
      },
      { test: /\.css$/, loader: 'style-loader!css-loader' }
    ]
  },
  plugins: [
    new webpack.ProvidePlugin({
      $: 'jquery',
      jQuery: 'jquery',
      'window.jQuery': 'jquery',
      'root.jQuery': 'jquery'
    }),
    new ExtractTextPlugin('app.css', { allChunks: true }),
    new webpack.optimize.DedupePlugin(),
    new webpack.optimize.OccurenceOrderPlugin(),
    new webpack.optimize.UglifyJsPlugin({
      compress: {
        warnings: false,
        screw_ie8: true,
        sequences: true,
        dead_code: true,
        drop_debugger: true,
        comparisons: true,
        conditionals: true,
        evaluate: true,
        booleans: true,
        loops: true,
        unused: true,
        hoist_funs: true,
        if_return: true,
        join_vars: true,
        cascade: true,
        drop_console: true
      },
      output: {
        comments: false
      }
    })
  ],
  resolve: {
    extensions: [ '', '.js', '.jsx' ],
    // root: [path.join(__dirname, "public", "javascripts")],
    modulesDirectories: [ 'node_modules' ]
  }

};
