<template>
	<view class="financial-analysis-container">
		<view class="main-content">
			<!-- 顶部标题和筛选 -->
			<view class="header-section heritage-card">
				<view class="filter-section">
					<!-- 收支情况选择 -->
					<view class="filter-item">
						<text class="filter-label">收支情况：</text>
						<radio-group @change="onTypeChange" class="radio-group">
							<label class="radio-item">
								<radio value="0" :checked="expenseData.isExpenses === '0'" color="#8B4513" />
								<text class="radio-text">支出</text>
							</label>
							<label class="radio-item">
								<radio value="1" :checked="expenseData.isExpenses === '1'" color="#8B4513" />
								<text class="radio-text">收入</text>
							</label>
						</radio-group>
					</view>

					<!-- 时间筛选 -->
					<view class="filter-item">
						<text class="filter-label">时间筛选：</text>
						<view class="time-filters-row">
							<view class="time-picker-group">
								<text class="time-label">年</text>
								<picker mode="date" fields="year" :value="expenseData.selectYear + ''"
									@change="onYearChange" class="time-picker">
									<view class="picker-text">{{ expenseData.selectYear }}</view>
								</picker>
							</view>

							<view class="time-picker-group">
								<text class="time-label">月</text>
								<picker mode="date" fields="month" :value="currentMonthValue" @change="onMonthChange"
									class="time-picker">
									<view class="picker-text">{{ expenseData.selectMonth + 1 }}</view>
								</picker>
							</view>

							<view class="time-picker-group">
								<text class="time-label">日</text>
								<picker mode="date" fields="day" :value="currentDayValue" @change="onDayChange"
									class="time-picker">
									<view class="picker-text">{{ expenseData.selectDay }}</view>
								</picker>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 选项卡 -->
			<view class="tab-section heritage-card">
				<view class="tab-container">
					<view v-for="tab in tabs" :key="tab.value" class="tab-item"
						:class="{ 'active': activeTab === tab.value }" @tap="switchTab(tab.value)">
						<text class="tab-icon">{{ tab.icon }}</text>
						<text class="tab-text">{{ tab.label }}</text>
						<view class="tab-indicator" v-if="activeTab === tab.value"></view>
					</view>
				</view>
			</view>

			<!-- 详情占比内容 -->
			<view class="content-section" v-if="activeTab === 'proportion'">
				<scroll-view scroll-y class="content-scroll" :style="{ height: scrollHeight + 'px' }">
					<view class="charts-container">
						<!-- 年度占比 -->
						<view class="chart-card heritage-card">
							<view class="chart-header">
								<text class="chart-title">{{ expenseData.selectYear }}年{{ spanText }}占比</text>
								<view class="chart-actions">
									<text class="total-amount">总额: {{ yearTotal }}元</text>
								</view>
							</view>
							<view class="chart-content">
								<canvas canvas-id="yearChart" id="yearChart" class="chart-canvas"
									:style="{ width: chartWidth + 'px', height: chartHeight + 'px' }"
									@touchend="tap"></canvas>
							</view>
						</view>

						<!-- 月度占比 -->
						<view class="chart-card heritage-card">
							<view class="chart-header">
								<text class="chart-title">{{ expenseData.selectMonth + 1 }}月{{ spanText }}占比</text>
								<view class="chart-actions">
									<text class="total-amount">总额: {{ monthTotal }}元</text>
								</view>
							</view>
							<view class="chart-content">
								<canvas canvas-id="monthChart" id="monthChart" class="chart-canvas"
									:style="{ width: chartWidth + 'px', height: chartHeight + 'px' }"
									@touchend="tap"></canvas>
							</view>
						</view>

						<!-- 日度占比 -->
						<view class="chart-card heritage-card">
							<view class="chart-header">
								<text class="chart-title">{{ expenseData.selectDay }}日{{ spanText }}占比</text>
								<view class="chart-actions">
									<text class="total-amount">总额: {{ dayTotal }}元</text>
								</view>
							</view>
							<view class="chart-content">
								<canvas canvas-id="dayChart" id="dayChart" class="chart-canvas"
									:style="{ width: chartWidth + 'px', height: chartHeight + 'px' }"
									@touchend="tap"></canvas>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>

			<!-- 排行榜内容 -->
			<view class="content-section" v-if="activeTab === 'ranking'">
				<scroll-view scroll-y class="content-scroll" :style="{ height: scrollHeight + 'px' }">
					<view class="rank-content">
						<!-- 排行榜头部 -->
						<view class="rank-header heritage-card">
							<view class="rank-title">
								<text class="title-text">{{ spanText }}排行榜</text>
								<text class="title-desc">按金额从高到低排序</text>
							</view>
							<view class="rank-stats">
								<view class="stat-item">
									<text class="stat-value">{{ totalItems }}</text>
									<text class="stat-label">总项数</text>
								</view>
								<view class="stat-item">
									<text class="stat-value">{{ totalAmount.toFixed(2) }}</text>
									<text class="stat-label">总金额(元)</text>
								</view>
							</view>
						</view>

						<!-- 时间维度选择 -->
						<view class="dimension-tabs heritage-card">
							<view class="dimension-container">
								<view v-for="dimension in dimensions" :key="dimension.value" class="dimension-item"
									:class="{ 'active': activeDimension === dimension.value }"
									@tap="switchDimension(dimension.value)">
									<text class="dimension-text">{{ dimension.label }}</text>
									<view class="dimension-indicator" v-if="activeDimension === dimension.value"></view>
								</view>
							</view>
						</view>

						<!-- 排行榜列表 -->
						<view class="rank-list">
							<!-- 年度排行榜 -->
							<view v-if="activeDimension === 'year'" class="rank-section">
								<view v-for="(item, index) in rankData.year" :key="index"
									class="rank-item heritage-card" :class="getRankClass(index)">
									<view class="rank-number">
										<text class="rank-index">{{ index + 1 }}</text>
										<view class="rank-medal" v-if="index < 3">
											<text class="medal-icon">{{ getMedalIcon(index) }}</text>
										</view>
									</view>

									<view class="rank-info">
										<text class="item-name">{{ item.name }}</text>
										<text class="item-category">{{ item.category || '通用' }}</text>
									</view>

									<view class="rank-amount">
										<text class="amount-value">{{ item.value.toFixed(2) }}元</text>
										<text
											class="amount-percentage">{{ ((item.value / yearTotal) * 100).toFixed(1) }}%</text>
									</view>

									<view class="rank-progress">
										<view class="progress-bar"
											:style="{ width: ((item.value / yearTotal) * 100) + '%' }"></view>
									</view>
								</view>
							</view>

							<!-- 月度排行榜 -->
							<view v-if="activeDimension === 'month'" class="rank-section">
								<view v-for="(item, index) in rankData.month" :key="index"
									class="rank-item heritage-card" :class="getRankClass(index)">
									<view class="rank-number">
										<text class="rank-index">{{ index + 1 }}</text>
										<view class="rank-medal" v-if="index < 3">
											<text class="medal-icon">{{ getMedalIcon(index) }}</text>
										</view>
									</view>

									<view class="rank-info">
										<text class="item-name">{{ item.name }}</text>
										<text class="item-category">{{ item.category || '通用' }}</text>
									</view>

									<view class="rank-amount">
										<text class="amount-value">{{ item.value.toFixed(2) }}元</text>
										<text
											class="amount-percentage">{{ ((item.value / monthTotal) * 100).toFixed(1) }}%</text>
									</view>

									<view class="rank-progress">
										<view class="progress-bar"
											:style="{ width: ((item.value / monthTotal) * 100) + '%' }"></view>
									</view>
								</view>
							</view>

							<!-- 日度排行榜 -->
							<view v-if="activeDimension === 'day'" class="rank-section">
								<view v-for="(item, index) in rankData.day" :key="index" class="rank-item heritage-card"
									:class="getRankClass(index)">
									<view class="rank-number">
										<text class="rank-index">{{ index + 1 }}</text>
										<view class="rank-medal" v-if="index < 3">
											<text class="medal-icon">{{ getMedalIcon(index) }}</text>
										</view>
									</view>

									<view class="rank-info">
										<text class="item-name">{{ item.name }}</text>
										<text class="item-category">{{ item.category || '通用' }}</text>
									</view>

									<view class="rank-amount">
										<text class="amount-value">{{ item.value.toFixed(2) }}元</text>
										<text
											class="amount-percentage">{{ ((item.value / dayTotal) * 100).toFixed(1) }}%</text>
									</view>

									<view class="rank-progress">
										<view class="progress-bar"
											:style="{ width: ((item.value / dayTotal) * 100) + '%' }"></view>
									</view>
								</view>
							</view>
						</view>

						<!-- 空状态 -->
						<view class="empty-state heritage-card" v-if="currentRankData.length === 0">
							<text class="empty-icon">📊</text>
							<text class="empty-text">暂无排行榜数据</text>
							<text class="empty-desc">当前时间段内没有{{ spanText }}记录</text>
						</view>
					</view>
				</scroll-view>
			</view>

			<!-- 底部操作 -->
			<view class="bottom-actions">
				<button class="action-btn refresh-btn" @tap="refreshData">刷新数据</button>
				<button class="action-btn back-btn" @tap="handleBack">返回首页</button>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		lifeApi
	} from "@/api/index.js";
	import {
		getDaysInMonth
	} from '@/common/formatDate.js';
	import uCharts from '@qiun/ucharts/u-charts';
	var uChartsInstance = {};
	export default {
		name: "FinancialAnalysis",
		data() {
			const now = new Date();
			return {
				scrollHeight: 600,
				chartWidth: 300,
				chartHeight: 300,
				activeTab: 'proportion',
				activeDimension: 'year',
				tabs: [{
						label: '详情占比',
						value: 'proportion',
						icon: '📊'
					},
					{
						label: '排行榜',
						value: 'ranking',
						icon: '🏆'
					}
				],
				dimensions: [{
						label: '年度排行',
						value: 'year'
					},
					{
						label: '月度排行',
						value: 'month'
					},
					{
						label: '日度排行',
						value: 'day'
					}
				],
				spanText: '支出',
				expenseData: {
					isExpenses: "0",
					selectYear: now.getFullYear(),
					selectMonth: now.getMonth(),
					selectDay: now.getDate(),
					queryId: ''
				},
				// 详情占比数据
				chartData: {
					year: [],
					month: [],
					day: []
				},
				yearTotal: 0,
				monthTotal: 0,
				dayTotal: 0,
				// 排行榜数据
				rankData: {
					year: [],
					month: [],
					day: []
				},
				totalAmount: 0,
				totalItems: 0
			};
		},
		computed: {
			currentMonthValue() {
				const year = this.expenseData.selectYear;
				const month = this.expenseData.selectMonth + 1;
				return `${year}-${month.toString().padStart(2, '0')}`;
			},
			currentDayValue() {
				const year = this.expenseData.selectYear;
				const month = this.expenseData.selectMonth + 1;
				const day = this.expenseData.selectDay;
				return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
			},
			currentRankData() {
				return this.rankData[this.activeDimension] || [];
			}
		},
		onLoad(options) {
			// 获取传递的参数
			if (options.type) {
				this.expenseData.isExpenses = options.type;
				this.spanText = options.type === '0' ? '支出' : '收入';
			}

			if (options.tab) {
				this.activeTab = options.tab;
			}

			// 安全获取用户ID
			try {
				this.expenseData.queryId = uni.getStorageSync(this.$constants.USER_INFO).id;
			} catch (error) {
				console.error('获取用户信息失败:', error);
			}

			setTimeout(() => {
				this.init();
			}, 100);
		},
		onReady() {
			this.calculateLayout();
		},
		onShow() {
			this.$nextTick(() => {
				setTimeout(() => {
					this.calculateLayout();
				}, 100);
			});
		},
		methods: {
			init() {
				this.loadProportionData();
				// this.loadRankingData();
			},
			tap(e) {
				uChartsInstance[e.target.id].touchLegend(e);
				uChartsInstance[e.target.id].showToolTip(e);
			},
			calculateLayout() {
				const systemInfo = uni.getSystemInfoSync();
				const windowHeight = systemInfo.windowHeight;
				const windowWidth = systemInfo.windowWidth;

				this.scrollHeight = windowHeight - 410;
				this.chartWidth = windowWidth - 80;
				this.chartHeight = Math.min(this.chartWidth, 400);
			},
			//获取详情占比数据
			async loadProportionData() {
				try {
					uni.showLoading({
						title: '加载图表数据...',
						mask: true
					});

					// 年度数据
					const yearTime = `${this.expenseData.selectYear}-01-01`;
					const yearRes = await lifeApi._getConsumptionCountYInfo(
						this.expenseData.isExpenses,
						yearTime,
						this.expenseData.queryId
					);

					// 月度数据
					const monthTime = `${this.expenseData.selectYear}-${this.expenseData.selectMonth + 1}-01`;
					const monthRes = await lifeApi._getConsumptionCountMInfo(
						this.expenseData.isExpenses,
						monthTime,
						this.expenseData.queryId
					);

					// 日度数据
					const dayTime =
						`${this.expenseData.selectYear}-${this.expenseData.selectMonth + 1}-${this.expenseData.selectDay}`;
					const dayRes = await lifeApi._getConsumptionCountDInfo(
						this.expenseData.isExpenses,
						dayTime,
						this.expenseData.queryId
					);

					if (yearRes) {
						this.chartData.year = yearRes.map(item => ({
							name: item.consumptionType,
							value: parseFloat(item.consumptionCost)
						}));
					}

					if (monthRes) {
						this.chartData.month = monthRes.map(item => ({
							name: item.consumptionType,
							value: parseFloat(item.consumptionCost)
						}));
					}

					if (dayRes) {
						this.chartData.day = dayRes.map(item => ({
							name: item.consumptionType,
							value: parseFloat(item.consumptionCost)
						}));
					}

					this.calculateProportionTotals();

					this.$nextTick(() => {
						setTimeout(() => {
							this.renderCharts();
						}, 300);
					});

				} catch (error) {
					console.error('加载图表数据失败:', error);
					uni.showToast({
						title: '数据加载失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},
			//获取排行榜数据
			async loadRankingData() {
				try {
					uni.showLoading({
						title: '加载排行榜...',
						mask: true
					});

					// 年度排行榜
					const yearTime = `${this.expenseData.selectYear}-01-01`;
					const yearRes = await lifeApi._getConsumptionCountYInfo(
						this.expenseData.isExpenses,
						yearTime,
						this.expenseData.queryId
					);

					// 月度排行榜
					const monthTime = `${this.expenseData.selectYear}-${this.expenseData.selectMonth + 1}-01`;
					const monthRes = await lifeApi._getConsumptionCountMInfo(
						this.expenseData.isExpenses,
						monthTime,
						this.expenseData.queryId
					);

					// 日度排行榜
					const dayTime =
						`${this.expenseData.selectYear}-${this.expenseData.selectMonth + 1}-${this.expenseData.selectDay}`;
					const dayRes = await lifeApi._getConsumptionCountDInfo(
						this.expenseData.isExpenses,
						dayTime,
						this.expenseData.queryId
					);

					if (yearRes) {
						this.rankData.year = yearRes
							.map(item => ({
								name: item.consumptionType,
								value: parseFloat(item.consumptionCost)
							}))
							.sort((a, b) => b.value - a.value)
							.slice(0, 20);
					}

					if (monthRes) {
						this.rankData.month = monthRes
							.map(item => ({
								name: item.consumptionType,
								value: parseFloat(item.consumptionCost)
							}))
							.sort((a, b) => b.value - a.value)
					}

					if (dayRes) {
						this.rankData.day = dayRes
							.map(item => ({
								name: item.consumptionType,
								value: parseFloat(item.consumptionCost)
							}))
							.sort((a, b) => b.value - a.value)
					}

					this.calculateRankingStats();

				} catch (error) {
					console.error('加载排行榜数据失败:', error);
					uni.showToast({
						title: '数据加载失败',
						icon: 'none'
					});
				} finally {
					uni.hideLoading();
				}
			},

			calculateProportionTotals() {
				this.yearTotal = this.chartData.year?.reduce((sum, item) => sum + item.value, 0) || 0;
				this.yearTotal = parseFloat(this.yearTotal).toFixed(2)
				this.monthTotal = this.chartData.month?.reduce((sum, item) => sum + item.value, 0) || 0;
				this.monthTotal = parseFloat(this.monthTotal).toFixed(2)
				this.dayTotal = this.chartData.day?.reduce((sum, item) => sum + item.value, 0) || 0;
				this.dayTotal = parseFloat(this.dayTotal).toFixed(2)
			},

			calculateRankingStats() {
				const currentData = this.currentRankData;
				this.totalItems = currentData.length;
				this.totalAmount = currentData.reduce((sum, item) => sum + item.value, 0);
			},

			renderCharts() {
				this.renderYearChart();
				this.renderMonthChart();
				this.renderDayChart();
			},

			renderYearChart() {
				let res = {
					series: [{
						data: this.chartData.year
					}]
				};
				this.drawCharts('yearChart', res);
			},

			renderMonthChart() {
				let res = {
					series: [{
						data: this.chartData.month
					}]
				};
				this.drawCharts('monthChart', res);
			},

			renderDayChart() {
				let res = {
					series: [{
						data: this.chartData.day
					}]
				};
				this.drawCharts('dayChart', res);
			},
			//饼状图
			drawCharts(id, data) {
				const ctx = uni.createCanvasContext(id, this);
				uChartsInstance[id] = new uCharts({
					type: "pie",
					context: ctx,
					width: this.chartWidth,
					height: this.chartHeight,
					series: data.series,
					animation: true,
					background: "#FFFFFF",

					padding: [5, 5, 5, 5],
					enableScroll: false,
					extra: {
						pie: {
							activeOpacity: 0.5,
							activeRadius: 10,
							offsetAngle: 0,
							labelWidth: 15,
							border: true,
							borderWidth: 3,
							borderColor: "#FFFFFF"
						}
					}
				});
			},

			onTypeChange(e) {
				this.expenseData.isExpenses = e.detail.value;
				this.spanText = e.detail.value === '0' ? '支出' : '收入';
				this.refreshCurrentData();
			},

			onYearChange(e) {
				const date = new Date(e.detail.value);
				this.expenseData.selectYear = date.getFullYear();
				this.updateDays();
				this.refreshCurrentData();
			},

			onMonthChange(e) {
				const date = new Date(e.detail.value);
				this.expenseData.selectMonth = date.getMonth();
				this.updateDays();
				this.refreshCurrentData();
			},

			onDayChange(e) {
				const date = new Date(e.detail.value);
				this.expenseData.selectDay = date.getDate();
				this.refreshCurrentData();
			},

			updateDays() {
				const daysInMonth = getDaysInMonth(this.expenseData.selectYear, this.expenseData.selectMonth);
				if (this.expenseData.selectDay > daysInMonth) {
					this.expenseData.selectDay = daysInMonth;
				}
			},

			switchTab(tab) {
				this.activeTab = tab;
				this.$nextTick(() => {
					setTimeout(() => {
						this.calculateLayout();
						this.refreshCurrentData();
					}, 100);
				});
			},

			switchDimension(dimension) {
				this.activeDimension = dimension;
				this.calculateRankingStats();
			},

			refreshCurrentData() {
				if (this.activeTab === 'proportion') {
					this.loadProportionData();
				} else {
					this.loadRankingData();
				}
			},

			refreshData() {
				this.refreshCurrentData();
				uni.showToast({
					title: '数据刷新中',
					icon: 'loading',
					duration: 1000
				});
			},

			getRankClass(index) {
				if (index === 0) return 'rank-gold';
				if (index === 1) return 'rank-silver';
				if (index === 2) return 'rank-bronze';
				return '';
			},

			getMedalIcon(index) {
				const icons = ['🥇', '🥈', '🥉'];
				return icons[index] || '';
			},

			handleBack() {
				uni.navigateBack();
			}
		}
	};
</script>

<style lang="scss" scoped>
	.financial-analysis-container {
		padding: 30rpx;
		background: linear-gradient(135deg, #5D4037 0%, #8B4513 100%);
		min-height: 100vh;
		position: relative;
	}

	.main-content {
		position: relative;
		z-index: 1;
	}

	.header-section {
		background: rgba(250, 240, 230, 0.95);
		border-radius: 20rpx;
		padding: 40rpx 30rpx;
		margin-bottom: 20rpx;

		.filter-section {
			.filter-item {
				margin-bottom: 30rpx;

				.filter-label {
					font-size: 28rpx;
					color: #5D4037;
					font-weight: bold;
					margin-bottom: 10rpx;
					display: block;
				}

				.radio-group {
					display: flex;
					gap: 40rpx;

					.radio-item {
						display: flex;
						align-items: center;
						gap: 10rpx;

						.radio-text {
							font-size: 26rpx;
							color: #5D4037;
						}
					}
				}

				.time-filters-row {
					display: flex;
					gap: 20rpx;
					align-items: center;

					.time-picker-group {
						flex: 1;
						display: flex;
						flex-direction: column;
						align-items: center;

						.time-label {
							font-size: 24rpx;
							color: #A1887F;
							margin-bottom: 8rpx;
							font-weight: 500;
						}

						.time-picker {
							width: 100%;

							.picker-text {
								height: 70rpx;
								border: 1rpx solid #D4AF37;
								border-radius: 10rpx;
								background: rgba(212, 175, 55, 0.1);
								display: flex;
								align-items: center;
								justify-content: center;
								font-size: 26rpx;
								color: #5D4037;
								font-weight: 500;
								transition: all 0.3s ease;
							}

							&:active .picker-text {
								background: rgba(212, 175, 55, 0.2);
								transform: scale(0.98);
							}
						}
					}
				}
			}
		}
	}

	.tab-section {
		background: rgba(250, 240, 230, 0.95);
		border-radius: 20rpx;
		padding: 20rpx 30rpx;
		margin-bottom: 20rpx;

		.tab-container {
			display: flex;
			background: rgba(212, 175, 55, 0.1);
			border-radius: 16rpx;
			padding: 8rpx;

			.tab-item {
				flex: 1;
				text-align: center;
				padding: 5rpx 0;
				border-radius: 12rpx;
				position: relative;
				transition: all 0.3s ease;

				&.active {
					background: #D4AF37;

					.tab-icon,
					.tab-text {
						color: #fff;
					}
				}

				.tab-icon {
					display: block;
					font-size: 36rpx;
					margin-bottom: 8rpx;
				}

				.tab-text {
					font-size: 26rpx;
					color: #5D4037;
					font-weight: 500;
				}

				.tab-indicator {
					position: absolute;
					bottom: -8rpx;
					left: 50%;
					transform: translateX(-50%);
					width: 40rpx;
					height: 4rpx;
					background: #D4AF37;
					border-radius: 2rpx;
				}
			}
		}
	}

	.dimension-tabs {
		background: rgba(250, 240, 230, 0.95);
		border-radius: 20rpx;
		padding: 20rpx 30rpx;
		margin-bottom: 20rpx;

		.dimension-container {
			display: flex;
			background: rgba(212, 175, 55, 0.1);
			border-radius: 16rpx;
			padding: 8rpx;

			.dimension-item {
				flex: 1;
				text-align: center;
				padding: 20rpx 0;
				border-radius: 12rpx;
				position: relative;
				transition: all 0.3s ease;

				&.active {
					background: #D4AF37;

					.dimension-text {
						color: #fff;
					}
				}

				.dimension-text {
					font-size: 24rpx;
					color: #5D4037;
					font-weight: 500;
				}

				.dimension-indicator {
					position: absolute;
					bottom: -8rpx;
					left: 50%;
					transform: translateX(-50%);
					width: 30rpx;
					height: 3rpx;
					background: #D4AF37;
					border-radius: 2rpx;
				}
			}
		}
	}

	.content-section {
		flex: 1;
	}

	.content-scroll {
		margin-bottom: 20rpx;
	}

	.charts-container {
		padding: 20rpx 0;
	}

	.chart-card {
		background: rgba(250, 240, 230, 0.95);
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 30rpx;

		.chart-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 30rpx;
			padding-bottom: 20rpx;
			border-bottom: 1rpx solid rgba(212, 175, 55, 0.3);

			.chart-title {
				font-size: 32rpx;
				font-weight: bold;
				color: #5D4037;
				font-family: 'SimHei', sans-serif;
			}

			.chart-actions {
				.total-amount {
					font-size: 24rpx;
					color: #D4AF37;
					font-weight: 500;
					background: rgba(212, 175, 55, 0.1);
					padding: 8rpx 16rpx;
					border-radius: 8rpx;
					border: 1rpx solid rgba(212, 175, 55, 0.3);
				}
			}
		}

		.chart-content {
			display: flex;
			justify-content: center;
			align-items: center;

			.chart-canvas {
				border-radius: 16rpx;
				background: rgba(255, 255, 255, 0.8);
			}
		}
	}

	.rank-content {
		padding: 20rpx 0;
	}

	.rank-header {
		background: rgba(250, 240, 230, 0.95);
		border-radius: 20rpx;
		padding: 5rpx;
		margin-bottom: 20rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;

		.rank-title {
			.title-text {
				display: block;
				font-size: 32rpx;
				font-weight: bold;
				color: #5D4037;
				margin-bottom: 8rpx;
				font-family: 'SimHei', sans-serif;
			}

			.title-desc {
				display: block;
				font-size: 24rpx;
				color: #A1887F;
			}
		}

		.rank-stats {
			display: flex;
			gap: 30rpx;

			.stat-item {
				text-align: center;

				.stat-value {
					display: block;
					font-size: 28rpx;
					font-weight: bold;
					color: #D4AF37;
					margin-bottom: 4rpx;
				}

				.stat-label {
					display: block;
					font-size: 22rpx;
					color: #A1887F;
				}
			}
		}
	}

	.rank-subtitle {
		background: rgba(250, 240, 230, 0.95);
		border-radius: 16rpx;
		padding: 20rpx;
		margin-bottom: 20rpx;
		text-align: center;

		.subtitle-text {
			font-size: 28rpx;
			font-weight: bold;
			color: #5D4037;
		}
	}

	.rank-list {
		.rank-item {
			background: rgba(250, 240, 230, 0.95);
			border-radius: 16rpx;
			padding: 30rpx;
			margin-bottom: 20rpx;
			display: flex;
			align-items: center;
			position: relative;
			overflow: hidden;

			&.rank-gold {
				border-left: 8rpx solid #D4AF37;
				background: linear-gradient(135deg, rgba(250, 240, 230, 0.95) 0%, rgba(255, 0, 0, 0.1) 100%);
			}

			&.rank-silver {
				border-left: 8rpx solid #C0C0C0;
				background: linear-gradient(135deg, rgba(250, 240, 230, 0.95) 0%, rgba(192, 192, 192, 0.1) 100%);
			}

			&.rank-bronze {
				border-left: 8rpx solid #CD7F32;
				background: linear-gradient(135deg, rgba(250, 240, 230, 0.95) 0%, rgba(205, 127, 50, 0.1) 100%);
			}

			.rank-number {
				width: 80rpx;
				text-align: center;
				position: relative;

				.rank-index {
					font-size: 36rpx;
					font-weight: bold;
					color: #5D4037;
				}

				.rank-medal {
					position: absolute;
					top: -10rpx;
					right: -10rpx;

					.medal-icon {
						font-size: 32rpx;
					}
				}
			}

			.rank-info {
				flex: 1;
				margin: 0 30rpx;

				.item-name {
					display: block;
					font-size: 28rpx;
					font-weight: bold;
					color: #5D4037;
					margin-bottom: 8rpx;
				}

				.item-category {
					display: block;
					font-size: 22rpx;
					color: #A1887F;
					background: rgba(161, 136, 127, 0.1);
					padding: 4rpx 12rpx;
					border-radius: 6rpx;
					display: inline-block;
				}
			}

			.rank-amount {
				text-align: right;
				min-width: 120rpx;

				.amount-value {
					display: block;
					font-size: 26rpx;
					font-weight: bold;
					color: #C41E3A;
					margin-bottom: 4rpx;
				}

				.amount-percentage {
					display: block;
					font-size: 22rpx;
					color: #2E8B57;
					background: rgba(46, 139, 87, 0.1);
					padding: 4rpx 8rpx;
					border-radius: 6rpx;
				}
			}

			.rank-progress {
				position: absolute;
				bottom: 0;
				left: 0;
				right: 0;
				height: 4rpx;
				background: rgba(161, 136, 127, 0.2);

				.progress-bar {
					height: 100%;
					background: linear-gradient(90deg, #D4AF37, #8B4513);
					border-radius: 0 0 0 4rpx;
					transition: width 0.5s ease;
				}
			}
		}
	}

	.empty-state {
		text-align: center;
		padding: 80rpx 30rpx;
		background: rgba(250, 240, 230, 0.95);
		border-radius: 20rpx;

		.empty-icon {
			display: block;
			font-size: 80rpx;
			margin-bottom: 30rpx;
		}

		.empty-text {
			display: block;
			font-size: 32rpx;
			color: #5D4037;
			margin-bottom: 16rpx;
			font-weight: bold;
		}

		.empty-desc {
			display: block;
			font-size: 26rpx;
			color: #A1887F;
		}
	}

	.bottom-actions {
		display: flex;
		gap: 20rpx;

		.action-btn {
			flex: 1;
			height: 80rpx;
			border-radius: 12rpx;
			font-size: 28rpx;
			font-weight: bold;
			border: none;

			&.refresh-btn {
				background: linear-gradient(135deg, #8B4513, #D4AF37);
				color: #fff;
			}

			&.back-btn {
				background: #A1887F;
				color: #fff;
			}

			&:active {
				transform: scale(0.98);
			}
		}
	}

	// 传承风格卡片效果
	.heritage-card {
		position: relative;
		border: 2rpx solid rgba(212, 175, 55, 0.3);
		box-shadow: 0 8rpx 32rpx rgba(139, 69, 19, 0.15);

		&::before {
			content: '';
			position: absolute;
			top: 0;
			left: 0;
			right: 0;
			height: 4rpx;
			background: linear-gradient(90deg, #D4AF37, #C41E3A, #2E8B57, #D4AF37);
			background-size: 200% 100%;
			animation: shimmer 3s infinite linear;
			border-radius: 20rpx 20rpx 0 0;
		}
	}

	@keyframes shimmer {
		0% {
			background-position: -200% 0;
		}

		100% {
			background-position: 200% 0;
		}
	}

	// 响应式适配
	@media (max-width: 750px) {
		.financial-analysis-container {
			padding: 20rpx;
		}

		.header-section {
			padding: 30rpx 20rpx;
		}

		.tab-section,
		.dimension-tabs {
			padding: 20rpx;
		}

		.chart-card,
		.rank-header,
		.rank-subtitle {
			padding: 20rpx;
		}

		.rank-item {
			padding: 20rpx !important;

			.rank-info {
				margin: 0 20rpx !important;
			}
		}

		.rank-header {
			flex-direction: column;
			gap: 20rpx;
			text-align: center;

			.rank-stats {
				justify-content: center;
			}
		}
	}
</style>