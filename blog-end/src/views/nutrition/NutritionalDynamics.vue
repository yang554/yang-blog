<template>
    <div class="nutrition-dashboard">
        <!-- 实时进度条 -->
        <div class="realtime-progress">
            <el-row :gutter="20">
                <el-col :xs="24" :sm="6">
                    <div class="progress-card">
                        <div class="progress-title">
                            <i class="el-icon-fire"></i>
                            <span>热量</span>
                        </div>
                        <div class="progress-content">
                            <el-progress :percentage="calorieProgress" :color="calorieColor" :stroke-width="12"
                                :show-text="false" />
                            <div class="progress-details">
                                <span class="progress-value">{{ currentCalories }} / {{ calorieGoal }} kcal</span>
                                <span class="progress-percentage">{{ calorieProgress }}%</span>
                            </div>
                        </div>
                    </div>
                </el-col>
                <el-col :xs="24" :sm="6">
                    <div class="progress-card">
                        <div class="progress-title">
                            <i class="el-icon-goblet-square"></i>
                            <span>蛋白质</span>
                        </div>
                        <div class="progress-content">
                            <el-progress :percentage="proteinProgress" color="#3b82f6" :stroke-width="12"
                                :show-text="false" />
                            <div class="progress-details">
                                <span class="progress-value">{{ currentProtein }} / {{ proteinGoal }} g</span>
                                <span class="progress-percentage">{{ proteinProgress }}%</span>
                            </div>
                        </div>
                    </div>
                </el-col>
                <el-col :xs="24" :sm="6">
                    <div class="progress-card">
                        <div class="progress-title">
                            <i class="el-icon-watermelon"></i>
                            <span>脂肪</span>
                        </div>
                        <div class="progress-content">
                            <el-progress :percentage="fatProgress" color="#f59e0b" :stroke-width="12" :show-text="false" />
                            <div class="progress-details">
                                <span class="progress-value">{{ currentFat }} / {{ fatGoal }} g</span>
                                <span class="progress-percentage">{{ fatProgress }}%</span>
                            </div>
                        </div>
                    </div>
                </el-col>
                <el-col :xs="24" :sm="6">
                    <div class="progress-card">
                        <div class="progress-title">
                            <i class="el-icon-grape"></i>
                            <span>碳水</span>
                        </div>
                        <div class="progress-content">
                            <el-progress :percentage="carbProgress" color="#10b981" :stroke-width="12" :show-text="false" />
                            <div class="progress-details">
                                <span class="progress-value">{{ currentCarbs }} / {{ carbGoal }} g</span>
                                <span class="progress-percentage">{{ carbProgress }}%</span>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>

        <!-- 主内容区 -->
        <main class="dashboard-main">
            <el-row :gutter="20" class="main-content">
                <!-- 左侧 - 营养趋势图表 -->
                <el-col :xs="24" :sm="24" :md="16" :lg="16">
                    <div class="trends-container">
                        <!-- 图表区域 -->
                        <div class="chart-section">
                            <div class="chart-header">
                                <h3><i class="el-icon-trend-charts"></i> 营养摄入趋势</h3>
                                <div class="chart-controls">
                                    <el-radio-group v-model="chartTimeRange" size="mini" @change="updateChart">
                                        <el-radio-button label="day">今日</el-radio-button>
                                        <el-radio-button label="week">本周</el-radio-button>
                                        <el-radio-button label="month">本月</el-radio-button>
                                    </el-radio-group>
                                </div>
                            </div>
                            <div class="chart-wrapper">
                                <div id="nutritionTrendChart" style="height: 300px;"></div>
                            </div>
                        </div>

                        <!-- 营养分布 -->
                        <div class="distribution-section">
                            <div class="distribution-header">
                                <h3><i class="el-icon-pie-chart"></i> 实时营养分布</h3>
                                <el-tag type="info" size="small">自动更新</el-tag>
                            </div>
                            <div class="distribution-content">
                                <div class="distribution-chart">
                                    <div id="nutritionDistributionChart" style="height: 240px;"></div>
                                </div>
                                <div class="distribution-details">
                                    <div class="distribution-item" v-for="item in distributionData" :key="item.name">
                                        <div class="distribution-label">
                                            <div class="distribution-color" :style="{ backgroundColor: item.color }"></div>
                                            <span>{{ item.name }}</span>
                                        </div>
                                        <div class="distribution-values">
                                            <span class="distribution-amount">{{ item.value }}g</span>
                                            <span class="distribution-percentage">{{ item.percentage }}%</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </el-col>

                <!-- 右侧 - 动态信息和目标 -->
                <el-col :xs="24" :sm="24" :md="8" :lg="8">
                    <div class="dynamic-sidebar">
                        <!-- 今日摄入记录 -->
                        <div class="today-intake-section">
                            <div class="section-header">
                                <h3><i class="el-icon-notebook-2"></i> 今日摄入记录</h3>
                                <el-button type="text" size="mini" @click="showAllRecords = !showAllRecords">
                                    {{ showAllRecords ? '收起' : '查看全部' }}
                                </el-button>
                            </div>
                            <div class="intake-list">
                                <div v-if="todayIntake.length === 0" class="empty-intake">
                                    <i class="el-icon-dish"></i>
                                    <p>暂无记录，添加食物开始追踪</p>
                                </div>
                                <div v-else>
                                    <div v-for="(item, index) in (showAllRecords ? todayIntake : todayIntake.slice(0, 3))"
                                        :key="index" class="intake-item">
                                        <div class="intake-item-main">
                                            <div class="intake-food">{{ item.food }}</div>
                                            <div class="intake-time">{{ item.time }}</div>
                                        </div>
                                        <div class="intake-item-details">
                                            <div class="intake-calories">{{ item.calories }} kcal</div>
                                            <div class="intake-macros">
                                                <span class="macro-tag protein">{{ item.protein }}g 蛋白</span>
                                                <span class="macro-tag fat">{{ item.fat }}g 脂肪</span>
                                                <span class="macro-tag carb">{{ item.carbs }}g 碳水</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 营养目标 -->
                        <div class="nutrition-goals-section">
                            <div class="section-header">
                                <h3><i class="el-icon-aim"></i> 营养目标</h3>
                                <el-button type="text" size="mini" icon="el-icon-edit" @click="editGoals"></el-button>
                            </div>
                            <div class="goals-list">
                                <div class="goal-item">
                                    <div class="goal-label">
                                        <i class="el-icon-fire"></i>
                                        <span>热量目标</span>
                                    </div>
                                    <div class="goal-value">{{ calorieGoal }} kcal</div>
                                </div>
                                <div class="goal-item">
                                    <div class="goal-label">
                                        <i class="el-icon-goblet-square"></i>
                                        <span>蛋白质目标</span>
                                    </div>
                                    <div class="goal-value">{{ proteinGoal }} g</div>
                                </div>
                                <div class="goal-item">
                                    <div class="goal-label">
                                        <i class="el-icon-watermelon"></i>
                                        <span>脂肪目标</span>
                                    </div>
                                    <div class="goal-value">{{ fatGoal }} g</div>
                                </div>
                                <div class="goal-item">
                                    <div class="goal-label">
                                        <i class="el-icon-grape"></i>
                                        <span>碳水目标</span>
                                    </div>
                                    <div class="goal-value">{{ carbGoal }} g</div>
                                </div>
                            </div>
                        </div>

                        <!-- 健康提醒 -->
                        <div class="health-reminders-section">
                            <div class="section-header">
                                <h3><i class="el-icon-bell"></i> 健康提醒</h3>
                                <el-button type="text" size="mini" icon="el-icon-more"
                                    @click="showReminders = !showReminders"></el-button>
                            </div>
                            <div class="reminders-list">
                                <div v-for="(reminder, index) in (showReminders ? healthReminders : healthReminders.slice(0, 2))"
                                    :key="index" class="reminder-item" :class="reminder.type">
                                    <div class="reminder-icon">
                                        <i :class="reminder.icon"></i>
                                    </div>
                                    <div class="reminder-content">
                                        <div class="reminder-title">{{ reminder.title }}</div>
                                        <div class="reminder-message">{{ reminder.message }}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </el-col>
            </el-row>
        </main>

        <!-- 底部统计 -->
        <footer class="dashboard-footer">
            <div class="footer-stats">
                <el-row :gutter="20">
                    <el-col :xs="12" :sm="6">
                        <div class="footer-stat">
                            <div class="footer-stat-value">{{ todayIntake.length }}</div>
                            <div class="footer-stat-label">今日食物种类</div>
                        </div>
                    </el-col>
                    <el-col :xs="12" :sm="6">
                        <div class="footer-stat">
                            <div class="footer-stat-value">{{ calorieBalance > 0 ? '+' : '' }}{{ calorieBalance }}</div>
                            <div class="footer-stat-label">热量结余 (kcal)</div>
                        </div>
                    </el-col>
                    <el-col :xs="12" :sm="6">
                        <div class="footer-stat">
                            <div class="footer-stat-value">{{ dailyStreak }}</div>
                            <div class="footer-stat-label">连续记录天数</div>
                        </div>
                    </el-col>
                    <el-col :xs="12" :sm="6">
                        <div class="footer-stat">
                            <div class="footer-stat-value">{{ overallScore }}/100</div>
                            <div class="footer-stat-label">营养评分</div>
                        </div>
                    </el-col>
                </el-row>
            </div>
        </footer>

        <!-- 目标编辑对话框 -->
        <el-dialog title="编辑营养目标" :visible.sync="goalDialogVisible" width="500px">
            <el-form :model="goalForm" label-width="120px">
                <el-form-item label="热量目标 (kcal)">
                    <el-input-number v-model="goalForm.calorieGoal" :min="1000" :max="5000" :step="100"
                        style="width: 100%;" />
                </el-form-item>
                <el-form-item label="蛋白质目标 (g)">
                    <el-input-number v-model="goalForm.proteinGoal" :min="20" :max="200" :step="5" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="脂肪目标 (g)">
                    <el-input-number v-model="goalForm.fatGoal" :min="20" :max="150" :step="5" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="碳水目标 (g)">
                    <el-input-number v-model="goalForm.carbGoal" :min="100" :max="500" :step="10" style="width: 100%;" />
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="goalDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveGoals">保存</el-button>
            </span>
        </el-dialog>
    </div>
</template>
<script>
import { _getDietPlanByUid, _getDietPlanByDateScope } from "@/api/api.js";
import { getTime, parseTime, getCurrentWeekRange, getCurrentMonthRange } from "../../utils/formatDate";
export default {
    name: 'NutritionDashboard',
    data() {
        return {
            // 当前日期
            currentDate: parseTime(Date.now(), '{y}-{m}-{d}'),
            //当前用户
            currentUser: this.$store.state.login_user,

            // 实时进度数据
            currentCalories: 0,
            currentProtein: 0,
            currentFat: 0,
            currentCarbs: 0,

            // 营养目标
            calorieGoal: 2000,
            proteinGoal: 100,
            fatGoal: 65,
            carbGoal: 250,

            // 图表时间范围
            chartTimeRange: 'day',

            // 今日摄入记录
            todayIntake: [],
            showAllRecords: false,

            // 营养分布数据
            distributionData: [
                { name: '蛋白质', value: 68, percentage: 20, color: '#3b82f6' },
                { name: '脂肪', value: 45, percentage: 30, color: '#f59e0b' },
                { name: '碳水化合物', value: 162, percentage: 50, color: '#10b981' }
            ],

            //营养趋势数据
            xData: [],
            yData: {},
            xWeekData: [],
            yWeekData: [],
            xMonthData: [],
            yMonthData: [],

            // 健康提醒
            healthReminders: [
                {
                    type: 'info',
                    icon: 'el-icon-info',
                    title: '饮水提醒',
                    message: '今日已饮水1.2L，建议再喝600ml'
                },
                {
                    type: 'success',
                    icon: 'el-icon-success',
                    title: '目标达成',
                    message: '蛋白质摄入已达标'
                },
                {
                    type: 'warning',
                    icon: 'el-icon-warning',
                    title: '脂肪偏高',
                    message: '脂肪摄入接近上限，注意控制'
                },
                {
                    type: 'info',
                    icon: 'el-icon-time',
                    title: '下一餐提醒',
                    message: '距离晚餐还有2小时'
                }
            ],
            showReminders: false,

            // 目标编辑
            goalDialogVisible: false,
            goalForm: {
                calorieGoal: 2000,
                proteinGoal: 100,
                fatGoal: 65,
                carbGoal: 250
            },

            // 底部统计
            dailyStreak: 7,
            overallScore: 86,

            // 图表实例
            trendChart: null,
            distributionChart: null
        };
    },

    computed: {
        // 计算进度百分比
        calorieProgress() {
            return Math.min(Math.round((this.currentCalories / this.calorieGoal) * 100), 100);
        },

        proteinProgress() {
            return Math.min(Math.round((this.currentProtein / this.proteinGoal) * 100), 100);
        },

        fatProgress() {
            return Math.min(Math.round((this.currentFat / this.fatGoal) * 100), 100);
        },

        carbProgress() {
            return Math.min(Math.round((this.currentCarbs / this.carbGoal) * 100), 100);
        },

        // 进度条颜色
        calorieColor() {
            if (this.calorieProgress < 70) return '#10b981';
            if (this.calorieProgress < 90) return '#f59e0b';
            return '#ef4444';
        },

        // 热量结余
        calorieBalance() {
            return this.calorieGoal - this.currentCalories;
        }
    },
    created() {
        //获取指定日期的营养数据【当日】
        _getDietPlanByUid(this.currentUser.id, this.currentDate).then((res) => {
            if (res.data) {
                this.currentCalories = res.data.energy;
                this.currentProtein = res.data.protein;
                this.currentFat = res.data.fat;
                this.currentCarbs = res.data.carbonWater;

                this.todayIntake = [];
                const foods = res.data.planInfoEntities;
                foods.forEach(item => {
                    let i = {
                        food: item.foodName,
                        time: item.time,
                        calories: item.energy,
                        protein: item.protein,
                        fat: item.fat,
                        carbs: item.carbonWater
                    }
                    this.todayIntake.push(i)
                });
                const { xData, yData } = this.processNutritionData(foods)

                this.xData = xData;
                this.yData = yData;
            }

            // 初始化图表
            this.$nextTick(() => {
                this.initMealBasedChart()
                this.initDistributionChart();
            });
        })

        //获取本周营养数据
        _getDietPlanByDateScope(this.currentUser.id, getCurrentWeekRange().startDate, getCurrentWeekRange().endDate).then((res) => {
            if (res.data) {
                const dataList = res.data;
                const { xData, yData } = this.processWeeklyDataWithDates(dataList)
                this.xWeekData = xData;
                this.yWeekData = yData;

            }
        })

        //获取本月的营养数据
        _getDietPlanByDateScope(this.currentUser.id, getCurrentMonthRange().startDate, getCurrentMonthRange().endDate).then((res) => {
            if (res.data) {
                const dataList = res.data;
                const { xData, yData } = this.processMonthlyDataWithDates(dataList)
                this.xMonthData = xData;
                this.yMonthData = yData;
            }
        })
    },
    mounted() {
        // 模拟实时数据更新
        // this.startRealTimeUpdates();
    },

    methods: {
        // 处理今日营养数据
        processNutritionData(apiData) {
            // 使用餐次作为X轴坐标
            const mealTypes = ['早餐', '加餐', '午餐', '加餐', '晚餐', '夜宵']

            // 按餐次分组并计算营养总量
            const nutritionByMeal = {}
            mealTypes.forEach(meal => {
                nutritionByMeal[meal] = {
                    energy: 0,
                    protein: 0,
                    fat: 0,
                    carbonWater: 0,
                    count: 0 // 记录该餐有多少种食物
                }
            })

            // 统计每个餐次的营养数据
            apiData.forEach(food => {
                const meal = food.time
                if (nutritionByMeal[meal]) {
                    nutritionByMeal[meal].energy += parseFloat(food.energy || 0)
                    nutritionByMeal[meal].protein += parseFloat(food.protein || 0)
                    nutritionByMeal[meal].fat += parseFloat(food.fat || 0)
                    nutritionByMeal[meal].carbonWater += parseFloat(food.carbonWater || 0)
                    nutritionByMeal[meal].count += 1
                }
            })

            // 生成每个餐次的营养数据
            const energyData = []
            const proteinData = []
            const fatData = []
            const carbData = []

            mealTypes.forEach(meal => {
                energyData.push(Math.round(nutritionByMeal[meal].energy))
                proteinData.push(Math.round(nutritionByMeal[meal].protein * 10) / 10)
                fatData.push(Math.round(nutritionByMeal[meal].fat * 10) / 10)
                carbData.push(Math.round(nutritionByMeal[meal].carbonWater * 10) / 10)
            })

            return {
                xData: mealTypes,
                yData: {
                    '热量': energyData,
                    '蛋白质': proteinData,
                    '脂肪': fatData,
                    '碳水化合物': carbData
                }
            }
        },

        //处理本周营养数据
        processWeeklyDataWithDates(apiData) {
            // 需要先按日期排序
            const sortedData = [...apiData].sort((a, b) => {
                return new Date(a.createDate) - new Date(b.createDate)
            })

            // 获取本周的日期范围
            const weekRange = getCurrentWeekRange()

            // 创建本周每天的数据映射
            const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
            const weeklyNutrition = {}

            // 初始化
            weekDays.forEach(day => {
                weeklyNutrition[day] = {
                    energy: 0,
                    protein: 0,
                    fat: 0,
                    carbonWater: 0,
                    date: null,
                    hasData: false
                }
            })

            // 遍历API数据，匹配到对应的星期几
            sortedData.forEach(dayData => {
                const dataDate = new Date(dayData.createDate)
                // 判断是否在本周范围内
                if (dataDate >= weekRange.start && dataDate <= weekRange.end) {
                    // 获取是星期几（0=周日，1=周一，...）
                    const dayOfWeek = dataDate.getDay()
                    // 转换为索引（0=周一，6=周日）
                    const dayIndex = dayOfWeek === 0 ? 6 : dayOfWeek - 1

                    if (dayIndex >= 0 && dayIndex < 7) {
                        const dayName = weekDays[dayIndex]
                        weeklyNutrition[dayName] = {
                            energy: parseFloat(dayData.energy || 0),
                            protein: parseFloat(dayData.protein || 0),
                            fat: parseFloat(dayData.fat || 0),
                            carbonWater: parseFloat(dayData.carbonWater || 0),
                            date: dayData.createDate,
                            hasData: true
                        }
                    }
                }
            })

            // 提取数据
            const energyData = []
            const proteinData = []
            const fatData = []
            const carbData = []

            weekDays.forEach(day => {
                const data = weeklyNutrition[day]
                energyData.push(Math.round(data.energy))
                proteinData.push(Math.round(data.protein * 10) / 10)
                fatData.push(Math.round(data.fat * 10) / 10)
                carbData.push(Math.round(data.carbonWater * 10) / 10)
            })

            return {
                xData: weekDays,
                yData: {
                    '热量': energyData,
                    '蛋白质': proteinData,
                    '脂肪': fatData,
                    '碳水化合物': carbData
                },
                rawData: weeklyNutrition
            }
        },

        //处理本月营养数据
        processMonthlyDataWithDates(apiData) {
            // 获取本月范围
            const monthRange = getCurrentMonthRange()

            // 计算本月有几周
            const firstDay = new Date(monthRange.start)
            const lastDay = new Date(monthRange.end)

            // 计算本月总天数
            const totalDays = Math.ceil((lastDay - firstDay) / (1000 * 60 * 60 * 24)) + 1

            // 计算周数（按实际日历周）
            const weekCount = Math.ceil((totalDays + firstDay.getDay() - 1) / 7)

            // 创建周标签
            const weekLabels = []
            for (let i = 1; i <= weekCount; i++) {
                weekLabels.push(`第${i}周`)
            }

            // 初始化周数据
            const weeklyData = {}
            weekLabels.forEach(label => {
                weeklyData[label] = {
                    energy: 0,
                    protein: 0,
                    fat: 0,
                    carbonWater: 0,
                    days: 0,
                    dateRange: ''
                }
            })

            // 按实际日期分组
            apiData.forEach(dayData => {
                const dataDate = new Date(dayData.createDate)

                // 确保数据在本月范围内
                if (dataDate >= monthRange.start && dataDate <= monthRange.end) {
                    // 计算属于第几周（基于日历周）
                    const dayOfMonth = dataDate.getDate()
                    const weekNumber = Math.ceil((dayOfMonth + firstDay.getDay() - 1) / 7)
                    const weekLabel = `第${weekNumber}周`

                    if (weeklyData[weekLabel]) {
                        weeklyData[weekLabel].energy += parseFloat(dayData.energy || 0)
                        weeklyData[weekLabel].protein += parseFloat(dayData.protein || 0)
                        weeklyData[weekLabel].fat += parseFloat(dayData.fat || 0)
                        weeklyData[weekLabel].carbonWater += parseFloat(dayData.carbonWater || 0)
                        weeklyData[weekLabel].days += 1
                    }
                }
            })

            // 生成每周范围字符串（用于tooltip显示）
            weekLabels.forEach((weekLabel, index) => {
                const weekData = weeklyData[weekLabel]
                if (weekData.days > 0) {
                    // 计算该周的平均值
                    weekData.avgEnergy = Math.round(weekData.energy / weekData.days)
                    weekData.avgProtein = Math.round((weekData.protein / weekData.days) * 10) / 10
                    weekData.avgFat = Math.round((weekData.fat / weekData.days) * 10) / 10
                    weekData.avgCarb = Math.round((weekData.carbonWater / weekData.days) * 10) / 10

                    // 设置周范围
                    const startDay = index * 7 + 1
                    const endDay = Math.min(startDay + 6, totalDays)
                    weekData.dateRange = `${startDay}-${endDay}日`
                }
            })

            // 提取平均值用于图表
            const energyData = []
            const proteinData = []
            const fatData = []
            const carbData = []

            weekLabels.forEach(weekLabel => {
                const week = weeklyData[weekLabel]
                if (week.days > 0) {
                    energyData.push(week.avgEnergy || 0)
                    proteinData.push(week.avgProtein || 0)
                    fatData.push(week.avgFat || 0)
                    carbData.push(week.avgCarb || 0)
                } else {
                    energyData.push(0)
                    proteinData.push(0)
                    fatData.push(0)
                    carbData.push(0)
                }
            })

            return {
                xData: weekLabels,
                yData: {
                    '热量': energyData,
                    '蛋白质': proteinData,
                    '脂肪': fatData,
                    '碳水化合物': carbData
                },
                weeklyDetails: weeklyData
            }
        },

        // 初始化基于餐次的图表
        initMealBasedChart() {
            const chartDom = document.getElementById('nutritionTrendChart')
            if (!chartDom) return

            this.trendChart = this.$echarts.init(chartDom)

            const option = {
                backgroundColor: 'transparent',
                tooltip: {
                    trigger: 'axis',
                    backgroundColor: 'rgba(15, 23, 42, 0.9)',
                    borderColor: '#0f172a',
                    textStyle: { color: '#fff' },
                    formatter: (params) => {
                        let result = `<div style="margin-bottom: 5px;">${params[0].name}</div>`
                        params.forEach(param => {
                            const value = param.value === 0 ? '0' : param.value.toFixed(1)
                            result += `
            <div style="display: flex; align-items: center; margin: 3px 0;">
              <span style="display: inline-block; width: 10px; height: 10px; background-color: ${param.color}; border-radius: 50%; margin-right: 8px;"></span>
              <span style="flex: 1;">${param.seriesName}:</span>
              <span style="font-weight: bold; margin-left: 10px;">${value}${param.seriesName === '热量' ? ' kcal' : ' g'}</span>
            </div>
          `
                        })
                        return result
                    }
                },
                legend: {
                    data: ['热量', '蛋白质', '脂肪', '碳水化合物'],
                    textStyle: { color: '#475569' },
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
                    data: this.xData,
                    axisLine: {
                        lineStyle: { color: '#cbd5e1' }
                    },
                    axisLabel: {
                        color: '#64748b',
                        fontWeight: 'bold'
                    }
                },
                yAxis: [
                    {
                        type: 'value',
                        name: '营养摄入量 (g)',
                        position: 'left',
                        axisLine: {
                            lineStyle: { color: '#cbd5e1' }
                        },
                        axisLabel: {
                            color: '#64748b',
                            formatter: '{value} g'
                        },
                        splitLine: {
                            lineStyle: {
                                color: '#e2e8f0',
                                type: 'dashed'
                            }
                        }
                    },
                    {
                        type: 'value',
                        name: '热量 (kcal)',
                        position: 'right',
                        axisLine: {
                            lineStyle: { color: '#ef4444' }
                        },
                        axisLabel: {
                            color: '#ef4444',
                            formatter: '{value} kcal'
                        }
                    }
                ],
                series: [
                    {
                        name: '热量',
                        type: 'line',
                        data: this.yData['热量'],
                        smooth: true,
                        lineStyle: {
                            width: 3,
                            color: '#ef4444'
                        },
                        itemStyle: {
                            color: '#ef4444'
                        },
                        areaStyle: {
                            color: new this.$echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                { offset: 0, color: 'rgba(239, 68, 68, 0.5)' },
                                { offset: 1, color: 'rgba(239, 68, 68, 0.1)' }
                            ])
                        }
                    },
                    {
                        name: '蛋白质',
                        type: 'line',
                        data: this.yData['蛋白质'],
                        smooth: true,
                        lineStyle: {
                            width: 2,
                            color: '#3b82f6'
                        },
                        itemStyle: {
                            color: '#3b82f6'
                        }
                    },
                    {
                        name: '脂肪',
                        type: 'line',
                        data: this.yData['脂肪'],
                        smooth: true,
                        lineStyle: {
                            width: 2,
                            color: '#f59e0b'
                        },
                        itemStyle: {
                            color: '#f59e0b'
                        }
                    },
                    {
                        name: '碳水化合物',
                        type: 'line',
                        data: this.yData['碳水化合物'],
                        smooth: true,
                        lineStyle: {
                            width: 2,
                            color: '#10b981'
                        },
                        itemStyle: {
                            color: '#10b981'
                        }
                    }
                ]
            }

            this.trendChart.setOption(option)
        },

        // 更新营养分布数据
        updateDistributionData() {
            // 使用当前累计的总营养值
            const totalProtein = this.currentProtein
            const totalFat = this.currentFat
            const totalCarbs = this.currentCarbs
            const total = totalProtein + totalFat + totalCarbs

            if (total === 0) {
                this.distributionData = [
                    { name: '蛋白质', value: 0, percentage: 0, color: '#3b82f6' },
                    { name: '脂肪', value: 0, percentage: 0, color: '#f59e0b' },
                    { name: '碳水化合物', value: 0, percentage: 0, color: '#10b981' }
                ]
            } else {
                this.distributionData = [
                    {
                        name: '蛋白质',
                        value: Math.round(totalProtein * 10) / 10,
                        percentage: Math.round((totalProtein / total) * 100),
                        color: '#3b82f6'
                    },
                    {
                        name: '脂肪',
                        value: Math.round(totalFat * 10) / 10,
                        percentage: Math.round((totalFat / total) * 100),
                        color: '#f59e0b'
                    },
                    {
                        name: '碳水化合物',
                        value: Math.round(totalCarbs * 10) / 10,
                        percentage: Math.round((totalCarbs / total) * 100),
                        color: '#10b981'
                    }
                ]
            }

            // 更新分布图表
            if (this.distributionChart) {
                this.distributionChart.setOption({
                    series: [{
                        data: this.distributionData.map(item => ({
                            value: item.value,
                            name: item.name,
                            itemStyle: { color: item.color }
                        }))
                    }]
                })
            }
        },

        // 编辑目标
        editGoals() {
            this.goalForm = {
                calorieGoal: this.calorieGoal,
                proteinGoal: this.proteinGoal,
                fatGoal: this.fatGoal,
                carbGoal: this.carbGoal
            };
            this.goalDialogVisible = true;
        },

        // 保存目标
        saveGoals() {
            this.calorieGoal = this.goalForm.calorieGoal;
            this.proteinGoal = this.goalForm.proteinGoal;
            this.fatGoal = this.goalForm.fatGoal;
            this.carbGoal = this.goalForm.carbGoal;

            this.goalDialogVisible = false;
            this.$message.success('营养目标已更新');
        },

        // 初始化分布图表
        initDistributionChart() {
            const chartDom = document.getElementById('nutritionDistributionChart');
            if (!chartDom) return;

            this.distributionChart = this.$echarts.init(chartDom);

            const option = {
                tooltip: {
                    trigger: 'item',
                    formatter: '{a} <br/>{b}: {c}g ({d}%)',
                    backgroundColor: 'rgba(15, 23, 42, 0.9)',
                    borderColor: '#0f172a',
                    textStyle: { color: '#fff' }
                },
                series: [
                    {
                        name: '营养分布',
                        type: 'pie',
                        radius: ['50%', '70%'],
                        avoidLabelOverlap: false,
                        label: {
                            show: false
                        },
                        emphasis: {
                            label: {
                                show: true,
                                fontSize: '14',
                                fontWeight: 'bold'
                            }
                        },
                        labelLine: {
                            show: false
                        },
                        data: this.distributionData.map(item => ({
                            value: item.value,
                            name: item.name,
                            itemStyle: { color: item.color }
                        }))
                    }
                ]
            };

            this.distributionChart.setOption(option);
        },

        // 更新图表
        updateCharts() {
            if (this.distributionChart) {
                const option = {
                    series: [{
                        data: this.distributionData.map(item => ({
                            value: item.value,
                            name: item.name,
                            itemStyle: { color: item.color }
                        }))
                    }]
                };
                this.distributionChart.setOption(option);
            }
        },

        // 更新趋势图表（根据时间范围）
        updateChart() {
            if (!this.trendChart) return

            let xData, energyData, proteinData, fatData, carbData

            switch (this.chartTimeRange) {
                case 'day':
                    // 使用餐次数据
                    xData = this.xData
                    energyData = this.yData['热量']
                    proteinData = this.yData['蛋白质']
                    fatData = this.yData['脂肪']
                    carbData = this.yData['碳水化合物']
                    break

                case 'week':
                    // 按周展示（示例数据）
                    xData = this.xWeekData
                    energyData = this.yWeekData['热量']
                    proteinData = this.yWeekData['蛋白质']
                    fatData = this.yWeekData['脂肪']
                    carbData = this.yWeekData['碳水化合物']
                    break

                case 'month':
                    // 按月展示（示例数据）
                    xData = this.xMonthData
                    energyData = this.yMonthData['热量']
                    proteinData = this.yMonthData['蛋白质']
                    fatData = this.yMonthData['脂肪']
                    carbData = this.yMonthData['碳水化合物']
                    break
            }

            const option = {
                xAxis: {
                    data: xData
                },
                series: [
                    { data: energyData },
                    { data: proteinData },
                    { data: fatData },
                    { data: carbData }
                ]
            }

            this.trendChart.setOption(option)
        },

        // 模拟实时数据更新
        startRealTimeUpdates() {
            // 每30秒更新一次数据（模拟实时）
            setInterval(() => {
                // 随机小幅度更新当前数据（模拟新数据进入）
                const randomChange = () => (Math.random() - 0.5) * 10;

                this.currentCalories = Math.max(0, this.currentCalories + randomChange());
                this.currentProtein = Math.max(0, this.currentProtein + randomChange() / 10);
                this.currentFat = Math.max(0, this.currentFat + randomChange() / 10);
                this.currentCarbs = Math.max(0, this.currentCarbs + randomChange() / 5);

                // 更新分布数据
                this.updateDistributionData();

                // 更新图表
                this.updateCharts();

            }, 30000); // 30秒
        }
    },

    beforeDestroy() {
        // 清理图表实例
        if (this.trendChart) {
            this.trendChart.dispose();
        }
        if (this.distributionChart) {
            this.distributionChart.dispose();
        }
    }
};
</script>
<style scoped>
.nutrition-dashboard {
    font-family: 'Helvetica Neue', Arial, sans-serif;
    color: #333;
    background-color: #f8fafc;
    min-height: 100vh;
}

/* 实时进度条 */
.realtime-progress {
    margin: 20px auto;
    padding: 0 20px;
}

.progress-card {
    background: white;
    border-radius: 10px;
    padding: 15px;
    box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
    border: 1px solid #e2e8f0;
    transition: transform 0.3s;
}

.progress-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(15, 23, 42, 0.12);
}

.progress-title {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;
    color: #475569;
    font-weight: 600;
}

.progress-title i {
    font-size: 18px;
}

.progress-content {
    position: relative;
}

.progress-details {
    display: flex;
    justify-content: space-between;
    margin-top: 8px;
}

.progress-value {
    font-size: 14px;
    font-weight: 600;
    color: #0f172a;
}

.progress-percentage {
    font-size: 14px;
    font-weight: 600;
    color: #3b82f6;
}

/* 主内容区 */
.dashboard-main {
    margin: 20px auto;
    padding: 0 20px;
}

/* 左侧 - 趋势图表区 */
.trends-container {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.chart-section,
.distribution-section {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 16px rgba(15, 23, 42, 0.08);
    border: 1px solid #e2e8f0;
}

.chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.chart-header h3 {
    margin: 0;
    font-size: 18px;
    color: #0f172a;
    display: flex;
    align-items: center;
    gap: 8px;
}

.chart-wrapper {
    height: 300px;
}

.distribution-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}

.distribution-header h3 {
    margin: 0;
    font-size: 18px;
    color: #0f172a;
    display: flex;
    align-items: center;
    gap: 8px;
}

.distribution-content {
    display: flex;
    gap: 30px;
    align-items: center;
}

.distribution-chart {
    flex: 1;
    height: 240px;
}

.distribution-details {
    flex: 1;
}

.distribution-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f1f5f9;
}

.distribution-item:last-child {
    border-bottom: none;
}

.distribution-label {
    display: flex;
    align-items: center;
    gap: 10px;
}

.distribution-color {
    width: 12px;
    height: 12px;
    border-radius: 50%;
}

.distribution-values {
    display: flex;
    align-items: center;
    gap: 15px;
}

.distribution-amount {
    font-weight: 600;
    color: #0f172a;
}

.distribution-percentage {
    font-weight: 600;
    color: #64748b;
    min-width: 40px;
}

/* 右侧 - 动态信息区 */
.dynamic-sidebar {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.quick-add-section,
.today-intake-section,
.nutrition-goals-section,
.health-reminders-section {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 16px rgba(15, 23, 42, 0.08);
    border: 1px solid #e2e8f0;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}

.section-header h3 {
    margin: 0;
    font-size: 16px;
    color: #0f172a;
    display: flex;
    align-items: center;
    gap: 8px;
}

.quick-add-form {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

/* 今日摄入记录 */
.intake-list {
    max-height: 400px;
    overflow-y: auto;
}

.empty-intake {
    text-align: center;
    padding: 30px 20px;
    color: #94a3b8;
}

.empty-intake i {
    font-size: 36px;
    margin-bottom: 10px;
    display: block;
    color: #cbd5e1;
}

.intake-item {
    padding: 12px 0;
    border-bottom: 1px solid #f1f5f9;
    position: relative;
}

.intake-item:last-child {
    border-bottom: none;
}

.intake-item-main {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
}

.intake-food {
    font-weight: 600;
    color: #0f172a;
}

.intake-time {
    font-size: 12px;
    color: #94a3b8;
    background-color: #f1f5f9;
    padding: 2px 8px;
    border-radius: 10px;
}

.intake-item-details {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.intake-calories {
    font-weight: 600;
    color: #ef4444;
}

.intake-macros {
    display: flex;
    gap: 8px;
}

.macro-tag {
    font-size: 11px;
    padding: 2px 6px;
    border-radius: 4px;
}

.macro-tag.protein {
    background-color: rgba(59, 130, 246, 0.1);
    color: #1d4ed8;
}

.macro-tag.fat {
    background-color: rgba(245, 158, 11, 0.1);
    color: #b45309;
}

.macro-tag.carb {
    background-color: rgba(16, 185, 129, 0.1);
    color: #047857;
}

.intake-actions {
    position: absolute;
    top: 12px;
    right: 0;
}

/* 营养目标 */
.goals-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.goal-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px solid #f1f5f9;
}

.goal-item:last-child {
    border-bottom: none;
}

.goal-label {
    display: flex;
    align-items: center;
    gap: 10px;
    color: #475569;
}

.goal-label i {
    font-size: 16px;
}

.goal-value {
    font-weight: 600;
    color: #0f172a;
}

/* 健康提醒 */
.reminders-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.reminder-item {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    padding: 12px;
    border-radius: 8px;
}

.reminder-item.info {
    background-color: rgba(59, 130, 246, 0.05);
    border-left: 3px solid #3b82f6;
}

.reminder-item.success {
    background-color: rgba(16, 185, 129, 0.05);
    border-left: 3px solid #10b981;
}

.reminder-item.warning {
    background-color: rgba(245, 158, 11, 0.05);
    border-left: 3px solid #f59e0b;
}

.reminder-icon {
    font-size: 18px;
    margin-top: 2px;
}

.reminder-item.info .reminder-icon {
    color: #3b82f6;
}

.reminder-item.success .reminder-icon {
    color: #10b981;
}

.reminder-item.warning .reminder-icon {
    color: #f59e0b;
}

.reminder-content {
    flex: 1;
}

.reminder-title {
    font-weight: 600;
    color: #0f172a;
    margin-bottom: 4px;
}

.reminder-message {
    font-size: 13px;
    color: #64748b;
    line-height: 1.4;
}

/* 底部统计 */
.dashboard-footer {
    margin: 30px auto 20px;
    padding: 0 20px;
}

.footer-stats {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 16px rgba(15, 23, 42, 0.08);
    border: 1px solid #e2e8f0;
}

.footer-stat {
    text-align: center;
}

.footer-stat-value {
    font-size: 28px;
    font-weight: 700;
    color: #0f172a;
    margin-bottom: 5px;
}

.footer-stat-label {
    font-size: 13px;
    color: #64748b;
}

/* 响应式调整 */
@media (max-width: 768px) {
    .header-content {
        flex-direction: column;
        gap: 15px;
        text-align: center;
    }

    .current-date {
        margin-left: 0;
        margin-top: 5px;
        display: block;
    }

    .distribution-content {
        flex-direction: column;
        gap: 20px;
    }

    .distribution-details {
        width: 100%;
    }

    .footer-stat-value {
        font-size: 22px;
    }
}

@media (max-width: 992px) {
    .main-content {
        flex-direction: column;
    }

    .trends-container,
    .dynamic-sidebar {
        width: 100%;
    }
}
</style>