<template>
	<view class="add-member-container">
		<!-- 头部 -->
		<!-- <view class="header">
			<view class="header-back" @tap="goBack">
				<text class="back-icon">←</text>
				<text class="back-text">返回</text>
			</view>
			<text class="header-title">{{ pageTitle }}</text>
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
								<label class="radio-item" @tap="formData.isSurvival = '是'">
									<view class="radio-icon">
										<view class="radio-dot" :class="{ active: formData.isSurvival === '是' }"></view>
									</view>
									<text class="radio-text">在世</text>
								</label>
								<label class="radio-item" @tap="formData.isSurvival = '否'">
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
				<view class="form-section" v-if="actionType === 'child' || actionType === 'sibling' ">
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
					<view class="form-row" v-if="actionType === 'child' && showSpouseSelection">
						<view class="form-item">
							<text class="form-label">选择配偶</text>
							<view class="spouse-selection">
								<view class="selection-tip">请选择一位配偶作为孩子的父母：</view>
								<view class="spouse-list">
									<view class="spouse-option" v-for="spouse in currentMember.spouse" :key="spouse.id"
										:class="{ selected: selectedSpouseId === spouse.id }"
										@tap="selectSpouse(spouse)">
										<image :src="spouse.rawData.userImgUrl || '/static/default-avatar.png'"
											class="spouse-avatar" mode="aspectFill" />
										<view class="spouse-info">
											<text class="spouse-name">{{ spouse.name }}</text>
											<text class="spouse-detail">{{ spouse.age }}岁 • {{ spouse.relation }}</text>
										</view>
										<view class="check-icon" v-if="selectedSpouseId === spouse.id">✓</view>
									</view>
								</view>
							</view>
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
							<text class="form-label">户口地址</text>
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
							<text class="form-label">座右铭</text>
							<textarea class="form-textarea" v-model="formData.userNote" placeholder="请输入备注或者座右铭信息"
								maxlength="200" />
							<text class="textarea-count">{{ formData.userNote.length }}/200</text>
						</view>
					</view>
				</view>

				<!-- 提交按钮 -->
				<view class="submit-section">
					<button class="submit-btn" @tap="handleSubmit" :disabled="!canSubmit">
						{{ submitButtonText }}
					</button>
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
				actionType: '', // parent, child, spouse, sibling
				currentMember: null,
				selectedSpouseId: null,
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
					type: '', // 操作
					pTree: '', //族谱ID
					
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
			// 页面标题
			pageTitle() {
				const titles = {
					'parent': '添加父母',
					'child': '添加子女',
					'spouse': '添加配偶',
					'sibling': '添加兄弟姐妹'
				};
				return titles[this.actionType] || '添加成员';
			},

			// 是否显示配偶选择
			showSpouseSelection() {
				return this.actionType === 'child' &&
					this.currentMember &&
					this.currentMember.spouse &&
					this.currentMember.spouse.length > 0;
			},

			// 是否可以提交
			canSubmit() {
				// 基本必填字段验证
				if (!this.formData.userName.trim()) return false;
				if (!this.formData.userSex) return false;
				if (!this.formData.userLove) return false;

				// 如果选择已故，需要死亡日期
				if (this.formData.isSurvival === '否' && !this.formData.userDieDay) return false;

				return true;
			},

			// 提交按钮文字
			submitButtonText() {
				if (!this.canSubmit) return '请填写完整信息';
				return '确认添加';
			}
		},

		onLoad(options) {
			// 解析参数
			if (options.type) {
				this.actionType = options.type;
				this.formData.type = options.type;
			}

			if (options.member) {
				try {
					this.currentMember = JSON.parse(decodeURIComponent(options.member));
					this.initFormData();
				} catch (error) {
					console.error('解析成员数据失败:', error);
					uni.showToast({
						title: '数据解析失败',
						icon: 'none'
					});
				}
			}

			if (options.gender) {
				this.formData.userSex = options.gender === 'male' ? '男' : '女';
			}

			// 自动选择第一个配偶
			if (this.showSpouseSelection && this.currentMember.spouse.length > 0) {
				this.selectedSpouseId = this.currentMember.spouse[0].id;
			}
		},

		methods: {
			// 返回上一页
			goBack() {
				uni.navigateBack();
			},

			//户籍地
			onAddressOldChange(result, addressText, code) {
				if (addressText) this.formData.userAddressOld = addressText
				if (code) this.formData.userAddressOldCode = code

			},
			//现居地
			onAddressNewChange(result, addressText, code) {
				if (addressText) this.formData.userAddressNew = addressText
				if (code) this.formData.userAddressNewCode = code
			},
			// 初始化表单数据
			initFormData() {
				if (!this.currentMember) return;

				this.formData.userLove = this.currentMember.rawData.userLove;
				this.formData.pTree = this.currentMember.rawData.pTree;
				this.formData.userAddressNewCode = this.currentMember.rawData.userAddressNewCode || '';
				this.formData.userAddressOldCode = this.currentMember.rawData.userAddressOldCode || '';

				// 根据操作类型预填一些字段
				switch (this.actionType) {
					case 'parent':
						// 添加父母时，当前成员就是子女
						this.formData.userPId = this.currentMember.id;
						this.formData.userMID = this.currentMember.id;
						break;

					case 'child':
						// 添加子女时，当前成员就是父母
						if (this.currentMember.sex === '女') {
							this.formData.mName = this.currentMember.name;
							this.formData.userMID = this.currentMember.id;
							this.formData.pName = this.currentMember.spouse[0].name;
							this.formData.userPId = this.currentMember.spouse[0].id;
						} else {
							this.formData.mName = this.currentMember.spouse[0].name;
							this.formData.userMID = this.currentMember.spouse[0].id;
							this.formData.pName = this.currentMember.name;
							this.formData.userPId = this.currentMember.id;
						}

						this.formData.type = 'addChildren';
						break;

					case 'spouse':
						this.formData.pName = this.currentMember.pName;
						this.formData.mName = this.currentMember.mName;
						this.formData.userSex = this.currentMember.gender === 'male' ? '女' : '男';
						this.formData.type = 'addMate';
						this.formData.userId = this.currentMember.id;
						break;

					case 'sibling':
						// 添加兄弟姐妹时，继承相同的父母
						this.formData.pName = this.currentMember.pName;
						this.formData.userPId = this.currentMember.rawData.userPId;
						this.formData.mName = this.currentMember.mName;
						this.formData.userMID = this.currentMember.rawData.userMID;
						this.formData.type = 'addChildren';
						break;
				}
			},

			// 选择配偶
			selectSpouse(spouse) {
				this.selectedSpouseId = spouse.id;
				if (this.currentMember.sex === '女') {
					this.formData.pName = spouse.name;
					this.formData.userPId = spouse.id;
				} else {
					this.formData.mName = spouse.name;
					this.formData.userMID = spouse.id;
				}
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

			// 提交表单
			async handleSubmit() {
				if (!this.canSubmit) {
					uni.showToast({
						title: '请填写完整信息',
						icon: 'none'
					});
					return;
				}

				uni.showLoading({
					title: '提交中...'
				});

				try {
					// 准备提交数据
					const submitData = {
						...this.formData
					};

					// 调用API添加成员
					if (submitData.type === 'parent') {


					} else {
						const res = await familyApi._addSource(submitData);
					}

					uni.hideLoading();

					uni.showToast({
						title: '添加成功',
						icon: 'success'
					});

					// 返回上一页并刷新数据
					const pages = getCurrentPages();
					const prevPage = pages[pages.length - 2];
					if (prevPage) {
						// 直接调用上一页的方法
						if (prevPage.$vm && prevPage.$vm.loadFamilyData) {
							prevPage.$vm.loadFamilyData();
						} else if (prevPage.loadFamilyData) {
							prevPage.loadFamilyData();
						}
					}
					setTimeout(() => {
						uni.navigateBack();
					}, 1500);

				} catch (error) {
					uni.hideLoading();
					console.error('提交失败:', error);
					uni.showToast({
						title: '添加失败',
						icon: 'none'
					});
				}
			}
		},

		watch: {
			'formData.isSurvival': function(newVal) {
				if (newVal === '是') {
					this.formData.userDieDay = '';
					this.formData.dieDayNum = '';
				}
			}
		}
	};
</script>

<style scoped>
	.add-member-container {
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
		/* width: 98%; */
		padding: 20rpx;
	}

	/* 表单区域 */
	.form-section {
		background: white;
		border-radius: 16rpx;
		padding: 30rpx;
		margin-right: 30rpx;
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

	/* 配偶选择 */
	.spouse-selection {
		margin-top: 20rpx;
	}

	.selection-tip {
		font-size: 26rpx;
		color: #666;
		margin-bottom: 30rpx;
	}

	.spouse-list {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.spouse-option {
		display: flex;
		align-items: center;
		padding: 25rpx;
		border: 2rpx solid #e8e8e8;
		border-radius: 12rpx;
		background: #f8f9fa;
		transition: all 0.3s ease;
	}

	.spouse-option.selected {
		border-color: #007AFF;
		background: rgba(0, 122, 255, 0.1);
	}

	.spouse-avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		margin-right: 20rpx;
	}

	.spouse-info {
		flex: 1;
	}

	.spouse-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 8rpx;
	}

	.spouse-detail {
		font-size: 24rpx;
		color: #666;
	}

	.check-icon {
		color: #007AFF;
		font-size: 32rpx;
		font-weight: bold;
	}

	/* 提交按钮 */
	.submit-section {
		margin: 50rpx 0;
	}

	.submit-btn {
		width: 50%;
		height: 90rpx;
		background: linear-gradient(135deg, #007AFF 0%, #0056CC 100%);
		color: white;
		border-radius: 45rpx;
		font-size: 32rpx;
		font-weight: bold;
		border: none;
	}

	.submit-btn:disabled {
		background: #ccc;
		color: #999;
	}
</style>