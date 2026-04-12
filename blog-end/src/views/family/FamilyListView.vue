<template>
    <div class="clan-manager">
        <!-- 宗祠匾额 + 搜索栏 -->
        <div class="manager-header">
            <div class="clan-plaque">
                <h1>📜 族 人 名 录</h1>
                <p>—— 支分派別 · 世系昭然 ——</p>
            </div>

            <!-- 搜索工具 -->
            <div class="header-tools">
                <div class="search-box">
                    <span class="search-icon">🔍</span>
                    <input type="text" v-model="inputKeyWord" placeholder="按姓名搜索族人..." @keyup.enter="handleSearchByName" />
                    <button v-if="inputKeyWord" class="clear-search" @click="handleRefBtn">✕</button>
                </div>

                <div class="tree-selector">
                    <select v-model="pTreeId" @change="changeTreeBtn" class="tree-select">
                        <option value="">📚 全 部 宗 譜</option>
                        <option v-for="(item, index) in treeData" :key="index" :value="item.id">
                            {{ item.name }} · {{ item.displayName }}
                        </option>
                    </select>
                </div>
            </div>
        </div>

        <!-- 统计信息 - 仿古籍卷首语 -->
        <div class="stat-scroll">
            <div class="stat-item">
                <span class="stat-label">总 人 數</span>
                <span class="stat-value">{{ pagination.total }}</span>
            </div>
            <div class="stat-item">
                <span class="stat-label">当 前 页</span>
                <span class="stat-value">{{ pagination.currentPage }}/{{ pagination.totalPage }}</span>
            </div>
            <div class="stat-item">
                <span class="stat-label">宗 譜</span>
                <span class="stat-value">{{ currentTreeName || '全 部' }}</span>
            </div>
            <div class="stat-item" v-if="pagination.total > 0">
                <span class="stat-label">世 系</span>
                <span class="stat-value">{{ generationRange }}</span>
            </div>
        </div>

        <!-- 表格区域 - 仿古籍谱牒 -->
        <div class="table-container">
            <el-table v-loading="pagination.loading" :element-loading-text="'翻阅族譜中...'"
                element-loading-spinner="el-icon-loading" element-loading-background="rgba(242, 229, 210, 0.8)" border
                :data="pagination.currentTableData" style="width: 100%" class="genealogy-table"
                :header-cell-style="headerCellStyle" :cell-style="cellStyle">

                <!-- 头像列 -->
                <el-table-column label="头像" width="90px" align="center">
                    <template slot-scope="scope">
                        <div class="avatar-wrapper">
                            <el-image class="table-avatar" :src="getAvatarUrl(scope.row)" fit="cover">
                                <div slot="error" class="avatar-error">
                                    {{ scope.row.userSex === '男' ? '👤' : '👩' }}
                                </div>
                            </el-image>
                            <span v-if="scope.row.isSurvival === '0'" class="deceased-mark">故</span>
                        </div>
                    </template>
                </el-table-column>

                <!-- 姓名列 -->
                <el-table-column label="名字" width="100px" align="center">
                    <template slot-scope="scope">
                        <div class="name-cell">
                            <span class="name-text">{{ scope.row.userName }}</span>
                            <span class="name-pinyin" v-if="scope.row.userPinYin">{{ scope.row.userPinYin }}</span>
                        </div>
                    </template>
                </el-table-column>

                <!-- 性别列 -->
                <el-table-column label="性別" width="60px" align="center">
                    <template slot-scope="scope">
                        <span class="gender-badge"
                            :class="{ 'male': scope.row.userSex === '男', 'female': scope.row.userSex === '女' }">
                            {{ scope.row.userSex }}
                        </span>
                    </template>
                </el-table-column>

                <!-- 世系列 -->
                <el-table-column label="世系" width="70px" align="center">
                    <template slot-scope="scope">
                        <span class="generation-cell" v-if="scope.row.userLove">
                            {{ scope.row.userLove }} 世
                        </span>
                        <span v-else class="text-muted">—</span>
                    </template>
                </el-table-column>

                <!-- 生辰列 -->
                <el-table-column label="生辰" width="120px" align="center">
                    <template slot-scope="scope">
                        <div class="date-cell">
                            <span>{{ scope.row.userBirthDay || '不祥' }}</span>
                        </div>
                    </template>
                </el-table-column>

                <!-- 忌日列 (有已故成员才显示) -->
                <el-table-column label="忌日" width="120px" align="center" v-if="hasDeceased">
                    <template slot-scope="scope">
                        <div class="date-cell deceased-date" v-if="scope.row.isSurvival === '0'">
                            <span>{{ scope.row.userDieDay || '不祥' }}</span>
                        </div>
                        <span v-else class="text-muted">健在</span>
                    </template>
                </el-table-column>

                <!-- 联系方式列 -->
                <el-table-column label="联系" min-width="160px" align="center">
                    <template slot-scope="scope">
                        <div class="contact-cell">
                            <span v-if="scope.row.userPhone" class="contact-item">{{ scope.row.userPhone }}</span>
                            <span v-else-if="scope.row.userEmil" class="contact-item">✉</span>
                            <span v-else class="text-muted">无</span>
                        </div>
                    </template>
                </el-table-column>

                <!-- 父母列 -->
                <el-table-column label="父母" width="140px" align="center">
                    <template slot-scope="scope">
                        <div class="parents-cell">
                            <span class="parent-item" v-if="scope.row.pName">父 {{ scope.row.pName }}</span>
                            <span class="parent-item" v-if="scope.row.mName">母 {{ scope.row.mName }}</span>
                            <span v-if="!scope.row.pName && !scope.row.mName" class="text-muted">无</span>
                        </div>
                    </template>
                </el-table-column>

                <!-- 配偶列 -->
                <el-table-column label="配偶" width="100px" align="center">
                    <template slot-scope="scope">
                        <div class="spouse-cell">
                            <span v-for="(item, index) in scope.row.list" :key="index" class="spouse-item">
                                {{ item.userName }}{{ index < scope.row.list.length - 1 ? '、' : '' }} </span>
                                    <span v-if="!scope.row.list || scope.row.list.length === 0" class="text-muted">无</span>
                        </div>
                    </template>
                </el-table-column>

                <!-- 籍贯列 -->
                <el-table-column label="籍贯" min-width="160px" align="center">
                    <template slot-scope="scope">
                        <div class="address-cell">
                            <span>{{ scope.row.userAddressOld || '不祥' }}</span>
                        </div>
                    </template>
                </el-table-column>

                <!-- 現居地列 -->
                <el-table-column label="现居" min-width="160px" align="center">
                    <template slot-scope="scope">
                        <div class="address-cell">
                            <span>{{ scope.row.userAddressNew || '不祥' }}</span>
                        </div>
                    </template>
                </el-table-column>

                <!-- 族谱记载列 -->
                <el-table-column label="族譜記載" min-width="180px" align="center">
                    <template slot-scope="scope">
                        <div class="note-cell" :title="scope.row.userNote">
                            <span>{{ scope.row.userNote || '无' }}</span>
                        </div>
                    </template>
                </el-table-column>

                <!-- 操作列 -->
                <el-table-column label="操作" width="80px" align="center" fixed="right">
                    <template slot-scope="scope">
                        <button class="action-btn" @click="handleEdit(scope.$index, scope.row)" title="查看詳情">
                            📖
                        </button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

        <!-- 底部信息 + 分页 -->
        <div class="clan-footer">
            <div class="footer-left">
                <span>🏮 共 {{ pagination.total }} 位族人 · 第 {{ pagination.currentPage }}/{{ pagination.totalPage }} 卷</span>
            </div>

            <div class="pagination-wrapper">
                <button class="page-btn prev" @click="handlePageChange(pagination.currentPage - 1)"
                    :disabled="pagination.currentPage === 1">
                    ◀
                </button>

                <span class="page-info">{{ pagination.currentPage }} / {{ pagination.totalPage }}</span>

                <button class="page-btn next" @click="handlePageChange(pagination.currentPage + 1)"
                    :disabled="pagination.currentPage === pagination.totalPage">
                    ▶
                </button>

                <select class="page-size" v-model="pagination.pageSize" @change="handleSizeChange">
                    <option v-for="size in pagination.pageSizes" :key="size" :value="size">{{ size }} 条</option>
                </select>
            </div>
        </div>
    </div>
</template>

<script>
import { _getSourceAll, _getAll } from "@/api/api.js";

export default {
    name: "FamilyListView",
    data() {
        return {
            treeData: [],
            pTreeId: '',
            ManImg: '/static/defaultImg.png',
            WmanImg: '/static/defaultW.png',
            inputKeyWord: "",
            pagination: {
                listData: [],
                currentPage: 1,
                pageSizes: [10, 20, 30, 50, 100],
                pageSize: 10,
                totalPage: 1,
                currentTableData: [],
                total: 0,
                loading: false,
            },
            headerCellStyle: {
                background: '#b59569',
                color: '#2b1d0d',
                fontWeight: 'bold',
                fontSize: '1rem',
                fontFamily: '"华文楷体", "KaiTi", serif',
                borderRight: '1px solid #7b5b39',
                borderBottom: '2px solid #7b5b39',
                padding: '12px 0',
                textAlign: 'center'
            },
            cellStyle: {
                background: '#f2e5d2',
                color: '#4f3621',
                borderRight: '1px solid #c9a97a',
                borderBottom: '1px solid #c9a97a',
                padding: '8px 0',
                textAlign: 'center',
                fontFamily: '"华文楷体", "KaiTi", serif'
            }
        }
    },
    computed: {
        currentTreeName() {
            const tree = this.treeData.find(t => t.id === this.pTreeId);
            return tree ? tree.name : '全部';
        },
        hasDeceased() {
            return this.pagination.listData.some(item => item.isSurvival === '0');
        },
        generationRange() {
            const generations = this.pagination.listData
                .map(item => parseInt(item.userLove))
                .filter(love => !isNaN(love));

            if (generations.length === 0) return '—';

            const min = Math.min(...generations);
            const max = Math.max(...generations);
            return min === max ? `${min}世` : `${min}-${max}世`;
        }
    },
    mounted() {
        this.loadBooks();
    },
    methods: {
        loadBooks() {
            _getAll().then((res) => {
                if (res.data) {
                    this.treeData = res.data;
                }
                this.getSourceData();
            });
        },

        getSourceData() {
            this.pagination.loading = true;
            _getSourceAll(this.pTreeId).then(res => {
                if (res.status === 200) {
                    this.pagination.listData = res.data || [];
                    this.pagination.total = this.pagination.listData.length;
                    this.updatePagination();
                }
                this.pagination.loading = false;
            }).catch(() => {
                this.pagination.loading = false;
                alert('数据加载失败');
            });
        },

        updatePagination() {
            this.pagination.totalPage = Math.ceil(this.pagination.total / this.pagination.pageSize) || 1;
            if (this.pagination.currentPage > this.pagination.totalPage) {
                this.pagination.currentPage = this.pagination.totalPage;
            }
            this.getCurrentPageData();
        },

        getCurrentPageData() {
            let begin = (this.pagination.currentPage - 1) * this.pagination.pageSize;
            let end = this.pagination.currentPage * this.pagination.pageSize;
            this.pagination.currentTableData = this.pagination.listData.slice(begin, end);
        },

        getAvatarUrl(row) {
            if (row.userImgUrl) {
                return row.userImgUrl;
            }
            return row.userSex === '男' ? this.ManImg : this.WmanImg;
        },

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
                "addressNewCode": row.userAddressNewCode,
                "addressOldCode": row.userAddressOldCode,
                "addressNew": row.userAddressNew,
                "addressOld": row.userAddressOld,
                "emil": row.userEmil,
                "img": row.userImgUrl || (row.userSex === '男' ? this.ManImg : this.WmanImg),
                "mateId": row.userMateId,
                "note": row.userNote,
                "love": row.userLove,
                "pName": row.pName,
                "mName": row.mName,
                "mateName": row.mateName,
                "styleNo": row.styleNo,
                "mate": row.list,
            };

            this.$router.push({
                name: 'FamilyEditRank',
                params: {
                    jsonParam: JSON.stringify(obj)
                }
            });
        },

        handleSearchByName() {
            if (!this.inputKeyWord.trim() && !this.pTreeId) {
                this.getSourceData();
                return;
            }

            this.pagination.loading = true;
            this.$getRequest(`/source/getSourceByName?s_name=${this.inputKeyWord.trim()}&pTree=${this.pTreeId}`).then(res => {
                if (res.status === 200) {
                    this.pagination.listData = res.data || [];
                    this.pagination.total = this.pagination.listData.length;
                    this.pagination.currentPage = 1;
                    this.updatePagination();
                } else {
                    alert('查询异常');
                }
                this.pagination.loading = false;
            }).catch(() => {
                this.pagination.loading = false;
                alert('查询失败');
            });
        },

        handleRefBtn() {
            this.pTreeId = '';
            this.inputKeyWord = '';
            this.pagination.currentPage = 1;
            this.getSourceData();
        },

        handleSizeChange() {
            this.pagination.currentPage = 1;
            this.updatePagination();
        },

        handlePageChange(page) {
            if (page < 1 || page > this.pagination.totalPage) return;
            this.pagination.currentPage = page;
            this.getCurrentPageData();
        },

        changeTreeBtn() {
            this.pagination.currentPage = 1;
            this.handleSearchByName();
        }
    }
}
</script>

<style scoped>
.clan-manager {
    background: #e8dccc;
    min-height: 100vh;
    padding: 30px 20px 40px;
    font-family: '华文楷体', 'KaiTi', '宋体', serif;
    display: flex;
    flex-direction: column;
    align-items: center;
    box-sizing: border-box;
}

/* 头部区域 */
.manager-header {
    width: 100%;
    max-width: 1400px;
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 30px;
    flex-wrap: wrap;
    gap: 20px;
}

.clan-plaque {
    text-align: left;
}

.clan-plaque h1 {
    font-size: 2.8rem;
    color: #5d3e28;
    margin: 0;
    letter-spacing: 6px;
    font-weight: normal;
    border-bottom: 3px solid #b48c5c;
    padding-bottom: 10px;
    text-shadow: 2px 2px 0 #d4b48c;
}

.clan-plaque p {
    font-size: 1.1rem;
    color: #7b5f42;
    margin: 8px 0 0;
    font-style: italic;
}

/* 头部工具栏 */
.header-tools {
    display: flex;
    gap: 15px;
    align-items: center;
    flex-wrap: wrap;
}

.search-box {
    position: relative;
    display: flex;
    align-items: center;
    background: white;
    border-radius: 40px;
    box-shadow: 0 4px 0 #a77c52, inset 0 1px 5px #dbbd96;
    border: 1px solid #b48b5c;
    overflow: hidden;
    width: 280px;
}

.search-icon {
    position: absolute;
    left: 15px;
    color: #8b6d48;
    font-size: 1.1rem;
    z-index: 1;
}

.search-box input {
    width: 100%;
    padding: 12px 20px 12px 45px;
    border: none;
    background: transparent;
    font-size: 1rem;
    font-family: inherit;
    color: #3f2e1b;
    outline: none;
}

.search-box input::placeholder {
    color: #b19673;
    font-style: italic;
}

.clear-search {
    position: absolute;
    right: 10px;
    background: none;
    border: none;
    font-size: 1.2rem;
    cursor: pointer;
    color: #9e7c56;
    padding: 0 5px;
    z-index: 1;
}

.clear-search:hover {
    color: #5e3d1e;
}

.tree-selector {
    position: relative;
}

.tree-select {
    appearance: none;
    background: white;
    border: 1px solid #b48b5c;
    border-radius: 40px;
    padding: 12px 40px 12px 20px;
    font-size: 1rem;
    font-family: inherit;
    color: #3f2e1b;
    cursor: pointer;
    box-shadow: 0 4px 0 #a77c52, inset 0 1px 5px #dbbd96;
    min-width: 220px;
    outline: none;
}

.tree-select option {
    background: #f5e5ce;
    color: #3f2e1b;
}

.tree-selector::after {
    content: '▼';
    position: absolute;
    right: 15px;
    top: 50%;
    transform: translateY(-50%);
    color: #8b6d48;
    pointer-events: none;
    font-size: 0.8rem;
}

/* 统计区域 - 仿卷首语 */
.stat-scroll {
    width: 100%;
    max-width: 1400px;
    margin-bottom: 25px;
    padding: 15px 25px;
    background: #b29063;
    border-radius: 40px;
    box-shadow: 0 5px 0 #5d4229, inset 0 1px 8px #ffefcf;
    border: 1px solid #ddb585;
    display: flex;
    flex-wrap: wrap;
    gap: 30px;
    color: #fef3e0;
}

.stat-item {
    display: flex;
    align-items: baseline;
    gap: 10px;
}

.stat-label {
    font-size: 1rem;
    letter-spacing: 2px;
    opacity: 0.9;
}

.stat-value {
    font-size: 1.4rem;
    font-weight: bold;
    font-family: "华文楷体", "KaiTi", serif;
    text-shadow: 2px 2px 0 #5d4229;
}

/* 表格容器 */
.table-container {
    width: 100%;
    max-width: 1400px;
    background: rgba(190, 160, 120, 0.15);
    border-radius: 40px;
    padding: 25px;
    margin-bottom: 25px;
    overflow-x: auto;
}

/* 表格样式 */
.genealogy-table {
    border: 2px solid #7b5b39;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 10px 20px rgba(100, 70, 30, 0.3);
}

.genealogy-table /deep/ .el-table__body-wrapper {
    background: #f2e5d2;
}

.genealogy-table /deep/ .el-table__row:hover td {
    background: #e6d5b8 !important;
    cursor: pointer;
}

/* 头像样式 */
.avatar-wrapper {
    position: relative;
    display: inline-block;
}

.table-avatar {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    border: 2px solid #b48b5c;
    background: #f5e5ce;
}

.avatar-error {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    background: #f5e5ce;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.5rem;
    color: #8b6d48;
    border: 2px solid #b48b5c;
}

.deceased-mark {
    position: absolute;
    bottom: -2px;
    right: -2px;
    width: 20px;
    height: 20px;
    background: #b33c2e;
    color: #fef3e0;
    border-radius: 50%;
    font-size: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid #fef3e0;
}

/* 姓名样式 */
.name-cell {
    display: flex;
    flex-direction: column;
}

.name-text {
    font-weight: bold;
    font-size: 1.1rem;
    color: #5d3e28;
}

.name-pinyin {
    font-size: 0.7rem;
    color: #8b6d48;
    margin-top: 2px;
}

/* 性别徽章 */
.gender-badge {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 20px;
    font-size: 0.9rem;
}

.gender-badge.male {
    background: rgba(42, 92, 138, 0.15);
    color: #2a5c8a;
    border: 1px solid #2a5c8a;
}

.gender-badge.female {
    background: rgba(179, 60, 46, 0.15);
    color: #b33c2e;
    border: 1px solid #b33c2e;
}

/* 世系样式 */
.generation-cell {
    font-weight: bold;
    color: #b48c5c;
}

/* 日期样式 */
.date-cell {
    color: #5d3e28;
}

.deceased-date {
    color: #b33c2e;
}

.deceased-date::before {
    content: '';
    margin-right: 4px;
    font-size: 0.9rem;
}

/* 联系方式样式 */
.contact-cell {
    display: flex;
    justify-content: center;
    align-items: center;
}

.contact-item {
    padding: 2px 8px;
    background: rgba(180, 140, 100, 0.1);
    border-radius: 12px;
    font-size: 0.9rem;
}

/* 父母样式 */
.parents-cell {
    display: flex;
    flex-direction: column;
    gap: 2px;
}

.parent-item {
    font-size: 0.9rem;
    color: #5d3e28;
}

/* 配偶样式 */
.spouse-cell {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 2px 4px;
}

.spouse-item {
    font-size: 0.9rem;
    color: #b48c5c;
}

/* 地址样式 */
.address-cell {
    color: #5d3e28;
    font-size: 0.9rem;
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

/* 族谱记载样式 */
.note-cell {
    color: #5d3e28;
    font-size: 0.9rem;
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.note-cell:hover {
    overflow: visible;
    white-space: normal;
    word-break: break-all;
    background: #f5e5ce;
    position: relative;
    z-index: 10;
}

/* 操作按钮 */
.action-btn {
    width: 36px;
    height: 36px;
    border: none;
    background: #b29063;
    color: #fef3e0;
    border-radius: 50%;
    cursor: pointer;
    font-size: 1.2rem;
    box-shadow: 0 3px 0 #5d4229;
    transition: 0.1s;
    display: flex;
    align-items: center;
    justify-content: center;
}

.action-btn:hover {
    background: #c9a97a;
    transform: translateY(-2px);
    box-shadow: 0 5px 0 #5d4229;
}

/* 底部区域 */
.clan-footer {
    width: 100%;
    max-width: 1400px;
    background: #b29063;
    padding: 12px 25px;
    border-radius: 40px;
    color: #fef3e0;
    font-size: 1.1rem;
    box-shadow: 0 5px 0 #5d4229, inset 0 1px 8px #ffefcf;
    border: 1px solid #ddb585;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 15px;
}

.footer-left {
    display: flex;
    align-items: center;
    gap: 20px;
}

/* 分页控件 */
.pagination-wrapper {
    display: flex;
    align-items: center;
    gap: 15px;
}

.page-btn {
    width: 36px;
    height: 36px;
    border: none;
    background: #f5e5ce;
    color: #5d3e28;
    border-radius: 50%;
    cursor: pointer;
    font-size: 1rem;
    box-shadow: 0 3px 0 #a77c52;
    transition: 0.1s;
    display: flex;
    align-items: center;
    justify-content: center;
}

.page-btn:hover:not(:disabled) {
    background: #ffe4c4;
    transform: translateY(-2px);
    box-shadow: 0 5px 0 #a77c52;
}

.page-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.page-info {
    font-size: 1.1rem;
    font-weight: bold;
    min-width: 60px;
    text-align: center;
}

.page-size {
    padding: 8px 12px;
    border: 1px solid #b48b5c;
    border-radius: 30px;
    background: #f5e5ce;
    color: #5d3e28;
    font-family: inherit;
    cursor: pointer;
    outline: none;
    box-shadow: 0 2px 0 #a77c52;
}

/* 文字辅助 */
.text-muted {
    color: #b19673;
    font-style: italic;
}

/* 响应式 */
@media (max-width: 1000px) {
    .clan-plaque h1 {
        font-size: 2.2rem;
    }

    .stat-scroll {
        gap: 15px;
    }

    .stat-item {
        flex-direction: column;
        gap: 5px;
    }
}

@media (max-width: 800px) {
    .manager-header {
        flex-direction: column;
        align-items: stretch;
    }

    .header-tools {
        justify-content: space-between;
    }

    .search-box {
        width: 100%;
        max-width: none;
    }

    .tree-select {
        min-width: 180px;
    }

    .clan-footer {
        flex-direction: column;
        align-items: stretch;
        text-align: center;
    }

    .pagination-wrapper {
        justify-content: center;
    }
}

@media (max-width: 450px) {
    .pagination-wrapper {
        flex-wrap: wrap;
        justify-content: center;
    }

    .stat-scroll {
        flex-direction: column;
        gap: 10px;
    }
}
</style>