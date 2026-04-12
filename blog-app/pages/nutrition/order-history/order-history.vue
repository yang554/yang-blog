<template>
	<view class="history-page">
		<!-- 头部 -->
		<view class="header">
			<view class="header-left">
				<uni-icons type="arrowleft" size="28" @click="goBack"></uni-icons>
				<text class="title">订单历史</text>
			</view>
			<view class="header-right">
				<view class="filter-btn" @click="showFilter = !showFilter">
					<uni-icons type="bars" size="24" color="#666"></uni-icons>
				</view>
			</view>
		</view>

		<!-- 筛选弹窗 -->
		<view v-if="showFilter" class="filter-modal" @click="showFilter = false">
			<view class="filter-content" @click.stop>
				<view class="filter-header">
					<text class="filter-title">筛选订单</text>
					<uni-icons type="close" size="24" @click="showFilter = false"></uni-icons>
				</view>
				<view class="filter-options">
					<view class="filter-group">
						<text class="group-title">时间范围</text>
						<view class="time-range">
							<!-- 开始时间选择器 -->
							<picker mode="date" :value="startTime" @change="onStartTimeChange" :start="startDate"
								:end="endDate">
								<view class="time-input">
									<text>{{ startTime || '开始时间' }}</text>
									<uni-icons type="calendar" size="18" color="#999"></uni-icons>
								</view>
							</picker>
							<text class="time-separator">至</text>
							<!-- 结束时间选择器 -->
							<picker mode="date" :value="endTime" @change="onEndTimeChange" :start="startDate"
								:end="endDate">
								<view class="time-input">
									<text>{{ endTime || '结束时间' }}</text>
									<uni-icons type="calendar" size="18" color="#999"></uni-icons>
								</view>
							</picker>
						</view>
					</view>
				</view>
				<view class="filter-actions">
					<button class="reset-btn" @click="resetFilter">重置</button>
					<button class="confirm-btn" @click="applyFilter">确定</button>
				</view>
			</view>
		</view>

		<!-- 订单列表 -->
		<scroll-view class="order-list" scroll-y :style="{height: scrollHeight + 'px'}" @scrolltolower="loadMore">
			<!-- 空状态 -->
			<view v-if="filteredOrders.length === 0" class="empty-history">
				<image src="/static/images/empty-history.png" class="empty-image"></image>
				<text class="empty-text">暂无订单记录</text>
				<text class="empty-subtext">快去下单体验吧</text>
				<button class="empty-btn" @click="gotoOrderFood">去点餐</button>
			</view>

			<!-- 订单卡片 -->
			<view v-for="order in filteredOrders" :key="order.id" class="order-card">
				<view class="order-header" @click="viewOrderDetail(order)">
					<view class="order-info">
						<text class="order-number">订单号: {{ order.id }}</text>
						<text class="order-time">{{ order.createDate | formatTime }}</text>
					</view>
					<view class="order-status">
						<text :class="getStatusClass(order.status)">{{ order.statusText }}</text>
					</view>
				</view>

				<view class="order-items">
					<view v-for="item in order.planInfoEntities.slice(0, 4)" :key="item.id" class="order-item">
						<text class="item-name">{{ item.time }}</text>
						<text class="item-name">{{ item.foodName }}</text>
					</view>
					<view v-if="order.planInfoEntities.length > 4" class="more-items">
						<text>...等{{ order.planInfoEntities.length }}道菜品</text>
					</view>
				</view>

				<view class="order-footer">
					<text class="total-label">共{{ order.planInfoEntities.length }}道</text>

					<view class="order-actions">
						<button class="action-btn cancel" @click="deleteOrder(order)">
							删除订单
						</button>
						<button class="action-btn detail" @click="viewOrderDetail(order)">
							查看详情
						</button>
					</view>
				</view>
			</view>

			<!-- 加载更多 -->
			<view v-if="loadingMore" class="load-more">
				<uni-load-more status="loading"></uni-load-more>
			</view>
			<view v-else-if="hasMore" class="load-more-tip">
				<text>上拉加载更多</text>
			</view>
			<view v-else-if="filteredOrders.length > 0" class="load-more-tip">
				<text class="no-more">没有更多订单了</text>
			</view>
		</scroll-view>

		<!-- 统计信息 -->
		<view v-if="filteredOrders.length > 0" class="stats-footer">
			<view class="stat-item">
				<button class="index-btn" @click="gotoIndex">返回首页</button>
			</view>
			<view class="stat-item">
				<button class="food-btn" @click="gotoOrderFood">继续点餐</button>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		dietPlanApi
	} from '@/api/index.js';
	import {
		parseDate
	} from '@/common/formatDate.js';
	export default {
		data() {
			const today = new Date();
			const year = today.getFullYear();
			const month = today.getMonth() + 1;
			const day = today.getDate();
			const todayStr = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;

			return {
				orders: [],
				filteredOrders: [],
				loadingMore: false,
				hasMore: true,
				scrollHeight: 500,

				// 筛选
				showFilter: false,
				startTime: '',
				endTime: '',
				startDate: '2000-01-01', // 可选择的最小日期
				endDate: todayStr, // 可选择的最大日期

				// 分页
				pageSize: 10,
				currentPage: 1,
				currentUserInfo: uni.getStorageSync(this.$constants.USER_INFO), //当前登录用户
			};
		},
		filters: {
			formatTime(value) {
				return parseDate(value);
			}
		},
		computed: {
			totalOrders() {
				return this.filteredOrders.length;
			},

			totalSpent() {
				return this.filteredOrders.reduce((total, order) =>
					total + parseFloat(order.totalAmount), 0).toFixed(2);
			},

			avgCalories() {
				if (this.filteredOrders.length === 0) return 0;

				const totalCalories = this.filteredOrders.reduce((total, order) => {
					const orderCalories = order.planInfoEntities.reduce((sum, item) =>
						sum + (item.calories), 0);
					return total + orderCalories;
				}, 0);

				return Math.round(totalCalories / this.filteredOrders.length);
			}
		},
		onLoad() {
			this.initScrollHeight();
			this.loadOrders();
		},
		methods: {
			initScrollHeight() {
				const systemInfo = uni.getSystemInfoSync();
				this.scrollHeight = systemInfo.windowHeight - 130;
			},

			async loadOrders() {
				const res = await dietPlanApi._getDietPlanByDateScope(this.currentUserInfo.id, this.startTime, this
					.endTime);
				if (res) {
					this.orders = res
					this.applyFilters();
				}
			},

			getStatusClass(status) {
				const classes = {
					paid: 'status-paid',
					making: 'status-making',
					delivering: 'status-delivering',
					delivered: 'status-delivered',
					cancelled: 'status-cancelled'
				};
				return classes[status] || 'status-default';
			},

			// 时间选择器相关方法
			onStartTimeChange(e) {
				this.startTime = e.detail.value;
			},

			onEndTimeChange(e) {
				this.endTime = e.detail.value;
			},

			resetFilter() {
				this.startTime = '';
				this.endTime = '';
				this.currentPage = 1;
				this.applyFilters();
			},

			applyFilter() {
				this.currentPage = 1;
				this.applyFilters();
				this.showFilter = false;
			},

			applyFilters() {
				let filtered = [...this.orders];

				// 时间范围筛选
				if (this.startTime) {
					const startDate = new Date(this.startTime);
					filtered = filtered.filter(order => {
						const orderDate = new Date(order.createDate);
						return orderDate >= startDate;
					});
				}

				if (this.endTime) {
					const endDate = new Date(this.endTime + ' 23:59:59');
					filtered = filtered.filter(order => {
						const orderDate = new Date(order.createDate);
						return orderDate <= endDate;
					});
				}

				// 按时间倒序排序
				filtered.sort((a, b) => new Date(b.createDate) - new Date(a.createDate));
				// 分页
				const startIndex = (this.currentPage - 1) * this.pageSize;
				const endIndex = startIndex + this.pageSize;
				this.filteredOrders = filtered.slice(0, endIndex);
				this.hasMore = endIndex < filtered.length;
			},

			loadMore() {
				if (this.loadingMore || !this.hasMore) return;

				this.loadingMore = true;
				setTimeout(() => {
					this.currentPage++;
					this.applyFilters();
					this.loadingMore = false;
				}, 500);
			},

			//订单详情
			viewOrderDetail(order) {
				uni.setStorageSync('orderHistory', order);
				uni.navigateTo({
					url: `/pages/nutrition/order-detail/order-detail?orderId=${order.id}`
				});
			},

			//删除订单
			// 删除订单
			deleteOrder() {
				uni.showModal({
					title: '确认删除',
					content: '确定要删除这个订单吗？删除后无法恢复。',
					success: (res) => {
						if (res.confirm) {
							this.confirmDelete();
						}
					}
				});
			},
			// 确认删除
			async confirmDelete() {
				uni.showLoading({
					title: '删除中...'
				});
				try {
					// 调用删除接口
					// await api.deleteOrder(this.orderData.id);
					uni.showToast({
						title: '骗你的，删不掉的，就是要记录',
						icon: 'none'
					});
					setTimeout(() => {
						// uni.navigateBack();
					}, 1500);
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

			goBack() {
				uni.navigateBack();
			},

			gotoIndex() {
				uni.reLaunch({
					url: '/pages/index/index'
				});
			},
			gotoOrderFood() {
				uni.navigateTo({
					url: '/pages/nutrition/order-food/order-food'
				});
			}
		}
	};
</script>

<style lang="scss">
	@import "@/scss/variables.scss";

	.history-page {
		background: #f5f7fa;
		min-height: 100vh;

		.header {
			background: #fff;
			padding: 40rpx 24rpx 20rpx;
			display: flex;
			justify-content: space-between;
			align-items: center;
			position: sticky;
			top: 0;
			z-index: 100;

			.header-left {
				display: flex;
				align-items: center;
				gap: 16rpx;

				.title {
					font-size: 32rpx;
					font-weight: 600;
					color: #333;
				}
			}

			.filter-btn {
				padding: 8rpx;

				&:active {
					background: #f5f7fa;
					border-radius: 50%;
				}
			}
		}

		.filter-modal {
			position: fixed;
			top: 0;
			left: 0;
			right: 0;
			bottom: 0;
			background: rgba(0, 0, 0, 0.5);
			z-index: 1000;
			display: flex;
			align-items: flex-end;

			.filter-content {
				background: #fff;
				width: 100%;
				border-radius: 24rpx 24rpx 0 0;
				padding: 40rpx 24rpx;
				max-height: 70vh;
				overflow-y: auto;

				.filter-header {
					display: flex;
					justify-content: space-between;
					align-items: center;
					margin-bottom: 40rpx;

					.filter-title {
						font-size: 32rpx;
						font-weight: 600;
						color: #333;
					}
				}

				.filter-options {
					.filter-group {
						margin-bottom: 40rpx;

						.group-title {
							font-size: 26rpx;
							font-weight: 600;
							color: #333;
							display: block;
							margin-bottom: 20rpx;
						}

						.time-range {
							display: flex;
							align-items: center;
							gap: 20rpx;

							.time-input {
								flex: 1;
								padding: 20rpx;
								background: #f5f7fa;
								border-radius: 12rpx;
								display: flex;
								justify-content: space-between;
								align-items: center;
								font-size: 26rpx;
								color: #333;
							}

							.time-separator {
								font-size: 24rpx;
								color: #999;
							}
						}
					}
				}

				.filter-actions {
					display: flex;
					gap: 20rpx;
					margin-top: 40rpx;

					.reset-btn,
					.confirm-btn {
						flex: 1;
						padding: 14rpx 0;
						border-radius: 40rpx;
						font-size: 28rpx;
						font-weight: 600;

						&::after {
							border: none;
						}
					}

					.reset-btn {
						background: #f5f7fa;
						color: #666;
					}

					.confirm-btn {
						background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
						color: #fff;
					}
				}
			}
		}

		.order-list {
			padding: 12rpx;

			.empty-history {
				text-align: center;
				padding: 120rpx 0;

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
					background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
					color: #fff;
					padding: 20rpx 48rpx;
					border-radius: 40rpx;
					font-size: 28rpx;

					&::after {
						border: none;
					}
				}
			}

			.order-card {
				background: #fff;
				border-radius: 16rpx;
				padding: 24rpx;
				margin: 20rpx 0;
				box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);

				.order-header {
					display: flex;
					justify-content: space-between;
					align-items: flex-start;
					margin-bottom: 20rpx;

					.order-info {
						flex: 1;

						.order-number {
							font-size: 28rpx;
							font-weight: 600;
							color: #1890ff;
							display: block;
							margin-bottom: 8rpx;
						}

						.order-time {
							font-size: 24rpx;
							color: #999;
						}
					}

					.order-status {
						.status-paid {
							color: #1890ff;
						}

						.status-making {
							color: #ff9800;
						}

						.status-delivering {
							color: #52c41a;
						}

						.status-delivered {
							color: #999;
						}

						.status-cancelled {
							color: #ff3b30;
						}

						.status-default {
							color: #666;
						}
					}
				}

				.order-items {
					.order-item {
						display: flex;
						justify-content: space-between;
						padding: 12rpx 0;
						border-bottom: 2rpx solid #f0f0f0;
						font-size: 26rpx;

						&:last-child {
							border-bottom: none;
						}

						.item-name {
							color: #333;
						}
					}

					.more-items {
						padding: 12rpx 0;
						font-size: 24rpx;
						color: #999;
						text-align: center;
					}
				}

				.order-footer {
					margin-top: 20rpx;
					padding-top: 20rpx;
					border-top: 2rpx solid #f0f0f0;

					.total-label {
						font-size: 24rpx;
						color: #666;
						margin-right: 8rpx;
					}

					.order-actions {
						display: flex;
						justify-content: flex-end;
						gap: 16rpx;
						margin-top: 20rpx;

						.action-btn {
							padding: 12rpx 24rpx;
							border-radius: 40rpx;
							font-size: 24rpx;

							&::after {
								border: none;
							}

							&.cancel {
								background: #fff;
								color: #ff3b30;
								border: 2rpx solid #ff3b30;
							}

							&.detail {
								background: #f5f7fa;
								color: #666;
							}

							&:active {
								transform: scale(0.98);
							}
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

		.stats-footer {
			position: fixed;
			bottom: 0;
			left: 0;
			right: 0;
			background: #fff;
			display: flex;
			justify-content: space-around;
			padding: 24rpx 0;
			box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.1);
			z-index: 100;

			.stat-item {
				text-align: center;

				.food-btn {
					background: linear-gradient(135deg, $info-color 0%, $accent-color 100%);
					color: #fff;
					padding: 20rpx 48rpx;
					border-radius: 40rpx;
					font-size: 28rpx;
					font-weight: 600;
					margin: 0;
					line-height: 1.5;

					&::after {
						border: none;
					}

					&:active {
						transform: scale(0.98);
					}
				}

				.index-btn {
					background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
					color: #fff;
					padding: 20rpx 48rpx;
					border-radius: 40rpx;
					font-size: 28rpx;
					font-weight: 600;
					margin: 0;
					line-height: 1.5;

					&::after {
						border: none;
					}

					&:active {
						transform: scale(0.98);
					}
				}
			}
		}
	}
</style>