<template>
    <div class="food-container">
        <!-- 主要内容区 -->
        <div class="main-food">
            <!-- 左侧主内容 -->
            <div class="main-content">
                <!-- 推荐区域 -->
                <div class="recommended-for-you">
                    <h2 class="recommended-title">
                        为您推荐的今日食谱
                    </h2>
                    <p>根据您的饮食偏好和营养需求，我们为您精心挑选了以下食谱</p>
                    <div class="user-preferences">
                        <div class="preferences-header">
                            <p>当前偏好设置：</p>
                            <el-button type="text" @click="showPreferenceDialog = true" icon="el-icon-edit">
                                修改偏好
                            </el-button>
                        </div>
                        <div class="preferences-list">
                            <div v-for="(pref, index) in currentPreferences" :key="index" class="preference-item">
                                {{ pref }}
                            </div>
                        </div>
                    </div>
                </div>
                <!-- 偏好设置对话框 -->
                <el-dialog title="设置饮食偏好" :visible.sync="showPreferenceDialog" width="600px">
                    <div class="preference-dialog">
                        <p>请选择您的饮食偏好（可多选）：</p>
                        <div class="preference-options">
                            <el-checkbox-group v-model="selectedPreferences">
                                <el-checkbox v-for="pref in allPreferences" :key="pref" :label="pref">{{ pref
                                }}</el-checkbox>
                            </el-checkbox-group>
                        </div>
                        <div slot="footer" class="dialog-footer">
                            <el-button @click="showPreferenceDialog = false">取 消</el-button>
                            <el-button type="primary" @click="savePreferences">保 存</el-button>
                        </div>
                    </div>
                </el-dialog>
                <!-- 食谱筛选 -->
                <div class="card">
                    <div class="card-header">
                        <i class="fas fa-filter"></i> 食谱筛选
                    </div>
                    <div class="card-body">
                        <div class="foodTypes">
                            <div v-for="(item, index) in foodTypes" :key="index" class="filter-item"
                                :class="{ active: activeFilter === item }" @click="activeFilter = item">
                                {{ item }}
                            </div>
                        </div>

                        <div class="recipes-grid">
                            <!-- 食谱卡片 -->
                            <div v-for="recipe in filteredRecipes" :key="recipe.fID" class="recipe-card">
                                <img :src="recipe.fImg" class="recipe-image">
                                <div class="recipe-content">
                                    <el-tooltip class="item" effect="dark" :content=recipe.fIntro placement="top">
                                        <h3 class="recipe-title">{{ recipe.fName }}</h3>
                                    </el-tooltip>
                                    <p>{{ recipe.fIntro | ellipsis }}</p>
                                    <div class="nutrition-info">
                                        <div class="nutrition-item">
                                            <div class="nutrition-value">{{ recipe.calories }} kcal</div>
                                            <div class="nutrition-label">热量</div>
                                        </div>
                                        <div class="nutrition-item">
                                            <div class="nutrition-value">{{ recipe.protein }}g</div>
                                            <div class="nutrition-label">蛋白质</div>
                                        </div>
                                        <div class="nutrition-item">
                                            <div class="nutrition-value">{{ recipe.carbs }}g</div>
                                            <div class="nutrition-label">碳水</div>
                                        </div>
                                        <div class="nutrition-item">
                                            <div class="nutrition-value">{{ recipe.fat }}g</div>
                                            <div class="nutrition-label">脂肪</div>
                                        </div>
                                    </div>

                                    <div class="recipe-actions">
                                        <el-button type="primary" icon="el-icon-plus" size="small"
                                            @click="addToPlanShow(recipe)">
                                            添加到计划
                                        </el-button>
                                        <el-button :icon="recipe.isCol === '1' ? 'el-icon-star-on' : 'el-icon-star-off'"
                                            size="small" @click="toggleFavorite(recipe)">
                                            {{ recipe.isCol === '1' ? '已收藏' : '收藏' }}
                                        </el-button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 右侧边栏 -->
            <div class="sidebar">
                <!-- 营养摘要 -->
                <div class="card">
                    <div class="card-header">
                        <i class="fas fa-chart-pie"></i> 今日营养摘要
                    </div>
                    <div class="card-body">
                        <div class="nutrition-summary">
                            <div class="summary-item">
                                <span>热量摄入</span>
                                <span class="summary-value">{{ this.sumCalories }}kcal</span>
                            </div>

                            <div class="summary-item">
                                <span>蛋白质</span>
                                <span class="summary-value">{{ this.sumProtein }}g</span>
                            </div>

                            <div class="summary-item">
                                <span>碳水化合物</span>
                                <span class="summary-value">{{ this.sumCarbs }}g</span>
                            </div>

                            <div class="summary-item">
                                <span>脂肪</span>
                                <span class="summary-value">{{ this.sumFat }}g</span>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- 添加计划 -->
                <div>
                    <el-dialog title="添加到计划" :visible.sync="dialogFormVisible" width="250px">
                        <el-select v-model="dialogFormValue">
                            <el-option label="早餐" value="早餐"></el-option>
                            <el-option label="午餐" value="午餐"></el-option>
                            <el-option label="晚餐" value="晚餐"></el-option>
                            <el-option label="加餐" value="加餐"></el-option>
                        </el-select>
                        <div slot="footer" class="dialog-footer">
                            <el-button @click="dialogFormVisible = false">取 消</el-button>
                            <el-button type="primary" @click="addToPlan">确 定</el-button>
                        </div>
                    </el-dialog>
                </div>
                <!-- 每日计划 -->
                <div class="card">
                    <div class="card-header">
                        <i class="fas fa-calendar-day"></i> 今日饮食计划
                    </div>
                    <div class="card-body">
                        <div class="daily-plan">
                            <div v-for="(item, index) in dailyPlan" :key="index" class="plan-item">
                                <div class="plan-time">{{ item.time }}</div>
                                <div class="plan-meal">{{ item.foodName }}</div>
                                <div class="plan-actions">
                                    <el-button icon="el-icon-minus" circle size="mini" @click="replaceMeal(item)">
                                    </el-button>
                                </div>
                            </div>
                        </div>

                        <el-button type="primary" icon="el-icon-download" class="save-plan-btn" @click="savePlan">
                            保存计划
                        </el-button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 页脚 -->
        <div class="footer">
            <p>科学饮食，健康生活</p>
            <p>数据更新于 {{ currentDate }}</p>
        </div>
    </div>
</template>
  
<script>
import { _getFoodType, _getAllByUserCol, _addFoodLikesOrCollectionCount,_getDietPlanByUid, _operationDietPlan } from "@/api/api.js";
import { getTime, parseTime } from "../../utils/formatDate";
export default {
    name: 'RecommendedRecipes',
    data() {
        return {
            dialogFormVisible: false,
            dialogFormValue: "早餐",
            activeFilter: '全部',
            foodTypes: [],
            foods: [],
            tempRecipe: [],
            planID:"",
            typeOp:'',
            dailyPlan: [],
            currentDate: new Date().toLocaleDateString('zh-CN', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            }),
            sumCalories: 0,
            sumProtein: 0,
            sumCarbs: 0,
            sumFat: 0,
            showPreferenceDialog: false,
            allPreferences: ['低脂', '高蛋白', '低碳水', '无麸质', '素食', '低糖', '高纤维', '中式风味', '西式风味', '日式风味'],
            selectedPreferences: [],
            currentPreferences: ['低脂', '高蛋白', '低碳水', '中式风味'],
        };
    },
    computed: {
        filteredRecipes() {
            if (this.activeFilter === '全部') {
                return this.foods;
            } else if (this.activeFilter === '我的收藏') {
                return this.foods.filter(food => food.isCol === '1');
            } else {
                return this.foods.filter(food => food.fType === this.activeFilter);
            }
        },
        totalSum() {
            if (this.tempRecipe.calories) {
                this.sumCalories = Number(this.sumCalories) + Number(this.tempRecipe.calories)
                this.sumCalories = parseFloat(this.sumCalories).toFixed(2);
            }
            if (this.tempRecipe.carbs) {
                this.sumCarbs = Number(this.sumCarbs) + Number(this.tempRecipe.carbs)
                this.sumCarbs = parseFloat(this.sumCarbs).toFixed(2);
            }
            if (this.tempRecipe.protein) {
                this.sumProtein = Number(this.sumProtein) + Number(this.tempRecipe.protein)
                this.sumProtein = parseFloat(this.sumProtein).toFixed(2);
            }
            if (this.tempRecipe.fat) {
                this.sumFat = Number(this.sumFat) + Number(this.tempRecipe.fat)
                this.sumFat = parseFloat(this.sumFat).toFixed(2);
            }
        },

    },
    filters: {
        ellipsis(value) {
            if (!value) return '';
            if (value.length > 29) {
                return value.slice(0, 29) + '...'
            }
            return value
        },
    },
    methods: {
        init() {
            //获取所有类型
            _getFoodType().then((res) => {
                this.foodTypes = res.data
                this.foodTypes.unshift('全部')
                this.foodTypes.push('我的收藏')
            })
            //获取所有食谱
            _getAllByUserCol(this.$store.state.login_user.id).then((res) => {
                this.foods = res.data
            })
            //获取当日饮食计划
            _getDietPlanByUid(this.$store.state.login_user.id,parseTime(Date.now(),'{y}-{m}-{d}')).then((res) => {
                if(res.data){
                    this.dailyPlan = res.data.planInfoEntities
                    this.sumCalories =  parseFloat(res.data.energy).toFixed(2)
                    this.sumProtein = parseFloat(res.data.protein).toFixed(2)
                    this.sumCarbs = parseFloat(res.data.carbonWater).toFixed(2)
                    this.sumFat = parseFloat(res.data.fat).toFixed(2)
                    this.planID = res.data.id
                    this.typeOp = 'upd'
                }else{
                    this.planID = getTime("start")
                    this.typeOp = 'add'
                }
            })
        },
        toggleFavorite(recipe) {
            _addFoodLikesOrCollectionCount(recipe.fID, this.$store.state.login_user.id, "foodCol").then(res => {
                if (res.data.data) {
                    recipe.isCol = '1'
                } else {
                    recipe.isCol = '0'
                }
                setTimeout(() => {

                }, 500);
                this.$message.success(recipe.isCol === '1' ?
                    `已将 "${recipe.fName}" 添加到收藏` :
                    `已取消收藏 "${recipe.fName}"`);
            })
        },
        // 保存偏好设置
        savePreferences() {
            this.currentPreferences = [...this.selectedPreferences];
            this.showPreferenceDialog = false;

            this.$message.success('偏好设置已更新');
        },
        addMealByTime(newMeal, targetTime) {
            // 1. 找到目标time的最后一次出现位置
            let lastIndex = -1;
            for (let i = this.dailyPlan.length - 1; i >= 0; i--) {
                if (this.dailyPlan[i].time === targetTime && this.dailyPlan[i].foodName === newMeal.foodName) {
                    lastIndex = -2;
                    break;
                }
                if (this.dailyPlan[i].time === targetTime) {
                    lastIndex = i;
                    break;
                }
            }
            // 2. 如果找到目标time，在其后插入；否则添加到数组末尾
            if (lastIndex === -2) {
                this.$message.error(`菜谱${newMeal.foodName}已经再计划里啦！`);
            } else if (lastIndex === -1) {
                this.dailyPlan.push(newMeal);
                this.totalSum
            } else {
                this.dailyPlan.splice(lastIndex + 1, 0, newMeal);
                this.totalSum
            }
            this.dialogFormVisible = false;
        },
        addToPlanShow(recipe) {
            this.dialogFormVisible = true;
            this.tempRecipe = recipe
        },
        addToPlan() {
            if (this.tempRecipe.fName) {
                const newRow = {
                    id: getTime("start"),
                    time: this.dialogFormValue,
                    foodName: this.tempRecipe.fName,
                    energy: this.tempRecipe.calories,
                    carbonWater: this.tempRecipe.carbs,
                    protein: this.tempRecipe.protein,
                    fat: this.tempRecipe.fat,
                    planID: this.planID,
                    createTime: parseTime(Date.now(),'{y}-{m}-{d}'),
                    note: this.tempRecipe.fID
                };
                this.addMealByTime(newRow, this.dialogFormValue)
            } else {
                this.$message.info(`添加菜谱${this.tempRecipe.fName}时出错啦...`);
            }
        },
        replaceMeal(item) {
            this.dailyPlan = this.dailyPlan.filter(plan => plan !== item);
        },
        savePlan() {
            let planData = {
                id: this.planID,
                energy: this.sumCalories,
                fat: this.sumFat,
                carbonWater: this.sumCarbs,
                protein: this.sumProtein,
                createDate: parseTime(Date.now(),'{y}-{m}-{d}'),
                createID: this.$store.state.login_user.id,
                note: '',
                op: this.typeOp,
                planInfoEntities: this.dailyPlan
            }
            if(this.dailyPlan.length > 0){
                _operationDietPlan(planData).then((res) => {
                })
                this.$message.success('饮食计划已保存');
            }else{
                this.$message.error('暂无计划');
            }
            
        }
    },
    created() {
        this.init();
    },
};
</script>
  
<style scoped>
@import '@/assets/RecommendedRecipes.css';
</style>