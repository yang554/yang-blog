<template>
  <div class="dashboard-view">
    <div class="panel-group">
      <el-row :gutter="40" class="panel-group">
        <el-col :xs="24" :sm="24" :lg="24" class="card-panel-col">
          <el-row style="margin-top: 10px; margin-bottom: 10px">
            <el-col :span="24">
              <h2>基础信息</h2>
              <el-form
                :model="bodyEntity"
                :inline="true"
                ref="bodyEntity"
                :rules="rules"
                class="form-basic-Information"
              >
                <el-form-item label="姓名" prop="uID">
                  <el-select
                    size="small"
                    v-model="bodyEntity.uID"
                    filterable
                    placeholder="请选择用户"
                    style="width: 130px"
                  >
                    <el-option
                      v-for="(item, index) in uNameAll"
                      :key="index"
                      :label="item.username"
                      :value="item.id"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="性别" prop="sex">
                  <el-select
                    v-model="bodyEntity.sex"
                    @change="handleChangeSex"
                    placeholder="性别"
                    style="width: 80px"
                  >
                    <el-option label="男" value="男"></el-option>
                    <el-option label="女" value="女"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="年龄" prop="age">
                  <el-input
                    v-model="bodyEntity.age"
                    placeholder="年龄"
                    @input="handleAge"
                  ></el-input>
                </el-form-item>
                <el-form-item label="身高(cm)">
                  <el-input
                    v-model="bodyEntity.height"
                    placeholder="身高"
                    @input="handleHeight"
                  ></el-input>
                </el-form-item>
                <el-form-item label="体重(kg)">
                  <el-input
                    v-model="bodyEntity.weight"
                    placeholder="体重"
                    @input="handleWeight"
                  ></el-input>
                </el-form-item>
                <el-form-item label="BMI">
                  <el-tooltip
                    class="item"
                    effect="dark"
                    content="用于估算体重是否健康的指标。它通过比较个人的体重和身高来计算出一个数值，从而判断个人的体重状况"
                    placement="top-start"
                  >
                    <el-input
                      v-model="bodyEntity.bmi"
                      placeholder="BMI"
                      :disabled="true"
                    ></el-input>
                  </el-tooltip>
                </el-form-item>
                <el-form-item label="肥胖等级(中国标准)">
                  <el-tooltip
                    class="item"
                    effect="dark"
                    content="用于估算体重是否健康的指标。它通过比较个人的体重和身高来计算出一个数值，从而判断个人的体重状况"
                    placement="top-start"
                  >
                    <el-input
                      v-model="bodyEntity.obesityLevel"
                      placeholder="肥胖等级"
                      :disabled="true"
                    ></el-input>
                  </el-tooltip>
                </el-form-item>
                <el-form-item label="基础代谢(kcal)">
                  <el-tooltip
                    class="item"
                    effect="dark"
                    content="人体维持生命的所有器官所需要的最低能量需要"
                    placement="top-start"
                  >
                    <el-input
                      v-model="bodyEntity.basalMetabolism"
                      placeholder="基础代谢"
                      :disabled="true"
                    ></el-input>
                  </el-tooltip>
                </el-form-item>
                <el-form-item label="进食量(kg)">
                  <el-input
                    v-model="bodyEntity.foodIntake"
                    placeholder="进食量"
                    @input="
                      bodyEntity.foodIntake = bodyEntity.foodIntake
                        .replace(/[^\d^\.]+/g, '')
                        .replace(/^0+(\d)/, '$1')
                        .replace(/^\./, '0.')
                        .match(/^\d*(\.?\d{0,2})/g)[0]
                        .slice(0, 5)
                    "
                  ></el-input>
                </el-form-item>
                <el-form-item label="记录时间" required>
                  <el-date-picker
                    v-model="bodyEntity.recordTime"
                    format="yyyy-MM-dd HH:mm:ss"
                    type="datetime"
                    placeholder="选择本次记录时间"
                    style="width: 200px"
                    :picker-options="pickerOptions"
                    @change="timeChange"
                  >
                  </el-date-picker>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
        </el-col>
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel">
            <h2>身体成分</h2>
            <el-form :inline="true" class="form-basic-Information">
              <el-form-item label="体脂(%)">
                <el-input
                  v-model="bodyEntity.fat"
                  placeholder="体脂"
                  @input="handleFat"
                ></el-input>
              </el-form-item>
              <el-form-item label="内脏脂肪">
                <el-input
                  v-model="bodyEntity.visceralFat"
                  placeholder="内脏脂肪"
                  @input="
                    bodyEntity.visceralFat = bodyEntity.visceralFat
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="肌肉(%)">
                <el-input
                  v-model="bodyEntity.muscle"
                  placeholder="肌肉"
                  @input="
                    bodyEntity.muscle = bodyEntity.muscle
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="骨骼肌(%)">
                <el-input
                  v-model="bodyEntity.skeletalMuscle"
                  placeholder="骨骼肌"
                  @input="handleSkeletalMuscle"
                ></el-input>
              </el-form-item>
              <el-form-item label="骨重(kg)">
                <el-input
                  v-model="bodyEntity.boneMass"
                  placeholder="骨重"
                  @input="
                    bodyEntity.boneMass = bodyEntity.boneMass
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="水分(%)">
                <el-input
                  v-model="bodyEntity.waterContent"
                  placeholder="水分"
                  @input="handleWaterContent"
                ></el-input>
              </el-form-item>
              <el-form-item label="蛋白质(%)">
                <el-input
                  v-model="bodyEntity.protein"
                  placeholder="蛋白质"
                  @input="
                    bodyEntity.protein = bodyEntity.protein
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="去脂体重(kg)">
                <el-input
                  v-model="bodyEntity.fatFreeBodyWeight"
                  placeholder="去脂体重"
                  @input="
                    bodyEntity.fatFreeBodyWeight = bodyEntity.fatFreeBodyWeight
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="肌肉量(kg)">
                <el-input
                  v-model="bodyEntity.muscleMass"
                  placeholder="肌肉量"
                  @input="
                    bodyEntity.muscleMass = bodyEntity.muscleMass
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="含水量(kg)">
                <el-input
                  v-model="bodyEntity.waterContent_1"
                  placeholder="含水量"
                  @input="
                    bodyEntity.waterContent_1 = bodyEntity.waterContent_1
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="脂肪量(kg)">
                <el-input
                  v-model="bodyEntity.fatMass"
                  placeholder="脂肪量"
                  @input="
                    bodyEntity.fatMass = bodyEntity.fatMass
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="皮下脂肪率(%)">
                <el-input
                  v-model="bodyEntity.subcutaneousFatPercentage"
                  placeholder="皮下脂肪率"
                  @input="
                    bodyEntity.subcutaneousFatPercentage =
                      bodyEntity.subcutaneousFatPercentage
                        .replace(/[^\d^\.]+/g, '')
                        .replace(/^0+(\d)/, '$1')
                        .replace(/^\./, '0.')
                        .match(/^\d*(\.?\d{0,2})/g)[0]
                        .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
            </el-form>
          </div>
        </el-col>

        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel">
            <h2>肌肉-脂肪控制</h2>
            <el-form :inline="true" class="form-basic-Information">
              <el-form-item label="体重控制量(kg)">
                <el-input
                  v-model="bodyEntity.weightControlAmount"
                  placeholder="体重控制量"
                  :disabled="true"
                ></el-input>
              </el-form-item>
              <el-form-item label="脂肪控制量(kg)">
                <el-input
                  v-model="bodyEntity.fatControlAmount"
                  placeholder="脂肪控制量"
                ></el-input>
              </el-form-item>
              <el-form-item label="肌肉控制量(kg)">
                <el-input
                  v-model="bodyEntity.muscleControlVolume"
                  placeholder="肌肉控制量"
                ></el-input>
              </el-form-item>
              <el-form-item label="标准体重(kg)">
                <el-tooltip
                  class="item"
                  effect="dark"
                  content="根据亚洲标准体重公式"
                  placement="top-start"
                >
                  <el-input
                    v-model="bodyEntity.standardWeight"
                    placeholder="标准体重"
                    :disabled="true"
                  ></el-input>
                </el-tooltip>
              </el-form-item>
              <el-form-item label="理想体重(kg)">
                <el-input
                  v-model="bodyEntity.idealWeight"
                  placeholder="理想体重"
                  :disabled="true"
                ></el-input>
              </el-form-item>
            </el-form>
          </div>
        </el-col>
        <el-col :xs="24" :lg="16" class="card-panel-col">
          <div class="card-panel">
            <h2>分析结果-建议</h2>
            <el-input
              type="textarea"
              v-model="bodyEntity.note"
              :rows="4"
              placeholder="请输入内容"
            >
            </el-input>
          </div>
        </el-col>
        <el-col :xs="24" :lg="4" class="card-panel-col">
          <div class="card-panel">
            <h2>操作</h2>
            <el-button type="primary" @click="handleSave('bodyEntity')"
              >保存分析结果
            </el-button>
            <el-button
              type="info"
              @click="$router.push('/home/nutrition/BodyComposition')"
              >返回
            </el-button>
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
  computed: {},
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
          this.bodyEntity = res.data.obj;
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
  mounted() {},
  created() {
    this.bodyEntity.id = this.$route.params.id;
    this.init();
  },
};
</script>
    
<style lang="less" scoped>
.form-basic-Information {
  border: 1px solid #ccc; /* 添加边框样式 */
  padding: 10px; /* 内边距 */
  border-radius: 8px; /* 边框圆角 */
  .el-form-item {
    text-align: center; /* 标签居中 */
    margin: 10px;
  }
  .el-input {
    width: 100px;
  }
}
</style>
    