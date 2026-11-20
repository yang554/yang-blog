<template>
    <div class="family-container">
        <!-- 顶部控制栏 -->
        <div class="control-bar">
            <div class="search-section">
                <el-input placeholder="按用户名搜索" v-model="serachName" size="medium" class="search-input"
                    prefix-icon="el-icon-search" clearable></el-input>
                <el-button type="primary" size="medium" @click="serachByNameFun" class="action-btn">
                    <i class="el-icon-search"></i>搜索
                </el-button>
                <el-button type="info" size="medium" @click="zoomToFitFun" class="action-btn">
                    <i class="el-icon-s-marketing"></i>总览
                </el-button>
                <el-button v-if="isEmptyGenealogy" type="success" size="medium" @click="openCreateFirstAncestorDialog"
                    class="action-btn create-first-btn">
                    <i class="el-icon-user-solid"></i>创建第一位先祖
                </el-button>
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
        <el-dialog width="700px" center :title="this.panTitle" :visible.sync="dialogUserInfo" custom-class="member-dialog">
            <div class="member-info-container">
                <div class="avatar-section">
                    <el-avatar :size="160" :src="this.UserInfo.userImgUrl" class="member-avatar"></el-avatar>
                    <div class="member-name">{{ UserInfo.userName }}</div>
                    <div class="generation">第{{ UserInfo.userLove }}代</div>
                </div>

                <div class="info-section">
                    <el-descriptions :column="2" border class="info-grid">
                        <el-descriptions-item label="性别">
                            <i class="el-icon-male" v-if="UserInfo.userSex === '男'"></i>
                            <i class="el-icon-female" v-else></i>
                            {{ UserInfo.userSex }}
                        </el-descriptions-item>
                        <el-descriptions-item label="年龄">
                            <i class="el-icon-time"></i>
                            {{ UserInfo.age }}
                        </el-descriptions-item>
                        <el-descriptions-item label="生辰">
                            <i class="el-icon-date"></i>
                            {{ UserInfo.userBirthDay }}
                        </el-descriptions-item>
                        <el-descriptions-item label="父亲">
                            <i class="el-icon-user"></i>
                            {{ UserInfo.pName }}
                        </el-descriptions-item>
                        <el-descriptions-item label="母亲">
                            <i class="el-icon-user"></i>
                            {{ UserInfo.mName }}
                        </el-descriptions-item>
                        <el-descriptions-item label="配偶">
                            <i class="el-icon-user"></i>
                            <div v-for="item in this.selectUserMates">{{ item.userName }}</div>
                        </el-descriptions-item>
                    </el-descriptions>

                    <el-descriptions :column="1" border class="info-grid">
                        <el-descriptions-item label="邮箱">
                            <i class="el-icon-message"></i>
                            {{ UserInfo.userEmil }}
                        </el-descriptions-item>
                        <el-descriptions-item label="手机号">
                            <i class="el-icon-mobile-phone"></i>
                            {{ UserInfo.userPhone }}
                        </el-descriptions-item>
                        <el-descriptions-item label="座右铭">
                            <i class="el-icon-notebook-2"></i>
                            {{ UserInfo.userNote }}
                        </el-descriptions-item>
                        <el-descriptions-item label="现地址">
                            <i class="el-icon-location"></i>
                            {{ UserInfo.userAddressNew }}
                        </el-descriptions-item>
                        <el-descriptions-item label="户籍地">
                            <i class="el-icon-office-building"></i>
                            {{ UserInfo.userAddressOld }}
                        </el-descriptions-item>
                    </el-descriptions>

                    <el-descriptions :column="2" border v-show="this.isDieShow" class="info-grid">
                        <el-descriptions-item label="忌日">
                            <i class="el-icon-moon"></i>
                            {{ UserInfo.userDieDay }}
                        </el-descriptions-item>
                        <el-descriptions-item label="亡故年">
                            <i class="el-icon-watch"></i>
                            {{ UserInfo.dieDayNum }}年
                        </el-descriptions-item>
                    </el-descriptions>
                </div>
            </div>
        </el-dialog>

        <!-- 创建第一位先祖专用对话框 -->
        <el-dialog width="800px" center title="创建第一位先祖" :visible.sync="dialogCreateFirst"
            custom-class="create-first-dialog">
            <div class="first-form-panel">
                <el-main style="overflow:auto;height: 450px;">
                    <el-form ref="childrenFrom" :rules="userFormRules" :model="UserInfo" label-width="80px">
                        <el-form-item label="姓名" prop="userName">
                            <el-input v-model="UserInfo.userName"></el-input>
                        </el-form-item>
                        <el-form-item label="性别" prop="userSex">
                            <el-radio-group v-model="UserInfo.userSex">
                                <el-radio label="男"></el-radio>
                                <el-radio label="女"></el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item label="生辰" prop="userBirthDay">
                            <el-date-picker type="date" placeholder="选择生辰" v-model="UserInfo.userBirthDay"
                                :picker-options="pickerOptions" style="width: 100%;"></el-date-picker>
                        </el-form-item>
                        <el-form-item label="是否建在">
                            <el-radio-group v-model="UserInfo.isSurvival" @change="changeRadio_C">
                                <el-radio label="是"></el-radio>
                                <el-radio label="否"></el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item label="忌日">
                            <el-date-picker type="date" placeholder="选择忌日" v-model="UserInfo.userDieDay"
                                :disabled="this.isDieDayDisabled_C" :picker-options="pickerOptions"
                                style="width: 100%;"></el-date-picker>
                        </el-form-item>
                        <el-form-item label="电话" prop="userPhone">
                            <el-input v-model="UserInfo.userPhone"></el-input>
                        </el-form-item>
                        <el-form-item label="邮箱" prop="userEmil">
                            <el-input v-model="UserInfo.userEmil"></el-input>
                        </el-form-item>
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
                            <el-input type="textarea" v-model="UserInfo.userNote"></el-input>
                        </el-form-item>
                    </el-form>
                </el-main>
                <div class="form-actions">
                    <el-button type="primary" @click="addFirstBtnFun">确认添加</el-button>
                </div>
            </div>
        </el-dialog>

        <!-- 成员操作对话框 -->
        <el-dialog width="900px" center :title="this.selectUserName" :visible.sync="dialogUserOp"
            custom-class="operation-dialog">
            <div class="operation-container">
                <div class="operation-menu">
                    <el-button @click="addFMinfoBtn" class="menu-btn" type="primary" v-show="this.fmInfoBtn">
                        <i class="el-icon-user-solid"></i>添加父母
                    </el-button>
                    <el-button @click="addMateInfoBtn" class="menu-btn" type="success" v-show="this.mateInfoBtn">
                        <i class="el-icon-s-custom"></i>添加配偶
                    </el-button>
                    <el-button @click="addChildrenBtn" class="menu-btn" type="warning" v-show="this.childrenInfoBtn">
                        <i class="el-icon-children"></i>添加子女
                    </el-button>
                    <el-button @click="delChildrenBtn" class="menu-btn" type="danger" v-show="this.childrenInfoBtn">
                        <i class="el-icon-delete"></i>删除成员
                    </el-button>
                </div>

                <div class="operation-content">
                    <!-- 添加父母面板 -->
                    <div v-show="this.fmInfoPan" class="form-panel">
                        <div class="panel-header">添加父母</div>
                        <el-collapse accordion class="parent-collapse">
                            <el-collapse-item title="父亲信息" class="collapse-item">
                                <el-form ref="userFatherForm" :rules="fatherFormRules" :model="userFatherForm"
                                    label-width="80px">
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
                                        <el-date-picker type="date" placeholder="选择生辰" v-model="userFatherForm.userBirthDay"
                                            :picker-options="pickerOptions" style="width: 100%;"></el-date-picker>
                                    </el-form-item>
                                    <el-form-item label="是否建在">
                                        <el-radio-group v-model="userFatherForm.isSurvival" @change="changeRadio_F">
                                            <el-radio label="是"></el-radio>
                                            <el-radio label="否"></el-radio>
                                        </el-radio-group>
                                    </el-form-item>
                                    <el-form-item label="忌日">
                                        <el-date-picker type="date" placeholder="选择忌日" v-model="userFatherForm.userDieDay"
                                            :disabled="this.isDieDayDisabled_F" :picker-options="pickerOptions"
                                            style="width: 100%;"></el-date-picker>
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
                                        <el-input type="textarea" v-model="userFatherForm.userNote"></el-input>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                            <el-collapse-item title="母亲信息" class="collapse-item">
                                <el-form ref="userMatherForm" :rules="matherFormRules" :model="userMatherForm"
                                    label-width="80px">
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
                                        <el-date-picker type="date" placeholder="选择生辰" v-model="userMatherForm.userBirthDay"
                                            :picker-options="pickerOptions" style="width: 100%;"></el-date-picker>
                                    </el-form-item>
                                    <el-form-item label="是否建在">
                                        <el-radio-group v-model="userMatherForm.isSurvival" @change="changeRadio_M">
                                            <el-radio label="是"></el-radio>
                                            <el-radio label="否"></el-radio>
                                        </el-radio-group>
                                    </el-form-item>
                                    <el-form-item label="忌日">
                                        <el-date-picker type="date" placeholder="选择忌日" v-model="userMatherForm.userDieDay"
                                            :disabled="this.isDieDayDisabled_M" :picker-options="pickerOptions"
                                            style="width: 100%;"></el-date-picker>
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
                                        <el-input type="textarea" v-model="userMatherForm.userNote"></el-input>
                                    </el-form-item>
                                </el-form>
                            </el-collapse-item>
                        </el-collapse>
                        <div class="form-actions">
                            <el-button type="primary" @click="addFMinfoBtnFun">确认添加</el-button>
                        </div>
                    </div>

                    <!-- 添加配偶面板 -->
                    <div v-show="this.mateInfoPan" class="form-panel">
                        <div class="panel-header">添加配偶</div>
                        <el-form ref="mateFrom" :rules="userFormRules" :model="UserInfo" label-width="80px">
                            <el-form-item label="姓名" prop="userName">
                                <el-input v-model="UserInfo.userName"></el-input>
                            </el-form-item>
                            <el-form-item label="性别" prop="userSex">
                                <el-radio-group v-model="UserInfo.userSex">
                                    <el-radio label="男"></el-radio>
                                    <el-radio label="女"></el-radio>
                                </el-radio-group>
                            </el-form-item>
                            <el-form-item label="生辰" prop="userBirthDay">
                                <el-date-picker type="date" placeholder="选择生辰" v-model="UserInfo.userBirthDay"
                                    :picker-options="pickerOptions" style="width: 100%;"></el-date-picker>
                            </el-form-item>
                            <el-form-item label="是否建在">
                                <el-radio-group v-model="UserInfo.isSurvival" @change="changeRadio_Mate">
                                    <el-radio label="是"></el-radio>
                                    <el-radio label="否"></el-radio>
                                </el-radio-group>
                            </el-form-item>
                            <el-form-item label="忌日">
                                <el-date-picker type="date" placeholder="选择忌日" v-model="UserInfo.userDieDay"
                                    :disabled="this.isDieDayDisabled_Mate" :picker-options="pickerOptions"
                                    style="width: 100%;"></el-date-picker>
                            </el-form-item>
                            <el-form-item label="电话" prop="userPhone">
                                <el-input v-model="UserInfo.userPhone"></el-input>
                            </el-form-item>
                            <el-form-item label="邮箱" prop="userEmil">
                                <el-input v-model="UserInfo.userEmil"></el-input>
                            </el-form-item>
                            <el-form-item label="户籍地">
                                <address-cascader :value="UserInfo.userAddressOldCode"
                                    :detail-value="UserInfo.userAddressOldDetail" @address-change="handleOldAddressChange"
                                    @detail-change="(val) => UserInfo.userAddressOldDetail = val">
                                </address-cascader>
                            </el-form-item>
                            <el-form-item label="现居地">
                                <address-cascader :value="UserInfo.userAddressNewCode"
                                    :detail-value="UserInfo.userAddressNewDetail" @address-change="handleNewAddressChange"
                                    @detail-change="(val) => UserInfo.userAddressNewDetail = val">
                                </address-cascader>
                            </el-form-item>

                            <el-form-item label="座右铭">
                                <el-input type="textarea" v-model="UserInfo.userNote"></el-input>
                            </el-form-item>
                        </el-form>
                        <div class="form-actions">
                            <el-button type="primary" @click="addMateInfoBtnFun">确认添加</el-button>
                        </div>
                    </div>

                    <!-- 添加子女面板 -->
                    <div v-show="this.childrenInfoPan" class="form-panel">
                        <div class="panel-header">{{ this.panTitle }}</div>
                        <el-main style="overflow:auto;height: 450px;">
                            <el-form ref="childrenFrom" :rules="userFormRules" :model="UserInfo" label-width="80px">
                                <el-form-item label="姓名" prop="userName">
                                    <el-input v-model="UserInfo.userName"></el-input>
                                </el-form-item>
                                <el-form-item label="性别" prop="userSex">
                                    <el-radio-group v-model="UserInfo.userSex">
                                        <el-radio label="男"></el-radio>
                                        <el-radio label="女"></el-radio>
                                    </el-radio-group>
                                </el-form-item>
                                <el-form-item label="生辰" prop="userBirthDay">
                                    <el-date-picker type="date" placeholder="选择生辰" v-model="UserInfo.userBirthDay"
                                        :picker-options="pickerOptions" style="width: 100%;"></el-date-picker>
                                </el-form-item>
                                <el-form-item label="是否建在">
                                    <el-radio-group v-model="UserInfo.isSurvival" @change="changeRadio_C">
                                        <el-radio label="是"></el-radio>
                                        <el-radio label="否"></el-radio>
                                    </el-radio-group>
                                </el-form-item>
                                <el-form-item label="忌日">
                                    <el-date-picker type="date" placeholder="选择忌日" v-model="UserInfo.userDieDay"
                                        :disabled="this.isDieDayDisabled_C" :picker-options="pickerOptions"
                                        style="width: 100%;"></el-date-picker>
                                </el-form-item>
                                <el-form-item label="电话" prop="userPhone">
                                    <el-input v-model="UserInfo.userPhone"></el-input>
                                </el-form-item>
                                <el-form-item label="邮箱" prop="userEmil">
                                    <el-input v-model="UserInfo.userEmil"></el-input>
                                </el-form-item>
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
                                    <el-input type="textarea" v-model="UserInfo.userNote"></el-input>
                                </el-form-item>
                            </el-form>
                        </el-main>
                        <div class="form-actions">
                            <el-button type="primary" @click="addChildrenBtnFun">确认添加</el-button>
                        </div>
                    </div>

                    <!-- 配偶选择面板 -->
                    <div v-show="this.mateOptionPan" class="mate-panel">
                        <div class="panel-header">选择配偶</div>
                        <div class="mate-list">
                            <div v-for="item in this.selectUserMates" class="mate-item">
                                <el-button v-if="item.userSex == '男'" type="primary" @click="selectUserMateBtn(item)"
                                    class="mate-btn">
                                    <i class="el-icon-male"></i> {{ item.userName }}
                                </el-button>
                                <el-button v-if="item.userSex == '女'" type="danger" @click="selectUserMateBtn(item)"
                                    class="mate-btn">
                                    <i class="el-icon-female"></i> {{ item.userName }}
                                </el-button>
                            </div>
                        </div>
                    </div>

                    <!-- 删除成员面板 -->
                    <div v-show="this.delCHildrenPan" class="delete-panel">
                        <div class="panel-header">下列人员将会被移除族谱</div>
                        <div class="delete-list">
                            <el-tag v-for="item in this.selectChildrenName" type="danger" class="delete-tag">
                                <i class="el-icon-user"></i> {{ item }}
                            </el-tag>
                        </div>
                        <div class="form-actions">
                            <el-button type="danger" @click="delChildrenBtnFun">确认删除</el-button>
                        </div>
                    </div>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import { parseCodeToString, combineFullAddress } from '@/utils/addressUtil';
import AddressCascader from '@/components/AddressCascader.vue';

class GenogramLayout extends go.LayeredDigraphLayout {
    constructor() {
        super();
        this.alignOption = go.LayeredDigraphLayout.AlignAll;
        this.initializeOption = go.LayeredDigraphLayout.InitDepthFirstIn;
        this.spouseSpacing = 30;  // 配偶之间的最小空间
        this.isRouting = false;
    }
    makeNetwork(coll) {
        // 为每个父子链接生成LayoutEdges
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
    // 用于创建LayeredDigraphNetwork的内部方法，其中表示丈夫/妻子对
    // 通过与婚姻链接上的标签“节点”对应的单个LayeredDigraphVertex
    add(net, coll, nonmemberonly) {
        var multiSpousePeople = new go.Set();
        // 考虑给定集合中的所有节点
        var it = coll.iterator;
        while (it.next()) {
            var node = it.value;
            if (!(node instanceof go.Node)) continue;
            if (!node.isLayoutPositioned || !node.isVisible()) continue;
            if (nonmemberonly && node.containingGroup !== null) continue;
            // 如果是未婚节点，或者是链接标签节点，请为其创建LayoutVertex
            if (node.isLinkLabel) {
                // 获得婚姻链接
                var link = node.labeledLink;
                var spouseA = link.fromNode;
                var spouseB = link.toNode;
                // 创建表示丈夫和妻子的顶点
                var vertex = net.addNode(node);
                // 现在定义顶点大小，使其足够大以容纳两个配偶
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
        // 现在做所有链接
        it.reset();
        while (it.next()) {
            var link = it.value;
            if (!(link instanceof go.Link)) continue;
            if (!link.isLayoutPositioned || !link.isVisible()) continue;
            if (nonmemberonly && link.containingGroup !== null) continue;
            // 现在执行所有链接如果是父子链接，请为其添加布局
            if (!link.isLabeledLink) {
                var parent = net.findVertex(link.fromNode);  // 应该是标签节点
                var child = net.findVertex(link.toNode);
                if (child !== null) {  // 未婚的孩子
                    net.linkVertexes(parent, child, link);
                } else {  // 已婚的孩子
                    link.toNode.linksConnected.each(function (l) {
                        if (!l.isLabeledLink)
                            return;
                        // 找到婚姻链接，现在获取其标签节点
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
            // 查找所有间接结婚的人的集合
            var node = multiSpousePeople.first();
            var cohort = new go.Set();
            this.extendCohort(cohort, node);
            // 然后，通过使用一个公共顶点将它们连接起来
            var dummyvert = net.createVertex();
            net.addVertex(dummyvert);
            var marriages = new go.Set();
            cohort.each(function (n) {
                n.linksConnected.each(function (l) {
                    marriages.add(l);
                })
            });
            marriages.each(function (link) {
                // 查找婚姻链接的顶点（即标签节点的顶点）
                var mlab = link.labelNodes.first()
                var v = net.findVertex(mlab);
                if (v !== null) {
                    net.linkVertexes(dummyvert, v, null);
                }
            });
            // 完成这些人，现在看看是否还有其他多人结婚
            multiSpousePeople.removeAll(cohort);
        }
    }

    // 收集所有与某人间接结婚的人
    extendCohort(coll, node) {
        if (coll.has(node)) return;
        coll.add(node);
        var lay = this;
        node.linksConnected.each(function (l) {
            if (l.isLabeledLink) {  // 如果是婚姻关系，继续与配偶双方保持联系
                lay.extendCohort(coll, l.fromNode);
                lay.extendCohort(coll, l.toNode);
            }
        });
    }

    assignLayers() {
        super.assignLayers();
        const horiz = this.direction == 0.0 || this.direction == 180.0;
        // for every vertex, record the maximum vertex width or height for the vertex's layer
        const maxsizes = [];
        this.network.vertexes.each(v => {
            const lay = v.layer;
            let max = maxsizes[lay];
            if (max === undefined) max = 0;
            const sz = (horiz ? v.width : v.height);
            if (sz > max) maxsizes[lay] = sz;
        });
        // now make sure every vertex has the maximum width or height according to which layer it is in,
        // and aligned on the left (if horizontal) or the top (if vertical)
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
        // from now on, the LayeredDigraphLayout will think that the Node is bigger than it really is
        // (other than the ones that are the widest or tallest in their respective layer).
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
        //定位规则节点
        this.network.vertexes.each(function (v) {
            if (v.node !== null && !v.node.isLinkLabel) {
                v.node.position = new go.Point(v.x, v.y);
            }
        });
        // 定位每个婚姻顶点的配偶
        var layout = this;
        this.network.vertexes.each(function (v) {
            if (v.node === null) return;
            if (!v.node.isLinkLabel) return;
            var labnode = v.node;
            var lablink = labnode.labeledLink;
            lablink.invalidateRoute();
            var spouseA = lablink.fromNode;
            var spouseB = lablink.toNode;
            // 父亲在左边，母亲在右边
            if (spouseA.data.sex === "女") {
                var temp = spouseA;
                spouseA = spouseB;
                spouseB = temp;
            }
            // 查看父对象是否位于所需的一侧，以避免链接交叉
            var aParentsNode = layout.findParentsMarriageLabelNode(spouseA);
            var bParentsNode = layout.findParentsMarriageLabelNode(spouseB);
            if (aParentsNode !== null && bParentsNode !== null && aParentsNode.position.x > bParentsNode.position.x) {
                // 交换配偶
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
        // 仅将子节点放置在婚姻标签节点下
        this.network.vertexes.each(function (v) {
            if (v.node === null || v.node.linksConnected.count > 1)
                return;
            var mnode = layout.findParentsMarriageLabelNode(v.node);
            if (mnode !== null && mnode.linksConnected.count === 1) {  // 如果只有一个孩子
                var mvert = layout.network.findVertex(mnode);
                var newbnds = v.node.actualBounds.copy();
                newbnds.x = mvert.centerX - v.node.actualBounds.width / 2;
                // 查看该层的水平中点是否有任何空白
                var overlaps = layout.diagram.findObjectsIn(newbnds, function (x) { return x.part; }, function (p) { return p !== v.node; }, true);
                if (overlaps.count === 0) {
                    v.node.move(newbnds.position);
                }
            }
        });
    }
    //迭代下一代
    findParentsMarriageLabelNode(node) {
        const it = node.findNodesInto();
        while (it.next()) {
            const n = it.value;
            if (n.isLinkLabel) return n;
        }
        return null;
    }
} // end GenogramLayout class

var gojs = null
var myDiagram = null
import { _getSourceAll, _addSource, _addParents, _delSource } from "@/api/api.js";
import { validatePhone, validateEmail } from "@/utils/validate";
import { parseTime } from '@/utils/calendar';
export default {
    name: 'test',
    components: {
        AddressCascader
    },
    data() {
        return {
            parentTreeId: '',    //族谱ID
            isEmptyGenealogy: false, // 族谱是否为空标志
            dialogCreateFirst: false,  // 创建先祖对话框开关
            selectChildren: [],
            selectChildrenName: [],
            // myDiagram: null, //这个是为了将初始化的结构保存起来，可以调用go.js的方法
            panTitle: '',    //面板标题
            mateOptionPan: false,    //配偶列表
            isDieDayDisabled_F: true,  //忌日控件开关
            isDieDayDisabled_M: true,
            isDieDayDisabled_Mate: true,
            isDieDayDisabled_C: true,
            isDieShow: false,
            dialogUserInfo: false,   //成员详细面板开关
            dialogUserOp: false,  //成员操作面板开关
            fmInfoPan: false,    //父母信息面板开关
            fmInfoBtn: true,
            mateInfoPan: false,  //配偶信息面板开关
            mateInfoBtn: true,
            childrenInfoPan: false,  //子女信息面板开关
            childrenInfoBtn: true,
            delCHildrenPan: false,   //删除人员名单
            jsonarray: [],  //数据
            ManImg: '/static/defaultImg.png',
            WmanImg: '/static/defaultW.png',
            timeId: null,    //延时标识
            serachName: '',  //搜索用户
            serachNameKey: '',   //搜索用户的key
            selectUserKey: '',   //选中成员的key
            selectUserSex: '',   //选中成员的性别
            selectUserName: '',  //选中成员姓名
            selectUserLove: '',  //选中成员代数
            selectUserMates: [], //选中成员配偶
            selectUserMId: '',   //选中成员母ID
            selectUserPId: '',   //选中成员父ID
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
                pTree: '',   //族谱ID

                // 地址相关字段
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
                pTree: '',   //族谱ID

                // 地址相关字段
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
                pTree: '',   //族谱ID

                // 地址相关字段
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
            pickerOptions: { //限制日期控件选择
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
        //初始化画布
        this.init();
    },
    created() {
        if (this.$route.params.pTree != undefined) {
            this.parentTreeId = this.$route.params.pTree
        }
    },
    methods: {
        // 地址变更处理方法
        handleOldAddressChange(addressInfo) {
            this.UserInfo.userAddressOldCode = addressInfo.codeString;
            this.UserInfo.userAddressOldProvince = addressInfo.province;
            this.UserInfo.userAddressOldCity = addressInfo.city;
            this.UserInfo.userAddressOldArea = addressInfo.area;
            // 组合完整地址
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
            // 组合完整地址
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
            // 组合完整地址
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
            // 组合完整地址
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
            // 组合完整地址
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
            // 组合完整地址
            this.userMatherForm.userAddressNew = (addressInfo.province || '') +
                (addressInfo.city || '') +
                (addressInfo.area || '') +
                (this.userMatherForm.userAddressNewDetail || '');
        },

        init() {
            gojs = this.go.GraphObject.make; // 为了定义模板的简洁性
            myDiagram = gojs(go.Diagram, this.$refs.myDiagramDiv,
                {
                    initialAutoScale: go.Diagram.Uniform,
                    "undoManager.isEnabled": false,
                    layout: gojs(GenogramLayout, { direction: 90, layerSpacing: 35 }),
                    allowZoom: true
                });
            // 两个不同的节点模板，每个性别一个
            myDiagram.nodeTemplateMap.add("活女",
                gojs(go.Node, "Auto",
                    // 对于排序，将Node.text作为data.name
                    new go.Binding("text", "name"),
                    // 定义节点的背景
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
                    // 对于排序，将Node.text作为data.name
                    new go.Binding("text", "name"),
                    // 定义节点的背景
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
                    // 对于排序，将Node.text作为data.name
                    new go.Binding("text", "name"),
                    // 绑定Part.layerName以根据是否选择控制节点的层
                    // new go.Binding("layerName", "isSelected", function (sel) { return sel ? "Foreground" : ""; }).ofObject(),
                    // 定义节点的背景
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
                    // 对于排序，将Node.text作为data.name
                    new go.Binding("text", "name"),
                    // 绑定Part.layerName以根据是否选择控制节点的层
                    // new go.Binding("layerName", "isSelected", function (sel) { return sel ? "Foreground" : ""; }).ofObject(),
                    // 定义节点的背景
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
            // 每个标签节点的表示形式——婚姻链接上不显示任何内容
            myDiagram.nodeTemplateMap.add("LinkLabel",
                gojs(go.Node, { selectable: false, width: 1, height: 1, fromEndSegmentLength: 70 }));
            myDiagram.linkTemplate =  // 亲子关系
                gojs(go.Link,
                    {
                        routing: go.Link.Orthogonal, corner: 5,
                        layerName: "Background", selectable: false,
                        fromSpot: go.Spot.Bottom, toSpot: go.Spot.Top
                    },
                    gojs(go.Shape, { stroke: "#e30007", strokeWidth: 2 })
                );
            myDiagram.linkTemplateMap.add("Marriage",  // 婚姻关系
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
                    // 数据为空 - 设置空状态标志
                    this.isEmptyGenealogy = true;
                    // 清空画布或显示空状态
                    if (myDiagram) {
                        myDiagram.model = new go.GraphLinksModel(); // 清空画布
                    }

                }

            })
        },
        //字体样式
        textStyle() {
            return { font: "9pt  Segoe UI,sans-serif", stroke: "white" };
        },
        //了解所有婚姻后处理亲子关系
        setupDiagram(diagram, array, focusId) {
            diagram.model =
                go.GraphObject.make(go.GraphLinksModel,
                    { // 声明对链接标签节点的支持
                        linkLabelKeysProperty: "labelKeys",
                        // 此属性确定使用哪个模板
                        nodeCategoryProperty: "styleNo",
                        // 如果复制了节点数据对象，请复制其数据
                        copiesArrays: true,
                        // 为人员创建所有节点
                        nodeDataArray: array
                    });
            this.setupMarriages(diagram);
            this.setupParents(diagram);
            var node = diagram.findNodeForKey(focusId);
            if (node !== null) {
                diagram.select(node);
            }
        },
        //处理节点数据以确定婚姻
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
                            // 为婚姻链接添加标签节点
                            var mlab = { styleNo: "LinkLabel" };
                            model.addNodeData(mlab);
                            // 添加婚姻链接本身，也指标签节点
                            var mdata = { from: key, to: wife, labelKeys: [mlab.key], category: "Marriage" };
                            model.addLinkData(mdata);
                        }
                    }
                }
            }
        },
        //了解所有婚姻后处理亲子关系
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
        //连线
        findMarriage(diagram, a, b) {  // A和B是节点密钥
            var nodeA = diagram.findNodeForKey(a);
            var nodeB = diagram.findNodeForKey(b);
            if (nodeA !== null && nodeB !== null) {
                var it = nodeA.findLinksBetween(nodeB);
                while (it.next()) {
                    var link = it.value;
                    // 链接数据类别==“Marriage”意味着它是一种婚姻关系
                    if (link.data !== null && link.data.category === "Marriage")
                        return link;
                }
            }
            return null;
        },
        //单击节点事件
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
        //双击节点
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
        //总览全图
        zoomToFitFun() {
            myDiagram.commandHandler.zoomToFit();
        },
        //定位搜索人员
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
        //获取选中节点及字节点
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
        //获取选中节点子节点的配偶
        getMateNodes(key) {
            for (let i = 0; i < this.jsonarray.length; i++) {
                if (key === this.jsonarray[i].mateId) {
                    this.selectChildren.push(this.jsonarray[i].key)
                    this.selectChildrenName.push(this.jsonarray[i].name)
                }
            }
        },
        //删除事件按钮
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
                //网络请求
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
        //年龄计算
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
        //添加父母信息按钮
        addFMinfoBtn() {
            this.fmInfoPan = true
            this.mateInfoPan = false
            this.childrenInfoPan = false
            this.mateOptionPan = false
            this.delCHildrenPan = false

            // 重置表单
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
        //添加父母信息方法
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

                    // 组合完整地址
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

                    // 组合完整地址
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
        //添加配偶信息按钮
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
        //添加配偶信息方法
        addMateInfoBtnFun() {
            this.$refs.mateFrom.validate((valid) => {
                if (valid) {
                    this.UserInfo.userId = this.selectUserKey
                    this.UserInfo.userBirthDay = parseTime(this.UserInfo.userBirthDay)
                    this.UserInfo.userLove = this.selectUserLove
                    if (this.UserInfo.isSurvival == '是') this.UserInfo.userDieDay = ''
                    if (this.UserInfo.userDieDay != '') this.UserInfo.userDieDay = parseTime(this.UserInfo.userDieDay)

                    // 组合完整地址
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
        //添加子女信息按钮
        addChildrenBtn() {
            this.fmInfoPan = false
            this.mateInfoPan = false
            this.childrenInfoPan = false
            this.mateOptionPan = true
            this.delCHildrenPan = false

            this.resetValue(this.UserInfo)
            this.UserInfo.isSurvival = '是'
        },
        //添加子女信息方法
        addChildrenBtnFun() {
            this.$refs.childrenFrom.validate((valid) => {
                if (valid) {
                    this.UserInfo.userBirthDay = parseTime(this.UserInfo.userBirthDay)
                    this.UserInfo.userLove = this.selectUserLove
                    this.UserInfo.userPId = this.selectUserPId
                    this.UserInfo.userMID = this.selectUserMId
                    if (this.UserInfo.isSurvival == '是') this.UserInfo.userDieDay = ''
                    if (this.UserInfo.userDieDay != '') this.UserInfo.userDieDay = parseTime(this.UserInfo.userDieDay)

                    // 组合完整地址
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
        //清空对象值
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
        //选中成员配偶按钮
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

            // 重置子女表单
            this.resetValue(this.UserInfo)
            this.UserInfo.isSurvival = '是'
        },

        // 打开创建第一位先祖的对话框
        openCreateFirstAncestorDialog() {
            this.UserInfo.isSurvival = '是';
            this.UserInfo.userSex = '男';
            this.dialogCreateFirst = true;

            // 重置地址字段
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

        //添加第一位先祖的方法
        addFirstBtnFun() {
            this.$refs.childrenFrom.validate((valid) => {
                if (valid) {
                    this.UserInfo.userBirthDay = parseTime(this.UserInfo.userBirthDay)
                    this.UserInfo.userLove = '1'
                    this.UserInfo.userPId = ''
                    this.UserInfo.userMID = ''
                    if (this.UserInfo.isSurvival == '是') this.UserInfo.userDieDay = ''
                    if (this.UserInfo.userDieDay != '') this.UserInfo.userDieDay = parseTime(this.UserInfo.userDieDay)

                    // 组合完整地址
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

        // 重新加载族谱数据
        reloadGenealogy() {
            // 清空现有数据
            this.jsonarray = [];
            // 重新从数据库获取数据
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
.family-container {
    height: 85vh;
    display: flex;
    flex-direction: column;
    background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
    padding: 15px;
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    overflow: hidden;

    .control-bar {
        margin-bottom: 15px;
        padding: 12px 20px;
        background: rgba(30, 41, 59, 0.7);
        border-radius: 10px;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);

        .search-section {
            display: flex;
            align-items: center;

            .search-input {
                flex: 1;
                margin-right: 15px;
                border-radius: 30px;
                background: rgba(255, 255, 255, 0.1);
                border: 1px solid rgba(56, 189, 248, 0.2);

                /deep/ .el-input__inner {
                    background: transparent;
                    border: none;
                    color: #e2e8f0;
                    padding-left: 40px;

                    &::placeholder {
                        color: #94a3b8;
                    }
                }

                /deep/ .el-input__prefix {
                    left: 15px;
                    color: #38bdf8;
                }
            }

            .action-btn {
                border-radius: 30px;
                padding: 10px 20px;
                font-weight: 500;
                letter-spacing: 0.5px;
                transition: all 0.3s ease;

                i {
                    margin-right: 8px;
                    font-size: 16px;
                }

                &:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
                }
            }

            .create-first-btn {
                background: linear-gradient(135deg, #38bdf8 0%, #0284c7 100%) !important;
                border: none !important;
                box-shadow: 0 4px 10px rgba(56, 189, 248, 0.3) !important;
                margin-left: 10px !important;
            }

            .create-first-btn:hover {
                transform: translateY(-2px) !important;
                box-shadow: 0 6px 15px rgba(56, 189, 248, 0.4) !important;
            }
        }
    }

    .genealogy-panel {
        flex: 1;
        background: rgba(30, 41, 59, 0.5);
        border-radius: 12px;
        border: 1px solid rgba(56, 189, 248, 0.2);
        box-shadow: inset 0 0 20px rgba(0, 0, 0, 0.2);
        overflow: hidden;
        position: relative;
    }

    .empty-genealogy-overlay {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(187, 187, 187, 0.8);
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
        background: rgba(30, 41, 59, 0.7);
        border-radius: 20px;
        border: 1px solid rgba(56, 189, 248, 0.3);
        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
        max-width: 400px;
        animation: fadeInUp 0.5s ease;
    }

    .empty-icon {
        font-size: 80px;
        color: #38bdf8;
        margin-bottom: 20px;
        opacity: 0.8;
    }

    .empty-title {
        font-size: 24px;
        color: #e2e8f0;
        margin-bottom: 10px;
        font-weight: 500;
    }

    .empty-desc {
        font-size: 16px;
        color: #94a3b8;
        margin-bottom: 30px;
        line-height: 1.6;
    }

    .create-first-btn-large {
        padding: 15px 40px !important;
        font-size: 18px !important;
        border-radius: 50px !important;
        background: linear-gradient(135deg, #38bdf8 0%, #0284c7 100%) !important;
        border: none !important;
        box-shadow: 0 10px 20px rgba(56, 189, 248, 0.3) !important;
        transition: all 0.3s ease !important;
    }

    .create-first-btn-large:hover {
        transform: translateY(-3px) !important;
        box-shadow: 0 15px 30px rgba(56, 189, 248, 0.4) !important;
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

    &:before {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background:
            linear-gradient(90deg, rgba(30, 41, 59, 0.05) 1px, transparent 1px),
            linear-gradient(rgba(30, 41, 59, 0.05) 1px, transparent 1px);
        background-size: 30px 30px;
        pointer-events: none;
        z-index: 0;
    }
}

// 成员信息对话框样式
.member-dialog {
    border-radius: 15px;
    overflow: hidden;
    background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
    border: 1px solid rgba(56, 189, 248, 0.3);

    .member-info-container {
        display: flex;
        padding: 20px;
        background: rgba(15, 23, 42, 0.7);

        .avatar-section {
            width: 200px;
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 20px;
            background: rgba(30, 41, 59, 0.5);
            border-radius: 10px;
            margin-right: 20px;

            .member-avatar {
                border: 3px solid rgba(56, 189, 248, 0.5);
                box-shadow: 0 0 20px rgba(56, 189, 248, 0.3);
                margin-bottom: 15px;
            }

            .member-name {
                font-size: 20px;
                font-weight: 600;
                color: #e2e8f0;
                margin-bottom: 5px;
            }

            .generation {
                font-size: 14px;
                color: #94a3b8;
                background: rgba(56, 189, 248, 0.2);
                padding: 3px 10px;
                border-radius: 20px;
            }
        }

        .info-section {
            flex: 1;

            .info-grid {
                margin-bottom: 15px;
                border-radius: 8px;
                overflow: hidden;
                background: rgba(30, 41, 59, 0.5);

                /deep/ .el-descriptions__header {
                    padding: 10px 15px;
                    background: rgba(56, 189, 248, 0.1);

                    .el-descriptions__title {
                        color: #38bdf8;
                        font-size: 16px;
                    }
                }

                /deep/ .el-descriptions-item__label {
                    color: #94a3b8;
                    font-weight: 500;
                    background: rgba(30, 41, 59, 0.7);

                    i {
                        margin-right: 8px;
                        color: #38bdf8;
                    }
                }

                /deep/ .el-descriptions-item__content {
                    color: #e2e8f0;
                    background: rgba(15, 23, 42, 0.5);
                }
            }
        }
    }
}

::v-deep .member-dialog .el-dialog__header {
    background: rgba(30, 41, 59, 0.9);
    padding: 15px 20px;
    border-bottom: 1px solid rgba(56, 189, 248, 0.2);

    .el-dialog__title {
        color: #38bdf8;
        font-weight: 600;
        font-size: 20px;
        letter-spacing: 0.5px;
    }

    .el-dialog__headerbtn {
        top: 15px;

        .el-dialog__close {
            color: #94a3b8;
            font-size: 20px;

            &:hover {
                color: #38bdf8;
            }
        }
    }
}

::v-deep .member-dialog .el-dialog__body {
    padding: 0;
}


/* 创建先祖对话框样式 */
.create-first-dialog {
    border-radius: 20px;
    overflow: hidden;
    background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
    border: 1px solid rgba(56, 189, 248, 0.3);

    .first-form-panel {
        .form-actions {
            margin-top: 20px;
            text-align: center;
        }
    }
}

::v-deep .create-first-dialog .el-dialog__header {
    background: rgba(30, 41, 59, 0.9);
    padding: 20px 30px;
    border-bottom: 1px solid rgba(56, 189, 248, 0.2);
}

::v-deep .create-first-dialog .el-dialog__title {
    color: #38bdf8;
    font-size: 22px;
    font-weight: 600;
    letter-spacing: 1px;
}

::v-deep .create-first-dialog .el-dialog__body {
    padding: 30px;
    background: rgba(15, 23, 42, 0.7);
}

// 成员操作对话框样式
.operation-dialog {
    border-radius: 15px;
    overflow: hidden;
    background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
    border: 1px solid rgba(56, 189, 248, 0.3);

    .operation-container {
        display: flex;
        height: 550px;
        background: rgba(15, 23, 42, 0.7);

        .operation-menu {
            width: 200px;
            padding: 20px 10px;
            background: rgba(30, 41, 59, 0.5);
            border-right: 1px solid rgba(56, 189, 248, 0.2);
            display: flex;
            flex-direction: column;
            align-items: center;

            .menu-btn {
                width: 160px;
                height: 60px;
                margin: 10px 0;
                border-radius: 10px;
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                font-size: 16px;
                font-weight: 500;
                letter-spacing: 0.5px;
                transition: all 0.3s ease;

                i {
                    font-size: 24px;
                    margin-bottom: 5px;
                }

                &:hover {
                    transform: translateY(-3px);
                    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
                }
            }
        }

        .operation-content {
            flex: 1;
            padding: 20px;
            overflow-y: auto;

            .panel-header {
                padding: 12px 20px;
                background: rgba(56, 189, 248, 0.1);
                color: #38bdf8;
                font-size: 18px;
                font-weight: 600;
                border-radius: 8px;
                margin-bottom: 20px;
                text-align: center;
            }

            .form-panel {
                .form-actions {
                    text-align: right;
                    padding: 15px 0;

                    .el-button {
                        padding: 10px 30px;
                        border-radius: 30px;
                        font-weight: 500;
                        letter-spacing: 0.5px;
                    }
                }
            }

            .parent-collapse {
                border: 1px solid rgba(56, 189, 248, 0.2);
                border-radius: 8px;
                overflow: hidden;

                .collapse-item {
                    /deep/ .el-collapse-item__header {
                        background: rgba(30, 41, 59, 0.7);
                        color: #38bdf8;
                        font-weight: 500;
                        padding: 0 15px;
                        border-bottom: 1px solid rgba(56, 189, 248, 0.2);

                        i {
                            color: #38bdf8;
                        }
                    }

                    /deep/ .el-collapse-item__content {
                        padding: 20px;
                        background: rgba(15, 23, 42, 0.5);
                    }
                }
            }

            .mate-panel {
                .mate-list {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 15px;
                    padding: 15px;

                    .mate-item {
                        .mate-btn {
                            height: 60px;
                            width: 150px;
                            font-size: 16px;
                            border-radius: 10px;
                            display: flex;
                            flex-direction: column;
                            justify-content: center;
                            align-items: center;

                            i {
                                font-size: 24px;
                                margin-bottom: 5px;
                            }
                        }
                    }
                }
            }

            .delete-panel {
                .delete-list {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 10px;
                    padding: 20px;

                    .delete-tag {
                        height: 40px;
                        padding: 0 20px;
                        font-size: 16px;
                        border-radius: 20px;
                        background: rgba(255, 73, 73, 0.1);
                        border: 1px solid rgba(255, 73, 73, 0.3);

                        i {
                            margin-right: 8px;
                        }
                    }
                }

                .form-actions {
                    text-align: center;
                    padding: 20px 0;

                    .el-button {
                        padding: 12px 40px;
                        border-radius: 30px;
                        font-size: 16px;
                        font-weight: 500;
                    }
                }
            }
        }
    }
}

::v-deep .operation-dialog .el-dialog__header {
    background: rgba(30, 41, 59, 0.9);
    padding: 15px 20px;
    border-bottom: 1px solid rgba(56, 189, 248, 0.2);

    .el-dialog__title {
        color: #38bdf8;
        font-weight: 600;
        font-size: 20px;
        letter-spacing: 0.5px;
    }

    .el-dialog__headerbtn {
        top: 15px;

        .el-dialog__close {
            color: #94a3b8;
            font-size: 20px;

            &:hover {
                color: #38bdf8;
            }
        }
    }
}

::v-deep .operation-dialog .el-dialog__body {
    padding: 0;
}

// 表单元素美化
/deep/ .el-form {
    .el-form-item {
        margin-bottom: 20px;

        .el-form-item__label {
            color: #e2e8f0;
            font-weight: 500;
        }

        .el-input__inner,
        .el-textarea__inner {
            background: rgba(30, 41, 59, 0.5);
            border: 1px solid rgba(56, 189, 248, 0.2);
            color: #e2e8f0;
            border-radius: 6px;

            &:focus {
                border-color: #38bdf8;
                box-shadow: 0 0 0 2px rgba(56, 189, 248, 0.2);
            }
        }

        .el-radio {
            color: #e2e8f0;

            &.is-checked {
                .el-radio__inner {
                    border-color: #38bdf8;
                    background: #38bdf8;
                }

                .el-radio__label {
                    color: #38bdf8;
                }
            }
        }

        .el-date-editor {
            width: 100%;
        }
    }
}

// 动画效果
.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease;
}

.fade-enter,
.fade-leave-to {
    opacity: 0;
}
</style>