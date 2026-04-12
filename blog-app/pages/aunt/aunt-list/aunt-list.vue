<template>
	<view class="user-list-view">
		<!--搜索-->
		<view class="search-container" v-if="showUserSearch">
			<view class="search-item">
				<text class="search-label">姓名：</text>
				<input class="search-input" placeholder="按姓名搜索" v-model="searchName" />
				<button class="btn primary" size="mini" @click="handleSearchEvent">
					<text class="btn-text">搜索</text>
				</button>
				<button class="btn warning" size="mini" @click="handleReset">
					<text class="btn-text">重置</text>
				</button>
			</view>

		</view>

		<!-- 表格区域 -->
		<view class="table-container">
			<scroll-view class="table-scroll" scroll-x="true" scroll-y="true" :style="{height: screenHeight + 'px'}">
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
						<view v-if="pagination.loading" class="loading-container">
							<text class="loading-text">加载中...</text>
						</view>
						<view v-else-if="pagination.currentTableData.length === 0" class="empty-container">
							<text class="empty-text">暂无数据</text>
						</view>
						<view v-else>
							<view class="table-row" v-for="(item, index) in pagination.currentTableData" :key="item.id">
								<view class="table-cell"
									:style="{width: tableHeaders[0].width + 'px', 'min-width': tableHeaders[0].width + 'px'}">
									{{ item.id }}
								</view>
								<view class="table-cell"
									:style="{width: tableHeaders[1].width + 'px', 'min-width': tableHeaders[1].width + 'px'}">
									{{ item.uName }}
								</view>
								<view class="table-cell"
									:style="{width: tableHeaders[2].width + 'px', 'min-width': tableHeaders[2].width + 'px'}">
									{{ item.startDate }}
								</view>
								<view class="table-cell"
									:style="{width: tableHeaders[3].width + 'px', 'min-width': tableHeaders[3].width + 'px'}">
									{{ item.endDate }}
								</view>
								<view class="table-cell"
									:style="{width: tableHeaders[4].width + 'px', 'min-width': tableHeaders[4].width + 'px'}">
									{{ item.duration }}
								</view>
								<view class="table-cell"
									:style="{width: tableHeaders[5].width + 'px', 'min-width': tableHeaders[5].width + 'px'}">
									{{ item.bloodVolume }}ml
								</view>
								<view class="table-cell"
									:style="{width: tableHeaders[6].width + 'px', 'min-width': tableHeaders[6].width + 'px'}">
									{{ item.cycle }}
								</view>
								<view class="table-cell"
									:style="{width: tableHeaders[7].width + 'px', 'min-width': tableHeaders[7].width + 'px'}">
									{{ tableTile(item.symptom) }}
								</view>
								<view class="table-cell"
									:style="{width: tableHeaders[8].width + 'px', 'min-width': tableHeaders[8].width + 'px'}">
									{{ tableTile(item.note) }}
								</view>
								<view class="table-cell"
									:style="{width: tableHeaders[9].width + 'px', 'min-width': tableHeaders[9].width + 'px'}">
									<view class="action-buttons">
										<text class="action-btn edit" @tap="handleEdit(index, item)">查看\编辑</text>
										<text class="action-btn delete" @tap="handleDelete(index, item)">删除</text>
									</view>
								</view>
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
					@tap="handlePrevPage">上一页</text>
				<text class="page-info">第 {{ pagination.currentPage }} 页 / 共 {{ pagination.totalPage }} 页</text>
				<text class="page-btn" :class="{disabled: pagination.currentPage >= pagination.totalPage}"
					@tap="handleNextPage">下一页</text>
			</view>
			<view class="page-size-selector">
				<text>每页显示：</text>
				<picker @change="handleSizeChange" :value="pageSizeIndex" :range="pagination.pageSizes">
					<view class="picker">{{ pagination.pageSize }} 条</view>
				</picker>
			</view>
		</view>

		<!-- 编辑对话框 -->
		<view v-if="showEditDialog" class="edit-dialog-mask" @tap="closeEditDialog">
			<view class="edit-dialog glass-card" @tap.stop>
				<view class="dialog-header">
					<text class="dialog-title">记录编辑 - {{ realAuntForm.name }}</text>
					<text class="dialog-close" @tap="closeEditDialog">×</text>
				</view>
				<view class="dialog-content">
					<view class="form-row">
						<view class="form-col">
							<view class="form-item">
								<text class="form-label">姓名</text>
								<input v-model="realAuntForm.name" disabled class="form-input" />
							</view>
						</view>
						<view class="form-col">
							<view class="form-item">
								<text class="form-label">周期长度</text>
								<input v-model="realAuntForm.cycle" disabled class="form-input" />
							</view>
						</view>
					</view>

					<view class="form-row">
						<view class="form-col">
							<view class="form-item">
								<text class="form-label">开始日期</text>
								<picker mode="date" :value="realAuntForm.startDate">
									<view class="form-picker">{{ realAuntForm.startDate || '选择开始时间' }}
									</view>
								</picker>
							</view>
						</view>
						<view class="form-col">
							<view class="form-item">
								<text class="form-label">结束日期</text>
								<picker mode="date" :value="realAuntForm.endDate">
									<view class="form-picker">{{ realAuntForm.endDate || '选择结束时间' }}</view>
								</picker>
							</view>
						</view>
						<view class="form-col">
							<view class="form-item">
								<text class="form-label">持续时间</text>
								<input v-model="realAuntForm.duration" disabled class="form-input" />
							</view>
						</view>
						<view class="form-col">
							<view class="form-item">
								<text class="form-label">血量(ml)</text>
								<input v-model="realAuntForm.bloodVolume" class="form-input" type="number" />
							</view>
						</view>
					</view>

					<view class="form-row">
						<view class="form-col-full">
							<view class="form-item">
								<text class="form-label">症状</text>
								<textarea placeholder="记录每个月姨妈期间出现的症状，如腹痛、乳房胀痛、情绪波动等" v-model="realAuntForm.symptom"
									class="form-textarea"></textarea>
							</view>
						</view>
					</view>

					<view class="form-row">
						<view class="form-col-full">
							<view class="form-item">
								<text class="form-label">备注</text>
								<textarea placeholder="可以记录一些额外的信息，如服用的药物、健康状况等。" v-model="realAuntForm.note"
									class="form-textarea"></textarea>
							</view>
						</view>
					</view>
				</view>
				<view class="dialog-footer">
					<button @click="closeEditDialog" class="dialog-btn cancel">取 消</button>
					<button type="primary" @click="handleEditAunt" class="dialog-btn save">保 存</button>
				</view>
			</view>
		</view>

	</view>
</template>

<script>
	import {
		auntApi,
		userApi
	} from '@/api/index.js'

	export default {
		name: "AuntList",
		data() {
			return {
				showUserSearch: false,
				screenHeight: 500,
				showEditDialog: false,
				tableData: [],
				pagination: {
					listData: null, //总data
					currentPage: 1, //当前页
					pageSizes: [5, 10, 15, 20],
					pageSize: 20, //每页显示条数
					totalPage: 1, //总页数
					currentTableData: [], //当前页data
					total: 0, //总数量
					loading: true, //table加载样式是否显示
				},
				searchName: "", //搜索的用户名
				currentUserInfo: uni.getStorageSync(this.$constants.USER_INFO), //当前登录用户
				searchID: '', //用户ID
				realAuntForm: {
					id: "",
					name: "",
					startDate: "", //开始日期
					endDate: "", //结束日期
					duration: "", //持续时间
					cycle: "", //周期长度
					bloodVolume: "", //姨妈血量
					symptom: "", //症状
					note: "", //备注
					uStatus: "",
				},
				pageSizeIndex: 3, // 默认选择20条/页
				// 表格配置
				tableHeaders: [{
						label: '编码',
						width: 130
					},
					{
						label: '姓名',
						width: 100
					},
					{
						label: '开始时间',
						width: 130
					},
					{
						label: '结束时间',
						width: 130
					},
					{
						label: '持续天数',
						width: 80
					},
					{
						label: '经血量',
						width: 80
					},
					{
						label: '周期长度',
						width: 80
					},
					{
						label: '症状',
						width: 200
					},
					{
						label: '备注',
						width: 200
					},
					{
						label: '操作',
						width: 200
					}
				],
				tableWidth: 1330 // 所有列宽总和
			};
		},
		onLoad() {
			this.init();
			this.calculateScreenHeight();
		},
		onReady() {
			// 监听窗口尺寸变化
			uni.onWindowResize((res) => {
				this.calculateScreenHeight();
			});

			if (this.currentUserInfo.roles[0].name === 'root') {
				this.showUserSearch = true
			}
		},
		onUnload() {
			// 移除监听 - 修复：传入回调函数
			uni.offWindowResize((res) => {
				this.calculateScreenHeight();
			});
		},
		methods: {
			// 计算屏幕高度
			calculateScreenHeight() {
				const systemInfo = uni.getSystemInfoSync();
				this.screenHeight = systemInfo.windowHeight - 300; // 减去其他组件的高度
			},

			// 文本截断过滤器
			tableTile(value) {
				if (!value) return "";
				if (value.length > 19) {
					return value.slice(0, 19) + "...";
				}
				return value;
			},
			// 打开编辑对话框
			dialogFunction() {
				this.showEditDialog = true;
			},

			// 关闭编辑对话框
			closeEditDialog() {
				this.showEditDialog = false;
			},
			//初始化
			init() {
				auntApi._selectByName(this.currentUserInfo.id).then((res) => {
					if (res.status === 200) {
						this.pagination.listData = res.data;
						this.pagination.total = res.data.length;

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
					console.error('初始化数据失败:', err);
					this.pagination.loading = false;
				});
			},

			//分页
			getCurrentPageData(listData, currentPage, pageSize) {
				let begin = (currentPage - 1) * pageSize;
				let end = currentPage * pageSize;
				this.pagination.currentTableData = listData.slice(begin, end);
			},

			//表格操作-删除
			handleDelete(index, row) {
				uni.showModal({
					title: "提示",
					content: "此操作将永久删除该记录, 是否继续?",
					confirmText: "确定",
					cancelText: "取消",
					success: (res) => {
						if (res.confirm) {
							//网络请求
							auntApi._delAunt(row.id).then((res) => {
								if (res.status === 200) {
									this.init();
									uni.showToast({
										title: "删除成功!",
										icon: "success"
									});
								}
							}).catch(err => {
								console.error('删除失败:', err);
								uni.showToast({
									title: "删除失败",
									icon: "error"
								});
							});
						} else if (res.cancel) {
							uni.showToast({
								title: "已取消删除",
								icon: "none"
							});
						}
					}
				});
			},

			//表格操作-编辑
			handleEdit(index, row) {
				this.realAuntForm.id = row.id;
				this.realAuntForm.name = row.uName;
				this.realAuntForm.startDate = row.startDate;
				this.realAuntForm.endDate = row.endDate;
				this.realAuntForm.duration = row.duration;
				this.realAuntForm.cycle = row.cycle;
				this.realAuntForm.bloodVolume = row.bloodVolume;
				this.realAuntForm.symptom = row.symptom;
				this.realAuntForm.note = row.note;

				this.dialogFunction();
			},

			// 保存当前记录
			handleEditAunt() {
				auntApi._updAunt(this.realAuntForm).then((res) => {
					uni.showToast({
						title: "编辑成功!",
						icon: "success"
					});
					this.closeEditDialog();
					// 刷新数据
					this.init();
				}).catch(err => {
					console.error('编辑失败:', err);
					uni.showToast({
						title: "编辑失败",
						icon: "error"
					});
				});
			},

			/*搜索*/
			handleSearchEvent() {
				if (this.searchName == "") {
					this.init();
				} else {
					userApi.getUserByName(this.searchName).then((res) => {
						if (res.data.length > 0) {
							this.searchID = res.data[0].id;
							auntApi._selectByName(this.searchID).then((res) => {
								if (res.status === 200) {
									this.pagination.listData = res.data;
									this.pagination.total = res.data.length;

									this.pagination.totalPage = Math.ceil(
										this.pagination.listData.length / this.pagination.pageSize
									);
									this.pagination.totalPage =
										this.pagination.totalPage == 0 ?
										1 :
										this.pagination.totalPage;

									this.getCurrentPageData(
										this.pagination.listData,
										this.pagination.currentPage,
										this.pagination.pageSize
									);
									this.pagination.loading = false;
								} else {
									uni.showToast({
										title: res.data.msg,
										icon: "error"
									});
								}
							});
						} else {
							uni.showToast({
								title: "查无此人",
								icon: "error"
							});
						}
					}).catch(err => {
						console.error('搜索失败:', err);
						uni.showToast({
							title: "搜索失败",
							icon: "error"
						});
					});
				}
			},

			handleReset() {
				this.searchName = "";
				this.searchID = this.currentUserInfo.id;
				this.init();
			},

			/*分页事件*/
			handleSizeChange(e) {
				const index = e.detail.value;
				this.pageSizeIndex = index;
				this.pagination.pageSize = this.pagination.pageSizes[index];
				this.pagination.currentPage = 1;
				this.getCurrentPageData(
					this.pagination.listData,
					this.pagination.currentPage,
					this.pagination.pageSize
				);
			},

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
		}
	};
</script>

<style lang="scss" scoped>
	@import '@/scss/variables';

	.user-list-view {
		padding: 20rpx;
		display: flex;
		flex-direction: column;
		height: 100vh;
		box-sizing: border-box;
		background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
	}

	.search-container {
		display: flex;
		align-items: center;
		margin-bottom: 30rpx;
		padding: 20rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.search-item {
		display: flex;
		align-items: center;
		margin-right: 30rpx;
	}

	.search-label {
		font-size: 28rpx;
		color: #333;
		margin-right: 15rpx;
		white-space: nowrap;
	}

	.search-input {
		width: 200rpx;
		height: 60rpx;
		border: 1rpx solid #ddd;
		border-radius: 8rpx;
		padding: 0 20rpx;
		font-size: 28rpx;
	}

	.search-buttons {
		display: flex;
	}

	.btn {
		margin: 0 5rpx;
		width: 230rpx;
		height: 60rpx;
		padding: 0rpx;
		border-radius: 8rpx;
		color: #fff;
		font-size: 28rpx;
		text-align: center;

		&:active {
			transform: scale(0.95);
		}
	}

	.primary {
		background-color: #007AFF;
	}

	.warning {
		background-color: #FF9500;
	}

	/* 表格样式 */
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

	.loading-container,
	.empty-container {
		display: flex;
		justify-content: center;
		align-items: center;
		height: 200rpx;
	}

	.loading-text,
	.empty-text {
		font-size: 28rpx;
		color: #999;
	}

	.action-buttons {
		display: flex;
		justify-content: center;
	}

	.action-btn {
		padding: 8rpx 16rpx;
		margin: 0 10rpx;
		font-size: 24rpx;
		border-radius: 6rpx;
	}

	.edit {
		color: #fff;
		background: rgba(76, 217, 100, 0.8);
	}

	.delete {
		color: #fff;
		background: rgba(255, 59, 48, 0.8);
	}

	/* 分页样式 */
	.pagination {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx;
		background-color: #fff;
		border-radius: 10rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.pagination-controls {
		display: flex;
		align-items: center;
	}

	.page-btn {
		padding: 10rpx 20rpx;
		margin: 0 10rpx;
		border: 1rpx solid #007AFF;
		border-radius: 6rpx;
		color: #007AFF;
	}

	.page-btn.disabled {
		border-color: #ccc;
		color: #ccc;
	}

	.page-info {
		margin: 0 20rpx;
		font-size: 26rpx;
	}

	.page-size-selector {
		display: flex;
		align-items: center;
	}

	.picker {
		padding: 10rpx 20rpx;
		border: 1rpx solid #ddd;
		border-radius: 8rpx;
		font-size: 26rpx;
	}

	/* 玻璃态效果卡片 */
	.glass-card {
		background: rgba(255, 255, 255, 0.95);
		backdrop-filter: blur(20rpx);
		border-radius: 24rpx;
		border: 1rpx solid rgba(255, 255, 255, 0.3);
		box-shadow: $shadow-heavy;
		overflow: hidden;
	}

	/* 对话框样式 */
	.edit-dialog-mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 9999;
		padding: 40rpx;
	}

	.edit-dialog {
		width: 100%;
		max-width: 800rpx;
		max-height: 80%;
		overflow-y: auto;
	}

	.dialog-header {
		padding: 40rpx;
		border-bottom: 1px solid rgba($primary-color, 0.1);
		display: flex;
		justify-content: space-between;
		align-items: center;

		.dialog-title {
			color: $text-primary;
			font-size: 36rpx;
			font-weight: 600;
		}

		.dialog-close {
			color: $text-secondary;
			font-size: 48rpx;
			line-height: 1;
			cursor: pointer;
			transition: color 0.3s ease;

			&:active {
				color: $text-primary;
			}
		}
	}

	.dialog-content {
		padding: 10rpx;
	}

	.form-row {
		display: flex;
		flex-wrap: wrap;
		margin-bottom: 0rpx;
		gap: 30rpx;
	}

	.form-col {
		flex: 1;
		min-width: 200rpx;
	}

	.form-col-full {
		flex: 100%;
	}

	.form-item {
		margin-bottom: 30rpx;

		.form-label {
			display: block;
			color: $text-secondary;
			font-size: 28rpx;
			margin-bottom: 15rpx;
			font-weight: 500;
		}

		.form-input {
			background: rgba($primary-color, 0.05);
			border: 1px solid rgba($primary-color, 0.2);
			color: $text-primary;
			border-radius: 12rpx;
			padding: 20rpx;
			width: 100%;
			height: 100%;
			box-sizing: border-box;
			font-size: 28rpx;

			&:focus {
				border-color: $primary-color;
			}
		}

		.form-picker {
			background: rgba($primary-color, 0.05);
			border: 1px solid rgba($primary-color, 0.2);
			color: $text-primary;
			border-radius: 12rpx;
			padding: 20rpx;
			text-align: center;
			font-size: 28rpx;
			transition: all 0.3s ease;

			&:active {
				background: rgba($primary-color, 0.1);
			}
		}

		.form-textarea {
			background: rgba($primary-color, 0.05);
			border: 1px solid rgba($primary-color, 0.2);
			color: $text-primary;
			border-radius: 12rpx;
			padding: 20rpx;
			width: 100%;
			height: 200rpx;
			box-sizing: border-box;
			font-size: 28rpx;

			&:focus {
				border-color: $primary-color;
			}
		}
	}

	.dialog-footer {
		display: flex;
		padding: 30rpx;
		border-top: 1px solid rgba($primary-color, 0.1);
		gap: 30rpx;

		.dialog-btn {
			flex: 1;
			padding: 4rpx;
			border-radius: 12rpx;
			font-size: 28rpx;
			font-weight: 500;
			transition: all 0.3s ease;

			&.cancel {
				background: rgba($primary-color, 0.1);
				border: 1px solid rgba($primary-color, 0.2);
				color: $text-primary;

				&:active {
					background: rgba($primary-color, 0.2);
				}
			}

			&.save {
				background: linear-gradient(135deg, $primary-color, $accent-color);
				border: none;
				color: #fff;

				&:active {
					transform: scale(0.98);
				}
			}
		}
	}

	/* 响应式设计 */
	@media (max-width: 768px) {
		.search-container {
			flex-direction: column;
			align-items: flex-start;
		}

		.search-item {
			margin-bottom: 20rpx;
			width: 100%;
		}

		.search-input {
			width: 100%;
		}

		.pagination {
			flex-direction: column;
			align-items: flex-start;
		}

		.pagination-controls {
			margin: 20rpx 0;
		}

		.page-size-selector {
			width: 100%;
			justify-content: flex-end;
		}
	}
</style>