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
							<text class="signature" v-if="userInfo.note">{{ userInfo.note }}</text>
							<text class="signature placeholder" v-else>这个人很懒，什么都没有留下~</text>
						</view>
						<view class="edit-btn" @click="navigateToEdit">
							<text class="edit-text">编辑资料</text>
							<text class="icon-arrow">›</text>
						</view>
					</view>
				</view>

				<!-- 详细信息区域 -->
				<view class="info-section">
					<view class="section-title">基本信息</view>
					<view class="info-list">
						<view class="info-item">
							<text class="info-label">用户ID</text>
							<text class="info-value">{{ userInfo.id }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">用户昵称</text>
							<text class="info-value">{{ userInfo.nickname || '未填写' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">职业</text>
							<text class="info-value">{{ userInfo.ext01 || '未绑定' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">手机号</text>
							<text class="info-value">{{ userInfo.phone || '未绑定' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">邮箱</text>
							<text class="info-value">{{ userInfo.email || '未绑定' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">地址</text>
							<text class="info-value">{{ userInfo.address || '未设置' }}</text>
						</view>
					</view>
				</view>

				<!-- 账户信息 -->
				<view class="info-section">
					<view class="section-title">账户信息</view>
					<view class="info-list">
						<view class="info-item">
							<text class="info-label">注册时间</text>
							<text class="info-value">{{ formatDate(userInfo.createTime) }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">最后登录</text>
							<text class="info-value">{{ formatDate(new Date()) }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">账户状态</text>
							<text class="info-value status-active">{{ formatStatus(userInfo.status) }}</text>
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
	import {IMG_URL} from '@/api/config.js'
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
				console.log('加载用户信息', this.userInfo);
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

	.edit-btn {
		display: flex;
		align-items: center;
		padding: 16rpx 24rpx;
		background: linear-gradient(135deg, $primary-color, $accent-color);
		border-radius: 50rpx;
		color: white;
		font-size: 24rpx;
	}

	.edit-text {
		margin-right: 8rpx;
	}

	.icon-arrow {
		font-size: 32rpx;
		line-height: 1;
	}

	/* 信息区域样式 */
	.info-section {
		background: rgba(255, 255, 255, 0.95);
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
	}

	.section-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 30rpx;
		padding-left: 10rpx;
		border-left: 6rpx solid $primary-color;
	}

	.info-list {
		.info-item {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 24rpx 0;
			border-bottom: 1rpx solid #f5f5f5;

			&:last-child {
				border-bottom: none;
			}
		}

		.info-label {
			font-size: 28rpx;
			color: #666;
		}

		.info-value {
			font-size: 28rpx;
			color: #333;

			&.status-active {
				color: #52c41a;
				font-weight: bold;
			}
		}
	}

	/* 安全区域 */
	.safe-area {
		height: 20rpx;
	}
</style>