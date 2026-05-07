<template>
    <div class="app-container">
        <!-- ==================== 顶部状态栏 ==================== -->
        <div class="top-bar">
            <div class="stats-group">
                <div class="stat-item">
                    <div class="stat-icon">📦</div>
                    <div class="stat-info">
                        <div class="stat-value">{{ totalProducts }}</div>
                        <div class="stat-label">商品总数</div>
                    </div>
                </div>
                <div class="stat-item">
                    <div class="stat-icon">⭐</div>
                    <div class="stat-info">
                        <div class="stat-value">{{ avgRatingFormatted }}</div>
                        <div class="stat-label">平均评分</div>
                    </div>
                </div>
            </div>

            <div class="action-group">
                <div class="search-box">
                    <span class="search-icon">🔍</span>
                    <input type="text" v-model="globalSearchKeyword" placeholder="搜索商品名称或品牌..." class="search-input"
                        @input="onGlobalSearch" />
                    <button v-if="globalSearchKeyword" @click="clearGlobalSearch" class="clear-search">✕</button>
                </div>
                <button class="add-btn" @click="openAddProductModal">
                    <span>+</span> 添加商品
                </button>
            </div>
        </div>

        <!-- ==================== 内容区域 ==================== -->
        <div class="main-content" v-if="selectedProduct">
            <!-- 左侧区域：商品选择器 + 价格走势图 -->
            <div class="left-panel">
                <!-- 商品切换卡片（分页版） -->
                <div class="product-selector">
                    <div class="selector-header">
                        <h3>📋 商品列表</h3>
                        <span class="product-count">{{ filteredProducts.length }}个商品</span>
                    </div>

                    <!-- 分页商品列表 -->
                    <div class="product-tabs-container">
                        <div v-for="product in paginatedProducts" :key="product.id"
                            :class="['product-tab', { active: selectedProduct && selectedProduct.id === product.id }]"
                            @click="selectProduct(product)">
                            <div class="tab-info">
                                <div class="tab-name-wrapper">
                                    <span class="tab-name">{{ product.name }}</span>
                                    <span class="tab-stars" v-if="product.rating">
                                        {{ '⭐'.repeat(product.rating) }}
                                    </span>
                                </div>
                                <div class="tab-meta">
                                    <span v-if="product.brand" class="tab-brand">品牌：{{ product.brand }}</span>
                                    <span v-if="product.specification" class="tab-brand">规格：{{ product.specification
                                    }}</span>
                                    <span v-if="product.origin" class="tab-brand">产地：{{ product.origin }}</span>
                                    <span v-if="product.remarks" class="tab-brand">备注：{{ product.remarks }}</span>
                                </div>
                            </div>
                            <div class="tab-actions">
                                <button @click.stop="openEditProductModal(product)" class="tab-edit" title="编辑">
                                    <i class="el-icon-edit"></i>
                                </button>
                                <button @click.stop="deleteProduct(product.id)" class="tab-delete" title="删除">
                                    <i class="el-icon-delete"></i>
                                </button>
                            </div>
                        </div>
                        <div v-if="paginatedProducts.length === 0" class="no-product-tab">
                            暂无匹配的商品
                        </div>
                    </div>

                    <!-- 分页组件 -->
                    <div class="pagination-wrapper" v-if="totalPages > 1 || searchedProducts.length > pageSize">
                        <div class="pagination-info">
                            共 {{ searchedProducts.length }} 条，第 {{ currentPage }}/{{ totalPages }} 页
                        </div>
                        <div class="pagination-controls">
                            <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--">
                                «
                            </button>
                            <button v-for="page in displayPages" :key="page"
                                :class="['page-btn', { active: currentPage === page }]" @click="goToPage(page)">
                                {{ page }}
                            </button>
                            <button class="page-btn" :disabled="currentPage === totalPages" @click="currentPage++">
                                »
                            </button>
                            <select v-model="pageSize" @change="resetPagination" class="page-size-select">
                                <option :value="5">5条/页</option>
                                <option :value="10">10条/页</option>
                                <option :value="20">20条/页</option>
                                <option :value="50">50条/页</option>
                            </select>
                        </div>
                    </div>
                </div>

                <!-- 价格走势图 -->
                <div class="chart-card">
                    <div class="card-header">
                        <h3>📈 价格走势图</h3>
                        <div class="chart-legend">
                            <span class="legend-dot"></span>
                            <span>历史价格趋势</span>
                        </div>
                    </div>
                    <div class="chart-container" ref="chartContainer"></div>
                    <div v-if="!selectedProduct || !selectedProduct.prices || selectedProduct.prices.length === 0"
                        class="chart-empty">
                        <span>📊</span>
                        <p>暂无价格数据，请添加价格记录</p>
                    </div>
                </div>
            </div>

            <!-- 右侧区域：历史价格列表（可按日期搜索） -->
            <div class="right-panel">
                <div class="history-card">
                    <div class="card-header">
                        <h3>📜 历史价格记录</h3>
                        <button class="add-price-btn" @click="openAddPriceModal">
                            + 添加价格
                        </button>
                    </div>

                    <!-- 日期搜索栏 -->
                    <div class="date-search-bar">
                        <div class="date-range">
                            <div class="date-input-wrapper">
                                <span class="date-label">从</span>
                                <input type="date" v-model="dateFrom" class="date-input" />
                            </div>
                            <div class="date-input-wrapper">
                                <span class="date-label">至</span>
                                <input type="date" v-model="dateTo" class="date-input" />
                            </div>
                            <select v-model="sortType" class="sort-select">
                                <option value="date_desc">按日期（最新优先）</option>
                                <option value="date_asc">按日期（最早优先）</option>
                                <option value="price_asc">按价格（从低到高）</option>
                                <option value="price_desc">按价格（从高到低）</option>
                            </select>
                            <button @click="clearDateFilter" class="clear-date-btn" v-if="dateFrom || dateTo">清除筛选</button>
                        </div>
                    </div>

                    <!-- 价格列表 -->
                    <div class="price-list-container">
                        <div v-if="filteredPrices.length > 0" class="price-table">
                            <div class="price-table-header">
                                <div class="col-date">日期</div>
                                <div class="col-price">价格 (¥)</div>
                                <div class="col-change">涨跌幅</div>
                                <div class="col-action">操作</div>
                            </div>
                            <div class="price-table-body">
                                <div v-for="(item, idx) in filteredPrices" :key="idx" class="price-table-row">
                                    <div class="col-date">
                                        <span class="date-badge">{{ formatDate(item.date) }}</span>
                                    </div>
                                    <div class="col-price">
                                        <span class="tab-price">¥{{ item.value.toFixed(2) }}</span>
                                    </div>
                                    <div class="col-change">
                                        <span :class="['change-badge', getChangeClass(item, idx)]">
                                            {{ getChangeText(item, idx) }}
                                        </span>
                                    </div>
                                    <div class="col-action">
                                        <button @click="deletePriceItem(idx)" class="delete-price-btn"
                                            title="删除此记录">✕</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div v-else class="empty-price-list">
                            <span>📭</span>
                            <p v-if="dateFrom || dateTo">没有找到该时间范围内的价格记录</p>
                            <p v-else>暂无历史价格，点击“添加价格”开始记录</p>
                        </div>
                    </div>

                    <!-- 价格统计摘要 -->
                    <div class="price-summary"
                        v-if="selectedProduct && selectedProduct.prices && selectedProduct.prices.length > 0">
                        <div class="summary-item">
                            <span class="summary-label">最低价</span>
                            <span class="summary-value min">¥{{ minPrice.toFixed(2) }}</span>
                        </div>
                        <div class="summary-item">
                            <span class="summary-label">最高价</span>
                            <span class="summary-value max">¥{{ maxPrice.toFixed(2) }}</span>
                        </div>
                        <div class="summary-item">
                            <span class="summary-label">平均价</span>
                            <span class="summary-value avg">¥{{ avgPrice.toFixed(2) }}</span>
                        </div>
                        <div class="summary-item">
                            <span class="summary-label">最新价</span>
                            <span class="summary-value latest">¥{{ latestPrice.toFixed(2) }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 未选择商品时的空状态 -->
        <div class="empty-state-large" v-else>
            <div class="empty-icon">🛒</div>
            <h3>暂无商品</h3>
            <p>点击上方“添加商品”按钮开始记录商品价格</p>
            <button class="btn-primary" @click="openAddProductModal">+ 添加商品</button>
        </div>

        <!-- ==================== 弹窗 ==================== -->
        <!-- 添加/编辑商品弹窗 -->
        <div v-if="showProductModal" class="modal" @click.self="closeProductModal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>{{ productModalTitle }}</h3>
                    <button class="modal-close" @click="closeProductModal">✕</button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label>商品名称 <span class="required">*</span></label>
                        <input type="text" v-model="editingProduct.name" placeholder="例：iPhone 15 Pro" />
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>品牌</label>
                            <input type="text" v-model="editingProduct.brand" placeholder="例：Apple" />
                        </div>
                        <div class="form-group">
                            <label>产地</label>
                            <input type="text" v-model="editingProduct.origin" placeholder="例：中国" />
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>星级评分</label>
                            <div class="rating-select">
                                <span v-for="n in 5" :key="n" class="star" @click="editingProduct.rating = n">
                                    {{ n <= (editingProduct.rating || 0) ? '⭐' : '☆' }} </span>
                                        <span v-if="editingProduct.rating" class="clear-rating"
                                            @click="editingProduct.rating = null">清除</span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label>规格</label>
                            <input type="text" v-model="editingProduct.specification" placeholder="例：台" />
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label>备注</label>
                            <input type="text" v-model="editingProduct.remarks" placeholder="其他信息" />
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn-cancel" @click="closeProductModal">取消</button>
                    <button class="btn-save" @click="saveProduct">保存</button>
                </div>
            </div>
        </div>

        <!-- 添加价格弹窗 -->
        <div v-if="showPriceModal" class="modal" @click.self="closePriceModal">
            <div class="modal-content modal-small">
                <div class="modal-header">
                    <h3>添加价格记录</h3>
                    <button class="modal-close" @click="closePriceModal">✕</button>
                </div>
                <div class="modal-body">
                    <div class="price-product-info" v-if="selectedProduct">
                        <span class="product-badge">{{ selectedProduct.name }}</span>
                    </div>
                    <div class="form-group">
                        <label>日期 <span class="required">*</span></label>
                        <input type="date" v-model="newPrice.date" :max="todayDate" />
                    </div>
                    <div class="form-group">
                        <label>价格 (¥) <span class="required">*</span></label>
                        <input type="number" step="0.01" v-model.number="newPrice.value" placeholder="0.00" />
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn-cancel" @click="closePriceModal">取消</button>
                    <button class="btn-save" @click="savePrice">添加</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { getProduct, addProduct, editProduct, delProduct, addPriceRecord, getRecordsByProductId, delPriceRecord, } from "@/api/api.js";
import { formatDate } from '@/utils/formatDate'

export default {
    name: 'PriceTracker',
    data() {
        return {
            sortType: 'date_desc', // date_desc, date_asc, price_asc, price_desc
            products: [],
            globalSearchKeyword: '',
            productListSearch: '',
            selectedProduct: null,
            dateFrom: '',
            dateTo: '',
            showProductModal: false,
            showPriceModal: false,
            isEditMode: false,
            // 分页相关
            currentPage: 1,
            pageSize: 10,
            editingProduct: {
                id: null,
                name: '',
                brand: '',
                origin: '',
                specification: '',
                rating: null,
                remarks: '',
                prices: [],
                createdAt: '',
                isDeleted: 0,
            },
            selectPrices: {},
            newPrice: {
                date: '',
                value: null
            },
            chartInstance: null
        }
    },
    computed: {
        totalProducts() {
            return this.products.length;
        },
        avgRatingFormatted() {
            const rated = this.products.filter(p => p.rating);
            if (rated.length === 0) return '--';
            const sum = rated.reduce((s, p) => s + p.rating, 0);
            return (sum / rated.length).toFixed(1);
        },
        // 全局搜索过滤后的商品
        filteredProducts() {
            if (!this.globalSearchKeyword.trim()) return this.products;
            const kw = this.globalSearchKeyword.trim().toLowerCase();
            return this.products.filter(p =>
                p.name.toLowerCase().includes(kw) ||
                (p.brand && p.brand.toLowerCase().includes(kw))
            );
        },
        // 左侧商品列表内的二次搜索（在全局搜索结果基础上再搜索）
        searchedProducts() {
            if (!this.productListSearch.trim()) return this.filteredProducts;
            const kw = this.productListSearch.trim().toLowerCase();
            return this.filteredProducts.filter(p =>
                p.name.toLowerCase().includes(kw) ||
                (p.brand && p.brand.toLowerCase().includes(kw))
            );
        },
        // 分页后的商品列表
        paginatedProducts() {
            const start = (this.currentPage - 1) * this.pageSize;
            const end = start + this.pageSize;
            return this.searchedProducts.slice(start, end);
        },
        // 总页数
        totalPages() {
            return Math.ceil(this.searchedProducts.length / this.pageSize) || 1;
        },
        // 显示的页码（最多5个）
        displayPages() {
            const total = this.totalPages;
            const current = this.currentPage;
            const maxVisible = 5;
            let start = Math.max(1, current - Math.floor(maxVisible / 2));
            let end = Math.min(total, start + maxVisible - 1);
            if (end - start + 1 < maxVisible) {
                start = Math.max(1, end - maxVisible + 1);
            }
            const pages = [];
            for (let i = start; i <= end; i++) {
                pages.push(i);
            }
            return pages;
        },
        filteredPrices() {
            if (!this.selectedProduct || !this.selectedProduct.prices) return [];
            let prices = [...this.selectedProduct.prices];

            // 根据选择的排序方式排序
            switch (this.sortType) {
                case 'date_asc':
                    prices.sort((a, b) => new Date(a.date) - new Date(b.date));
                    break;
                case 'price_asc':
                    prices.sort((a, b) => a.value - b.value);
                    break;
                case 'price_desc':
                    prices.sort((a, b) => b.value - a.value);
                    break;
                default: // date_desc
                    prices.sort((a, b) => new Date(b.date) - new Date(a.date));
            }

            // 日期筛选
            if (this.dateFrom) {
                prices = prices.filter(p => new Date(p.date) >= new Date(this.dateFrom));
            }
            if (this.dateTo) {
                prices = prices.filter(p => new Date(p.date) <= new Date(this.dateTo));
            }
            return prices;
        },
        minPrice() {
            if (!this.selectedProduct?.prices?.length) return 0;
            return Math.min(...this.selectedProduct.prices.map(p => p.value));
        },
        maxPrice() {
            if (!this.selectedProduct?.prices?.length) return 0;
            return Math.max(...this.selectedProduct.prices.map(p => p.value));
        },
        avgPrice() {
            if (!this.selectedProduct?.prices?.length) return 0;
            const sum = this.selectedProduct.prices.reduce((s, p) => s + p.value, 0);
            return sum / this.selectedProduct.prices.length;
        },
        latestPrice() {
            if (!this.selectedProduct?.prices?.length) return 0;
            const sorted = [...this.selectedProduct.prices].sort((a, b) => new Date(b.date) - new Date(a.date));
            return sorted[0].value;
        },
        todayDate() {
            return new Date().toISOString().slice(0, 10);
        },
        productModalTitle() {
            return this.isEditMode ? '编辑商品信息' : '添加新商品';
        }
    },
    watch: {
        selectedProduct: {
            handler() {
                this.$nextTick(() => {
                    this.drawChart();
                });
            },
            deep: true,
            immediate: true
        },
        'selectedProduct.prices': {
            handler() {
                this.$nextTick(() => {
                    this.drawChart();
                });
            },
            deep: true
        },
        searchedProducts: {
            handler(newList) {
                // 当搜索结果变化时，重置到第一页
                this.currentPage = 1;

                if (newList.length > 0) {
                    // 如果当前没有选中的商品，或者选中的商品不在搜索结果中
                    if (!this.selectedProduct || !newList.find(p => p.id === this.selectedProduct.id)) {
                        this.selectProduct(newList[0]);
                    }
                } else {
                    // 搜索结果为空时，清空选中的商品
                    this.selectedProduct = null;
                }
            },
            immediate: true
        }
    },
    mounted() {
        this.loadData();
    },
    beforeDestroy() {
        if (this.chartInstance) {
            this.chartInstance.remove();
        }
    },
    methods: {
        async loadData() {
            const res = await getProduct();

            if (res.data.data) {
                this.products = res.data.data;
            }
            if (this.products.length > 0 && !this.selectedProduct) {
                this.selectProduct(this.products[0])
            }
        },
        onGlobalSearch() {
            // 触发 computed 更新
        },
        //清空搜索框
        clearGlobalSearch() {
            this.globalSearchKeyword = '';
            // 清空搜索后，重新初始化商品列表
            if (this.products.length > 0) {
                // 如果当前没有选中商品，或者选中的商品不在商品列表中
                if (!this.selectedProduct || !this.products.find(p => p.id === this.selectedProduct.id)) {
                    this.selectProduct(this.products[0]);
                }
            }
        },
        resetPagination() {
            this.currentPage = 1;
        },
        goToPage(page) {
            this.currentPage = page;
        },
        async selectProduct(product) {
            if (this.selectedProduct) {
                if (this.selectedProduct.id == product.id)
                    return
            }
            // 先清空当前选中的商品，触发重新渲染
            this.selectedProduct = null;

            const res = await getRecordsByProductId(product.id);
            try {
                if (res.data.data) {
                    const prices = [];
                    res.data.data.forEach(element => {
                        prices.push({
                            id: element.id,
                            date: element.recordDate,
                            value: element.price
                        });
                    });
                    // 创建新的商品对象，包含价格记录
                    const productWithPrices = {
                        ...product,
                        prices: prices
                    };
                    this.selectedProduct = productWithPrices;

                    // 同步更新 products 数组中的对应商品
                    const index = this.products.findIndex(p => p.id === product.id);
                    if (index !== -1) {
                        this.products[index] = productWithPrices;
                    }
                }
            } catch (err) {
                console.log("历史价格记录获取失败:", err)
            }

            this.dateFrom = '';
            this.dateTo = '';

            // 确保图表在数据更新后绘制
            this.$nextTick(() => {
                this.drawChart();
            });

        },
        formatDate(dateStr) {
            const date = new Date(dateStr);
            return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`;
        },
        getChangeClass(item, idx) {
            const prices = this.filteredPrices;
            if (idx === prices.length - 1) return 'no-change';
            const prevPrice = prices[idx + 1].value;
            if (item.value > prevPrice) return 'up';
            if (item.value < prevPrice) return 'down';
            return 'flat';
        },
        getChangeText(item, idx) {
            const prices = this.filteredPrices;
            if (idx === prices.length - 1) return '--';
            const prevPrice = prices[idx + 1].value;
            const change = ((item.value - prevPrice) / prevPrice * 100).toFixed(1);
            if (item.value > prevPrice) return `+${change}%`;
            if (item.value < prevPrice) return `${change}%`;
            return '0%';
        },
        clearDateFilter() {
            this.dateFrom = '';
            this.dateTo = '';
        },
        deletePriceItem(idx) {
            const actualPrice = this.filteredPrices[idx];
            if (!actualPrice) return;

            this.$confirm(`确定删除 ${this.formatDate(actualPrice.date)} 的价格记录 ¥${actualPrice.value} 吗？`, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                delPriceRecord(actualPrice.id).then(res => {
                    if (res.data.status === 200) {
                        const indexInOriginal = this.selectedProduct.prices.findIndex(
                            p => p.date === actualPrice.date && p.value === actualPrice.value
                        );
                        if (indexInOriginal !== -1) {
                            this.selectedProduct.prices.splice(indexInOriginal, 1);
                            this.$nextTick(() => this.drawChart());
                        }
                    }

                });

            }).catch(() => {
                this.$message.info('已取消删除');
            });

        },
        drawChart() {
            const container = this.$refs.chartContainer;
            if (!container) return;
            const prices = this.selectedProduct?.prices;
            if (!prices || prices.length === 0) {
                container.innerHTML = '';
                return;
            }
            const sorted = [...prices].sort((a, b) => new Date(a.date) - new Date(b.date));
            const width = container.clientWidth;
            const height = 280;
            container.innerHTML = '';
            const canvas = document.createElement('canvas');
            canvas.width = width;
            canvas.height = height;
            canvas.style.width = '100%';
            canvas.style.height = '280px';
            container.appendChild(canvas);
            const ctx = canvas.getContext('2d');
            if (!ctx) return;

            const padding = { top: 20, right: 30, bottom: 30, left: 45 };
            const chartWidth = width - padding.left - padding.right;
            const chartHeight = height - padding.top - padding.bottom;

            const values = sorted.map(p => p.value);
            const minVal = Math.min(...values);
            const maxVal = Math.max(...values);
            const range = maxVal - minVal || 1;

            const getX = (i) => padding.left + (i / (values.length - 1 || 1)) * chartWidth;
            const getY = (val) => padding.top + chartHeight - ((val - minVal) / range) * chartHeight;

            ctx.save();
            ctx.strokeStyle = '#e2e8f0';
            ctx.fillStyle = '#64748b';
            ctx.font = '11px Inter, sans-serif';
            for (let i = 0; i <= 4; i++) {
                const y = padding.top + (i / 4) * chartHeight;
                ctx.beginPath();
                ctx.moveTo(padding.left, y);
                ctx.lineTo(width - padding.right, y);
                ctx.stroke();
                const val = maxVal - (i / 4) * range;
                ctx.fillText(val.toFixed(0), 5, y + 3);
            }

            ctx.beginPath();
            ctx.strokeStyle = '#2c7da0';
            ctx.lineWidth = 2.5;
            ctx.lineJoin = 'round';
            for (let i = 0; i < values.length; i++) {
                const x = getX(i);
                const y = getY(values[i]);
                if (i === 0) ctx.moveTo(x, y);
                else ctx.lineTo(x, y);
            }
            ctx.stroke();

            for (let i = 0; i < values.length; i++) {
                const x = getX(i);
                const y = getY(values[i]);
                ctx.beginPath();
                ctx.fillStyle = '#2c7da0';
                ctx.arc(x, y, 5, 0, 2 * Math.PI);
                ctx.fill();
                ctx.fillStyle = 'white';
                ctx.beginPath();
                ctx.arc(x, y, 2, 0, 2 * Math.PI);
                ctx.fill();

                ctx.fillStyle = '#475569';
                ctx.font = '10px Inter';
                const dateStr = sorted[i].date.slice(5);
                ctx.fillText(dateStr, x - 12, y - 8);
            }

            ctx.fillStyle = '#64748b';
            ctx.font = '10px Inter';
            for (let i = 0; i < values.length; i++) {
                const x = getX(i);
                ctx.fillText(sorted[i].date.slice(5), x - 10, height - 8);
            }
            ctx.restore();
        },
        openAddProductModal() {
            this.isEditMode = false;
            this.editingProduct = {
                id: Date.now(),
                name: '',
                brand: '',
                origin: '',
                specification: '',
                rating: null,
                remarks: '',
                prices: [],
                createdAt: '',
                isDeleted: 0,
            };
            this.showProductModal = true;
        },
        //打开商品信息模态框
        openEditProductModal(product) {
            this.isEditMode = true;
            this.editingProduct = JSON.parse(JSON.stringify(product));
            this.showProductModal = true;
        },
        //保存商品信息
        async saveProduct() {
            if (!this.editingProduct.name.trim()) {
                alert('请填写商品名称');
                return;
            }
            if (this.isEditMode) {
                const index = this.products.findIndex(p => p.id === this.editingProduct.id);
                if (index !== -1) {
                    this.products[index] = this.editingProduct;
                    if (this.selectedProduct?.id === this.editingProduct.id) {
                        this.selectedProduct = this.products[index];
                    }
                    const res = await editProduct(this.editingProduct);
                    if (res.data.status === 200) {
                        this.$message.success('保存成功');
                        this.loadData();
                    } else {
                        this.$message.error('保存失败');
                        console.log(res.data.msg);
                    }
                }
            } else {
                this.products.push(this.editingProduct);
                if (!this.selectedProduct) {
                    this.selectedProduct = this.editingProduct;
                }
                const res = await addProduct(this.editingProduct);
                if (res.data.status === 200) {
                    this.$message.success('保存成功');
                } else {
                    this.$message.error('保存失败');
                    console.log(res.data.msg);
                }
            }


            this.closeProductModal();
        },
        //删除商品信息
        deleteProduct(id) {
            this.$confirm('确定删除该商品及其所有价格记录吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.products = this.products.filter(p => p.id !== id);
                if (this.selectedProduct?.id === id) {
                    this.selectedProduct = this.products[0] || null;
                }
                delProduct(id).then(res => {
                    if (res.data.status === 200) {
                        this.$message.success('删除成功');
                    } else {
                        this.$message.error('删除失败');
                        console.log(res.data.msg);
                    }
                });

            }).catch(() => {
                this.$message.info('已取消删除');
            });
        },
        //打开添加价格模态框
        openAddPriceModal() {
            if (!this.selectedProduct) return;
            this.newPrice = {
                date: this.todayDate,
                value: null
            };
            this.showPriceModal = true;
        },
        //保存历史价格
        async savePrice() {
            if (!this.newPrice.date) {
                this.$message.error('请选择日期');
                return;
            }
            if (!this.newPrice.value || this.newPrice.value <= 0) {
                this.$message.error('请输入正确的价格');
                return;
            }
            if (!this.selectedProduct.prices) this.selectedProduct.prices = [];
            const exists = this.selectedProduct.prices.some(p => p.date === this.newPrice.date);
            if (exists) {
                this.$message.error('该日期已存在价格记录');
                return;
            }
            const priceData = {
                id: Date.now(),
                productId: this.selectedProduct.id,
                price: this.newPrice.value,
                recordDate: this.newPrice.date
            }

            try {
                const res = await addPriceRecord(priceData);
                if (res.data.status === 200) {
                    this.selectedProduct.prices.push({
                        date: this.newPrice.date,
                        value: this.newPrice.value
                    });
                    this.$message.success('价格添加成功');
                    this.selectedProduct.prices.sort((a, b) => new Date(a.date) - new Date(b.date));
                    this.closePriceModal();
                    this.$nextTick(() => this.drawChart());
                }
            } catch (err) {
                console.log("err", err)
            }

        },
        closeProductModal() {
            this.showProductModal = false;
        },
        closePriceModal() {
            this.showPriceModal = false;
        }
    }
}
</script>

<style scoped>
.app-container {
    min-height: 100vh;
    background: #f0f2f5;
    font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 顶部状态栏 */
.top-bar {
    background: white;
    padding: 16px 32px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 20px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    border-bottom: 1px solid #e9ecef;
}

.stats-group {
    display: flex;
    gap: 32px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 12px;
}

.stat-icon {
    font-size: 28px;
}

.stat-info {
    display: flex;
    flex-direction: column;
}

.stat-value {
    font-size: 24px;
    font-weight: 700;
    color: #1e293b;
    line-height: 1.2;
}

.stat-label {
    font-size: 12px;
    color: #64748b;
    letter-spacing: 0.3px;
}

.action-group {
    display: flex;
    gap: 16px;
    align-items: center;
}

.search-box {
    position: relative;
    display: flex;
    align-items: center;
}

.search-icon {
    position: absolute;
    left: 14px;
    font-size: 14px;
    opacity: 0.6;
}

.search-input {
    padding: 10px 16px 10px 38px;
    border: 1px solid #e2e8f0;
    border-radius: 40px;
    width: 260px;
    font-size: 14px;
    background: #f8fafc;
    transition: all 0.2s;
}

.search-input:focus {
    outline: none;
    border-color: #2c7da0;
    background: white;
    box-shadow: 0 0 0 3px rgba(44, 125, 160, 0.1);
}

.clear-search {
    position: absolute;
    right: 12px;
    background: none;
    border: none;
    cursor: pointer;
    color: #94a3b8;
    font-size: 14px;
}

.add-btn {
    background: #2c7da0;
    color: white;
    border: none;
    padding: 10px 24px;
    border-radius: 40px;
    font-weight: 500;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;
    transition: all 0.2s;
}

.add-btn:hover {
    background: #1f5e7a;
    transform: translateY(-1px);
}

/* 主内容区域 */
.main-content {
    max-width: 1600px;
    margin: 0 auto;
    padding: 24px;
    display: flex;
    gap: 24px;
    min-height: calc(100vh - 90px);
}

.left-panel {
    flex: 1.2;
    display: flex;
    flex-direction: column;
    gap: 24px;
}

.right-panel {
    flex: 1;
}

/* 商品选择器 - 分页版 */
.product-selector {
    background: white;
    border-radius: 24px;
    overflow: hidden;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    display: flex;
    flex-direction: column;
}

.selector-header {
    padding: 16px 20px;
    background: #fafbfc;
    border-bottom: 1px solid #eef2f6;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-shrink: 0;
}

.selector-header h3 {
    font-size: 16px;
    font-weight: 600;
    color: #1e293b;
}

.product-count {
    font-size: 12px;
    color: #64748b;
}

/* 商品列表容器 */
.product-tabs-container {
    max-height: 380px;
    overflow-y: auto;
    flex: 1;
}

.product-tab {
    padding: 12px 16px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #f1f5f9;
    cursor: pointer;
    transition: background 0.2s;
}

.product-tab:hover {
    background: #f8fafc;
}

.product-tab.active {
    background: #eef2ff;
    border-left: 3px solid #2c7da0;
}

.tab-info {
    flex: 1;
}

.tab-name-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 6px;
    flex-wrap: wrap;
}

.tab-name {
    font-weight: 600;
    color: #1e293b;
    font-size: 14px;
}

.tab-stars {
    font-size: 12px;
    color: #f5a623;
    letter-spacing: 1px;
}

.tab-meta {
    display: flex;
    gap: 12px;
    font-size: 12px;
    color: #64748b;
    flex-wrap: wrap;
}

.tab-brand {
    background: #f1f5f9;
    padding: 2px 8px;
    border-radius: 12px;
}

.tab-price {
    font-weight: 600;
    color: #e67e22;
}

.tab-actions {
    display: flex;
    gap: 6px;
}

.tab-edit,
.tab-delete {
    background: none;
    border: none;
    cursor: pointer;
    font-size: 14px;
    padding: 4px;
    border-radius: 8px;
}

.tab-edit:hover {
    background: #e2e8f0;
}

.tab-delete:hover {
    background: #fee2e2;
    color: #ef4444;
}

.no-product-tab {
    padding: 40px;
    text-align: center;
    color: #94a3b8;
}

/* 分页组件样式 */
.pagination-wrapper {
    padding: 12px 16px;
    border-top: 1px solid #eef2f6;
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;
    background: #fafbfc;
}

.pagination-info {
    font-size: 12px;
    color: #64748b;
}

.pagination-controls {
    display: flex;
    gap: 6px;
    align-items: center;
    flex-wrap: wrap;
}

.page-btn {
    min-width: 32px;
    height: 32px;
    padding: 0 8px;
    border: 1px solid #e2e8f0;
    background: white;
    border-radius: 8px;
    cursor: pointer;
    font-size: 13px;
    transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
    background: #eef2ff;
    border-color: #2c7da0;
    color: #2c7da0;
}

.page-btn.active {
    background: #2c7da0;
    border-color: #2c7da0;
    color: white;
}

.page-btn:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.page-size-select {
    padding: 6px 10px;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    font-size: 12px;
    background: white;
    cursor: pointer;
}

/* 图表卡片 */
.chart-card {
    background: white;
    border-radius: 24px;
    padding: 20px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.card-header h3 {
    font-size: 18px;
    font-weight: 600;
    color: #1e293b;
}

.chart-legend {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #64748b;
}

.legend-dot {
    width: 10px;
    height: 10px;
    background: #2c7da0;
    border-radius: 10px;
}

.chart-container {
    min-height: 280px;
    position: relative;
}

.chart-empty {
    height: 280px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #94a3b8;
    gap: 12px;
}

/* 历史价格卡片 */
.history-card {
    background: white;
    border-radius: 24px;
    padding: 20px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.add-price-btn {
    background: #2c7da0;
    color: white;
    border: none;
    padding: 8px 18px;
    border-radius: 40px;
    font-size: 13px;
    cursor: pointer;
}

.date-search-bar {
    background: #f8fafc;
    padding: 12px 16px;
    border-radius: 20px;
}

.date-range {
    display: flex;
    gap: 16px;
    flex-wrap: wrap;
    align-items: center;
}

.sort-select {
    padding: 8px 12px;
    border: 1px solid #e2e8f0;
    border-radius: 20px;
    font-size: 12px;
    background: white;
    cursor: pointer;
}

.sort-select:focus {
    outline: none;
    border-color: #2c7da0;
}

.date-input-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
}

.date-label {
    font-size: 13px;
    color: #475569;
}

.date-input {
    padding: 8px 12px;
    border: 1px solid #e2e8f0;
    border-radius: 20px;
    font-size: 13px;
}

.clear-date-btn {
    background: #e2e8f0;
    border: none;
    padding: 6px 14px;
    border-radius: 30px;
    font-size: 12px;
    cursor: pointer;
}

/* 价格表格 */
.price-list-container {
    max-height: 400px;
    overflow-y: auto;
}

.price-table {
    width: 100%;
}

.price-table-header {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr 60px;
    padding: 12px 16px;
    background: #f8fafc;
    border-radius: 16px;
    font-size: 12px;
    font-weight: 600;
    color: #475569;
    margin-bottom: 8px;
}

.price-table-row {
    display: grid;
    grid-template-columns: 1fr 1fr 1fr 60px;
    padding: 14px 16px;
    border-bottom: 1px solid #f1f5f9;
    align-items: center;
}

.price-table-row:hover {
    background: #fafbfc;
}

.col-date .date-badge {
    font-size: 13px;
    font-weight: 500;
}

.price-value {
    font-weight: 700;
    color: #1e293b;
    font-size: 15px;
}

.change-badge {
    font-size: 12px;
    padding: 2px 8px;
    border-radius: 20px;
    display: inline-block;
}

.change-badge.up {
    background: #d1fae5;
    color: #059669;
}

.change-badge.down {
    background: #fee2e2;
    color: #dc2626;
}

.change-badge.flat,
.change-badge.no-change {
    background: #f1f5f9;
    color: #64748b;
}

.delete-price-btn {
    background: none;
    border: none;
    cursor: pointer;
    font-size: 16px;
    color: #cbd5e1;
    transition: color 0.2s;
}

.delete-price-btn:hover {
    color: #ef4444;
}

.empty-price-list {
    text-align: center;
    padding: 60px 20px;
    color: #94a3b8;
    display: flex;
    flex-direction: column;
    gap: 12px;
}

/* 价格摘要 */
.price-summary {
    display: flex;
    justify-content: space-between;
    background: #f8fafc;
    padding: 16px 20px;
    border-radius: 20px;
    margin-top: 8px;
}

.summary-item {
    text-align: center;
}

.summary-label {
    display: block;
    font-size: 11px;
    color: #64748b;
    margin-bottom: 4px;
}

.summary-value {
    font-size: 18px;
    font-weight: 700;
}

.summary-value.min {
    color: #059669;
}

.summary-value.max {
    color: #dc2626;
}

.summary-value.avg {
    color: #3b82f6;
}

.summary-value.latest {
    color: #e67e22;
}

/* 空状态 */
.empty-state-large {
    text-align: center;
    padding: 100px 20px;
    background: white;
    margin: 40px auto;
    max-width: 500px;
    border-radius: 40px;
}

.empty-icon {
    font-size: 64px;
    margin-bottom: 20px;
    opacity: 0.6;
}

.btn-primary {
    background: #2c7da0;
    color: white;
    border: none;
    padding: 12px 28px;
    border-radius: 40px;
    margin-top: 20px;
    cursor: pointer;
}

/* 弹窗样式 */
.modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background: white;
    border-radius: 32px;
    width: 90%;
    max-width: 560px;
    max-height: 85vh;
    overflow-y: auto;
}

.modal-small {
    max-width: 440px;
}

.modal-header {
    padding: 20px 24px;
    border-bottom: 1px solid #f0f0f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.modal-close {
    background: none;
    border: none;
    font-size: 20px;
    cursor: pointer;
    color: #94a3b8;
}

.modal-body {
    padding: 24px;
}

.modal-footer {
    padding: 16px 24px;
    border-top: 1px solid #f0f0f0;
    display: flex;
    justify-content: flex-end;
    gap: 12px;
}

.form-group {
    margin-bottom: 20px;
}

.form-group label {
    font-size: 13px;
    font-weight: 500;
    color: #475569;
    margin-bottom: 6px;
    display: block;
}

.required {
    color: #ef4444;
}

.form-group input,
.form-group textarea {
    width: 100%;
    padding: 12px 14px;
    border: 1px solid #e2e8f0;
    border-radius: 20px;
    font-size: 14px;
}

.form-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
}

.rating-select {
    display: flex;
    gap: 6px;
    align-items: center;
}

.star {
    font-size: 22px;
    cursor: pointer;
}

.clear-rating {
    font-size: 11px;
    color: #94a3b8;
    cursor: pointer;
    margin-left: 8px;
}

.price-product-info {
    text-align: center;
    margin-bottom: 20px;
}

.product-badge {
    background: #eef2ff;
    padding: 6px 16px;
    border-radius: 40px;
    font-size: 14px;
}

.btn-cancel {
    background: #f1f5f9;
    border: none;
    padding: 10px 24px;
    border-radius: 40px;
    cursor: pointer;
}

.btn-save {
    background: #2c7da0;
    color: white;
    border: none;
    padding: 10px 28px;
    border-radius: 40px;
    cursor: pointer;
}

@media (max-width: 1000px) {
    .main-content {
        flex-direction: column;
    }

    .stats-group {
        width: 100%;
        justify-content: space-between;
    }

    .top-bar {
        flex-direction: column;
        align-items: stretch;
    }

    .action-group {
        justify-content: space-between;
    }
}
</style>