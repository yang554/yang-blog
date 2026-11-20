<template>
	<view class="recipe-detail-page">
		<!-- 封面图片 -->
		<view class="recipe-cover">
			<image :src="getFullImageUrl(recipe.fImg)" class="cover-image" mode="widthFix"></image>
			<view class="cover-overlay"></view>
			<view class="cover-content">
				<text class="recipe-name">{{ recipe.fName }}</text>
				<text class="recipe-type">{{ recipe.fType }}</text>
			</view>
		</view>

		<scroll-view class="detail-content" scroll-y :style="{'height': scrollHeight + 'px'}">
			<!-- 基本信息卡片 -->
			<view class="info-card">
				<view class="card-header">
					<uni-icons type="info" size="20" color="#1890ff"></uni-icons>
					<text class="card-title">基本信息</text>
				</view>
				<view class="card-body">
					<text class="recipe-intro">{{ recipe.fIntro }}</text>

					<view class="create-info">
						<view class="info-item">
							<uni-icons type="calendar" size="16" color="#999"></uni-icons>
							<text class="info-text">创建: {{ formatDate(recipe.fCreateTime) }}</text>
						</view>
						<view class="info-item" v-if="recipe.fUpdateTime">
							<uni-icons type="refresh" size="16" color="#999"></uni-icons>
							<text class="info-text">更新: {{ formatDate(recipe.fUpdateTime) }}</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 营养概览卡片 -->
			<view class="info-card">
				<view class="card-header">
					<uni-icons type="fire" size="20" color="#ff6b6b"></uni-icons>
					<text class="card-title">营养概览</text>
				</view>
				<view class="card-body">
					<view class="nutrition-circles">
						<view class="nutrition-circle">
							<view class="circle-content" style="background: linear-gradient(135deg, #ff6b6b, #ff8e8e);">
								<text class="circle-value">{{ recipe.calories || '0' }}</text>
								<text class="circle-label">热量</text>
								<text class="circle-unit">kcal</text>
							</view>
						</view>
						<view class="nutrition-circle">
							<view class="circle-content" style="background: linear-gradient(135deg, #4d96ff, #6bc5ff);">
								<text class="circle-value">{{ recipe.protein || '0' }}</text>
								<text class="circle-label">蛋白质</text>
								<text class="circle-unit">g</text>
							</view>
						</view>
						<view class="nutrition-circle">
							<view class="circle-content" style="background: linear-gradient(135deg, #6bcf7f, #8de39e);">
								<text class="circle-value">{{ recipe.carbs || '0' }}</text>
								<text class="circle-label">碳水</text>
								<text class="circle-unit">g</text>
							</view>
						</view>
						<view class="nutrition-circle">
							<view class="circle-content" style="background: linear-gradient(135deg, #ffd166, #ffe29c);">
								<text class="circle-value">{{ recipe.fat || '0' }}</text>
								<text class="circle-label">脂肪</text>
								<text class="circle-unit">g</text>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 食材清单卡片 -->
			<view class="info-card">
				<view class="card-header">
					<uni-icons type="list" size="20" color="#6bcf7f"></uni-icons>
					<text class="card-title">食材清单</text>
					<text class="card-subtitle">{{ (recipe.ingredients || []).length }} 种食材</text>
				</view>
				<view class="card-body">
					<view class="ingredients-grid">
						<view v-for="(item, index) in recipe.ingredients" :key="index" class="ingredient-card">
							<view class="ingredient-header">
								<text class="ingredient-name">{{ item.foodName }}</text>
								<text class="ingredient-amount">{{ item.weight }}g</text>
							</view>
							<view class="ingredient-nutrition">
								<view class="nutrition-tag" style="background: rgba(255, 107, 107, 0.1);">
									<text style="color: #ff6b6b;">🔥 {{ item.calories }}kcal</text>
								</view>
								<view class="nutrition-tag" style="background: rgba(77, 150, 255, 0.1);">
									<text style="color: #4d96ff;">💪 {{ item.protein }}g</text>
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 调味料卡片 -->
			<view class="info-card" v-if="recipe.fSeasoning">
				<view class="card-header">
					<uni-icons type="spoon" size="20" color="#ffa94d"></uni-icons>
					<text class="card-title">调味料</text>
				</view>
				<view class="card-body">
					<text class="content-text">{{ recipe.fSeasoning }}</text>
				</view>
			</view>

			<!-- 制作步骤卡片 -->
			<view class="info-card">
				<view class="card-header">
					<uni-icons type="gear" size="20" color="#9c88ff"></uni-icons>
					<text class="card-title">制作步骤</text>
				</view>
				<view class="card-body">
					<view class="steps-container">
						<text class="content-text">{{ recipe.fMade }}</text>
					</view>
				</view>
			</view>

			<!-- 小贴士卡片 -->
			<view class="info-card" v-if="recipe.fTips">
				<view class="card-header">
					<uni-icons type="lightbulb" size="20" color="#ffd166"></uni-icons>
					<text class="card-title">小贴士</text>
				</view>
				<view class="card-body">
					<view class="tips-container">
						<uni-icons type="quote" size="24" color="#ffd166" class="quote-icon"></uni-icons>
						<text class="tips-text">{{ recipe.fTips }}</text>
					</view>
				</view>
			</view>
		</scroll-view>

		<!-- 底部操作栏 -->
		<view class="action-bar">
			<button class="action-btn share" @click="shareRecipe">
				<text>分享</text>
			</button>
			<button class="action-btn favorite" @click="toggleFavorite">
				<text>{{ isFavorite ? '已收藏' : '收藏' }}</text>
			</button>
			<button class="action-btn cook" @click="startCooking">
				<text>开始烹饪</text>
			</button>
		</view>
	</view>
</template>

<script>
	import {
		recipeApi
	} from '@/api/index.js';
	import {
		IMG_URL
	} from '@/api/config.js';

	export default {
		data() {
			return {
				recipe: {
					fID: "",
					fImg: "",
					fType: "",
					fName: "",
					fIntro: "",
					fMade: "",
					fSeasoning: "",
					fBatching: "",
					ingredients: [],
					fCreateTime: "",
					fUpdateTime: "",
					fTips: "",
					calories: "0",
					protein: "0",
					carbs: "0",
					fat: "0"
				},
				isFavorite: false,
				scrollHeight: 500
			};
		},

		onLoad(options) {
			this.initScrollHeight();
			if (options && options.id) {
				this.loadRecipe(options.id);
				this.checkFavoriteStatus(options.id);
			}
		},

		methods: {
			initScrollHeight() {
				const systemInfo = uni.getSystemInfoSync();
				this.scrollHeight = systemInfo.windowHeight - 50;
			},

			async loadRecipe(id) {
				try {
					uni.showLoading({
						title: '加载中...'
					});

					const res = await recipeApi._getNutritionalRecipeById(id);
					if (res && res.data) {
						this.recipe = {
							...res.data
						};

						// 加载食材信息
						const ingredients = await recipeApi._getByRecipeId(this.recipe.fID);
						const ingredientsToCreate = ingredients.map(item => ({
							id: item.id,
							foodId: item.ingredient.fID,
							foodName: item.ingredient.fName,
							weight: item.weight,
							unit: 'g',
							calories: ((parseFloat(item.ingredient.fEnergy) / parseFloat(item.ingredient
								.fEdibleSection)) * item.weight).toFixed(1),
							carbs: ((parseFloat(item.ingredient.fCarbonWater) / parseFloat(item.ingredient
								.fEdibleSection)) * item.weight).toFixed(1),
							fat: ((parseFloat(item.ingredient.fFat) / parseFloat(item.ingredient
								.fEdibleSection)) * item.weight).toFixed(1),
							protein: ((parseFloat(item.ingredient.fProtein) / parseFloat(item.ingredient
								.fEdibleSection)) * item.weight).toFixed(1),
							edible: item.ingredient.fEdibleSection,
						}));

						this.recipe.ingredients = ingredientsToCreate;
					}
				} catch (error) {
					console.error('加载食谱失败:', error);
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			checkFavoriteStatus(recipeId) {
				// 从本地存储检查收藏状态
				const favorites = uni.getStorageSync('recipe_favorites') || [];
				this.isFavorite = favorites.includes(recipeId);
			},

			toggleFavorite() {
				const favorites = uni.getStorageSync('recipe_favorites') || [];

				if (this.isFavorite) {
					// 取消收藏
					const index = favorites.indexOf(this.recipe.fID);
					if (index > -1) {
						favorites.splice(index, 1);
					}
					uni.showToast({
						title: '已取消收藏',
						icon: 'success'
					});
				} else {
					// 添加收藏
					favorites.push(this.recipe.fID);
					uni.showToast({
						title: '收藏成功',
						icon: 'success'
					});
				}

				this.isFavorite = !this.isFavorite;
				uni.setStorageSync('recipe_favorites', favorites);
			},

			shareRecipe() {
				uni.share({
					provider: 'weixin',
					scene: 'WXSceneSession',
					type: 0,
					title: this.recipe.fName,
					summary: this.recipe.fIntro,
					imageUrl: this.getFullImageUrl(this.recipe.fImg),
					href: `/pages/recipe/detail?id=${this.recipe.fID}`,
					success: function(res) {
						console.log('share success', res);
					},
					fail: function(err) {
						console.log('share fail', err);
					}
				});
			},

			startCooking() {
				uni.showModal({
					title: '开始烹饪',
					content: '准备好开始制作这道美食了吗？',
					confirmText: '开始',
					success: (res) => {
						if (res.confirm) {
							// 这里可以跳转到烹饪模式页面
							uni.showToast({
								title: '开始烹饪模式',
								icon: 'success'
							});
						}
					}
				});
			},

			formatDate(dateString) {
				if (!dateString) return '未知时间';
				const date = new Date(dateString);
				return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
			},

			getFullImageUrl(path) {
				if (!path) return "/static/default-recipe.png";
				if (path.startsWith("http")) return path;
				return IMG_URL + (path.startsWith("/") ? path : "/" + path);
			}
		}
	};
</script>

<style lang="scss">
	@import "@/scss/variables.scss";

	.recipe-detail-page {
		min-height: 100vh;
		background: #f5f7fa;
		padding-bottom: 120rpx;

		.recipe-cover {
			position: relative;
			height: 400rpx;
			overflow: hidden;

			.cover-image {
				width: 100%;
				height: 100%;
			}

			.cover-overlay {
				position: absolute;
				top: 0;
				left: 0;
				right: 0;
				bottom: 0;
				background: linear-gradient(to bottom, rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0.6));
			}

			.cover-content {
				position: absolute;
				bottom: 40rpx;
				left: 32rpx;
				right: 32rpx;
				color: white;

				.recipe-name {
					font-size: 40rpx;
					font-weight: 700;
					display: block;
					margin-bottom: 12rpx;
					text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
				}

				.recipe-type {
					font-size: 28rpx;
					opacity: 0.9;
					display: block;
				}
			}
		}

		.detail-content {
			padding: 24rpx;
			box-sizing: border-box;

			.info-card {
				background: #fff;
				border-radius: 20rpx;
				padding: 32rpx;
				margin-bottom: 24rpx;
				box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
				box-sizing: border-box;

				.card-header {
					display: flex;
					align-items: center;
					margin-bottom: 24rpx;
					padding-bottom: 20rpx;
					border-bottom: 2rpx solid #f5f7fa;

					.card-title {
						font-size: 32rpx;
						font-weight: 600;
						color: #333;
						margin-left: 12rpx;
					}

					.card-subtitle {
						font-size: 24rpx;
						color: #666;
						margin-left: auto;
					}
				}

				.card-body {
					.recipe-intro {
						font-size: 28rpx;
						line-height: 1.6;
						color: #555;
						display: block;
						margin-bottom: 24rpx;
					}

					.create-info {
						display: flex;
						flex-direction: column;
						gap: 12rpx;

						.info-item {
							display: flex;
							align-items: center;
							gap: 8rpx;

							.info-text {
								font-size: 24rpx;
								color: #888;
							}
						}
					}

					.nutrition-circles {
						display: grid;
						grid-template-columns: repeat(2, 1fr);
						gap: 24rpx;

						.nutrition-circle {
							.circle-content {
								border-radius: 20rpx;
								padding: 32rpx 24rpx;
								text-align: center;
								box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);

								.circle-value {
									font-size: 40rpx;
									font-weight: 700;
									color: white;
									display: block;
									margin-bottom: 4rpx;
								}

								.circle-label {
									font-size: 24rpx;
									color: rgba(255, 255, 255, 0.9);
									display: block;
									margin-bottom: 4rpx;
								}

								.circle-unit {
									font-size: 20rpx;
									color: rgba(255, 255, 255, 0.8);
								}
							}
						}
					}

					.ingredients-grid {
						display: grid;
						grid-template-columns: repeat(2, 1fr);
						gap: 20rpx;

						.ingredient-card {
							background: #f8f9fa;
							border-radius: 16rpx;
							padding: 24rpx;
							box-sizing: border-box;

							.ingredient-header {
								display: flex;
								justify-content: space-between;
								align-items: flex-start;
								margin-bottom: 16rpx;

								.ingredient-name {
									font-size: 28rpx;
									font-weight: 500;
									color: #333;
									flex: 1;
									margin-right: 12rpx;
								}

								.ingredient-amount {
									font-size: 24rpx;
									font-weight: 600;
									color: #1890ff;
									flex-shrink: 0;
								}
							}

							.ingredient-nutrition {
								display: flex;
								gap: 8rpx;

								.nutrition-tag {
									padding: 6rpx 12rpx;
									border-radius: 20rpx;
									font-size: 20rpx;
								}
							}
						}
					}

					.content-text {
						font-size: 28rpx;
						line-height: 1.6;
						color: #555;
						display: block;
					}

					.steps-container {
						.content-text {
							white-space: pre-line;
						}
					}

					.tips-container {
						background: linear-gradient(135deg, rgba(255, 209, 102, 0.1), rgba(255, 209, 102, 0.05));
						border-radius: 16rpx;
						padding: 32rpx;
						position: relative;

						.quote-icon {
							position: absolute;
							top: 16rpx;
							left: 16rpx;
							opacity: 0.3;
						}

						.tips-text {
							font-size: 26rpx;
							line-height: 1.5;
							color: #666;
							font-style: italic;
							padding-left: 32rpx;
						}
					}
				}
			}
		}

		.action-bar {
			position: fixed;
			bottom: 0;
			left: 0;
			right: 0;
			background: #fff;
			padding: 10rpx;
			display: flex;
			gap: 8rpx;
			border-top: 2rpx solid #f0f0f0;
			box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);
			z-index: 100;

			.action-btn {
				flex: 1;
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
				gap: 8rpx;
				padding: 10rpx;
				border-radius: 16rpx;
				border: none;
				font-size: 24rpx;
				font-weight: 500;

				&.share {
					background: linear-gradient(135deg, #9c88ff, #8c7ae6);
					color: white;
				}

				&.favorite {
					background: linear-gradient(135deg, #ff6b6b, #ee5a52);
					color: white;
				}

				&.cook {
					background: linear-gradient(135deg, #4d96ff, #6bc5ff);
					color: white;
				}

				text {
					font-size: 24rpx;
				}
			}
		}
	}

	/* 响应式调整 */
	@media (min-width: 768px) {
		.recipe-detail-page {
			.detail-content {
				max-width: 750px;
				margin: 0 auto;
			}

			.ingredients-grid {
				grid-template-columns: repeat(3, 1fr) !important;
			}

			.nutrition-circles {
				grid-template-columns: repeat(4, 1fr) !important;
			}
		}
	}
</style>