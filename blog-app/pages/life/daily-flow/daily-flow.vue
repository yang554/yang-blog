<template>
	<view class="daily-flow-container">
		<!-- 顶部搜索区域 -->
		<view class="search-container">
			<view class="search-card">
				<!-- 账簿人搜索（管理员可见） -->
				<view class="search-item" v-if="isSerachTxt">
					<text class="search-label">账簿人：</text>
					<input class="search-input" placeholder="根据账户或者身份证号搜索" v-model="searchName"
						@confirm="handleSearchEvent" />
				</view>

				<!-- 收支情况选择 -->
				<view class="search-item">
					<text class="search-label">收支情况：</text>
					<picker @change="onTypeChange" :value="typeIndex" :range="typeOptions" class="picker">
						<view class="picker-text">{{ searchType || '请选择' }}</view>
					</picker>
				</view>

				<!-- 账单日期范围 -->
				<view class="search-item">
					<text class="search-label">账单日期：</text>
					<view class="date-range">
						<picker mode="date" :value="searchDate1" @change="onStartDateChange" class="date-picker">
							<view class="date-text">{{ searchDate1 || '开始日期' }}</view>
						</picker>
						<text class="date-separator">至</text>
						<picker mode="date" :value="searchDate2" @change="onEndDateChange" class="date-picker">
							<view class="date-text">{{ searchDate2 || '结束日期' }}</view>
						</picker>
					</view>
				</view>

				<!-- 金额范围 -->
				<view class="search-item">
					<text class="search-label">金额范围：</text>
					<view class="amount-range">
						<view class="amount-input-group">
							<text class="currency-symbol">￥</text>
							<input class="amount-input" placeholder="最小值" v-model="queryCost.minCost" type="digit"
								@input="handleInput('min', $event)" />
						</view>
						<text class="range-separator">至</text>
						<view class="amount-input-group">
							<text class="currency-symbol">￥</text>
							<input class="amount-input" placeholder="最大值" v-model="queryCost.maxCost" type="digit"
								@input="handleInput('max', $event)" />
						</view>
					</view>
				</view>

				<!-- 操作按钮 -->
				<view class="action-buttons">
					<button class="action-btn search-btn" @tap="handleSearchEvent">搜索</button>
					<button class="action-btn add-btn" @tap="showTypeDialog">添加流水</button>
					<button class="action-btn reset-btn" @tap="handleReset">重置</button>
				</view>
			</view>
		</view>

		<!-- 数据列表 -->
		<view class="list-container">
			<scroll-view scroll-y class="scroll-view" :style="{ height: scrollViewHeight + 'px' }">
				<!-- 加载状态 -->
				<view class="loading-container" v-if="pagination.loading">
					<text class="loading-text">加载中...</text>
				</view>

				<!-- 空状态 -->
				<view class="empty-container" v-else-if="pagination.currentTableData.length === 0">
					<text class="empty-text">暂无数据</text>
				</view>

				<!-- 数据列表 -->
				<view class="list-content" v-else>
					<view class="flow-item" v-for="(item, index) in pagination.currentTableData" :key="index"
						@tap="handleViewDetail(item)">
						<view class="item-header">
							<text class="flow-id">单号: {{ item.id }}</text>
							<view class="type-tag" :class="item.type === '收入' ? 'income-tag' : 'expense-tag'">
								{{ item.type }}
							</view>
						</view>

						<view class="item-content">
							<view class="amount-section">
								<text class="amount-label">金额：</text>
								<text class="amount-value" :class="item.type === '收入' ? 'income' : 'expense'">
									{{ item.type === '收入' ? '+' : '-' }}{{ item.cost }}
								</text>
							</view>

							<view class="info-section">
								<view class="info-item">
									<text class="info-label">账单日期：</text>
									<text class="info-value">{{ item.recordTime | tableTile }}</text>
								</view>
								<view class="info-item">
									<text class="info-label">制单人：</text>
									<text class="info-value">{{ item.uName }}</text>
								</view>
								<view class="info-item">
									<text class="info-label">创建时间：</text>
									<text class="info-value">{{ item.fCreateTime }}</text>
								</view>
							</view>
						</view>

						<view class="item-actions">
							<button class="action-btn edit-btn" @tap.stop="handleEdit(item)">查看/编辑</button>
							<button class="action-btn delete-btn" @tap.stop="handleDelete(item)">删除</button>
						</view>
					</view>
				</view>

				<!-- 加载更多 -->
				<view class="load-more" v-if="showLoadMore">
					<text class="load-more-text">{{ loadMoreText }}</text>
				</view>
			</scroll-view>
		</view>

		<!-- 分页 -->
		<view class="pagination-container" v-if="pagination.total > pagination.pageSize">
			<view class="pagination-content">
				<button class="page-btn" :disabled="pagination.currentPage === 1" @tap="handlePrevPage">
					上一页
				</button>

				<view class="page-info">
					<text class="page-text">{{ pagination.currentPage }} / {{ pagination.totalPage }}</text>
				</view>

				<button class="page-btn" :disabled="pagination.currentPage === pagination.totalPage"
					@tap="handleNextPage">
					下一页
				</button>
			</view>
		</view>

		<!-- 类型选择弹窗 -->
		<uni-popup ref="typeDialog" type="dialog">
			<view class="dialog-container">
				<view class="dialog-header">
					<text class="dialog-title">请选择流水类型</text>
				</view>
				<view class="dialog-content">
					<picker @change="onDialogTypeChange" :value="dialogTypeIndex" :range="dialogTypeOptions"
						class="dialog-picker">
						<view class="dialog-picker-text">
							{{ dialogFormValue || '请选择流水类型' }}
						</view>
					</picker>
				</view>
				<view class="dialog-footer">
					<button class="dialog-btn cancel-btn" @tap="closeTypeDialog">取消</button>
					<button class="dialog-btn confirm-btn" @tap="handleAddConfirm">确定</button>
				</view>
			</view>
		</uni-popup>

		<!-- 删除确认弹窗 -->
		<uni-popup ref="deleteDialog" type="dialog">
			<view class="dialog-container">
				<view class="dialog-header">
					<text class="dialog-title">确认删除</text>
				</view>
				<view class="dialog-content">
					<text class="delete-text">此操作将永久删除该流水记录, 是否继续?</text>
				</view>
				<view class="dialog-footer">
					<button class="dialog-btn cancel-btn" @tap="closeDeleteDialog">取消</button>
					<button class="dialog-btn confirm-btn" @tap="confirmDelete">确定</button>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	import {
		lifeApi
	} from "@/api/index.js";
	import {
		getTime,
		parseTime,
		parseDate
	} from "@/common/formatDate.js";

	export default {
		name: "DailyFlowView",
		data() {
			return {
				isSerachTxt: false,
				scrollViewHeight: 500,
				typeOptions: ['收入', '支出'],
				typeIndex: -1,
				dialogTypeOptions: ['收入单', '支出单'],
				dialogTypeIndex: -1,
				tableData: [],
				pagination: {
					listData: [],
					currentPage: 1,
					pageSize: 10,
					totalPage: 1,
					currentTableData: [],
					total: 0,
					loading: true,
				},
				currentUserInfo: uni.getStorageSync(this.$constants.USER_INFO), //当前登录用户
				searchName: "",
				searchNameID: "",
				searchType: "",
				searchDate1: "",
				searchDate2: "",
				uID: "",
				queryCost: {
					minCost: null,
					maxCost: null
				},
				dialogFormValue: "支出",
				showLoadMore: false,
				loadMoreText: "上拉加载更多",
				currentDeleteItem: null
			};
		},
		watch: {
			'queryCost.minCost'(newVal) {
				this.validateQueryCost();
			},
			'queryCost.maxCost'(newVal) {
				this.validateQueryCost();
			},
		},
		filters: {
			tableTile(value) {
				if (!value) return "";
				if (value.length > 19) {
					return value.slice(0, 19) + "...";
				}
				return value;
			},
			dateFormat(val) {
				if (!val) return "";
				else return parseTime(val);
			},
		},
		onLoad() {
			this.init();
			uni.$on('refreshDailyFlowList', this.init);
			// this.calculateScrollViewHeight();
		},
		onUnload() {
			// 移除事件监听，避免内存泄漏
			uni.$off('refreshDailyFlowList', this.init);
		},
		onShow() {
			// this.init();
		},
		onReady() {
			// 确保页面渲染完成后计算高度
			setTimeout(() => {
				this.calculateScrollViewHeight();
			}, 300);
		},
		onPullDownRefresh() {
			// 下拉刷新
			this.init();
			setTimeout(() => {
				uni.stopPullDownRefresh();
			}, 1000);
		},
		methods: {
			calculateScrollViewHeight() {
				// 计算scroll-view的高度
				const systemInfo = uni.getSystemInfoSync();
				const windowHeight = systemInfo.windowHeight;

				// 创建选择器查询搜索区域高度
				const query = uni.createSelectorQuery().in(this);
				query.select('.search-container').boundingClientRect(data => {
					const searchHeight = data ? data.height : 200;
					this.scrollViewHeight = windowHeight - searchHeight - 50;
				}).exec();
			},

			validateQueryCost() {
				if (this.queryCost.minCost !== null && this.queryCost.maxCost !== null &&
					parseFloat(this.queryCost.minCost) > parseFloat(this.queryCost.maxCost)) {
					uni.showToast({
						title: '金额范围最大值不能小于最小值',
						icon: 'none',
						duration: 2000
					});
				}
			},

			handleInput(type, e) {
				let value = e.detail ? e.detail.value : e;
				value = value.replace(/(^0+(?=\d))|[^\d.]|(?<=\..{2}).|^\.|\.+/g, (match) => {
						if (match.startsWith("0") && match !== "0") return "";
						if (match === ".") return ".";
						return "";
					}).replace(/(\d*\.?\d{0,2}).*/, "$1")
					.replace(/^\./, "0.");

				if (type === 'min') {
					this.queryCost.minCost = value;
				} else {
					this.queryCost.maxCost = value;
				}
			},

			// 初始化
			init() {
				if (this.currentUserInfo.roles[0].name === 'root') {
					this.isSerachTxt = true;
					this.uID = '';
				} else {
					this.isSerachTxt = false;
					this.uID = this.currentUserInfo.id;
				}

				lifeApi._getDailyFlowAll(this.uID).then((res) => {
					if (res) {
						this.pagination.listData = res;
						this.pagination.total = res.length;

						this.pagination.totalPage = Math.ceil(
							this.pagination.listData.length / this.pagination.pageSize
						);
						this.pagination.totalPage =
							this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

						this.getCurrentPageData(
							this.pagination.listData,
							this.pagination.currentPage,
							this.pagination.pageSize
						);

						this.pagination.loading = false;
					}
				}).catch(err => {
					console.error('初始化失败:', err);
					this.pagination.loading = false;
				});
			},

			// 分页
			getCurrentPageData(listData, currentPage, pageSize) {
				let begin = (currentPage - 1) * pageSize;
				let end = currentPage * pageSize;
				this.pagination.currentTableData = listData.slice(begin, end);
			},

			// 类型选择变化
			onTypeChange(e) {
				this.typeIndex = e.detail.value;
				this.searchType = this.typeOptions[this.typeIndex];
			},

			// 开始日期变化
			onStartDateChange(e) {
				this.searchDate1 = e.detail.value;
			},

			// 结束日期变化
			onEndDateChange(e) {
				this.searchDate2 = e.detail.value;
			},

			// 搜索事件
			handleSearchEvent() {
				lifeApi._getUserByName(this.searchName).then((res) => {

					console.log(this.currentUserInfo.roles[0].name)

					if (res.data.length > 0 && this.searchName != '') {
						this.searchNameID = res.data[0].id;
					} else if(this.currentUserInfo.roles[0].name == 'root'){
						this.searchNameID = '';
					} else {
						this.searchNameID = this.currentUserInfo.id;
					}

					console.log(this.searchNameID)
					const params = {
						searchDate1: this.searchDate1 || '',
						searchDate2: this.searchDate2 || '',
						searchType: this.searchType || '',
						searchName: this.searchNameID || '',
						queryCost: typeof this.queryCost === 'object' ?
							JSON.stringify(this.queryCost) : this.queryCost || ''
					};

					const encodedParams = Object.keys(params).map(key =>
						`${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`
					).join('&');

					lifeApi._findByFactor(encodedParams).then((res) => {
						this.pagination.listData = res;
						this.pagination.total = res.length;

						this.pagination.totalPage = Math.ceil(
							this.pagination.listData.length / this.pagination.pageSize
						);
						this.pagination.totalPage =
							this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

						this.pagination.currentPage = 1;

						this.getCurrentPageData(
							this.pagination.listData,
							this.pagination.currentPage,
							this.pagination.pageSize
						);
						this.pagination.loading = false;
					});
				});
			},

			// 重置
			handleReset() {
				this.searchDate1 = "";
				this.searchDate2 = "";
				this.searchType = "";
				this.searchName = "";
				this.searchNameID = this.currentUserInfo.id;
				this.queryCost.minCost = "";
				this.queryCost.maxCost = "";
				this.typeIndex = -1;
				this.init();
			},

			// 显示类型选择弹窗
			showTypeDialog() {
				if (this.$refs.typeDialog) {
					this.$refs.typeDialog.open();
				}
			},

			// 关闭类型选择弹窗
			closeTypeDialog() {
				if (this.$refs.typeDialog) {
					this.$refs.typeDialog.close();
				}
			},

			// 弹窗类型选择变化
			onDialogTypeChange(e) {
				this.dialogTypeIndex = e.detail.value;
				this.dialogFormValue = this.dialogTypeOptions[this.dialogTypeIndex] === '收入单' ? '收入' : '支出';
			},

			// 确认添加
			handleAddConfirm() {
				this.closeTypeDialog();
				const type = this.dialogFormValue === '收入' ? 'income' : 'expense';
				uni.navigateTo({
					url: `/pages/life/add-daily-flow/add-daily-flow?type=${type}`
				});
			},

			// 查看详情
			handleViewDetail(item) {
				console.log('查看详情:', item);
			},

			// 编辑
			handleEdit(item) {
				uni.navigateTo({
					// encodeURIComponent(JSON.stringify(item))
					url: `/pages/life/add-daily-flow/add-daily-flow?data=${JSON.stringify(item)}`
				});
			},

			// 删除
			handleDelete(item) {
				this.currentDeleteItem = item;
				if (this.$refs.deleteDialog) {
					this.$refs.deleteDialog.open();
				}
			},

			// 关闭删除确认弹窗
			closeDeleteDialog() {
				if (this.$refs.deleteDialog) {
					this.$refs.deleteDialog.close();
				}
				this.currentDeleteItem = null;
			},

			// 确认删除
			confirmDelete() {
				if (!this.currentDeleteItem) return;

				lifeApi._delFlow(this.currentDeleteItem.id).then((res) => {
					if (res.status === 200) {
						uni.showToast({
							title: '删除成功',
							icon: 'success',
							duration: 2000
						});
						this.init();
					} else {
						uni.showToast({
							title: '删除失败',
							icon: 'none',
							duration: 2000
						});
					}
					this.closeDeleteDialog();
				}).catch(err => {
					console.error('删除失败:', err);
					uni.showToast({
						title: '删除失败',
						icon: 'none',
						duration: 2000
					});
					this.closeDeleteDialog();
				});
			},

			// 上一页
			handlePrevPage() {
				if (this.pagination.currentPage > 1) {
					this.pagination.currentPage--;
					this.getCurrentPageData(
						this.pagination.listData,
						this.pagination.currentPage,
						this.pagination.pageSize
					);
				}
			},

			// 下一页
			handleNextPage() {
				if (this.pagination.currentPage < this.pagination.totalPage) {
					this.pagination.currentPage++;
					this.getCurrentPageData(
						this.pagination.listData,
						this.pagination.currentPage,
						this.pagination.pageSize
					);
				}
			},

			// 滚动到底部
			onScrollToLower() {
				if (this.pagination.currentPage < this.pagination.totalPage) {
					this.showLoadMore = true;
					this.loadMoreText = "加载中...";

					setTimeout(() => {
						this.pagination.currentPage++;
						this.getCurrentPageData(
							this.pagination.listData,
							this.pagination.currentPage,
							this.pagination.pageSize
						);
						this.showLoadMore = false;
					}, 500);
				} else {
					this.showLoadMore = true;
					this.loadMoreText = "没有更多数据了";
					setTimeout(() => {
						this.showLoadMore = false;
					}, 1500);
				}
			}
		}
	};
</script>

<style lang="scss" scoped>
	.daily-flow-container {
		padding: 20rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	.search-container {
		margin-bottom: 20rpx;
	}

	.search-card {
		background: #fff;
		border-radius: 16rpx;
		padding: 30rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
	}

	.search-item {
		display: flex;
		align-items: center;
		margin-bottom: 30rpx;

		.search-label {
			font-size: 28rpx;
			color: #333;
			min-width: 140rpx;
			margin-right: 20rpx;
		}

		.search-input {
			flex: 1;
			height: 70rpx;
			border: 1rpx solid #ddd;
			border-radius: 8rpx;
			padding: 0 20rpx;
			font-size: 28rpx;
		}

		.picker {
			flex: 1;

			.picker-text {
				height: 70rpx;
				border: 1rpx solid #ddd;
				border-radius: 8rpx;
				padding: 0 20rpx;
				line-height: 70rpx;
				font-size: 28rpx;
				color: #333;
			}
		}
	}

	.date-range {
		display: flex;
		align-items: center;
		flex: 1;

		.date-picker {
			flex: 1;

			.date-text {
				height: 70rpx;
				border: 1rpx solid #ddd;
				border-radius: 8rpx;
				padding: 0 20rpx;
				line-height: 70rpx;
				font-size: 28rpx;
				color: #333;
				text-align: center;
			}
		}

		.date-separator {
			margin: 0 20rpx;
			color: #666;
		}
	}

	.amount-range {
		display: flex;
		align-items: center;
		flex: 1;

		.amount-input-group {
			display: flex;
			align-items: center;
			flex: 1;
			height: 70rpx;
			border: 1rpx solid #ddd;
			border-radius: 8rpx;
			overflow: hidden;

			.currency-symbol {
				padding: 0 20rpx;
				background: #f5f5f5;
				height: 100%;
				line-height: 70rpx;
				color: #666;
			}

			.amount-input {
				flex: 1;
				height: 100%;
				padding: 0 20rpx;
				font-size: 28rpx;
			}
		}

		.range-separator {
			margin: 0 20rpx;
			color: #666;
		}
	}

	.action-buttons {
		display: flex;
		align-items: center;
		justify-content: center;
		border: none;
		border-radius: 10rpx;
		font-size: 36rpx;
		transition: all 0.3s ease;

		.action-btn {
			flex: 1;
			height: 80rpx;
			border-radius: 8rpx;
			font-size: 28rpx;
			margin: 0 10rpx;

			&:active {
				transform: scale(0.95);
			}

			&.search-btn {
				background: #007AFF;
				color: #fff;
			}

			&.add-btn {
				background: #4CD964;
				color: #fff;
			}

			&.reset-btn {
				background: #FF3B30;
				color: #fff;
			}
		}
	}

	.list-container {
		background: #fff;
		border-radius: 16rpx;
		overflow: hidden;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
	}

	.scroll-view {
		padding: 20rpx;
	}

	.loading-container,
	.empty-container {
		display: flex;
		justify-content: center;
		align-items: center;
		height: 200rpx;

		.loading-text,
		.empty-text {
			font-size: 28rpx;
			color: #666;
		}
	}

	.flow-item {
		border: 1rpx solid #eee;
		border-radius: 12rpx;
		margin-bottom: 20rpx;
		padding: 30rpx;
		background: #fff;

		.item-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 20rpx;

			.flow-id {
				font-size: 28rpx;
				color: #333;
				font-weight: bold;
			}

			.type-tag {
				padding: 8rpx 20rpx;
				border-radius: 20rpx;
				font-size: 24rpx;
				color: #fff;

				&.income-tag {
					background: #4CD964;
				}

				&.expense-tag {
					background: #FF3B30;
				}
			}
		}

		.item-content {
			.amount-section {
				display: flex;
				align-items: center;
				margin-bottom: 20rpx;

				.amount-label {
					font-size: 28rpx;
					color: #666;
				}

				.amount-value {
					font-size: 36rpx;
					font-weight: bold;

					&.income {
						color: #4CD964;
					}

					&.expense {
						color: #FF3B30;
					}
				}
			}

			.info-section {
				.info-item {
					display: flex;
					margin-bottom: 10rpx;

					.info-label {
						font-size: 24rpx;
						color: #999;
						min-width: 140rpx;
					}

					.info-value {
						font-size: 24rpx;
						color: #333;
						flex: 1;
					}
				}
			}
		}

		.item-actions {
			display: flex;
			justify-content: flex-end;
			margin-top: 20rpx;

			.action-btn {
				padding: 6rpx 24rpx;
				border-radius: 8rpx;
				font-size: 24rpx;
				margin-left: 20rpx;

				&.edit-btn {
					background: #4CD964;
					color: #fff;
				}

				&:active {
					transform: scale(0.95);
				}

				&.delete-btn {
					background: #FF3B30;
					color: #fff;
				}
			}
		}
	}

	.pagination-container {
		margin-top: 30rpx;
		padding: 20rpx;
		background: #fff;
		border-radius: 16rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);

		.pagination-content {
			display: flex;
			justify-content: space-between;
			align-items: center;

			.page-btn {
				padding: 2rpx 40rpx;
				border-radius: 8rpx;
				background: #007AFF;
				color: #fff;
				font-size: 28rpx;

				&:disabled {
					background: #ccc;
					color: #999;
				}

				&:active {
					transform: scale(0.95);
				}
			}

			.page-info {
				.page-text {
					font-size: 28rpx;
					color: #333;
				}
			}
		}
	}

	.dialog-container {
		background: #fff;
		border-radius: 16rpx;
		padding: 40rpx;
		width: 600rpx;

		.dialog-header {
			text-align: center;
			margin-bottom: 30rpx;

			.dialog-title {
				font-size: 32rpx;
				color: #333;
				font-weight: bold;
			}
		}

		.dialog-content {
			margin-bottom: 40rpx;

			.dialog-picker {
				.dialog-picker-text {
					height: 80rpx;
					border: 1rpx solid #ddd;
					border-radius: 8rpx;
					padding: 0 20rpx;
					line-height: 80rpx;
					font-size: 28rpx;
					color: #333;
					text-align: center;
				}
			}

			.delete-text {
				font-size: 28rpx;
				color: #333;
				text-align: center;
			}
		}

		.dialog-footer {
			display: flex;
			justify-content: space-between;

			.dialog-btn {
				flex: 1;
				height: 80rpx;
				border-radius: 8rpx;
				font-size: 28rpx;
				margin: 0 10rpx;

				&.cancel-btn {
					background: #f5f5f5;
					color: #333;
				}

				&.confirm-btn {
					background: #007AFF;
					color: #fff;
				}
			}
		}
	}

	.load-more {
		text-align: center;
		padding: 30rpx;

		.load-more-text {
			font-size: 24rpx;
			color: #999;
		}
	}
</style>