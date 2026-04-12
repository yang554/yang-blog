<template>
	<view class="app-update-history">
		<!-- 页面内容 -->
		<view class="page-content">
			<!-- 版本列表 -->
			<view class="version-list">
				<view v-for="(version, index) in versionList" :key="version.id" class="version-item"
					@click="showVersionDetail(version)">
					<!-- 版本内容 -->
					<view class="version-content">
						<view class="version-header">
							<text class="version-name">{{ appName }} v{{ version.versionName }}</text>
							<view class="action-arrow">
								<text class="iconfont icon-arrow-right">›</text>
							</view>
						</view>

						<view class="version-main">
							<text class="update-title">{{ version.updateTitle }}</text>
							<view class="version-bottom">
								<text class="update-time">{{ formatFullTime(version.publishTime) }}</text>
								<text v-if="version.forceUpdate" class="force-update-tag">
									强制更新
								</text>
							</view>
						</view>
					</view>
				</view>

				<!-- 空状态 -->
				<view v-if="versionList.length === 0" class="empty-state">
					<image src="/static/empty.png" mode="aspectFit" class="empty-image"></image>
					<text class="empty-text">暂无更新记录</text>
				</view>
			</view>
		</view>

		<!-- 版本详情弹窗 -->
		<uni-popup ref="detailPopup" type="bottom" background-color="#fff" :mask-click="true">
			<view class="version-detail-popup">
				<!-- 弹窗头部 -->
				<view class="popup-header">
					<text class="popup-title">版本详情</text>
					<view class="popup-close" @click="closeDetailPopup">
						<text class="iconfont icon-close">×</text>
					</view>
				</view>

				<!-- 弹窗内容 -->
				<scroll-view class="popup-content" scroll-y>
					<view v-if="currentVersionDetail" class="detail-content">
						<!-- 版本信息 -->
						<view class="detail-section">
							<view class="section-title">版本信息</view>
							<view class="info-list">
								<view class="info-item">
									<text class="info-label">版本号</text>
									<text class="info-value">{{ appName }}
										v{{ currentVersionDetail.versionName }}</text>
								</view>
								<view class="info-item">
									<text class="info-label">版本代码</text>
									<text class="info-value">{{ currentVersionDetail.versionCode }}</text>
								</view>
								<view class="info-item">
									<text class="info-label">更新时间</text>
									<text
										class="info-value">{{ formatFullTime(currentVersionDetail.publishTime) }}</text>
								</view>
							</view>
						</view>

						<!-- 更新内容 -->
						<view class="detail-section">
							<view class="section-title">更新内容</view>
							<view class="update-content">
								<text class="update-main-title">{{ currentVersionDetail.updateTitle }}</text>
								<view class="update-desc">
									<text class="desc-text">{{ currentVersionDetail.updateContent }}</text>
								</view>
							</view>
						</view>

						<!-- 下载区域 -->
						<view class="detail-section" v-if="currentVersionDetail.downloadUrl">
							<view class="section-title">下载更新</view>
							<view class="download-area">
								<button class="download-btn" @click="downloadUpdate(currentVersionDetail.downloadUrl)">
									<text class="btn-text">下载安装包</text>
								</button>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	import {
		appApi
	} from "@/api/index.js";
	export default {
		name: "version",
		data() {
			return {
				// 应用名称
				appName: "随便",
				// 当前版本
				currentVersion: "1.0.0",
				// 版本列表数据
				versionList: [],
				// 当前查看的版本
				currentVersionDetail: null
			};
		},

		async onLoad() {
			await this.getCurrentVersion();
			this.loadVersionList();
		},

		methods: {
			// 获取当前版本（返回Promise）
			getCurrentVersion() {
				return new Promise((resolve) => {
					// #ifdef APP-PLUS
					plus.runtime.getProperty(plus.runtime.appid, (widgetInfo) => {
						this.currentVersion = widgetInfo.version;
						this.appName = widgetInfo.name;
						resolve(true);
					});
					// #endif

					// #ifndef APP-PLUS
					resolve(true);
					// #endif
				});
			},
			// 加载版本列表
			async loadVersionList() {
				try {
					const params = `versionName=${this.appName}&versionCode=&startTime=&endTime=`
					const response = await appApi._getVersionLis(params);

					if (response.status === 200) {
						// 按版本代码降序排列（最新版本在前）
						this.versionList = (response.data || [])
							.sort((a, b) => b.versionCode - a.versionCode);
					} else {
						uni.showToast({
							title: response.message || '加载失败',
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('加载版本列表失败:', error);
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					});
				}
			},
			// 显示版本详情
			showVersionDetail(version) {
				this.currentVersionDetail = version;
				this.$refs.detailPopup.open();
			},

			// 关闭详情弹窗
			closeDetailPopup() {
				this.$refs.detailPopup.close();
				this.currentVersionDetail = null;
			},

			// 下载更新
			downloadUpdate(downloadUrl) {
				// #ifdef APP-PLUS
				plus.runtime.openURL(downloadUrl);
				// #endif

				// #ifdef H5
				window.open(downloadUrl, '_blank');
				// #endif

				this.closeDetailPopup();
			},

			// 格式化时间（简洁版）
			formatTime(timeString) {
				if (!timeString) return '-';
				try {
					const date = new Date(timeString);
					const now = new Date();
					const diffTime = Math.abs(now - date);
					const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

					if (diffDays === 1) {
						return '今天';
					} else if (diffDays <= 7) {
						return `${diffDays - 1}天前`;
					} else {
						return `${date.getMonth() + 1}-${date.getDate()}`;
					}
				} catch (e) {
					return timeString;
				}
			},

			// 格式化完整时间
			formatFullTime(timeString) {
				if (!timeString) return '-';
				try {
					const date = new Date(timeString);
					return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
				} catch (e) {
					return timeString;
				}
			}
		}
	};
</script>

<style lang="scss" scoped>
	.app-update-history {
		min-height: 100vh;
		background-color: #f5f7fa;
	}

	/* 页面内容 */
	.page-content {
		padding: 30rpx 30rpx 30rpx;
	}

	/* 版本列表 */
	.version-list {
		position: relative;
	}

	.version-item {
		position: relative;
		display: flex;
		margin-bottom: 40rpx;

		/* 版本内容 */
		.version-content {
			flex: 1;
			background: #fff;
			border-radius: 16rpx;
			padding: 24rpx;
			box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);

			.version-header {
				display: flex;
				justify-content: space-between;
				align-items: center;
				margin-bottom: 16rpx;

				.version-name {
					font-size: 28rpx;
					font-weight: 600;
					color: #333;
				}

				.action-arrow {
					.iconfont {
						font-size: 32rpx;
						color: #ccc;
					}
				}
			}

			.version-main {
				.update-title {
					font-size: 28rpx;
					color: #666;
					line-height: 1.5;
					margin-bottom: 12rpx;
					display: block;
				}

				.version-bottom {
					display: flex;
					align-items: center;

					.update-time {
						font-size: 24rpx;
						color: #999;
						margin-right: 16rpx;
					}

					.force-update-tag {
						background: #ff4d4f;
						color: #fff;
						font-size: 20rpx;
						padding: 4rpx 12rpx;
						border-radius: 20rpx;
					}
				}
			}
		}

		/* 点击效果 */
		&:active {
			.version-content {
				background: #f8f9fa;
			}
		}
	}

	/* 空状态 */
	.empty-state {
		text-align: center;
		padding: 100rpx 0;

		.empty-image {
			width: 200rpx;
			height: 200rpx;
			margin-bottom: 30rpx;
			opacity: 0.5;
		}

		.empty-text {
			display: block;
			font-size: 28rpx;
			color: #999;
		}
	}

	/* 版本详情弹窗 */
	.version-detail-popup {
		background: #fff;
		border-radius: 30rpx 30rpx 0 0;
		max-height: 80vh;
		display: flex;
		flex-direction: column;

		.popup-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 30rpx;
			border-bottom: 1rpx solid #f0f0f0;

			.popup-title {
				font-size: 32rpx;
				font-weight: 600;
				color: #333;
			}

			.popup-close {
				width: 60rpx;
				height: 60rpx;
				border-radius: 50%;
				background: #f5f5f5;
				display: flex;
				align-items: center;
				justify-content: center;

				.iconfont {
					font-size: 32rpx;
					color: #666;
				}
			}
		}

		.popup-content {
			flex: 1;
			padding: 0 30rpx;
		}
	}

	/* 详情内容 */
	.detail-content {
		padding: 30rpx 0;

		.detail-section {
			margin-bottom: 40rpx;
			margin-right: 60rpx;

			.section-title {
				font-size: 28rpx;
				font-weight: 600;
				color: #333;
				margin-bottom: 20rpx;
				padding-left: 20rpx;
				border-left: 6rpx solid #667eea;
			}

			.info-list {
				background: #f8f9fa;
				border-radius: 12rpx;
				overflow: hidden;

				.info-item {
					display: flex;
					justify-content: space-between;
					align-items: center;
					padding: 20rpx 24rpx;
					border-bottom: 1rpx solid #e9ecef;

					&:last-child {
						border-bottom: none;
					}

					.info-label {
						font-size: 26rpx;
						color: #666;
					}

					.info-value {
						font-size: 26rpx;
						color: #333;
						font-weight: 500;
					}
				}
			}

			.update-content {
				.update-main-title {
					display: block;
					font-size: 28rpx;
					color: #333;
					font-weight: 500;
					margin-bottom: 16rpx;
				}

				.update-desc {
					background: #f8f9fa;
					border-radius: 12rpx;
					padding: 24rpx;

					.desc-text {
						font-size: 26rpx;
						color: #666;
						line-height: 1.6;
						white-space: pre-line;
					}
				}
			}

			.download-area {
				.download-btn {
					background: linear-gradient(135deg, #667eea, #764ba2);
					color: #fff;
					border: none;
					border-radius: 50rpx;
					padding: 24rpx;
					display: flex;
					flex-direction: column;
					align-items: center;
					justify-content: center;

					.btn-text {
						font-size: 28rpx;
						font-weight: 500;
					}

					&:active {
						opacity: 0.9;
					}
				}
			}
		}
	}

	/* 响应式调整 */
	@media (max-width: 750px) {
		.page-content {
			padding-left: 20rpx;
			padding-right: 20rpx;
		}

		.app-info-card,
		.version-content {
			padding: 20rpx;
		}
	}
</style>