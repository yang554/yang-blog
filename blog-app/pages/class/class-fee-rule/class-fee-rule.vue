<template>
	<view class="fee-calculator">
		<!-- 背景渐变 -->
		<view class="background-gradient"></view>
		<!-- 页面内容 -->
		<view class="calculator-container">
			<!-- 基础课时费卡片 -->
			<view class="card-container">
				<view class="calculator-card">
					<view class="card-header">
						<h2>基础课时费</h2>
					</view>
					<view class="table-container">
						<view class="table-header">
							<view class="table-cell">教师评级</view>
							<view class="table-cell">小学</view>
							<view class="table-cell">初中</view>
							<view class="table-cell">高一二</view>
							<view class="table-cell">高三</view>
						</view>
						<view class="table-row" v-for="item in baseFeesData" :key="item.level">
							<view class="table-cell">{{ item.level }}</view>
							<view class="table-cell">{{ item.primary }}</view>
							<view class="table-cell">{{ item.junior }}</view>
							<view class="table-cell">{{ item.seniorOne }}</view>
							<view class="table-cell">{{ item.seniorThree }}</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 班课正价班课时费卡片 -->
			<view class="card-container">
				<view class="calculator-card">
					<view class="card-header">
						<h2>班课正价班课时费</h2>
					</view>
					<view class="table-container">
						<view class="table-header">
							<view class="table-cell center">班级人数</view>
							<view class="table-cell center">小学初中（增加额）</view>
							<view class="table-cell center">高中（增加额）</view>
						</view>
						<view class="table-row" v-for="item in feeExtraData" :key="item.studentCount">
							<view class="table-cell center">{{ item.studentCount }}</view>
							<view class="table-cell center">{{ item.primaryMiddle }}</view>
							<view class="table-cell center">{{ item.highSchool }}</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 右侧计算器区域 -->
			<view class="card-container">
				<!-- 班课流量班课时费卡片 -->
				<view class="calculator-card">
					<view class="card-header">
						<h2>班课流量班课时费</h2>
					</view>
					<view class="flow-class-info">
						<text>小于五人，按照基础课时费计算</text>
						<text>大于五人，基础课时费+10*（班级人数-1）</text>
					</view>
				</view>

				<!-- 课时费计算器卡片 -->
				<view class="calculator-card calculator-form">
					<view class="card-header">
						<h2>课时费计算器</h2>
					</view>

					<view class="form-container">
						<!-- 教师等级 -->
						<view class="form-item">
							<text class="form-label">教师等级</text>
							<picker @change="onTeacherLevelChange" :value="teacherLevelIndex" :range="teacherLevels"
								range-key="label">
								<view class="picker">{{ form.teacherLevel ? `${form.teacherLevel}级` : '请选择' }}</view>
							</picker>
						</view>

						<!-- 年级 -->
						<view class="form-item">
							<text class="form-label">年级</text>
							<picker @change="onGradeChange" :value="gradeIndex" :range="grades" range-key="label">
								<view class="picker">{{ form.grade || '请选择' }}</view>
							</picker>
						</view>

						<!-- 授课类型 -->
						<view class="form-item">
							<text class="form-label">授课类型</text>
							<radio-group @change="onClassTypeChange" class="radio-group">
								<label class="radio-label" v-for="item in classTypes" :key="item.value">
									<radio :value="item.value" :checked="form.classType === item.value" />
									<text>{{ item.label }}</text>
								</label>
							</radio-group>
						</view>

						<!-- 班课相关字段 -->
						<view v-if="form.classType === '班课'">
							<view class="form-item">
								<text class="form-label">学生人数</text>
								<view class="number-input-container">
									<button class="number-btn" @click="decreaseStudentCount"
										:disabled="form.studentCount <= 1">-</button>
									<text class="number-display">{{ form.studentCount }}</text>
									<button class="number-btn" @click="increaseStudentCount"
										:disabled="form.studentCount >= 30">+</button>
								</view>
							</view>

							<view class="form-item">
								<text class="form-label">班课类型</text>
								<radio-group @change="onClassCategoryChange" class="radio-group">
									<label class="radio-label" v-for="item in classCategories" :key="item.value">
										<radio :value="item.value" :checked="form.classCategory === item.value" />
										<text>{{ item.label }}</text>
									</label>
								</radio-group>
							</view>
						</view>

						<view class="form-item">
							<button class="calculate-btn" @click="calculateFee" :disabled="!isFormValid">
								计算课时费
							</button>
						</view>
					</view>

					<!-- 结果显示 -->
					<view v-if="result !== null" class="result-section">
						<view class="result-header">
							<h3>计算结果</h3>
						</view>
						<view class="fee-result">
							<text>课时费：</text>
							<text class="fee-amount">{{ result }} 元</text>
						</view>

						<view class="calculation-details">
							<view class="details-header">
								<h4>计算说明：</h4>
							</view>
							<rich-text :nodes="calculationDetails" class="details-content"></rich-text>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				baseFeesData: [{
						level: '一级',
						primary: 100,
						junior: 105,
						seniorOne: 120,
						seniorThree: 125
					},
					{
						level: '二级',
						primary: 105,
						junior: 110,
						seniorOne: 125,
						seniorThree: 130
					},
					{
						level: '三级',
						primary: 110,
						junior: 115,
						seniorOne: 130,
						seniorThree: 140
					},
					{
						level: '四级',
						primary: 115,
						junior: 120,
						seniorOne: 140,
						seniorThree: 150
					},
					{
						level: '五级',
						primary: 130,
						junior: 140,
						seniorOne: 160,
						seniorThree: 170
					},
					{
						level: '六级',
						primary: 135,
						junior: 150,
						seniorOne: 170,
						seniorThree: 180
					},
					{
						level: '七级',
						primary: 140,
						junior: 160,
						seniorOne: 180,
						seniorThree: 190
					},
					{
						level: '八级',
						primary: 145,
						junior: 170,
						seniorOne: 190,
						seniorThree: 200
					},
					{
						level: '九级',
						primary: 150,
						junior: 180,
						seniorOne: 200,
						seniorThree: 210
					},
					{
						level: '十级',
						primary: 155,
						junior: 190,
						seniorOne: 210,
						seniorThree: 220
					},
					{
						level: '十一级',
						primary: 160,
						junior: 200,
						seniorOne: 220,
						seniorThree: 230
					},
					{
						level: '十二级',
						primary: 170,
						junior: 210,
						seniorOne: 230,
						seniorThree: 250
					}
				],
				feeExtraData: [{
						studentCount: '4个学生',
						primaryMiddle: 0,
						highSchool: 0
					},
					{
						studentCount: '5个学生',
						primaryMiddle: 50,
						highSchool: 60
					},
					{
						studentCount: '6个学生',
						primaryMiddle: 70,
						highSchool: 80
					},
					{
						studentCount: '7个学生',
						primaryMiddle: 90,
						highSchool: 100
					},
					{
						studentCount: '8个学生',
						primaryMiddle: 120,
						highSchool: 130
					},
					{
						studentCount: '9个学生',
						primaryMiddle: 140,
						highSchool: 150
					},
					{
						studentCount: '10个学生',
						primaryMiddle: 160,
						highSchool: 170
					},
					{
						studentCount: '11个学生',
						primaryMiddle: 180,
						highSchool: 190
					},
					{
						studentCount: '12个学生',
						primaryMiddle: 220,
						highSchool: 230
					},
					{
						studentCount: '13个学生',
						primaryMiddle: 240,
						highSchool: 250
					},
					{
						studentCount: '14个学生',
						primaryMiddle: 280,
						highSchool: 290
					},
					{
						studentCount: '15个学生',
						primaryMiddle: 300,
						highSchool: 310
					},
				],
				// 基础课时费表
				baseFees: {
					'小学': {
						1: 100,
						2: 105,
						3: 110,
						4: 115,
						5: 130,
						6: 135,
						7: 140,
						8: 145,
						9: 150,
						10: 155,
						11: 160,
						12: 170
					},
					'初中': {
						1: 105,
						2: 110,
						3: 115,
						4: 120,
						5: 140,
						6: 150,
						7: 160,
						8: 170,
						9: 180,
						10: 190,
						11: 200,
						12: 210
					},
					'高一二': {
						1: 120,
						2: 125,
						3: 130,
						4: 140,
						5: 160,
						6: 170,
						7: 180,
						8: 190,
						9: 200,
						10: 210,
						11: 220,
						12: 230
					},
					'高三': {
						1: 125,
						2: 130,
						3: 140,
						4: 150,
						5: 170,
						6: 180,
						7: 190,
						8: 200,
						9: 210,
						10: 220,
						11: 230,
						12: 250
					}
				},
				// 小学初中班课增加额表
				primaryMiddleExtra: {
					5: 50,
					6: 70,
					7: 90,
					8: 120,
					9: 140,
					10: 160,
					11: 180,
					12: 220,
					13: 240,
					14: 280,
					15: 300
				},
				// 高中班课增加额表
				highSchoolExtra: {
					5: 60,
					6: 80,
					7: 100,
					8: 130,
					9: 150,
					10: 170,
					11: 190,
					12: 230,
					13: 250,
					14: 290,
					15: 310
				},
				form: {
					teacherLevel: 1,
					grade: '小学',
					classType: '一对一',
					studentCount: 1,
					classCategory: '正价班',
					fee: 0
				},
				result: null,
				calculationDetails: '',
				teacherLevels: Array.from({
					length: 12
				}, (_, i) => ({
					label: `${i + 1}级`,
					value: i + 1
				})),
				grades: [{
						label: '小学',
						value: '小学'
					},
					{
						label: '初中',
						value: '初中'
					},
					{
						label: '高一二',
						value: '高一二'
					},
					{
						label: '高三',
						value: '高三'
					}
				],
				classTypes: [{
						label: '一对一',
						value: '一对一'
					},
					{
						label: '班课',
						value: '班课'
					}
				],
				classCategories: [{
						label: '正价班',
						value: '正价班'
					},
					{
						label: '流量班',
						value: '流量班'
					}
				]
			};
		},
		computed: {
			isFormValid() {
				return this.form.teacherLevel && this.form.grade;
			},
			teacherLevelIndex() {
				return this.form.teacherLevel - 1;
			},
			gradeIndex() {
				return this.grades.findIndex(item => item.value === this.form.grade);
			}
		},
		methods: {
			onTeacherLevelChange(e) {
				this.form.teacherLevel = this.teacherLevels[e.detail.value].value;
			},
			onGradeChange(e) {
				this.form.grade = this.grades[e.detail.value].value;
			},
			onClassTypeChange(e) {
				this.form.classType = e.detail.value;
			},
			onClassCategoryChange(e) {
				this.form.classCategory = e.detail.value;
			},
			decreaseStudentCount() {
				if (this.form.studentCount > 1) {
					this.form.studentCount--;
				}
			},
			increaseStudentCount() {
				if (this.form.studentCount < 30) {
					this.form.studentCount++;
				}
			},
			getBaseFee(grade, level) {
				if (this.baseFees[grade] && this.baseFees[grade][level]) {
					return this.baseFees[grade][level];
				}
				throw new Error(`无效的年级或教师等级: ${grade} ${level}`);
			},
			getExtraFee(grade, studentCount) {
				const isHighSchool = grade === '高一二' || grade === '高三';
				const extraMap = isHighSchool ? this.highSchoolExtra : this.primaryMiddleExtra;
				// 处理学生人数大于15的情况
				let count = studentCount
				// 查找最接近的学生人数
				while (count > 4 && !extraMap.hasOwnProperty(count)) {
					count--;
				}
				return extraMap[count] || 0;
			},
			calculateFee() {
				try {
					const {
						teacherLevel,
						grade,
						classType,
						studentCount,
						classCategory
					} = this.form;
					const baseFee = this.getBaseFee(grade, teacherLevel);
					let details = `基础课时费（${grade} ${teacherLevel}级）：${baseFee}元`;

					if (classType === '一对一') {
						this.result = baseFee;
						this.calculationDetails = details;
						return;
					}

					if (studentCount < 5) {
						this.result = baseFee;
					} else if (classCategory === '流量班') {
						this.result = baseFee + 10 * (studentCount - 1);
						details += `<br>流量班公式：${baseFee} + 10 × (${studentCount} - 1) = ${this.result}元`;
					} else {
						this.result = baseFee + this.getExtraFee(grade, studentCount);
						details += `<br>正价班增加额${this.getExtraFee(grade, studentCount)}元`;
					}
					this.calculationDetails = details;
				} catch (error) {
					uni.showToast({
						title: '计算失败，请检查输入',
						icon: 'none'
					});
					this.result = null;
				}
			}
		}
	};
</script>

<style lang="scss" scoped>
	@import '@/scss/variables';


	.fee-calculator {
		padding: 20rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
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

	.calculator-container {
		position: relative;
		z-index: 2;
		display: flex;
		flex-direction: column;
		gap: 30rpx;
	}

	.card-container {
		width: 100%;
	}

	.calculator-card {
		background-color: #ffffff;
		border-radius: 10rpx;
		padding: 30rpx;
		box-shadow: 0 2rpx 12rpx 0 rgba(0, 0, 0, 0.1);
		margin-bottom: 30rpx;
	}

	.calculator-form {
		margin-top: 30rpx;
	}

	.card-header h2 {
		margin: 0 0 20rpx 0;
		font-size: 32rpx;
		color: #303133;
		font-weight: bold;
	}

	/* 表格样式 */
	.table-container {
		border: 1rpx solid #ebeef5;
		border-radius: 8rpx;
		overflow: hidden;
	}

	.table-header,
	.table-row {
		display: flex;
		border-bottom: 1rpx solid #ebeef5;
	}

	.table-header:last-child,
	.table-row:last-child {
		border-bottom: none;
	}

	.table-cell {
		flex: 1;
		padding: 20rpx;
		text-align: left;
		font-size: 28rpx;
		color: #606266;
	}

	.table-header .table-cell {
		background-color: #fafafa;
		font-weight: bold;
		color: #303133;
	}

	.table-row:nth-child(even) {
		background-color: #fafafa;
	}

	.center {
		text-align: center;
	}

	/* 流量班信息 */
	.flow-class-info {
		display: flex;
		flex-direction: column;
		gap: 10rpx;
	}

	.flow-class-info text {
		font-size: 28rpx;
		color: #606266;
	}

	/* 表单样式 */
	.form-container {
		display: flex;
		flex-direction: column;
		gap: 30rpx;
	}

	.form-item {
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.form-label {
		font-size: 28rpx;
		color: #606266;
		font-weight: bold;
	}

	.picker {
		padding: 20rpx;
		border: 1rpx solid #dcdfe6;
		border-radius: 8rpx;
		font-size: 28rpx;
		color: #606266;
		background-color: #ffffff;
	}

	.radio-group {
		display: flex;
		gap: 40rpx;
	}

	.radio-label {
		display: flex;
		align-items: center;
		gap: 10rpx;
		font-size: 28rpx;
		color: #606266;
	}

	/* 数字输入样式 */
	.number-input-container {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.number-btn {
		width: 60rpx;
		height: 60rpx;
		border: 1rpx solid #dcdfe6;
		background-color: #ffffff;
		border-radius: 8rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 28rpx;
	}

	.number-btn:disabled {
		background-color: #f5f7fa;
		color: #c0c4cc;
	}

	.number-display {
		font-size: 28rpx;
		color: #303133;
		min-width: 80rpx;
		text-align: center;
	}

	/* 计算按钮 */
	.calculate-btn {
		background-color: #409eff;
		color: #ffffff;
		border: none;
		border-radius: 8rpx;
		padding: 20rpx;
		font-size: 28rpx;
		margin-top: 10rpx;
		
		&:active {
			transform: scale(0.95);
		}
	}

	.calculate-btn:disabled {
		background-color: #a0cfff;
	}

	/* 结果区域 */
	.result-section {
		margin-top: 30rpx;
		padding: 30rpx;
		background-color: #f8f9fa;
		border-radius: 8rpx;
	}

	.result-header h3 {
		margin: 0 0 20rpx 0;
		font-size: 30rpx;
		color: #303133;
	}

	.fee-result {
		display: flex;
		align-items: center;
		gap: 15rpx;
		margin-bottom: 20rpx;
		font-size: 28rpx;
		color: #606266;
	}

	.fee-amount {
		font-size: 36rpx;
		font-weight: bold;
		color: #e6a23c;
	}

	.calculation-details {
		background-color: #ecf5ff;
		border-radius: 8rpx;
		padding: 30rpx;
	}

	.details-header h4 {
		margin: 0 0 15rpx 0;
		font-size: 28rpx;
		color: #303133;
	}

	.details-content {
		font-size: 26rpx;
		color: #606266;
		line-height: 1.6;
	}

	/* 响应式布局 */
	@media (min-width: 768px) {
		.calculator-container {
			flex-direction: row;
			flex-wrap: wrap;
			justify-content: space-between;
		}

		.card-container {
			width: 48%;
		}

		.card-container:last-child {
			width: 100%;
		}
	}

	@media (min-width: 1024px) {
		.calculator-container {
			flex-wrap: nowrap;
		}

		.card-container {
			width: 32%;
		}

		.card-container:last-child {
			width: 32%;
		}
	}
</style>