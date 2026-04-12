<template>
	<view class="recipe-edit-page">
		<scroll-view class="edit-content" scroll-y :style="{'height': scrollHeight + 'px'}">
			<!-- 基本信息 -->
			<view class="edit-section">
				<text class="section-title">基本信息</text>

				<view class="form-group">
					<text class="form-label">食谱名称</text>
					<input class="form-input" v-model="recipe.fName" placeholder="请输入食谱名称" maxlength="50" />
					<text class="char-count">{{ recipe.fName.length }}/50</text>
				</view>

				<view class="form-group">
					<text class="form-label">食谱菜系</text>
					<input class="form-input" v-model="recipe.fType" placeholder="请输入食谱菜系：川菜、粤菜、闽菜..." maxlength="50" />
					<text class="char-count">{{ recipe.fType.length }}/50</text>
				</view>

				<view class="form-group">
					<text class="form-label-no">简介</text>
					<textarea class="form-textarea" v-model="recipe.fIntro" placeholder="简单介绍一下这个食谱的特点..."
						maxlength="200" auto-height />
					<text class="char-count">{{ recipe.fIntro.length }}/200</text>
				</view>

				<view class="form-group">
					<text class="form-label-no">封面图片</text>
					<view class="image-upload" @click="chooseImage">
						<image v-if="recipe.fImg" :src="getFullImageUrl(recipe.fImg)" class="recipe-image"></image>
						<view v-else class="image-placeholder">
							<uni-icons type="camera" size="40" color="#ccc"></uni-icons>
							<text>点击上传图片</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 配料管理 -->
			<view class="edit-section">
				<view class="section-header">
					<text class="section-title">食材管理</text>
					<text class="section-subtitle">已添加 {{ (recipe.ingredients || []).length }} 种食材</text>
				</view>

				<!-- 营养统计 -->
				<view class="nutrition-summary">
					<view class="nutrition-item">
						<text class="nutrition-label">总热量</text>
						<text class="nutrition-value">{{ recipe.calories || '0' }} kcal</text>
					</view>
					<view class="nutrition-divider"></view>
					<view class="nutrition-item">
						<text class="nutrition-label">蛋白质</text>
						<text class="nutrition-value">{{ recipe.protein || '0' }}g</text>
					</view>
					<view class="nutrition-divider"></view>
					<view class="nutrition-item">
						<text class="nutrition-label">碳水</text>
						<text class="nutrition-value">{{ recipe.carbs || '0' }}g</text>
					</view>
					<view class="nutrition-divider"></view>
					<view class="nutrition-item">
						<text class="nutrition-label">脂肪</text>
						<text class="nutrition-value">{{ recipe.fat || '0' }}g</text>
					</view>
				</view>

				<!-- 食材列表 -->
				<view class="ingredients-list">
					<view v-for="(item, index) in recipe.ingredients" :key="index" class="ingredient-item">
						<view class="ingredient-info">
							<text class="ingredient-name">{{ item.foodName }}</text>
							<text class="ingredient-amount">{{ item.weight }}g</text>
							<text class="ingredient-calories">{{ item.calories }} kcal</text>
						</view>
						<view class="ingredient-actions">
							<button class="action-btn edit" @click="editIngredient(index)">
								<uni-icons type="compose" size="14" color="#1890ff"></uni-icons>
							</button>
							<button class="action-btn delete" @click="removeIngredient(index)">
								<uni-icons type="trash" size="14" color="#ff4444"></uni-icons>
							</button>
						</view>
					</view>

					<view class="add-ingredient-btn" @click="openAddIngredientPopup">
						<uni-icons type="plus" size="20" color="#1890ff"></uni-icons>
						<text>添加食材</text>
					</view>
				</view>
			</view>

			<!-- 制作步骤 -->
			<view class="edit-section">
				<text class="section-title">制作步骤</text>

				<view class="form-group">
					<text class="form-label-no">食材清单</text>
					<textarea :disabled="true" class="form-textarea" v-model="recipe.fBatching" placeholder="列出需要准备的食材和用量..."
						maxlength="500" auto-height />
					<text class="char-count">{{ recipe.fBatching.length }}/500</text>
				</view>

				<view class="form-group">
					<text class="form-label-no">调味料</text>
					<textarea class="form-textarea" v-model="recipe.fSeasoning" placeholder="列出需要的调味料和用量..."
						maxlength="200" auto-height />
					<text class="char-count">{{ recipe.fSeasoning.length }}/200</text>
				</view>

				<view class="form-group">
					<text class="form-label-no">制作方法</text>
					<textarea class="form-textarea" v-model="recipe.fMade" placeholder="详细描述制作步骤..." maxlength="1000"
						auto-height />
					<text class="char-count">{{ recipe.fMade.length }}/1000</text>
				</view>

				<view class="form-group">
					<text class="form-label-no">小贴士</text>
					<textarea class="form-textarea" v-model="recipe.fTips" placeholder="添加一些烹饪小贴士或注意事项..."
						maxlength="300" auto-height />
					<text class="char-count">{{ recipe.fTips.length }}/300</text>
				</view>
			</view>
		</scroll-view>

		<!-- 添加食材弹窗 -->
		<uni-popup ref="addIngredientPopup" type="bottom">
			<view class="add-ingredient-popup">
				<view class="popup-header">
					<text class="popup-title">添加食材</text>
					<button class="popup-close" @click="closeAddIngredientPopup">
						<uni-icons type="close" size="20" color="#999"></uni-icons>
					</button>
				</view>

				<view class="popup-content">
					<!-- 搜索食材 -->
					<view class="search-food">
						<uni-icons type="search" size="18" color="#999"></uni-icons>
						<input class="search-input" v-model="foodSearch" placeholder="搜索食物名称..." @input="searchFoods" />
					</view>

					<!-- 食物列表 -->
					<scroll-view class="food-list" scroll-y :style="{'height': foodListHeight + 'px'}">
						<view v-if="loadingFoods" class="loading-food">
							<uni-load-more status="loading"></uni-load-more>
						</view>

						<view v-else-if="filteredFoods.length === 0" class="empty-food">
							<text>未找到相关食材</text>
						</view>

						<view v-else>
							<view v-for="food in filteredFoods" :key="food.fID" class="food-item"
								@click="selectFood(food)">
								<view class="food-info">
									<text class="food-name">{{ food.fName }}</text>
									<text class="food-category">{{ food.fType }}</text>
									<text class="food-nutrition">
										热量: {{ food.fEnergy || 0 }}kcal | 蛋白: {{ food.fProtein || 0 }}g | 碳水:{{ food.fCarbonWater || 0 }}g
									</text>
								</view>
								<uni-icons type="arrowright" size="16" color="#999"></uni-icons>
							</view>
						</view>
					</scroll-view>
				</view>
			</view>
		</uni-popup>

		<!-- 食材用量弹窗 -->
		<uni-popup ref="amountPopup" type="dialog">
			<view class="amount-popup">
				<view class="popup-header">
					<text class="popup-title">设置用量</text>
					<text class="food-name">{{ selectedFood ? selectedFood.fName : manualFoodName }}</text>
				</view>

				<view class="popup-content">
					<view class="amount-input-group">
						<text class="input-label">数量</text>
						<view class="amount-input">
							<button class="amount-btn" @click="decreaseAmount">-</button>
							<input class="amount-field" v-model="ingredientAmount" type="number" />
							<button class="amount-btn" @click="increaseAmount">+</button>
						</view>
					</view>

					<view class="unit-select-group">
						<text class="input-label">单位</text>
						<view class="unit-options">
							<view v-for="unit in units" :key="unit" class="unit-option"
								:class="{ active: ingredientUnit === unit }" @click="ingredientUnit = unit">
								<text>{{ unit }}</text>
							</view>
						</view>
					</view>

					<!-- 营养信息预览 -->
					<view class="nutrition-preview" v-if="selectedFood">
						<text class="preview-title">营养信息</text>
						<view class="preview-content">
							<view class="preview-item">
								<text>热量:</text>
								<text>{{ calculateCalories() }} kcal</text>
							</view>
							<view class="preview-item">
								<text>蛋白质:</text>
								<text>{{ calculateProtein() }} g</text>
							</view>
							<view class="preview-item">
								<text>碳水:</text>
								<text>{{ calculateCarbs() }} g</text>
							</view>
							<view class="preview-item">
								<text>脂肪:</text>
								<text>{{ calculateFat() }} g</text>
							</view>
						</view>
					</view>
				</view>

				<view class="popup-actions">
					<button class="popup-btn cancel" @click="closeAmountPopup">取消</button>
					<button class="popup-btn confirm" @click="addIngredient">确认添加</button>
				</view>
			</view>
		</uni-popup>

		<!-- 保存确认 -->
		<view class="save-bar">
			<view class="nutrition-summary-bar">
			</view>
			<button class="save-btn-bar" @click="saveRecipe">
				<text>{{ isEdit ? '保存修改' : '创建食谱' }}</text>
			</button>
		</view>
	</view>
</template>

<script>
	import {
		recipeApi,
		foodApi
	} from '@/api/index.js';
	import {
		getTime
	} from '@/common/formatDate.js';
	import {
		IMG_URL,
		BASE_URL
	} from '@/api/config.js';

	export default {
		data() {
			return {
				// 食谱数据
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
					fat: "0",
					op: ""
				},

				// 编辑状态
				isEdit: false,
				recipeId: null,

				// 食材相关
				selectedFood: null,
				manualFoodName: "",
				ingredientAmount: 100,
				ingredientUnit: "g",
				editingIngredientIndex: -1,

				// 食物数据
				allFoods: [],
				filteredFoods: [],
				loadingFoods: false,
				foodSearch: "",

				// 单位和选项
				units: ["g"],

				// 页面尺寸
				scrollHeight: 500,
				foodListHeight: 300
			};
		},

		computed: {
			// 表单验证
			isFormName() {
				return this.recipe.fName && this.recipe.fName.trim().length > 0;
			},
			isFormType() {
				return this.recipe.fType && this.recipe.fType.trim().length > 0;
			},
			isFormIngredients() {
				return this.recipe.ingredients && this.recipe.ingredients.length > 0;
			},
		},

		onLoad(options) {
			this.initScrollHeight();

			// 检查是否为编辑模式
			if (options && options.id) {
				this.isEdit = true;
				this.recipeId = options.id;
				this.loadRecipe(this.recipeId);
			}

			// 加载食物数据
			this.loadFoods();
		},

		methods: {
			// 初始化滚动区域高度
			initScrollHeight() {
				const systemInfo = uni.getSystemInfoSync();
				const windowHeight = systemInfo.windowHeight;
				// 减去底部保存栏高度（约100rpx）
				this.scrollHeight = windowHeight - 50; // 使用像素单位
				this.foodListHeight = windowHeight * 0.4;
			},

			// 加载食谱
			async loadRecipe(id) {
				try {
					const res = await recipeApi._getNutritionalRecipeById(id);
					if (res && res.data) {
						this.recipe = {
							...res.data
						};
						const ingredients = await recipeApi._getByRecipeId(this.recipe.fID);
						console.log(ingredients)
						const ingredientsToCreate = ingredients.map(item => ({
							id: item.id,
							foodId: item.ingredient.fID,
							foodName: item.ingredient.fName,
							weight: item.weight,
							unit: 'g',
							calories: ((parseFloat(item.ingredient.fEnergy) / parseFloat(item.ingredient.fEdibleSection)) * item.weight).toFixed(1),
							carbs: ((parseFloat(item.ingredient.fCarbonWater) / parseFloat(item.ingredient.fEdibleSection)) * item.weight).toFixed(1),
							fat: ((parseFloat(item.ingredient.fFat) / parseFloat(item.ingredient.fEdibleSection)) * item.weight).toFixed(1),
							protein: ((parseFloat(item.ingredient.fProtein) / parseFloat(item.ingredient.fEdibleSection)) * item.weight).toFixed(1),
							edible: item.ingredient.fEdibleSection,
						}));
						this.recipe.ingredients = ingredientsToCreate
					}
				} catch (error) {
					console.error('加载食谱失败:', error);
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					});
				}
			},

			// 加载食物数据
			async loadFoods() {
				try {
					this.loadingFoods = true;
					const res = await foodApi._getFoodNutritionAll();
					if (res) {
						this.allFoods = res;
						this.filteredFoods = res.slice(0, 20); // 初始显示20条
					}
				} catch (error) {
					console.error('加载食物失败:', error);
				} finally {
					this.loadingFoods = false;
				}
			},

			// 搜索食物
			searchFoods() {
				const keyword = this.foodSearch.toLowerCase().trim();

				if (!keyword) {
					this.filteredFoods = this.allFoods.slice(0, 20);
					return;
				}

				this.filteredFoods = this.allFoods.filter(food =>
					food.fName.toLowerCase().includes(keyword) ||
					(food.fType && food.fType.toLowerCase().includes(keyword))
				).slice(0, 20);
			},

			// 选择食物
			selectFood(food) {
				this.selectedFood = food;
				this.manualFoodName = "";
				this.ingredientAmount = 100;
				this.ingredientUnit = "g";
				this.closeAddIngredientPopup();
				this.openAmountPopup();
			},

			// 打开添加食材弹窗
			openAddIngredientPopup() {
				if (this.$refs.addIngredientPopup) {
					this.$refs.addIngredientPopup.open();
				}
				this.foodSearch = "";
				this.filteredFoods = this.allFoods.slice(0, 20);
			},

			// 关闭添加食材弹窗
			closeAddIngredientPopup() {
				if (this.$refs.addIngredientPopup) {
					this.$refs.addIngredientPopup.close();
				}
			},

			// 打开用量弹窗
			openAmountPopup() {
				if (this.$refs.amountPopup) {
					this.$refs.amountPopup.open({
						maskClick: false // 禁止点击遮罩层关闭
					});
				}
			},

			// 关闭用量弹窗
			closeAmountPopup() {
				this.editingIngredientIndex = -1;
				if (this.$refs.amountPopup) {
					this.$refs.amountPopup.close();
				}
			},

			// 编辑食材
			editIngredient(index) {
				const ingredient = this.recipe.ingredients[index];
				this.editingIngredientIndex = index;

				// 查找对应的食物
				const food = this.allFoods.find(f => f.fID === ingredient.foodId);
				if (food) {
					this.selectedFood = food;
				} else {
					this.manualFoodName = ingredient.foodName;
				}
				this.ingredientAmount = ingredient.weight;
				this.ingredientUnit = ingredient.unit;

				this.openAmountPopup();
			},

			// 增加数量
			increaseAmount() {
				this.ingredientAmount = (parseFloat(this.ingredientAmount) || 0) + 1;
			},

			// 减少数量
			decreaseAmount() {
				if (this.ingredientAmount > 1) {
					this.ingredientAmount = parseFloat(this.ingredientAmount) - 1;
				}
			},

			// 计算热量
			calculateCalories() {
				if (!this.selectedFood) return 0;
				const energy = parseFloat(this.selectedFood.fEnergy) || 0;
				const edible = parseFloat(this.selectedFood.fEdibleSection) || 0; //可食部
				return ((energy / edible) * this.ingredientAmount).toFixed(1);
			},

			// 计算蛋白质
			calculateProtein() {
				if (!this.selectedFood) return 0;
				const protein = parseFloat(this.selectedFood.fProtein) || 0;
				const edible = parseFloat(this.selectedFood.fEdibleSection) || 0;
				return ((protein / edible) * this.ingredientAmount).toFixed(1);
			},

			// 计算碳水
			calculateCarbs() {
				if (!this.selectedFood) return 0;
				const carbs = parseFloat(this.selectedFood.fCarbonWater) || 0;
				const edible = parseFloat(this.selectedFood.fEdibleSection) || 0;
				return ((carbs / edible) * this.ingredientAmount).toFixed(1);
			},

			// 计算脂肪
			calculateFat() {
				if (!this.selectedFood) return 0;
				const fat = parseFloat(this.selectedFood.fFat) || 0;
				const edible = parseFloat(this.selectedFood.fEdibleSection) || 0;
				return ((fat / edible) * this.ingredientAmount).toFixed(1);
			},

			// 添加食材
			addIngredient() {
				console.log("添加之前：",this.recipe.ingredients)
				const f = this.recipe.ingredients.find(f => f.foodId === this.selectedFood.fID);

				const ingredient = {
					id: getTime("start"),
					foodId: this.selectedFood ? this.selectedFood.fID : null,
					foodName: this.selectedFood ? this.selectedFood.fName : this.manualFoodName.trim(),
					weight: parseFloat(this.ingredientAmount) || 0,
					unit: this.ingredientUnit,
					calories: this.calculateCalories(),
					protein: this.calculateProtein(),
					carbs: this.calculateCarbs(),
					fat: this.calculateFat()
				};

				if (this.editingIngredientIndex >= 0) {
					// 编辑现有食材
					this.recipe.ingredients[this.editingIngredientIndex] = ingredient;
					this.editingIngredientIndex = -1;
				} else if (!f) {
					// 添加新食材
					this.recipe.ingredients.push(ingredient);
				} else {
					uni.showToast({
						title: '食材已存在！',
						icon: 'none'
					});
				}
				
				console.log("添加之后：",this.recipe.ingredients)

				this.selectedFood = null;
				this.manualFoodName = "";

				// 重新计算总营养
				this.calculateNutrition();

				// 关闭弹窗
				this.closeAmountPopup();
				this.closeAddIngredientPopup();

			},

			// 移除食材
			removeIngredient(index) {
				uni.showModal({
					title: '确认删除',
					content: '确定要删除这个食材吗？',
					success: (res) => {
						if (res.confirm) {
							this.recipe.ingredients.splice(index, 1);
							this.calculateNutrition();
						}
					}
				});
			},

			// 计算总营养
			calculateNutrition() {
				let totalCalories = 0;
				let totalProtein = 0;
				let totalCarbs = 0;
				let totalFat = 0;
				let text = "";

				this.recipe.ingredients.forEach(ingredient => {
					totalCalories += parseFloat(ingredient.calories) || 0;
					totalProtein += parseFloat(ingredient.protein) || 0;
					totalCarbs += parseFloat(ingredient.carbs) || 0;
					totalFat += parseFloat(ingredient.fat) || 0;
					text += `${ingredient.foodName} ${ingredient.weight}g\n`;
				});

				this.recipe.calories = totalCalories.toFixed(1);
				this.recipe.protein = totalProtein.toFixed(1);
				this.recipe.carbs = totalCarbs.toFixed(1);
				this.recipe.fat = totalFat.toFixed(1);
				this.recipe.fBatching = text;
			},

			// 选择图片
			chooseImage() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						const tempFilePath = res.tempFilePaths[0]
						uni.showLoading({
							title: '上传中...'
						})
						const uploadUrl = BASE_URL + '/file/upload';
						// 上传图片
						uni.uploadFile({
							url: uploadUrl,
							filePath: tempFilePath,
							name: 'file',
							header: {
								'Authorization': 'Bearer ' + uni.getStorageSync('token')
							},
							success: (uploadRes) => {
								const data = JSON.parse(uploadRes.data)
								if (data.status === 200) {
									uni.showToast({
										title: '上传成功',
										icon: 'success'
									})
									this.recipe.fImg = data.data;
								} else {
									uni.showToast({
										title: data.message || '上传失败',
										icon: 'none'
									})
								}
							},
							fail: (err) => {
								console.log('上传失败', err)
								uni.showToast({
									title: '上传失败',
									icon: 'none'
								})
							}
						})
					}
				});
			},

			// 保存食谱
			async saveRecipe() {
				if (!this.isFormName) {
					uni.showToast({
						title: '请填写食谱名称',
						icon: 'none'
					});
					return;
				} else if (!this.isFormType) {
					uni.showToast({
						title: '请填写食谱菜系',
						icon: 'none'
					});
					return;
				} else if (!this.isFormIngredients) {
					uni.showToast({
						title: '请选择食材',
						icon: 'none'
					});
					return;
				} else {
					try {
						uni.showLoading({
							title: '保存中...'
						});

						const recipeData = {
							...this.recipe,
							fUpdateTime: new Date().toISOString(),
							op: 'add'
						};
						if (this.isEdit) {
							recipeData.op = 'upd';
							const res = await recipeApi._operationNutritionalRecipe(recipeData);
							if (res.status === 200) {
								uni.showToast({
									title: '保存成功',
									icon: 'success'
								});
								setTimeout(() => {
									uni.navigateBack();
								}, 1500);
							}
						} else {
							const res = await recipeApi._operationNutritionalRecipe(recipeData);
							if (res.status === 200) {
								uni.showToast({
									title: '创建成功',
									icon: 'success'
								});
								setTimeout(() => {
									uni.navigateBack();
								}, 1500);
							}
						}
					} catch (error) {
						console.error('保存食谱失败:', error);
						uni.showToast({
							title: '保存失败',
							icon: 'none'
						});
					}
				}

			},
			// 图片URL处理
			getFullImageUrl(path) {
				if (!path) return "/static/default-knowledge.png";
				if (path.startsWith("http")) return path;
				return IMG_URL + (path.startsWith("/") ? path : "/" + path);
			}
		}
	};
</script>

<style lang="scss">
	@import "@/scss/variables.scss";

	.recipe-edit-page {
		min-height: 100vh;
		background: #f5f7fa;

		.edit-content {
			padding: 24rpx;
			box-sizing: border-box;

			.edit-section {
				background: #fff;
				border-radius: 16rpx;
				padding: 32rpx;
				margin-bottom: 24rpx;
				box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
				box-sizing: border-box;
				width: 100%;
				overflow: hidden;

				.section-header {
					display: flex;
					justify-content: space-between;
					align-items: center;
					margin-bottom: 24rpx;

					.section-title {
						font-size: 32rpx;
						font-weight: 600;
						color: #333;

						&::after {
							content: '*';
							color: #ff4444;
							margin-left: 6rpx;
						}
					}

					.section-subtitle {
						font-size: 24rpx;
						color: #666;
					}
				}

				.section-title {
					font-size: 32rpx;
					font-weight: 600;
					color: #333;
					display: block;
					margin-bottom: 24rpx;
					padding-bottom: 12rpx;
					border-bottom: 2rpx solid #f0f0f0;
				}

				.form-group {
					margin-bottom: 32rpx;
					width: 100%;
					box-sizing: border-box;

					&:last-child {
						margin-bottom: 0;
					}

					.form-label {
						font-size: 28rpx;
						font-weight: 500;
						color: #333;
						display: block;
						margin-bottom: 16rpx;

						&::after {
							content: '*';
							color: #ff4444;
							margin-left: 6rpx;
						}
					}

					.form-label-no {
						font-size: 28rpx;
						font-weight: 500;
						color: #333;
						display: block;
						margin-bottom: 16rpx;
					}

					.form-label {
						font-size: 28rpx;
						font-weight: 500;
						color: #333;
						display: block;
						margin-bottom: 16rpx;

						&::after {
							content: '*';
							color: #ff4444;
							margin-left: 6rpx;
						}
					}

					.form-input {
						border: 2rpx solid #e8e8e8;
						border-radius: 12rpx;
						padding: 20rpx;
						font-size: 28rpx;
						background: #f8f9fa;
						width: 100%;

						&:focus {
							border-color: #1890ff;
							background: #fff;
						}
					}

					.form-textarea {
						border: 2rpx solid #e8e8e8;
						border-radius: 12rpx;
						padding: 20rpx;
						font-size: 28rpx;
						background: #f8f9fa;
						width: 100%;
						min-height: 120rpx;
						box-sizing: border-box;

						&:focus {
							border-color: #1890ff;
							background: #fff;
						}
					}

					.char-count {
						font-size: 24rpx;
						color: #999;
						text-align: right;
						display: block;
						margin-top: 8rpx;
					}

					.image-upload {
						border: 2rpx dashed #e8e8e8;
						border-radius: 12rpx;
						padding: 40rpx;
						text-align: center;
						background: #f8f9fa;
						box-sizing: border-box;

						.recipe-image {
							width: 100%;
							height: 300rpx;
							border-radius: 8rpx;
							object-fit: cover;
						}

						.image-placeholder {
							display: flex;
							flex-direction: column;
							align-items: center;
							gap: 16rpx;
							color: #ccc;
						}
					}
				}

				.nutrition-summary {
					background: #f8f9fa;
					border-radius: 12rpx;
					padding: 24rpx;
					display: flex;
					justify-content: space-between;
					align-items: center;
					margin-bottom: 24rpx;
					box-sizing: border-box;

					.nutrition-item {
						flex: 1;
						text-align: center;
						min-width: 0;

						.nutrition-label {
							font-size: 24rpx;
							color: #666;
							display: block;
							margin-bottom: 8rpx;
							white-space: nowrap;
						}

						.nutrition-value {
							font-size: 28rpx;
							font-weight: 600;
							color: #333;
							display: block;
							white-space: nowrap;
						}
					}

					.nutrition-divider {
						width: 1rpx;
						height: 40rpx;
						background: #e8e8e8;
						flex-shrink: 0;
					}
				}

				.ingredients-list {
					.ingredient-item {
						padding: 20rpx;
						border-bottom: 1rpx solid #f0f0f0;
						display: flex;
						justify-content: space-between;
						align-items: center;
						box-sizing: border-box;

						&:last-child {
							border-bottom: none;
						}

						.ingredient-info {
							flex: 1;
							min-width: 0;
							overflow: hidden;

							.ingredient-name {
								font-size: 28rpx;
								font-weight: 500;
								color: #333;
								display: block;
								margin-bottom: 4rpx;
								white-space: nowrap;
								overflow: hidden;
								text-overflow: ellipsis;
							}

							.ingredient-amount {
								font-size: 24rpx;
								color: #666;
								display: block;
								margin-bottom: 4rpx;
							}

							.ingredient-calories {
								font-size: 22rpx;
								color: #999;
								display: block;
							}
						}

						.ingredient-actions {
							display: flex;
							gap: 8rpx;
							flex-shrink: 0;

							.action-btn {
								padding: 8rpx;
								background: transparent;
								border: none;
							}
						}
					}

					.add-ingredient-btn {
						padding: 24rpx;
						border: 2rpx dashed #1890ff;
						border-radius: 12rpx;
						text-align: center;
						color: #1890ff;
						display: flex;
						align-items: center;
						justify-content: center;
						gap: 8rpx;
						margin-top: 16rpx;
						box-sizing: border-box;
					}
				}
			}
		}

		.add-ingredient-popup {
			background: #fff;
			border-radius: 24rpx 24rpx 0 0;
			max-height: 80vh;
			box-sizing: border-box;

			.popup-header {
				padding: 32rpx 24rpx 16rpx;
				border-bottom: 2rpx solid #f0f0f0;
				display: flex;
				justify-content: space-between;
				align-items: center;
				box-sizing: border-box;

				.popup-title {
					font-size: 32rpx;
					font-weight: 600;
					color: #333;
				}

				.popup-close {
					padding: 8rpx;
					background: transparent;
					border: none;
				}
			}

			.popup-content {
				padding: 24rpx;
				box-sizing: border-box;

				.search-food {
					display: flex;
					align-items: center;
					background: #f8f9fa;
					border-radius: 40rpx;
					padding: 0 24rpx;
					margin-bottom: 24rpx;
					box-sizing: border-box;

					.search-input {
						flex: 1;
						margin-left: 12rpx;
						font-size: 28rpx;
						height: 80rpx;
						box-sizing: border-box;
					}
				}

				.food-list {
					margin-bottom: 24rpx;
					box-sizing: border-box;

					.loading-food,
					.empty-food {
						padding: 40rpx 0;
						text-align: center;
						color: #999;
					}

					.food-item {
						padding: 20rpx;
						border-bottom: 1rpx solid #f0f0f0;
						display: flex;
						align-items: center;
						justify-content: space-between;
						box-sizing: border-box;

						.food-info {
							flex: 1;
							min-width: 0;
							overflow: hidden;

							.food-name {
								font-size: 28rpx;
								font-weight: 500;
								color: #333;
								display: block;
								margin-bottom: 4rpx;
								white-space: nowrap;
								overflow: hidden;
								text-overflow: ellipsis;
							}

							.food-category {
								font-size: 22rpx;
								color: #1890ff;
								display: block;
								margin-bottom: 4rpx;
							}

							.food-nutrition {
								font-size: 22rpx;
								color: #666;
								display: block;
							}
						}
					}
				}
			}
		}

		.amount-popup {
			background: #fff;
			border-radius: 24rpx;
			width: 90vw;
			max-width: 600rpx;
			overflow: hidden;
			box-sizing: border-box;

			.popup-header {
				padding: 32rpx 24rpx 16rpx;
				text-align: center;
				box-sizing: border-box;

				.popup-title {
					font-size: 32rpx;
					font-weight: 600;
					color: #333;
					display: block;
					margin-bottom: 8rpx;
				}

				.food-name {
					font-size: 28rpx;
					color: #666;
					display: block;
				}
			}

			.popup-content {
				padding: 0 24rpx 24rpx;
				box-sizing: border-box;

				.amount-input-group {
					margin-bottom: 24rpx;

					.input-label {
						font-size: 28rpx;
						color: #333;
						display: block;
						margin-bottom: 12rpx;
					}

					.amount-input {
						display: flex;
						align-items: center;

						.amount-btn {
							width: 80rpx;
							height: 80rpx;
							background: #f8f9fa;
							border: 2rpx solid #e8e8e8;
							font-size: 32rpx;
							color: #333;
							border: none;

							&:first-child {
								border-radius: 12rpx 0 0 12rpx;
								border-right: none;
							}

							&:last-child {
								border-radius: 0 12rpx 12rpx 0;
								border-left: none;
							}
						}

						.amount-field {
							flex: 1;
							height: 80rpx;
							border-top: 2rpx solid #e8e8e8;
							border-bottom: 2rpx solid #e8e8e8;
							text-align: center;
							font-size: 32rpx;
							box-sizing: border-box;
						}
					}
				}

				.unit-select-group {
					margin-bottom: 24rpx;

					.input-label {
						font-size: 28rpx;
						color: #333;
						display: block;
						margin-bottom: 12rpx;
					}

					.unit-options {
						display: flex;
						flex-wrap: wrap;
						gap: 12rpx;

						.unit-option {
							padding: 16rpx 24rpx;
							border: 2rpx solid #e8e8e8;
							border-radius: 20rpx;
							font-size: 24rpx;
							color: #666;
							box-sizing: border-box;

							&.active {
								background: #1890ff;
								border-color: #1890ff;
								color: #fff;
							}
						}
					}
				}

				.nutrition-preview {
					background: #f8f9fa;
					border-radius: 12rpx;
					padding: 20rpx;
					box-sizing: border-box;

					.preview-title {
						font-size: 24rpx;
						font-weight: 500;
						color: #333;
						display: block;
						margin-bottom: 12rpx;
					}

					.preview-content {
						.preview-item {
							display: flex;
							justify-content: space-between;
							align-items: center;
							padding: 8rpx 0;
							border-bottom: 1rpx solid #e8e8e8;
							box-sizing: border-box;

							&:last-child {
								border-bottom: none;
							}

							text {
								font-size: 24rpx;
								color: #666;

								&:last-child {
									font-weight: 500;
									color: #333;
								}
							}
						}
					}
				}
			}

			.popup-actions {
				display: flex;
				gap: 24rpx;
				padding: 24rpx;
				border-top: 2rpx solid #f0f0f0;
				box-sizing: border-box;

				.popup-btn {
					flex: 1;
					padding: 8rpx;
					border-radius: 12rpx;
					font-size: 28rpx;
					font-weight: 500;
					border: none;
					box-sizing: border-box;

					&.cancel {
						background: #f8f9fa;
						color: #666;
					}

					&.confirm {
						background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
						color: #fff;

						&:disabled {
							background: #ccc;
							opacity: 0.6;
						}
					}
				}
			}
		}

		.save-bar {
			position: fixed;
			bottom: 0;
			left: 0;
			right: 0;
			background: #fff;
			padding: 16rpx 24rpx;
			border-top: 2rpx solid #f0f0f0;
			display: flex;
			align-items: center;
			justify-content: space-between;
			box-sizing: border-box;
			// z-index: 2;

			.nutrition-summary-bar {
				flex: 1;
				display: flex;
				align-items: center;
				gap: 12rpx;
				font-size: 22rpx;
				color: #666;
				overflow-x: auto;
				white-space: nowrap;
				padding: 8rpx 0;

				.summary-item {
					white-space: nowrap;
					flex-shrink: 0;
				}

				.summary-divider {
					color: #ccc;
					flex-shrink: 0;
				}
			}

			.save-btn-bar {
				padding: 16rpx 32rpx;
				background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
				color: #fff;
				border-radius: 40rpx;
				font-size: 28rpx;
				font-weight: 500;
				border: none;
				flex-shrink: 0;

				&:disabled {
					background: #ccc;
					opacity: 0.6;
				}
			}
		}
	}
</style>