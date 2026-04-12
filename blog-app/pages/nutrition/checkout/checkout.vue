<template>
	<view class="checkout-page">
		<!-- 头部 -->
		<view class="header">
			<uni-icons type="arrowleft" size="28" @click="goBack"></uni-icons>
			<text class="title">确认订单</text>
		</view>

		<!-- 开饭时间 -->
		<view class="time-section">
			<text class="time-label">期望什么时候开饭</text>
			<picker mode="time" :value="deliveryTime" @change="onTimeChange">
				<view class="time-picker">
					<text>{{ deliveryTime || '请选择开饭时间' }}</text>
					<uni-icons type="arrowdown" size="16" color="#999"></uni-icons>
				</view>
			</picker>
		</view>

		<!-- 订单信息 -->
		<view class="order-section">
			<view class="section-title">
				<text>菜单信息</text>
				<text class="edit-btn" @click="gotoCart">修改</text>
			</view>

			<view class="order-items">
				<view v-for="item in orderItems" :key="item.id" class="order-item">
					<text class="item-name">{{ item.time }}</text>
					<text class="item-name">{{ item.foodName }} ×{{ item.quantity }}</text>
					<!-- <text class="item-price">¥{{ item.price * item.quantity }}</text> -->
				</view>
			</view>

			<view class="order-remark">
				<text class="remark-label">订单备注：</text>
				<textarea class="remark-input" v-model="remark" placeholder="请输入备注信息（口味、忌口等）" maxlength="100"
					placeholder-style="color: #aaa;"></textarea>
				<text class="word-count">{{ remark.length }}/100</text>
			</view>
		</view>

		<!-- 支付方式 -->
		<!-- <view class="payment-section">
			<view class="section-title">
				<text>支付方式</text>
			</view>
			<view class="payment-methods">
				<view v-for="method in paymentMethods" :key="method.value" class="payment-method"
					:class="{ 'active': paymentMethod === method.value }" @click="selectPayment(method.value)">
					<view class="method-left">
						<uni-icons :type="method.icon" size="24"
							:color="paymentMethod === method.value ? '#1890ff' : '#666'"></uni-icons>
						<text class="method-name">{{ method.name }}</text>
					</view>
					<uni-icons v-if="paymentMethod === method.value" type="checkmark" size="20"
						color="#1890ff"></uni-icons>
				</view>
			</view>
		</view> -->

		<!-- 优惠信息 -->
		<!-- <view class="discount-section">
			<view class="section-title">
				<text>优惠信息</text>
			</view>
			<view class="discount-item" @click="selectCoupon">
				<text class="discount-label">优惠券</text>
				<view class="discount-right">
					<text v-if="couponSelected" class="discount-value">-¥{{ discount }}</text>
					<text v-else class="no-discount">暂无可用</text>
					<uni-icons type="arrowright" size="16" color="#999"></uni-icons>
				</view>
			</view>
		</view> -->

		<!-- 订单金额 -->
		<!-- <view class="amount-section">
			<view class="amount-item">
				<text class="amount-label">商品金额</text>
				<text class="amount-value">¥{{ subtotal }}</text>
			</view>
			<view class="amount-item">
				<text class="amount-label">配送费</text>
				<text class="amount-value">¥{{ deliveryFee }}</text>
			</view>
			<view v-if="couponSelected" class="amount-item">
				<text class="amount-label">优惠券</text>
				<text class="amount-value discount">-¥{{ discount }}</text>
			</view>
			<view class="amount-total">
				<text class="total-label">合计</text>
				<text class="total-value">¥{{ totalAmount }}</text>
			</view>
		</view> -->

		<!-- 底部支付栏 -->
		<view class="payment-footer">
			<!-- <view class="total-info">
				<text class="total-text">¥{{ totalAmount }}</text>
				<text class="total-desc">待支付</text>
			</view> -->
			<button class="pay-btn" @click="submitOrder">
				确认
				<!-- // {{ paymentButtonText }} -->
			</button>
		</view>
	</view>
</template>

<script>
	import {
		recipeApi,
		dietPlanApi
	} from '@/api/index.js';
	
	export default {
		data() {
			return {
				selectedAddress: null,
				deliveryTime: '',
				orderItems: [],
				order:[],
				remark: '',
				subtotal: 0,
				deliveryFee: 3,
				discount: 5,
				couponSelected: false,
				paymentMethod: 'wechat',
				paymentMethods: [{
						value: 'wechat',
						name: '微信支付',
						icon: 'weixin'
					},
					{
						value: 'alipay',
						name: '支付宝',
						icon: 'alipay'
					},
					{
						value: 'balance',
						name: '余额支付',
						icon: 'wallet'
					}
				]
			};
		},
		computed: {
			totalAmount() {
				let total = this.subtotal + this.deliveryFee;
				if (this.couponSelected) {
					total = Math.max(0, total - this.discount);
				}
				return total.toFixed(2);
			},
			paymentButtonText() {
				switch (this.paymentMethod) {
					case 'wechat':
						return '微信支付';
					case 'alipay':
						return '支付宝支付';
					case 'balance':
						return '余额支付';
					default:
						return '立即支付';
				}
			}
		},
		onLoad() {
			this.loadOrderData();
			this.setDefaultDeliveryTime();
		},
		methods: {
			loadOrderData() {
				const cartData = uni.getStorageSync('cartData');
				if (cartData) {
					this.order = cartData;
					this.orderItems = cartData.planInfoEntities || [];
					this.remark = cartData.note || '';
					this.couponSelected = cartData.couponSelected || false;
					this.calculateSubtotal();
				}
			},

			calculateSubtotal() {
				this.subtotal = this.orderItems.reduce((total, item) =>
					total + (item.price * item.quantity), 0);
			},

			setDefaultDeliveryTime() {
				const now = new Date();
				const hour = now.getHours();
				const minute = now.getMinutes();

				// 默认设置为1小时后
				const deliveryHour = (hour + 1) % 24;
				this.deliveryTime = `${deliveryHour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
			},

			onTimeChange(e) {
				this.deliveryTime = e.detail.value;
			},

			selectAddress() {
				uni.navigateTo({
					url: '/pages/order/address/address'
				});
			},

			selectPayment(method) {
				this.paymentMethod = method;
			},

			selectCoupon() {
				uni.navigateTo({
					url: '/pages/nutrition/coupon/coupon'
				});
			},

			gotoCart() {
				uni.navigateTo({
					url: '/pages/nutrition/cart/cart'
				});
			},

			goBack() {
				uni.navigateBack();
			},

			async submitOrder() {
				if (!this.deliveryTime) {
					uni.showToast({
						title: '请选择开饭时间',
						icon: 'none'
					});
					return;
				}

				this.order.note = this.remark;
				
				if(this.order.id){
					const res = await dietPlanApi._operationDietPlan(this.order);
					if(res.status === 200){
						// 清空购物车
						this.clearCart();
						// 跳转到历史订单界面页面
						uni.reLaunch({
							url: `/pages/nutrition/order-history/order-history`
						});
					}
				}
			},


			saveOrder(orderData) {
				// 获取历史订单
				let orders = uni.getStorageSync('orderHistory') || [];

				// 添加新订单
				orders.unshift({
					...orderData,
					status: 'paid',
					statusText: '已通知'
				});

				// 保存到缓存
				uni.setStorageSync('orderHistory', orders);
			},

			clearCart() {
				uni.removeStorageSync('cartData');
			}
		}
	};
</script>

<style lang="scss">
	@import "@/scss/variables.scss";

	.checkout-page {
		background: #f5f7fa;
		min-height: 100vh;
		padding-bottom: 120rpx;

		.header {
			background: #fff;
			padding: 40rpx 24rpx 20rpx;
			display: flex;
			align-items: center;
			gap: 16rpx;
			position: sticky;
			top: 0;
			z-index: 100;

			.title {
				font-size: 32rpx;
				font-weight: 600;
				color: #333;
			}
		}

		.time-section {
			background: #fff;
			margin: 20rpx 20rpx 20rpx;
			padding: 24rpx;
			border-radius: 16rpx;
			display: flex;
			justify-content: space-between;
			align-items: center;
			box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);

			.time-label {
				font-size: 26rpx;
				color: #333;
			}

			.time-picker {
				display: flex;
				align-items: center;
				gap: 8rpx;
				font-size: 26rpx;
				color: #1890ff;
			}
		}

		.order-section {
			background: #fff;
			margin: 0 20rpx 20rpx;
			padding: 24rpx;
			border-radius: 16rpx;
			box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);

			.section-title {
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-bottom: 20rpx;
				font-size: 26rpx;
				color: #333;
				font-weight: 600;

				.edit-btn {
					font-size: 24rpx;
					color: #1890ff;
					padding: 8rpx 16rpx;

					&:active {
						background: #f5f7fa;
						border-radius: 20rpx;
					}
				}
			}

			.order-items {
				.order-item {
					display: flex;
					justify-content: space-between;
					padding: 16rpx 0;
					border-bottom: 2rpx solid #f0f0f0;

					&:last-child {
						border-bottom: none;
					}

					.item-name {
						font-size: 26rpx;
						color: #333;
					}

					.item-price {
						font-size: 26rpx;
						font-weight: 600;
						color: #333;
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
					font-size: 24rpx;
					color: #333;
					margin-bottom: 16rpx;
				}

				.remark-input {
					width: 90%;
					height: 120rpx;
					background: #f5f7fa;
					border-radius: 12rpx;
					padding: 16rpx;
					font-size: 24rpx;
					color: #333;
					margin-bottom: 8rpx;
				}

				.word-count {
					text-align: right;
					font-size: 20rpx;
					color: #999;
					display: block;
				}
			}

			.order-remark {
				background: #f8f9fa;
				padding: 16rpx;
				border-radius: 12rpx;
				margin-top: 16rpx;

				.remark-label {
					font-size: 24rpx;
					color: #666;
				}

				.remark-content {
					font-size: 24rpx;
					color: #333;
				}
			}
		}

		.payment-section {
			background: #fff;
			margin: 0 20rpx 20rpx;
			padding: 24rpx;
			border-radius: 16rpx;
			box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);

			.section-title {
				font-size: 26rpx;
				color: #333;
				font-weight: 600;
				margin-bottom: 20rpx;
			}

			.payment-methods {
				.payment-method {
					display: flex;
					justify-content: space-between;
					align-items: center;
					padding: 24rpx 0;
					border-bottom: 2rpx solid #f0f0f0;

					&:last-child {
						border-bottom: none;
					}

					&.active {
						.method-name {
							color: #1890ff;
						}
					}

					.method-left {
						display: flex;
						align-items: center;
						gap: 16rpx;

						.method-name {
							font-size: 26rpx;
							color: #333;
						}
					}
				}
			}
		}

		.discount-section {
			background: #fff;
			margin: 0 20rpx 20rpx;
			padding: 24rpx;
			border-radius: 16rpx;
			box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);

			.section-title {
				font-size: 26rpx;
				color: #333;
				font-weight: 600;
				margin-bottom: 20rpx;
			}

			.discount-item {
				display: flex;
				justify-content: space-between;
				align-items: center;

				.discount-label {
					font-size: 26rpx;
					color: #333;
				}

				.discount-right {
					display: flex;
					align-items: center;
					gap: 8rpx;

					.discount-value {
						font-size: 26rpx;
						color: #ff3b30;
					}

					.no-discount {
						font-size: 24rpx;
						color: #999;
					}
				}
			}
		}

		.amount-section {
			background: #fff;
			margin: 0 20rpx 20rpx;
			padding: 24rpx;
			border-radius: 16rpx;
			box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);

			.amount-item {
				display: flex;
				justify-content: space-between;
				padding: 16rpx 0;
				border-bottom: 2rpx solid #f0f0f0;
				font-size: 26rpx;

				.amount-label {
					color: #666;
				}

				.amount-value {
					color: #333;
					font-weight: 600;

					&.discount {
						color: #52c41a;
					}
				}
			}

			.amount-total {
				display: flex;
				justify-content: space-between;
				padding: 24rpx 0 0;
				margin-top: 16rpx;
				border-top: 2rpx solid #f0f0f0;

				.total-label {
					font-size: 28rpx;
					color: #333;
					font-weight: 600;
				}

				.total-value {
					font-size: 36rpx;
					color: #ff3b30;
					font-weight: 600;
				}
			}
		}

		.payment-footer {
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
				text-align: center;

				.total-text {
					font-size: 36rpx;
					font-weight: 600;
					color: #ff3b30;
					display: block;
				}

				.total-desc {
					font-size: 22rpx;
					color: #999;
					display: block;
				}
			}

			.pay-btn {
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