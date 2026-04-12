<template>
	<view class="home-container">
		<!-- 背景渐变 -->
		<view class="background-gradient"></view>

		<!-- 页面内容 -->
		<view class="content-wrapper">
			<scroll-view class="main-content" scroll-y :show-scrollbar="false" refresher-background="transparent"
				:style="{ height: '100%' }">
				<view class="container">
					<!-- 配置表单 -->
					<view class="form-container">
						<view class="form-row">
							<view class="form-item">
								<text class="label">教师评级</text>
								<picker @change="onTeacherLevelChange" :value="teacherLevelIndex"
									:range="teacherLevelOptions" range-key="label">
									<view class="picker">{{ entity.teacherLevel ? `${entity.teacherLevel}级` : '请选择' }}
									</view>
								</picker>
							</view>

							<view class="form-item">
								<text class="label">年级</text>
								<picker @change="onGradeChange" :value="gradeIndex" :range="gradeOptions"
									range-key="label">
									<view class="picker">{{ entity.grade || '请选择' }}</view>
								</picker>
							</view>

							<view class="form-item">
								<text class="label">授课类型</text>
								<picker @change="onClassTypeChange" :value="classTypeIndex" :range="classTypeOptions"
									range-key="label">
									<view class="picker">{{ entity.classType || '请选择' }}</view>
								</picker>
							</view>
						</view>

						<!-- 班课相关字段 -->
						<view class="form-row" v-if="entity.classType === '班课'">
							<view class="form-item">
								<text class="label">学生人数</text>
								<view class="number-input">
									<text class="number-btn" @tap="decreaseStudentCount">-</text>
									<text class="number-value">{{ entity.studentCount }}</text>
									<text class="number-btn" @tap="increaseStudentCount">+</text>
								</view>
							</view>

							<view class="form-item">
								<text class="label">班课类型</text>
								<picker @change="onClassCategoryChange" :value="classCategoryIndex"
									:range="classCategoryOptions" range-key="label">
									<view class="picker">{{ entity.classCategory || '请选择' }}</view>
								</picker>
							</view>
						</view>

						<view class="button-group">
							<button class="btn primary" @tap="addClassFee">添加课时</button>
							<button class="btn success" @tap="importFromExcel">导入课时</button>
							<button class="btn warning" @tap="exportToExcel">导出课时</button>
							<button class="btn danger" @tap="clearAll">清空列表</button>
						</view>
					</view>

					<!-- 课时列表 -->
					<view class="table-container">
						<scroll-view class="table-scroll" scroll-x="true" scroll-y="true"
							:style="{height: screenHeight + 'px'}">
							<view class="table-wrapper" :style="{width: tableWidth + 'px'}">
								<!-- 表头 -->
								<view class="table-header">
									<view class="table-cell" v-for="(header, index) in tableHeaders" :key="index"
										:style="{width: header.width + 'px', 'min-width': header.width + 'px'}">
										{{ header.label }}
									</view>
								</view>

								<!-- 表格内容 -->
								<view class="table-body">
									<view class="table-row" v-for="(item, index) in pagination.currentTableData"
										:key="item.id">
										<view class="table-cell"
											:style="{width: tableHeaders[0].width + 'px', 'min-width': tableHeaders[0].width + 'px'}">
											{{ item.teacherLevel }}级
										</view>
										<view class="table-cell"
											:style="{width: tableHeaders[1].width + 'px', 'min-width': tableHeaders[1].width + 'px'}">
											{{ item.grade }}
										</view>
										<view class="table-cell"
											:style="{width: tableHeaders[2].width + 'px', 'min-width': tableHeaders[2].width + 'px'}">
											{{ item.classType }}
										</view>
										<view class="table-cell"
											:style="{width: tableHeaders[3].width + 'px', 'min-width': tableHeaders[3].width + 'px'}">
											{{ item.studentCount }}
										</view>
										<view class="table-cell"
											:style="{width: tableHeaders[4].width + 'px', 'min-width': tableHeaders[4].width + 'px'}">
											{{ item.classCategory }}
										</view>
										<view class="table-cell"
											:style="{width: tableHeaders[5].width + 'px', 'min-width': tableHeaders[5].width + 'px'}">
											{{ calculateFee(item) }}
										</view>
										<view class="table-cell"
											:style="{width: tableHeaders[6].width + 'px', 'min-width': tableHeaders[6].width + 'px'}">
											<picker mode="date" :value="item.date"
												@change="(e) => onDateChange(e, index)">
												<view class="date-picker">{{ item.date || '选择日期' }}</view>
											</picker>
										</view>
										<view class="table-cell"
											:style="{width: tableHeaders[7].width + 'px', 'min-width': tableHeaders[7].width + 'px'}">
											<text class="delete-btn" @tap="deleteClassFee(item)">删除</text>
										</view>
									</view>
								</view>
							</view>
						</scroll-view>
					</view>

					<!-- 分页 -->
					<view class="pagination">
						<view class="pagination-info">
							共 {{ pagination.total }} 条记录
						</view>
						<view class="pagination-controls">
							<text class="page-btn" :class="{disabled: pagination.currentPage === 1}"
								@tap="prevPage">上一页</text>
							<text class="page-info">第 {{ pagination.currentPage }} 页</text>
							<text class="page-btn" :class="{disabled: pagination.currentPage >= pagination.totalPage}"
								@tap="nextPage">下一页</text>
						</view>
					</view>

					<!-- 统计信息 -->
					<view class="stats">
						<text class="total-amount" v-if="pagination.total > 0">总金额：{{ totalFee }}元</text>
						<button class="save-btn" @tap="saveBtn">保存</button>
					</view>
				</view>

			</scroll-view>
		</view>
	</view>
</template>

<script>
	import CustomNavbar from '@/components/custom-navbar/custom-navbar.vue'
	import CustomTabbar from '@/components/custom-tabbar/custom-tabbar.vue'

	export default {
		components: {
			CustomNavbar,
			CustomTabbar
		},
		name: "basicFeeView",
		data() {
			return {
				statusBarHeight: 0,
				screenHeight: 500,
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
				entity: {
					id: '',
					teacherLevel: 2,
					grade: '初中',
					classType: '一对一',
					studentCount: 1,
					classCategory: '正价班',
					fee: 0,
					date: this.formatDate(new Date())
				},
				// 选择器选项
				teacherLevelOptions: Array.from({
					length: 12
				}, (_, i) => ({
					label: `${i+1}级`,
					value: i + 1
				})),
				gradeOptions: [{
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
				classTypeOptions: [{
						label: '一对一',
						value: '一对一'
					},
					{
						label: '班课',
						value: '班课'
					}
				],
				classCategoryOptions: [{
						label: '正价班',
						value: '正价班'
					},
					{
						label: '流量班',
						value: '流量班'
					}
				],
				// 选择器索引
				teacherLevelIndex: 1,
				gradeIndex: 1,
				classTypeIndex: 0,
				classCategoryIndex: 0,
				// 分页数据
				pagination: {
					listData: [], // 总数据
					currentPage: 1, // 当前页
					pageSize: 10, // 每页显示条数
					totalPage: 1, // 总页数
					currentTableData: [], // 当前页数据
					total: 0 // 总数量
				},
				// 表格配置
				tableHeaders: [{
						label: '教师评级',
						width: 100
					},
					{
						label: '年级',
						width: 80
					},
					{
						label: '授课类型',
						width: 100
					},
					{
						label: '学生人数',
						width: 100
					},
					{
						label: '班课类型',
						width: 100
					},
					{
						label: '课时费(￥)',
						width: 120
					},
					{
						label: '日期',
						width: 150
					},
					{
						label: '操作',
						width: 100
					}
				],
				tableWidth: 850 // 所有列宽总和

			};
		},
		computed: {
			totalFee() {
				return this.pagination.listData.reduce((sum, item) => sum + (item.fee || 0), 0)
			}
		},
		onLoad() {
			this.getStatusBarHeight();
			this.updateScreenHeight()
			// 生成初始ID
			this.entity.id = this.generateId()
			this.loadConfig();
		},
		onReady() {
			uni.hideTabBar();
			// 组件挂载时获取屏幕高度
			this.updateScreenHeight()
		},
		onUnload() {

		},
		methods: {
			getStatusBarHeight() {
				const systemInfo = uni.getSystemInfoSync();
				this.statusBarHeight = systemInfo.statusBarHeight;
			},
			updateScreenHeight() {
				// 获取屏幕高度并计算表格高度
				const systemInfo = uni.getSystemInfoSync()
				this.screenHeight = systemInfo.windowHeight - 400
			},

			// 生成唯一ID
			generateId() {
				return Date.now().toString()
			},

			// 格式化日期
			formatDate(date) {
				const year = date.getFullYear()
				const month = (date.getMonth() + 1).toString().padStart(2, '0')
				const day = date.getDate().toString().padStart(2, '0')
				return `${year}/${month}/${day}`
			},

			// 选择器变化事件
			onTeacherLevelChange(e) {
				const index = parseInt(e.detail.value)
				this.teacherLevelIndex = index
				this.entity.teacherLevel = this.teacherLevelOptions[index].value
			},

			onGradeChange(e) {
				const index = parseInt(e.detail.value)
				this.gradeIndex = index
				this.entity.grade = this.gradeOptions[index].value
			},

			onClassTypeChange(e) {
				const index = parseInt(e.detail.value)
				this.classTypeIndex = index
				this.entity.classType = this.classTypeOptions[index].value

				// 如果是班课，设置默认值
				if (this.entity.classType === '一对一') {
					this.entity.classCategory = '-'
					this.entity.studentCount = 1
				} else {
					this.entity.classCategory = '正价班'
				}
			},

			onClassCategoryChange(e) {
				const index = parseInt(e.detail.value)
				this.classCategoryIndex = index
				this.entity.classCategory = this.classCategoryOptions[index].value
			},

			onDateChange(e, index) {
				this.pagination.listData[index].date = e.detail.value
				this.updateCurrentTableData()
			},

			// 学生人数调整
			decreaseStudentCount() {
				if (this.entity.studentCount > 1) {
					this.entity.studentCount--
				}
			},

			increaseStudentCount() {
				if (this.entity.studentCount < 300) {
					this.entity.studentCount++
				}
			},

			// 获取基础课时费
			getBaseFee(grade, level) {
				if (this.baseFees[grade] && this.baseFees[grade][level]) {
					return this.baseFees[grade][level]
				}
				throw new Error(`无效的年级或教师等级: ${grade} ${level}`)
			},

			// 获取额外费用
			getExtraFee(grade, studentCount) {
				const isHighSchool = grade === '高一二' || grade === '高三'
				const extraMap = isHighSchool ? this.highSchoolExtra : this.primaryMiddleExtra

				// 处理学生人数大于15的情况
				let count = studentCount
				// 查找最接近的学生人数
				while (count > 4 && !extraMap.hasOwnProperty(count)) {
					count--
				}
				return extraMap[count] || 0
			},

			// 计算课时费
			calculateFee(row) {
				try {
					const {
						teacherLevel,
						grade,
						classType,
						studentCount,
						classCategory
					} = row
					const baseFee = this.getBaseFee(grade, teacherLevel)

					if (classType === '一对一') {
						row.fee = baseFee
						return row.fee
					}

					if (studentCount < 5) {
						row.fee = baseFee
					} else if (classCategory === '流量班') {
						row.fee = baseFee + 10 * (studentCount - 1)
					} else {
						row.fee = baseFee + this.getExtraFee(grade, studentCount)
					}
					return row.fee
				} catch (error) {
					console.error(error)
					uni.showToast({
						title: '计算失败: ' + error.message,
						icon: 'none'
					})
					row.fee = 0
					return row.fee
				}
			},
			// 清空列表
			clearAll() {
				if (this.pagination.listData.length === 0) {
					uni.showToast({
						title: '列表已为空',
						icon: 'none'
					});
					return;
				}

				uni.showModal({
					title: '确认清空',
					content: '确定要清空所有课时记录吗？此操作不可恢复',
					success: (res) => {
						if (res.confirm) {
							this.pagination.listData = [];
							this.pagination.total = 0;
							this.pagination.currentPage = 1;
							this.updateCurrentTableData();
							this.saveToLocalStorage();

							uni.showToast({
								title: '已清空',
								icon: 'success'
							});
						}
					}
				});
			},
			// 添加课时
			addClassFee() {
				// 验证必填字段
				if (!this.entity.teacherLevel || !this.entity.grade || !this.entity.classType) {
					uni.showToast({
						title: '请填写完整信息',
						icon: 'none'
					})
					return
				}

				// 添加记录
				const newRecord = {
					...this.entity
				}
				newRecord.fee = this.calculateFee(newRecord)

				this.pagination.listData.push(newRecord)
				this.pagination.total = this.pagination.listData.length
				this.updatePagination()

				// 重置表单
				this.entity.id = this.generateId()
				this.entity.date = this.formatDate(new Date())

				uni.showToast({
					title: '添加成功',
					icon: 'success'
				})
			},

			// 删除课时
			deleteClassFee(row) {
				uni.showModal({
					title: '确认删除',
					content: '确定要删除这条记录吗？',
					success: (res) => {
						if (res.confirm) {
							this.pagination.listData = this.pagination.listData.filter(
								item => item.id !== row.id
							)
							this.pagination.total = this.pagination.listData.length
							this.updatePagination()

							uni.showToast({
								title: '删除成功',
								icon: 'success'
							})
						}
					}
				})
			},

			// 导入Excel
			importFromExcel() {
				uni.showToast({
					title: '导入功能开发中',
					icon: 'none'
				})
				// 在实际项目中，这里可以使用uni.chooseFile选择文件
				// 然后使用第三方库解析Excel文件
			},

			// 导出Excel
			exportToExcel() {
				if (this.pagination.listData.length === 0) {
					uni.showToast({
						title: '没有数据可导出',
						icon: 'none'
					})
					return
				}

				// 在实际项目中，这里可以使用第三方库生成Excel文件
				// 然后使用uni.downloadFile下载文件

				// 这里简单模拟导出功能
				let csvContent = "教师等级,年级,授课类型,学生人数,班课类型,课时费(元),日期\n"

				this.pagination.listData.forEach(record => {
					csvContent +=
						`${record.teacherLevel},${record.grade},${record.classType},${record.studentCount},${record.classCategory},${record.fee},${record.date}\n`
				})

				// 在实际项目中，这里应该生成真正的Excel文件
				uni.showModal({
					title: '导出成功',
					content: '已生成数据文件',
					showCancel: false
				})

				console.log('导出数据:', csvContent)
			},

			// 分页方法
			updatePagination() {
				this.pagination.totalPage = Math.ceil(this.pagination.listData.length / this.pagination.pageSize) || 1
				this.updateCurrentTableData()
			},

			updateCurrentTableData() {
				const startIndex = (this.pagination.currentPage - 1) * this.pagination.pageSize
				const endIndex = startIndex + this.pagination.pageSize
				this.pagination.currentTableData = this.pagination.listData.slice(startIndex, endIndex)
			},

			prevPage() {
				if (this.pagination.currentPage > 1) {
					this.pagination.currentPage--
					this.updateCurrentTableData()
				}
			},

			nextPage() {
				if (this.pagination.currentPage < this.pagination.totalPage) {
					this.pagination.currentPage++
					this.updateCurrentTableData()
				}
			},
			// 加载本地数据
			loadConfig() {
				try {
					const data = uni.getStorageSync('basicFee');
					if (data) {
						let res = JSON.parse(data);
						this.pagination.listData = res;
						this.pagination.total = res.length;
						this.updatePagination();
					}
				} catch (error) {
					console.error('加载配置失败:', error);
				}
			},
			// 保存到本地存储
			saveToLocalStorage() {
				try {
					uni.setStorageSync('basicFee', JSON.stringify(this.pagination.listData));
				} catch (error) {
					console.error('保存到本地存储失败:', error);
				}
			},
			// 保存数据
			saveBtn() {
				if (this.pagination.listData.length === 0) {
					uni.showToast({
						title: '没有数据可保存',
						icon: 'none'
					})
					return
				}

				this.saveToLocalStorage();

				uni.showModal({
					title: '保存成功',
					content: '数据已保存至本地',
					showCancel: false
				})
			}

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

	.container {
		background-color: #f5f5f5;
	}

	.form-container {
		background-color: #fff;
		border-radius: 10rpx;
		padding: 20rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.form-row {
		display: flex;
		flex-wrap: wrap;
		margin-bottom: 20rpx;
	}

	.form-item {
		flex: 1;
		min-width: 200rpx;
		margin-right: 20rpx;
		margin-bottom: 20rpx;
	}

	.label {
		display: block;
		margin-bottom: 10rpx;
		font-size: 28rpx;
		color: #333;
	}

	.picker {
		padding: 20rpx;
		border: 1rpx solid #ddd;
		border-radius: 8rpx;
		background-color: #f9f9f9;
	}

	.number-input {
		display: flex;
		align-items: center;
		border: 1rpx solid #ddd;
		border-radius: 8rpx;
		overflow: hidden;
	}

	.number-btn {
		flex: 1;
		padding: 20rpx;
		text-align: center;
		background-color: #f0f0f0;
	}

	.number-value {
		flex: 2;
		padding: 20rpx;
		text-align: center;
		background-color: #fff;
	}

	.button-group {
		display: flex;
		justify-content: space-between;
		margin-top: 20rpx;
	}

	.btn {
		flex: 1;
		margin: 0 10rpx;
		padding: 5rpx;
		border-radius: 8rpx;
		color: #fff;
		font-size: 28rpx;
		text-align: center;
		
		&:active {
			transform: scale(0.95);
		}
	}

	.primary {
		background-color: #007AFF;
	}

	.success {
		background-color: #4CD964;
	}

	.warning {
		background-color: #FF9500;
	}
	
	.danger {
		background-color: #aa0000;
	}

	.table-container {
		background-color: #fff;
		border-radius: 10rpx;
		overflow: hidden;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.table-scroll {
		width: 100%;
	}

	.table-header,
	.table-row {
		display: flex;
		border-bottom: 1rpx solid #eee;
	}

	.table-cell {
		flex: 1;
		padding: 20rpx 10rpx;
		text-align: center;
		font-size: 24rpx;
		min-width: 100rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.table-header .table-cell {
		font-weight: bold;
		background-color: #f5f5f5;
	}

	.date-picker {
		padding: 10rpx;
		border: 1rpx solid #ddd;
		border-radius: 6rpx;
		background-color: #f9f9f9;
	}

	.delete-btn {
		color: #FF3B30;
		font-size: 24rpx;
	}

	.pagination {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx;
		background-color: #fff;
		border-radius: 10rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.pagination-controls {
		display: flex;
		align-items: center;
	}

	.page-btn {
		padding: 10rpx 20rpx;
		margin: 0 10rpx;
		border: 1rpx solid #007AFF;
		border-radius: 6rpx;
		color: #007AFF;
	}

	.page-btn.disabled {
		border-color: #ccc;
		color: #ccc;
	}

	.page-info {
		margin: 0 20rpx;
	}

	.stats {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 10rpx;
		background-color: #fff;
		border-radius: 10rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.total-amount {
		color: #FFD700;
		font-size: 32rpx;
		font-weight: bold;
	}

	.save-btn {
		background-color: #4CD964;
		color: #fff;
		padding: 5rpx 40rpx;
		border-radius: 8rpx;
		
		&:active {
			transform: scale(0.95);
		}
	}

	.table-container {
		background-color: #fff;
		border-radius: 10rpx;
		overflow: hidden;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
	}

	.table-scroll {
		width: 100%;
	}

	.table-wrapper {
		min-width: 100%;
	}

	.table-header,
	.table-row {
		display: flex;
		border-bottom: 1rpx solid #eee;
		min-width: 100%;
	}

	.table-cell {
		padding: 20rpx 10rpx;
		text-align: center;
		font-size: 24rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
		flex-shrink: 0;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.table-header .table-cell {
		font-weight: bold;
		background-color: #f5f5f5;
	}

	.date-picker {
		padding: 10rpx;
		border: 1rpx solid #ddd;
		border-radius: 6rpx;
		background-color: #f9f9f9;
		font-size: 24rpx;
		text-align: center;
		width: 100%;
	}

	.delete-btn {
		color: #FF3B30;
		font-size: 24rpx;
		padding: 8rpx 16rpx;
		background: rgba(255, 59, 48, 0.1);
		border-radius: 6rpx;
	}
</style>