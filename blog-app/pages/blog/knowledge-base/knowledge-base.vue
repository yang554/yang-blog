<template>
	<view class="knowledge-base-view">
		<!-- 顶部搜索和操作栏 -->
		<view class="header-container">
			<view class="search-bar">
				<input class="search-input" placeholder="搜索知识标题" v-model="searchKeyword" @confirm="handleSearch" />
				<view class="search-actions">
					<button class="search-btn" @click="handleSearch">
						<text class="iconfont icon-sousuo"></text>
					</button>
				</view>
			</view>

			<!-- 标签筛选 -->
			<scroll-view class="tag-filter" scroll-x="true" v-if="activeTags.length > 0">
				<view class="tag-filter-container">
					<text class="filter-label">当前标签:</text>
					<view v-for="(tag,index) in activeTags" :key="index" class="active-tag"
						@click="removeTagFilter(tag)">
						#{{ tag.name }}
						<text class="iconfont icon-close"></text>
					</view>
					<text class="clear-all" @click="clearAllTags">清空所有</text>
				</view>
			</scroll-view>
		</view>

		<!-- 主内容区：双栏布局 -->
		<view class="main-container">
			<!-- 左侧：分类封面区 -->
			<view class="category-sidebar-container">
				<view class="sidebar-header">
					<text class="sidebar-title">知识分类</text>
					<text class="sidebar-count">{{ categories.length }}个分类</text>
				</view>
				<scroll-view class="category-scroll" scroll-y="true" :style="categoryScrollHeight">
					<view class="category-list">
						<view v-for="category in categories" :key="category.tid" class="category-card"
							@click="selectCategory(category)" :class="{ active: activeCategoryId === category.tid }">
							<!-- <view class="category-cover">
								<image :src="getCategoryCover(category.cover)" mode="aspectFill" class="cover-image" />
								<view class="cover-overlay">
									<text class="category-icon" :class="category.icon || 'iconfont icon-folder'"></text>
								</view>
							</view> -->
							<view class="category-info">
								<text class="category-name">{{ category.name }}</text>
								<text class="knowledge-count">{{ category.count || 0 }}条知识</text>
								<view class="category-tags">
									<text v-for="tag in (category.topTags || [])" :key="tag" class="mini-tag">
										#{{ tag }}
									</text>
								</view>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>

			<!-- 右侧：知识列表区 -->
			<view class="knowledge-main-container">
				<!-- 当前分类/标签标题 -->
				<view class="section-header">
					<text class="section-title">
						{{ getSectionTitle() }}
					</text>
					<view class="section-actions">
						<view class="view-options">
							<text class="view-option" :class="{ active: viewMode === 'list' }"
								@click="viewMode = 'list'">
								<text class="iconfont icon-list"></text>
							</text>
							<text class="view-option" :class="{ active: viewMode === 'card' }"
								@click="viewMode = 'card'">
								<text class="iconfont icon-grid"></text>
							</text>
						</view>
						<text class="total-count">{{ pagination.totalCount }}条记录</text>
					</view>
				</view>

				<scroll-view class="knowledge-scroll" scroll-y="true" :style="knowledgeScrollHeight"
					@scrolltolower="loadMore" :scroll-with-animation="true">
					<!-- 知识列表 -->
					<view class="knowledge-list" :class="viewMode">
						<view v-for="(item, index) in pagination.currentTableData" :key="item.bid"
							class="knowledge-item" :class="viewMode">
							<!-- 卡片视图 -->
							<view v-if="viewMode === 'card'" class="card-view">
								<view class="card-header">
									<image v-if="item.cover" :src="getFullImageUrl(item.cover)" class="knowledge-cover"
										mode="aspectFill" />
									<view class="card-title-container">
										<text class="knowledge-title">{{ item.title }}</text>
										<view class="knowledge-meta">
											<text class="meta-item">
												<text class="iconfont icon-time"></text>
												{{ item.createtime | formatTime }}
											</text>
											<text class="meta-item">
												<text class="iconfont icon-view"></text>
												{{ item.browsecount || 0 }}
											</text>
										</view>
									</view>
								</view>
								<view class="card-content">
									<text class="knowledge-excerpt">{{ item.description || '暂无描述' }}</text>
								</view>
								<view class="card-footer">
									<view class="knowledge-tags">
										<text v-for="(tag,index) in (item.blogTags || [])" :key="'tag-' + index"
											class="knowledge-tag" @click.stop="addTagFilter(tag)">
											#{{ tag.name }}
										</text>
									</view>
									<view class="card-actions">
										<text class="action-btn" @click.stop="previewKnowledge(item)">
											<text class="iconfont icon-chakan"></text>
										</text>
										<text class="action-btn" @click.stop="editKnowledge(item)">
											<text class="iconfont icon-bianji"></text>
										</text>
										<text class="action-btn" @click.stop="delKnowledge(item)">
											<text class="iconfont icon-shanchu"></text>
										</text>
									</view>
								</view>
							</view>

							<!-- 列表视图 -->
							<view v-else class="list-view">
								<view class="list-main">
									<text class="knowledge-title">{{ item.title }}</text>
									<text class="knowledge-excerpt">{{ item.description || '暂无描述' }}</text>
									<view class="knowledge-tags">
										<text v-for="(tag,index) in (item.blogTags || [])" :key="'tag-' + index"
											class="knowledge-tag" @click.stop="addTagFilter(tag)">
											#{{ tag.name }}
										</text>
									</view>
								</view>
								<view class="list-side">
									<view class="knowledge-meta">
										<text class="meta-item">
											<text class="iconfont icon-time"></text>
											{{ item.createtime | formatTime }}
										</text>
									</view>
									<view class="list-actions">
										<text class="action-btn" @click.stop="previewKnowledge(item)">
											<text class="iconfont icon-chakan"></text>
										</text>
										<text class="action-btn" @click.stop="editKnowledge(item)">
											<text class="iconfont icon-bianji"></text>
										</text>
										<text class="action-btn" @click.stop="delKnowledge(item)">
											<text class="iconfont icon-shanchu"></text>
										</text>
									</view>
								</view>
							</view>
						</view>
					</view>

					<!-- 空状态 -->
					<view v-if="pagination.currentTableData.length === 0 && !pagination.loading" class="empty-state">
						<text class="iconfont icon-empty"></text>
						<text class="empty-text">暂无知识内容</text>
						<button class="empty-btn" @click="goToAddKnowledge">开始记录</button>
					</view>

					<!-- 加载更多 -->
					<view v-if="pagination.loading" class="loading-more">
						<text class="loading-text">加载中...</text>
					</view>
					<view v-if="pagination.hasMore === false && pagination.currentTableData.length > 0" class="no-more">
						<text class="no-more-text">没有更多了</text>
					</view>
				</scroll-view>
			</view>
		</view>

		<!-- 快速操作浮窗 -->
		<view class="quick-actions" v-if="showQuickActions">
			<button class="quick-action-btn" @click="quickAddNote">
				<text class="action-label">快速笔记</text>
			</button>
			<button class="quick-action-btn" @click="quickAddLink">
				<text class="action-label">收藏链接</text>
			</button>

		</view>
		<button class="floating-action-btn" @click="toggleQuickActions">
			<text class="iconfont icon-bianji"></text>
		</button>

		<!-- 知识预览弹窗 -->
		<uni-popup ref="previewPopup" type="center" background-color="#fff">
			<view class="knowledge-preview">
				<view class="preview-header">
					<text class="preview-title">{{ previewData.title }}</text>
					<text class="close-btn" @click="closePreview">×</text>
				</view>
				<scroll-view class="preview-content" scroll-y="true">
					<view class="preview-meta">
						<text class="meta-item">
							<text class="iconfont icon-category"></text>
							{{ previewData.tName || '未分类' }}
						</text>
						<text class="meta-item">
							<text class="iconfont icon-time"></text>
							{{ previewData.createtime | formatTime }}
						</text>
						<text class="meta-item">
							<text class="iconfont icon-view"></text>
							{{ previewData.browsecount || 0 }}次浏览
						</text>
					</view>
					<view class="preview-tags" v-if="previewData.tags && previewData.tags.length > 0">
						<text v-for="tag in previewData.tags" :key="tag.id" class="preview-tag">
							#{{ tag.name }}
						</text>
					</view>
					<view class="preview-body">
						<uParse :content="markdownToHtmlFun(previewData.content)" @preview="preview"
							@navigate="navigate"></uParse>
					</view>
				</scroll-view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	import MarkdownIt from 'markdown-it'
	import uParse from '@/components/u-parse/u-parse.vue'
	import {
		blogApi,
		typeApi
	} from "@/api/index.js";
	import {
		parseDate
	} from '@/common/formatDate.js';
	import {
		IMG_URL
	} from '@/api/config.js';

	export default {
		name: "KnowledgeBase",
		components: {
			uParse
		},
		data() {
			return {
				searchKeyword: "",
				activeTags: [], // 当前选中的标签筛选
				activeCategoryId: null, // 当前选中的分类ID
				viewMode: "card", // list 或 card
				showQuickActions: false,
				screenHeight: 0,

				// 分类数据
				categories: [],

				// 分页数据
				pagination: {
					listData: [],
					currentTableData: [],
					currentPage: 1,
					pageSize: 12,
					totalPage: 1,
					totalCount: 0,
					loading: false,
					hasMore: true
				},

				// 预览数据
				previewData: {
					title: "",
					content: "",
					tName: "",
					tags: [],
					createtime: "",
					browsecount: 0
				},
				userInfo: uni.getStorageSync('userInfo')
			};
		},

		filters: {
			formatTime(value) {
				return parseDate(value);
			}
		},

		computed: {
			categoryScrollHeight() {
				return `height: ${this.screenHeight - 150}px;`;
			},
			knowledgeScrollHeight() {
				return `height: ${this.screenHeight - 150}px;`;
			}
		},

		onLoad() {
			this.getScreenHeight();
			this.loadCategories();
			this.loadKnowledgeList();
		},
		onShow(){
			this.refreshData();
		},
		onPullDownRefresh() {
			this.refreshData();
		},

		onReachBottom() {
			this.loadMore();
		},

		methods: {
			// 获取屏幕高度
			getScreenHeight() {
				const systemInfo = uni.getSystemInfoSync();
				this.screenHeight = systemInfo.windowHeight;
			},

			// 加载分类数据
			async loadCategories() {
				try {
					const res = await typeApi._getTypeByUserId(this.userInfo.id);
					if (res.status === 200) {
						// 处理分类数据，确保每个分类都有必要的字段
						this.categories = res.data.map(category => ({
							tid: category.id,
							name: category.name || '未命名分类',
							cover: category.cover || '',
							count: category.count || 0,
							topTags: (category.tags || []).slice(0, 3)
						}));
					}
				} catch (error) {
					console.error("加载分类失败:", error);
				}
			},

			// 构建查询参数
			buildQueryParams() {
				let baseParams =
					`/blog/getBlogByParams?uID=${this.userInfo.id}`;

				// 添加分类筛选
				if (this.activeCategoryId) {
					baseParams += `&type=${this.activeCategoryId}`;
				}

				// 添加标签筛选
				if (this.activeTags.length > 0) {
					const tagNames = this.activeTags.map(tag => tag.id).join(',');
					baseParams += `&tags=${tagNames}`;
				}

				// 添加搜索关键词
				if (this.searchKeyword) {
					baseParams += `&title=${encodeURIComponent(this.searchKeyword)}`;
				}

				// 默认只显示已发布、公开、未删除的知识
				baseParams += `&ispublic=1&issue=1&isdel=0`;

				return baseParams;
			},

			// 加载知识列表
			async loadKnowledgeList(isLoadMore = false) {
				if (this.pagination.loading) return;

				this.pagination.loading = true;

				if (!isLoadMore) {
					this.pagination.currentPage = 1;
					this.pagination.hasMore = true;
				}

				try {
					const params = this.buildQueryParams();
					const res = await blogApi.getBlogParams(params);

					if (res.status === 200) {
						const data = res.data || [];

						if (isLoadMore) {
							// 加载更多
							this.pagination.listData = [...this.pagination.listData, ...data];
						} else {
							// 刷新或初次加载
							this.pagination.listData = data;
						}

						this.pagination.totalCount = this.pagination.listData.length;
						this.pagination.totalPage = Math.ceil(this.pagination.totalCount / this.pagination.pageSize);

						// 检查是否还有更多数据
						this.pagination.hasMore = data.length >= this.pagination.pageSize;

						this.updateCurrentTableData();
					} else {
						uni.showToast({
							title: res.msg || '加载失败',
							icon: 'none'
						});
					}
				} catch (error) {
					console.error("加载知识列表失败:", error);
					uni.showToast({
						title: '网络错误',
						icon: 'none'
					});
				} finally {
					this.pagination.loading = false;
					uni.stopPullDownRefresh();
				}
			},

			// 加载更多
			loadMore() {
				if (this.pagination.hasMore && !this.pagination.loading) {
					this.pagination.currentPage++;
					this.loadKnowledgeList(true);
				}
			},

			// 更新当前页数据
			updateCurrentTableData() {
				const start = (this.pagination.currentPage - 1) * this.pagination.pageSize;
				const end = start + this.pagination.pageSize;
				this.pagination.currentTableData = this.pagination.listData.slice(start, end);
			},

			// 选择分类
			selectCategory(category) {
				this.activeCategoryId = category.tid;
				this.pagination.currentPage = 1;
				this.loadKnowledgeList();
			},

			// 添加标签筛选
			addTagFilter(tag) {
				if (!this.activeTags.find(t => t.id === tag.id)) {
					this.activeTags.push(tag);
					this.pagination.currentPage = 1;
					this.loadKnowledgeList();
				}
			},

			// 移除标签筛选
			removeTagFilter(tag) {
				this.activeTags = this.activeTags.filter(t => t.id !== tag.id);
				this.pagination.currentPage = 1;
				this.loadKnowledgeList();
			},

			// 清空所有标签
			clearAllTags() {
				this.activeTags = [];
				this.pagination.currentPage = 1;
				this.loadKnowledgeList();
			},

			// 搜索处理
			handleSearch() {
				this.pagination.currentPage = 1;
				this.loadKnowledgeList();
			},

			// 获取分区标题
			getSectionTitle() {
				if (this.activeCategoryId) {
					const category = this.categories.find(c => c.tid === this.activeCategoryId);
					return category ? category.name : "全部知识";
				}
				if (this.activeTags.length > 0) {
					return `标签: ${this.activeTags.map(t => t.name).join(", ")}`;
				}
				return this.searchKeyword ? `搜索: ${this.searchKeyword}` : "全部知识";
			},

			// 页面跳转
			goToAddKnowledge() {
				uni.navigateTo({
					url: "/pages/home/editblog/editblog"
				});
			},

			// 预览知识
			previewKnowledge(item) {
				// 处理标签数据
				const tags = item.blogTags ?
					(typeof item.blogTags === 'string' ? item.blogTags.split(',') : item.blogTags) : [];

				this.previewData = {
					bid: item.bid,
					title: item.title || '无标题',
					content: item.content || '暂无内容',
					tName: item.tName || '未分类',
					tags: tags,
					createtime: item.createtime,
					browsecount: item.browsecount || 0
				};
				this.$refs.previewPopup.open();

				// 更新阅读计数
				this.updateViewCount(item.bid);
			},

			// 更新阅读计数
			async updateViewCount(bid) {
				try {
					// await blogApi._addBlogBrowseCount(bid);
				} catch (error) {
					console.error('更新阅读计数失败:', error);
				}
			},

			// 编辑知识
			editKnowledge(item) {
				uni.navigateTo({
					url: `/pages/blog/write/write?bid=${item.bid}`
				});
			},

			//逻辑删除知识
			delKnowledge(item) {
				uni.showModal({
					title: '确认删除',
					content: '确定要删除该数据吗？',
					success: ({
						confirm
					}) => {
						if (confirm) {
							blogApi.deleteBlogH(item.bid, "1").then(res => {
								console.log(res.status)
								if (res.status === 200) {
									this.refreshData();
								}


							})
						}
					}
				})
			},

			// 快速操作
			toggleQuickActions() {
				this.showQuickActions = !this.showQuickActions;
			},

			quickAddNote() {
				uni.navigateTo({
					url: "/pages/blog/write/write"
				});
			},

			quickAddLink() {
				uni.navigateTo({
					url: "/pages/knowledge/save-link"
				});
			},

			// 关闭预览
			closePreview() {
				this.$refs.previewPopup.close();
			},

			// 刷新数据
			refreshData() {
				this.loadCategories();
				this.loadKnowledgeList();
			},

			// 图片URL处理
			getFullImageUrl(path) {
				if (!path) return "/static/default-knowledge.png";
				if (path.startsWith("http")) return path;
				return IMG_URL + (path.startsWith("/") ? path : "/" + path);
			},

			getCategoryCover(cover) {
				return cover ? this.getFullImageUrl(cover) : "/static/default-category.png";
			},
			//将 markdown 转换为 html 
			markdownToHtmlFun(context) {
				const md = new MarkdownIt()
				const markdownText = context
				const html = md.render(markdownText)
				return html;
			},
			preview(src, e) {
				// do something
			},
			navigate(href, e) {
				// do something
			}
		}
	};
</script>

<style lang="scss" scoped>
	.knowledge-base-view {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background: #f5f7fa;

		.header-container {
			flex-shrink: 0;
			padding: 20rpx 30rpx;
			background: #ffffff;
			box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);

			.search-bar {
				display: flex;
				align-items: center;
				gap: 20rpx;
				margin-bottom: 20rpx;

				.search-input {
					flex: 1;
					height: 80rpx;
					padding: 0 30rpx;
					background: #f8f9fa;
					border-radius: 40rpx;
					font-size: 28rpx;
					border: 1rpx solid #e4e6eb;
					transition: all 0.3s;

					&:focus {
						border-color: #409eff;
						background: #fff;
						box-shadow: 0 0 0 2rpx rgba(64, 158, 255, 0.1);
					}
				}

				.search-actions {
					display: flex;
					gap: 15rpx;

					.search-btn,
					.add-btn {
						height: 80rpx;
						border-radius: 40rpx;
						display: flex;
						align-items: center;
						justify-content: center;
						font-size: 28rpx;
						border: none;
						transition: all 0.3s;
					}

					.search-btn {
						width: 80rpx;
						background: #f0f2f5;
						color: #606266;

						&:active {
							background: #e4e6eb;
						}
					}

					.add-btn {
						padding: 0 30rpx;
						background: linear-gradient(135deg, #409eff, #66b1ff);
						color: white;
						font-weight: 500;

						&:active {
							opacity: 0.9;
							transform: translateY(1rpx);
						}
					}
				}
			}

			.tag-filter {
				margin-top: 10rpx;

				.tag-filter-container {
					display: flex;
					align-items: center;
					flex-wrap: nowrap;
					padding: 15rpx 0;
					gap: 20rpx;

					.filter-label {
						font-size: 26rpx;
						color: #606266;
						white-space: nowrap;
						font-weight: 500;
					}

					.active-tag {
						display: flex;
						align-items: center;
						padding: 8rpx 20rpx;
						background: linear-gradient(135deg, #f093fb, #f5576c);
						border-radius: 20rpx;
						font-size: 24rpx;
						color: white;
						white-space: nowrap;
						box-shadow: 0 4rpx 12rpx rgba(245, 87, 108, 0.3);

						.icon-close {
							margin-left: 10rpx;
							font-size: 20rpx;
							opacity: 0.8;
						}

						&:active {
							opacity: 0.9;
						}
					}

					.clear-all {
						font-size: 26rpx;
						color: #409eff;
						white-space: nowrap;
						font-weight: 500;

						&:active {
							opacity: 0.8;
						}
					}
				}
			}
		}

		.main-container {
			flex: 1;
			display: flex;
			overflow: hidden;

			.category-sidebar-container {
				width: 280rpx;
				background: #ffffff;
				border-right: 1rpx solid #e4e6eb;
				display: flex;
				flex-direction: column;

				.sidebar-header {
					flex-shrink: 0;
					padding: 30rpx 20rpx;
					border-bottom: 1rpx solid #e4e6eb;

					.sidebar-title {
						font-size: 32rpx;
						font-weight: bold;
						color: #303133;
						display: block;
						margin-bottom: 10rpx;
					}

					.sidebar-count {
						font-size: 24rpx;
						color: #909399;
					}
				}

				.category-scroll {
					flex: 1;

					.category-list {
						padding: 20rpx;

						.category-card {
							margin-bottom: 20rpx;
							border-radius: 16rpx;
							overflow: hidden;
							background: #f8f9fa;
							transition: all 0.3s;
							border: 1rpx solid #e4e6eb;

							&.active {
								border-color: #409eff;
								box-shadow: 0 4rpx 20rpx rgba(64, 158, 255, 0.15);
								transform: translateY(-2rpx);
							}

							&:active:not(.active) {
								background: #f0f2f5;
							}

							.category-cover {
								width: 100%;
								height: 160rpx;
								position: relative;

								.cover-image {
									width: 100%;
									height: 100%;
								}

								.cover-overlay {
									position: absolute;
									top: 0;
									left: 0;
									right: 0;
									bottom: 0;
									background: linear-gradient(to bottom, rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0.4));
									display: flex;
									align-items: center;
									justify-content: center;

									.category-icon {
										color: white;
										font-size: 48rpx;
										text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.3);
									}
								}
							}

							.category-info {
								padding: 20rpx;

								.category-name {
									display: block;
									font-size: 28rpx;
									font-weight: 600;
									color: #303133;
									margin-bottom: 8rpx;
									white-space: nowrap;
									overflow: hidden;
									text-overflow: ellipsis;
								}

								.knowledge-count {
									font-size: 22rpx;
									color: #909399;
									display: block;
									margin-bottom: 12rpx;
								}

								.category-tags {
									display: flex;
									flex-wrap: wrap;
									gap: 8rpx;

									.mini-tag {
										padding: 4rpx 10rpx;
										background: rgba(64, 158, 255, 0.1);
										border: 1rpx solid rgba(64, 158, 255, 0.2);
										border-radius: 12rpx;
										font-size: 20rpx;
										color: #409eff;
										white-space: nowrap;
									}
								}
							}
						}
					}
				}
			}

			.knowledge-main-container {
				flex: 1;
				display: flex;
				flex-direction: column;
				background: #f5f7fa;

				.section-header {
					flex-shrink: 0;
					padding: 30rpx;
					background: #ffffff;
					border-bottom: 1rpx solid #e4e6eb;
					display: flex;
					justify-content: space-between;
					align-items: center;

					.section-title {
						font-size: 36rpx;
						font-weight: bold;
						color: #303133;
					}

					.section-actions {
						display: flex;
						align-items: center;
						gap: 30rpx;

						.view-options {
							display: flex;
							gap: 15rpx;
							background: #f8f9fa;
							border-radius: 12rpx;
							padding: 4rpx;

							.view-option {
								width: 60rpx;
								height: 60rpx;
								border-radius: 8rpx;
								display: flex;
								align-items: center;
								justify-content: center;
								background: transparent;
								color: #909399;

								&.active {
									background: #ffffff;
									color: #409eff;
									box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
								}
							}
						}

						.total-count {
							font-size: 26rpx;
							color: #909399;
							font-weight: 500;
						}
					}
				}

				.knowledge-scroll {
					flex: 1;

					.knowledge-list {
						padding: 30rpx;

						&.card {
							display: grid;
							grid-template-columns: repeat(auto-fill, minmax(340rpx, 1fr));
							gap: 30rpx;

							.knowledge-item {
								background: white;
								border-radius: 20rpx;
								overflow: hidden;
								box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
								transition: all 0.3s;

								&:active {
									transform: translateY(-2rpx);
									box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.1);
								}

								.card-header {
									position: relative;

									.knowledge-cover {
										width: 100%;
										height: 200rpx;
									}

									.card-title-container {
										padding: 25rpx 25rpx 15rpx;

										.knowledge-title {
											font-size: 30rpx;
											font-weight: 600;
											color: #303133;
											display: block;
											margin-bottom: 15rpx;
											line-height: 1.4;
											display: -webkit-box;
											-webkit-line-clamp: 2;
											-webkit-box-orient: vertical;
											overflow: hidden;
										}

										.knowledge-meta {
											display: flex;
											gap: 20rpx;
											font-size: 22rpx;
											color: #909399;

											.meta-item {
												display: flex;
												align-items: center;
												gap: 6rpx;
											}
										}
									}
								}

								.card-content {
									padding: 0 25rpx 20rpx;

									.knowledge-excerpt {
										font-size: 26rpx;
										color: #606266;
										line-height: 1.5;
										display: -webkit-box;
										-webkit-line-clamp: 2;
										-webkit-box-orient: vertical;
										overflow: hidden;
									}
								}

								.card-footer {
									padding: 15rpx 25rpx 25rpx;
									border-top: 1rpx solid #f0f2f5;
									display: flex;
									justify-content: space-between;
									align-items: flex-start;

									.knowledge-tags {
										display: flex;
										flex-wrap: wrap;
										gap: 10rpx;
										flex: 1;
										margin-right: 20rpx;

										.knowledge-tag {
											padding: 6rpx 14rpx;
											background: linear-gradient(135deg, #36d1dc, #5b86e5);
											border-radius: 15rpx;
											font-size: 22rpx;
											color: white;
											white-space: nowrap;

											&:active {
												opacity: 0.9;
											}
										}
									}

									.card-actions {
										display: flex;
										gap: 15rpx;

										.action-btn {
											width: 60rpx;
											height: 60rpx;
											border-radius: 50%;
											background: #f8f9fa;
											display: flex;
											align-items: center;
											justify-content: center;
											color: #606266;

											&:active {
												background: #e4e6eb;
											}
										}
									}
								}
							}
						}

						&.list {
							.knowledge-item {
								background: white;
								border-radius: 16rpx;
								padding: 30rpx;
								margin-bottom: 20rpx;
								display: flex;
								justify-content: space-between;
								box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
								transition: all 0.3s;

								&:active {
									background: #fafafa;
								}

								.list-main {
									flex: 1;

									.knowledge-title {
										font-size: 32rpx;
										font-weight: 600;
										color: #303133;
										display: block;
										margin-bottom: 15rpx;
									}

									.knowledge-excerpt {
										font-size: 26rpx;
										color: #606266;
										line-height: 1.5;
										display: -webkit-box;
										-webkit-line-clamp: 1;
										-webkit-box-orient: vertical;
										overflow: hidden;
										margin-bottom: 20rpx;
									}

									.knowledge-tags {
										display: flex;
										flex-wrap: wrap;
										gap: 10rpx;

										.knowledge-tag {
											padding: 6rpx 14rpx;
											background: linear-gradient(135deg, #ff9a9e, #fad0c4);
											border-radius: 15rpx;
											font-size: 22rpx;
											color: white;
											white-space: nowrap;

											&:active {
												opacity: 0.9;
											}
										}
									}
								}

								.list-side {
									display: flex;
									flex-direction: column;
									justify-content: space-between;
									align-items: flex-end;
									margin-left: 30rpx;

									.knowledge-meta {
										.meta-item {
											font-size: 24rpx;
											color: #909399;
											display: flex;
											align-items: center;
											gap: 6rpx;
										}
									}

									.list-actions {
										display: flex;
										gap: 15rpx;

										.action-btn {
											width: 60rpx;
											height: 60rpx;
											border-radius: 50%;
											background: #f8f9fa;
											display: flex;
											align-items: center;
											justify-content: center;
											color: #606266;

											&:active {
												background: #e4e6eb;
											}
										}
									}
								}
							}
						}
					}

					.empty-state {
						text-align: center;
						padding: 100rpx 30rpx;

						.icon-empty {
							font-size: 120rpx;
							color: #dcdfe6;
							display: block;
							margin-bottom: 30rpx;
						}

						.empty-text {
							font-size: 32rpx;
							color: #909399;
							display: block;
							margin-bottom: 40rpx;
						}

						.empty-btn {
							width: 300rpx;
							height: 80rpx;
							background: linear-gradient(135deg, #409eff, #66b1ff);
							color: white;
							border-radius: 40rpx;
							font-size: 28rpx;
							border: none;
							font-weight: 500;

							&:active {
								opacity: 0.9;
							}
						}
					}

					.loading-more {
						text-align: center;
						padding: 40rpx 0;
						color: #909399;
						font-size: 26rpx;
					}

					.no-more {
						text-align: center;
						padding: 40rpx 0;
						color: #c0c4cc;
						font-size: 26rpx;
						border-top: 1rpx solid #e4e6eb;
						margin-top: 20rpx;
					}
				}
			}
		}

		.quick-actions {
			position: fixed;
			right: 40rpx;
			bottom: 160rpx;
			display: flex;
			flex-direction: column;
			gap: 20rpx;
			animation: slideUp 0.3s ease;

			.quick-action-btn {
				width: 220rpx;
				height: 80rpx;
				background: white;
				border-radius: 40rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				gap: 15rpx;
				box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.12);
				border: none;
				font-weight: 500;

				.action-label {
					font-size: 26rpx;
					color: #303133;
				}

				&:active {
					opacity: 0.9;
					transform: translateY(1rpx);
				}
			}

			@keyframes slideUp {
				from {
					transform: translateY(20rpx);
					opacity: 0;
				}

				to {
					transform: translateY(0);
					opacity: 1;
				}
			}
		}

		.floating-action-btn {
			position: fixed;
			right: 40rpx;
			bottom: 60rpx;
			width: 100rpx;
			height: 100rpx;
			border-radius: 50%;
			background: linear-gradient(135deg, #409eff, #66b1ff);
			color: white;
			display: flex;
			align-items: center;
			justify-content: center;
			box-shadow: 0 8rpx 25rpx rgba(64, 158, 255, 0.3);
			border: none;
			z-index: 1000;
			font-size: 36rpx;

			&:active {
				opacity: 0.9;
				transform: translateY(1rpx);
			}
		}

		.knowledge-preview {
			width: 90vw;
			max-height: 80vh;
			background: white;
			border-radius: 20rpx;
			overflow: hidden;
			box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.2);

			.preview-header {
				padding: 40rpx 30rpx;
				background: linear-gradient(135deg, #409eff, #66b1ff);
				display: flex;
				justify-content: space-between;
				align-items: center;

				.preview-title {
					font-size: 36rpx;
					font-weight: bold;
					color: white;
					flex: 1;
					margin-right: 30rpx;
					display: -webkit-box;
					-webkit-line-clamp: 2;
					-webkit-box-orient: vertical;
					overflow: hidden;
				}

				.close-btn {
					font-size: 50rpx;
					color: white;
					line-height: 1;
					padding: 10rpx;
					opacity: 0.8;

					&:active {
						opacity: 0.6;
					}
				}
			}

			.preview-content {
				max-height: 60vh;
				padding: 30rpx;

				.preview-meta {
					display: flex;
					gap: 30rpx;
					margin-bottom: 25rpx;
					flex-wrap: wrap;

					.meta-item {
						font-size: 26rpx;
						color: #606266;
						display: flex;
						align-items: center;
						gap: 10rpx;
						background: #f8f9fa;
						padding: 8rpx 16rpx;
						border-radius: 12rpx;
					}
				}

				.preview-tags {
					display: flex;
					flex-wrap: wrap;
					gap: 15rpx;
					margin-bottom: 30rpx;

					.preview-tag {
						padding: 8rpx 20rpx;
						background: linear-gradient(135deg, #a8edea, #fed6e3);
						border-radius: 20rpx;
						font-size: 24rpx;
						color: #333;
					}
				}

				.preview-body {
					margin-right: 35rpx;
					font-size: 30rpx;
					line-height: 1.6;
					color: #303133;
					word-wrap: break-word;
					overflow-wrap: break-word;

					>* {
						max-width: 100%;
						box-sizing: border-box;
					}

					pre,
					code {
						white-space: pre-wrap;
						word-break: break-all;
					}

					::v-deep img {
						max-width: 100%;
						height: auto;
						border-radius: 8rpx;
					}
				}
			}

			.preview-footer {
				padding: 25rpx 30rpx;
				border-top: 1rpx solid #e4e6eb;
				display: flex;
				justify-content: flex-end;
				gap: 20rpx;

				.footer-btn {
					padding: 15rpx 40rpx;
					border-radius: 25rpx;
					font-size: 28rpx;
					border: none;
					font-weight: 500;
					transition: all 0.3s;

					&:first-child {
						background: #409eff;
						color: white;

						&:active {
							background: #66b1ff;
						}
					}

					&:last-child {
						background: #f8f9fa;
						color: #606266;

						&:active {
							background: #e4e6eb;
						}
					}
				}
			}
		}
	}
</style>