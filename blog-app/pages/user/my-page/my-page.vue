<template>
	<view class="home-container">
		<!-- 背景渐变 -->
		<view class="background-gradient"></view>

		<!-- 顶部导航栏 -->
		<custom-navbar :statusBarHeight="statusBarHeight" title="个人中心" @notificationClick="handleNotification"
			@settingsClick="handleSettings" />

		<!-- 页面内容 -->
		<view class="content-wrapper" :style="{
			paddingTop: (statusBarHeight + 70) + 'px',
			height: `calc(100vh - ${statusBarHeight + 5}px)`
		}">
			<scroll-view class="main-content" scroll-y :show-scrollbar="false" refresher-background="transparent"
				:style="{ height: '100%' }">

				<!-- 用户信息卡片 -->
				<view class="user-card">
					<view class="user-header">
						<image class="avatar" :src="getFullAvatarUrl(userInfo.avatar)" mode="aspectFill"></image>
						<view class="user-basic-info">
							<text class="username">{{ userInfo.username }}</text>
							<text class="signature" v-if="userInfo.description">{{ userInfo.description }}</text>
							<text class="signature placeholder" v-else>这个人很懒，什么都没有留下~</text>
						</view>
						<view class="edit-btn" @click="navigateToEdit">
							<text class="icon-arrow">›</text>
						</view>
					</view>
				</view>

				<!-- 功能菜单 -->
				<view class="menu-section">
					<view class="menu-list">
						<!-- 个人资料 -->
						<view class="menu-item" @click="navigateToEdit">
							<view class="menu-item-left">
								<view class="menu-icon">
									<image src="@/static/icon/gerenziliao.png" mode="aspectFit" class="icon" />
								</view>
								<text class="menu-text">个人资料</text>
							</view>
							<text class="menu-arrow">›</text>
						</view>

						<!-- 账号安全 -->
						<view class="menu-item" @click="navigateToSecurity">
							<view class="menu-item-left">
								<view class="menu-icon">
									<image src="@/static/icon/zhanghaoanquan.png" mode="aspectFit" class="icon" />
								</view>
								<text class="menu-text">账号安全</text>
							</view>
							<text class="menu-arrow">›</text>
						</view>

						<!-- 关于 -->
						<view class="menu-item" @click="navigateToAbout">
							<view class="menu-item-left">
								<view class="menu-icon">
									<image src="@/static/icon/guanyu1.png" mode="aspectFit" class="icon" />
								</view>
								<text class="menu-text">关于</text>
							</view>
							<text class="menu-arrow">›</text>
						</view>
					</view>
				</view>

				<!-- 底部安全区域 -->
				<view class="safe-area"></view>
			</scroll-view>
		</view>

		<!-- 自定义底部导航栏组件 -->
		<custom-tabbar :currentTab="1" />
	</view>
</template>

<script>
	import CustomNavbar from '@/components/custom-navbar/custom-navbar.vue'
	import CustomTabbar from '@/components/custom-tabbar/custom-tabbar.vue'
	import {
		IMG_URL
	} from '@/api/config.js'
	import {
		parseDate
	} from '@/common/formatDate.js'

	export default {
		components: {
			CustomNavbar,
			CustomTabbar
		},
		name: "mypage",
		data() {
			return {
				statusBarHeight: 0,
				userInfo: {}
			};
		},
		onShow() {
			this.loadUserInfo();
		},
		onReady() {
			uni.hideTabBar();
		},
		onLoad() {
			this.getStatusBarHeight();
			this.loadUserInfo();
		},
		methods: {
			getStatusBarHeight() {
				const systemInfo = uni.getSystemInfoSync();
				this.statusBarHeight = systemInfo.statusBarHeight;
			},
			// 图片资源路径处理
			getFullAvatarUrl(avatarPath) {
				if (!avatarPath) return '/static/defaultImg.png';

				if (avatarPath.startsWith('http')) {
					return avatarPath;
				}

				if (avatarPath.startsWith('/')) {
					return IMG_URL + avatarPath;
				}

				return avatarPath;
			},
			loadUserInfo() {
				this.userInfo = uni.getStorageSync(this.$constants.USER_INFO) || {};
			},
			formatStatus(status) {
				const statusMap = {
					0: '禁用',
					1: '正常',
					2: '冻结'
				};
				return statusMap[status] || '未知';
			},
			formatDate(dateString) {
				if (!dateString) return '-';
				const date = new Date(dateString);
				return parseDate(date);
			},
			navigateTo(url) {
				uni.navigateTo({
					url: url
				});
			},
			navigateToEdit() {
				this.navigateTo('/pages/user/edit-profile/edit-profile');
			},
			navigateToSecurity() {
				this.navigateTo('/pages/user/account-security/account-security');
			},
			navigateToAbout() {
				this.navigateTo('/pages/about/about');
			},
			handleNotification() {
				// 处理通知
				uni.navigateTo({
					url: '/pages/notification/notification'
				});
			},
			handleSettings() {
				// 处理设置
				uni.navigateTo({
					url: '/pages/settings/settings'
				});
			}
		}
	};
</script>

<style lang="scss" scoped>
	@import '@/scss/variables';

	.home-container {
		min-height: 100vh;
		background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
		position: relative;
	}

	.background-gradient {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		height: 100%;
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

	/* 用户卡片样式 */
	.user-card {
		background: rgba(255, 255, 255, 0.95);
		border-radius: 20rpx;
		padding: 40rpx 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
		backdrop-filter: blur(10rpx);
	}

	.user-header {
		display: flex;
		align-items: center;
		margin-bottom: 40rpx;
	}

	.avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		border: 4rpx solid rgba(255, 255, 255, 0.8);
		box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.1);
	}

	.user-basic-info {
		flex: 1;
		margin-left: 30rpx;
		display: flex;
		flex-direction: column;
	}

	.username {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}

	.signature {
		font-size: 24rpx;
		color: #666;
		line-height: 1.4;

		&.placeholder {
			color: #999;
			font-style: italic;
		}
	}

	.edit-text {
		margin-right: 8rpx;
	}

	.icon-arrow {
		font-size: 32rpx;
		line-height: 1;
	}

	/* 菜单区域样式 */
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

			.menu-icon {
				width: 50rpx;
				height: 50rpx;
				border-radius: 15rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				margin-right: 20rpx;
				font-size: 32rpx;
			}

			.menu-text {
				font-size: 30rpx;
				color: #333;
				font-weight: 500;
			}

			.menu-arrow {
				font-size: 36rpx;
				color: #999;
			}
		}
	}

	/* 安全区域 */
	.safe-area {
		height: 20rpx;
	}
</style>