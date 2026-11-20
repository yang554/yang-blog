<template>
    <div class="user-add-container">
      <!-- <div class="header">
        <h1><i class="el-icon-user-solid"></i> 添加新用户</h1>
        <p class="subtitle">创建新的系统用户账户</p>
      </div> -->
  
      <div class="step-container">
        <el-steps :active="active" finish-status="success" align-center>
          <el-step title="基本信息" icon="el-icon-document">
            <template #description>
              <span class="step-desc">账户基础信息</span>
            </template>
          </el-step>
          <el-step title="详细信息" icon="el-icon-edit">
            <template #description>
              <span class="step-desc">完善用户资料</span>
            </template>
          </el-step>
          <el-step title="信息确认" icon="el-icon-view">
            <template #description>
              <span class="step-desc">确认用户信息</span>
            </template>
          </el-step>
        </el-steps>
      </div>
  
      <div class="form-container">
        <!-- 第一步: 基本信息 -->
        <div v-show="active === 0" class="step step1 animated fadeIn">
          <div class="step-header">
            <i class="el-icon-document"></i>
            <h2>账户基本信息</h2>
            <p>请填写用户账户基础信息</p>
          </div>
          
          <el-form ref="userForm" status-icon label-width="120px" :rules='rules' label-position="top" :model="userForm">
            <div class="form-grid">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="userForm.username" placeholder="请输入用户名" size="medium">
                  <template #prefix>
                    <i class="el-icon-user"></i>
                  </template>
                </el-input>
                <div class="form-tip">长度在2-12个字符之间</div>
              </el-form-item>
              
              <el-form-item label="密码" prop="password">
                <el-input placeholder="请输入密码" show-password v-model="userForm.password" size="medium">
                  <template #prefix>
                    <i class="el-icon-lock"></i>
                  </template>
                </el-input>
                <div class="form-tip">长度在3-12个字符之间</div>
              </el-form-item>
            </div>
            
            <el-form-item label="身份证号" prop="ext02">
              <el-input v-model="userForm.ext02" placeholder="请输入身份证号" size="medium">
                <template #prefix>
                  <i class="el-icon-credit-card"></i>
                </template>
              </el-input>
            </el-form-item>
          </el-form>
        </div>
  
        <!-- 第二步: 详细信息 -->
        <div v-show="active === 1" class="step step2 animated fadeIn">
          <div class="step-header">
            <i class="el-icon-edit"></i>
            <h2>用户详细信息</h2>
            <p>请完善用户详细资料</p>
          </div>
          
          <el-form ref="userForm1" status-icon label-width="120px" :rules="rules" label-position="top" :model="userForm">
            <div class="form-grid">
              <el-form-item label="姓名">
                <el-input placeholder="请输入姓名" v-model="userForm.nickname" size="medium">
                  <template #prefix>
                    <i class="el-icon-user"></i>
                  </template>
                </el-input>
              </el-form-item>
              
              <el-form-item label="电话" prop="phone">
                <el-input placeholder="请输入电话" v-model="userForm.phone" size="medium">
                  <template #prefix>
                    <i class="el-icon-phone"></i>
                  </template>
                </el-input>
                <div class="form-tip">支持手机号或固话格式</div>
              </el-form-item>
            </div>
            
            <div class="form-grid">
              <el-form-item label="邮箱" prop="email">
                <el-input placeholder="请输入邮箱" v-model="userForm.email" size="medium">
                  <template #prefix>
                    <i class="el-icon-message"></i>
                  </template>
                </el-input>
              </el-form-item>
              
              <el-form-item label="地址">
                <el-input placeholder="请输入地址" v-model="userForm.address" size="medium">
                  <template #prefix>
                    <i class="el-icon-location-information"></i>
                  </template>
                </el-input>
              </el-form-item>
            </div>
            
            <div class="form-grid">
              <el-form-item label="用户角色">
                <el-select v-model="userForm.role" placeholder="请选择用户角色" size="medium" class="role-selector">
                  <el-option v-for="(item, index) in rolesList" :key="index" :label="item.nameZh" :value="item.id">
                    <div class="role-option">
                      <i class="el-icon-s-custom"></i>
                      <div>
                        <div class="role-name">{{ item.nameZh }}</div>
                        <div class="role-id">{{ item.name }}</div>
                      </div>
                    </div>
                  </el-option>
                </el-select>
              </el-form-item>
              
              <el-form-item label="职业">
                <el-input v-model="userForm.ext01" placeholder="请输入职业" size="medium">
                  <template #prefix>
                    <i class="el-icon-suitcase"></i>
                  </template>
                </el-input>
              </el-form-item>
            </div>
            
            <el-form-item label="用户头像">
              <div class="avatar-upload-container">
                <el-upload class="avatar-uploader" action="/api/file/upload" :show-file-list="false"
                    :on-success="handleUploadSuccess" :before-upload="beforeUpload">
                  <div class="avatar-preview">
                    <img v-if="this.userForm.avatar" :src="this.userForm.avatar" class="avatar">
                    <div v-else class="avatar-placeholder">
                      <i class="el-icon-plus"></i>
                      <div>上传头像</div>
                    </div>
                  </div>
                </el-upload>
                <div class="avatar-tip">支持JPG/PNG格式，小于50MB</div>
              </div>
            </el-form-item>
          </el-form>
        </div>
  
        <!-- 第三步: 信息确认 -->
        <div v-show="active === 2" class="step step3 animated fadeIn">
          <div class="step-header">
            <i class="el-icon-view"></i>
            <h2>用户信息确认</h2>
            <p>请确认您输入的用户信息</p>
          </div>
          
          <div class="user-card-container">
            <el-card class="user-card" shadow="hover">
              <div class="user-card-header">
                <div class="user-avatar">
                  <el-image style="width: 100%; height: 100%" :src="this.userForm.avatar" fit="cover">
                    <div slot="error" class="image-error">
                      <i class="el-icon-user"></i>
                    </div>
                  </el-image>
                </div>
                <div class="user-title">
                  <h3>{{ this.userForm.nickname || '新用户' }}</h3>
                  <el-tag type="success">{{ this.userForm.note || '普通用户' }}</el-tag>
                </div>
              </div>
              
              <div class="user-info-grid">
                <div class="info-item">
                  <i class="el-icon-user"></i>
                  <div>
                    <div class="info-label">用户名</div>
                    <div class="info-value">{{ this.userForm.username }}</div>
                  </div>
                </div>
                
                <div class="info-item">
                  <i class="el-icon-credit-card"></i>
                  <div>
                    <div class="info-label">身份证号</div>
                    <div class="info-value">{{ this.userForm.ext02 }}</div>
                  </div>
                </div>
                
                <div class="info-item">
                  <i class="el-icon-suitcase"></i>
                  <div>
                    <div class="info-label">职业</div>
                    <div class="info-value">{{ this.userForm.ext01 || '-' }}</div>
                  </div>
                </div>
                
                <div class="info-item">
                  <i class="el-icon-message"></i>
                  <div>
                    <div class="info-label">邮箱</div>
                    <div class="info-value">{{ this.userForm.email || '-' }}</div>
                  </div>
                </div>
                
                <div class="info-item">
                  <i class="el-icon-phone"></i>
                  <div>
                    <div class="info-label">电话</div>
                    <div class="info-value">{{ this.userForm.phone || '-' }}</div>
                  </div>
                </div>
                
                <div class="info-item">
                  <i class="el-icon-location-information"></i>
                  <div>
                    <div class="info-label">地址</div>
                    <div class="info-value">{{ this.userForm.address || '-' }}</div>
                  </div>
                </div>
              </div>
            </el-card>
          </div>
        </div>
  
        <div class="step-actions">
          <el-button v-if="active > 0" type="info" @click="pre" class="btn-pre">
            <i class="el-icon-arrow-left"></i> 上一步
          </el-button>
          
          <el-button v-if="active < 2" type="primary" @click="next" class="btn-next">
            下一步 <i class="el-icon-arrow-right"></i>
          </el-button>
          
          <router-link to="/home/user/list" v-if="active === 2">
            <el-button type="success" class="btn-complete">
              <i class="el-icon-check"></i> 完成并返回
            </el-button>
          </router-link>
        </div>
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
          callback(new Error('请填写有效的二代身份证号码'));
        } else {
          if (!reg.test(value)) {
            callback(new Error('请填写有效的二代身份证号码'));
          } else {
            callback();
          }
        }
      };
      return {
        dialogAvatarUrl: '',
        dialogAvatarVisible: false,
        rolesList: [],      //所有的角色列表
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
            { validator: ext02, required: true, message: '请填写有效的二代身份证号码',  trigger: 'blur' }
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
            this.rolesList.push(...res.data.data);
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
          this.userForm.avatar = res.data;
          this.$message.success("头像上传成功");
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
                    text: '验证用户信息',
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
  .user-add-container {
    /* max-width: 900px; */
    margin: 0 auto;
    padding: 5px;
    font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
    background-color: #f8fafc;
    min-height: 100vh;
  }
  
  .header {
    text-align: center;
    margin-bottom: 30px;
    padding: 20px 0;
    color: #2c3e50;
    animation: fadeInDown 0.6s;
  }
  
  .header h1 {
    font-size: 1.2rem;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    font-weight: 600;
    color: #1a365d;
  }
  
  .header .subtitle {
    font-size: 1rem;
    color: #718096;
    font-weight: 400;
  }
  
  .step-container {
    margin: 30px 0;
    padding: 20px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  }
  
  .step-desc {
    font-size: 0.85rem;
    color: #718096;
  }
  
  .form-container {
    background: white;
    border-radius: 12px;
    padding: 30px;
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  }
  
  .step {
    padding: 20px 0;
  }
  
  .step-header {
    text-align: center;
    margin-bottom: 30px;
  }
  
  .step-header i {
    font-size: 2rem;
    color: #409eff;
    margin-bottom: 15px;
  }
  
  .step-header h2 {
    font-size: 1rem;
    margin-bottom: 10px;
    color: #2c3e50;
  }
  
  .step-header p {
    color: #718096;
    font-size: 1rem;
  }
  
  .form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 30px;
    margin-bottom: 20px;
  }
  
  @media (max-width: 768px) {
    .form-grid {
      grid-template-columns: 1fr;
    }
  }
  
  .form-tip {
    font-size: 0.85rem;
    color: #718096;
    margin-top: 6px;
  }
  
  .avatar-upload-container {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .avatar-preview {
    width: 180px;
    height: 180px;
    border: 2px dashed #dcdfe6;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s;
    overflow: hidden;
    background: #f8fafc;
  }
  
  .avatar-preview:hover {
    border-color: #409eff;
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
  }
  
  .avatar {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .avatar-placeholder {
    text-align: center;
    color: #8c939d;
  }
  
  .avatar-placeholder i {
    font-size: 3rem;
    display: block;
    margin-bottom: 10px;
  }
  
  .avatar-tip {
    font-size: 0.85rem;
    color: #718096;
    margin-top: 10px;
    text-align: center;
  }
  
  .role-selector {
    width: 100%;
  }
  
  .role-option {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 8px 0;
  }
  
  .role-name {
    font-weight: 500;
    margin-bottom: 4px;
  }
  
  .role-id {
    font-size: 0.85rem;
    color: #718096;
  }
  
  .user-card-container {
    max-width: 700px;
    margin: 0 auto;
  }
  
  .user-card {
    border-radius: 12px;
    overflow: hidden;
  }
  
  .user-card-header {
    display: flex;
    align-items: center;
    padding: 20px;
    background: linear-gradient(135deg, #f0f7ff, #e6f7ff);
    border-bottom: 1px solid #e8f4ff;
  }
  
  .user-avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    overflow: hidden;
    border: 3px solid white;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .user-title {
    margin-left: 20px;
  }
  
  .user-title h3 {
    font-size: 1.5rem;
    margin-bottom: 10px;
    color: #2c3e50;
  }
  
  .image-error {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    color: #909399;
    font-size: 2.5rem;
  }
  
  .user-info-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
    padding: 30px;
  }
  
  .info-item {
    display: flex;
    align-items: center;
    gap: 15px;
    padding: 15px;
    border-radius: 8px;
    background: #f9fbfd;
    transition: all 0.3s;
  }
  
  .info-item:hover {
    background: #f0f7ff;
    transform: translateY(-3px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
  }
  
  .info-item i {
    font-size: 1.8rem;
    color: #409eff;
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background: #e8f4ff;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .info-label {
    font-size: 0.9rem;
    color: #718096;
    margin-bottom: 4px;
  }
  
  .info-value {
    font-size: 1.1rem;
    font-weight: 500;
    color: #2c3e50;
  }
  
  .step-actions {
    display: flex;
    justify-content: center;
    margin-top: 40px;
    gap: 20px;
  }
  
  .btn-pre, .btn-next, .btn-complete {
    padding: 12px 30px;
    font-size: 1rem;
    font-weight: 500;
    border-radius: 8px;
    min-width: 150px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    transition: all 0.3s;
  }
  
  .btn-pre {
    background: #f0f2f5;
    border-color: #dcdfe6;
    color: #606266;
  }
  
  .btn-pre:hover {
    background: #e6e9ed;
    transform: translateY(-2px);
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
  }
  
  .btn-next {
    background: linear-gradient(135deg, #409eff, #337ecc);
    border: none;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    color: white;
  }
  
  .btn-next:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
  }
  
  .btn-complete {
    background: linear-gradient(135deg, #67c23a, #529b2e);
    border: none;
    box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
    color: white;
  }
  
  .btn-complete:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(103, 194, 58, 0.4);
  }
  
  @keyframes fadeInDown {
    from {
      opacity: 0;
      transform: translateY(-20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  
  .animated {
    animation-duration: 0.5s;
    animation-fill-mode: both;
  }
  
  .fadeIn {
    animation-name: fadeIn;
  }
  
  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }
  
  /* 表单标签样式 */
  .el-form-item >>> .el-form-item__label {
    font-weight: 600;
    color: #2d3748;
    padding-bottom: 8px;
    font-size: 1rem;
  }
  
  /* 输入框样式 */
  .el-input >>> .el-input__inner, 
  .el-textarea >>> .el-textarea__inner {
    border-radius: 8px;
    border: 1px solid #e2e8f0;
    transition: all 0.3s;
  }
  
  .el-input >>> .el-input__inner:focus, 
  .el-textarea >>> .el-textarea__inner:focus {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(66, 153, 225, 0.2);
  }
  </style>