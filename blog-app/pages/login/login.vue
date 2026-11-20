<template>
	<view class='login-container'>
		<!-- 背景渐变 -->
		<view class="background-gradient"></view>

		<!-- 主内容区域 -->
		<view class="main-content">
			<view class="login-card glass-card">
				<!-- 左侧品牌展示区 -->
				<view class="brand-section">
					<view class="logo-container">
						<view class="avatar-glow"></view>
						<image src='/static/logo.png' class="logo-image" mode="aspectFit"></image>
					</view>
					<view class="welcome-text">
						<text class="title">枫萱悦宁</text>
						<!-- <text class="subtitle">个人管理系统</text> -->
					</view>
					<view class="brand-slogan">
						<text>汇总日常点子</text>
					</view>
				</view>

				<!-- 右侧登录表单 -->
				<view class="form-section">
					<view class="form-header">
						<view class="header-icon">🌳</view>
						<text class="header-title">欢迎回来</text>
						<text class="header-subtitle">请登录您的账号继续使用系统</text>
					</view>

					<form @submit="submitForm">
						<!-- 用户名输入 -->
						<view class="input-group" :class="{ 'input-focused': focusedField === 'username' }">
							<text class="input-icon">👤</text>
							<input v-model="userForm.username" placeholder="请输入用户名" class="custom-input"
								placeholder-class="placeholder-style" @focus="setFocus('username')"
								@blur="clearFocus" />
							<view class="input-underline"></view>
						</view>

						<!-- 密码输入 -->
						<view class="input-group" :class="{ 'input-focused': focusedField === 'password' }">
							<text class="input-icon">🔒</text>
							<input v-model="userForm.password" placeholder="请输入密码" password class="custom-input"
								placeholder-class="placeholder-style" @focus="setFocus('password')"
								@blur="clearFocus" />
							<view class="input-underline"></view>
						</view>

						<!-- 验证码输入 -->
						<view class="input-group" :class="{ 'input-focused': focusedField === 'verify_code' }">
							<text class="input-icon">🔑</text>
							<view class="captcha-row">
								<input v-model="userForm.verify_code" placeholder="请输入验证码" class="captcha-input"
									placeholder-class="placeholder-style" @focus="setFocus('verify_code')"
									@blur="clearFocus" />
								<view class="input-underline"></view>
								<view class="captcha-wrapper" @tap="updateVerifyCode">
									<image :src="captchaImageUrl" class="captcha-image" mode="aspectFit"></image>
									<view class="captcha-overlay">
										<text class="captcha-hint">点击刷新</text>
									</view>
								</view>
							</view>
						</view>

						<!-- 记住我 & 忘记密码 -->
						<view class="remember-forgot">
							<label class="remember-me" @tap="toggleRememberMe">
								<view class="checkbox-container" :class="{ 'checkbox-checked': userForm.rememberMe }">
									<text class="checkmark" v-if="userForm.rememberMe">✓</text>
								</view>
								<text class="remember-text">记住我</text>
							</label>
							<text class="forgot-link" @tap="forgotPassword">忘记密码?</text>
						</view>

						<!-- 登录按钮 -->
						<button form-type="submit" class="login-btn primary" :disabled="loading"
							:class="{ 'btn-loading': loading }">
							<view class="btn-content">
								<text class="btn-text">{{ loading ? '登录中...' : '登 录' }}</text>
							</view>
							<view class="btn-loading-indicator" v-if="loading">
								<view class="loading-dot dot-1"></view>
								<view class="loading-dot dot-2"></view>
								<view class="loading-dot dot-3"></view>
							</view>
						</button>

						<!-- 游客登录按钮 -->
						<button class="login-btn guest" @tap="guestLogin" :disabled="loading">
							<text class="btn-text">游客登录</text>
						</button>

						<!-- 注册链接 -->
						<view class="register-section">
							<text class="register-text">还没有账号？</text>
							<text class="register-btn" @tap="registerBtn">立即注册</text>
						</view>
					</form>
				</view>
			</view>
		</view>

		<!-- 成功动画遮罩 -->
		<view v-if="showSuccessAnimation" class="success-animation">
			<view class="animation-content glass-card">
				<view class="success-icon">
					<view class="checkmark-circle"></view>
					<view class="checkmark-stem"></view>
					<view class="checkmark-kick"></view>
				</view>
				<text class="success-text">登录成功</text>
				<text class="redirect-text">正在加载...</text>
				<view class="loading-bar">
					<view class="loading-progress"></view>
				</view>
			</view>
		</view>

		<!-- 页脚 -->
		<view class="footer">
			<text class="footer-text">
				© 2025 枫萱悦宁家族管理系统 | 当前版本 v1.0.0
			</text>
		</view>
	</view>
</template>

<script>
	import {
		userApi
	} from '@/api/index.js'
	export default {
		name: 'LoginView',
		data() {
			return {
				showSuccessAnimation: false,
				loading: false,
				focusedField: '',
				userForm: {
					username: '',
					password: '',
					verify_code: '',
					captcha_key: '',
					rememberMe: false,
				},
				captchaImageUrl: '',
				captchaToken: '',
			};
		},
		onShow() {
			// 检查是否已登录且记住我
			this.checkAutoLogin();
		},
		methods: {
			// 检查自动登录
			checkAutoLogin() {
				// 检查是否已登录（有token和用户信息）
				const token = uni.getStorageSync(this.$constants.TOKEN);
				const userInfo = uni.getStorageSync(this.$constants.USER_INFO);
				const rememberMe = uni.getStorageSync(this.$constants.REMEMBER_ME);

				// 如果有token、用户信息且勾选了记住我，直接跳转到首页
				if (token && userInfo && rememberMe) {
					console.log('检测到已登录且记住我，跳转到首页...');
					uni.showToast({
						title: '登录中...',
						icon: 'none'
					});
					// 可以选择延迟跳转，避免页面闪烁
					setTimeout(() => {
						uni.reLaunch({
							url: this.$PATHS.INDEX
						});
					}, 1000);
				} else {
					console.log('未登录或未记住登录状态，显示登录页');
				}
			},
			// 登录提交
			async submitForm(e) {
				if (this.loading) return;

				// 简单验证
				if (!this.userForm.username || !this.userForm.password || !this.userForm.verify_code) {
					this.shakeEmptyFields();
					uni.showToast({
						title: '请填写完整信息',
						icon: 'none'
					});
					return;
				}

				this.loading = true;

				// 获取存储的captcha_key
				const captchaKey = uni.getStorageSync(this.$constants.CURRENT_CAPTCHA_KEY);
				if (!captchaKey) {
					uni.showToast({
						title: '请先获取验证码',
						icon: 'none'
					});
					this.updateVerifyCode();
					return;
				}

				// 添加到提交数据
				this.userForm.captcha_key = captchaKey;

				try {
					const result = await userApi.login(this.userForm);
					if (result.status === 200) {
						// 如果勾选了"记住我"，保存登录状态
						if (this.userForm.rememberMe) {
							// 保存记住我标记
							console.log("this.rememberMe",this.userForm.rememberMe)
							uni.setStorageSync(this.$constants.REMEMBER_ME, true);
							uni.setStorageSync(this.$constants.TOKEN, result.data.rememberMeToken);
						} else {
							// 清除记住我标记
							console.log("==============",this.userForm.rememberMe)
							uni.removeStorageSync(this.$constants.REMEMBER_ME);
							uni.setStorageSync(this.$constants.TOKEN, result.data.accessToken); 
						}

						// 显示成功动画
						this.showSuccessAnimation = true
						// 存储用户信息
						uni.setStorageSync(this.$constants.USER_INFO, result.data.user);
						
						console.log(result)

						// 跳转到首页
						setTimeout(() => {
							uni.reLaunch({
								url: this.$PATHS.INDEX
							})
						}, 1800);

					} else {
						uni.showToast({
							title: result.msg || '登录失败',
							icon: 'none'
						});
						this.updateVerifyCode(); // 刷新验证码
					}
				} catch (error) {
					console.error('登录错误:', error);
					uni.showToast({
						title: '网络错误，请重试',
						icon: 'none'
					});
					this.updateVerifyCode();
				} finally {
					this.loading = false;
				}
			},

			// 游客登录
			async guestLogin() {
				if (this.loading) return;

				this.loading = true;
				try {
					// 模拟游客登录过程
					setTimeout(() => {
						// 设置游客用户信息
						const guestUser = {
							id: Date.now(),
							username: '游客用户',
							roles: [{
								name: 'visitor'
							}],
							isGuest: true
						};

						uni.removeStorageSync(this.$constants.REMEMBER_ME);

						uni.setStorageSync(this.$constants.TOKEN, 'guest_token_' + Date.now());
						uni.setStorageSync(this.$constants.USER_INFO, guestUser);

						// 显示成功动画
						this.showSuccessAnimation = true;

						// 跳转到首页
						setTimeout(() => {
							uni.reLaunch({
								url: this.$PATHS.INDEX
							});
						}, 1800);
					}, 1000);
				} catch (error) {
					console.error('游客登录失败:', error);
					uni.showToast({
						title: '游客登录失败，请重试',
						icon: 'none'
					});
				} finally {
					this.loading = false;
				}
			},

			// 注册
			registerBtn() {
				uni.showToast({
					title: '暂不开放此功能',
					icon: 'none'
				});
			},

			// 忘记密码
			forgotPassword() {
				uni.showToast({
					title: '请联系管理员重置密码',
					icon: 'none'
				});
			},

			// 更新验证码
			async updateVerifyCode() {
				this.captchaImageUrl = ''
				const timestamp = new Date().getTime()
				const result = await userApi.getCaptcha(`?refresh=true&t=${timestamp}`)

				if (result.status === 200 && result.data) {
					this.captchaImageUrl = result.data.imageUrl
					this.captchaToken = result.data.captchaKey

					// 存储 captchaToken 到本地，用于登录时提交
					if (this.captchaToken) {
						uni.setStorageSync(this.$constants.CURRENT_CAPTCHA_KEY, this.captchaToken)
					}

				} else {
					throw new Error(result.msg || '获取验证码失败')
				}
			},

			// 记住我状态改变
			toggleRememberMe() {
				this.userForm.rememberMe = !this.userForm.rememberMe;
			},

			// 设置焦点字段
			setFocus(field) {
				this.focusedField = field;
			},

			// 清除焦点
			clearFocus() {
				this.focusedField = '';
			},

			// 抖动空字段
			shakeEmptyFields() {
				const fields = ['username', 'password', 'verify_code'];
				fields.forEach(field => {
					if (!this.userForm[field]) {
						this.focusedField = field;
						setTimeout(() => {
							this.clearFocus();
						}, 600);
					}
				});
			}
		},
		onLoad() {
			this.updateVerifyCode();

			const savedUsername = uni.getStorageSync('savedUsername');
			if (savedUsername) {
				this.userForm.username = savedUsername;
				this.userForm.rememberMe = true;
			}
		}
	}
</script>

<style lang="scss" scoped>
	@import "@/scss/variables.scss";


	.login-container {
		width: 100%;
		min-height: 100vh;
		display: flex;
		flex-direction: column;
		position: relative;
		overflow: hidden;
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

	.main-content {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 40rpx;
		position: relative;
		z-index: 2;
	}

	/* 玻璃态效果卡片 */
	.glass-card {
		background: rgba(255, 255, 255, 0.95);
		backdrop-filter: blur(20rpx);
		border-radius: 24rpx;
		border: 1rpx solid rgba(255, 255, 255, 0.3);
		box-shadow: $shadow-heavy;
		overflow: hidden;
	}

	/* 登录卡片容器 */
	.login-card {
		width: 100%;
		max-width: 1200rpx;
		display: flex;
		flex-direction: column;
		animation: slideInUp 0.8s ease-out;

		/* 平板和桌面端横向布局 */
		@media (min-width: 768px) {
			flex-direction: row;
			min-height: 650rpx;
			/* 稍微降低高度 */
			max-height: 80vh;
			/* 限制最大高度 */
		}
	}

	/* 品牌展示区 */
	.brand-section {
		padding: 60rpx 40rpx;
		background: linear-gradient(135deg, rgba($primary-color, 0.1) 0%, rgba($accent-color, 0.05) 100%);
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		text-align: center;
		position: relative;

		@media (min-width: 768px) {
			flex: 0.45;
			/* 调整比例 */
			max-width: 450rpx;
			padding: 50rpx 30rpx;

			.logo-container {
				width: 140rpx;
				height: 140rpx;
				margin-bottom: 30rpx;

				.logo-image {
					width: 160rpx;
					height: 160rpx;
				}
			}

			.welcome-text {
				.title {
					font-size: 44rpx;
					margin-bottom: 8rpx;
				}

				.subtitle {
					font-size: 30rpx;
					margin-bottom: 20rpx;
				}
			}

			.brand-slogan {
				margin-top: 20rpx;

				text {
					font-size: 24rpx;
					color: $text-secondary;
					font-style: italic;
				}
			}
		}

		.logo-container {
			width: 160rpx;
			height: 160rpx;
			margin: 0 auto 40rpx;
			background: rgba(255, 255, 255, 0.2);
			border-radius: 50%;
			display: flex;
			justify-content: center;
			align-items: center;
			position: relative;
			border: 1rpx solid rgba($primary-color, 0.2);

			.avatar-glow {
				position: absolute;
				width: 100%;
				height: 100%;
				border-radius: 50%;
				background: radial-gradient(circle, rgba($primary-color, 0.3) 0%, transparent 70%);
				animation: glow 2s ease-in-out infinite alternate;
			}

			.logo-image {
				width: 150rpx;
				height: 150rpx;
				border-radius: 50%;
				position: relative;
				z-index: 2;
			}
		}

		.welcome-text {
			margin-bottom: 0rpx;

			.title {
				display: block;
				font-size: 48rpx;
				font-weight: bold;
				color: $text-primary;
				margin-bottom: 0rpx;
			}

			.subtitle {
				display: block;
				font-size: 32rpx;
				color: $text-secondary;
				margin-bottom: 0rpx;
			}

			.feature-list {
				display: grid;
				grid-template-columns: 1fr;
				gap: 20rpx;

				@media (min-width: 768px) {
					grid-template-columns: 1fr;
				}

				.feature-item {
					display: flex;
					align-items: center;
					justify-content: flex-start;
					padding: 20rpx;
					background: rgba(255, 255, 255, 0.6);
					border-radius: 12rpx;
					border: 1rpx solid rgba($primary-color, 0.1);
					transition: all 0.3s ease;

					&:active {
						transform: translateY(-2rpx);
						box-shadow: $shadow-light;
						border-color: rgba($primary-color, 0.3);
					}

					.feature-icon-wrapper {
						width: 50rpx;
						height: 50rpx;
						border-radius: 50%;
						background: rgba($primary-color, 0.1);
						display: flex;
						align-items: center;
						justify-content: center;
						margin-right: 15rpx;
					}

					.feature-icon {
						font-size: 32rpx;
						color: $primary-color;
					}

					.feature-text {
						font-size: 26rpx;
						color: $text-primary;
						font-weight: 500;
					}
				}
			}
		}

		.brand-footer {
			margin-top: auto;
			padding-top: 30rpx;
			border-top: 1rpx solid rgba($primary-color, 0.1);

			.brand-slogan {
				font-size: 24rpx;
				color: $text-secondary;
				font-style: italic;
			}
		}
	}

	/* 表单区域 */
	.form-section {
		padding: 20rpx 40rpx;
		flex: 1;
		display: flex;
		flex-direction: column;
		justify-content: center;

		@media (min-width: 768px) {
			flex: 0.55;
			padding: 40rpx 50rpx;

			.form-header {
				margin-bottom: 50rpx;

				.header-icon {
					font-size: 48rpx;
					margin-bottom: 15rpx;
				}

				.header-title {
					font-size: 38rpx;
				}

				.header-subtitle {
					font-size: 26rpx;
				}
			}
		}

		.form-header {
			text-align: center;
			margin-bottom: 60rpx;

			.header-title {
				display: block;
				font-size: 40rpx;
				font-weight: bold;
				color: $text-primary;
				margin-bottom: 15rpx;
			}

			.header-subtitle {
				display: block;
				font-size: 28rpx;
				color: $text-secondary;
			}
		}

		.input-group {
			position: relative;
			margin-bottom: 35rpx;
			transition: all 0.3s ease;

			&.input-focused {
				transform: translateY(-5rpx);

				.input-underline {
					width: 100%;
					background: $primary-color;
					box-shadow: 0 0 10rpx rgba($primary-color, 0.3);
				}
			}

			&:not(.input-focused) {
				.input-underline {
					width: 0;
				}
			}

			.input-icon {
				position: absolute;
				left: 25rpx;
				top: 50%;
				transform: translateY(-50%);
				font-size: 32rpx;
				z-index: 2;
				transition: all 0.3s ease;
			}

			.input-underline {
				position: absolute;
				bottom: 0;
				left: 0;
				height: 2rpx;
				background: $primary-color;
				transition: all 0.3s ease;
				border-radius: 2rpx;
			}

			.custom-input,
			.captcha-input {
				width: 100%;
				height: 85rpx;
				padding: 0 70rpx 0 70rpx;
				border-radius: 12rpx;
				border: 1rpx solid rgba($primary-color, 0.2);
				background: rgba(255, 255, 255, 0.8);
				color: $text-primary;
				font-size: 26rpx;
				box-sizing: border-box;
				transition: all 0.3s ease;

				&:focus {
					border-color: rgba($primary-color, 0.6);
					box-shadow: 0 0 10rpx rgba($primary-color, 0.1);
				}
			}

			.captcha-row {
				display: flex;
				gap: 20rpx;
				position: relative;

				.captcha-input {
					flex: 1;
				}

				.captcha-wrapper {
					width: 180rpx;
					height: 85rpx;
					position: relative;
					border-radius: 12rpx;
					border: 1rpx solid rgba($primary-color, 0.2);
					overflow: hidden;
					transition: all 0.3s ease;
					background: rgba(255, 255, 255, 0.8);

					&:active {
						transform: scale(0.98);
					}

					.captcha-image {
						width: 100%;
						height: 100%;
					}

					.captcha-overlay {
						position: absolute;
						bottom: 0;
						left: 0;
						right: 0;
						background: rgba(0, 0, 0, 0.7);
						color: white;
						font-size: 18rpx;
						text-align: center;
						padding: 6rpx 0;
						transition: all 0.3s ease;
					}

					&:hover .captcha-overlay {
						background: rgba($primary-color, 0.7);
					}
				}
			}
		}

		.remember-forgot {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 50rpx;

			.remember-me {
				display: flex;
				align-items: center;

				.checkbox-container {
					width: 36rpx;
					height: 36rpx;
					border-radius: 6rpx;
					border: 2rpx solid rgba($primary-color, 0.5);
					display: flex;
					align-items: center;
					justify-content: center;
					margin-right: 15rpx;
					transition: all 0.3s ease;

					&.checkbox-checked {
						background: $primary-color;
						border-color: $primary-color;

						.checkmark {
							color: white;
							font-size: 24rpx;
							font-weight: bold;
						}
					}
				}

				.remember-text {
					color: $text-secondary;
					font-size: 26rpx;
				}
			}

			.forgot-link {
				color: $primary-color;
				font-size: 26rpx;
				transition: all 0.3s ease;

				&:active {
					opacity: 0.7;
				}
			}
		}

		.login-btn {
			width: 100%;
			height: 85rpx;
			border-radius: 12rpx;
			border: none;
			margin-bottom: 25rpx;
			position: relative;
			overflow: hidden;
			transition: all 0.3s ease;
			font-size: 32rpx;
			font-weight: bold;

			&:active:not([disabled]) {
				transform: scale(0.98);
			}

			&.btn-loading {
				opacity: 0.8;
			}

			&.primary {
				background: linear-gradient(135deg, $primary-color, $accent-color);
				color: white;
			}

			&.guest {
				background: transparent;
				border: 2rpx solid $primary-color;
				color: $primary-color;
			}

			.btn-content {
				position: relative;
				z-index: 2;
			}

			.btn-text {
				font-size: 30rpx;
				font-weight: bold;
			}

			.btn-loading-indicator {
				display: flex;
				justify-content: center;
				align-items: center;
				position: absolute;
				top: 0;
				left: 0;
				right: 0;
				bottom: 0;

				.loading-dot {
					width: 12rpx;
					height: 12rpx;
					border-radius: 50%;
					background: white;
					margin: 0 4rpx;
					animation: loadingBounce 1.4s infinite ease-in-out both;

					&.dot-1 {
						animation-delay: -0.32s;
					}

					&.dot-2 {
						animation-delay: -0.16s;
					}
				}
			}

			&[disabled] {
				opacity: 0.6;
			}
		}

		.register-section {
			text-align: center;
			margin-top: 30rpx;

			.register-text {
				color: $text-secondary;
				font-size: 26rpx;
				margin-right: 15rpx;
			}

			.register-btn {
				color: $primary-color;
				font-size: 26rpx;
				font-weight: bold;
				transition: all 0.3s ease;

				&:active {
					opacity: 0.7;
				}
			}
		}
	}

	.footer {
		padding: 20rpx 40rpx;
		text-align: center;
		z-index: 2;
		position: relative;

		.footer-text {
			color: rgba(255, 255, 255, 0.8);
			font-size: 22rpx;
		}
	}

	// 占位符样式
	.placeholder-style {
		color: $text-light;
	}

	.success-animation {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 9999;
	}

	.animation-content {
		padding: 60rpx 40rpx;
		text-align: center;
		animation: scaleIn 0.5s ease-out;
		box-shadow: $shadow-heavy;
	}

	.success-icon {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		background: $success-color;
		margin: 0 auto 30rpx;
		position: relative;
		animation: checkmarkScale 0.5s ease-out;

		.checkmark-circle {
			position: absolute;
			width: 80rpx;
			height: 80rpx;
			border-radius: 50%;
			border: 4rpx solid $success-color;
			animation: checkmarkCircle 0.6s ease-out;
		}

		.checkmark-stem {
			position: absolute;
			width: 8rpx;
			height: 40rpx;
			background-color: white;
			left: 38rpx;
			top: 18rpx;
			transform: rotate(45deg);
			animation: checkmarkStem 0.3s ease-out 0.7s both;
		}

		.checkmark-kick {
			position: absolute;
			width: 8rpx;
			height: 20rpx;
			background-color: white;
			left: 22rpx;
			top: 38rpx;
			transform: rotate(-45deg);
			animation: checkmarkKick 0.3s ease-out 0.9s both;
		}
	}

	.success-text {
		display: block;
		font-size: 36rpx;
		font-weight: bold;
		color: $text-primary;
		margin-bottom: 16rpx;
	}

	.redirect-text {
		display: block;
		font-size: 28rpx;
		color: $text-secondary;
		margin-bottom: 30rpx;
	}

	.loading-bar {
		width: 100%;
		height: 6rpx;
		background: #eee;
		border-radius: 3rpx;
		overflow: hidden;

		.loading-progress {
			height: 100%;
			background: $success-color;
			border-radius: 3rpx;
			animation: loadingProgress 1.5s ease-in-out;
		}
	}

	.header-icon {
		font-size: 40rpx;
		margin-bottom: 10rpx;
	}

	.brand-slogan {
		margin-top: 15rpx;
		text-align: center;

		text {
			font-size: 22rpx;
			color: $text-secondary;
			font-style: italic;
			opacity: 0.8;
		}
	}

	/* 移动端优化 */
	@media (max-width: 767px) {
		.main-content {
			padding: 40rpx;
		}

		.brand-section {
			padding: 40rpx 30rpx 30rpx;

			.logo-container {
				width: 140rpx;
				height: 140rpx;

				.logo-image {
					width: 160rpx;
					height: 160rpx;
				}
			}

			.brand-slogan {
				margin-top: 15rpx;
			}
		}

		.form-section {
			padding: 30rpx;
		}
	}

	// 动画定义
	@keyframes slideInUp {
		0% {
			opacity: 0;
			transform: translateY(50rpx);
		}

		100% {
			opacity: 1;
			transform: translateY(0);
		}
	}

	@keyframes glow {
		0% {
			opacity: 0.5;
			transform: scale(1);
		}

		100% {
			opacity: 0.8;
			transform: scale(1.05);
		}
	}

	@keyframes loadingBounce {

		0%,
		80%,
		100% {
			transform: scale(0);
		}

		40% {
			transform: scale(1);
		}
	}

	@keyframes scaleIn {
		0% {
			transform: scale(0.8);
			opacity: 0;
		}

		100% {
			transform: scale(1);
			opacity: 1;
		}
	}

	@keyframes checkmarkCircle {
		0% {
			stroke-dashoffset: 240;
		}

		100% {
			stroke-dashoffset: 480;
		}
	}

	@keyframes checkmarkScale {

		0%,
		100% {
			transform: scale(1);
		}

		50% {
			transform: scale(1.1);
		}
	}

	@keyframes checkmarkStem {
		0% {
			height: 0;
		}

		100% {
			height: 40rpx;
		}
	}

	@keyframes checkmarkKick {
		0% {
			height: 0;
		}

		100% {
			height: 20rpx;
		}
	}

	@keyframes loadingProgress {
		0% {
			width: 0;
		}

		100% {
			width: 100%;
		}
	}
</style>