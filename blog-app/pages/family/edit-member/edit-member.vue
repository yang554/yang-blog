<template>
	<view class="edit-member-container">
		<!-- 头部 -->
		<!-- <view class="header">
			<view class="header-back" @tap="goBack">
				<text class="back-icon">←</text>
				<text class="back-text">返回</text>
			</view>
			<text class="header-title">编辑成员信息</text>
			<view class="header-placeholder"></view>
		</view> -->

		<view class="content">
			<scroll-view class="form-scroll" scroll-y>
				<!-- 基本信息 -->
				<view class="form-section">
					<view class="section-title">基本信息</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">姓名 <text class="required">*</text></text>
							<input class="form-input" v-model="formData.userName" placeholder="请输入姓名" />
						</view>
					</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">性别 <text class="required">*</text></text>
							<view class="radio-group">
								<label class="radio-item" @tap="formData.userSex = '男'">
									<view class="radio-icon">
										<view class="radio-dot" :class="{ active: formData.userSex === '男' }"></view>
									</view>
									<text class="radio-text">男</text>
								</label>
								<label class="radio-item" @tap="formData.userSex = '女'">
									<view class="radio-icon">
										<view class="radio-dot" :class="{ active: formData.userSex === '女' }"></view>
									</view>
									<text class="radio-text">女</text>
								</label>
							</view>
						</view>
					</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">代数 <text class="required">*</text></text>
							<picker class="form-input" :value="loveIndex" :range="loveOptions" @change="onLoveChange">
								<view>{{ formData.userLove ? `第${formData.userLove}代` : '请选择代数' }}</view>
							</picker>
						</view>
					</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">出生日期</text>
							<picker mode="date" :value="formData.userBirthDay" @change="onBirthdayChange">
								<view class="form-input">{{ formData.userBirthDay || '请选择出生日期' }}</view>
							</picker>
						</view>
					</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">是否在世</text>
							<view class="radio-group">
								<label class="radio-item" @tap="setSurvivalStatus('是')">
									<view class="radio-icon">
										<view class="radio-dot" :class="{ active: formData.isSurvival === '是' }"></view>
									</view>
									<text class="radio-text">在世</text>
								</label>
								<label class="radio-item" @tap="setSurvivalStatus('否')">
									<view class="radio-icon">
										<view class="radio-dot" :class="{ active: formData.isSurvival === '否' }"></view>
									</view>
									<text class="radio-text">已故</text>
								</label>
							</view>
						</view>
					</view>

					<view class="form-row" v-if="formData.isSurvival === '否'">
						<view class="form-item">
							<text class="form-label">死亡日期</text>
							<picker mode="date" :value="formData.userDieDay" @change="onDieDayChange">
								<view class="form-input">{{ formData.userDieDay || '请选择死亡日期' }}</view>
							</picker>
						</view>
					</view>
				</view>

				<!-- 家庭成员关系 -->
				<view class="form-section">
					<view class="section-title">家庭成员关系</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">父亲姓名</text>
							<input class="form-input" disabled="true" v-model="formData.pName" placeholder="请输入父亲姓名" />
						</view>
					</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">母亲姓名</text>
							<input class="form-input" disabled="true" v-model="formData.mName" placeholder="请输入母亲姓名" />
						</view>
					</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">配偶姓名</text>
							<text class="form-input" disabled="true"
								v-for="item in formData.mateName">{{ item.userName }}</text>
						</view>
					</view>
				</view>

				<!-- 联系信息 -->
				<view class="form-section">
					<view class="section-title">联系信息</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">手机号码</text>
							<input class="form-input" v-model="formData.userPhone" placeholder="请输入手机号码"
								type="number" />
						</view>
					</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">邮箱</text>
							<input class="form-input" v-model="formData.userEmil" placeholder="请输入邮箱地址" />
						</view>
					</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">户籍地址</text>
							<AddressPicker v-model="formData.userAddressOldCode" @change="onAddressOldChange" />
						</view>
					</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">现居地址</text>
							<AddressPicker v-model="formData.userAddressNewCode" @change="onAddressNewChange" />
						</view>
					</view>
				</view>

				<!-- 其他信息 -->
				<view class="form-section">
					<view class="section-title">其他信息</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">备注</text>
							<textarea class="form-textarea" v-model="formData.userNote" placeholder="请输入备注信息"
								maxlength="200" />
							<text class="textarea-count">{{ formData.userNote.length }}/200</text>
						</view>
					</view>

					<view class="form-row">
						<view class="form-item">
							<text class="form-label">姓名拼音</text>
							<input class="form-input" disabled="true" v-model="formData.userPinYin"
								placeholder="请输入姓名拼音" />
						</view>
					</view>
				</view>

				<!-- 操作按钮 -->
				<view class="action-buttons">
					<button class="btn btn-cancel" @tap="goBack">取消</button>
					<button class="btn btn-save" @tap="handleSave" :disabled="!canSave">保存修改</button>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script>
	import {
		familyApi
	} from "@/api/index.js";
	import AddressPicker from '@/components/address-picker/address-picker.vue'
	export default {
		components: {
			AddressPicker
		},
		data() {
			return {
				memberData: null,
				loveIndex: -1,
				loveOptions: ['第一代', '第二代', '第三代', '第四代', '第五代', '第六代', '第七代', '第八代', '第九代', '第十代'],

				// 表单数据
				formData: {
					userId: '', // id
					userName: '', // 姓名
					isSurvival: '是', // 是否存活
					mName: '', // 母亲姓名
					pName: '', // 父亲姓名
					mateName: '', // 配偶名称
					userAddressNew: '', // 现地址
					userAddressOld: '', // 户口地址
					userBirthDay: '', // 生日
					userDieDay: '', // 死亡日期
					userEmil: '', // 邮箱
					userImgUrl: '', // 头像
					userLove: '', // 代数
					userMID: '', // 母亲ID
					userMateId: '', // 配偶ID
					userNote: '', // 备注
					userPId: '', // 父亲ID
					userPhone: '', // 电话
					userPinYin: '', // 拼音
					userSex: '', // 性别
					age: '', // 年龄
					dieDayNum: '', // 亡故日期
					
					userAddressNewCode: '',
					userAddressNewDetail: '',
					userAddressNewProvince: '',
					userAddressNewCity: '',
					userAddressNewArea: '',
					userAddressOldCode: '',
					userAddressOldDetail: '',
					userAddressOldProvince: '',
					userAddressOldCity: '',
					userAddressOldArea: '',
				}
			};
		},

		computed: {
			// 是否可以保存
			canSave() {
				// 基本必填字段验证
				if (!this.formData.userName.trim()) return false;
				if (!this.formData.userSex) return false;
				if (!this.formData.userLove) return false;

				// 如果选择已故，需要死亡日期
				if (this.formData.isSurvival === '否' && !this.formData.userDieDay) return false;

				return true;
			}
		},

		onLoad(options) {
			if (options.node) {
				// 如果传递了节点数据
				try {
					const nodeData = JSON.parse(decodeURIComponent(options.node));
					this.memberData = nodeData;
					this.initFormData();
				} catch (error) {
					console.error('解析成员数据失败:', error);
					uni.showToast({
						title: '数据解析失败',
						icon: 'none'
					});
				}
			}
		},

		methods: {
			// 返回上一页
			goBack() {
				uni.navigateBack();
			},
			//户籍地
			onAddressOldChange(result,addressText,code) {
				if(addressText) this.formData.userAddressOld = addressText
				if(code) this.formData.userAddressOldCode = code
				
			},
			//现居地
			onAddressNewChange(result,addressText,code) {
				if(addressText) this.formData.userAddressNew = addressText
				if(code) this.formData.userAddressNewCode  = code
			},
			// 初始化表单数据
			initFormData() {
				if (!this.memberData) return;

				const rawData = this.memberData.rawData || {};

				// 映射数据到表单
				this.formData = {
					userId: rawData.userId || this.memberData.id,
					userName: rawData.userName || this.memberData.name,
					isSurvival: rawData.isSurvival === '1' ? '是' : '否',
					mName: rawData.mName || this.memberData.mName,
					pName: rawData.pName || this.memberData.pName,
					mateName: this.memberData.mateNames || '',
					userAddressNew: rawData.userAddressNew || '',
					userAddressOld: rawData.userAddressOld || '',
					userBirthDay: rawData.userBirthDay || this.memberData.birthDate,
					userDieDay: rawData.userDieDay || '',
					userEmil: rawData.userEmil || '',
					userImgUrl: rawData.userImgUrl || '',
					userLove: rawData.userLove || this.memberData.love,
					userMID: rawData.userMID || '',
					userMateId: rawData.userMateId || '',
					userNote: rawData.userNote || '',
					userPId: rawData.userPId || '',
					userPhone: rawData.userPhone || '',
					userPinYin: rawData.userPinYin || '',
					userSex: rawData.userSex || this.memberData.sex,
					age: this.memberData.age ? this.memberData.age.toString() : '',
					dieDayNum: rawData.dieDayNum || rawData.userDieDay || '',
					
					userAddressNewCode: rawData.userAddressNewCode || '',
					userAddressOldCode: rawData.userAddressOldCode || '',
				};

				// 设置代数选择器索引
				if (this.formData.userLove) {
					this.loveIndex = parseInt(this.formData.userLove) - 1;
				}
			},

			// 设置生存状态
			setSurvivalStatus(status) {
				this.formData.isSurvival = status;
				if (status === '是') {
					this.formData.userDieDay = '';
					this.formData.dieDayNum = '';
				}
			},

			// 代数选择
			onLoveChange(e) {
				const index = e.detail.value;
				this.loveIndex = index;
				this.formData.userLove = (parseInt(index) + 1).toString();
			},

			// 日期选择事件
			onBirthdayChange(e) {
				this.formData.userBirthDay = e.detail.value;
				// 自动计算年龄
				if (this.formData.userBirthDay) {
					const birth = new Date(this.formData.userBirthDay);
					const now = new Date();
					let age = now.getFullYear() - birth.getFullYear();
					const monthDiff = now.getMonth() - birth.getMonth();
					if (monthDiff < 0 || (monthDiff === 0 && now.getDate() < birth.getDate())) {
						age--;
					}
					this.formData.age = age.toString();
				}
			},

			onDieDayChange(e) {
				this.formData.userDieDay = e.detail.value;
				this.formData.dieDayNum = e.detail.value;
			},

			// 自动生成拼音
			generatePinyin() {
				if (this.formData.userName && !this.formData.userPinYin) {
					// 这里可以集成拼音转换库，暂时用简单实现
					this.formData.userPinYin = this.formData.userName;
				}
			},

			// 保存修改
			async handleSave() {
				if (!this.canSave) {
					uni.showToast({
						title: '请填写完整信息',
						icon: 'none'
					});
					return;
				}

				// 自动生成拼音
				this.generatePinyin();

				uni.showLoading({
					title: '保存中...'
				});

				try {
					// 准备提交数据
					const submitData = {
						...this.formData,
						// 转换是否在世为数字格式
						isSurvival: this.formData.isSurvival === '是' ? '1' : '0'
					};

					// 调用API更新成员信息
					const res = await familyApi._saveSource(submitData);
					if (res.status === 200) {
						uni.hideLoading();

						uni.showToast({
							title: '保存成功',
							icon: 'success',
							duration: 1500
						});

						// 发送全局事件通知数据更新
						uni.$emit('memberDataUpdated', {
							data: this.formData,
							timestamp: new Date().getTime()
						});

						// 延迟返回让用户看到成功提示
						setTimeout(() => {
							uni.navigateBack();
						}, 1500);
					}





				} catch (error) {
					uni.hideLoading();
					console.error('保存失败:', error);
					uni.showToast({
						title: '保存失败: ' + (error.message || '未知错误'),
						icon: 'none',
						duration: 3000
					});
				}
			}
		},

		watch: {
			'formData.userName': function(newVal) {
				if (newVal && !this.formData.userPinYin) {
					this.generatePinyin();
				}
			}
		}
	};
</script>

<style scoped>
	.edit-member-container {
		min-height: 100vh;
		background-color: #f5f5f5;
	}

	/* 头部样式 */
	.header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 30rpx;
		background: white;
		border-bottom: 1rpx solid #f0f0f0;
		position: sticky;
		top: 0;
		z-index: 10;
	}

	.header-back {
		display: flex;
		align-items: center;
		color: #333;
	}

	.back-icon {
		font-size: 36rpx;
		margin-right: 10rpx;
	}

	.back-text {
		font-size: 28rpx;
	}

	.header-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.header-placeholder {
		width: 120rpx;
	}

	/* 内容区域 */
	.content {
		flex: 1;
	}

	.form-scroll {
		height: calc(100vh - 120rpx);
		padding: 30rpx 0rpx 30rpx 0rpx;
	}

	/* 表单区域 */
	.form-section {
		background: white;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.form-row {
		margin-bottom: 30rpx;
	}

	.form-row:last-child {
		margin-bottom: 0;
	}

	.form-label {
		display: block;
		font-size: 28rpx;
		color: #333;
		margin-bottom: 15rpx;
		font-weight: 500;
	}

	.required {
		color: #ff4757;
		margin-left: 5rpx;
	}

	.form-input {
		height: 80rpx;
		background: #f8f9fa;
		border-radius: 8rpx;
		padding: 0 20rpx;
		font-size: 28rpx;
		color: #333;
		border: 1rpx solid #e8e8e8;
		display: flex;
		align-items: center;
	}

	.form-textarea {
		height: 160rpx;
		background: #f8f9fa;
		border-radius: 8rpx;
		padding: 20rpx;
		font-size: 28rpx;
		color: #333;
		border: 1rpx solid #e8e8e8;
	}

	.textarea-count {
		display: block;
		text-align: right;
		font-size: 24rpx;
		color: #999;
		margin-top: 10rpx;
	}

	/* 单选按钮组 */
	.radio-group {
		display: flex;
		gap: 40rpx;
	}

	.radio-item {
		display: flex;
		align-items: center;
	}

	.radio-icon {
		width: 36rpx;
		height: 36rpx;
		border: 2rpx solid #ddd;
		border-radius: 50%;
		margin-right: 15rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.radio-dot {
		width: 20rpx;
		height: 20rpx;
		border-radius: 50%;
		background: transparent;
		transition: all 0.3s ease;
	}

	.radio-dot.active {
		background: #007AFF;
	}

	.radio-text {
		font-size: 28rpx;
		color: #333;
	}

	/* 操作按钮 */
	.action-buttons {
		display: flex;
		gap: 20rpx;
		margin: 50rpx 150rpx;
	}

	.btn {
		flex: 1;
		height: 90rpx;
		border-radius: 45rpx;
		font-size: 32rpx;
		font-weight: bold;
		border: none;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.btn-cancel {
		background: #f8f9fa;
		color: #666;
		border: 2rpx solid #e8e8e8;
	}

	.btn-save {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		color: white;
	}

	.btn-save:disabled {
		background: #ccc;
		color: #999;
	}
</style>