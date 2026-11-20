<!-- 顶部导航 -->
<template>
	<view class="custom-navbar" :style="{ paddingTop: statusBarHeight + 'px' }">
		<view class="navbar-content">
			<view class="navbar-left">
				<view class="logo-wrapper">
					<image src="/static/logo.png" class="navbar-logo" mode="aspectFit"></image>
				</view>
				<text class="navbar-title">{{ title }}</text>
			</view>
			<view class="navbar-actions">
				<view class="action-icon" @click="handleNotification">
					<text class="iconfont icon-tongzhi"></text>
					<view class="badge" v-if="unreadCount > 0">{{ unreadCount > 99 ? '99+' : unreadCount }}</view>
				</view>
				<view class="action-icon" @click="toggleSettingsMenu">
					<text class="iconfont icon-shezhi"></text>
				</view>
			</view>
		</view>

		<!-- 设置菜单和遮罩层放在 navbar-content 外面 -->
		<view v-if="showSettingsMenu" class="menu-mask" @tap="closeSettingsMenu"></view>
		<view v-if="showSettingsMenu" class="settings-menu" @tap.stop>
			<view class="menu-item" @tap="handlePersonalCenter">
				<text class="iconfont icon-gerenzhongxin"></text>
				<text class="menu-text">个人中心</text>
			</view>
			<view class="menu-divider"></view>
			<view class="menu-item logout" @tap="handleLogout">
				<text class="iconfont icon-tuichu"></text>
				<text class="menu-text">退出登录</text>
			</view>
		</view>

		<!-- 退出确认弹窗 -->
		<view v-if="showLogoutConfirm" class="logout-modal" @tap="cancelLogout">
			<view class="modal-content" @tap.stop>
				<view class="modal-header">
					<text class="modal-title">确认退出</text>
				</view>
				<view class="modal-body">
					<text class="modal-text">确定要退出登录吗？</text>
				</view>
				<view class="modal-actions">
					<view class="modal-btn cancel" @tap="cancelLogout">取消</view>
					<view class="modal-btn confirm" @tap="confirmLogout">确定</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "CustomNavbar",
		props: {
			// 状态栏高度
			statusBarHeight: {
				type: Number,
				default: 0
			},
			// 标题
			title: {
				type: String,
				default: "枫萱悦宁"
			},
			// 未读通知数量
			unreadCount: {
				type: Number,
				default: 0
			},
			// 是否显示返回按钮
			showBack: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				showSettingsMenu: false, // 控制设置菜单显示
				showLogoutConfirm: false // 控制退出确认弹窗
			};
		},
		methods: {
			// 处理通知点击
			handleNotification() {
				uni.navigateTo({
					url: '/pages/notification/notification'
				});
			},

			// 切换设置菜单显示状态
			toggleSettingsMenu() {
				this.showSettingsMenu = !this.showSettingsMenu;
			},

			// 关闭设置菜单
			closeSettingsMenu() {
				this.showSettingsMenu = false;
			},

			// 处理个人中心
			handlePersonalCenter() {
				uni.switchTab({
					url: '/pages/user/my-page/my-page'
				});
				this.closeSettingsMenu();
			},

			// 处理退出登录
			handleLogout() {
				this.closeSettingsMenu();
				this.showLogoutConfirm = true;
			},

			// 取消退出
			cancelLogout() {
				this.showLogoutConfirm = false;
			},

			// 确认退出
			confirmLogout() {
				this.showLogoutConfirm = false;

				// 清除token、用户信息、记住我
				uni.removeStorageSync(this.$constants.TOKEN);
				uni.removeStorageSync(this.$constants.USER_INFO);
				uni.removeStorageSync(this.$constants.REMEMBER_ME);

				// 显示退出成功提示
				uni.showToast({
					title: '退出成功',
					icon: 'success',
					duration: 1500
				});

				// 跳转到登录页面
				setTimeout(() => {
					uni.reLaunch({
						url: this.$PATHS.LOGIN
					});
				}, 1500);
			},

			// 处理返回
			handleBack() {
				uni.navigateBack();
			}
		}
	};
</script>

<style lang="scss" scoped>
	$primary-color: #667eea;
	$text-white: #ffffff;
	$error-color: #ff4d4f;
	$bg-dark: rgba(0, 0, 0, 0.5);

	.custom-navbar {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		background: transparent;
		z-index: 1000;

		.navbar-content {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 20rpx 30rpx;
			position: relative;
			z-index: 1002;

			.navbar-left {
				display: flex;
				align-items: center;

				.logo-wrapper {
					width: 60rpx;
					height: 60rpx;
					border-radius: 16rpx;
					background: rgba(255, 255, 255, 0.2);
					backdrop-filter: blur(10rpx);
					display: flex;
					align-items: center;
					justify-content: center;
					margin-right: 20rpx;

					.navbar-logo {
						width: 40rpx;
						height: 40rpx;
						border-radius: 10rpx;
					}
				}

				.navbar-title {
					font-size: 36rpx;
					font-weight: bold;
					color: $text-white;
					text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.2);
				}
			}

			.navbar-actions {
				display: flex;
				gap: 20rpx;
				position: relative;

				.action-icon {
					position: relative;
					width: 80rpx;
					height: 80rpx;
					border-radius: 50%;
					display: flex;
					align-items: center;
					justify-content: center;
					background: rgba(255, 255, 255, 0.2);
					backdrop-filter: blur(10rpx);
					transition: all 0.3s ease;

					&:active {
						background: rgba(255, 255, 255, 0.3);
						transform: scale(0.95);
					}

					.iconfont {
						font-size: 36rpx;
						color: $text-white;
					}

					.badge {
						position: absolute;
						top: 8rpx;
						right: 8rpx;
						background: $error-color;
						color: $text-white;
						font-size: 20rpx;
						padding: 4rpx 8rpx;
						border-radius: 20rpx;
						min-width: 24rpx;
						height: 24rpx;
						display: flex;
						align-items: center;
						justify-content: center;
						line-height: 1;
					}
				}
			}
		}

		// 菜单遮罩层 
		.menu-mask {
			position: fixed;
			top: 0;
			left: 0;
			right: 0;
			bottom: 0;
			background: rgba(0, 0, 0, 0.3);
			z-index: 1001;
		}

		// 设置菜单 
		.settings-menu {
			position: fixed;
			top: 180rpx;
			/* 调整位置，确保在导航栏下方 */
			right: 30rpx;
			background: rgba(255, 255, 255, 0.95);
			backdrop-filter: blur(20rpx);
			border-radius: 16rpx;
			padding: 20rpx 0;
			min-width: 240rpx;
			box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.15);
			z-index: 1002;
			animation: menuSlideDown 0.3s ease;

			.menu-item {
				display: flex;
				align-items: center;
				padding: 20rpx 30rpx;
				transition: all 0.3s ease;

				&:active {
					background: rgba(0, 0, 0, 0.05);
				}

				.iconfont {
					font-size: 32rpx;
					color: #666;
					margin-right: 20rpx;
					width: 40rpx;
					text-align: center;
				}

				.menu-text {
					font-size: 28rpx;
					color: #333;
					font-weight: 500;
				}

				&.logout {
					.iconfont {
						color: $error-color;
					}

					.menu-text {
						color: $error-color;
					}
				}
			}

			.menu-divider {
				height: 1rpx;
				background: rgba(0, 0, 0, 0.1);
				margin: 10rpx 0;
			}
		}

		// 退出确认弹窗
		.logout-modal {
			position: fixed;
			top: 0;
			left: 0;
			right: 0;
			bottom: 0;
			background: $bg-dark;
			display: flex;
			align-items: center;
			justify-content: center;
			z-index: 1003;
			animation: fadeIn 0.3s ease;

			.modal-content {
				background: #fff;
				border-radius: 20rpx;
				width: 600rpx;
				overflow: hidden;
				animation: scaleIn 0.3s ease;

				.modal-header {
					padding: 40rpx 30rpx 20rpx;
					text-align: center;

					.modal-title {
						font-size: 32rpx;
						font-weight: bold;
						color: #333;
					}
				}

				.modal-body {
					padding: 20rpx 30rpx 40rpx;
					text-align: center;

					.modal-text {
						font-size: 28rpx;
						color: #666;
						line-height: 1.5;
					}
				}

				.modal-actions {
					display: flex;
					border-top: 1rpx solid #eee;

					.modal-btn {
						flex: 1;
						padding: 30rpx;
						text-align: center;
						font-size: 28rpx;
						font-weight: 500;
						transition: all 0.3s ease;

						&.cancel {
							color: #666;
							border-right: 1rpx solid #eee;

							&:active {
								background: #f5f5f5;
							}
						}

						&.confirm {
							color: $error-color;

							&:active {
								background: #fff5f5;
							}
						}
					}
				}
			}
		}
	}

	// 动画定义
	@keyframes menuSlideDown {
		from {
			opacity: 0;
			transform: translateY(-20rpx);
		}

		to {
			opacity: 1;
			transform: translateY(0);
		}
	}

	@keyframes fadeIn {
		from {
			opacity: 0;
		}

		to {
			opacity: 1;
		}
	}

	@keyframes scaleIn {
		from {
			opacity: 0;
			transform: scale(0.9);
		}

		to {
			opacity: 1;
			transform: scale(1);
		}
	}

	// 响应式设计
	@media (max-width: 750px) {
		.custom-navbar {
			.navbar-content {
				padding: 20rpx 20rpx;

				.navbar-left {
					.logo-wrapper {
						width: 50rpx;
						height: 50rpx;
						margin-right: 16rpx;

						.navbar-logo {
							width: 32rpx;
							height: 32rpx;
						}
					}

					.navbar-title {
						font-size: 32rpx;
					}
				}

				.navbar-actions {
					.action-icon {
						width: 70rpx;
						height: 70rpx;

						.iconfont {
							font-size: 32rpx;
						}
					}
				}
			}

			.settings-menu {
				right: 30rpx;
				min-width: 220rpx;
				top: 180rpx;

				.menu-item {
					padding: 18rpx 25rpx;

					.iconfont {
						font-size: 30rpx;
						margin-right: 16rpx;
					}

					.menu-text {
						font-size: 26rpx;
					}
				}
			}

			.logout-modal {
				.modal-content {
					width: 560rpx;

					.modal-header {
						padding: 35rpx 25rpx 15rpx;
					}

					.modal-body {
						padding: 15rpx 25rpx 35rpx;
					}

					.modal-actions {
						.modal-btn {
							padding: 25rpx;
						}
					}
				}
			}
		}
	}
</style>