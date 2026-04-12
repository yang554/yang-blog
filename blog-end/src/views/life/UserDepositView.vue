<template>
    <div class="user-list-view">
        <!--搜索-->
        <div class="search-container">
            <el-row :gutter="20">
                <el-col :span="8">
                    <div class="search-item">
                        <span class="search-label">账户：</span>
                        <el-input placeholder="根据账户搜索" size="small" v-model="inputName">
                        </el-input>
                    </div>
                </el-col>
                <el-col :xs="24" :sm="24" :md="8" :lg="3">
                    <el-button type="primary" @click="searchByName" size="small">
                        <i class="el-icon-search"></i>
                        搜索
                    </el-button>
                    <el-button type="success" disabled size="small">
                        <i class="el-icon-plus"></i>
                        添加
                    </el-button>
                </el-col>
            </el-row>
        </div>


        <!--修改分类的对话框-->
        <el-dialog title="编辑" :visible.sync="updateTagDialogFormVisible">
            <div class="updateType" v-if="editRow != null">
                编码：
                <br />
                <br />
                <el-input v-model="editRow.userId" disabled></el-input>
                <br />
                <br />
                账户：
                <br />
                <br />
                <el-input v-model="editRow.username" disabled></el-input>
                <br />
                <br />
                资产：
                <el-input v-model="editRow.totalDeposit" @input="handleInput(editRow.totalDeposit)"></el-input>
                <br />
                <br />
                <el-button type="primary" @click="handleConfirmUpdate">确 定</el-button>
            </div>
        </el-dialog>

        <div class="table-container">
            <el-table v-loading="pagination.listLoading" :data="pagination.currentPageData" stripe style="width: 100%">
                <el-table-column label="编码" align="center">
                    <template slot-scope="scope">
                        <span style="margin-left: 10px">{{ scope.row.userId }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="用户" align="center">
                    <template slot-scope="scope">
                        <el-tag :type="scope.row.color == '' ? 'success' : scope.row.color">{{ scope.row.username
                        }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="资产(￥)" align="center">
                    <template slot-scope="scope">
                        {{ scope.row.totalDeposit }}
                    </template>
                </el-table-column>
                <el-table-column label="创建时间" align="center">
                    <template slot-scope="scope">
                        {{ scope.row.createdAT }}
                    </template>
                </el-table-column>
                <el-table-column label="修改时间" align="center">
                    <template slot-scope="scope">
                        {{ scope.row.updatedAT }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" align="center">
                    <template slot-scope="scope">
                        <el-button type="warning" size="small" @click="handleEdit(scope.$index, scope.row)">编辑
                        </el-button>
                        <el-button type="danger" size="small" @click="handleDelete(scope.$index, scope.row)">删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <div class="pagination-container">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :page-sizes="[5, 10, 15, 20]" layout="total, sizes, prev, pager, next, jumper" :total="pagination.total">
            </el-pagination>
        </div>
    </div>
</template>

<script>
import { _getUserDeposit, _updUserDepositById, _getUserDepositByName } from "@/api/api.js"
export default {
    name: "ConsumptionTypeView",
    components: {},
    data() {
        return {
            inputName: "",
            updateTagDialogFormVisible: false,  //编辑type对话框是否显示
            editRow: {
                userId: "",
                username: "",
                totalDeposit: "",
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
        getUserDeposit() {
            _getUserDeposit().then(res => {
                if (res.data.length > 0) {
                    this.pagination.list = res.data
                    this.pagination.listLoading = false
                    this.pagination.total = this.pagination.list.length

                    this.pagination.totalPage = Math.ceil(this.pagination.list.length / this.pagination.pageSize);
                    this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
                    this.getCurrentPageData(this.pagination.currentPage, this.pagination.pageSize);
                }
            })
        },
        handleInput(row) {
            let value = row;
            // value = value.replace(/([0-9]\d*\.?\d{0,2})?.*$/, "$1");
            value = value.replace(/(^0+(?=\d))|[^\d.]|(?<=\..{2}).|^\.|\.+/g, (match) => { //deepseek给出表达式
                if (match.startsWith("0") && match !== "0") return ""; // 去除前导零（如00→0）
                if (match === ".") return ".";                        // 允许单个小数点
                return "";                                            // 过滤非法字符
            }).replace(/(\d*\.?\d{0,2}).*/, "$1")                   // 截断小数到两位
                .replace(/^\./, "0.");
            this.editRow.totalDeposit = value;                               // 处理以小数点开头的情况（如.12→0.12）
            // this.$set(row, "cost", value); // 确保响应式更新
            // this.handleChange();
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
                        this.getUserDeposit();
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
            this.editRow = row;
        },
        //确认修改
        handleConfirmUpdate() {
            _updUserDepositById(this.editRow).then(res => {
                if (res.data.status === 200) {
                    this.$message.success(res.data.msg);
                    this.updateTagDialogFormVisible = false;
                    this.getUserDeposit();
                } else {
                    this.$message.error(res.data.msg);
                }
            })
        },
        //模糊搜索
        searchByName() {
            if (this.inputName === '') {
                this.getUserDeposit()
            } else {
                _getUserDepositByName(this.inputName).then(res => {
                    if (res.data.length > 0) {
                        this.pagination.list = res.data
                        this.pagination.listLoading = false
                        this.pagination.total = this.pagination.list.length

                        this.pagination.totalPage = Math.ceil(this.pagination.list.length / this.pagination.pageSize);
                        this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
                        this.getCurrentPageData(this.pagination.currentPage, this.pagination.pageSize);
                    } else {
                        this.$message.error(res.statusText);
                    }
                })
            }

        },
    },
    created() {
        this.getUserDeposit();
    }
}
</script>

<style>
@import '@/assets/table-container.css';
</style>
