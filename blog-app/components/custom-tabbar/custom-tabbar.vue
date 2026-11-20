<template>
	<view class="custom-tabbar">
		<view v-for="(item, index) in tabList" :key="index" class="tabbar-item" :class="{ 
				active: currentTab === index,
				'center-item': item.isCenter,
				'disabled': isNavigating
			}" @click="handleTabClick(item, index)">
			<view class="tabbar-icon">
				<text class="iconfont" :class="item.icon"></text>
				<!-- <view class="tabbar-badge" v-if="item.badge && item.badge > 0">
					{{ item.badge > 99 ? '99+' : item.badge }}
				</view> -->
			</view>
			<text class="tabbar-text">{{ item.text }}</text>
		</view>
	</view>
</template>

<script>
	export default {
		name: "CustomTabbar",
		props: {
			currentTab: {
				type: Number,
				default: 0
			},
			customTabs: {
				type: Array,
				default: null
			},
			badges: {
				type: Object,
				default: () => ({})
			}
		},
		data() {
			return {
				defaultTabList: [{
						pagePath: "/pages/index/index",
						icon: "icon-shouye",
						text: "首页"
					},
					{
						pagePath: "/pages/user/my-page/my-page",
						icon: "icon-wode",
						text: "我"
					}
				],
				isNavigating: false,
				navigateTimer: null
			};
		},
		computed: {
			tabList() {
				const tabs = this.customTabs || this.defaultTabList;
				return tabs.map((tab, index) => ({
					...tab,
					badge: this.badges[index] || 0
				}));
			}
		},
		methods: {
			handleTabClick(item, index) {
				if (this.isNavigating) return;
				if (this.currentTab === index) return;

				this.isNavigating = true;
				this.showLoading();

				// 执行跳转
				this.navigateToPage(item.pagePath);

				// 设置超时保护
				this.navigateTimer = setTimeout(() => {
					this.hideLoading();
				}, 3000);
			},

			navigateToPage(path) {
				if (!path) {
					this.hideLoading();
					return;
				}

				uni.switchTab({
					url: path,
					success: () => {
						// 延迟隐藏loading，确保页面切换完成
						setTimeout(() => {
							this.hideLoading();
						}, 600);
					},
					fail: () => {
						this.hideLoading();
						// 备用跳转方案
						uni.reLaunch({
							url: path
						});
					}
				});
			},

			showLoading() {
				uni.showLoading({
					title: '加载中...',
					mask: true
				});
			},

			hideLoading() {
				this.isNavigating = false;
				if (this.navigateTimer) {
					clearTimeout(this.navigateTimer);
					this.navigateTimer = null;
				}
				uni.hideLoading();
			},

			setBadge(index, count) {
				this.$emit('update:badges', {
					...this.badges,
					[index]: count
				});
			},

			clearBadge(index) {
				const newBadges = {
					...this.badges
				};
				delete newBadges[index];
				this.$emit('update:badges', newBadges);
			}
		}
	};
</script>

<style lang="scss" scoped>
	.custom-tabbar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		height: 90rpx;
		background: #5D4037;
		border-top: 2rpx solid #D4AF37;
		backdrop-filter: blur(20rpx);
		display: flex;
		align-items: center;
		justify-content: space-around;
		// border-top: 1rpx solid rgba(0, 0, 0, 0.06);
		box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.08);
		z-index: 999;
		padding-bottom: env(safe-area-inset-bottom);

		.tabbar-item {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			flex: 1;
			height: 100%;
			transition: all 0.3s ease;
			position: relative;

			&.disabled {
				opacity: 0.6;
				pointer-events: none;
			}

			.tabbar-icon {
				position: relative;
				width: 48rpx;
				height: 48rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				margin-bottom: 8rpx;

				.iconfont {
					font-size: 40rpx;
					color: #A1887F;
					transition: all 0.3s ease;
				}

				.tabbar-badge {
					position: absolute;
					top: -8rpx;
					right: -8rpx;
					background: #ff4d4f;
					color: white;
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

			.tabbar-text {
				font-size: 30rpx;
				color: #A1887F;
				transition: all 0.3s ease;
			}

			&.active {
				.tabbar-icon .iconfont {
					color: #D4AF37;
				}

				.tabbar-text {
					color: #A1887F;
					font-weight: 500;
				}
			}

			&.center-item {
				flex: 0 0 auto;

				.tabbar-icon {
					width: 80rpx;
					height: 80rpx;
					border-radius: 50%;
					box-shadow: 0 8rpx 20rpx rgba(102, 126, 234, 0.3);

					.iconfont {
						font-size: 44rpx;
						color: white;
					}
				}

				.tabbar-text {
					font-weight: 500;
				}
			}

			&:active:not(.center-item) {
				background: rgba(#8B4513, 0.05);
			}
		}
	}

	// 响应式适配
	@media (max-width: 750px) {
		.custom-tabbar {
			height: 90rpx;

			.tabbar-item {
				.tabbar-icon {
					width: 40rpx;
					height: 40rpx;
					margin-bottom: 16rpx;

					.iconfont {
						font-size: 36rpx;
					}

					.tabbar-badge {
						font-size: 18rpx;
						min-width: 20rpx;
						height: 20rpx;
					}
				}

				.tabbar-text {
					font-size: 22rpx;
				}

				&.center-item .tabbar-icon {
					width: 70rpx;
					height: 70rpx;

					.iconfont {
						font-size: 40rpx;
					}
				}
			}
		}
	}
</style>