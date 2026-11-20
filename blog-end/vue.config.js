const { defineConfig } = require('@vue/cli-service')
const path = require('path');
const webpack = require('webpack'); // 新增这行引入webpack
const versionInfo = require('./src/version.json');
module.exports = defineConfig({
  configureWebpack: config => {
    return {
      plugins: [
        new webpack.DefinePlugin({
          'process.env.APP_VERSION': JSON.stringify(versionInfo.version),
          'process.env.BUILD_TIME': JSON.stringify(new Date().toISOString())
        })
      ]
    }
  },
  // chainWebpack: config => {
  //   // 确保 CSS 文件使用相对路径
  //   config.plugin('extract-css').tap(args => {
  //     args[0].filename = 'static/css/[name].[contenthash:8].css'
  //     args[0].chunkFilename = 'static/css/[name].[contenthash:8].css'
  //     return args
  //   })
  // },
  // publicPath: '/', // 关键：使用相对路径
  // outputDir: 'dist',
  // assetsDir: 'static',
  // productionSourceMap: false, // 关闭 source map 减小体积
  
  publicPath: process.env.NODE_ENV === 'production' ? '/' : '/',
  devServer: {  //配置开发环境
    // host: '180.76.189.135',
    host: '127.0.0.1',
    // open: true,
    port: 8091,
    proxy: {
      '^/api': {
        // target: 'http://180.76.189.135:8090/',
        target: 'http://127.0.0.1:8090/',
        // target: 'http://backend:8090/',
        ws: false,
        changOrigin: true,      //允许跨域
        pathRewrite: {
          '^/api': '/'
        }
      }
    }
  }

})
