<template>
	<view class="type-manage-page">
		<!-- 头部操作栏 -->
		<view class="header">
			<view class="header-left">
				<text class="title">知识类型管理</text>
			</view>
			<view class="header-right">
				<button class="add-btn" @click="openAddDialog">
					<uni-icons type="plus" size="20" color="#fff"></uni-icons>
					<text>新增类型</text>
				</button>
			</view>
		</view>

		<!-- 搜索和筛选 -->
		<view class="search-section">
			<view class="search-input">
				<uni-icons type="search" size="20" color="#999"></uni-icons>
				<input class="input" v-model="searchKeyword" placeholder="搜索类型名称或备注..." @input="onSearch"
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
				<button class="filter-btn" @click="loadTypeList">
					<uni-icons type="refresh" size="16"></uni-icons>
					<text>刷新</text>
				</button>
			</view>
		</view>

		<!-- 类型列表 -->
		<view class="type-list">
			<view v-if="loading" class="loading-container">
				<uni-load-more status="loading"></uni-load-more>
			</view>

			<view v-else-if="filteredTypes.length === 0" class="empty-state">
				<uni-icons type="compose" size="60" color="#ccc"></uni-icons>
				<text class="empty-text">暂无类型数据</text>
				<text class="empty-subtext">点击上方按钮添加类型</text>
			</view>

			<view v-else>
				<view v-for="type in sortedTypes" :key="type.id" class="type-item">
					<view class="type-info">
						<view class="type-details">
							<view class="type-name-row">
								<text class="type-name">{{ type.name }}</text>
								<text class="type-id">ID: {{ type.id }}</text>
							</view>
							<view class="type-note">
								<text class="note-label">备注:</text>
								<text class="note-content">{{ type.note || '无备注信息' }}</text>
							</view>
						</view>
					</view>

					<view class="type-actions">
						<button class="action-btn edit" @click="openEditDialog(type)">
							<uni-icons type="compose" size="16" color="#1890ff"></uni-icons>
							<text>编辑</text>
						</button>
						<button class="action-btn delete" @click="confirmDelete(type)">
							<uni-icons type="trash" size="16" color="#ff4444"></uni-icons>
							<text>删除</text>
						</button>
					</view>
				</view>
			</view>
		</view>

		<!-- 添加/编辑弹窗 -->
		<uni-popup ref="typeDialog" type="dialog">
			<view class="dialog-content">
				<view class="dialog-header">
					<text class="dialog-title">{{ isEditing ? '编辑类型' : '添加新类型' }}</text>
					<button class="close-btn" @click="closeDialog">
						<uni-icons type="close" size="20" color="#999"></uni-icons>
					</button>
				</view>

				<view class="form-content">
					<!-- 类型名称 -->
					<view class="form-item">
						<text class="form-label">类型名称</text>
						<input class="form-input" v-model="currentType.name" placeholder="请输入类型名称" maxlength="20"
							placeholder-style="color: #aaa;" @input="checkTypeNameDuplicate" />
						<view class="input-info">
							<text class="char-count">{{ currentType.name.length }}/20</text>
							<text v-if="nameDuplicateError" class="error-text">{{ nameDuplicateError }}</text>
						</view>
					</view>

					<!-- 备注信息 -->
					<view class="form-item">
						<text class="nutrition-label">备注信息</text>
						<textarea class="form-textarea" v-model="currentType.note" placeholder="请输入备注信息（可选）"
							maxlength="100" placeholder-style="color: #aaa;" auto-height />
						<text class="char-count">{{ currentType.note ? currentType.note.length : 0 }}/100</text>
					</view>

					<!-- 重复类型提示 -->
					<view v-if="duplicateTypes.length > 0 && !isEditing" class="duplicate-section">
						<text class="duplicate-title">⚠️ 注意：以下类型名称可能重复</text>
						<view class="duplicate-list">
							<view v-for="duplicate in duplicateTypes" :key="duplicate.id" class="duplicate-item">
								<text class="duplicate-name">{{ duplicate.name }}</text>
								<text v-if="duplicate.note" class="duplicate-note">{{ duplicate.note }}</text>
							</view>
						</view>
					</view>
				</view>

				<view class="dialog-actions">
					<button class="dialog-btn cancel" @click="closeDialog">取消</button>
					<button class="dialog-btn confirm" @click="saveType">
						{{ isEditing ? '保存修改' : '添加类型' }}
					</button>
				</view>
			</view>
		</uni-popup>

		<!-- 删除确认弹窗 -->
		<uni-popup ref="deleteDialog" type="dialog">
			<view class="delete-dialog">
				<text class="delete-title">确认删除</text>
				<text class="delete-content">确定要删除类型 "{{ deleteType ? deleteType.name : '' }}" 吗？</text>
				<text class="delete-warning">此操作不可撤销！</text>
				<view class="delete-actions">
					<button class="delete-btn cancel" @click="cancelDelete">取消</button>
					<button class="delete-btn confirm" @click="deleteTypeConfirm">确认删除</button>
				</view>
			</view>
		</uni-popup>

		<!-- 底部统计 -->
		<view class="footer-tip">
			<text>共 {{ typeList.length }} 个类型</text>
			<text> | </text>
			<text>最多可创建 100 个类型</text>
		</view>
	</view>
</template>

<script>
	import {
		typeApi
	} from '@/api/index.js';

	export default {
		data() {
			return {
				// 类型列表
				typeList: [],
				filteredTypes: [],
				loading: false,

				// 搜索和排序
				searchKeyword: '',
				sortBy: 'id', // 'id' 或 'name'

				// 当前操作类型
				currentType: {
					id: null,
					name: '',
					note: ''
				},
				isEditing: false,
				nameDuplicateError: '',
				duplicateTypes: [], // 重复类型列表

				// 删除相关
				deleteType: null
			}
		},
		computed: {
			// 排序后的类型
			sortedTypes() {
				if (!this.filteredTypes.length) return [];

				return [...this.filteredTypes].sort((a, b) => {
					if (this.sortBy === 'id') {
						return a.id - b.id;
					} else {
						return a.name.localeCompare(b.name);
					}
				});
			},

			// 表单验证
			isFormValid() {
				const name = this.currentType.name.trim();

				// 验证名称非空
				if (!name) return false;

				// 名称长度限制
				if (name.length > 20) return false;

				return true;
			}
		},
		onLoad() {
			this.loadTypeList();
		},
		methods: {
			// 加载类型列表
			async loadTypeList() {
				try {
					this.loading = true;
					const res = await typeApi._typeAll();

					if (res.status === 200) {
						this.typeList = res.data || [];
						this.filteredTypes = [...this.typeList];
					} else {
						uni.showToast({
							title: '加载失败: ' + (res.message || '未知错误'),
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('加载类型列表失败:', error);
					uni.showToast({
						title: '加载失败，请检查网络',
						icon: 'none'
					});
				} finally {
					this.loading = false;
				}
			},

			// 搜索类型
			onSearch() {
				if (!this.searchKeyword.trim()) {
					this.filteredTypes = [...this.typeList];
					return;
				}

				const keyword = this.searchKeyword.toLowerCase().trim();
				this.filteredTypes = this.typeList.filter(type =>
					type.name.toLowerCase().includes(keyword) ||
					(type.note && type.note.toLowerCase().includes(keyword)) ||
					type.id.toString().includes(keyword)
				);
			},

			// 清除搜索
			clearSearch() {
				this.searchKeyword = '';
				this.filteredTypes = [...this.typeList];
			},

			// 检查类型名是否重复
			async checkTypeNameDuplicate() {
				const name = this.currentType.name.trim();

				// 清空之前的错误信息
				this.nameDuplicateError = '';
				this.duplicateTypes = [];

				// 如果名称为空或正在编辑且名称未改变，则不需要检查
				if (!name || (this.isEditing && name === this.originalName)) {
					return;
				}

				// 从当前列表中查找是否有相同名称（不区分大小写）
				const existingTypes = this.typeList.filter(type => {
					// 编辑时排除自身
					if (this.isEditing && type.id === this.currentType.id) {
						return false;
					}
					return type.name.toLowerCase() === name.toLowerCase();
				});

				if (existingTypes.length > 0) {
					this.nameDuplicateError = '该类型名称已存在';
					this.duplicateTypes = existingTypes;
					return;
				}

				// 调用API检查类型名是否重复
				try {
					const res = await typeApi._typeByName(name);

					if (res.status === 200 && res.data.length > 0) {
						// 编辑时需要排除自身
						if (this.isEditing) {
							const isSelf = res.data.id === this.currentType.id;
							if (!isSelf) {
								this.nameDuplicateError = '该类型名称已存在';
								this.duplicateTypes = [res.data];
							}
						} else {
							this.nameDuplicateError = '该类型名称已存在';
							this.duplicateTypes = [res.data];
						}
					}
				} catch (error) {

					console.log('后端类型名检查失败:', error);
				}
			},

			// 打开添加弹窗
			openAddDialog() {
				this.isEditing = false;
				this.currentType = {
					id: null,
					name: '',
					note: ''
				};
				this.nameDuplicateError = '';
				this.duplicateTypes = [];
				this.$refs.typeDialog.open();
			},

			// 打开编辑弹窗
			openEditDialog(type) {
				this.isEditing = true;
				this.currentType = {
					...type
				};
				this.originalName = type.name; // 保存原始名称用于比较
				this.nameDuplicateError = '';
				this.duplicateTypes = [];
				this.$refs.typeDialog.open();
			},

			// 关闭弹窗
			closeDialog() {
				this.$refs.typeDialog.close();
			},

			// 保存类型（添加或编辑）
			async saveType() {
				if (!this.isFormValid) {
					uni.showToast({
						title: '请填写正确的类型信息',
						icon: 'none'
					});
					return;
				}

				// 检查是否有重复名称错误
				if (this.nameDuplicateError) {
					uni.showToast({
						title: this.nameDuplicateError,
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
						// 编辑类型
						const typeData = {
							id: this.currentType.id,
							name: this.currentType.name.trim(),
							note: this.currentType.note ? this.currentType.note.trim() : ''
						};
						res = await typeApi._updTypeById(typeData);

						if (res.status === 200) {
							// 更新本地列表
							const index = this.typeList.findIndex(t => t.id === typeData.id);
							if (index !== -1) {
								this.typeList[index] = {
									...typeData
								};
								this.onSearch(); // 重新过滤
							}
							uni.showToast({
								title: '类型更新成功',
								icon: 'success'
							});
							this.closeDialog();
						} else {
							uni.showToast({
								title: res.message || '更新失败',
								icon: 'none'
							});
						}
					} else {
						// 添加类型
						const typeData = {
							tName: this.currentType.name.trim()
						};
						res = await typeApi._addType(typeData);

						if (res.status === 200) {
							this.loadTypeList();
							uni.showToast({
								title: '类型添加成功',
								icon: 'success'
							});
							this.closeDialog();
						} else {
							// 检查是否是重复名称的错误
							if (res.message && res.message.includes('重复') || res.message.includes('已存在')) {
								uni.showToast({
									title: '类型名称已存在，请修改名称',
									icon: 'none'
								});
								this.nameDuplicateError = '类型名称已存在';

								// 尝试从后端获取重复的类型信息
								try {
									const duplicateRes = await typeApi._typeByName(typeData.name);
									if (duplicateRes.status === 200 && duplicateRes.data) {
										this.duplicateTypes = [duplicateRes.data];
									}
								} catch (error) {
									console.log('获取重复类型信息失败:', error);
								}
							} else {
								uni.showToast({
									title: res.message || '添加失败',
									icon: 'none'
								});
							}
						}
					}
				} catch (error) {
					console.error('保存类型失败:', error);
					uni.showToast({
						title: '保存失败: ' + (error.message || '未知错误'),
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			// 确认删除
			confirmDelete(type) {
				this.deleteType = type;
				this.$refs.deleteDialog.open();
			},

			// 取消删除
			cancelDelete() {
				this.deleteType = null;
				this.$refs.deleteDialog.close();
			},

			// 执行删除
			async deleteTypeConfirm() {
				if (!this.deleteType) return;

				try {
					uni.showLoading({
						title: '删除中...'
					});

					const res = await typeApi._delTypeById(this.deleteType.id);

					if (res.status === 200) {
						// 从本地列表中移除
						const index = this.typeList.findIndex(t => t.id === this.deleteType.id);
						if (index !== -1) {
							this.typeList.splice(index, 1);
							this.onSearch(); // 重新过滤
						}

						uni.showToast({
							title: '类型删除成功',
							icon: 'success'
						});
					} else {
						uni.showToast({
							title: res.message || '删除失败',
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('删除类型失败:', error);
					uni.showToast({
						title: '删除失败: ' + (error.message || '未知错误'),
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
					this.$refs.deleteDialog.close();
					this.deleteType = null;
				}
			}
		}
	}
</script>

<style lang="scss">
	@import "@/scss/variables.scss";

	.type-manage-page {
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
						background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
						color: #fff;
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

		/* 类型列表 */
		.type-list {
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

			.type-item {
				background: #fff;
				border-radius: 16rpx;
				padding: 32rpx;
				margin-bottom: 20rpx;
				box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
				display: flex;
				justify-content: space-between;
				align-items: center;

				.type-info {
					flex: 1;
					display: flex;
					align-items: center;
					gap: 24rpx;

					.type-details {
						flex: 1;

						.type-name-row {
							display: flex;
							justify-content: space-between;
							align-items: center;
							margin-bottom: 12rpx;

							.type-name {
								font-size: 32rpx;
								font-weight: 600;
								color: #333;
							}

							.type-id {
								font-size: 24rpx;
								color: #999;
								background: #f5f7fa;
								padding: 4rpx 12rpx;
								border-radius: 12rpx;
							}
						}

						.type-note {
							display: flex;
							align-items: flex-start;
							gap: 12rpx;

							.note-label {
								font-size: 26rpx;
								color: #666;
								white-space: nowrap;
							}

							.note-content {
								font-size: 26rpx;
								color: #999;
								flex: 1;
								word-break: break-word;
							}
						}
					}
				}

				.type-actions {
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
			max-height: 80vh;
			overflow-y: auto;

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

				.form-textarea {
					border: 2rpx solid #e8e8e8;
					border-radius: 12rpx;
					padding: 24rpx;
					font-size: 28rpx;
					min-height: 120rpx;
					background: #f8f9fa;

					&:focus {
						border-color: #1890ff;
						background: #fff;
					}
				}

				.input-info {
					display: flex;
					justify-content: space-between;
					align-items: center;
					margin-top: 8rpx;
				}

				.char-count {
					font-size: 24rpx;
					color: #999;
				}

				.error-text {
					font-size: 24rpx;
					color: #ff4444;
				}

				.duplicate-section {
					background: #fff7e6;
					border-radius: 12rpx;
					padding: 20rpx;
					margin-top: 20rpx;
					border: 1rpx solid #ffd591;

					.duplicate-title {
						font-size: 26rpx;
						color: #fa8c16;
						font-weight: 500;
						display: block;
						margin-bottom: 12rpx;
					}

					.duplicate-list {
						.duplicate-item {
							display: flex;
							justify-content: space-between;
							align-items: center;
							padding: 12rpx 0;
							border-bottom: 1rpx dashed #ffd591;

							&:last-child {
								border-bottom: none;
							}

							.duplicate-name {
								font-size: 26rpx;
								color: #333;
								font-weight: 500;
							}

							.duplicate-note {
								font-size: 24rpx;
								color: #666;
							}
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