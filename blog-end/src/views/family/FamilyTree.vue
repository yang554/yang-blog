<template>
    <div class="clan-manager">
        <!-- 宗祠匾额 + 搜索栏 -->
        <div class="manager-header">
            <div class="clan-plaque">
                <h1>📖 宗 譜 堂</h1>
                <p>—— 木本水源 · 慎终追远 ——</p>
            </div>

            <!-- 搜索框 + 新增按钮 -->
            <div class="header-tools">
                <div class="search-box">
                    <span class="search-icon">🔍</span>
                    <input type="text" v-model="searchKeyword" placeholder="搜索族谱名称或堂号..." @input="handleSearch" />
                    <button v-if="searchKeyword" class="clear-search" @click="clearSearch">✕</button>
                </div>
                <button class="btn-create" @click="openCreateModal"> 新 增 族 譜</button>
            </div>
        </div>

        <!-- 谱本陈列 靠左对齐 -->
        <div class="clan-shelf">
            <div class="clan-book" v-for="book in filteredBooks" :key="book.id">
                <!-- 古籍封面 -->
                <div class="classic-cover">
                    <!-- 书签题名 (堂号) -->
                    <div class="classic-label">{{ book.displayName || book.name }}</div>

                    <!-- 卷次信息 -->
                    <div class="classic-volume" v-if="book.generation">卷 {{ book.generation }}</div>
                    <div class="classic-volume" v-else>· 宗 譜 ·</div>

                    <!-- 藏书印 -->
                    <div class="classic-seal"></div>

                    <!-- 操作按钮 (悬停显示) -->
                    <div class="book-actions">
                        <button class="action-btn edit-btn" @click.stop="openEditModal(book)" title="编辑"><i class="el-icon-edit"></i></button>
                        <button class="action-btn delete-btn" @click.stop="confirmDelete(book)" title="刪除"><i class="el-icon-delete"></i></button>
                    </div>
                </div>

                <!-- 书页厚度 -->
                <div class="classic-pages"></div>

                <!-- 书脊装饰 -->
                <div class="classic-spine"></div>

                <!-- 谱名 (数据库name) 点击查看详情 -->
                <div class="classic-caption" @click="viewBookDetail(book)">
                    {{ book.name }}
                </div>
            </div>

            <!-- 空状态 / 无搜索结果 -->
            <div v-if="filteredBooks.length === 0" class="clan-empty">
                <span v-if="pedigreeBooks.length === 0">📭 暂无族譜，请点击「新增族谱」创建</span>
                <span v-else>🔎 未找到符合「{{ searchKeyword }}」的族譜</span>
            </div>
        </div>

        <!-- 底部信息 -->
        <div class="clan-footer">
            <span>🏮 共 {{ filteredBooks.length }} 部 / 总藏 {{ pedigreeBooks.length }} 部 · 源自「宗譜数据库」</span>
        </div>

        <!-- ========== 创建/编辑 ========== -->
        <div class="modal-overlay" v-if="showModal" @click.self="closeModal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>{{ modalMode === 'create' ? ' 新增族譜' : ' 编辑族譜' }}</h2>
                    <button class="close-btn" @click="closeModal">✕</button>
                </div>

                <div class="modal-body">
                    <div class="form-group">
                        <label>谱名 <span class="required">*</span></label>
                        <input type="text" v-model="formData.name" placeholder="例：李氏宗譜" />
                    </div>

                    <div class="form-group">
                        <label>堂號 / 封面名</label>
                        <input type="text" v-model="formData.displayName" placeholder="例：隴西堂" />
                        <span class="form-hint">显示在封面书签上，留空则使用譜名</span>
                    </div>

                    <div class="form-row">
                        <div class="form-group half">
                            <label>卷次</label>
                            <input type="text" v-model="formData.generation" placeholder="例：十二" />
                        </div>
                        <div class="form-group half">
                            <label>备注</label>
                            <input type="text" v-model="formData.remarks" placeholder="选填" />
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button class="btn-cancel" @click="closeModal">取 消</button>
                    <button class="btn-save" @click="saveBook" :disabled="!formData.name.trim()">
                        {{ modalMode === 'create' ? '创 建' : '保 存' }}
                    </button>
                </div>
            </div>
        </div>

        <!-- ========== 刪除确认对话框 ========== -->
        <div class="modal-overlay" v-if="showDeleteConfirm" @click.self="cancelDelete">
            <div class="modal-content confirm-modal">
                <div class="modal-header">
                    <h2>⚠️ 确认刪除</h2>
                    <button class="close-btn" @click="cancelDelete">✕</button>
                </div>

                <div class="modal-body">
                    <p>确定要刪除《{{ bookToDelete?.name }}》吗？</p>
                    <p class="warning-text">此操作无法复原。</p>
                </div>

                <div class="modal-footer">
                    <button class="btn-cancel" @click="cancelDelete">取 消</button>
                    <button class="btn-delete-confirm" @click="deleteBook">确 认 刪 除</button>
                </div>
            </div>
        </div>
    </div>
</template>
  
<script>
import { _getAll, _operationData, _delTree } from "@/api/api.js";
import { getTime } from "../../utils/formatDate";

export default {
    name: 'FamilyTree',
    data() {
        return {
            // 数据
            pedigreeBooks: [],
            searchKeyword: '',

            // 弹出框控制
            showModal: false,
            showDeleteConfirm: false,
            modalMode: 'create', // 'create' 或 'edit'

            // 表单数据
            formData: {
                id: null,
                name: '',
                displayName: '',
                generation: '',
                remarks: '',
                createID: this.$store.state.login_user.id || '',
                op: ''
            },

            // 待删除的族谱
            bookToDelete: null,

            // 搜索防抖计时器
            searchTimer: null
        };
    },

    computed: {
        // 筛选之后的族谱列表 (即時搜索)
        filteredBooks() {
            if (!this.searchKeyword.trim()) {
                return this.pedigreeBooks;
            }

            const keyword = this.searchKeyword.toLowerCase().trim();
            return this.pedigreeBooks.filter(book => {
                return (
                    book.name.toLowerCase().includes(keyword) ||
                    (book.displayName && book.displayName.toLowerCase().includes(keyword))
                );
            });
        }
    },

    mounted() {
        this.loadBooks();
    },

    methods: {
        // 加载数据
        loadBooks() {
            _getAll().then((res) => {
                if (res.data) {
                    this.pedigreeBooks = res.data;
                }
            });
        },

        // 搜索处理 (防抖)
        handleSearch() {
            clearTimeout(this.searchTimer);
            this.searchTimer = setTimeout(() => {
                // 搜索已經通過 computed 即時響應，這裡只是為了防抖
                // 可以留空或添加額外邏輯
            }, 300);
        },

        // 清空搜索
        clearSearch() {
            this.searchKeyword = '';
        },

        // 打开操作框
        openCreateModal() {
            this.modalMode = 'create';
            this.formData = {
                id: getTime("start"),
                name: '',
                displayName: '',
                generation: '',
                remarks: '',
                createID: this.$store.state.login_user.id || '',
                op: 'add'
            };
            this.showModal = true;
        },

        // 打开编辑框
        openEditModal(book) {
            this.modalMode = 'edit';
            this.formData = { ...book };
            this.formData.op = 'upd'
            this.showModal = true;
        },

        // 关闭操作框
        closeModal() {
            this.showModal = false;
            this.formData = {
                id: null,
                name: '',
                displayName: '',
                generation: '',
                remarks: '',
                createID: this.$store.state.login_user.id || '',
                op: ''
            };
        },

        // 保存 (創建/更新)
        saveBook() {
            if (!this.formData.name.trim()) {
                alert('请填写譜名');
                return;
            }

            _operationData(this.formData).then((res) => {
                if (res.data.status === 200) {
                    this.loadBooks();
                }
                this.closeModal();
            });
        },

        // 确认删除对话框
        confirmDelete(book) {
            this.bookToDelete = book;
            this.showDeleteConfirm = true;
        },

        // 取消刪除
        cancelDelete() {
            this.showDeleteConfirm = false;
            this.bookToDelete = null;
        },

        // 执行刪除
        deleteBook() {
            if (!this.bookToDelete) return;

            _delTree(this.bookToDelete.id).then((res) => {
                if (res.data.status === 200) {
                    this.loadBooks();
                }
                this.cancelDelete();
            });
        },

        // 查看详情
        viewBookDetail(book) {
            this.$router.push({
                path: "/home/family/FamilySource/"+book.id,
            });
        }
    }
};
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
    max-width: 1300px;
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
}

.clear-search:hover {
    color: #5e3d1e;
}

.btn-create {
    background: #9e7a56;
    border: none;
    padding: 12px 25px;
    border-radius: 40px;
    font-size: 1.1rem;
    font-weight: bold;
    color: #fdf3e0;
    cursor: pointer;
    box-shadow: 0 5px 0 #5d3f26, inset 0 1px 8px #f7dbb6;
    border: 1px solid #c9a16e;
    transition: 0.1s ease;
    font-family: inherit;
    letter-spacing: 1px;
}

.btn-create:hover {
    background: #b28d63;
    transform: translateY(-2px);
    box-shadow: 0 7px 0 #5d3f26;
}

.clan-shelf {
    max-width: 1300px;
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    justify-content: flex-start;
    gap: 35px 30px;
    padding: 25px;
    background: rgba(190, 160, 120, 0.15);
    border-radius: 40px;
    min-height: 400px;
}

/* 单个族谱 */
.clan-book {
    width: 200px;
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    filter: drop-shadow(5px 8px 10px rgba(100, 70, 30, 0.25));
    transition: filter 0.2s;
}

.clan-book:hover {
    filter: drop-shadow(8px 12px 15px rgba(70, 45, 15, 0.35));
}

/* 古籍封面 */
.classic-cover {
    width: 100%;
    height: 260px;
    background: #a77b51;
    background: linear-gradient(145deg, #9c7a58, #7b5937);
    border-radius: 6px 18px 18px 6px;
    position: relative;
    box-shadow:
        -8px 0 0 #4d3722,
        0 2px 8px rgba(0, 0, 0, 0.2) inset,
        2px 0 5px rgba(255, 235, 200, 0.2) inset;
    border: 1px solid #4f3b27;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 15px;
}

/* 书签 */
.classic-label {
    background: #ecdbba;
    border: 3px solid #9e784b;
    border-radius: 30px 5px 30px 5px;
    padding: 10px 18px;
    font-size: 1.4rem;
    font-weight: bold;
    color: #352712;
    box-shadow: 4px 4px 0 #5d401f, inset 0 1px 5px #fff0d0;
    transform: rotate(-0.5deg);
    text-align: center;
    width: 90%;
    margin-bottom: 15px;
    font-family: '华文楷体', 'KaiTi', serif;
    word-break: break-word;
}

/* 卷次 */
.classic-volume {
    background: #8b6943;
    color: #f5e5ce;
    padding: 4px 15px;
    border-radius: 20px;
    font-size: 0.95rem;
    box-shadow: inset 0 1px 4px #f5dbb0, 0 2px 0 #4f371d;
    border: 1px solid #caa571;
}

/* 藏书印 */
.classic-seal {
    position: absolute;
    bottom: 15px;
    right: 15px;
    width: 40px;
    height: 40px;
    border: 2px solid #b5472e;
    border-radius: 4px 15px 4px 15px;
    opacity: 0.7;
    transform: rotate(-10deg);
}

.classic-seal::after {
    content: '宗譜';
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 0.6rem;
    color: #a03c20;
    font-weight: bold;
}

/* 书页厚度 */
.classic-pages {
    position: absolute;
    right: 0px;
    top: 8px;
    bottom: 15px;
    width: 15px;
    height: 70%;
    background: #e5d5b5;
    background: linear-gradient(90deg, #eedbba, #c9aa7a);
    border-radius: 0 3px 3px 0;
    box-shadow: 0 2px 5px #2f1f0e;
    z-index: 2;
}

/* 书脊 */
.classic-spine {
    position: absolute;
    left: -8px;
    top: 0px;
    bottom: 25px;
    width: 16px;
    height: 74%;
    background: #4f3923;
    border-radius: 2px;
    box-shadow: inset -1px 0 3px #0f0a04, inset 1px 0 2px #b58f5a;
    z-index: 3;
}

/* 操作按钮 */
.book-actions {
    position: absolute;
    top: 8px;
    right: 18px;
    display: flex;
    gap: 5px;
    opacity: 0;
    transition: opacity 0.2s;
    z-index: 10;
}

.classic-cover:hover .book-actions {
    opacity: 1;
}

.action-btn {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    border: none;
    background: rgba(245, 235, 215, 0.9);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    cursor: pointer;
    font-size: 1rem;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: 0.15s;
    border: 1px solid #b48b5c;
}

.edit-btn {
    color: #2a5c8a;
}

.edit-btn:hover {
    background: #d9e3f0;
    transform: scale(1.1);
}

.delete-btn {
    color: #b33c2e;
}

.delete-btn:hover {
    background: #ffe1db;
    transform: scale(1.1);
}

/* 譜名 */
.classic-caption {
    margin-top: 18px;
    font-size: 1.2rem;
    font-weight: bold;
    color: #382915;
    background: #c9a97a;
    padding: 8px 20px;
    border-radius: 30px;
    box-shadow: 0 4px 0 #6d4d2b, inset 0 1px 6px #fae7c8;
    border: 1px solid #b48b57;
    text-align: center;
    width: fit-content;
    max-width: 100%;
    transition: 0.2s;
    cursor: pointer;
}

.classic-caption:hover {
    background: #dbbc8f;
    box-shadow: 0 6px 0 #7d5a33;
    transform: translateY(-2px);
}

/* 空狀態 */
.clan-empty {
    width: 100%;
    padding: 80px 20px;
    text-align: center;
    font-size: 1.6rem;
    color: #8b704d;
    background: rgba(200, 170, 130, 0.1);
    border-radius: 50px;
}

/* 底部 */
.clan-footer {
    margin-top: 40px;
    background: #b29063;
    padding: 12px 40px;
    border-radius: 40px;
    color: #fef3e0;
    font-size: 1.1rem;
    box-shadow: 0 5px 0 #5d4229, inset 0 1px 8px #ffefcf;
    border: 1px solid #ddb585;
}

/* ========== 弹出框样式 ========== */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(30, 20, 10, 0.6);
    backdrop-filter: blur(3px);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background: #f2e5d2;
    width: 500px;
    max-width: 90%;
    border-radius: 30px 30px 20px 20px;
    box-shadow: 0 20px 30px #1a130b, 0 0 0 2px #bb9a70, 0 0 0 5px #876e4a;
    overflow: hidden;
}

.confirm-modal {
    width: 400px;
}

.modal-header {
    background: #b59569;
    padding: 18px 25px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 3px solid #7b5b39;
}

.modal-header h2 {
    margin: 0;
    color: #2b1d0d;
    font-size: 1.6rem;
    font-weight: normal;
}

.close-btn {
    background: none;
    border: none;
    font-size: 1.8rem;
    cursor: pointer;
    color: #4f3621;
    line-height: 1;
}

.close-btn:hover {
    color: #1f1207;
}

.modal-body {
    padding: 25px;
}

.form-group {
    margin-bottom: 20px;
}

.form-row {
    display: flex;
    gap: 15px;
}

.form-group.half {
    flex: 1;
}

label {
    display: block;
    margin-bottom: 8px;
    color: #4f3621;
    font-weight: bold;
    font-size: 1rem;
}

.required {
    color: #b33c2e;
}

.form-group input {
    width: 100%;
    padding: 12px 15px;
    border: 2px solid #c9a97a;
    border-radius: 30px;
    background: #fff9ed;
    font-size: 1rem;
    font-family: inherit;
    box-sizing: border-box;
    outline: none;
    transition: 0.15s;
}

.form-group input:focus {
    border-color: #9e7a53;
    box-shadow: 0 0 0 3px #e6cbaa;
}

.form-hint {
    display: block;
    margin-top: 5px;
    font-size: 0.85rem;
    color: #8b6f4b;
    font-style: italic;
}

.modal-footer {
    padding: 20px 25px;
    display: flex;
    justify-content: flex-end;
    gap: 15px;
    border-top: 2px solid #d6b48b;
}

.btn-cancel {
    padding: 10px 25px;
    border: none;
    border-radius: 30px;
    background: #b7a07e;
    color: #2b1d0e;
    font-size: 1rem;
    cursor: pointer;
    box-shadow: 0 3px 0 #6f5539;
    font-family: inherit;
}

.btn-cancel:hover {
    background: #c9b595;
    transform: translateY(-1px);
    box-shadow: 0 5px 0 #6f5539;
}

.btn-save {
    padding: 10px 35px;
    border: none;
    border-radius: 30px;
    background: #9e7e5a;
    color: #fcf0dd;
    font-size: 1rem;
    font-weight: bold;
    cursor: pointer;
    box-shadow: 0 3px 0 #5a402b;
    font-family: inherit;
}

.btn-save:hover:not(:disabled) {
    background: #b5956b;
    transform: translateY(-1px);
    box-shadow: 0 5px 0 #5a402b;
}

.btn-save:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.btn-delete-confirm {
    padding: 10px 35px;
    border: none;
    border-radius: 30px;
    background: #b3523e;
    color: white;
    font-size: 1rem;
    font-weight: bold;
    cursor: pointer;
    box-shadow: 0 3px 0 #712e1f;
    font-family: inherit;
}

.btn-delete-confirm:hover {
    background: #c96a56;
    transform: translateY(-1px);
    box-shadow: 0 5px 0 #712e1f;
}

.warning-text {
    color: #b33c2e;
    font-size: 0.9rem;
    margin-top: 5px;
}

/* 响应式 */
@media (max-width: 800px) {
    .manager-header {
        flex-direction: column;
        align-items: stretch;
    }

    .clan-plaque h1 {
        font-size: 2.2rem;
    }

    .header-tools {
        justify-content: space-between;
    }

    .search-box {
        width: 100%;
        max-width: 300px;
    }

    .clan-book {
        width: 170px;
    }

    .classic-cover {
        height: 230px;
    }

    .classic-label {
        font-size: 1.2rem;
        padding: 8px 12px;
    }
}

@media (max-width: 450px) {
    .clan-book {
        width: 150px;
    }

    .classic-cover {
        height: 200px;
    }

    .clan-footer {
        font-size: 0.9rem;
        padding: 8px 15px;
    }

    .form-row {
        flex-direction: column;
        gap: 0;
    }
}
</style>