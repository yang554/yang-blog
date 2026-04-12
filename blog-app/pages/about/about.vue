<template>
	<view class="about-container">
		<!-- 背景渐变 -->
		<view class="background-gradient"></view>

		<!-- 页面内容 -->
		<view class="content-wrapper">
			<scroll-view class="main-content" scroll-y :show-scrollbar="false">
				<!-- App信息卡片 -->
				<view class="app-info-card">
					<view class="logo-wrapper">
						<image class="app-logo" src="/static/logo.png" mode="aspectFit"></image>
					</view>
					<text class="app-name">{{appVersion.name}}</text>
					<text class="app-version">Version {{ appVersion.version || '1.0.0' }}</text>
				</view>

				<!-- 功能菜单 -->
				<view class="menu-section">
					<view class="menu-list">
						<!-- 功能介绍 -->
						<view class="menu-item" @click="showFeatureIntro">
							<view class="menu-item-left">
								<text class="menu-text">功能介绍</text>
							</view>
							<text class="menu-arrow">›</text>
						</view>

						<!-- 检查更新 -->
						<view class="menu-item" @click="checkUpdate">
							<view class="menu-item-left">
								<text class="menu-text">检查新版本</text>
							</view>
							<view class="menu-right">
								<text class="update-status" v-if="checking">检查中...</text>
								<text class="update-status latest" v-else-if="isLatest">已是最新</text>
								<text class="update-status new-version" v-else-if="hasNewVersion">有新版本</text>
								<text class="menu-arrow">›</text>
							</view>
						</view>

						<!-- 用户投诉 -->
						<view class="menu-item" @click="handleComplaint">
							<view class="menu-item-left">
								<text class="menu-text">投诉与反馈</text>
							</view>
							<text class="menu-arrow">›</text>
						</view>
					</view>
				</view>

				<!-- 其他信息 -->
				<view class="info-section">
					<view class="info-item">
						<text class="info-label">官方网站</text>
						<text class="info-value" @click="visitWebsite">https://www.yanghj.com.cn</text>
					</view>
					<view class="info-item">
						<text class="info-label">客服电话</text>
						<text class="info-value" @click="makeCall">400-123-4567</text>
					</view>
					<view class="info-item">
						<text class="info-label">版权信息</text>
						<text class="info-value">© 2024 枫萱悦宁 版权所有</text>
					</view>
				</view>

				<!-- 底部版权 -->
				<view class="copyright">
					<text class="copyright-text"></text>
				</view>
			</scroll-view>
		</view>

		<!-- 更新提示弹窗 -->
		<uni-popup ref="updatePopup" type="center" background-color="#fff" :mask-click="!updateInfo.forceUpdate">
			<view class="update-popup">
				<view class="popup-header">
					<text class="popup-title">{{ updateInfo.updateTitle || '发现新版本' }}</text>
					<text class="popup-close" v-if="!updateInfo.forceUpdate" @click="closeUpdatePopup">×</text>
				</view>
				<view class="popup-content">
					<view class="version-info">
						<text class="current-version">当前版本: v{{ appVersion.version }}</text>
						<text class="new-version">新版本: v{{ updateInfo.versionName }}</text>
					</view>
					<scroll-view class="update-desc" scroll-y>
						<text class="update-desc-text">{{ updateInfo.updateContent }}</text>
					</scroll-view>
					<view class="update-meta">
						<text class="file-size">文件大小: {{ updateInfo.fileSize }}</text>
						<text class="publish-time">发布时间: {{ formatTime(updateInfo.publishTime) }}</text>
					</view>
					<view class="update-actions">
						<view class="update-btn cancel" v-if="!updateInfo.forceUpdate" @click="closeUpdatePopup">暂不更新
						</view>
						<view class="update-btn confirm" @click="confirmUpdate">立即更新</view>
					</view>
				</view>
			</view>
		</uni-popup>

		<!-- 下载进度弹窗 -->
		<uni-popup ref="downloadPopup" type="center" background-color="#fff">
			<view class="download-popup">
				<view class="popup-header">
					<text class="popup-title">下载更新</text>
				</view>
				<view class="popup-content">
					<view class="progress-container">
						<view class="progress-bar">
							<view class="progress-inner" :style="{ width: downloadProgress + '%' }"></view>
						</view>
						<text class="progress-text">{{ downloadProgress }}%</text>
					</view>
					<text class="download-tip">请耐心等待，下载完成后将自动安装...</text>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	import updateManager from '@/common/updateManager.js';
	export default {
		name: "about",
		data() {
			return {
				statusBarHeight: 0,
				appVersion: {
					version: '1.0.0',
					name: '枫萱悦宁',
					versionCode: 0
				},
				checking: false,
				isLatest: true,
				hasNewVersion: false,
				downloadProgress: 0,
				updateInfo: {
					versionCode: 0,
					versionName: '',
					downloadUrl: '',
					updateTitle: '',
					updateContent: '',
					forceUpdate: false,
					fileSize: '',
					publishTime: ''
				}
			};
		},
		onLoad() {
			this.getStatusBarHeight();
			this.getAppVersion();
			// 设置更新回调
			updateManager.setUpdateCallback(this.handleUpdateCallback.bind(this));
		},
		methods: {
			getStatusBarHeight() {
				const systemInfo = uni.getSystemInfoSync();
				this.statusBarHeight = systemInfo.statusBarHeight;
			},
			// 处理更新回调
			handleUpdateCallback(result) {
				if (result.hasUpdate) {
					// 发现新版本
					this.hasNewVersion = true;
					this.isLatest = false;
					this.updateInfo = result.versionInfo;
					this.$refs.updatePopup.open();
				} else if (result.isLatest) {
					// 已是最新版本
					this.hasNewVersion = false;
					this.isLatest = true;
					this.checking = false;
				} else if (result.downloading) {
					// 下载中
					this.downloadProgress = result.progress;
					if (result.progress === 0) {
						this.$refs.downloadPopup.open();
					}
				} else if (result.downloadComplete) {
					// 下载完成
					this.$refs.downloadPopup.close();
					uni.showModal({
						title: '更新完成',
						content: '新版本安装完成，需要重启应用生效',
						showCancel: false,
						confirmText: '立即重启',
						success: (res) => {
							if (res.confirm) {
								plus.runtime.restart();
							}
						}
					});
				} else if (result.downloadError) {
					// 下载错误
					this.$refs.downloadPopup.close();
					uni.showModal({
						title: '更新失败',
						content: result.error || '更新失败，请重试',
						showCancel: false,
						confirmText: '确定'
					});
				} else if (result.error) {
					// 检查更新错误
					this.checking = false;
					uni.showToast({
						title: result.error,
						icon: 'none'
					});
				}
			},
			async getAppVersion() {
				this.appVersion = await this.getCurrentVersion();
			},
			// 获取当前App版本信息
			getCurrentVersion() {
				return new Promise((resolve, reject) => {
					plus.runtime.getProperty(plus.runtime.appid, (inf) => {
						resolve({
							version: inf.version,
							versionCode: parseInt(inf.versionCode),
							name: inf.name
						});
					}, reject);
				});
			},
			handleBack() {
				uni.navigateBack();
			},
			// 功能介绍
			showFeatureIntro() {
				uni.navigateTo({
					url: '/pages/about/version/version'
				});
			},
			// 检查更新
			async checkUpdate() {
				this.checking = true;
				try {
					await updateManager.checkUpdate();
				} catch (error) {
					console.error('检查更新失败:', error);
					this.checking = false;
					uni.showToast({
						title: '检查更新失败',
						icon: 'none'
					});
				}
			},
			closeUpdatePopup() {
				this.$refs.updatePopup.close();
				this.checking = false;
			},
			// 确认更新
			async confirmUpdate() {
				this.closeUpdatePopup();
				try {
					await updateManager.downloadAndInstall(this.updateInfo);
				} catch (error) {
					console.error('更新失败:', error);
				}
			},
			// 格式化时间
			formatTime(timeString) {
				if (!timeString) return '';
				try {
					const date = new Date(timeString);
					return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
				} catch (e) {
					return timeString;
				}
			},
			// 处理投诉
			handleComplaint() {
				uni.navigateTo({
					url: '/pages/user/complaint/complaint'
				});
			},
			// 访问官网
			visitWebsite() {
				// 在App中打开网页
				// #ifdef APP-PLUS
				plus.runtime.openWeb('http://180.76.189.135');
				// #endif

				// #ifdef H5
				window.open('http://180.76.189.135', '_blank');
				// #endif
			},
			// 拨打电话
			makeCall() {
				uni.makePhoneCall({
					phoneNumber: '4001234567'
				});
			}
		}
	};
</script>

<style lang="scss" scoped>
	@import '@/scss/variables';

	.about-container {
		min-height: 100vh;
		background: #f5f7fa;
		position: relative;
	}

	.background-gradient {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		height: 300rpx;
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

	/* App信息卡片 */
	.app-info-card {
		background: rgba(255, 255, 255, 0.95);
		border-radius: 20rpx;
		padding: 60rpx 30rpx;
		margin: 30rpx 0;
		text-align: center;
		box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
		backdrop-filter: blur(10rpx);

		.logo-wrapper {
			width: 120rpx;
			height: 120rpx;
			border-radius: 24rpx;
			background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
			display: flex;
			align-items: center;
			justify-content: center;
			margin: 0 auto 30rpx;

			.app-logo {
				width: 70rpx;
				height: 70rpx;
				border-radius: 14rpx;
			}
		}

		.app-name {
			display: block;
			font-size: 36rpx;
			font-weight: bold;
			color: #333;
			margin-bottom: 16rpx;
		}

		.app-version {
			font-size: 28rpx;
			color: #000;
		}
	}

	/* 菜单区域 */
	.menu-section {
		background: rgba(255, 255, 255, 0.95);
		border-radius: 20rpx;
		padding: 0;
		margin-bottom: 30rpx;
		box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
		overflow: hidden;
	}

	.menu-list {
		.menu-item {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 30rpx;
			border-bottom: 1rpx solid #f5f5f5;
			transition: all 0.3s ease;

			&:active {
				background: rgba(0, 0, 0, 0.03);
			}

			&:last-child {
				border-bottom: none;
			}

			.menu-item-left {
				display: flex;
				align-items: center;
			}

			.menu-text {
				font-size: 30rpx;
				color: #333;
				font-weight: 500;
			}

			.menu-right {
				display: flex;
				align-items: center;
			}

			.update-status {
				font-size: 24rpx;
				color: #999;
				margin-right: 16rpx;

				&.latest {
					color: #52c41a;
				}

				&.new-version {
					color: #ff6b35;
					font-weight: bold;
				}
			}

			.menu-arrow {
				font-size: 36rpx;
				color: #999;
			}
		}
	}

	/* 其他信息区域 */
	.info-section {
		background: rgba(255, 255, 255, 0.95);
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);

		.info-item {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 20rpx 0;
			border-bottom: 1rpx solid #f5f5f5;

			&:last-child {
				border-bottom: none;
			}

			.info-label {
				font-size: 28rpx;
				color: #666;
			}

			.info-value {
				font-size: 28rpx;
				color: #333;
			}
		}
	}

	/* 底部版权 */
	.copyright {
		text-align: center;
		padding: 40rpx 0;

		.copyright-text {
			font-size: 24rpx;
			color: #999;
		}
	}

	/* 更新弹窗 */
	.update-popup {
		width: 600rpx;
		background: #fff;
		border-radius: 20rpx;
		overflow: hidden;

		.popup-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 30rpx;
			border-bottom: 1rpx solid #f5f5f5;

			.popup-title {
				font-size: 32rpx;
				font-weight: bold;
				color: #333;
			}

			.popup-close {
				font-size: 40rpx;
				color: #999;
			}
		}

		.popup-content {
			padding: 30rpx;
		}

		.version-info {
			display: flex;
			justify-content: space-between;
			margin-bottom: 20rpx;

			.current-version,
			.new-version {
				font-size: 24rpx;
				color: #666;
			}

			.new-version {
				color: $primary-color;
				font-weight: bold;
			}
		}

		.update-desc {
			max-height: 300rpx;
			margin-bottom: 20rpx;
			padding: 20rpx;
			background: #f8f9fa;
			border-radius: 12rpx;

			.update-desc-text {
				font-size: 26rpx;
				color: #666;
				line-height: 1.6;
				white-space: pre-line;
			}
		}

		.update-meta {
			margin-bottom: 30rpx;

			.file-size,
			.publish-time {
				display: block;
				font-size: 22rpx;
				color: #999;
				line-height: 1.6;
			}
		}

		.update-actions {
			display: flex;
			gap: 20rpx;

			.update-btn {
				flex: 1;
				padding: 24rpx;
				text-align: center;
				border-radius: 12rpx;
				font-size: 28rpx;
				font-weight: 500;

				&.cancel {
					background: #f5f5f5;
					color: #666;
				}

				&.confirm {
					background: linear-gradient(135deg, $primary-color, $accent-color);
					color: #fff;
				}
			}
		}
	}

	/* 下载进度弹窗 */
	.download-popup {
		width: 500rpx;
		background: #fff;
		border-radius: 20rpx;
		overflow: hidden;

		.popup-header {
			padding: 30rpx;
			border-bottom: 1rpx solid #f5f5f5;
			text-align: center;

			.popup-title {
				font-size: 32rpx;
				font-weight: bold;
				color: #333;
			}
		}

		.popup-content {
			padding: 40rpx 30rpx;
		}

		.progress-container {
			display: flex;
			align-items: center;
			margin-bottom: 30rpx;

			.progress-bar {
				flex: 1;
				height: 16rpx;
				background: #f0f0f0;
				border-radius: 8rpx;
				overflow: hidden;
				margin-right: 20rpx;

				.progress-inner {
					height: 100%;
					background: linear-gradient(135deg, $primary-color, $accent-color);
					border-radius: 8rpx;
					transition: width 0.3s ease;
				}
			}

			.progress-text {
				font-size: 24rpx;
				color: #666;
				min-width: 80rpx;
			}
		}

		.download-tip {
			font-size: 24rpx;
			color: #999;
			text-align: center;
			line-height: 1.6;
		}
	}
</style>