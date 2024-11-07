<template>
  <div class="dashboard-view">
    <div class="panel-group">
      <el-row :gutter="40" class="panel-group">
        <el-col :xs="24" :lg="3" class="card-panel-col">
          <div class="card-panel">
            <h2>基础信息</h2>
            <el-upload
              ref="upload"
              :on-success="handleFileSuccess"
              accept=".jpg,.png,.bmp,.jpeg,.gif"
              action="/api/file/upload"
              list-type="picture-card"
              :auto-upload="true"
              :on-change="handleChange"
              :limit="1"
              :on-remove="handleRemove"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
            <el-dialog :visible.sync="dialogVisible">
              <img width="100%" :src="dialogImageUrl" alt="" />
            </el-dialog>
          </div>
        </el-col>
        <el-col :xs="24" :lg="20" class="card-panel-col">
          <div class="card-panel-info">
            <el-form
              class="el-form-item"
              :inline="true"
              ref="foodEntity"
              :rules="rules"
              :model="foodEntity"
              @submit.prevent
            >
              <el-form-item label="类别" prop="fType">
                <el-input
                  v-model="foodEntity.fType"
                  placeholder="类别"
                ></el-input>
              </el-form-item>
              <el-form-item label="名称" prop="fName">
                <el-input
                  v-model="foodEntity.fName"
                  placeholder="名称"
                ></el-input>
              </el-form-item>
              <el-form-item label="可食部" prop="fEdibleSection">
                <el-input
                  v-model="foodEntity.fEdibleSection"
                  placeholder="可食部"
                  @input="
                    foodEntity.fEdibleSection = foodEntity.fEdibleSection
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
        <el-col :xs="24" :sm="24" :lg="24" class="card-panel-col">
          <el-row style="margin-top: 10px; margin-bottom: 10px">
            <el-col :span="24">
              <h2>基础营养</h2>
              <el-form
                :inline="true"
                class="form-basic-Information"
                @submit.prevent
              >
                <el-form-item label="能量">
                  <el-input
                    v-model="foodEntity.fEnergy"
                    placeholder="能量"
                  ></el-input>
                </el-form-item>
                <el-form-item label="碳水">
                  <el-input
                    v-model="foodEntity.fCarbonWater"
                    placeholder="碳水"
                  ></el-input>
                </el-form-item>
                <el-form-item label="脂肪">
                  <el-input
                    v-model="foodEntity.fFat"
                    placeholder="脂肪"
                  ></el-input>
                </el-form-item>
                <el-form-item label="蛋白质">
                  <el-tooltip
                    class="item"
                    effect="dark"
                    content=""
                    placement="top-start"
                  >
                    <el-input
                      v-model="foodEntity.fProtein"
                      placeholder="蛋白质"
                    ></el-input>
                  </el-tooltip>
                </el-form-item>
                <el-form-item label="膳食纤维">
                  <el-tooltip
                    class="item"
                    effect="dark"
                    content=""
                    placement="top-start"
                  >
                    <el-input
                      v-model="foodEntity.fDietaryFiber"
                      placeholder="膳食纤维"
                    ></el-input>
                  </el-tooltip>
                </el-form-item>
              </el-form>
            </el-col>
          </el-row>
        </el-col>

        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel">
            <h2>维生素</h2>
            <el-form
              :inline="true"
              class="form-basic-Information"
              @submit.prevent
            >
              <el-form-item label="维A">
                <el-input
                  v-model="foodEntity.fVitaminA"
                  placeholder="维A"
                ></el-input>
              </el-form-item>
              <el-form-item label="维B1">
                <el-input
                  v-model="foodEntity.fVitaminB1"
                  placeholder="维B1"
                  @input="
                    foodEntity.fVitaminB1 = foodEntity.fVitaminB1
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="维B2">
                <el-input
                  v-model="foodEntity.fVitaminB2"
                  placeholder="维B2"
                  @input="
                    foodEntity.fVitaminB2 = foodEntity.fVitaminB2
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="维B3">
                <el-input
                  v-model="foodEntity.fVitaminB3"
                  placeholder="维B3"
                ></el-input>
              </el-form-item>
              <el-form-item label="维B6">
                <el-input
                  v-model="foodEntity.fVitaminB6"
                  placeholder="维B6"
                  @input="
                    foodEntity.fVitaminB6 = foodEntity.fVitaminB6
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="维B9">
                <el-input
                  v-model="foodEntity.fVitaminB9"
                  placeholder="维B9"
                ></el-input>
              </el-form-item>
              <el-form-item label="维B12">
                <el-input
                  v-model="foodEntity.fVitaminB12"
                  placeholder="维B12"
                  @input="
                    foodEntity.fVitaminB12 = foodEntity.fVitaminB12
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="维C">
                <el-input
                  v-model="foodEntity.fVitaminC"
                  placeholder="维C"
                  @input="
                    foodEntity.fVitaminC = foodEntity.fVitaminC
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="维D">
                <el-input
                  v-model="foodEntity.fVitaminD"
                  placeholder="维D"
                  @input="
                    foodEntity.fVitaminD = foodEntity.fVitaminD
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="维E">
                <el-input
                  v-model="foodEntity.fVitaminE"
                  placeholder="维E"
                  @input="
                    foodEntity.fVitaminE = foodEntity.fVitaminE
                      .replace(/[^\d^\.]+/g, '')
                      .replace(/^0+(\d)/, '$1')
                      .replace(/^\./, '0.')
                      .match(/^\d*(\.?\d{0,2})/g)[0]
                      .slice(0, 5)
                  "
                ></el-input>
              </el-form-item>
              <el-form-item label="维K">
                <el-input
                  v-model="foodEntity.fVitaminK"
                  placeholder="维K"
                  @input="
                    foodEntity.fVitaminK = foodEntity.fVitaminK
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
            <h2>微量元素</h2>
            <el-form
              :inline="true"
              class="form-basic-Information"
              @submit.prevent
            >
              <el-form-item label="钾">
                <el-input v-model="foodEntity.fK" placeholder="钾"></el-input>
              </el-form-item>
              <el-form-item label="钠">
                <el-input v-model="foodEntity.fNa" placeholder="钠"></el-input>
              </el-form-item>
              <el-form-item label="锌">
                <el-input v-model="foodEntity.fZn" placeholder="锌"></el-input>
              </el-form-item>
              <el-form-item label="钙">
                <el-tooltip
                  class="item"
                  effect="dark"
                  content=""
                  placement="top-start"
                >
                  <el-input
                    v-model="foodEntity.fCa"
                    placeholder="钙"
                  ></el-input>
                </el-tooltip>
              </el-form-item>
              <el-form-item label="硒">
                <el-input v-model="foodEntity.fSe" placeholder="硒"></el-input>
              </el-form-item>
              <el-form-item label="铁">
                <el-input v-model="foodEntity.fFe" placeholder="铁"></el-input>
              </el-form-item>
            </el-form>
          </div>
        </el-col>
        <el-col :xs="24" :lg="12" class="card-panel-col">
          <div class="card-panel">
            <h2>作用</h2>
            <el-input
              type="textarea"
              v-model="foodEntity.fRole"
              :rows="4"
              placeholder="请输入内容"
            >
            </el-input>
          </div>
        </el-col>
        <el-col :xs="24" :lg="12" class="card-panel-col">
          <div class="card-panel">
            <h2>备注</h2>
            <el-input
              type="textarea"
              v-model="foodEntity.fNote"
              :rows="4"
              placeholder="请输入内容"
            >
            </el-input>
          </div>
        </el-col>
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel">
            <h2>操作</h2>
            <el-button type="primary" @click="handleSave('foodEntity')"
              >保存
            </el-button>
            <el-button
              type="info"
              @click="$router.push('/home/nutrition/FoodNutrition')"
              >返回
            </el-button>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
    
<script>
import {
  _getUserAll,
  _getFoodNutritionByFactor,
  _operationFoodNutrition,
} from "@/api/api.js";
import { parseDateTime } from "../../utils/calendar";

export default {
  name: "OpBodyComposition",
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
      foodEntity: {
        fID: "",
        fImg: "",
        fType: "",
        fName: "",
        fEdibleSection: "",
        fEnergy: "",
        fCarbonWater: "",
        fFat: "",
        fProtein: "",
        fDietaryFiber: "",
        fVitaminA: "",
        fVitaminC: "",
        fVitaminD: "",
        fVitaminE: "",
        fVitaminK: "",
        fVitaminB1: "",
        fVitaminB2: "",
        fVitaminB3: "",
        fVitaminB6: "",
        fVitaminB9: "",
        fVitaminB12: "",
        fK: "",
        fNa: "",
        fCa: "",
        fZn: "",
        fSe: "",
        fFe: "",
        fRole: "",
        fCreateTime: "",
        fUpdateTime: "",
        fNote: "",
        op: "",
      },
      rules: {
        fType: [{ required: true, message: "请输入食物类别", trigger: "blur" }],
        fName: [{ required: true, message: "请输入食物名称", trigger: "blur" }],
        fEdibleSection: [
          { required: true, message: "请输入可食部分", trigger: "blur" },
        ],
      },
    };
  },
  computed: {},
  methods: {
    init() {
      if (this.foodEntity.fID) {
        this.isOperation = true;
        _getFoodNutritionByFactor(this.foodEntity.fID).then((res) => {
          this.foodEntity = res.data[0];
        });
      } else {
        this.isOperation = false;
      }
    },
    timeChange(val) {
      const d = Date.parse(val);
      this.foodEntity.recordTime = parseDateTime(new Date(d));
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    handleFileSuccess(response, file, fileList) {
      //   this.$message.success("封面上传成功");
      if (response.status == 200) {
        this.foodEntity.fImg = response.obj;
      }
    },
    handleChange(file, fileList) {
      if (fileList.length > 0) {
        // 当上传图片成功时，即隐藏上传按钮
        this.$refs.upload.$children[1].$el.style.display = "none"; //= ('display', 'none');
      }
    },
    handleRemove(file, fileList) {
      this.fileList = [];
      if (fileList.length == 0) {
        this.$refs.upload.$children[1].$el.style.display = "inline-block"; // = ('display', 'inline-block');
      }
    },
    handleSave(foodEntity) {
      this.$refs[foodEntity].validate((valid) => {
        if (valid) {
          if (this.isOperation) {
            this.foodEntity.op = "upd";
          } else {
            this.foodEntity.op = "add";
          }
          _operationFoodNutrition(this.foodEntity).then((res) => {
            this.$message.success(res.data.msg);
            this.$router.push({
              path: "/home/nutrition/FoodNutrition" || "/",
            });
          });
        } else {
          this.$message.error("error sufFatt!!");
          return false;
        }
      });
    },
  },
  mounted() {},
  created() {
    this.foodEntity.fID = this.$route.params.id;
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
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.el-form-item {
  margin: 0px;
}
.card-panel-info {
  margin-top: 180px;
}
</style>
    