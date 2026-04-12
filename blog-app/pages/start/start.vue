<template>
	<view class="splash-container">
		<view class="splash-content">
			<view class="logo">
				<text class="logo-text">枫萱悦宁</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		onLoad() {
			setTimeout(() => {
				uni.redirectTo({
					url: '/pages/login/login' 
				});
			}, 3000);
		}
	}
</script>

<style scoped>
	.splash-container {
		position: fixed;  
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		display: flex;
		justify-content: center;
		align-items: center;
		background: linear-gradient(135deg, #8B4513 0%, #D4AF37 100%);
	}

	.splash-content {
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.logo {
		width: 400rpx;
		height: 400rpx;
		border-radius: 50%;
		background: rgba(255, 255, 255, 0.2);
		display: flex;
		justify-content: center;
		align-items: center;
		backdrop-filter: blur(10px);
		border: 1px solid rgba(255, 255, 255, 0.3);
		animation: zoomIn 1.5s ease-out;
	}

	.logo-text {
		font-size: 100rpx;
		color: #fff;
		font-weight: bold;
	}
	
	
	@keyframes zoomIn {
	  from {
	    opacity: 0;
	    transform: scale(0.5);
	  }
	  to {
	    opacity: 1;
	    transform: scale(1);
	  }
	}
</style>