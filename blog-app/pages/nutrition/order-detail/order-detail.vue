<template>
	<view class="order-detail-page">
		<!-- 内容区域 -->
		<scroll-view class="content-scroll" scroll-y>
			<!-- 订单基本信息卡片 -->
			<view class="order-info-card">
				<view class="order-header">
					<view class="order-title">
						<text class="order-id">订单号: {{ orderData.id }}</text>
						<view class="order-status">
							<text class="status-text">已创建</text>
						</view>
					</view>
					<text class="order-date">{{ formatDate(orderData.createDate) }}</text>
				</view>

				<view class="order-summary">
					<view class="summary-item">
						<text class="summary-label">总能量</text>
						<text class="summary-value energy">{{ orderData.energy }} kcal</text>
					</view>
					<view class="nutrition-grid">
						<view class="nutrition-item">
							<text class="nutrition-label">蛋白质</text>
							<text class="nutrition-value protein">{{ orderData.protein }}g</text>
						</view>
						<view class="nutrition-item">
							<text class="nutrition-label">脂肪</text>
							<text class="nutrition-value fat">{{ orderData.fat }}g</text>
						</view>
						<view class="nutrition-item">
							<text class="nutrition-label">碳水</text>
							<text class="nutrition-value carbs">{{ orderData.carbonWater }}g</text>
						</view>
					</view>
				</view>

				<!-- 备注信息 -->
				<view v-if="orderData.note" class="order-note">
					<text class="note-label">备注:</text>
					<text class="note-content">{{ orderData.note }}</text>
				</view>
			</view>

			<!-- 餐次分类 -->
			<view class="meal-section">
				<view class="section-header">
					<text class="section-title">餐次计划</text>
					<text class="section-subtitle">共 {{ mealStats.total }} 道菜品</text>
				</view>

				<!-- 早餐 -->
				<view v-if="breakfastItems.length > 0" class="meal-category">
					<view class="meal-header">
						<uni-icons type="sun" size="20" color="#FF9800"></uni-icons>
						<text class="meal-name">早餐</text>
						<text class="meal-count">({{ breakfastItems.length }}道)</text>
					</view>
					<view class="meal-summary">
						<view class="meal-nutrition">
							<text class="nutrition-text">能量: {{ calculateMealTotal('energy', '早餐') }} kcal</text>
							<text class="nutrition-text">蛋白质: {{ calculateMealTotal('protein', '早餐') }}g</text>
						</view>
					</view>
					<view class="dish-list">
						<view v-for="dish in breakfastItems" :key="dish.id" class="dish-item">
							<view class="dish-info">
								<text class="dish-name">{{ dish.foodName }}</text>
								<view class="dish-nutrition">
									<view class="dish-nutrition-tag">
										<uni-icons type="fire" size="12" color="#FF5722"></uni-icons>
										<text>{{ dish.energy }} kcal</text>
									</view>
									<view class="dish-nutrition-tag">
										<uni-icons type="muscle" size="12" color="#4CAF50"></uni-icons>
										<text>{{ dish.protein }}g 蛋白</text>
									</view>
								</view>
							</view>
							<view class="dish-actions">
								<button class="detail-btn" @click="viewDishDetail(dish)">详情</button>
							</view>
						</view>
					</view>
				</view>

				<!-- 午餐 -->
				<view v-if="lunchItems.length > 0" class="meal-category">
					<view class="meal-header">
						<uni-icons type="sun" size="20" color="#FF5722"></uni-icons>
						<text class="meal-name">午餐</text>
						<text class="meal-count">({{ lunchItems.length }}道)</text>
					</view>
					<view class="meal-summary">
						<view class="meal-nutrition">
							<text class="nutrition-text">能量: {{ calculateMealTotal('energy', '午餐') }} kcal</text>
							<text class="nutrition-text">蛋白质: {{ calculateMealTotal('protein', '午餐') }}g</text>
						</view>
					</view>
					<view class="dish-list">
						<view v-for="dish in lunchItems" :key="dish.id" class="dish-item">
							<view class="dish-info">
								<text class="dish-name">{{ dish.foodName }}</text>
								<view class="dish-nutrition">
									<view class="dish-nutrition-tag">
										<uni-icons type="fire" size="12" color="#FF5722"></uni-icons>
										<text>{{ dish.energy }} kcal</text>
									</view>
									<view class="dish-nutrition-tag">
										<uni-icons type="muscle" size="12" color="#4CAF50"></uni-icons>
										<text>{{ dish.protein }}g 蛋白</text>
									</view>
								</view>
							</view>
							<view class="dish-actions">
								<button class="detail-btn" @click="viewDishDetail(dish)">详情</button>
							</view>
						</view>
					</view>
				</view>

				<!-- 晚餐 -->
				<view v-if="dinnerItems.length > 0" class="meal-category">
					<view class="meal-header">
						<uni-icons type="moon" size="20" color="#673AB7"></uni-icons>
						<text class="meal-name">晚餐</text>
						<text class="meal-count">({{ dinnerItems.length }}道)</text>
					</view>
					<view class="meal-summary">
						<view class="meal-nutrition">
							<text class="nutrition-text">能量: {{ calculateMealTotal('energy', '晚餐') }} kcal</text>
							<text class="nutrition-text">蛋白质: {{ calculateMealTotal('protein', '晚餐') }}g</text>
						</view>
					</view>
					<view class="dish-list">
						<view v-for="dish in dinnerItems" :key="dish.id" class="dish-item">
							<view class="dish-info">
								<text class="dish-name">{{ dish.foodName }}</text>
								<view class="dish-nutrition">
									<view class="dish-nutrition-tag">
										<uni-icons type="fire" size="12" color="#FF5722"></uni-icons>
										<text>{{ dish.energy }} kcal</text>
									</view>
									<view class="dish-nutrition-tag">
										<uni-icons type="muscle" size="12" color="#4CAF50"></uni-icons>
										<text>{{ dish.protein }}g 蛋白</text>
									</view>
								</view>
							</view>
							<view class="dish-actions">
								<button class="detail-btn" @click="viewDishDetail(dish)">详情</button>
							</view>
						</view>
					</view>
				</view>

				<!-- 加餐/零食 -->
				<view v-if="snackItems.length > 0" class="meal-category">
					<view class="meal-header">
						<uni-icons type="cafe" size="20" color="#795548"></uni-icons>
						<text class="meal-name">加餐/零食</text>
						<text class="meal-count">({{ snackItems.length }}道)</text>
					</view>
					<view class="meal-summary">
						<view class="meal-nutrition">
							<text class="nutrition-text">能量: {{ calculateMealTotal('energy', '加餐') }} kcal</text>
							<text class="nutrition-text">蛋白质: {{ calculateMealTotal('protein', '加餐') }}g</text>
						</view>
					</view>
					<view class="dish-list">
						<view v-for="dish in snackItems" :key="dish.id" class="dish-item">
							<view class="dish-info">
								<text class="dish-name">{{ dish.foodName }}</text>
								<view class="dish-nutrition">
									<view class="dish-nutrition-tag">
										<uni-icons type="fire" size="12" color="#FF5722"></uni-icons>
										<text>{{ dish.energy }} kcal</text>
									</view>
									<view class="dish-nutrition-tag">
										<uni-icons type="muscle" size="12" color="#4CAF50"></uni-icons>
										<text>{{ dish.protein }}g 蛋白</text>
									</view>
								</view>
							</view>
							<view class="dish-actions">
								<button class="detail-btn" @click="viewDishDetail(dish)">详情</button>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 营养统计 -->
			<view class="nutrition-statistics">
				<view class="section-header">
					<text class="section-title">营养统计分析</text>
				</view>
				<view class="stats-grid">
					<view class="stat-item">
						<text class="stat-label">每餐平均能量</text>
						<text class="stat-value">{{ calculateAverage('energy') }} kcal</text>
					</view>
					<view class="stat-item">
						<text class="stat-label">能量最高菜品</text>
						<text class="stat-value">{{ highestEnergyDish.foodName || '-' }}</text>
					</view>
					<view class="stat-item">
						<text class="stat-label">蛋白质最丰富</text>
						<text class="stat-value">{{ highestProteinDish.foodName || '-' }}</text>
					</view>
					<view class="stat-item">
						<text class="stat-label">菜品总数</text>
						<text class="stat-value">{{ orderData.planInfoEntities.length }} 道</text>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 菜品详情弹窗 -->
		<uni-popup ref="dishDetailPopup" type="center">
			<view class="dish-detail-popup">
				<view class="popup-header">
					<text class="popup-title">{{ currentDish.foodName }}</text>
					<uni-icons type="closeempty" size="24" color="#999" @click="closeDishDetail"></uni-icons>
				</view>
				<view class="popup-content">
					<view class="detail-section">
						<text class="detail-label">餐次:</text>
						<text class="detail-value">{{ currentDish.time }}</text>
					</view>
					<view class="detail-section">
						<text class="detail-label">创建时间:</text>
						<text class="detail-value">{{ formatDateTime(currentDish.createTime) }}</text>
					</view>
					<view class="detail-section">
						<text class="detail-label">备注:</text>
						<text class="detail-value">{{ currentDish.note }}</text>
					</view>
					<view class="nutrition-details">
						<view class="nutrition-detail-item">
							<text class="nutrition-detail-label">能量</text>
							<text class="nutrition-detail-value">{{ currentDish.energy }} kcal</text>
						</view>
						<view class="nutrition-detail-item">
							<text class="nutrition-detail-label">蛋白质</text>
							<text class="nutrition-detail-value">{{ currentDish.protein }}g</text>
						</view>
						<view class="nutrition-detail-item">
							<text class="nutrition-detail-label">脂肪</text>
							<text class="nutrition-detail-value">{{ currentDish.fat }}g</text>
						</view>
						<view class="nutrition-detail-item">
							<text class="nutrition-detail-label">碳水</text>
							<text class="nutrition-detail-value">{{ currentDish.carbonWater }}g</text>
						</view>
					</view>
				</view>
				<view class="popup-actions">
					<button class="popup-btn cancel" @click="closeDishDetail">关闭</button>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				//订单数据
				orderData: {},
				// 当前查看的菜品
				currentDish: {}
			};
		},
		computed: {
			// 按餐次分类的菜品
			breakfastItems() {
				return this.orderData.planInfoEntities.filter(item => item.time === '早餐');
			},
			lunchItems() {
				return this.orderData.planInfoEntities.filter(item => item.time === '午餐');
			},
			dinnerItems() {
				return this.orderData.planInfoEntities.filter(item => item.time === '晚餐');
			},
			snackItems() {
				const validMeals = ['早餐', '午餐', '晚餐'];
				return this.orderData.planInfoEntities.filter(item => !validMeals.includes(item.time));
			},
			// 餐次统计
			mealStats() {
				return {
					breakfast: this.breakfastItems.length,
					lunch: this.lunchItems.length,
					dinner: this.dinnerItems.length,
					snack: this.snackItems.length,
					total: this.orderData.planInfoEntities.length
				};
			},
			// 能量最高的菜品
			highestEnergyDish() {
				if (this.orderData.planInfoEntities.length === 0) return {};
				return this.orderData.planInfoEntities.reduce((prev, current) => {
					return (Number(prev.energy) > Number(current.energy)) ? prev : current;
				});
			},
			// 蛋白质最高的菜品
			highestProteinDish() {
				if (this.orderData.planInfoEntities.length === 0) return {};
				return this.orderData.planInfoEntities.reduce((prev, current) => {
					return (Number(prev.protein) > Number(current.protein)) ? prev : current;
				});
			}
		},
		onLoad(options) {
			const order = uni.getStorageSync("orderHistory");
			if (order) {
				this.orderData = order;
			} else if (options.orderId) {
				this.loadOrderData(options.orderId);
			} else {
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				});
			}
		},
		methods: {
			// 加载订单数据
			async loadOrderData(orderId) {
				uni.showLoading({
					title: '加载中...'
				});
				try {
					// const res = await api.getOrderDetail(orderId);
					// this.orderData = res.data;
				} catch (error) {
					console.error('加载订单失败:', error);
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},
			// 返回上一页
			goBack() {
				uni.navigateBack();
			},
			// 格式化日期
			formatDate(dateString) {
				if (!dateString) return '';
				const date = new Date(dateString);
				return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
			},
			// 格式化日期时间
			formatDateTime(dateTimeString) {
				if (!dateTimeString) return '';
				return dateTimeString.split(' ')[0].replace(/-/g, '/');
			},
			// 计算某餐次的总营养值
			calculateMealTotal(nutritionType, mealTime) {
				const items = this.orderData.planInfoEntities.filter(item => item.time === mealTime);
				if (items.length === 0) return '0.00';

				const total = items.reduce((sum, item) => {
					return sum + Number(item[nutritionType] || 0);
				}, 0);

				return total.toFixed(2);
			},
			// 计算平均值
			calculateAverage(nutritionType) {
				const total = this.orderData.planInfoEntities.reduce((sum, item) => {
					return sum + Number(item[nutritionType] || 0);
				}, 0);

				const average = total / this.orderData.planInfoEntities.length;
				return average.toFixed(2);
			},
			// 查看菜品详情
			viewDishDetail(dish) {
				this.currentDish = dish;
				this.$refs.dishDetailPopup.open();
			},
			// 关闭菜品详情
			closeDishDetail() {
				this.$refs.dishDetailPopup.close();
			},
			// 删除订单
			deleteOrder() {
				uni.showModal({
					title: '确认删除',
					content: '确定要删除这个营养计划吗？删除后无法恢复。',
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
						title: '删除成功',
						icon: 'success'
					});
					setTimeout(() => {
						uni.navigateBack();
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
			}
		}
	};
</script>

<style lang="scss" scoped>
	@import "@/scss/variables.scss";
	
	.order-detail-page {
		min-height: 100vh;
		background: #f5f7fa;

		.content-scroll {
			height: calc(100vh - 120rpx);
		}

		.order-info-card {
			background: #fff;
			margin: 20rpx 30rpx;
			padding: 30rpx;
			border-radius: 20rpx;
			box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);

			.order-header {
				margin-bottom: 30rpx;
				padding-bottom: 20rpx;
				border-bottom: 2rpx solid #f0f0f0;

				.order-title {
					display: flex;
					justify-content: space-between;
					align-items: center;
					margin-bottom: 15rpx;

					.order-id {
						font-size: 28rpx;
						color: #666;
					}

					.order-status {
						.status-text {
							background: #4CAF50;
							color: #fff;
							padding: 8rpx 20rpx;
							border-radius: 30rpx;
							font-size: 24rpx;
						}
					}
				}

				.order-date {
					font-size: 24rpx;
					color: #999;
				}
			}

			.order-summary {
				.summary-item {
					margin-bottom: 25rpx;

					.summary-label {
						display: block;
						font-size: 26rpx;
						color: #666;
						margin-bottom: 10rpx;
					}

					.summary-value {
						font-size: 48rpx;
						font-weight: 700;

						&.energy {
							color: #FF5722;
						}
					}
				}

				.nutrition-grid {
					display: grid;
					grid-template-columns: repeat(3, 1fr);
					gap: 20rpx;

					.nutrition-item {
						text-align: center;
						padding: 20rpx;
						background: #f8f9fa;
						border-radius: 12rpx;

						.nutrition-label {
							display: block;
							font-size: 24rpx;
							color: #666;
							margin-bottom: 8rpx;
						}

						.nutrition-value {
							display: block;
							font-size: 32rpx;
							font-weight: 600;

							&.protein {
								color: #4CAF50;
							}

							&.fat {
								color: #FF9800;
							}

							&.carbs {
								color: #2196F3;
							}
						}
					}
				}
			}

			.order-note {
				margin-top: 25rpx;
				padding: 20rpx;
				background: #f8f9fa;
				border-radius: 12rpx;
				border-left: 6rpx solid #2196F3;

				.note-label {
					font-size: 26rpx;
					font-weight: 600;
					color: #333;
					margin-right: 15rpx;
				}

				.note-content {
					font-size: 26rpx;
					color: #666;
				}
			}
		}

		.meal-section {
			background: #fff;
			margin: 20rpx 30rpx;
			padding: 30rpx;
			border-radius: 20rpx;
			box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);

			.section-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-bottom: 30rpx;

				.section-title {
					font-size: 32rpx;
					font-weight: 600;
					color: #333;
				}

				.section-subtitle {
					font-size: 24rpx;
					color: #999;
				}
			}

			.meal-category {
				margin-bottom: 40rpx;

				&:last-child {
					margin-bottom: 0;
				}

				.meal-header {
					display: flex;
					align-items: center;
					gap: 15rpx;
					margin-bottom: 20rpx;
					padding-bottom: 15rpx;
					border-bottom: 2rpx solid #f0f0f0;

					.meal-name {
						font-size: 30rpx;
						font-weight: 600;
						color: #333;
					}

					.meal-count {
						font-size: 24rpx;
						color: #999;
					}
				}

				.meal-summary {
					margin-bottom: 20rpx;

					.meal-nutrition {
						display: flex;
						gap: 30rpx;

						.nutrition-text {
							font-size: 26rpx;
							color: #666;
							padding: 10rpx 20rpx;
							background: #f8f9fa;
							border-radius: 30rpx;
						}
					}
				}

				.dish-list {
					.dish-item {
						display: flex;
						justify-content: space-between;
						align-items: center;
						padding: 25rpx;
						margin-bottom: 20rpx;
						background: #f8f9fa;
						border-radius: 16rpx;
						border-left: 6rpx solid #4CAF50;

						&:last-child {
							margin-bottom: 0;
						}

						.dish-info {
							flex: 1;

							.dish-name {
								display: block;
								font-size: 30rpx;
								font-weight: 600;
								color: #333;
								margin-bottom: 15rpx;
							}

							.dish-nutrition {
								display: flex;
								gap: 15rpx;

								.dish-nutrition-tag {
									display: flex;
									align-items: center;
									gap: 8rpx;
									padding: 8rpx 16rpx;
									background: rgba(255, 255, 255, 0.9);
									border-radius: 20rpx;
									font-size: 22rpx;
									color: #666;
								}
							}
						}

						.dish-actions {
							.detail-btn {
								background: #2196F3;
								color: #fff;
								padding: 12rpx 24rpx;
								border-radius: 30rpx;
								font-size: 24rpx;

								&::after {
									border: none;
								}
							}
						}
					}
				}
			}
		}

		.nutrition-statistics {
			background: #fff;
			margin: 20rpx 30rpx;
			padding: 30rpx;
			border-radius: 20rpx;
			box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);

			.section-header {
				margin-bottom: 30rpx;

				.section-title {
					font-size: 32rpx;
					font-weight: 600;
					color: #333;
				}
			}

			.stats-grid {
				display: grid;
				grid-template-columns: repeat(2, 1fr);
				gap: 20rpx;

				.stat-item {
					padding: 25rpx;
					background: #f8f9fa;
					border-radius: 16rpx;
					text-align: center;

					.stat-label {
						display: block;
						font-size: 24rpx;
						color: #666;
						margin-bottom: 10rpx;
					}

					.stat-value {
						display: block;
						font-size: 28rpx;
						font-weight: 600;
						color: #333;
					}
				}
			}
		}

		.dish-detail-popup {
			width: 600rpx;
			background: #fff;
			border-radius: 20rpx;
			overflow: hidden;

			.popup-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding: 30rpx;
				border-bottom: 2rpx solid #f0f0f0;

				.popup-title {
					font-size: 32rpx;
					font-weight: 600;
					color: #333;
				}
			}

			.popup-content {
				padding: 30rpx;
				max-height: 600rpx;
				overflow-y: auto;

				.detail-section {
					margin-bottom: 20rpx;

					.detail-label {
						font-size: 26rpx;
						font-weight: 600;
						color: #333;
						margin-right: 15rpx;
					}

					.detail-value {
						font-size: 26rpx;
						color: #666;
					}
				}

				.nutrition-details {
					margin-top: 30rpx;

					.nutrition-detail-item {
						display: flex;
						justify-content: space-between;
						align-items: center;
						padding: 20rpx;
						margin-bottom: 15rpx;
						background: #f8f9fa;
						border-radius: 12rpx;

						&:last-child {
							margin-bottom: 0;
						}

						.nutrition-detail-label {
							font-size: 26rpx;
							color: #666;
						}

						.nutrition-detail-value {
							font-size: 28rpx;
							font-weight: 600;
							color: #333;
						}
					}
				}
			}

			.popup-actions {
				display: flex;
				padding: 20rpx 30rpx;
				border-top: 2rpx solid #f0f0f0;

				.popup-btn {
					flex: 1;
					height: 70rpx;
					margin: 0 10rpx;
					border-radius: 35rpx;
					font-size: 26rpx;
					font-weight: 600;

					&::after {
						border: none;
					}

					&.cancel {
						background: #f0f0f0;
						color: #666;
					}
				}
			}
		}
	}
</style>