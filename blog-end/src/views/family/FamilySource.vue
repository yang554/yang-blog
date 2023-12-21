<template>
    <div style="height: 100%;">
        <div style="height: 7%;padding-top: 20px;">
            <el-row style="margin-top: 10px;margin-bottom: 10px;">
                <el-col :span="8">
                    <el-input placeholder="按用户名搜索" v-model="serachName" size="small"></el-input>
                </el-col>
                <el-col :span="1">
                    <el-button type="success" size="small" @click="serachByNameFun">
                        <i class="el-icon-search"></i>
                        搜索
                    </el-button>
                </el-col>
                <el-col :span="1" :offset="1">
                    <el-button type="warning" size="small" @click="zoomToFitFun">总览
                    </el-button>
                </el-col>
            </el-row>
        </div>
        <!--族谱面板-->
        <div ref="myDiagramDiv"
            style="background-color: rgb(248, 248, 248); border: 0px solid #9b9b9b; -webkit-tap-highlight-color: rgba(255, 255, 255, 0); cursor: auto;height: 90%;">
        </div>
        <div> <!--单击节点-成员详细信息面板-->
            <el-dialog width="30%" center :title=this.panTitle :visible.sync="dialogUserInfo">
                <el-avatar style="margin-left: 25%;" :size="200" :src="this.UserInfo.userImgUrl"></el-avatar>
                <el-descriptions :column="3" border>
                    <el-descriptions-item labelStyle="width: 90px">
                        <template slot="label">
                            <i class="el-icon-user"></i>
                            性别
                        </template>
                        {{ UserInfo.userSex }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-user"></i>
                            年龄
                        </template>
                        {{ UserInfo.age }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-user"></i>
                            生日
                        </template>
                        {{ UserInfo.userBirthDay }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-user"></i>
                            父亲
                        </template>
                        {{ UserInfo.pName }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-user"></i>
                            母亲
                        </template>
                        {{ UserInfo.mName }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-user"></i>
                            配偶
                        </template>
                        <div v-for="item in this.selectUserMates">{{ item.userName }}</div>
                    </el-descriptions-item>
                </el-descriptions>
                <el-descriptions :column="1" border>
                    <el-descriptions-item labelStyle="width: 90px">
                        <template slot="label">
                            <i class="el-icon-user"></i>
                            邮箱
                        </template>
                        {{ UserInfo.userEmil }}
                    </el-descriptions-item>
                    <el-descriptions-item labelStyle="width: 90px">
                        <template slot="label">
                            <i class="el-icon-mobile-phone"></i>
                            手机号
                        </template>
                        {{ UserInfo.userPhone }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-tickets"></i>
                            座右铭
                        </template>
                        {{ UserInfo.userNote }}
                    </el-descriptions-item>
                    <el-descriptions-item labelStyle="width: 90px">
                        <template slot="label">
                            <i class="el-icon-location-outline"></i>
                            现地址
                        </template>
                        {{ UserInfo.userAddressNew }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-office-building"></i>
                            户籍地
                        </template>
                        {{ UserInfo.userAddressOld }}
                    </el-descriptions-item>
                </el-descriptions>
                <el-descriptions :column="1" border v-show="this.isDieShow">
                    <el-descriptions-item labelStyle="width: 90px">
                        <template slot="label">
                            <i class="el-icon-location-outline"></i>
                            忌日
                        </template>
                        {{ UserInfo.userDieDay }}
                    </el-descriptions-item>
                    <el-descriptions-item>
                        <template slot="label">
                            <i class="el-icon-office-building"></i>
                            亡故年
                        </template>
                        {{ UserInfo.dieDayNum }}年
                    </el-descriptions-item>
                </el-descriptions>

            </el-dialog>
        </div>
        <div> <!--双击节点-添加成员信息面板-->
            <el-dialog width="50%" center :title="this.selectUserName" :visible.sync="dialogUserOp">
                <el-container>
                    <el-aside width="200px">
                        <ul style="list-style: none;padding-inline-start: 1px;">
                            <li><el-button @click="addFMinfoBtn" class="btn-style" type="primary" v-show="this.fmInfoBtn"
                                    plain>添加父母</el-button>
                            </li>
                            <li><a><el-button @click="addMateInfoBtn" class="btn-style" v-show="this.mateInfoBtn"
                                        type="danger" plain>添加配偶</el-button></a></li>
                            <li><a><el-button @click="addChildrenBtn" class="btn-style" v-show="this.childrenInfoBtn"
                                        type="success" plain>添加子女</el-button></a></li>
                            <li><a><el-button @click="delChildrenBtn" class="btn-style" v-show="this.childrenInfoBtn"
                                        type="warning" plain>删除成员</el-button></a></li>
                        </ul>
                    </el-aside>
                    <el-main style="overflow:auto;height: 500px;">
                        <el-container v-show="this.fmInfoPan">
                            <el-header class="f-header">添加父母</el-header>
                            <el-main style="overflow:auto;">
                                <el-container>
                                    <el-main style="overflow:auto;height: 370px;">
                                        <el-collapse accordion>
                                            <el-collapse-item class="f-header" title="父亲信息">
                                                <el-form ref="userFatherForm" :rules="fatherFormRules"
                                                    :model="userFatherForm" label-width="80px">
                                                    <el-form-item label="姓名" prop="userName">
                                                        <el-input v-model="userFatherForm.userName"></el-input>
                                                    </el-form-item>
                                                    <el-form-item label="生辰" prop="userBirthDay">
                                                        <el-date-picker type="date" placeholder="选择生辰"
                                                            v-model="userFatherForm.userBirthDay"
                                                            :picker-options="pickerOptions"
                                                            style="width: 100%;"></el-date-picker>
                                                    </el-form-item>
                                                    <el-form-item label="是否建在">
                                                        <el-radio-group v-model="userFatherForm.isSurvival"
                                                            @change="changeRadio_F">
                                                            <el-radio label="是"></el-radio>
                                                            <el-radio label="否"></el-radio>
                                                        </el-radio-group>
                                                    </el-form-item>
                                                    <el-form-item label="忌日">
                                                        <el-date-picker type="date" placeholder="选择忌日"
                                                            v-model="userFatherForm.userDieDay"
                                                            :disabled=this.isDieDayDisabled_F
                                                            :picker-options="pickerOptions"
                                                            style="width: 100%;"></el-date-picker>
                                                    </el-form-item>
                                                    <el-form-item label="电话" prop="userPhone">
                                                        <el-input v-model="userFatherForm.userPhone"></el-input>
                                                    </el-form-item>
                                                    <el-form-item label="邮箱" prop="UserEmil">
                                                        <el-input v-model="userFatherForm.userEmil"></el-input>
                                                    </el-form-item>
                                                    <el-form-item label="户籍地">
                                                        <el-input v-model="userFatherForm.userAddressOld"></el-input>
                                                    </el-form-item>
                                                    <el-form-item label="现居地">
                                                        <el-input v-model="userFatherForm.userAddressNew"></el-input>
                                                    </el-form-item>
                                                    <el-form-item label="座右铭">
                                                        <el-input type="textarea"
                                                            v-model="userFatherForm.userNote"></el-input>
                                                    </el-form-item>
                                                </el-form>
                                            </el-collapse-item>
                                            <el-collapse-item class="f-header" title="母亲信息">
                                                <el-form ref="userMatherForm" :rules="matherFormRules"
                                                    :model="userMatherForm" label-width="80px">
                                                    <el-form-item label="姓名" prop="userName">
                                                        <el-input v-model="userMatherForm.userName"></el-input>
                                                    </el-form-item>
                                                    <el-form-item label="生辰" prop="userBirthDay">
                                                        <el-date-picker type="date" placeholder="选择生辰"
                                                            v-model="userMatherForm.userBirthDay"
                                                            :picker-options="pickerOptions"
                                                            style="width: 100%;"></el-date-picker>
                                                    </el-form-item>
                                                    <el-form-item label="是否建在">
                                                        <el-radio-group v-model="userMatherForm.isSurvival"
                                                            @change="changeRadio_M">
                                                            <el-radio label="是"></el-radio>
                                                            <el-radio label="否"></el-radio>
                                                        </el-radio-group>
                                                    </el-form-item>
                                                    <el-form-item label="忌日">
                                                        <el-date-picker type="date" placeholder="选择忌日"
                                                            v-model="userMatherForm.userDieDay"
                                                            :disabled=this.isDieDayDisabled_M
                                                            :picker-options="pickerOptions"
                                                            style="width: 100%;"></el-date-picker>
                                                    </el-form-item>
                                                    <el-form-item label="电话" prop="userPhone">
                                                        <el-input v-model="userMatherForm.userPhone"></el-input>
                                                    </el-form-item>
                                                    <el-form-item label="邮箱" prop="UserEmil">
                                                        <el-input v-model="userMatherForm.userEmil"></el-input>
                                                    </el-form-item>
                                                    <el-form-item label="户籍地">
                                                        <el-input v-model="userMatherForm.userAddressOld"></el-input>
                                                    </el-form-item>
                                                    <el-form-item label="现居地">
                                                        <el-input v-model="userMatherForm.userAddressNew"></el-input>
                                                    </el-form-item>
                                                    <el-form-item label="座右铭">
                                                        <el-input type="textarea"
                                                            v-model="userMatherForm.userNote"></el-input>
                                                    </el-form-item>
                                                </el-form>
                                            </el-collapse-item>
                                        </el-collapse>
                                    </el-main>
                                </el-container>
                                <el-button style="margin-top: 10px;float: right;" type="primary"
                                    @click="addFMinfoBtnFun('fatherFormRules', 'userMatherForm')">确认添加</el-button>
                            </el-main>
                        </el-container>
                        <el-container v-show="this.mateInfoPan">
                            <el-header class="m-header">添加配偶</el-header>
                            <el-main style="overflow:auto;">
                                <el-container>
                                    <el-main style="overflow:auto;height: 370px;">
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
                                                <el-date-picker type="date" placeholder="选择生辰"
                                                    v-model="UserInfo.userBirthDay" :picker-options="pickerOptions"
                                                    style="width: 100%;"></el-date-picker>
                                            </el-form-item>
                                            <el-form-item label="是否建在">
                                                <el-radio-group v-model="UserInfo.isSurvival" @change="changeRadio_Mate">
                                                    <el-radio label="是"></el-radio>
                                                    <el-radio label="否"></el-radio>
                                                </el-radio-group>
                                            </el-form-item>
                                            <el-form-item label="忌日">
                                                <el-date-picker type="date" placeholder="选择忌日" v-model="UserInfo.userDieDay"
                                                    :picker-options="pickerOptions" :disabled=this.isDieDayDisabled_Mate
                                                    style="width: 100%;"></el-date-picker>
                                            </el-form-item>
                                            <el-form-item label="电话" prop="userPhone">
                                                <el-input v-model="UserInfo.userPhone"></el-input>
                                            </el-form-item>
                                            <el-form-item label="邮箱" prop="userEmil">
                                                <el-input v-model="UserInfo.userEmil"></el-input>
                                            </el-form-item>
                                            <el-form-item label="户籍地">
                                                <el-input v-model="UserInfo.userAddressOld"></el-input>
                                            </el-form-item>
                                            <el-form-item label="现居地">
                                                <el-input v-model="UserInfo.userAddressNew"></el-input>
                                            </el-form-item>
                                            <el-form-item label="座右铭">
                                                <el-input type="textarea" v-model="UserInfo.userNote"></el-input>
                                            </el-form-item>
                                        </el-form>
                                    </el-main>
                                </el-container>
                                <el-button style="margin-top: 10px;float: right;" type="primary"
                                    @click="addMateInfoBtnFun">确认添加</el-button>
                            </el-main>
                        </el-container>
                        <el-container v-show="this.childrenInfoPan">
                            <el-header class="c-header">{{ this.panTitle }}</el-header>
                            <el-main style="overflow:auto;">
                                <el-container>
                                    <el-main style="overflow:auto;height: 370px;">
                                        <el-form ref="childrenFrom" :rules="userFormRules" :model="UserInfo"
                                            label-width="80px">
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
                                                <el-date-picker type="date" placeholder="选择生辰"
                                                    v-model="UserInfo.userBirthDay" :picker-options="pickerOptions"
                                                    style="width: 100%;"></el-date-picker>
                                            </el-form-item>
                                            <el-form-item label="是否建在">
                                                <el-radio-group v-model="UserInfo.isSurvival" @change="changeRadio_C">
                                                    <el-radio label="是"></el-radio>
                                                    <el-radio label="否"></el-radio>
                                                </el-radio-group>
                                            </el-form-item>
                                            <el-form-item label="忌日">
                                                <el-date-picker type="date" placeholder="选择忌日" v-model="UserInfo.userDieDay"
                                                    :disabled=this.isDieDayDisabled_C :picker-options="pickerOptions"
                                                    style="width: 100%;"></el-date-picker>
                                            </el-form-item>
                                            <el-form-item label="电话" prop="userPhone">
                                                <el-input v-model="UserInfo.userPhone"></el-input>
                                            </el-form-item>
                                            <el-form-item label="邮箱" prop="userEmil">
                                                <el-input v-model="UserInfo.userEmil"></el-input>
                                            </el-form-item>
                                            <el-form-item label="户籍地">
                                                <el-input v-model="UserInfo.userAddressOld"></el-input>
                                            </el-form-item>
                                            <el-form-item label="现居地">
                                                <el-input v-model="UserInfo.userAddressNew"></el-input>
                                            </el-form-item>
                                            <el-form-item label="座右铭">
                                                <el-input type="textarea" v-model="UserInfo.userNote"></el-input>
                                            </el-form-item>
                                        </el-form>
                                    </el-main>
                                </el-container>
                                <el-button style="margin-top: 10px;float: right;" type="primary"
                                    @click="addChildrenBtnFun">确认添加</el-button>
                            </el-main>
                        </el-container>
                        <el-container v-show="this.mateOptionPan">
                            <el-header class="m-header">选择配偶</el-header>
                            <el-main style="overflow:auto;">
                                <div v-for="item in this.selectUserMates" style="float: left;margin:5px">
                                    <el-button v-if="item.userSex == '男'" type="primary" @click="selectUserMateBtn(item)"
                                        plain>{{ item.userName }}</el-button>
                                    <el-button v-if="item.userSex == '女'" type="danger" @click="selectUserMateBtn(item)"
                                        plain>{{ item.userName }}</el-button>
                                </div>
                            </el-main>
                        </el-container>
                        <el-container v-show="this.delCHildrenPan">
                            <el-header class="d-header">下列人员将会被移除族谱</el-header>
                            <el-main style="overflow:auto;">
                                <el-container>
                                    <el-main style="overflow:auto;height: 370px;">
                                        <div v-for="item in this.selectChildrenName" style="float: left;margin:5px">
                                            <el-button plain>{{ item }}</el-button>
                                        </div>
                                    </el-main>
                                </el-container>
                                <el-button style="margin-top: 10px;float: right;" type="warning"
                                    @click="delChildrenBtnFun">确认删除</el-button>
                            </el-main>

                        </el-container>
                    </el-main>
                </el-container>
            </el-dialog>
        </div>
    </div>
</template>

<script>
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
    data() {
        return {
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
            },
            pickerOptions: { //限制日期控件选择
                disabledDate(time) {
                    // const num = 1000 * 3600 * 24 * 35
                    // const d = Date.now() - num
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
    },
    methods: {
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
            _getSourceAll().then(res => {
                if (res.status === 200) {
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
                }
                this.setupDiagram(myDiagram, this.jsonarray, 1);
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
                for (let i = 0;i<this.selectUserMates.length; i++) {
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
        },
        //添加父母信息方法
        addFMinfoBtnFun() {
            let arr = []
            this.$refs.userFatherForm.validate((valid) => {
                if (valid) {
                    this.userFatherForm.userId = this.selectUserKey
                    if (this.userFatherForm.isSurvival == '是') {
                        this.userFatherForm.isSurvival = '1'
                        this.userFatherForm.userDieDay = ''
                    } else {
                        this.userFatherForm.isSurvival = '0'
                    }
                    this.userFatherForm.userLove = this.selectUserLove
                    this.userFatherForm.userBirthDay = parseTime(this.userFatherForm.userBirthDay)
                    if (this.userFatherForm.userDieDay != '') this.userFatherForm.userDieDay = parseTime(this.userFatherForm.userDieDay)
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
                    this.userMatherForm.UserLove = this.selectUserLove
                    this.userMatherForm.userBirthDay = parseTime(this.userMatherForm.userBirthDay)
                    if (this.userMatherForm.userDieDay != '') this.userMatherForm.userDieDay = parseTime(this.userMatherForm.userDieDay)
                    arr.push(this.userMatherForm)
                }
            })

            if (arr.length == 2) {
                _addParents(JSON.stringify(arr)).then(res => {
                    if (res.status === 200) {
                        this.$message.success("添加成功!");
                        // this.$router.go(0);
                        this.$store.state.date = new Date().getTime()
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
                    this.UserInfo.type = 'addMate'
                    _addSource(this.UserInfo).then(res => {
                        if (res.status === 200) {
                            this.$message.success("添加成功!");
                            // this.$router.go(0);
                            this.$store.state.date = new Date().getTime()
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
                    this.UserInfo.type = 'addChildren'
                    _addSource(this.UserInfo).then(res => {
                        if (res.status === 200) {
                            this.$message.success("添加成功!");
                            // this.$router.go(0);
                            this.$store.state.date = new Date().getTime()
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
        }
    }
}
</script>
<style lang="less">
.btn-style {
    padding: 20px 70px 20px 70px;
}

.el-header,
.el-footer {
    background-color: #8ebbf5;
    color: #333;
    // text-align: center;
    line-height: 60px;
}

/deep/ .f-header {
    background-color: #3698e9;
    color: #333;
    font-size: 18px;
}

.m-header {
    background-color: #ee6666;
    color: #333;
}

.d-header {
    background-color: #e7b445;
    color: #333;
}

.c-header {
    background-color: #5fd342;
    color: #333;
}

.el-main {
    background-color: #f3f3f3;
    color: #333;
    padding: 5px;
    // text-align: center;
    // line-height: 160px;
}
</style>