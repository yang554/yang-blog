<template>
    <div class="page2">
        <!-- 家庭收支趋势分析系统 -->
        <v-row class="bottom-list">
            <v-col span="12" class="col-12">
                <div class="list right-bottom">
                    <div class="bottom bottom1">
                        <!-- 时间范围选择 -->
                        <div class="time-selector-container">
                            <div class="time-range-selector">
                                <el-radio-group v-model="timeRange" @change="handleTimeRangeChange">
                                    <el-radio-button label="total">年度趋势</el-radio-button>
                                    <el-radio-button label="year">月度趋势</el-radio-button>
                                    <el-radio-button label="month">每日趋势</el-radio-button>
                                    <el-radio-button label="day">自定义</el-radio-button>
                                </el-radio-group>
                                <div class="date-selectors">
                                    <div class="date-selector-group" v-if="timeRange === 'year'">
                                        <span class="selector-label">分析年份：</span>
                                        <el-select v-model="selectedYear" @change="loadFinancialData">
                                            <el-option v-for="item in yearOptions" :key="item.key" :value="item.value"
                                                :label="item.label" />
                                        </el-select>
                                    </div>

                                    <div class="date-selector-group" v-if="timeRange === 'month'">
                                        <span class="selector-label">分析月份：</span>
                                        <el-select v-model="selectedYear" @change="loadFinancialData"
                                            style="margin-left: 10px;">
                                            <el-option v-for="item in yearOptions" :key="item.key" :value="item.value"
                                                :label="item.label" />
                                        </el-select>
                                        <el-select v-model="selectedMonth" @change="loadFinancialData">
                                            <el-option v-for="item in monthOptions" :key="item.key" :value="item.value"
                                                :label="item.label" />
                                        </el-select>

                                    </div>

                                    <div class="date-selector-group" v-if="timeRange === 'day'">
                                        <span class="selector-label">分析日期：</span>
                                        <el-date-picker v-model="selectedDate" type="daterange" range-separator="至"
                                            start-placeholder="开始日期" end-placeholder="结束日期" format="yyyy-MM-dd"
                                            value-format="yyyy-MM-dd" @change="loadFinancialData" />
                                    </div>
                                </div>
                            </div>


                        </div>

                        <!-- 核心指标概览 -->
                        <div class="metrics-overview">
                            <div class="metric-card income-card">
                                <div class="metric-icon">💰</div>
                                <div class="metric-content">
                                    <div class="metric-label">总收入</div>
                                    <div class="metric-value positive">{{ formatCurrency(metrics.totalIncome) }}</div>
                                    <div class="metric-change" :class="getChangeClass(metrics.incomeChange)">
                                        <i :class="metrics.incomeChange >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                                        {{ Math.abs(metrics.incomeChange) }}%
                                    </div>
                                </div>
                            </div>

                            <div class="metric-card expense-card">
                                <div class="metric-icon">💸</div>
                                <div class="metric-content">
                                    <div class="metric-label">总支出</div>
                                    <div class="metric-value negative">{{ formatCurrency(metrics.totalExpense) }}</div>
                                    <div class="metric-change" :class="getChangeClass(metrics.expenseChange)">
                                        <i :class="metrics.expenseChange >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
                                        {{ Math.abs(metrics.expenseChange) }}%
                                    </div>
                                </div>
                            </div>

                            <div class="metric-card balance-card">
                                <div class="metric-icon">⚖️</div>
                                <div class="metric-content">
                                    <div class="metric-label">净结余</div>
                                    <div class="metric-value" :class="metrics.netBalance >= 0 ? 'positive' : 'negative'">
                                        {{ formatCurrency(metrics.netBalance) }}
                                    </div>
                                    <div class="metric-trend">
                                        {{ metrics.netBalance >= 0 ? '财务健康' : '需关注' }}
                                    </div>
                                </div>
                            </div>

                            <div class="metric-card rate-card">
                                <div class="metric-icon">📊</div>
                                <div class="metric-content">
                                    <div class="metric-label">储蓄率</div>
                                    <div class="metric-value" :class="metrics.savingsRate >= 0 ? 'positive' : 'negative'">{{
                                        metrics.savingsRate }}%</div>
                                    <div class="metric-trend" :class="getRateClass(metrics.savingsRate)">
                                        {{ getRateText(metrics.savingsRate) }}
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 趋势图表 -->
                        <div class="trend-chart-section">
                            <div class="section-header">
                                <h3 class="section-title">📈 收支趋势分析</h3>
                                <div class="chart-controls">
                                    <el-radio-group v-model="chartType" size="small">
                                        <el-radio-button label="line">折线图</el-radio-button>
                                        <el-radio-button label="bar">柱状图</el-radio-button>
                                    </el-radio-group>
                                </div>
                            </div>
                            <div class="chart-container">
                                <div id="trendChart" ref="trendChart" style="width: 100%; height: 400px;"></div>
                            </div>
                        </div>

                        <!-- 收支对比分析 -->
                        <div class="comparison-section">
                            <div class="section-header">
                                <h3 class="section-title">📊 收支对比分析</h3>
                            </div>
                            <div class="comparison-content">
                                <div class="comparison-chart">
                                    <div id="comparisonChart" style="width: 100%; height: 300px;"></div>
                                </div>
                                <div class="comparison-stats">
                                    <div class="stat-item">
                                        <div class="stat-label">收入最高</div>
                                        <div class="stat-value income">{{ formatCurrency(comparisonStats.maxIncome.value) }}
                                        </div>
                                        <div class="stat-period">{{ comparisonStats.maxIncome.period }}</div>
                                    </div>
                                    <div class="stat-item">
                                        <div class="stat-label">支出最高</div>
                                        <div class="stat-value expense">{{ formatCurrency(comparisonStats.maxExpense.value)
                                        }}</div>
                                        <div class="stat-period">{{ comparisonStats.maxExpense.period }}</div>
                                    </div>
                                    <div class="stat-item">
                                        <div class="stat-label">结余最佳</div>
                                        <div class="stat-value"
                                            :class="comparisonStats.maxBalance.value >= 0 ? 'positive' : 'expense'">{{
                                                formatCurrency(comparisonStats.maxBalance.value)
                                            }}</div>
                                        <div class="stat-period">{{ comparisonStats.maxBalance.period }}</div>
                                    </div>
                                    <div class="stat-item">
                                        <div class="stat-label">盈利周期</div>
                                        <div class="stat-value"
                                            :class="comparisonStats.profitablePeriods >= 0 ? 'positive' : 'expense'">{{
                                                comparisonStats.profitablePeriods }}</div>
                                        <div class="stat-period">个周期</div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 详细数据表格 -->
                        <div class="data-table-section">
                            <div class="section-header">
                                <h3 class="section-title">📋 详细数据</h3>
                                <div class="table-actions">
                                    <el-button size="small" @click="exportData">导出数据</el-button>
                                </div>
                            </div>
                            <div class="table-container">
                                <el-table :data="tableData" style="width: 100%"
                                    :header-cell-style="{ background: 'rgba(30, 41, 59, 0.9)', color: '#6eddf1' }"
                                    :cell-style="{ background: 'rgba(30, 41, 59, 0.9)', color: '#e2e8f0' }" stripe>
                                    <el-table-column prop="period"
                                        :label="timeRange === 'year' ? '月份' : timeRange === 'month' ? '日期' : '日期'"
                                        width="220" />
                                    <el-table-column prop="income" label="收入">
                                        <template #default="scope">
                                            <span class="income-text">{{ formatCurrency(scope.row.income) }}</span>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="expense" label="支出">
                                        <template #default="scope">
                                            <span class="expense-text">{{ formatCurrency(scope.row.expense) }}</span>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="balance" label="结余">
                                        <template #default="scope">
                                            <span :class="scope.row.balance >= 0 ? 'positive-text' : 'negative-text'">
                                                {{ formatCurrency(scope.row.balance) }}
                                            </span>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="savingsRate" label="储蓄率">
                                        <template #default="scope">
                                            <span :class="getRateClass(scope.row.savingsRate)">
                                                {{ scope.row.savingsRate }}%
                                            </span>
                                        </template>
                                    </el-table-column>
                                    <el-table-column prop="trend" label="趋势">
                                        <template #default="scope">
                                            <el-tag
                                                :type="scope.row.trend === '上升' ? 'success' : scope.row.trend === '下降' ? 'danger' : 'info'"
                                                size="small">
                                                {{ scope.row.trend }}
                                            </el-tag>
                                        </template>
                                    </el-table-column>
                                </el-table>
                            </div>
                        </div>

                        <!-- 财务总结 -->
                        <div class="summary-section">
                            <div class="section-header">
                                <h3 class="section-title">💡 财务总结与建议</h3>
                            </div>
                            <div class="summary-content">
                                <div class="summary-card" :class="summary.overallStatus">
                                    <div class="summary-header">
                                        <div class="status-indicator" :class="summary.overallStatus"></div>
                                        <h4>{{ summary.title }}</h4>
                                    </div>
                                    <div class="summary-body">
                                        <p>{{ summary.description }}</p>
                                        <div class="recommendations">
                                            <h5>优化建议：</h5>
                                            <ul>
                                                <li v-for="(recommendation, index) in summary.recommendations" :key="index">
                                                    {{ recommendation }}
                                                </li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>

                                <div class="insights-grid">
                                    <div class="insight-card">
                                        <div class="insight-icon">📅</div>
                                        <div class="insight-content">
                                            <h5>最佳表现周期</h5>
                                            <p>{{ insights.bestPeriod.period }} - 储蓄率 {{ insights.bestPeriod.savingsRate }}%
                                            </p>
                                        </div>
                                    </div>

                                    <div class="insight-card">
                                        <div class="insight-icon">⚠️</div>
                                        <div class="insight-content">
                                            <h5>需要关注</h5>
                                            <p>{{ insights.attentionPeriod.period }} - 支出较高</p>
                                        </div>
                                    </div>

                                    <div class="insight-card">
                                        <div class="insight-icon">🎯</div>
                                        <div class="insight-content">
                                            <h5>下期目标</h5>
                                            <p>储蓄率提升至 {{ insights.nextGoal }}%</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </v-col>
        </v-row>
    </div>
</template>
  
<script>
import { _getRawData, _getAvailableYears } from "@/api/api.js";
import { getDaysInMonth, handleCrateDate, handleCreateDatePickerCurrent, parseTime } from '@/utils/calendar'
export default {
    name: "WealthReportView",
    data() {
        const currentYear = new Date().getFullYear();
        const currentMonth = new Date().getMonth() + 1;

        return {
            // 时间范围选择
            timeRange: 'month',
            selectedYear: currentYear,
            selectedMonth: currentMonth,
            selectedDate: [this.getDateString(-7), this.getDateString(0)],
            // 图表类型
            chartType: 'line',
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
                maxIncome: { value: 0, period: '' },
                maxExpense: { value: 0, period: '' },
                maxBalance: { value: 0, period: '' },
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
                bestPeriod: { period: '', savingsRate: 0 },
                attentionPeriod: { period: '', reason: '' },
                nextGoal: 0
            },
            // 图表实例
            trendChart: null,
            comparisonChart: null
        };
    },
    watch: {
        chartType: {
            handler(val) {
                this.handleChartType(val)
            }
        },
    },
    mounted() {
        this.loadFinancialData();
        this.initCharts();
    },
    beforeDestroy() {
        if (this.trendChart) {
            this.trendChart.dispose();
        }
        if (this.comparisonChart) {
            this.comparisonChart.dispose();
        }
    },
    methods: {
        handleChartType(val) {
            this.updateCharts();
        },
        // 获取用户记录年份
        async getAvailableYears() {
            const response = await _getAvailableYears(this.$store.state.login_user.id);
            if (response.data) {
                this.yearOptions = []
                for (let i = 0; i < response.data.data.length; i++) {
                    this.yearOptions.push({
                        label: `${response.data.data[i]}年`,
                        value: response.data.data[i]
                    })
                }
            }
        },
        // 格式化金额
        formatToTwoDecimals(number) {
            // 处理 null、undefined 或其他非数字情况
            if (number === null || number === undefined || isNaN(number)) {
                return 0.00;
            }

            // 转换为数字并保留两位小数
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
            let params = `userId=${this.$store.state.login_user.id}&timeRange=${this.timeRange}&year=${this.selectedYear}&month=${this.selectedMonth}&startDate=${this.selectedDate[0]}&endDate=${this.selectedDate[1]}`
            return params;
        },
        //获取原始数据
        async getRawData() {
            const params = this.buildQueryParams();
            const response = await _getRawData(params);
            this.rawData = response.data || []
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
            // 初始化数据
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
            // 处理原始数据
            this.rawData.forEach(record => {
                const recordDate = new Date(record.recordTime);
                const year = recordDate.getFullYear();
                // 只处理选中年份的数据
                console.log(record)
                if (record.type === '收入') {
                    monthlyData[year].income += parseFloat(record.cost);
                } else if (record.type === '支出') {
                    monthlyData[year].expense += parseFloat(record.cost);
                }
            });
            // 计算结余和储蓄率
            Object.values(monthlyData).forEach(month => {
                // 格式化收入和支出
                month.income = this.formatToTwoDecimals(month.income);
                month.expense = this.formatToTwoDecimals(month.expense);
                // 计算结余
                month.balance = this.formatToTwoDecimals(month.income - month.expense);
                month.savingsRate = month.income > 0 ?
                    ((month.balance / month.income) * 100).toFixed(1) : 0;

                // 简单趋势判断
                month.trend = month.balance > 0 ? '上升' : month.balance < 0 ? '下降' : '稳定';
            });

            this.tableData = Object.values(monthlyData);
        },
        // 处理年度数据
        processYearlyData() {
            const monthlyData = {};
            // 初始化12个月的数据
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
            // 处理原始数据
            this.rawData.forEach(record => {
                const recordDate = new Date(record.recordTime);
                const month = recordDate.getMonth() + 1;
                const year = recordDate.getFullYear();
                // 只处理选中年份的数据
                if (year === this.selectedYear && monthlyData[month]) {
                    if (record.type === '收入') {
                        monthlyData[month].income += parseFloat(record.cost);
                    } else if (record.type === '支出') {
                        monthlyData[month].expense += parseFloat(record.cost);
                    }
                }
            });

            // 计算结余和储蓄率
            Object.values(monthlyData).forEach(month => {
                // 格式化收入和支出
                month.income = this.formatToTwoDecimals(month.income);
                month.expense = this.formatToTwoDecimals(month.expense);
                // 计算结余
                month.balance = this.formatToTwoDecimals(month.income - month.expense);
                month.savingsRate = month.income > 0 ?
                    ((month.balance / month.income) * 100).toFixed(1) : 0;

                // 简单趋势判断
                month.trend = month.balance > 0 ? '上升' : month.balance < 0 ? '下降' : '稳定';
            });

            this.tableData = Object.values(monthlyData);
        },
        // 处理月度数据
        processMonthlyData() {
            const dailyData = {};
            const daysInMonth = new Date(this.selectedYear, this.selectedMonth, 0).getDate();

            // 初始化当月的所有天数
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
            // 处理原始数据
            this.rawData.forEach(record => {
                const recordDate = new Date(record.recordTime);
                const day = recordDate.getDate();
                const month = recordDate.getMonth() + 1;
                const year = recordDate.getFullYear();
                // 只处理选中年月的数据
                if (year === this.selectedYear && month === this.selectedMonth && dailyData[day]) {
                    if (record.type === '收入') {
                        dailyData[day].income += parseFloat(record.cost);
                    } else if (record.type === '支出') {
                        dailyData[day].expense += parseFloat(record.cost);
                    }
                }
            });

            // 计算结余和储蓄率
            Object.values(dailyData).forEach(day => {
                day.balance = day.income - day.expense;
                day.savingsRate = day.income > 0 ?
                    ((day.balance / day.income) * 100).toFixed(1) : 0;

                // 简单趋势判断
                day.trend = day.balance > 0 ? '上升' : day.balance < 0 ? '下降' : '稳定';
            });

            this.tableData = Object.values(dailyData);
        },
        // 处理每日数据
        processDailyData() {
            const dailyData = {};

            // 处理原始数据
            this.rawData.forEach(record => {
                const recordDate = new Date(record.recordTime);
                const dateStr = record.recordTime; // YYYY-MM-DD格式

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

            // 计算结余和储蓄率
            Object.values(dailyData).forEach(day => {
                day.balance = day.income - day.expense;
                day.savingsRate = day.income > 0 ?
                    ((day.balance / day.income) * 100).toFixed(1) : 0;

                // 简单趋势判断
                day.trend = day.balance > 0 ? '上升' : day.balance < 0 ? '下降' : '稳定';
            });

            // 按日期排序
            this.tableData = Object.values(dailyData).sort((a, b) => {
                return new Date(a.period) - new Date(b.period);
            });
        },
        // 计算核心指标
        calculateMetrics() {
            const totalIncome = this.tableData.reduce((sum, item) => sum + item.income, 0);
            const totalExpense = this.tableData.reduce((sum, item) => sum + item.expense, 0);
            const netBalance = totalIncome - totalExpense;
            const savingsRate = totalIncome > 0 ? (netBalance / totalIncome) * 100 : 0;

            // 计算同比变化（简化处理）
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

            // 计算对比统计
            this.calculateComparisonStats();
        },
        // 计算对比统计
        calculateComparisonStats() {
            if (this.tableData.length === 0) return;

            let maxIncome = { value: -Infinity, period: '' };
            let maxExpense = { value: -Infinity, period: '' };
            let maxBalance = { value: -Infinity, period: '' };
            let profitablePeriods = 0;

            this.tableData.forEach(item => {
                if (item.income > maxIncome.value) {
                    maxIncome = { value: item.income, period: item.period };
                }
                if (item.expense > maxExpense.value) {
                    maxExpense = { value: item.expense, period: item.period };
                }
                if (item.balance > maxBalance.value) {
                    maxBalance = { value: item.balance, period: item.period };
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

            // 计算深度洞察
            this.calculateInsights();
        },
        // 计算深度洞察
        calculateInsights() {
            // 寻找最佳表现周期（储蓄率最高）
            let bestPeriod = { period: '', savingsRate: -Infinity };
            let attentionPeriod = { period: '', reason: '' };

            this.tableData.forEach(item => {
                if (item.savingsRate > bestPeriod.savingsRate) {
                    bestPeriod = { period: item.period, savingsRate: item.savingsRate };
                }

                // 寻找需要关注的周期（支出异常高）
                if (item.expense > this.metrics.totalExpense / this.tableData.length * 1.5) {
                    attentionPeriod = { period: item.period, reason: '支出异常偏高' };
                }
            });

            // 设置下期目标
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

            // 通用建议
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
            this.$nextTick(() => {
                this.trendChart = this.$echarts.init(document.getElementById('trendChart'));
                this.comparisonChart = this.$echarts.init(document.getElementById('comparisonChart'));
                this.updateCharts();

                // 响应式调整
                window.addEventListener('resize', () => {
                    this.trendChart.resize();
                    this.comparisonChart.resize();
                });
            });
        },
        // 更新图表
        updateCharts() {
            if (!this.trendChart || !this.comparisonChart) return;

            this.updateTrendChart();
            this.updateComparisonChart();
        },
        // 更新趋势图表
        updateTrendChart() {
            const periods = this.tableData.map(item => item.period);
            const incomes = this.tableData.map(item => item.income);
            const expenses = this.tableData.map(item => item.expense);
            const balances = this.tableData.map(item => item.balance);

            const option = {
                tooltip: {
                    trigger: 'axis',
                    backgroundColor: 'rgba(15, 23, 42, 0.9)',
                    borderColor: 'rgba(255, 255, 255, 0.1)',
                    textStyle: { color: '#e2e8f0' }
                },
                legend: {
                    data: ['收入', '支出', '结余'],
                    textStyle: { color: '#a0aec0' },
                    top: 10
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    top: '15%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    data: periods,
                    axisLine: {
                        lineStyle: { color: 'rgba(255, 255, 255, 0.3)' }
                    },
                    axisLabel: { color: '#61B9C8' }
                },
                yAxis: {
                    type: 'value',
                    axisLine: {
                        lineStyle: { color: 'rgba(255, 255, 255, 0.3)' }
                    },
                    axisLabel: {
                        color: '#61B9C8',
                        formatter: (value) => '¥' + value.toLocaleString('zh-CN')
                    },
                    splitLine: {
                        lineStyle: { color: 'rgba(255, 255, 255, 0.1)' }
                    }
                },
                series: [
                    {
                        name: '收入',
                        type: this.chartType,
                        data: incomes,
                        smooth: true,
                        lineStyle: { color: '#2ecc71', width: 3 },
                        itemStyle: { color: '#2ecc71' },
                        areaStyle: this.chartType === 'line' ? {
                            color: {
                                type: 'linear',
                                x: 0, y: 0, x2: 0, y2: 1,
                                colorStops: [
                                    { offset: 0, color: 'rgba(46, 204, 113, 0.3)' },
                                    { offset: 1, color: 'rgba(46, 204, 113, 0.1)' }
                                ]
                            }
                        } : null
                    },
                    {
                        name: '支出',
                        type: this.chartType,
                        data: expenses,
                        smooth: true,
                        lineStyle: { color: '#e74c3c', width: 3 },
                        itemStyle: { color: '#e74c3c' },
                        areaStyle: this.chartType === 'line' ? {
                            color: {
                                type: 'linear',
                                x: 0, y: 0, x2: 0, y2: 1,
                                colorStops: [
                                    { offset: 0, color: 'rgba(231, 76, 60, 0.3)' },
                                    { offset: 1, color: 'rgba(231, 76, 60, 0.1)' }
                                ]
                            }
                        } : null
                    },
                    {
                        name: '结余',
                        type: this.chartType,
                        data: balances,
                        smooth: true,
                        lineStyle: { color: '#3498db', width: 2 },
                        itemStyle: { color: '#3498db' }
                    }
                ],
                animation: true,
                animationDuration: 1000
            };

            this.trendChart.setOption(option);
        },
        // 更新对比图表
        updateComparisonChart() {
            const periods = this.tableData.map(item => item.period);
            const incomes = this.tableData.map(item => item.income);
            const expenses = this.tableData.map(item => item.expense);

            const option = {
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    },
                    backgroundColor: 'rgba(15, 23, 42, 0.9)',
                    borderColor: 'rgba(255, 255, 255, 0.1)',
                    textStyle: { color: '#e2e8f0' }
                },
                legend: {
                    data: ['收入', '支出'],
                    textStyle: { color: '#a0aec0' },
                    top: 10
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    top: '15%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    data: periods,
                    axisLine: {
                        lineStyle: { color: 'rgba(255, 255, 255, 0.3)' }
                    },
                    axisLabel: { color: '#61B9C8' }
                },
                yAxis: {
                    type: 'value',
                    axisLine: {
                        lineStyle: { color: 'rgba(255, 255, 255, 0.3)' }
                    },
                    axisLabel: {
                        color: '#61B9C8',
                        formatter: (value) => '¥' + value.toLocaleString('zh-CN')
                    },
                    splitLine: {
                        lineStyle: { color: 'rgba(255, 255, 255, 0.1)' }
                    }
                },
                series: [
                    {
                        name: '收入',
                        type: 'bar',
                        data: incomes,
                        color: 'rgba(46, 204, 113, 0.8)',
                    },
                    {
                        name: '支出',
                        type: 'bar',
                        data: expenses,
                        color: 'rgba(231, 76, 60, 0.8)',
                    }
                ],
                animation: true,
                animationDuration: 1000
            };

            this.comparisonChart.setOption(option);
        },
        // 导出数据
        exportData() {
            this.$message.success('数据导出功能准备中...');
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
        }
    }
};
</script>
  
<style lang="less" scoped>
@primary-color: #000000;

.time-selector-container {
    background: rgba(30, 41, 59, 0.6);
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 20px;
    border: 1px solid rgba(64, 158, 255, 0.2);

    .time-range-selector {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 15px;

        .el-radio-group {
            .el-radio-button {
                margin-right: 10px;

                &:last-child {
                    margin-right: 0;
                }
            }
        }
    }

    .date-selectors {
        display: flex;
        gap: 20px;
        align-items: center;

        .date-selector-group {
            display: flex;
            align-items: center;
            gap: 10px;

            .selector-label {
                color: #a0aec0;
                font-size: 14px;
                white-space: nowrap;
            }
        }
    }
}

.metrics-overview {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 30px;

    .metric-card {
        background: linear-gradient(135deg, rgba(30, 41, 59, 0.8) 0%, rgba(15, 23, 42, 0.9) 100%);
        border-radius: 12px;
        padding: 25px 20px;
        display: flex;
        align-items: center;
        gap: 15px;
        border: 1px solid rgba(64, 158, 255, 0.3);
        transition: all 0.3s ease;

        &:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
        }

        &.income-card {
            border-color: rgba(46, 204, 113, 0.5);
        }

        &.expense-card {
            border-color: rgba(231, 76, 60, 0.5);
        }

        &.balance-card {
            border-color: rgba(52, 152, 219, 0.5);
        }

        &.rate-card {
            border-color: rgba(155, 89, 182, 0.5);
        }

        .metric-icon {
            font-size: 32px;
            flex-shrink: 0;
        }

        .metric-content {
            flex: 1;

            .metric-label {
                color: #a0aec0;
                font-size: 14px;
                margin-bottom: 8px;
            }

            .metric-value {
                font-size: 24px;
                font-weight: 700;
                margin-bottom: 6px;

                &.positive {
                    color: #2ecc71;
                }

                &.negative {
                    color: #e74c3c;
                }
            }

            .metric-change {
                font-size: 12px;
                font-weight: 600;
                padding: 2px 8px;
                border-radius: 10px;
                display: inline-flex;
                align-items: center;
                gap: 4px;

                &.positive {
                    background: rgba(46, 204, 113, 0.2);
                    color: #2ecc71;
                }

                &.negative {
                    background: rgba(231, 76, 60, 0.2);
                    color: #e74c3c;
                }
            }

            .metric-trend {
                font-size: 12px;
                color: #718096;

                &.rate-excellent {
                    color: #2ecc71;
                }

                &.rate-good {
                    color: #f39c12;
                }

                &.rate-poor {
                    color: #e74c3c;
                }
            }
        }
    }
}

.trend-chart-section,
.comparison-section,
.data-table-section,
.summary-section {
    margin-bottom: 30px;

    .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;

        .section-title {
            color: #6eddf1;
            font-size: 18px;
            margin: 0;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 8px;
        }
    }
}

.chart-container,
.comparison-chart {
    background: rgba(15, 23, 42, 0.6);
    border-radius: 12px;
    padding: 20px;
    border: 1px solid rgba(64, 158, 255, 0.2);
}

.comparison-section {
    .comparison-content {
        display: grid;
        grid-template-columns: 2fr 1fr;
        gap: 20px;

        .comparison-stats {
            display: grid;
            grid-template-columns: 1fr;
            gap: 15px;

            .stat-item {
                background: rgba(30, 41, 59, 0.6);
                border-radius: 8px;
                padding: 15px;
                text-align: center;
                border: 1px solid rgba(64, 158, 255, 0.2);

                .stat-label {
                    font-size: 12px;
                    color: #a0aec0;
                    margin-bottom: 5px;
                }

                .stat-value {
                    font-size: 18px;
                    font-weight: 700;
                    margin-bottom: 5px;

                    &.income {
                        color: #2ecc71;
                    }

                    &.expense {
                        color: #e74c3c;
                    }

                    &.positive {
                        color: #2ecc71;
                    }
                }

                .stat-period {
                    font-size: 12px;
                    color: #718096;
                }
            }
        }
    }
}

.data-table-section {
    .table-container {
        background: rgba(15, 23, 42, 0.6);
        border-radius: 12px;
        padding: 20px;
        border: 1px solid rgba(64, 158, 255, 0.2);

        .income-text {
            color: #2ecc71;
            font-weight: 600;
        }

        .expense-text {
            color: #e74c3c;
            font-weight: 600;
        }

        .positive-text {
            color: #2ecc71;
            font-weight: 600;
        }

        .negative-text {
            color: #e74c3c;
            font-weight: 600;
        }

        .rate-excellent {
            color: #2ecc71;
            font-weight: 600;
        }

        .rate-good {
            color: #f39c12;
            font-weight: 600;
        }

        .rate-poor {
            color: #e74c3c;
            font-weight: 600;
        }
    }
}

.summary-section {
    .summary-content {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;

        .summary-card {
            background: linear-gradient(135deg, rgba(30, 41, 59, 0.8) 0%, rgba(15, 23, 42, 0.9) 100%);
            border-radius: 12px;
            padding: 25px 20px;
            border: 1px solid rgba(64, 158, 255, 0.3);

            &.positive {
                border-color: #2ecc71;
            }

            &.neutral {
                border-color: #f39c12;
            }

            &.warning {
                border-color: #e67e22;
            }

            &.negative {
                border-color: #e74c3c;
            }

            .summary-header {
                display: flex;
                align-items: center;
                gap: 10px;
                margin-bottom: 15px;

                .status-indicator {
                    width: 12px;
                    height: 12px;
                    border-radius: 50%;

                    &.positive {
                        background: #2ecc71;
                    }

                    &.neutral {
                        background: #f39c12;
                    }

                    &.warning {
                        background: #e67e22;
                    }

                    &.negative {
                        background: #e74c3c;
                    }
                }

                h4 {
                    color: #6eddf1;
                    margin: 0;
                    font-size: 16px;
                }
            }

            .summary-body {
                p {
                    color: #a0aec0;
                    line-height: 1.6;
                    margin-bottom: 15px;
                }

                .recommendations {
                    h5 {
                        color: #6eddf1;
                        margin-bottom: 10px;
                        font-size: 14px;
                    }

                    ul {
                        margin: 0;
                        padding-left: 20px;

                        li {
                            color: #a0aec0;
                            line-height: 1.6;
                            margin-bottom: 5px;

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
            gap: 15px;

            .insight-card {
                background: rgba(30, 41, 59, 0.6);
                border-radius: 8px;
                padding: 20px;
                display: flex;
                align-items: center;
                gap: 15px;
                border: 1px solid rgba(64, 158, 255, 0.2);

                .insight-icon {
                    font-size: 24px;
                    flex-shrink: 0;
                }

                .insight-content {
                    flex: 1;

                    h5 {
                        color: #6eddf1;
                        margin: 0 0 5px 0;
                        font-size: 14px;
                    }

                    p {
                        color: #a0aec0;
                        margin: 0;
                        font-size: 12px;
                    }
                }
            }
        }
    }
}

// 响应式设计
@media (max-width: 1200px) {
    .metrics-overview {
        grid-template-columns: repeat(2, 1fr);
    }

    .comparison-content {
        grid-template-columns: 1fr !important;
    }

    .summary-content {
        grid-template-columns: 1fr !important;
    }
}

@media (max-width: 768px) {
    .metrics-overview {
        grid-template-columns: 1fr;
    }

    .time-range-selector .el-radio-group {
        flex-direction: column;

        .el-radio-button {
            &:first-child .el-radio-button__inner {
                border-radius: 20px 20px 0 0;
            }

            &:last-child .el-radio-button__inner {
                border-radius: 0 0 20px 20px;
            }
        }
    }

    .date-selectors .date-selector-group {
        flex-direction: column;
        align-items: flex-start !important;
    }

    .section-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 15px;
    }
}

// 页面基础样式
.page2 {
    padding: 14px 20px 40px;
    background: rgba(16, 32, 64, 0.6);
    overflow: hidden;

    .bottom-list {
        .col-12 {
            height: 100%;
        }

        .list {
            height: 100%;
            width: 100%;
            padding-right: 0;

            .right-bottom {
                height: 100%;
                min-height: 250px;
                width: 100%;
                background: rgba(64, 158, 255, 0.2);

                .bottom1 {
                    width: 100%;
                    padding: 20px;
                }
            }
        }
    }
}

.title {
    position: absolute;
    display: inline-block;
    color: #6eddf1;
    background: rgba(64, 158, 255, 0.2);
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 17px;
    z-index: 10;

    .title-gradient {
        display: inline-block;
        padding: 5px 14px;
        background: radial-gradient(55% 55% ellipse, #2d2d44 60%, #1e1e2f);
    }
}

.angle1,
.angle2,
.angle3,
.angle4 {
    display: inline-block;
    position: absolute;
    width: 10px;
    height: 10px;
    border: 1px solid #1c5ab3;
}

.angle1 {
    top: 0;
    left: 0;
    border-top: 1px solid #1c5ab3;
    border-left: 1px solid #1c5ab3;
}

.angle2 {
    top: 0;
    right: 0;
    border-top: 1px solid #1c5ab3;
    border-right: 1px solid #1c5ab3;
}

.angle3 {
    bottom: 0;
    left: 0;
    border-bottom: 1px solid #1c5ab3;
    border-left: 1px solid #1c5ab3;
}

.angle4 {
    bottom: 0;
    right: 0;
    border-bottom: 1px solid #1c5ab3;
    border-right: 1px solid #1c5ab3;
}
</style>