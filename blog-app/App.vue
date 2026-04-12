<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
			uni.hideTabBar();
		},
		onShow: function() {
			console.log('App Show')
			uni.hideTabBar();
		},
		onHide: function() {
			console.log('App Hide')
			uni.hideTabBar();
		}
	}
</script>

<style>
	/*每个页面公共css */
	@import '@/static/fonts/iconfont/iconfont.css';

	/* 全局图标样式 */
	.iconfont {
		font-family: "iconfont" !important;
		font-size: 36rpx !important;
		font-style: normal;
		-webkit-font-smoothing: antialiased;
		-moz-osx-font-smoothing: grayscale;
	}

	.icon {
		width: 70rpx;
		height: 70rpx;
	}
</style>