<template>
    <div class="tag-blog-view">
        <!--搜索-->
        <el-row style="margin-top: 10px;margin-bottom: 10px;">
            <el-col :span="8">
                <el-input placeholder="输入需要搜素\新增的标签名称" v-model="inputTagName">
                </el-input>
            </el-col>
            <el-col :span="1">
                <el-button type="primary" @click="searchByName">
                    <i class="el-icon-search"></i>
                    搜索
                </el-button>
            </el-col>
            <el-col :span="1" :offset="1">
                <el-button type="success" @click="addTagBtn">
                    <i class="el-icon-plus"></i>
                    添加
                </el-button>
            </el-col>
        </el-row>

        <!--修改分类的对话框-->
        <el-dialog title="编辑Tag信息" :visible.sync="updateTagDialogFormVisible">
            <div class="updateType" v-if="editTag != null">
                编码：
                <br />
                <br />
                <el-input v-model="editTag.id" disabled></el-input>
                <br />
                <br />
                标签名：
                <br />
                <br />
                <el-input v-model="editTag.name"></el-input>
                <br />
                <br />
                选择tag颜色：
                <br />
                <br />
                <el-select v-model="editTag.color" placeholder="请选择标签颜色">
                    <el-option v-for="item in selectTagOptions" :key="item.value" :label="item.label"
                        :value="item.value">
                        <el-button size="mini" :type="item.value" circle></el-button>
                        <span style="margin-left: 20px; color: #8492a6; font-size: 13px">{{ item.label }}</span>
                    </el-option>
                </el-select>

                <br />
                <br />
                <el-button type="primary" @click="handleConfirmUpdate">确 定</el-button>
            </div>
        </el-dialog>

        <el-table v-loading="pagination.listLoading" :data="pagination.currentPageData" height="700" stripe
            style="width: 100%">
            <el-table-column label="编码">
                <template slot-scope="scope">
                    <span style="margin-left: 10px">{{ scope.row.id }}</span>
                </template>
            </el-table-column>
            <el-table-column label="名称">
                <template slot-scope="scope">
                    <el-tag :type="scope.row.color == '' ? 'success' : scope.row.color">{{ scope.row.name }}</el-tag>
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
import { _getTagAll, _getTagByName, _addTag, _updTagById, _delTagById } from "@/api/api.js"
export default {
    name: "TagView",
    components: {},
    data() {
        return {
            inputTagName: "",
            updateTagDialogFormVisible: false,  //编辑type对话框是否显示
            editTag: {
                id: "",
                name: "",
                color: "",
            },
            pagination: {
                list: null,
                listLoading: true,
                totalPage: 1, // 统共页数，默认为1
                currentPage: 1, //当前页数 ，默认为1
                pageSize: 10, // 每页显示数量
                currentPageData: [], //当前页显示内容
                total: 1    //总数量
            },
            selectTagOptions: [
                {
                    value: '',
                    label: '默认'
                },
                {
                    value: 'primary',
                    label: '主要'
                },
                {
                    value: 'success',
                    label: '成功'
                },
                {
                    value: 'info',
                    label: '信息'
                },
                {
                    value: 'warning',
                    label: '警告'
                },
                {
                    value: 'danger',
                    label: '危险'
                },
            ],
        }
    },
    methods: {
        //分页
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
        getTagALl() {
            _getTagAll().then(res => {
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

        //表格操作-删除标签
        handleDelete(index, row) {
            this.$confirm('此操作将永久删除该标签, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                _delTagById(row.id).then(res => {
                    if (res.data.status === 200) {
                        this.$message.success('删除成功!');
                        this.getTagALl();
                    } else {
                        this.$message.error(res.data.msg);
                    }
                })
            }).catch(() => {
                this.$message.info('已取消删除');
            });
        },

        //表格操作-编辑
        handleEdit(index, row) {
            this.updateTagDialogFormVisible = true;
            this.editTag = row;
        },
        //确认修改
        handleConfirmUpdate() {
            _updTagById(this.editTag).then(res => {
                if (res.data.status === 200) {
                    this.$message.success(res.data.msg);
                    this.updateTagDialogFormVisible = false;
                    this.getTagALl();
                } else {
                    this.$message.error(res.data.msg);
                }
            })
        },

        //模糊搜索
        searchByName() {
            if (this.inputTagName == '') {
                this.$message.error("请输入标签名称")
            } else {
                _getTagByName(this.inputTagName).then(res => {
                    if (res.data.status === 200) {
                        this.pagination.list = res.data.obj
                        this.pagination.listLoading = false
                        this.pagination.total = this.pagination.list.length

                        this.pagination.totalPage = Math.ceil(this.pagination.list.length / this.pagination.pageSize);
                        this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
                        this.getCurrentPageData(this.pagination.currentPage, this.pagination.pageSize);
                    } else {
                        this.$message.error(res.data.msg);
                    }
                })
            }

        },
        //添加标签
        addTagBtn() {
            if (this.inputTagName == '') {
                this.$message.error("请输入标签名称")
            } else {
                let isExist = this.pagination.list.some((item) => {
                    return item.name.toLowerCase() === this.inputTagName.toLowerCase()
                });
                if (isExist) {
                    this.$message.error("标签已存在")
                } else {
                    let postData = {
                        "tName": this.inputTagName
                    };
                    _addTag(postData).then(res => {
                        if (res.data.status === 200) {
                            this.$message.success("标签创建成功");
                            this.getTagALl();
                            this.inputTagName = '';
                        } else {
                            this.$message.error(res.data.msg);
                        }
                    })
                }

            }
        }
    },
    created() {
        this.getTagALl();
    }
}
</script>

<style>

</style>
