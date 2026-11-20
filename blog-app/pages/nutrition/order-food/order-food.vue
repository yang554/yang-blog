<template>
	<view class="order-page">
		<!-- 固定在顶部的头部区域 -->
		<view class="fixed-header">
			<!-- 头部 -->
			<view class="header">
				<view class="header-left">
					<text class="title">大王~ 想吃点儿啥？</text>
					<text class="subtitle">{{ todayDate }}</text>
				</view>
				<view class="header-right">
					<view class="cart-btn" @click="gotoCart">
						<uni-icons type="cart" size="24" color="#333"></uni-icons>
						<view v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</view>
					</view>
					<view class="history-btn" @click="gotoHistory">
						<uni-icons type="bars" size="24" color="#333"></uni-icons>
					</view>
				</view>
			</view>

			<!-- 日期选择 -->
			<view class="date-selector">
				<scroll-view class="date-scroll" scroll-x :show-scrollbar="false">
					<view class="date-list">
						<view v-for="(date, index) in dateList" :key="date.date" class="date-item"
							:class="{ 'active': activeDate === date.date, 'today': date.isToday }"
							@click="selectDate(date.date)">
							<text class="day-text">{{ date.day }}</text>
							<text class="date-text">{{ date.month }}月{{ date.day }}日</text>
							<text v-if="date.isToday" class="today-tag">今日</text>
						</view>
					</view>
				</scroll-view>
			</view>

			<!-- 餐次选择 -->
			<view class="meal-selector">
				<view class="meal-tabs">
					<view v-for="meal in meals" :key="meal.value" class="meal-tab"
						:class="{ 'active': activeMeal === meal.value }" @click="selectMeal(meal.value)">
						<uni-icons :type="meal.icon" size="20"
							:color="activeMeal === meal.value ? '#fff' : '#666'"></uni-icons>
						<text>{{ meal.label }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 内容区域 -->
		<view class="content-area">
			<!-- 菜单分类 -->
			<view class="menu-category">
				<view class="category-tabs">
					<!-- 固定"全部"按钮 -->
					<view class="category-fixed-tab" :class="{ 'active': activeCategory === 'all' }"
						@click="selectCategory('all')">
						<text>全部</text>
						<text v-if="dishes.length > 0" class="count-badge">{{ dishes.length }}</text>
					</view>

					<!-- 可滚动的分类 -->
					<scroll-view scroll-x class="category-scroll" :show-scrollbar="false">
						<view class="category-list">
							<view v-for="category in categories" :key="category.id" class="category-tab"
								:class="{ 'active': activeCategory === category.name }"
								@click="selectCategory(category.name)">
								<text>{{ category.name }}</text>
								<text v-if="category.count > 0" class="count-badge">{{ category.count }}</text>
							</view>
						</view>
					</scroll-view>
				</view>

				<!-- 菜品列表 -->
				<scroll-view class="dish-list" scroll-y :show-scrollbar="false" :style="{height: scrollHeight + 'px'}">
					<view v-for="dish in filteredDishes" :key="dish.fID" class="dish-card">
						<view class="dish-image">
							<image v-if="dish.fImg" :src="getFullImageUrl(dish.fImg)" mode="aspectFill"
								class="dish-img"></image>
							<view v-else class="recipe-img-placeholder">
								<uni-icons type="food" size="40" color="#ccc"></uni-icons>
								<text>暂无图片</text>
							</view>
							<view v-if="dish.isCol" class="hot-tag">收藏</view>
							<view v-if="dish.isNew" class="new-tag">新品</view>
						</view>
						<view class="dish-info">
							<view class="dish-header">
								<text class="dish-name">{{ dish.fName }}</text>
								<view class="dish-price-container">
									<view class="dish-price">
										<text v-if="false" class="price">¥{{ dish.price || 0.1 }}</text>
										<text v-if="dish.originalPrice"
											class="original-price">¥{{ dish.originalPrice || 0.2 }}</text>
									</view>
								</view>
							</view>
							<text class="dish-desc">{{ dish.fIntro | limitText(30) }}</text>

							<view class="dish-nutrition">
								<view class="nutrition-tag">
									<uni-icons type="fire" size="8" color="#FF5722"></uni-icons>
									<text>{{ dish.calories || 0 }} kcal</text>
								</view>
								<view class="nutrition-tag">
									<uni-icons type="muscle" size="8" color="#4CAF50"></uni-icons>
									<text>{{ dish.protein || 0 }}g 蛋白</text>
								</view>
								<view class="nutrition-tag">
									<uni-icons type="leaf" size="8" color="#FF9800"></uni-icons>
									<text>{{ dish.carbs || 0 }}g 碳水</text>
								</view>
								<view class="nutrition-tag">
									<uni-icons type="fat" size="8" color="#FFC107"></uni-icons>
									<text>{{ dish.fat || 0 }}g 脂肪</text>
								</view>
							</view>

							<view class="dish-actions">
								<view class="quantity-control">
									<button class="quantity-btn minus" @click.stop="decreaseQuantity(dish)"
										:disabled="getCartQuantity(dish.fID) <= 0">-</button>
									<text class="quantity">{{ getCartQuantity(dish.fID) }}</text>
									<button class="quantity-btn plus" @click.stop="addToCart(dish)"
										:disabled="getCartQuantity(dish.fID) >= 1">+</button>
								</view>
								<button v-if="getCartQuantity(dish.fID) === 0" class="add-btn"
									@click.stop="addToCart(dish)">
									加入
								</button>
								<button v-else class="del-btn" @click.stop="decreaseQuantity(dish)">
									移除
								</button>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
		</view>

		<!-- 固定在底部的购物车栏 -->
		<view class="fixed-cart-footer">
			<view class="cart-footer">
				<view class="cart-info" @click="gotoCart">
					<uni-icons type="cart" size="28" color="#333"></uni-icons>
					<view class="cart-count" v-if=" cartCount > 0 ">{{ cartCount }}</view>
				</view>
				<view class="total-info">
					<!-- <text class="total-label">合计</text>
					<text class="total-price">¥{{ totalPrice }}</text> -->
					<text class="total-calories">能量：{{ totalCalories }} kcal 碳水：{{ totalCarbonWater }}</text>
					<text class="total-calories">蛋白：{{ totalProtein }} 脂肪：{{ totalFat }}</text>
				</view>
				<button class="checkout-btn" @click="gotoCheckout">去通知ta</button>
			</view>
		</view>

		<!-- 底部安全区域占位 -->
		<view class="bottom-safe-area"></view>
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
	import {
		formatTime,
		parseTime,
		getTime
	} from '@/common/formatDate.js';
	export default {
		data() {
			return {
				// 日期相关
				todayDate: '',
				dateList: [],
				activeDate: '',

				// 餐次
				meals: [{
						value: '早餐',
						label: '早餐',
						icon: 'sunrise'
					},
					{
						value: '午餐',
						label: '午餐',
						icon: 'sun'
					},
					{
						value: '晚餐',
						label: '晚餐',
						icon: 'moon'
					},
					{
						value: '加餐',
						label: '加餐',
						icon: 'cafe'
					}
				],
				activeMeal: '早餐',

				// 分类
				categories: [],
				activeCategory: 'all',

				// 菜品数据
				dishes: [],
				scrollHeight: 500,

				// 购物车
				cartItems: [],

				orderData: {
					"id": "",
					"energy": "",
					"fat": "",
					"carbonWater": "",
					"protein": "",
					"createDate": "",
					"createID": "",
					"note": "",
					"op": "",
					"planInfoEntities": []
				},

				// 营养目标
				targetCalories: 2000,

				// 固定头部的高度
				fixedHeaderHeight: 0,

				currentUserInfo: uni.getStorageSync(this.$constants.USER_INFO), //当前登录用户
			};
		},
		computed: {
			// 购物车数量
			cartCount() {

				return this.cartItems.reduce((total, item) => total + 1, 0);
			},

			// 总价格
			totalPrice() {
				return this.cartItems.reduce((total, item) => {
					return parseFloat(total + (item.price * item.quantity)).toFixed(2);
				}, 0);
			},

			//总能量
			totalCalories() {
				const sum = this.cartItems.reduce((total, item) => {
					const energy = Number(item.energy) || 0;
					return total + energy;
				}, 0);

				return parseFloat(sum).toFixed(2);
			},

			//总蛋白
			totalProtein() {
				const sum = this.cartItems.reduce((total, item) => {
					const energy = Number(item.protein) || 0;
					return total + energy;
				}, 0);

				return parseFloat(sum).toFixed(2);
			},

			//总碳水
			totalCarbonWater() {
				const sum = this.cartItems.reduce((total, item) => {
					const energy = Number(item.carbonWater) || 0;
					return total + energy;
				}, 0);

				return parseFloat(sum).toFixed(2);
			},

			//总脂肪
			totalFat() {
				const sum = this.cartItems.reduce((total, item) => {
					const energy = Number(item.fat) || 0;
					return total + energy;
				}, 0);

				return parseFloat(sum).toFixed(2);
			},

			// 筛选后的菜品
			filteredDishes() {
				let filtered = this.dishes;
				// 按分类筛选
				if (this.activeCategory !== 'all') {
					filtered = filtered.filter(dish => dish.fType === this.activeCategory);
				}
				return filtered;
			}
		},
		filters: {
			limitText: function(value, length = 50, suffix = '...') {
				if (!value) return '';
				if (value.length <= length) return value;
				return value.substring(0, length) + suffix;
			},
		},
		onShow() {
			this.loadCartData(); //加载购物车
		},
		onLoad() {
			this.initScrollHeight(); //计算高度
			this.initDateList(); //加载日期
			this.loadRecipes(); //加载菜品
		},
		methods: {
			// 初始化日期列表
			initDateList() {
				const today = new Date();
				this.todayDate = today.getMonth() + 1 + '月' + today.getDate() + '日';
				this.activeDate = this.formatDate(today);

				const dates = [];
				for (let i = -2; i <= 4; i++) {
					const date = new Date();
					date.setDate(today.getDate() + i);

					dates.push({
						date: this.formatDate(date),
						day: date.getDate(),
						month: date.getMonth() + 1,
						isToday: i === 0
					});
				}
				this.dateList = dates;
			},

			// 格式化日期
			formatDate(date) {
				return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`;
			},

			// 选择日期
			selectDate(date) {
				if (this.activeDate === date) return
				this.activeDate = date;
				// 加载该日期的菜品
				this.loadDishesByDate(date);
			},

			// 选择餐次
			selectMeal(meal) {
				this.activeMeal = meal;
			},

			// 选择分类
			selectCategory(categoryId) {
				this.activeCategory = categoryId;
			},

			// 初始化滚动高度
			initScrollHeight() {
				const systemInfo = uni.getSystemInfoSync();
				const windowHeight = systemInfo.windowHeight;
				// 计算可用高度：窗口高度 - 固定头部高度 - 底部购物车高度（如果有）- 底部安全区域
				const fixedHeaderHeight = 375; // 固定头部的大致高度

				let availableHeight = windowHeight - fixedHeaderHeight;

				this.scrollHeight = availableHeight;
			},

			// 加载食谱列表
			async loadRecipes() {
				try {
					this.loading = true;
					const res = await recipeApi._getNutritionalRecipeAll();
					if (res) {
						this.dishes = res;
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
				this.dishes.forEach(item => {
					const index = this.categories.findIndex(obj => obj.name === item.fType);
					if (index !== -1) {
						this.categories[index].count++;
					} else {
						this.categories.push({
							id: item.fID,
							name: item.fType,
							count: 1
						});
					}
				});
			},

			// 获取购物车中菜品数量
			getCartQuantity(fID) {
				const item = this.cartItems.find(item => Number(item.note) === Number(fID));
				return item ? 1 : 0;
			},

			// 移除购物车
			decreaseQuantity(dish) {
				const index = this.cartItems.findIndex(item => Number(item.note) === Number(dish.fID));
				if (index !== -1) {
					this.cartItems.splice(index, 1);
					this.totalSum;
					this.saveCartData();
					// 重新计算滚动高度
					setTimeout(() => {
						this.initScrollHeight();
					}, 50);
				}
			},

			// 添加到购物车
			addToCart(dish) {
				const existingItem = this.cartItems.find(item => Number(item.note) === dish.fID);
				if (existingItem) {
					existingItem.quantity++;
					existingItem.createTime = this.activeDate;
				} else {
					// 找到目标time的最后一次出现位置
					let lastIndex = -1;
					for (let i = this.cartItems.length - 1; i >= 0; i--) {
						if (this.cartItems[i].time === this.activeMeal && this.cartItems[i].foodName === dish.fName) {
							lastIndex = -2;
							break;
						}
						if (this.cartItems[i].time === this.activeMeal) {
							lastIndex = i;
							break;
						}
					}

					let row = {
						id: getTime("start"),
						note: dish.fID,
						time: this.activeMeal,
						foodName: dish.fName,
						price: 0.1,
						energy: dish.calories,
						protein: dish.protein,
						carbonWater: dish.carbs,
						fat: dish.fat,
						planID: this.orderData.id,
						image: dish.fImg,
						createTime: this.activeDate,
						quantity: 1
					}

					// 如果找到目标time，在其后插入；否则添加到数组末尾
					if (lastIndex === -2) {

					} else if (lastIndex === -1) {
						this.cartItems.push(row);
					} else {
						this.cartItems.splice(lastIndex + 1, 0, row);
					}
				}
				this.totalSum;
				this.saveCartData();
				// 重新计算滚动高度
				setTimeout(() => {
					this.initScrollHeight();
				}, 50);
			},

			// 保存购物车数据
			saveCartData() {
				this.orderData = {
					id: this.orderData.id,
					energy: this.totalCalories,
					fat: this.totalFat,
					carbonWater: this.totalCarbonWater,
					protein: this.totalProtein,
					createDate: this.activeDate,
					createID: this.currentUserInfo.id,
					op: this.orderData.op,
					note: '',
					planInfoEntities: this.cartItems
				}
				uni.setStorageSync('cartData', this.orderData);
			},

			// 加载购物车数据
			loadCartData() {
				const cartData = uni.getStorageSync('cartData');
				if (cartData) {
					this.cartItems = cartData.planInfoEntities || [];
					this.orderData.id = cartData.id || '';
				} else {
					this.loadDishesByDate(this.activeDate);
				}
			},

			// 跳转到购物车
			gotoCart() {
				uni.navigateTo({
					url: '/pages/nutrition/cart/cart'
				});
			},

			// 跳转到历史记录
			gotoHistory() {
				uni.navigateTo({
					url: '/pages/nutrition/order-history/order-history'
				});
			},

			// 跳转到结算 || 下单，保存
			gotoCheckout() {
				uni.navigateTo({
					url: '/pages/nutrition/checkout/checkout'
				});

			},

			// 按日期加载菜品
			async loadDishesByDate(date) {
				const res = await dietPlanApi._getDietPlanByUid(this.currentUserInfo.id, date);
				if (res) {
					this.orderData = res;
					this.orderData.op = 'upd';
					this.orderData.id = res.id + '';
					this.cartItems = res.planInfoEntities;
				} else {
					this.orderData.id = getTime("start");
					this.orderData.op = 'add';
					this.cartItems = [];
				}

				this.saveCartData()
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

	.order-page {
		background: #f5f7fa;
		min-height: 100vh;
		box-sizing: border-box;
		/* 防止页面抖动 */
		backface-visibility: hidden;
		transform: translateZ(0);
		-webkit-transform: translateZ(0);
		overflow-x: hidden;
		position: relative;
		display: flex;
		flex-direction: column;

		/* 固定在顶部的头部区域 */
		.fixed-header {
			position: fixed;
			top: 0;
			left: 0;
			right: 0;
			z-index: 1000;
			background: #fff;
			box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
		}

		/* 内容区域 */
		.content-area {
			flex: 1;
			overflow: hidden;
			margin-top: 320rpx;
			/* 固定头部的高度 */
			margin-bottom: 120rpx;
			/* 为底部购物车预留空间 */
		}

		/* 固定在底部的购物车区域 */
		.fixed-cart-footer {
			position: fixed;
			bottom: 0;
			left: 0;
			right: 0;
			z-index: 1000;
			background: #fff;
			box-shadow: 0 -4rpx 12rpx rgba(0, 0, 0, 0.08);
		}

		/* 底部安全区域占位 */
		.bottom-safe-area {
			height: 20rpx;
			width: 100%;
			background: transparent;
		}

		.header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 40rpx 24rpx 20rpx;
			background: #fff;
			position: relative;
			z-index: 10;

			.header-left {
				.title {
					font-size: 36rpx;
					font-weight: 600;
					color: #333;
					display: block;
				}

				.subtitle {
					font-size: 24rpx;
					color: #999;
					margin-top: 4rpx;
					display: block;
				}
			}

			.header-right {
				display: flex;
				gap: 24rpx;

				.cart-btn {
					position: relative;

					.cart-badge {
						position: absolute;
						top: -8rpx;
						right: -8rpx;
						background: #ff3b30;
						color: #fff;
						font-size: 20rpx;
						width: 36rpx;
						height: 36rpx;
						border-radius: 50%;
						display: flex;
						align-items: center;
						justify-content: center;
					}
				}
			}
		}

		.date-selector {
			background: #fff;
			padding: 20rpx 0;
			position: relative;
			z-index: 9;

			.date-scroll {
				white-space: nowrap;
				/* 防止滚动时抖动 */
				-webkit-overflow-scrolling: touch;
				overflow-scrolling: touch;

				.date-list {
					display: inline-flex;
					padding: 0 24rpx;
					gap: 20rpx;

					.date-item {
						padding: 16rpx 24rpx;
						border-radius: 40rpx;
						background: #f5f7fa;
						display: flex;
						flex-direction: column;
						align-items: center;
						min-width: 120rpx;
						flex-shrink: 0;
						/* 防止点击时缩放 */
						transform: scale(1);
						transition: transform 0.1s;

						&:active {
							transform: scale(0.98);
						}

						&.active {
							background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
							color: #fff;
						}

						&.today {
							border: 2rpx solid $primary-color;
						}

						.day-text {
							font-size: 32rpx;
							font-weight: 600;
						}

						.date-text {
							font-size: 22rpx;
							margin-top: 4rpx;
						}

						.today-tag {
							font-size: 20rpx;
							background: $accent-color;
							color: #fff;
							padding: 4rpx 12rpx;
							border-radius: 20rpx;
							margin-top: 8rpx;
						}
					}
				}
			}
		}

		.meal-selector {
			background: #fff;
			padding: 20rpx 24rpx;
			position: relative;
			z-index: 8;

			.meal-tabs {
				display: flex;
				justify-content: space-around;

				.meal-tab {
					padding: 16rpx 32rpx;
					border-radius: 40rpx;
					background: #f5f7fa;
					display: flex;
					align-items: center;
					gap: 8rpx;
					font-size: 26rpx;
					color: #666;
					flex: 1;
					justify-content: center;
					margin: 0 10rpx;
					/* 防止点击时缩放 */
					transform: scale(1);
					transition: transform 0.1s;

					&:active {
						transform: scale(0.98);
					}

					&.active {
						background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
						color: #fff;
					}
				}
			}
		}

		.menu-category {
			background: #fff;
			border-radius: 24rpx 24rpx 0 0;
			padding: 20rpx 20rpx 0 0;
			position: relative;
			z-index: 7;

			/* 添加上边距，避免与固定头部重叠 */
			margin-top: -10rpx;

			.category-tabs {
				display: flex;
				align-items: center;
				padding: 120rpx 20rpx 5rpx;
				position: relative;
				background: #fff;

				/* 固定的"全部"按钮 */
				.category-fixed-tab {
					padding: 16rpx 32rpx;
					background: #f5f7fa;
					border-radius: 40rpx;
					font-size: 26rpx;
					color: #666;
					display: flex;
					align-items: center;
					gap: 8rpx;
					margin-right: 16rpx;
					flex-shrink: 0;
					transform: scale(1);
					transition: transform 0.1s;

					&:active {
						transform: scale(0.98);
					}

					&.active {
						background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
						color: #fff;

						.count-badge {
							background: rgba(255, 255, 255, 0.5);
						}
					}

					.count-badge {
						font-size: 20rpx;
						background: rgba(0, 0, 0, 0.1);
						padding: 2rpx 8rpx;
						border-radius: 20rpx;
					}
				}

				.category-scroll {
					white-space: nowrap;
					flex: 1;
					-webkit-overflow-scrolling: touch;
					overflow-scrolling: touch;

					.category-list {
						display: inline-flex;
						gap: 16rpx;
						margin-right: 160rpx;

						.category-tab {
							padding: 16rpx 32rpx;
							background: #f5f7fa;
							border-radius: 40rpx;
							font-size: 26rpx;
							color: #666;
							display: flex;
							align-items: center;
							gap: 8rpx;
							flex-shrink: 0;
							transform: scale(1);
							transition: transform 0.1s;

							&:active {
								transform: scale(0.98);
							}

							&.active {
								background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
								color: #fff;

								.count-badge {
									background: rgba(255, 255, 255, 0.5);
								}
							}

							.count-badge {
								font-size: 20rpx;
								background: rgba(0, 0, 0, 0.1);
								padding: 2rpx 8rpx;
								border-radius: 20rpx;
							}
						}
					}
				}
			}

			.dish-list {
				padding: 24rpx;
				-webkit-overflow-scrolling: touch;
				overflow-scrolling: touch;

				.dish-card {
					display: flex;
					padding: 24rpx;
					border-bottom: 2rpx solid #f0f0f0;
					position: relative;

					&:last-child {
						border-bottom: none;
					}

					.dish-image {
						position: relative;
						width: 180rpx;
						height: 180rpx;
						flex-shrink: 0;

						.dish-img {
							width: 100%;
							height: 100%;
							border-radius: 16rpx;
							display: block;
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

						.hot-tag,
						.new-tag {
							position: absolute;
							top: 8rpx;
							left: 8rpx;
							padding: 4rpx 12rpx;
							font-size: 20rpx;
							color: #fff;
							border-radius: 12rpx;
						}

						.hot-tag {
							background: #ff3b30;
						}

						.new-tag {
							background: #52c41a;
						}
					}

					.dish-info {
						flex: 1;
						margin-left: 20rpx;
						min-width: 0;
						/* 重要：防止内容溢出 */

						.dish-header {
							display: flex;
							justify-content: space-between;
							align-items: flex-start;
							margin-bottom: 8rpx;

							.dish-name {
								font-size: 30rpx;
								font-weight: 600;
								color: #333;
								flex: 1;
								margin-right: 16rpx;
								/* 防止文字过长溢出 */
								overflow: hidden;
								text-overflow: ellipsis;
								white-space: nowrap;
								max-width: 60%;
							}

							/* 修复价格显示被遮挡的问题 */
							.dish-price-container {
								flex-shrink: 0;
								min-width: 150rpx;
								text-align: right;

								.dish-price {
									display: flex;
									flex-direction: column;
									align-items: flex-end;

									.price {
										font-size: 28rpx;
										font-weight: 600;
										color: #ff3b30;
										display: block;
										line-height: 1.2;
									}

									.original-price {
										font-size: 22rpx;
										color: #999;
										text-decoration: line-through;
										display: block;
										line-height: 1.2;
										margin-top: 4rpx;
									}
								}
							}
						}

						.dish-desc {
							font-size: 24rpx;
							color: #666;
							line-height: 1.4;
							margin-bottom: 12rpx;
							display: block;
							/* 防止描述过长 */
							overflow: hidden;
							text-overflow: ellipsis;
							display: -webkit-box;
							-webkit-line-clamp: 2;
							-webkit-box-orient: vertical;
						}

						.dish-nutrition {
							display: flex;
							flex-wrap: wrap;
							gap: 8rpx;
							margin-bottom: 16rpx;

							.nutrition-tag {
								padding: 4rpx 12rpx;
								background: #f5f7fa;
								border-radius: 16rpx;
								font-size: 16rpx;
								color: #666;
								display: flex;
								align-items: center;
								gap: 4rpx;
								flex-shrink: 0;
							}
						}

						.dish-actions {
							display: flex;
							justify-content: space-between;
							align-items: center;

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
									padding: 0;
									margin: 0;
									line-height: 1;

									&::after {
										border: none;
									}

									&:disabled {
										opacity: 0.5;
									}

									&:active {
										transform: scale(0.95);
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

							.add-btn {
								background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
								color: #fff;
								padding: 12rpx 24rpx;
								border-radius: 40rpx;
								font-size: 24rpx;
								margin: 0;
								line-height: 1.5;

								&::after {
									border: none;
								}

								&:active {
									transform: scale(0.98);
								}
							}

							.del-btn {
								background: linear-gradient(135deg, $primary-color 0%, $error-color 100%);
								color: #fff;
								padding: 12rpx 24rpx;
								border-radius: 40rpx;
								font-size: 24rpx;
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
			}
		}

		.cart-footer {
			background: #fff;
			display: flex;
			align-items: center;
			padding: 20rpx 24rpx;
			/* 防止点击时抖动 */
			backface-visibility: hidden;

			.cart-info {
				position: relative;
				margin-right: 20rpx;

				.cart-count {
					position: absolute;
					top: -8rpx;
					right: -8rpx;
					background: #ff3b30;
					color: #fff;
					font-size: 20rpx;
					width: 36rpx;
					height: 36rpx;
					border-radius: 50%;
					display: flex;
					align-items: center;
					justify-content: center;
				}
			}

			.total-info {
				flex: 1;

				.total-label {
					font-size: 24rpx;
					color: #666;
					display: block;
				}

				.total-price {
					font-size: 32rpx;
					font-weight: 600;
					color: #ff3b30;
					display: block;
				}

				.total-calories {
					font-size: 20rpx;
					color: #999;
					display: block;
				}
			}

			.checkout-btn {
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
</style>