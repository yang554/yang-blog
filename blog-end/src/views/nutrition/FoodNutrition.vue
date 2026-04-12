<template>
  <div class="user-list-view">
    <!--搜索-->
    <div class="search-container">
      <el-row :gutter="20">
        <el-col :span="4">
          <div class="search-item">
            <span class="search-label">查询：</span>
            <el-input size="small" v-model="searchParam" placeholder="编码/名称/类别"></el-input>
          </div>
        </el-col>
        <el-col :span="5">
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
        <el-col :span="5">
          <el-upload class="upload-demo" action="" :auto-upload="false" :on-change="handleImport" :show-file-list="false">
            <el-button type="warning" size="small">
              导入
            </el-button>
          </el-upload>
        </el-col>
      </el-row>
    </div>


    <div class="table-container">
      <el-table v-loading="pagination.loading" border :data="pagination.currentTableData" style="min-width: 100%"
        :max-height="this.screenHeight" :header-cell-style="{ 'text-align': 'center' }">
        <el-table-column fixed="left" label="编码" width="130" align="center">
          <template slot-scope="scope">
            {{ scope.row.fID }}
          </template>
        </el-table-column>
        <el-table-column fixed="left" label="图片" align="center" width="80">
          <template slot-scope="scope">
            <el-image :src="scope.row.fImg"></el-image>
          </template>
        </el-table-column>
        <el-table-column fixed="left" label="类别" align="center">
          <template slot-scope="scope">
            {{ scope.row.fType }}
          </template>
        </el-table-column>
        <el-table-column fixed="left" label="名称" align="center" width="180">
          <template slot-scope="scope">
            {{ scope.row.fName }}
          </template>
        </el-table-column>
        <el-table-column label="可食部(g)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fEdibleSection | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="能量(kcal)" align="center">
          <template slot-scope="scope"> {{ scope.row.fEnergy }} </template>
        </el-table-column>
        <el-table-column label="碳水(g)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fCarbonWater }}
          </template>
        </el-table-column>
        <el-table-column label="脂肪(g)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fFat | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="蛋白质(g)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fProtein | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="膳食纤维(g)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fDietaryFiber | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="维A(ug)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fVitaminA | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="维B1(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fVitaminB1 | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="维B2(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fVitaminB2 | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="维B3(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fVitaminB3 | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="维B6(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fVitaminB6 | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="维B9(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fVitaminB9 | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="维B12(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fVitaminB12 | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="维C(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fVitaminC | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="维D(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fVitaminD | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="维E(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fVitaminE | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="维K(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fVitaminK | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="钾(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fK | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="钠(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fNa | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="钙(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fCa | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="锌(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fZn | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="硒(ug)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fSe | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="铁(mg)" align="center">
          <template slot-scope="scope">
            {{ scope.row.fFe | tableTile }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="success" @click="$router.push('/home/nutrition/EditFoodNutrition/' + scope.row.fID)
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
import * as XLSX from 'xlsx';
import FileSaver from 'file-saver';
import {
  _selectByName,
  _delFoodNutritionById,
  _getFoodNutritionByFactor,
  _getUserAll,
  _insertBatchFood,
} from "@/api/api.js";
import { getTime, parseTime } from "@/utils/formatDate";
export default {
  name: "BodyComposition",
  data() {
    return {
      screenHeight: 950,
      tableData: [],
      importData: [],
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
      this.screenHeight = window.innerHeight - 250; // 更新屏幕高度
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
    // 处理文件导入
    handleImport(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        try {
          const data = new Uint8Array(e.target.result);
          const workbook = XLSX.read(data, { type: 'array' });

          // 获取第一个工作表
          const firstSheetName = workbook.SheetNames[0];
          const worksheet = workbook.Sheets[firstSheetName];

          // 转换为JSON
          const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });
          // 移除表头
          if (jsonData.length > 0) jsonData.shift();

          // 处理导入的数据并添加到记录列表
          this.importData = jsonData.filter(row => row && row.length >= 3).map(row => {
            // 确保行数据有足够的列
            const food = {
              fID: Number.isInteger(row[0]) ? row[0] : '' + getTime("start"),
              fType: row[1] || '未知类型',
              fName: row[2] || '异常食物',
              fEdibleSection: row[3] || 0,
              fEnergy: row[5] || 0,
              fCarbonWater: row[9] || 0,
              fFat: row[8] || 0,
              fProtein: row[7] || 0,
              fDietaryFiber: row[10] || 0,
              fVitaminA: row[13] || 0,
              fVitaminC: row[19] || 0,
              fVitaminD: '/',
              fVitaminE: row[20] || 0,
              fVitaminK: '/',
              fVitaminB1: row[16] || 0,
              fVitaminB2: row[17] || 0,
              fVitaminB3: row[18] || 0,
              fVitaminB6: '/',
              fVitaminB9: '/',
              fVitaminB12: '/',
              fK: row[26] || 0,
              fNa: row[27] || 0,
              fCa: row[24] || 0,
              fZn: row[30] || 0,
              fSe: row[31] || 0,
              fFe: row[29] || 0,
              fRole: '/',
              fNote: row[34] || '/',
              fCreateTime: parseTime(new Date()),
              fUpdateTime: parseTime(new Date()),
            };
            return food;
          });
          console.log(this.importData)
          _insertBatchFood(this.importData).then((res) => {
            if (res.data.status === 200) {
              this.$message.success(`成功导入 ${this.importData.length} 条记录`);
              this.init();
              this.importData = []
            } else {
              this.$message.error(`成功失败 ${this.importData.length} 条记录`);
            }
          })


        } catch (error) {
          console.error('导入失败:', error);
          this.$message.error('导入失败: ' + error.message);
        }
      };

      reader.onerror = () => {
        this.$message.error('文件读取失败');
      };

      reader.readAsArrayBuffer(file.raw);
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
@import '@/assets/table-container.css';
</style>
  