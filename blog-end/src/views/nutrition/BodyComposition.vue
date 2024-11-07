<template>
  <div class="user-list-view">
    <!--搜索-->
    <el-row style="margin-top: 10px; margin-bottom: 10px">
      <el-col :span="5">
        <span class="span-txt">&emsp;姓名：</span>
        <el-select
          size="small"
          v-model="uID"
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
      </el-col>
      <el-col :span="1">
        <el-button size="small" type="primary" @click="handleSearchEvent">
          <i class="el-icon-search"></i>
          搜索
        </el-button>
      </el-col>
      <el-col :span="1">
        <el-button size="small" type="info" @click="handleReset">
          <i class="el-icon-refresh"></i>
          重置
        </el-button>
      </el-col>
      <el-col :span="1">
        <el-button size="small" type="success" @click="handleAddBtn">
          <i class="el-icon-plus"></i>
          添加
        </el-button>
      </el-col>
    </el-row>

    <el-table
      v-loading="pagination.loading"
      border
      :data="pagination.currentTableData"
      style="width: 100%"
      :max-height="this.screenHeight"
      :header-cell-style="{ 'text-align': 'center' }"
    >
    <el-table-column label="记录日期" width="110px" align="center">
        <template slot-scope="scope">
          {{ scope.row.recordTime }}
        </template>
      </el-table-column>
      <el-table-column label="编码" width="130px" align="center">
        <template slot-scope="scope">
          {{ scope.row.id }}
        </template>
      </el-table-column>
      <el-table-column label="姓名" align="center">
        <template slot-scope="scope">
          <!-- {{ scope.row.e_title | tableTile }} -->
          {{ scope.row.uName }}
        </template>
      </el-table-column>
      <el-table-column label="身高(cm)" align="center">
        <template slot-scope="scope">
          {{ scope.row.height }}
        </template>
      </el-table-column>
      <el-table-column label="体重(kg)" align="center">
        <template slot-scope="scope">
          {{ scope.row.weight }}
        </template>
      </el-table-column>
      <el-table-column label="年龄">
        <template slot-scope="scope">
          {{ scope.row.age | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="进食量(kg)" align="center">
        <template slot-scope="scope"> {{ scope.row.foodIntake }} </template>
      </el-table-column>
      <el-table-column label="基础代谢(kcal)" align="center">
        <template slot-scope="scope">
          {{ scope.row.basalMetabolism }}
        </template>
      </el-table-column>
      <el-table-column label="BMI">
        <template slot-scope="scope">
          {{ scope.row.bmi | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="体脂率(%)">
        <template slot-scope="scope">
          {{ scope.row.fat | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="内脏脂肪">
        <template slot-scope="scope">
          {{ scope.row.visceralFat | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="肌肉量(kg)">
        <template slot-scope="scope">
          {{ scope.row.muscleMass | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="骨骼肌(%)">
        <template slot-scope="scope">
          {{ scope.row.skeletalMuscle | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="骨重(kg)">
        <template slot-scope="scope">
          {{ scope.row.boneMass | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="水分(%)">
        <template slot-scope="scope">
          {{ scope.row.waterContent | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="蛋白质(%)">
        <template slot-scope="scope">
          {{ scope.row.protein | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="去脂体重(kg)">
        <template slot-scope="scope">
          {{ scope.row.fatFreeBodyWeight | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="皮下脂肪(%)">
        <template slot-scope="scope">
          {{ scope.row.subcutaneousFatPercentage | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="体重控制量(kg)">
        <template slot-scope="scope">
          {{ scope.row.weightControlAmount | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="脂肪控制量(kg)">
        <template slot-scope="scope">
          {{ scope.row.fatControlAmount | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="肌肉控制量(kg)">
        <template slot-scope="scope">
          {{ scope.row.muscleControlVolume | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="肥胖等级">
        <template slot-scope="scope">
          {{ scope.row.obesityLevel | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="标准体重(kg)">
        <template slot-scope="scope">
          {{ scope.row.standardWeight | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="理想体重(kg)">
        <template slot-scope="scope">
          {{ scope.row.idealWeight | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="生理年龄">
        <template slot-scope="scope">
          {{ scope.row.physiologicalAge | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="success"
            @click="
              $router.push(
                '/home/nutrition/EditBodyComposition/' + scope.row.id
              )
            "
            >查看\编辑
          </el-button>
          <el-button
            size="mini"
            type="danger"
            @click="handleDelete(scope.$index, scope.row)"
            >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 -->
    <div style="margin-top: 20px" class="my-border-padding">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.currentPage"
        :page-sizes="pagination.pageSizes"
        :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
      >
      </el-pagination>
    </div>
  </div>
</template>
  
<script>
import {
  _selectByName,
  _delNutritionById,
  _getNutritionByUser,
  _getNutritionAll,
  _getUserAll,
} from "@/api/api.js";
export default {
  name: "BodyComposition",
  data() {
    return {
      screenHeight: 950,
      tableData: [],
      pagination: {
        listData: null, //总data
        currentPage: 1, //当前页
        pageSizes: [5, 10, 15, 20],
        pageSize: 10, //每页显示条数
        totalPage: 1, //总页数
        currentTableData: [], //当前页data
        total: 0, //总数量
        loading: true, //table加载样式是否显示
      },
      uNameAll: [], //所有用户
      searchName: "", //搜索的用户名
      uID: "", //用户ID
    };
  },
  filters: {
    tableTile(value) {
      if (!value) return "";
      if (value.length > 19) {
        return value.slice(0, 19) + "...";
      }
      return value;
    },
  },
  mounted() {
    this.updateScreenHeight(); // 组件挂载时获取屏幕高度
    window.addEventListener("resize", this.updateScreenHeight); // 监听窗口大小变化
  },
  methods: {
    updateScreenHeight() {
      this.screenHeight = window.innerHeight - 300; // 更新屏幕高度
    },
    //初始化
    init() {
      _getUserAll().then((res) => {
        if (res.data.length > 0) {
          this.uNameAll = res.data;
        }
      });
      _getNutritionAll().then((res) => {
        if (res.status === 200) {
          if (res.data.length > 0) {
            this.pagination.listData = res.data;
            this.pagination.total = res.data.length;

            this.pagination.totalPage = Math.ceil(
              this.pagination.listData.length / this.pagination.pageSize
            );
            this.pagination.totalPage =
              this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

            this.getCurrentPageData(
              this.pagination.listData,
              this.pagination.currentPage,
              this.pagination.pageSize
            );
            this.pagination.loading = false;
          } else {
          }
        }
      });
    },
    //分页
    getCurrentPageData(listData, currentPage, pageSize) {
      let begin = (currentPage - 1) * pageSize;
      let end = currentPage * pageSize;
      this.pagination.currentTableData = listData.slice(begin, end);
    },
    //表格操作-删除
    handleDelete(index, row) {
      this.$confirm("此操作将永久删除该标签, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          //网络请求
          _delNutritionById(row.id).then((res) => {
            if (res.data.status === 200) {
              this.init();
              this.$message.success("删除成功!");
            }
          });
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: "已取消删除",
          });
        });
    },
    /*搜索*/
    handleSearchEvent() {
      if (this.uID == "") {
        this.init();
      } else {
        _getNutritionByUser(this.uID).then((res) => {
          if (res.data.length > 0) {
            this.pagination.listData = res.data;
            this.pagination.total = res.data.length;

            this.pagination.totalPage = Math.ceil(
              this.pagination.listData.length / this.pagination.pageSize
            );
            this.pagination.totalPage =
              this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

            this.getCurrentPageData(
              this.pagination.listData,
              this.pagination.currentPage,
              this.pagination.pageSize
            );
            this.pagination.loading = false;
          } else {
            this.$message.error("查无此人");
          }
        });
      }
    },
    handleReset() {
      this.uID = "";
    },
    handleAddBtn() {
      this.$router.push({
        path: "/home/nutrition/AddBodyComposition" || "/",
      });
    },
    /*分页事件*/
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.getCurrentPageData(
        this.pagination.listData,
        this.pagination.currentPage,
        this.pagination.pageSize
      );
    },
    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.getCurrentPageData(
        this.pagination.listData,
        this.pagination.currentPage,
        this.pagination.pageSize
      );
    },
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.updateScreenHeight); // 组件销毁前移除事件监听
  },
  created() {
    this.init();
  },
};
</script>
  
<style  lang="less" scoped>
.span-txt {
  font-size: 14px;
  color: black;
}
</style>
  