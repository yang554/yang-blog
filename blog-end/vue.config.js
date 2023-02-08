const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  // transpileDependencies: true
  devServer: {  //配置开发环境
    host: 'localhost',
    open: true,
    port: 8081,
    proxy: {
      '^/api': {
        target: 'http://localhost:8080/',
        ws: false,
        changOrigin: true,      //允许跨域
        pathRewrite: {
          '^/api': ''             //请求的时候使用这个api就可以
        }
      }
    }
  }
  
})
