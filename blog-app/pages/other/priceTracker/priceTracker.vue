<template>
	<view class="app-container">
		<!-- ==================== 顶部状态栏 ==================== -->
		<view class="top-bar">
			<view class="stats-group">
				<view class="stat-item">
					<view class="stat-icon">📦</view>
					<view class="stat-info">
						<view class="stat-value">{{ totalProducts }}</view>
						<view class="stat-label">商品总数</view>
					</view>
				</view>
				<view class="stat-item">
					<view class="stat-icon">⭐</view>
					<view class="stat-info">
						<view class="stat-value">{{ avgRatingFormatted }}</view>
						<view class="stat-label">平均评分</view>
					</view>
				</view>
			</view>

			<view class="action-group">
				<view class="search-box">
					<text class="search-icon">🔍</text>
					<input type="text" v-model="globalSearchKeyword" placeholder="搜索商品名称或品牌..." class="search-input" />
					<button v-if="globalSearchKeyword" @click="clearGlobalSearch" class="clear-search">✕</button>
				</view>
				<button class="add-btn" @click="openAddProductModal">
					<text>+</text> 添加商品
				</button>
			</view>
		</view>

		<!-- ==================== 内容区域 ==================== -->
		<view class="main-content" v-if="selectedProduct">
			<!-- 左侧区域：商品选择器 -->
			<view class="left-panel">
				<!-- 商品切换卡片（分页版） -->
				<view class="product-selector">
					<view class="selector-header">
						<text class="selector-title">📋 商品列表</text>
						<text class="product-count">{{ filteredProducts.length }}个商品</text>
					</view>

					<!-- 商品列表内置搜索框 -->
					<view class="product-search">
						<input type="text" v-model="productListSearch" placeholder="在列表中搜索商品..."
							class="product-search-input" @input="resetPagination" />
					</view>

					<!-- 分页商品列表 -->
					<scroll-view scroll-y class="product-tabs-container">
						<view v-for="product in paginatedProducts" :key="product.id"
							:class="['product-tab', { active: selectedProduct && selectedProduct.id === product.id }]"
							@click="selectProduct(product)">
							<view class="tab-info">
								<view class="tab-name-wrapper">
									<text class="tab-name">{{ product.name }}</text>
									<text class="tab-stars" v-if="product.rating">
										{{ '⭐'.repeat(product.rating) }}
									</text>
								</view>
								<view class="tab-meta">
									<text v-if="product.brand" class="tab-brand">品牌：{{ product.brand }}</text>
									<text v-if="product.specification"
										class="tab-brand">规格：{{ product.specification }}</text>
									<text v-if="product.origin" class="tab-brand">产地：{{ product.origin }}</text>
									<text v-if="product.remarks" class="tab-brand">备注：{{ product.remarks }}</text>
								</view>
							</view>
							<view class="tab-actions">
								<text @click.stop="openEditProductModal(product)" class="tab-edit">✏️</text>
								<text @click.stop="deleteProduct(product.id)" class="tab-delete">🗑️</text>
							</view>
						</view>
						<view v-if="paginatedProducts.length === 0" class="no-product-tab">
							暂无匹配的商品
						</view>
					</scroll-view>

					<!-- 分页组件 -->
					<view class="pagination-wrapper" v-if="totalPages > 1 || searchedProducts.length > pageSize">
						<view class="pagination-info">
							共 {{ searchedProducts.length }} 条，第 {{ currentPage }}/{{ totalPages }} 页
						</view>
						<view class="pagination-controls">
							<button class="page-btn" :disabled="currentPage === 1" @click="currentPage--">«</button>
							<button v-for="page in displayPages" :key="page"
								:class="['page-btn', { active: currentPage === page }]" @click="goToPage(page)">
								{{ page }}
							</button>
							<button class="page-btn" :disabled="currentPage === totalPages"
								@click="currentPage++">»</button>
							<picker mode="selector" :range="pageSizeOptions" :value="pageSizeIndex"
								@change="onPageSizeChange">
								<button class="page-size-select">{{ pageSize }}条/页</button>
							</picker>
						</view>
					</view>
				</view>
			</view>

			<!-- 右侧区域：历史价格列表（可按日期搜索） -->
			<view class="right-panel">
				<view class="history-card">
					<view class="card-header">
						<text class="card-title">📜 历史价格记录</text>
						<button class="add-price-btn" @click="openAddPriceModal">+ 添加价格</button>
					</view>

					<!-- 日期搜索栏 -->
					<view class="date-search-bar">
						<view class="date-range">
							<view class="date-input-wrapper">
								<text class="date-label">从</text>
								<picker mode="date" :value="dateFrom" @change="onDateFromChange">
									<view class="date-input">{{ dateFrom || '请选择' }}</view>
								</picker>
								<text class="date-label">至</text>
								<picker mode="date" :value="dateTo" @change="onDateToChange">
									<view class="date-input">{{ dateTo || '请选择' }}</view>
								</picker>
							</view>
							
							<picker mode="selector" :range="sortTypeOptions" :value="sortTypeIndex"
								@change="onSortTypeChange">
								<view class="sort-select">{{ sortTypeLabel }}</view>
							</picker>
							<button v-if="dateFrom || dateTo" @click="clearDateFilter"
								class="clear-date-btn">清除筛选</button>
						</view>
					</view>

					<!-- 价格列表 -->
					<scroll-view scroll-y class="price-list-container">
						<view v-if="filteredPrices.length > 0" class="price-table">
							<view class="price-table-header">
								<view class="col-date">日期</view>
								<view class="col-price">价格 (¥)</view>
								<view class="col-change">涨跌幅</view>
								<view class="col-action">操作</view>
							</view>
							<view class="price-table-body">
								<view v-for="(item, idx) in filteredPrices" :key="item.id || idx"
									class="price-table-row">
									<view class="col-date">
										<text class="date-badge">{{ formatDate(item.date) }}</text>
									</view>
									<view class="col-price">
										<text class="price-value">¥{{ item.value.toFixed(2) }}</text>
									</view>
									<view class="col-change">
										<text :class="['change-badge', getChangeClass(item, idx)]">
											{{ getChangeText(item, idx) }}
										</text>
									</view>
									<view class="col-action">
										<text @click="deletePriceItem(item)" class="delete-price-btn">✕</text>
									</view>
								</view>
							</view>
						</view>
						<view v-else class="empty-price-list">
							<text>📭</text>
							<text v-if="dateFrom || dateTo">没有找到该时间范围内的价格记录</text>
							<text v-else>暂无历史价格，点击“添加价格”开始记录</text>
						</view>
					</scroll-view>

					<!-- 价格统计摘要 -->
					<view class="price-summary"
						v-if="selectedProduct && selectedProduct.prices && selectedProduct.prices.length > 0">
						<view class="summary-item">
							<text class="summary-label">最低价</text>
							<text class="summary-value min">¥{{ minPrice.toFixed(2) }}</text>
						</view>
						<view class="summary-item">
							<text class="summary-label">最高价</text>
							<text class="summary-value max">¥{{ maxPrice.toFixed(2) }}</text>
						</view>
						<view class="summary-item">
							<text class="summary-label">平均价</text>
							<text class="summary-value avg">¥{{ avgPrice.toFixed(2) }}</text>
						</view>
						<view class="summary-item">
							<text class="summary-label">最新价</text>
							<text class="summary-value latest">¥{{ latestPrice.toFixed(2) }}</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 未选择商品时的空状态 -->
		<view class="empty-state-large" v-else>
			<view class="empty-icon">🛒</view>
			<text class="empty-title">暂无商品</text>
			<text class="empty-desc">点击上方“添加商品”按钮开始记录商品价格</text>
			<button class="btn-primary" @click="openAddProductModal">+ 添加商品</button>
		</view>

		<!-- ==================== 弹窗 ==================== -->
		<!-- 添加/编辑商品弹窗 -->
		<view v-if="showProductModal" class="modal-mask" @click.self="closeProductModal">
			<view class="modal-container">
				<view class="modal-header">
					<text class="modal-title">{{ productModalTitle }}</text>
					<text class="modal-close" @click="closeProductModal">✕</text>
				</view>
				<scroll-view scroll-y class="modal-body">
					<view class="form-group">
						<text class="form-label">商品名称 <text class="required">*</text></text>
						<input type="text" v-model="editingProduct.name" placeholder="例：iPhone 15 Pro"
							class="form-input" />
					</view>
					<view class="form-row">
						<view class="form-group">
							<text class="form-label">品牌</text>
							<input type="text" v-model="editingProduct.brand" placeholder="例：Apple"
								class="form-input" />
						</view>
						<view class="form-group">
							<text class="form-label">产地</text>
							<input type="text" v-model="editingProduct.origin" placeholder="例：中国" class="form-input" />
						</view>
					</view>
					<view class="form-row">
						<view class="form-group">
							<text class="form-label">星级评分</text>
							<view class="rating-select">
								<text v-for="n in 5" :key="n" class="star" @click="editingProduct.rating = n">
									{{ n <= (editingProduct.rating || 0) ? '⭐' : '☆' }}
								</text>
								<text v-if="editingProduct.rating" class="clear-rating"
									@click="editingProduct.rating = null">清除</text>
							</view>
						</view>
						<view class="form-group">
							<text class="form-label">规格</text>
							<input type="text" v-model="editingProduct.specification" placeholder="例：台"
								class="form-input" />
						</view>
					</view>
					<view class="form-group">
						<text class="form-label">备注</text>
						<input type="text" v-model="editingProduct.remarks" placeholder="其他信息" class="form-input" />
					</view>
				</scroll-view>
				<view class="modal-footer">
					<button class="btn-cancel" @click="closeProductModal">取消</button>
					<button class="btn-save" @click="saveProduct">保存</button>
				</view>
			</view>
		</view>

		<!-- 添加价格弹窗 -->
		<view v-if="showPriceModal" class="modal-mask" @click.self="closePriceModal">
			<view class="modal-container modal-small">
				<view class="modal-header">
					<text class="modal-title">添加价格记录</text>
					<text class="modal-close" @click="closePriceModal">✕</text>
				</view>
				<view class="modal-body">
					<view class="price-product-info" v-if="selectedProduct">
						<text class="product-badge">{{ selectedProduct.name }}</text>
					</view>
					<view class="form-group">
						<text class="form-label">日期 <text class="required">*</text></text>
						<picker mode="date" :value="newPrice.date" :end="todayDate" @change="onNewPriceDateChange">
							<view class="form-input date-placeholder">{{ newPrice.date || '请选择日期' }}</view>
						</picker>
					</view>
					<view class="form-group">
						<text class="form-label">价格 (¥) <text class="required">*</text></text>
						<input type="digit" v-model="newPrice.value" placeholder="0.00" class="form-input" />
					</view>
				</view>
				<view class="modal-footer">
					<button class="btn-cancel" @click="closePriceModal">取消</button>
					<button class="btn-save" @click="savePrice">添加</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		priceTrackerApi
	} from "@/api/index.js";

	export default {
		name: 'PriceTracker',
		data() {
			return {
				sortType: 'date_desc',
				sortTypeOptions: ['按日期（最新优先）', '按日期（最早优先）', '按价格（从低到高）', '按价格（从高到低）'],
				sortTypeMap: {
					'按日期（最新优先）': 'date_desc',
					'按日期（最早优先）': 'date_asc',
					'按价格（从低到高）': 'price_asc',
					'按价格（从高到低）': 'price_desc'
				},
				pageSizeOptions: ['5条/页', '10条/页', '20条/页', '50条/页'],
				pageSizeValues: [5, 10, 20, 50],
				products: [],
				globalSearchKeyword: '',
				productListSearch: '',
				selectedProduct: null,
				dateFrom: '',
				dateTo: '',
				showProductModal: false,
				showPriceModal: false,
				isEditMode: false,
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
					isDeleted: 0
				},
				newPrice: {
					date: '',
					value: null
				}
			};
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
			filteredProducts() {
				if (!this.globalSearchKeyword.trim()) return this.products;
				const kw = this.globalSearchKeyword.trim().toLowerCase();
				return this.products.filter(p =>
					p.name.toLowerCase().includes(kw) ||
					(p.brand && p.brand.toLowerCase().includes(kw))
				);
			},
			searchedProducts() {
				if (!this.productListSearch.trim()) return this.filteredProducts;
				const kw = this.productListSearch.trim().toLowerCase();
				return this.filteredProducts.filter(p =>
					p.name.toLowerCase().includes(kw) ||
					(p.brand && p.brand.toLowerCase().includes(kw))
				);
			},
			paginatedProducts() {
				const start = (this.currentPage - 1) * this.pageSize;
				const end = start + this.pageSize;
				return this.searchedProducts.slice(start, end);
			},
			totalPages() {
				return Math.ceil(this.searchedProducts.length / this.pageSize) || 1;
			},
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
					default:
						prices.sort((a, b) => new Date(b.date) - new Date(a.date));
				}

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
				const today = new Date();
				return `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;
			},
			productModalTitle() {
				return this.isEditMode ? '编辑商品信息' : '添加新商品';
			},
			sortTypeIndex() {
				return this.sortTypeOptions.findIndex(opt => this.sortTypeMap[opt] === this.sortType);
			},
			sortTypeLabel() {
				return this.sortTypeOptions[this.sortTypeIndex] || '按日期（最新优先）';
			},
			pageSizeIndex() {
				return this.pageSizeValues.indexOf(this.pageSize);
			}
		},
		watch: {
			searchedProducts: {
				handler(newList) {
					this.currentPage = 1;
					if (newList.length > 0) {
						if (!this.selectedProduct || !newList.find(p => p.id === this.selectedProduct.id)) {
							this.selectProduct(newList[0]);
						}
					} else {
						this.selectedProduct = null;
					}
				},
				immediate: true
			}
		},
		mounted() {
			this.loadData();
		},
		methods: {
			async loadData() {
				try {
					const res = await priceTrackerApi.getProduct();
					if (res.data) {
						this.products = res.data;
					}
					if (this.products.length > 0 && !this.selectedProduct) {
						await this.selectProduct(this.products[0]);
					}
				} catch (err) {
					console.error("加载商品失败:", err);
					uni.showToast({
						title: "加载商品失败",
						icon: "none"
					});
				}
			},
			clearGlobalSearch() {
				this.globalSearchKeyword = '';
				if (this.products.length > 0 && !this.selectedProduct) {
					this.selectProduct(this.products[0]);
				}
			},
			resetPagination() {
				this.currentPage = 1;
			},
			goToPage(page) {
				this.currentPage = page;
			},
			onPageSizeChange(e) {
				const index = e.detail.value;
				this.pageSize = this.pageSizeValues[index];
				this.resetPagination();
			},
			onSortTypeChange(e) {
				const index = e.detail.value;
				this.sortType = this.sortTypeMap[this.sortTypeOptions[index]];
			},
			onDateFromChange(e) {
				this.dateFrom = e.detail.value;
			},
			onDateToChange(e) {
				this.dateTo = e.detail.value;
			},
			onNewPriceDateChange(e) {
				this.newPrice.date = e.detail.value;
			},
			async selectProduct(product) {
				if (this.selectedProduct && this.selectedProduct.id === product.id) return;

				this.selectedProduct = null;

				try {
					const res = await priceTrackerApi.getRecordsByProductId(product.id);
					const prices = [];
					if (res.data) {
						res.data.forEach(element => {
							prices.push({
								id: element.id,
								date: element.recordDate,
								value: element.price
							});
						});
					}

					const productWithPrices = {
						...product,
						prices: prices
					};
					this.selectedProduct = productWithPrices;

					const index = this.products.findIndex(p => p.id === product.id);
					if (index !== -1) {
						this.products[index] = productWithPrices;
					}
				} catch (err) {
					console.error("获取价格记录失败:", err);
					this.selectedProduct = {
						...product,
						prices: []
					};
				}

				this.dateFrom = '';
				this.dateTo = '';
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
			deletePriceItem(priceItem) {
				uni.showModal({
					title: '提示',
					content: `确定删除 ${this.formatDate(priceItem.date)} 的价格记录 ¥${priceItem.value} 吗？`,
					success: async (res) => {
						if (res.confirm) {
							try {
								const result = await priceTrackerApi.delPriceRecord(priceItem.id);
								if (result.status === 200) {
									const indexInOriginal = this.selectedProduct.prices.findIndex(p => p.id ===
										priceItem.id);
									if (indexInOriginal !== -1) {
										this.selectedProduct.prices.splice(indexInOriginal, 1);
										const productIndex = this.products.findIndex(p => p.id === this
											.selectedProduct.id);
										if (productIndex !== -1) {
											this.products[productIndex].prices = [...this.selectedProduct
												.prices
											];
										}
										uni.showToast({
											title: '删除成功',
											icon: 'success'
										});
									}
								} else {
									uni.showToast({
										title: result.data.msg || '删除失败',
										icon: 'none'
									});
								}
							} catch (err) {
								console.error("删除失败:", err);
								uni.showToast({
									title: '删除失败',
									icon: 'none'
								});
							}
						}
					}
				});
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
					isDeleted: 0
				};
				this.showProductModal = true;
			},
			openEditProductModal(product) {
				this.isEditMode = true;
				this.editingProduct = JSON.parse(JSON.stringify(product));
				this.showProductModal = true;
			},
			async saveProduct() {
				if (!this.editingProduct.name.trim()) {
					uni.showToast({
						title: '请填写商品名称',
						icon: 'none'
					});
					return;
				}
				try {
					let res;
					if (this.isEditMode) {
						res = await priceTrackerApi.editProduct(this.editingProduct);
						console.log("编辑商品结果:", res);
						if (res.status === 200) {
							const index = this.products.findIndex(p => p.id === this.editingProduct.id);
							if (index !== -1) {
								this.products[index] = {
									...this.editingProduct,
									prices: this.products[index]?.prices || []
								};
								if (this.selectedProduct?.id === this.editingProduct.id) {
									this.selectedProduct = this.products[index];
								}
							}
							uni.showToast({
								title: '保存成功',
								icon: 'success'
							});
						} else {
							uni.showToast({
								title: res.data.msg || '保存失败',
								icon: 'none'
							});
						}
					} else {
						res = await priceTrackerApi.addProduct(this.editingProduct);
						if (res.status === 200) {
							await this.loadData();
							uni.showToast({
								title: '添加成功',
								icon: 'success'
							});
						} else {
							uni.showToast({
								title: res.msg || '添加失败',
								icon: 'none'
							});
						}
					}
					this.closeProductModal();
				} catch (err) {
					console.error("保存失败:", err);
					uni.showToast({
						title: '保存失败',
						icon: 'none'
					});
				}
			},
			deleteProduct(id) {
				uni.showModal({
					title: '提示',
					content: '确定删除该商品及其所有价格记录吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								const result = await priceTrackerApi.delProduct(id);
								if (result.status === 200) {
									this.products = this.products.filter(p => p.id !== id);
									if (this.selectedProduct?.id === id) {
										this.selectedProduct = this.products[0] || null;
									}
									uni.showToast({
										title: '删除成功',
										icon: 'success'
									});
								} else {
									uni.showToast({
										title: result.data.msg || '删除失败',
										icon: 'none'
									});
								}
							} catch (err) {
								console.error("删除失败:", err);
								uni.showToast({
									title: '删除失败',
									icon: 'none'
								});
							}
						}
					}
				});
			},
			openAddPriceModal() {
				if (!this.selectedProduct) return;
				this.newPrice = {
					date: this.todayDate,
					value: null
				};
				this.showPriceModal = true;
			},
			async savePrice() {
				if (!this.newPrice.date) {
					uni.showToast({
						title: '请选择日期',
						icon: 'none'
					});
					return;
				}
				if (!this.newPrice.value || this.newPrice.value <= 0) {
					uni.showToast({
						title: '请输入正确的价格',
						icon: 'none'
					});
					return;
				}
				if (!this.selectedProduct.prices) this.selectedProduct.prices = [];

				const exists = this.selectedProduct.prices.some(p => p.date === this.newPrice.date);
				if (exists) {
					uni.showToast({
						title: '该日期已存在价格记录',
						icon: 'none'
					});
					return;
				}

				const priceData = {
					productId: this.selectedProduct.id,
					price: parseFloat(this.newPrice.value),
					recordDate: this.newPrice.date
				};

				try {
					const res = await priceTrackerApi.addPriceRecord(priceData);
					if (res.status === 200) {
						const newRecord = {
							id: res.data?.id || Date.now(),
							date: this.newPrice.date,
							value: parseFloat(this.newPrice.value)
						};
						this.selectedProduct.prices.push(newRecord);
						this.selectedProduct.prices.sort((a, b) => new Date(a.date) - new Date(b.date));

						const productIndex = this.products.findIndex(p => p.id === this.selectedProduct.id);
						if (productIndex !== -1) {
							this.products[productIndex].prices = [...this.selectedProduct.prices];
						}

						uni.showToast({
							title: '价格添加成功',
							icon: 'success'
						});
						this.closePriceModal();
					} else {
						uni.showToast({
							title: res.data.msg || '添加失败',
							icon: 'none'
						});
					}
				} catch (err) {
					console.error("添加价格失败:", err);
					uni.showToast({
						title: '添加失败',
						icon: 'none'
					});
				}
			},
			closeProductModal() {
				this.showProductModal = false;
			},
			closePriceModal() {
				this.showPriceModal = false;
			}
		}
	};
</script>

<style scoped>
	/* 全局样式 */
	.app-container {
		min-height: 98vh;
		background: #f0f2f5;
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
	}

	/* 顶部状态栏 */
	.top-bar {
		background: #ffffff;
		padding: 24rpx 32rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		flex-wrap: wrap;
		gap: 20rpx;
		box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
		border-bottom: 1rpx solid #e9ecef;
		position: sticky;
		top: 0;
		z-index: 100;
	}

	.stats-group {
		display: flex;
		gap: 48rpx;
	}

	.stat-item {
		display: flex;
		align-items: center;
		gap: 16rpx;
	}

	.stat-icon {
		font-size: 48rpx;
	}

	.stat-info {
		display: flex;
		flex-direction: column;
	}

	.stat-value {
		font-size: 40rpx;
		font-weight: 700;
		color: #1e293b;
		line-height: 1.2;
	}

	.stat-label {
		font-size: 22rpx;
		color: #64748b;
	}

	.action-group {
		display: flex;
		gap: 24rpx;
		align-items: center;
	}

	.search-box {
		position: relative;
		display: flex;
		align-items: center;
	}

	.search-icon {
		position: absolute;
		left: 24rpx;
		font-size: 28rpx;
		opacity: 0.6;
	}

	.search-input {
		padding: 16rpx 32rpx 16rpx 64rpx;
		border: 1rpx solid #e2e8f0;
		border-radius: 60rpx;
		width: 400rpx;
		font-size: 26rpx;
		background: #f8fafc;
	}

	.search-input:focus {
		border-color: #2c7da0;
		background: #ffffff;
	}

	.clear-search {
		position: absolute;
		right: 20rpx;
		background: none;
		border: none;
		font-size: 28rpx;
		color: #94a3b8;
	}

	.add-btn {
		background: #2c7da0;
		color: #ffffff;
		border: none;
		padding: 0 10rpx 0 10rpx;
		border-radius: 60rpx;
		font-weight: 500;
		display: flex;
		align-items: center;
		gap: 12rpx;
		font-size: 28rpx;
	}

	/* 主内容区域 - 两列布局 */
	.main-content {
		max-width: 1600rpx;
		margin: 0 auto;
		padding: 24rpx;
		display: flex;
		gap: 24rpx;
		min-height: calc(100vh - 120rpx);
	}

	.left-panel {
		flex: 1.2;
		display: flex;
		flex-direction: column;
		gap: 24rpx;
	}

	.right-panel {
		flex: 1;
	}

	/* 商品选择器 */
	.product-selector {
		background: #ffffff;
		border-radius: 32rpx;
		overflow: hidden;
		box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
		display: flex;
		flex-direction: column;
	}

	.selector-header {
		padding: 24rpx 32rpx;
		background: #fafbfc;
		border-bottom: 1rpx solid #eef2f6;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.selector-title {
		font-size: 28rpx;
		font-weight: 600;
		color: #1e293b;
	}

	.product-count {
		font-size: 22rpx;
		color: #64748b;
	}

	.product-search {
		padding: 16rpx 24rpx;
		border-bottom: 1rpx solid #eef2f6;
	}

	.product-search-input {
		width: 95%;
		padding: 16rpx 24rpx;
		border: 1rpx solid #e2e8f0;
		border-radius: 48rpx;
		font-size: 24rpx;
		background: #f8fafc;
	}

	.product-tabs-container {
		max-height: 500rpx;
		overflow-y: auto;
	}

	.product-tab {
		padding: 20rpx 24rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		border-bottom: 1rpx solid #f1f5f9;
	}

	.product-tab.active {
		background: #eef2ff;
		border-left: 6rpx solid #2c7da0;
	}

	.tab-info {
		flex: 1;
	}

	.tab-name-wrapper {
		display: flex;
		align-items: center;
		gap: 12rpx;
		margin-bottom: 8rpx;
		flex-wrap: wrap;
	}

	.tab-name {
		font-weight: 600;
		color: #1e293b;
		font-size: 26rpx;
	}

	.tab-stars {
		font-size: 22rpx;
		color: #f5a623;
	}

	.tab-meta {
		display: flex;
		gap: 16rpx;
		font-size: 22rpx;
		color: #64748b;
		flex-wrap: wrap;
	}

	.tab-brand {
		background: #f1f5f9;
		padding: 4rpx 12rpx;
		border-radius: 20rpx;
	}

	.tab-actions {
		display: flex;
		gap: 16rpx;
	}

	.tab-edit,
	.tab-delete {
		font-size: 32rpx;
		padding: 8rpx;
	}

	.tab-delete {
		color: #ef4444;
	}

	.no-product-tab {
		padding: 60rpx;
		text-align: center;
		color: #94a3b8;
	}

	/* 分页组件 */
	.pagination-wrapper {
		padding: 16rpx 24rpx;
		border-top: 1rpx solid #eef2f6;
		display: flex;
		justify-content: space-between;
		align-items: center;
		flex-wrap: wrap;
		gap: 16rpx;
		background: #fafbfc;
	}

	.pagination-info {
		font-size: 22rpx;
		color: #64748b;
	}

	.pagination-controls {
		display: flex;
		gap: 12rpx;
		align-items: center;
		flex-wrap: wrap;
	}

	.page-btn {
		min-width: 56rpx;
		height: 56rpx;
		padding: 0 16rpx;
		border: 1rpx solid #e2e8f0;
		background: #ffffff;
		border-radius: 16rpx;
		font-size: 24rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.page-btn.active {
		background: #2c7da0;
		border-color: #2c7da0;
		color: #ffffff;
	}

	.page-btn[disabled] {
		opacity: 0.5;
	}

	.page-size-select {
		padding: 12rpx 20rpx;
		border: 1rpx solid #e2e8f0;
		border-radius: 16rpx;
		font-size: 22rpx;
		background: #ffffff;
	}

	/* 历史价格卡片 */
	.history-card {
		background: #ffffff;
		border-radius: 32rpx;
		padding: 24rpx;
		box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
		display: flex;
		flex-direction: column;
		gap: 24rpx;
	}

	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.card-title {
		font-size: 32rpx;
		font-weight: 600;
		color: #1e293b;
	}

	.add-price-btn {
		background: #2c7da0;
		color: #ffffff;
		border: none;
		padding: 12rpx 28rpx;
		border-radius: 60rpx;
		font-size: 24rpx;
	}

	/* 日期搜索栏 */
	.date-search-bar {
		background: #f8fafc;
		padding: 20rpx 24rpx;
		border-radius: 32rpx;
	}

	.date-range {
		display: flex;
		gap: 20rpx;
		flex-wrap: wrap;
		align-items: center;
	}

	.date-input-wrapper {
		display: flex;
		align-items: center;
		gap: 24rpx;
	}

	.date-label {
		font-size: 24rpx;
		color: #475569;
	}

	.date-input {
		padding: 12rpx 20rpx;
		border: 1rpx solid #e2e8f0;
		border-radius: 32rpx;
		font-size: 24rpx;
		background: #ffffff;
		min-width: 200rpx;
	}

	.sort-select {
		padding: 12rpx 24rpx;
		border: 1rpx solid #e2e8f0;
		border-radius: 32rpx;
		font-size: 24rpx;
		background: #ffffff;
	}

	.clear-date-btn {
		background: #e2e8f0;
		border: none;
		padding: 10rpx 24rpx;
		border-radius: 48rpx;
		font-size: 22rpx;
	}

	/* 价格表格 */
	.price-list-container {
		max-height: 600rpx;
		overflow-y: auto;
	}

	.price-table {
		width: 100%;
	}

	.price-table-header {
		display: grid;
		grid-template-columns: 1.2fr 1fr 1fr 0.6fr;
		padding: 16rpx 20rpx;
		background: #f8fafc;
		border-radius: 24rpx;
		font-size: 22rpx;
		font-weight: 600;
		color: #475569;
		margin-bottom: 12rpx;
	}

	.price-table-row {
		display: grid;
		grid-template-columns: 1.2fr 1fr 1fr 0.6fr;
		padding: 20rpx 20rpx;
		border-bottom: 1rpx solid #f1f5f9;
		align-items: center;
	}

	.date-badge {
		font-size: 24rpx;
		font-weight: 500;
	}

	.price-value {
		font-weight: 700;
		color: #1e293b;
		font-size: 28rpx;
	}

	.change-badge {
		font-size: 22rpx;
		padding: 4rpx 12rpx;
		border-radius: 32rpx;
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
		font-size: 32rpx;
		color: #cbd5e1;
		text-align: center;
	}

	.empty-price-list {
		text-align: center;
		padding: 80rpx 32rpx;
		color: #94a3b8;
		display: flex;
		flex-direction: column;
		gap: 16rpx;
	}

	/* 价格摘要 */
	.price-summary {
		display: flex;
		justify-content: space-between;
		background: #f8fafc;
		padding: 24rpx 32rpx;
		border-radius: 32rpx;
		margin-top: 12rpx;
	}

	.summary-item {
		text-align: center;
	}

	.summary-label {
		display: block;
		font-size: 20rpx;
		color: #64748b;
		margin-bottom: 6rpx;
	}

	.summary-value {
		font-size: 32rpx;
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
		padding: 120rpx 40rpx;
		background: #ffffff;
		margin: 80rpx auto;
		max-width: 600rpx;
		border-radius: 64rpx;
	}

	.empty-icon {
		font-size: 96rpx;
		margin-bottom: 32rpx;
		opacity: 0.6;
	}

	.empty-title {
		font-size: 36rpx;
		font-weight: 600;
		color: #1e293b;
		display: block;
		margin-bottom: 16rpx;
	}

	.empty-desc {
		font-size: 26rpx;
		color: #64748b;
		display: block;
		margin-bottom: 32rpx;
	}

	.btn-primary {
		background: #2c7da0;
		color: #ffffff;
		border: none;
		padding: 20rpx 48rpx;
		border-radius: 60rpx;
		font-size: 28rpx;
	}

	/* 弹窗样式 */
	.modal-mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		justify-content: center;
		align-items: center;
		z-index: 1000;
	}

	.modal-container {
		background: #ffffff;
		border-radius: 48rpx;
		width: 90%;
		max-width: 600rpx;
		max-height: 85vh;
		display: flex;
		flex-direction: column;
	}

	.modal-small {
		max-width: 500rpx;
	}

	.modal-header {
		padding: 28rpx 32rpx;
		border-bottom: 1rpx solid #f0f0f0;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.modal-title {
		font-size: 32rpx;
		font-weight: 600;
		color: #1e293b;
	}

	.modal-close {
		font-size: 40rpx;
		color: #94a3b8;
	}

	.modal-body {
		padding: 32rpx;
		flex: 1;
		overflow-y: auto;
	}

	.modal-footer {
		padding: 24rpx 32rpx;
		border-top: 1rpx solid #f0f0f0;
		display: flex;
		justify-content: flex-end;
		gap: 20rpx;
	}

	.form-group {
		margin-bottom: 28rpx;
	}

	.form-label {
		font-size: 26rpx;
		font-weight: 500;
		color: #475569;
		margin-bottom: 12rpx;
		display: block;
	}

	.required {
		color: #ef4444;
	}

	.form-input {
		width: 60%;
		padding: 20rpx 24rpx;
		border: 1rpx solid #e2e8f0;
		border-radius: 32rpx;
		font-size: 26rpx;
		background: #f8fafc;
	}

	.form-row {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 24rpx;
	}

	.rating-select {
		display: flex;
		gap: 12rpx;
		align-items: center;
	}

	.star {
		font-size: 40rpx;
	}

	.clear-rating {
		font-size: 22rpx;
		color: #94a3b8;
		margin-left: 16rpx;
	}

	.price-product-info {
		text-align: center;
		margin-bottom: 28rpx;
	}

	.product-badge {
		background: #eef2ff;
		padding: 12rpx 28rpx;
		border-radius: 60rpx;
		font-size: 26rpx;
	}

	.date-placeholder {
		color: #64748b;
	}

	.btn-cancel {
		background: #f1f5f9;
		border: none;
		padding: 16rpx 40rpx;
		border-radius: 60rpx;
		font-size: 26rpx;
	}

	.btn-save {
		background: #2c7da0;
		color: #ffffff;
		border: none;
		padding: 16rpx 48rpx;
		border-radius: 60rpx;
		font-size: 26rpx;
	}

	/* 响应式 */
	@media (max-width: 1000rpx) {
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

		.date-range {
			flex-direction: column;
			align-items: stretch;
		}

		.date-input-wrapper {
			justify-content: space-between;
		}
	}
</style>