<template>
	<view class="user-list-view">
		<!-- 搜索区域 -->
		<view class="search-container">
			<view class="search-row">
				<view class="search-item">
					<text class="search-label">流水类型：</text>
					<input class="search-input" placeholder="输入需要搜索/新增的消费类型" v-model="inputTagName"
						placeholder-class="placeholder-style" />
				</view>
				<view class="action-buttons">
					<button class="btn-primary" @click="searchByName" size="mini">
						<text class="iconfont icon-search"></text>
						搜索
					</button>
					<button class="btn-success" @click="addTagBtn" size="mini">
						<text class="iconfont icon-plus"></text>
						添加
					</button>
				</view>
			</view>
		</view>

		<!-- 表格区域 -->
		<view class="table-container">
			<view class="table-header">
				<view class="table-cell" style="width: 150rpx;">编码</view>
				<view class="table-cell" style="width: 150rpx;">流水类型</view>
				<view class="table-cell" style="flex: 1;">关键词</view>
				<view class="table-cell" style="width: 200rpx;">操作</view>
			</view>

			<scroll-view class="table-body" scroll-y :style="{height: screenHeight + 'px'}"
				v-if="!pagination.listLoading">
				<view class="table-row" v-for="(item, index) in pagination.currentPageData" :key="index"
					:class="index % 2 === 0 ? 'even-row' : 'odd-row'">
					<view class="table-cell" style="width: 150rpx;">
						<text>{{ item.id }}</text>
					</view>
					<view class="table-cell" style="width: 150rpx;">
						<text class="tag" :class="item.color || 'success'">
							{{ item.name }}
						</text>
					</view>
					<view class="table-cell" style="flex: 1;">
						<text>{{ item.keywords | tableKeys }}</text>
					</view>
					<view class="table-cell" style="width: 200rpx;">
						<button class="btn-warning" @click="handleEdit(index, item)" size="mini">编辑</button>
						<button class="btn-danger" @click="handleDelete(index, item)" size="mini">删除</button>
					</view>
				</view>
			</scroll-view>

			<!-- 加载中状态 -->
			<view class="loading-container" v-if="pagination.listLoading">
				<text>加载中...</text>
			</view>
		</view>

		<!-- 分页 -->
		<view class="pagination-container">
			<view class="pagination-info">共 {{ pagination.total }} 条</view>
			<view class="pagination-controls">
				<button class="pagination-btn" :disabled="pagination.currentPage === 1"
					@click="handleCurrentChange(pagination.currentPage - 1)">
					上一页
				</button>
				<text class="pagination-page">{{ pagination.currentPage }}/{{ pagination.totalPage }}</text>
				<button class="pagination-btn" :disabled="pagination.currentPage === pagination.totalPage"
					@click="handleCurrentChange(pagination.currentPage + 1)">
					下一页
				</button>
			</view>
			<picker class="page-size-picker" :value="pageSizeIndex" :range="pageSizeOptions" @change="handleSizeChange">
				<view class="picker">
					{{ pagination.pageSize }} 条/页
				</view>
			</picker>
		</view>

		<!-- 修改分类的弹窗 -->
		<uni-popup ref="updateTagPopup" type="dialog">
			<uni-popup-dialog type="info" title="编辑信息" :before-close="true" @close="handleCloseUpdateDialog"
				@confirm="handleConfirmUpdate">
				<view class="update-type" v-if="editTag != null">
					<text class="label">编码：</text>
					<input class="input-field" v-model="editTag.id" disabled />

					<text class="label">消费类型：</text>
					<input class="input-field" v-model="editTag.name" />

					<text class="label">关键词（多个关键词用、号分割开）：</text>
					<input class="input-field" v-model="editTag.keywords" />

					<text class="label">选择tag颜色：</text>
					<picker class="color-picker" :value="colorIndex" :range="selectTagOptions" range-key="label"
						@change="onColorChange">
						<view class="picker">
							{{ selectTagOptions[colorIndex].label }}
						</view>
					</picker>
				</view>
			</uni-popup-dialog>
		</uni-popup>
	</view>
</template>

<script>
	import {
		lifeApi
	} from "@/api/index.js"

	export default {
		name: "ConsumptionTypeView",
		data() {
			return {
				screenHeight: 500,
				inputTagName: "",
				editTag: {
					id: "",
					name: "",
					keywords: "",
					color: "",
				},
				pagination: {
					list: null,
					listLoading: true,
					totalPage: 1, // 统共页数，默认为1
					currentPage: 1, //当前页数 ，默认为1
					pageSize: 10, // 每页显示数量
					currentPageData: [], //当前页显示内容
					total: 1 //总数量
				},
				selectTagOptions: [{
						value: '',
						label: '默认'
					},
					{
						value: 'primary',
						label: '主要'
					},
					{
						value: 'success',
						label: '成功'
					},
					{
						value: 'info',
						label: '信息'
					},
					{
						value: 'warning',
						label: '警告'
					},
					{
						value: 'danger',
						label: '重要'
					},
				],
				colorIndex: 0,
				pageSizeOptions: [5, 10, 15, 20],
				pageSizeIndex: 1 // 默认选中10条/页
			}
		},
		filters: {
			tableKeys(value) {
				if (!value) return "";
				if (value.length > 60) {
					return value.slice(0, 60) + "...";
				}
				return value;
			}
		},
		onLoad() {
			this.updateScreenHeight();
			this.getTagALl();
		},
		onReady() {
			uni.onWindowResize((res) => {
				this.updateScreenHeight();
			});
		},
		onUnload() {
			// uni.offWindowResize();
		},
		methods: {
			updateScreenHeight() {
				// 获取屏幕可用高度，减去搜索区域和分页区域的高度
				const systemInfo = uni.getSystemInfoSync();
				this.screenHeight = systemInfo.windowHeight - 220;
			},
			// 分页方法
			getCurrentPageData(currentPage, pageSize) {
				let begin = (currentPage - 1) * pageSize;
				let end = currentPage * pageSize;
				this.pagination.currentPageData = this.pagination.list.slice(
					begin,
					end
				);
			},
			handleSizeChange(e) {
				this.pageSizeIndex = e.detail.value;
				this.pagination.pageSize = this.pageSizeOptions[this.pageSizeIndex];
				this.getCurrentPageData(this.pagination.currentPage, this.pagination.pageSize);
			},
			handleCurrentChange(val) {
				this.pagination.currentPage = val;
				this.getCurrentPageData(this.pagination.currentPage, this.pagination.pageSize);
			},
			// 获取所有分类
			getTagALl() {
				lifeApi._getConsumptionTypeAll().then(res => {
					if (res.status === 200) {
						this.pagination.list = res.data;
						this.pagination.listLoading = false;
						this.pagination.total = this.pagination.list.length;

						this.pagination.totalPage = Math.ceil(this.pagination.list.length / this.pagination
							.pageSize);
						this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;
						this.getCurrentPageData(this.pagination.currentPage, this.pagination.pageSize);
					}
				})
			},
			// 表格操作-删除标签
			handleDelete(index, row) {
				uni.showModal({
					title: '提示',
					content: '此操作将永久删除该消费类型, 是否继续?',
					confirmText: '确定',
					cancelText: '取消',
					success: (res) => {
						if (res.confirm) {
							lifeApi._delConsumptionType(row.id).then(res => {
								console.log(res)
								if (res.status === 200) {
									uni.showToast({
										title: '删除成功!',
										icon: 'success'
									});
									this.getTagALl();
								} else {
									uni.showToast({
										title: res.msg,
										icon: 'none'
									});
								}
							})
						} else if (res.cancel) {
							uni.showToast({
								title: '已取消删除',
								icon: 'none'
							});
						}
					}
				});
			},
			// 表格操作-编辑
			handleEdit(index, row) {
				this.editTag = {
					...row
				};
				// 设置当前颜色对应的索引
				const colorIndex = this.selectTagOptions.findIndex(item => item.value === row.color);
				this.colorIndex = colorIndex >= 0 ? colorIndex : 0;
				this.$refs.updateTagPopup.open();
			},
			// 颜色选择变化
			onColorChange(e) {
				this.colorIndex = e.detail.value;
				this.editTag.color = this.selectTagOptions[this.colorIndex].value;
			},
			// 关闭编辑对话框
			handleCloseUpdateDialog() {
				this.$refs.updateTagPopup.close();
			},
			// 确认修改
			handleConfirmUpdate() {
				lifeApi._updConsumptionTypeById(this.editTag).then(res => {
					if (res.status === 200) {
						uni.showToast({
							title: res.msg,
							icon: 'success'
						});
						this.$refs.updateTagPopup.close();
						this.getTagALl();
					} else {
						uni.showToast({
							title: res.msg,
							icon: 'none'
						});
					}
				})
			},
			// 模糊搜索
			searchByName() {
				if (this.inputTagName == '') {
					this.getTagALl();
				} else {
					lifeApi._getConsumptionTypeByName(this.inputTagName).then(res => {
						if (res.status === 200) {
							this.pagination.list = res.data;
							this.pagination.listLoading = false;
							this.pagination.total = this.pagination.list.length;

							this.pagination.totalPage = Math.ceil(this.pagination.list.length / this.pagination
								.pageSize);
							this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination
								.totalPage;
							this.getCurrentPageData(this.pagination.currentPage, this.pagination.pageSize);
						} else {
							uni.showToast({
								title: res.data.msg,
								icon: 'none'
							});
						}
					})
				}
			},
			// 添加标签
			addTagBtn() {
				if (this.inputTagName == '') {
					uni.showToast({
						title: "请输入标签名称",
						icon: 'none'
					});
				} else {
					let isExist = this.pagination.list.some((item) => {
						return item.name.toLowerCase() === this.inputTagName.toLowerCase();
					});
					if (isExist) {
						uni.showToast({
							title: "标签已存在",
							icon: 'none'
						});
					} else {
						let postData = {
							"tName": this.inputTagName
						};
						lifeApi._addConsumptionType(postData).then(res => {
							if (res.status === 200) {
								uni.showToast({
									title: "标签创建成功",
									icon: 'success'
								});
								this.getTagALl();
								this.inputTagName = '';
							} else {
								uni.showToast({
									title: res.msg,
									icon: 'none'
								});
							}
						})
					}
				}
			}
		}
	}
</script>

<style>
	/* 主容器 */
	.user-list-view {
		padding: 20rpx;
		background-color: #f5f7fa;
		min-height: 100vh;
	}

	/* 搜索区域 */
	.search-container {
		background-color: #ffffff;
		border-radius: 8rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 12rpx 0 rgba(0, 0, 0, 0.1);
	}

	.search-row {
		display: flex;
		align-items: center;
	}

	.search-item {
		flex: 1;
		display: flex;
		align-items: center;
		margin-right: 20rpx;
	}

	.search-label {
		font-size: 28rpx;
		color: #606266;
		margin-right: 20rpx;
		white-space: nowrap;
	}

	.search-input {
		flex: 1;
		height: 64rpx;
		border: 1rpx solid #dcdfe6;
		border-radius: 8rpx;
		padding: 0 20rpx;
		font-size: 28rpx;
	}

	.placeholder-style {
		color: #c0c4cc;
	}

	.action-buttons {
		display: flex;
	}

	.action-buttons button {
		margin-left: 20rpx;
		height: 64rpx;
		line-height: 64rpx;
		font-size: 24rpx;
	}

	/* 按钮样式 */
	.btn-primary {
		background-color: #409eff;
		color: white;
		border: none;
		
		&:active {
			transform: scale(0.95);
		}
	}

	.btn-success {
		background-color: #67c23a;
		color: white;
		border: none;
		
		&:active {
			transform: scale(0.95);
		}
	}

	.btn-warning {
		background-color: #e6a23c;
		color: white;
		border: none;
		margin-right: 10rpx;
		
		&:active {
			transform: scale(0.95);
		}
	}

	.btn-danger {
		background-color: #f56c6c;
		color: white;
		border: none;
		
		&:active {
			transform: scale(0.95);
		}
	}

	/* 表格样式 */
	.table-container {
		background-color: #ffffff;
		border-radius: 8rpx;
		overflow: hidden;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 12rpx 0 rgba(0, 0, 0, 0.1);
	}

	.table-header {
		display: flex;
		background-color: #f0f7ff;
		border-bottom: 1rpx solid #ebeef5;
	}

	.table-cell {
		padding: 24rpx 10rpx;
		text-align: center;
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		border-right: 1rpx solid #ebeef5;
	}

	.table-cell:last-child {
		border-right: none;
	}

	.table-body {
		/* max-height: 800rpx; */
	}

	.table-row {
		display: flex;
		border-bottom: 1rpx solid #ebeef5;
	}

	.even-row {
		background-color: #ffffff;
	}

	.odd-row {
		background-color: #fafafa;
	}

	.table-row .table-cell {
		font-weight: normal;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	/* 标签样式 */
	.tag {
		display: inline-block;
		padding: 8rpx 16rpx;
		border-radius: 4rpx;
		font-size: 24rpx;
		line-height: 1;
		white-space: nowrap;
	}

	.primary {
		background-color: #409eff;
		color: white;
	}

	.success {
		background-color: #67c23a;
		color: white;
	}

	.info {
		background-color: #909399;
		color: white;
	}

	.warning {
		background-color: #e6a23c;
		color: white;
	}

	.danger {
		background-color: #f56c6c;
		color: white;
	}

	/* 加载中样式 */
	.loading-container {
		padding: 100rpx 0;
		text-align: center;
		color: #909399;
	}

	/* 分页样式 */
	.pagination-container {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx;
		background-color: #ffffff;
		border-radius: 8rpx;
		box-shadow: 0 2rpx 12rpx 0 rgba(0, 0, 0, 0.1);
	}

	.pagination-info {
		color: #606266;
		font-size: 26rpx;
	}

	.pagination-controls {
		display: flex;
		align-items: center;
	}

	.pagination-btn {
		padding: 12rpx 24rpx;
		font-size: 26rpx;
		background-color: #ffffff;
		border: 1rpx solid #dcdfe6;
		color: #606266;
	}

	.pagination-btn:disabled {
		color: #c0c4cc;
		background-color: #f5f7fa;
	}

	.pagination-page {
		margin: 0 20rpx;
		font-size: 26rpx;
		color: #606266;
	}

	.page-size-picker {
		width: 150rpx;
	}

	.picker {
		padding: 12rpx 24rpx;
		border: 1rpx solid #dcdfe6;
		border-radius: 8rpx;
		text-align: center;
		font-size: 26rpx;
		color: #606266;
	}

	/* 编辑表单样式 */
	.update-type {
		padding: 20rpx 0;
	}

	.label {
		display: block;
		font-size: 28rpx;
		color: #606266;
		margin-bottom: 10rpx;
		margin-top: 20rpx;
	}

	.input-field {
		width: 100%;
		height: 70rpx;
		border: 1rpx solid #dcdfe6;
		border-radius: 8rpx;
		padding: 0 20rpx;
		font-size: 28rpx;
		margin-bottom: 20rpx;
	}

	.input-field:disabled {
		background-color: #f5f7fa;
		color: #c0c4cc;
	}

	.color-picker {
		width: 100%;
		margin-bottom: 20rpx;
	}
</style>