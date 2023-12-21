<template>
    <div class="editUser-view">
        <div class="header">{{ this.UserInfo.userName }}的信息</div>
        <div class="form-wrapper">
            <div class="upload-div">
                <el-upload class="avatar-uploader" :action="uploadAvatarUrl" :data="uploadAvatarParams"
                    :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
                    <img v-if="UserInfo.userImgUrl" :src="UserInfo.userImgUrl" class="avatar my-border" width="120px"
                        height="120px">
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                </el-upload>
            </div>
            <div class="upload-div">
                <el-form ref="UserInfo" status-icon label-width="60px" :model="UserInfo">
                    <el-form-item>
                        <label class="label-cls">编码</label>
                        <el-input v-model="UserInfo.userId" disabled style="width: 110px;"></el-input>
                        <label class="label-cls">姓名</label>
                        <el-input v-model="UserInfo.userName" @blur="changeVail(UserInfo.userName, 'name')"
                            style="width: 100px;"></el-input>
                        <div class="label-cls-name__error">{{ msgName }}</div>
                        <label class="label-cls">性别</label>
                        <el-select v-model="UserInfo.userSex" placeholder="请选择" style="width: 60px;">
                            <el-option v-for="item in ['男', '女']" :key="item" :label="item" :value="item">
                            </el-option>
                        </el-select>
                        <label class="label-cls">户籍地</label>
                        <el-input v-model="UserInfo.userAddressOld" style="width: 200px;"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <label class="label-cls">电话</label>
                        <el-input v-model="UserInfo.userPhone" @blur="changeVail(UserInfo.userPhone, 'phone')"
                            style="width: 120px;"></el-input>
                        <div class="label-cls-phone__error">{{ msgPhone }}</div>
                        <label class="label-cls">邮箱</label>
                        <el-input v-model="UserInfo.userEmil" @blur="changeVail(UserInfo.userEmil, 'email')"
                            style="width: 196px;"></el-input>
                        <div class="label-cls-email__error">{{ msgEmail }}</div>
                        <label class="label-cls">现居地</label>
                        <el-input v-model="UserInfo.userAddressNew" style="width: 200px;"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <label class="label-cls">生辰</label>
                        <el-date-picker v-model="UserInfo.userBirthDay"
                            @change="changeVail(UserInfo.userBirthDay, 'birthDay')" format="yyyy-MM-dd"
                            :picker-options="pickerOptions" style="width: 150px;" type="date" placeholder="选择开始时间">
                        </el-date-picker>
                        <div class="label-cls-brithDay__error">{{ msgBrithDay }}</div>
                        <label class="label-cls">建在</label>
                        <el-select v-model="UserInfo.isSurvival" @change="handleSelectDie" placeholder="请选择"
                            style="width: 60px;">
                            <el-option v-for="item in ['是', '否']" :key="item" :label="item" :value="item">
                            </el-option>
                        </el-select>
                        <label class="label-cls">忌日</label>
                        <el-date-picker :disabled="this.isDie" v-model="UserInfo.userDieDay" format="yyyy-MM-dd"
                            :picker-options="pickerOptions" @change="changeVail(UserInfo.userDieDay, 'dieDay')"
                            style="width: 150px;" type="date" placeholder="选择开始时间">
                        </el-date-picker>
                        <div class="label-cls-dieDay__error">{{ msgDieDay }}</div>
                    </el-form-item>
                    <el-form-item>
                        <label class="label-cls">父亲</label>
                        <el-input disabled v-model="UserInfo.pName" style="width: 100px;"></el-input>
                        <label class="label-cls">母亲</label>
                        <el-input disabled v-model="UserInfo.mName" style="width: 100px;"></el-input>
                        <label class="label-cls">配偶</label>
                        <el-input disabled v-for="item in UserInfo.list" :value="item.userName"
                            style="width: 100px;"></el-input>
                    </el-form-item>

                    <el-form-item>
                        <label class="label-cls">个签</label>
                        <el-input type="textarea" style="width: 400px;" :rows="4" v-model="UserInfo.userNote">
                        </el-input>
                    </el-form-item>

                    <el-form-item class="my-marginTop10">
                        <el-button type="info" style="float: right;margin-left: 8px;" @click="callBtn">取消</el-button>
                        <el-button type="primary" style="float: right;" @click="submitBlogForm">提交修改</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>
  
<script>
import { _uploadImgs, _getUserByNameLike, _saveSource, _delUserById } from "@/api/api.js";
import { parseTime } from '@/utils/calendar';
export default {
    name: "EditUser",
    data() {

        return {
            isName: true,
            msgName: '',
            isDie: true,
            isPhone: true,
            msgPhone: '',
            isEmail: true,
            msgEmail: '',
            isBrithDay: true,
            msgBrithDay: '',
            isDieDay: true,
            msgDieDay: '',
            uploadAvatarParams: {
                "id": "",
                "type": 'sourceAvatar',
            },
            UserInfo: {
                userId: '', //id
                userName: '',   //姓名
                isSurvival: '是', //是否存活
                mName: '',  //母亲姓名
                pName: '',  //父亲姓名
                mateName: '',    //配偶名称
                userAddressNew: '', //现地址
                userAddressOld: '', //户口地址
                userBirthDay: '',   //生日
                userDieDay: '', //死亡日期
                userEmil: '',   //邮箱
                userImgUrl: '', //头像
                userLove: '',   //代数
                userMID: '',    //母亲ID
                userMateId: '', //配偶ID
                userNote: '',   //备注
                userPId: '',    //父亲ID
                userPhone: '',  //电话
                userPinYin: '', //拼音
                userSex: '', //性别
                age: '', //年龄
                dieDayNum: '',   //亡故日期
                type: '',    //操作
                list: [],
            },
            pickerOptions: {
                disabledDate(time) {
                    return time.getTime() > Date.now();
                },
            },
        }
    },
    filters: {
        fortTime(val) {
            return formatDate(val);
        },
    },
    methods: {
        changeVail(val, type) {
            const mobilereg = /^[1][3,4,5,6,7,8,9][0-9]{9}$/ // 手机
            const reg = /^([a-zA-Z]|[0-9])(\w|\-|\.)+@[a-zA-Z0-9]+\.([a-zA-Z]{2,4})$/ //邮箱
            switch (type) {
                case 'phone':
                    if (val && !mobilereg.test(val)) {
                        this.msgPhone = '请填写正确的手机号码'
                        this.isPhone = false
                    } else {
                        this.msgPhone = ''
                        this.isPhone = true
                    }
                    break;
                case 'email':
                    if (val && !reg.test(val)) {
                        this.msgEmail = '请输入正确的邮箱'
                        this.isEmail = false
                    } else {
                        this.msgEmail = ''
                        this.isEmail = true
                    }
                    break;
                case 'name':
                    if (!val) {
                        this.msgName = '请输入姓名'
                        this.isName = false
                    } else {
                        this.msgName = ''
                        this.isName = true
                    }
                    break;
                case 'birthDay':
                    if (!val) {
                        this.msgBrithDay = '请选择生辰日期'
                        this.isBrithDay = false
                    } else {
                        if (val && this.UserInfo.userDieDay && this.UserInfo.isSurvival === '否') {
                            if (val.getTime() >= this.UserInfo.userDieDay.getTime()) {
                                this.msgBrithDay = '生辰不能大于忌日日期'
                                this.isBrithDay = false
                            } else {
                                this.msgBrithDay = ''
                                this.isBrithDay = true
                            }

                        } else {
                            this.msgBrithDay = ''
                            this.isBrithDay = true
                        }

                    }
                    break;
                case 'dieDay':
                    if (val && this.UserInfo.userBirthDay && this.UserInfo.isSurvival === '否') {
                        const a = new Date(this.UserInfo.userBirthDay)
                        if (val.getTime() <= a.getTime()) {
                            this.msgDieDay = '忌日不能小于生辰日期'
                            this.isDieDay = false
                        } else {
                            this.msgDieDay = ''
                            this.isDieDay = true
                        }

                    }
                    break;
            }
        },
        submitBlogForm() {

            if (this.isName && this.isPhone && this.isEmail && this.isDieDay && this.isBrithDay) {
                this.UserInfo.userBirthDay = parseTime(this.UserInfo.userBirthDay)
                if (this.UserInfo.isSurvival == '是') {
                    this.UserInfo.isSurvival = '1'
                    this.UserInfo.userDieDay = ''
                } else {
                    this.UserInfo.isSurvival = '0'
                    this.UserInfo.userDieDay = parseTime(this.UserInfo.userDieDay)
                }
                _saveSource(this.UserInfo).then(res => {
                    if (res.data.status === 200) {
                        this.$notify.success(res.data.msg);
                        this.$router.push({
                            name: 'FamilyListView'
                        })
                    }else{
                        this.$notify.success(res.data.msg);
                    }
                })
            }
        },
        callBtn() {
            this.$router.push({
                name: 'FamilyListView'
            })
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
                this.UserInfo.userImgUrl = res.obj;
            }
        },
        handleSelectDie(val) {
            if (val === '是') {
                this.isDie = true
            } else {
                this.isDie = false
            }
        }
    },
    computed: {
        uploadAvatarUrl() {
            return '/api/file/upload/avatar';
        },
    },
    created() {
        let data = JSON.parse(this.$route.params.jsonParam)
        this.uploadAvatarParams.id = data.key;
        this.UserInfo.userId = data.key //id
        this.UserInfo.userName = data.name   //姓名
        if (data.isDie === '1') {
            this.UserInfo.isSurvival = '是'
            this.isDie = true
        } else {
            this.UserInfo.isSurvival = '否'
            this.isDie = false
        }
        this.UserInfo.mName = data.mName //母亲姓名
        this.UserInfo.pName = data.pName //父亲姓名
        this.UserInfo.userAddressNew = data.addressNew //现地址
        this.UserInfo.userAddressOld = data.addressOld //户口地址
        this.UserInfo.userBirthDay = data.birthDay   //生日
        this.UserInfo.userDieDay = data.dieDay //死亡日期
        this.UserInfo.userEmil = data.emil  //邮箱
        this.UserInfo.userImgUrl = data.img //头像
        this.UserInfo.userLove = data.love   //代数
        this.UserInfo.userMID = data.mId    //母亲ID
        this.UserInfo.userNote = data.note  //备注
        this.UserInfo.userPId = data.pId    //父亲ID
        this.UserInfo.userPhone = data.phone  //电话
        this.UserInfo.userPinYin = data.pinyin //拼音
        this.UserInfo.userSex = data.sex //性别
        this.UserInfo.age = '' //年龄
        this.UserInfo.dieDayNum = ''   //亡故日期
        this.UserInfo.type = ''    //操作
        this.UserInfo.list = data.mate
    }
}
</script>
  
<style lang="less">
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

        .upload-div {
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;

        }

        .label-cls {
            padding: 5px;
            margin-left: 10px;
            line-height: 40px;
            position: relative;
            font-size: 14px;


        }

        .label-cls-phone__error {
            color: #F56C6C;
            font-size: 12px;
            line-height: 1;
            padding-top: 4px;
            position: absolute;
            top: 100%;
            left: 50px;
        }

        .label-cls-brithDay__error {
            color: #F56C6C;
            font-size: 12px;
            line-height: 1;
            padding-top: 4px;
            position: absolute;
            top: 100%;
            left: 50px;
        }

        .label-cls-dieDay__error {
            color: #F56C6C;
            font-size: 12px;
            line-height: 1;
            padding-top: 4px;
            position: absolute;
            top: 100%;
            left: 250px;
        }

        .label-cls-email__error {
            color: #F56C6C;
            font-size: 12px;
            line-height: 1;
            padding-top: 4px;
            position: absolute;
            top: 100%;
            left: 220px;
        }

        .label-cls-name__error {
            color: #F56C6C;
            font-size: 12px;
            line-height: 1;
            padding-top: 4px;
            position: absolute;
            top: 100%;
            left: 220px;
        }

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
  