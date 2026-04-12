<template>
  <div class="dashboard-view">
    <div class="panel-group">
      <el-row :gutter="20" class="panel-group">
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel header-panel">
            <h2 class="panel-title">食谱编辑</h2>
            <el-form class="el-form-item recipe-form" :inline="true" ref="recipesEntity" :rules="rules"
              :model="recipesEntity" @submit.prevent>
              <el-form-item label="食谱名称" prop="fName" class="form-item">
                <el-input v-model="recipesEntity.fName" placeholder="请输入食谱名称" class="form-input"></el-input>
              </el-form-item>
              <el-form-item label="目标群体/人" class="form-item">
                <el-input v-model="recipesEntity.fTarget" placeholder="如：儿童、运动员等" class="form-input"></el-input>
              </el-form-item>
              <el-form-item label="创建人" class="form-item">
                <el-input v-model="recipesEntity.uName" :disabled="true" class="disabled-input"></el-input>
              </el-form-item>
            </el-form>
          </div>
        </el-col>

        <!-- 餐次卡片组 -->
        <div class="meal-cards">
          <!-- 早餐 -->
          <el-col :xs="24" :sm="12" :md="8" :lg="6" class="card-panel-col">
            <div class="card-panel meal-card">
              <div class="meal-header">
                <div class="meal-icon breakfast"></div>
                <h3 class="meal-title">早餐</h3>
              </div>
              <el-input type="textarea" v-model="recipesEntity.zMeal" :rows="5" placeholder="请输入早餐内容" class="meal-input"
                resize="vertical"></el-input>
            </div>
          </el-col>



          <!-- 午餐 -->
          <el-col :xs="24" :sm="12" :md="8" :lg="6" class="card-panel-col">
            <div class="card-panel meal-card">
              <div class="meal-header">
                <div class="meal-icon lunch"></div>
                <h3 class="meal-title">午餐</h3>
              </div>
              <el-input type="textarea" v-model="recipesEntity.aMeal" :rows="5" placeholder="请输入午餐内容" class="meal-input"
                resize="vertical"></el-input>
            </div>
          </el-col>

          <!-- 晚餐 -->
          <el-col :xs="24" :sm="12" :md="8" :lg="6" class="card-panel-col">
            <div class="card-panel meal-card">
              <div class="meal-header">
                <div class="meal-icon dinner"></div>
                <h3 class="meal-title">晚餐</h3>
              </div>
              <el-input type="textarea" v-model="recipesEntity.nMeal" :rows="5" placeholder="请输入晚餐内容" class="meal-input"
                resize="vertical"></el-input>
            </div>
          </el-col>
          <!-- 早加餐 -->
          <el-col :xs="24" :sm="12" :md="8" :lg="6" class="card-panel-col">
            <div class="card-panel meal-card">
              <div class="meal-header">
                <div class="meal-icon snack"></div>
                <h3 class="meal-title">早加餐</h3>
              </div>
              <el-input type="textarea" v-model="recipesEntity.zSnack" :rows="5" placeholder="请输入早加餐内容" class="meal-input"
                resize="vertical"></el-input>
            </div>
          </el-col>

          <!-- 午加餐 -->
          <el-col :xs="24" :sm="12" :md="8" :lg="6" class="card-panel-col">
            <div class="card-panel meal-card">
              <div class="meal-header">
                <div class="meal-icon snack"></div>
                <h3 class="meal-title">午加餐</h3>
              </div>
              <el-input type="textarea" v-model="recipesEntity.aSnack" :rows="5" placeholder="请输入午加餐内容" class="meal-input"
                resize="vertical"></el-input>
            </div>
          </el-col>

          <!-- 晚加餐 -->
          <el-col :xs="24" :sm="12" :md="8" :lg="6" class="card-panel-col">
            <div class="card-panel meal-card">
              <div class="meal-header">
                <div class="meal-icon snack"></div>
                <h3 class="meal-title">晚加餐</h3>
              </div>
              <el-input type="textarea" v-model="recipesEntity.nSnack" :rows="5" placeholder="请输入晚加餐内容" class="meal-input"
                resize="vertical"></el-input>
            </div>
          </el-col>
        </div>

        <!-- 详细信息卡片 -->
        <el-col :xs="24" :lg="8" class="card-panel-col">
          <div class="card-panel info-card">
            <div class="card-header">
              <i class="el-icon-document"></i>
              <h3 class="info-title">食谱说明</h3>
            </div>
            <el-input type="textarea" v-model="recipesEntity.fIntro" :rows="6" placeholder="请输入食谱说明" class="info-input"
              resize="vertical"></el-input>
          </div>
        </el-col>

        <el-col :xs="24" :lg="8" class="card-panel-col">
          <div class="card-panel info-card">
            <div class="card-header">
              <i class="el-icon-data-analysis"></i>
              <h3 class="info-title">营养结构</h3>
            </div>
            <el-input type="textarea" v-model="recipesEntity.fType" :rows="6" placeholder="请输入营养结构说明" class="info-input"
              resize="vertical"></el-input>
          </div>
        </el-col>

        <el-col :xs="24" :lg="8" class="card-panel-col">
          <div class="card-panel info-card">
            <div class="card-header">
              <i class="el-icon-edit-outline"></i>
              <h3 class="info-title">备注</h3>
            </div>
            <el-input type="textarea" v-model="recipesEntity.fNote" :rows="6" placeholder="请输入备注信息" class="info-input"
              resize="vertical"></el-input>
          </div>
        </el-col>

        <!-- 操作按钮 -->
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel action-panel">
            <el-button type="primary" icon="el-icon-check" @click="handleSave('recipesEntity')"
              class="action-btn save-btn">
              保存食谱
            </el-button>
            <el-button type="info" icon="el-icon-back" @click="$router.push('/home/nutrition/NutritionalRecipes')"
              class="action-btn">
              返回列表
            </el-button>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import {
  _getNutritionalRecipesById,
  _operationNutritionalRecipes,
} from "@/api/api.js";
import { parseDateTime } from "../../utils/calendar";

export default {
  name: "OpNutritionRecipes",
  components: {},
  data() {
    return {
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
      },
      dialogImageUrl: "",
      dialogVisible: false,
      isOperation: false,
      recipesEntity: {
        fID: "",
        fType: "",
        fName: "",
        fIntro: "",
        zMeal: "",
        zSnack: "",
        aMeal: "",
        aSnack: "",
        nMeal: "",
        nSnack: "",
        fTarget: "",
        uID: "",
        uName: "",
        fCreateTime: "",
        fUpdateTime: "",
        fNote: "",
        op: "",
      },
      rules: {
        fType: [{ required: true, message: "请输入食物类别", trigger: "blur" }],
        fName: [{ required: true, message: "请输入食物名称", trigger: "blur" }],
      },
    };
  },
  computed: {},
  methods: {
    init() {
      if (this.recipesEntity.fID) {
        this.isOperation = true;
        _getNutritionalRecipesById(this.recipesEntity.fID).then((res) => {
          this.recipesEntity = res.data.data;
        });
      } else {
        this.isOperation = false;
        this.recipesEntity.uName = this.$store.state.login_user.username;
      }
    },
    handleSave(recipesEntity) {
      this.$refs[recipesEntity].validate((valid) => {
        if (valid) {
          if (this.isOperation) {
            this.recipesEntity.op = "upd";
          } else {
            this.recipesEntity.op = "add";
            this.recipesEntity.uID = this.$store.state.login_user.id;
          }
          _operationNutritionalRecipes(this.recipesEntity).then((res) => {
            this.$message.success(res.data.msg);
            this.$router.push({
              path: "/home/nutrition/NutritionalRecipes" || "/",
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
@primary-color: #409EFF;
@success-color: #67C23A;
@warning-color: #E6A23C;
@danger-color: #F56C6C;
@info-color: #909399;
@border-color: #EBEEF5;
@card-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
@card-radius: 8px;

.dashboard-view {
  padding: 20px;
  background-color: #f5f7fa;
}

.panel-group {
  margin-top: 0;
}

.card-panel-col {
  margin-bottom: 20px;
}

.card-panel {
  background: #fff;
  border-radius: @card-radius;
  box-shadow: @card-shadow;
  padding: 20px;
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  }
}

.header-panel {
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  border-left: 4px solid @primary-color;

  .panel-title {
    color: #303133;
    margin-top: 0;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 1px solid @border-color;
    font-weight: 600;
  }
}

.recipe-form {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;

  .form-item {
    margin: 0;
    flex: 1;
    min-width: 250px;
  }

  .form-input {
    width: 100%;
  }

  .disabled-input {
    /deep/ .el-input__inner {
      background-color: #f5f7fa;
      color: @info-color;
    }
  }
}

.meal-cards {
  display: flex;
  flex-wrap: wrap;
  width: 100%;
  gap: 20px;
}

.meal-card {
  height: 100%;
  display: flex;
  flex-direction: column;

  .meal-header {
    display: flex;
    align-items: center;
    margin-bottom: 15px;

    .meal-icon {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      margin-right: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 18px;

      &.breakfast {
        background-color: @warning-color;

        &:before {
          content: "☀️";
        }
      }

      &.lunch {
        background-color: @success-color;

        &:before {
          content: "🍲";
        }
      }

      &.dinner {
        background-color: @primary-color;

        &:before {
          content: "🌙";
        }
      }

      &.snack {
        background-color: @info-color;

        &:before {
          content: "🍎";
        }
      }
    }

    .meal-title {
      margin: 0;
      color: #303133;
      font-size: 18px;
    }
  }

  .meal-input {
    flex: 1;

    /deep/ .el-textarea__inner {
      min-height: 120px;
    }
  }
}

.info-card {
  height: 100%;

  .card-header {
    display: flex;
    align-items: center;
    margin-bottom: 15px;

    .el-icon-document,
    .el-icon-data-analysis,
    .el-icon-edit-outline {
      font-size: 24px;
      margin-right: 10px;
      color: @primary-color;
    }

    .info-title {
      margin: 0;
      color: #303133;
      font-size: 18px;
    }
  }

  .info-input {
    /deep/ .el-textarea__inner {
      min-height: 180px;
    }
  }
}

.action-panel {
  display: flex;
  justify-content: center;
  padding: 20px;
  border-top: 1px solid @border-color;
  background-color: #fafafa;

  .action-btn {
    padding: 12px 28px;
    font-size: 16px;
    border-radius: 30px;
    box-shadow: 0 4px 6px rgba(50, 50, 93, 0.11), 0 1px 3px rgba(0, 0, 0, 0.08);
    transition: all 0.2s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 7px 14px rgba(50, 50, 93, 0.1), 0 3px 6px rgba(0, 0, 0, 0.08);
    }

    &.save-btn {
      background: linear-gradient(to right, @primary-color, #66b1ff);
      border: none;
    }
  }
}

@media (max-width: 1200px) {
  .meal-card {
    .meal-title {
      font-size: 16px;
    }
  }
}

@media (max-width: 768px) {
  .recipe-form {
    flex-direction: column;

    .form-item {
      min-width: 100%;
    }
  }

  .meal-card {
    margin-bottom: 20px;
  }
}
</style>