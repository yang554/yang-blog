<template>
    <div class="editUser-view">
        <div class="header">编辑用户信息</div>
        <div class="form-wrapper">
            <el-form ref="editForm" status-icon label-width="80px" :rules='rules' label-position="left" :model="editForm">
                <el-form-item label="头像">
                    <el-upload class="avatar-uploader" :action="uploadAvatarUrl" :data="uploadAvatarParams"
                        :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
                        <img v-if="editForm.avatar" :src="editForm.avatar" class="avatar my-border" width="120px"
                            height="120px">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-form-item>

                <el-form-item label="编码">
                    <el-input v-model="editForm.id" disabled></el-input>
                </el-form-item>

                <el-form-item label="账户">
                    <el-input v-model="editForm.username"></el-input>
                </el-form-item>

                <el-form-item label="昵称">
                    <el-input v-model="editForm.nickname"></el-input>
                </el-form-item>

                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="editForm.email"></el-input>
                </el-form-item>

                <el-form-item label="电话" prop="phone">
                    <el-input v-model="editForm.phone"></el-input>
                </el-form-item>

                <el-form-item label="描述">
                    <el-input type="textarea" :rows="4" v-model="editForm.description">
                    </el-input>
                </el-form-item>

                <el-form-item class="my-marginTop10">
                    <i style="margin-right: 80px;" class="iconfont icon-31shijian">创建时间：{{ this.editForm.createTime |
                        fortTime(this.editForm.createTime) }}</i>
                    <i style="margin-right: 80px;" class="iconfont icon-31shijian">上一次编辑时间：{{ this.editForm.updateTime |
                        fortTime(this.editForm.updateTime) }}</i>
                    <el-button type="primary" style="float: right;" @click="submitBlogForm">提交修改</el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>
  
<script>
import { _uploadImgs, _getUserByNameLike, _editUserById, _delUserById } from "@/api/api.js";
import { formatDate } from '@/utils/formatDate';
export default {
    name: "EditUser",
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
        return {
            uploadAvatarParams: {
                "id": "",
                "type":'userAvatar',
            },
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
                ]
            },
            editForm: {
                id: "1",
                username: "",
                password: "",
                nickname: "",
                avatar: "",
                email: "",
                description: "",
                createTime: "",
                updateTime: "",
                phone: ""
            },

        }
    },
    filters: {
        fortTime(val) {
            return formatDate(val);
        },
    },
    methods: {
        submitBlogForm() {
            this.$refs.editForm.validate((valid) => {
                if (valid) {
                    _editUserById(this.editForm).then(res => {
                        if (res.data.status === 200) {
                            this.$notify.success(res.data.msg);
                            this.$router.push("/home/user/list")
                        } else {
                            this.$notify.error(res.data.msg);
                        }
                    })
                } else {
                    this.$notify.error('error submit!!');
                    return false;
                }
            });
        },

        /*上传头像*/
        beforeAvatarUpload(file) {
            const isJPG = file.type.startsWith("image");
            const isLt10M = file.size / 1024 / 1024 < 10;
            if (!isJPG) {
                this.$message.error('上传头像图片只能是 JPG 格式!');
            }
            if (!isLt10M) {
                this.$message.error('上传头像图片大小不能超过 10MB!');
            }
            return isJPG && isLt10M;
        },
        handleAvatarSuccess(res, file) {
            if (res.status === 200) {
                this.$message.success("头像上传成功");
                this.editForm.avatar = res.obj;
            }
        },
    },
    computed: {
        uploadAvatarUrl() {
            return '/api/file/upload/avatar';
        },
    },
    created() {
        _getUserByNameLike(this.$route.query.username).then(res => {
            console.log(res)
            if (res.data.status === 200) {
                this.editForm.id = res.data.obj.id,
                this.editForm.username = res.data.obj.username,
                // this.editForm.password = res.data.obj[0].,
                this.editForm.nickname = res.data.obj.nickname,
                this.editForm.avatar = res.data.obj.avatar,
                this.editForm.email = res.data.obj.email,
                this.editForm.description = res.data.obj.description,
                this.editForm.createTime = res.data.obj.createTime,
                this.editForm.updateTime = res.data.obj.updateTime,
                this.editForm.phone = res.data.obj.phone
            }
        })
        this.uploadAvatarParams.id = this.$route.query.id;
    }
}
</script>
  
<style lang="less" scoped>
.editUser-view {
    width: 1000px;
    background-color: #fff;

    .header {
        line-height: 60px;
        font-size: 26px;
        padding: 0 30px;
        border-bottom: 1px solid #ddd;
    }

    .form-wrapper {
        padding: 20px 30px;

        .avatar-uploader .el-upload {
            border: 1px dashed #d9d9d9;
            border-radius: 6px;
            cursor: pointer;
            position: relative;
            overflow: hidden;
        }

        .avatar-uploader .el-upload:hover {
            border-color: #409EFF;
        }

        .avatar-uploader-icon {
            font-size: 28px;
            border: 1px dashed #d9d9d9;
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
    }
}
</style>
  