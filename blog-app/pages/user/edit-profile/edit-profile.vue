<template>
	<view class="edit-profile-container">
		<!-- 页面内容 -->
		<view class="content-wrapper">
			<scroll-view class="main-content" scroll-y :show-scrollbar="false" :style="{ height: '100%' }"
				@scrolltolower="onScrollLower">
				<!-- 头像编辑 -->
				<view class="section-card">
					<view class="section-header">
						<text class="section-title">头像</text>
					</view>
					<view class="avatar-edit">
						<view class="avatar-preview" @click="changeAvatar">
							<image class="avatar-image" :src="getFullAvatarUrl(formData.avatar)" mode="aspectFill">
							</image>
							<view class="avatar-overlay">
								<text class="icon-camera">📷</text>
							</view>
						</view>
						<text class="avatar-tip">点击修改头像</text>
					</view>
				</view>

				<!-- 基本信息 -->
				<view class="section-card">
					<view class="section-header">
						<text class="section-title">基本信息</text>
					</view>
					<view class="form-list">
						<view class="form-item">
							<text class="form-label">用户ID</text>
							<text class="form-value readonly">{{ formData.id }}</text>
						</view>
						<view class="form-item">
							<text class="form-label">账号</text>
							<text class="form-value readonly">{{ formData.username }}</text>
						</view>
						<view class="form-item">
							<text class="form-label">昵称</text>
							<input class="form-input" v-model="formData.nickname" placeholder="请输入昵称" maxlength="20" />
						</view>
						<view class="form-item">
							<text class="form-label">职业</text>
							<input class="form-input" v-model="formData.ext01" placeholder="请输入职业" maxlength="20" />
						</view>
						<view class="form-item">
							<text class="form-label">身份证号</text>
							<input class="form-input" v-model="formData.ext02" placeholder="请输入身份证号" maxlength="20" />
						</view>
					</view>
				</view>

				<!-- 联系信息 -->
				<view class="section-card">
					<view class="section-header">
						<text class="section-title">联系信息</text>
					</view>
					<view class="form-list">
						<view class="form-item">
							<text class="form-label">电子邮箱</text>
							<input class="form-input" v-model="formData.email" placeholder="请输入电子邮箱" type="email" />
						</view>
						<view class="form-item">
							<text class="form-label">联系电话</text>
							<input class="form-input" v-model="formData.phone" placeholder="请输入联系电话" type="number" />
						</view>
						<view class="form-item">
							<text class="form-label">地址</text>
							<input class="form-input" v-model="formData.address" placeholder="请输入地址" />
						</view>
					</view>
				</view>

				<!-- 备注信息 -->
				<view class="section-card">
					<view class="section-header">
						<text class="section-title">备注信息</text>
					</view>
					<view class="form-list">
						<view class="form-item">
							<textarea class="form-textarea" v-model="formData.description" placeholder="请输入个人备注"
								maxlength="200" :show-confirm-bar="false" auto-height />
							<text class="word-count">{{ formData.description.length }}/200</text>
						</view>
					</view>
				</view>

				<!-- 系统信息 -->
				<view class="section-card">
					<view class="section-header">
						<text class="section-title">系统信息</text>
					</view>
					<view class="form-list">
						<view class="form-item">
							<text class="form-label">创建时间</text>
							<text class="form-value readonly">{{ formatDate(formData.createTime) }}</text>
						</view>
						<view class="form-item">
							<text class="form-label">修改时间</text>
							<text class="form-value readonly">{{ formatDate(formData.updateTime) }}</text>
						</view>
					</view>
				</view>

				<!-- 操作按钮 -->
				<view class="action-buttons">
					<button class="btn btn-cancel" @click="cancelEdit">取消</button>
					<button class="btn btn-save" @click="saveProfile" :disabled="saving">
						<text v-if="saving">保存中...</text>
						<text v-else>保存修改</text>
					</button>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script>
	import {
		userApi
	} from "@/api/index.js"
	import {
		IMG_URL,
		BASE_URL
	} from '@/api/config.js'
	export default {
		name: "edit-profile",
		data() {
			return {
				statusBarHeight: 0,
				saving: false,
				formData: {
					id: "10001",
					username: "fengxuanyuening",
					nickname: "枫萱悦宁",
					avatar: "/static/avatar/default-avatar.jpg",
					email: "user@example.com",
					phone: "13800138000",
					address: "北京市朝阳区",
					description: "生活就像一杯茶，不会苦一辈子，但总会苦一阵子。",
					note: "这是一个示例用户的备注信息",
					createTime: "2023-01-15 10:30:00",
					updateTime: "2024-01-20 14:25:36",
					ext01: "",
					ext02: ""
				},
			};
		},
		onLoad() {
			this.getStatusBarHeight();
			this.loadUserData();
		},
		methods: {
			getStatusBarHeight() {
				const systemInfo = uni.getSystemInfoSync();
				this.statusBarHeight = systemInfo.statusBarHeight;
			},

			loadUserData() {
				this.formData = uni.getStorageSync(this.$constants.USER_INFO) || {};
			},

			formatDate(dateString) {
				if (!dateString) return '-';
				const date = new Date(dateString);
				return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
			},

			// 修改头像
			changeAvatar() {
				uni.showActionSheet({
					itemList: ['拍照', '从相册选择'],
					success: (res) => {
						if (res.tapIndex === 0) {
							this.takePhoto();
						} else if (res.tapIndex === 1) {
							this.chooseImage();
						}
					}
				});
			},

			takePhoto() {
				uni.chooseImage({
					count: 1,
					sourceType: ['camera'],
					success: (res) => {
						this.uploadAvatar(res.tempFilePaths[0]);
					}
				});
			},

			chooseImage() {
				uni.chooseImage({
					count: 1,
					sourceType: ['album'],
					success: (res) => {
						this.uploadAvatar(res.tempFilePaths[0]);
					}
				});
			},

			uploadAvatar(tempFilePath) {
				// 显示上传进度
				uni.showLoading({
					title: '上传中...'
				});
				const uploadUrl = BASE_URL + '/file/upload/avatar';
				// 上传图片
				uni.uploadFile({
					url: uploadUrl,
					filePath: tempFilePath,
					name: 'file',
					formData: {
						id: this.formData.id,
						type: 'userAvatar'
					},
					header: {
						'Authorization': 'Bearer ' + uni.getStorageSync('token')
					},
					success: (uploadRes) => {
						console.log(uploadRes)
						const data = JSON.parse(uploadRes.data)
						if (data.status === 200) {
							uni.showToast({
								title: '上传成功',
								icon: 'success'
							})
							this.formData.avatar = data.data;
						} else {
							uni.showToast({
								title: data.message || '上传失败',
								icon: 'none'
							})
						}
					},
					fail: (err) => {
						console.log('上传失败', err)
						uni.showToast({
							title: '上传失败',
							icon: 'none'
						})
					}
				})
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
			// 保存资料
			async saveProfile() {
				// 表单验证
				console.log(!this.validateForm())
				if (!this.validateForm()) {
					return;
				}

				this.saving = true;

				try {
					const res = await userApi.editUser(this.formData);
					if (res.status === 200) {
						uni.setStorageSync(this.$constants.USER_INFO, this.formData); //重新储存用户信息
						uni.showToast({
							title: '保存成功',
							icon: 'success'
						});
						// 返回上一页
						setTimeout(() => {
							uni.navigateBack();
						}, 1500);
					}

				} catch (error) {
					uni.showToast({
						title: '保存失败',
						icon: 'error'
					});
				} finally {
					this.saving = false;
				}
			},

			// 表单验证
			validateForm() {
				if (!this.formData.nickname.trim()) {
					uni.showToast({
						title: '请输入昵称',
						icon: 'none'
					});
					return false;
				}

				if (this.formData.email && !this.validateEmail(this.formData.email)) {
					uni.showToast({
						title: '邮箱格式不正确',
						icon: 'none'
					});
					return false;
				}

				if (this.formData.phone && !this.validatePhone(this.formData.phone)) {
					uni.showToast({
						title: '手机号格式不正确',
						icon: 'none'
					});
					return false;
				}

				return true;
			},

			validateEmail(email) {
				const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
				return regex.test(email);
			},

			validatePhone(phone) {
				const regex = /^1[3-9]\d{9}$/;
				return regex.test(phone);
			},

			// 模拟更新用户资料
			updateUserProfile() {
				return new Promise((resolve) => {
					setTimeout(() => {
						// 更新修改时间
						this.formData.updateTime = new Date().toISOString().replace('T', ' ').substring(0,
							19);
						resolve();
					}, 1000);
				});
			},

			// 取消编辑
			cancelEdit() {
				uni.showModal({
					title: '提示',
					content: '您有未保存的修改，确定要离开吗？',
					success: (res) => {
						if (res.confirm) {
							uni.navigateBack();
						}
					}
				});
			},

			onScrollLower() {
				// 滚动到底部
			}
		},

		onBackPress() {
			// 拦截返回键，检查是否有未保存的修改

			return false;
		}
	};
</script>

<style lang="scss" scoped>
	.edit-profile-container {
		min-height: 100vh;
		background: #f5f7fa;
	}

	.content-wrapper {
		box-sizing: border-box;
		overflow: hidden;
	}

	.main-content {
		box-sizing: border-box;
		overflow: hidden;
		background: transparent;
		padding: 30rpx;
	}

	/* 卡片样式 */
	.section-card {
		background: #ffffff;
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	/* 头像编辑 */
	.avatar-edit {
		display: flex;
		flex-direction: column;
		align-items: center;
	}

	.avatar-preview {
		position: relative;
		width: 160rpx;
		height: 160rpx;
		border-radius: 50%;
		overflow: hidden;
		margin-bottom: 20rpx;
	}

	.avatar-image {
		width: 100%;
		height: 100%;
	}

	.avatar-overlay {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		opacity: 0;
		transition: opacity 0.3s;
	}

	.avatar-preview:active .avatar-overlay {
		opacity: 1;
	}

	.icon-camera {
		font-size: 48rpx;
		color: white;
	}

	.avatar-tip {
		font-size: 24rpx;
		color: #999;
	}

	/* 表单样式 */
	.form-list {
		.form-item {
			display: flex;
			flex-direction: column;
			padding: 30rpx 0;
			border-bottom: 1rpx solid #f5f5f5;

			&:last-child {
				border-bottom: none;
			}
		}

		.form-label {
			font-size: 28rpx;
			color: #333;
			margin-bottom: 20rpx;
			font-weight: 500;
		}

		.form-value {
			font-size: 28rpx;
			color: #666;

			&.readonly {
				color: #999;
			}
		}

		.form-input {
			font-size: 28rpx;
			color: #333;
			padding: 20rpx 0;
			background: transparent;
		}

		.form-textarea {
			font-size: 28rpx;
			color: #333;
			padding: 0rpx;
			background: transparent;
			min-height: 120rpx;
			line-height: 1.6;
		}

		.word-count {
			font-size: 24rpx;
			color: #999;
			text-align: right;
			margin-top: 10rpx;
		}
	}

	/* 操作按钮 */
	.action-buttons {
		display: flex;
		gap: 30rpx;
		margin-top: 40rpx;

		.btn {
			flex: 1;
			height: 88rpx;
			border-radius: 44rpx;
			font-size: 32rpx;
			font-weight: 500;
			border: none;
			transition: all 0.3s;

			&:active {
				transform: scale(0.98);
			}

			&.btn-cancel {
				background: #f8f9fa;
				color: #666;
			}

			&.btn-save {
				background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
				color: white;

				&:disabled {
					background: #ccc;
					color: #999;
				}
			}
		}
	}
</style>