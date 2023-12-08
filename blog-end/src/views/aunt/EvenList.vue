<template>
    <div class="user-list-view">
        <!--搜索-->
        <el-row style="margin-top: 10px;margin-bottom: 10px;">
            <el-col :span="12">
                <el-dropdown>
                    <span class="span-txt">
                        事件类型：<i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                    <el-input disabled size="small" style="width: 80px;" placeholder="事件类型"
                        v-model="params.type"></el-input>
                    <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item @click.native="selectClickEvent('普通')">普通</el-dropdown-item>
                        <el-dropdown-item @click.native="selectClickEvent('生日')">生日</el-dropdown-item>
                        <el-dropdown-item @click.native="selectClickEvent('纪念日')">纪念日</el-dropdown-item>
                        <el-dropdown-item disabled>其他</el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown>
                <span class="span-txt">&emsp; 事件标题：</span>
                <el-input size="small" style="width: 200px;" placeholder="按事件标题模糊搜索" v-model="params.eTitle"></el-input>
                <span class="span-txt">&emsp;当事人：</span>
                <el-input size="small" style="width: 200px;" placeholder="按事件当事人搜索" v-model="params.uName"></el-input>
            </el-col>
            <el-col :span="1">
                <el-button size="small" type="success" @click="handleSearchEvent">
                    <i class="el-icon-search"></i>
                    搜索
                </el-button>
            </el-col>
            <el-col :span="1">
                <el-button size="small" @click="handleReset">
                    <i class="el-icon-refresh"></i>
                    重置
                </el-button>
            </el-col>
        </el-row>

        <el-table v-loading="pagination.loading" border :data="pagination.currentTableData" style="width: 100%">
            <el-table-column label="编码">
                <template slot-scope="scope">
                    {{ scope.row.e_id }}
                </template>
            </el-table-column>
            <el-table-column label="事件标题">
                <template slot-scope="scope">
                    {{ scope.row.e_title | tableTile }}
                </template>
            </el-table-column>
            <el-table-column label="事件类型">
                <template slot-scope="scope">
                    {{ scope.row.e_type }}
                </template>
            </el-table-column>
            <el-table-column label="当事人">
                <template slot-scope="scope">
                    {{ scope.row.e_name }}
                </template>
            </el-table-column>
            <el-table-column label="开始时间">
                <template slot-scope="scope">
                    {{ scope.row.e_startDate }}
                </template>
            </el-table-column>
            <el-table-column label="结束时间">
                <template slot-scope="scope">
                    {{ scope.row.e_endDate }}
                </template>
            </el-table-column>
            <el-table-column label="创建时间">
                <template slot-scope="scope">
                    {{ scope.row.e_createDate }}
                </template>
            </el-table-column>
            <el-table-column label="创建人">
                <template slot-scope="scope">
                    {{ scope.row.e_createName }}
                </template>
            </el-table-column>
            <el-table-column label="操作" width="210">
                <template slot-scope="scope">
                    <el-button type="warning" size="mini">预览</el-button>
                    <el-button size="mini" type="success" @click="handleEdit(scope.$index, scope.row)">编辑
                    </el-button>
                    <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除
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
import { _getUserAll, _delUserById, _getEventAll } from "@/api/api.js";
export default {
    name: "UserListView",
    data() {
        return {
            baseUrl: '/event/getEventAll?1=1',
            inputKeyWord: "",
            inputNewTypeName: "",
            postNewTypeLoading: false,
            tableData: [],
            pagination: {
                listData: null,  //总data
                currentPage: 1, //当前页
                pageSizes: [5, 10, 15, 20],
                pageSize: 10,    //每页显示条数
                totalPage: 1,		//总页数
                currentTableData: [],		//当前页data
                total: 0,  //总数量
                loading: true, 		//table加载样式是否显示
            },
            params: {
                type: '', //事件类型
                eTitle: '',  //标题
                uName: '',   //当事人
                createName: ''   //创建人
            },
            currentUser: {
                id: "",
                name: ""
            },		//当前编辑的user
        }
    },
    filters: {
        tableTile(value) {
            if (!value) return '';
            if (value.length > 10) {
                return value.slice(0, 10) + '...'
            }
            return value
        },
    },
    methods: {
        //初始化
        init() {
            _getEventAll(this.baseUrl).then(res => {
                if (res.status === 200) {
                    if (res.data.obj != '') {
                        this.pagination.listData = res.data.obj;
                        this.pagination.total = res.data.obj.length;

                        this.pagination.totalPage = Math.ceil(this.pagination.listData.length / this.pagination.pageSize);
                        this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

                        this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize);
                        this.pagination.loading = false;
                    } else {

                    }

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
        //表格操作-删除
        handleDelete(index, row) {
            this.$confirm('此操作将永久删除该标签, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                //网络请求
                _delUserById(row.id).then(res => {
                    if (res.data.status === 200) {
                        this.init();
                        this.$message.success('删除成功!');
                    }
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },

        //表格操作-编辑
        handleEdit(index, row) {
            let obj = {
                id: row.id,
                username: row.username,
                password: row.password,
                nickname: row.nickname,
                avatar: row.avatar,
                email: row.email,
                phone: row.phone,
                description: row.description,
            };
            let stringfy_url = this.$qs.stringify(obj);
            this.$router.replace("/home/user/update?" + stringfy_url)
        },

        /*搜索*/
        handleSearchEvent() {

            if (this.params.type != '') {
                this.baseUrl += "&type="
                this.baseUrl += this.params.type
            }
            if (this.params.eTitle != '') {
                this.baseUrl += "&eTitle="
                this.baseUrl += this.params.eTitle
            }
            if (this.params.uName != '') {
                this.baseUrl += "&uName="
                this.baseUrl += this.params.uName
            }
            if (this.params.createName != '') {
                this.baseUrl += "&createName="
                this.baseUrl += this.params.createName
            }
            _getEventAll(this.baseUrl).then(res => {
                if (res.data.status === 200) {
                    this.baseUrl = '/event/getEventAll?1=1'
                    this.pagination.listData = res.data.obj;
                    this.pagination.total = res.data.obj.length;

                    this.pagination.totalPage = Math.ceil(this.pagination.listData.length / this.pagination.pageSize);
                    this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

                    this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize);
                    this.pagination.loading = false;
                } else {
                    this.baseUrl = '/event/getEventAll?1=1'
                    this.$message.error(res.data.msg);
                }
            })
        },
        handleReset(){
            this.params = {
                type: '', //事件类型
                eTitle: '',  //标题
                uName: '',   //当事人
                createName: ''   //创建人
            }
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
        //下拉点击事件
        selectClickEvent(val) {
            this.params.type = val
        }
    },
    created() {
        this.init();
    }
}
</script>
  
<style  lang="less" scoped>
.span-txt {
    font-size: 14px;
    color: black;
}
</style>
  