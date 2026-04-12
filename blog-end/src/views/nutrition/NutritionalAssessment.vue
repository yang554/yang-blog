<template>
  <div id="app" class="nutrition-assessment">
    <!-- 主要内容区域 -->
    <main class="app-main">
      <!-- 快速统计 -->
      <div class="quick-stats">
        <el-row :gutter="20">
          <el-col :xs="12" :sm="6">
            <div class="stat-card calorie">
              <div class="stat-icon">
                <i class="el-icon-fire"></i>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ totalCalories }} kcal</div>
                <div class="stat-label">总热量</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="stat-card protein">
              <div class="stat-icon">
                <i class="el-icon-goblet-square"></i>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ totalProtein }}g</div>
                <div class="stat-label">蛋白质</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="stat-card fat">
              <div class="stat-icon">
                <i class="el-icon-watermelon"></i>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ totalFat }}g</div>
                <div class="stat-label">脂肪</div>
              </div>
            </div>
          </el-col>
          <el-col :xs="12" :sm="6">
            <div class="stat-card carb">
              <div class="stat-icon">
                <i class="el-icon-grape"></i>
              </div>
              <div class="stat-content">
                <div class="stat-value">{{ totalCarbs }}g</div>
                <div class="stat-label">碳水化合物</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-row :gutter="24" class="main-content">
        <!-- 左侧表单 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <div class="form-container">
            <h2 class="section-title">
              <i class="el-icon-edit"></i> 营养数据输入
            </h2>

            <el-form ref="nutritionForm" :model="nutritionForm" :rules="formRules" label-position="top"
              class="nutrition-form">
              <!-- 基本信息 -->
              <el-form-item label="食物名称" prop="foodName">
                <el-input v-model="nutritionForm.foodName" placeholder="请输入食物名称" clearable>
                  <template slot="prepend">
                    <i class="el-icon-food"></i>
                  </template>
                </el-input>
              </el-form-item>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="份量 (克)" prop="portion">
                    <el-input-number v-model="nutritionForm.portion" :min="1" :max="2000" controls-position="right"
                      style="width: 100%;" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="餐次" prop="mealTime">
                    <el-select v-model="nutritionForm.mealTime" placeholder="选择餐次" style="width: 100%;">
                      <el-option label="早餐" value="breakfast"></el-option>
                      <el-option label="午餐" value="lunch"></el-option>
                      <el-option label="晚餐" value="dinner"></el-option>
                      <el-option label="加餐" value="snack"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>

              <!-- 营养成分 -->
              <h3 class="sub-title">营养成分 (每100克)</h3>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="热量 (千卡)" prop="calories">
                    <el-input-number v-model="nutritionForm.calories" :min="0" :max="1000" controls-position="right"
                      style="width: 100%;" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="蛋白质 (克)" prop="protein">
                    <el-input-number v-model="nutritionForm.protein" :min="0" :max="100" :step="0.1"
                      controls-position="right" style="width: 100%;" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="脂肪 (克)" prop="fat">
                    <el-input-number v-model="nutritionForm.fat" :min="0" :max="100" :step="0.1" controls-position="right"
                      style="width: 100%;" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="碳水化合物 (克)" prop="carbs">
                    <el-input-number v-model="nutritionForm.carbs" :min="0" :max="200" :step="0.1"
                      controls-position="right" style="width: 100%;" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-row :gutter="20">
                <el-col :span="12">
                  <el-form-item label="膳食纤维 (克)" prop="fiber">
                    <el-input-number v-model="nutritionForm.fiber" :min="0" :max="50" :step="0.1"
                      controls-position="right" style="width: 100%;" />
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="糖 (克)" prop="sugar">
                    <el-input-number v-model="nutritionForm.sugar" :min="0" :max="100" :step="0.1"
                      controls-position="right" style="width: 100%;" />
                  </el-form-item>
                </el-col>
              </el-row>

              <el-form-item label="备注" prop="notes">
                <el-input v-model="nutritionForm.notes" type="textarea" :rows="3" placeholder="可添加食物烹饪方式、特殊说明等信息"
                  maxlength="100" show-word-limit />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" @click="addFoodItem" icon="el-icon-plus" style="width: 100%;">
                  添加食物
                </el-button>
              </el-form-item>
            </el-form>

            <!-- 已添加食物列表 -->
            <div class="food-list">
              <h3 class="sub-title">已添加食物</h3>
              <div v-if="foodItems.length === 0" class="empty-list">
                <i class="el-icon-dish"></i>
                <p>暂无食物记录，请添加食物开始评估</p>
              </div>
              <div v-else>
                <el-table :data="foodItems" style="width: 100%" max-height="250" empty-text="暂无数据">
                  <el-table-column prop="foodName" label="食物名称" width="120"></el-table-column>
                  <el-table-column prop="portion" label="份量(克)" width="100"></el-table-column>
                  <el-table-column prop="calories" label="热量(kcal)" width="100"></el-table-column>
                  <el-table-column prop="protein" label="蛋白质(g)" width="100"></el-table-column>
                  <el-table-column label="操作" width="80">
                    <template slot-scope="scope">
                      <el-button type="danger" icon="el-icon-delete" circle size="mini"
                        @click="removeFoodItem(scope.$index)"></el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div class="list-actions">
                  <el-button type="primary" @click="calculateNutrition" icon="el-icon-cpu">
                    开始评估
                  </el-button>
                  <el-button @click="clearAllFoods" icon="el-icon-delete">
                    清空列表
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 右侧评估结果 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <div class="result-container">
            <h2 class="section-title">
              <i class="el-icon-data-analysis"></i> 营养评估结果
            </h2>

            <!-- 评估状态 -->
            <div class="assessment-status" v-if="assessmentStatus">
              <div class="status-card" :class="assessmentStatus.level">
                <div class="status-icon">
                  <i :class="assessmentStatus.icon"></i>
                </div>
                <div class="status-content">
                  <h3>{{ assessmentStatus.title }}</h3>
                  <p>{{ assessmentStatus.message }}</p>
                  <div class="score" v-if="assessmentStatus.score">
                    评分: <span class="score-value">{{ assessmentStatus.score }}/100</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 营养成分分布 -->
            <div class="nutrient-distribution" v-if="showResults">
              <h3 class="sub-title">宏量营养素比例</h3>
              <div class="chart-container">
                <div id="nutrientChart" style="height: 200px;"></div>
              </div>

              <div class="macronutrient-bars">
                <div class="macronutrient-item">
                  <div class="macro-label">蛋白质</div>
                  <div class="macro-bar">
                    <div class="macro-fill protein-fill" :style="{ width: proteinPercentage + '%' }"></div>
                  </div>
                  <div class="macro-value">{{ proteinPercentage.toFixed(1) }}%</div>
                </div>
                <div class="macronutrient-item">
                  <div class="macro-label">脂肪</div>
                  <div class="macro-bar">
                    <div class="macro-fill fat-fill" :style="{ width: fatPercentage + '%' }"></div>
                  </div>
                  <div class="macro-value">{{ fatPercentage.toFixed(1) }}%</div>
                </div>
                <div class="macronutrient-item">
                  <div class="macro-label">碳水化合物</div>
                  <div class="macro-bar">
                    <div class="macro-fill carb-fill" :style="{ width: carbPercentage + '%' }"></div>
                  </div>
                  <div class="macro-value">{{ carbPercentage.toFixed(1) }}%</div>
                </div>
              </div>
            </div>

            <!-- 详细建议 -->
            <div class="nutrition-recommendations" v-if="showResults">
              <h3 class="sub-title">营养建议</h3>
              <el-collapse v-model="activeRecommendations">
                <el-collapse-item title="热量摄入" name="1">
                  <div class="recommendation-content">
                    <p v-if="totalCalories < 1500">您今日的热量摄入较低，建议适当增加食物摄入，特别是优质蛋白质和复合碳水化合物的摄入。</p>
                    <p v-else-if="totalCalories >= 1500 && totalCalories <= 2200">您今日的热量摄入处于正常范围，继续保持均衡饮食。</p>
                    <p v-else>您今日的热量摄入较高，建议适当减少高热量食物摄入，增加蔬菜和膳食纤维。</p>
                  </div>
                </el-collapse-item>
                <el-collapse-item title="蛋白质摄入" name="2">
                  <div class="recommendation-content">
                    <p v-if="proteinPercentage < 15">您的蛋白质摄入比例偏低，建议增加鱼、禽、蛋、瘦肉、豆制品的摄入。</p>
                    <p v-else-if="proteinPercentage >= 15 && proteinPercentage <= 25">您的蛋白质摄入比例合理，继续保持。</p>
                    <p v-else>您的蛋白质摄入比例偏高，建议适当减少，特别是红肉和加工肉类的摄入。</p>
                  </div>
                </el-collapse-item>
                <el-collapse-item title="脂肪摄入" name="3">
                  <div class="recommendation-content">
                    <p v-if="fatPercentage < 20">您的脂肪摄入比例偏低，可适量增加健康脂肪来源，如坚果、牛油果、橄榄油。</p>
                    <p v-else-if="fatPercentage >= 20 && fatPercentage <= 30">您的脂肪摄入比例合理，注意选择健康脂肪来源。</p>
                    <p v-else>您的脂肪摄入比例偏高，建议减少油炸食品和高脂肪食物的摄入。</p>
                  </div>
                </el-collapse-item>
                <el-collapse-item title="碳水化合物摄入" name="4">
                  <div class="recommendation-content">
                    <p v-if="carbPercentage < 50">您的碳水化合物摄入比例偏低，可适量增加全谷物、薯类、豆类的摄入。</p>
                    <p v-else-if="carbPercentage >= 50 && carbPercentage <= 65">您的碳水化合物摄入比例合理，建议多选择复合碳水化合物。</p>
                    <p v-else>您的碳水化合物摄入比例偏高，建议减少精制糖和精制碳水化合物的摄入。</p>
                  </div>
                </el-collapse-item>
              </el-collapse>
            </div>

            <!-- 历史记录 -->
            <div class="history-section">
              <h3 class="sub-title">历史记录</h3>
              <div class="history-list">
                <div v-for="(record, index) in historyRecords" :key="index" class="history-item"
                  @click="loadHistoryRecord(record)">
                  <div class="history-date">{{ record.date }}</div>
                  <div class="history-calories">{{ record.totalCalories }} kcal</div>
                  <div class="history-status" :class="record.statusClass">
                    {{ record.status }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </main>

    <!-- 底部信息 -->
    <footer class="app-footer">
      <p>营养评估 &copy; 2023 | 数据仅供参考，实际营养需求因人而异，如有健康问题请咨询专业营养师</p>
    </footer>
  </div>
</template>
<script>
import * as echarts from 'echarts';

export default {
  name: 'NutritionAssessment',
  data() {
    return {
      // 表单数据
      nutritionForm: {
        foodName: '',
        portion: 100,
        mealTime: '',
        calories: 0,
        protein: 0,
        fat: 0,
        carbs: 0,
        fiber: 0,
        sugar: 0,
        notes: ''
      },

      // 表单验证规则
      formRules: {
        foodName: [
          { required: true, message: '请输入食物名称', trigger: 'blur' }
        ],
        portion: [
          { required: true, message: '请输入食物份量', trigger: 'blur' }
        ],
        calories: [
          { required: true, message: '请输入热量', trigger: 'blur' }
        ]
      },

      // 食物列表
      foodItems: [],

      // 营养成分总计
      totalCalories: 0,
      totalProtein: 0,
      totalFat: 0,
      totalCarbs: 0,

      // 营养素比例
      proteinPercentage: 0,
      fatPercentage: 0,
      carbPercentage: 0,

      // 评估结果
      showResults: false,
      assessmentStatus: null,

      // 建议展开项
      activeRecommendations: ['1', '2', '3', '4'],

      // 历史记录
      historyRecords: [
        {
          id: 1,
          date: '2023-10-01',
          totalCalories: 1850,
          proteinPercentage: 18,
          fatPercentage: 25,
          carbPercentage: 57,
          status: '良好',
          statusClass: 'good'
        },
        {
          id: 2,
          date: '2023-09-30',
          totalCalories: 2100,
          proteinPercentage: 15,
          fatPercentage: 30,
          carbPercentage: 55,
          status: '正常',
          statusClass: 'normal'
        },
        {
          id: 3,
          date: '2023-09-29',
          totalCalories: 1650,
          proteinPercentage: 22,
          fatPercentage: 28,
          carbPercentage: 50,
          status: '良好',
          statusClass: 'good'
        }
      ],

      // ECharts实例
      nutrientChart: null
    };
  },

  mounted() {
    // 初始化图表
    this.$nextTick(() => {
      this.initChart();
    });
  },

  methods: {
    // 添加食物项
    addFoodItem() {
      this.$refs.nutritionForm.validate((valid) => {
        if (valid) {
          // 根据份量计算实际营养值
          const portionFactor = this.nutritionForm.portion / 100;

          const foodItem = {
            id: Date.now(),
            foodName: this.nutritionForm.foodName,
            portion: this.nutritionForm.portion,
            mealTime: this.nutritionForm.mealTime,
            calories: (this.nutritionForm.calories * portionFactor).toFixed(1),
            protein: (this.nutritionForm.protein * portionFactor).toFixed(1),
            fat: (this.nutritionForm.fat * portionFactor).toFixed(1),
            carbs: (this.nutritionForm.carbs * portionFactor).toFixed(1),
            fiber: (this.nutritionForm.fiber * portionFactor).toFixed(1),
            sugar: (this.nutritionForm.sugar * portionFactor).toFixed(1),
            notes: this.nutritionForm.notes
          };

          this.foodItems.push(foodItem);

          // 更新总计
          this.updateTotalNutrition();

          // 重置表单
          this.resetForm();

          this.$message({
            message: '食物添加成功',
            type: 'success'
          });
        } else {
          this.$message.error('请填写完整的食物信息');
          return false;
        }
      });
    },

    // 移除食物项
    removeFoodItem(index) {
      this.foodItems.splice(index, 1);
      this.updateTotalNutrition();
    },

    // 清空所有食物
    clearAllFoods() {
      this.$confirm('确定要清空所有食物记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.foodItems = [];
        this.resetTotals();
        this.$message({
          message: '已清空所有食物记录',
          type: 'success'
        });
      });
    },

    // 重置表单
    resetForm() {
      this.nutritionForm = {
        foodName: '',
        portion: 100,
        mealTime: '',
        calories: 0,
        protein: 0,
        fat: 0,
        carbs: 0,
        fiber: 0,
        sugar: 0,
        notes: ''
      };

      // 重置表单验证状态
      this.$refs.nutritionForm.clearValidate();
    },

    // 重置总计
    resetTotals() {
      this.totalCalories = 0;
      this.totalProtein = 0;
      this.totalFat = 0;
      this.totalCarbs = 0;
      this.proteinPercentage = 0;
      this.fatPercentage = 0;
      this.carbPercentage = 0;
      this.showResults = false;
      this.assessmentStatus = null;
    },

    // 更新总营养值
    updateTotalNutrition() {
      this.totalCalories = 0;
      this.totalProtein = 0;
      this.totalFat = 0;
      this.totalCarbs = 0;

      this.foodItems.forEach(item => {
        this.totalCalories += parseFloat(item.calories) || 0;
        this.totalProtein += parseFloat(item.protein) || 0;
        this.totalFat += parseFloat(item.fat) || 0;
        this.totalCarbs += parseFloat(item.carbs) || 0;
      });

      // 计算热量百分比（每克蛋白质和碳水化合物提供4千卡，每克脂肪提供9千卡）
      const caloriesFromProtein = this.totalProtein * 4;
      const caloriesFromFat = this.totalFat * 9;
      const caloriesFromCarbs = this.totalCarbs * 4;
      const totalCaloriesFromMacros = caloriesFromProtein + caloriesFromFat + caloriesFromCarbs;

      if (totalCaloriesFromMacros > 0) {
        this.proteinPercentage = (caloriesFromProtein / totalCaloriesFromMacros) * 100;
        this.fatPercentage = (caloriesFromFat / totalCaloriesFromMacros) * 100;
        this.carbPercentage = (caloriesFromCarbs / totalCaloriesFromMacros) * 100;
      } else {
        this.proteinPercentage = 0;
        this.fatPercentage = 0;
        this.carbPercentage = 0;
      }
    },

    // 计算营养评估
    calculateNutrition() {
      if (this.foodItems.length === 0) {
        this.$message.warning('请先添加食物');
        return;
      }

      this.showResults = true;

      // 计算评估分数
      let score = 100;
      let statusLevel = 'good';
      let statusTitle = '营养均衡';
      let statusMessage = '您的饮食结构合理，继续保持！';
      let statusIcon = 'el-icon-success';

      // 评估逻辑
      if (this.totalCalories < 1200) {
        score -= 30;
        statusLevel = 'warning';
        statusTitle = '热量不足';
        statusMessage = '您摄入的热量可能不足以满足身体基本需求';
        statusIcon = 'el-icon-warning';
      } else if (this.totalCalories > 2500) {
        score -= 25;
        statusLevel = 'warning';
        statusTitle = '热量偏高';
        statusMessage = '您摄入的热量可能超过日常需求';
        statusIcon = 'el-icon-warning';
      }

      if (this.proteinPercentage < 10) {
        score -= 20;
        statusLevel = 'warning';
        statusTitle = '蛋白质不足';
        statusMessage = '蛋白质摄入不足，建议增加优质蛋白来源';
        statusIcon = 'el-icon-warning';
      } else if (this.proteinPercentage > 35) {
        score -= 15;
        statusLevel = 'warning';
        statusTitle = '蛋白质偏高';
        statusMessage = '蛋白质摄入偏高，可能增加肾脏负担';
        statusIcon = 'el-icon-warning';
      }

      if (this.fatPercentage > 35) {
        score -= 15;
        statusLevel = 'warning';
        statusTitle = '脂肪偏高';
        statusMessage = '脂肪摄入比例偏高，建议减少高脂肪食物';
        statusIcon = 'el-icon-warning';
      }

      if (score < 70) {
        statusLevel = 'poor';
        statusTitle = '需要改善';
        statusMessage = '您的饮食结构有待改善，请参考下方建议';
        statusIcon = 'el-icon-error';
      } else if (score >= 70 && score < 85) {
        statusLevel = 'normal';
        statusTitle = '基本合格';
        statusMessage = '您的饮食结构基本合理，仍有改进空间';
        statusIcon = 'el-icon-info';
      }

      this.assessmentStatus = {
        level: statusLevel,
        title: statusTitle,
        message: statusMessage,
        icon: statusIcon,
        score: score
      };

      // 更新图表
      this.updateChart();

      // 添加到历史记录
      this.addToHistory(score);

      this.$message.success('营养评估完成');
    },

    // 初始化图表
    initChart() {
      const chartDom = document.getElementById('nutrientChart');
      if (!chartDom) return;

      this.nutrientChart = echarts.init(chartDom);

      // 初始图表配置
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c}% ({d}%)'
        },
        legend: {
          orient: 'vertical',
          right: 10,
          top: 'center',
          data: ['蛋白质', '脂肪', '碳水化合物']
        },
        series: [
          {
            name: '营养素比例',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['40%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '16',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: 0, name: '蛋白质', itemStyle: { color: '#409EFF' } },
              { value: 0, name: '脂肪', itemStyle: { color: '#E6A23C' } },
              { value: 0, name: '碳水化合物', itemStyle: { color: '#67C23A' } }
            ]
          }
        ]
      };

      this.nutrientChart.setOption(option);

      // 响应窗口大小变化
      window.addEventListener('resize', () => {
        if (this.nutrientChart) {
          this.nutrientChart.resize();
        }
      });
    },

    // 更新图表
    updateChart() {
      if (!this.nutrientChart) {
        this.initChart();
      }

      const option = {
        series: [{
          data: [
            { value: this.proteinPercentage, name: '蛋白质', itemStyle: { color: '#409EFF' } },
            { value: this.fatPercentage, name: '脂肪', itemStyle: { color: '#E6A23C' } },
            { value: this.carbPercentage, name: '碳水化合物', itemStyle: { color: '#67C23A' } }
          ]
        }]
      };

      this.nutrientChart.setOption(option);
    },

    // 添加到历史记录
    addToHistory(score) {
      const today = new Date();
      const dateString = today.toISOString().split('T')[0];

      // 检查今天是否已有记录
      const existingIndex = this.historyRecords.findIndex(record => record.date === dateString);

      const newRecord = {
        id: Date.now(),
        date: dateString,
        totalCalories: Math.round(this.totalCalories),
        proteinPercentage: parseFloat(this.proteinPercentage.toFixed(1)),
        fatPercentage: parseFloat(this.fatPercentage.toFixed(1)),
        carbPercentage: parseFloat(this.carbPercentage.toFixed(1)),
        status: this.assessmentStatus.title,
        statusClass: this.assessmentStatus.level
      };

      if (existingIndex >= 0) {
        // 更新现有记录
        this.historyRecords.splice(existingIndex, 1, newRecord);
      } else {
        // 添加到记录列表开头
        this.historyRecords.unshift(newRecord);

        // 限制历史记录数量
        if (this.historyRecords.length > 10) {
          this.historyRecords = this.historyRecords.slice(0, 10);
        }
      }
    },

    // 加载历史记录
    loadHistoryRecord(record) {
      this.totalCalories = record.totalCalories;
      this.proteinPercentage = record.proteinPercentage;
      this.fatPercentage = record.fatPercentage;
      this.carbPercentage = record.carbPercentage;

      // 重新计算总蛋白质、脂肪和碳水化合物
      const totalCaloriesFromMacros = this.totalCalories;
      this.totalProtein = (record.proteinPercentage / 100 * totalCaloriesFromMacros) / 4;
      this.totalFat = (record.fatPercentage / 100 * totalCaloriesFromMacros) / 9;
      this.totalCarbs = (record.carbPercentage / 100 * totalCaloriesFromMacros) / 4;

      // 设置评估状态
      let statusLevel = 'normal';
      let statusIcon = 'el-icon-info';

      if (record.statusClass === 'good') {
        statusLevel = 'good';
        statusIcon = 'el-icon-success';
      } else if (record.statusClass === 'poor') {
        statusLevel = 'poor';
        statusIcon = 'el-icon-error';
      } else if (record.statusClass === 'warning') {
        statusLevel = 'warning';
        statusIcon = 'el-icon-warning';
      }

      this.assessmentStatus = {
        level: statusLevel,
        title: record.status,
        message: `历史记录：${record.date}`,
        icon: statusIcon,
        score: 85 // 假设分数
      };

      this.showResults = true;
      this.updateChart();

      this.$message({
        message: `已加载 ${record.date} 的记录`,
        type: 'info'
      });
    }
  },

  beforeDestroy() {
    // 清理图表实例
    if (this.nutrientChart) {
      this.nutrientChart.dispose();
    }
  }
};
</script>
<style scoped>
.nutrition-assessment {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  color: #333;
  background-color: #f8fafc;
  min-height: 100vh;
}

/* 快速统计*/
.quick-stats {
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 10px;
  padding: 20px 15px;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
  display: flex;
  align-items: center;
  transition: transform 0.3s, box-shadow 0.3s;
  border-top: 4px solid #0f172a;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.12);
}

.stat-card.calorie {
  border-top-color: #ef4444; /* 红色 - 热量 */
  background: linear-gradient(135deg, #fef2f2 0%, #fecaca 100%);
}

.stat-card.protein {
  border-top-color: #3b82f6; /* 蓝色 - 蛋白质 */
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
}

.stat-card.fat {
  border-top-color: #f59e0b; /* 琥珀色 - 脂肪 */
  background: linear-gradient(135deg, #fffbeb 0%, #fde68a 100%);
}

.stat-card.carb {
  border-top-color: #10b981; /* 绿色 - 碳水化合物 */
  background: linear-gradient(135deg, #ecfdf5 0%, #a7f3d0 100%);
}

.stat-icon {
  font-size: 32px;
  margin-right: 15px;
  width: 60px;
  text-align: center;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-card.calorie .stat-icon {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.1);
}

.stat-card.protein .stat-icon {
  color: #3b82f6;
  background: rgba(59, 130, 246, 0.1);
}

.stat-card.fat .stat-icon {
  color: #f59e0b;
  background: rgba(245, 158, 11, 0.1);
}

.stat-card.carb .stat-icon {
  color: #10b981;
  background: rgba(16, 185, 129, 0.1);
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-card.calorie .stat-value {
  color: #dc2626;
}

.stat-card.protein .stat-value {
  color: #1d4ed8;
}

.stat-card.fat .stat-value {
  color: #b45309;
}

.stat-card.carb .stat-value {
  color: #047857;
}

.stat-label {
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
}

/* 主要内容区域 */
.main-content {
  margin-bottom: 40px;
}

/* 容器样式 */
.form-container, .result-container {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 16px rgba(15, 23, 42, 0.08);
  height: 100%;
  border: 1px solid #e2e8f0;
}

.section-title {
  color: #0f172a;
  margin-top: 0;
  margin-bottom: 25px;
  font-size: 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  padding-bottom: 12px;
  border-bottom: 2px solid #f1f5f9;
}

.sub-title {
  color: #475569;
  margin: 20px 0 15px;
  font-size: 16px;
  font-weight: 600;
}

/* 表单样式 - 调整按钮和输入框 */
.nutrition-form {
  margin-bottom: 30px;
}

.el-button--primary:hover {
  background-color: #1e293b;
  border-color: #1e293b;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.2);
}

/* 输入框和选择框焦点状态 */
.el-input__inner:focus, .el-textarea__inner:focus {
  border-color: #0f172a;
}

.el-select .el-input.is-focus .el-input__inner {
  border-color: #0f172a;
}

/* 食物列表 */
.food-list {
  margin-top: 30px;
  border-top: 2px solid #f1f5f9;
  padding-top: 20px;
}

.empty-list {
  text-align: center;
  padding: 40px 20px;
  color: #94a3b8;
}

.empty-list i {
  font-size: 48px;
  margin-bottom: 15px;
  display: block;
  color: #cbd5e1;
}

.list-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

/* 评估状态 */
.assessment-status {
  margin-bottom: 30px;
}

.status-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 10px;
  background: #f8fafc;
  border-left: 5px solid #0f172a;
}

.status-card.good {
  border-left-color: #10b981;
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
}

.status-card.normal {
  border-left-color: #3b82f6;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
}

.status-card.warning {
  border-left-color: #f59e0b;
  background: linear-gradient(135deg, #fefce8 0%, #fef3c7 100%);
}

.status-card.poor {
  border-left-color: #ef4444;
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
}

.status-icon {
  font-size: 36px;
  margin-right: 20px;
  width: 60px;
  text-align: center;
}

.status-card.good .status-icon {
  color: #10b981;
}

.status-card.normal .status-icon {
  color: #3b82f6;
}

.status-card.warning .status-icon {
  color: #f59e0b;
}

.status-card.poor .status-icon {
  color: #ef4444;
}

.status-content h3 {
  margin: 0 0 8px;
  font-size: 18px;
  color: #0f172a;
}

.status-content p {
  margin: 0 0 10px;
  color: #475569;
}

.score {
  font-size: 14px;
  color: #64748b;
}

.score-value {
  font-weight: bold;
  font-size: 16px;
  color: #0f172a;
}

/* 宏量营养素条形图 */
.macronutrient-bars {
  margin-top: 20px;
  background: #f8fafc;
  padding: 20px;
  border-radius: 10px;
}

.macronutrient-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.macro-label {
  width: 120px;
  font-weight: 600;
  color: #475569;
}

.macro-bar {
  flex-grow: 1;
  height: 20px;
  background-color: #e2e8f0;
  border-radius: 10px;
  overflow: hidden;
  margin: 0 15px;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
}

.macro-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 1s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.protein-fill {
  background: linear-gradient(90deg, #3b82f6, #60a5fa);
}

.fat-fill {
  background: linear-gradient(90deg, #f59e0b, #fbbf24);
}

.carb-fill {
  background: linear-gradient(90deg, #10b981, #34d399);
}

.macro-value {
  width: 60px;
  text-align: right;
  font-weight: 600;
  color: #0f172a;
}

/* 建议区域 */
.nutrition-recommendations {
  margin-top: 30px;
}

.el-collapse {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.el-collapse-item__header {
  background-color: #f8fafc;
  color: #0f172a;
  font-weight: 600;
  border-bottom: 1px solid #e2e8f0;
  padding: 0 15px;
  height: 48px;
  line-height: 48px;
}

.el-collapse-item__content {
  color: #475569;
  line-height: 1.6;
  padding: 15px;
}

.recommendation-content {
  color: #475569;
  line-height: 1.6;
}

/* 历史记录 */
.history-section {
  margin-top: 30px;
}

.history-list {
  max-height: 250px;
  overflow-y: auto;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  border-bottom: 1px solid #e2e8f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.history-item:hover {
  background-color: #f8fafc;
}

.history-item:last-child {
  border-bottom: none;
}

.history-date {
  font-weight: 500;
  color: #0f172a;
}

.history-calories {
  color: #ef4444;
  font-weight: 600;
}

.history-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  min-width: 70px;
  text-align: center;
}

.history-status.good {
  background-color: #d1fae5;
  color: #065f46;
}

.history-status.normal {
  background-color: #dbeafe;
  color: #1e40af;
}

.history-status.warning {
  background-color: #fef3c7;
  color: #92400e;
}

.history-status.poor {
  background-color: #fee2e2;
  color: #991b1b;
}

/* 图表容器 */
.chart-container {
  height: 200px;
  margin-bottom: 20px;
  background: #f8fafc;
  border-radius: 10px;
  padding: 15px;
}

/* 底部样式 */
.app-footer {
  text-align: center;
  padding: 20px;
  color: #64748b;
  font-size: 14px;
  border-top: 1px solid #e2e8f0;
  background-color: #f8fafc;
}

/* 响应式调整 */
@media (max-width: 768px) {
  
  .stat-value {
    font-size: 20px;
  }
  
  .stat-icon {
    font-size: 24px;
    margin-right: 10px;
    width: 50px;
    height: 50px;
  }
  
  .form-container, .result-container {
    padding: 15px;
  }
  
  .status-icon {
    font-size: 28px;
    margin-right: 15px;
    width: 50px;
  }
  
  .macro-label {
    width: 90px;
  }
}
</style>