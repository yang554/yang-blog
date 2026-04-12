<template>
	<view class="container">
		<!-- 顶部卡片信息 - 两行布局 -->
		<view class="summary-section">
			<view class="section-title">
				<text class="iconfont icon-jibenxinxi"></text>
				<text class="title-text">基本信息</text>
			</view>
			<view class="summary-cards">
				<!-- 第一行 -->
				<view class="card-row">
					<view class="summary-card">
						<view class="card-icon" style="background: #e6f7ff;">
							<text class="iconfont icon-danju" style="color: #1890ff;"></text>
						</view>
						<view class="card-content">
							<view class="card-title">流水号</view>
							<view class="card-value">{{ dailyFlowEntity.id }}</view>
						</view>
					</view>
				</view>

				<!-- 第二行 -->
				<view class="card-row">
					<view class="summary-card">
						<view class="card-icon" style="background: #fff7e6;">
							<text class="iconfont icon-jine" style="color: #fa8c16;"></text>
						</view>
						<view class="card-content">
							<view class="card-title">总金额</view>
							<view class="card-value amount-highlight">￥{{ dailyFlowEntity.cost || '0.00' }}</view>
						</view>
					</view>

					<view class="summary-card">
						<view class="card-icon" :style="typeStyle">
							<text class="iconfont" :class="typeIcon" :style="typeIconStyle"></text>
						</view>
						<view class="card-content">
							<view class="card-title">收支类型</view>
							<view class="card-value">{{ typeText }}</view>
						</view>
					</view>
				</view>

				<!-- 第三行 -->
				<view class="card-row">
					<view class="summary-card">
						<view class="card-icon" style="background: #f9f0ff;">
							<text class="iconfont icon-riqi" style="color: #722ed1;"></text>
						</view>
						<view class="card-content">
							<view class="card-title">账单日期</view>
							<view class="card-value">
								<!-- 修改账单日期样式 -->
								<picker mode="date" :value="dailyFlowEntity.recordTime" @change="handleDateChange">
									<view class="date-picker">{{ dailyFlowEntity.recordTime || '选择日期' }}</view>
								</picker>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 操作信息 -->
		<view class="info-section">
			<view class="section-title">
				<text class="iconfont icon-caozuo"></text>
				<text class="title-text">操作信息</text>
			</view>
			<view class="info-grid">
				<view class="info-item">
					<text class="info-label">操作人</text>
					<text class="info-value">{{ dailyFlowEntity.uName || '未记录' }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">创建时间</text>
					<text class="info-value">{{ dailyFlowEntity.fCreateTime || '未记录' }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">更新时间</text>
					<text class="info-value">{{ dailyFlowEntity.fUpdateTime || '未记录' }}</text>
				</view>
			</view>
		</view>

		<!-- 操作按钮-->
		<view class="action-section">
			<view class="section-title">
				<text class="iconfont icon-anniu"></text>
				<text class="title-text">操作</text>
			</view>
			<view class="action-buttons">
				<button class="btn btn-warning" @click="handleReset">
					<text class="iconfont icon-zhongzhi"></text>
					<text class="btn-text">重置</text>
				</button>
				<button class="btn btn-primary" @click="handleAddBtn">
					<text class="iconfont icon-tianjia"></text>
					<text class="btn-text">添加行</text>
				</button>
				<button class="btn btn-success" @click="handleSaveBtn">
					<text class="iconfont icon-baocun"></text>
					<text class="btn-text">保存</text>
				</button>
			</view>
		</view>

		<!-- 明细列表 - 修改为表格样式 -->
		<view class="detail-section">
			<view class="section-header">
				<view class="section-title">
					<text class="iconfont icon-mingxi"></text>
					<text class="title-text">明细列表</text>
				</view>
				<view class="list-stats">
					<text class="stats-text">共 {{ detailList.length }} 条记录</text>
					<text class="stats-divider">|</text>
					<text class="stats-text">当前第 {{ pagination.currentPage }}/{{ pagination.totalPage }} 页</text>
				</view>
			</view>

			<!-- 表格容器 -->
			<view class="table-container">
				<scroll-view class="table-scroll" scroll-x="true" scroll-y="true" :style="{height: tableHeight + 'px'}">
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
							<view class="table-row" v-for="(item, index) in pagination.currentTableData" :key="item.id">
								<!-- 费用名目 -->
								<view class="table-cell"
									:style="{width: tableHeaders[0].width + 'px', 'min-width': tableHeaders[0].width + 'px'}">
									<input v-model="item.purpose" placeholder="输入费用名称" class="table-input"
										@input="getMatchSource(getGlobalIndex(index), item)" />
								</view>

								<!-- 金额 -->
								<view class="table-cell"
									:style="{width: tableHeaders[1].width + 'px', 'min-width': tableHeaders[1].width + 'px'}">
									<input v-model="item.cost" type="text" placeholder="0.00"
										class="table-input amount-input"
										@input="handleInput(getGlobalIndex(index), item)" />
								</view>

								<!-- 费用来源 -->
								<view class="table-cell"
									:style="{width: tableHeaders[2].width + 'px', 'min-width': tableHeaders[2].width + 'px'}">
									<picker @change="(e) => handleTypeChange(e, getGlobalIndex(index))"
										:value="typeTagIndex(item.typeTagID)" :range="tagList" range-key="name">
										<view class="table-picker">
											{{ item.typeTagID || '选择费用来源' }}
										</view>
									</picker>
								</view>

								<!-- 操作 -->
								<view class="table-cell"
									:style="{width: tableHeaders[3].width + 'px', 'min-width': tableHeaders[3].width + 'px'}">
									<text class="delete-btn" @tap="handleDelete(getGlobalIndex(index), item)">删除</text>
								</view>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>

			<!-- 分页控件 -->
			<view class="pagination-container" v-if="pagination.totalPage > 1">
				<view class="pagination-info">
					<text>每页 {{ pagination.pageSize }} 条</text>
				</view>
				<view class="pagination-controls">
					<button class="page-btn" :disabled="pagination.currentPage === 1"
						@click="handlePageChange(pagination.currentPage - 1)">
						<text class="iconfont icon-shangyiyehoutuifanhui-yuankuang"></text>
					</button>
					<view class="page-numbers">
						<text class="page-number current">{{ pagination.currentPage }}</text>
						<text class="page-divider">/</text>
						<text class="page-number total">{{ pagination.totalPage }}</text>
					</view>
					<button class="page-btn" :disabled="pagination.currentPage === pagination.totalPage"
						@click="handlePageChange(pagination.currentPage + 1)">
						<text class="iconfont icon-xiayiyeqianjinchakangengduo-yuankuang"></text>
					</button>
				</view>
			</view>

			<!-- 空状态 -->
			<view v-if="detailList.length === 0" class="empty-state">
				<text class="iconfont icon-wushuju"></text>
				<text class="empty-text">暂无明细数据</text>
				<button class="btn-add-first" @click="handleAddBtn">
					<text class="iconfont icon-tianjia"></text>
					<text>添加第一条明细</text>
				</button>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getTime,
		parseTime,
		parseDate
	} from '@/common/formatDate.js';
	import {
		lifeApi
	} from '@/api/index.js'

	// 防抖函数
	const debounce = (func, wait) => {
		let timeout;
		return function executedFunction(...args) {
			const later = () => {
				clearTimeout(timeout);
				func(...args);
			};
			clearTimeout(timeout);
			timeout = setTimeout(later, wait);
		};
	};

	export default {
		data() {
			return {
				showDatePicker: false,
				type: 'expense', // income: 收入, expense: 支出
				detailList: [],
				tagList: [],
				listHeight: 400,
				tableHeight: 400,
				// 表格配置
				tableHeaders: [{
						label: '费用名目',
						width: 200
					},
					{
						label: '金额(￥)',
						width: 150
					},
					{
						label: '费用来源',
						width: 150
					},
					{
						label: '操作',
						width: 100
					}
				],
				tableWidth: 600, // 所有列宽总和
				dailyFlowEntity: {
					id: '',
					type: '',
					cost: '0.00',
					recordTime: '',
					note: '',
					made: '',
					uName: '',
					fCreateTime: '',
					fUpdateTime: '',
					op: 'add'
				},
				pagination: {
					currentPage: 1,
					pageSize: 10,
					totalPage: 1,
					currentTableData: []
				}
			};
		},

		computed: {
			typeText() {
				return this.type === 'income' ? '收入' : '支出';
			},
			typeStyle() {
				return this.type === 'income' ? {
					background: '#f6ffed'
				} : {
					background: '#fff2f0'
				};
			},
			typeIcon() {
				return this.type === 'income' ?
					'icon-shouru' :
					'icon-zhichu';
			},
			typeIconStyle() {
				return this.type === 'income' ? {
					color: '#52c41a'
				} : {
					color: '#ff4d4f'
				};
			}
		},

		onLoad(options) {
			if (options.data)
				this.dailyFlowEntity = JSON.parse(options.data) || null;
			this.type = this.dailyFlowEntity.type === '收入' ? 'income' : 'expense'
			if (options.type) {
				this.type = options.type || 'expense';
				this.dailyFlowEntity.type = this.type === 'income' ? '收入' : '支出';
			}

			this.init();
			this.calculateTableHeight();
		},

		onReady() {
			this.calculateTableHeight();
		},

		methods: {
			// 计算表格高度
			calculateTableHeight() {
				const systemInfo = uni.getSystemInfoSync();
				this.tableHeight = systemInfo.windowHeight - 400; // 根据实际布局调整
			},

			// 获取全局索引
			getGlobalIndex(pageIndex) {
				return (this.pagination.currentPage - 1) * this.pagination.pageSize + pageIndex;
			},

			// 初始化分页数据
			updatePagination() {
				this.pagination.totalPage = Math.ceil(this.detailList.length / this.pagination.pageSize) || 1;
				const startIndex = (this.pagination.currentPage - 1) * this.pagination.pageSize;
				const endIndex = startIndex + this.pagination.pageSize;
				this.pagination.currentTableData = this.detailList.slice(startIndex, endIndex);
			},

			// 页面变化
			handlePageChange(page) {
				if (page < 1 || page > this.pagination.totalPage) return;
				this.pagination.currentPage = page;
				this.updatePagination();
			},

			// 初始化
			init() {
				if (this.dailyFlowEntity.id) {
					this.dailyFlowEntity.op = "upd";
					lifeApi._getByFactor(this.dailyFlowEntity.id).then(res => {
						if (res) {
							this.detailList = res;
							this.pagination.currentTableData = res;
							this.pagination.total = res.length;

							this.pagination.totalPage = Math.ceil(
								this.pagination.currentTableData.length / this.pagination.pageSize
							);
							this.pagination.totalPage =
								this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

							this.getCurrentPageData(
								this.pagination.currentTableData,
								this.pagination.currentPage,
								this.pagination.pageSize
							);
							this.pagination.loading = false;
						} else {}
					});
				} else {
					this.dailyFlowEntity.op = "add";
					this.dailyFlowEntity.id = getTime("start");
					this.dailyFlowEntity.recordTime = parseDate(Date.now());
					this.dailyFlowEntity.made = uni.getStorageSync('userInfo').id;
					this.dailyFlowEntity.uName = uni.getStorageSync('userInfo').nickname;
				}
				// 获取费用来源列表
				this.getTagList();
			},

			// 获取费用来源列表
			async getTagList() {
				try {
					// 模拟数据
					const res = await lifeApi._getConsumptionTypeAll();
					if (res.status === 200) {
						this.tagList.splice(0);
						this.tagList.push(...res.data);
					}
				} catch (error) {
					console.error('获取费用来源失败:', error);
				}
			},

			// 金额输入处理
			handleInput(index, item) {
				let value = item.cost;
				value = value.replace(/(^0+(?=\d))|[^\d.]|(?<=\..{2}).|^\.|\.+/g, (match) => { //deepseek给出表达式
						if (match.startsWith("0") && match !== "0") return ""; // 去除前导零（如00→0）
						if (match === ".") return "."; // 允许单个小数点
						return ""; // 过滤非法字符
					}).replace(/(\d*\.?\d{0,2}).*/, "$1") // 截断小数到两位
					.replace(/^\./, "0.");

				this.detailList[index].cost = value;
				this.$set(item, 'cost', value);
				this.calculateTotal();
			},

			// 计算总金额
			calculateTotal() {
				const total = this.detailList.reduce((sum, item) => {
					return sum + parseFloat(item.cost || 0);
				}, 0);
				this.dailyFlowEntity.cost = total.toFixed(2);
			},

			// 日期选择
			handleDateChange(e) {
				this.dailyFlowEntity.recordTime = parseDate(e.detail.value);
				this.showDatePicker = false;
			},

			// 费用来源匹配
			getMatchSource: debounce(async function(index, row) {
				if (!row.purpose) return;
				const res = await lifeApi._getMatchSource(row.purpose)
				if (res.data) {
					row.typeTagID = res.data[0]
					row.note = ''
				} else {
					row.note = 'x'
				}
			}, 300),

			// 费用来源选择
			handleTypeChange(e, index) {
				const selectedIndex = e.detail.value;
				if (this.tagList[selectedIndex]) {
					this.detailList[index].typeTagID = this.tagList[selectedIndex].name;
				}
			},

			// 获取费用来源索引
			typeTagIndex(typeTagID) {
				return this.tagList.findIndex(item => item.name === typeTagID);
			},

			// 删除明细
			handleDelete(index, item) {
				uni.showModal({
					title: '提示',
					content: '确定删除这条明细吗？',
					success: (res) => {
						if (res.confirm) {
							this.detailList.splice(index, 1);
							this.calculateTotal();
							this.updatePagination();

							// 如果当前页没有数据且不是第一页，则跳转到上一页
							if (this.pagination.currentTableData.length === 0 && this.pagination.currentPage >
								1) {
								this.pagination.currentPage--;
								this.updatePagination();
							}
						}
					}
				});
			},

			// 重置
			handleReset() {
				uni.showModal({
					title: '提示',
					content: '确定重置所有数据吗？',
					success: (res) => {
						if (res.confirm) {
							this.detailList = [];
							this.pagination.currentPage = 1;
							this.init();
							this.updatePagination();
						}
					}
				});
			},

			// 添加明细
			handleAddBtn() {
				const newRow = {
					id: getTime("start"),
					cost: '0',
					type: this.dailyFlowEntity.type,
					fID: this.dailyFlowEntity.id,
					typeTagID: '',
					note: '',
					purpose: ''
				};
				this.detailList.push(newRow);
				this.calculateTotal();
				this.updatePagination();

				// 自动跳转到最后一页
				this.pagination.currentPage = this.pagination.totalPage;
				this.updatePagination();
			},

			// 保存
			async handleSaveBtn() {
				if (this.detailList.length === 0) {
					uni.showToast({
						title: '请至少添加一条明细',
						icon: 'none'
					});
					return;
				}

				// 验证数据
				for (let item of this.detailList) {
					if (!item.purpose || !item.cost || parseFloat(item.cost) === 0) {
						uni.showToast({
							title: '请完善明细信息',
							icon: 'none'
						});
						return;
					}
				}

				try {
					uni.showLoading({
						title: '保存中...'
					});

					// 保存明细
					const detailRes = await lifeApi._insertBatch(this.detailList);
					if (detailRes.status === 200) {
						// 保存主记录
						const mainRes = await lifeApi._operationDailyFlow(this.dailyFlowEntity);
						if (mainRes.status === 200) {
							uni.showToast({
								title: '保存成功'
							});
							uni.$emit('refreshDailyFlowList');
							setTimeout(() => {
								uni.navigateBack();
							}, 1500);
						}
					}
				} catch (error) {
					console.error('保存失败:', error);
					uni.showToast({
						title: '保存失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			}
		},

		watch: {
			detailList: {
				handler() {
					this.updatePagination();
				},
				deep: true
			}
		}
	};
</script>

<style lang="scss" scoped>
	.container {
		padding: 20rpx;
		background-color: #f8f9fa;
		min-height: 100vh;
	}

	/* 通用标题样式 */
	.section-title {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;

		.iconfont {
			font-size: 32rpx;
			color: #409eff;
			margin-right: 12rpx;
		}

		.title-text {
			font-size: 32rpx;
			font-weight: 600;
			color: #303133;
		}
	}

	/* 顶部卡片区域 */
	.summary-section {
		background: white;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 24rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
	}

	.summary-cards {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.card-row {
		display: flex;
		gap: 20rpx;
	}

	.summary-card {
		flex: 1;
		background: white;
		border-radius: 12rpx;
		padding: 24rpx;
		display: flex;
		align-items: center;
		border: 1rpx solid #f0f0f0;
		transition: all 0.3s ease;

		&:active {
			transform: scale(0.98);
		}
	}

	.card-icon {
		width: 80rpx;
		height: 80rpx;
		border-radius: 12rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 20rpx;

		.iconfont {
			font-size: 36rpx;
		}
	}

	.card-content {
		flex: 1;
	}

	.card-title {
		color: #909399;
		font-size: 26rpx;
		margin-bottom: 8rpx;
	}

	.card-value {
		color: #303133;
		font-size: 30rpx;
		font-weight: 600;
	}

	.amount-highlight {
		color: #e6a23c;
		font-weight: 700;
	}

	/* 账单日期选择器样式 - 参考basic-fee */
	.date-picker {
		padding: 20rpx;
		border: 1rpx solid #ddd;
		border-radius: 8rpx;
		background-color: #f9f9f9;
		font-size: 28rpx;
		text-align: center;
		color: #333;
	}

	/* 操作信息区域 */
	.info-section {
		background: white;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 24rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
	}

	.info-grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 20rpx;
	}

	.info-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		text-align: center;
		padding: 20rpx;
		background: #fafafa;
		border-radius: 8rpx;
	}

	.info-label {
		color: #909399;
		font-size: 24rpx;
		margin-bottom: 8rpx;
	}

	.info-value {
		color: #606266;
		font-size: 26rpx;
		font-weight: 500;
	}

	/* 操作按钮区域 */
	.action-section {
		background: white;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 24rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
	}

	.action-buttons {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 6rpx;
	}

	.btn {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 10rpx 60rpx;
		border: none;
		border-radius: 10rpx;
		font-size: 36rpx;
		transition: all 0.3s ease;

		&:active {
			transform: scale(0.95);
		}

		.iconfont {
			font-size: 36rpx;
			margin-bottom: 8rpx;
		}

		.btn-text {
			font-size: 24rpx;
		}
	}

	.btn-primary {
		background: linear-gradient(135deg, #409eff, #66b1ff);
		color: white;
	}

	.btn-success {
		background: linear-gradient(135deg, #67c23a, #85ce61);
		color: white;
	}

	.btn-warning {
		background: linear-gradient(135deg, #e6a23c, #ebb563);
		color: white;
	}

	.btn-info {
		background: linear-gradient(135deg, #909399, #a6a9ad);
		color: white;
	}

	/* 明细列表区域 - 修改为表格样式 */
	.detail-section {
		background: white;
		border-radius: 16rpx;
		padding: 30rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.list-stats {
		display: flex;
		align-items: center;
		color: #909399;
		font-size: 24rpx;

		.stats-divider {
			margin: 0 12rpx;
		}
	}

	/* 表格容器样式 - 参考basic-fee */
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

	/* 表格内输入框样式 */
	.table-input {
		width: 100%;
		padding: 16rpx;
		border: 1rpx solid #dcdfe6;
		border-radius: 6rpx;
		font-size: 26rpx;
		text-align: center;
		background: #fff;

		&:focus {
			border-color: #409eff;
		}
	}

	.amount-input {
		text-align: center;
		font-weight: 500;
	}

	/* 表格内选择器样式 */
	.table-picker {
		padding: 16rpx;
		border: 1rpx solid #ddd;
		border-radius: 6rpx;
		background-color: #f9f9f9;
		font-size: 24rpx;
		text-align: center;
		width: 100%;
	}

	.delete-btn {
		color: #FF3B30;
		font-size: 24rpx;
		padding: 8rpx 16rpx;
		background: rgba(255, 59, 48, 0.1);
		border-radius: 6rpx;
	}

	/* 分页控件 */
	.pagination-container {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx 0;
		margin-top: 20rpx;
		border-top: 1rpx solid #ebeef5;
	}

	.pagination-info {
		color: #909399;
		font-size: 24rpx;
	}

	.pagination-controls {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.page-btn {
		width: 60rpx;
		height: 60rpx;
		border: 1rpx solid #dcdfe6;
		border-radius: 6rpx;
		background: white;
		display: flex;
		align-items: center;
		justify-content: center;

		&:disabled {
			background: #f5f7fa;
			color: #c0c4cc;
		}

		.iconfont {
			font-size: 24rpx;
		}
	}

	.page-numbers {
		display: flex;
		align-items: center;
		color: #606266;
		font-size: 26rpx;
	}

	.page-number {
		&.current {
			font-weight: 600;
			color: #409eff;
		}

		&.total {
			color: #909399;
		}
	}

	.page-divider {
		margin: 0 8rpx;
		color: #dcdfe6;
	}

	/* 空状态 */
	.empty-state {
		text-align: center;
		padding: 80rpx 0;
		color: #909399;

		.iconfont {
			font-size: 120rpx;
			margin-bottom: 20rpx;
			display: block;
			color: #c0c4cc;
		}

		.empty-text {
			font-size: 28rpx;
			margin-bottom: 30rpx;
			display: block;
		}
	}

	.btn-add-first {
		background: linear-gradient(135deg, #409eff, #66b1ff);
		color: white;
		border: none;
		border-radius: 8rpx;
		padding: 20rpx 40rpx;
		font-size: 26rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 0 auto;

		.iconfont {
			font-size: 28rpx;
			margin-right: 8rpx;
		}
	}

	/* 响应式调整 */
	@media (max-width: 300px) {
		.card-row {
			flex-direction: column;
		}

		.info-grid {
			grid-template-columns: 1fr;
		}

		.action-buttons {
			grid-template-columns: repeat(2, 1fr);
		}

		.section-header {
			flex-direction: column;
			align-items: flex-start;
			gap: 12rpx;
		}
	}
</style>