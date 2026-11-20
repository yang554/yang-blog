<template>
    <div class='login-container'>
        <!-- 添加全息投影装饰 -->
        <div class="holographic-circle-1"></div>
        <div class="holographic-circle-2"></div>

        <!-- 更新粒子效果为科技感蓝色 -->
        <vue-particles color='#00e5ff' :particleOpacity='0.7' :particlesNumber='150' shapeType='circle' :particleSize='3'
            linesColor='#7e57c2' :linesWidth='1' :lineLinked='true' :lineOpacity='0.4' :linesDistance='180' :moveSpeed='3'
            :hoverEffect='true' hoverMode='grab' :clickEffect='true' clickMode='push' class="particles-bg">
        </vue-particles>

        <!-- 左侧品牌展示区 -->
        <div class="brand-section">
            <div class="brand-content">
                <div class="logo-container">
                    <div class="glowing-effect"></div>
                    <img src='@/assets/images/logo.png' alt='枫萱悦宁' class="logo-image">
                </div>
                <div class="welcome-text">
                    <h1>枫萱悦宁</h1>
                    <p class="subtitle">家族树管理系统</p>
                    <div class="feature-list">
                        <div class="feature-item" v-for="(feature, index) in features" :key="index">
                            <i :class="feature.icon"></i>
                            <span>{{ feature.text }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 右侧登录表单 -->
        <div class="login-form-section">
            <div class="form-container">
                <!-- 添加科技感边框 -->
                <div class="tech-border top"></div>
                <div class="tech-border right"></div>
                <div class="tech-border bottom"></div>
                <div class="tech-border left"></div>

                <div class="form-header">
                    <h2>欢迎回来</h2>
                    <p>请登录您的账号继续使用系统</p>
                </div>

                <el-form :model='userForm' :rules='rules' ref='userForm' label-width='0' class='user-form'
                    @keyup.enter.native="submitForm('userForm')">

                    <div class="input-group">
                        <i class="el-icon-user input-icon"></i>
                        <el-form-item prop='username' class="form-item">
                            <el-input v-model='userForm.username' placeholder="请输入用户名" class="custom-input">
                            </el-input>
                        </el-form-item>
                    </div>

                    <div class="input-group">
                        <i class="el-icon-lock input-icon"></i>
                        <el-form-item prop='password' class="form-item">
                            <el-input v-model='userForm.password' placeholder="请输入密码" show-password class="custom-input">
                            </el-input>
                        </el-form-item>
                    </div>

                    <div class="input-group">
                        <i class="el-icon-key input-icon"></i>
                        <el-form-item prop="verify_code" class="form-item">
                            <el-row :gutter="15">
                                <el-col :span="16">
                                    <el-input v-model="userForm.verify_code" placeholder="请输入验证码" class="custom-input">
                                    </el-input>
                                </el-col>
                                <el-col :span="8">
                                    <div class="captcha-wrapper" @click="updateVerifyCode">
                                        <el-image id="verify_captcha" style="width: 100%; height: 40px; border-radius: 6px;"
                                            :src="captchaUrl" fit="scale-down">
                                        </el-image>
                                        <div class="captcha-hint">点击刷新</div>
                                    </div>
                                </el-col>
                            </el-row>
                        </el-form-item>
                    </div>

                    <div class="remember-forgot">
                        <el-checkbox v-model="userForm.rememberMe">记住我</el-checkbox>
                        <el-link type="primary" :underline="false" @click="registerBtn" class="forgot-link">忘记密码?</el-link>
                    </div>

                    <el-form-item>
                        <el-button type='primary' @click='submitForm("userForm")' class="login-btn">
                            登 录
                        </el-button>
                    </el-form-item>

                    <div class="register-section">
                        <span>还没有账号？</span>
                        <el-button type='text' @click='registerBtn' class="register-btn">
                            立即注册
                        </el-button>
                    </div>
                </el-form>
            </div>
        </div>

        <!-- 页脚 -->
        <div class="footer">
            <div class="glowing-text">
                <p>© 2025 枫萱悦宁家族管理系统 | 传承家族记忆，连接世代情感</p>
            </div>
        </div>
    </div>
</template>

<script>
import { _userLogin, _getCaptchaImg } from "@/api/api.js"

export default {
    name: 'LoginView',
    data() {
        return {
            captchaUrl: '',

            isUsernamePasswordLogin: true,
            userForm: {
                username: '',
                password: '',
                verify_code: '',
                captcha_key: '',
                rememberMe: false,
            },
            rules: {
                username: [
                    { required: true, message: '请输入用户名', trigger: 'blur' },
                    { min: 1, max: 18, message: '长度在 1 到 18 个字符', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 3, max: 18, message: '长度在 3 到 18 个字符', trigger: 'blur' }
                ],
                verify_code: [
                    { required: true, message: '请输入验证码', trigger: 'blur' },
                ]
            },
            features: [
                { icon: "el-icon-user-solid", text: "家族成员管理" },
                { icon: "el-icon-data-line", text: "族谱可视化" },
                { icon: "el-icon-picture", text: "家族相册" },
                { icon: "el-icon-notebook-2", text: "家族资产管理" }
            ]
        };
    },
    methods: {
        //登录
        submitForm(formName) {
            if (formName === 'userForm') {
                this.$refs[formName].validate(valid => {
                    if (valid) {
                        _userLogin(this.userForm).then(res => {
                            if (res.data.status === 200) {
                                // 调用 Vuex action 保存登录状态
                                this.$store.dispatch('login', {
                                    token: res.data.data.accessToken,
                                    refreshToken:res.data.data.refreshToken,
                                    user: res.data.data.user
                                });

                                if (this.userForm.rememberMe) {
                                    // 记住我时存储刷新Token
                                    localStorage.setItem('refresh_token', res.data.data.refreshToken);
                                }

                                this.$message.success('登录成功');
                                const redirect = this.$router.currentRoute.query.redirect || '/admin/dashboard';
                                this.$router.push(redirect);

                            } else {
                                this.$message.error(res.data.msg || '登录失败');
                                this.updateVerifyCode(); // 刷新验证码
                            }
                        })
                    } else {
                        console.log('error submit!!')
                        return false
                    }
                })
            }
        },
        //注册
        registerBtn() {
            this.$message.info("暂不开放此功能!");
            // this.$router.push({
            //     //   接受路由参数然后跳转
            //     path: '/register' || "/"
            // });
        },
        //更新验证码
        async updateVerifyCode(e) {
            const result = await _getCaptchaImg()
            if (result.status === 200 && result.data) {
                this.captchaUrl = result.data.imageUrl
                this.userForm.captcha_key = result.data.captchaKey
            } else {
                throw new Error('获取验证码失败')
            }
        },
        keyDown(e) {
            // 回车则执行登录方法 enter键的ASCII是13
            if (e.key == 'Enter') {
                this.submitForm("userForm"); // 定义的登录方法 ...............
                e.preventDefault(); // 去掉默认的换行
            }
        },
    },
    mounted() {
        // 如果已经登录，直接跳转到首页
        if (this.$store.getters.isAuthenticated) {
            this.redirectToTarget();
        } else {
            this.updateVerifyCode();
        }
        // 绑定监听事件
        window.addEventListener("keydown", this.keyDown);
    },
    destroyed() {

        // 销毁事件
        window.removeEventListener("keydown", this.keyDown);
    },
    created() {
        // window.addEventListener('keydown', this.handleKeyCode, false)
    }
}
</script>

<style lang="less" scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;600;700&display=swap');

// 科技感配色方案
@primary-color: #00e5ff; // 科技蓝
@secondary-color: #7e57c2; // 紫色
@accent-color: #00ffcc; // 青绿色
@dark-bg: #0a0e17; // 深空蓝
@card-bg: rgba(16, 22, 36, 0.85); // 半透明深蓝
@light-color: rgba(255, 255, 255, 0.9);

.login-container {
    width: 100%;
    height: 100vh;
    display: flex;
    font-family: 'Noto Sans SC', sans-serif;
    background: radial-gradient(ellipse at bottom, #0a0e17 0%, #020204 100%),
        linear-gradient(to bottom, rgba(0, 229, 255, 0.1), transparent),
        url('@/assets/images/bg.jpg') no-repeat center center;
    background-size: cover;
    position: relative;
    overflow: hidden;

    .holographic-circle-1 {
        position: absolute;
        top: 20%;
        left: 10%;
        width: 300px;
        height: 300px;
        background: radial-gradient(circle, rgba(126, 87, 194, 0.15) 0%, transparent 70%);
        border-radius: 50%;
        filter: blur(40px);
        z-index: 0;
        animation: floatHolo 15s infinite alternate ease-in-out;
    }

    .holographic-circle-2 {
        position: absolute;
        bottom: 10%;
        right: 15%;
        width: 200px;
        height: 200px;
        background: radial-gradient(circle, rgba(0, 229, 255, 0.1) 0%, transparent 70%);
        border-radius: 50%;
        filter: blur(30px);
        z-index: 0;
        animation: floatHolo2 12s infinite alternate ease-in-out;
    }

    .particles-bg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 1;
    }

    .brand-section {
        flex: 1;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 40px;
        z-index: 2;
        position: relative;

        .brand-content {
            max-width: 600px;
            text-align: center;
            padding: 40px;
            background: @card-bg;
            border-radius: 20px;
            backdrop-filter: blur(12px);
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.5),
                inset 0 0 30px rgba(0, 229, 255, 0.15);
            border: 1px solid rgba(@primary-color, 0.3);

            .logo-container {
                width: 150px;
                height: 150px;
                margin: 0 auto 25px;
                background: rgba(255, 255, 255, 0.1);
                border-radius: 50%;
                display: flex;
                justify-content: center;
                align-items: center;
                position: relative;
                box-shadow: 0 0 30px rgba(0, 229, 255, 0.2),
                    inset 0 0 20px rgba(255, 255, 255, 0.1);
                border: 1px solid rgba(@primary-color, 0.2);

                .glowing-effect {
                    position: absolute;
                    width: 100%;
                    height: 100%;
                    border-radius: 50%;
                    background: radial-gradient(circle, rgba(0, 229, 255, 0.3) 0%, transparent 70%);
                    filter: blur(10px);
                    animation: glowPulse 3s infinite alternate;
                }

                .logo-image {
                    width: 100px;
                    height: 100px;
                    border-radius: 50%;
                    object-fit: contain;
                    filter: drop-shadow(0 0 15px rgba(0, 229, 255, 0.5));
                    position: relative;
                    z-index: 2;
                }
            }

            .welcome-text {
                color: @light-color;
                margin-bottom: 30px;

                h1 {
                    font-size: 42px;
                    font-weight: 700;
                    margin-bottom: 10px;
                    text-shadow: 0 0 15px @primary-color;
                    letter-spacing: 2px;
                    background: linear-gradient(90deg, @primary-color, @accent-color);
                    -webkit-background-clip: text;
                    -webkit-text-fill-color: transparent;
                }

                .subtitle {
                    font-size: 22px;
                    color: @accent-color;
                    margin-bottom: 40px;
                    position: relative;
                    display: inline-block;
                    text-shadow: 0 0 8px rgba(0, 255, 204, 0.5);

                    &::after {
                        content: '';
                        position: absolute;
                        bottom: -10px;
                        left: 50%;
                        transform: translateX(-50%);
                        width: 80px;
                        height: 3px;
                        background: linear-gradient(90deg, transparent, @accent-color, transparent);
                        box-shadow: 0 0 10px @accent-color;
                    }
                }
            }

            .feature-list {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 15px;

                .feature-item {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    padding: 12px 20px;
                    background: rgba(25, 35, 58, 0.6);
                    border-radius: 12px;
                    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
                    backdrop-filter: blur(4px);
                    border: 1px solid rgba(@primary-color, 0.2);
                    position: relative;
                    overflow: hidden;
                    z-index: 2;

                    &::before {
                        content: '';
                        position: absolute;
                        top: 0;
                        left: 0;
                        width: 100%;
                        height: 100%;
                        background: linear-gradient(135deg, rgba(0, 229, 255, 0.1), rgba(126, 87, 194, 0.1));
                        z-index: -1;
                    }

                    i {
                        font-size: 24px;
                        color: @accent-color;
                        margin-right: 12px;
                        transition: all 0.3s;
                        text-shadow: 0 0 10px rgba(0, 255, 204, 0.5);
                    }

                    span {
                        font-size: 16px;
                        color: @light-color;
                        font-weight: 500;
                    }

                    &:hover {
                        background: rgba(37, 52, 86, 0.7);
                        transform: translateY(-5px);
                        box-shadow: 0 0 25px rgba(0, 229, 255, 0.3);

                        i {
                            transform: scale(1.2);
                            text-shadow: 0 0 15px @accent-color;
                        }
                    }
                }
            }
        }
    }

    .login-form-section {
        width: 450px;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 40px 20px;
        z-index: 2;

        .form-container {
            width: 100%;
            padding: 40px;
            background: @card-bg;
            border-radius: 16px;
            backdrop-filter: blur(12px);
            box-shadow: 0 20px 50px rgba(0, 0, 0, 0.5),
                inset 0 0 30px rgba(0, 229, 255, 0.15);
            border: 1px solid rgba(@primary-color, 0.3);
            position: relative;
            overflow: hidden;

            .tech-border {
                position: absolute;
                background: @primary-color;
                box-shadow: 0 0 10px @primary-color;
                z-index: 3;

                &.top,
                &.bottom {
                    height: 2px;
                    width: 100%;
                    left: 0;
                    animation: scanlineX 6s linear infinite;
                }

                &.left,
                &.right {
                    width: 2px;
                    height: 100%;
                    top: 0;
                    animation: scanlineY 8s linear infinite;
                }

                &.top {
                    top: 0;
                }

                &.bottom {
                    bottom: 0;
                }

                &.left {
                    left: 0;
                }

                &.right {
                    right: 0;
                }
            }

            .form-header {
                text-align: center;
                margin-bottom: 35px;

                h2 {
                    font-size: 28px;
                    font-weight: 700;
                    color: @light-color;
                    margin-bottom: 8px;
                    letter-spacing: 1px;
                    text-shadow: 0 0 10px @primary-color;
                }

                p {
                    font-size: 16px;
                    color: rgba(255, 255, 255, 0.7);
                    margin: 0;
                }
            }

            .user-form {
                .input-group {
                    position: relative;
                    margin-bottom: 28px;

                    .input-icon {
                        position: absolute;
                        left: 18px;
                        top: 50%;
                        transform: translateY(-50%);
                        font-size: 20px;
                        color: @accent-color;
                        z-index: 2;
                        text-shadow: 0 0 8px rgba(0, 255, 204, 0.5);
                    }

                    .form-item {
                        margin-bottom: 0;
                    }

                    .custom-input {
                        /deep/ .el-input__inner {
                            padding-left: 52px;
                            height: 52px;
                            border-radius: 10px;
                            border: 1px solid rgba(@primary-color, 0.3);
                            background: rgba(10, 15, 28, 0.7);
                            color: @light-color;
                            font-size: 16px;
                            transition: all 0.3s;
                            box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.3),
                                0 0 15px rgba(0, 229, 255, 0.1);

                            &:focus {
                                border-color: @accent-color;
                                box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.3),
                                    0 0 15px rgba(0, 255, 204, 0.3);
                            }

                            &::placeholder {
                                color: rgba(192, 180, 157, 0.7);
                            }
                        }
                    }

                    .captcha-wrapper {
                        position: relative;
                        height: 40px;
                        cursor: pointer;
                        transition: all 0.3s;
                        border-radius: 6px;
                        overflow: hidden;
                        border: 1px solid rgba(@primary-color, 0.3);
                        box-shadow: 0 0 10px rgba(0, 229, 255, 0.1);

                        &:hover {
                            transform: translateY(-2px);
                            box-shadow: 0 0 20px rgba(0, 229, 255, 0.3);

                            .captcha-hint {
                                opacity: 1;
                            }
                        }

                        .captcha-hint {
                            position: absolute;
                            bottom: 0;
                            left: 0;
                            right: 0;
                            background: rgba(0, 0, 0, 0.7);
                            color: white;
                            font-size: 12px;
                            text-align: center;
                            padding: 3px 0;
                            opacity: 0;
                            transition: opacity 0.3s;
                        }
                    }
                }

                .remember-forgot {
                    display: flex;
                    justify-content: space-between;
                    align-items: center;
                    margin-bottom: 30px;

                    /deep/ .el-checkbox__label {
                        color: rgba(255, 255, 255, 0.7);
                        font-size: 14px;
                    }

                    .forgot-link {
                        color: @accent-color;
                        font-size: 14px;
                        font-weight: 500;

                        &:hover {
                            color: @primary-color;
                            text-shadow: 0 0 8px @primary-color;
                        }
                    }
                }

                .login-btn {
                    width: 100%;
                    height: 52px;
                    border-radius: 10px;
                    background: linear-gradient(135deg, @primary-color, @secondary-color);
                    border: none;
                    font-size: 18px;
                    font-weight: 600;
                    letter-spacing: 2px;
                    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
                    box-shadow: 0 0 25px rgba(0, 229, 255, 0.5);
                    color: white;
                    position: relative;
                    overflow: hidden;
                    z-index: 2;

                    &::before {
                        content: '';
                        position: absolute;
                        top: -50%;
                        left: -50%;
                        width: 200%;
                        height: 200%;
                        background: linear-gradient(45deg,
                                transparent,
                                rgba(255, 255, 255, 0.3),
                                transparent);
                        transform: rotate(45deg);
                        transition: all 0.6s;
                        z-index: -1;
                    }

                    &:hover {
                        transform: translateY(-3px);
                        box-shadow: 0 0 35px rgba(0, 229, 255, 0.8);
                        background: linear-gradient(135deg, lighten(@primary-color, 10%), lighten(@secondary-color, 10%));

                        &::before {
                            top: 0;
                            left: 0;
                        }
                    }

                    &:active {
                        transform: translateY(1px);
                    }
                }

                .register-section {
                    text-align: center;
                    margin-top: 25px;
                    color: rgba(255, 255, 255, 0.7);
                    font-size: 14px;

                    .register-btn {
                        padding: 0;
                        margin-left: 6px;
                        font-size: 15px;
                        font-weight: 600;
                        color: @accent-color;

                        &:hover {
                            color: @primary-color;
                            text-shadow: 0 0 8px @primary-color;
                        }
                    }
                }
            }
        }
    }

    .footer {
        position: absolute;
        bottom: 25px;
        width: 100%;
        text-align: center;
        z-index: 2;

        .glowing-text {
            p {
                color: rgba(255, 255, 255, 0.6);
                font-size: 13px;
                text-shadow: 0 0 8px rgba(0, 229, 255, 0.5);
                letter-spacing: 0.5px;
            }
        }
    }
}

// 新增动画效果
@keyframes floatHolo {
    0% {
        transform: translate(0, 0) rotate(0deg);
    }

    100% {
        transform: translate(30px, -40px) rotate(5deg);
    }
}

@keyframes floatHolo2 {
    0% {
        transform: translate(0, 0);
    }

    100% {
        transform: translate(-30px, 30px);
    }
}

@keyframes glowPulse {
    0% {
        opacity: 0.5;
    }

    100% {
        opacity: 0.8;
    }
}

@keyframes scanlineX {
    0% {
        transform: translateX(-100%);
    }

    100% {
        transform: translateX(100%);
    }
}

@keyframes scanlineY {
    0% {
        transform: translateY(-100%);
    }

    100% {
        transform: translateY(100%);
    }
}

// 响应式调整
@media (max-width: 1200px) {
    .login-container {
        .brand-section {
            .brand-content {
                padding: 30px;

                .logo-container {
                    width: 130px;
                    height: 130px;

                    .logo-image {
                        width: 85px;
                        height: 85px;
                    }
                }

                .welcome-text {
                    h1 {
                        font-size: 36px;
                    }

                    .subtitle {
                        font-size: 20px;
                    }
                }

                .feature-list {
                    .feature-item {
                        padding: 10px 15px;

                        i {
                            font-size: 20px;
                        }

                        span {
                            font-size: 15px;
                        }
                    }
                }
            }
        }
    }
}

@media (max-width: 992px) {
    .login-container {
        flex-direction: column;
        padding: 20px;

        .brand-section {
            width: 100%;
            padding: 30px 20px 20px;

            .brand-content {
                max-width: 500px;
                padding: 25px;

                .logo-container {
                    width: 110px;
                    height: 110px;

                    .logo-image {
                        width: 70px;
                        height: 70px;
                    }
                }

                .welcome-text {
                    h1 {
                        font-size: 32px;
                    }

                    .subtitle {
                        font-size: 18px;
                        margin-bottom: 30px;
                    }
                }

                .feature-list {
                    grid-template-columns: 1fr;
                    max-width: 350px;
                    margin: 0 auto;

                    .feature-item {
                        padding: 12px 20px;
                    }
                }
            }
        }

        .login-form-section {
            width: 100%;
            padding: 20px;

            .form-container {
                max-width: 500px;
                margin: 0 auto;
                padding: 30px;
            }
        }

        .footer {
            position: relative;
            margin-top: 30px;
            padding: 20px;
        }
    }
}
</style>