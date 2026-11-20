<template>
  <div id="app">
    <router-view />
    <div class="global-version">
      Version: {{ appVersion }}
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      appVersion: process.env.APP_VERSION || 'dev' // 开发环境显示dev
    }
  },
  created() {
    this.checkAutoLogin();
  },
  methods: {
    async checkAutoLogin() {
      const accessToken = localStorage.getItem('ADMIN-TOKEN');
      const refreshToken = localStorage.getItem('refresh_token');

      if (!accessToken && refreshToken) {
        try {
          const response = await this.$http.post('/user/autoLogin');
          if (response.data.status === 200) {
            const { accessToken } = res.data.data.accessToken;
            localStorage.setItem('ADMIN-TOKEN', accessToken);
          }
        } catch (error) {
          console.log('自动登录失败:', error);
        }
      }
    },
  }
}
</script>

<style lang="less">
@import "./assets/css/normalize.css";
@import "./assets/css/mybase.css";
@import "./assets/less/org-tree.less";
@import './assets/css/global.css';

/* 隐藏滚动条 */
::-webkit-scrollbar {
  display: none;
}

/* 适用于所有浏览器 */
body {
  overflow: -moz-scrollbars-none;
  -ms-overflow-style: none;
}

#app {
  width: 100%;
  height: 100%;

  .global-version {
    position: fixed;
    bottom: 10px;
    right: 10px;
    color: #666;
    font-size: 12px;
    z-index: 9999;
  }
}

.icon {
  width: 1.4em;
  height: 1.4em;
  vertical-align: -0.95em;
  fill: currentColor;
  overflow: hidden;
}

.iconfont {
  vertical-align: middle;
  margin-left: 5px;
  margin-right: 10px !important;
  width: 18px !important;
  text-align: center !important;
  font-size: 16px !important;
}
</style>
