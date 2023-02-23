<template>
    <div class="category-blog-view">
        <!--搜索-->
        <el-row style="margin-top: 10px;margin-bottom: 10px;">
            <el-col :span="8">
                <el-input placeholder="输入需要搜素\新增的分类名称" v-model="tNameInput"></el-input>
            </el-col>
            <el-col :span="1">
                <el-button type="success" @click="searchByName">
                    <i class="el-icon-search"></i>
                    搜索
                </el-button>
            </el-col>
            <el-col :span="1" :offset="1">
                <el-button type="primary" @click="addTypeBten">
                    <i class="el-icon-plus"></i>
                    增加
                </el-button>
            </el-col>
        </el-row>

        <!--修改分类的对话框-->
        <el-dialog title="编辑分类专栏" :visible.sync="updateTypeDialogFormVisible">
            <div class="updateType" v-if="typeData != null">
                <span>编码：</span>
                <br><br>
                <span><el-input v-model="typeData.id" disabled></el-input></span>
                <br><br>
                <span>分类专栏名：</span>
                <br><br>
                <span><el-input v-model="typeData.name"></el-input></span>
                <br><br>
                <span>分类说明：</span>
                <br><br>
                <span><el-input v-model="typeData.note"></el-input></span>
                <br><br>
                <el-button type="primary" @click="handleDialogForm">确 定</el-button>
            </div>
        </el-dialog>


        <el-table v-loading="pagination.listLoading" :data="pagination.currentPageData" border stripe height="700"
            style="width: 100%">
            <el-table-column label="编码">
                <template slot-scope="scope">
                    <span style="margin-left: 10px">{{ scope.row.id }}</span>
                </template>
            </el-table-column>
            <el-table-column label="名称">
                <template slot-scope="scope">
                    <el-tag type="success">{{ scope.row.name }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="说明">
                <template slot-scope="scope">
                    <span style="margin-left: 10px">{{ scope.row.note }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button type="warning" @click="handleEdit(scope.$index, scope.row)">编辑
                    </el-button>
                    <el-button type="danger" @click="handleDelete(scope.$index, scope.row)">删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <div class="block">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :page-sizes="[5, 10, 15, 20]" layout="total, sizes, prev, pager, next, jumper"
                :total="pagination.total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
import { _typeAll, _typeByName, _addType, _updTypeById, _delTypeById } from "@/api/api.js"

export default {
    name: "SortView",
    components: {},
    data() {
        return {
            tNameInput: "",
            updateTypeDialogFormVisible: false, //编辑type对话框是否显示
            typeData: {
                id: "",
                name: "",
                note: ""
            },
            pagination: {
                list: null,
                listLoading: true,
                totalPage: 1, // 统共页数，默认为1
                currentPage: 1, //当前页数 ，默认为1
                pageSize: 10, // 每页显示数量
                currentPageData: [], //当前页显示内容
                total: 1
            }
        }
    },
    methods: {
        getCurrentPageData(currentPage, pageSize) {
            let begin = (currentPage - 1) * pageSize;
            let end = currentPage * pageSize;
            this.pagination.currentPageData = this.pagination.list.slice(
                begin,
                end
            );
        },
        handleSizeChange(val) {
            this.pagination.pageSize = val
            this.getCurrentPageData(this.pagination.currentPage, this.pagination.pageSize);

        },
        handleCurrentChange(val) {
            this.pagination.currentPage = val
            this.getCurrentPageData(this.pagination.currentPage, this.pagination.pageSize);
        },
        //获取所有分类
        getTypeAll() {
            _typeAll().then(res => {
                if (res.data.status === 200) {
                    this.pagination.list = res.data.obj
                    this.pagination.listLoading = false
                    this.pagination.total = this.pagination.list.length

                    this.pagination.totalPage = Math.ceil(this.pagination.list.length / this.pagination.pageSize);
                    this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
                    this.getCurrentPageData(this.pagination.currentPage, this.pagination.pageSize);
                }
            })
        },
        //表格操作-删除分类
        handleDelete(index, row) {
            this.$confirm('此操作将永久删除该标签, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                _delTypeById(row.id).then(res => {
                    if (res.status === 200) {
                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        });
                        this.getTypeAll();
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
            this.updateTypeDialogFormVisible = true;
            this.typeData = row;
        },
        //确认修改
        handleDialogForm() {
            _updTypeById(this.typeData).then(res => {
                if (res.data.status === 200) {
                    this.$message.success("修改成功");
                    this.updateTypeDialogFormVisible = false;
                    this.getTypeAll();
                } else {
                    this.$message.error(res.data.msg);
                }
            })
        },
        /*搜索*/
        searchByName() {
            if (this.tNameInput == '') {
                this.$message.error("请输入分类名称")
            } else {
                let tmp = decodeURI(this.tNameInput);
                _typeByName(tmp).then(res => {
                    if (res.data.status === 200) {
                        this.pagination.list = res.data.obj
                        this.pagination.listLoading = false
                        this.pagination.total = this.pagination.list.length

                        this.pagination.totalPage = Math.ceil(this.pagination.list.length / this.pagination.pageSize);
                        this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
                        this.getCurrentPageData(this.pagination.currentPage, this.pagination.pageSize);
                    } else {
                        this.$message.warning(res.data.obj);
                    }
                })
            }
        },
        //新增分类
        addTypeBten() {
            if (this.tNameInput == '') {
                this.$message.error("请输入分类名")
            } else {
                let isExist = this.pagination.list.some((item) => {
                    return item.name.toLowerCase() === this.tNameInput.toLowerCase()
                });
                if (isExist) {
                    this.$message.error("分类已存在")
                } else {
                    let postData = {
                        "tName": this.tNameInput
                    };
                    _addType(postData).then(res => {
                        if (res.data.status === 200) {
                            this.$message.success("分类创建成功");
                            this.getTypeAll();
                            this.tNameInput = '';
                        } else {
                            this.$message.error(res.data.msg);
                        }
                    })
                }
            }
        }
    },
    created() {
        this.getTypeAll();
    }
}
</script>

<style>
    .block{
        margin-top: 20px;
    }
</style>
