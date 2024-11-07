<template>
  <div class="user-list-view">
    <!--搜索-->
    <el-row style="margin-top: 10px; margin-bottom: 10px">
      <el-col :span="3">
        <el-input
          size="small"
          v-model="searchParam"
          placeholder="编码/名称/类别"
        ></el-input>
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
      <el-table-column label="编码" width="130px" align="center">
        <template slot-scope="scope">
          {{ scope.row.fID }}
        </template>
      </el-table-column>
      <el-table-column label="图片" align="center">
        <template slot-scope="scope">
          <el-image class="my-border" :src="scope.row.fImg"></el-image>
        </template>
      </el-table-column>
      <el-table-column label="类别" align="center">
        <template slot-scope="scope">
          {{ scope.row.fType }}
        </template>
      </el-table-column>
      <el-table-column label="名称" align="center">
        <template slot-scope="scope">
          {{ scope.row.fName }}
        </template>
      </el-table-column>
      <el-table-column label="可食部" align="center">
        <template slot-scope="scope">
          {{ scope.row.fEdibleSection | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="能量" align="center">
        <template slot-scope="scope"> {{ scope.row.fEnergy }} </template>
      </el-table-column>
      <el-table-column label="碳水" align="center">
        <template slot-scope="scope">
          {{ scope.row.fCarbonWater }}
        </template>
      </el-table-column>
      <el-table-column label="脂肪" align="center">
        <template slot-scope="scope">
          {{ scope.row.fFat | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="蛋白质" align="center">
        <template slot-scope="scope">
          {{ scope.row.fProtein | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="膳食纤维" align="center">
        <template slot-scope="scope">
          {{ scope.row.fDietaryFiber | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="维A" align="center">
        <template slot-scope="scope">
          {{ scope.row.fVitaminA | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="维B1" align="center">
        <template slot-scope="scope">
          {{ scope.row.fVitaminB1 | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="维B2" align="center">
        <template slot-scope="scope">
          {{ scope.row.fVitaminB2 | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="维B3" align="center">
        <template slot-scope="scope">
          {{ scope.row.fVitaminB3 | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="维B6" align="center">
        <template slot-scope="scope">
          {{ scope.row.fVitaminB6 | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="维B9" align="center">
        <template slot-scope="scope">
          {{ scope.row.fVitaminB9 | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="维B12" align="center">
        <template slot-scope="scope">
          {{ scope.row.fVitaminB12 | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="维C" align="center">
        <template slot-scope="scope">
          {{ scope.row.fVitaminC | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="维D" align="center">
        <template slot-scope="scope">
          {{ scope.row.fVitaminD | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="维E" align="center">
        <template slot-scope="scope">
          {{ scope.row.fVitaminE | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="维K" align="center">
        <template slot-scope="scope">
          {{ scope.row.fVitaminK | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="钾" align="center">
        <template slot-scope="scope">
          {{ scope.row.fK | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="钠" align="center">
        <template slot-scope="scope">
          {{ scope.row.fNa | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="钙" align="center">
        <template slot-scope="scope">
          {{ scope.row.fCa | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="锌" align="center">
        <template slot-scope="scope">
          {{ scope.row.fZn | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="硒" align="center">
        <template slot-scope="scope">
          {{ scope.row.fSe | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="铁" align="center">
        <template slot-scope="scope">
          {{ scope.row.fFe | tableTile }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="success"
            @click="
              $router.push('/home/nutrition/EditFoodNutrition/' + scope.row.fID)
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
  _delFoodNutritionById,
  _getFoodNutritionByFactor,
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
      searchParam: "", //搜索条件
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
      _getFoodNutritionByFactor(this.searchParam).then((res) => {
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
          _delFoodNutritionById(row.fID).then((res) => {
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
      if (this.searchParam == "") {
        this.init();
      } else {
        _getFoodNutritionByFactor(this.searchParam).then((res) => {
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
            this.$message.error("暂无数据");
          }
        });
      }
    },
    handleReset() {
      this.searchParam = "";
    },
    handleAddBtn() {
      this.$router.push({
        path: "/home/nutrition/AddFoodNutrition" || "/",
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
  