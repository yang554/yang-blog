<template>
	<view class="nutrition-plan-manage">
		<!-- 顶部搜索栏 -->
		<view class="header-section">
			<!-- 头部标题和按钮 -->
			<view class="header">
				<view class="header-left">
					<text class="title">营养方案</text>
				</view>
				<view class="header-right">
					<button class="add-btn" @click="handleAddBtn">
						<uni-icons type="plus" size="20" color="#fff"></uni-icons>
						<text>添加方案</text>
					</button>
				</view>
			</view>

			<!-- 快速筛选 -->
			<view class="quick-filter">
				<scroll-view class="filter-scroll" scroll-x>
					<view class="filter-tags">
						<view class="filter-tag" :class="{ active: activeFilter === '全部方案' }"
							@click="setFilter('全部方案')">
							<text>全部方案</text>
						</view>
						<view v-for="category in planCategories" :key="category" class="filter-tag"
							:class="{ active: activeFilter === category }" @click="setFilter(category)">
							<text>{{ category }}</text>
						</view>
					</view>
				</scroll-view>
			</view>

			<!-- 搜索和筛选 -->
			<view class="search-sort">
				<view class="search-input">
					<uni-icons type="search" size="20" color="#999"></uni-icons>
					<input class="input" v-model="searchKeyword" placeholder="搜索方案名称、目标人群..."
						placeholder-style="color: #aaa;" @input="handleSearchInput" />
					<view v-if="searchKeyword" class="clear-icon" @click="clearSearch">
						<uni-icons type="clear" size="18" color="#999"></uni-icons>
					</view>
				</view>
				<picker class="sort-picker" @change="onSortChange" :value="sortIndex" :range="sortOptions">
					<view class="sort-select">
						<text>{{ sortOptions[sortIndex] }}</text>
						<uni-icons type="arrowdown" size="14" color="#666"></uni-icons>
					</view>
				</picker>
			</view>
		</view>

		<!-- 方案列表 -->
		<scroll-view class="plan-list" scroll-y :style="{'height': scrollHeight + 'px'}" @scrolltolower="loadMore">
			<!-- 加载状态 -->
			<view v-if="loading" class="loading-container">
				<view class="skeleton-plan" v-for="n in 3" :key="n">
					<view class="skeleton-header">
						<view class="skeleton-title"></view>
						<view class="skeleton-tag"></view>
					</view>
					<view class="skeleton-content">
						<view class="skeleton-info">
							<view class="skeleton-info-item"></view>
							<view class="skeleton-info-item"></view>
						</view>
						<view class="skeleton-meals">
							<view class="skeleton-meal"></view>
							<view class="skeleton-meal"></view>
							<view class="skeleton-meal"></view>
						</view>
						<view class="skeleton-intro"></view>
					</view>
				</view>
			</view>

			<!-- 空状态 -->
			<view v-else-if="plans.length === 0" class="empty-state">
				<image src="/static/images/empty-plan.png" class="empty-image" mode="widthFix"></image>
				<text class="empty-text">暂无营养方案数据</text>
				<text class="empty-subtext">点击上方按钮创建你的第一个方案</text>
				<button class="empty-btn" @click="handleAddBtn">
					<uni-icons type="plus" size="16" color="#fff"></uni-icons>
					<text>开始创建</text>
				</button>
			</view>

			<!-- 方案列表 -->
			<view v-else class="plans-container">
				<view v-for="plan in plans" :key="plan.fID" class="plan-card" @click="viewPlanDetail(plan.fID)">
					<view class="plan-header">
						<view class="plan-title-section">
							<text class="plan-name">{{ plan.fName }}</text>
							<text class="plan-code">{{ plan.fID }}</text>
						</view>
						<view class="plan-tag" :style="getPlanTagStyle(plan.fType)">
							<text>{{ plan.fType || '通用' }}</text>
						</view>
					</view>

					<view class="plan-content">
						<!-- 简要信息 -->
						<view class="plan-info">
							<view class="info-item">
								<uni-icons type="person" size="14" color="#666"></uni-icons>
								<text class="info-text">{{ plan.uName || '系统' }}</text>
							</view>
							<view class="info-item">
								<uni-icons type="flag" size="14" color="#666"></uni-icons>
								<text class="info-text">{{ plan.fTarget || '通用人群' }}</text>
							</view>
						</view>

						<!-- 餐次安排摘要 -->
						<view class="meals-summary">
							<view v-if="plan.zMeal" class="meal-item">
								<text class="meal-label">早餐</text>
								<text class="meal-value">{{ getRecipeName(plan.zMeal) }}</text>
							</view>
							<view v-if="plan.zSnack" class="meal-item">
								<text class="meal-label">加餐</text>
								<text class="meal-value">{{ getRecipeName(plan.zSnack) }}</text>
							</view>
							<view v-if="plan.aMeal" class="meal-item">
								<text class="meal-label">午餐</text>
								<text class="meal-value">{{ getRecipeName(plan.aMeal) }}</text>
							</view>
							<view v-if="plan.aSnack" class="meal-item">
								<text class="meal-label">加餐</text>
								<text class="meal-value">{{ getRecipeName(plan.aSnack) }}</text>
							</view>
							<view v-if="plan.nMeal" class="meal-item">
								<text class="meal-label">晚餐</text>
								<text class="meal-value">{{ getRecipeName(plan.nMeal) }}</text>
							</view>
							<view v-if="plan.nSnack" class="meal-item">
								<text class="meal-label">加餐</text>
								<text class="meal-value">{{ getRecipeName(plan.nSnack) }}</text>
							</view>
						</view>

						<!-- 简介 -->
						<view class="plan-intro">
							<text>{{ plan.fIntro || '暂无简介' | truncate(60) }}</text>
						</view>

						<!-- 备注 -->
						<view v-if="plan.fNote" class="plan-note">
							<uni-icons type="info" size="12" color="#fa8c16"></uni-icons>
							<text class="note-text">{{ plan.fNote | truncate(40) }}</text>
						</view>
					</view>

					<!-- 操作按钮 -->
					<view class="plan-actions">
						<button class="action-btn edit" @click.stop="editPlan(plan.fID)">
							<uni-icons type="compose" size="16" color="#1890ff"></uni-icons>
							<text>编辑</text>
						</button>
						<button class="action-btn delete" @click.stop="handleDelete(plan)">
							<uni-icons type="trash" size="16" color="#FF5252"></uni-icons>
							<text>删除</text>
						</button>
					</view>
				</view>

				<!-- 加载更多 -->
				<view v-if="loadingMore" class="load-more">
					<uni-load-more status="loading"></uni-load-more>
				</view>
				<view v-else-if="hasMore" class="load-more-tip">
					<text>上拉加载更多方案</text>
				</view>
				<view v-else-if="plans.length > 0" class="load-more-tip">
					<text class="no-more">没有更多方案了</text>
				</view>
			</view>
		</scroll-view>

		<!-- 底部统计 -->
		<view class="footer-tip">
			<text>{{ activeFilter }} 共 {{ totalCount }} 个方案</text>
			<text> | </text>
			<text>最后更新: {{ lastUpdateTime }}</text>
		</view>

		<!-- 快速操作浮窗 -->
		<view class="quick-action-fab" @click="showQuickActions = !showQuickActions">
			<uni-icons type="plus" size="24" color="#fff"></uni-icons>
		</view>

		<!-- 删除确认对话框 -->
		<uni-popup ref="deletePopup" type="dialog">
			<uni-popup-dialog type="warn" title="删除确认" content="确定要删除这个营养方案吗？删除后无法恢复。" :before-close="true"
				@confirm="confirmDelete" @close="cancelDelete"></uni-popup-dialog>
		</uni-popup>
	</view>
</template>

<script>
	import {
		recipesApi
	} from '@/api/index.js';

	export default {
		data() {
			return {
				// 搜索条件
				searchKeyword: '',
				searchTimer: null,

				// 筛选和排序
				activeFilter: '全部方案',
				planCategories: [],
				sortIndex: 0,
				sortOptions: ['按创建时间', '按方案类型', '按目标人群'],

				// 方案数据
				plans: [],
				loading: false,
				loadingMore: false,

				// 分页
				currentPage: 1,
				pageSize: 10,
				hasMore: true,

				// 页面尺寸
				scrollHeight: 500,

				// 删除相关
				deletingPlanId: null,

				// 快速操作
				showQuickActions: false,

				// 食谱缓存
				recipeCache: {}
			};
		},

		filters: {
			// 截断文本
			truncate(value, length) {
				if (!value) return '';
				if (value.length > length) {
					return value.slice(0, length) + '...';
				}
				return value;
			}
		},

		computed: {
			// 总数据量
			totalCount() {
				return this.plans.length;
			},

			// 最后更新时间
			lastUpdateTime() {
				if (this.plans.length === 0) return '暂无数据';

				const latest = this.plans.reduce((latest, plan) => {
					const time = plan.fUpdateTime || plan.fCreateTime;
					return time > latest ? time : latest;
				}, '');

				return latest ? this.formatTime(latest) : '未知时间';
			}
		},

		onLoad() {
			this.initScrollHeight();
			this.loadPlans();
		},

		onShow() {
			// 页面显示时刷新数据
			if (this.plans.length > 0) {
				this.refreshList();
			}
		},

		methods: {
			// 初始化页面高度
			initScrollHeight() {
				const systemInfo = uni.getSystemInfoSync();
				// 计算可用高度：屏幕高度 - 头部区域高度
				this.scrollHeight = systemInfo.windowHeight - 220;
			},

			// 加载方案列表
			async loadPlans(isRefresh = false) {
				if (this.loading && !isRefresh) return;

				try {
					this.loading = true;

					const params = {
						searchName: this.searchKeyword,
						searchType: this.activeFilter !== '全部方案' ? this.activeFilter : '',
						searchTarget: '',
						page: this.currentPage,
						pageSize: this.pageSize
					};

					const res = await recipesApi._getNutritionalRecipesByFactor(
						params.searchName,
						params.searchType,
						params.searchTarget
					);

					if (res) {
						const data = res || [];

						if (isRefresh) {
							this.plans = data;
						} else {
							this.plans = [...this.plans, ...data];
						}

						// 判断是否有更多数据
						this.hasMore = data.length >= this.pageSize;

						// 提取方案类型
						this.extractPlanCategories();

						// 缓存食谱名称
						await this.cacheRecipeNames(data);
					} else {
						this.plans = [];
						this.hasMore = false;
					}
				} catch (error) {
					console.error('加载营养方案失败:', error);
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					});
				} finally {
					this.loading = false;
					this.loadingMore = false;
				}
			},

			// 提取所有方案类型
			extractPlanCategories() {
				const categoriesSet = new Set();
				this.plans.forEach(plan => {
					if (plan.fType) {
						categoriesSet.add(plan.fType);
					}
				});
				this.planCategories = Array.from(categoriesSet);
			},

			// 缓存食谱名称
			async cacheRecipeNames(plans) {
				const recipeIds = new Set();

				plans.forEach(plan => {
					const mealFields = ['zMeal', 'zSnack', 'aMeal', 'aSnack', 'nMeal', 'nSnack'];
					mealFields.forEach(field => {
						if (plan[field]) {
							recipeIds.add(plan[field]);
						}
					});
				});

				// 这里需要根据实际情况获取食谱名称
				// 暂时用占位符
				if (recipeIds.size > 0) {
					recipeIds.forEach(id => {
						if (!this.recipeCache[id]) {
							this.recipeCache[id] = `${id}`;
						}
					});
				}
			},

			// 获取食谱名称
			getRecipeName(recipeId) {
				return this.recipeCache[recipeId] || '未知食谱';
			},

			// 获取方案标签样式
			getPlanTagStyle(type) {
				const colorMap = {
					'减肥': {
						background: 'rgba(255, 107, 107, 0.9)',
						color: '#fff'
					},
					'增肌': {
						background: 'rgba(77, 150, 255, 0.9)',
						color: '#fff'
					},
					'健康': {
						background: 'rgba(107, 207, 127, 0.9)',
						color: '#fff'
					},
					'素食': {
						background: 'rgba(129, 199, 132, 0.9)',
						color: '#fff'
					},
					'儿童': {
						background: 'rgba(255, 183, 77, 0.9)',
						color: '#fff'
					},
					'老年': {
						background: 'rgba(149, 117, 205, 0.9)',
						color: '#fff'
					}
				};

				return colorMap[type] || {
					background: 'rgba(102, 102, 102, 0.9)',
					color: '#fff'
				};
			},

			// 设置筛选
			setFilter(filter) {
				this.activeFilter = filter;
				this.currentPage = 1;
				this.loadPlans(true);
			},

			// 排序变化
			onSortChange(e) {
				this.sortIndex = e.detail.value;
				// 这里可以根据排序选项重新排序数据
				this.applySorting();
			},

			// 应用排序
			applySorting() {
				const sortMethod = this.sortIndex;
				this.plans.sort((a, b) => {
					switch (sortMethod) {
						case 1: // 按方案类型
							return (a.fType || '').localeCompare(b.fType || '');
						case 2: // 按目标人群
							return (a.fTarget || '').localeCompare(b.fTarget || '');
						default: // 按创建时间
							return new Date(b.fUpdateTime || b.fCreateTime) - new Date(a.fUpdateTime || a
								.fCreateTime);
					}
				});
			},

			// 清除搜索
			clearSearch() {
				this.searchKeyword = '';
				this.currentPage = 1;
				this.loadPlans(true);
			},

			// 搜索输入处理（防抖）
			handleSearchInput() {
				if (this.searchTimer) {
					clearTimeout(this.searchTimer);
				}

				this.searchTimer = setTimeout(() => {
					this.performSearch();
				}, 300);
			},

			// 执行搜索
			async performSearch() {
				this.currentPage = 1;
				this.loadPlans(true);
			},

			// 格式化时间
			formatTime(time) {
				if (!time) return '';
				const date = new Date(time);
				return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
			},

			// 添加方案
			handleAddBtn() {
				uni.navigateTo({
					url: '/pages/nutrition-plan/edit'
				});
			},

			// 查看方案详情
			viewPlanDetail(planId) {
				uni.navigateTo({
					url: `/pages/nutrition-plan/detail?id=${planId}`
				});
			},

			// 编辑方案
			editPlan(planId) {
				uni.navigateTo({
					url: `/pages/nutrition-plan/edit?id=${planId}`
				});
			},

			// 删除方案
			handleDelete(plan) {
				this.deletingPlanId = plan.fID;
				this.$refs.deletePopup.open();
			},

			// 确认删除
			async confirmDelete() {
				if (!this.deletingPlanId) return;

				try {
					uni.showLoading({
						title: '删除中...'
					});

					// 这里需要调用实际的删除API
					// const res = await _delNutritionalRecipes(this.deletingPlanId);

					// 模拟删除成功
					setTimeout(() => {
						// 从列表中移除
						this.plans = this.plans.filter(plan => plan.fID !== this.deletingPlanId);

						uni.showToast({
							title: '删除成功',
							icon: 'success'
						});

						this.deletingPlanId = null;
						this.$refs.deletePopup.close();
						uni.hideLoading();
					}, 500);

				} catch (error) {
					console.error('删除方案失败:', error);
					uni.showToast({
						title: '删除失败',
						icon: 'none'
					});
					uni.hideLoading();
				}
			},

			// 取消删除
			cancelDelete() {
				this.deletingPlanId = null;
				this.$refs.deletePopup.close();
			},

			// 刷新列表
			refreshList() {
				this.currentPage = 1;
				this.hasMore = true;
				this.loadPlans(true);
			},

			// 加载更多
			loadMore() {
				if (this.loadingMore || !this.hasMore) return;

				this.loadingMore = true;
				this.currentPage++;
				this.loadPlans();
			}
		}
	};
</script>

<style lang="scss">
	@import "@/scss/variables.scss";

	.nutrition-plan-manage {
		min-height: 100vh;
		background: #f5f7fa;
		padding: 0 24rpx 10rpx;

		.header-section {
			.header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding: 40rpx 0 20rpx;

				.header-left {
					.title {
						font-size: 40rpx;
						font-weight: 600;
						color: #333;
						display: block;
					}
				}

				.add-btn {
					background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
					color: #fff;
					padding: 0rpx 20rpx;
					border-radius: 40rpx;
					font-size: 24rpx;
					display: flex;
					align-items: center;
					gap: 8rpx;
					box-shadow: 0 4rpx 16rpx rgba(24, 144, 255, 0.3);

					&::after {
						border: none;
					}
				}
			}

			.quick-filter {
				margin-bottom: 20rpx;

				.filter-scroll {
					white-space: nowrap;

					.filter-tags {
						display: inline-flex;
						gap: 16rpx;
						padding: 8rpx 0;

						.filter-tag {
							padding: 12rpx 24rpx;
							background: #fff;
							border-radius: 40rpx;
							font-size: 26rpx;
							color: #666;
							display: flex;
							align-items: center;
							gap: 6rpx;
							border: 2rpx solid transparent;
							box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);

							&.active {
								background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
								color: #fff;
							}
						}
					}
				}
			}

			.search-sort {
				display: flex;
				gap: 20rpx;
				margin-bottom: 20rpx;

				.search-input {
					flex: 1;
					display: flex;
					align-items: center;
					background: #fff;
					border-radius: 40rpx;
					padding: 0 24rpx;
					height: 72rpx;
					box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);

					.input {
						flex: 1;
						margin: 0 16rpx;
						font-size: 28rpx;
						height: 100%;
					}

					.clear-icon {
						padding: 8rpx;
					}
				}

				.sort-picker {
					background: #fff;
					border-radius: 40rpx;
					padding: 0 24rpx;
					height: 72rpx;
					display: flex;
					align-items: center;
					box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);

					.sort-select {
						display: flex;
						align-items: center;
						gap: 8rpx;
						font-size: 26rpx;
						color: #666;
					}
				}
			}
		}

		.plan-list {
			margin-bottom: 0rpx;

			.loading-container {
				.skeleton-plan {
					background: #fff;
					border-radius: 16rpx;
					margin-bottom: 20rpx;
					overflow: hidden;
					padding: 24rpx;

					.skeleton-header {
						display: flex;
						justify-content: space-between;
						align-items: center;
						margin-bottom: 20rpx;

						.skeleton-title {
							width: 60%;
							height: 36rpx;
							background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
							background-size: 200% 100%;
							animation: loading 1.5s infinite;
							border-radius: 8rpx;
						}

						.skeleton-tag {
							width: 80rpx;
							height: 32rpx;
							background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
							background-size: 200% 100%;
							animation: loading 1.5s infinite;
							border-radius: 20rpx;
						}
					}

					.skeleton-content {
						.skeleton-info {
							display: flex;
							gap: 24rpx;
							margin-bottom: 20rpx;

							.skeleton-info-item {
								width: 120rpx;
								height: 28rpx;
								background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
								background-size: 200% 100%;
								animation: loading 1.5s infinite;
								border-radius: 6rpx;
							}
						}

						.skeleton-meals {
							display: flex;
							flex-direction: column;
							gap: 12rpx;
							margin-bottom: 20rpx;

							.skeleton-meal {
								height: 32rpx;
								background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
								background-size: 200% 100%;
								animation: loading 1.5s infinite;
								border-radius: 6rpx;
							}
						}

						.skeleton-intro {
							width: 100%;
							height: 24rpx;
							background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
							background-size: 200% 100%;
							animation: loading 1.5s infinite;
							border-radius: 6rpx;
						}
					}
				}
			}

			.empty-state {
				text-align: center;
				padding: 80rpx 0;

				.empty-image {
					width: 200rpx;
					height: 200rpx;
					margin-bottom: 24rpx;
				}

				.empty-text {
					font-size: 32rpx;
					color: #666;
					display: block;
					margin-bottom: 12rpx;
				}

				.empty-subtext {
					font-size: 26rpx;
					color: #999;
					display: block;
					margin-bottom: 40rpx;
				}

				.empty-btn {
					background: $primary-color;
					color: #fff;
					padding: 20rpx 40rpx;
					border-radius: 40rpx;
					font-size: 28rpx;
					display: inline-flex;
					align-items: center;
					gap: 8rpx;

					&::after {
						border: none;
					}
				}
			}

			.plans-container {
				.plan-card {
					background: #fff;
					border-radius: 16rpx;
					margin-bottom: 20rpx;
					overflow: hidden;
					box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
					transition: all 0.3s ease;

					&:active {
						transform: translateY(-2rpx);
						box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.12);
					}

					.plan-header {
						display: flex;
						justify-content: space-between;
						align-items: flex-start;
						padding: 24rpx 24rpx 16rpx;

						.plan-title-section {
							flex: 1;
							min-width: 0;

							.plan-name {
								font-size: 32rpx;
								font-weight: 600;
								color: #333;
								display: block;
								margin-bottom: 8rpx;
								overflow: hidden;
								text-overflow: ellipsis;
								white-space: nowrap;
							}

							.plan-code {
								font-size: 22rpx;
								color: #999;
								display: block;
							}
						}

						.plan-tag {
							padding: 6rpx 16rpx;
							border-radius: 20rpx;
							font-size: 22rpx;
							font-weight: 500;
							flex-shrink: 0;
							margin-left: 16rpx;
						}
					}

					.plan-content {
						padding: 0 24rpx 16rpx;

						.plan-info {
							display: flex;
							gap: 24rpx;
							margin-bottom: 16rpx;

							.info-item {
								display: flex;
								align-items: center;
								gap: 6rpx;

								.info-text {
									font-size: 24rpx;
									color: #666;
								}
							}
						}

						.meals-summary {
							background: #f8f9fa;
							border-radius: 12rpx;
							padding: 16rpx;
							margin-bottom: 16rpx;

							.meal-item {
								display: flex;
								align-items: center;
								padding: 10rpx 0;

								&:not(:last-child) {
									border-bottom: 1rpx solid #e8e8e8;
								}

								&.snacks {
									border-bottom: none;
									padding-top: 12rpx;

									.meal-value {
										color: #666;
									}
								}

								.meal-label {
									font-size: 24rpx;
									color: #666;
									width: 80rpx;
									flex-shrink: 0;
									font-weight: 500;
								}

								.meal-value {
									font-size: 26rpx;
									color: #333;
									flex: 1;
									overflow: hidden;
									text-overflow: ellipsis;
									white-space: nowrap;
								}
							}
						}

						.plan-intro {
							font-size: 26rpx;
							line-height: 1.4;
							color: #666;
							margin-bottom: 12rpx;
							display: -webkit-box;
							-webkit-line-clamp: 2;
							-webkit-box-orient: vertical;
							overflow: hidden;
						}

						.plan-note {
							display: flex;
							align-items: center;
							gap: 8rpx;
							background: #fff7e6;
							border-radius: 8rpx;
							padding: 12rpx 16rpx;
							margin-top: 12rpx;

							.note-text {
								font-size: 24rpx;
								color: #666;
								flex: 1;
							}
						}
					}

					.plan-actions {
						display: flex;
						gap: 12rpx;
						padding: 16rpx 24rpx;
						border-top: 1rpx solid #f0f0f0;

						.action-btn {
							flex: 1;
							display: flex;
							align-items: center;
							justify-content: center;
							gap: 8rpx;
							height: 64rpx;
							border-radius: 12rpx;
							font-size: 26rpx;
							font-weight: 500;
							background: transparent;
							border: 2rpx solid transparent;

							&.edit {
								color: #1890ff;
								border-color: #1890ff;

								&:active {
									background: rgba(24, 144, 255, 0.1);
								}
							}

							&.delete {
								color: #FF5252;
								border-color: #FF5252;

								&:active {
									background: rgba(255, 82, 82, 0.1);
								}
							}

							&::after {
								border: none;
							}
						}
					}
				}

				.load-more,
				.load-more-tip {
					padding: 30rpx 0;
					text-align: center;
					font-size: 26rpx;
					color: #999;

					.no-more {
						color: #ccc;
					}
				}
			}
		}

		/* 底部提示 */
		.footer-tip {
			position: fixed;
			bottom: 0;
			left: 0;
			right: 0;
			background: #fff;
			padding: 24rpx;
			text-align: center;
			font-size: 24rpx;
			color: #999;
			border-top: 2rpx solid #f0f0f0;
			z-index: 10;
			display: flex;
			justify-content: center;
			gap: 16rpx;
		}

		.quick-action-fab {
			position: fixed;
			right: 40rpx;
			bottom: 120rpx;
			width: 100rpx;
			height: 100rpx;
			background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			box-shadow: 0 6rpx 24rpx rgba(24, 144, 255, 0.4);
			z-index: 100;
		}
	}

	@keyframes loading {
		0% {
			background-position: 200% 0;
		}

		100% {
			background-position: -200% 0;
		}
	}

	/* 平板适配 */
	@media (min-width: 768px) {
		.nutrition-plan-manage {
			max-width: 750px;
			margin: 0 auto;
		}
	}
</style>