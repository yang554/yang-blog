<template>
	<view class="clan-manager">
		<!-- 宗祠匾额 + 搜索栏 -->
		<view class="manager-header">
			<view class="clan-plaque">
				<text class="plaque-title">📖 宗 譜 堂</text>
				<text class="plaque-subtitle">—— 木本水源 · 慎终追远 ——</text>
			</view>

			<!-- 搜索框 + 新增按钮 -->
			<view class="header-tools">
				<view class="search-box">
					<text class="search-icon">🔍</text>
					<input type="text" v-model="searchKeyword" placeholder="搜索族谱名称或堂号..." @input="handleSearch" />
					<text v-if="searchKeyword" class="clear-search" @click="clearSearch">✕</text>
				</view>
				<button class="btn-create" @click="openCreateModal">新 增 族 譜</button>
			</view>
		</view>

		<!-- 谱本陈列 靠左对齐 -->
		<scroll-view class="clan-shelf" scroll-y="true">
			<view class="clan-shelf-inner">
				<view class="clan-book" v-for="book in filteredBooks" :key="book.id">
					<!-- 古籍封面 -->
					<view class="classic-cover">
						<!-- 书签题名 (堂号) -->
						<view class="classic-label">{{ book.displayName || book.name }}</view>

						<!-- 卷次信息 -->
						<view class="classic-volume" v-if="book.generation">卷 {{ book.generation }}</view>
						<view class="classic-volume" v-else>· 宗 譜 ·</view>

						<!-- 藏书印 -->
						<view class="classic-seal"></view>

						<!-- 操作按钮  -->
						<view class="book-actions">
							<button class="action-btn edit-btn" @click.stop="openEditModal(book)" title="编辑">✎</button>
							<button class="action-btn delete-btn" @click.stop="confirmDelete(book)"
								title="刪除">🗑</button>
						</view>
					</view>

					<!-- 书页厚度 -->
					<view class="classic-pages"></view>

					<!-- 谱名 (数据库name) 点击查看详情 -->
					<view class="classic-caption" @click="viewBookDetail(book)">
						{{ book.name }}
					</view>
				</view>

				<!-- 空状态 / 无搜索结果 -->
				<view v-if="filteredBooks.length === 0" class="clan-empty">
					<text v-if="pedigreeBooks.length === 0">📭 暂无族譜，请点击「新增族谱」创建</text>
					<text v-else>🔎 未找到符合「{{ searchKeyword }}」的族譜</text>
				</view>
			</view>
		</scroll-view>

		<!-- 底部信息 -->
		<view class="clan-footer">
			<text>🏮 共 {{ filteredBooks.length }} 部 / 总藏 {{ pedigreeBooks.length }} 部 · 源自「宗譜数据库」</text>
		</view>

		<!-- ========== 创建/编辑 ========== -->
		<uni-popup ref="modalPopup" type="center" background-color="rgba(30, 20, 10, 0.6)">
			<view class="modal-content">
				<view class="modal-header">
					<text class="modal-title">{{ modalMode === 'create' ? '📖 新增族譜' : '✏️ 编辑族譜' }}</text>
					<text class="close-btn" @click="closeModal">✕</text>
				</view>

				<scroll-view class="modal-body" scroll-y="true">
					<view class="form-group">
						<text class="form-label">谱名 <text class="required">*</text></text>
						<input  type="text" v-model="formData.name" placeholder="例：李氏宗譜" />
					</view>

					<view class="form-group">
						<text class="form-label">堂號 / 封面名</text>
						<input type="text" v-model="formData.displayName" placeholder="例：隴西堂" />
						<text class="form-hint">显示在封面书签上，留空则使用譜名</text>
					</view>

					<view class="form-row">
						<view class="form-group half">
							<text class="form-label">卷次</text>
							<input type="text" v-model="formData.generation" placeholder="例：一" />
						</view>
						<view class="form-group half">
							<text class="form-label">备注</text>
							<input type="text" v-model="formData.remarks" placeholder="选填" />
						</view>
					</view>
				</scroll-view>

				<view class="modal-footer">
					<button class="btn-cancel" @click="closeModal">取 消</button>
					<button class="btn-save" @click="saveBook" :disabled="!formData.name.trim()">
						{{ modalMode === 'create' ? '创 建' : '保 存' }}
					</button>
				</view>
			</view>
		</uni-popup>

		<!-- ========== 刪除确认对话框 ========== -->
		<uni-popup ref="deletePopup" type="center" background-color="rgba(30, 20, 10, 0.6)">
			<view class="modal-content confirm-modal">
				<view class="modal-header">
					<text class="modal-title">⚠️ 确认刪除</text>
					<text class="close-btn" @click="cancelDelete">✕</text>
				</view>

				<view class="modal-body">
					<text class="confirm-text">确定要刪除《{{ bookToDelete ? bookToDelete.name : '' }}》吗？</text>
					<text class="warning-text">此操作无法复原。</text>
				</view>

				<view class="modal-footer">
					<button class="btn-cancel" @click="cancelDelete">取 消</button>
					<button class="btn-delete-confirm" @click="deleteBook">确 认 刪 除</button>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	import {
		familyApi
	} from "@/api/index.js";
	import {
		getTime
	} from "@/common/formatDate.js";

	export default {
		name: 'FamilyTree',
		data() {
			return {
				// 数据
				pedigreeBooks: [],
				searchKeyword: '',

				// 弹出框控制
				showModal: false,
				showDeleteConfirm: false,
				modalMode: 'create', // 'create' 或 'edit'

				// 表单数据
				formData: {
					id: null,
					name: '',
					displayName: '',
					generation: '',
					remarks: '',
					createID: '',
					op: ''
				},

				// 待删除的族谱
				bookToDelete: null,

				// 搜索防抖计时器
				searchTimer: null,

				userInfo: {}
			};
		},

		computed: {
			// 筛选之后的族谱列表 (即時搜索)
			filteredBooks() {
				if (!this.searchKeyword.trim()) {
					return this.pedigreeBooks;
				}

				const keyword = this.searchKeyword.toLowerCase().trim();
				return this.pedigreeBooks.filter(book => {
					return (
						book.name.toLowerCase().includes(keyword) ||
						(book.displayName && book.displayName.toLowerCase().includes(keyword))
					);
				});
			}
		},

		onLoad() {
			this.userInfo = uni.getStorageSync('userInfo') || {};
			this.formData.createID = this.userInfo.id || '';
			this.loadBooks();
		},

		onShow() {
			this.loadBooks();
		},

		onPullDownRefresh() {
			this.loadBooks();
		},

		methods: {
			// 加载数据
			loadBooks() {
				familyApi._getAll().then((res) => {
					if (res) {
						this.pedigreeBooks = res;
					}
					uni.stopPullDownRefresh();
				}).catch(err => {
					console.error('加载族谱失败', err);
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					});
					uni.stopPullDownRefresh();
				});
			},

			// 搜索处理 (防抖)
			handleSearch() {
				clearTimeout(this.searchTimer);
				this.searchTimer = setTimeout(() => {
					// 搜索已經通過 computed 即時響應，這裡只是為了防抖
				}, 300);
			},

			// 清空搜索
			clearSearch() {
				this.searchKeyword = '';
			},

			// 打开操作框
			openCreateModal() {
				this.modalMode = 'create';
				this.formData = {
					id: getTime("start"),
					name: '',
					displayName: '',
					generation: '',
					remarks: '',
					createID: this.userInfo.id || '',
					op: 'add'
				};
				this.$refs.modalPopup.open();
			},

			// 打开编辑框
			openEditModal(book) {
				this.modalMode = 'edit';
				this.formData = {
					...book
				};
				this.formData.op = 'upd';
				this.$refs.modalPopup.open();
			},

			// 关闭操作框
			closeModal() {
				this.$refs.modalPopup.close();
				this.formData = {
					id: null,
					name: '',
					displayName: '',
					generation: '',
					remarks: '',
					createID: this.userInfo.id || '',
					op: ''
				};
			},

			// 保存 (創建/更新)
			saveBook() {
				if (!this.formData.name.trim()) {
					uni.showToast({
						title: '请填写譜名',
						icon: 'none'
					});
					return;
				}

				uni.showLoading({
					title: '保存中...'
				});
				familyApi._operationData(this.formData).then((res) => {
					uni.hideLoading();
					console.log(res)
					if (res.status === 200) {
						uni.showToast({
							title: '保存成功',
							icon: 'none'
						});
						this.loadBooks();
						this.closeModal();
					} else {
						uni.showToast({
							title: res.data.msg || '保存失败',
							icon: 'none'
						});
					}
				}).catch(err => {
					uni.hideLoading();
					console.error('保存失败', err);
					uni.showToast({
						title: '保存失败',
						icon: 'none'
					});
				});
			},

			// 确认删除对话框
			confirmDelete(book) {
				this.bookToDelete = book;
				this.$refs.deletePopup.open();
			},

			// 取消刪除
			cancelDelete() {
				this.$refs.deletePopup.close();
				this.bookToDelete = null;
			},

			// 执行刪除
			deleteBook() {
				if (!this.bookToDelete) return;

				uni.showLoading({
					title: '删除中...'
				});
				familyApi._delTree(this.bookToDelete.id).then((res) => {
					uni.hideLoading();
					if (res.status === 200) {
						uni.showToast({
							title: '删除成功',
							icon: 'none'
						});
						this.loadBooks();
						this.cancelDelete();
					} else {
						uni.showToast({
							title: res.data.msg || '删除失败',
							icon: 'none'
						});
					}
				}).catch(err => {
					uni.hideLoading();
					console.error('删除失败', err);
					uni.showToast({
						title: '删除失败',
						icon: 'none'
					});
				});
			},

			// 查看详情
			viewBookDetail(book) {
				uni.navigateTo({
					url: "/pages/family/family-source/family-source?pTree=" + book.id,
				});
			}
		}
	};
</script>

<style lang="scss" scoped>
	.clan-manager {
		background: #e8dccc;
		min-height: 100vh;
		padding: 30rpx 20rpx 40rpx;
		font-family: '华文楷体', 'KaiTi', '宋体', serif;
		display: flex;
		flex-direction: column;
		align-items: center;
		box-sizing: border-box;
	}

	/* 头部区域 */
	.manager-header {
		width: 100%;
		max-width: 1300rpx;
		display: flex;
		justify-content: space-between;
		align-items: flex-end;
		margin-bottom: 10rpx;
		flex-wrap: wrap;
		gap: 20rpx;
		padding: 0 20rpx;
	}

	.clan-plaque {
		text-align: left;
	}

	.plaque-title {
		font-size: 56rpx;
		color: #5d3e28;
		margin: 0;
		letter-spacing: 6rpx;
		font-weight: normal;
		border-bottom: 6rpx solid #b48c5c;
		padding-bottom: 10rpx;
		text-shadow: 2rpx 2rpx 0 #d4b48c;
		display: block;
	}

	.plaque-subtitle {
		font-size: 22rpx;
		color: #7b5f42;
		margin-top: 8rpx;
		font-style: italic;
		display: block;
	}

	/* 头部工具栏 */
	.header-tools {
		display: flex;
		gap: 15rpx;
		align-items: center;
		flex-wrap: wrap;
	}

	.search-box {
		position: relative;
		display: flex;
		align-items: center;
		background: white;
		border-radius: 40rpx;
		box-shadow: 0 4rpx 0 #a77c52, inset 0 1rpx 5rpx #dbbd96;
		border: 2rpx solid #b48b5c;
		overflow: hidden;
		width: 280rpx;
	}

	.search-icon {
		position: absolute;
		left: 15rpx;
		color: #8b6d48;
		font-size: 22rpx;
	}

	.search-box input {
		width: 100%;
		height: 35rpx;
		padding: 12rpx 20rpx 12rpx 45rpx;
		border: none;
		background: transparent;
		font-size: 20rpx;
		font-family: inherit;
		color: #3f2e1b;
	}

	.clear-search {
		position: absolute;
		right: 10rpx;
		background: none;
		border: none;
		font-size: 24rpx;
		color: #9e7c56;
		padding: 0 10rpx;
	}

	.btn-create {
		background: #9e7a56;
		border: none;
		padding: 0rpx 25rpx;
		border-radius: 40rpx;
		font-size: 22rpx;
		font-weight: bold;
		color: #fdf3e0;
		box-shadow: 0 5rpx 0 #5d3f26, inset 0 1rpx 8rpx #f7dbb6;
		border: 2rpx solid #c9a16e;
		font-family: inherit;
		letter-spacing: 2rpx;
	}

	.btn-create:active {
		transform: translateY(2rpx);
		box-shadow: 0 2rpx 0 #5d3f26;
	}

	.clan-shelf {
	    max-width: 1300rpx;
	    width: 100%;
	    height: calc(100vh - 420rpx);
	    background: rgba(190, 160, 120, 0.15);
	    border-radius: 40rpx;
	    // 移除 display: flex 和 flex-wrap 相关样式，因为现在内部 view 负责布局
	}
	
	.clan-shelf-inner {
	    display: flex;
	    flex-wrap: wrap;
	    justify-content: flex-start;
	    gap: 35rpx 30rpx;
	    padding: 25rpx;
	}
	
	.clan-book {
	    width: 200rpx;
	    flex-shrink: 0; // 防止压缩
		margin: 10rpx 15rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		position: relative;
	}

	/* 古籍封面 */
	.classic-cover {
		width: 100%;
		height: 260rpx;
		background: #a77b51;
		background: linear-gradient(145deg, #9c7a58, #7b5937);
		border-radius: 6rpx 18rpx 18rpx 6rpx;
		position: relative;
		box-shadow: -8rpx 0 0 #4d3722, 0 2rpx 8rpx rgba(0, 0, 0, 0.2) inset, 2rpx 0 5rpx rgba(255, 235, 200, 0.2) inset;
		border: 2rpx solid #4f3b27;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		padding: 15rpx;
	}

	/* 书签 */
	.classic-label {
		background: #ecdbba;
		border: 3rpx solid #9e784b;
		border-radius: 30rpx 5rpx 30rpx 5rpx;
		padding: 10rpx 18rpx;
		font-size: 28rpx;
		font-weight: bold;
		color: #352712;
		box-shadow: 4rpx 4rpx 0 #5d401f, inset 0 1rpx 5rpx #fff0d0;
		transform: rotate(-0.5deg);
		text-align: center;
		width: 90%;
		margin-bottom: 15rpx;
		font-family: '华文楷体', 'KaiTi', serif;
		word-break: break-word;
	}

	/* 卷次 */
	.classic-volume {
		background: #8b6943;
		color: #f5e5ce;
		padding: 4rpx 15rpx;
		border-radius: 20rpx;
		font-size: 19rpx;
		box-shadow: inset 0 1rpx 4rpx #f5dbb0, 0 2rpx 0 #4f371d;
		border: 2rpx solid #caa571;
	}

	/* 藏书印 */
	.classic-seal {
		position: absolute;
		bottom: 15rpx;
		right: 15rpx;
		width: 40rpx;
		height: 40rpx;
		border: 2rpx solid #b5472e;
		border-radius: 4rpx 15rpx 4rpx 15rpx;
		opacity: 0.7;
		transform: rotate(-10deg);
	}

	.classic-seal::after {
		content: '宗譜';
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		font-size: 12rpx;
		color: #a03c20;
		font-weight: bold;
	}

	/* 书页厚度 */
	.classic-pages {
		position: absolute;
		right: -17rpx;
		top: 8rpx;
		bottom: 8rpx;
		width: 15rpx;
		height: 76%;
		background: #e5d5b5;
		background: linear-gradient(90deg, #eedbba, #c9aa7a);
		border-radius: 0 5rpx 13rpx 0;
		box-shadow: 0 2rpx 5rpx #2f1f0e;
		z-index: 2;
	}

	/* 操作按钮 */
	.book-actions {
		position: absolute;
		top: 8rpx;
		right: 18rpx;
		display: flex;
		gap: 5rpx;
		opacity: 0;
		transition: opacity 0.2s;
		z-index: 10;
	}

	.classic-cover:hover .book-actions {
		opacity: 1;
	}

	.action-btn {
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		border: none;
		background: rgba(245, 235, 215, 0.9);
		box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
		font-size: 20rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border: 2rpx solid #b48b5c;
	}

	.edit-btn {
		color: #2a5c8a;
	}

	.delete-btn {
		color: #b33c2e;
	}

	/* 譜名 */
	.classic-caption {
		margin-top: 18rpx;
		font-size: 24rpx;
		font-weight: bold;
		color: #382915;
		background: #c9a97a;
		padding: 8rpx 20rpx;
		border-radius: 30rpx;
		box-shadow: 0 4rpx 0 #6d4d2b, inset 0 1rpx 6rpx #fae7c8;
		border: 2rpx solid #b48b57;
		text-align: center;
		width: fit-content;
		max-width: 100%;
	}

	.classic-caption:active {
		background: #dbbc8f;
		box-shadow: 0 2rpx 0 #7d5a33;
		transform: translateY(2rpx);
	}

	/* 空狀態 */
	.clan-empty {
		width: 100%;
		padding: 80rpx 20rpx;
		text-align: center;
		font-size: 32rpx;
		color: #8b704d;
		background: rgba(200, 170, 130, 0.1);
		border-radius: 50rpx;
	}

	/* 底部 */
	.clan-footer {
		margin-top: 40rpx;
		background: #b29063;
		padding: 12rpx 40rpx;
		border-radius: 40rpx;
		color: #fef3e0;
		font-size: 22rpx;
		box-shadow: 0 5rpx 0 #5d4229, inset 0 1rpx 8rpx #ffefcf;
		border: 2rpx solid #ddb585;
	}

	/* ========== 弹出框样式 ========== */
	.modal-content {
		background: #f2e5d2;
		width: 600rpx;
		max-width: 90vw;
		border-radius: 30rpx 30rpx 20rpx 20rpx;
		box-shadow: 0 20rpx 30rpx #1a130b, 0 0 0 2rpx #bb9a70, 0 0 0 5rpx #876e4a;
		overflow: hidden;
	}

	.confirm-modal {
		width: 500rpx;
	}

	.modal-header {
		background: #b59569;
		padding: 18rpx 25rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		border-bottom: 3rpx solid #7b5b39;
	}

	.modal-title {
		color: #2b1d0d;
		font-size: 32rpx;
		font-weight: normal;
	}

	.close-btn {
		font-size: 36rpx;
		color: #4f3621;
		line-height: 1;
		padding: 0 10rpx;
	}

	.modal-body {
		padding: 25rpx;
		max-height: 500rpx;
	}

	.form-group {
		margin-bottom: 20rpx;
	}

	.form-row {
		display: flex;
		gap: 15rpx;
	}

	.form-group.half {
		flex: 1;
	}

	.form-label {
		display: block;
		margin-bottom: 8rpx;
		color: #4f3621;
		font-weight: bold;
		font-size: 20rpx;
	}

	.required {
		color: #b33c2e;
	}
	
	.form-group input {
		width: 80%;
		height: 50rpx;
		padding: 12rpx 15rpx;
		border: 2rpx solid #c9a97a;
		border-radius: 30rpx;
		background: #fff9ed;
		font-size: 20rpx;
		font-family: inherit;
		box-sizing: border-box;
	}

	.form-hint {
		display: block;
		margin-top: 5rpx;
		font-size: 17rpx;
		color: #8b6f4b;
		font-style: italic;
	}

	.modal-footer {
		padding: 20rpx 25rpx;
		display: flex;
		justify-content: flex-end;
		gap: 15rpx;
		border-top: 2rpx solid #d6b48b;
	}

	.btn-cancel {
		padding: 10rpx 25rpx;
		border: none;
		border-radius: 30rpx;
		background: #b7a07e;
		color: #2b1d0e;
		font-size: 20rpx;
		box-shadow: 0 3rpx 0 #6f5539;
		font-family: inherit;
	}

	.btn-cancel:active {
		transform: translateY(2rpx);
		box-shadow: 0 1rpx 0 #6f5539;
	}

	.btn-save {
		padding: 10rpx 35rpx;
		border: none;
		border-radius: 30rpx;
		background: #9e7e5a;
		color: #fcf0dd;
		font-size: 20rpx;
		font-weight: bold;
		box-shadow: 0 3rpx 0 #5a402b;
		font-family: inherit;
	}

	.btn-save:active:not(:disabled) {
		transform: translateY(2rpx);
		box-shadow: 0 1rpx 0 #5a402b;
	}

	.btn-save:disabled {
		opacity: 0.5;
	}

	.btn-delete-confirm {
		padding: 10rpx 35rpx;
		border: none;
		border-radius: 30rpx;
		background: #b3523e;
		color: white;
		font-size: 20rpx;
		font-weight: bold;
		box-shadow: 0 3rpx 0 #712e1f;
		font-family: inherit;
	}

	.btn-delete-confirm:active {
		transform: translateY(2rpx);
		box-shadow: 0 1rpx 0 #712e1f;
	}

	.warning-text {
		color: #b33c2e;
		font-size: 18rpx;
		margin-top: 5rpx;
		display: block;
	}

	.confirm-text {
		display: block;
		margin-bottom: 10rpx;
	}

	/* 响应式 */
	@media (max-width: 800rpx) {
		.manager-header {
			flex-direction: column;
			align-items: stretch;
		}

		.plaque-title {
			font-size: 44rpx;
		}

		.header-tools {
			justify-content: space-between;
		}

		.search-box {
			width: 100%;
			max-width: 300rpx;
		}

		.clan-book {
			width: 170rpx;
		}

		.classic-cover {
			height: 230rpx;
		}

		.classic-label {
			font-size: 24rpx;
			padding: 8rpx 12rpx;
		}
	}

	@media (max-width: 450rpx) {
		.clan-book {
			width: 150rpx;
		}

		.classic-cover {
			height: 200rpx;
		}

		.clan-footer {
			font-size: 18rpx;
			padding: 8rpx 15rpx;
		}

		.form-row {
			flex-direction: column;
			gap: 0;
		}
	}
</style>