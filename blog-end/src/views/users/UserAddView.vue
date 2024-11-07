<template>
    <div class="user-info-view">
        <el-steps :active="active" finish-status="success">
            <el-step title="基本信息"></el-step>
            <el-step title="详细信息"></el-step>
            <el-step title="展示用户信息"></el-step>
        </el-steps>

        <div class="step-form">
            <div v-show="active === 0" class="step1">
                <el-form ref="userForm" status-icon label-width="100px" :rules='rules' label-position="left"
                    :model="userForm">
                    <el-form-item label="账户" prop="username">
                        <el-input v-model="userForm.username" placeholder="请输入用户名"></el-input>
                    </el-form-item>
                    <el-form-item label="身份证号" prop="ext02">
                        <el-input v-model="userForm.ext02" placeholder="请输入身份证号"></el-input>
                    </el-form-item>
                    <el-form-item label="密码" prop="password">
                        <el-input placeholder="请输入密码" show-password v-model="userForm.password"></el-input>
                    </el-form-item>
                </el-form>
            </div>

            <div v-show="active === 1" class="step2">
                <el-form ref="userForm1" status-icon label-width="100px" :rules="rules" label-position="left"
                    :model="userForm">
                    <el-form-item label="昵称">
                        <el-input placeholder="请输入昵称" v-model="userForm.nickname"></el-input>
                    </el-form-item>
                    <el-form-item label="电话" prop="phone">
                        <el-input placeholder="请输入电话" v-model="userForm.phone"></el-input>
                    </el-form-item>
                    <el-form-item label="邮箱" prop="email">
                        <el-input placeholder="请输入邮箱" v-model="userForm.email"></el-input>
                    </el-form-item>
                    <el-form-item label="地址">
                        <el-input placeholder="请输入地址" v-model="userForm.address"></el-input>
                    </el-form-item>

                    <el-form-item label="用户角色">
                        <el-select v-model="userForm.role" placeholder="请选择用户角色">
                            <el-option v-for="(item, index) in rolesList" :key="index" :label="item.nameZh"
                                :value="item.id">
                                <span style="float: left">{{ item.nameZh }}</span>
                                <span style="float: right; color: #8492a6; font-size: 13px">{{ item.name }}</span>
                            </el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="职业">
                        <el-input v-model="userForm.ext01"></el-input>
                    </el-form-item>

                    <el-form-item label="头像">
                        <el-upload class="avatar-uploader" action="/api/file/upload" :show-file-list="false"
                            :on-success="handleUploadSuccess" :before-upload="beforeUpload">
                            <img v-if="this.userForm.avatar" :src="this.userForm.avatar" class="avatar">
                            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                        </el-upload>
                    </el-form-item>
                </el-form>
            </div>

            <div v-if="active === 2" class="active1">
                <el-card class="box-card">
                    <div class="user-card">
                        <div class="avatar">
                            <el-image style="width: 100%; height: 100%" :src="this.userForm.avatar" fit="cover"></el-image>
                        </div>
                        <div class="info">
                            <p class="user-title"><b>账户：</b>{{ this.userForm.username }}</p>
                            <p class="user-title"><b>身份证号：</b>{{ this.userForm.ext02 }}</p>
                            <p class="user-title"><b>昵称：</b>{{ this.userForm.nickname }}</p>
                            <p class="user-title"><b>职业：</b>{{ this.userForm.ext01 }}</p>
                            <p class="user-title"><b>邮箱：</b>{{ this.userForm.email }}</p>
                            <p class="user-title"><b>电话：</b>{{ this.userForm.phone }}</p>
                            <p class="user-title"><b>地址：</b>{{ this.userForm.address }}</p>
                            <p class="user-title"><b>角色：</b>{{ this.userForm.note }}</p>
                        </div>
                    </div>
                </el-card>
            </div>

            <el-button v-if="active < 2 && active > 0" style="margin-top: 12px" @click="pre">上一步</el-button>
            <el-button v-if="active < 2" style="margin-top: 12px" @click="next">下一步</el-button>
            <a href="/home/user/list" v-if="active === 2">
                <el-button type="success" style="margin-top: 12px">完成</el-button>
            </a>
        </div>
    </div>
</template>

<script>
import { _addUser, _getRoleAll, _findUser } from "@/api/api.js"

export default {
    name: "UserAddView",
    data() {
        var phone = (rule, value, callback) => {
            const reg = /^((0\d{2,3}-\d{7,8})|(1[34578]\d{9}))$/;//电话或者固话规则
            if ((!reg.test(value)) && value != '') {
                callback(new Error('请输入正确的电话号码或者固话号码'));
            } else {
                callback();
            }
        };
        var email = (rule, value, callback) => {
            const reg = /^([a-zA-Z0-9]+[-_\.]?)+@[a-zA-Z0-9]+\.[a-z]+$/;
            if (value == '' || value == undefined || value == null) {
                callback();
            } else {
                if (!reg.test(value)) {
                    callback(new Error('请输入正确的邮箱'));
                } else {
                    callback();
                }
            }
        };
        var ext02 = (rule, value, callback) => {
            const reg = /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/;
            if (value == '' || value == undefined || value == null) {
                callback(new Error('请输入正确的二代身份证号'));
            } else {
                if (!reg.test(value)) {
                    callback(new Error('请输入正确的二代身份证号'));
                } else {
                    callback();
                }
            }
        };
        return {
            dialogAvatarUrl: '',
            dialogAvatarVisible: false,
            rolesList: [],			//所有的角色列表
            active: 0,
            rules: {
                username: [
                    { required: true, message: '请输入用户名', trigger: 'blur' },
                    { min: 2, max: 12, message: '长度在 2 到 12 个字符', trigger: 'blur' }
                ],
                password: [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 3, max: 12, message: '长度在 3 到 12 个字符', trigger: 'blur' }
                ],
                phone: [
                    { validator: phone, trigger: 'blur' }
                ],
                email: [
                    { validator: email, trigger: 'blur' }
                ],
                ext02: [
                    { validator: ext02, required: true, message: '请输入身份证号',  trigger: 'blur' }
                ]
            },
            //用户信息
            userForm: {
                username: "",
                password: "",
                nickname: "",
                phone: "",
                address: "",
                avatar: "",
                email: "",
                role: "4",
                description: "",
                note: "",
                ext01:"",
                ext02:"",
            },
        }
    },
    methods: {
        //获取所有角色
        init() {
            _getRoleAll().then(res => {
                if (res.data.status === 200) {
                    this.rolesList.push(...res.data.obj);
                }
            })
        },

        /*用户头像上传*/
        beforeUpload(file) {
            const isJPG = file.type.startsWith("image");
            const isLt50M = file.size / 1024 / 1024 < 50;

            if (!isJPG) {
                this.$message.error('上传头像图片只能是Image!');
            }
            if (!isLt50M) {
                this.$message.error('上传头像图片大小不能超过 50MB!');
            }
            return isJPG && isLt50M;
        },
        handleUploadSuccess(res, file) {
            if (res.status === 200) {
                this.userForm.avatar = res.obj;
            }
        },

        //步骤条下一步的方法
        next() {
            if (this.active === 0) {
                this.$refs.userForm.validate((valid) => {
                    let self = this;
                    if (valid) {
                        _findUser(this.userForm).then(res => {
                            if (res.data.status == 200) {
                                this.$notify.error('error 用户已存在!!');
                                return false;
                            } else {
                                const loading = this.$loading({
                                    lock: true,
                                    text: 'Loading',
                                    spinner: 'el-icon-loading',
                                    background: 'rgba(0, 0, 0, 0.7)'
                                });
                                setTimeout(() => {
                                    self.active++;
                                    loading.close();
                                }, 1000)
                            }
                        })
                    } else {
                        this.$notify.error('error submit!!');
                        return false;
                    }
                });
            }
            if (this.active === 1) {
                this.$refs.userForm1.validate((valid) => {
                    let self = this;
                    if (valid) {
                        // self.active++;
                        this.userForm.note = this.rolesList[parseInt(this.userForm.role)-1].nameZh
                        _addUser(this.userForm).then(res => {
                            if (res.data.status === 200) {
                                this.$notify.success(res.data.msg);
                                setTimeout(() => {
                                    self.active++;
                                }, 1000)
                            } else {
                                this.$notify.error('error submit!!');
                                return false;
                            }
                        })
                    }
                });
            }
            if (this.active === 2) {
                this.$router.replace("/home/user/list")
            }
        },
        //步骤条上一步的方法
        pre() {
            this.active--;

        },
    },
    created() {
        this.init();
    },
    filters: {
        filterRoles(val) {
            return val.nameZh + "-" + item.name;
        }
    }
}
</script>

<style scoped>
.step-form {
    width: 40vw;
    padding: 20px;
    margin: 20px auto;
    box-shadow: 0 0 2px #ddd;
}

.user-card {
    display: flex;
    flex-wrap: nowrap;
}

.user-card .avatar {
    width: 300px;
    height: 260px;
}

.user-card .info {
    flex: 1;
    margin-left: 10px;
    font-size: 15px;
    color: #8492a6;
    padding: 10px;
    border-left: 1px solid #dddddd;
}

/*头像上传*/
.avatar-uploader .el-upload {
    border: 1px solid #dbdada;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
}

.avatar-uploader .el-upload i {
    border: 1px solid #dbdada;
}

.avatar-uploader .el-upload i:hover {
    border-color: #409EFF;
}

.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
}

.avatar {
    width: 178px;
    height: 178px;
    display: block;
}
</style>
