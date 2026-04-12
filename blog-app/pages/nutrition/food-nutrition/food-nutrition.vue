<template>
	<view class="food-manage-page">
		<!-- 头部 -->
		<view class="header">
			<view class="header-left">
				<text class="title">食物基础营养</text>
			</view>
			<view class="header-right">
				<button class="add-btn" @click="openAddDialog">
					<uni-icons type="plus" size="20" color="#fff"></uni-icons>
					<text>新增食物</text>
				</button>
			</view>
		</view>

		<!-- 搜索和筛选 -->
		<view class="search-section">
			<view class="search-input">
				<uni-icons type="search" size="20" color="#999"></uni-icons>
				<input class="input" v-model="searchKeyword" placeholder="搜索食物名称或类别..." @input="handleSearchInput"
					placeholder-style="color: #aaa;" />
				<view v-if="searchKeyword" class="clear-icon" @click="clearSearch">
					<uni-icons type="clear" size="18" color="#999"></uni-icons>
				</view>
			</view>

			<view class="filter-options">
				<view class="filter-row">
					<text class="filter-label">类别筛选:</text>
					<scroll-view class="category-scroll" scroll-x>
						<view class="category-tags">
							<view class="category-tag" :class="{ active: activeCategory === '全部' }"
								@click="setCategory('全部')">
								<text>全部</text>
							</view>
							<view v-for="category in categories" :key="category" class="category-tag"
								:class="{ active: activeCategory === category }" @click="setCategory(category)">
								<text>{{ category }}</text>
							</view>
						</view>
					</scroll-view>
				</view>

				<view class="filter-row">
					<view class="sort-options">
						<button class="sort-btn" :class="{ active: sortBy === 'fID' }" @click="setSort('fID')">
							<text>按ID</text>
						</button>
						<button class="sort-btn" :class="{ active: sortBy === 'fName' }" @click="setSort('fName')">
							<text>按名称</text>
						</button>
						<button class="sort-btn" :class="{ active: sortBy === 'fEnergy' }" @click="setSort('fEnergy')">
							<text>按热量</text>
						</button>
					</view>
				</view>
			</view>
		</view>

		<!-- 食物列表 -->
		<scroll-view class="food-list-scroll" scroll-y :style="{height: scrollHeight + 'px'}"
			@scrolltolower="loadMoreData" :lower-threshold="100" :scroll-with-animation="true" :show-scrollbar="false">
			<!-- 骨架屏 -->
			<view v-if="loading" class="skeleton-container">
				<view v-for="n in 5" :key="n" class="skeleton-item">
					<view class="skeleton-image"></view>
					<view class="skeleton-content">
						<view class="skeleton-title"></view>
						<view class="skeleton-text"></view>
						<view class="skeleton-text short"></view>
					</view>
				</view>
			</view>

			<!-- 空状态 -->
			<view v-else-if="displayFoods.length === 0" class="empty-state">
				<uni-icons type="food" size="60" color="#ccc"></uni-icons>
				<text class="empty-text">暂无食物数据</text>
				<text class="empty-subtext">点击上方按钮添加食物</text>
			</view>

			<!-- 虚拟列表 -->
			<view v-else>
				<view v-for="food in displayFoods" :key="food.fID" class="food-item" @click="viewFoodDetail(food)">
					<view class="food-header">
						<view class="food-image" v-if="food.fImg">
							<image :src="getFullImageUrl(food.fImg)" class="food-img" mode="aspectFill" lazy-load>
							</image>
						</view>
						<view v-else class="food-image placeholder">
							<uni-icons type="food" size="30" color="#999"></uni-icons>
						</view>

						<view class="food-basic-info">
							<view class="food-name-row">
								<text class="food-name">{{ food.fName }}</text>
								<text class="food-id">ID: {{ food.fID }}</text>
							</view>
							<view class="food-category">
								<text class="category-tag">{{ food.fType || '未分类' }}</text>
							</view>
						</view>
					</view>

					<view class="nutrition-summary">
						<view class="nutrition-item">
							<text class="nutrition-label">热量</text>
							<text class="nutrition-value">{{ food.fEnergy || 0 }} 千卡</text>
						</view>
						<view class="nutrition-divider"></view>
						<view class="nutrition-item">
							<text class="nutrition-label">蛋白质</text>
							<text class="nutrition-value">{{ food.fProtein || 0 }}g</text>
						</view>
						<view class="nutrition-divider"></view>
						<view class="nutrition-item">
							<text class="nutrition-label">脂肪</text>
							<text class="nutrition-value">{{ food.fFat || 0 }}g</text>
						</view>
						<view class="nutrition-divider"></view>
						<view class="nutrition-item">
							<text class="nutrition-label">碳水</text>
							<text class="nutrition-value">{{ food.fCarbonWater || 0 }}g</text>
						</view>
					</view>

					<view class="food-actions">
						<button class="action-btn edit" @click.stop="openEditDialog(food)">
							<uni-icons type="compose" size="16" color="#1890ff"></uni-icons>
							<text>编辑</text>
						</button>
						<button class="action-btn delete" @click.stop="confirmDelete(food)">
							<uni-icons type="trash" size="16" color="#ff4444"></uni-icons>
							<text>删除</text>
						</button>
					</view>
				</view>

				<!-- 加载更多提示 -->
				<view v-if="loadingMore" class="load-more">
					<uni-load-more status="loading"></uni-load-more>
				</view>
				<view v-else-if="hasMore" class="load-more-tip">
					<uni-icons type="bottom" size="16" color="#999"></uni-icons>
					<text>上拉加载更多</text>
				</view>
				<view v-else-if="displayFoods.length > 0" class="load-more-tip">
					<text class="no-more">已加载全部数据</text>
				</view>
			</view>
		</scroll-view>

		<!-- 添加/编辑弹窗 -->
		<uni-popup ref="foodDialog" type="dialog">
			<view class="dialog-content">
				<view class="dialog-header">
					<text class="dialog-title">{{ isEditing ? '编辑食物' : '添加新食物' }}</text>
					<button class="close-btn" @click="closeDialog">
						<uni-icons type="close" size="20" color="#999"></uni-icons>
					</button>
				</view>

				<scroll-view class="form-scroll" scroll-y>
					<view class="form-content">
						<!-- 基本信息 -->
						<view class="form-section">
							<text class="section-title">基本信息</text>

							<view class="form-item">
								<text class="form-label">食物名称</text>
								<input class="form-input" v-model="currentFood.fName" placeholder="请输入食物名称"
									maxlength="50" placeholder-style="color: #aaa;" />
								<text
									class="char-count">{{ currentFood.fName ? currentFood.fName.length : 0 }}/50</text>
							</view>

							<view class="form-item">
								<text class="form-label">食物类别</text>
								<input class="form-input" v-model="currentFood.fType" placeholder="请输入食物类别（如：水果、蔬菜）"
									maxlength="20" placeholder-style="color: #aaa;" />
								<text
									class="char-count">{{ currentFood.fType ? currentFood.fType.length : 0 }}/20</text>
							</view>
							
							<view class="form-item">
								<text class="form-label">可食部</text>
								<input class="form-input" v-model="currentFood.fEdibleSection" placeholder="请输入可食部百分比"
									placeholder-style="color: #aaa;" />
							</view>

							<view class="form-item">
								<text class="nutrition-label">图片URL</text>
								<input class="form-input" v-model="currentFood.fImg" placeholder="请输入图片地址"
									placeholder-style="color: #aaa;" />
							</view>
						</view>

						<!-- 宏量营养素 -->
						<view class="form-section">
							<text class="section-title">宏量营养素 (每100克)</text>

							<view class="nutrition-grid">
								<view class="nutrition-input">
									<text class="nutrition-label">能量(千卡)</text>
									<input class="form-input small" v-model="currentFood.fEnergy" placeholder="0"
										type="number" />
								</view>

								<view class="nutrition-input">
									<text class="nutrition-label">蛋白质(g)</text>
									<input class="form-input small" v-model="currentFood.fProtein" placeholder="0"
										type="number" />
								</view>

								<view class="nutrition-input">
									<text class="nutrition-label">脂肪(g)</text>
									<input class="form-input small" v-model="currentFood.fFat" placeholder="0"
										type="number" />
								</view>

								<view class="nutrition-input">
									<text class="nutrition-label">碳水(g)</text>
									<input class="form-input small" v-model="currentFood.fCarbonWater" placeholder="0"
										type="number" />
								</view>

								<view class="nutrition-input">
									<text class="nutrition-label">膳食纤维(g)</text>
									<input class="form-input small" v-model="currentFood.fDietaryFiber" placeholder="0"
										type="number" />
								</view>
							</view>
						</view>

						<!-- 维生素 -->
						<view class="form-section">
							<text class="section-title">维生素 (每100克)</text>

							<view class="vitamin-grid">
								<view v-for="vitamin in vitamins" :key="vitamin.field" class="vitamin-input">
									<text class="vitamin-label">{{ vitamin.name }}</text>
									<input class="form-input tiny" v-model="currentFood[vitamin.field]"
										:placeholder="vitamin.unit" type="number" />
								</view>
							</view>
						</view>

						<!-- 矿物质 -->
						<view class="form-section">
							<text class="section-title">矿物质 (每100克)</text>

							<view class="mineral-grid">
								<view v-for="mineral in minerals" :key="mineral.field" class="mineral-input">
									<text class="mineral-label">{{ mineral.name }}</text>
									<input class="form-input tiny" v-model="currentFood[mineral.field]"
										:placeholder="mineral.unit" type="number" />
								</view>
							</view>
						</view>

						<!-- 其他信息 -->
						<view class="form-section">
							<text class="section-title">其他信息</text>

							<view class="form-item">
								<text class="nutrition-label">主要作用</text>
								<textarea class="form-textarea" v-model="currentFood.fRole" placeholder="请输入食物的主要作用"
									maxlength="200" placeholder-style="color: #aaa;" auto-height />
								<text
									class="char-count">{{ currentFood.fRole ? currentFood.fRole.length : 0 }}/200</text>
							</view>

							<view class="form-item">
								<text class="nutrition-label">备注</text>
								<textarea class="form-textarea" v-model="currentFood.fNote" placeholder="请输入备注信息"
									maxlength="500" placeholder-style="color: #aaa;" auto-height />
								<text
									class="char-count">{{ currentFood.fNote ? currentFood.fNote.length : 0 }}/500</text>
							</view>
						</view>
					</view>
				</scroll-view>

				<view class="dialog-actions">
					<button class="dialog-btn cancel" @click="closeDialog">取消</button>
					<button class="dialog-btn confirm" @click="saveFood" >
						{{ isEditing ? '保存修改' : '添加食物' }}
					</button>
				</view>
			</view>
		</uni-popup>

		<!-- 删除确认弹窗 -->
		<uni-popup ref="deleteDialog" type="dialog">
			<view class="delete-dialog">
				<text class="delete-title">确认删除</text>
				<text class="delete-content">确定要删除食物 "{{ deleteFood ? deleteFood.fName : '' }}" 吗？</text>
				<text class="delete-warning">此操作不可撤销！</text>
				<view class="delete-actions">
					<button class="delete-btn cancel" @click="cancelDelete">取消</button>
					<button class="delete-btn confirm" @click="deleteFoodConfirm">确认删除</button>
				</view>
			</view>
		</uni-popup>

		<!-- 底部统计 -->
		<view class="footer-tip">
			<text>{{ categories.length }}个类别 共 {{ totalCount }} 种食物</text>
			<text> | </text>
			<text>最后更新: {{ lastUpdateTime }}</text>
		</view>
	</view>
</template>

<script>
	import {
		foodApi
	} from '@/api/index.js';
	import {
		IMG_URL
	} from '@/api/config.js';

	export default {
		data() {
			return {
				// 食物列表
				foodList: [], // 完整数据（所有数据）
				filteredFoods: [], // 搜索筛选后的数据
				displayFoods: [], // 当前显示的数据（分页）
				loading: false,

				// 分页和懒加载
				pageSize: 20,
				currentPage: 0,
				loadingMore: false,
				hasMore: true,
				scrollHeight: 500,

				// 搜索和筛选
				searchKeyword: '',
				activeCategory: '全部',
				categories: [],
				sortBy: 'fID',
				sortOrder: 'desc',
				searchTimer: null, // 搜索防抖

				// 当前操作食物
				currentFood: this.getEmptyFood(),
				isEditing: false,

				// 删除相关
				deleteFood: null,

				// 维生素配置
				vitamins: [{
						field: 'fVitaminA',
						name: '维生素A',
						unit: 'μg'
					},
					{
						field: 'fVitaminC',
						name: '维生素C',
						unit: 'mg'
					},
					{
						field: 'fVitaminD',
						name: '维生素D',
						unit: 'μg'
					},
					{
						field: 'fVitaminE',
						name: '维生素E',
						unit: 'mg'
					},
					{
						field: 'fVitaminK',
						name: '维生素K',
						unit: 'μg'
					},
					{
						field: 'fVitaminB1',
						name: '维生素B1',
						unit: 'mg'
					},
					{
						field: 'fVitaminB2',
						name: '维生素B2',
						unit: 'mg'
					},
					{
						field: 'fVitaminB3',
						name: '维生素B3',
						unit: 'mg'
					},
					{
						field: 'fVitaminB6',
						name: '维生素B6',
						unit: 'mg'
					},
					{
						field: 'fVitaminB9',
						name: '维生素B9',
						unit: 'μg'
					},
					{
						field: 'fVitaminB12',
						name: '维生素B12',
						unit: 'μg'
					}
				],

				// 矿物质配置
				minerals: [{
						field: 'fK',
						name: '钾',
						unit: 'mg'
					},
					{
						field: 'fNa',
						name: '钠',
						unit: 'mg'
					},
					{
						field: 'fCa',
						name: '钙',
						unit: 'mg'
					},
					{
						field: 'fZn',
						name: '锌',
						unit: 'mg'
					},
					{
						field: 'fSe',
						name: '硒',
						unit: 'μg'
					},
					{
						field: 'fFe',
						name: '铁',
						unit: 'mg'
					}
				]
			}
		},
		computed: {
			// 总数据量
			totalCount() {
				if (this.searchKeyword || this.activeCategory !== '全部') {
					return this.filteredFoods.length;
				}
				return this.foodList.length;
			},

			// 最后更新时间
			lastUpdateTime() {
				if (this.foodList.length === 0) return '暂无数据';

				const latest = this.foodList.reduce((latest, food) => {
					const time = food.fUpdateTime || food.fCreateTime;
					return time > latest ? time : latest;
				}, '');

				return latest || '未知时间';
			}
		},
		onLoad() {
			this.initScrollHeight();
			this.loadFoodList();
		},
		onPullDownRefresh() {
			// 下拉刷新
			this.refreshData();
		},
		methods: {
			// 初始化滚动区域高度
			initScrollHeight() {
				const systemInfo = uni.getSystemInfoSync();
				// 屏幕高度 - 头部
				this.scrollHeight = systemInfo.windowHeight - 170;
			},

			// 获取空食物对象
			getEmptyFood() {
				return {
					fID: null,
					fImg: '',
					fType: '',
					fName: '',
					fEdibleSection: '',
					fEnergy: '',
					fCarbonWater: '',
					fFat: '',
					fProtein: '',
					fDietaryFiber: '',
					fVitaminA: '',
					fVitaminC: '',
					fVitaminD: '',
					fVitaminE: '',
					fVitaminK: '',
					fVitaminB1: '',
					fVitaminB2: '',
					fVitaminB3: '',
					fVitaminB6: '',
					fVitaminB9: '',
					fVitaminB12: '',
					fK: '',
					fNa: '',
					fCa: '',
					fZn: '',
					fSe: '',
					fFe: '',
					fRole: '',
					fNote: '',
					fCreateTime: '',
					fUpdateTime: ''
				}
			},

			// 加载食物列表
			async loadFoodList() {
				try {
					this.loading = true;
					const res = await foodApi._getFoodNutritionAll();

					if (res && Array.isArray(res)) {
						this.foodList = res;

						// 初始显示数据（第一页）
						this.resetPagination();
						this.filteredFoods = [...this.foodList];
						this.applySortAndFilter();
						setTimeout(() => {
							const startIndex = this.currentPage * this.pageSize;
							const endIndex = startIndex + this.pageSize;

							if (startIndex >= this.filteredFoods.length) {
								this.hasMore = false;
								this.loadingMore = false;
								return;
							}

							const moreData = this.filteredFoods.slice(startIndex, endIndex);
							this.displayFoods = [...this.displayFoods, ...moreData];
							this.currentPage++;
							this.hasMore = endIndex < this.filteredFoods.length;
							this.loadingMore = false;
						}, 300);

						// 提取所有类别
						this.extractCategories();
					} else {
						uni.showToast({
							title: '数据格式错误',
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('加载食物列表失败:', error);
					uni.showToast({
						title: '加载失败，请检查网络',
						icon: 'none'
					});
				} finally {
					this.loading = false;
				}
			},

			// 刷新数据
			async refreshData() {
				try {
					const res = await foodApi._getFoodNutritionAll();

					if (res && Array.isArray(res)) {
						this.foodList = res;
						this.resetPagination();
						this.filteredFoods = [...this.foodList];
						this.applySortAndFilter();
						this.loadNextPage();
						this.extractCategories();

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

			// 提取所有类别
			extractCategories() {
				const categoriesSet = new Set();
				this.filteredFoods.forEach(food => {
					if (food.fType) {
						categoriesSet.add(food.fType);
					}
				});
				this.categories = Array.from(categoriesSet);
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
					this.loadFoodList();
					return;
				}

				try {
					// 尝试后端搜索
					const res = await foodApi._getFoodNutritionByFactor(keyword);

					if (res) {
						this.foodList = res;
					} else {
						// 如果后端搜索失败，使用前端搜索
						this.frontendSearch(keyword);
					}
				} catch (error) {
					console.error('搜索失败，使用前端搜索:', error);
					// 使用前端搜索作为备用
					this.frontendSearch(keyword);
				}

				// 重置分页并加载第一页
				this.resetPagination();
				this.applySortAndFilter();
				this.loadNextPage();
				this.extractCategories();
			},

			// 前端搜索
			frontendSearch(keyword) {
				this.filteredFoods = this.foodList.filter(food => {
					return (
						(food.fName && food.fName.toLowerCase().includes(keyword)) ||
						(food.fType && food.fType.toLowerCase().includes(keyword)) ||
						(food.fRole && food.fRole.toLowerCase().includes(keyword)) ||
						(food.fNote && food.fNote.toLowerCase().includes(keyword)) ||
						(food.fID && food.fID.toString().includes(keyword))
					);
				});
			},

			// 清除搜索
			clearSearch() {
				this.searchKeyword = '';
				this.loadFoodList();
			},

			// 设置分类筛选
			setCategory(category) {
				this.activeCategory = category;
				this.applySortAndFilter();
				this.resetPagination();
				this.loadNextPage();
				this.extractCategories();
			},

			// 应用排序和筛选
			applySortAndFilter() {
				let filtered = this.filteredFoods;
				const keyword = this.searchKeyword.toLowerCase().trim();

				// 分类筛选
				if (this.activeCategory !== '全部') {
					filtered = this.foodList.filter(food => food.fType === this.activeCategory);
				} else {
					filtered = this.foodList
				}
				// 排序
				this.filteredFoods = filtered.sort((a, b) => {
					let valueA = a[this.sortBy];
					let valueB = b[this.sortBy];

					// 处理数字类型的比较
					if (['fEnergy', 'fProtein', 'fFat', 'fCarbonWater'].includes(this.sortBy)) {
						valueA = parseFloat(valueA) || 0;
						valueB = parseFloat(valueB) || 0;
					}
					if (this.sortOrder === 'asc') {
						return valueA > valueB ? 1 : -1;
					} else {
						return valueA < valueB ? 1 : -1;
					}
				});
			},

			// 重置分页
			resetPagination() {
				this.currentPage = 0;
				this.displayFoods = [];
				this.hasMore = this.filteredFoods.length > 0;
			},

			// 加载下一页
			loadNextPage() {
				if (this.loadingMore || !this.hasMore) return;

				this.loadingMore = true;

				// 网络延迟加载
				setTimeout(() => {
					const startIndex = this.currentPage * this.pageSize;
					const endIndex = startIndex + this.pageSize;

					if (startIndex >= this.filteredFoods.length) {
						this.hasMore = false;
						this.loadingMore = false;
						return;
					}

					const moreData = this.filteredFoods.slice(startIndex, endIndex);
					this.displayFoods = [...this.displayFoods, ...moreData];
					this.currentPage++;
					this.hasMore = endIndex < this.filteredFoods.length;
					this.loadingMore = false;
				}, 300);
			},

			// 加载更多数据（滚动到底部触发）
			loadMoreData() {
				if (!this.loading && !this.loadingMore && this.hasMore) {
					this.loadNextPage();
				}
			},

			// 设置排序
			setSort(sortBy) {
				if (this.sortBy === sortBy) {
					// 切换排序顺序
					this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc';
				} else {
					this.sortBy = sortBy;
					this.sortOrder = 'asc';
				}

				this.applySortAndFilter();
				this.resetPagination();
				this.loadNextPage();
			},

			// 查看食物详情
			viewFoodDetail(food) {
				uni.showModal({
					title: food.fName,
					content: this.getFoodDetailText(food),
					showCancel: false,
					confirmText: '关闭'
				});
			},

			// 获取食物详情文本
			getFoodDetailText(food) {
				let text = `类别：${food.fType || '未分类'}\n`;
				text += `可食部：${food.fEdibleSection || '未知'}%\n`;
				text += `热量：${food.fEnergy || 0} 千卡/100g\n`;
				text += `蛋白质：${food.fProtein || 0}g\n`;
				text += `脂肪：${food.fFat || 0}g\n`;
				text += `碳水：${food.fCarbonWater || 0}g\n`;
				text += `膳食纤维：${food.fDietaryFiber || 0}g\n`;

				if (food.fRole) {
					text += `\n主要作用：${food.fRole}\n`;
				}

				if (food.fNote) {
					text += `\n备注：${food.fNote}\n`;
				}

				return text;
			},

			// 打开添加弹窗
			openAddDialog() {
				this.isEditing = false;
				this.currentFood = this.getEmptyFood();
				this.$refs.foodDialog.open();
			},

			// 打开编辑弹窗
			openEditDialog(food) {
				this.isEditing = true;
				this.currentFood = {
					...food
				};
				this.$refs.foodDialog.open();
			},

			// 关闭弹窗
			closeDialog() {
				this.$refs.foodDialog.close();
			},

			// 保存食物（添加或编辑）
			async saveFood() {
				const name = this.currentFood.fName?.trim();
				if (!name){
					uni.showToast({
						title: '请输入食物名称',
						icon: 'none'
					});
					return;
				} 
				const type = this.currentFood.fType?.trim();
				if (!type){
					uni.showToast({
						title: '请输入食物类别',
						icon: 'none'
					});
					return;
				} 
				const edibleSection = this.currentFood.fEdibleSection?.trim();
				if (!edibleSection){
					uni.showToast({
						title: '请输入可食部分',
						icon: 'none'
					});
					return;
				} 

				try {
					uni.showLoading({
						title: '保存中...'
					});

					// 处理数值字段，确保是字符串格式
					const foodData = this.prepareFoodData(this.currentFood);

					let res;
					if (this.isEditing) {
						foodData.op = 'upd';
						// 编辑食物
						console.log(foodData)
						res = await foodApi._operationFoodNutrition(foodData);

						if (res && res.status === 200) {
							// 更新本地列表
							const index = this.foodList.findIndex(f => f.fID === foodData.fID);
							if (index !== -1) {
								this.foodList[index] = {
									...foodData
								};
								// 重新应用筛选和排序
								this.applySortAndFilter();
								this.resetPagination();
								this.loadNextPage();
								this.extractCategories();
							}
							uni.showToast({
								title: '食物更新成功',
								icon: 'success'
							});
							this.closeDialog();
						}
					} else {
						foodData.op = 'add';
						// 添加食物
						res = await foodApi._operationFoodNutrition(foodData);
						console.log(res)

						if (res && res.status === 200) {
							this.loadFoodList();
							uni.showToast({
								title: '食物添加成功',
								icon: 'success'
							});
							this.closeDialog();
						}
					}
				} catch (error) {
					console.error('保存食物失败:', error);
					uni.showToast({
						title: '保存失败: ' + (error.message || '未知错误'),
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			// 准备食物数据
			prepareFoodData(food) {
				const data = {
					...food
				};

				// 确保所有字段都是字符串
				const numberFields = [
					'fEnergy', 'fCarbonWater', 'fFat', 'fProtein', 'fDietaryFiber',
					'fVitaminA', 'fVitaminC', 'fVitaminD', 'fVitaminE', 'fVitaminK',
					'fVitaminB1', 'fVitaminB2', 'fVitaminB3', 'fVitaminB6', 'fVitaminB9', 'fVitaminB12',
					'fK', 'fNa', 'fCa', 'fZn', 'fSe', 'fFe'
				];

				numberFields.forEach(field => {
					if (data[field] !== null && data[field] !== undefined) {
						data[field] = String(data[field]);
					} else {
						data[field] = '';
					}
				});

				return data;
			},

			// 确认删除
			confirmDelete(food) {
				this.deleteFood = food;
				this.$refs.deleteDialog.open();
			},

			// 取消删除
			cancelDelete() {
				this.deleteFood = null;
				this.$refs.deleteDialog.close();
			},

			// 执行删除
			async deleteFoodConfirm() {
				if (!this.deleteFood) return;

				try {
					uni.showLoading({
						title: '删除中...'
					});

					const res = await foodApi._delFoodNutritionById(this.deleteFood.fID);

					if (res && res.status === 200) {
						// 从本地列表中移除
						const index = this.foodList.findIndex(f => f.fID === this.deleteFood.fID);
						if (index !== -1) {
							this.foodList.splice(index, 1);
							// 重新应用筛选和排序
							this.applySortAndFilter();
							this.resetPagination();
							this.loadNextPage();
							this.extractCategories();
						}

						uni.showToast({
							title: '食物删除成功',
							icon: 'success'
						});
					}
				} catch (error) {
					console.error('删除食物失败:', error);
					uni.showToast({
						title: '删除失败: ' + (error.message || '未知错误'),
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
					this.$refs.deleteDialog.close();
					this.deleteFood = null;
				}
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
	}
</script>

<style lang="scss">
	@import "@/scss/variables.scss";

	.food-manage-page {
		min-height: 100vh;
		background: #f5f7fa;
		padding: 0 24rpx 10rpx;

		/* 头部 */
		.header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 40rpx 0 30rpx;

			.header-left {
				.title {
					font-size: 40rpx;
					font-weight: 600;
					color: #333;
					display: block;
				}
			}

			.header-right {
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

					&:active {
						opacity: 0.8;
					}
				}
			}
		}

		/* 搜索区域 */
		.search-section {
			background: #fff;
			border-radius: 16rpx;
			padding: 24rpx;
			margin-bottom: 20rpx;
			box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

			.search-input {
				display: flex;
				align-items: center;
				border: 2rpx solid #e8e8e8;
				border-radius: 40rpx;
				padding: 20rpx 24rpx;
				background: #f8f9fa;
				margin-bottom: 24rpx;

				.input {
					flex: 1;
					margin: 0 16rpx;
					font-size: 28rpx;
					background: transparent;
				}

				.clear-icon {
					padding: 4rpx;

					&:active {
						opacity: 0.6;
					}
				}
			}

			.filter-options {
				.filter-row {
					margin-bottom: 20rpx;

					&:last-child {
						margin-bottom: 0;
					}

					.filter-label {
						font-size: 28rpx;
						color: #333;
						font-weight: 500;
						margin-bottom: 16rpx;
						display: block;
					}

					.category-scroll {
						white-space: nowrap;

						.category-tags {
							display: inline-flex;
							gap: 16rpx;
							padding-bottom: 8rpx;

							.category-tag {
								padding: 12rpx 24rpx;
								background: #f0f2f5;
								border-radius: 40rpx;
								font-size: 26rpx;
								color: #666;

								&.active {
									background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
									color: #fff;
								}

								&:active {
									opacity: 0.8;
								}
							}
						}
					}

					.sort-options {
						display: flex;
						gap: 16rpx;

						.sort-btn {
							flex: 1;
							padding: 0rpx;
							background: #f0f2f5;
							color: #666;
							font-size: 26rpx;
							border-radius: 20rpx;
							display: flex;
							align-items: center;
							justify-content: center;
							gap: 8rpx;

							&::after {
								border: none;
							}

							&:active {
								background: #e8e8e8;
							}

							&.active {
								background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
								color: #fff;
							}
						}
					}
				}
			}
		}

		/* 食物列表滚动区域 */
		.food-list-scroll {
			margin-bottom: 100rpx;
			border-radius: 16rpx;

			/* 骨架屏 */
			.skeleton-container {
				padding: 20rpx;

				.skeleton-item {
					background: #fff;
					border-radius: 16rpx;
					padding: 32rpx;
					margin-bottom: 20rpx;
					display: flex;
					align-items: center;
					gap: 24rpx;
					box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);

					.skeleton-image {
						width: 100rpx;
						height: 100rpx;
						border-radius: 12rpx;
						background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
						background-size: 200% 100%;
						animation: loading 1.5s infinite;
					}

					.skeleton-content {
						flex: 1;

						.skeleton-title {
							width: 60%;
							height: 32rpx;
							background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
							background-size: 200% 100%;
							animation: loading 1.5s infinite;
							border-radius: 8rpx;
							margin-bottom: 16rpx;
						}

						.skeleton-text {
							width: 80%;
							height: 24rpx;
							background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
							background-size: 200% 100%;
							animation: loading 1.5s infinite;
							border-radius: 6rpx;
							margin-bottom: 8rpx;

							&.short {
								width: 40%;
							}
						}
					}
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

			.empty-state {
				padding: 100rpx 0;
				display: flex;
				flex-direction: column;
				align-items: center;
				gap: 20rpx;

				.empty-text {
					font-size: 32rpx;
					color: #666;
				}

				.empty-subtext {
					font-size: 26rpx;
					color: #999;
				}
			}

			.food-item {
				background: #fff;
				border-radius: 16rpx;
				padding: 32rpx;
				margin-bottom: 20rpx;
				box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
				transition: all 0.3s ease;

				&:active {
					transform: scale(0.99);
					box-shadow: 0 1rpx 6rpx rgba(0, 0, 0, 0.08);
				}

				.food-header {
					display: flex;
					align-items: center;
					gap: 24rpx;
					margin-bottom: 24rpx;

					.food-image {
						width: 100rpx;
						height: 100rpx;
						border-radius: 12rpx;
						overflow: hidden;
						flex-shrink: 0;

						&.placeholder {
							background: #f8f9fa;
							display: flex;
							align-items: center;
							justify-content: center;
							border: 2rpx dashed #e8e8e8;
						}

						.food-img {
							width: 100%;
							height: 100%;
						}
					}

					.food-basic-info {
						flex: 1;

						.food-name-row {
							display: flex;
							justify-content: space-between;
							align-items: center;
							margin-bottom: 12rpx;

							.food-name {
								font-size: 32rpx;
								font-weight: 600;
								color: #333;
								flex: 1;
								overflow: hidden;
								text-overflow: ellipsis;
								white-space: nowrap;
							}

							.food-id {
								font-size: 24rpx;
								color: #999;
								background: #f5f7fa;
								padding: 4rpx 12rpx;
								border-radius: 12rpx;
								margin-left: 16rpx;
								flex-shrink: 0;
							}
						}

						.food-category {
							.category-tag {
								display: inline-block;
								padding: 6rpx 16rpx;
								background: rgba(24, 144, 255, 0.1);
								color: #1890ff;
								font-size: 24rpx;
								border-radius: 20rpx;
							}
						}
					}
				}

				.nutrition-summary {
					background: #f8f9fa;
					border-radius: 12rpx;
					padding: 20rpx;
					display: flex;
					justify-content: space-between;
					align-items: center;
					margin-bottom: 24rpx;

					.nutrition-item {
						flex: 1;
						text-align: center;

						.nutrition-label {
							font-size: 24rpx;
							color: #666;
							display: block;
							margin-bottom: 4rpx;
						}

						.nutrition-value {
							font-size: 26rpx;
							font-weight: 600;
							color: #333;
							display: block;
						}
					}

					.nutrition-divider {
						width: 1rpx;
						height: 40rpx;
						background: #e8e8e8;
					}
				}

				.food-actions {
					display: flex;
					gap: 16rpx;

					.action-btn {
						flex: 1;
						padding: 16rpx;
						font-size: 26rpx;
						border-radius: 20rpx;
						display: flex;
						align-items: center;
						justify-content: center;
						gap: 6rpx;

						&::after {
							border: none;
						}

						&.edit {
							background: rgba(24, 144, 255, 0.1);
							color: #1890ff;

							&:active {
								background: rgba(24, 144, 255, 0.2);
							}
						}

						&.delete {
							background: rgba(255, 68, 68, 0.1);
							color: #ff4444;

							&:active {
								background: rgba(255, 68, 68, 0.2);
							}
						}
					}
				}
			}

			/* 加载更多 */
			.load-more {
				padding: 30rpx 0;
				text-align: center;
			}

			.load-more-tip {
				padding: 30rpx 0;
				text-align: center;
				display: flex;
				flex-direction: column;
				align-items: center;
				gap: 8rpx;

				text {
					font-size: 26rpx;
					color: #999;
				}

				.no-more {
					font-size: 24rpx;
					color: #ccc;
				}
			}
		}

		/* 添加/编辑弹窗 */
		.dialog-content {
			background: #fff;
			border-radius: 24rpx;
			width: 90vw;
			max-width: 700rpx;
			max-height: 80vh;
			display: flex;
			flex-direction: column;

			.dialog-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				padding: 20rpx 20rpx 20rpx;
				border-bottom: 2rpx solid #f0f0f0;

				.dialog-title {
					font-size: 36rpx;
					font-weight: 600;
					color: #333;
				}

				.close-btn {
					padding: 0rpx 0rpx 0rpx 380rpx;
					background: transparent;

					&::after {
						border: none;
					}

					&:active {
						opacity: 0.6;
					}
				}
			}

			.form-scroll {
				flex: 1;
				max-height: 60vh;

				.form-content {
					padding: 20rpx 40rpx;
				}
			}

			.form-section {
				margin-bottom: 40rpx;

				&:last-child {
					margin-bottom: 20rpx;
				}

				.section-title {
					font-size: 30rpx;
					font-weight: 600;
					color: #333;
					display: block;
					margin-bottom: 24rpx;
					padding-bottom: 12rpx;
					border-bottom: 2rpx solid #f0f0f0;
				}
			}

			.form-item {
				margin-bottom: 24rpx;

				&:last-child {
					margin-bottom: 0;
				}

				.form-label {
					font-size: 28rpx;
					font-weight: 500;
					color: #333;
					display: block;
					margin-bottom: 12rpx;

					&::after {
						content: '*';
						color: #ff4444;
						margin-left: 4rpx;
					}
				}

				.form-input {
					border: 2rpx solid #e8e8e8;
					border-radius: 12rpx;
					padding: 20rpx;
					font-size: 28rpx;
					background: #f8f9fa;

					&:focus {
						border-color: #1890ff;
						background: #fff;
					}

					&.small {
						padding: 12rpx;
						font-size: 26rpx;
						text-align: center;
					}

					&.tiny {
						padding: 8rpx;
						font-size: 24rpx;
						text-align: center;
					}
				}

				.form-textarea {
					border: 2rpx solid #e8e8e8;
					border-radius: 12rpx;
					padding: 20rpx;
					font-size: 28rpx;
					min-height: 120rpx;
					background: #f8f9fa;

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
			}

			.nutrition-grid {
				display: grid;
				grid-template-columns: repeat(2, 1fr);
				gap: 20rpx;

				.nutrition-input {
					.nutrition-label {
						font-size: 26rpx;
						color: #666;
						display: block;
						margin-bottom: 8rpx;
					}
				}
			}

			.vitamin-grid {
				display: grid;
				grid-template-columns: repeat(3, 1fr);
				gap: 16rpx;

				.vitamin-input {
					.vitamin-label {
						font-size: 24rpx;
						color: #666;
						display: block;
						margin-bottom: 6rpx;
						white-space: nowrap;
					}
				}
			}

			.mineral-grid {
				display: grid;
				grid-template-columns: repeat(3, 1fr);
				gap: 16rpx;

				.mineral-input {
					.mineral-label {
						font-size: 24rpx;
						color: #666;
						display: block;
						margin-bottom: 6rpx;
					}
				}
			}

			.dialog-actions {
				display: flex;
				gap: 24rpx;
				padding: 20rpx 40rpx 40rpx;
				border-top: 2rpx solid #f0f0f0;

				.dialog-btn {
					flex: 1;
					padding: 10rpx;
					font-size: 30rpx;
					font-weight: 500;
					border-radius: 40rpx;

					&::after {
						border: none;
					}

					&.cancel {
						background: #f5f7fa;
						color: #666;

						&:active {
							background: #e8e8e8;
						}
					}

					&.confirm {
						background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
						color: #fff;

						&:disabled {
							opacity: 0.5;
						}

						&:active:not(:disabled) {
							opacity: 0.8;
						}
					}
				}
			}
		}

		/* 删除确认弹窗 */
		.delete-dialog {
			background: #fff;
			border-radius: 24rpx;
			padding: 48rpx 40rpx;
			width: 80vw;
			max-width: 500rpx;
			text-align: center;

			.delete-title {
				font-size: 36rpx;
				font-weight: 600;
				color: #333;
				display: block;
				margin: 20rpx 0 12rpx;
			}

			.delete-content {
				font-size: 30rpx;
				color: #666;
				display: block;
				margin-bottom: 8rpx;
			}

			.delete-warning {
				font-size: 26rpx;
				color: #ff4444;
				display: block;
				margin-bottom: 40rpx;
			}

			.delete-actions {
				display: flex;
				gap: 24rpx;

				.delete-btn {
					flex: 1;
					padding: 20rpx;
					font-size: 30rpx;
					font-weight: 500;
					border-radius: 40rpx;

					&::after {
						border: none;
					}

					&.cancel {
						background: #f5f7fa;
						color: #666;

						&:active {
							background: #e8e8e8;
						}
					}

					&.confirm {
						background: #ff4444;
						color: #fff;

						&:active {
							background: #d9363e;
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
	}
</style>