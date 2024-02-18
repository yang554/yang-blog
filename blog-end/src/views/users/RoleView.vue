<template>
    <div class="user-list-view">
        <!--搜索-->
        <el-row style="margin-top: 10px;margin-bottom: 10px;">
            <el-col :span="8">
                <el-input placeholder="权限" v-model="inputKeyWord"></el-input>
            </el-col>
            <el-col :span="1">
                <el-button type="success" @click="handleSearchByName">
                    <i class="el-icon-search"></i>
                    搜索
                </el-button>
            </el-col>
            <el-col :span="1" :offset="1">
                <el-button type="warning" @click="addRoleBtn">
                    <i class="el-icon-plus"></i>新增
                </el-button>
            </el-col>
        </el-row>

        <el-table v-loading="pagination.loading" border :data="pagination.currentTableData"
            :header-cell-style="{ 'text-align': 'center' }" max-height="650px" style="width: 50%">
            <el-table-column label="权限" width="250px" align='center'>
                <template slot-scope="scope">
                    <span style="margin-left: 10px">{{ scope.row.name }}</span>
                </template>
            </el-table-column>
            <el-table-column width="250px" label="说明" align='center'>
                <template slot-scope="scope">
                    <span style="margin-left: 10px">{{ scope.row.nameZh }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" align='center'>
                <template slot-scope="scope">
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
        <!--dialog  -->
        <el-dialog title="编辑权限" :visible.sync="dialogFormVisible"  width="30%">
            <el-form v-model="currentRole">
                <el-form-item label="权限" label-width="120px">
                    <el-input v-model="currentRole.name" autocomplete="off" style="width: 80%;">{{
                        currentRole.name }}</el-input>
                </el-form-item>
                <el-form-item label="说明" label-width="120px">
                    <el-input v-model="currentRole.nameZH" autocomplete="off" style="width: 80%;">{{
                        currentRole.nameZH }}</el-input>
                </el-form-item>
                <el-form-item label-width="120px">
                    
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleEditAunt">保 存</el-button>
           
                </el-form-item>
                
            </el-form>
            
        </el-dialog>
    </div>
</template>
  
<script>
import { _getRoleAll, _addRole, _updRole, _delRole } from "@/api/api.js";
export default {
    name: "UserListView",
    data() {
        return {
            dialogFormVisible: false,
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

            currentRole: {
                id: "",
                name: "",
                nameZH:""
            },
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
            _getRoleAll().then(res => {
                if (res.status === 200) {
                    this.pagination.listData = res.data.obj;
                    this.pagination.total = res.data.obj.length;


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
        //表格操作-删除
        handleDelete(index, row) {
            this.$confirm('此操作将永久删除该标签, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                //网络请求
                _delRole(row.id).then(res => {
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
            this.currentRole.id = row.id
            this.currentRole.name = row.name
            this.currentRole.nameZH = row.nameZh
            this.dialogFormVisible = true
        },
        // 保存当前记录
        handleEditAunt() {
            _updRole(this.currentRole).then(res => {
                this.$message.success("编辑成功!");
                this.dialogFormVisible = false
            })
            this.$store.state.date = new Date().getTime()
        },
        // 新增按钮事件
        addRoleBtn() {
            if (this.inputKeyWord == '') {
                this.$message.error("请输入权限代码")
            } else {
                let isExist = this.pagination.listData.some((item) => {
                    return item.name.toLowerCase() === this.inputKeyWord.toLowerCase()
                });
                if (isExist) {
                    this.$message.error("该权限已存在")
                } else {
                    let postData = {
                        "roleName": this.inputKeyWord
                    };
                    _addRole(postData).then(res => {
                        if (res.data.status === 200) {
                            this.$message.success("创建成功");
                            this.init();
                            this.inputKeyWord = '';
                        } else {
                            this.$message.error(res.data.msg);
                        }
                    })
                }

            }
            this.$store.state.date = new Date().getTime()
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
  