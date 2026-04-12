<template>
    <div class="genealogy-edit">
        <!-- 装饰性顶部线条 -->
        <div class="decoration-line"></div>

        <div class="edit-container">
            <!-- 标题区域 -->
            <div class="edit-header">
                <h2 class="title">
                    <span class="title-chinese">编辑族人信息</span>
                </h2>
                <div class="title-decoration">
                    <span class="line"></span>
                    <i class="el-icon-user"></i>
                    <span class="line"></span>
                </div>
                <div class="member-name">{{ UserInfo.userName }}</div>
            </div>

            <div class="form-wrapper">
                <!-- 头像上传区 -->
                <div class="avatar-section">
                    <div class="avatar-frame">
                        <el-upload class="avatar-uploader" :action="uploadAvatarUrl" :data="uploadAvatarParams"
                            :show-file-list="false" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload"
                            accept="image/jpeg,image/jpg,image/png,image/gif,image/bmp,image/webp">
                            <img v-if="UserInfo.userImgUrl" :src="UserInfo.userImgUrl" class="avatar-image" width="160px"
                                height="160px">
                            <div v-else class="avatar-placeholder">
                                <i class="el-icon-camera"></i>
                                <span>上传头像</span>
                            </div>
                        </el-upload>
                    </div>
                    <div class="avatar-tip">点击上传族人肖像</div>
                </div>

                <!-- 表单区域 -->
                <el-form ref="UserInfo" :model="UserInfo" label-width="70px" class="genealogy-form">
                    <!-- 基本信息行1 -->
                    <div class="form-row">
                        <div class="form-item">
                            <span class="field-label">编码</span>
                            <el-input v-model="UserInfo.userId" disabled class="genealogy-input disabled-input"></el-input>
                        </div>

                        <div class="form-item">
                            <span class="field-label required">姓名</span>
                            <el-input v-model="UserInfo.userName" @blur="changeVail(UserInfo.userName, 'name')"
                                class="genealogy-input"></el-input>
                            <div class="error-message" v-if="msgName">{{ msgName }}</div>
                        </div>

                        <div class="form-item">
                            <span class="field-label">性别</span>
                            <el-select v-model="UserInfo.userSex" placeholder="请选择" class="genealogy-select">
                                <el-option v-for="item in ['男', '女']" :key="item" :label="item" :value="item">
                                </el-option>
                            </el-select>
                        </div>
                    </div>

                    <!-- 地址信息行 -->
                    <div class="form-row">
                        <div class="form-item address-item">
                            <span class="field-label">户籍地</span>
                            <address-cascader :value="UserInfo.userAddressOldCode"
                                :detail-value="UserInfo.userAddressOldDetail" @address-change="handleOldAddressChange"
                                @detail-change="(val) => UserInfo.userAddressOldDetail = val"
                                @update:detailValue="(val) => UserInfo.userAddressOldDetail = val">
                            </address-cascader>
                        </div>

                        <div class="form-item address-item">
                            <span class="field-label">现居地</span>
                            <address-cascader :value="UserInfo.userAddressNewCode"
                                :detail-value="UserInfo.userAddressNewDetail" @address-change="handleNewAddressChange"
                                @detail-change="(val) => UserInfo.userAddressNewDetail = val"
                                @update:detailValue="(val) => UserInfo.userAddressNewDetail = val">
                            </address-cascader>
                        </div>
                    </div>

                    <!-- 联系方式行 -->
                    <div class="form-row">
                        <div class="form-item">
                            <span class="field-label">电话</span>
                            <el-input v-model="UserInfo.userPhone" @blur="changeVail(UserInfo.userPhone, 'phone')"
                                class="genealogy-input"></el-input>
                            <div class="error-message" v-if="msgPhone">{{ msgPhone }}</div>
                        </div>

                        <div class="form-item">
                            <span class="field-label">邮箱</span>
                            <el-input v-model="UserInfo.userEmil" @blur="changeVail(UserInfo.userEmil, 'email')"
                                class="genealogy-input"></el-input>
                            <div class="error-message" v-if="msgEmail">{{ msgEmail }}</div>
                        </div>
                    </div>

                    <!-- 生辰信息行 -->
                    <div class="form-row">
                        <div class="form-item">
                            <span class="field-label required">生辰</span>
                            <el-date-picker v-model="UserInfo.userBirthDay"
                                @change="changeVail(UserInfo.userBirthDay, 'birthDay')" format="yyyy-MM-dd"
                                value-format="yyyy-MM-dd" :picker-options="pickerOptions" class="genealogy-datepicker"
                                type="date" placeholder="选择日期">
                            </el-date-picker>
                            <div class="error-message" v-if="msgBrithDay">{{ msgBrithDay }}</div>
                        </div>

                        <div class="form-item">
                            <span class="field-label">建在</span>
                            <el-select v-model="UserInfo.isSurvival" @change="handleSelectDie"
                                class="genealogy-select small-select">
                                <el-option v-for="item in ['是', '否']" :key="item" :label="item" :value="item">
                                </el-option>
                            </el-select>
                        </div>

                        <div class="form-item">
                            <span class="field-label">忌日</span>
                            <el-date-picker :disabled="isDie" v-model="UserInfo.userDieDay" format="yyyy-MM-dd"
                                value-format="yyyy-MM-dd" :picker-options="pickerOptions"
                                @change="changeVail(UserInfo.userDieDay, 'dieDay')" class="genealogy-datepicker" type="date"
                                placeholder="选择日期">
                            </el-date-picker>
                            <div class="error-message" v-if="msgDieDay">{{ msgDieDay }}</div>
                        </div>
                    </div>

                    <!-- 家族关系行 -->
                    <div class="form-row family-relations">
                        <div class="relation-item">
                            <span class="relation-label">父亲</span>
                            <div class="relation-value">{{ UserInfo.pName || '无' }}</div>
                        </div>
                        <div class="relation-item">
                            <span class="relation-label">母亲</span>
                            <div class="relation-value">{{ UserInfo.mName || '无' }}</div>
                        </div>
                        <div class="relation-item">
                            <span class="relation-label">配偶</span>
                            <div class="relation-value">
                                <span v-for="(item, index) in UserInfo.list" :key="index">
                                    {{ item.userName }}{{ index < UserInfo.list.length - 1 ? '、' : '' }} </span>
                                        <span v-if="!UserInfo.list || UserInfo.list.length === 0">无</span>
                            </div>
                        </div>
                    </div>

                    <!-- 个签区域 -->
                    <div class="form-row signature-row">
                        <div class="signature-label">
                            <i class="el-icon-edit-outline"></i>
                            <span>族谱记载/备注/个签</span>
                        </div>
                        <el-input type="textarea" :rows="4" v-model="UserInfo.userNote" class="signature-input"
                            placeholder="请输入族谱记载/备注/个签...">
                        </el-input>
                    </div>

                    <!-- 按钮区域 -->
                    <div class="form-actions">
                        <el-button class="cancel-btn" @click="callBtn">
                            <i class="el-icon-close"></i> 取消
                        </el-button>
                        <el-button class="submit-btn" @click="submitBlogForm">
                            <i class="el-icon-check"></i> 确认修改
                        </el-button>
                    </div>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script>
import { _uploadImgs, _getUserByNameLike, _saveSource, _delUserById } from "@/api/api.js";
import { parseTime } from '@/utils/calendar';
import AddressCascader from '@/components/AddressCascader.vue';
import { parseCodeToString, extractDetailAddress, combineFullAddress } from '@/utils/addressUtil';

export default {
    components: {
        AddressCascader
    },
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

                userAddressNewCode: '',
                userAddressNewDetail: '',
                userAddressNewProvince: '',
                userAddressNewCity: '',
                userAddressNewArea: '',

                userAddressOldCode: '',
                userAddressOldDetail: '',
                userAddressOldProvince: '',
                userAddressOldCity: '',
                userAddressOldArea: '',
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
        handleOldAddressChange(addressInfo) {
            this.UserInfo.userAddressOldCode = addressInfo.codeString;
            this.UserInfo.userAddressOldProvince = addressInfo.province;
            this.UserInfo.userAddressOldCity = addressInfo.city;
            this.UserInfo.userAddressOldArea = addressInfo.area;
        },
        handleNewAddressChange(addressInfo) {
            this.UserInfo.userAddressNewCode = addressInfo.codeString;
            this.UserInfo.userAddressNewProvince = addressInfo.province;
            this.UserInfo.userAddressNewCity = addressInfo.city;
            this.UserInfo.userAddressNewArea = addressInfo.area;
        },
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
                            if (new Date(val) >= new Date(this.UserInfo.userDieDay)) {
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
                        if (new Date(val) <= new Date(this.UserInfo.userBirthDay)) {
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
            // 组合完整地址
            this.UserInfo.userAddressOld = (this.UserInfo.userAddressOldProvince || '') +
                (this.UserInfo.userAddressOldCity || '') +
                (this.UserInfo.userAddressOldArea || '') +
                this.UserInfo.userAddressOldDetail;


            this.UserInfo.userAddressNew = (this.UserInfo.userAddressNewProvince || '') +
                (this.UserInfo.userAddressNewCity || '') +
                (this.UserInfo.userAddressNewArea || '') +
                this.UserInfo.userAddressNewDetail;


            if (this.isName && this.isPhone && this.isEmail && this.isDieDay && this.isBrithDay) {
                if (this.UserInfo.isSurvival == '是') {
                    this.UserInfo.isSurvival = '1'
                    this.UserInfo.userDieDay = ''
                } else {
                    this.UserInfo.isSurvival = '0'
                }
                _saveSource(this.UserInfo).then(res => {
                    if (res.data.status === 200) {
                        this.$notify({
                            title: '成功',
                            message: res.data.msg,
                            type: 'success',
                            customClass: 'genealogy-notification'
                        });
                        this.$router.push({
                            name: 'FamilyListView'
                        })
                    } else {
                        this.$notify.error({
                            title: '提示',
                            message: res.data.msg,
                            customClass: 'genealogy-notification'
                        });
                    }
                })
            }
        },
        callBtn() {
            this.$router.push({
                name: 'FamilyListView'
            })
        },
        beforeAvatarUpload(file) {
            const isImage = file.type.startsWith("image");
            const isLt10M = file.size / 1024 / 1024 < 10;

            // 允许的图片格式
            const allowedExts = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp'];
            const isAllowedExt = allowedExts.includes(fileExt);
            if (!isImage || !isAllowedExt) {
                this.$message.error('只能上传 JPG/JPEG/PNG/GIF/BMP/WEBP 格式的图片!');
                return false;
            }
            if (!isLt10M) {
                this.$message.error('上传头像图片大小不能超过 10MB!');
                return false;
            }
            return isImage && isLt10M;
        },
        handleAvatarSuccess(res, file) {
            if (res.status === 200) {
                this.$message.success("头像上传成功");
                this.UserInfo.userImgUrl = res.data;
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

        if (data.addressOldCode) {
            this.UserInfo.userAddressOldCode = data.addressOldCode;

            // 使用工具函数解析地址文本
            const addressInfo = parseCodeToString(data.addressOldCode);

            this.UserInfo.userAddressOldProvince = addressInfo.province;
            this.UserInfo.userAddressOldCity = addressInfo.city;
            this.UserInfo.userAddressOldArea = addressInfo.area;

            this.UserInfo.userAddressOldDetail = extractDetailAddress(data.addressOld, addressInfo.province, addressInfo.city, addressInfo.area) || '';
        }

        if (data.addressNewCode) {
            this.UserInfo.userAddressNewCode = data.addressNewCode;

            // 使用工具函数解析地址文本
            const addressInfo = parseCodeToString(data.addressNewCode);

            this.UserInfo.userAddressNewProvince = addressInfo.province;
            this.UserInfo.userAddressNewCity = addressInfo.city;
            this.UserInfo.userAddressNewArea = addressInfo.area;

            this.UserInfo.userAddressNewDetail = extractDetailAddress(data.addressNew, addressInfo.province, addressInfo.city, addressInfo.area) || '';
        }

    }
}
</script>

<style lang="less">
@gold: #b69d12;
@light-gold: #d4b88c;
@brown: #8b6b4d;
@light-brown: #a78e6f;
@paper: #fdf8e7;
@border-color: #dac5a5;
@shadow: rgba(150, 120, 80, 0.2);

.genealogy-edit {
    min-height: 100vh;
    background: linear-gradient(135deg, #f5efe0 0%, #f8f1e3 100%);
    padding: 30px 20px;
    position: relative;
    font-family: "Microsoft YaHei", "宋体", serif;

    // 装饰性线条
    .decoration-line {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 6px;
        background: linear-gradient(90deg, transparent 0%, @gold 20%, @gold 80%, transparent 100%);
    }

    .edit-container {
        max-width: 1100px;
        margin: 0 auto;
        background: @paper;
        border: 1px solid @border-color;
        border-radius: 12px;
        box-shadow: 0 10px 30px @shadow;
        position: relative;
        overflow: hidden;

        // 中式边框装饰
        &::before {
            content: '';
            position: absolute;
            top: 10px;
            left: 10px;
            right: 10px;
            bottom: 10px;
            border: 1px solid @border-color;
            border-radius: 8px;
            pointer-events: none;
        }

        .edit-header {
            text-align: center;
            padding: 30px 20px 20px;
            background: linear-gradient(to bottom, rgba(210, 180, 140, 0.1), transparent);
            border-bottom: 2px solid @border-color;
            position: relative;

            .title {
                margin: 0;

                .title-chinese {
                    font-size: 28px;
                    font-weight: normal;
                    color: @brown;
                    letter-spacing: 4px;
                    display: block;
                    font-family: "华文楷体", "KaiTi", serif;
                }
            }

            .title-decoration {
                display: flex;
                align-items: center;
                justify-content: center;
                margin: 15px 0 10px;

                .line {
                    width: 60px;
                    height: 1px;
                    background: linear-gradient(90deg, transparent, @gold, transparent);
                }

                i {
                    color: @gold;
                    font-size: 24px;
                    margin: 0 15px;
                }
            }

            .member-name {
                font-size: 24px;
                color: @brown;
                font-weight: bold;
                font-family: "华文楷体", "KaiTi", serif;
                position: relative;
                display: inline-block;
                padding: 0 30px;

                &::before,
                &::after {
                    content: '❖';
                    position: absolute;
                    top: 50%;
                    transform: translateY(-50%);
                    color: @gold;
                    font-size: 16px;
                }

                &::before {
                    left: 0;
                }

                &::after {
                    right: 0;
                }
            }
        }

        .form-wrapper {
            padding: 30px;

            .avatar-section {
                text-align: center;
                margin-bottom: 30px;

                .avatar-frame {
                    display: inline-block;
                    padding: 8px;
                    background: linear-gradient(145deg, @border-color, @light-gold);
                    border-radius: 50%;
                    box-shadow: 0 5px 15px @shadow;

                    .avatar-uploader {
                        cursor: pointer;

                        .avatar-image {
                            width: 160px;
                            height: 160px;
                            border-radius: 50%;
                            object-fit: cover;
                            border: 3px solid @paper;
                        }

                        .avatar-placeholder {
                            width: 160px;
                            height: 160px;
                            border-radius: 50%;
                            background: #f0e8d5;
                            display: flex;
                            flex-direction: column;
                            align-items: center;
                            justify-content: center;
                            border: 3px solid @paper;

                            i {
                                font-size: 40px;
                                color: @light-gold;
                                margin-bottom: 5px;
                            }

                            span {
                                color: @brown;
                                font-size: 14px;
                            }
                        }
                    }
                }

                .avatar-tip {
                    margin-top: 10px;
                    color: @light-gold;
                    font-size: 12px;
                    font-style: italic;
                }
            }

            .genealogy-form {
                .form-row {
                    display: flex;
                    flex-wrap: wrap;
                    margin-bottom: 20px;
                    padding: 15px;
                    background: rgba(210, 180, 140, 0.05);
                    border-radius: 8px;
                    border: 1px solid @border-color;
                    position: relative;

                    &:hover {
                        background: rgba(210, 180, 140, 0.1);
                        border-color: @gold;
                    }

                    .form-item {
                        flex: 1;
                        min-width: 200px;
                        margin-right: 20px;
                        position: relative;

                        &:last-child {
                            margin-right: 0;
                        }

                        .field-label {
                            display: inline-block;
                            width: 60px;
                            color: @brown;
                            font-size: 14px;
                            font-weight: bold;

                            &.required::before {
                                content: '*';
                                color: #e6a23c;
                                margin-right: 4px;
                            }
                        }

                        .error-message {
                            color: #e6a23c;
                            font-size: 12px;
                            margin-top: 4px;
                            padding-left: 60px;
                        }
                    }

                    .address-item {
                        flex: 1.5;
                    }

                    &.family-relations {
                        background: rgba(182, 157, 18, 0.1);

                        .relation-item {
                            flex: 1;
                            text-align: center;
                            padding: 10px;

                            .relation-label {
                                display: block;
                                color: @gold;
                                font-size: 14px;
                                margin-bottom: 5px;
                                font-weight: bold;
                            }

                            .relation-value {
                                color: @brown;
                                font-size: 16px;
                                font-family: "华文楷体", "KaiTi", serif;
                                padding: 5px 10px;
                                background: @paper;
                                border-radius: 20px;
                                display: inline-block;
                                min-width: 100px;
                            }
                        }
                    }

                    &.signature-row {
                        flex-direction: column;

                        .signature-label {
                            margin-bottom: 10px;
                            color: @brown;

                            i {
                                color: @gold;
                                margin-right: 5px;
                            }
                        }
                    }
                }

                .form-actions {
                    display: flex;
                    justify-content: center;
                    padding: 20px 0 10px;

                    .cancel-btn,
                    .submit-btn {
                        min-width: 140px;
                        height: 44px;
                        border-radius: 22px;
                        font-size: 16px;
                        letter-spacing: 2px;
                        margin: 0 15px;
                        border: none;
                        transition: all 0.3s ease;

                        i {
                            margin-right: 5px;
                        }
                    }

                    .cancel-btn {
                        background: #e4e4e4;
                        color: #666;

                        &:hover {
                            background: #d0d0d0;
                            transform: translateY(-2px);
                            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
                        }
                    }

                    .submit-btn {
                        background: linear-gradient(135deg, @gold, @brown);
                        color: @paper;

                        &:hover {
                            transform: translateY(-2px);
                            box-shadow: 0 5px 20px rgba(182, 157, 18, 0.4);
                        }
                    }
                }
            }
        }
    }
}

// Element UI 组件样式覆盖
.genealogy-input,
.genealogy-select,
.genealogy-datepicker {
    &.el-input {
        width: calc(100% - 70px);

        .el-input__inner {
            background: @paper;
            border: 1px solid @border-color;
            border-radius: 20px;
            height: 38px;
            line-height: 38px;
            color: @brown;

            &:focus {
                border-color: @gold;
                box-shadow: 0 0 0 2px rgba(182, 157, 18, 0.1);
            }
        }

        &.disabled-input .el-input__inner {
            background: #f0e8d5;
            color: #a78e6f;
        }
    }
}

.genealogy-select {
    &.el-select {
        width: calc(100% - 70px);

        .el-input__inner {
            background: @paper;
        }
    }

    &.small-select {
        width: 80px;
    }
}

.genealogy-datepicker {
    &.el-date-editor {
        width: calc(100% - 70px);

        .el-input__inner {
            background: @paper;
        }
    }
}

// 地址级联选择器样式
.address-cascader {
    display: inline-block;
    width: calc(100% - 70px);

    .el-cascader {
        width: 100%;

        .el-input__inner {
            background: @paper;
            border: 1px solid @border-color;
            border-radius: 20px;
            height: 38px;
            line-height: 38px;
            color: @brown;

            &:focus {
                border-color: @gold;
                box-shadow: 0 0 0 2px rgba(182, 157, 18, 0.1);
            }
        }
    }
}

// 通知样式
.genealogy-notification {
    background: @paper !important;
    border: 1px solid @gold !important;
    border-radius: 8px !important;

    .el-notification__title {
        color: @brown !important;
    }

    .el-notification__content {
        color: @light-gold !important;
    }
}
</style>