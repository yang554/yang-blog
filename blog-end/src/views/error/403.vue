<template>
    <div class="error-page">
        <div class="error-content">
            <div class="error-code">403</div>
            <div class="error-message">无权限访问</div>
            <div class="error-description">抱歉，您没有权限访问此页面</div>
            <el-button type="primary" @click="goHome">返回首页</el-button>
            <el-button type="text" @click="goBack">返回上一页</el-button>
        </div>
    </div>
</template>
  
<script>
export default {
    name: '403',
    methods: {
        goHome() {
            this.$router.push('/admin/dashboard')
        },
        goBack() {
            this.$router.go(-1)
        }
    }
}
</script>
  
<style scoped>
.error-page {
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
}

.error-content {
    text-align: center;
    padding: 40px;
    background: rgba(30, 41, 59, 0.8);
    border-radius: 16px;
    border: 1px solid #1e293b;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.5);
}

.error-code {
    font-size: 120px;
    font-weight: 700;
    background: linear-gradient(to right, #f59e0b, #ef4444);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    margin-bottom: 20px;
    text-shadow: 0 0 20px rgba(245, 158, 11, 0.3);
}

.error-message {
    font-size: 24px;
    color: #e2e8f0;
    margin-bottom: 10px;
}

.error-description {
    color: #94a3b8;
    margin-bottom: 30px;
    font-size: 16px;
}

.el-button {
    margin: 0 10px;
    padding: 12px 30px;
    font-size: 16px;
    border-radius: 8px;
}

.el-button--text {
    color: #94a3b8;
}
</style>