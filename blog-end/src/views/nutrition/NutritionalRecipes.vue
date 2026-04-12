<template>
  <div class="user-list-view">
    <!--搜索-->
    <div class="search-container">
      <el-row :gutter="20">
        <el-col :span="5">
          <div class="search-item">
            <span class="search-label">食谱名称：</span>
            <el-input size="small" style="width: 200px" v-model="searchName" placeholder="食谱名称"></el-input>
          </div>
        </el-col>
        <el-col :span="5">
          <div class="search-item">
            <span class="search-label">营养结构：</span>
            <el-input size="small" style="width: 200px" v-model="searchType" placeholder="营养结构"></el-input>
          </div>
        </el-col>
        <el-col :span="5">
          <div class="search-item">
            <span class="search-label">目标人群/人：</span>
            <el-input size="small" style="width: 200px" v-model="searchTarter" placeholder="目标人群/人"></el-input>
          </div>
        </el-col>
        <el-col :span="6">
          <el-button size="small" type="primary" @click="handleSearchEvent">
            <i class="el-icon-search"></i>
            搜索
          </el-button>
          <el-button size="small" type="info" @click="handleReset">
            <i class="el-icon-refresh"></i>
            重置
          </el-button>
          <el-button size="small" type="success" @click="handleAddBtn">
            <i class="el-icon-plus"></i>
            添加
          </el-button>
        </el-col>
      </el-row>
    </div>

    <div class="table-container">
      <el-table v-loading="pagination.loading" border :data="pagination.currentTableData" style="width: 100%"
        :max-height="this.screenHeight" :header-cell-style="{ 'text-align': 'center' }">
        <el-table-column label="编码" width="130px" align="center">
          <template slot-scope="scope">
            {{ scope.row.fID }}
          </template>
        </el-table-column>
        <el-table-column label="食谱名称" align="center">
          <template slot-scope="scope">
            {{ scope.row.fName }}
          </template>
        </el-table-column>
        <el-table-column label="食谱说明" align="center">
          <template slot-scope="scope">
            {{ scope.row.fIntro | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="营养结构" align="center">
          <template slot-scope="scope">
            {{ scope.row.fType }}
          </template>
        </el-table-column>
        <el-table-column label="早餐" align="center">
          <template slot-scope="scope"> {{ scope.row.zMeal }} </template>
        </el-table-column>
        <el-table-column label="加餐" align="center">
          <template slot-scope="scope">
            {{ scope.row.zSnack }}
          </template>
        </el-table-column>
        <el-table-column label="午餐" align="center">
          <template slot-scope="scope">
            {{ scope.row.aMeal | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="加餐" align="center">
          <template slot-scope="scope">
            {{ scope.row.aSnack | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="晚餐" align="center">
          <template slot-scope="scope">
            {{ scope.row.nMeal | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="加餐" align="center">
          <template slot-scope="scope">
            {{ scope.row.nSnack | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="目标人群" align="center">
          <template slot-scope="scope">
            {{ scope.row.fTarget | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="创建人" align="center">
          <template slot-scope="scope">
            {{ scope.row.uName | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center">
          <template slot-scope="scope">
            {{ scope.row.fNote | tableTile }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="success" @click="$router.push(
              '/home/nutrition/EditNutritionRecipes/' + scope.row.fID
            )
              ">查看\编辑
            </el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page="pagination.currentPage" :page-sizes="pagination.pageSizes" :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="pagination.total">
      </el-pagination>
    </div>
  </div>
</template>
    
<script>
import {
  _getNutritionalRecipesAll,
  _getNutritionalRecipesByFactor,
  _delNutritionalRecipes,
} from "@/api/api.js";
export default {
  name: "NutritionalRecipes",
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
      searchName: "", //搜索菜名
      searchType: "", //搜索菜系
      searchTarter: "", //目标人群
      uID: "", //用户ID
    };
  },
  filters: {
    tableTile(value) {
      if (!value) return "";
      if (value.length > 10) {
        return value.slice(0, 10) + "...";
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
      _getNutritionalRecipesByFactor(this.searchName, this.searchType, this.searchTarter).then((res) => {
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
          _delNutritionalRecipes(row.fID).then((res) => {
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
      _getNutritionalRecipesByFactor(this.searchName, this.searchType, this.searchTarter).then((res) => {
        console.log(res.data)
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
    },
    handleReset() {
      this.searchName = "";
      this.searchType = "";
      this.searchTarter = "";
    },
    handleAddBtn() {
      this.$router.push({
        path: "/home/nutrition/AddNutritionRecipes" || "/",
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
@import '@/assets/table-container.css';
</style>
    