<template>
    <div class="user-list-view">
        <!--搜索-->
        <el-row style="margin-top: 10px;margin-bottom: 10px;">
            <el-col :span="8">
                <el-input placeholder="按用户名搜索" v-model="inputKeyWord"></el-input>
            </el-col>
            <el-col :span="1">
                <el-button type="success" @click="handleSearchByName">
                    <i class="el-icon-search"></i>
                    搜索
                </el-button>
            </el-col>
        </el-row>

        <el-table v-loading="pagination.loading" border max-height="650px" :header-cell-style="{ 'text-align': 'center' }"
            :data="pagination.currentTableData" style="width: 100%">
            <el-table-column label="头像" width="100px" align='center'>
                <template slot-scope="scope">
                    <el-image class="my-border" v-if="scope.row.userImgUrl != null" :src="scope.row.userImgUrl"></el-image>
                    <el-image class="my-border"
                        v-else-if="(scope.row.userImgUrl == null || scope.row.userImgUrl == undefined || scope.row.userImgUrl == '') && scope.row.userSex == '男'"
                        :src="ManImg"></el-image>
                    <el-image class="my-border"
                        v-else-if="(scope.row.userImgUrl == null || scope.row.userImgUrl == undefined || scope.row.userImgUrl == '') && scope.row.userSex == '女'"
                        :src="WmanImg"></el-image>
                </template>
            </el-table-column>
            <el-table-column label="姓名" width="100px" align='center'>
                <template slot-scope="scope">
                    {{ scope.row.userName }}
                </template>
            </el-table-column>
            <el-table-column label="性别" width="50px" align='center'>
                <template slot-scope="scope">
                    {{ scope.row.userSex }}
                </template>
            </el-table-column>
            <el-table-column label="生辰" width="120px" align='center'>
                <template slot-scope="scope">
                    {{ scope.row.userBirthDay }}
                </template>
            </el-table-column>
            <el-table-column label="邮箱" width="200px">
                <template slot-scope="scope">
                    {{ scope.row.userEmil }}
                </template>
            </el-table-column>
            <el-table-column label="电话" width="120px" align='center'>
                <template slot-scope="scope">
                    {{ scope.row.userPhone }}
                </template>
            </el-table-column>
            <el-table-column label="父亲" width="100px" align='center'>
                <template slot-scope="scope">
                    {{ scope.row.pName }}
                </template>
            </el-table-column>
            <el-table-column label="母亲" width="100px" align='center'>
                <template slot-scope="scope">
                    {{ scope.row.mName }}
                </template>
            </el-table-column>
            <el-table-column label="配偶" width="100px" align='center'>
                <template slot-scope="scope">
                    <div v-for=" (item,index) in scope.row.list" :key="index">{{ item.userName }}</div>

                </template>
            </el-table-column>
            <el-table-column label="个人签名">
                <template slot-scope="scope">
                    {{ scope.row.userNote | tableTile }}
                </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align='center'>
                <template slot-scope="scope">
                    <el-button size="mini" type="success" @click="handleEdit(scope.$index, scope.row)">查看\编辑
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页 -->
        <div style="margin-top: 20px;" class="my-border-padding">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="pagination.currentPage" :page-sizes="pagination.pageSizes" :page-size="pagination.pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="pagination.total">
            </el-pagination>
        </div>
    </div>
</template>
  
<script>
import { _getSourceAll, _delSource } from "@/api/api.js";
export default {
    name: "FamilyListView",
    data() {
        return {
            ManImg: '/static/defaultImg.png',
            WmanImg: '/static/defaultW.png',
            inputKeyWord: "",
            inputNewTypeName: "",
            postNewTypeLoading: false,
            tableData: [],
            pagination: {
                listData: null,  //总data
                currentPage: 1, //当前页
                pageSizes: [5, 10, 15, 20],
                pageSize: 5,    //每页显示条数
                totalPage: 1,		//总页数
                currentTableData: [],		//当前页data
                total: 0,  //总数量
                loading: true, 		//table加载样式是否显示
            },
            ManImg: '/static/defaultImg.png',
            WmanImg: '/static/defaultW.png',
            currentUser: {
                id: "",
                name: ""
            },		//当前编辑的user
        }
    },
    filters: {
        tableTile(value) {
            if (!value) return '';
            if (value.length > 30) {
                return value.slice(0, 30) + '...'
            }
            return value
        },
    },
    methods: {
        //初始化
        init() {
            _getSourceAll().then(res => {
                if (res.status === 200) {
                    this.pagination.listData = res.data;
                    this.pagination.total = res.data.length;

                    this.pagination.totalPage = Math.ceil(this.pagination.listData.length / this.pagination.pageSize);
                    this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

                    this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize);
                    this.pagination.loading = false;

                }
            })
        },
        //分页
        getCurrentPageData(listData, currentPage, pageSize) {
            let begin = (currentPage - 1) * pageSize;
            let end = currentPage * pageSize;
            this.pagination.currentTableData = listData.slice(
                begin,
                end
            );
        },
        //表格操作-编辑
        handleEdit(index, row) {

            let obj = {
                "key": row.userId,
                "pId": row.userPId,
                "mId": row.userMID,
                "name": row.userName,
                "pinyin": row.userPinYin,
                "birthDay": row.userBirthDay,
                "isDie": row.isSurvival,
                "dieDay": row.userDieDay,
                "sex": row.userSex,
                "phone": row.userPhone,
                "addressNew": row.userAddressNew,
                "addressOld": row.userAddressOld,
                "emil": row.userEmil,
                "img": row.userImgUrl,
                "mateId": row.userMateId,
                "note": row.userNote,
                "love": row.userLove,
                "pName": row.pName,
                "mName": row.mName,
                "mateName": row.mateName,
                "styleNo": row.styleNo,
                "mate": row.list,
            };
            if((row.userImgUrl == '' || row.userImgUrl == null) && row.userSex == '男'){
                obj.img = this.ManImg
            }else if ((row.userImgUrl == '' || row.userImgUrl == null) && row.userSex == '女'){
                obj.img = this.WmanImg
            }
            this.$router.push({
                name: 'FamilyEditRank',
                params: {
                    jsonParam: JSON.stringify(obj)
                }
            })
        },

        /*搜索*/
        handleSearchByName() {
            this.$getRequest("/source/getSourceByName?s_name=" + this.inputKeyWord.trim()).then(res => {
                if (res.status === 200) {
                    this.pagination.listData = res.data;
                    this.pagination.total = res.data.length;

                    this.pagination.totalPage = Math.ceil(this.pagination.listData.length / this.pagination.pageSize);
                    this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

                    this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize);
                    this.pagination.loading = false;
                } else {
                    this.$message.error("查询异常");
                }
            })
        },
        /*分页事件*/
        handleSizeChange(val) {
            this.pagination.pageSize = val;
            this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize)
        },
        handleCurrentChange(val) {
            this.pagination.currentPage = val
            this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize)
        },
    },
    created() {
        this.init();
    }
}
</script>
  
<style scoped></style>
  