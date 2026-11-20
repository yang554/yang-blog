<template>
	<view class="home-container">
		<!-- 背景渐变 -->
		<view class="background-gradient"></view>

		<!-- 顶部导航栏 -->
		<custom-navbar :statusBarHeight="statusBarHeight" title="用户管理" :unreadCount="2"
			@notificationClick="handleNotification" @settingsClick="handleSettings" />

		<!-- 页面内容 -->
		<view class="content-wrapper" :style="{
			paddingTop: (statusBarHeight + 70) + 'px',
			height: `calc(100vh - ${statusBarHeight +5 }px`
		}">
			<scroll-view class="main-content" scroll-y :show-scrollbar="false" refresher-background="transparent"
				:style="{ height: '100%' }">


			</scroll-view>
		</view>

		<!-- 底部导航栏 -->
		<custom-tabbar :currentTab="3" @centerClick="handleCenterClick" />
	</view>
</template>

<script>
	import CustomNavbar from '@/components/custom-navbar/custom-navbar.vue'
	import CustomTabbar from '@/components/custom-tabbar/custom-tabbar.vue'
	import uCharts from '@qiun/ucharts';

	export default {
		components: {
			CustomNavbar,
			CustomTabbar
		},
		name: "WealthReportView",
		data() {
			return {
				statusBarHeight: 0,
			};
		},
		onLoad() {
			this.getStatusBarHeight();
		},
		onUnload() {

		},
		methods: {
			getStatusBarHeight() {
				const systemInfo = uni.getSystemInfoSync();
				this.statusBarHeight = systemInfo.statusBarHeight;
			},
			handleNotification() {
				// 处理通知
			},
			handleSettings() {
				// 处理设置
			},
			// 处理中间按钮点击
			handleCenterClick() {
				// 显示快速记录选择
				uni.showActionSheet({
					itemList: ['记收入', '记支出', '写博客', '健康记录'],
					success: (res) => {
						const actions = [
							'/pages/life/add-daily-flow/add-daily-flow?type=income',
							'/pages/life/add-daily-flow/add-daily-flow?type=expense',
							'/pages/blog/write/write',
							'/pages/aunt/aunt-add/aunt-add'
						];
						this.navigateTo(actions[res.tapIndex]);
					}
				});
			},

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
		/* 高度通过动态样式计算，确保不会超出屏幕 */
		box-sizing: border-box;
		overflow: hidden;
	}

	/* 主要内容区域 */
	.main-content {
		/* 高度设置为100%填充父容器 */
		box-sizing: border-box;
		overflow: hidden;
		background: transparent;
		padding: 0 30rpx;
	}
</style>