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
            <el-col :span="1" :offset="1">
                <el-button type="warning" @click="$router.push('/home/user/add')">
                    <i class="el-icon-plus"></i>增加
                </el-button>
            </el-col>
        </el-row>

        <el-table v-loading="pagination.loading" border :data="pagination.currentTableData"  :header-cell-style="{'text-align':'center'}"  max-height="650px" style="width: 100%">
            <el-table-column label="编码" width="150px" align='center'>
                <template slot-scope="scope" >
                    <span style="margin-left: 10px">{{ scope.row.id }}</span>
                </template>
            </el-table-column>
            <el-table-column width="100px" label="头像" align='center'>
                <template slot-scope="scope">
                    <el-image class="my-border"  :src="scope.row.avatar"
                        fit="contain"></el-image>
                </template>
            </el-table-column>
            <el-table-column label="用户名" width="100px" align='center'>
                <template slot-scope="scope" style="">
                    {{ scope.row.username }}
                </template>
            </el-table-column>
            <el-table-column label="昵称">
                <template slot-scope="scope">
                    {{ scope.row.nickname }}
                </template>
            </el-table-column>
            <el-table-column label="邮箱" width="200px">
                <template slot-scope="scope">
                    {{ scope.row.email }}
                </template>
            </el-table-column>
            <el-table-column label="电话" width="120px" align='center'>
                <template slot-scope="scope">
                    {{ scope.row.phone }}
                </template>
            </el-table-column>
            <el-table-column label="个人签名">
                <template slot-scope="scope">
                    {{ scope.row.description | tableTile }}
                </template>
            </el-table-column>
            <el-table-column label="操作" width="250" align='center'>
                <template slot-scope="scope">
                    <el-button size="mini" type="success" @click="handleEdit(scope.$index, scope.row)">编辑
                    </el-button>
                    <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除
                    </el-button>
                    <el-button size="mini" type="warning" @click="handleRest(scope.$index, scope.row)">重置密码
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
import { _getUserAll, _delUserById, _resetUserPwd } from "@/api/api.js";
export default {
    name: "UserListView",
    data() {
        return {
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
            _getUserAll().then(res => {
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
        //表格操作-重置密码
        handleRest(index, row){
            let form = {
                id: row.id,
                pwd:'123456'
            }
            _resetUserPwd(form).then(res => {
                if(res.data.status === 200){
                    this.$message.success('重置成功!初始密码为：123456');
                }else{
                    this.$message.success(res.data.msg);
                }
            })

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
        handleSearchByName() {
            this.$getRequest("/user/selectUserByName?name=" + this.inputKeyWord.trim()).then(res => {
                if (res.data.status === 200) {
                    this.pagination.listData = res.data.obj;
                    this.pagination.total = res.data.length;


                    this.pagination.totalPage = Math.ceil(this.pagination.listData.length / this.pagination.pageSize);
                    this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

                    this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize);
                    this.pagination.loading = false;
                } else {
                    this.$message.error(res.data.msg);
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
  