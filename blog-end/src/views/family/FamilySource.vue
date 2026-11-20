<template>
    <div class="genealogy-source">
        <div class="source-container">
            <!-- 顶部控制栏 -->
            <div class="control-bar">
                <div class="search-section">
                    <div class="search-item">
                        <span class="search-label">
                            <i class="el-icon-search"></i>
                            姓名
                        </span>
                        <el-input placeholder="按用户名搜索" v-model="serachName" size="small" class="genealogy-input"
                            prefix-icon="el-icon-search" clearable @keyup.enter="serachByNameFun"></el-input>
                    </div>

                    <div class="search-actions">
                        <el-button class="search-btn" size="small" @click="serachByNameFun">
                            <i class="el-icon-search"></i>搜索
                        </el-button>
                        <el-button class="reset-btn" size="small" @click="zoomToFitFun">
                            <i class="el-icon-s-marketing"></i>总览
                        </el-button>
                        <el-button v-if="isEmptyGenealogy" class="create-first-btn" size="small"
                            @click="openCreateFirstAncestorDialog">
                            <i class="el-icon-user-solid"></i>创建第一位先祖
                        </el-button>
                    </div>
                </div>
            </div>

            <!-- 族谱面板 - 当数据为空时显示空状态 -->
            <div class="genealogy-panel" ref="myDiagramDiv" :class="{ 'hidden': isEmptyGenealogy }"></div>

            <!-- 空状态覆盖层 -->
            <div v-if="isEmptyGenealogy" class="empty-genealogy-overlay">
                <div class="empty-state-content">
                    <div class="empty-icon">
                        <i class="el-icon-folder-opened"></i>
                    </div>
                    <h3 class="empty-title">暂无族谱数据</h3>
                    <p class="empty-desc">当前族谱还没有成员，开始创建第一位先祖吧</p>
                    <el-button type="primary" size="large" @click="openCreateFirstAncestorDialog"
                        class="create-first-btn-large">
                        <i class="el-icon-plus"></i> 创建第一位先祖
                    </el-button>
                </div>
            </div>

            <!-- 成员信息对话框 -->
            <el-dialog width="700px" center :title="panTitle" :visible.sync="dialogUserInfo"
                custom-class="genealogy-member-dialog">
                <div class="member-info-container">
                    <div class="avatar-section">
                        <div class="avatar-frame">
                            <el-avatar :size="140" :src="UserInfo.userImgUrl" class="member-avatar"></el-avatar>
                        </div>
                        <div class="member-name">{{ UserInfo.userName }}</div>
                        <div class="member-generation">第 {{ UserInfo.userLove }} 代</div>
                    </div>

                    <div class="info-section">
                        <div class="info-grid">
                            <div class="info-row">
                                <span class="info-label">性别</span>
                                <span class="info-value">
                                    <i :class="UserInfo.userSex === '男' ? 'el-icon-male' : 'el-icon-female'"></i>
                                    {{ UserInfo.userSex }}
                                </span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">年龄</span>
                                <span class="info-value"><i class="el-icon-time"></i>{{ UserInfo.age }}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">生辰</span>
                                <span class="info-value"><i class="el-icon-date"></i>{{ UserInfo.userBirthDay }}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">父亲</span>
                                <span class="info-value"><i class="el-icon-user"></i>{{ UserInfo.pName || '无' }}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">母亲</span>
                                <span class="info-value"><i class="el-icon-user"></i>{{ UserInfo.mName || '无' }}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">配偶</span>
                                <span class="info-value">
                                    <span v-for="(item, index) in selectUserMates" :key="index">
                                        {{ item.userName }}{{ index < selectUserMates.length - 1 ? '、' : '' }} </span>
                                            <span v-if="!selectUserMates || selectUserMates.length === 0">无</span>
                                    </span>
                            </div>
                        </div>

                        <div class="info-grid">
                            <div class="info-row">
                                <span class="info-label">邮箱</span>
                                <span class="info-value"><i class="el-icon-message"></i>{{ UserInfo.userEmil || '无'
                                }}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">手机号</span>
                                <span class="info-value"><i class="el-icon-mobile-phone"></i>{{ UserInfo.userPhone || '无'
                                }}</span>
                            </div>
                            <div class="info-row full-row">
                                <span class="info-label">座右铭</span>
                                <span class="info-value note-value">{{ UserInfo.userNote || '无' }}</span>
                            </div>
                            <div class="info-row full-row">
                                <span class="info-label">现地址</span>
                                <span class="info-value">{{ UserInfo.userAddressNew || '无' }}</span>
                            </div>
                            <div class="info-row full-row">
                                <span class="info-label">户籍地</span>
                                <span class="info-value">{{ UserInfo.userAddressOld || '无' }}</span>
                            </div>
                        </div>

                        <div class="info-grid" v-show="isDieShow">
                            <div class="info-row">
                                <span class="info-label">忌日</span>
                                <span class="info-value"><i class="el-icon-moon"></i>{{ UserInfo.userDieDay }}</span>
                            </div>
                            <div class="info-row">
                                <span class="info-label">亡故年</span>
                                <span class="info-value"><i class="el-icon-watch"></i>{{ UserInfo.dieDayNum }}年</span>
                            </div>
                        </div>
                    </div>
                </div>
            </el-dialog>

            <!-- 创建第一位先祖专用对话框 -->
            <el-dialog width="800px" center title="创建第一位先祖" :visible.sync="dialogCreateFirst"
                custom-class="genealogy-create-dialog">
                <div class="create-form-panel">
                    <el-form ref="childrenFrom" :rules="userFormRules" :model="UserInfo" label-width="80px"
                        class="genealogy-dialog-form">
                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item label="姓名" prop="userName">
                                    <el-input v-model="UserInfo.userName" class="genealogy-dialog-input"></el-input>
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="性别" prop="userSex">
                                    <el-radio-group v-model="UserInfo.userSex">
                                        <el-radio label="男"></el-radio>
                                        <el-radio label="女"></el-radio>
                                    </el-radio-group>
                                </el-form-item>
                            </el-col>
                        </el-row>

                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item label="生辰" prop="userBirthDay">
                                    <el-date-picker type="date" placeholder="选择生辰" v-model="UserInfo.userBirthDay"
                                        :picker-options="pickerOptions" style="width: 100%;"></el-date-picker>
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="是否建在">
                                    <el-radio-group v-model="UserInfo.isSurvival" @change="changeRadio_C">
                                        <el-radio label="是"></el-radio>
                                        <el-radio label="否"></el-radio>
                                    </el-radio-group>
                                </el-form-item>
                            </el-col>
                        </el-row>

                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item label="忌日">
                                    <el-date-picker type="date" placeholder="选择忌日" v-model="UserInfo.userDieDay"
                                        :disabled="isDieDayDisabled_C" :picker-options="pickerOptions"
                                        style="width: 100%;"></el-date-picker>
                                </el-form-item>
                            </el-col>
                            <el-col :span="12">
                                <el-form-item label="电话" prop="userPhone">
                                    <el-input v-model="UserInfo.userPhone"></el-input>
                                </el-form-item>
                            </el-col>
                        </el-row>

                        <el-row :gutter="20">
                            <el-col :span="12">
                                <el-form-item label="邮箱" prop="userEmil">
                                    <el-input v-model="UserInfo.userEmil"></el-input>
                                </el-form-item>
                            </el-col>
                        </el-row>

                        <el-form-item label="户籍地">
                            <address-cascader :value="UserInfo.userAddressOldCode"
                                :detail-value="UserInfo.userAddressOldDetail" @address-change="handleOldAddressChange"
                                @detail-change="(val) => UserInfo.userAddressOldDetail = val"
                                @update:detailValue="(val) => UserInfo.userAddressOldDetail = val">
                            </address-cascader>
                        </el-form-item>

                        <el-form-item label="现居地">
                            <address-cascader :value="UserInfo.userAddressNewCode"
                                :detail-value="UserInfo.userAddressNewDetail" @address-change="handleNewAddressChange"
                                @detail-change="(val) => UserInfo.userAddressNewDetail = val"
                                @update:detailValue="(val) => UserInfo.userAddressNewDetail = val">
                            </address-cascader>
                        </el-form-item>

                        <el-form-item label="座右铭">
                            <el-input type="textarea" :rows="3" v-model="UserInfo.userNote"></el-input>
                        </el-form-item>
                    </el-form>
                </div>
                <div class="dialog-actions">
                    <el-button class="dialog-cancel-btn" @click="dialogCreateFirst = false">取 消</el-button>
                    <el-button class="dialog-confirm-btn" @click="addFirstBtnFun">确 认 添 加</el-button>
                </div>
            </el-dialog>

            <!-- 成员操作对话框 -->
            <el-dialog width="900px" center :title="selectUserName" :visible.sync="dialogUserOp"
                custom-class="genealogy-operation-dialog">
                <div class="operation-container">
                    <div class="operation-menu">
                        <button class="menu-btn menu-primary" @click="addFMinfoBtn" v-show="fmInfoBtn">
                            <i class="el-icon-user-solid"></i>
                            <span>添加父母</span>
                        </button>
                        <button class="menu-btn menu-success" @click="addMateInfoBtn" v-show="mateInfoBtn">
                            <i class="el-icon-s-custom"></i>
                            <span>添加配偶</span>
                        </button>
                        <button class="menu-btn menu-warning" @click="addChildrenBtn" v-show="childrenInfoBtn">
                            <i class="el-icon-children"></i>
                            <span>添加子女</span>
                        </button>
                        <button class="menu-btn menu-danger" @click="delChildrenBtn" v-show="childrenInfoBtn">
                            <i class="el-icon-delete"></i>
                            <span>删除成员</span>
                        </button>
                    </div>

                    <div class="operation-content">
                        <!-- 添加父母面板 -->
                        <div v-show="fmInfoPan" class="operation-panel">
                            <div class="panel-header">添加父母</div>
                            <el-collapse accordion class="genealogy-collapse">
                                <el-collapse-item title="父亲信息" class="collapse-item">
                                    <el-form ref="userFatherForm" :rules="fatherFormRules" :model="userFatherForm"
                                        label-width="80px" class="genealogy-dialog-form">
                                        <el-form-item label="姓名" prop="userName">
                                            <el-input v-model="userFatherForm.userName"></el-input>
                                        </el-form-item>
                                        <el-form-item label="性别" prop="userSex">
                                            <el-radio-group v-model="userFatherForm.userSex">
                                                <el-radio label="男"></el-radio>
                                                <el-radio label="女"></el-radio>
                                            </el-radio-group>
                                        </el-form-item>
                                        <el-form-item label="生辰" prop="userBirthDay">
                                            <el-date-picker type="date" placeholder="选择生辰"
                                                v-model="userFatherForm.userBirthDay" :picker-options="pickerOptions"
                                                style="width: 100%;"></el-date-picker>
                                        </el-form-item>
                                        <el-form-item label="是否建在">
                                            <el-radio-group v-model="userFatherForm.isSurvival" @change="changeRadio_F">
                                                <el-radio label="是"></el-radio>
                                                <el-radio label="否"></el-radio>
                                            </el-radio-group>
                                        </el-form-item>
                                        <el-form-item label="忌日">
                                            <el-date-picker type="date" placeholder="选择忌日"
                                                v-model="userFatherForm.userDieDay" :disabled="isDieDayDisabled_F"
                                                :picker-options="pickerOptions" style="width: 100%;"></el-date-picker>
                                        </el-form-item>
                                        <el-form-item label="电话" prop="userPhone">
                                            <el-input v-model="userFatherForm.userPhone"></el-input>
                                        </el-form-item>
                                        <el-form-item label="邮箱" prop="userEmil">
                                            <el-input v-model="userFatherForm.userEmil"></el-input>
                                        </el-form-item>
                                        <el-form-item label="户籍地">
                                            <address-cascader :value="userFatherForm.userAddressOldCode"
                                                :detail-value="userFatherForm.userAddressOldDetail"
                                                @address-change="handleFatherOldAddressChange"
                                                @detail-change="(val) => userFatherForm.userAddressOldDetail = val">
                                            </address-cascader>
                                        </el-form-item>
                                        <el-form-item label="现居地">
                                            <address-cascader :value="userFatherForm.userAddressNewCode"
                                                :detail-value="userFatherForm.userAddressNewDetail"
                                                @address-change="handleFatherNewAddressChange"
                                                @detail-change="(val) => userFatherForm.userAddressNewDetail = val">
                                            </address-cascader>
                                        </el-form-item>
                                        <el-form-item label="座右铭">
                                            <el-input type="textarea" :rows="2"
                                                v-model="userFatherForm.userNote"></el-input>
                                        </el-form-item>
                                    </el-form>
                                </el-collapse-item>
                                <el-collapse-item title="母亲信息" class="collapse-item">
                                    <el-form ref="userMatherForm" :rules="matherFormRules" :model="userMatherForm"
                                        label-width="80px" class="genealogy-dialog-form">
                                        <el-form-item label="姓名" prop="userName">
                                            <el-input v-model="userMatherForm.userName"></el-input>
                                        </el-form-item>
                                        <el-form-item label="性别" prop="userSex">
                                            <el-radio-group v-model="userMatherForm.userSex">
                                                <el-radio label="男"></el-radio>
                                                <el-radio label="女"></el-radio>
                                            </el-radio-group>
                                        </el-form-item>
                                        <el-form-item label="生辰" prop="userBirthDay">
                                            <el-date-picker type="date" placeholder="选择生辰"
                                                v-model="userMatherForm.userBirthDay" :picker-options="pickerOptions"
                                                style="width: 100%;"></el-date-picker>
                                        </el-form-item>
                                        <el-form-item label="是否建在">
                                            <el-radio-group v-model="userMatherForm.isSurvival" @change="changeRadio_M">
                                                <el-radio label="是"></el-radio>
                                                <el-radio label="否"></el-radio>
                                            </el-radio-group>
                                        </el-form-item>
                                        <el-form-item label="忌日">
                                            <el-date-picker type="date" placeholder="选择忌日"
                                                v-model="userMatherForm.userDieDay" :disabled="isDieDayDisabled_M"
                                                :picker-options="pickerOptions" style="width: 100%;"></el-date-picker>
                                        </el-form-item>
                                        <el-form-item label="电话" prop="userPhone">
                                            <el-input v-model="userMatherForm.userPhone"></el-input>
                                        </el-form-item>
                                        <el-form-item label="邮箱" prop="userEmil">
                                            <el-input v-model="userMatherForm.userEmil"></el-input>
                                        </el-form-item>
                                        <el-form-item label="户籍地">
                                            <address-cascader :value="userMatherForm.userAddressOldCode"
                                                :detail-value="userMatherForm.userAddressOldDetail"
                                                @address-change="handleMotherOldAddressChange"
                                                @detail-change="(val) => userMatherForm.userAddressOldDetail = val">
                                            </address-cascader>
                                        </el-form-item>
                                        <el-form-item label="现居地">
                                            <address-cascader :value="userMatherForm.userAddressNewCode"
                                                :detail-value="userMatherForm.userAddressNewDetail"
                                                @address-change="handleMotherNewAddressChange"
                                                @detail-change="(val) => userMatherForm.userAddressNewDetail = val">
                                            </address-cascader>
                                        </el-form-item>
                                        <el-form-item label="座右铭">
                                            <el-input type="textarea" :rows="2"
                                                v-model="userMatherForm.userNote"></el-input>
                                        </el-form-item>
                                    </el-form>
                                </el-collapse-item>
                            </el-collapse>
                            <div class="panel-actions">
                                <el-button class="panel-confirm-btn" @click="addFMinfoBtnFun">确认添加</el-button>
                            </div>
                        </div>

                        <!-- 添加配偶面板 -->
                        <div v-show="mateInfoPan" class="operation-panel">
                            <div class="panel-header">添加配偶</div>
                            <el-form ref="mateFrom" :rules="userFormRules" :model="UserInfo" label-width="80px"
                                class="genealogy-dialog-form">
                                <el-row :gutter="20">
                                    <el-col :span="12">
                                        <el-form-item label="姓名" prop="userName">
                                            <el-input v-model="UserInfo.userName"></el-input>
                                        </el-form-item>
                                    </el-col>
                                    <el-col :span="12">
                                        <el-form-item label="性别" prop="userSex">
                                            <el-radio-group v-model="UserInfo.userSex">
                                                <el-radio label="男"></el-radio>
                                                <el-radio label="女"></el-radio>
                                            </el-radio-group>
                                        </el-form-item>
                                    </el-col>
                                </el-row>
                                <el-row :gutter="20">
                                    <el-col :span="12">
                                        <el-form-item label="生辰" prop="userBirthDay">
                                            <el-date-picker type="date" placeholder="选择生辰" v-model="UserInfo.userBirthDay"
                                                :picker-options="pickerOptions" style="width: 100%;"></el-date-picker>
                                        </el-form-item>
                                    </el-col>
                                    <el-col :span="12">
                                        <el-form-item label="是否建在">
                                            <el-radio-group v-model="UserInfo.isSurvival" @change="changeRadio_Mate">
                                                <el-radio label="是"></el-radio>
                                                <el-radio label="否"></el-radio>
                                            </el-radio-group>
                                        </el-form-item>
                                    </el-col>
                                </el-row>
                                <el-row :gutter="20">
                                    <el-col :span="12">
                                        <el-form-item label="忌日">
                                            <el-date-picker type="date" placeholder="选择忌日" v-model="UserInfo.userDieDay"
                                                :disabled="isDieDayDisabled_Mate" :picker-options="pickerOptions"
                                                style="width: 100%;"></el-date-picker>
                                        </el-form-item>
                                    </el-col>
                                    <el-col :span="12">
                                        <el-form-item label="电话" prop="userPhone">
                                            <el-input v-model="UserInfo.userPhone"></el-input>
                                        </el-form-item>
                                    </el-col>
                                </el-row>
                                <el-row :gutter="20">
                                    <el-col :span="12">
                                        <el-form-item label="邮箱" prop="userEmil">
                                            <el-input v-model="UserInfo.userEmil"></el-input>
                                        </el-form-item>
                                    </el-col>
                                </el-row>
                                <el-form-item label="户籍地">
                                    <address-cascader :value="UserInfo.userAddressOldCode"
                                        :detail-value="UserInfo.userAddressOldDetail"
                                        @address-change="handleOldAddressChange"
                                        @detail-change="(val) => UserInfo.userAddressOldDetail = val">
                                    </address-cascader>
                                </el-form-item>
                                <el-form-item label="现居地">
                                    <address-cascader :value="UserInfo.userAddressNewCode"
                                        :detail-value="UserInfo.userAddressNewDetail"
                                        @address-change="handleNewAddressChange"
                                        @detail-change="(val) => UserInfo.userAddressNewDetail = val">
                                    </address-cascader>
                                </el-form-item>
                                <el-form-item label="座右铭">
                                    <el-input type="textarea" :rows="2" v-model="UserInfo.userNote"></el-input>
                                </el-form-item>
                            </el-form>
                            <div class="panel-actions">
                                <el-button class="panel-confirm-btn" @click="addMateInfoBtnFun">确认添加</el-button>
                            </div>
                        </div>

                        <!-- 添加子女面板 -->
                        <div v-show="childrenInfoPan" class="operation-panel">
                            <div class="panel-header">{{ panTitle }}</div>
                            <el-form ref="childrenFrom" :rules="userFormRules" :model="UserInfo" label-width="80px"
                                class="genealogy-dialog-form">
                                <el-row :gutter="20">
                                    <el-col :span="12">
                                        <el-form-item label="姓名" prop="userName">
                                            <el-input v-model="UserInfo.userName"></el-input>
                                        </el-form-item>
                                    </el-col>
                                    <el-col :span="12">
                                        <el-form-item label="性别" prop="userSex">
                                            <el-radio-group v-model="UserInfo.userSex">
                                                <el-radio label="男"></el-radio>
                                                <el-radio label="女"></el-radio>
                                            </el-radio-group>
                                        </el-form-item>
                                    </el-col>
                                </el-row>
                                <el-row :gutter="20">
                                    <el-col :span="12">
                                        <el-form-item label="生辰" prop="userBirthDay">
                                            <el-date-picker type="date" placeholder="选择生辰" v-model="UserInfo.userBirthDay"
                                                :picker-options="pickerOptions" style="width: 100%;"></el-date-picker>
                                        </el-form-item>
                                    </el-col>
                                    <el-col :span="12">
                                        <el-form-item label="是否建在">
                                            <el-radio-group v-model="UserInfo.isSurvival" @change="changeRadio_C">
                                                <el-radio label="是"></el-radio>
                                                <el-radio label="否"></el-radio>
                                            </el-radio-group>
                                        </el-form-item>
                                    </el-col>
                                </el-row>
                                <el-row :gutter="20">
                                    <el-col :span="12">
                                        <el-form-item label="忌日">
                                            <el-date-picker type="date" placeholder="选择忌日" v-model="UserInfo.userDieDay"
                                                :disabled="isDieDayDisabled_C" :picker-options="pickerOptions"
                                                style="width: 100%;"></el-date-picker>
                                        </el-form-item>
                                    </el-col>
                                    <el-col :span="12">
                                        <el-form-item label="电话" prop="userPhone">
                                            <el-input v-model="UserInfo.userPhone"></el-input>
                                        </el-form-item>
                                    </el-col>
                                </el-row>
                                <el-row :gutter="20">
                                    <el-col :span="12">
                                        <el-form-item label="邮箱" prop="userEmil">
                                            <el-input v-model="UserInfo.userEmil"></el-input>
                                        </el-form-item>
                                    </el-col>
                                </el-row>
                                <el-form-item label="户籍地">
                                    <address-cascader :value="UserInfo.userAddressOldCode"
                                        :detail-value="UserInfo.userAddressOldDetail"
                                        @address-change="handleOldAddressChange"
                                        @detail-change="(val) => UserInfo.userAddressOldDetail = val">
                                    </address-cascader>
                                </el-form-item>
                                <el-form-item label="现居地">
                                    <address-cascader :value="UserInfo.userAddressNewCode"
                                        :detail-value="UserInfo.userAddressNewDetail"
                                        @address-change="handleNewAddressChange"
                                        @detail-change="(val) => UserInfo.userAddressNewDetail = val">
                                    </address-cascader>
                                </el-form-item>
                                <el-form-item label="座右铭">
                                    <el-input type="textarea" :rows="2" v-model="UserInfo.userNote"></el-input>
                                </el-form-item>
                            </el-form>
                            <div class="panel-actions">
                                <el-button class="panel-confirm-btn" @click="addChildrenBtnFun">确认添加</el-button>
                            </div>
                        </div>

                        <!-- 配偶选择面板 -->
                        <div v-show="mateOptionPan" class="mate-panel">
                            <div class="panel-header">选择配偶</div>
                            <div class="mate-grid">
                                <div v-for="item in selectUserMates" :key="item.userId" class="mate-item">
                                    <button :class="['mate-btn', item.userSex === '男' ? 'male' : 'female']"
                                        @click="selectUserMateBtn(item)">
                                        <i :class="item.userSex === '男' ? 'el-icon-male' : 'el-icon-female'"></i>
                                        <span>{{ item.userName }}</span>
                                    </button>
                                </div>
                            </div>
                        </div>

                        <!-- 删除成员面板 -->
                        <div v-show="delCHildrenPan" class="delete-panel">
                            <div class="panel-header warning">下列人员将会被移除族谱</div>
                            <div class="delete-list">
                                <span v-for="item in selectChildrenName" :key="item" class="delete-tag">
                                    <i class="el-icon-user"></i> {{ item }}
                                </span>
                            </div>
                            <div class="panel-actions">
                                <el-button class="panel-delete-btn" @click="delChildrenBtnFun">确认删除</el-button>
                            </div>
                        </div>
                    </div>
                </div>
            </el-dialog>
        </div>
    </div>
</template>

<script>
// ... 保持原有的 script 内容完全不变 ...
import { parseCodeToString, combineFullAddress } from '@/utils/addressUtil';
import AddressCascader from '@/components/AddressCascader.vue';

class GenogramLayout extends go.LayeredDigraphLayout {
    // ... 保持原有的 GenogramLayout 代码不变 ...
    constructor() {
        super();
        this.alignOption = go.LayeredDigraphLayout.AlignAll;
        this.initializeOption = go.LayeredDigraphLayout.InitDepthFirstIn;
        this.spouseSpacing = 30;
        this.isRouting = false;
    }
    makeNetwork(coll) {
        const net = this.createNetwork();
        if (coll instanceof go.Diagram) {
            this.add(net, coll.nodes, true);
            this.add(net, coll.links, true);
        } else if (coll instanceof go.Group) {
            this.add(net, coll.memberParts, false);
        } else if (coll.iterator) {
            this.add(net, coll.iterator, false);
        }
        return net;
    }
    add(net, coll, nonmemberonly) {
        var multiSpousePeople = new go.Set();
        var it = coll.iterator;
        while (it.next()) {
            var node = it.value;
            if (!(node instanceof go.Node)) continue;
            if (!node.isLayoutPositioned || !node.isVisible()) continue;
            if (nonmemberonly && node.containingGroup !== null) continue;
            if (node.isLinkLabel) {
                var link = node.labeledLink;
                var spouseA = link.fromNode;
                var spouseB = link.toNode;
                var vertex = net.addNode(node);
                vertex.width = spouseA.actualBounds.width + this.spouseSpacing + spouseB.actualBounds.width;
                vertex.height = Math.max(spouseA.actualBounds.height, spouseB.actualBounds.height);
                vertex.focus = new go.Point(spouseA.actualBounds.width + this.spouseSpacing / 2, vertex.height / 2);
            } else {
                var marriages = 0;
                node.linksConnected.each(function (l) { if (l.isLabeledLink) marriages++; });
                if (marriages === 0) {
                    var vertex = net.addNode(node);
                } else if (marriages > 1) {
                    multiSpousePeople.add(node);
                }
            }
        }
        it.reset();
        while (it.next()) {
            var link = it.value;
            if (!(link instanceof go.Link)) continue;
            if (!link.isLayoutPositioned || !link.isVisible()) continue;
            if (nonmemberonly && link.containingGroup !== null) continue;
            if (!link.isLabeledLink) {
                var parent = net.findVertex(link.fromNode);
                var child = net.findVertex(link.toNode);
                if (child !== null) {
                    net.linkVertexes(parent, child, link);
                } else {
                    link.toNode.linksConnected.each(function (l) {
                        if (!l.isLabeledLink)
                            return;
                        var mlab = l.labelNodes.first();
                        var mlabvert = net.findVertex(mlab);
                        if (mlabvert !== null) {
                            net.linkVertexes(parent, mlabvert, link);
                        }
                    });
                }
            }
        }
        while (multiSpousePeople.count > 0) {
            var node = multiSpousePeople.first();
            var cohort = new go.Set();
            this.extendCohort(cohort, node);
            var dummyvert = net.createVertex();
            net.addVertex(dummyvert);
            var marriages = new go.Set();
            cohort.each(function (n) {
                n.linksConnected.each(function (l) {
                    marriages.add(l);
                })
            });
            marriages.each(function (link) {
                var mlab = link.labelNodes.first()
                var v = net.findVertex(mlab);
                if (v !== null) {
                    net.linkVertexes(dummyvert, v, null);
                }
            });
            multiSpousePeople.removeAll(cohort);
        }
    }

    extendCohort(coll, node) {
        if (coll.has(node)) return;
        coll.add(node);
        var lay = this;
        node.linksConnected.each(function (l) {
            if (l.isLabeledLink) {
                lay.extendCohort(coll, l.fromNode);
                lay.extendCohort(coll, l.toNode);
            }
        });
    }

    assignLayers() {
        super.assignLayers();
        const horiz = this.direction == 0.0 || this.direction == 180.0;
        const maxsizes = [];
        this.network.vertexes.each(v => {
            const lay = v.layer;
            let max = maxsizes[lay];
            if (max === undefined) max = 0;
            const sz = (horiz ? v.width : v.height);
            if (sz > max) maxsizes[lay] = sz;
        });
        this.network.vertexes.each(v => {
            const lay = v.layer;
            const max = maxsizes[lay];
            if (horiz) {
                v.focus = new go.Point(0, v.height / 2);
                v.width = max;
            } else {
                v.focus = new go.Point(v.width / 2, 0);
                v.height = max;
            }
        });
    }

    initializeIndices() {
        super.initializeIndices();
        const vertical = this.direction === 90 || this.direction === 270;
        this.network.edges.each(e => {
            if (e.fromVertex.node && e.fromVertex.node.isLinkLabel) {
                e.portFromPos = vertical ? e.fromVertex.focusX : e.fromVertex.focusY;
            }
            if (e.toVertex.node && e.toVertex.node.isLinkLabel) {
                e.portToPos = vertical ? e.toVertex.focusX : e.toVertex.focusY;
            }
        })
    }

    commitNodes() {
        super.commitNodes();
        go.LayeredDigraphLayout.prototype.commitNodes.call(this);
        this.network.vertexes.each(function (v) {
            if (v.node !== null && !v.node.isLinkLabel) {
                v.node.position = new go.Point(v.x, v.y);
            }
        });
        var layout = this;
        this.network.vertexes.each(function (v) {
            if (v.node === null) return;
            if (!v.node.isLinkLabel) return;
            var labnode = v.node;
            var lablink = labnode.labeledLink;
            lablink.invalidateRoute();
            var spouseA = lablink.fromNode;
            var spouseB = lablink.toNode;
            if (spouseA.data.sex === "女") {
                var temp = spouseA;
                spouseA = spouseB;
                spouseB = temp;
            }
            var aParentsNode = layout.findParentsMarriageLabelNode(spouseA);
            var bParentsNode = layout.findParentsMarriageLabelNode(spouseB);
            if (aParentsNode !== null && bParentsNode !== null && aParentsNode.position.x > bParentsNode.position.x) {
                var temp = spouseA;
                spouseA = spouseB;
                spouseB = temp;
            }
            spouseA.position = new go.Point(v.x, v.y);
            spouseB.position = new go.Point(v.x + spouseA.actualBounds.width + layout.spouseSpacing, v.y);
            if (spouseA.opacity === 0) {
                var pos = new go.Point(v.centerX - spouseA.actualBounds.width / 2, v.y);
                spouseA.position = pos;
                spouseB.position = pos;
            } else if (spouseB.opacity === 0) {
                var pos = new go.Point(v.centerX - spouseB.actualBounds.width / 2, v.y);
                spouseA.position = pos;
                spouseB.position = pos;
            }
        });
        this.network.vertexes.each(function (v) {
            if (v.node === null || v.node.linksConnected.count > 1)
                return;
            var mnode = layout.findParentsMarriageLabelNode(v.node);
            if (mnode !== null && mnode.linksConnected.count === 1) {
                var mvert = layout.network.findVertex(mnode);
                var newbnds = v.node.actualBounds.copy();
                newbnds.x = mvert.centerX - v.node.actualBounds.width / 2;
                var overlaps = layout.diagram.findObjectsIn(newbnds, function (x) { return x.part; }, function (p) { return p !== v.node; }, true);
                if (overlaps.count === 0) {
                    v.node.move(newbnds.position);
                }
            }
        });
    }
    findParentsMarriageLabelNode(node) {
        const it = node.findNodesInto();
        while (it.next()) {
            const n = it.value;
            if (n.isLinkLabel) return n;
        }
        return null;
    }
}

var gojs = null
var myDiagram = null
import { _getSourceAll, _addSource, _addParents, _delSource } from "@/api/api.js";
import { validatePhone, validateEmail } from "@/utils/validate";
import { parseTime } from '@/utils/calendar';
export default {
    name: 'FamilySource',
    components: {
        AddressCascader
    },
    data() {
        return {
            parentTreeId: '',
            isEmptyGenealogy: false,
            dialogCreateFirst: false,
            selectChildren: [],
            selectChildrenName: [],
            panTitle: '',
            mateOptionPan: false,
            isDieDayDisabled_F: true,
            isDieDayDisabled_M: true,
            isDieDayDisabled_Mate: true,
            isDieDayDisabled_C: true,
            isDieShow: false,
            dialogUserInfo: false,
            dialogUserOp: false,
            fmInfoPan: false,
            fmInfoBtn: true,
            mateInfoPan: false,
            mateInfoBtn: true,
            childrenInfoPan: false,
            childrenInfoBtn: true,
            delCHildrenPan: false,
            jsonarray: [],
            ManImg: '/static/defaultImg.png',
            WmanImg: '/static/defaultW.png',
            timeId: null,
            serachName: '',
            serachNameKey: '',
            selectUserKey: '',
            selectUserSex: '',
            selectUserName: '',
            selectUserLove: '',
            selectUserMates: [],
            selectUserMId: '',
            selectUserPId: '',
            UserInfo: {
                userId: '',
                userName: '',
                isSurvival: '是',
                mName: '',
                pName: '',
                mateName: '',
                userAddressNew: '',
                userAddressOld: '',
                userBirthDay: '',
                userDieDay: '',
                userEmil: '',
                userImgUrl: '',
                userLove: '',
                userMID: '',
                userMateId: '',
                userNote: '',
                userPId: '',
                userPhone: '',
                userPinYin: '',
                userSex: '',
                age: '',
                dieDayNum: '',
                type: '',
                pTree: '',
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
            userFatherForm: {
                userId: '',
                userLove: '',
                userName: '',
                userSex: '男',
                userBirthDay: '',
                isSurvival: '是',
                userDieDay: '',
                userAddressNew: '',
                userAddressOld: '',
                userNote: '',
                userPhone: '',
                userEmil: '',
                pTree: '',
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
            userMatherForm: {
                userId: '',
                userName: '',
                userSex: '女',
                userBirthDay: '',
                isSurvival: '是',
                userDieDay: '',
                userAddressNew: '',
                userAddressOld: '',
                userNote: '',
                userPhone: '',
                userEmil: '',
                pTree: '',
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
            fatherFormRules: {
                userName: [
                    { required: true, message: '请输入父亲名称', trigger: 'blur' }
                ],
                userBirthDay: [
                    { required: true, message: '请输入生辰', trigger: 'change' }
                ],
                userPhone: [
                    { validator: validatePhone, trigger: 'blur' },
                ],
                userEmil: [
                    { validator: validateEmail, trigger: 'blur' },
                ]
            },
            matherFormRules: {
                userName: [
                    { required: true, message: '请输入母亲名称', trigger: 'blur' }
                ],
                userBirthDay: [
                    { required: true, message: '请输入生辰', trigger: 'change' }
                ],
                userPhone: [
                    { validator: validatePhone, trigger: 'blur' },
                ],
                userEmil: [
                    { validator: validateEmail, trigger: 'blur' },
                ]
            },
            userFormRules: {
                userName: [
                    { required: true, message: '请输入姓名', trigger: 'blur' }
                ],
                userSex: [
                    { required: true, message: '请选择性别', trigger: 'blur' }
                ],
                userBirthDay: [
                    { required: true, message: '请选择生辰', trigger: 'change' }
                ],
                userPhone: [
                    { validator: validatePhone, trigger: 'blur' },
                ],
                userEmil: [
                    { validator: validateEmail, trigger: 'blur' },
                ]
            },
        };
    },
    mounted() {
        this.init();
    },
    created() {
        if (this.$route.params.pTree != undefined) {
            this.parentTreeId = this.$route.params.pTree
        }
    },
    methods: {
        handleOldAddressChange(addressInfo) {
            this.UserInfo.userAddressOldCode = addressInfo.codeString;
            this.UserInfo.userAddressOldProvince = addressInfo.province;
            this.UserInfo.userAddressOldCity = addressInfo.city;
            this.UserInfo.userAddressOldArea = addressInfo.area;
            this.UserInfo.userAddressOld = (addressInfo.province || '') +
                (addressInfo.city || '') +
                (addressInfo.area || '') +
                (this.UserInfo.userAddressOldDetail || '');
        },
        handleNewAddressChange(addressInfo) {
            this.UserInfo.userAddressNewCode = addressInfo.codeString;
            this.UserInfo.userAddressNewProvince = addressInfo.province;
            this.UserInfo.userAddressNewCity = addressInfo.city;
            this.UserInfo.userAddressNewArea = addressInfo.area;
            this.UserInfo.userAddressNew = (addressInfo.province || '') +
                (addressInfo.city || '') +
                (addressInfo.area || '') +
                (this.UserInfo.userAddressNewDetail || '');
        },
        handleFatherOldAddressChange(addressInfo) {
            this.userFatherForm.userAddressOldCode = addressInfo.codeString;
            this.userFatherForm.userAddressOldProvince = addressInfo.province;
            this.userFatherForm.userAddressOldCity = addressInfo.city;
            this.userFatherForm.userAddressOldArea = addressInfo.area;
            this.userFatherForm.userAddressOld = (addressInfo.province || '') +
                (addressInfo.city || '') +
                (addressInfo.area || '') +
                (this.userFatherForm.userAddressOldDetail || '');
        },
        handleFatherNewAddressChange(addressInfo) {
            this.userFatherForm.userAddressNewCode = addressInfo.codeString;
            this.userFatherForm.userAddressNewProvince = addressInfo.province;
            this.userFatherForm.userAddressNewCity = addressInfo.city;
            this.userFatherForm.userAddressNewArea = addressInfo.area;
            this.userFatherForm.userAddressNew = (addressInfo.province || '') +
                (addressInfo.city || '') +
                (addressInfo.area || '') +
                (this.userFatherForm.userAddressNewDetail || '');
        },
        handleMotherOldAddressChange(addressInfo) {
            this.userMatherForm.userAddressOldCode = addressInfo.codeString;
            this.userMatherForm.userAddressOldProvince = addressInfo.province;
            this.userMatherForm.userAddressOldCity = addressInfo.city;
            this.userMatherForm.userAddressOldArea = addressInfo.area;
            this.userMatherForm.userAddressOld = (addressInfo.province || '') +
                (addressInfo.city || '') +
                (addressInfo.area || '') +
                (this.userMatherForm.userAddressOldDetail || '');
        },
        handleMotherNewAddressChange(addressInfo) {
            this.userMatherForm.userAddressNewCode = addressInfo.codeString;
            this.userMatherForm.userAddressNewProvince = addressInfo.province;
            this.userMatherForm.userAddressNewCity = addressInfo.city;
            this.userMatherForm.userAddressNewArea = addressInfo.area;
            this.userMatherForm.userAddressNew = (addressInfo.province || '') +
                (addressInfo.city || '') +
                (addressInfo.area || '') +
                (this.userMatherForm.userAddressNewDetail || '');
        },
        init() {
            gojs = this.go.GraphObject.make;
            myDiagram = gojs(go.Diagram, this.$refs.myDiagramDiv,
                {
                    initialAutoScale: go.Diagram.Uniform,
                    "undoManager.isEnabled": false,
                    layout: gojs(GenogramLayout, { direction: 90, layerSpacing: 35 }),
                    allowZoom: true
                });
            myDiagram.nodeTemplateMap.add("活女",
                gojs(go.Node, "Auto",
                    new go.Binding("text", "name"),
                    gojs(go.Shape, "Rectangle", {
                        name: "SHAPE", fill: "#ec4ec3", stroke: 'white', strokeWidth: 3.5,
                        portId: "", fromLinkable: true, toLinkable: true, cursor: "pointer"
                    }),
                    gojs(go.Panel, "Horizontal",
                        gojs(go.Picture, {
                            name: "Picture",
                            desiredSize: new go.Size(75, 75),
                            margin: 1,
                        },
                            new go.Binding("source", "img").makeTwoWay()),
                        gojs(go.Panel, "Table", {
                            minSize: new go.Size(140, NaN),
                            maxSize: new go.Size(200, NaN),
                            margin: new go.Margin(6, 10, 0, 6),
                            defaultAlignment: go.Spot.Left
                        },
                            gojs(go.TextBlock, "姓名:", this.textStyle(), { row: 0, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 0, column: 1, columnSpan: 5,
                                font: "12pt Segoe UI,sans-serif",
                                minSize: new go.Size(10, 16)
                            },
                                new go.Binding("text", "name").makeTwoWay()),
                            gojs(go.TextBlock, "生辰:", this.textStyle(), { row: 1, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 1, column: 1, columnSpan: 4,
                                minSize: new go.Size(10, 14)
                            },
                                new go.Binding("text", "birthDay").makeTwoWay()),
                            gojs(go.TextBlock, "电话：", this.textStyle(), { row: 2, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 2, column: 1, columnSpan: 4,
                                minSize: new go.Size(10, 14)
                            },
                                new go.Binding("text", "phone").makeTwoWay()),
                            gojs(go.TextBlock, "现住址：", this.textStyle(), { row: 3, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 3, column: 1, columnSpan: 5,
                                font: "italic 9pt sans-serif",
                                wrap: go.TextBlock.WrapFit,
                                minSize: new go.Size(10, 14)
                            },
                                new go.Binding("text", "addressNew").makeTwoWay())
                        )
                    ),
                    {
                        doubleClick: this.nodeDoubleClick
                    },
                    {
                        click: this.nodeClick
                    }
                ));
            myDiagram.nodeTemplateMap.add("亡女",
                gojs(go.Node, "Auto",
                    new go.Binding("text", "name"),
                    gojs(go.Shape, "Rectangle", {
                        name: "SHAPE", fill: "#471f1f", stroke: 'white', strokeWidth: 3.5,
                        portId: "", fromLinkable: true, toLinkable: true, cursor: "pointer"
                    }),
                    gojs(go.Panel, "Horizontal",
                        gojs(go.Picture, {
                            name: "Picture",
                            desiredSize: new go.Size(75, 75),
                            margin: 1,
                        },
                            new go.Binding("source", "img").makeTwoWay()),
                        gojs(go.Panel, "Table", {
                            minSize: new go.Size(140, NaN),
                            maxSize: new go.Size(200, NaN),
                            margin: new go.Margin(6, 10, 0, 6),
                            defaultAlignment: go.Spot.Left
                        },
                            gojs(go.TextBlock, "姓名:", this.textStyle(), { row: 0, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 0, column: 1, columnSpan: 5,
                                font: "12pt Segoe UI,sans-serif",
                                minSize: new go.Size(10, 16)
                            },
                                new go.Binding("text", "name").makeTwoWay()),
                            gojs(go.TextBlock, "生辰:", this.textStyle(), { row: 1, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 1, column: 1, columnSpan: 4,
                                minSize: new go.Size(10, 14)
                            },
                                new go.Binding("text", "birthDay").makeTwoWay()),
                            gojs(go.TextBlock, "电话：", this.textStyle(), { row: 2, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 2, column: 1, columnSpan: 4,
                                minSize: new go.Size(10, 14)
                            },
                                new go.Binding("text", "phone").makeTwoWay()),
                            gojs(go.TextBlock, "现住址：", this.textStyle(), { row: 3, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 3, column: 1, columnSpan: 5,
                                font: "italic 9pt sans-serif",
                                wrap: go.TextBlock.WrapFit,
                                minSize: new go.Size(10, 14)
                            },
                                new go.Binding("text", "addressNew").makeTwoWay())
                        )
                    ),
                    {
                        doubleClick: this.nodeDoubleClick
                    },
                    {
                        click: this.nodeClick
                    }
                ));
            myDiagram.nodeTemplateMap.add("亡男",
                gojs(go.Node, "Auto",
                    new go.Binding("text", "name"),
                    gojs(go.Shape, "Rectangle", {
                        name: "SHAPE", fill: "#25294b", stroke: 'white', strokeWidth: 3.5,
                        portId: "", fromLinkable: true, toLinkable: true, cursor: "pointer"
                    }),
                    gojs(go.Panel, "Horizontal",
                        gojs(go.Picture, {
                            name: "Picture",
                            desiredSize: new go.Size(75, 75),
                            margin: 1
                        },
                            new go.Binding("source", "img").makeTwoWay()),
                        gojs(go.Panel, "Table", {
                            minSize: new go.Size(140, NaN),
                            maxSize: new go.Size(200, NaN),
                            margin: new go.Margin(6, 10, 0, 6),
                            defaultAlignment: go.Spot.Left
                        },
                            gojs(go.TextBlock, "姓名:", this.textStyle(), { row: 0, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 0, column: 1, columnSpan: 4,
                                font: "12pt Segoe UI,sans-serif",
                                minSize: new go.Size(10, 16)
                            },
                                new go.Binding("text", "name").makeTwoWay()),
                            gojs(go.TextBlock, "生辰:", this.textStyle(), { row: 1, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 1, column: 1, columnSpan: 4,
                                minSize: new go.Size(10, 14)
                            },
                                new go.Binding("text", "birthDay").makeTwoWay()),
                            gojs(go.TextBlock, "电话：", this.textStyle(), { row: 2, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 2, column: 1, columnSpan: 4,
                                minSize: new go.Size(10, 14)
                            },
                                new go.Binding("text", "phone").makeTwoWay()),
                            gojs(go.TextBlock, "现住址：", this.textStyle(), { row: 3, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 3, column: 1, columnSpan: 4,
                                font: "italic 9pt sans-serif",
                                wrap: go.TextBlock.WrapFit,
                                minSize: new go.Size(10, 14)
                            },
                                new go.Binding("text", "addressNew").makeTwoWay())
                        )
                    ),
                    {
                        doubleClick: this.nodeDoubleClick
                    },
                    {
                        click: this.nodeClick
                    }
                ));
            myDiagram.nodeTemplateMap.add("活男",
                gojs(go.Node, "Auto",
                    new go.Binding("text", "name"),
                    gojs(go.Shape, "Rectangle", {
                        name: "SHAPE", fill: "#6a76cb", stroke: 'white', strokeWidth: 3.5,
                        portId: "", fromLinkable: true, toLinkable: true, cursor: "pointer"
                    }),
                    gojs(go.Panel, "Horizontal",
                        gojs(go.Picture, {
                            name: "Picture",
                            desiredSize: new go.Size(75, 75),
                            margin: 1,
                        },
                            new go.Binding("source", "img").makeTwoWay()),
                        gojs(go.Panel, "Table", {
                            minSize: new go.Size(140, NaN),
                            maxSize: new go.Size(200, NaN),
                            margin: new go.Margin(6, 10, 0, 6),
                            defaultAlignment: go.Spot.Left
                        },
                            gojs(go.TextBlock, "姓名:", this.textStyle(), { row: 0, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 0, column: 1, columnSpan: 4,
                                font: "12pt Segoe UI,sans-serif",
                                minSize: new go.Size(10, 16)
                            },
                                new go.Binding("text", "name").makeTwoWay()),
                            gojs(go.TextBlock, "生辰:", this.textStyle(), { row: 1, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 1, column: 1, columnSpan: 4,
                                minSize: new go.Size(10, 14)
                            },
                                new go.Binding("text", "birthDay").makeTwoWay()),
                            gojs(go.TextBlock, "电话：", this.textStyle(), { row: 2, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 2, column: 1, columnSpan: 4,
                                minSize: new go.Size(10, 14)
                            },
                                new go.Binding("text", "phone").makeTwoWay()),
                            gojs(go.TextBlock, "现住址：", this.textStyle(), { row: 3, column: 0 }),
                            gojs(go.TextBlock, this.textStyle(), {
                                row: 3, column: 1, columnSpan: 4,
                                font: "italic 9pt sans-serif",
                                wrap: go.TextBlock.WrapFit,
                                minSize: new go.Size(10, 14)
                            },
                                new go.Binding("text", "addressNew").makeTwoWay())
                        )
                    ),
                    {
                        doubleClick: this.nodeDoubleClick
                    },
                    {
                        click: this.nodeClick
                    }
                ));
            myDiagram.nodeTemplateMap.add("LinkLabel",
                gojs(go.Node, { selectable: false, width: 1, height: 1, fromEndSegmentLength: 70 }));
            myDiagram.linkTemplate =
                gojs(go.Link,
                    {
                        routing: go.Link.Orthogonal, corner: 5,
                        layerName: "Background", selectable: false,
                        fromSpot: go.Spot.Bottom, toSpot: go.Spot.Top
                    },
                    gojs(go.Shape, { stroke: "#e30007", strokeWidth: 2 })
                );
            myDiagram.linkTemplateMap.add("Marriage",
                gojs(go.Link,
                    { selectable: false },
                    gojs(go.Shape, { strokeWidth: 2, stroke: "#0071c1" })
                ));
            _getSourceAll(this.parentTreeId).then(res => {
                if (res.status === 200 && res.data && res.data.length > 0) {
                    let arrays = res.data
                    for (var i = 0; i < arrays.length; i++) {
                        let img = arrays[i].userImgUrl;
                        if ((img == null || img == undefined || img == "") && arrays[i].userSex == "男") {
                            img = this.ManImg;
                        } else if ((img == null || img == undefined || img == "") && arrays[i].userSex == "女") {
                            img = this.WmanImg;
                        }
                        let arr = {
                            "key": arrays[i].userId,
                            "pId": arrays[i].userPId,
                            "mId": arrays[i].userMID,
                            "name": arrays[i].userName,
                            "pinyin": arrays[i].userPinYin,
                            "birthDay": arrays[i].userBirthDay,
                            "isDie": arrays[i].isSurvival,
                            "dieDay": arrays[i].userDieDay,
                            "sex": arrays[i].userSex,
                            "phone": arrays[i].userPhone,
                            "addressNew": arrays[i].userAddressNew,
                            "addressOld": arrays[i].userAddressOld,
                            "emil": arrays[i].userEmil,
                            "img": img,
                            "mateId": arrays[i].userMateId,
                            "note": arrays[i].userNote,
                            "love": arrays[i].userLove,
                            "pName": arrays[i].pName,
                            "mName": arrays[i].mName,
                            "mateName": arrays[i].mateName,
                            "styleNo": arrays[i].styleNo,
                            "mate": arrays[i].list,
                        };
                        this.jsonarray.push(arr);
                    }
                    this.setupDiagram(myDiagram, this.jsonarray, 1);
                } else {
                    this.isEmptyGenealogy = true;
                    if (myDiagram) {
                        myDiagram.model = new go.GraphLinksModel();
                    }
                }
            })
        },
        textStyle() {
            return { font: "9pt  Segoe UI,sans-serif", stroke: "white" };
        },
        setupDiagram(diagram, array, focusId) {
            diagram.model =
                go.GraphObject.make(go.GraphLinksModel,
                    {
                        linkLabelKeysProperty: "labelKeys",
                        nodeCategoryProperty: "styleNo",
                        copiesArrays: true,
                        nodeDataArray: array
                    });
            this.setupMarriages(diagram);
            this.setupParents(diagram);
            var node = diagram.findNodeForKey(focusId);
            if (node !== null) {
                diagram.select(node);
            }
        },
        setupMarriages(diagram) {
            var model = diagram.model;
            var nodeDataArray = model.nodeDataArray;
            for (var i = 0; i < nodeDataArray.length; i++) {
                var data = nodeDataArray[i];
                var key = data.key;
                var uxs = data.mateId;
                if (uxs !== undefined && uxs !== "" && uxs != null) {
                    uxs = uxs.toString();
                    uxs = uxs.split(",");
                    for (var j = 0; j < uxs.length; j++) {
                        var wife = uxs[j];
                        if (key === wife || wife === "" || wife == null) {
                            continue;
                        }
                        var link = this.findMarriage(diagram, key, wife);
                        if (link === null) {
                            var mlab = { styleNo: "LinkLabel" };
                            model.addNodeData(mlab);
                            var mdata = { from: key, to: wife, labelKeys: [mlab.key], category: "Marriage" };
                            model.addLinkData(mdata);
                        }
                    }
                }
            }
        },
        setupParents(diagram) {
            var model = diagram.model;
            var nodeDataArray = model.nodeDataArray;
            for (var i = 0; i < nodeDataArray.length; i++) {
                var data = nodeDataArray[i];
                var key = data.key;
                var mother = data.mId;
                var father = data.pId;
                if (mother !== undefined && father !== undefined) {
                    var link = this.findMarriage(diagram, mother, father);
                    if (link === null) {
                        continue;
                    }
                    var mdata = link.data;
                    var mlabkey = mdata.labelKeys[0];
                    var cdata = { from: mlabkey, to: key };
                    myDiagram.model.addLinkData(cdata);
                }
            }
        },
        findMarriage(diagram, a, b) {
            var nodeA = diagram.findNodeForKey(a);
            var nodeB = diagram.findNodeForKey(b);
            if (nodeA !== null && nodeB !== null) {
                var it = nodeA.findLinksBetween(nodeB);
                while (it.next()) {
                    var link = it.value;
                    if (link.data !== null && link.data.category === "Marriage")
                        return link;
                }
            }
            return null;
        },
        nodeClick(e, obj) {
            clearTimeout(this.timeId)
            this.timeId = setTimeout(() => {
                var clicked = obj.part;
                if (clicked !== null) {
                    var pData = clicked.data;
                    this.UserInfo.userId = pData.key
                    this.UserInfo.userName = pData.name
                    this.UserInfo.isSurvival = pData.isDie
                    this.UserInfo.userAddressNew = pData.addressNew
                    this.UserInfo.userAddressOld = pData.addressOld
                    this.UserInfo.userBirthDay = pData.birthDay
                    this.UserInfo.userDieDay = pData.dieDay
                    this.UserInfo.mName = pData.mName
                    this.UserInfo.pName = pData.pName
                    this.UserInfo.mateName = pData.mateName
                    this.UserInfo.userEmil = pData.emil
                    this.UserInfo.userImgUrl = pData.img
                    this.UserInfo.userLove = pData.love
                    this.UserInfo.userMID = pData.mId
                    this.UserInfo.userMateId = pData.mateId
                    this.UserInfo.userPId = pData.pId
                    this.UserInfo.userNote = pData.note
                    this.UserInfo.userPhone = pData.phone
                    this.UserInfo.userPinYin = pData.pinyin
                    this.UserInfo.userSex = pData.sex
                    this.selectUserMates = pData.mate
                    myDiagram.scale = 1;
                    myDiagram.commandHandler.scrollToPart(myDiagram.findNodeForKey(obj.part.data.key));
                    this.countAgeFun()
                    if (pData.isDie === "0") {
                        this.isDieShow = true
                    } else {
                        this.isDieShow = false
                    }
                    this.dialogUserInfo = true
                    this.panTitle = '姓名：' + pData.name + '-第' + pData.love + '代'
                } else {
                    this.dialogUserInfo = false
                }
            }, 250)
        },
        nodeDoubleClick(e, obj) {
            clearTimeout(this.timeId)
            var clicked = obj.part;
            if (clicked !== null) {
                this.dialogUserOp = true
                this.fmInfoPan = false
                this.mateInfoPan = false
                this.childrenInfoPan = false
                this.mateOptionPan = false
                this.delCHildrenPan = false
                if (clicked.data.pId == null && clicked.data.mId == null) {
                    this.fmInfoBtn = true
                } else {
                    this.fmInfoBtn = false
                }
                this.selectUserKey = clicked.data.key
                this.selectUserName = clicked.data.name
                this.selectUserSex = clicked.data.sex
                this.selectUserLove = clicked.data.love
                this.selectUserMates = clicked.data.mate
                myDiagram.scale = 1;
                myDiagram.commandHandler.scrollToPart(myDiagram.findNodeForKey(this.selectUserKey));
            }
        },
        zoomToFitFun() {
            myDiagram.commandHandler.zoomToFit();
        },
        serachByNameFun() {
            let res = myDiagram.findNodesByExample({ "name": this.serachName });
            if (res.count < 1) {
                this.$notify.warning('未查到该人员')
                return
            }
            res.each(function (node) {
                myDiagram.select(node);
                myDiagram.scale = 1;
                myDiagram.commandHandler.scrollToPart(myDiagram.findNodeForKey(node.key));
            });
        },
        getChildNodes(key) {
            for (let i = 0; i < this.jsonarray.length; i++) {
                if (key === this.jsonarray[i].pId || key === this.jsonarray[i].mId) {
                    this.selectChildren.push(this.jsonarray[i].key)
                    this.selectChildrenName.push(this.jsonarray[i].name)
                    this.getChildNodes(this.jsonarray[i].key)
                    this.getMateNodes(this.jsonarray[i].key)
                }
            }
        },
        getMateNodes(key) {
            for (let i = 0; i < this.jsonarray.length; i++) {
                if (key === this.jsonarray[i].mateId) {
                    this.selectChildren.push(this.jsonarray[i].key)
                    this.selectChildrenName.push(this.jsonarray[i].name)
                }
            }
        },
        delChildrenBtn() {
            this.fmInfoPan = false
            this.mateInfoPan = false
            this.childrenInfoPan = false
            this.mateOptionPan = false
            this.delCHildrenPan = true
            this.selectChildren = []
            this.selectChildrenName = []
            this.selectChildren.push(this.selectUserKey)
            this.selectChildrenName.push(this.selectUserName)
            if (this.selectUserMates) {
                for (let i = 0; i < this.selectUserMates.length; i++) {
                    this.selectChildren.push(this.selectUserMates[i].userId)
                    this.selectChildrenName.push(this.selectUserMates[i].userName)
                }
            }
            this.getChildNodes(this.selectUserKey);
        },
        delChildrenBtnFun() {
            this.$confirm('此操作将永久删除成员, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                _delSource(this.selectChildren).then(res => {
                    if (res.data.status === 200) {
                        this.$message.success('删除成功!');
                        this.$store.state.date = new Date().getTime()
                    }
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        countAgeFun() {
            let uDate = this.UserInfo.userBirthDay
            let dDate = this.UserInfo.userDieDay
            let date = new Date().getTime()
            if (uDate != "") {
                uDate = new Date(uDate).getTime()
                let numAge = date - uDate
                this.UserInfo.age = Math.floor(numAge / 1000 / 60 / 60 / 24 / 365);
            }
            if (dDate != "") {
                dDate = new Date(dDate).getTime()
                let numDie = date - dDate
                this.UserInfo.dieDayNum = Math.floor(numDie / 1000 / 60 / 60 / 24 / 365);
            }
        },
        addFMinfoBtn() {
            this.fmInfoPan = true
            this.mateInfoPan = false
            this.childrenInfoPan = false
            this.mateOptionPan = false
            this.delCHildrenPan = false
            this.userFatherForm = {
                userId: this.selectUserKey,
                userLove: this.selectUserLove,
                userName: '',
                userSex: '男',
                userBirthDay: '',
                isSurvival: '是',
                userDieDay: '',
                userAddressNew: '',
                userAddressOld: '',
                userNote: '',
                userPhone: '',
                userEmil: '',
                pTree: this.parentTreeId,
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
            };
            this.userMatherForm = {
                userId: this.selectUserKey,
                userLove: this.selectUserLove,
                userName: '',
                userSex: '女',
                userBirthDay: '',
                isSurvival: '是',
                userDieDay: '',
                userAddressNew: '',
                userAddressOld: '',
                userNote: '',
                userPhone: '',
                userEmil: '',
                pTree: this.parentTreeId,
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
            };
        },
        addFMinfoBtnFun() {
            let arr = []
            this.$refs.userFatherForm.validate((valid) => {
                if (valid) {
                    if (this.userFatherForm.isSurvival == '是') {
                        this.userFatherForm.isSurvival = '1'
                        this.userFatherForm.userDieDay = ''
                    } else {
                        this.userFatherForm.isSurvival = '0'
                    }
                    this.userFatherForm.userBirthDay = parseTime(this.userFatherForm.userBirthDay)
                    if (this.userFatherForm.userDieDay != '') this.userFatherForm.userDieDay = parseTime(this.userFatherForm.userDieDay)
                    this.userFatherForm.userAddressOld = (this.userFatherForm.userAddressOldProvince || '') +
                        (this.userFatherForm.userAddressOldCity || '') +
                        (this.userFatherForm.userAddressOldArea || '') +
                        (this.userFatherForm.userAddressOldDetail || '');
                    this.userFatherForm.userAddressNew = (this.userFatherForm.userAddressNewProvince || '') +
                        (this.userFatherForm.userAddressNewCity || '') +
                        (this.userFatherForm.userAddressNewArea || '') +
                        (this.userFatherForm.userAddressNewDetail || '');
                    arr.push(this.userFatherForm)
                }
            })
            this.$refs.userMatherForm.validate((valid) => {
                if (valid) {
                    if (this.userMatherForm.isSurvival == '是') {
                        this.userMatherForm.isSurvival = '1'
                        this.userMatherForm.userDieDay = ''
                    } else {
                        this.userMatherForm.isSurvival = '0'
                    }
                    this.userMatherForm.userBirthDay = parseTime(this.userMatherForm.userBirthDay)
                    if (this.userMatherForm.userDieDay != '') this.userMatherForm.userDieDay = parseTime(this.userMatherForm.userDieDay)
                    this.userMatherForm.userAddressOld = (this.userMatherForm.userAddressOldProvince || '') +
                        (this.userMatherForm.userAddressOldCity || '') +
                        (this.userMatherForm.userAddressOldArea || '') +
                        (this.userMatherForm.userAddressOldDetail || '');
                    this.userMatherForm.userAddressNew = (this.userMatherForm.userAddressNewProvince || '') +
                        (this.userMatherForm.userAddressNewCity || '') +
                        (this.userMatherForm.userAddressNewArea || '') +
                        (this.userMatherForm.userAddressNewDetail || '');
                    arr.push(this.userMatherForm)
                }
            })
            if (arr.length == 2) {
                _addParents(arr).then(res => {
                    if (res.status === 200) {
                        this.$message.success("添加成功!");
                        this.$store.state.date = new Date().getTime()
                        this.reloadGenealogy();
                        this.dialogUserOp = false;
                    }
                })
            } else {
                this.$message.error("请完善父母基础信息！");
            }
        },
        addMateInfoBtn() {
            this.fmInfoPan = false
            this.mateInfoPan = true
            this.childrenInfoPan = false
            this.mateOptionPan = false
            this.delCHildrenPan = false
            this.resetValue(this.UserInfo)
            this.UserInfo.isSurvival = '是'
            if (this.selectUserSex === '男') {
                this.UserInfo.userSex = '女'
            } else {
                this.UserInfo.userSex = '男'
            }
        },
        addMateInfoBtnFun() {
            this.$refs.mateFrom.validate((valid) => {
                if (valid) {
                    this.UserInfo.userId = this.selectUserKey
                    this.UserInfo.userBirthDay = parseTime(this.UserInfo.userBirthDay)
                    this.UserInfo.userLove = this.selectUserLove
                    if (this.UserInfo.isSurvival == '是') this.UserInfo.userDieDay = ''
                    if (this.UserInfo.userDieDay != '') this.UserInfo.userDieDay = parseTime(this.UserInfo.userDieDay)
                    this.UserInfo.userAddressOld = (this.UserInfo.userAddressOldProvince || '') +
                        (this.UserInfo.userAddressOldCity || '') +
                        (this.UserInfo.userAddressOldArea || '') +
                        (this.UserInfo.userAddressOldDetail || '');
                    this.UserInfo.userAddressNew = (this.UserInfo.userAddressNewProvince || '') +
                        (this.UserInfo.userAddressNewCity || '') +
                        (this.UserInfo.userAddressNewArea || '') +
                        (this.UserInfo.userAddressNewDetail || '');
                    this.UserInfo.type = 'addMate'
                    this.UserInfo.pTree = this.parentTreeId;
                    _addSource(this.UserInfo).then(res => {
                        if (res.status === 200) {
                            this.$message.success("添加成功!");
                            this.$store.state.date = new Date().getTime()
                            this.reloadGenealogy();
                            this.dialogUserOp = false;
                        }
                    })
                }
            });
        },
        addChildrenBtn() {
            this.fmInfoPan = false
            this.mateInfoPan = false
            this.childrenInfoPan = false
            this.mateOptionPan = true
            this.delCHildrenPan = false
            this.resetValue(this.UserInfo)
            this.UserInfo.isSurvival = '是'
        },
        addChildrenBtnFun() {
            this.$refs.childrenFrom.validate((valid) => {
                if (valid) {
                    this.UserInfo.userBirthDay = parseTime(this.UserInfo.userBirthDay)
                    this.UserInfo.userLove = this.selectUserLove
                    this.UserInfo.userPId = this.selectUserPId
                    this.UserInfo.userMID = this.selectUserMId
                    if (this.UserInfo.isSurvival == '是') this.UserInfo.userDieDay = ''
                    if (this.UserInfo.userDieDay != '') this.UserInfo.userDieDay = parseTime(this.UserInfo.userDieDay)
                    this.UserInfo.userAddressOld = (this.UserInfo.userAddressOldProvince || '') +
                        (this.UserInfo.userAddressOldCity || '') +
                        (this.UserInfo.userAddressOldArea || '') +
                        (this.UserInfo.userAddressOldDetail || '');
                    this.UserInfo.userAddressNew = (this.UserInfo.userAddressNewProvince || '') +
                        (this.UserInfo.userAddressNewCity || '') +
                        (this.UserInfo.userAddressNewArea || '') +
                        (this.UserInfo.userAddressNewDetail || '');
                    this.UserInfo.type = 'addChildren'
                    this.UserInfo.pTree = this.parentTreeId;
                    _addSource(this.UserInfo).then(res => {
                        if (res.status === 200) {
                            this.$message.success("添加成功!");
                            this.$store.state.date = new Date().getTime()
                            this.reloadGenealogy();
                            this.dialogUserOp = false;
                        }
                    })
                }
            });
        },
        resetValue(data) {
            const searchForm = data
            for (const key in searchForm) {
                if (Object.hasOwnProperty.call(searchForm, key)) {
                    searchForm[key] = this.returnNormalValue(searchForm[key])
                }
            }
        },
        returnNormalValue(value) {
            if (typeof value === 'string') {
                return ''
            }
            if (typeof value === 'number') {
                return 0
            }
            const toStrong = Object.prototype.toString
            const type = toStrong.call(value)
            if (type.includes('Date')) {
                return new Date()
            }
            if (type.includes('Object')) {
                return {}
            }
            if (type.includes('Array')) {
                return []
            }
        },
        changeRadio_F(val) {
            if (val == '是') {
                this.isDieDayDisabled_F = true
            } else {
                this.isDieDayDisabled_F = false
            }
        },
        changeRadio_M(val) {
            if (val == '是') {
                this.isDieDayDisabled_M = true
            } else {
                this.isDieDayDisabled_M = false
            }
        },
        changeRadio_Mate(val) {
            if (val == '是') {
                this.isDieDayDisabled_Mate = true
            } else {
                this.isDieDayDisabled_Mate = false
            }
        },
        changeRadio_C(val) {
            if (val == '是') {
                this.isDieDayDisabled_C = true
            } else {
                this.isDieDayDisabled_C = false
            }
        },
        selectUserMateBtn(data) {
            this.childrenInfoPan = true
            this.mateOptionPan = false
            this.panTitle = '添加  ' + this.selectUserName + '&' + data.userName + '  的子女信息'
            if (data.userSex === '男') {
                this.selectUserPId = data.userId
                this.selectUserMId = this.selectUserKey
            } else {
                this.selectUserPId = this.selectUserKey
                this.selectUserMId = data.userId
            }
            this.resetValue(this.UserInfo)
            this.UserInfo.isSurvival = '是'
        },
        openCreateFirstAncestorDialog() {
            this.UserInfo.isSurvival = '是';
            this.UserInfo.userSex = '男';
            this.dialogCreateFirst = true;
            this.UserInfo.userAddressOldCode = '';
            this.UserInfo.userAddressOldDetail = '';
            this.UserInfo.userAddressOldProvince = '';
            this.UserInfo.userAddressOldCity = '';
            this.UserInfo.userAddressOldArea = '';
            this.UserInfo.userAddressOld = '';
            this.UserInfo.userAddressNewCode = '';
            this.UserInfo.userAddressNewDetail = '';
            this.UserInfo.userAddressNewProvince = '';
            this.UserInfo.userAddressNewCity = '';
            this.UserInfo.userAddressNewArea = '';
            this.UserInfo.userAddressNew = '';
        },
        addFirstBtnFun() {
            this.$refs.childrenFrom.validate((valid) => {
                if (valid) {
                    this.UserInfo.userBirthDay = parseTime(this.UserInfo.userBirthDay)
                    this.UserInfo.userLove = '1'
                    this.UserInfo.userPId = ''
                    this.UserInfo.userMID = ''
                    if (this.UserInfo.isSurvival == '是') this.UserInfo.userDieDay = ''
                    if (this.UserInfo.userDieDay != '') this.UserInfo.userDieDay = parseTime(this.UserInfo.userDieDay)
                    this.UserInfo.userAddressOld = (this.UserInfo.userAddressOldProvince || '') +
                        (this.UserInfo.userAddressOldCity || '') +
                        (this.UserInfo.userAddressOldArea || '') +
                        (this.UserInfo.userAddressOldDetail || '');
                    this.UserInfo.userAddressNew = (this.UserInfo.userAddressNewProvince || '') +
                        (this.UserInfo.userAddressNewCity || '') +
                        (this.UserInfo.userAddressNewArea || '') +
                        (this.UserInfo.userAddressNewDetail || '');
                    this.UserInfo.type = 'addFirstUser'
                    this.UserInfo.pTree = this.parentTreeId;
                    _addSource(this.UserInfo).then(res => {
                        if (res.status === 200) {
                            this.$message.success("添加成功!");
                            this.reloadGenealogy();
                            this.dialogCreateFirst = false;
                        }
                    })
                }
            });
        },
        reloadGenealogy() {
            this.jsonarray = [];
            _getSourceAll(this.parentTreeId).then(res => {
                if (res.status === 200 && res.data && res.data.length > 0) {
                    let arrays = res.data;
                    if (arrays && arrays.length > 0) {
                        this.isEmptyGenealogy = false;
                        for (var i = 0; i < arrays.length; i++) {
                            let img = arrays[i].userImgUrl;
                            if ((img == null || img == undefined || img == "") && arrays[i].userSex == "男") {
                                img = this.ManImg;
                            } else if ((img == null || img == undefined || img == "") && arrays[i].userSex == "女") {
                                img = this.WmanImg;
                            }
                            let arr = {
                                "key": arrays[i].userId,
                                "pId": arrays[i].userPId,
                                "mId": arrays[i].userMID,
                                "name": arrays[i].userName,
                                "pinyin": arrays[i].userPinYin,
                                "birthDay": arrays[i].userBirthDay,
                                "isDie": arrays[i].isSurvival,
                                "dieDay": arrays[i].userDieDay,
                                "sex": arrays[i].userSex,
                                "phone": arrays[i].userPhone,
                                "addressNew": arrays[i].userAddressNew,
                                "addressOld": arrays[i].userAddressOld,
                                "emil": arrays[i].userEmil,
                                "img": img,
                                "mateId": arrays[i].userMateId,
                                "note": arrays[i].userNote,
                                "love": arrays[i].userLove,
                                "pName": arrays[i].pName,
                                "mName": arrays[i].mName,
                                "mateName": arrays[i].mateName,
                                "styleNo": arrays[i].styleNo,
                                "mate": arrays[i].list,
                            };
                            this.jsonarray.push(arr);
                        }
                        this.setupDiagram(myDiagram, this.jsonarray, 1);
                    }
                } else {
                    this.isEmptyGenealogy = true;
                    if (myDiagram) {
                        myDiagram.model = new go.GraphLinksModel();
                    }
                }
            });
        },
    }
}
</script>

<style lang="less" scoped>
@gold: #b69d12;
@light-gold: #d4b88c;
@brown: #8b6b4d;
@light-brown: #a78e6f;
@paper: #fdf8e7;
@border-color: #dac5a5;
@shadow: rgba(150, 120, 80, 0.2);
@dark-bg: #0f172a;
@dark-paper: #1e293b;

.genealogy-source {
    background: @paper;
    padding: 30px 20px;
    position: relative;
    font-family: "Microsoft YaHei", "宋体", serif;
}

.source-container {
    height: 80vh;
    max-width: 1600px;
    margin: 0 auto;
    background: linear-gradient(135deg, #f5efe0 0%, #f8f1e3 100%);
    border: 1px solid @border-color;
    border-radius: 12px;
    box-shadow: 0 10px 30px @shadow;
    position: relative;
    overflow: hidden;
}

.source-container::before {
    content: '';
    position: absolute;
    top: 10px;
    left: 10px;
    right: 10px;
    bottom: 10px;
    border: none;
    border-radius: 8px;
    pointer-events: none;
}


/* 顶部控制栏 */
.control-bar {
    padding: 20px 30px;
    border-bottom: 1px dashed @border-color;
}

.search-section {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 20px;
}

.search-item {
    display: flex;
    align-items: center;
    flex: 1;
}

.search-label {
    min-width: 60px;
    color: @brown;
    font-size: 14px;
}

.search-label i {
    color: @gold;
    margin-right: 5px;
}

.search-actions {
    display: flex;
    gap: 10px;
}

.search-btn,
.reset-btn,
.create-first-btn {
    min-width: 80px;
    height: 36px;
    border-radius: 18px;
    font-size: 13px;
    border: none;
    transition: all 0.3s ease;
}

.search-btn i,
.reset-btn i,
.create-first-btn i {
    margin-right: 4px;
}

.search-btn {
    background: linear-gradient(135deg, @gold, @brown);
    color: @paper;
}

.search-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(182, 157, 18, 0.3);
}

.reset-btn {
    background: #e4e4e4;
    color: #666;
}

.reset-btn:hover {
    background: #d0d0d0;
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.create-first-btn {
    background: linear-gradient(135deg, #67C23A, #529b2e);
    color: @paper;
}

.create-first-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(103, 194, 58, 0.3);
}

/* 族谱面板 */
.genealogy-panel {
    height: 600px;
    margin: 20px 30px;
    background: rgba(210, 180, 140, 0.05);
    border: none;
    border-radius: 8px;
    overflow: hidden;
    position: relative;
}

.genealogy-panel.hidden {
    display: none;
}

/* 空状态覆盖层 */
.empty-genealogy-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(253, 248, 231, 0.95);
    backdrop-filter: blur(5px);
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 12px;
    z-index: 10;
}

.empty-state-content {
    text-align: center;
    padding: 40px;
    background: @paper;
    border-radius: 20px;
    border: 1px solid @border-color;
    box-shadow: 0 20px 40px @shadow;
    max-width: 400px;
    animation: fadeInUp 0.5s ease;
}

.empty-icon {
    font-size: 80px;
    color: @gold;
    margin-bottom: 20px;
    opacity: 0.8;
}

.empty-title {
    font-size: 24px;
    color: @brown;
    margin-bottom: 10px;
    font-weight: 500;
    font-family: "华文楷体", "KaiTi", serif;
}

.empty-desc {
    font-size: 16px;
    color: @light-gold;
    margin-bottom: 30px;
    line-height: 1.6;
}

.create-first-btn-large {
    padding: 15px 40px !important;
    font-size: 18px !important;
    border-radius: 50px !important;
    background: linear-gradient(135deg, @gold, @brown) !important;
    border: none !important;
    box-shadow: 0 10px 20px rgba(182, 157, 18, 0.3) !important;
    transition: all 0.3s ease !important;
    color: @paper !important;
}

.create-first-btn-large:hover {
    transform: translateY(-3px) !important;
    box-shadow: 0 15px 30px rgba(182, 157, 18, 0.4) !important;
}

.create-first-btn-large i {
    margin-right: 8px;
    font-size: 20px;
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(20px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

/* 成员信息对话框样式 */
.genealogy-member-dialog {
    border-radius: 15px;
    overflow: hidden;
    background: @paper;
    border: 1px solid @border-color;
}

::v-deep .genealogy-member-dialog .el-dialog__header {
    background: linear-gradient(135deg, @gold, @brown);
    padding: 15px 20px;
    border-bottom: 1px solid @border-color;
}

::v-deep .genealogy-member-dialog .el-dialog__title {
    color: @paper;
    font-size: 18px;
    font-weight: 500;
    letter-spacing: 1px;
    font-family: "华文楷体", "KaiTi", serif;
}

::v-deep .genealogy-member-dialog .el-dialog__headerbtn .el-dialog__close {
    color: @paper;
}

::v-deep .genealogy-member-dialog .el-dialog__body {
    padding: 20px;
    background: @paper;
}

.member-info-container {
    display: flex;
    gap: 20px;
}

.avatar-section {
    width: 200px;
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;
    background: rgba(210, 180, 140, 0.1);
    border-radius: 10px;
}

.avatar-frame {
    width: 140px;
    height: 140px;
    border-radius: 50%;
    padding: 5px;
    background: linear-gradient(145deg, @border-color, @light-gold);
    box-shadow: 0 5px 15px @shadow;
    margin-bottom: 15px;
}

.member-avatar {
    width: 100%;
    height: 100%;
    border: 3px solid @paper;
}

.member-name {
    font-size: 20px;
    font-weight: bold;
    color: @brown;
    margin-bottom: 5px;
    font-family: "华文楷体", "KaiTi", serif;
}

.member-generation {
    font-size: 14px;
    color: @light-gold;
    background: rgba(182, 157, 18, 0.1);
    padding: 3px 15px;
    border-radius: 20px;
    border: 1px solid @border-color;
}

.info-section {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.info-grid {
    background: rgba(210, 180, 140, 0.05);
    border: 1px solid @border-color;
    border-radius: 8px;
    overflow: hidden;
}

.info-row {
    display: flex;
    border-bottom: 1px solid @border-color;
}

.info-row:last-child {
    border-bottom: none;
}

.info-row.full-row {
    display: flex;
}

.info-label {
    width: 80px;
    padding: 10px;
    background: rgba(182, 157, 18, 0.1);
    color: @brown;
    font-weight: bold;
    font-size: 14px;
    border-right: 1px solid @border-color;
}

.info-value {
    flex: 1;
    padding: 10px;
    color: @brown;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 8px;
}

.info-value i {
    color: @gold;
    font-size: 16px;
}

.note-value {
    max-height: 60px;
    overflow-y: auto;
}

/* 创建先祖对话框样式 */
.genealogy-create-dialog {
    border-radius: 15px;
    overflow: hidden;
    background: @paper;
    border: 1px solid @border-color;
}

::v-deep .genealogy-create-dialog .el-dialog__header {
    background: linear-gradient(135deg, @gold, @brown);
    padding: 15px 20px;
    border-bottom: 1px solid @border-color;
}

::v-deep .genealogy-create-dialog .el-dialog__title {
    color: @paper;
    font-size: 18px;
    font-weight: 500;
    letter-spacing: 1px;
    font-family: "华文楷体", "KaiTi", serif;
}

::v-deep .genealogy-create-dialog .el-dialog__body {
    padding: 20px;
    background: @paper;
}

.create-form-panel {
    max-height: 500px;
    overflow-y: auto;
    padding-right: 10px;
}

.dialog-actions {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid @border-color;
}

.dialog-cancel-btn,
.dialog-confirm-btn {
    min-width: 120px;
    height: 40px;
    border-radius: 20px;
    font-size: 14px;
    letter-spacing: 1px;
    border: none;
    transition: all 0.3s ease;
}

.dialog-cancel-btn {
    background: #e4e4e4;
    color: #666;
}

.dialog-cancel-btn:hover {
    background: #d0d0d0;
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.dialog-confirm-btn {
    background: linear-gradient(135deg, @gold, @brown);
    color: @paper;
}

.dialog-confirm-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 20px rgba(182, 157, 18, 0.4);
}

/* 成员操作对话框样式 */
.genealogy-operation-dialog {
    border-radius: 15px;
    overflow: hidden;
    background: @paper;
    border: 1px solid @border-color;
}

::v-deep .genealogy-operation-dialog .el-dialog__header {
    background: linear-gradient(135deg, @gold, @brown);
    padding: 15px 20px;
    border-bottom: 1px solid @border-color;
}

::v-deep .genealogy-operation-dialog .el-dialog__title {
    color: @paper;
    font-size: 18px;
    font-weight: 500;
    letter-spacing: 1px;
    font-family: "华文楷体", "KaiTi", serif;
}

::v-deep .genealogy-operation-dialog .el-dialog__body {
    padding: 0;
    background: @paper;
}

.operation-container {
    display: flex;
    height: 550px;
}

.operation-menu {
    width: 200px;
    padding: 20px 10px;
    background: rgba(210, 180, 140, 0.1);
    border-right: 1px solid @border-color;
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.menu-btn {
    width: 160px;
    height: 70px;
    margin: 0 auto;
    border-radius: 10px;
    border: 1px solid @border-color;
    background: @paper;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 5px;
    font-size: 14px;
    font-weight: 500;
    color: @brown;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 0 @border-color;
}

.menu-btn i {
    font-size: 24px;
}

.menu-primary {
    border-left: 4px solid #409EFF;
}

.menu-primary:hover {
    background: #409EFF;
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 0 #2a5c8a;
}

.menu-success {
    border-left: 4px solid #67C23A;
}

.menu-success:hover {
    background: #67C23A;
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 0 #529b2e;
}

.menu-warning {
    border-left: 4px solid #E6A23C;
}

.menu-warning:hover {
    background: #E6A23C;
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 0 #b88230;
}

.menu-danger {
    border-left: 4px solid #F56C6C;
}

.menu-danger:hover {
    background: #F56C6C;
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 0 #b3523e;
}

.operation-content {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
}

.operation-panel {
    height: 100%;
}

.panel-header {
    padding: 12px 20px;
    background: rgba(182, 157, 18, 0.1);
    color: @brown;
    font-size: 16px;
    font-weight: bold;
    border-radius: 8px;
    margin-bottom: 20px;
    text-align: center;
    border: 1px solid @border-color;
}

.panel-header.warning {
    background: rgba(245, 108, 108, 0.1);
    color: #F56C6C;
}

.panel-actions {
    text-align: center;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid @border-color;
}

.panel-confirm-btn,
.panel-delete-btn {
    min-width: 120px;
    height: 40px;
    border-radius: 20px;
    font-size: 14px;
    letter-spacing: 1px;
    border: none;
    transition: all 0.3s ease;
}

.panel-confirm-btn {
    background: linear-gradient(135deg, @gold, @brown);
    color: @paper;
}

.panel-confirm-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 20px rgba(182, 157, 18, 0.4);
}

.panel-delete-btn {
    background: linear-gradient(135deg, #F56C6C, #b3523e);
    color: white;
}

.panel-delete-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 20px rgba(245, 108, 108, 0.4);
}

/* 折叠面板样式 */
.genealogy-collapse {
    border: 1px solid @border-color;
    border-radius: 8px;
    overflow: hidden;
    margin-bottom: 20px;
}

::v-deep .genealogy-collapse .el-collapse-item__header {
    background: rgba(210, 180, 140, 0.1);
    color: @brown;
    font-weight: 500;
    padding: 0 15px;
    height: 50px;
    border-bottom: 1px solid @border-color;
    font-family: "华文楷体", "KaiTi", serif;
}

::v-deep .genealogy-collapse .el-collapse-item__content {
    padding: 20px;
    background: @paper;
}

/* 配偶选择面板 */
.mate-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
    padding: 15px;
}

.mate-btn {
    width: 100%;
    height: 70px;
    border: 1px solid @border-color;
    border-radius: 10px;
    background: @paper;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 5px;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 0 @border-color;
}

.mate-btn.male {
    border-left: 4px solid #409EFF;
    color: #409EFF;
}

.mate-btn.male:hover {
    background: #409EFF;
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 0 #2a5c8a;
}

.mate-btn.female {
    border-left: 4px solid #F56C6C;
    color: #F56C6C;
}

.mate-btn.female:hover {
    background: #F56C6C;
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 0 #b3523e;
}

.mate-btn i {
    font-size: 24px;
}

/* 删除列表 */
.delete-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    padding: 20px;
    min-height: 100px;
}

.delete-tag {
    padding: 8px 16px;
    background: rgba(245, 108, 108, 0.1);
    border: 1px solid #F56C6C;
    border-radius: 20px;
    color: #F56C6C;
    font-size: 14px;
    display: inline-flex;
    align-items: center;
    gap: 5px;
}

.delete-tag i {
    font-size: 14px;
}

/* 表单样式 */
.genealogy-dialog-form {
    max-height: 400px;
    overflow-y: auto;
    padding-right: 10px;
}

::v-deep .genealogy-dialog-form .el-form-item__label {
    color: @brown;
    font-weight: 500;
    font-size: 14px;
}

::v-deep .genealogy-dialog-form .el-input__inner,
::v-deep .genealogy-dialog-form .el-textarea__inner {
    background: @paper;
    border: 1px solid @border-color;
    border-radius: 20px;
    color: @brown;
}

::v-deep .genealogy-dialog-form .el-input__inner:focus,
::v-deep .genealogy-dialog-form .el-textarea__inner:focus {
    border-color: @gold;
    box-shadow: 0 0 0 2px rgba(182, 157, 18, 0.1);
}

::v-deep .genealogy-dialog-form .el-radio__label {
    color: @brown;
}

::v-deep .genealogy-dialog-form .el-radio.is-checked .el-radio__label {
    color: @gold;
}

::v-deep .genealogy-dialog-form .el-date-editor {
    width: 100%;
}

/* 自定义滚动条 */
::-webkit-scrollbar {
    width: 8px;
    height: 8px;
}

::-webkit-scrollbar-track {
    background: @paper;
    border-radius: 4px;
}

::-webkit-scrollbar-thumb {
    background: @light-gold;
    border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
    background: @gold;
}

/* Element UI 组件样式覆盖 */
.genealogy-input {
    width: 100%;
}

::v-deep .genealogy-input .el-input__inner {
    background: @paper;
    border: 1px solid @border-color;
    border-radius: 20px;
    height: 36px;
    line-height: 36px;
    color: @brown;
}

::v-deep .genealogy-input .el-input__inner:focus {
    border-color: @gold;
    box-shadow: 0 0 0 2px rgba(182, 157, 18, 0.1);
}

::v-deep .genealogy-input .el-input__prefix {
    left: 10px;
    color: @light-gold;
}

/* 地址级联选择器样式 */
.address-cascader {
    display: inline-block;
    width: calc(100% - 0px);

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

/* 响应式 */
@media (max-width: 1200px) {
    .member-info-container {
        flex-direction: column;
    }

    .avatar-section {
        width: 100%;
    }

    .operation-container {
        flex-direction: column;
    }

    .operation-menu {
        width: 100%;
        flex-direction: row;
        flex-wrap: wrap;
        justify-content: center;
        border-right: none;
        border-bottom: 1px solid @border-color;
    }

    .menu-btn {
        width: 140px;
    }
}

@media (max-width: 768px) {
    .search-section {
        flex-direction: column;
    }

    .search-actions {
        width: 100%;
        justify-content: center;
    }

    .info-row {
        flex-direction: column;
    }

    .info-label {
        width: 100%;
        border-right: none;
        border-bottom: 1px solid @border-color;
    }

    .mate-grid {
        grid-template-columns: 1fr;
    }
}
</style>