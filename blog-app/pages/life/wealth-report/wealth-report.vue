<template>
	<view class="home-container">
		<!-- 背景渐变 -->
		<view class="background-gradient"></view>

		<!-- 页面内容 -->
		<view class="content-wrapper">
			<scroll-view class="main-content" scroll-y :show-scrollbar="false" refresher-background="transparent"
				:style="{ height: '100%' }">

				<!-- 时间范围选择 -->
				<view class="time-selector-container glass-card">
					<view class="time-range-selector">
						<view class="radio-group">
							<view class="radio-button" :class="{ active: timeRange === 'total' }"
								@click="timeRange = 'total'; handleTimeRangeChange()">
								<text>年度趋势</text>
							</view>
							<view class="radio-button" :class="{ active: timeRange === 'year' }"
								@click="timeRange = 'year'; handleTimeRangeChange()">
								<text>月度趋势</text>
							</view>
							<view class="radio-button" :class="{ active: timeRange === 'month' }"
								@click="timeRange = 'month'; handleTimeRangeChange()">
								<text>每日趋势</text>
							</view>
							<view class="radio-button" :class="{ active: timeRange === 'day' }"
								@click="timeRange = 'day'; handleTimeRangeChange()">
								<text>自定义</text>
							</view>
						</view>
						<view class="date-selectors">
							<view class="date-selector-group" v-if="timeRange === 'year'">
								<text class="selector-label">分析年份：</text>
								<picker @change="onYearChange" :value="yearIndex" :range="yearOptions"
									range-key="label">
									<view class="picker">
										{{ selectedYear ? `${selectedYear}年` : '请选择年份' }}
									</view>
								</picker>
							</view>

							<view class="date-selector-group" v-if="timeRange === 'month'">
								<text class="selector-label">分析月份：</text>
								<picker @change="onYearChange" :value="yearIndex" :range="yearOptions" range-key="label"
									style="margin-left: 10px;">
									<view class="picker">
										{{ selectedYear ? `${selectedYear}年` : '请选择年份' }}
									</view>
								</picker>
								<picker @change="onMonthChange" :value="monthIndex" :range="monthOptions"
									range-key="label">
									<view class="picker">
										{{ selectedMonth ? `${selectedMonth}月` : '请选择月份' }}
									</view>
								</picker>
							</view>

							<view class="date-selector-group" v-if="timeRange === 'day'">
								<text class="selector-label">分析日期：</text>
								<view class="date-picker-group">
									<picker mode="date" :value="selectedDate[0]" @change="onStartDateChange">
										<view class="date-picker">{{ selectedDate[0] || '开始日期' }}</view>
									</picker>
									<text class="date-separator">至</text>
									<picker mode="date" :value="selectedDate[1]" @change="onEndDateChange">
										<view class="date-picker">{{ selectedDate[1] || '结束日期' }}</view>
									</picker>
								</view>
							</view>
						</view>
					</view>
				</view>

				<!-- 核心指标概览 -->
				<view class="metrics-overview">
					<view class="metric-card glass-card income-card">
						<view class="metric-icon">💰</view>
						<view class="metric-content">
							<view class="metric-label">总收入</view>
							<view class="metric-value positive">
								{{ formatCurrency(metrics.totalIncome) }}
							</view>
							<view class="metric-change" :class="getChangeClass(metrics.incomeChange)">
								<text :class="metrics.incomeChange >= 0 ? 'icon-top' : 'icon-bottom'"></text>
								{{ Math.abs(metrics.incomeChange) }}%
							</view>
						</view>
					</view>

					<view class="metric-card glass-card expense-card">
						<view class="metric-icon">💸</view>
						<view class="metric-content">
							<view class="metric-label">总支出</view>
							<view class="metric-value negative">
								{{ formatCurrency(metrics.totalExpense) }}
							</view>
							<view class="metric-change" :class="getChangeClass(metrics.expenseChange)">
								<text :class="metrics.expenseChange >= 0 ? 'icon-top' : 'icon-bottom'"></text>
								{{ Math.abs(metrics.expenseChange) }}%
							</view>
						</view>
					</view>

					<view class="metric-card glass-card balance-card">
						<view class="metric-icon">⚖️</view>
						<view class="metric-content">
							<view class="metric-label">净结余</view>
							<view class="metric-value" :class="metrics.netBalance >= 0 ? 'positive' : 'negative'">
								{{ formatCurrency(metrics.netBalance) }}
							</view>
							<view class="metric-trend">
								{{ metrics.netBalance >= 0 ? '财务健康' : '需关注' }}
							</view>
						</view>
					</view>

					<view class="metric-card glass-card rate-card">
						<view class="metric-icon">📊</view>
						<view class="metric-content">
							<view class="metric-label">储蓄率</view>
							<view class="metric-value" :class="metrics.savingsRate >= 0 ? 'positive' : 'negative'">
								{{ metrics.savingsRate }}%
							</view>
							<view class="metric-trend" :class="getRateClass(metrics.savingsRate)">
								{{ getRateText(metrics.savingsRate) }}
							</view>
						</view>
					</view>
				</view>

				<!-- 趋势图表 -->
				<view class="trend-chart-section">
					<view class="section-header">
						<view class="section-title">📈 收支趋势分析</view>
						<view class="chart-controls">
							<radio-group v-model="chartType" @change="handleChartType">
								<label class="radio-label">
									<radio value="line" :checked="chartType === 'line'" color="#667eea" />折线图
								</label>
								<label class="radio-label">
									<radio value="column" :checked="chartType === 'column'" color="#667eea" />柱状图
								</label>
							</radio-group>
						</view>
					</view>
					<view class="chart-scroll-container glass-card">
						<scroll-view class="chart-scroll" scroll-x :show-scrollbar="true">
							<view class="chart-container">
								<canvas canvas-id="trendChart" id="trendChart" class="chart-canvas"
									@touchend="tap"></canvas>
							</view>
						</scroll-view>
					</view>
				</view>

				<!-- 收支对比分析 -->
				<view class="comparison-section">
					<view class="section-header">
						<view class="section-title">📊 收支对比</view>
					</view>
					<view class="comparison-content glass-card">
						<view class="comparison-stats">
							<view class="stat-item">
								<view class="stat-label">收入最高</view>
								<view class="stat-value income">
									{{ formatCurrency(comparisonStats.maxIncome.value) }}
								</view>
								<view class="stat-period">{{ comparisonStats.maxIncome.period }}</view>
							</view>
							<view class="stat-item">
								<view class="stat-label">支出最高</view>
								<view class="stat-value expense">
									{{ formatCurrency(comparisonStats.maxExpense.value) }}
								</view>
								<view class="stat-period">{{ comparisonStats.maxExpense.period }}</view>
							</view>
							<view class="stat-item">
								<view class="stat-label">结余最佳</view>
								<view class="stat-value"
									:class="comparisonStats.maxBalance.value >= 0 ? 'positive' : 'expense'">
									{{ formatCurrency(comparisonStats.maxBalance.value) }}
								</view>
								<view class="stat-period">{{ comparisonStats.maxBalance.period }}</view>
							</view>
							<view class="stat-item">
								<view class="stat-label">盈利周期</view>
								<view class="stat-value"
									:class="comparisonStats.profitablePeriods >= 0 ? 'positive' : 'expense'">
									{{ comparisonStats.profitablePeriods }}
								</view>
								<view class="stat-period">个周期</view>
							</view>
						</view>
					</view>
				</view>

				<!-- 详细数据表格 -->
				<view class="data-table-section">
					<view class="section-header">
						<view class="section-title">📋 详细数据</view>
						<view class="table-actions">
							<button class="action-btn" @click="exportData" size="small">导出数据</button>
						</view>
					</view>
					<view class="table-container glass-card">
						<view class="table-header">
							<view class="table-cell" style="width: 220rpx;">
								{{ timeRange === 'year' ? '月份' : timeRange === 'month' ? '日期' : '日期' }}
							</view>
							<view class="table-cell">收入</view>
							<view class="table-cell">支出</view>
							<view class="table-cell">结余</view>
							<view class="table-cell">储蓄率</view>
							<view class="table-cell">趋势</view>
						</view>
						<scroll-view class="table-body" scroll-y>
							<view class="table-row" v-for="(item, index) in tableData" :key="index">
								<view class="table-cell" style="width: 220rpx;">{{ item.period }}</view>
								<view class="table-cell">
									<text class="income-text">{{ formatCurrency(item.income) }}</text>
								</view>
								<view class="table-cell">
									<text class="expense-text">{{ formatCurrency(item.expense) }}</text>
								</view>
								<view class="table-cell">
									<text :class="item.balance >= 0 ? 'positive-text' : 'negative-text'">
										{{ formatCurrency(item.balance) }}
									</text>
								</view>
								<view class="table-cell">
									<text :class="getRateClass(item.savingsRate)">
										{{ item.savingsRate }}%
									</text>
								</view>
								<view class="table-cell">
									<view class="trend-tag" :class="getTrendClass(item.trend)">
										{{ item.trend }}
									</view>
								</view>
							</view>
						</scroll-view>
					</view>
				</view>

				<!-- 财务总结 -->
				<view class="summary-section">
					<view class="section-header">
						<view class="section-title">💡 财务总结与建议</view>
					</view>
					<view class="summary-content">
						<view class="summary-card glass-card" :class="summary.overallStatus">
							<view class="summary-header">
								<view class="status-indicator" :class="summary.overallStatus"></view>
								<view class="summary-title">{{ summary.title }}</view>
							</view>
							<view class="summary-body">
								<text class="summary-desc">{{ summary.description }}</text>
								<view class="recommendations">
									<view class="recommendations-title">优化建议：</view>
									<view class="recommendations-list">
										<view class="recommendation-item"
											v-for="(recommendation, index) in summary.recommendations" :key="index">
											{{ recommendation }}
										</view>
									</view>
								</view>
							</view>
						</view>

						<view class="insights-grid">
							<view class="insight-card glass-card">
								<view class="insight-icon">📅</view>
								<view class="insight-content">
									<view class="insight-title">最佳表现周期</view>
									<view class="insight-text">{{ insights.bestPeriod.period }} - 储蓄率
										{{ insights.bestPeriod.savingsRate }}%
									</view>
								</view>
							</view>

							<view class="insight-card glass-card">
								<view class="insight-icon">⚠️</view>
								<view class="insight-content">
									<view class="insight-title">需要关注</view>
									<view class="insight-text">{{ insights.attentionPeriod.period }} -
										支出较高
									</view>
								</view>
							</view>

							<view class="insight-card glass-card">
								<view class="insight-icon">🎯</view>
								<view class="insight-content">
									<view class="insight-title">下期目标</view>
									<view class="insight-text">储蓄率提升至 {{ insights.nextGoal }}%</view>
								</view>
							</view>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script>
	import CustomNavbar from '@/components/custom-navbar/custom-navbar.vue'
	import CustomTabbar from '@/components/custom-tabbar/custom-tabbar.vue'
	import uCharts from '@qiun/ucharts/u-charts';
	import {
		lifeApi
	} from '@/api/index.js'
	var uChartsInstance = {};
	const handleCreateDatePickerCurrent = () => {
		const currentYear = new Date().getFullYear();
		const years = [];
		const months = [];

		for (let i = currentYear - 5; i <= currentYear; i++) {
			years.push({
				label: `${i}年`,
				value: i
			});
		}

		for (let i = 1; i <= 12; i++) {
			months.push({
				label: `${i}月`,
				value: i
			});
		}

		return {
			years,
			months
		};
	};

	export default {
		components: {
			CustomNavbar,
			CustomTabbar
		},
		name: "WealthReportView",
		data() {
			const currentYear = new Date().getFullYear();
			const currentMonth = new Date().getMonth() + 1;

			return {
				cWidth: 2200,
				cHeight: 600,
				statusBarHeight: 0,
				timeRange: 'month', // 时间范围选择
				selectedYear: currentYear,
				selectedMonth: currentMonth,
				selectedDate: [this.getDateString(-7), this.getDateString(0)],
				yearIndex: 0,
				monthIndex: currentMonth - 1,
				chartType: 'line', // 图表类型
				// 年份和月份选项
				yearOptions: handleCreateDatePickerCurrent().years,
				monthOptions: handleCreateDatePickerCurrent().months,
				// 核心指标
				metrics: {
					totalIncome: 0,
					totalExpense: 0,
					netBalance: 0,
					savingsRate: 0,
					incomeChange: 0,
					expenseChange: 0
				},
				// 对比统计
				comparisonStats: {
					maxIncome: {
						value: 0,
						period: ''
					},
					maxExpense: {
						value: 0,
						period: ''
					},
					maxBalance: {
						value: 0,
						period: ''
					},
					profitablePeriods: 0
				},
				// 表格数据
				tableData: [],
				// 原始数据
				rawData: [],
				// 财务总结
				summary: {
					overallStatus: 'positive',
					title: '',
					description: '',
					recommendations: []
				},
				// 深度洞察
				insights: {
					bestPeriod: {
						period: '',
						savingsRate: 0
					},
					attentionPeriod: {
						period: '',
						reason: ''
					},
					nextGoal: 0
				}
			};
		},
		onReady() {
			uni.hideTabBar();
			this.cWidth = uni.upx2px(2200);
			this.cHeight = uni.upx2px(600);
			this.$nextTick(() => {
				setTimeout(() => {
					this.initCharts();
				}, 300);
			});
			
		},
		onLoad() {
			this.getStatusBarHeight();
			this.loadFinancialData();
			this.$nextTick(() => {
				setTimeout(() => {
					this.initCharts();
				}, 500);
			});
			
		},
		onUnload() {
			// 清理图表实例
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
			//图表类型选择
			handleChartType(e) {
				console.log(e.detail.value)
				this.chartType = e.detail.value;
				this.initCharts();
			},
			// 年份选择变化
			onYearChange(e) {
				const index = e.detail.value;
				this.yearIndex = index;
				this.selectedYear = this.yearOptions[index].value;
				this.loadFinancialData();
			},
			// 月份选择变化
			onMonthChange(e) {
				const index = e.detail.value;
				this.monthIndex = index;
				this.selectedMonth = this.monthOptions[index].value;
				this.loadFinancialData();
			},
			// 开始日期变化
			onStartDateChange(e) {
				this.selectedDate[0] = e.detail.value;
				this.loadFinancialData();
			},
			// 结束日期变化
			onEndDateChange(e) {
				this.selectedDate[1] = e.detail.value;
				this.loadFinancialData();
			},
			// 获取用户记录年份
			async getAvailableYears() {
				try {
					const response = await lifeApi._getAvailableYears(uni.getStorageSync(this.$constants.USER_INFO)
						.id);
					if (response.data) {
						this.yearOptions = []
						for (let i = 0; i < response.data.length; i++) {
							this.yearOptions.push({
								label: `${response.data[i]}年`,
								value: response.data[i]
							})
						}
					}
				} catch (error) {
					console.error('获取年份失败:', error);
				}
			},
			// 格式化金额
			formatToTwoDecimals(number) {
				if (number === null || number === undefined || isNaN(number)) {
					return 0.00;
				}
				const num = typeof number === 'string' ? parseFloat(number) : number;
				return parseFloat(num.toFixed(2));
			},
			// 获取日期字符串
			getDateString(offsetDays) {
				const date = new Date();
				date.setDate(date.getDate() + offsetDays);
				return date.toISOString().split('T')[0];
			},
			// 处理时间范围变化
			handleTimeRangeChange() {
				this.loadFinancialData();
			},
			// 加载财务数据
			async loadFinancialData() {
				// 获取原始数据
				await this.getRawData();
				await this.getAvailableYears();

				this.processFinancialData();
				this.calculateMetrics();
				this.generateSummary();
				this.updateCharts();
			},
			// 构建查询参数
			buildQueryParams() {
				let params =
					`userId=${uni.getStorageSync('userInfo').id}&timeRange=${this.timeRange}&year=${this.selectedYear}&month=${this.selectedMonth}&startDate=${this.selectedDate[0]}&endDate=${this.selectedDate[1]}`
				return params;
			},
			// 获取原始数据
			async getRawData() {
				try {
					const params = this.buildQueryParams();
					const response = await lifeApi._getRawData(params);
					this.rawData = response || [];
				} catch (error) {
					console.error('获取数据失败:', error);
					this.rawData = [];
				}
			},
			// 处理财务数据
			processFinancialData() {
				this.tableData = [];
				if (this.timeRange === 'year') {
					this.processYearlyData();
				} else if (this.timeRange === 'month') {
					this.processMonthlyData();
				} else if (this.timeRange === 'day') {
					this.processDailyData();
				} else {
					this.processTotalData();
				}
			},
			// 处理整体数据
			processTotalData() {
				const monthlyData = {};
				for (let i = 1; i <= this.yearOptions.length; i++) {
					monthlyData[this.yearOptions[i - 1].value] = {
						period: this.yearOptions[i - 1].label,
						income: 0,
						expense: 0,
						balance: 0,
						savingsRate: 0,
						trend: '稳定'
					};
				}

				this.rawData.forEach(record => {
					const recordDate = new Date(record.recordTime);
					const year = recordDate.getFullYear();
					if (record.type === '收入') {
						monthlyData[year].income += parseFloat(record.cost);
					} else if (record.type === '支出') {
						monthlyData[year].expense += parseFloat(record.cost);
					}
				});

				Object.values(monthlyData).forEach(month => {
					month.income = this.formatToTwoDecimals(month.income);
					month.expense = this.formatToTwoDecimals(month.expense);
					month.balance = this.formatToTwoDecimals(month.income - month.expense);
					month.savingsRate = month.income > 0 ?
						((month.balance / month.income) * 100).toFixed(1) : 0;
					month.trend = month.balance > 0 ? '上升' : month.balance < 0 ? '下降' : '稳定';
				});

				this.tableData = Object.values(monthlyData);
			},
			// 处理年度数据
			processYearlyData() {
				const monthlyData = {};
				for (let i = 1; i <= 12; i++) {
					monthlyData[i] = {
						period: `${i}月`,
						income: 0,
						expense: 0,
						balance: 0,
						savingsRate: 0,
						trend: '稳定'
					};
				}

				this.rawData.forEach(record => {
					const recordDate = new Date(record.recordTime);
					const month = recordDate.getMonth() + 1;
					const year = recordDate.getFullYear();
					if (year === this.selectedYear && monthlyData[month]) {
						if (record.type === '收入') {
							monthlyData[month].income += parseFloat(record.cost);
						} else if (record.type === '支出') {
							monthlyData[month].expense += parseFloat(record.cost);
						}
					}
				});

				Object.values(monthlyData).forEach(month => {
					month.income = this.formatToTwoDecimals(month.income);
					month.expense = this.formatToTwoDecimals(month.expense);
					month.balance = this.formatToTwoDecimals(month.income - month.expense);
					month.savingsRate = month.income > 0 ?
						((month.balance / month.income) * 100).toFixed(1) : 0;
					month.trend = month.balance > 0 ? '上升' : month.balance < 0 ? '下降' : '稳定';
				});

				this.tableData = Object.values(monthlyData);
			},
			// 处理月度数据
			processMonthlyData() {
				const dailyData = {};
				const daysInMonth = new Date(this.selectedYear, this.selectedMonth, 0).getDate();

				for (let i = 1; i <= daysInMonth; i++) {
					dailyData[i] = {
						period: `${i}号`,
						income: 0,
						expense: 0,
						balance: 0,
						savingsRate: 0,
						trend: '稳定'
					};
				}

				this.rawData.forEach(record => {
					const recordDate = new Date(record.recordTime);
					const day = recordDate.getDate();
					const month = recordDate.getMonth() + 1;
					const year = recordDate.getFullYear();
					if (year === this.selectedYear && month === this.selectedMonth && dailyData[day]) {
						if (record.type === '收入') {
							dailyData[day].income += parseFloat(record.cost);
						} else if (record.type === '支出') {
							dailyData[day].expense += parseFloat(record.cost);
						}
					}
				});

				Object.values(dailyData).forEach(day => {
					day.income = this.formatToTwoDecimals(day.income);
					day.expense = this.formatToTwoDecimals(day.expense);
					day.balance = this.formatToTwoDecimals(day.income - day.expense);
					day.savingsRate = day.income > 0 ?
						((day.balance / day.income) * 100).toFixed(1) : 0;
					day.trend = day.balance > 0 ? '上升' : day.balance < 0 ? '下降' : '稳定';
				});

				this.tableData = Object.values(dailyData);
			},
			// 处理每日数据
			processDailyData() {
				const dailyData = {};

				this.rawData.forEach(record => {
					const dateStr = record.recordTime;
					if (!dailyData[dateStr]) {
						dailyData[dateStr] = {
							period: dateStr,
							income: 0,
							expense: 0,
							balance: 0,
							savingsRate: 0,
							trend: '稳定'
						};
					}

					if (record.type === '收入') {
						dailyData[dateStr].income += parseFloat(record.cost);
					} else if (record.type === '支出') {
						dailyData[dateStr].expense += parseFloat(record.cost);
					}
				});

				Object.values(dailyData).forEach(day => {
					day.income = this.formatToTwoDecimals(day.income);
					day.expense = this.formatToTwoDecimals(day.expense);
					day.balance = this.formatToTwoDecimals(day.income - day.expense);
					day.savingsRate = day.income > 0 ?
						((day.balance / day.income) * 100).toFixed(1) : 0;
					day.trend = day.balance > 0 ? '上升' : day.balance < 0 ? '下降' : '稳定';
				});

				this.tableData = Object.values(dailyData).sort((a, b) => {
					return new Date(a.period) - new Date(b.period);
				});
			},
			// 计算核心指标
			calculateMetrics() {
				const totalIncome = this.formatToTwoDecimals(this.tableData.reduce((sum, item) => sum + item.income, 0));
				const totalExpense = this.formatToTwoDecimals(this.tableData.reduce((sum, item) => sum + item.expense, 0));
				const netBalance = this.formatToTwoDecimals(totalIncome - totalExpense);
				const savingsRate = totalIncome > 0 ? (netBalance / totalIncome) * 100 : 0;

				const incomeChange = (Math.random() * 20 - 10).toFixed(1);
				const expenseChange = (Math.random() * 15 - 5).toFixed(1);

				this.metrics = {
					totalIncome,
					totalExpense,
					netBalance,
					savingsRate: savingsRate.toFixed(1),
					incomeChange: parseFloat(incomeChange),
					expenseChange: parseFloat(expenseChange)
				};

				this.calculateComparisonStats();
			},
			// 计算对比统计
			calculateComparisonStats() {
				if (this.tableData.length === 0) return;

				let maxIncome = {
					value: -Infinity,
					period: ''
				};
				let maxExpense = {
					value: -Infinity,
					period: ''
				};
				let maxBalance = {
					value: -Infinity,
					period: ''
				};
				let profitablePeriods = 0;

				this.tableData.forEach(item => {
					if (item.income > maxIncome.value) {
						maxIncome = {
							value: item.income,
							period: item.period
						};
					}
					if (item.expense > maxExpense.value) {
						maxExpense = {
							value: item.expense,
							period: item.period
						};
					}
					if (item.balance > maxBalance.value) {
						maxBalance = {
							value: item.balance,
							period: item.period
						};
					}
					if (item.balance > 0) {
						profitablePeriods++;
					}
				});

				this.comparisonStats = {
					maxIncome,
					maxExpense,
					maxBalance,
					profitablePeriods
				};

				this.calculateInsights();
			},
			// 计算深度洞察
			calculateInsights() {
				let bestPeriod = {
					period: '',
					savingsRate: -Infinity
				};
				let attentionPeriod = {
					period: '',
					reason: ''
				};

				this.tableData.forEach(item => {
					if (item.savingsRate > bestPeriod.savingsRate) {
						bestPeriod = {
							period: item.period,
							savingsRate: item.savingsRate
						};
					}

					if (item.expense > this.metrics.totalExpense / this.tableData.length * 1.5) {
						attentionPeriod = {
							period: item.period,
							reason: '支出异常偏高'
						};
					}
				});

				const nextGoal = Math.min(parseFloat(bestPeriod.savingsRate) + 5, 30).toFixed(1);

				this.insights = {
					bestPeriod,
					attentionPeriod,
					nextGoal
				};
			},
			// 生成财务总结
			generateSummary() {
				const savingsRate = parseFloat(this.metrics.savingsRate);
				let overallStatus, title, description;
				const recommendations = [];

				if (savingsRate >= 20) {
					overallStatus = 'positive';
					title = '财务状况优秀';
					description = '您的储蓄率保持在健康水平，财务状况良好。继续保持当前的收支管理习惯。';
					recommendations.push('考虑将部分储蓄用于投资增值');
					recommendations.push('建立长期财务目标规划');
				} else if (savingsRate >= 10) {
					overallStatus = 'neutral';
					title = '财务状况良好';
					description = '您的财务状况基本健康，但仍有提升空间。建议关注支出结构优化。';
					recommendations.push('分析并减少非必要支出');
					recommendations.push('设立月度预算控制机制');
				} else if (savingsRate >= 0) {
					overallStatus = 'warning';
					title = '需要关注';
					description = '您的储蓄率偏低，需要关注支出管理。建议制定详细的预算计划。';
					recommendations.push('建立紧急备用金账户');
					recommendations.push('跟踪每日支出明细');
				} else {
					overallStatus = 'negative';
					title = '需要改善';
					description = '您的支出超过收入，财务状况需要立即改善。建议重新评估收支结构。';
					recommendations.push('立即削减非必要支出');
					recommendations.push('寻求增加收入的途径');
					recommendations.push('制定债务偿还计划');
				}

				recommendations.push('定期检查财务报告');
				recommendations.push('建立长期储蓄目标');

				this.summary = {
					overallStatus,
					title,
					description,
					recommendations
				};
			},
			// 初始化图表
			initCharts() {
				this.updateCharts();
			},
			// 更新图表
			updateCharts() {
				const periods = this.tableData.map(item => item.period);
				const incomes = this.tableData.map(item => item.income);
				const expenses = this.tableData.map(item => item.expense);
				const balances = this.tableData.map(item => item.balance);
				let res = {
					categories: periods,
					series: [{
							name: "收入",
							data: incomes,
							color: '#2ecc71'
						},
						{
							name: "支出",
							data: expenses,
							color: '#e74c3c'
						},
						{
							name: '结余',
							type: this.chartType,
							data: balances,
							smooth: true,
							lineStyle: {
								color: '#3498db',
								width: 2
							},
							itemStyle: {
								color: '#3498db'
							}
						}
					]
				};
				this.updateTrendChart('trendChart', res);
			},
			// 更新趋势图表
			updateTrendChart(id, data) {
				const ctx = uni.createCanvasContext(id, this);
				uChartsInstance[id] = new uCharts({
					type: this.chartType,
					context: ctx,
					width: this.cWidth,
					height: this.cHeight,
					categories: data.categories,
					series: data.series,
					animation: true,
					background: "#000",
					padding: [15, 5, 5, 5],
					enableScroll: false,
					fontSize: 14,
					fontColor: "#000",
					legend: {
						data: ['收入', '支出', '结余'],
						textStyle: {
							color: '#a0aec0'
						},
						top: 10
					},
					xAxis: {
						disableGrid: true
					},
					yAxis: {
						type: 'value',

						axisLine: {
							lineStyle: {
								color: 'rgba(255, 255, 255, 0.3)'
							}
						},
						axisLabel: {
							color: '#61B9C8',
							formatter: (value) => '¥' + value.toLocaleString('zh-CN')
						},
					},
					extra: this.chartType === 'line' ? {
						line: {
							type: "curve",
							width: 2,
							activeType: "hollow"
						}
					} : {
						column: {
							type: "group",
							width: 30,
							activeBgColor: "#000000",
							activeBgOpacity: 0.08
						}
					}
				});
			},
			tap(e) {
				uChartsInstance[e.target.id].touchLegend(e);
				uChartsInstance[e.target.id].showToolTip(e);
			},
			// 导出数据
			exportData() {
				uni.showToast({
					title: '数据导出功能准备中...',
					icon: 'none'
				});
			},
			// 格式化货币
			formatCurrency(value) {
				if (value === null || value === undefined) return '¥0.00';
				return '¥' + parseFloat(value).toLocaleString('zh-CN', {
					minimumFractionDigits: 2,
					maximumFractionDigits: 2
				});
			},
			// 获取变化样式类
			getChangeClass(change) {
				return change >= 0 ? 'positive' : 'negative';
			},
			// 获取比率样式类
			getRateClass(rate) {
				if (rate >= 20) return 'rate-excellent';
				if (rate >= 10) return 'rate-good';
				return 'rate-poor';
			},
			// 获取比率文本
			getRateText(rate) {
				if (rate >= 20) return '优秀';
				if (rate >= 10) return '良好';
				return '需改善';
			},
			// 获取趋势样式类
			getTrendClass(trend) {
				if (trend === '上升') return 'trend-up';
				if (trend === '下降') return 'trend-down';
				return 'trend-stable';
			}
		}
	};
</script>

<style lang="scss" scoped>
	@import "@/scss/variables.scss";

	.home-container {
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

	.content-wrapper {
		// flex: 1;
		position: relative;
		z-index: 2;
		box-sizing: border-box;
		overflow: hidden;
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

	.main-content {
		box-sizing: border-box;
		overflow: hidden;
		background: transparent;
		padding: 0 30rpx 120rpx;
	}

	/* 时间选择器 */
	.time-selector-container {
		margin-bottom: 40rpx;
		padding: 40rpx;

		.time-range-selector {
			display: flex;
			flex-direction: column;
			gap: 30rpx;

			.radio-group {
				display: flex;
				flex-wrap: wrap;
				gap: 20rpx;

				.radio-button {
					padding: 16rpx 32rpx;
					background: rgba(255, 255, 255, 0.8);
					border-radius: 12rpx;
					border: 1px solid rgba($primary-color, 0.3);
					color: $text-secondary;
					font-size: 28rpx;
					transition: all 0.3s ease;
					font-weight: 500;

					&.active {
						background: rgba($primary-color, 0.1);
						color: $primary-color;
						border-color: $primary-color;
						box-shadow: $shadow-light;
					}

					text {
						color: inherit;
					}
				}
			}

			.date-selectors {
				display: flex;
				flex-direction: column;
				gap: 30rpx;

				.date-selector-group {
					display: flex;
					align-items: center;
					gap: 20rpx;
					flex-wrap: wrap;

					.selector-label {
						color: $text-secondary;
						font-size: 28rpx;
						white-space: nowrap;
						font-weight: 500;
					}

					.picker {
						padding: 16rpx 32rpx;
						background: rgba(255, 255, 255, 0.8);
						border-radius: 12rpx;
						border: 1px solid rgba($primary-color, 0.3);
						color: $primary-color;
						min-width: 200rpx;
						text-align: center;
						font-weight: 500;
					}

					.date-picker-group {
						display: flex;
						align-items: center;
						gap: 20rpx;

						.date-picker {
							padding: 16rpx 32rpx;
							background: rgba(255, 255, 255, 0.8);
							border-radius: 12rpx;
							border: 1px solid rgba($primary-color, 0.3);
							color: $primary-color;
							min-width: 200rpx;
							text-align: center;
							font-weight: 500;
						}

						.date-separator {
							color: $text-secondary;
						}
					}
				}
			}
		}
	}

	/* 核心指标概览 */
	.metrics-overview {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 30rpx;
		margin-bottom: 40rpx;

		.metric-card {
			padding: 40rpx 30rpx;
			display: flex;
			align-items: center;
			gap: 30rpx;
			transition: all 0.3s ease;

			&:active {
				transform: translateY(-5rpx);
				box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.15);
			}

			.metric-icon {
				font-size: 60rpx;
				flex-shrink: 0;
			}

			.metric-content {
				flex: 1;

				.metric-label {
					color: $text-secondary;
					font-size: 28rpx;
					margin-bottom: 16rpx;
					font-weight: 500;
				}

				.metric-value {
					font-size: 36rpx;
					font-weight: 700;
					margin-bottom: 12rpx;

					&.positive {
						color: $success-color;
					}

					&.negative {
						color: $error-color;
					}
				}

				.metric-change {
					font-size: 24rpx;
					font-weight: 600;
					padding: 4rpx 16rpx;
					border-radius: 20rpx;
					display: inline-flex;
					align-items: center;
					gap: 8rpx;

					&.positive {
						background: rgba($success-color, 0.1);
						color: $success-color;
					}

					&.negative {
						background: rgba($error-color, 0.1);
						color: $error-color;
					}

					.icon-top,
					.icon-bottom {
						display: inline-block;
						width: 0;
						height: 0;
						border-left: 8rpx solid transparent;
						border-right: 8rpx solid transparent;
					}

					.icon-top {
						border-bottom: 12rpx solid $success-color;
					}

					.icon-bottom {
						border-top: 12rpx solid $error-color;
					}
				}

				.metric-trend {
					font-size: 24rpx;
					color: $text-light;

					&.rate-excellent {
						color: $success-color;
					}

					&.rate-good {
						color: $warning-color;
					}

					&.rate-poor {
						color: $error-color;
					}
				}
			}
		}
	}

	/* 趋势图表 */
	.trend-chart-section {
		margin-bottom: 40rpx;

		.section-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 30rpx;

			.section-title {
				color: $text-primary;
				font-size: 36rpx;
				font-weight: 600;
				display: flex;
				align-items: center;
				gap: 16rpx;
			}

			.chart-controls {
				.radio-label {
					margin-left: 20rpx;
					font-size: 28rpx;
					color: '#333333';
				}
			}
		}

		.chart-scroll-container {
			.chart-scroll {
				width: 100%;
				height: 650rpx;

				.chart-container {
					width: 2200rpx;
					height: 600rpx;
					padding: 40rpx;

					.chart-canvas {
						width: 2200rpx;
						height: 600rpx;
					}
				}
			}
		}
	}

	/* 收支对比分析 */
	.comparison-section {
		margin-bottom: 40rpx;

		.section-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 30rpx;

			.section-title {
				color: $text-primary;
				font-size: 36rpx;
				font-weight: 600;
				display: flex;
				align-items: center;
				gap: 16rpx;
			}
		}

		.comparison-content {
			padding: 40rpx;

			.comparison-stats {
				display: grid;
				grid-template-columns: repeat(2, 1fr);
				gap: 30rpx;

				.stat-item {
					text-align: center;
					padding: 30rpx;

					.stat-label {
						font-size: 24rpx;
						color: $text-secondary;
						margin-bottom: 10rpx;
						font-weight: 500;
					}

					.stat-value {
						font-size: 32rpx;
						font-weight: 700;
						margin-bottom: 10rpx;

						&.income {
							color: $success-color;
						}

						&.expense {
							color: $error-color;
						}

						&.positive {
							color: $success-color;
						}
					}

					.stat-period {
						font-size: 24rpx;
						color: $text-light;
					}
				}
			}
		}
	}

	/* 详细数据表格 */
	.data-table-section {
		margin-bottom: 40rpx;

		.section-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 30rpx;

			.section-title {
				color: $text-primary;
				font-size: 36rpx;
				font-weight: 600;
				display: flex;
				align-items: center;
				gap: 16rpx;
			}

			.table-actions {
				.action-btn {
					background: #24b166;
					color: #333333;
					border: 1px solid rgba($primary-color, 0.3);
					border-radius: 12rpx;
					padding: 5rpx 32rpx;
					font-size: 28rpx;
					font-weight: 500;

					&:active {
						background: rgba($primary-color, 0.2);
					}
				}
			}
		}

		.table-container {
			padding: 0;

			.table-header {
				display: flex;
				background: rgba($primary-color, 0.05);
				color: $text-primary;
				font-weight: 600;
				border-radius: 12rpx 12rpx 0 0;

				.table-cell {
					padding: 24rpx 20rpx;
					text-align: center;
					flex: 1;
					border-right: 1px solid rgba($primary-color, 0.1);

					&:last-child {
						border-right: none;
					}
				}
			}

			.table-body {
				max-height: 800rpx;

				.table-row {
					display: flex;
					border-bottom: 1px solid rgba($primary-color, 0.1);

					&:last-child {
						border-bottom: none;
					}

					.table-cell {
						padding: 24rpx 20rpx;
						text-align: center;
						flex: 1;
						color: $text-primary;
						border-right: 1px solid rgba($primary-color, 0.1);

						&:last-child {
							border-right: none;
						}
					}
				}
			}

			.income-text {
				color: $success-color;
				font-weight: 600;
			}

			.expense-text {
				color: $error-color;
				font-weight: 600;
			}

			.positive-text {
				color: $success-color;
				font-weight: 600;
			}

			.negative-text {
				color: $error-color;
				font-weight: 600;
			}

			.rate-excellent {
				color: $success-color;
				font-weight: 600;
			}

			.rate-good {
				color: $warning-color;
				font-weight: 600;
			}

			.rate-poor {
				color: $error-color;
				font-weight: 600;
			}

			.trend-tag {
				padding: 8rpx 16rpx;
				border-radius: 20rpx;
				font-size: 24rpx;
				font-weight: 600;

				&.trend-up {
					background: rgba($success-color, 0.1);
					color: $success-color;
				}

				&.trend-down {
					background: rgba($error-color, 0.1);
					color: $error-color;
				}

				&.trend-stable {
					background: rgba($info-color, 0.1);
					color: $info-color;
				}
			}
		}
	}

	/* 财务总结 */
	.summary-section {
		margin-bottom: 40rpx;

		.section-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 30rpx;

			.section-title {
				color: $text-primary;
				font-size: 36rpx;
				font-weight: 600;
				display: flex;
				align-items: center;
				gap: 16rpx;
			}
		}

		.summary-content {
			display: flex;
			flex-direction: column;
			gap: 30rpx;

			.summary-card {
				padding: 40rpx 30rpx;

				&.positive {
					border-left: 6rpx solid $success-color;
				}

				&.neutral {
					border-left: 6rpx solid $warning-color;
				}

				&.warning {
					border-left: 6rpx solid $warning-color;
				}

				&.negative {
					border-left: 6rpx solid $error-color;
				}

				.summary-header {
					display: flex;
					align-items: center;
					gap: 20rpx;
					margin-bottom: 30rpx;

					.status-indicator {
						width: 24rpx;
						height: 24rpx;
						border-radius: 50%;

						&.positive {
							background: $success-color;
						}

						&.neutral {
							background: $warning-color;
						}

						&.warning {
							background: $warning-color;
						}

						&.negative {
							background: $error-color;
						}
					}

					.summary-title {
						color: $text-primary;
						font-size: 32rpx;
						font-weight: 600;
					}
				}

				.summary-body {
					.summary-desc {
						color: $text-secondary;
						line-height: 1.6;
						margin-bottom: 30rpx;
						display: block;
						font-size: 28rpx;
					}

					.recommendations {
						.recommendations-title {
							color: $text-primary;
							margin-bottom: 20rpx;
							font-size: 28rpx;
							font-weight: 600;
						}

						.recommendations-list {
							.recommendation-item {
								color: $text-secondary;
								line-height: 1.6;
								margin-bottom: 10rpx;
								padding-left: 40rpx;
								position: relative;
								font-size: 26rpx;

								&:before {
									content: "•";
									position: absolute;
									left: 20rpx;
									color: $primary-color;
								}

								&:last-child {
									margin-bottom: 0;
								}
							}
						}
					}
				}
			}

			.insights-grid {
				display: grid;
				grid-template-columns: 1fr;
				gap: 30rpx;

				.insight-card {
					padding: 30rpx;
					display: flex;
					align-items: center;
					gap: 30rpx;

					.insight-icon {
						font-size: 48rpx;
						flex-shrink: 0;
					}

					.insight-content {
						flex: 1;

						.insight-title {
							color: $text-primary;
							font-size: 28rpx;
							font-weight: 600;
							margin-bottom: 10rpx;
						}

						.insight-text {
							color: $text-secondary;
							font-size: 24rpx;
							line-height: 1.4;
						}
					}
				}
			}
		}
	}

	/* 响应式设计 */
	@media (max-width: 750px) {
		.metrics-overview {
			grid-template-columns: 1fr;
		}

		.comparison-content .comparison-stats {
			grid-template-columns: 1fr;
		}
	}

	// 动画效果
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

	// 为各个部分添加动画
	.time-selector-container,
	.metrics-overview,
	.trend-chart-section,
	.comparison-section,
	.data-table-section,
	.summary-section {
		animation: slideInUp 0.6s ease-out;
	}

	.metrics-overview {
		animation-delay: 0.1s;
	}

	.trend-chart-section {
		animation-delay: 0.2s;
	}

	.comparison-section {
		animation-delay: 0.3s;
	}

	.data-table-section {
		animation-delay: 0.4s;
	}

	.summary-section {
		animation-delay: 0.5s;
	}
</style>