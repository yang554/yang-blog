<template>
  <div class="dashboard-view">
    <div class="panel-group">
      <el-row :gutter="24" class="panel-group">
        <!-- 基础信息区域 -->
        <el-col :xs="24" :lg="3" class="card-panel-col">
          <h2 class="nutrition-h2-cs">
            <i class="el-icon-picture"></i> 基础信息
          </h2>
          <div class="card-panel">
            <el-upload class="avatar-uploader" action="/api/file/upload" :show-file-list="false"
              accept=".jpg,.png,.bmp,.jpeg,.gif" :on-success="handleFileSuccess" :before-upload="beforeAvatarUpload">
              <img v-if="recipesEntity.fImg" :src="recipesEntity.fImg" class="avatar my-border" width="100px"
                height="100px" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
              <div style="font-size: 14px; margin-top: 5px;">上传封面</div>
            </el-upload>
          </div>
        </el-col>

        <el-col :xs="24" :lg="9" class="card-panel-col">
          <div class="card-panel">
            <h2 class="nutrition-h2-cs">
              <i class="el-icon-document"></i> 菜品信息
            </h2>
            <el-form :inline="true" ref="recipesEntity" :model="recipesEntity" @submit.prevent>
              <el-row :gutter="15">
                <el-col :span="12">
                  <el-form-item label="菜系" prop="fType" style="width: 100%;color: #909399;">
                    <el-input v-model="recipesEntity.fType" placeholder="如：川菜、粤菜等"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="名称" prop="fName" style="width: 100%;">
                    <el-input v-model="recipesEntity.fName" placeholder="菜品名称"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>

              <h3 style="margin: 20px 0 15px; color: #606266; font-weight: 500;">
                <i class="el-icon-data-analysis"></i> 营养信息
              </h3>

              <div class="nutrient-cards">
                <div class="nutrient-card">
                  <div class="nutrient-label">能量</div>
                  <div class="nutrient-value">{{ recipesEntity.calories || '0.0' }}<span class="nutrient-unit">kcal</span>
                  </div>
                </div>
                <div class="nutrient-card">
                  <div class="nutrient-label">碳水</div>
                  <div class="nutrient-value">{{ recipesEntity.carbs || '0.0' }}<span class="nutrient-unit">g</span></div>
                </div>
                <div class="nutrient-card">
                  <div class="nutrient-label">脂肪</div>
                  <div class="nutrient-value">{{ recipesEntity.fat || '0.0' }}<span class="nutrient-unit">g</span></div>
                </div>
                <div class="nutrient-card">
                  <div class="nutrient-label">蛋白</div>
                  <div class="nutrient-value">{{ recipesEntity.protein || '0.0' }}<span class="nutrient-unit">g</span>
                  </div>
                </div>
              </div>
            </el-form>
          </div>
        </el-col>

        <el-col :xs="24" :lg="12" class="card-panel-col">
          <div class="card-panel">
            <h2 class="nutrition-h2-cs">
              <i class="el-icon-notebook-2"></i> 简介
            </h2>
            <el-input type="textarea" v-model="recipesEntity.fIntro" :rows="4" placeholder="请输入菜品简介..."></el-input>
          </div>
        </el-col>

        <!-- 食材管理区域 -->
        <el-col :xs="24" :lg="16" class="card-panel-col">
          <div class="card-panel">
            <h2 class="nutrition-h2-cs">
              <i class="el-icon-s-management"></i> 食材管理
            </h2>

            <div class="ingredient-control">
              <el-autocomplete v-model="newIngredient.foodName" placeholder="选择食材" style="flex: 2;"
                :fetch-suggestions="querySearch" @select="handleSelect">
                <template #default="{ item }">
                  <div style="display: flex; justify-content: space-between; align-items: center;">
                    <span>{{ item.fName }}</span>
                    <span style="font-size: 0.9rem; color: #909399;">
                      {{ item.fEnergy }}kcal/{{ item.fEdibleSection }}g
                    </span>
                  </div>
                </template>
              </el-autocomplete>
              <!-- <el-select v-model="newIngredient.foodId" placeholder="选择食材" style="flex: 2;">
                <el-option v-for="food in foods" :key="food.fID" :label="food.fName" :value="food.fID">
                  <div style="display: flex; justify-content: space-between; align-items: center;">
                    <span>{{ food.fName }}</span>
                    <span style="font-size: 0.9rem; color: #909399;">
                      {{ food.fEnergy }}kcal/{{ food.fEdibleSection}}g
                    </span>
                  </div>
                </el-option>
              </el-select> -->

              <el-input v-model="newIngredient.weight" placeholder="重量" style="flex: 1;">
                <template slot="append">g</template>
              </el-input>

              <el-button type="primary" icon="el-icon-plus" @click="addIngredient"
                :disabled="!newIngredient.foodId || !newIngredient.weight">
                添加食材
              </el-button>
            </div>

            <div class="ingredient-table">
              <el-table :data="recipesEntity.ingredients" style="width: 100%;" height="300">
                <el-table-column prop="foodName" label="食材名称" width="180"></el-table-column>
                <el-table-column prop="weight" label="重量" width="180">
                  <template slot-scope="scope">
                    <el-input-number v-model="scope.row.weight" :min="1" :max="10000" size="small"
                      @change="updateNutrition" controls-position="right">
                      <template slot="append">g</template>
                    </el-input-number>
                  </template>
                </el-table-column>
                <el-table-column label="营养值">
                  <template slot-scope="scope">
                    <div class="nutrition-details">
                      <div class="nutrition-detail-item">
                        <span class="nutrient-name">能量:</span>
                        <span class="nutrient-amount">{{ calculateNutrient(scope.row, 'calories') }}kcal</span>
                      </div>
                      <div class="nutrition-detail-item">
                        <span class="nutrient-name">碳水:</span>
                        <span class="nutrient-amount">{{ calculateNutrient(scope.row, 'carbs') }}g</span>
                      </div>
                      <div class="nutrition-detail-item">
                        <span class="nutrient-name">蛋白:</span>
                        <span class="nutrient-amount">{{ calculateNutrient(scope.row, 'protein') }}g</span>
                      </div>
                      <div class="nutrition-detail-item">
                        <span class="nutrient-name">脂肪:</span>
                        <span class="nutrient-amount">{{ calculateNutrient(scope.row, 'fat') }}g</span>
                      </div>
                      <div class="nutrition-detail-item">
                        <span class="nutrient-name">纤维:</span>
                        <span class="nutrient-amount">{{ calculateNutrient(scope.row, 'fDietaryFiber') }}g</span>
                      </div>
                      <div class="nutrition-detail-item">
                        <span class="nutrient-name">维C:</span>
                        <span class="nutrient-amount">{{ calculateNutrient(scope.row, 'fVitaminC') }}mg</span>
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="80" align="center">
                  <template slot-scope="scope">
                    <el-button size="mini" type="danger" icon="el-icon-delete" circle
                      @click="removeIngredient(scope.$index)"></el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :lg="8" class="card-panel-col">
          <div class="card-panel">
            <h2 class="nutrition-h2-cs">
              <i class="el-icon-shopping-cart-full"></i> 食材清单
            </h2>
            <div class="selected-foods">
              <ul>
                <li v-for="(item, index) in recipesEntity.ingredients" :key="index">
                  <span>{{ item.foodName }}</span>
                  <span class="food-weight">{{ item.weight }}g</span>
                </li>
                <li v-if="!recipesEntity.ingredients" style="text-align: center; color: #909399;">
                  <i class="el-icon-info" style="margin-right: 5px;"></i> 暂无食材
                </li>
              </ul>
            </div>
          </div>
        </el-col>

        <!-- 调料、做法和小贴士区域 -->
        <el-col :xs="24" :lg="8" class="card-panel-col">
          <div class="card-panel">
            <h2 class="nutrition-h2-cs">
              <i class="el-icon-knife-fork"></i> 调料
            </h2>
            <el-input type="textarea" v-model="recipesEntity.fSeasoning" :rows="8" placeholder="请输入调料及用量..."></el-input>
          </div>
        </el-col>

        <el-col :xs="24" :lg="8" class="card-panel-col">
          <div class="card-panel">
            <h2 class="nutrition-h2-cs">
              <i class="el-icon-fork-spoon"></i> 做法
            </h2>
            <el-input type="textarea" v-model="recipesEntity.fMade" :rows="8" placeholder="请输入详细制作步骤..."></el-input>
          </div>
        </el-col>

        <el-col :xs="24" :lg="8" class="card-panel-col">
          <div class="card-panel">
            <h2 class="nutrition-h2-cs">
              <i class="el-icon-lightbulb"></i> 小贴士
            </h2>
            <el-input type="textarea" v-model="recipesEntity.fTips" :rows="8" placeholder="烹饪技巧、注意事项等..."></el-input>
          </div>
        </el-col>

        <!-- 操作按钮区域 -->
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="nutrition-panel">
            <el-button type="primary" @click="handleSave('recipesEntity')" size="medium">
              <i class="el-icon-check"></i> 保存食谱
            </el-button>
            <el-button type="info" @click="$router.push('/home/nutrition/NutritionalRecipe')" size="medium">
              <i class="el-icon-back"></i> 返回列表
            </el-button>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
    
<script>
import {
  _getNutritionalRecipeById,
  _operationNutritionalRecipe,
  _getFoodNutritionAll,
  _getByRecipeId,
} from "@/api/api.js";
import { parseDateTime } from "../../utils/calendar";
import { getTime, parseTime } from "../../utils/formatDate";

export default {
  name: "OpNutritionRecipe",
  components: {},
  data() {
    return {
      foods: [],  //食物对象
      newIngredient: {// 新配料对象
        foodName: "",
        foodId: "",
        weight: ""
      },
      isOperation: false,
      recipesEntity: {
        fID: "",
        fImg: "",
        fType: "",
        fName: "",
        fIntro: "",
        fMade: "",
        fSeasoning: "",
        fBatching: "",
        ingredients: [], // 新增字段，存储配料对象数组
        fCreateTime: "",
        fUpdateTime: "",
        fTips: "",
        calories: "",
        protein: "",
        carbs: "",
        fat: "",
        op: "",
      },
      rules: {
        fType: [{ required: true, message: "请输入菜品菜系", trigger: "blur" }],
        fName: [{ required: true, message: "请输入菜品名称", trigger: "blur" }],
        fEdibleSection: [
          { required: true, message: "请输入可食部分", trigger: "blur" },
        ],
      },
    };
  },
  computed: {},
  methods: {
    init() {
      _getFoodNutritionAll().then((res) => {

        if (res.data.length) {
          this.foods = res.data
        }
      }).catch((error) => {
        console.error('获取食物营养数据失败:', error);
        this.foods = [];
        // 或者显示错误提示
        this.$message.error(error.message);
      });
      if (this.recipesEntity.fID) {
        this.isOperation = true;
        _getNutritionalRecipeById(this.recipesEntity.fID).then((res) => {
          this.recipesEntity = res.data.data;
        });
        _getByRecipeId(this.recipesEntity.fID).then((res) => {
          const ingredientsToCreate = res.data.map(item => ({
            id: item.id,
            foodId: item.ingredient.fID,
            foodName: item.ingredient.fName,
            weight: item.weight,
            calories: item.ingredient.fEnergy,
            carbs: item.ingredient.fCarbonWater,
            fat: item.ingredient.fFat,
            protein: item.ingredient.fProtein,
            edible: item.ingredient.fEdibleSection,
          }));
          this.recipesEntity.ingredients = ingredientsToCreate
        })
      } else {
        this.isOperation = false;
      }
    },
    querySearch(queryString, cb) {
      // 过滤匹配的食材
      const results = queryString ? this.foods.filter(food =>
        food.fName.toLowerCase().includes(queryString.toLowerCase())
      ) : this.foods;
      // 调用回调函数返回建议结果
      cb(results);
    },
    handleSelect(item) {
      // 当选择建议项时，设置选中的食材ID
      this.newIngredient.foodId = item.fID;
      this.newIngredient.foodName = item.fName;
    },
    // 添加配料
    addIngredient() {
      if (this.newIngredient.foodId && this.newIngredient.weight) {
        const food = this.foods.find(f => f.fID === this.newIngredient.foodId);
        const f = this.recipesEntity.ingredients.find(f => f.foodId === this.newIngredient.foodId);
        if (food && !f) {
          this.recipesEntity.ingredients.push({
            id: getTime("start"),
            foodId: this.newIngredient.foodId,
            foodName: food.fName,
            weight: Number(this.newIngredient.weight),
            calories: food.fEnergy === 'Tr' ? 0 : food.fEnergy,
            carbs: food.fCarbonWater === 'Tr' ? 0 : food.fCarbonWater,
            fat: food.fFat === 'Tr' ? 0 : food.fFat,
            protein: food.fProtein === 'Tr' ? 0 : food.fProtein,
            edible: food.fEdibleSection
          });

          // 重置新配料输入
          this.newIngredient = {
            foodName: "",
            foodId: "",
            weight: ""
          };

          // 更新营养值
          this.updateNutrition();
        } else {
          this.$message.error("食材已存在！");
        }
      }
    },
    // 生成配料文本描述
    generateBatchingText() {
      let text = "";
      this.recipesEntity.ingredients.forEach(ingredient => {
        const food = this.foods.find(f => f.fID === ingredient.foodId);
        if (food) {
          text += `${food.fName} ${ingredient.weight}g\n`;
        }
      });
      this.recipesEntity.fBatching = text;
    },
    // 更新营养值
    updateNutrition() {
      let calories = 0;
      let carbs = 0;
      let fat = 0;
      let protein = 0;

      let text = "";

      this.recipesEntity.ingredients.forEach(ingredient => {
        const ratio = ingredient.weight / ingredient.edible;
        calories += ingredient.calories * ratio;
        carbs += ingredient.carbs * ratio;
        fat += ingredient.fat * ratio;
        protein += ingredient.protein * ratio;
        const food = this.foods.find(f => f.fID === ingredient.foodId);
        if (food) {
          text += `${food.fName} ${ingredient.weight}g\n`;
        }
      });

      this.recipesEntity.fBatching = text;
      this.recipesEntity.calories = calories.toFixed(1);
      this.recipesEntity.carbs = carbs.toFixed(1);
      this.recipesEntity.fat = fat.toFixed(1);
      this.recipesEntity.protein = protein.toFixed(1);
    },
    // 统一营养计算方法
    calculateNutrient(ingredient, nutrientType) {
      // 确保数值是数字，避免字符串拼接
      const value = parseFloat(ingredient[nutrientType]);
      if (isNaN(value) || value === 'Tr') return '0.0';

      // 计算实际营养值：(营养值/100) * 重量
      const result = (value * ingredient.weight) / ingredient.edible;

      // 返回格式化后的结果
      return result.toFixed(1);
    },
    // 移除配料
    removeIngredient(index) {
      this.recipesEntity.ingredients.splice(index, 1);
      this.updateNutrition();
    },

    timeChange(val) {
      const d = Date.parse(val);
      this.recipesEntity.recordTime = parseDateTime(new Date(d));
    },
    handleFileSuccess(response, file, fileList) {
      if (response.status == 200) {
        this.recipesEntity.fImg = response.data;
      }
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type.startsWith("image");
      const isLt10M = file.size / 1024 / 1024 < 10;
      if (!isJPG) {
        this.$message.error("上传头像图片只能是 JPG 格式!");
      }
      if (!isLt10M) {
        this.$message.error("上传头像图片大小不能超过 10MB!");
      }
      return isJPG && isLt10M;
    },
    handleSave(recipesEntity) {
      this.$refs[recipesEntity].validate((valid) => {
        if (valid) {
          if (this.isOperation) {
            this.recipesEntity.op = "upd";
          } else {
            this.recipesEntity.op = "add";
          }
          _operationNutritionalRecipe(this.recipesEntity).then((res) => {
            this.$message.success(res.data.msg);
            this.$router.push({
              path: "/home/nutrition/NutritionalRecipe" || "/",
            });
          });
        } else {
          this.$message.error("error sufFatt!!");
          return false;
        }
      });
    },
  },
  mounted() { },
  created() {
    this.recipesEntity.fID = this.$route.params.id;
    this.init();
  },
};
</script>
    
<style lang="less" scoped>
.panel-group {
  background: white;
  border-radius: 16px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  padding: 25px;
}

.el-row {
  margin-bottom: 20px;
}

.card-panel-col {
  margin-bottom: 20px;
}

.card-panel {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  height: 100%;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.card-panel:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-3px);
}

.nutrition-h2-cs {
  font-size: 18px;
  color: #2c3e50;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #eaeaea;
  display: flex;
  align-items: center;
}

.nutrition-h2-cs i {
  margin-right: 10px;
  color: #409eff;
}

/* 头像上传样式优化 */
.avatar-uploader {
  text-align: center;
  position: relative;
}

.avatar-uploader .el-upload {
  display: block;
  width: 100%;
}

.avatar-uploader-icon {
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  width: 100%;
  height: 150px;
  line-height: 150px;
  text-align: center;
  background: #fafafa;
  color: #8c939d;
  font-size: 28px;
  transition: border-color 0.3s;
}

.avatar-uploader-icon:hover {
  border-color: #409eff;
}

.avatar {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

/* 表单样式优化 */
.el-form-item {
  margin-bottom: 18px;
}

.el-form-item__label {
  font-weight: 600;
  color: #5a5e66;
}

.el-input__inner {
  border-radius: 6px;
}

.el-textarea__inner {
  border-radius: 6px;
  min-height: 120px !important;
}

/* 食材管理区域优化 */
.ingredient-control {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
  background: #f8fafc;
  padding: 15px;
  border-radius: 8px;
}

.ingredient-table {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

.el-table {
  border-radius: 8px;
}

.el-table th {
  background-color: #f5f7fa !important;
  font-weight: 600;
  color: #606266;
}

/* 食材列表样式优化 */
.selected-foods {
  height: 395px;
  overflow-y: auto;
  padding: 10px;
  background: #f8fafc;
  border-radius: 8px;
}

.selected-foods ul {
  list-style: none;
  padding: 0;
}

.selected-foods li {
  padding: 12px 15px;
  margin-bottom: 8px;
  background: white;
  border-radius: 6px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.2s;
}

.selected-foods li:hover {
  transform: translateX(5px);
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.08);
}

.selected-foods li .food-weight {
  background: #409eff;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.85rem;
}

/* 操作按钮区域优化 */
.nutrition-panel {
  display: flex;
  justify-content: center;
  gap: 15px;
  padding: 20px;
  background: #f8fafc;
  border-radius: 12px;
  margin-top: 20px;
}

.el-button {
  border-radius: 6px;
  padding: 12px 28px;
  font-weight: 500;
  transition: all 0.3s;
}

.el-button--primary {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border: none;
}

.el-button--info {
  background: linear-gradient(135deg, #909399, #a6a9ad);
  border: none;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .card-panel-col {
    margin-bottom: 20px;
  }

  .ingredient-control {
    flex-direction: column;
  }

  .ingredient-control>* {
    width: 100%;
    margin-right: 0 !important;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-panel {
  animation: fadeIn 0.5s ease;
}

/* 添加图标 */
.section-icon {
  background: #e6f7ff;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  color: #409eff;
}

/* 营养值卡片 */
.nutrient-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  margin-top: 15px;
}

.nutrient-card {
  background: #f8fafc;
  border-radius: 8px;
  padding: 15px;
  border-left: 4px solid #409eff;
}

.nutrient-label {
  font-size: 0.9rem;
  color: #909399;
  margin-bottom: 5px;
}

.nutrient-value {
  font-size: 1.2rem;
  font-weight: 600;
  color: #303133;
}

.nutrient-unit {
  color: #909399;
  font-size: 0.9rem;
  margin-left: 3px;
}

/* 食材表格中的营养值显示优化 */
.nutrition-details {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  font-size: 0.85rem;
}

.nutrition-detail-item {
  background: #f0f7ff;
  padding: 4px 8px;
  border-radius: 4px;
  display: flex;
}

.nutrient-name {
  color: #606266;
  margin-right: 4px;
}

.nutrient-amount {
  color: #409eff;
  font-weight: 500;
}
</style>
    