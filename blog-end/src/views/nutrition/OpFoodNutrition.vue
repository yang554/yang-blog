<template>
  <div class="dashboard-view">
    <div class="panel-group">
      <el-row :gutter="25" class="panel-group">
        <!-- 基础信息区域 -->
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel">
            <h2 class="nutrition-h2-cs">
              <i class="el-icon-info"></i> 基础信息
            </h2>
            <div class="basic-info-container">
              <div class="image-upload-section">
                <div class="image-upload-wrapper">
                  <div v-if="foodEntity.fImg">
                    <img :src="foodEntity.fImg" class="preview-image">
                  </div>
                  <div v-else>
                    <i class="el-icon-plus upload-icon"></i>
                    <div class="upload-text">上传食物图片</div>
                    <div style="font-size: 12px; color: #909399; margin-top: 5px;">
                      支持 JPG/PNG 格式
                    </div>
                  </div>
                  <el-upload ref="upload" :on-success="handleFileSuccess" accept=".jpg,.png,.bmp,.jpeg,.gif"
                    action="/api/file/upload" :auto-upload="true" :on-change="handleChange" :limit="1"
                    :on-remove="handleRemove" class="upload-button">
                    <el-button size="small" type="primary" style="margin-top: 15px;">
                      {{ foodEntity.fImg ? '更换图片' : '选择图片' }}
                    </el-button>
                  </el-upload>
                </div>
              </div>

              <div class="info-form-section">
                <el-form ref="foodEntity" :model="foodEntity" label-width="100px">
                  <el-row :gutter="20">
                    <el-col :xs="24" :sm="12" :md="8">
                      <el-form-item label="类别" prop="fType" class="highlight-input">
                        <el-input v-model="foodEntity.fType" placeholder="例：水果、蔬菜、肉类"></el-input>
                      </el-form-item>
                    </el-col>
                    <el-col :xs="24" :sm="12" :md="8">
                      <el-form-item label="名称" prop="fName" class="highlight-input">
                        <el-input v-model="foodEntity.fName" placeholder="食物名称"></el-input>
                      </el-form-item>
                    </el-col>
                    <el-col :xs="24" :sm="12" :md="8">
                      <el-form-item label="可食部(%)" prop="fEdibleSection" class="highlight-input">
                        <el-input v-model="foodEntity.fEdibleSection" placeholder="可食用部分百分比" @input="foodEntity.fEdibleSection = foodEntity.fEdibleSection
                            .replace(/[^\d^\.]+/g, '')
                            .replace(/^0+(\d)/, '$1')
                            .replace(/^\./, '0.')
                            .match(/^\d*(\.?\d{0,2})/g)[0]
                            .slice(0, 5)">
                          <template slot="append">%</template>
                        </el-input>
                      </el-form-item>
                    </el-col>
                  </el-row>
                </el-form>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 基础营养区域 -->
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel">
            <h2 class="nutrition-h2-cs">
              <i class="el-icon-data-analysis"></i> 基础营养（每100克含量）
            </h2>
            <div class="nutrient-grid">
              <div class="nutrient-card">
                <div class="nutrient-label">能量</div>
                <el-input v-model="foodEntity.fEnergy" placeholder="千卡">
                  <template slot="append">kcal</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">碳水化合物</div>
                <el-input v-model="foodEntity.fCarbonWater" placeholder="克">
                  <template slot="append">g</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">脂肪</div>
                <el-input v-model="foodEntity.fFat" placeholder="克">
                  <template slot="append">g</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">蛋白质</div>
                <el-input v-model="foodEntity.fProtein" placeholder="克">
                  <template slot="append">g</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">膳食纤维</div>
                <el-input v-model="foodEntity.fDietaryFiber" placeholder="克">
                  <template slot="append">g</template>
                </el-input>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 维生素区域 -->
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel vitamin-group">
            <h2 class="nutrition-h2-cs">
              <i class="el-icon-apple"></i> 维生素含量（每100克）
            </h2>
            <div class="nutrient-grid">
              <div class="nutrient-card">
                <div class="nutrient-label">维生素A</div>
                <el-input v-model="foodEntity.fVitaminA" placeholder="微克">
                  <template slot="append">ug</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">维生素B1</div>
                <el-input v-model="foodEntity.fVitaminB1" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">维生素B2</div>
                <el-input v-model="foodEntity.fVitaminB2" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">维生素B3</div>
                <el-input v-model="foodEntity.fVitaminB3" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">维生素B6</div>
                <el-input v-model="foodEntity.fVitaminB6" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">维生素B9</div>
                <el-input v-model="foodEntity.fVitaminB9" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">维生素B12</div>
                <el-input v-model="foodEntity.fVitaminB12" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">维生素C</div>
                <el-input v-model="foodEntity.fVitaminC" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">维生素D</div>
                <el-input v-model="foodEntity.fVitaminD" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">维生素E</div>
                <el-input v-model="foodEntity.fVitaminE" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">维生素K</div>
                <el-input v-model="foodEntity.fVitaminK" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 微量元素区域 -->
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel mineral-group">
            <h2 class="nutrition-h2-cs">
              <i class="el-icon-magic-stick"></i> 微量元素（每100克）
            </h2>
            <div class="nutrient-grid">
              <div class="nutrient-card">
                <div class="nutrient-label">钾</div>
                <el-input v-model="foodEntity.fK" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">钠</div>
                <el-input v-model="foodEntity.fNa" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">锌</div>
                <el-input v-model="foodEntity.fZn" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">钙</div>
                <el-input v-model="foodEntity.fCa" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">硒</div>
                <el-input v-model="foodEntity.fSe" placeholder="毫克">
                  <template slot="append">ug</template>
                </el-input>
              </div>
              <div class="nutrient-card">
                <div class="nutrient-label">铁</div>
                <el-input v-model="foodEntity.fFe" placeholder="毫克">
                  <template slot="append">mg</template>
                </el-input>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 作用与备注区域 -->
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel other-group">
            <el-row :gutter="25">
              <el-col :xs="24" :lg="12">
                <h3 class="nutrient-group-title">作用与功效</h3>
                <el-input type="textarea" v-model="foodEntity.fRole" :rows="6" placeholder="描述该食物的主要营养作用和健康功效..."
                  resize="none" class="custom-textarea"></el-input>
              </el-col>
              <el-col :xs="24" :lg="12">
                <h3 class="nutrient-group-title">备注信息</h3>
                <el-input type="textarea" v-model="foodEntity.fNote" :rows="6" placeholder="添加任何其他相关信息或说明..."
                  resize="none" class="custom-textarea"></el-input>
              </el-col>
            </el-row>
          </div>
        </el-col>

        <!-- 操作按钮区域 -->
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="action-panel">
            <el-button type="primary" size="small" @click="handleSave('foodEntity')" icon="el-icon-check">
              保存食物信息
            </el-button>
            <el-button type="info"  size="small" @click="$router.push('/home/nutrition/FoodNutrition')" icon="el-icon-back">
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
        fEdibleSection: "100",
        fEnergy: "/",
        fCarbonWater: "/",
        fFat: "/",
        fProtein: "/",
        fDietaryFiber: "/",
        fVitaminA: "/",
        fVitaminC: "/",
        fVitaminD: "/",
        fVitaminE: "/",
        fVitaminK: "/",
        fVitaminB1: "/",
        fVitaminB2: "/",
        fVitaminB3: "/",
        fVitaminB6: "/",
        fVitaminB9: "/",
        fVitaminB12: "/",
        fK: "/",
        fNa: "/",
        fCa: "/",
        fZn: "/",
        fSe: "/",
        fFe: "/",
        fRole: "/",
        fCreateTime: "/",
        fUpdateTime: "/",
        fNote: "/",
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
        this.foodEntity.fImg = response.data;
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
  mounted() { },
  created() {
    this.foodEntity.fID = this.$route.params.id;
    this.init();
  },
};
</script>
    
<style lang="less" scoped>
.dashboard-view {
  max-width: 1600px;
  margin: 0 auto;
}

.panel-group {
  background: white;
  border-radius: 16px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  padding: 30px;
}

.el-row {
  margin-bottom: 25px;
}

.card-panel-col {
  margin-bottom: 25px;
}

.card-panel {
  background: #fff;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  height: 100%;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.card-panel:hover {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}

.nutrition-h2-cs {
  font-size: 20px;
  color: #2c3e50;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f2f5;
  display: flex;
  align-items: center;
}

.nutrition-h2-cs i {
  margin-right: 12px;
  color: #409eff;
  background: #e6f7ff;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 基础信息区域优化 */
.basic-info-container {
  display: flex;
  gap: 25px;
}

.image-upload-section {
  flex: 0 0 200px;
}

.info-form-section {
  flex: 1;
}

/* 图片上传样式优化 */
.image-upload-wrapper {
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  padding: 20px;
  background: #fafafa;
  text-align: center;
  transition: border-color 0.3s;
  min-height: 240px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.image-upload-wrapper:hover {
  border-color: #409eff;
}

.upload-icon {
  font-size: 48px;
  color: #8c939d;
  margin-bottom: 15px;
}

.upload-text {
  font-size: 14px;
  color: #606266;
  margin-top: 10px;
}

.preview-image {
  width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 6px;
}

/* 表单样式优化 */
.el-form-item {
  margin-bottom: 22px;
}

.el-form-item__label {
  font-weight: 600;
  color: #5a5e66;
  padding-right: 15px;
  width: 100px;
  text-align: right;
}

.el-input__inner,
.el-textarea__inner {
  border-radius: 6px;
  transition: border-color 0.3s;
}

.el-input__inner:focus,
.el-textarea__inner:focus {
  border-color: #409eff;
}

/* 营养信息分组优化 */
.nutrient-group {
  margin-bottom: 30px;
}

.nutrient-group-title {
  font-size: 18px;
  color: #409eff;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 4px solid #409eff;
}

.nutrient-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.nutrient-card {
  background: #f8fafc;
  border-radius: 8px;
  padding: 15px;
  border-left: 3px solid #409eff;
  transition: all 0.3s;
}

.nutrient-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
}

.nutrient-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
  font-weight: 500;
}

/* 操作按钮区域优化 */
.action-panel {
  display: flex;
  justify-content: center;
  gap: 25px;
  padding: 25px 0;
  margin-top: 20px;
}

.el-button {
  border-radius: 8px;
  padding: 14px 36px;
  font-weight: 500;
  font-size: 16px;
  transition: all 0.3s;
  letter-spacing: 1px;
}

.el-button--primary {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border: none;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
}

.el-button--info {
  background: linear-gradient(135deg, #909399, #a6a9ad);
  border: none;
}

.el-button--info:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(144, 147, 153, 0.3);
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .basic-info-container {
    flex-direction: column;
  }

  .image-upload-section {
    flex: 0 0 auto;
  }

  .nutrient-grid {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  }
}

@media (max-width: 768px) {
  .panel-group {
    padding: 15px;
  }

  .nutrient-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
  }

  .el-form-item__label {
    width: 80px;
    font-size: 14px;
  }

  .el-button {
    padding: 12px 24px;
    font-size: 14px;
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(15px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-panel {
  animation: fadeIn 0.5s ease;
}

/* 颜色标识 */
.vitamin-group .nutrient-card {
  border-left-color: #67c23a;
}

.mineral-group .nutrient-card {
  border-left-color: #e6a23c;
}

.other-group .nutrient-card {
  border-left-color: #f56c6c;
}

.vitamin-group .nutrient-group-title {
  color: #67c23a;
  border-left-color: #67c23a;
}

.mineral-group .nutrient-group-title {
  color: #e6a23c;
  border-left-color: #e6a23c;
}

.other-group .nutrient-group-title {
  color: #f56c6c;
  border-left-color: #f56c6c;
}

/* 特殊输入框样式 */
.highlight-input {
  border: 1px solid #409eff;
  background-color: #f0f7ff;
}

.highlight-input .el-input__inner {
  background-color: #f0f7ff;
}</style>
    