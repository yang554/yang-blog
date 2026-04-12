<template>
    <div class="app-version-history">
        <!-- 页面标题和操作栏 -->
        <div class="page-header">
            <h2 class="page-title">App历史更新记录</h2>
            <div class="header-actions">
                <el-button type="primary" @click="handleAddVersion">
                    <i class="el-icon-plus"></i> 新增版本
                </el-button>
                <el-button @click="refreshList">
                    <i class="el-icon-refresh"></i> 刷新
                </el-button>
            </div>
        </div>

        <!-- 搜索和筛选区域 -->
        <div class="filter-container">
            <el-form :model="queryParams" :inline="true">
                <el-form-item label="版本名称">
                    <el-input v-model="queryParams.versionName" placeholder="请输入版本名称" clearable
                        @keyup.enter.native="handleSearch" />
                </el-form-item>
                <el-form-item label="版本代码">
                    <el-input v-model="queryParams.versionCode" placeholder="请输入版本代码" clearable
                        @keyup.enter.native="handleSearch" />
                </el-form-item>
                <el-form-item label="发布时间">
                    <el-date-picker v-model="queryParams.publishTimeRange" type="daterange" range-separator="至"
                        start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleSearch">
                        <i class="el-icon-search"></i> 搜索
                    </el-button>
                    <el-button @click="resetSearch">
                        <i class="el-icon-refresh-left"></i> 重置
                    </el-button>
                </el-form-item>
            </el-form>
        </div>

        <!-- 数据表格 -->
        <div class="table-container">
            <el-table :data="tableData" v-loading="loading" border style="width: 100%"
                :default-sort="{ prop: 'versionCode', order: 'descending' }">
                <el-table-column prop="appName" label="APP名称" width="120" align="center">
                    <template slot-scope="scope">
                        <span class="version-code">{{ scope.row.appName }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="versionCode" label="版本代码" width="120" sortable align="center">
                    <template slot-scope="scope">
                        <span class="version-code">{{ scope.row.versionCode }}</span>
                    </template>
                </el-table-column>

                <el-table-column prop="versionName" label="版本名称" width="150" align="center">
                    <template slot-scope="scope">
                        <el-tag type="primary">{{ scope.row.versionName }}</el-tag>
                    </template>
                </el-table-column>

                <el-table-column prop="updateTitle" label="更新标题" min-width="200" show-overflow-tooltip />

                <el-table-column prop="fileSize" label="文件大小" width="120" align="center" />

                <el-table-column prop="forceUpdate" label="强制更新" width="100" align="center">
                    <template slot-scope="scope">
                        <el-tag :type="scope.row.forceUpdate ? 'danger' : 'success'" effect="plain">
                            {{ scope.row.forceUpdate ? '是' : '否' }}
                        </el-tag>
                    </template>
                </el-table-column>

                <el-table-column prop="publishTime" label="发布时间" width="180" sortable align="center">
                    <template slot-scope="scope">
                        {{ formatTime(scope.row.publishTime) }}
                    </template>
                </el-table-column>

                <el-table-column label="操作" width="200" fixed="right" align="center">
                    <template slot-scope="scope">
                        <el-button type="text" size="small" @click="handleView(scope.row)">
                            查看
                        </el-button>
                        <el-button type="text" size="small" @click="handleEdit(scope.row)">
                            编辑
                        </el-button>
                        <el-button type="text" size="small" class="danger-btn" @click="handleDelete(scope.row)">
                            删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!-- 分页 -->
            <div class="pagination-container">
                <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                    :current-page="queryParams.pageNum" :page-sizes="[10, 20, 50, 100]" :page-size="queryParams.pageSize"
                    layout="total, sizes, prev, pager, next, jumper" :total="total" />
            </div>
        </div>

        <!-- 版本详情对话框 -->
        <el-dialog title="版本详情" :visible.sync="detailDialogVisible" width="600px" :before-close="closeDetailDialog">
            <div v-if="currentVersion" class="version-detail">
                <el-descriptions :column="2" border>
                    <el-descriptions-item label="APP名称">
                        {{ currentVersion.appName }}
                    </el-descriptions-item>
                    <el-descriptions-item label="版本代码">
                        <el-tag type="primary">{{ currentVersion.versionCode }}</el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="文件大小">
                        {{ currentVersion.fileSize }}
                    </el-descriptions-item>
                    <el-descriptions-item label="强制更新">
                        <el-tag :type="currentVersion.forceUpdate ? 'danger' : 'success'">
                            {{ currentVersion.forceUpdate ? '是' : '否' }}
                        </el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="发布时间" :span="2">
                        {{ formatTime(currentVersion.publishTime) }}
                    </el-descriptions-item>
                    <el-descriptions-item label="更新标题" :span="2">
                        {{ currentVersion.updateTitle }}
                    </el-descriptions-item>
                    <el-descriptions-item label="更新内容" :span="2">
                        <div class="update-content">
                            <pre>{{ currentVersion.updateContent }}</pre>
                        </div>
                    </el-descriptions-item>
                    <el-descriptions-item label="下载地址" :span="2">
                        <el-link type="primary" :href="currentVersion.downloadUrl" target="_blank">
                            {{ currentVersion.downloadUrl }}
                        </el-link>
                    </el-descriptions-item>
                    <el-descriptions-item label="备注" :span="2">
                        {{ currentVersion.note || '无' }}
                    </el-descriptions-item>
                </el-descriptions>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="closeDetailDialog">关闭</el-button>
                <el-button type="primary" @click="closeDetailDialog">确定</el-button>
            </div>
        </el-dialog>

        <!-- 删除确认对话框 -->
        <el-dialog title="删除确认" :visible.sync="deleteDialogVisible" width="400px">
            <div class="delete-confirm">
                <i class="el-icon-warning" style="color: #e6a23c; font-size: 24px; margin-right: 10px;"></i>
                <span>确定要删除版本 "{{ currentVersion ? currentVersion.versionName : '' }}" 吗？</span>
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button @click="deleteDialogVisible = false">取消</el-button>
                <el-button type="danger" @click="confirmDelete" :loading="deleteLoading">
                    确定删除
                </el-button>
            </div>
        </el-dialog>
    </div>
</template>
  
<script>
import { _getVersionLis, _deleteVersion } from '@/api/api.js'

export default {
    name: 'AppVersionHistory',
    data() {
        return {
            // 加载状态
            loading: false,
            deleteLoading: false,

            // 查询参数
            queryParams: {
                versionName: '',
                versionCode: '',
                publishTimeRange: [],
                pageNum: 1,
                pageSize: 10
            },

            // 表格数据
            tableData: [],
            total: 0,

            // 对话框状态
            detailDialogVisible: false,
            deleteDialogVisible: false,

            // 当前操作的版本
            currentVersion: null
        }
    },

    mounted() {
        this.getList()
    },

    methods: {
        // 获取版本列表
        async getList() {
            this.loading = true
            try {
                // 构建查询参数
                let params = ''
                if (this.queryParams.publishTimeRange && this.queryParams.publishTimeRange.length === 2) {
                    params = `versionName=${this.queryParams.versionName}&versionCode=${this.queryParams.versionCode}&startTime=${this.queryParams.publishTimeRange[0]}&endTime=${this.queryParams.publishTimeRange[1]}`
                }else{
                    params = `versionName=${this.queryParams.versionName}&versionCode=${this.queryParams.versionCode}&startTime=&endTime=`
                }
                const response = await _getVersionLis(params)
                if (response.data.status === 200) {
                    this.tableData = response.data.data || null
                    this.total = this.tableData.length
                } else {
                    this.$message.error(response.message || '获取数据失败')
                }
            } catch (error) {
                console.error('获取版本列表失败:', error)
                this.$message.error('获取数据失败')
            } finally {
                this.loading = false
            }
        },

        // 搜索
        handleSearch() {
            this.queryParams.pageNum = 1
            this.getList()
        },

        // 重置搜索
        resetSearch() {
            this.queryParams = {
                versionName: '',
                versionCode: '',
                publishTimeRange: [],
                pageNum: 1,
                pageSize: 10
            }
            this.getList()
        },

        // 刷新列表
        refreshList() {
            this.getList()
        },

        // 分页大小改变
        handleSizeChange(val) {
            this.queryParams.pageSize = val
            this.getList()
        },

        // 当前页改变
        handleCurrentChange(val) {
            this.queryParams.pageNum = val
            this.getList()
        },

        // 查看详情
        handleView(row) {
            this.currentVersion = { ...row }
            this.detailDialogVisible = true
        },

        // 关闭详情对话框
        closeDetailDialog() {
            this.detailDialogVisible = false
            this.currentVersion = null
        },

        // 编辑版本
        handleEdit(row) {
            // 跳转到编辑页面或打开编辑对话框
            this.$message.info('编辑功能开发中...')
        },

        // 新增版本
        handleAddVersion() {
            // 跳转到新增页面
            this.$router.push('/app/version/add')
        },

        // 删除版本
        handleDelete(row) {
            this.currentVersion = { ...row }
            this.deleteDialogVisible = true
        },

        // 确认删除
        async confirmDelete() {
            this.deleteLoading = true
            try {
                const response = await _deleteVersion(this.currentVersion.id)
                if (response.data.status === 200) {
                    this.$message.success('删除成功')
                    this.deleteDialogVisible = false
                    this.getList() // 刷新列表
                } else {
                    this.$message.error(response.message || '删除失败')
                }
            } catch (error) {
                console.error('删除版本失败:', error)
                this.$message.error('删除失败')
            } finally {
                this.deleteLoading = false
            }
        },

        // 格式化时间
        formatTime(timeString) {
            if (!timeString) return '-'
            try {
                const date = new Date(timeString)
                return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
            } catch (e) {
                return timeString
            }
        }
    }
}
</script>
  
<style lang="scss" scoped>
.app-version-history {
    padding: 20px;
    background-color: #f5f7fa;
    min-height: calc(100vh - 84px);
}

.page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 20px;
    background: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .page-title {
        margin: 0;
        color: #303133;
        font-size: 20px;
        font-weight: 600;
    }
}

.filter-container {
    padding: 20px;
    background: #fff;
    border-radius: 4px;
    margin-bottom: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.table-container {
    background: #fff;
    border-radius: 4px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);

    .version-code {
        font-weight: bold;
        color: #409EFF;
    }

    .danger-btn {
        color: #F56C6C;

        &:hover {
            color: #f78989;
        }
    }
}

.pagination-container {
    margin-top: 20px;
    text-align: right;
}

.version-detail {
    .update-content {
        max-height: 200px;
        overflow-y: auto;
        background: #f5f7fa;
        padding: 10px;
        border-radius: 4px;
        border: 1px solid #e4e7ed;

        pre {
            margin: 0;
            white-space: pre-wrap;
            word-wrap: break-word;
            font-family: inherit;
            line-height: 1.5;
        }
    }
}

.delete-confirm {
    display: flex;
    align-items: center;
    font-size: 16px;
}

::v-deep .el-descriptions__label {
    font-weight: 600;
    width: 100px;
}

::v-deep .el-descriptions__content {
    word-break: break-all;
}
</style>