<template>
  <view class="debug-container">
    <view class="debug-section">
      <text class="section-title">接口连通性测试</text>
      
      <button @tap="testBaseConnectivity" class="test-btn">
        测试基础连通性
      </button>
      
      <button @tap="testCaptchaAPI" class="test-btn">
        测试验证码接口
      </button>
      
      <button @tap="testLoginAPI" class="test-btn">
        测试登录接口
      </button>
    </view>

    <view class="result-section">
      <text class="section-title">测试结果</text>
      <scroll-view scroll-y class="result-log">
        <text class="log-item" v-for="(log, index) in logs" :key="index">
          {{ log }}
        </text>
      </scroll-view>
    </view>

    <!-- 验证码显示区域 -->
    <view class="captcha-section" v-if="captchaBase64">
      <text class="section-title">验证码显示</text>
      <image :src="captchaBase64" class="captcha-image" mode="widthFix"></image>
      <button @tap="refreshCaptcha" class="refresh-btn">刷新验证码</button>
    </view>
  </view>
</template>

<script>
import { userApi } from '@/api/index.js'

export default {
  data() {
    return {
      logs: [],
      captchaBase64: ''
    }
  },
  methods: {
    // 添加日志
    addLog(message) {
      const timestamp = new Date().toLocaleTimeString()
      this.logs.unshift(`[${timestamp}] ${message}`)
      console.log(message)
    },

    // 测试基础连通性
    async testBaseConnectivity() {
      this.addLog('开始测试基础连通性...')
      
      try {
        // 测试简单的 GET 请求
        const result = await uni.request({
          url: 'http://192.168.0.100:8090/user/getCaptcha',
          method: 'GET',
          timeout: 5000
        })
        console.log(JSON.stringify(result))
        this.addLog(`✅ 请求成功，状态码: ${result}`)
        this.addLog(`响应头: ${JSON.stringify(result.header)}`)
        this.addLog(`响应数据长度: ${JSON.stringify(result.data).length}`)
        
      } catch (error) {
        this.addLog(`❌ 请求失败: ${error.errMsg || error.message}`)
        
        if (error.errMsg?.includes('timeout')) {
          this.addLog('⏰ 请求超时，请检查网络或服务器状态')
        }
      }
    },

    // 测试验证码接口
    async testCaptchaAPI() {
      this.addLog('开始测试验证码接口...')
      
      try {
        // 方法1: 直接使用 uni.request
        const result = await uni.request({
          url: '/api/user/getCaptcha',
          method: 'GET',
          responseType: 'arraybuffer', // 重要：告诉 Uni-app 这是二进制数据
          timeout: 8000
        })
        
        this.addLog(`✅ 验证码接口请求成功`)
        this.addLog(`状态码: ${result.statusCode}`)
        this.addLog(`数据类型: ${typeof result.data}`)
        this.addLog(`数据长度: ${result.data?.length || 0}`)
        
        if (result.statusCode === 200 && result.data) {
          // 将 ArrayBuffer 转换为 Base64
          const base64 = this.arrayBufferToBase64(result.data)
          this.captchaBase64 = `data:image/jpeg;base64,${base64}`
          this.addLog('✅ 验证码图片已生成并显示')
        } else {
          this.addLog('❌ 验证码数据为空或格式不正确')
        }
        
      } catch (error) {
        this.addLog(`❌ 验证码接口请求失败: ${error.errMsg || error.message}`)
        this.addLog('💡 提示：请检查后端服务是否启动，接口路径是否正确')
      }
    },

    // 测试封装后的验证码 API
    async testCaptchaWithApi() {
      this.addLog('测试封装后的验证码 API...')
      
      try {
        // 使用我们封装的 API
        const result = await userApi.getCaptcha()
        this.addLog(`✅ 封装API调用成功`)
        this.addLog(`返回数据: ${JSON.stringify(result).substring(0, 100)}...`)
        
      } catch (error) {
        this.addLog(`❌ 封装API调用失败: ${error.message || error}`)
      }
    },

    // 测试登录接口
    async testLoginAPI() {
      this.addLog('测试登录接口...')
      
      const testData = {
        username: 'testuser',
        password: 'testpass',
        verify_code: '1234'
      }
      
      try {
        const result = await userApi.login(testData)
        this.addLog(`✅ 登录接口请求成功`)
        this.addLog(`响应状态: ${result.status}`)
        this.addLog(`响应消息: ${result.msg}`)
        
      } catch (error) {
        this.addLog(`❌ 登录接口请求失败: ${error.message || error}`)
      }
    },

    // ArrayBuffer 转 Base64
    arrayBufferToBase64(buffer) {
      let binary = ''
      const bytes = new Uint8Array(buffer)
      const len = bytes.byteLength
      
      for (let i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[i])
      }
      
      return uni.arrayBufferToBase64 ? 
        uni.arrayBufferToBase64(buffer) : 
        window.btoa(binary)
    },

    // 刷新验证码
    refreshCaptcha() {
      this.testCaptchaAPI()
    }
  },
  
  onLoad() {
    this.addLog('调试页面已加载')
    this.addLog('请点击按钮开始测试接口连通性')
  }
}
</script>

<style scoped>
.debug-container {
  padding: 20rpx;
  background: #f5f5f5;
  min-height: 100vh;
}

.debug-section {
  background: white;
  border-radius: 10rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
  display: block;
}

.test-btn {
  background: #007AFF;
  color: white;
  border: none;
  border-radius: 8rpx;
  padding: 20rpx 30rpx;
  margin: 10rpx 0;
  font-size: 28rpx;
}

.test-btn:active {
  background: #0056CC;
}

.result-section {
  background: white;
  border-radius: 10rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.result-log {
  height: 400rpx;
  background: #f8f8f8;
  border: 1rpx solid #eee;
  border-radius: 8rpx;
  padding: 20rpx;
}

.log-item {
  display: block;
  font-size: 24rpx;
  color: #666;
  line-height: 1.6;
  margin-bottom: 8rpx;
  font-family: monospace;
}

.captcha-section {
  background: white;
  border-radius: 10rpx;
  padding: 30rpx;
  text-align: center;
}

.captcha-image {
  width: 200rpx;
  height: 80rpx;
  margin: 20rpx 0;
  border: 1rpx solid #ddd;
}

.refresh-btn {
  background: #34C759;
  color: white;
  border: none;
  border-radius: 8rpx;
  padding: 15rpx 25rpx;
  font-size: 26rpx;
}
</style>