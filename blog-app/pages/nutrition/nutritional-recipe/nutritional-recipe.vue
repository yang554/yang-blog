<template>
	<view class="recipe-page">
		<!-- 头部 -->
		<view class="header">
			<view class="header-left">
				<text class="title">营养食谱</text>
			</view>
			<view class="header-right">
				<button class="add-btn" @click="createRecipe">
					<uni-icons type="plus" size="20" color="#fff"></uni-icons>
					<text>创建食谱</text>
				</button>
			</view>
		</view>

		<!-- 快速筛选 -->
		<view class="quick-filter">
			<scroll-view class="filter-scroll" scroll-x>
				<view class="filter-tags">
					<view class="filter-tag" :class="{ active: activeFilter === '全部菜系' }" @click="setFilter('全部菜系')">
						<text>全部菜系</text>
					</view>
					<view v-for="category in categories" :key="category" class="filter-tag"
						:class="{ active: activeFilter === category }" @click="setFilter(category)">
						<text>{{ category }}</text>
					</view>
				</view>
			</scroll-view>
		</view>

		<!-- 搜索和排序 -->
		<view class="search-sort">
			<view class="search-input">
				<uni-icons type="search" size="20" color="#999"></uni-icons>
				<input class="input" v-model="searchKeyword" placeholder="搜索食谱名称或食材..." placeholder-style="color: #aaa;"
					@input="handleSearchInput" />
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

		<!-- 食谱列表 -->
		<scroll-view class="recipe-list" scroll-y :style="{height: scrollHeight + 'px'}" @scrolltolower="loadMore">
			<!-- 加载状态 -->
			<view v-if="loading" class="loading-container">
				<view class="skeleton-recipe" v-for="n in 3" :key="n">
					<view class="skeleton-image"></view>
					<view class="skeleton-content">
						<view class="skeleton-title"></view>
						<view class="skeleton-tags">
							<view class="skeleton-tag"></view>
							<view class="skeleton-tag"></view>
						</view>
						<view class="skeleton-stats">
							<view class="skeleton-stat"></view>
							<view class="skeleton-stat"></view>
							<view class="skeleton-stat"></view>
						</view>
					</view>
				</view>
			</view>

			<!-- 空状态 -->
			<view v-else-if="displayRecipes.length === 0" class="empty-state">
				<image src="/static/images/empty-recipe.png" class="empty-image" mode="widthFix"></image>
				<text class="empty-text">暂无食谱数据</text>
				<text class="empty-subtext">点击上方按钮创建你的第一个食谱</text>
				<button class="empty-btn" @click="createRecipe">
					<uni-icons type="plus" size="16" color="#fff"></uni-icons>
					<text>开始创建</text>
				</button>
			</view>

			<!-- 食谱卡片 -->
			<view v-else>
				<view v-for="recipe in displayRecipes" :key="recipe.fID" class="recipe-card"
					@click="viewRecipeDetail(recipe)">
					<!-- 食谱图片 -->
					<view class="recipe-image">
						<image v-if="recipe.fImg" :src="getFullImageUrl(recipe.fImg)" class="recipe-img"
							mode="aspectFill" lazy-load>
						</image>
						<view v-else class="recipe-img-placeholder">
							<uni-icons type="food" size="40" color="#ccc"></uni-icons>
							<text>暂无图片</text>
						</view>
						<view class="recipe-type-tag breakfast">
							<text>{{ getRecipeTypeText(recipe.fType) }}</text>
						</view>
						<view v-if="recipe.calories < 500" class="low-cal-badge">
							<uni-icons type="fire" size="12" color="#fff"></uni-icons>
							<text>低卡</text>
						</view>
					</view>

					<!-- 食谱信息 -->
					<view class="recipe-info">
						<view class="recipe-header">
							<text class="recipe-name">{{ recipe.fName }}</text>
							<view class="recipe-actions">
								<button class="icon-btn" @click.stop="quickEditRecipe(recipe)">
									<uni-icons type="compose" size="16" color="#666"></uni-icons>
								</button>
								<button class="icon-btn" @click.stop="confirmDeleteRecipe(recipe)">
									<uni-icons type="trash" size="16" color="#FF5252"></uni-icons>
								</button>
							</view>
						</view>

						<!-- 简介 -->
						<text class="recipe-intro" v-if="recipe.fIntro">{{ recipe.fIntro }}</text>

						<!-- 营养标签 -->
						<view class="nutrition-tags">
							<view class="nutrition-tag" :class="getCalorieLevel(recipe.calories)">
								<uni-icons type="fire" size="12" color="#FF5722"></uni-icons>
								<text>{{ recipe.calories }} kcal</text>
							</view>
							<view class="nutrition-tag protein">
								<uni-icons type="muscle" size="12" color="#4CAF50"></uni-icons>
								<text>{{ recipe.protein }}g 蛋白</text>
							</view>
							<view class="nutrition-tag carbs">
								<uni-icons type="leaf" size="12" color="#FF9800"></uni-icons>
								<text>{{ recipe.carbs }}g 碳水</text>
							</view>
							<view class="nutrition-tag fat">
								<uni-icons type="fat" size="12" color="#FFC107"></uni-icons>
								<text>{{ recipe.fat }}g 脂肪</text>
							</view>
						</view>

						<!-- 配料预览 -->
						<view class="ingredients-preview" v-if="recipe.ingredients && recipe.ingredients.length > 0">
							<text class="preview-label">主要食材：</text>
							<text class="preview-text">
								{{ recipe.ingredients.slice(0, 3).map(i => `${i.foodName} ${i.amount}${i.unit}`).join('、') }}
								<text v-if="recipe.ingredients.length > 3">等 {{ recipe.ingredients.length }} 种</text>
							</text>
						</view>

						<!-- 制作时间估算 -->
						<view class="cooking-estimate" v-if="recipe.estimateTime">
							<uni-icons type="time" size="14" color="#666"></uni-icons>
							<text>约 {{ recipe.estimateTime }} 分钟</text>
						</view>

						<!-- 难度和评分 -->
						<view class="recipe-meta">
							<view class="meta-item" v-if="recipe.difficulty">
								<view class="difficulty-stars">
									<uni-icons v-for="n in 3" :key="n"
										:type="n <= recipe.difficulty ? 'star-filled' : 'star'" size="12"
										:color="n <= recipe.difficulty ? '#FFB300' : '#ddd'">
									</uni-icons>
								</view>
								<text class="meta-text">
									{{ ['简单', '中等', '困难'][recipe.difficulty - 1] }}
								</text>
							</view>
							<view class="meta-item" v-if="recipe.rating">
								<uni-icons type="heart" size="12" color="#FF5252"></uni-icons>
								<text class="meta-text">{{ recipe.rating }} 人喜欢</text>
							</view>
							<text class="update-time" v-if="recipe.fUpdateTime">
								{{ recipe.fUpdateTime | formatTimes }}
							</text>
						</view>
					</view>
				</view>

				<!-- 加载更多 -->
				<view v-if="loadingMore" class="load-more">
					<uni-load-more status="loading"></uni-load-more>
				</view>
				<view v-else-if="hasMore" class="load-more-tip">
					<text>上拉加载更多食谱</text>
				</view>
				<view v-else-if="displayRecipes.length > 0" class="load-more-tip">
					<text class="no-more">没有更多食谱了</text>
				</view>
			</view>
		</scroll-view>

		<!-- 底部统计 -->
		<view class="footer-tip">
			<text>{{ this.activeFilter }} 共 {{ totalCount }} 道食谱</text>
			<text> | </text>
			<text>最后更新: {{ lastUpdateTime }}</text>
		</view>

		<!-- 快速操作浮窗 -->
		<view class="quick-action-fab" @click="showQuickActions = !showQuickActions">
			<uni-icons type="plus" size="24" color="#fff"></uni-icons>
			<view v-if="showQuickActions" class="action-menu">
				<view class="action-item">
					<text>还没想好</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		recipeApi,
		foodApi
	} from '@/api/index.js';
	import {
		IMG_URL
	} from '@/api/config.js';
	import {
		formatTime
	} from '@/common/formatDate.js';


	export default {
		name: "nutritionalRecipe",
		data() {
			return {
				// 食谱数据
				recipes: [],
				displayRecipes: [],
				loading: false,
				loadingMore: false,

				// 分页
				pageSize: 10,
				currentPage: 1,
				hasMore: true,
				scrollHeight: 500,

				// 筛选和搜索
				searchTimer: null, // 搜索防抖
				activeFilter: '全部菜系',
				searchKeyword: '',
				categories: [],
				sortIndex: 0,
				sortOptions: ['按创建时间', '按热量', '按碳水', '按蛋白'],

				// 导航
				showQuickActions: false,
			};
		},
		filters: {
			formatTimes(value) {
				return formatTime(value);
			}
		},
		computed: {
			// 总数据量
			totalCount() {
				if (this.searchKeyword || this.activeCategory !== '全部') {
					return this.displayRecipes.length;
				}
				return this.recipes.length;
			},
			// 最后更新时间
			lastUpdateTime() {
				if (this.recipes.length === 0) return '暂无数据';

				const latest = this.recipes.reduce((latest, recipe) => {
					const time = recipe.fUpdateTime || recipe.fUpdateTime;
					return time > latest ? time : latest;
				}, '');

				return latest || '未知时间';
			},
			// 统计计算
			totalCalories() {
				return this.recipes.reduce((sum, recipe) => sum + (parseFloat(recipe.calories) || 0), 0);
			},
			avgProtein() {
				const total = this.recipes.reduce((sum, recipe) => sum + (parseFloat(recipe.protein) || 0), 0);
				return this.recipes.length > 0 ? total / this.recipes.length : 0;
			},
			avgFat() {
				const total = this.recipes.reduce((sum, recipe) => sum + (parseFloat(recipe.fat) || 0), 0);
				return this.recipes.length > 0 ? total / this.recipes.length : 0;
			}
		},
		onLoad() {
			this.initScrollHeight();
			this.loadRecipes();
		},
		onPullDownRefresh() {
			this.refreshRecipes();
		},
		onShow() {
			this.loadRecipes();
		},
		methods: {
			// 初始化滚动区域高度
			initScrollHeight() {
				const systemInfo = uni.getSystemInfoSync();
				// 屏幕高度 - 头部
				this.scrollHeight = systemInfo.windowHeight - 170;
			},

			// 加载食谱列表
			async loadRecipes() {
				try {
					this.loading = true;
					const res = await recipeApi._getNutritionalRecipeAll();
					if (res) {
						this.recipes = res.map(recipe => ({
							...recipe,
							ingredients: recipe.ingredients ? JSON.parse(recipe.ingredients) : []
						}));

						this.applyFilters();
						this.extractCategories();
					}
				} catch (error) {
					console.error('加载食谱失败:', error);
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					});
				} finally {
					this.loading = false;
				}
			},

			// 提取所有菜系
			extractCategories() {
				const categoriesSet = new Set();
				this.recipes.forEach(recipe => {
					if (recipe.fType) {
						categoriesSet.add(recipe.fType);
					}
				});
				this.categories = Array.from(categoriesSet);
			},

			// 应用筛选和排序
			applyFilters() {
				let filtered = [...this.recipes];

				// 搜索筛选
				if (this.searchKeyword) {
					const keyword = this.searchKeyword.toLowerCase();
					filtered = filtered.filter(recipe =>
						recipe.fName.toLowerCase().includes(keyword)
					);
				}

				// 类型筛选
				if (this.activeFilter !== '全部菜系') {
					filtered = filtered.filter(recipe => recipe.fType === this.activeFilter);
				}

				// 排序
				const sortMethod = this.sortIndex;
				filtered.sort((a, b) => {
					switch (sortMethod) {
						case 1: // 按热量
							return b.calories - a.calories;
						case 2: // 按碳水
							return (b.carbs || 0) - (a.carbs || 0);
						case 3: // 按蛋白
							return (b.protein || 1) - (a.protein || 1);
						default: // 按创建时间
							return new Date(b.fUpdateTime || b.fCreateTime) - new Date(a.fUpdateTime || a
								.fCreateTime);
					}
				});

				// 分页显示
				const startIndex = (this.currentPage - 1) * this.pageSize;
				const endIndex = startIndex + this.pageSize;
				this.displayRecipes = filtered.slice(0, endIndex);
				this.hasMore = endIndex < filtered.length;
			},

			// 设置筛选
			setFilter(filter) {
				this.activeFilter = filter;
				this.currentPage = 1;
				this.applyFilters();
			},

			// 排序变化
			onSortChange(e) {
				this.sortIndex = e.detail.value;
				this.applyFilters();
			},

			// 清除搜索
			clearSearch() {
				this.searchKeyword = '';
				this.loadRecipes();
			},

			// 加载更多
			loadMore() {
				if (this.loadingMore || !this.hasMore) return;

				this.loadingMore = true;
				setTimeout(() => {
					this.currentPage++;
					this.applyFilters();
					this.loadingMore = false;
				}, 500);
			},

			// 刷新食谱
			async refreshRecipes() {
				try {
					const res = await recipeApi.getAllRecipes();
					if (res && res.data) {
						this.recipes = res.data.map(recipe => ({
							...recipe,
							ingredients: recipe.ingredients ? JSON.parse(recipe.ingredients) : []
						}));
						this.applyFilters();
						uni.showToast({
							title: '刷新成功',
							icon: 'success'
						});
					}
				} catch (error) {
					console.error('刷新失败:', error);
				} finally {
					uni.stopPullDownRefresh();
				}
			},
			// 搜索输入处理（防抖）
			handleSearchInput() {
				// 清除之前的定时器
				if (this.searchTimer) {
					clearTimeout(this.searchTimer);
				}

				// 设置新的定时器，延迟300ms执行
				this.searchTimer = setTimeout(() => {
					this.performSearch();
				}, 300);
			},
			// 执行搜索
			async performSearch() {
				const keyword = this.searchKeyword.toLowerCase().trim();

				if (!keyword) {
					this.loadRecipes();
					return;
				}

				try {
					// 尝试后端搜索
					const res = await recipeApi._getNutritionalRecipeByFactor(keyword, '');

					if (res) {
						this.recipes = res.map(recipe => ({
							...recipe,
							ingredients: recipe.ingredients ? JSON.parse(recipe.ingredients) : []
						}));
						this.applyFilters();
						// this.extractCategories();
					} else {
						// 如果后端搜索失败，使用前端搜索
						this.frontendSearch(keyword);
					}
				} catch (error) {
					console.error('搜索失败，使用前端搜索:', error);
					// 使用前端搜索作为备用
					this.frontendSearch(keyword);
				}
			},
			// 前端搜索
			frontendSearch(keyword) {
				this.displayRecipes = this.recipes.filter(recipe => {
					return (
						(recipe.fName && recipe.fName.toLowerCase().includes(keyword)) ||
						(recipe.fType && recipe.fType.toLowerCase().includes(keyword))
					);
				});
			},
			// 创建新食谱
			createRecipe() {
				uni.navigateTo({
					url: '/pages/nutrition/recipe-edit/recipe-edit'
				});
			},

			// 查看食谱详情
			viewRecipeDetail(recipe) {
				uni.navigateTo({
					url: `/pages/nutrition/recipe-detail/recipe-detail?id=${recipe.fID}`
				});
			},

			// 快速编辑
			quickEditRecipe(recipe) {
				uni.navigateTo({
					url: `/pages/nutrition/recipe-edit/recipe-edit?id=${recipe.fID}`
				});
			},

			// 确认删除
			confirmDeleteRecipe(recipe) {
				uni.showModal({
					title: '删除确认',
					content: `确定要删除食谱 "${recipe.fName}" 吗？`,
					success: (res) => {
						if (res.confirm) {
							this.deleteRecipe(recipe.fID);
						}
					}
				});
			},

			// 删除食谱
			async deleteRecipe(id) {
				try {
					uni.showLoading({
						title: '删除中...'
					});
					await recipeApi._delNutritionalRecipe(id);

					// 从列表中移除
					this.recipes = this.recipes.filter(recipe => recipe.fID !== id);
					this.applyFilters();

					uni.showToast({
						title: '删除成功',
						icon: 'success'
					});
				} catch (error) {
					console.error('删除失败:', error);
					uni.showToast({
						title: '删除失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			getRecipeTypeText(type) {
				return type || '食谱';
			},

			getCalorieLevel(calories) {
				if (calories < 300) return 'low';
				if (calories < 500) return 'medium';
				return 'high';
			},

			// 获取完整图片URL
			getFullImageUrl(path) {
				if (!path) return '';
				if (path.startsWith('http')) return path;
				if (path.startsWith('/')) {
					return IMG_URL + path;
				}
				return IMG_URL + '/' + path;
			}
		}
	};
</script>

<style lang="scss">
	@import "@/scss/variables.scss";

	.recipe-page {
		min-height: 100vh;
		background: #f5f7fa;
		padding: 0 24rpx 10rpx;

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


		.recipe-list {
			margin-bottom: 0rpx;

			.loading-container {
				.skeleton-recipe {
					background: #fff;
					border-radius: 16rpx;
					margin-bottom: 20rpx;
					overflow: hidden;

					.skeleton-image {
						height: 200rpx;
						background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
						background-size: 200% 100%;
						animation: loading 1.5s infinite;
					}

					.skeleton-content {
						padding: 24rpx;

						.skeleton-title {
							width: 70%;
							height: 32rpx;
							background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
							background-size: 200% 100%;
							animation: loading 1.5s infinite;
							border-radius: 8rpx;
							margin-bottom: 20rpx;
						}

						.skeleton-tags {
							display: flex;
							gap: 12rpx;
							margin-bottom: 16rpx;

							.skeleton-tag {
								width: 100rpx;
								height: 40rpx;
								background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
								background-size: 200% 100%;
								animation: loading 1.5s infinite;
								border-radius: 20rpx;
							}
						}

						.skeleton-stats {
							display: flex;
							gap: 20rpx;

							.skeleton-stat {
								flex: 1;
								height: 24rpx;
								background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
								background-size: 200% 100%;
								animation: loading 1.5s infinite;
								border-radius: 6rpx;
							}
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
					background: #1890ff;
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

			.recipe-card {
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

				.recipe-image {
					position: relative;
					height: 300rpx;

					.recipe-img {
						width: 100%;
						height: 100%;
					}

					.recipe-img-placeholder {
						width: 100%;
						height: 100%;
						background: #f8f9fa;
						display: flex;
						flex-direction: column;
						align-items: center;
						justify-content: center;
						color: #ccc;
						gap: 12rpx;
					}

					.recipe-type-tag {
						position: absolute;
						top: 20rpx;
						left: 20rpx;
						padding: 8rpx 16rpx;
						border-radius: 20rpx;
						font-size: 24rpx;
						color: #fff;
						font-weight: 500;

						&.breakfast {
							background: rgba(162, 109, 3, 0.9);
						}

						&.lunch {
							background: rgba(76, 175, 80, 0.9);
						}

						&.dinner {
							background: rgba(33, 150, 243, 0.9);
						}

						&.snack {
							background: rgba(156, 39, 176, 0.9);
						}

						&.default {
							background: rgba(0, 0, 0, 0.6);
						}
					}

					.low-cal-badge {
						position: absolute;
						top: 20rpx;
						right: 20rpx;
						padding: 6rpx 12rpx;
						background: rgba(255, 87, 34, 0.9);
						border-radius: 20rpx;
						font-size: 20rpx;
						color: #fff;
						display: flex;
						align-items: center;
						gap: 4rpx;
					}
				}

				.recipe-info {
					padding: 24rpx;

					.recipe-header {
						display: flex;
						justify-content: space-between;
						align-items: flex-start;
						margin-bottom: 16rpx;

						.recipe-name {
							font-size: 32rpx;
							font-weight: 600;
							color: #333;
							flex: 1;
							margin-right: 16rpx;
						}

						.recipe-actions {
							display: flex;
							gap: 8rpx;

							.icon-btn {
								padding: 8rpx;
								background: transparent;

								&::after {
									border: none;
								}
							}
						}
					}

					.recipe-intro {
						font-size: 26rpx;
						color: #666;
						line-height: 1.4;
						margin-bottom: 20rpx;
						display: block;
					}

					.nutrition-tags {
						display: flex;
						flex-wrap: wrap;
						gap: 12rpx;
						margin-bottom: 20rpx;

						.nutrition-tag {
							padding: 8rpx 16rpx;
							border-radius: 20rpx;
							font-size: 24rpx;
							display: flex;
							align-items: center;
							gap: 4rpx;

							&.low {
								background: rgba(76, 175, 80, 0.1);
								color: #4CAF50;
							}

							&.medium {
								background: rgba(255, 152, 0, 0.1);
								color: #FF9800;
							}

							&.high {
								background: rgba(244, 67, 54, 0.1);
								color: #F44336;
							}

							&.protein {
								background: rgba(76, 175, 80, 0.1);
								color: #4CAF50;
							}

							&.carbs {
								background: rgba(255, 152, 0, 0.1);
								color: #FF9800;
							}

							&.fat {
								background: rgba(255, 193, 7, 0.1);
								color: #FFC107;
							}
						}
					}

					.ingredients-preview {
						background: #f8f9fa;
						border-radius: 12rpx;
						padding: 16rpx;
						margin-bottom: 16rpx;

						.preview-label {
							font-size: 24rpx;
							color: #666;
							font-weight: 500;
							margin-right: 8rpx;
						}

						.preview-text {
							font-size: 24rpx;
							color: #333;
							line-height: 1.4;
						}
					}

					.cooking-estimate {
						display: flex;
						align-items: center;
						gap: 8rpx;
						margin-bottom: 16rpx;
						font-size: 24rpx;
						color: #666;
					}

					.recipe-meta {
						display: flex;
						align-items: center;
						gap: 20rpx;
						padding-top: 16rpx;
						border-top: 1rpx solid #eee;

						.meta-item {
							display: flex;
							align-items: center;
							gap: 6rpx;

							.difficulty-stars {
								display: flex;
								gap: 2rpx;
							}

							.meta-text {
								font-size: 22rpx;
								color: #666;
							}
						}

						.update-time {
							font-size: 22rpx;
							color: #999;
							margin-left: auto;
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

		.quick-action-fab {
			position: fixed;
			right: 40rpx;
			bottom: 120rpx;
			width: 100rpx;
			height: 100rpx;
			background: linear-gradient(135deg, #1890ff, #52c41a);
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			box-shadow: 0 6rpx 24rpx rgba(24, 144, 255, 0.4);
			z-index: 100;

			.action-menu {
				position: absolute;
				bottom: 120rpx;
				right: 0;
				background: rgba(0, 0, 0, 0.9);
				border-radius: 12rpx;
				padding: 16rpx 0;
				min-width: 200rpx;

				.action-item {
					padding: 16rpx 24rpx;
					display: flex;
					align-items: center;
					gap: 12rpx;
					font-size: 26rpx;
					color: #fff;

					&:active {
						background: rgba(255, 255, 255, 0.1);
					}
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

	@keyframes loading {
		0% {
			background-position: 200% 0;
		}

		100% {
			background-position: -200% 0;
		}
	}
</style>