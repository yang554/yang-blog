<template>
	<view class="home-container">
		<!-- 背景渐变 -->
		<view class="background-gradient"></view>

		<!-- 页面内容 -->
		<view class="content-wrapper">
			<scroll-view class="main-content" scroll-y :show-scrollbar="false" refresher-background="transparent"
				:style="{ height: '100%' }">

				<view class="dashboard-container">
					<!-- 用户选择区域 -->
					<view class="user-select-card glass-card" v-if="showUserSearch">
						<view class="user-select-group">
							<view class="user-select-input">
								<picker @change="onUserChange" :value="userIndex" :range="uNameAll" range-key="uName">
									<view class="picker">
										{{ selectedUser ? selectedUser.uName : '请选择用户' }}
									</view>
								</picker>
							</view>
							<button type="primary" size="default" @click="selectByNameAllFun" class="search-btn">
								<text class="icon-search">🔍</text> 加载数据
							</button>
						</view>
					</view>

					<!-- 数据面板区域 -->
					<view class="panel-row">
						<!-- 上次记录面板 -->
						<view class="panel-col">
							<view class="data-panel glass-card">
								<view class="panel-header">
									<view class="panel-icon">📅</view>
									<text class="panel-title">上次记录</text>
								</view>
								<view class="panel-content">
									<view class="info-item">
										<text class="info-label">姓名</text>
										<text class="info-value">{{ auntForm.name }}</text>
									</view>
									<view class="info-item">
										<text class="info-label">周期长度</text>
										<text class="info-value">{{ auntForm.cycle }}</text>
									</view>
									<view class="info-item">
										<text class="info-label">开始日期</text>
										<text class="info-value">{{ auntForm.startDate }}</text>
									</view>
									<view class="info-item">
										<text class="info-label">结束日期</text>
										<text class="info-value">{{ auntForm.endDate }}</text>
									</view>
									<view class="info-item">
										<text class="info-label">持续时间</text>
										<text class="info-value">{{ auntForm.duration }}</text>
									</view>
									<view class="info-item">
										<text class="info-label">血量</text>
										<text class="info-value">{{ auntForm.bloodVolume }}ml</text>
									</view>
								</view>
							</view>
						</view>

						<!-- 预测信息面板 -->
						<view class="panel-col">
							<view class="data-panel glass-card">
								<view class="panel-header">
									<view class="panel-icon">🔮</view>
									<text class="panel-title">{{ nextAuntForm.msg }}</text>
								</view>
								<view class="panel-content">
									<view class="info-item">
										<text class="info-label">姓名</text>
										<text class="info-value">{{ auntForm.name }}</text>
									</view>
									<view class="info-item">
										<text class="info-label">周期长度</text>
										<text class="info-value">{{ nextAuntForm.cycleLength }}天</text>
									</view>
									<view class="info-item">
										<text class="info-label">开始日期</text>
										<text class="info-value">{{ nextAuntForm.nextPeriodStartDate }}</text>
									</view>
									<view class="info-item">
										<text class="info-label">结束日期</text>
										<text class="info-value">{{ nextAuntForm.nextPeriodEndDate }}</text>
									</view>
									<view class="info-item">
										<text class="info-label">持续时间</text>
										<text class="info-value">{{ nextAuntForm.periodLength }}天</text>
									</view>
									<view class="info-item">
										<text class="info-label">距离开始还有</text>
										<text
											class="info-value highlight">{{ nextAuntForm.daysUntilNextPeriod }}天</text>
									</view>
								</view>
							</view>
						</view>

						<!-- 本次记录面板 -->
						<view class="panel-col">
							<view class="data-panel glass-card">
								<view class="panel-header">
									<view class="panel-icon">✏️</view>
									<text class="panel-title">本次实际记录</text>
									<button type="primary" @click="dialogFunction" size="mini" class="edit-btn">
										<text class="icon-edit">️</text> 编辑
									</button>
								</view>
								<view class="panel-content">
									<view class="info-item">
										<text class="info-label">姓名</text>
										<text class="info-value">{{ realAuntForm.name }}</text>
									</view>
									<view class="info-item">
										<text class="info-label">周期长度</text>
										<text class="info-value">{{ realAuntForm.cycle }}</text>
									</view>
									<view class="info-item">
										<text class="info-label">开始日期</text>
										<text class="info-value">{{ realAuntForm.startDate }}</text>
									</view>
									<view class="info-item">
										<text class="info-label">结束日期</text>
										<text class="info-value">{{ realAuntForm.endDate }}</text>
									</view>
									<view class="info-item">
										<text class="info-label">持续时间</text>
										<text class="info-value">{{ realAuntForm.duration }}</text>
									</view>
									<view class="info-item">
										<text class="info-label">血量</text>
										<text class="info-value">{{ realAuntForm.bloodVolume }}ml</text>
									</view>
								</view>
							</view>
						</view>
					</view>

					<!-- 图表和日历区域 -->
					<view class="visualization-section">
						<view class="chart-card glass-card">
							<view class="chart-header">
								<view class="chart-title">
									<text class="icon-data">📊</text> 月经周期日历
								</view>
							</view>
							<!-- 日历区域 -->
							<view class="visualization-content">
								<view class="calendar-container">
									<view class="calendar-wrapper">
										<view class="calendar-header">
											<view class="calendar-title">{{ calendarTitle }}</view>
											<view class="calendar-actions">
												<button circle size="mini" @click="handleQuickChange('prev')"
													class="action-btn">
													<text class="icon-arrow">⬅️</text>
												</button>
												<button circle size="mini" @click="handleQuickChange('today')"
													class="action-btn">
													今
												</button>
												<button circle size="mini" @click="handleQuickChange('next')"
													class="action-btn">
													<text class="icon-arrow">➡️</text>
												</button>
											</view>
										</view>

										<view class="weekdays">
											<view v-for="(item, i) in weeks" :key="i" class="weekday"
												:class="{ weekend: i === 0 || i === 6 }">
												{{ item }}
											</view>
										</view>

										<view class="days-grid">
											<view v-for="(item, index) in showDays" :key="index" class="day-cell"
												:class="{
													'prev-month': item.flag === 'prev',
													'next-month': item.flag === 'rear',
													'current-month': item.flag === 'current',
													'current-day': item.isCurrentDay,
													'selected-day': item.isSelected,
													'today': item.isToday,
													'weekend': item.isWeekend,
													'has-event': item.hasEvent,
													'menstrual-phase': item.phase === 'menstrual',
													'follicular-phase': item.phase === 'follicular',
													'ovulation-phase': item.phase === 'ovulation',
													'luteal-phase': item.phase === 'luteal',
													'predicted-menstrual' : item.phase === 'predicted-menstrual',
													'predicted-follicular' : item.phase === 'predicted-follicular',
													'predicted-ovulation' : item.phase === 'predicted-ovulation',
													'predicted-luteal' : item.phase === 'predicted-luteal',
												}" @click="handleItemClick(item, Math.floor(index / 7), index % 7)">
												<view class="date-info">
													<text class="day-number">
														{{ item.day }}
													</text>
													<text v-if="item.isToday" class="today-badge">今</text>
												</view>
												<text class="lunar-day">{{ item.nl }}</text>
											</view>
										</view>

										<view class="calendar-footer">
											<view class="legend">
												<view class="legend-item">
													<view class="legend-icon today"></view>
													<text>今日</text>
												</view>
												<view class="legend-item">
													<view class="legend-icon menstrual-phase"></view>
													<text>月经期</text>
												</view>
												<view class="legend-item">
													<view class="legend-icon follicular-phase"></view>
													<text>卵泡期</text>
												</view>
												<view class="legend-item">
													<view class="legend-icon ovulation-phase"></view>
													<text>排卵期</text>
												</view>
												<view class="legend-item">
													<view class="legend-icon luteal-phase"></view>
													<text>黄体期</text>
												</view>
											</view>
										</view>
									</view>
								</view>
							</view>
						</view>
					</view>

					<!-- 编辑对话框 -->
					<view v-if="showEditDialog" class="edit-dialog-mask" @tap="closeEditDialog">
						<view class="edit-dialog glass-card" @tap.stop>
							<view class="dialog-header">
								<text class="dialog-title">记录编辑 - {{ realAuntForm.name }}</text>
								<text class="dialog-close" @tap="closeEditDialog">×</text>
							</view>
							<view class="dialog-content">
								<view class="form-row">
									<view class="form-col">
										<view class="form-item">
											<text class="form-label">姓名</text>
											<input v-model="realAuntForm.name" disabled class="form-input" />
										</view>
									</view>
									<view class="form-col">
										<view class="form-item">
											<text class="form-label">周期长度</text>
											<input v-model="realAuntForm.cycle" disabled class="form-input" />
										</view>
									</view>
								</view>

								<view class="form-row">
									<view class="form-col">
										<view class="form-item">
											<text class="form-label">开始日期</text>
											<picker mode="date" :value="realAuntForm.startDate"
												@change="onStartDateChange">
												<view class="form-picker">{{ realAuntForm.startDate || '选择开始时间' }}
												</view>
											</picker>
										</view>
									</view>
									<view class="form-col">
										<view class="form-item">
											<text class="form-label">结束日期</text>
											<picker mode="date" :value="realAuntForm.endDate" @change="onEndDateChange">
												<view class="form-picker">{{ realAuntForm.endDate || '选择结束时间' }}</view>
											</picker>
										</view>
									</view>
									<view class="form-col">
										<view class="form-item">
											<text class="form-label">持续时间</text>
											<input v-model="realAuntForm.duration" disabled class="form-input" />
										</view>
									</view>
									<view class="form-col">
										<view class="form-item">
											<text class="form-label">血量(ml)</text>
											<input v-model="realAuntForm.bloodVolume" class="form-input"
												type="number" />
										</view>
									</view>
								</view>

								<view class="form-row">
									<view class="form-col-full">
										<view class="form-item">
											<text class="form-label">症状</text>
											<textarea placeholder="记录每个月姨妈期间出现的症状，如腹痛、乳房胀痛、情绪波动等"
												v-model="realAuntForm.symptom" class="form-textarea"></textarea>
										</view>
									</view>
								</view>

								<view class="form-row">
									<view class="form-col-full">
										<view class="form-item">
											<text class="form-label">备注</text>
											<textarea placeholder="可以记录一些额外的信息，如服用的药物、健康状况等。"
												v-model="realAuntForm.note" class="form-textarea"></textarea>
										</view>
									</view>
								</view>
							</view>
							<view class="dialog-footer">
								<button @click="closeEditDialog" class="dialog-btn cancel">取 消</button>
								<button type="primary" @click="handleEditAunt" class="dialog-btn save">保 存</button>
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
		auntApi
	} from '@/api/index.js'
	var uChartsInstance = {};
	export default {
		components: {
			CustomNavbar,
			CustomTabbar
		},
		name: "AuntDashBoardView",
		data() {
			const currentYear = new Date().getFullYear();
			const currentMonth = new Date().getMonth();
			return {
				statusBarHeight: 0,
				showUserSearch: false,
				isShowContent: false,
				showEditDialog: false, // 控制编辑对话框显示
				userIndex: 0,
				selectedUser: null,
				uNameAll: [],
				uID: '',
				uName: '',
				currentUserInfo: uni.getStorageSync(this.$constants.USER_INFO), //当前登录用户
				curYear: currentYear,
				curMonth: currentMonth,

				// 日历数据
				weeks: ['日', '一', '二', '三', '四', '五', '六'],
				showDays: [],
				calendarData: [],

				// 表单数据
				auntForm: {
					name: '',
					startDate: '',
					endDate: '',
					duration: '',
					cycle: '',
					bloodVolume: ''
				},
				realAuntForm: {
					name: '',
					startDate: '',
					endDate: '',
					duration: '',
					cycle: '',
					bloodVolume: '',
					symptom: '',
					note: ''
				},
				nextAuntForm: {
					daysUntilNextPeriod: '',
					cycleLength: '',
					periodLength: '',
					nextPeriodEndDate: '',
					nextPeriodStartDate: '',
					msg: ''
				},

				// 图表数据
				xData: [],
				yData: [],
				cWidth: 2400,
				cHeight: 350
			};
		},
		computed: {
			calendarTitle() {
				const months = ['一月', '二月', '三月', '四月', '五月', '六月',
					'七月', '八月', '九月', '十月', '十一月', '十二月'
				];
				return `${this.curYear}年 ${months[this.curMonth]}`;
			}
		},
		onLoad() {
			this.getStatusBarHeight();
			this.init();
		},
		onReady() {
			uni.hideTabBar();
		},
		onUnload() {

		},
		methods: {
			getStatusBarHeight() {
				const systemInfo = uni.getSystemInfoSync();
				this.statusBarHeight = systemInfo.statusBarHeight;
			},
			// 初始化
			init() {
				this.loadUserList();
				this.generateCalendar();
			},
			// 加载用户列表
			async loadUserList() {
				try {
					const response = await auntApi._getAuntNameAll();
					this.uNameAll = response.data;

					if (this.currentUserInfo.roles[0].name === 'visitor') {
						this.uName = this.currentUserInfo.username
						this.uID = this.currentUserInfo.id
						this.showUserSearch = false
						return
					}
					if (this.currentUserInfo.roles[0].name === 'root' && this.uNameAll != null) {
						this.selectedUser = this.uNameAll[0];
						this.uName = this.uNameAll[0].uName
						this.uID = this.uNameAll[0].uID
						this.showUserSearch = true
					}

					this.selectByNameAllFun();

				} catch (error) {
					console.error('经期页面初始化失败:', error);
				}
			},

			// 用户选择变化
			onUserChange(e) {
				const index = e.detail.value;
				this.userIndex = index;
				this.selectedUser = this.uNameAll[index];
				this.uID = this.selectedUser.uID;
				this.uName = this.selectedUser.uName;
			},

			// 开始日期变化
			onStartDateChange(e) {
				this.realAuntForm.startDate = e.detail.value;
				this.calculateDuration();
				this.calculateCycle();
			},

			// 结束日期变化
			onEndDateChange(e) {
				this.realAuntForm.endDate = e.detail.value;
				this.calculateDuration();
			},

			// 计算持续时间
			calculateDuration() {
				if (this.realAuntForm.startDate && this.realAuntForm.endDate) {
					const start = new Date(this.realAuntForm.startDate);
					const end = new Date(this.realAuntForm.endDate);
					const diffTime = Math.abs(end - start);
					const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
					this.realAuntForm.duration = `${diffDays}天`;
				}
			},
			//计算周期长度
			calculateCycle() {
				if (this.realAuntForm.startDate && this.auntForm.startDate) {
					const start = new Date(this.auntForm.startDate);
					const end = new Date(this.realAuntForm.startDate);
					const diffTime = Math.abs(end - start);
					const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1;
					this.realAuntForm.cycle = `${diffDays}天`;
				}
			},

			// 生成日历
			generateCalendar() {
				const year = this.curYear;
				const month = this.curMonth;
				const firstDay = new Date(year, month, 1);
				const lastDay = new Date(year, month + 1, 0);
				const daysInMonth = lastDay.getDate();
				const firstDayOfWeek = firstDay.getDay();

				this.showDays = [];

				// 添加上个月的天数
				const prevMonthLastDay = new Date(year, month, 0).getDate();
				for (let i = firstDayOfWeek - 1; i >= 0; i--) {
					this.showDays.push({
						day: prevMonthLastDay - i,
						date: `${year}-${month.toString().padStart(2, '0')}-${(prevMonthLastDay - i).toString().padStart(2, '0')}`,
						flag: 'prev',
						nl: this.getLunarDay(prevMonthLastDay - i),
						isCurrentDay: false,
						isSelected: false,
						isToday: this.isToday(year, month - 1, prevMonthLastDay - i),
						isWeekend: false
					});
				}

				// 添加当前月的天数
				const today = new Date();
				for (let i = 1; i <= daysInMonth; i++) {
					const date = new Date(year, month, i);
					const isWeekend = date.getDay() === 0 || date.getDay() === 6;

					this.showDays.push({
						day: i,
						date: `${year}-${(month + 1).toString().padStart(2, '0')}-${i.toString().padStart(2, '0')}`,
						flag: 'current',
						nl: this.getLunarDay(i),
						isCurrentDay: false,
						isSelected: false,
						isToday: today.getFullYear() === year && today.getMonth() === month && today.getDate() ===
							i,
						isWeekend: isWeekend,
						hasEvent: Math.random() > 0.7 // 模拟事件
					});
				}

				// 添加下个月的天数
				const totalCells = 42; // 6行x7列
				const remainingCells = totalCells - this.showDays.length;
				for (let i = 1; i <= remainingCells; i++) {
					this.showDays.push({
						day: i,
						date: `${year}-${(month + 2).toString().padStart(2, '0')}-${i.toString().padStart(2, '0')}`,
						flag: 'rear',
						nl: this.getLunarDay(i),
						isCurrentDay: false,
						isSelected: false,
						isToday: this.isToday(year, month + 1, i),
						isWeekend: false
					});
				}

				// 计算当前月经周期
				this.calculateMenstrualPhases();

			},

			// 计算月经周期阶段
			calculateMenstrualPhases() {
				if (!this.auntForm.startDate) return;

				const lastPeriodStart = new Date(this.auntForm.startDate);
				const cycleLength = parseInt(this.auntForm.cycle) || 28;
				const periodLength = parseInt(this.auntForm.duration) || 5;

				// 计算当前周期的开始日期
				let currentCycleStart = new Date(lastPeriodStart);
				while (currentCycleStart <= new Date()) {
					currentCycleStart.setDate(currentCycleStart.getDate() + cycleLength);
				}
				currentCycleStart.setDate(currentCycleStart.getDate() - cycleLength);

				// 计算当前周期各阶段
				const menstrualStart = new Date(currentCycleStart);
				const menstrualEnd = new Date(menstrualStart);
				menstrualEnd.setDate(menstrualEnd.getDate() + periodLength - 1);

				const follicularStart = new Date(menstrualEnd);
				follicularStart.setDate(follicularStart.getDate() + 1);
				const follicularEnd = new Date(follicularStart);
				follicularEnd.setDate(follicularEnd.getDate() + 7); // 经后期约7天

				const ovulationStart = new Date(follicularEnd);
				ovulationStart.setDate(ovulationStart.getDate() + 1);
				const ovulationEnd = new Date(ovulationStart);
				ovulationEnd.setDate(ovulationEnd.getDate() + 5); // 排卵期约5天

				const lutealStart = new Date(ovulationEnd);
				lutealStart.setDate(lutealStart.getDate() + 1);
				const lutealEnd = new Date(currentCycleStart);
				lutealEnd.setDate(lutealEnd.getDate() + cycleLength - 1); // 经前期

				// 标记当前周期的阶段
				this.showDays.forEach(day => {
					const dayDate = new Date(day.date);

					if (dayDate >= menstrualStart && dayDate <= menstrualEnd) {
						day.phase = 'menstrual'; // 月经期
					} else if (dayDate >= follicularStart && dayDate <= follicularEnd) {
						day.phase = 'follicular'; // 经后期
					} else if (dayDate >= ovulationStart && dayDate <= ovulationEnd) {
						day.phase = 'ovulation'; // 排卵期
					} else if (dayDate >= lutealStart && dayDate <= lutealEnd) {
						day.phase = 'luteal'; // 经前期
					} else {
						// 先清除，后续可能会被预测周期覆盖
						if (!day.phase) day.phase = '';
					}
				});

				// 如果有预测数据，计算并标记预测周期
				if (this.nextAuntForm && this.nextAuntForm.nextPeriodStartDate) {
					this.calculatePredictedPhases();
				}
			},

			// 计算预测周期阶段
			calculatePredictedPhases() {
				const nextPeriodStart = new Date(this.nextAuntForm.nextPeriodStartDate);
				const predictedCycleLength = parseInt(this.nextAuntForm.cycleLength) || 28;
				const predictedPeriodLength = parseInt(this.nextAuntForm.periodLength) || 5;

				// 计算预测周期的各阶段
				const predictedMenstrualStart = new Date(nextPeriodStart);
				const predictedMenstrualEnd = new Date(predictedMenstrualStart);
				predictedMenstrualEnd.setDate(predictedMenstrualEnd.getDate() + predictedPeriodLength - 1);

				const predictedFollicularStart = new Date(predictedMenstrualEnd);
				predictedFollicularStart.setDate(predictedFollicularStart.getDate() + 1);
				const predictedFollicularEnd = new Date(predictedFollicularStart);
				predictedFollicularEnd.setDate(predictedFollicularEnd.getDate() + 7);

				const predictedOvulationStart = new Date(predictedFollicularEnd);
				predictedOvulationStart.setDate(predictedOvulationStart.getDate() + 1);
				const predictedOvulationEnd = new Date(predictedOvulationStart);
				predictedOvulationEnd.setDate(predictedOvulationEnd.getDate() + 5);

				const predictedLutealStart = new Date(predictedOvulationEnd);
				predictedLutealStart.setDate(predictedLutealStart.getDate() + 1);
				const predictedLutealEnd = new Date(nextPeriodStart);
				predictedLutealEnd.setDate(predictedLutealEnd.getDate() + predictedCycleLength - 1);

				// 标记预测周期的阶段（使用不同的样式类名）
				this.showDays.forEach(day => {
					const dayDate = new Date(day.date);

					if (dayDate >= predictedMenstrualStart && dayDate <= predictedMenstrualEnd) {
						day.phase = 'predicted-menstrual'; // 预测月经期
					} else if (dayDate >= predictedFollicularStart && dayDate <= predictedFollicularEnd) {
						day.phase = 'predicted-follicular'; // 预测经后期
					} else if (dayDate >= predictedOvulationStart && dayDate <= predictedOvulationEnd) {
						day.phase = 'predicted-ovulation'; // 预测排卵期
					} else if (dayDate >= predictedLutealStart && dayDate <= predictedLutealEnd) {
						day.phase = 'predicted-luteal'; // 预测经前期
					}
				});
			},

			// 判断是否是今天
			isToday(year, month, day) {
				const today = new Date();
				return today.getFullYear() === year &&
					today.getMonth() === month &&
					today.getDate() === day;
			},

			// 获取农历日期
			getLunarDay(day) {
				// 实际项目中应该使用农历计算库
				const lunarDays = ['初一', '初二', '初三', '初四', '初五', '初六', '初七', '初八', '初九', '初十',
					'十一', '十二', '十三', '十四', '十五', '十六', '十七', '十八', '十九', '二十',
					'廿一', '廿二', '廿三', '廿四', '廿五', '廿六', '廿七', '廿八', '廿九', '三十'
				];
				return lunarDays[(day - 1) % 30] || '';
			},

			// 日期点击事件
			handleItemClick(item, row, col) {
				console.log('点击日期:', item, '位置:', row, col);
				// 处理日期选择逻辑
			},

			// 快速切换月份
			handleQuickChange(type) {
				if (type === 'prev') {
					this.curMonth--;
					if (this.curMonth < 0) {
						this.curMonth = 11;
						this.curYear--;
					}
				} else if (type === 'next') {
					this.curMonth++;
					if (this.curMonth > 11) {
						this.curMonth = 0;
						this.curYear++;
					}
				} else {
					// 回到今天
					const today = new Date();
					this.curYear = today.getFullYear();
					this.curMonth = today.getMonth();
				}

				this.generateCalendar();
			},

			// 获取用户经期记录
			async selectByNameAllFun() {
				if (!this.uID) {
					return;
				}

				try {
					this.isShowContent = true;

					this.xData = []
					this.yData = []

					const response = await auntApi._getAuntByNameAll(this.uID, 0);
					if (response) {
						response.auntList.forEach(element => {
							this.xData.push(element.startDate + "~" + element.endDate)
							this.yData.push(element.bloodVolume)
						});
						this.nextAuntForm.daysUntilNextPeriod = response.daysUntilNextPeriod
						this.nextAuntForm.nextPeriodEndDate = response.nextPeriodEndDate
						this.nextAuntForm.nextPeriodStartDate = response.nextPeriodStartDate
						this.nextAuntForm.cycleLength = response.cycleLength
						this.nextAuntForm.periodLength = response.periodLength
						this.nextAuntForm.msg = '根据' + response.msg + '预测下次经期情况'


						this.auntForm.name = response.auntList[0].uName
						this.auntForm.startDate = response.auntList[0].startDate
						this.auntForm.endDate = response.auntList[0].endDate
						this.auntForm.duration = response.auntList[0].duration
						this.auntForm.cycle = response.auntList[0].cycle
						this.auntForm.bloodVolume = response.auntList[0].bloodVolume

						const res = await auntApi._getAuntByNameAll(this.uID, 1);
						if (res === "") {
							this.realAuntForm.id = ''
							this.realAuntForm.uID = this.uID
							this.realAuntForm.name = this.auntForm.name
							this.realAuntForm.startDate = ''
							this.realAuntForm.endDate = ''
							this.realAuntForm.duration = ''
							this.realAuntForm.cycle = ''
							this.realAuntForm.bloodVolume = ''
							this.realAuntForm.symptom = ''
							this.realAuntForm.note = ''
						} else {
							this.realAuntForm.id = res.auntList[0].id
							this.realAuntForm.name = res.auntList[0].uName
							this.realAuntForm.startDate = res.auntList[0].startDate
							this.realAuntForm.endDate = res.auntList[0].endDate
							this.realAuntForm.duration = res.auntList[0].duration
							this.realAuntForm.cycle = res.auntList[0].cycle
							this.realAuntForm.bloodVolume = res.auntList[0].bloodVolume
							this.realAuntForm.symptom = res.auntList[0].symptom
							this.realAuntForm.note = res.auntList[0].note
						}
						this.generateCalendar();
					}
				} catch (error) {
					console.error('获取用户经期记录失败:', error);
				}
			},

			// 打开编辑对话框
			dialogFunction() {
				this.showEditDialog = true;
			},

			// 关闭编辑对话框
			closeEditDialog() {
				this.showEditDialog = false;
			},

			// 保存编辑
			async handleEditAunt() {
				try {
					if (this.realAuntForm.id === '') {
						if (this.realAuntForm.startDate === '' || this.realAuntForm.endDate === '')
							this.realAuntForm.uStatus = '1'
						else
							this.realAuntForm.uStatus = '0'
						const res = await auntApi._addAunt(this.realAuntForm);
					} else {
						const res = await auntApi._updAunt(this.realAuntForm);
					}
					uni.showToast({
						title: '保存成功',
						icon: 'success'
					});
					this.closeEditDialog();
					// 重新加载数据
					this.selectByNameAllFun();
				} catch (error) {
					console.error('获取用户经期记录失败:', error);
				}
			}
		}
	};
</script>

<style lang="scss" scoped>
	@import '@/scss/variables';

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

	.dashboard-container {
		padding: 20rpx 0;
	}

	/* 用户选择区域 */
	.user-select-card {
		padding: 0rpx;
		margin-bottom: 30rpx;
	}

	.user-select-group {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.user-select-input {
		flex: 1;

		.picker {
			padding: 21rpx 30rpx;
			background: rgba($primary-color, 0.1);
			border: 1px solid rgba($primary-color, 0.3);
			border-radius: 12rpx;
			color: $primary-color;
			text-align: center;
			font-weight: 500;
			transition: all 0.3s ease;

			&:active {
				background: rgba($primary-color, 0.2);
			}
		}
	}

	.search-btn {
		background: linear-gradient(135deg, $primary-color, $accent-color);
		border: none;
		border-radius: 12rpx;
		color: white;
		padding: 0 20rpx;
		font-weight: 500;
		transition: all 0.3s ease;

		&:active {
			transform: scale(0.98);
			box-shadow: $shadow-medium;
		}
	}

	/* 数据面板区域 */
	.panel-row {
		display: flex;
		flex-direction: column;
		gap: 30rpx;
		margin-bottom: 30rpx;
	}

	.panel-col {
		flex: 1;
	}

	.data-panel {
		padding: 40rpx 30rpx;
		transition: all 0.3s ease;

		&:active {
			transform: translateY(-5rpx);
			box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.15);
		}

		.panel-header {
			display: flex;
			align-items: center;
			margin-bottom: 30rpx;
			padding-bottom: 20rpx;
			border-bottom: 1px solid rgba($primary-color, 0.1);

			.panel-icon {
				font-size: 36rpx;
				margin-right: 15rpx;
			}

			.panel-title {
				font-size: 32rpx;
				font-weight: 600;
				color: $text-primary;
				flex: 1;
			}

			.edit-btn {
				background: rgba($primary-color, 0.1);
				color: $primary-color;
				border: 1px solid rgba($primary-color, 0.3);
				border-radius: 12rpx;
				padding: 12rpx 24rpx;
				font-size: 24rpx;
				font-weight: 500;

				&:active {
					background: rgba($primary-color, 0.2);
				}
			}
		}

		.panel-content {
			.info-item {
				display: flex;
				justify-content: space-between;
				margin-bottom: 20rpx;
				padding: 10rpx 0;

				.info-label {
					color: $text-secondary;
					font-size: 28rpx;
					font-weight: 500;
				}

				.info-value {
					color: $text-primary;
					font-size: 28rpx;
					font-weight: 500;

					&.highlight {
						color: $primary-color;
						font-weight: bold;
					}
				}
			}
		}
	}

	/* 日历区域 */
	.visualization-section {
		margin-bottom: 30rpx;
	}

	.chart-card {
		padding: 40rpx;

		.chart-header {
			margin-bottom: 30rpx;

			.chart-title {
				font-size: 36rpx;
				font-weight: 600;
				color: $text-primary;
				display: flex;
				align-items: center;
				gap: 16rpx;
			}
		}
	}

	.calendar-wrapper {
		.calendar-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 30rpx;

			.calendar-title {
				font-size: 32rpx;
				font-weight: 600;
				color: $text-primary;
			}

			.calendar-actions {
				display: flex;
				gap: 20rpx;

				.action-btn {
					width: 60rpx;
					height: 60rpx;
					border-radius: 50%;
					background: rgba($primary-color, 0.1);
					border: 1px solid rgba($primary-color, 0.3);
					color: $primary-color;
					display: flex;
					align-items: center;
					justify-content: center;
					transition: all 0.3s ease;

					&:active {
						background: rgba($primary-color, 0.2);
					}
				}
			}
		}

		.weekdays {
			display: grid;
			grid-template-columns: repeat(7, 1fr);
			margin-bottom: 20rpx;
			text-align: center;

			.weekday {
				font-size: 28rpx;
				color: $text-secondary;
				padding: 20rpx 0;
				font-weight: 500;

				&.weekend {
					color: $error-color;
				}
			}
		}

		.days-grid {
			display: grid;
			grid-template-columns: repeat(7, 1fr);
			grid-gap: 8rpx;
			margin-bottom: 30rpx;
		}

		.day-cell {
			position: relative;
			border-radius: 16rpx;
			padding: 16rpx 8rpx;
			min-height: 90rpx;
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			background: rgba($primary-color, 0.05);
			border: 1px solid rgba($primary-color, 0.1);
			transition: all 0.3s ease;

			// 当前月样式
			&.current-month {
				background: rgba($primary-color, 0.08);
				border-color: rgba($primary-color, 0.2);
			}

			// 上个月和下个月样式
			&.prev-month,
			&.next-month {
				background: rgba($primary-color, 0.02);
				border-color: rgba($primary-color, 0.05);

				.day-number {
					color: $text-light !important;
				}

				.lunar-day {
					color: $text-light !important;
				}
			}

			// 周末样式
			&.weekend {
				background: rgba($primary-color, 0.12);
			}

			// 今日样式
			&.today {
				background: rgba($primary-color, 0.15);
				border-color: $primary-color;
			}

			.date-info {
				display: flex;
				justify-content: space-between;
				align-items: center;

				.day-number {
					font-size: 28rpx;
					font-weight: 500;
					color: $text-primary;
				}

				.today-badge {
					font-size: 20rpx;
					background: $primary-color;
					color: white;
					padding: 4rpx 12rpx;
					border-radius: 20rpx;
					font-weight: 500;
				}
			}

			.lunar-day {
				font-size: 20rpx;
				color: $text-secondary;
				text-align: center;
				margin-top: 4rpx;
			}
		}

		// 月经周期阶段样式
		.menstrual-phase.current-month {
			background: rgba($error-color, 0.15) !important;
			border-color: rgba($error-color, 0.3) !important;
		}

		.follicular-phase.current-month {
			background: rgba($success-color, 0.15) !important;
			border-color: rgba($success-color, 0.3) !important;
		}

		.ovulation-phase.current-month {
			background: rgba($warning-color, 0.15) !important;
			border-color: rgba($warning-color, 0.3) !important;
		}

		.luteal-phase.current-month {
			background: rgba($info-color, 0.15) !important;
			border-color: rgba($info-color, 0.3) !important;
		}

		/* 预测月经周期阶段样式 - 使用虚线边框表示预测 */
		.predicted-menstrual.current-month {
			background: rgba($error-color, 0.08) !important;
			border: 2px dashed rgba($error-color, 0.4) !important;
		}

		.predicted-follicular.current-month {
			background: rgba($success-color, 0.08) !important;
			border: 2px dashed rgba($success-color, 0.4) !important;
		}

		.predicted-ovulation.current-month {
			background: rgba($warning-color, 0.08) !important;
			border: 2px dashed rgba($warning-color, 0.4) !important;
		}

		.predicted-luteal.current-month {
			background: rgba($info-color, 0.08) !important;
			border: 2px dashed rgba($info-color, 0.4) !important;
		}

		.calendar-footer {
			padding-top: 30rpx;
			border-top: 1px solid rgba($primary-color, 0.1);
		}

		.legend {
			display: flex;
			flex-wrap: wrap;
			gap: 20rpx;
			justify-content: center;

			.legend-item {
				display: flex;
				align-items: center;
				gap: 8rpx;

				.legend-icon {
					width: 24rpx;
					height: 24rpx;
					border-radius: 4rpx;
					border: 1px solid rgba($primary-color, 0.3);

					&.today {
						background: $primary-color;
						border-radius: 50%;
					}

					&.menstrual-phase {
						background: $error-color;
					}

					&.follicular-phase {
						background: $success-color;
					}

					&.ovulation-phase {
						background: $warning-color;
					}

					&.luteal-phase {
						background: $info-color;
					}
				}

				text {
					font-size: 22rpx;
					color: $text-secondary;
				}
			}
		}
	}

	// 编辑对话框样式
	.edit-dialog-mask {
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
		padding: 40rpx;
	}

	.edit-dialog {
		width: 100%;
		max-width: 800rpx;
		max-height: 80%;
		overflow-y: auto;
	}

	.dialog-header {
		padding: 40rpx;
		border-bottom: 1px solid rgba($primary-color, 0.1);
		display: flex;
		justify-content: space-between;
		align-items: center;

		.dialog-title {
			color: $text-primary;
			font-size: 36rpx;
			font-weight: 600;
		}

		.dialog-close {
			color: $text-secondary;
			font-size: 48rpx;
			line-height: 1;
			cursor: pointer;
			transition: color 0.3s ease;

			&:active {
				color: $text-primary;
			}
		}
	}

	.dialog-content {
		padding: 10rpx;
	}

	.form-row {
		display: flex;
		flex-wrap: wrap;
		margin-bottom: 0rpx;
		gap: 30rpx;
	}

	.form-col {
		flex: 1;
		min-width: 200rpx;
	}

	.form-col-full {
		flex: 100%;
	}

	.form-item {
		margin-bottom: 30rpx;

		.form-label {
			display: block;
			color: $text-secondary;
			font-size: 28rpx;
			margin-bottom: 15rpx;
			font-weight: 500;
		}

		.form-input {
			background: rgba($primary-color, 0.05);
			border: 1px solid rgba($primary-color, 0.2);
			color: $text-primary;
			border-radius: 12rpx;
			padding: 20rpx;
			width: 100%;
			height: 100%;
			box-sizing: border-box;
			font-size: 28rpx;

			&:focus {
				border-color: $primary-color;
			}
		}

		.form-picker {
			background: rgba($primary-color, 0.05);
			border: 1px solid rgba($primary-color, 0.2);
			color: $text-primary;
			border-radius: 12rpx;
			padding: 20rpx;
			text-align: center;
			font-size: 28rpx;
			transition: all 0.3s ease;

			&:active {
				background: rgba($primary-color, 0.1);
			}
		}

		.form-textarea {
			background: rgba($primary-color, 0.05);
			border: 1px solid rgba($primary-color, 0.2);
			color: $text-primary;
			border-radius: 12rpx;
			padding: 20rpx;
			width: 100%;
			height: 200rpx;
			box-sizing: border-box;
			font-size: 28rpx;

			&:focus {
				border-color: $primary-color;
			}
		}
	}

	.dialog-footer {
		display: flex;
		padding: 30rpx;
		border-top: 1px solid rgba($primary-color, 0.1);
		gap: 30rpx;

		.dialog-btn {
			flex: 1;
			padding: 4rpx;
			border-radius: 12rpx;
			font-size: 28rpx;
			font-weight: 500;
			transition: all 0.3s ease;

			&.cancel {
				background: rgba($primary-color, 0.1);
				border: 1px solid rgba($primary-color, 0.2);
				color: $text-primary;

				&:active {
					background: rgba($primary-color, 0.2);
				}
			}

			&.save {
				background: linear-gradient(135deg, $primary-color, $accent-color);
				border: none;
				color: #fff;

				&:active {
					transform: scale(0.98);
				}
			}
		}
	}

	// 响应式调整
	@media (min-width: 768px) {
		.panel-row {
			flex-direction: row;
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
	.user-select-card,
	.panel-row,
	.visualization-section {
		animation: slideInUp 0.6s ease-out;
	}

	.panel-row {
		animation-delay: 0.1s;
	}

	.visualization-section {
		animation-delay: 0.2s;
	}
</style>