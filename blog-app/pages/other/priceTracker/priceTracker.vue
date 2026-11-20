<template>
	<view class="app-container">
		<!-- ===== 顶部导航 ===== -->
		<view class="page-header">
			<view class="header-right">
				<view class="search-wrapper">
					<uni-icons type="search" size="20" color="#9a9693"></uni-icons>
					<input type="text" v-model="globalSearchKeyword" placeholder="搜索商品..." class="search-input" />
					<text v-if="globalSearchKeyword" class="clear-search" @click="globalSearchKeyword = ''">✕</text>
				</view>
				<view class="header-actions">
					<button class="btn-add" @click="openAddProductModal">新增</button>
					<button class="btn-sort" :class="{ active: sortField === 'name' }" @click="sortBy('name')">
						名称 {{ sortField === 'name' ? (sortOrder === 'asc' ? '↑' : '↓') : '' }}
					</button>
					<button class="btn-sort" :class="{ active: sortField === 'latest' }" @click="sortBy('latest')">
						最新 
					</button>
				</view>
			</view>
		</view>

		<!-- ===== 商品列表 ===== -->
		<view class="list-card">
			<view class="list-header">
				<view class="list-header-left">
					<text class="list-title">📋 商品列表</text>
					<text class="list-badge">{{ filteredProducts.length }}</text>
				</view>
				<view class="list-header-right">
					<text class="list-sort-info">{{ sortLabel }}</text>
				</view>
			</view>
			<view class="list-search">
				<input type="text" v-model="productListSearch" placeholder="筛选商品..." class="list-search-input" />
				<text v-if="productListSearch" class="clear-search-input" @click="productListSearch = ''">✕</text>
			</view>
			<scroll-view scroll-y class="list-scroll">
				<!-- 列表视图 -->
				<view>
					<view v-for="product in paginatedProducts" :key="product.id" class="list-item">
						<view class="item-info" @click="goToDetail(product)">
							<view class="item-name-row">
								<text class="item-name">{{ product.name }}</text>
								<text v-if="product.rating" class="item-stars">{{ getStars(product.rating) }}</text>
								<text v-if="product.isNew" class="item-badge-new">NEW</text>
							</view>
							<view class="item-meta">
								<text v-if="product.remarks" class="item-tag">备注：{{ product.remarks }}</text>
							</view>
						</view>
						<view class="item-actions">
							<uni-icons type="compose" @click="openEditProductModal(product)" size="20"
								color="#a5a29e"></uni-icons>
							<uni-icons type="clear" @click="deleteProduct(product.id)" size="20"
								color="#ac0a0a"></uni-icons>
						</view>
					</view>
				</view>

				<view v-if="paginatedProducts.length === 0" class="list-empty">
					<text class="empty-icon">🔍</text>
					<text class="empty-text">没有匹配的商品</text>
					<text class="empty-hint">试试调整搜索条件或添加新商品</text>
				</view>
			</scroll-view>

			<!-- 分页 -->
			<view v-if="filteredProducts.length > pageSize" class="list-pagination">
				<text class="page-info">{{ currentPage }}/{{ totalPages }} 页 · {{ filteredProducts.length }} 项</text>
				<view class="page-controls">
					<button class="page-btn" :disabled="currentPage === 1"
						@click="currentPage = currentPage - 1">‹</button>
					<button v-for="p in displayPages" :key="p" :class="['page-btn', { active: currentPage === p }]"
						@click="goToPage(p)">
						{{ p }}
					</button>
					<button class="page-btn" :disabled="currentPage === totalPages"
						@click="currentPage = currentPage + 1">›</button>
					<picker mode="selector" :range="pageSizeOptions" :value="pageSizeIndex" @change="onPageSizeChange">
						<button class="page-size-btn">{{ pageSize }}条</button>
					</picker>
				</view>
			</view>
		</view>

		<!-- ===== 弹窗：添加/编辑商品 ===== -->
		<view v-if="showProductModal" class="modal-mask" @click.self="closeProductModal">
			<view class="modal-box">
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
						<!-- 保留其他字段扩展，暂时隐藏 -->
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

		<!-- ===== Toast 提示 ===== -->
		<view v-if="toastMessage" class="toast" :class="toastType">
			<text>{{ toastMessage }}</text>
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
				pageSizeOptions: ['5条', '10条', '20条', '50条'],
				pageSizeValues: [5, 10, 20, 50],
				currentPage: 1,
				pageSize: 10,
				products: [],
				globalSearchKeyword: '',
				productListSearch: '',
				sortField: 'latest',
				sortOrder: 'desc',
				showProductModal: false,
				isEditMode: false,
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
				toastMessage: '',
				toastType: 'toast-info',
				toastTimer: null
			};
		},
		computed: {
			sortLabel: function() {
				var labels = {
					name: '按名称',
					price: '按价格',
					rating: '按评分',
					latest: '按最新'
				};
				return labels[this.sortField] || '默认';
			},
			filteredProducts: function() {
				var list = this.products.filter(function(p) {
					return p.isDeleted !== 1;
				});

				var kw = this.globalSearchKeyword.trim().toLowerCase();
				if (kw) {
					list = list.filter(function(p) {
						return p.name.toLowerCase().includes(kw) ||
							(p.brand && p.brand.toLowerCase().includes(kw)) ||
							(p.remarks && p.remarks.toLowerCase().includes(kw));
					});
				}

				var kw2 = this.productListSearch.trim().toLowerCase();
				if (kw2) {
					list = list.filter(function(p) {
						return p.name.toLowerCase().includes(kw2) ||
							(p.brand && p.brand.toLowerCase().includes(kw2));
					});
				}

				// 保留原有过滤（虽然未使用，但保留兼容）
				if (this.filterRating) {
					list = list.filter(function(p) {
						return p.rating === this.filterRating;
					}.bind(this));
				}

				list.sort(function(a, b) {
					switch (this.sortField) {
						case 'name':
							return this.sortOrder === 'asc' ?
								a.name.localeCompare(b.name) : b.name.localeCompare(a.name);
						case 'rating':
							var aRating = a.rating || 0;
							var bRating = b.rating || 0;
							return this.sortOrder === 'asc' ? aRating - bRating : bRating - aRating;
						case 'latest':
						default:
							var aDate = a.prices && a.prices.length ? a.prices[a.prices.length - 1].date :
								'1970-01-01';
							var bDate = b.prices && b.prices.length ? b.prices[b.prices.length - 1].date :
								'1970-01-01';
							return this.sortOrder === 'asc' ?
								aDate.localeCompare(bDate) : bDate.localeCompare(aDate);
					}
				}.bind(this));

				return list;
			},
			paginatedProducts: function() {
				var start = (this.currentPage - 1) * this.pageSize;
				return this.filteredProducts.slice(start, start + this.pageSize);
			},
			totalPages: function() {
				return Math.ceil(this.filteredProducts.length / this.pageSize) || 1;
			},
			displayPages: function() {
				var total = this.totalPages;
				var current = this.currentPage;
				var maxVisible = 5;
				var start = Math.max(1, current - Math.floor(maxVisible / 2));
				var end = Math.min(total, start + maxVisible - 1);
				if (end - start + 1 < maxVisible) {
					start = Math.max(1, end - maxVisible + 1);
				}
				var pages = [];
				for (var i = start; i <= end; i++) pages.push(i);
				return pages;
			},
			pageSizeIndex: function() {
				return this.pageSizeValues.indexOf(this.pageSize);
			},
			productModalTitle: function() {
				return this.isEditMode ? '编辑商品' : '添加商品';
			}
		},
		mounted: function() {
			this.loadData();
		},
		methods: {
			getStars: function(rating) {
				if (!rating) return '';
				var stars = '';
				for (var i = 0; i < rating; i++) {
					stars += '⭐';
				}
				return stars;
			},
			getLatestDate: function(product) {
				if (!product || !product.prices || !product.prices.length) return '暂无';
				var sorted = [].concat(product.prices).sort(function(a, b) {
					return new Date(b.date) - new Date(a.date);
				});
				var date = new Date(sorted[0].date);
				return (date.getMonth() + 1) + '/' + date.getDate();
			},
			showToast: function(message, type) {
				type = type || 'toast-info';
				if (this.toastTimer) clearTimeout(this.toastTimer);
				this.toastMessage = message;
				this.toastType = type;
				this.toastTimer = setTimeout(function() {
					this.toastMessage = '';
				}.bind(this), 3000);
			},
			loadData: function() {
				var _this = this;
				priceTrackerApi.getProduct().then(function(res) {
					if (res.data) {
						_this.products = res.data.map(function(p) {
							return {
								...p,
								prices: p.prices || []
							};
						});
					}
				}).catch(function(err) {
					console.error("加载商品失败:", err);
					_this.showToast('加载商品失败', 'toast-error');
				});
			},
			sortBy: function(field) {
				if (this.sortField === field) {
					this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
				} else {
					this.sortField = field;
					this.sortOrder = field === 'name' ? 'asc' : 'desc';
				}
				this.currentPage = 1;
			},
			goToDetail: function(product) {
				uni.navigateTo({
					url: '/pages/other/priceTrackerInfo/priceTrackerInfo?id=' + product.id
				});
			},
			goToPage: function(page) {
				this.currentPage = page;
			},
			onPageSizeChange: function(e) {
				this.pageSize = this.pageSizeValues[e.detail.value];
				this.currentPage = 1;
			},
			openAddProductModal: function() {
				this.isEditMode = false;
				this.editingProduct = {
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
				};
				this.showProductModal = true;
			},
			openEditProductModal: function(product) {
				this.isEditMode = true;
				this.editingProduct = JSON.parse(JSON.stringify(product));
				this.showProductModal = true;
			},
			saveProduct: function() {
				var _this = this;
				if (!this.editingProduct.name.trim()) {
					this.showToast('请填写商品名称', 'toast-error');
					return;
				}
				if (this.isEditMode) {
					priceTrackerApi.editProduct(this.editingProduct).then(function(res) {
						if (res.status === 200) {
							var idx = -1;
							for (var i = 0; i < _this.products.length; i++) {
								if (_this.products[i].id === _this.editingProduct.id) {
									idx = i;
									break;
								}
							}
							if (idx !== -1) {
								const updated = {
									..._this.editingProduct,
									prices: _this.products[idx] ? _this.products[idx].prices || [] : []
								};
								_this.$set(_this.products, idx, updated);
							}
							_this.showToast('保存成功', 'toast-success');
							_this.closeProductModal();
						} else {
							_this.showToast(res.data ? res.data.msg || '保存失败' : '保存失败', 'toast-error');
						}
					}).catch(function(err) {
						console.error("保存失败:", err);
						_this.showToast('保存失败', 'toast-error');
					});
				} else {
					priceTrackerApi.addProduct(this.editingProduct).then(function(res) {
						if (res.status === 200) {
							_this.loadData();
							_this.showToast('添加成功', 'toast-success');
							_this.closeProductModal();
						} else {
							_this.showToast(res.msg || '添加失败', 'toast-error');
						}
					}).catch(function(err) {
						console.error("添加失败:", err);
						_this.showToast('添加失败', 'toast-error');
					});
				}
			},
			deleteProduct: function(id) {
				var _this = this;
				uni.showModal({
					title: '提示',
					content: '确定删除该商品及其所有价格记录吗？',
					success: function(res) {
						if (res.confirm) {
							priceTrackerApi.delProduct(id).then(function(result) {
								if (result.status === 200) {
									_this.products = _this.products.filter(function(p) {
										return p.id !== id;
									});
									_this.showToast('删除成功', 'toast-success');
								} else {
									_this.showToast(result.data ? result.data.msg || '删除失败' :
										'删除失败', 'toast-error');
								}
							}).catch(function(err) {
								console.error("删除失败:", err);
								_this.showToast('删除失败', 'toast-error');
							});
						}
					}
				});
			},
			closeProductModal: function() {
				this.showProductModal = false;
			}
		}
	};
</script>

<style scoped>
	.app-container {
		min-height: 100vh;
		background: #f0f4f8;
		padding: 12px 16px 20px;
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
		position: relative;
	}

	/* ===== Toast ===== */
	.toast {
		position: fixed;
		top: 16px;
		left: 50%;
		transform: translateX(-50%);
		padding: 8px 20px;
		border-radius: 12px;
		background: #1e293b;
		color: #ffffff;
		font-size: 13px;
		font-weight: 500;
		z-index: 9999;
		box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
		animation: slideDown 0.3s ease;
	}

	.toast-success {
		background: #10b981;
	}

	.toast-error {
		background: #ef4444;
	}

	.toast-info {
		background: #4f7df3;
	}

	@keyframes slideDown {
		from {
			opacity: 0;
			transform: translateX(-50%) translateY(-20px);
		}

		to {
			opacity: 1;
			transform: translateX(-50%) translateY(0);
		}
	}

	/* ===== 顶部导航 ===== */
	.page-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		flex-wrap: wrap;
		gap: 8px 12px;
		background: #ffffff;
		padding: 8px 14px;
		border-radius: 14px;
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
		margin-bottom: 12px;
	}

	.header-left {
		display: flex;
		align-items: center;
		gap: 6px;
	}

	.header-right {
		display: flex;
		align-items: center;
		gap: 8px;
		flex-wrap: wrap;
		flex: 1;
	}

	.search-wrapper {
		position: relative;
		display: flex;
		align-items: center;
		background: #f8fafc;
		border-radius: 30px;
		padding: 0 0px;
		border: 1px solid #e2e8f0;
		transition: border 0.2s, box-shadow 0.2s;
		flex: 1;
		min-width: 180px;
	}

	.search-wrapper:focus-within {
		border-color: #4f7df3;
		box-shadow: 0 0 0 3px rgba(79, 125, 243, 0.12);
		background: #ffffff;
	}

	.search-input {
		border: none;
		background: transparent;
		padding: 6px 2px;
		font-size: 13px;
		flex: 1;
		outline: none;
	}

	.clear-search {
		font-size: 14px;
		color: #94a3b8;
		padding: 0 10px;
		cursor: pointer;
	}

	.header-actions {
		display: flex;
		gap: 6px;
		align-items: center;
		flex-wrap: nowrap;
	}

	/* 新增按钮 - 突出显示 */
	.btn-add {
		background: #4f7df3;
		color: #ffffff;
		border: none;
		padding: 4px 14px;
		border-radius: 30px;
		font-size: 13px;
		font-weight: 600;
		cursor: pointer;
		transition: all 0.2s;
		white-space: nowrap;
		height: 32px;
		display: flex;
		align-items: center;
	}

	.btn-add:hover {
		background: #3b66d0;
	}

	.btn-add:active {
		transform: scale(0.94);
	}

	/* 排序按钮 - 轻量标签样式 */
	.btn-sort {
		background: #f1f5f9;
		color: #475569;
		border: 1px solid #e2e8f0;
		padding: 0 12px;
		border-radius: 30px;
		font-size: 13px;
		font-weight: 500;
		cursor: pointer;
		transition: all 0.2s;
		white-space: nowrap;
		height: 32px;
		display: flex;
		align-items: center;
		gap: 2px;
	}

	.btn-sort:hover {
		background: #e2e8f0;
	}

	.btn-sort.active {
		background: #eef2ff;
		border-color: #4f7df3;
		color: #4f7df3;
	}

	.btn-sort:active {
		transform: scale(0.94);
	}

	/* 移除旧的无用按钮样式 */
	.btn-filter {
		display: none;
	}

	/* ===== 商品列表 ===== */
	.list-card {
		background: #ffffff;
		border-radius: 14px;
		border: 1px solid #eef2f6;
		overflow: hidden;
		display: flex;
		flex-direction: column;
	}

	.list-header {
		padding: 10px 14px;
		background: #fafbfc;
		border-bottom: 1px solid #eef2f6;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.list-header-left {
		display: flex;
		align-items: center;
		gap: 8px;
	}

	.list-title {
		font-weight: 600;
		font-size: 15px;
		color: #0f172a;
	}

	.list-badge {
		font-size: 12px;
		color: #64748b;
		background: #eef2f6;
		padding: 0 10px;
		border-radius: 20px;
	}

	.list-sort-info {
		font-size: 11px;
		color: #94a3b8;
	}

	.list-search {
		padding: 6px 12px;
		border-bottom: 1px solid #f1f5f9;
		position: relative;
	}

	.list-search-input {
		width: 85%;
		padding: 6px 32px 6px 12px;
		border: 1px solid #e2e8f0;
		border-radius: 20px;
		font-size: 13px;
		background: #f8fafc;
		outline: none;
		transition: border 0.2s;
	}

	.list-search-input:focus {
		border-color: #4f7df3;
		background: #ffffff;
	}

	.clear-search-input {
		position: absolute;
		right: 22px;
		top: 50%;
		transform: translateY(-50%);
		padding: 0 10px;
		font-size: 14px;
		color: #94a3b8;
		cursor: pointer;
	}

	.list-scroll {
		flex: 1;
		max-height: 520px;
		overflow-y: auto;
	}

	/* 保留卡片视图样式（未使用，但保留） */
	.card-grid {
		display: grid;
		grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
		gap: 10px;
		padding: 10px 12px;
	}

	/* ... 其余卡片样式省略，保留原有 */

	/* ===== 列表视图 ===== */
	.list-item {
		padding: 10px 14px;
		display: flex;
		justify-content: space-between;
		align-items: center;
		border-bottom: 1px solid #f1f5f9;
		cursor: pointer;
		transition: background 0.15s;
	}

	.list-item:hover {
		background: #f8fafc;
	}

	.item-info {
		flex: 1;
		min-width: 0;
	}

	.item-name-row {
		display: flex;
		align-items: center;
		gap: 6px;
		flex-wrap: wrap;
		margin-bottom: 2px;
	}

	.item-name {
		font-weight: 600;
		font-size: 14px;
		color: #0f172a;
	}

	.item-stars {
		font-size: 12px;
		color: #f5a623;
	}

	.item-badge-new {
		font-size: 9px;
		background: #10b981;
		color: #ffffff;
		padding: 0 8px;
		border-radius: 16px;
		font-weight: 600;
	}

	.item-meta {
		display: flex;
		gap: 4px;
		flex-wrap: wrap;
	}

	.item-tag {
		font-size: 11px;
		color: #64748b;
		background: #f1f5f9;
		padding: 0 8px;
		border-radius: 16px;
	}

	.price-tag {
		background: #eef2ff;
		color: #4f7df3;
		font-weight: 600;
	}

	.target-hit {
		background: #d1fae5;
		color: #10b981;
		font-weight: 600;
	}

	.item-actions {
		display: flex;
		gap: 8px;
		align-items: center;
		font-size: 16px;
		flex-shrink: 0;
	}

	.item-edit {
		cursor: pointer;
		opacity: 0.6;
		transition: opacity 0.2s;
	}

	.item-edit:hover {
		opacity: 1;
	}

	.item-delete {
		cursor: pointer;
		opacity: 0.5;
		transition: opacity 0.2s;
	}

	.item-delete:hover {
		opacity: 1;
	}

	.item-arrow {
		color: #cbd5e1;
		font-size: 18px;
	}

	.list-empty {
		padding: 30px 16px;
		text-align: center;
		color: #94a3b8;
	}

	.empty-icon {
		font-size: 32px;
		display: block;
		margin-bottom: 6px;
	}

	.empty-text {
		font-size: 14px;
		display: block;
	}

	.empty-hint {
		font-size: 12px;
		color: #cbd5e1;
		display: block;
		margin-top: 2px;
	}

	/* ===== 分页 ===== */
	.list-pagination {
		padding: 8px 14px;
		border-top: 1px solid #eef2f6;
		display: flex;
		justify-content: space-between;
		align-items: center;
		flex-wrap: wrap;
		gap: 6px;
		background: #fafbfc;
	}

	.page-info {
		font-size: 12px;
		color: #64748b;
	}

	.page-controls {
		display: flex;
		gap: 4px;
		align-items: center;
		flex-wrap: wrap;
	}

	.page-btn {
		min-width: 28px;
		height: 28px;
		padding: 0 6px;
		border: 1px solid #e2e8f0;
		background: #ffffff;
		border-radius: 6px;
		font-size: 12px;
		display: flex;
		align-items: center;
		justify-content: center;
		cursor: pointer;
		transition: all 0.15s;
	}

	.page-btn.active {
		background: #4f7df3;
		border-color: #4f7df3;
		color: #ffffff;
	}

	.page-btn:disabled {
		opacity: 0.4;
		cursor: not-allowed;
	}

	.page-size-btn {
		padding: 2px 10px;
		border: 1px solid #e2e8f0;
		border-radius: 6px;
		font-size: 12px;
		background: #ffffff;
		cursor: pointer;
	}

	/* ===== 弹窗 ===== */
	.modal-mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(15, 23, 42, 0.5);
		backdrop-filter: blur(4px);
		display: flex;
		justify-content: center;
		align-items: center;
		z-index: 1000;
	}

	.modal-box {
		background: #ffffff;
		border-radius: 24px;
		width: 90%;
		max-width: 500px;
		max-height: 85vh;
		display: flex;
		flex-direction: column;
		box-shadow: 0 30px 80px rgba(0, 0, 0, 0.15);
		animation: scaleIn 0.3s ease;
	}

	@keyframes scaleIn {
		from {
			opacity: 0;
			transform: scale(0.95);
		}

		to {
			opacity: 1;
			transform: scale(1);
		}
	}

	.modal-header {
		padding: 14px 18px;
		border-bottom: 1px solid #f1f5f9;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.modal-title {
		font-size: 17px;
		font-weight: 700;
		color: #0f172a;
	}

	.modal-close {
		font-size: 24px;
		color: #94a3b8;
		cursor: pointer;
		line-height: 1;
	}

	.modal-body {
		padding: 16px 18px;
		overflow-y: auto;
		flex: 1;
	}

	.modal-footer {
		padding: 12px 18px;
		border-top: 1px solid #f1f5f9;
		display: flex;
		justify-content: flex-end;
		gap: 10px;
	}

	.form-group {
		margin-bottom: 12px;
	}

	.form-label {
		font-size: 13px;
		font-weight: 500;
		color: #475569;
		display: block;
		margin-bottom: 3px;
	}

	.required {
		color: #ef4444;
	}

	.form-input {
		width: 85%;
		padding: 8px 14px;
		border: 1px solid #e2e8f0;
		border-radius: 10px;
		font-size: 14px;
		background: #f8fafc;
		outline: none;
		transition: border 0.2s;
	}

	.form-input:focus {
		border-color: #4f7df3;
		background: #ffffff;
	}

	.form-row {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 10px;
	}

	.rating-group {
		display: flex;
		gap: 6px;
		align-items: center;
	}

	.star {
		font-size: 24px;
		cursor: pointer;
	}

	.clear-rating {
		font-size: 12px;
		color: #94a3b8;
		cursor: pointer;
		margin-left: 6px;
	}

	.btn-cancel {
		background: #f1f5f9;
		border: none;
		padding: 6px 18px;
		border-radius: 20px;
		font-size: 14px;
		font-weight: 500;
		cursor: pointer;
	}

	.btn-cancel:hover {
		background: #e2e8f0;
	}

	.btn-save {
		background: #4f7df3;
		color: #ffffff;
		border: none;
		padding: 6px 24px;
		border-radius: 20px;
		font-size: 14px;
		font-weight: 600;
		cursor: pointer;
	}

	.btn-save:hover {
		background: #3b66d0;
	}

	/* ===== 响应式 ===== */
	@media (max-width: 700px) {
		.app-container {
			padding: 8px 10px 16px;
		}

		.page-header {
			flex-direction: column;
			align-items: stretch;
		}

		.header-right {
			flex-direction: column;
			align-items: stretch;
			gap: 8px;
		}

		.search-wrapper {
			width: 100%;
		}

		.search-input {
			width: 100%;
		}

		.header-actions {
			justify-content: stretch;
			flex-wrap: wrap;
		}

		.header-actions .btn-add,
		.header-actions .btn-sort {
			flex: 1;
			justify-content: center;
			min-width: 60px;
		}

		.form-row {
			grid-template-columns: 1fr;
		}

		.list-pagination {
			flex-direction: column;
			align-items: stretch;
			gap: 4px;
		}

		.page-controls {
			justify-content: center;
		}

		.card-grid {
			grid-template-columns: 1fr;
		}
	}
</style>