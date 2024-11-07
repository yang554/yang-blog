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
        <el-col :xs="24" :lg="9" class="card-panel-col">
          <div class="card-panel-info">
            <el-form
              class="el-form-item"
              :inline="true"
              ref="recipesEntity"
              :rules="rules"
              :model="recipesEntity"
              @submit.prevent
            >
              <el-form-item label="类别" prop="fType">
                <el-input
                  v-model="recipesEntity.fType"
                  placeholder="类别"
                ></el-input>
              </el-form-item>
              <el-form-item label="名称" prop="fName">
                <el-input
                  v-model="recipesEntity.fName"
                  placeholder="名称"
                ></el-input>
              </el-form-item>
            </el-form>
          </div>
        </el-col>
        <el-col :xs="24" :lg="12" class="card-panel-col">
          <div class="card-panel">
            <h2>小贴士</h2>
            <el-input
              type="textarea"
              v-model="recipesEntity.fTips"
              :rows="8"
              placeholder="小贴士"
            >
            </el-input>
          </div>
        </el-col>
        <el-col :xs="24" :lg="12" class="card-panel-col">
          <div class="card-panel">
            <h2>简介</h2>
            <el-input
              type="textarea"
              v-model="recipesEntity.fIntro"
              :rows="8"
              placeholder="请输入简介"
            >
            </el-input>
          </div>
        </el-col>
        <el-col :xs="24" :lg="12" class="card-panel-col">
          <div class="card-panel">
            <h2>做法</h2>
            <el-input
              type="textarea"
              v-model="recipesEntity.fMade"
              :rows="8"
              placeholder="请输入做法"
            >
            </el-input>
          </div>
        </el-col>
        <el-col :xs="24" :lg="12" class="card-panel-col">
          <div class="card-panel">
            <h2>配料</h2>
            <el-input
              type="textarea"
              v-model="recipesEntity.fBatching"
              :rows="8"
              placeholder="请输入配料"
            >
            </el-input>
          </div>
        </el-col>
        <el-col :xs="24" :lg="12" class="card-panel-col">
          <div class="card-panel">
            <h2>调料</h2>
            <el-input
              type="textarea"
              v-model="recipesEntity.fSeasoning"
              :rows="8"
              placeholder="请输入调料"
            >
            </el-input>
          </div>
        </el-col>
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div class="card-panel">
            <h2>操作</h2>
            <el-button type="primary" @click="handleSave('recipesEntity')"
              >保存
            </el-button>
            <el-button
              type="info"
              @click="$router.push('/home/nutrition/NutritionalRecipe')"
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
  _getNutritionalRecipeById,
  _operationNutritionalRecipe,
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
        fImg: "",
        fType: "",
        fName: "",
        fIntro: "",
        fMade: "",
        fSeasoning: "",
        fBatching: "",
        fCreateTime: "",
        fUpdateTime: "",
        fTips:"",
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
      if (this.recipesEntity.fID) {
        this.isOperation = true;
        _getNutritionalRecipeById(this.recipesEntity.fID).then((res) => {
          this.recipesEntity = res.data.obj;
        });
      } else {
        this.isOperation = false;
      }
    },
    timeChange(val) {
      const d = Date.parse(val);
      this.recipesEntity.recordTime = parseDateTime(new Date(d));
    },
    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    },
    handleFileSuccess(response, file, fileList) {
      //   this.$message.success("封面上传成功");
      if (response.status == 200) {
        this.recipesEntity.fImg = response.obj;
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
  mounted() {},
  created() {
    this.recipesEntity.fID = this.$route.params.id;
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
    