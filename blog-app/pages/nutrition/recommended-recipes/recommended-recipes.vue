<template>
	<view class="order-page">
		<!-- 头部 -->
		<view class="header">
			<view class="header-left">
				<text class="title">今日点餐</text>
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
			<scroll-view class="date-scroll" scroll-x>
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

		<!-- 点餐推荐 -->
		<view class="recommend-section">
			<view class="section-header">
				<text class="section-title">推荐搭配</text>
				<text class="section-desc">智能推荐适合您的营养餐</text>
			</view>
			<view class="recommend-list">
				<view class="recommend-card" v-for="combo in recommendCombos" :key="combo.id">
					<view class="combo-info">
						<text class="combo-name">{{ combo.name }}</text>
						<text class="combo-desc">{{ combo.desc }}</text>
						<view class="combo-tags">
							<text v-for="tag in combo.tags" :key="tag" class="combo-tag">{{ tag }}</text>
						</view>
						<view class="combo-nutrition">
							<text class="calories">{{ combo.calories }} kcal</text>
							<text class="price">¥{{ combo.price }}</text>
						</view>
					</view>
					<button class="add-combo-btn" @click="addComboToCart(combo)">一键加入</button>
				</view>
			</view>
		</view>

		<!-- 菜单分类 -->
		<view class="menu-category">
			<view class="category-tabs">
				<scroll-view scroll-x class="category-scroll">
					<view class="category-list">
						<view v-for="category in categories" :key="category.id" class="category-tab"
							:class="{ 'active': activeCategory === category.id }" @click="selectCategory(category.id)">
							<text>{{ category.name }}</text>
							<text v-if="category.count > 0" class="count-badge">{{ category.count }}</text>
						</view>
					</view>
				</scroll-view>
			</view>

			<!-- 菜品列表 -->
			<scroll-view class="dish-list" scroll-y :style="{height: scrollHeight + 'px'}">
				<view v-for="dish in filteredDishes" :key="dish.id" class="dish-card">
					<view class="dish-image">
						<image :src="dish.image" mode="aspectFill" class="dish-img"></image>
						<view v-if="dish.isHot" class="hot-tag">热销</view>
						<view v-if="dish.isNew" class="new-tag">新品</view>
					</view>
					<view class="dish-info">
						<view class="dish-header">
							<text class="dish-name">{{ dish.name }}</text>
							<view class="dish-price">
								<text class="price">¥{{ dish.price }}</text>
								<text v-if="dish.originalPrice" class="original-price">¥{{ dish.originalPrice }}</text>
							</view>
						</view>
						<text class="dish-desc">{{ dish.desc }}</text>

						<view class="dish-nutrition">
							<view class="nutrition-tag">
								<uni-icons type="fire" size="12" color="#FF5722"></uni-icons>
								<text>{{ dish.calories }} kcal</text>
							</view>
							<view class="nutrition-tag">
								<uni-icons type="muscle" size="12" color="#4CAF50"></uni-icons>
								<text>{{ dish.protein }}g 蛋白</text>
							</view>
						</view>

						<view class="dish-actions">
							<view class="quantity-control">
								<button class="quantity-btn minus" @click.stop="decreaseQuantity(dish)"
									:disabled="getCartQuantity(dish.id) <= 0">-</button>
								<text class="quantity">{{ getCartQuantity(dish.id) }}</text>
								<button class="quantity-btn plus" @click.stop="increaseQuantity(dish)">+</button>
							</view>
							<button v-if="getCartQuantity(dish.id) === 0" class="add-btn" @click.stop="addToCart(dish)">
								加入购物车
							</button>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>

		<!-- 购物车底部栏 -->
		<view v-if="cartCount > 0" class="cart-footer">
			<view class="cart-info" @click="gotoCart">
				<uni-icons type="cart" size="28" color="#fff"></uni-icons>
				<view class="cart-count">{{ cartCount }}</view>
			</view>
			<view class="total-info">
				<text class="total-label">合计</text>
				<text class="total-price">¥{{ totalPrice }}</text>
				<text class="total-calories">{{ totalCalories }} kcal</text>
			</view>
			<button class="checkout-btn" @click="gotoCheckout">去结算</button>
		</view>

		<!-- 今日营养统计 -->
		<view class="nutrition-summary">
			<view class="summary-header">
				<text class="summary-title">今日营养摄入</text>
				<text class="summary-target">目标: {{ targetCalories }} kcal</text>
			</view>
			<view class="nutrition-progress">
				<view class="progress-bar">
					<view class="progress-fill" :style="{width: calorieProgress + '%'}"></view>
				</view>
				<view class="progress-text">{{ currentCalories }} / {{ targetCalories }} kcal</view>
			</view>
			<view class="nutrition-details">
				<view class="detail-item">
					<text class="detail-label">蛋白质</text>
					<text class="detail-value">{{ currentProtein }}g</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">碳水</text>
					<text class="detail-value">{{ currentCarbs }}g</text>
				</view>
				<view class="detail-item">
					<text class="detail-label">脂肪</text>
					<text class="detail-value">{{ currentFat }}g</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				// 日期相关
				todayDate: '',
				dateList: [],
				activeDate: '',

				// 餐次
				meals: [{
						value: 'breakfast',
						label: '早餐',
						icon: 'sunrise'
					},
					{
						value: 'lunch',
						label: '午餐',
						icon: 'sun'
					},
					{
						value: 'dinner',
						label: '晚餐',
						icon: 'moon'
					},
					{
						value: 'snack',
						label: '加餐',
						icon: 'cafe'
					}
				],
				activeMeal: 'lunch',

				// 分类
				categories: [{
						id: 'all',
						name: '全部',
						count: 24
					},
					{
						id: 'main',
						name: '主食',
						count: 8
					},
					{
						id: 'meat',
						name: '肉类',
						count: 6
					},
					{
						id: 'vegetable',
						name: '蔬菜',
						count: 10
					},
					{
						id: 'soup',
						name: '汤羹',
						count: 4
					},
					{
						id: 'drink',
						name: '饮品',
						count: 6
					}
				],
				activeCategory: 'all',

				// 菜品数据
				dishes: [],
				scrollHeight: 500,

				// 购物车
				cartItems: [],

				// 营养目标
				targetCalories: 2000,
				currentCalories: 0,
				currentProtein: 0,
				currentCarbs: 0,
				currentFat: 0,

				// 推荐套餐
				recommendCombos: [{
						id: 1,
						name: '健身增肌餐',
						desc: '高蛋白低脂肪，适合健身人群',
						tags: ['高蛋白', '低脂肪', '增肌'],
						calories: 650,
						price: 38,
						dishes: [101, 102, 103]
					},
					{
						id: 2,
						name: '轻体减脂餐',
						desc: '低卡健康，助力体重管理',
						tags: ['低卡', '高纤维', '减脂'],
						calories: 480,
						price: 32,
						dishes: [104, 105, 106]
					}
				]
			};
		},
		computed: {
			// 购物车数量
			cartCount() {
				return this.cartItems.reduce((total, item) => total + item.quantity, 0);
			},

			// 总价格
			totalPrice() {
				return this.cartItems.reduce((total, item) => {
					return total + (item.price * item.quantity);
				}, 0);
			},

			// 总热量
			totalCalories() {
				return this.cartItems.reduce((total, item) => {
					return total + (item.calories * item.quantity);
				}, 0);
			},

			// 热量进度
			calorieProgress() {
				return Math.min((this.currentCalories / this.targetCalories) * 100, 100);
			},

			// 筛选后的菜品
			filteredDishes() {
				let filtered = this.dishes;

				// 按分类筛选
				if (this.activeCategory !== 'all') {
					filtered = filtered.filter(dish => dish.category === this.activeCategory);
				}

				// 按餐次推荐筛选
				if (this.activeMeal) {
					filtered = filtered.filter(dish => dish.availableMeals.includes(this.activeMeal));
				}

				return filtered;
			}
		},
		onLoad() {
			this.initDateList();
			this.loadDishes();
			this.initScrollHeight();
			this.loadCartData();
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
				this.scrollHeight = systemInfo.windowHeight - 400;
			},

			// 加载菜品数据
			async loadDishes() {
				// 模拟数据
				this.dishes = [{
						id: 101,
						name: '黑椒牛排配时蔬',
						desc: '澳洲进口牛排，搭配新鲜时蔬',
						price: 48,
						originalPrice: 58,
						calories: 420,
						protein: 32,
						carbs: 18,
						fat: 22,
						image: '/static/dishes/steak.jpg',
						category: 'meat',
						availableMeals: ['lunch', 'dinner'],
						isHot: true,
						isNew: false
					},
					{
						id: 102,
						name: '香煎鸡胸肉沙拉',
						desc: '低脂鸡胸肉，多种蔬菜搭配',
						price: 28,
						calories: 280,
						protein: 25,
						carbs: 15,
						fat: 8,
						image: '/static/dishes/salad.jpg',
						category: 'meat',
						availableMeals: ['lunch', 'dinner'],
						isHot: true,
						isNew: false
					},
					// 更多菜品...
				];
			},

			// 获取购物车中菜品数量
			getCartQuantity(dishId) {
				const item = this.cartItems.find(item => item.dishId === dishId);
				return item ? item.quantity : 0;
			},

			// 增加数量
			increaseQuantity(dish) {
				this.addToCart(dish);
			},

			// 减少数量
			decreaseQuantity(dish) {
				const index = this.cartItems.findIndex(item => item.dishId === dish.id);
				if (index !== -1) {
					if (this.cartItems[index].quantity > 1) {
						this.cartItems[index].quantity--;
					} else {
						this.cartItems.splice(index, 1);
					}
					this.updateNutrition();
					this.saveCartData();
				}
			},

			// 添加到购物车
			addToCart(dish) {
				const existingItem = this.cartItems.find(item => item.dishId === dish.id);

				if (existingItem) {
					existingItem.quantity++;
				} else {
					this.cartItems.push({
						dishId: dish.id,
						name: dish.name,
						price: dish.price,
						calories: dish.calories,
						protein: dish.protein,
						carbs: dish.carbs,
						fat: dish.fat,
						image: dish.image,
						quantity: 1
					});
				}

				this.updateNutrition();
				this.saveCartData();

				uni.showToast({
					title: '已添加到购物车',
					icon: 'success'
				});
			},

			// 添加套餐到购物车
			addComboToCart(combo) {
				// 添加套餐中的所有菜品
				combo.dishes.forEach(dishId => {
					const dish = this.dishes.find(d => d.id === dishId);
					if (dish) {
						this.addToCart(dish);
					}
				});

				uni.showToast({
					title: '套餐已加入购物车',
					icon: 'success'
				});
			},

			// 更新营养数据
			updateNutrition() {
				this.currentCalories = this.totalCalories;
				this.currentProtein = this.cartItems.reduce((total, item) =>
					total + (item.protein * item.quantity), 0);
				this.currentCarbs = this.cartItems.reduce((total, item) =>
					total + (item.carbs * item.quantity), 0);
				this.currentFat = this.cartItems.reduce((total, item) =>
					total + (item.fat * item.quantity), 0);
			},

			// 保存购物车数据
			saveCartData() {
				uni.setStorageSync('cartData', {
					items: this.cartItems,
					date: this.activeDate,
					meal: this.activeMeal
				});
			},

			// 加载购物车数据
			loadCartData() {
				const cartData = uni.getStorageSync('cartData');
				if (cartData && cartData.date === this.activeDate) {
					this.cartItems = cartData.items || [];
					this.updateNutrition();
				}
			},

			// 跳转到购物车
			gotoCart() {
				uni.navigateTo({
					url: '/pages/order/cart/cart'
				});
			},

			// 跳转到历史记录
			gotoHistory() {
				uni.navigateTo({
					url: '/pages/order/history/history'
				});
			},

			// 跳转到结算
			gotoCheckout() {
				uni.navigateTo({
					url: '/pages/order/checkout/checkout'
				});
			},

			// 按日期加载菜品
			async loadDishesByDate(date) {
				// 这里可以调用API获取指定日期的菜品
				console.log('加载日期:', date, '的菜品');
			}
		}
	};
</script>

<style lang="scss">
	.order-page {
		background: #f5f7fa;
		padding-bottom: 120rpx;

		.header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 40rpx 24rpx 20rpx;
			background: #fff;

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

			.date-scroll {
				white-space: nowrap;

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

						&.active {
							background: linear-gradient(135deg, #1890ff, #52c41a);
							color: #fff;
						}

						&.today {
							border: 2rpx solid #1890ff;
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
							background: #1890ff;
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

					&.active {
						background: linear-gradient(135deg, #1890ff, #52c41a);
						color: #fff;
					}
				}
			}
		}

		.recommend-section {
			background: #fff;
			margin: 20rpx 0;
			padding: 24rpx;

			.section-header {
				margin-bottom: 20rpx;

				.section-title {
					font-size: 30rpx;
					font-weight: 600;
					color: #333;
					display: block;
				}

				.section-desc {
					font-size: 24rpx;
					color: #999;
					margin-top: 4rpx;
					display: block;
				}
			}

			.recommend-list {
				.recommend-card {
					background: linear-gradient(135deg, #667eea, #764ba2);
					border-radius: 16rpx;
					padding: 24rpx;
					margin-bottom: 16rpx;
					display: flex;
					justify-content: space-between;
					align-items: center;
					color: #fff;

					.combo-info {
						flex: 1;

						.combo-name {
							font-size: 30rpx;
							font-weight: 600;
							display: block;
							margin-bottom: 8rpx;
						}

						.combo-desc {
							font-size: 24rpx;
							opacity: 0.9;
							display: block;
							margin-bottom: 12rpx;
						}

						.combo-tags {
							display: flex;
							gap: 8rpx;
							margin-bottom: 12rpx;

							.combo-tag {
								font-size: 20rpx;
								padding: 4rpx 12rpx;
								background: rgba(255, 255, 255, 0.2);
								border-radius: 20rpx;
							}
						}

						.combo-nutrition {
							display: flex;
							align-items: center;
							gap: 16rpx;

							.calories {
								font-size: 22rpx;
							}

							.price {
								font-size: 28rpx;
								font-weight: 600;
							}
						}
					}

					.add-combo-btn {
						background: rgba(255, 255, 255, 0.2);
						color: #fff;
						padding: 16rpx 24rpx;
						border-radius: 40rpx;
						font-size: 24rpx;
						border: 2rpx solid rgba(255, 255, 255, 0.3);

						&::after {
							border: none;
						}
					}
				}
			}
		}

		.menu-category {
			background: #fff;
			border-radius: 24rpx 24rpx 0 0;
			padding: 20rpx 0 0;

			.category-tabs {
				padding: 0 24rpx 20rpx;

				.category-scroll {
					white-space: nowrap;

					.category-list {
						display: inline-flex;
						gap: 16rpx;

						.category-tab {
							padding: 16rpx 32rpx;
							background: #f5f7fa;
							border-radius: 40rpx;
							font-size: 26rpx;
							color: #666;
							display: flex;
							align-items: center;
							gap: 8rpx;

							&.active {
								background: linear-gradient(135deg, #1890ff, #52c41a);
								color: #fff;
							}

							.count-badge {
								font-size: 20rpx;
								background: rgba(255, 255, 255, 0.3);
								padding: 2rpx 8rpx;
								border-radius: 20rpx;
							}
						}
					}
				}
			}

			.dish-list {
				padding: 0 24rpx;

				.dish-card {
					display: flex;
					padding: 24rpx 0;
					border-bottom: 2rpx solid #f0f0f0;

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
							}

							.dish-price {
								text-align: right;

								.price {
									font-size: 28rpx;
									font-weight: 600;
									color: #ff3b30;
									display: block;
								}

								.original-price {
									font-size: 22rpx;
									color: #999;
									text-decoration: line-through;
									display: block;
								}
							}
						}

						.dish-desc {
							font-size: 24rpx;
							color: #666;
							line-height: 1.4;
							margin-bottom: 12rpx;
							display: block;
						}

						.dish-nutrition {
							display: flex;
							gap: 12rpx;
							margin-bottom: 16rpx;

							.nutrition-tag {
								padding: 4rpx 12rpx;
								background: #f5f7fa;
								border-radius: 16rpx;
								font-size: 20rpx;
								color: #666;
								display: flex;
								align-items: center;
								gap: 4rpx;
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

									&::after {
										border: none;
									}

									&:disabled {
										opacity: 0.5;
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
								background: linear-gradient(135deg, #1890ff, #52c41a);
								color: #fff;
								padding: 12rpx 24rpx;
								border-radius: 40rpx;
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

		.cart-footer {
			position: fixed;
			bottom: 0;
			left: 0;
			right: 0;
			background: #fff;
			display: flex;
			align-items: center;
			padding: 20rpx 24rpx;
			box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.1);
			z-index: 100;

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
				background: linear-gradient(135deg, #1890ff, #52c41a);
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

		.nutrition-summary {
			background: #fff;
			margin: 20rpx 24rpx;
			padding: 24rpx;
			border-radius: 16rpx;
			box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);

			.summary-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-bottom: 16rpx;

				.summary-title {
					font-size: 28rpx;
					font-weight: 600;
					color: #333;
				}

				.summary-target {
					font-size: 24rpx;
					color: #666;
				}
			}

			.nutrition-progress {
				margin-bottom: 20rpx;

				.progress-bar {
					height: 16rpx;
					background: #f0f0f0;
					border-radius: 8rpx;
					overflow: hidden;
					margin-bottom: 8rpx;

					.progress-fill {
						height: 100%;
						background: linear-gradient(90deg, #52c41a, #1890ff);
						border-radius: 8rpx;
						transition: width 0.3s ease;
					}
				}

				.progress-text {
					text-align: center;
					font-size: 22rpx;
					color: #666;
				}
			}

			.nutrition-details {
				display: flex;
				justify-content: space-around;

				.detail-item {
					text-align: center;

					.detail-label {
						font-size: 22rpx;
						color: #999;
						display: block;
						margin-bottom: 4rpx;
					}

					.detail-value {
						font-size: 24rpx;
						font-weight: 600;
						color: #333;
						display: block;
					}
				}
			}
		}
	}
</style>