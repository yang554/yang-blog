<template>
	<view class="container">
		<view class="card">
			<view class="form-title">
				<text class="title-icon">📝</text>
				<text class="title-text">填写姨妈记录信息</text>
			</view>

			<view class="form-content">
				<!-- 姓名选择 -->
				<view class="form-section">
					<view class="form-item">
						<text class="form-label">姓名</text>
						<picker @change="onUserChange" :value="userIndex" :range="userOptions" range-key="label">
							<view class="picker">
								<text v-if="auntForm.uID && getUserLabel(auntForm.uID)">
									{{ getUserLabel(auntForm.uID) }}
								</text>
								<text v-else>请选择用户</text>
							</view>
						</picker>
					</view>
				</view>

				<!-- 日期选择 -->
				<view class="form-section">
					<view class="form-item">
						<text class="form-label">上次起始日</text>
						<view class="date-picker-group">
							<view class="date-picker-item">
								<text class="date-label">开始日期</text>
								<picker mode="date" :value="startDate" @change="onStartDateChange">
									<view class="picker">
										<text v-if="startDate">{{ startDate }}</text>
										<text v-else>选择开始日期</text>
									</view>
								</picker>
							</view>
							<view class="date-picker-item">
								<text class="date-label">结束日期</text>
								<picker mode="date" :value="endDate" @change="onEndDateChange">
									<view class="picker">
										<text v-if="endDate">{{ endDate }}</text>
										<text v-else>选择结束日期</text>
									</view>
								</picker>
							</view>
						</view>
						<text v-if="auntForm.duration" class="duration-display">持续时间：{{ auntForm.duration }}</text>
					</view>
				</view>

				<!-- 血量输入 -->
				<view class="form-section">
					<view class="form-item">
						<text class="form-label">血量(ml)</text>
						<view class="blood-input-group">
							<input class="blood-input" type="number" v-model="auntForm.bloodVolume"
								placeholder="请输入血量" />
							<text class="blood-unit">ml</text>
						</view>
						<text v-if="auntForm.bloodVolume" class="blood-level">
							<text v-if="auntForm.bloodVolume < 20">非常少</text>
							<text v-else-if="auntForm.bloodVolume < 50">较少</text>
							<text v-else-if="auntForm.bloodVolume < 80">正常</text>
							<text v-else-if="auntForm.bloodVolume < 120">较多</text>
							<text v-else>非常多</text>
						</text>
					</view>
				</view>

				<!-- 周期长度 -->
				<view class="form-section">
					<view class="form-item">
						<text class="form-label">周期长度</text>
						<view class="cycle-options">
							<view class="cycle-option" v-for="day in cycleDays" :key="day"
								:class="cycleOptionClass(day)" @click="selectCycle(day)">
								<text>{{ day }}天</text>
							</view>
						</view>
						<view class="custom-cycle">
							<text class="custom-label">自定义</text>
							<view class="custom-input-group">
								<input class="custom-input" type="number" v-model="customCycle" placeholder="输入天数" />
								<text class="custom-unit">天</text>
							</view>
						</view>
					</view>

					<!-- 统计信息 -->
					<view class="stats-container">
						<view class="stat-card">
							<text class="stat-label">持续时间</text>
							<text class="stat-value">{{ auntForm.duration || '0' }}</text>
						</view>
						<view class="stat-card">
							<text class="stat-label">平均周期长度</text>
							<text class="stat-value">{{ auntForm.cycle || '0' }}</text>
						</view>
					</view>
				</view>

				<!-- 症状选择 -->
				<view class="form-section">
					<view class="form-item">
						<text class="form-label">症状/表现</text>
						<textarea class="symptom-textarea" placeholder="记录每个月姨妈期间出现的症状，如腹痛、乳房胀痛、情绪波动等"
							v-model="auntForm.symptom" maxlength="-1"></textarea>
						<view class="symptom-tags">
							<view v-for="(symptom, index) in commonSymptoms" :key="index" class="symptom-tag"
								:class="symptomTagClass(symptom)" @click="toggleSymptom(symptom)">
								<text>{{ symptom }}</text>
							</view>
						</view>
					</view>
				</view>

				<!-- 备注 -->
				<view class="form-section">
					<view class="form-item">
						<text class="form-label">备注</text>
						<textarea class="note-textarea" placeholder="可以记录一些额外的信息，如服用的药物、健康状况等。" v-model="auntForm.note"
							maxlength="-1"></textarea>
					</view>
				</view>

				<!-- 操作按钮 -->
				<view class="footer-buttons">
					<button class="submit-btn" type="primary" @click="auntAddForm">
						<text class="btn-icon">💾</text>
						<text class="btn-text">保存记录</text>
					</button>
					<button class="reset-btn" @click="resetForm">
						<text class="btn-icon">🔄</text>
						<text class="btn-text">重置表单</text>
					</button>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		auntApi,
		userApi
	} from '@/api/index.js'

	export default {
		data() {
			return {
				auntForm: {
					uID: '',
					auntDate: '', //日期
					startDate: '', //开始日期
					endDate: '', //结束日期
					duration: '', //持续时间
					symptom: '', //症状
					cycle: '', //周期长度
					bloodVolume: '', //姨妈血量
					note: '', //备注
					uStatus: '0' //状态
				},
				startDate: '',
				endDate: '',
				userOptions: [],
				userIndex: -1,
				cycleDays: [26, 27, 28, 29, 30, 31, 32, 33, 34, 35],
				commonSymptoms: [
					'腹痛', '乳房胀痛', '情绪波动', '疲劳',
					'头痛', '腰酸背痛', '食欲变化', '失眠', '水肿'
				],
				selectedSymptoms: [],
				customCycle: '',
			};
		},
		watch: {
			customCycle(newVal) {
				if (newVal) {
					this.auntForm.cycle = newVal + '天';
				}
			},
			startDate(newVal) {
				if (newVal && this.endDate) {
					this.calculateDuration();
				}
			},
			endDate(newVal) {
				if (this.startDate && newVal) {
					this.calculateDuration();
				}
			}
		},
		methods: {
			// 获取用户标签
			getUserLabel(uID) {
				const user = this.userOptions.find(u => u.value === uID);
				return user ? user.label : '';
			},

			// 周期选项类名
			cycleOptionClass(day) {
				return this.auntForm.cycle === day + '天' ? 'active' : '';
			},

			// 症状标签类名
			symptomTagClass(symptom) {
				return this.selectedSymptoms.includes(symptom) ? 'active' : '';
			},

			// 用户选择
			onUserChange(e) {
				const index = e.detail.value;
				this.userIndex = index;
				this.auntForm.uID = this.userOptions[index].value;
			},

			// 开始日期选择
			onStartDateChange(e) {
				this.startDate = e.detail.value;
				this.auntForm.startDate = e.detail.value;
			},

			// 结束日期选择
			onEndDateChange(e) {
				this.endDate = e.detail.value;
				this.auntForm.endDate = e.detail.value;
			},

			// 计算持续时间
			calculateDuration() {
				if (this.startDate && this.endDate) {
					const start = new Date(this.startDate);
					const end = new Date(this.endDate);
					const diffTime = Math.abs(end - start);
					const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
					this.auntForm.duration = diffDays + '天';
				}
			},

			// 选择周期
			selectCycle(day) {
				this.auntForm.cycle = day + '天';
				this.customCycle = '';
			},

			// 切换症状
			toggleSymptom(symptom) {
				const index = this.selectedSymptoms.indexOf(symptom);
				if (index === -1) {
					this.selectedSymptoms.push(symptom);
				} else {
					this.selectedSymptoms.splice(index, 1);
				}
				// 更新症状文本
				this.auntForm.symptom = this.selectedSymptoms.join('，');
			},

			// 保存表单
			auntAddForm() {
				// 表单验证
				if (!this.auntForm.uID) {
					uni.showToast({
						title: '请选择用户',
						icon: 'none'
					});
					return;
				}

				if (!this.startDate || !this.endDate) {
					uni.showToast({
						title: '请选择完整的日期范围',
						icon: 'none'
					});
					return;
				}

				if (!this.auntForm.cycle) {
					uni.showToast({
						title: '请选择周期长度',
						icon: 'none'
					});
					return;
				}

				// 提交数据
				auntApi._addAunt(this.auntForm).then(res => {
					uni.showToast({
						title: "创建成功!",
						icon: "success"
					});
					this.resetForm();
				}).catch(err => {
					console.error('提交失败:', err);
					uni.showToast({
						title: "创建失败",
						icon: "error"
					});
				});
			},

			// 重置表单
			resetForm() {
				this.auntForm = {
					uID: '',
					auntDate: '',
					startDate: '',
					endDate: '',
					duration: '',
					symptom: '',
					cycle: '',
					bloodVolume: '',
					note: '',
					uStatus: '0'
				};
				this.startDate = '';
				this.endDate = '';
				this.userIndex = -1;
				this.selectedSymptoms = [];
				this.customCycle = '';
			},

			// 获取用户列表
			getUserNameAll() {
				userApi.getAllUsers().then(res => {
					this.userOptions = res.map(user => ({
						label: user.username,
						value: user.id
					}));
				}).catch(err => {
					console.error('获取用户列表失败:', err);
				});
			}
		},
		created() {
			this.getUserNameAll();
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		padding: 30rpx;
		background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
		min-height: 100vh;
		box-sizing: border-box;
	}

	.card {
		background-color: white;
		border-radius: 16rpx;
		box-shadow: 0 10rpx 30rpx rgba(138, 79, 255, 0.15);
		overflow: hidden;
		padding: 30rpx;
		margin-bottom: 30rpx;
	}

	.form-title {
		display: flex;
		align-items: center;
		color: #8a4fff;
		font-size: 32rpx;
		margin-bottom: 30rpx;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #f0e6ff;
	}

	.title-icon {
		margin-right: 15rpx;
		font-size: 36rpx;
	}

	.title-text {
		font-weight: bold;
	}

	.form-section {
		margin-bottom: 40rpx;
		padding-bottom: 30rpx;
		border-bottom: 1rpx dashed #eee;
	}

	.form-item {
		margin-bottom: 30rpx;
	}

	.form-label {
		display: block;
		margin-bottom: 15rpx;
		font-size: 28rpx;
		color: #333;
		font-weight: 500;
	}

	.picker {
		padding: 20rpx;
		border: 1rpx solid #ddd;
		border-radius: 8rpx;
		background-color: #f9f9f9;
		font-size: 28rpx;
		color: #333;
	}

	.date-picker-group {
		display: flex;
		gap: 20rpx;
	}

	.date-picker-item {
		flex: 1;
	}

	.date-label {
		display: block;
		margin-bottom: 10rpx;
		font-size: 24rpx;
		color: #666;
	}

	.duration-display {
		display: block;
		margin-top: 15rpx;
		font-size: 26rpx;
		color: #8a4fff;
		font-weight: bold;
	}

	.blood-input-group {
		display: flex;
		align-items: center;
		border: 1rpx solid #ddd;
		border-radius: 8rpx;
		overflow: hidden;
	}

	.blood-input {
		flex: 1;
		padding: 20rpx;
		font-size: 28rpx;
	}

	.blood-unit {
		padding: 20rpx;
		background-color: #f5f5f5;
		color: #666;
		font-size: 28rpx;
	}

	.blood-level {
		display: block;
		margin-top: 15rpx;
		font-size: 26rpx;
		color: #888;
	}

	.cycle-options {
		display: grid;
		grid-template-columns: repeat(4, 1fr);
		gap: 15rpx;
		margin-bottom: 20rpx;
	}

	.cycle-option {
		padding: 20rpx 10rpx;
		border: 1rpx solid #e6d6ff;
		border-radius: 8rpx;
		text-align: center;
		background: #f5eeff;
		color: #7a5cbd;
		font-size: 26rpx;
	}

	.cycle-option.active {
		background: #8a4fff;
		color: white;
		border-color: #8a4fff;
	}

	.custom-cycle {
		display: flex;
		align-items: center;
		gap: 15rpx;
	}

	.custom-label {
		font-size: 26rpx;
		color: #666;
		white-space: nowrap;
	}

	.custom-input-group {
		display: flex;
		align-items: center;
		border: 1rpx solid #ddd;
		border-radius: 8rpx;
		overflow: hidden;
		flex: 1;
	}

	.custom-input {
		flex: 1;
		padding: 15rpx;
		font-size: 26rpx;
	}

	.custom-unit {
		padding: 15rpx;
		background-color: #f5f5f5;
		color: #666;
		font-size: 26rpx;
	}

	.stats-container {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
		margin-top: 30rpx;
	}

	.stat-card {
		background: #f9f4ff;
		border-radius: 12rpx;
		padding: 30rpx 20rpx;
		text-align: center;
		border: 1rpx solid #e6d6ff;
	}

	.stat-value {
		font-size: 36rpx;
		font-weight: bold;
		color: #8a4fff;
		margin: 15rpx 0;
		display: block;
	}

	.stat-label {
		color: #7a5cbd;
		font-size: 24rpx;
	}

	.symptom-textarea,
	.note-textarea {
		width: 100%;
		padding: 20rpx;
		border: 1rpx solid #ddd;
		border-radius: 8rpx;
		font-size: 28rpx;
		background-color: #f9f9f9;
		box-sizing: border-box;
		margin-bottom: 20rpx;
	}

	.symptom-tags {
		display: flex;
		flex-wrap: wrap;
		gap: 15rpx;
	}

	.symptom-tag {
		padding: 12rpx 25rpx;
		border-radius: 30rpx;
		background: #f5eeff;
		border: 1rpx solid #e6d6ff;
		color: #7a5cbd;
		font-size: 24rpx;
		transition: all 0.2s;
	}

	.symptom-tag.active {
		background: #8a4fff;
		color: white;
		border-color: #8a4fff;
	}

	.footer-buttons {
		display: flex;
		justify-content: center;
		gap: 30rpx;
		margin-top: 40rpx;
	}

	.submit-btn,
	.reset-btn {
		padding: 20rpx 40rpx;
		border-radius: 8rpx;
		font-weight: 500;
		font-size: 28rpx;
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.submit-btn {
		background: linear-gradient(to right, #ff7eb3, #8a4fff);
		color: white;
		border: none;
	}

	.reset-btn {
		background: #f5f5f5;
		color: #666;
		border: 1rpx solid #ddd;
	}

	.btn-icon {
		font-size: 32rpx;
	}

	/* 响应式设计 */
	@media (max-width: 768px) {
		.container {
			padding: 20rpx;
		}

		.card {
			padding: 20rpx;
		}

		.date-picker-group {
			flex-direction: column;
			gap: 15rpx;
		}

		.cycle-options {
			grid-template-columns: repeat(2, 1fr);
		}

		.stats-container {
			grid-template-columns: 1fr;
		}

		.footer-buttons {
			flex-direction: column;
			gap: 20rpx;
		}

		.submit-btn,
		.reset-btn {
			width: 100%;
			justify-content: center;
		}
	}
</style>