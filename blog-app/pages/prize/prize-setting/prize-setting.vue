<template>
	<view class="prize-container">
		<!-- 背景渐变 -->
		<view class="background-gradient"></view>

		<!-- 页面内容 -->
		<view class="content-wrapper">
			<scroll-view class="main-content" scroll-y :show-scrollbar="false" refresher-background="transparent"
				:style="{ height: '100%' }">
				<view class="container">
					<!-- 配置表单 -->
					<view class="form-container">
						<view class="form-row">
							<view class="form-item">
								<text class="label">奖品名称</text>
								<input v-model="form.name" class="form-input" placeholder="请输入奖品名称"
									placeholder-style="color: #c0c4cc;" />
							</view>

							<view class="form-item">
								<text class="label">奖品数量</text>
								<view class="number-input">
									<text class="number-btn" @tap="decreaseNumber">-</text>
									<text class="number-value">{{ form.number }}</text>
									<text class="number-btn" @tap="increaseNumber">+</text>
								</view>
							</view>

							<view class="form-item">
								<text class="label">权重值</text>
								<view class="number-input">
									<text class="number-btn" @tap="decreaseWeight">-</text>
									<text class="number-value">{{ form.weight }}</text>
									<text class="number-btn" @tap="increaseWeight">+</text>
								</view>
							</view>
						</view>

						<view class="button-group">
							<button class="btn primary" @tap="addPrize">添加奖品</button>
							<button class="btn success" @tap="resetForm">重置表单</button>
							<button class="btn warning" @tap="clearAll">清空列表</button>
						</view>
					</view>

					<!-- 奖品列表 -->
					<view class="table-container">
						<scroll-view class="table-scroll" scroll-x="true" scroll-y="true"
							:style="{height: screenHeight + 'px'}">
							<view class="table-wrapper" :style="{width: tableWidth + 'px'}">
								<!-- 表头 -->
								<view class="table-header">
									<view class="table-cell" v-for="(header, index) in tableHeaders" :key="index"
										:style="{width: header.width + 'px', 'min-width': header.width + 'px'}">
										{{ header.label }}
									</view>
								</view>

								<!-- 表格内容 -->
								<view class="table-body">
									<view class="table-row" v-for="(item, index) in pagination.currentTableData"
										:key="item.id">
										<view class="table-cell"
											:style="{width: tableHeaders[0].width + 'px', 'min-width': tableHeaders[0].width + 'px'}">
											<input v-model="item.name" class="table-input" placeholder="奖品名称"
												@blur="updatePrize(item)" />
										</view>
										<view class="table-cell"
											:style="{width: tableHeaders[1].width + 'px', 'min-width': tableHeaders[1].width + 'px'}">
											<view class="number-input small">
												<text class="number-btn small" @tap="decreaseItemNumber(item)">-</text>
												<text class="number-value small">{{ item.number }}</text>
												<text class="number-btn small" @tap="increaseItemNumber(item)">+</text>
											</view>
										</view>
										<view class="table-cell"
											:style="{width: tableHeaders[2].width + 'px', 'min-width': tableHeaders[2].width + 'px'}">
											<view class="number-input small">
												<text class="number-btn small" @tap="decreaseItemWeight(item)">-</text>
												<text class="number-value small">{{ item.weight }}</text>
												<text class="number-btn small" @tap="increaseItemWeight(item)">+</text>
											</view>
										</view>
										<view class="table-cell"
											:style="{width: tableHeaders[3].width + 'px', 'min-width': tableHeaders[3].width + 'px'}">
											{{ calcProbability(item) }}%
										</view>
										<view class="table-cell"
											:style="{width: tableHeaders[4].width + 'px', 'min-width': tableHeaders[4].width + 'px'}">
											<text class="delete-btn" @tap="deletePrize(item)">删除</text>
										</view>
									</view>
								</view>
							</view>
						</scroll-view>
					</view>

					<!-- 分页 -->
					<view class="pagination">
						<view class="pagination-info">
							共 {{ pagination.total }} 条记录
						</view>
						<view class="pagination-controls">
							<text class="page-btn" :class="{disabled: pagination.currentPage === 1}"
								@tap="prevPage">上一页</text>
							<text class="page-info">第 {{ pagination.currentPage }} 页 / 共 {{ pagination.totalPage }}
								页</text>
							<text class="page-btn" :class="{disabled: pagination.currentPage >= pagination.totalPage}"
								@tap="nextPage">下一页</text>
						</view>
						<view class="page-size-selector">
							<text>每页显示：</text>
							<picker @change="onPageSizeChange" :value="pageSizeIndex" :range="pagination.pageSizes">
								<view class="picker">{{ pagination.pageSize }} 条</view>
							</picker>
						</view>
					</view>

					<!-- 统计信息 -->
					<view class="stats">
						<text class="total-weight">总权重：{{ totalWeight }}</text>
						<button class="save-btn" @tap="saveConfig">保存配置</button>
					</view>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "prizeSetting",
		data() {
			return {
				screenHeight: 500,
				form: {
					id: '',
					name: '',
					number: 1,
					weight: 1
				},
				// 分页数据
				pagination: {
					listData: [], // 总数据
					currentPage: 1, // 当前页
					pageSizes: [5, 10, 20],
					pageSize: 10, // 每页显示条数
					totalPage: 1, // 总页数
					currentTableData: [], // 当前页数据
					total: 0 // 总数量
				},
				// 表格配置
				tableHeaders: [{
						label: '奖品名称',
						width: 200
					},
					{
						label: '奖品数量',
						width: 150
					},
					{
						label: '权重',
						width: 150
					},
					{
						label: '中奖概率',
						width: 150
					},
					{
						label: '操作',
						width: 150
					}
				],
				tableWidth: 800 // 所有列宽总和
			};
		},
		computed: {
			totalWeight() {
				return this.pagination.listData.reduce((sum, item) => sum + item.weight, 0);
			},
			pageSizeIndex() {
				return this.pagination.pageSizes.indexOf(this.pagination.pageSize);
			}
		},
		onLoad() {
			this.updateScreenHeight();
			this.generateId();
			this.loadConfig();
		},
		onReady() {
			// 组件挂载时获取屏幕高度
			this.updateScreenHeight();
		},
		methods: {
			updateScreenHeight() {
				// 获取屏幕高度并计算表格高度
				const systemInfo = uni.getSystemInfoSync();
				this.screenHeight = systemInfo.windowHeight - 400;
			},

			// 生成唯一ID
			generateId() {
				this.form.id = Date.now().toString() + Math.random().toString(36).substr(2, 9);
			},

			// 表单数字控制
			decreaseNumber() {
				if (this.form.number > 1) this.form.number--;
			},
			increaseNumber() {
				if (this.form.number < 1000) this.form.number++;
			},
			decreaseWeight() {
				if (this.form.weight > 1) this.form.weight--;
			},
			increaseWeight() {
				if (this.form.weight < 1000) this.form.weight++;
			},

			// 列表项数字控制
			decreaseItemNumber(item) {
				if (item.number > 1) item.number--;
				this.updatePrize(item);
			},
			increaseItemNumber(item) {
				if (item.number < 1000) item.number++;
				this.updatePrize(item);
			},
			decreaseItemWeight(item) {
				if (item.weight > 1) item.weight--;
				this.updatePrize(item);
			},
			increaseItemWeight(item) {
				if (item.weight < 1000) item.weight++;
				this.updatePrize(item);
			},

			// 添加奖品
			addPrize() {
				if (!this.form.name || this.form.name.trim() === '') {
					uni.showToast({
						title: '请输入奖品名称',
						icon: 'none'
					});
					return;
				}

				const newRecord = {
					...this.form
				};

				this.pagination.listData.push(newRecord);
				this.pagination.total = this.pagination.listData.length;
				this.updatePagination();

				// 重置表单
				this.form = {
					id: '',
					name: '',
					number: 1,
					weight: 1
				};
				this.generateId();

				uni.showToast({
					title: '添加成功',
					icon: 'success'
				});
			},

			// 更新奖品
			updatePrize(item) {
				// 这里可以添加保存到本地存储的逻辑
				this.saveToLocalStorage();
			},

			// 删除奖品
			deletePrize(item) {
				uni.showModal({
					title: '确认删除',
					content: '确定要删除这个奖品吗？',
					success: (res) => {
						if (res.confirm) {
							this.pagination.listData = this.pagination.listData.filter(
								prize => prize.id !== item.id
							);
							this.pagination.total = this.pagination.listData.length;
							this.updatePagination();

							// 如果当前页没有数据且不是第一页，返回上一页
							if (
								this.pagination.currentTableData.length === 0 &&
								this.pagination.listData.length > 0 &&
								this.pagination.currentPage > 1
							) {
								this.pagination.currentPage--;
								this.updateCurrentTableData();
							}

							this.saveToLocalStorage();

							uni.showToast({
								title: '删除成功',
								icon: 'success'
							});
						}
					}
				});
			},

			// 计算单个奖品概率
			calcProbability(item) {
				if (this.totalWeight === 0) return 0;
				const probability = ((item.weight / this.totalWeight) * 100).toFixed(2);
				item.calc = probability;
				return probability;
			},

			// 重置表单
			resetForm() {
				this.form = {
					id: '',
					name: '',
					number: 1,
					weight: 1
				};
				this.generateId();
				uni.showToast({
					title: '表单已重置',
					icon: 'success'
				});
			},

			// 清空列表
			clearAll() {
				if (this.pagination.listData.length === 0) {
					uni.showToast({
						title: '列表已为空',
						icon: 'none'
					});
					return;
				}

				uni.showModal({
					title: '确认清空',
					content: '确定要清空所有奖品吗？此操作不可恢复',
					success: (res) => {
						if (res.confirm) {
							this.pagination.listData = [];
							this.pagination.total = 0;
							this.pagination.currentPage = 1;
							this.updateCurrentTableData();
							this.saveToLocalStorage();

							uni.showToast({
								title: '已清空',
								icon: 'success'
							});
						}
					}
				});
			},

			// 保存配置
			saveConfig() {
				if (this.pagination.listData.length === 0) {
					uni.showToast({
						title: '没有数据可保存',
						icon: 'none'
					});
					return;
				}

				this.saveToLocalStorage();

				// 在实际项目中，这里可以调用API保存数据到服务器
				uni.showToast({
					title: '配置已保存至本地',
					icon: 'none'
				});
			},

			// 保存到本地存储
			saveToLocalStorage() {
				try {
					uni.setStorageSync('prizeConfig', JSON.stringify(this.pagination.listData));
				} catch (error) {
					console.error('保存到本地存储失败:', error);
				}
			},

			// 加载配置
			loadConfig() {
				try {
					const data = uni.getStorageSync('prizeConfig');
					if (data) {
						let res = JSON.parse(data);
						this.pagination.listData = res;
						this.pagination.total = res.length;
						this.updatePagination();
					}
				} catch (error) {
					console.error('加载配置失败:', error);
				}
			},

			// 分页方法
			updatePagination() {
				this.pagination.totalPage = Math.ceil(this.pagination.listData.length / this.pagination.pageSize) || 1;
				this.updateCurrentTableData();
			},

			updateCurrentTableData() {
				const startIndex = (this.pagination.currentPage - 1) * this.pagination.pageSize;
				const endIndex = startIndex + this.pagination.pageSize;
				this.pagination.currentTableData = this.pagination.listData.slice(startIndex, endIndex);
			},

			prevPage() {
				if (this.pagination.currentPage > 1) {
					this.pagination.currentPage--;
					this.updateCurrentTableData();
				}
			},

			nextPage() {
				if (this.pagination.currentPage < this.pagination.totalPage) {
					this.pagination.currentPage++;
					this.updateCurrentTableData();
				}
			},

			onPageSizeChange(e) {
				const newSize = this.pagination.pageSizes[e.detail.value];
				this.pagination.pageSize = newSize;
				this.updatePagination();
			}
		}
	};
</script>

<style lang="scss" scoped>
	@import '@/scss/variables';
	
	.prize-container {
		min-height: 100vh;
		background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
		position: relative;
	}

	.background-gradient {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		height: 100%;
		background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
		z-index: 1;
	}

	.content-wrapper {
		position: relative;
		z-index: 2;
		box-sizing: border-box;
		overflow: hidden;
	}

	.main-content {
		box-sizing: border-box;
		overflow: hidden;
		background: transparent;
		padding: 0 30rpx;
	}

	.container {
		background-color: #f5f5f5;
	}

	.form-container {
		background-color: #fff;
		border-radius: 10rpx;
		padding: 20rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.form-row {
		display: flex;
		flex-wrap: wrap;
		margin-bottom: 20rpx;
	}

	.form-item {
		flex: 1;
		min-width: 200rpx;
		margin-right: 20rpx;
		margin-bottom: 20rpx;
	}

	.label {
		display: block;
		margin-bottom: 10rpx;
		font-size: 28rpx;
		color: #333;
		font-weight: bold;
	}

	.form-input {
		padding: 20rpx;
		border: 1rpx solid #ddd;
		border-radius: 8rpx;
		background-color: #f9f9f9;
		font-size: 28rpx;
	}

	.number-input {
		display: flex;
		align-items: center;
		border: 1rpx solid #ddd;
		border-radius: 8rpx;
		overflow: hidden;
		background-color: #f9f9f9;
	}

	.number-input.small {
		max-width: 180rpx;
		margin: 0 auto;
	}

	.number-btn {
		flex: 1;
		padding: 20rpx;
		text-align: center;
		background-color: #f0f0f0;
		font-size: 28rpx;
		font-weight: bold;
	}

	.number-btn.small {
		padding: 15rpx;
		font-size: 24rpx;
	}

	.number-value {
		flex: 2;
		padding: 20rpx;
		text-align: center;
		background-color: #fff;
		font-size: 28rpx;
	}

	.number-value.small {
		padding: 15rpx;
		font-size: 24rpx;
	}

	.button-group {
		display: flex;
		justify-content: space-between;
		margin-top: 20rpx;
	}

	.btn {
		flex: 1;
		margin: 0 8rpx;
		padding: 8rpx;
		border-radius: 8rpx;
		color: #fff;
		font-size: 28rpx;
		text-align: center;
		border: none;
		
		&:active {
			transform: scale(0.95);
		}
	}

	.primary {
		background-color: #007AFF;
	}

	.success {
		background-color: #4CD964;
	}

	.warning {
		background-color: #FF9500;
	}

	.table-container {
		background-color: #fff;
		border-radius: 10rpx;
		overflow: hidden;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.table-scroll {
		width: 100%;
	}

	.table-wrapper {
		min-width: 100%;
	}

	.table-header,
	.table-row {
		display: flex;
		border-bottom: 1rpx solid #eee;
		min-width: 100%;
	}

	.table-cell {
		padding: 20rpx 10rpx;
		text-align: center;
		font-size: 24rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		flex-shrink: 0;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.table-header .table-cell {
		font-weight: bold;
		background-color: #f5f5f5;
	}

	.table-input {
		border: 1rpx solid #ddd;
		border-radius: 6rpx;
		padding: 15rpx;
		font-size: 24rpx;
		text-align: center;
		width: 100%;
		background-color: #f9f9f9;
	}

	.delete-btn {
		color: #FF3B30;
		font-size: 24rpx;
		padding: 8rpx 16rpx;
		background: rgba(255, 59, 48, 0.1);
		border-radius: 6rpx;
	}

	.pagination {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
		padding: 20rpx;
		background-color: #fff;
		border-radius: 10rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.pagination-info {
		font-size: 28rpx;
		color: #606266;
		text-align: center;
	}

	.pagination-controls {
		display: flex;
		justify-content: center;
		align-items: center;
		gap: 30rpx;
	}

	.page-btn {
		padding: 15rpx 25rpx;
		border: 1rpx solid #007AFF;
		border-radius: 6rpx;
		color: #007AFF;
		font-size: 26rpx;
	}

	.page-btn.disabled {
		border-color: #ccc;
		color: #ccc;
	}

	.page-info {
		font-size: 26rpx;
		color: #606266;
	}

	.page-size-selector {
		display: flex;
		justify-content: center;
		align-items: center;
		gap: 15rpx;
		font-size: 26rpx;
		color: #606266;
	}

	.picker {
		padding: 10rpx 20rpx;
		border: 1rpx solid #dcdfe6;
		border-radius: 6rpx;
		background-color: #ffffff;
	}

	.stats {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.total-weight {
		color: #FFD700;
		font-size: 32rpx;
		font-weight: bold;
	}

	.save-btn {
		background-color: #4CD964;
		color: #fff;
		padding: 8rpx 40rpx;
		border-radius: 8rpx;
		border: none;
		font-size: 28rpx;
		
		&:active {
			transform: scale(0.95);
		}
	}

	/* 响应式布局 */
	@media (min-width: 768px) {
		.form-row {
			flex-wrap: nowrap;
		}

		.form-item {
			margin-bottom: 0;
		}

		.pagination {
			flex-direction: row;
			justify-content: space-between;
			align-items: center;
		}
	}
</style>