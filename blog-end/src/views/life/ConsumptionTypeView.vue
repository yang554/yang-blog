<template>
    <div class="user-list-view">
        <!-- 搜索区域 -->
        <div class="search-container" shadow="never">
            <el-row :gutter="20">
                <el-col :span="6">
                    <div class="search-item">
                        <span class="search-label">流水类型：</span>
                        <el-input placeholder="输入需要搜素\新增的消费类型" v-model="inputTagName" size="small"></el-input>
                    </div>
                </el-col>
                <!-- 操作按钮 -->
                <el-col :span="3" class="action-buttons">
                    <el-button type="primary" @click="searchByName" size="small">
                        <i class="el-icon-search"></i>
                        搜索
                    </el-button>
                    <el-button type="success" @click="addTagBtn" size="small">
                        <i class="el-icon-plus"></i>
                        添加
                    </el-button>
                </el-col>
            </el-row>
        </div>

        <!--修改分类的对话框-->
        <el-dialog title="编辑Tag信息" :visible.sync="updateTagDialogFormVisible">
            <div class="updateType" v-if="editTag != null">
                编码：
                <br />
                <br />
                <el-input v-model="editTag.id" disabled></el-input>
                <br />
                <br />
                消费类型：
                <br />
                <br />
                <el-input v-model="editTag.name"></el-input>
                <br />
                <br />
                关键词（多个关键词用、号分割开）：
                <br />
                <br />
                <el-input v-model="editTag.keywords"></el-input>
                <br />
                <br />
                选择tag颜色：
                <br />
                <br />
                <el-select v-model="editTag.color" placeholder="请选择标签颜色">
                    <el-option v-for="item in selectTagOptions" :key="item.value" :label="item.label" :value="item.value">
                        <el-button size="mini" :type="item.value" circle></el-button>
                        <span style="margin-left: 20px; color: #8492a6; font-size: 13px">{{ item.label }}</span>
                    </el-option>
                </el-select>

                <br />
                <br />
                <el-button type="primary" @click="handleConfirmUpdate">确 定</el-button>
            </div>
        </el-dialog>

        <el-table v-loading="pagination.listLoading" :data="pagination.currentPageData" stripe :max-height="screenHeight"
            :header-cell-style="{
                    background: '#f0f7ff',
                    'text-align': 'center',
                    color: '#333',
                    fontWeight: 'bold'
                }" border style="width: 100%">
            <el-table-column label="编码" width="150" align="center">
                <template slot-scope="scope">
                    <span style="margin-left: 10px">{{ scope.row.id }}</span>
                </template>
            </el-table-column>
            <el-table-column label="流水类型" width="150" align="center">
                <template slot-scope="scope">
                    <el-tag :type="scope.row.color == '' ? 'success' : scope.row.color">{{ scope.row.name }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="关键词" align="center">
                <template slot-scope="scope">
                    <span style="margin-left: 10px">{{ scope.row.keywords | tableKeys }}</span>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center">
                <template slot-scope="scope">
                    <el-button type="warning" @click="handleEdit(scope.$index, scope.row)" size="mini">编辑
                    </el-button>
                    <el-button type="danger" @click="handleDelete(scope.$index, scope.row)" size="mini">删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页 -->
        <div class="pagination-container">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :page-sizes="[5, 10, 15, 20]" layout="total, sizes, prev, pager, next, jumper" :total="pagination.total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
import { _getConsumptionTypeAll, _getConsumptionTypeByName, _addConsumptionType, _updConsumptionTypeById, _delConsumptionType } from "@/api/api.js"
export default {
    name: "ConsumptionTypeView",
    components: {},
    data() {
        return {
            screenHeight: 950,
            inputTagName: "",
            updateTagDialogFormVisible: false,  //编辑type对话框是否显示
            editTag: {
                id: "",
                name: "",
                keywords: "",
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
                    label: '重要'
                },
            ],
        }
    },
    filters: {
        tableKeys(value) {
            if (!value) return "";
            if (value.length > 60) {
                return value.slice(0, 60) + "...";
            }
            return value;
        }
    },
    mounted() {
        this.updateScreenHeight(); // 组件挂载时获取屏幕高度
        window.addEventListener("resize", this.updateScreenHeight); // 监听窗口大小变化
    },
    beforeDestroy() {
        // 移除监听
        window.removeEventListener("resize", this.updateScreenHeight);
    },

    methods: {
        updateScreenHeight() {
            this.screenHeight = window.innerHeight - 300; // 更新屏幕高度
        },
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
            _getConsumptionTypeAll().then(res => {
                if (res.data.status === 200) {
                    this.pagination.list = res.data.data
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
            this.$confirm('此操作将永久删除该消费类型, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                _delConsumptionType(row.id).then(res => {
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
            _updConsumptionTypeById(this.editTag).then(res => {
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
                this.getTagALl()
            } else {
                _getConsumptionTypeByName(this.inputTagName).then(res => {
                    if (res.data.status === 200) {
                        this.pagination.list = res.data.data
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
                    _addConsumptionType(postData).then(res => {
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
@import '@/assets/table-container.css';
</style>
