<template>
	<view class="tag-manage-page">
		<!-- 头部操作栏 -->
		<view class="header">
			<view class="header-left">
				<text class="title">知识标签管理</text>
			</view>
			<view class="header-right">
				<button class="add-btn" @click="openAddDialog">
					<uni-icons type="plus" size="20" color="#fff"></uni-icons>
					<text>新增标签</text>
				</button>
			</view>
		</view>

		<!-- 搜索和筛选 -->
		<view class="search-section">
			<view class="search-input">
				<uni-icons type="search" size="20" color="#999"></uni-icons>
				<input class="input" v-model="searchKeyword" placeholder="搜索标签名称..." @input="onSearch"
					placeholder-style="color: #aaa;" />
				<view v-if="searchKeyword" class="clear-icon" @click="clearSearch">
					<uni-icons type="clear" size="18" color="#999"></uni-icons>
				</view>
			</view>
			<view class="filter-buttons">
				<button class="filter-btn" :class="{ active: sortBy === 'id' }" @click="sortBy = 'id'">
					<text>按ID排序</text>
				</button>
				<button class="filter-btn" :class="{ active: sortBy === 'name' }" @click="sortBy = 'name'">
					<text>按名称排序</text>
				</button>
				<button class="filter-btn" @click="loadTagList">
					<uni-icons type="refresh" size="16"></uni-icons>
					<text>刷新</text>
				</button>
			</view>
		</view>

		<!-- 标签列表 -->
		<view class="tag-list">
			<view v-if="loading" class="loading-container">
				<uni-load-more status="loading"></uni-load-more>
			</view>

			<view v-else-if="filteredTags.length === 0" class="empty-state">
				<uni-icons type="tags" size="60" color="#ccc"></uni-icons>
				<text class="empty-text">暂无标签数据</text>
				<text class="empty-subtext">点击上方按钮添加标签</text>
			</view>

			<view v-else>
				<view v-for="tag in sortedTags" :key="tag.id" class="tag-item">
					<view class="tag-info">
						<view class="tag-details">
							<view class="tag-name-row">
								<text class="tag-name">{{ tag.name }}</text>
								<text class="tag-id">ID: {{ tag.id }}</text>
							</view>
							<view class="color-display">
								<text class="color-label">颜色:</text>
								<text class="color-value">{{ tag.color }}</text>
								<view class="color-sample" :style="{ backgroundColor: tag.color }"></view>
							</view>
						</view>
					</view>

					<view class="tag-actions">
						<button class="action-btn edit" @click="openEditDialog(tag)">
							<uni-icons type="compose" size="16" color="#1890ff"></uni-icons>
							<text>编辑</text>
						</button>
						<button class="action-btn delete" @click="confirmDelete(tag)">
							<uni-icons type="trash" size="16" color="#ff4444"></uni-icons>
							<text>删除</text>
						</button>
					</view>
				</view>
			</view>
		</view>

		<!-- 添加/编辑弹窗 -->
		<uni-popup ref="tagDialog" type="dialog">
			<view class="dialog-content">
				<view class="dialog-header">
					<text class="dialog-title">{{ isEditing ? '编辑标签' : '添加新标签' }}</text>
					<button class="close-btn" @click="closeDialog">
						<uni-icons type="close" size="20" color="#999"></uni-icons>
					</button>
				</view>

				<view class="form-content">
					<!-- 标签名称 -->
					<view class="form-item">
						<text class="form-label">标签名称</text>
						<input class="form-input" v-model="currentTag.name" placeholder="请输入标签名称" maxlength="20"
							placeholder-style="color: #aaa;" />
						<text class="char-count">{{ currentTag.name.length }}/20</text>
					</view>

					<!-- 颜色选择 -->
					<view class="form-item">
						<text class="nutrition-label">标签颜色</text>
						<view class="color-selector">
							<view class="color-preview-large" :style="{ backgroundColor: currentTag.color }"></view>
							<input class="color-input" v-model="currentTag.color" placeholder="#1890ff"
								placeholder-style="color: #aaa;" />
							<text class="color-format">(十六进制颜色码)</text>
						</view>

						<view class="color-presets">
							<text class="presets-label">预设颜色:</text>
							<view class="presets-grid">
								<view v-for="color in colorPresets" :key="color" class="preset-color"
									:style="{ backgroundColor: color }"
									:class="{ selected: currentTag.color === color }" @click="currentTag.color = color">
									<uni-icons v-if="currentTag.color === color" type="checkmarkempty" size="16"
										color="#fff"></uni-icons>
								</view>
							</view>
						</view>

						<view class="color-examples">
							<text class="example-label">预览效果:</text>
							<view class="example-tags">
								<view class="example-tag" :style="{ 
										backgroundColor: currentTag.color,
										color: getContrastColor(currentTag.color)
									}">
									<text>{{ currentTag.name || '示例标签' }}</text>
								</view>
							</view>
						</view>
					</view>
				</view>

				<view class="dialog-actions">
					<button class="dialog-btn cancel" @click="closeDialog">取消</button>
					<button class="dialog-btn confirm" @click="saveTag" :disabled="!isFormValid">
						{{ isEditing ? '保存修改' : '添加标签' }}
					</button>
				</view>
			</view>
		</uni-popup>

		<!-- 删除确认弹窗 -->
		<uni-popup ref="deleteDialog" type="dialog">
			<view class="delete-dialog">
				<text class="delete-title">确认删除</text>
				<text class="delete-content">确定要删除标签 "{{ deleteTag ? deleteTag.name : '' }}" 吗？</text>
				<text class="delete-warning">此操作不可撤销！</text>
				<view class="delete-actions">
					<button class="delete-btn cancel" @click="cancelDelete">取消</button>
					<button class="delete-btn confirm" @click="deleteTagConfirm">确认删除</button>
				</view>
			</view>
		</uni-popup>

		<!-- 底部提示 -->
		<view class="footer-tip">
			<text>共 {{ tagList.length }} 个标签</text>
			<text> | </text>
			<text>最多可创建 50 个标签</text>
		</view>
	</view>
</template>

<script>
	import {
		tagApi
	} from '@/api/index.js';

	export default {
		data() {
			return {
				// 标签列表
				tagList: [],
				filteredTags: [],
				loading: false,

				// 搜索和排序
				searchKeyword: '',
				sortBy: 'id', // 'id' 或 'name'

				// 当前操作标签
				currentTag: {
					id: null,
					name: '',
					color: '#1890ff'
				},
				isEditing: false,

				// 删除相关
				deleteTag: null,

				// 预设颜色
				colorPresets: [
					'#1890ff', '#52c41a', '#f5222d', '#fa8c16', '#722ed1',
					'#13c2c2', '#eb2f96', '#faad14', '#2f54eb', '#a0d911'
				]
			}
		},
		computed: {
			// 排序后的标签
			sortedTags() {
				if (!this.filteredTags.length) return [];

				return [...this.filteredTags].sort((a, b) => {
					if (this.sortBy === 'id') {
						return a.id - b.id;
					} else {
						return a.name.localeCompare(b.name);
					}
				});
			},

			// 表单验证
			isFormValid() {
				const name = this.currentTag.name.trim();
				const color = this.currentTag.color;

				// 验证名称非空
				if (!name) return false;

				// 验证颜色格式（简单验证）
				const colorRegex = /^#[0-9A-F]{6}$/i;
				if(!color) {
					this.currentTag.color = '';
					return true;
				}
				else if (!colorRegex.test(color)) return false;

				return true;
			}
		},
		onLoad() {
			this.loadTagList();
		},
		methods: {
			// 加载标签列表
			async loadTagList() {
				try {
					this.loading = true;
					const res = await tagApi._getTagAll();

					if (res.status === 200) {
						this.tagList = res.data || [];
						this.filteredTags = [...this.tagList];
					} else {
						uni.showToast({
							title: '加载失败: ' + (res.message || '未知错误'),
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('加载标签列表失败:', error);
					uni.showToast({
						title: '加载失败，请检查网络',
						icon: 'none'
					});
				} finally {
					this.loading = false;
				}
			},

			// 搜索标签
			onSearch() {
				if (!this.searchKeyword.trim()) {
					this.filteredTags = [...this.tagList];
					return;
				}
				const keyword = this.searchKeyword.toLowerCase().trim();
				this.filteredTags = this.tagList.filter(tag =>
					tag.name.toLowerCase().includes(keyword)
				);
			},

			// 清除搜索
			clearSearch() {
				this.searchKeyword = '';
				this.filteredTags = [...this.tagList];
			},

			// 打开添加弹窗
			openAddDialog() {
				this.isEditing = false;
				this.currentTag = {
					id: null,
					name: '',
					color: '#1890ff'
				};
				this.$refs.tagDialog.open();
			},

			// 打开编辑弹窗
			openEditDialog(tag) {
				this.isEditing = true;
				this.currentTag = {
					...tag
				};
				this.$refs.tagDialog.open();
			},

			// 关闭弹窗
			closeDialog() {
				this.$refs.tagDialog.close();
			},

			// 保存标签（添加或编辑）
			async saveTag() {
				if (!this.isFormValid) {
					uni.showToast({
						title: '请填写正确的标签信息',
						icon: 'none'
					});
					return;
				}

				try {
					uni.showLoading({
						title: '保存中...'
					});

					let res;
					if (this.isEditing) {
						// 编辑标签
						const tagData = {
							id: this.currentTag.id,
							name: this.currentTag.name.trim(),
							color: this.currentTag.color.trim().toUpperCase()
						};
						res = await tagApi._updTagById(tagData);

						if (res.status === 200) {
							// 更新本地列表
							const index = this.tagList.findIndex(t => t.id === tagData.id);
							if (index !== -1) {
								this.tagList[index] = {
									...tagData
								};
								this.onSearch(); // 重新过滤
							}
							uni.showToast({
								title: '标签更新成功',
								icon: 'success'
							});
							this.closeDialog();
						}
					} else {
						// 添加标签
						const tagData = {
							tName: this.currentTag.name.trim()
						};
						let isExist = this.tagList.some((item) => {
							return item.name.toLowerCase() === this.currentTag.name.toLowerCase()
						});
						if (isExist) {
							uni.showToast({
								title: '标签已存在',
								icon: 'none'
							});
							return;
						}
						res = await tagApi._addTag(tagData);

						if (res.status === 200) {
							this.loadTagList();
							uni.showToast({
								title: '标签添加成功',
								icon: 'success'
							});
							this.closeDialog();
						}
					}
				} catch (error) {
					console.error('保存标签失败:', error);
					uni.showToast({
						title: '保存失败: ' + (error.message || '未知错误'),
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			// 确认删除
			confirmDelete(tag) {
				this.deleteTag = tag;
				this.$refs.deleteDialog.open();
			},

			// 取消删除
			cancelDelete() {
				this.deleteTag = null;
				this.$refs.deleteDialog.close();
			},

			// 执行删除
			async deleteTagConfirm() {
				if (!this.deleteTag) return;

				try {
					uni.showLoading({
						title: '删除中...'
					});

					const res = await tagApi._delTagById(this.deleteTag.id);

					if (res.status === 200) {
						// 从本地列表中移除
						const index = this.tagList.findIndex(t => t.id === this.deleteTag.id);
						if (index !== -1) {
							this.tagList.splice(index, 1);
							this.onSearch(); // 重新过滤
						}

						uni.showToast({
							title: '标签删除成功',
							icon: 'success'
						});
					}
				} catch (error) {
					console.error('删除标签失败:', error);
					uni.showToast({
						title: '删除失败: ' + (error.message || '未知错误'),
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
					this.$refs.deleteDialog.close();
					this.deleteTag = null;
				}
			},

			// 根据背景颜色获取对比色（用于标签文字颜色）
			getContrastColor(hexColor) {
				// 简化的对比色计算
				if (!hexColor || hexColor.length !== 7) return '#000000';

				// 将十六进制颜色转换为RGB
				const r = parseInt(hexColor.substr(1, 2), 16);
				const g = parseInt(hexColor.substr(3, 2), 16);
				const b = parseInt(hexColor.substr(5, 2), 16);

				// 计算亮度
				const brightness = (r * 299 + g * 587 + b * 114) / 1000;

				// 根据亮度返回黑色或白色
				return brightness > 128 ? '#000000' : '#ffffff';
			}
		}
	}
</script>

<style lang="scss">
	@import "@/scss/variables.scss";

	.tag-manage-page {
		min-height: 100vh;
		background: #f5f7fa;
		padding: 0 24rpx 120rpx;

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
			margin-bottom: 24rpx;
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

			.filter-buttons {
				display: flex;
				gap: 16rpx;

				.filter-btn {
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

		/* 标签列表 */
		.tag-list {
			.loading-container {
				padding: 100rpx 0;
				display: flex;
				justify-content: center;
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

			.tag-item {
				background: #fff;
				border-radius: 16rpx;
				padding: 32rpx;
				margin-bottom: 20rpx;
				box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
				display: flex;
				justify-content: space-between;
				align-items: center;

				.tag-info {
					flex: 1;
					display: flex;
					align-items: center;
					gap: 24rpx;

					.tag-details {
						flex: 1;

						.tag-name-row {
							display: flex;
							justify-content: space-between;
							align-items: center;
							margin-bottom: 12rpx;

							.tag-name {
								font-size: 32rpx;
								font-weight: 600;
								color: #333;
							}

							.tag-id {
								font-size: 24rpx;
								color: #999;
								background: #f5f7fa;
								padding: 4rpx 12rpx;
								border-radius: 12rpx;
							}
						}

						.color-display {
							display: flex;
							align-items: center;
							gap: 12rpx;

							.color-label {
								font-size: 26rpx;
								color: #666;
							}

							.color-value {
								font-size: 26rpx;
								color: #1890ff;
								font-family: 'Monaco', 'Consolas', monospace;
							}

							.color-sample {
								width: 32rpx;
								height: 32rpx;
								border-radius: 8rpx;
								border: 2rpx solid #e8e8e8;
							}
						}
					}
				}

				.tag-actions {
					display: flex;
					gap: 16rpx;

					.action-btn {
						padding: 12rpx 24rpx;
						font-size: 26rpx;
						border-radius: 20rpx;
						display: flex;
						align-items: center;
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
		}

		/* 添加/编辑弹窗 */
		.dialog-content {
			background: #fff;
			border-radius: 24rpx;
			padding: 40rpx;
			width: 90vw;
			max-width: 600rpx;

			.dialog-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-bottom: 40rpx;

				.dialog-title {
					font-size: 36rpx;
					font-weight: 600;
					color: #333;
				}

				.close-btn {
					padding: 8rpx;
					background: transparent;

					&::after {
						border: none;
					}

					&:active {
						opacity: 0.6;
					}
				}
			}

			.form-content {
				margin-bottom: 40rpx;

				.form-item {
					margin-bottom: 40rpx;

					&:last-child {
						margin-bottom: 0;
					}
				}

				.form-label {
					font-size: 30rpx;
					font-weight: 500;
					color: #333;
					display: block;
					margin-bottom: 16rpx;

					&::after {
						content: '*';
						color: #ff4444;
						margin-left: 4rpx;
					}
				}
				
				.nutrition-label {
					font-size: 30rpx;
					font-weight: 500;
					color: #333;
					display: block;
					margin-bottom: 16rpx;
				}

				.form-input {
					border: 2rpx solid #e8e8e8;
					border-radius: 12rpx;
					padding: 24rpx;
					font-size: 28rpx;
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

				.color-selector {
					display: flex;
					align-items: center;
					gap: 20rpx;
					margin-bottom: 24rpx;

					.color-preview-large {
						width: 80rpx;
						height: 80rpx;
						border-radius: 16rpx;
						box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.1);
						border: 2rpx solid #e8e8e8;
					}

					.color-input {
						flex: 1;
						border: 2rpx solid #e8e8e8;
						border-radius: 12rpx;
						padding: 20rpx;
						font-size: 28rpx;
						font-family: 'Monaco', 'Consolas', monospace;
						background: #f8f9fa;

						&:focus {
							border-color: #1890ff;
							background: #fff;
						}
					}

					.color-format {
						font-size: 24rpx;
						color: #999;
					}
				}

				.color-presets {
					margin-bottom: 32rpx;

					.presets-label {
						font-size: 26rpx;
						color: #666;
						display: block;
						margin-bottom: 16rpx;
					}

					.presets-grid {
						display: grid;
						grid-template-columns: repeat(5, 1fr);
						gap: 16rpx;

						.preset-color {
							width: 48rpx;
							height: 48rpx;
							border-radius: 10rpx;
							display: flex;
							align-items: center;
							justify-content: center;
							border: 2rpx solid #e8e8e8;

							&.selected {
								border-color: #1890ff;
								transform: scale(1.1);
							}

							&:active {
								opacity: 0.8;
							}
						}
					}
				}

				.color-examples {
					.example-label {
						font-size: 26rpx;
						color: #666;
						display: block;
						margin-bottom: 16rpx;
					}

					.example-tags {
						.example-tag {
							display: inline-block;
							padding: 16rpx 32rpx;
							border-radius: 40rpx;
							font-size: 28rpx;
							font-weight: 500;
							box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
						}
					}
				}
			}

			.dialog-actions {
				display: flex;
				gap: 24rpx;

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
		}
	}
</style>