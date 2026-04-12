<template>
    <div>
        <div>
            <!-- 配置表单 -->
            <el-form :model="form" :rules="rules" ref="formRef" :inline="true">
                <el-form-item label="奖品名称" prop="name">
                    <el-input v-model="form.name" style="width: 181px;" placeholder="请输入奖品名称" size="small"></el-input>
                </el-form-item>
                <el-form-item label="奖品数量" prop="number">
                    <el-input-number v-model="form.number" :min="1" :max="1000" controls-position="right" size="small"></el-input-number>
                </el-form-item>
                <el-form-item :label="'权 重 值\u00A0\u00A0'" prop="weight">
                    <el-input-number v-model="form.weight" :min="1" :max="1000" controls-position="right" size="small"></el-input-number>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="addPrize" size="small">添加奖品</el-button>
                </el-form-item>
            </el-form>
        </div>
        <div class="weight-config">
            <!-- 奖品列表 -->
            <el-table :data="pagination.currentTableData" border style="width: 150%" :max-height="this.screenHeight"
                :header-cell-style="{ 'text-align': 'center' }">
                <el-table-column prop="name" label="奖品名称" align="center">
                    <template #default="{ row }">
                        <el-input v-model="row.name" :min="1"></el-input>
                    </template>
                </el-table-column>
                <el-table-column prop="number" label="奖品数量" align="center">
                    <template #default="{ row }">
                        <el-input-number v-model="row.number" :min="1"></el-input-number>
                    </template>
                </el-table-column>
                <el-table-column prop="weight" label="权重（值越大，中奖概率越大）" align="center">
                    <template #default="{ row }">
                        <el-input-number v-model="row.weight" :min="1"></el-input-number>
                    </template>
                </el-table-column>
                <el-table-column label="中奖概率" align="center">
                    <template #default="{ row }">
                        {{ calcProbability(row) }}%
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="120" align="center">
                    <template #default="{ row }">
                        <el-button type="danger" size="mini" @click="deletePrize(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

        </div>
        <!-- 分页 -->
        <div style="margin-top: 20px" class="my-border-padding">
            <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page="pagination.currentPage" :page-sizes="pagination.pageSizes" :page-size="pagination.pageSize"
                layout="total, sizes, prev, pager, next, jumper" :total="pagination.total"></el-pagination>

        </div>
        <!-- 统计信息 -->
        <div class="stats">
            <span style="color: #FFD700;">总权重：{{ totalWeight }}</span>&ensp;&ensp;
            <el-button type="success" @click="saveConfig">保存配置</el-button>
        </div>
    </div>
</template>
  
<script>
import { _getPrizeAll, _operationPrize } from "@/api/api.js";
import { getTime, parseTime } from "@/utils/formatDate";
export default {
    data() {
        return {
            screenHeight: 950,
            pagination: {
                listData: [], //总data
                currentPage: 1, //当前页
                pageSizes: [5, 10],
                pageSize: 10, //每页显示条数
                totalPage: 1, //总页数
                currentTableData: [], //当前页data
                total: 0, //总数量
                loading: false //table加载样式是否显示
            },
            form: {
                id: '' + getTime("start"),
                name: '',
                number: 1,
                weight: 1,
                calc: '0',
            },
            rules: {
                name: [{ required: true, message: '请输入奖品名称', trigger: 'blur' }],
                number: [{ required: true, message: '请输入奖品数量', trigger: 'blur' }],
                weight: [{ required: true, message: '请输入权重值', trigger: 'blur' }]
            }
        }
    },
    mounted() {
        this.updateScreenHeight(); // 组件挂载时获取屏幕高度
        window.addEventListener("resize", this.updateScreenHeight); // 监听窗口大小变化
        // 监听Tab键事件
        window.addEventListener("keyup", this.handleKeyUp);
    },
    beforeDestroy() {
        window.removeEventListener("resize", this.updateScreenHeight); // 组件销毁前移除事件监听
    },
    computed: {
        totalWeight() {
            return this.pagination.listData.reduce((sum, item) => sum + item.weight, 0)
        }
    },
    methods: {
        updateScreenHeight() {
            this.screenHeight = window.innerHeight - 400; // 更新屏幕高度
        },
        /*分页事件*/
        handleSizeChange(val) {
            this.pagination.pageSize = val;
            this.getCurrentPageData(
                this.pagination.listData,
                this.pagination.currentPage,
                this.pagination.pageSize
            );
        },
        handleCurrentChange(val) {
            this.pagination.currentPage = val;
            this.getCurrentPageData(
                this.pagination.listData,
                this.pagination.currentPage,
                this.pagination.pageSize
            );
        },
        //分页
        getCurrentPageData(listData, currentPage, pageSize) {
            let begin = (currentPage - 1) * pageSize;
            let end = currentPage * pageSize;
            this.pagination.currentTableData = listData.slice(begin, end);
        },
        // 添加奖品
        addPrize() {
            this.$refs.formRef.validate(valid => {
                if (valid) {
                    if (this.pagination.pageSize == this.pagination.currentTableData.length) {
                        this.pagination.currentPage = this.pagination.currentPage + 1;
                    }
                    this.pagination.listData.push({ ...this.form });
                    this.form = { id: '' + getTime("start"), name: '', number: 1, weight: 1 } // 清空表单
                    this.pagination.total = this.pagination.listData.length;
                    this.pagination.totalPage = Math.ceil(
                        this.pagination.listData.length / this.pagination.pageSize
                    );
                    this.pagination.totalPage =
                        this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
                    this.getCurrentPageData(
                        this.pagination.listData,
                        this.pagination.currentPage,
                        this.pagination.pageSize
                    );

                }
            })
        },
        // 删除奖品
        deletePrize(row) {
            this.pagination.listData = this.pagination.listData.filter(
                item => item.id !== row.id
            );
            this.pagination.total = this.pagination.listData.length;

            this.pagination.totalPage = Math.ceil(
                this.pagination.listData.length / this.pagination.pageSize
            );
            this.pagination.totalPage =
                this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
            this.getCurrentPageData(
                this.pagination.listData,
                this.pagination.currentPage,
                this.pagination.pageSize
            );
            if (
                this.pagination.currentTableData.length == 0 &&
                this.pagination.listData.length > 0
            ) {
                this.pagination.currentPage = this.pagination.currentPage - 1;
                this.getCurrentPageData(
                    this.pagination.listData,
                    this.pagination.currentPage,
                    this.pagination.pageSize
                );
            }
        },
        // 计算单个奖品概率
        calcProbability(row) {
            if (this.totalWeight === 0) return 0
            row.calc = ((row.weight / this.totalWeight) * 100).toFixed(2)
            return row.calc
        },
        // 保存配置（用本地存储）
        saveConfig() {
            localStorage.setItem('prizeConfig', JSON.stringify(this.pagination.listData))
            _operationPrize(this.pagination.listData).then((res) => {
                this.$message.success('配置已保存')
            })
        },

        // 加载配置
        loadConfig() {
            const data = localStorage.getItem('prizeConfig')
            if (data) {
                let res = JSON.parse(data)
                this.pagination.listData = res;
                this.pagination.total = res.length;

                this.pagination.totalPage = Math.ceil(
                    this.pagination.listData.length / this.pagination.pageSize
                );
                this.pagination.totalPage =
                    this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
                this.getCurrentPageData(
                    this.pagination.listData,
                    this.pagination.currentPage,
                    this.pagination.pageSize
                );
            } else {
                _getPrizeAll().then((res) => {
                    this.pagination.listData = res.data;
                    this.pagination.total = res.length;

                    this.pagination.totalPage = Math.ceil(
                        this.pagination.listData.length / this.pagination.pageSize
                    );
                    this.pagination.totalPage =
                        this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
                    this.getCurrentPageData(
                        this.pagination.listData,
                        this.pagination.currentPage,
                        this.pagination.pageSize
                    );
                })
            }
        }
    },
    created(){
        this.loadConfig()
    }
}
</script>
  
<style scoped>
.el-pagination__jump {
    color: #f3f3f3 !important;
}

.weight-config {
    /* max-width: 1000px; */
    /* margin: 20px auto; */
    /* padding: 20px; */
    /* background: #fff; */
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.stats {
    text-align: right;
    /* background: #f5f7fa; */
    border-radius: 4px;
}

.el-form-item {
    margin-bottom: 15px;
}
</style>