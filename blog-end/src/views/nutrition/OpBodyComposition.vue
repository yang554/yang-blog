<template>
  <div class="dashboard-view">
    <div class="panel-group">
      <el-row :gutter="24" class="panel-group">
        <!-- 基础信息卡片 -->
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel header-card">
            <h2 class="section-title">基础信息</h2>
            <el-form :model="bodyEntity" ref="bodyEntity" :rules="rules" class="compact-form">
              <el-row :gutter="24">
                <el-col :xs="24" :sm="12" :md="8" :span="5">
                  <el-form-item label="姓名" prop="uID" class="form-item">
                    <el-select v-model="bodyEntity.uID" filterable placeholder="请选择用户" class="full-width">
                      <el-option v-for="(item, index) in uNameAll" :key="index" :label="item.username"
                        :value="item.id"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="性别" prop="sex" class="form-item">
                    <el-select v-model="bodyEntity.sex" @change="handleChangeSex" placeholder="性别" class="full-width">
                      <el-option label="男" value="男"></el-option>
                      <el-option label="女" value="女"></el-option>
                    </el-select>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="年龄" prop="age" class="form-item">
                    <el-input v-model="bodyEntity.age" placeholder="年龄" @input="handleAge" suffix="岁"
                      class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="身高" class="form-item">
                    <el-input v-model="bodyEntity.height" placeholder="身高" @input="handleHeight" suffix="cm"
                      class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="体重" class="form-item">
                    <el-input v-model="bodyEntity.weight" placeholder="体重" @input="handleWeight" suffix="kg"
                      class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="BMI" class="form-item">
                    <el-tooltip effect="dark" content="用于估算体重是否健康的指标" placement="top">
                      <el-input v-model="bodyEntity.bmi" placeholder="BMI" :disabled="true"
                        class="full-width result-input"></el-input>
                    </el-tooltip>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="肥胖等级" class="form-item">
                    <el-tooltip effect="dark" content="用于估算体重是否健康的指标" placement="top">
                      <el-input v-model="bodyEntity.obesityLevel" placeholder="肥胖等级" :disabled="true"
                        class="full-width result-input"></el-input>
                    </el-tooltip>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="基础代谢" class="form-item">
                    <el-tooltip effect="dark" content="人体维持生命所需的最低能量" placement="top">
                      <el-input v-model="bodyEntity.basalMetabolism" placeholder="基础代谢" :disabled="true" suffix="kcal"
                        class="full-width result-input"></el-input>
                    </el-tooltip>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="进食量" class="form-item">
                    <el-input v-model="bodyEntity.foodIntake" placeholder="进食量" suffix="kg" @input="bodyEntity.foodIntake = bodyEntity.foodIntake
                        .replace(/[^\d^\.]+/g, '')
                        .replace(/^0+(\d)/, '$1')
                        .replace(/^\./, '0.')
                        .match(/^\d*(\.?\d{0,2})/g)[0]
                        .slice(0, 5)
                      " class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="记录时间" required class="form-item">
                    <el-date-picker v-model="bodyEntity.recordTime" type="datetime" placeholder="选择记录时间"
                      class="full-width" :picker-options="pickerOptions" @change="timeChange"></el-date-picker>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
        </el-col>

        <!-- 身体成分卡片 -->
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel">
            <h2 class="section-title">身体成分</h2>
            <el-form label-position="top" class="compact-form">
              <el-row :gutter="20">
                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="体脂率" class="form-item">
                    <el-input v-model="bodyEntity.fat" placeholder="体脂率" suffix="%" @input="handleFat"
                      class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="内脏脂肪" class="form-item">
                    <el-input v-model="bodyEntity.visceralFat" placeholder="内脏脂肪" @input="bodyEntity.visceralFat = bodyEntity.visceralFat
                        .replace(/[^\d^\.]+/g, '')
                        .replace(/^0+(\d)/, '$1')
                        .replace(/^\./, '0.')
                        .match(/^\d*(\.?\d{0,2})/g)[0]
                        .slice(0, 5)
                      " class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="肌肉率" class="form-item">
                    <el-input v-model="bodyEntity.muscle" placeholder="肌肉率" suffix="%" class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="骨骼肌率" class="form-item">
                    <el-input v-model="bodyEntity.skeletalMuscle" placeholder="骨骼肌率" suffix="%"
                      @input="handleSkeletalMuscle" class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="骨重" class="form-item">
                    <el-input v-model="bodyEntity.boneMass" placeholder="骨重" suffix="kg" @input="bodyEntity.boneMass = bodyEntity.boneMass
                        .replace(/[^\d^\.]+/g, '')
                        .replace(/^0+(\d)/, '$1')
                        .replace(/^\./, '0.')
                        .match(/^\d*(\.?\d{0,2})/g)[0]
                        .slice(0, 5)
                      " class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="水分率" class="form-item">
                    <el-input v-model="bodyEntity.waterContent" placeholder="水分率" suffix="%" @input="handleWaterContent"
                      class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="蛋白质率" class="form-item">
                    <el-input v-model="bodyEntity.protein" placeholder="蛋白质率" suffix="%" class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="去脂体重" class="form-item">
                    <el-input v-model="bodyEntity.fatFreeBodyWeight" placeholder="去脂体重" suffix="kg" @input="bodyEntity.fatFreeBodyWeight = bodyEntity.fatFreeBodyWeight
                        .replace(/[^\d^\.]+/g, '')
                        .replace(/^0+(\d)/, '$1')
                        .replace(/^\./, '0.')
                        .match(/^\d*(\.?\d{0,2})/g)[0]
                        .slice(0, 5)
                      " class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="肌肉量" class="form-item">
                    <el-input v-model="bodyEntity.muscleMass" placeholder="肌肉量" suffix="kg" @input="bodyEntity.muscleMass = bodyEntity.muscleMass
                        .replace(/[^\d^\.]+/g, '')
                        .replace(/^0+(\d)/, '$1')
                        .replace(/^\./, '0.')
                        .match(/^\d*(\.?\d{0,2})/g)[0]
                        .slice(0, 5)
                      " class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="含水量" class="form-item">
                    <el-input v-model="bodyEntity.waterContent_1" placeholder="含水量" suffix="kg" @input="bodyEntity.waterContent_1 = bodyEntity.waterContent_1
                        .replace(/[^\d^\.]+/g, '')
                        .replace(/^0+(\d)/, '$1')
                        .replace(/^\./, '0.')
                        .match(/^\d*(\.?\d{0,2})/g)[0]
                        .slice(0, 5)
                      " class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="脂肪量" class="form-item">
                    <el-input v-model="bodyEntity.fatMass" placeholder="脂肪量" suffix="kg" @input="bodyEntity.fatMass = bodyEntity.fatMass
                        .replace(/[^\d^\.]+/g, '')
                        .replace(/^0+(\d)/, '$1')
                        .replace(/^\./, '0.')
                        .match(/^\d*(\.?\d{0,2})/g)[0]
                        .slice(0, 5)
                      " class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="皮下脂肪率" class="form-item">
                    <el-input v-model="bodyEntity.subcutaneousFatPercentage" placeholder="皮下脂肪率" suffix="%" @input="bodyEntity.subcutaneousFatPercentage =
                      bodyEntity.subcutaneousFatPercentage
                        .replace(/[^\d^\.]+/g, '')
                        .replace(/^0+(\d)/, '$1')
                        .replace(/^\./, '0.')
                        .match(/^\d*(\.?\d{0,2})/g)[0]
                        .slice(0, 5)
                      " class="full-width"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
        </el-col>

        <!-- 肌肉-脂肪控制卡片 -->
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel">
            <h2 class="section-title">肌肉-脂肪控制</h2>
            <el-form label-position="top" class="compact-form">
              <el-row :gutter="20">
                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="体重控制量" class="form-item">
                    <el-input v-model="bodyEntity.weightControlAmount" placeholder="体重控制量" suffix="kg" :disabled="true"
                      class="full-width result-input"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="脂肪控制量" class="form-item">
                    <el-input v-model="bodyEntity.fatControlAmount" placeholder="脂肪控制量" suffix="kg"
                      class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="肌肉控制量" class="form-item">
                    <el-input v-model="bodyEntity.muscleControlVolume" placeholder="肌肉控制量" suffix="kg"
                      class="full-width"></el-input>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="标准体重" class="form-item">
                    <el-tooltip effect="dark" content="根据亚洲标准体重公式" placement="top">
                      <el-input v-model="bodyEntity.standardWeight" placeholder="标准体重" suffix="kg" :disabled="true"
                        class="full-width result-input"></el-input>
                    </el-tooltip>
                  </el-form-item>
                </el-col>

                <el-col :xs="24" :sm="12" :md="8">
                  <el-form-item label="理想体重" class="form-item">
                    <el-input v-model="bodyEntity.idealWeight" placeholder="理想体重" suffix="kg" :disabled="true"
                      class="full-width result-input"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </div>
        </el-col>

        <!-- 分析结果和建议 -->
        <el-col :xs="24" :lg="16" class="card-panel-col">

          <div class="card-panel action-panel">
            <div >
              <h2 class="section-title">分析结果与建议</h2>
              <el-input type="textarea" v-model="bodyEntity.note" :rows="5" placeholder="请输入分析结果和建议..."
                class="analysis-textarea" resize="vertical"></el-input>
            </div>
            <div class="summary-box">
              <h3>健康摘要</h3>
              <div class="summary-item">
                <span class="label">BMI:</span>
                <span class="value" :class="bmiClass">{{ bodyEntity.bmi || '-' }}</span>
                <span class="status" :class="bmiClass">{{ bodyEntity.obesityLevel || '-' }}</span>
              </div>
              <div class="summary-item">
                <span class="label">体脂率:</span>
                <span class="value">{{ bodyEntity.fat || '-' }}%</span>
              </div>
              <div class="summary-item">
                <span class="label">肌肉率:</span>
                <span class="value">{{ bodyEntity.muscle || '-' }}%</span>
              </div>
            </div>
          </div>

        </el-col>

        <!-- 操作按钮 -->
        <el-col :xs="24" :lg="8" class="card-panel-col">
          <div class="card-panel action-panel">
            <div class="action-buttons">
              <el-button type="primary" size="small" icon="el-icon-check" @click="handleSave('bodyEntity')"
                class="action-btn save-btn">
                保存分析结果
              </el-button>
              <el-button type="info" size="small" icon="el-icon-back" @click="$router.push('/home/nutrition/BodyComposition')"
                class="action-btn">
                返回列表
              </el-button>
            </div>


          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { _getUserAll, _getNutritionById, _operationNBC } from "@/api/api.js";
import { parseDateTime } from "../../utils/calendar";

export default {
  name: "OpBodyComposition",
  components: {},
  data() {
    return {
      uNameAll: [], //所有用户
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
      },
      isOperation: false,
      bodyEntity: {
        id: "",
        uID: "",
        age: "",
        sex: "男",
        height: "",
        weight: "",
        basalMetabolism: "",
        bmi: "",
        foodIntake: "",
        fat: "",
        visceralFat: "",
        muscle: "",
        boneMass: "",
        waterContent: "",
        protein: "",
        fatFreeBodyWeight: "",
        muscleMass: "",
        skeletalMuscle: "",
        waterContent_1: "",
        fatMass: "",
        subcutaneousFatPercentage: "",
        weightControlAmount: "",
        fatControlAmount: "",
        muscleControlVolume: "",
        standardWeight: "",
        idealWeight: "",
        note: "",
        obesityLevel: "",
        recordTime: "",
        op: "",
      },
      rules: {
        uID: [{ required: true, message: "请选择用户", trigger: "blur" }],
        sex: [{ required: true, message: "请选择性别", trigger: "blur" }],
        age: [{ required: true, message: "请输入年龄", trigger: "blur" }],
      },
    };
  },
  computed: {
    bmiClass() {
      const bmi = parseFloat(this.bodyEntity.bmi);
      if (bmi < 18.5) return 'bmi-warning';
      if (bmi >= 18.5 && bmi < 24) return 'bmi-normal';
      if (bmi >= 24 && bmi < 28) return 'bmi-warning';
      return 'bmi-danger';
    }

  },
  methods: {
    init() {
      //获取所有登记用户名字
      _getUserAll().then((res) => {
        if (res.data.length > 0) {
          this.uNameAll = res.data;
        }
      });
      if (this.bodyEntity.id) {
        this.isOperation = true;
        _getNutritionById(this.bodyEntity.id).then((res) => {
          this.bodyEntity = res.data.data;
        });
      } else {
        this.bodyEntity.recordTime = parseDateTime(new Date().getTime());
        this.isOperation = false;
      }
    },
    timeChange(val) {
      const d = Date.parse(val);
      this.bodyEntity.recordTime = parseDateTime(new Date(d));
    },
    basalMetabolism() {
      let h = this.bodyEntity.height;
      let w = this.bodyEntity.weight;
      let a = this.bodyEntity.age;
      if (h != "" && w != "" && a != "" && this.bodyEntity.sex != "男") {
        this.bodyEntity.basalMetabolism =
          Math.round((10 * w + 6.25 * h - 5 * a + 5) * 100) / 100;
        this.bodyEntity.standardWeight = h - 105;
        this.bodyEntity.idealWeight = Math.round((h - 80) * 0.7 * 100) / 100;
      } else if (h != "" && w != "" && a != "" && this.bodyEntity.sex != "女") {
        this.bodyEntity.basalMetabolism =
          Math.round((9.6 * w + 6.25 * h - 5 * a - 161) * 100) / 100;
        this.bodyEntity.standardWeight = h - 105 - 2.5;
        this.bodyEntity.idealWeight = Math.round((h - 70) * 0.6 * 100) / 100;
      }
      this.bodyEntity.weightControlAmount =
        Math.round(
          (this.bodyEntity.idealWeight - this.bodyEntity.weight) * 100
        ) / 100;
      let bmi = Math.round(this.bodyEntity.bmi * 100) / 100;
      if (bmi < 18.5) {
        this.bodyEntity.obesityLevel = "偏瘦";
      } else if (18.5 < bmi && bmi < 23.9) {
        this.bodyEntity.obesityLevel = "正常";
      } else if (24 < bmi && bmi < 27.9) {
        this.bodyEntity.obesityLevel = "偏胖";
      } else if (28 <= bmi && bmi < 30) {
        this.bodyEntity.obesityLevel = "肥胖";
      } else if (30 <= bmi && bmi < 40) {
        this.bodyEntity.obesityLevel = "重度肥胖";
      } else if (40 <= bmi) {
        this.bodyEntity.obesityLevel = "极重度肥胖";
      } else {
        this.bodyEntity.obesityLevel = "异常";
      }
    },
    handleWeight(event) {
      this.bodyEntity.weight =
        event
          .replace(/[^\d^\.]+/g, "")
          .replace(/^0+(\d)/, "$1")
          .replace(/^\./, "0.")
          .match(/^\d*(\.?\d{0,2})/g)[0] || "";
      if (parseInt(this.bodyEntity.weight, 10) > 999) {
        this.bodyEntity.weight = "999";
      }
      if (this.bodyEntity.height != "") {
        this.bodyEntity.bmi =
          Math.round(
            (this.bodyEntity.weight /
              (((this.bodyEntity.height / 100) * this.bodyEntity.height) /
                100)) *
            100
          ) / 100;
      }
      this.basalMetabolism();
      this.handleFat(this.bodyEntity.fat);
      this.handleSkeletalMuscle(this.bodyEntity.skeletalMuscle);
      this.handleWaterContent(this.bodyEntity.waterContent);
    },
    handleHeight(event) {
      this.bodyEntity.height =
        event
          .replace(/[^\d^\.]+/g, "")
          .replace(/^0+(\d)/, "$1")
          .replace(/^\./, "0.")
          .match(/^\d*(\.?\d{0,2})/g)[0] || "";
      if (parseInt(this.bodyEntity.height, 10) > 300) {
        this.bodyEntity.height = "300";
      }
      if (this.bodyEntity.weight != "") {
        this.bodyEntity.bmi =
          Math.round(
            (this.bodyEntity.weight /
              (((this.bodyEntity.height / 100) * this.bodyEntity.height) /
                100)) *
            100
          ) / 100;
      }
      this.basalMetabolism();
    },
    handleAge(event) {
      this.bodyEntity.age = event.replace(/[^\d^\.]+/g, "");
      if (parseInt(this.bodyEntity.age, 10) > 200) {
        this.bodyEntity.age = "200";
      }
      this.basalMetabolism();
    },
    handleChangeSex(value) {
      this.bodyEntity.sex = value;
      this.basalMetabolism();
    },
    handleFat(value) {
      this.bodyEntity.fat =
        value
          .replace(/[^\d^\.]+/g, "")
          .replace(/^0+(\d)/, "$1")
          .replace(/^\./, "0.")
          .match(/^\d*(\.?\d{0,2})/g)[0] || "";
      if (parseInt(this.bodyEntity.fat, 10) > 100) {
        this.bodyEntity.fat = "100";
      }
      this.bodyEntity.fatFreeBodyWeight =
        Math.round(
          (this.bodyEntity.weight -
            (this.bodyEntity.weight * this.bodyEntity.fat) / 100) *
          100
        ) / 100;
      this.bodyEntity.fatMass =
        Math.round(
          (this.bodyEntity.weight - this.bodyEntity.fatFreeBodyWeight) * 100
        ) / 100;
    },
    handleSkeletalMuscle(value) {
      this.bodyEntity.skeletalMuscle =
        value
          .replace(/[^\d^\.]+/g, "")
          .replace(/^0+(\d)/, "$1")
          .replace(/^\./, "0.")
          .match(/^\d*(\.?\d{0,2})/g)[0] || "";
      if (parseInt(this.bodyEntity.skeletalMuscle, 10) > 100) {
        this.bodyEntity.skeletalMuscle = "100";
      }
      this.bodyEntity.muscleMass =
        Math.round(
          ((this.bodyEntity.weight * this.bodyEntity.skeletalMuscle) / 100) *
          100
        ) / 100;
    },
    handleWaterContent(value) {
      this.bodyEntity.waterContent =
        value
          .replace(/[^\d^\.]+/g, "")
          .replace(/^0+(\d)/, "$1")
          .replace(/^\./, "0.")
          .match(/^\d*(\.?\d{0,2})/g)[0] || "";
      if (parseInt(this.bodyEntity.waterContent, 10) > 100) {
        this.bodyEntity.waterContent = "100";
      }
      this.bodyEntity.waterContent_1 =
        Math.round(
          ((this.bodyEntity.weight * this.bodyEntity.waterContent) / 100) * 100
        ) / 100;
    },
    handleSave(bodyEntity) {
      this.$refs[bodyEntity].validate((valid) => {
        if (valid) {
          if (this.isOperation) {
            this.bodyEntity.op = "upd";
          } else {
            this.bodyEntity.op = "add";
          }
          _operationNBC(this.bodyEntity).then((res) => {
            // console.log(this.bodyEntity)
            this.$message.success(res.data.msg);
            this.$router.push({
              path: "/home/nutrition/BodyComposition" || "/",
            });
          });
        } else {
          this.$message.error("error submit!!");
          return false;
        }
      });
    },
  },
  mounted() { },
  created() {
    this.bodyEntity.id = this.$route.params.id;
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
@card-header-bg: #f8f9fa;

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

.header-card {
  border-left: 4px solid @primary-color;
}

.section-title {
  color: #303133;
  margin-top: 0;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid @border-color;
  font-weight: 600;
  font-size: 18px;
  position: relative;

  &:after {
    content: '';
    position: absolute;
    left: 0;
    bottom: -1px;
    width: 60px;
    height: 2px;
    background: @primary-color;
  }
}

.compact-form {
  .form-item {
    margin-bottom: 15px;

    /deep/ .el-form-item__label {
      font-weight: 500;
      color: #606266;
      padding-bottom: 5px;
    }
  }

  .full-width {
    width: 100%;
  }

  .result-input {
    /deep/ .el-input__inner {
      background-color: #f5f7fa;
      color: @info-color;
      font-weight: 500;
    }
  }
}

.analysis-textarea {
  /deep/ .el-textarea__inner {
    min-height: 150px;
    border-radius: 4px;
    border: 1px solid @border-color;
    transition: border-color 0.3s;

    &:focus {
      border-color: @primary-color;
      box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
    }
  }
}

.action-panel {
  display: flex;
  flex-direction: column;
  height: 100%;

  .action-buttons {
    margin-bottom: 20px;
  }

  .action-btn {
    width: 100%;
    margin-bottom: 10px;
    padding: 12px;
    font-size: 16px;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    transition: all 0.2s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
    }

    &.save-btn {
      background: linear-gradient(to right, @primary-color, #66b1ff);
      border: none;
    }
  }

  .summary-box {
    flex: 1;
    background: #f8f9fa;
    border-radius: 4px;
    padding: 15px;
    border: 1px solid @border-color;

    h3 {
      margin-top: 0;
      margin-bottom: 15px;
      color: #303133;
      font-size: 16px;
    }

    .summary-item {
      display: flex;
      align-items: center;
      margin-bottom: 10px;
      padding-bottom: 10px;
      border-bottom: 1px dashed @border-color;

      .label {
        flex: 1;
        color: #606266;
        font-weight: 500;
      }

      .value {
        width: 70px;
        text-align: right;
        font-weight: 600;
        color: @primary-color;

        &.normal {
          color: @success-color;
        }

        &.warning {
          color: @warning-color;
        }

        &.danger {
          color: @danger-color;
        }
      }

      .status {
        width: 80px;
        text-align: right;
        font-size: 13px;

        &.normal {
          color: @success-color;
        }

        &.warning {
          color: @warning-color;
        }

        &.danger {
          color: @danger-color;
        }
      }
    }
  }
}

// 响应式调整
@media (max-width: 992px) {
  .action-panel {
    flex-direction: row;
    gap: 20px;

    .action-buttons {
      flex: 1;
      margin-bottom: 0;
    }

    .summary-box {
      flex: 1;
    }
  }
}

@media (max-width: 768px) {
  .action-panel {
    flex-direction: column;
  }

  .compact-form {
    .el-col {
      margin-bottom: 10px;
    }
  }
}

// BMI状态类
.bmi-normal {
  color: @success-color;
}

.bmi-warning {
  color: @warning-color;
}

.bmi-danger {
  color: @danger-color;
}
</style>