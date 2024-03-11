<template>
    <div class='login-view'>
        <el-card class='login-card'>
            <div slot='header' class='clearfix logo-title'>
                <div class='logo-image'><img src='@/assets/images/logo.png' alt='' width='50px' height='50px'></div>
                <div style="font-size: 26px;font-weight: bold;height: 50px;line-height: 50px;margin-left: 10px">
                    杨柳博客后台管理</div>
            </div>

            <el-form :model='userForm' :rules='rules' ref='userForm' label-width='100px' label-position='left'
                class='userFormClass'>

                <el-form-item label='用户名' prop='username'>
                    <el-input v-model='userForm.username'></el-input>
                </el-form-item>

                <el-form-item label='密码' prop='password'>
                    <el-input v-model='userForm.password' show-password></el-input>
                </el-form-item>

                <el-form-item label="验证码" prop="verify_code">
                    <el-row>
                        <el-col :span="13">
                            <el-input v-model="userForm.verify_code"></el-input>
                        </el-col>
                        <el-col :span="10" :offset="1">
                            <el-image id="verify_captcha" @click="updateVerifyCode" style="width: 100%; height: 48px"
                                src="/api/user/getCaptcha" fit="scale-down"></el-image>
                        </el-col>
                    </el-row>
                </el-form-item>

            </el-form>

            <el-row>
                <el-col :offset='16' :span='4'>
                    <el-button type='primary' @click='submitForm("userForm")' @keyup.enter='keyDown($event)'>登录
                    </el-button>
                </el-col>

                <el-col :span='4'>
                    <el-button type='danger' @click='registerBtn'>注册</el-button>
                </el-col>
            </el-row>
        </el-card>

        <!--粒子特效-->
        <vue-particles color='#dedede' :particleOpacity='0.7' :particlesNumber='80' shapeType='circle' :particleSize='4'
            linesColor='#dedede' :linesWidth='1' :lineLinked='true' :lineOpacity='0.4' :linesDistance='150' :moveSpeed='3'
            :hoverEffect='true' hoverMode='grab' :clickEffect='true' clickMode='push'>
        </vue-particles>
    </div>
</template>

<script>
import { _userLogin } from "@/api/api.js"

export default {
    name: 'LoginView',
    data() {
        return {
            isUsernamePasswordLogin: true,   // 默认使用用户名密码登录
            userForm: {
                username: '',
                password: '',
                verify_code: '',
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
            }
        };
    },
    directives: {
        enter: {
            bind(el, binding) {
                el.addEventListener('keydown', (event) => {
                    if (event.keyCode === 13) {
                        binding.value();
                    }
                });
            },
        },
    },
    methods: {
        //登录
        submitForm(formName) {
            if (formName === 'userForm') {
                this.$refs[formName].validate(valid => {
                    if (valid) {
                        _userLogin(this.userForm).then(res => {
                            // console.log(res);
                            if (res.data.status === 200) {
                                this.$store.commit('updateLoginUser', res.data.obj)
                                this.$router.push({
                                    //   接受路由参数然后跳转
                                    path: '/admin/dashboard' || "/"
                                });
                            } else {
                                this.$notify.error(res.data.msg)
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
            this.$router.push({
                //   接受路由参数然后跳转
                path: '/register' || "/"
            });
        },
        //更新验证码
        updateVerifyCode(e) {
            document.getElementById("verify_captcha").setAttribute("src", "/api/user/getCaptcha?time=" + new Date());
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

        // 绑定监听事件
        window.addEventListener("keydown", this.keyDown);
    },
    destroyed() {

        // 销毁事件
        window.addEventListener("keydown", this.keyDown);
    },
    created() {
        // window.addEventListener('keydown', this.handleKeyCode, false)
    }
}
</script>

<style scoped>
.login-view {
    width: 100%;
    height: 100vh;
    position: relative;
    background-image: url('@/assets/images/bg.jpg');
    background-size: cover;
    position: fixed;
}

.login-card {
    width: 480px;
    height: 400px;
    position: absolute;
    left: 60%;
    right: 0;
    top: 0;
    bottom: 0;
    margin: auto;
}

.login-card .logo-title {
    display: flex;
}
</style>
