<template>
	<view class="cart-page">
		<!-- 头部 -->
		<view class="header">
			<view class="header-left">
				<uni-icons type="arrowleft" size="28" @click="goBack"></uni-icons>
				<text class="title">{{ this.order.createDate }}购物车</text>
			</view>
			<view class="header-right">
				<text class="clear-btn" @click="clearCart">清空</text>
			</view>
		</view>

		<!-- 购物车内容 -->
		<scroll-view class="cart-content" scroll-y :style="{height: scrollHeight + 'px'}">
			<!-- 空状态 -->
			<view v-if="cartItems.length === 0" class="empty-cart">
				<image src="/static/images/empty-cart.png" class="empty-image"></image>
				<text class="empty-text">购物车空空如也</text>
				<text class="empty-subtext">快去添加喜欢的菜品吧</text>
				<button class="empty-btn" @click="goBack">去点餐</button>
			</view>

			<!-- 购物车列表 -->
			<view v-else>
				<!-- 出餐时间 -->
				<view class="delivery-section">
					<view class="section-title">
						<uni-icons type="time" size="20" color="#1890ff"></uni-icons>
						<text>预计出餐时间</text>
					</view>
					<text class="delivery-time">{{ estimatedTime }}</text>
				</view>

				<!-- 购物车商品 -->
				<view class="cart-section">
					<view class="section-title">
						<uni-icons type="cart" size="20" color="#1890ff"></uni-icons>
						<text>购物车</text>
						<text class="section-count">({{ totalQuantity }}件)</text>
					</view>

					<view class="cart-items">
						<view v-for="item in cartItems" :key="item.id" class="cart-item">
							<view class="item-left">
								<image v-if="item.image" :src="getFullImageUrl(item.image)" class="item-image"></image>
								<view v-else class="recipe-img-placeholder">
									<uni-icons type="food" size="40" color="#ccc"></uni-icons>
									<text>暂无图片</text>
								</view>
								<view class="item-info">
									<text class="item-name">{{ item.foodName }}</text>
									<view class="item-nutrition">
										<text class="nutrition-tag">{{ item.energy }} kcal</text>
										<text class="nutrition-tag">{{ item.carbonWater }}g 碳水</text>
										<text class="nutrition-tag">{{ item.fat }}g 脂肪</text>
										<text class="nutrition-tag">{{ item.protein }}g 蛋白</text>
									</view>
									<text class="item-price">{{ item.time }}</text>
								</view>
							</view>

							<view class="item-right">
								<!-- <view class="quantity-control">
									<button class="quantity-btn minus" @click="decreaseQuantity(item)">-</button>
									<text class="quantity">{{ item.quantity }}</text>
									<button class="quantity-btn plus" @click="increaseQuantity(item)">+</button>
								</view> -->
								<text class="item-total" v-if="false">¥{{ item.price * item.quantity }}</text>
								<button class="delete-btn" @click="removeItem(item)">
									<uni-icons type="trash" size="18" color="#999"></uni-icons>
								</button>
							</view>
						</view>
					</view>
				</view>

				<!-- 优惠券 -->
				<!-- <view class="coupon-section">
					<view class="section-title">
						<uni-icons type="ticket" size="20" color="#ff9800"></uni-icons>
						<text>优惠券</text>
					</view>
					<view class="coupon-list">
						<view class="coupon-item" @click="selectCoupon">
							<view class="coupon-left">
								<text class="coupon-amount">¥5</text>
								<text class="coupon-desc">满30元可用</text>
							</view>
							<view class="coupon-right">
								<text class="coupon-status">立即使用</text>
								<uni-icons type="arrowright" size="16" color="#999"></uni-icons>
							</view>
						</view>
					</view>
				</view> -->

				<!-- 订单备注 -->
				<view class="remark-section">
					<view class="section-title">
						<uni-icons type="compose" size="20" color="#4CAF50"></uni-icons>
						<text>订单备注</text>
					</view>
					<textarea class="remark-input" v-model="remark" placeholder="请输入备注信息（口味、忌口等）" maxlength="100"
						placeholder-style="color: #aaa;"></textarea>
					<text class="word-count">{{ remark.length }}/100</text>
				</view>

				<!-- 营养统计 -->
				<view class="nutrition-summary">
					<view class="section-title">
						<uni-icons type="medal" size="20" color="#52c41a"></uni-icons>
						<text>营养摄入统计</text>
					</view>
					<view class="nutrition-grid">
						<view class="nutrition-item">
							<text class="nutrition-value">{{ currentCalories }}</text>
							<text class="nutrition-label">热量(kcal)</text>
						</view>
						<view class="nutrition-item">
							<text class="nutrition-value">{{ currentProtein }}g</text>
							<text class="nutrition-label">蛋白质</text>
						</view>
						<view class="nutrition-item">
							<text class="nutrition-value">{{ currentCarbs }}g</text>
							<text class="nutrition-label">碳水</text>
						</view>
						<view class="nutrition-item">
							<text class="nutrition-value">{{ currentFat }}g</text>
							<text class="nutrition-label">脂肪</text>
						</view>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 底部结算栏 -->
		<view v-if="cartItems.length > 0" class="settle-footer">
			<!-- <view class="total-info">
				<text class="total-label">合计</text>
				<text class="total-price">¥{{ totalPrice }}</text>
				<text v-if="discount > 0" class="discount-price">¥{{ originalPrice }}</text>
			</view> -->
			<button class="settle-btn" @click="gotoCheckout">确认好啦({{ totalQuantity }})</button>
		</view>
	</view>
</template>

<script>
	import {
		recipeApi,
		dietPlanApi
	} from '@/api/index.js';
	import {
		IMG_URL
	} from '@/api/config.js';
	export default {
		data() {
			return {
				order:[],
				cartItems: [],
				scrollHeight: 500,
				estimatedTime: '60-80分钟',
				remark: '',
				discount: 5,
				couponSelected: false,
				currentCalories: 0,
				currentProtein: 0,
				currentCarbs: 0,
				currentFat: 0,
			};
		},
		computed: {
			totalQuantity() {
				return this.cartItems.reduce((total, item) => total + 1, 0);
			},
			totalPrice() {
				const total = this.cartItems.reduce((total, item) =>
					total + (item.price * item.quantity), 0);
				return this.couponSelected ? Math.max(0, total - this.discount) : total;
			},
			originalPrice() {
				return this.cartItems.reduce((total, item) =>
					total + (item.price * item.quantity), 0);
			},
			
		},
		onShow() {
			this.loadCartData();
		},
		onLoad() {
			this.initScrollHeight();
			this.loadCartData();
		},
		methods: {
			initScrollHeight() {
				const systemInfo = uni.getSystemInfoSync();
				this.scrollHeight = systemInfo.windowHeight - 150;
			},

			loadCartData() {
				const cartData = uni.getStorageSync('cartData');
				this.order = cartData;
				if (cartData && cartData.planInfoEntities) {
					this.cartItems = cartData.planInfoEntities;
					this.currentCalories = cartData.energy;
					this.currentCarbs = cartData.carbonWater;
					this.currentProtein = cartData.protein;
					this.currentFat = cartData.fat;
					this.remark = cartData.note;
				}
			},

			saveCartData() {
				const cartData = uni.getStorageSync('cartData') || {};
				cartData.planInfoEntities = this.cartItems;
				uni.setStorageSync('cartData', cartData);
			},

			increaseQuantity(item) {
				item.quantity++;
				this.saveCartData();
			},

			decreaseQuantity(item) {
				if (item.quantity > 1) {
					item.quantity--;
				} else {
					this.removeItem(item);
				}
				this.saveCartData();
			},

			removeItem(item) {
				const index = this.cartItems.findIndex(i => i.id === item.id);
				if (index !== -1) {
					this.cartItems.splice(index, 1);
					this.saveCartData();
					uni.showToast({
						title: '已删除',
						icon: 'success'
					});
				}
			},

			clearCart() {
				uni.showModal({
					title: '确认清空',
					content: '确定要清空购物车吗？',
					success: (res) => {
						if (res.confirm) {
							this.cartItems = [];
							this.saveCartData();
							uni.removeStorageSync('cartData')
							uni.showToast({
								title: '已清空',
								icon: 'success'
							});
						}
					}
				});
			},

			selectCoupon() {
				this.couponSelected = !this.couponSelected;
				uni.showToast({
					title: this.couponSelected ? '优惠券已使用' : '优惠券已取消',
					icon: 'none'
				});
			},

			goBack() {
				uni.navigateBack();
			},

			gotoCheckout() {
				// 保存备注信息
				const cartData = uni.getStorageSync('cartData') || {};
				cartData.note = this.remark;
				cartData.couponSelected = this.couponSelected;
				uni.setStorageSync('cartData', cartData);

				uni.navigateTo({
					url: '/pages/nutrition/checkout/checkout'
				});
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
	.cart-page {
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

			.clear-btn {
				font-size: 26rpx;
				color: #999;
				padding: 8rpx 16rpx;

				&:active {
					background: #f5f7fa;
					border-radius: 20rpx;
				}
			}
		}

		.cart-content {
			padding: 0 24rpx 0 0;

			.empty-cart {
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

			.delivery-section {
				background: #fff;
				border-radius: 16rpx;
				padding: 24rpx;
				margin: 20rpx 0;
				display: flex;
				justify-content: space-between;
				align-items: center;

				.section-title {
					display: flex;
					align-items: center;
					gap: 8rpx;
					font-size: 26rpx;
					color: #333;
				}

				.delivery-time {
					font-size: 28rpx;
					font-weight: 600;
					color: #1890ff;
				}
			}

			.cart-section {
				background: #fff;
				border-radius: 16rpx;
				padding: 24rpx;
				margin: 20rpx 0;

				.section-title {
					display: flex;
					align-items: center;
					gap: 8rpx;
					font-size: 26rpx;
					color: #333;
					margin-bottom: 20rpx;

					.section-count {
						color: #999;
						margin-left: 4rpx;
					}
				}

				.cart-items {
					.cart-item {
						display: flex;
						justify-content: space-between;
						padding: 20rpx 0;
						border-bottom: 2rpx solid #f0f0f0;

						&:last-child {
							border-bottom: none;
						}

						.item-left {
							display: flex;
							flex: 1;

							.item-image {
								width: 120rpx;
								height: 120rpx;
								border-radius: 12rpx;
								margin-right: 20rpx;
							}
							
							.recipe-img-placeholder {
								width: 120rpx;
								height: 120rpx;
								margin-right: 20rpx;
								background: #f8f9fa;
								display: flex;
								flex-direction: column;
								align-items: center;
								justify-content: center;
								color: #ccc;
								gap: 12rpx;
							}

							.item-info {
								flex: 1;

								.item-name {
									font-size: 28rpx;
									font-weight: 600;
									color: #333;
									display: block;
									margin-bottom: 8rpx;
								}

								.item-nutrition {
									display: flex;
									gap: 2rpx;
									margin-bottom: 8rpx;

									.nutrition-tag {
										font-size: 12rpx;
										color: #666;
										padding: 4rpx 12rpx;
										background: #f5f7fa;
										border-radius: 12rpx;
									}
								}

								.item-price {
									font-size: 26rpx;
									font-weight: 600;
									color: #ff3b30;
								}
							}
						}

						.item-right {
							display: flex;
							flex-direction: column;
							align-items: flex-end;
							gap: 12rpx;

							.quantity-control {
								display: flex;
								align-items: center;
								gap: 16rpx;

								.quantity-btn {
									width: 48rpx;
									height: 48rpx;
									border-radius: 50%;
									background: #f5f7fa;
									display: flex;
									align-items: center;
									justify-content: center;
									font-size: 28rpx;
									color: #333;

									&::after {
										border: none;
									}
								}

								.quantity {
									font-size: 26rpx;
									font-weight: 600;
									color: #333;
									min-width: 40rpx;
									text-align: center;
								}
							}

							.item-total {
								font-size: 26rpx;
								font-weight: 600;
								color: #333;
							}

							.delete-btn {
								background: transparent;
								padding: 8rpx;

								&::after {
									border: none;
								}
							}
						}
					}
				}
			}

			.coupon-section {
				background: #fff;
				border-radius: 16rpx;
				padding: 24rpx;
				margin: 20rpx 0;

				.section-title {
					display: flex;
					align-items: center;
					gap: 8rpx;
					font-size: 26rpx;
					color: #333;
					margin-bottom: 20rpx;
				}

				.coupon-list {
					.coupon-item {
						display: flex;
						justify-content: space-between;
						align-items: center;
						padding: 20rpx;
						background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
						border-radius: 12rpx;

						.coupon-left {
							display: flex;
							align-items: center;
							gap: 16rpx;

							.coupon-amount {
								font-size: 36rpx;
								font-weight: 600;
								color: #ff3b30;
							}

							.coupon-desc {
								font-size: 24rpx;
								color: #666;
							}
						}

						.coupon-right {
							display: flex;
							align-items: center;
							gap: 8rpx;

							.coupon-status {
								font-size: 24rpx;
								color: #666;
							}
						}
					}
				}
			}

			.remark-section {
				background: #fff;
				border-radius: 16rpx;
				padding: 24rpx;
				margin: 20rpx 0;

				.section-title {
					display: flex;
					align-items: center;
					gap: 8rpx;
					font-size: 26rpx;
					color: #333;
					margin-bottom: 16rpx;
				}

				.remark-input {
					width: 90%;
					height: 120rpx;
					background: #f5f7fa;
					border-radius: 12rpx;
					padding: 16rpx;
					font-size: 26rpx;
					color: #333;
					margin-bottom: 8rpx;
				}

				.word-count {
					text-align: right;
					font-size: 22rpx;
					color: #999;
					display: block;
				}
			}

			.nutrition-summary {
				background: #fff;
				border-radius: 16rpx;
				padding: 24rpx;
				margin: 20rpx 0;

				.section-title {
					display: flex;
					align-items: center;
					gap: 8rpx;
					font-size: 26rpx;
					color: #333;
					margin-bottom: 20rpx;
				}

				.nutrition-grid {
					display: grid;
					grid-template-columns: repeat(4, 1fr);
					gap: 16rpx;

					.nutrition-item {
						text-align: center;
						padding: 20rpx;
						background: #f8f9fa;
						border-radius: 12rpx;

						.nutrition-value {
							font-size: 28rpx;
							font-weight: 600;
							color: #1890ff;
							display: block;
							margin-bottom: 8rpx;
						}

						.nutrition-label {
							font-size: 22rpx;
							color: #666;
						}
					}
				}
			}
		}

		.settle-footer {
			position: fixed;
			bottom: 0;
			left: 0;
			right: 0;
			background: #fff;
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 20rpx 24rpx;
			box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.1);
			z-index: 100;

			.total-info {
				.total-label {
					font-size: 24rpx;
					color: #666;
					display: block;
				}

				.total-price {
					font-size: 36rpx;
					font-weight: 600;
					color: #ff3b30;
					display: block;
				}

				.discount-price {
					font-size: 22rpx;
					color: #999;
					text-decoration: line-through;
					display: block;
				}
			}

			.settle-btn {
				background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
				color: #fff;
				padding: 20rpx 48rpx;
				border-radius: 40rpx;
				font-size: 28rpx;
				font-weight: 600;

				&::after {
					border: none;
				}
			}
		}
	}
</style>