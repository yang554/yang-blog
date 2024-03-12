import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // 本地服务的配置
  server: {
    port: 8092, // 端口 默认5173
    // open: true,
    host: '127.0.0.1', // 主机地址 默认localhost
    // 代理配置
    proxy: {
      '/api': {
        target: 'http://118.25.110.52:8090/',
        // target: 'http://127.0.0.1:8090/',
        changeOrigin: true,
        secure: false,  
        rewrite: (path) => path.replace(/^\/api/, '')
      },
    },
  },
  build: {
    // outDir: 'ts-super-web', // 生成输出的根目录。如果该目录存在，则会在生成之前将其删除。 默认文件夹名称为dist
    target: 'esnext',
    terserOptions: {
      compress: {
        drop_console: true, // 生产环境去掉控制台 console
        drop_debugger: true, // 生产环境去掉控制台 debugger 默认就是true
        dead_code: true, // 删除无法访问的代码 默认就是true
      }
    },
    chunkSizeWarningLimit: 2000, // 调整区块大小警告限制
  }
})




