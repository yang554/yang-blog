const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  // transpileDependencies: true
  // IntOnSave:true,
  publicPath:"/",
  devServer: {  //配置开发环境
    // host: '118.25.110.52',
    host: '127.0.0.1',
    // open: true,
    port: 8091,
    proxy: {
      '^/api': {
        target: 'http://118.25.110.52:8090/',
        // target: 'http://127.0.0.1:8090/',
        ws: false,
        changOrigin: true,      //允许跨域
        pathRewrite: {
          '^/api': '/'             
        }
      }
    }
  }

})
