<template>
  <div class="user-list-view">
    <!-- 搜索区域 -->
    <div class="search-container">
      <el-row :gutter="20">
        <el-col :span="4" v-show="this.isSerachTxt">
          <div class="search-item">
            <span class="search-label">账簿人：</span>
            <el-input placeholder="根据账户或者身份证号搜索" v-model="searchName" size="small" clearable>
              <i slot="prefix" class="el-input__icon el-icon-user"></i>
            </el-input>
          </div>
        </el-col>
        <el-col :span="3">
          <div class="search-item">
            <span class="search-label">收支情况：</span>
            <el-select v-model="searchType" placeholder="请选择" size="small">
              <el-option label="收入" value="收入"></el-option>
              <el-option label="支出" value="支出"></el-option>
            </el-select>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="search-item">
            <span class="search-label">账单日期：</span>
            <el-date-picker v-model="searchDate" @change="dateChange" type="daterange" range-separator="~"
              start-placeholder="开始日期" end-placeholder="结束日期" :picker-options="pickerOptions" size="small"
              style="width: 100%">
            </el-date-picker>
          </div>
        </el-col>
        <el-col :span="8">
          <div class="search-item">
            <span class="search-label">金额范围：</span>
            <div class="amount-range">
              <el-input placeholder="最小值" v-model="queryCost.minCost" size="small"
                @input="handleInput('min', queryCost.minCost)">
                <template slot="prepend">￥</template>
              </el-input>
              <span class="range-separator">至</span>
              <el-input placeholder="最大值" v-model="queryCost.maxCost" size="small"
                @input="handleInput('max', queryCost.maxCost)">
                <template slot="prepend">￥</template>
              </el-input>
            </div>
          </div>
        </el-col>
        <!-- 操作按钮 -->
        <el-col :span="3" class="action-buttons">
          <el-button size="small" type="primary" @click="handleSearchEvent" icon="el-icon-search">搜索</el-button>
          <el-button size="small" type="success" @click="dialogFormVisible = true" icon="el-icon-plus">添加流水</el-button>
          <el-button size="small" @click="handleReset" icon="el-icon-refresh">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table v-loading="pagination.loading" :data="pagination.currentTableData" style="width: 100%"
        :max-height="this.screenHeight" :header-cell-style="{
            background: '#f0f7ff',
            'text-align': 'center',
            color: '#333',
            fontWeight: 'bold'
          }" stripe>
        <el-table-column label="单号" width="130px" align="center">
          <template slot-scope="scope">
            <div class="cell-content">{{ scope.row.id }}</div>
          </template>
        </el-table-column>
        <el-table-column label="类型" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === '收入' ? 'success' : 'danger'" size="small">
              {{ scope.row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="金额(￥)" align="center">
          <template slot-scope="scope">
            <div :class="['amount-cell', scope.row.type === '收入' ? 'income' : 'expense']">
              {{ scope.row.type === '收入' ? '+' : '-' }}{{ scope.row.cost }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="账单日期" align="center">
          <template slot-scope="scope">
            <div class="cell-content">{{ scope.row.recordTime | tableTile }}</div>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center">
          <template slot-scope="scope">
            <div class="cell-content">{{ scope.row.fCreateTime }}</div>
          </template>
        </el-table-column>
        <el-table-column label="修改时间" align="center">
          <template slot-scope="scope">
            <div class="cell-content">{{ scope.row.fUpdateTime }}</div>
          </template>
        </el-table-column>
        <el-table-column label="制单人" align="center">
          <template slot-scope="scope">
            <div class="cell-content">{{ scope.row.uName }}</div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button size="mini" type="success" @click="$router.push(
              '/home/life/EditDailyFlowInfoView/' + JSON.stringify(scope.row)
            )
              ">查看\编辑</el-button>
            <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
          :current-page="pagination.currentPage" :page-sizes="pagination.pageSizes" :page-size="pagination.pageSize"
          layout="total, sizes, prev, pager, next, jumper" :total="pagination.total">
        </el-pagination>
      </div>
    </div>

    <!-- 添加类型选择对话框 -->
    <el-dialog title="请选择流水类型" :visible.sync="dialogFormVisible" width="300px" center>
      <div class="dialog-content">
        <el-select v-model="dialogFormValue" size="medium" style="width: 100%">
          <el-option label="收入单" value="收入"></el-option>
          <el-option label="支出单" value="支出"></el-option>
        </el-select>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleAddBtn">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { _getDailyFlowAll, _findByFactor, _delFlow, _getUserByName } from "@/api/api.js";
import { getTime, parseTime, parseDate } from "../../utils/formatDate";
export default {
  name: "DailyFlowView",
  data() {
    return {
      isSerachTxt: false,  //搜索框
      dialogFormVisible: false,
      dialogFormValue: "支出",
      screenHeight: 950,
      tableData: [],
      pagination: {
        listData: null, //总data
        currentPage: 1, //当前页
        pageSizes: [5, 10, 15, 20],
        pageSize: 20, //每页显示条数
        totalPage: 1, //总页数
        currentTableData: [], //当前页data
        total: 0, //总数量
        loading: true, //table加载样式是否显示
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
      },
      searchName: "", //账簿人
      searchNameID: this.$store.state.login_user.id,
      searchType: "", //搜索收支情况
      searchDate: "", //搜索账单日期范围
      searchDate1: "",
      searchDate2: "",
      uID: "", //用户ID
      queryCost: {
        minCost: null, //金额筛选,最小值
        maxCost: null //金额筛选，最大值
      },
    };
  },
  watch: {
    'queryCost.minCost'(newVal) {
      this.validateQueryCost();
    },
    'queryCost.maxCost'(newVal) {
      this.validateQueryCost();
    },
  },
  filters: {
    tableTile(value) {
      if (!value) return "";
      if (value.length > 19) {
        return value.slice(0, 19) + "...";
      }
      return value;
    },
    dateFormat(val) {
      if (!val) return "";
      else return parseTime(val);
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
    validateQueryCost() {
      if (this.queryCost.minCost !== null && this.queryCost.maxCost !== null && parseFloat(this.queryCost.minCost) > parseFloat(this.queryCost.maxCost)) {
        this.$message({
          message: '查询金额范围的最大值不能小于最小值！',
          type: 'warning'
        });
      }
    },
    handleInput(type, value) {
      // value = value.replace(/([0-9]\d*\.?\d{0,2})?.*$/, "$1");
      value = value.replace(/(^0+(?=\d))|[^\d.]|(?<=\..{2}).|^\.|\.+/g, (match) => { //deepseek给出表达式
        if (match.startsWith("0") && match !== "0") return ""; // 去除前导零（如00→0）
        if (match === ".") return ".";                        // 允许单个小数点
        return "";                                            // 过滤非法字符
      }).replace(/(\d*\.?\d{0,2}).*/, "$1")                   // 截断小数到两位
        .replace(/^\./, "0.");                                 // 处理以小数点开头的情况（如.12→0.12）
      if (type === 'min') {
        this.queryCost.minCost = value; // 确保响应式更新
      } else {
        this.queryCost.maxCost = value;
      }
    },
    //初始化
    init() {
      if (this.$store.state.login_user.roles[0].name === 'root') {
        this.isSerachTxt = true
        this.uID = '';
      } else {
        this.isSerachTxt = false
        this.uID = this.$store.state.login_user.id;
      }
      _getDailyFlowAll(this.uID).then((res) => {
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
          } else {

          }
          this.pagination.loading = false;
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
          _delFlow(row.id).then((res) => {
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
    dateChange(val) {
      this.searchDate1 = parseDate(new Date(Date.parse(val[0])));
      this.searchDate2 = parseDate(new Date(Date.parse(val[1])));
    },
    /*搜索*/
    handleSearchEvent() {
      _getUserByName(this.searchName).then((res) => {
        if (res.data.data.length > 0 && this.searchName != '') {
          this.searchNameID = res.data.data[0].id
        } else {
          this.searchNameID = ''
        }
        const params = {
          searchDate1: this.searchDate1 || '',
          searchDate2: this.searchDate2 || '',
          searchType: this.searchType || '',
          searchName: this.searchNameID || '',
          queryCost: typeof this.queryCost === 'object'
            ? JSON.stringify(this.queryCost)  // 对象转字符串
            : this.queryCost || ''
        };

        // 对参数值进行URL编码
        const encodedParams = Object.keys(params).map(key =>
          `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`
        ).join('&');
        _findByFactor(encodedParams).then(
          (res) => {
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
          }
        );
      })

    },
    handleReset() {
      this.searchDate = "";
      this.searchDate1 = "";
      this.searchDate1 = "";
      this.searchType = "";
      this.searchName = "";
      this.searchNameID = this.$store.state.login_user.id;
      this.queryCost.minCost = "";
      this.queryCost.maxCost = "";
      this.init();
    },
    handleAddBtn() {
      this.$router.push({
        path: "/home/life/AddDailyFlowInfoView/" + this.dialogFormValue || "/",
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

<style lang="scss" scoped>
@import '@/assets/table-container.css';

.search-card {
  margin-bottom: 15px;
  border-radius: 8px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  .el-card__body {
    padding: 15px 20px;
  }
}

.search-item {
  display: flex;
  align-items: center;
  height: 100%;

  .search-label {
    font-size: 14px;
    color: #606266;
    min-width: 68px;
    text-align: right;
    padding-right: 10px;
    white-space: nowrap;
  }
}

.amount-range {
  display: flex;
  align-items: center;

  .el-input {
    width: 150px;

    &:first-child {
      margin-right: 8px;
    }
  }

  .range-separator {
    padding: 0 8px;
    color: #909399;
  }
}

.action-buttons {
  display: flex;
  align-items: center;
  justify-content: flex-end;

  .el-button {
    margin-left: 10px;
  }
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding: 0 5px;

  .summary-info {
    font-size: 14px;
    color: #606266;

    .highlight {
      color: #409eff;
      font-weight: 600;
    }
  }
}

.data-card {
  border-radius: 8px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);

  .el-card__body {
    padding: 0;
  }
}

.cell-content {
  padding: 8px 0;
  font-size: 13px;
}

.amount-cell {
  font-weight: 600;
  font-size: 14px;

  &.income {
    color: #67c23a;
  }

  &.expense {
    color: #f56c6c;
  }
}

.dialog-content {
  padding: 15px 0;
}

.dialog-footer {
  text-align: center;
}

// 响应式调整
@media (max-width: 1200px) {
  .search-item .search-label {
    min-width: 60px;
  }

  .amount-range {
    flex-direction: column;

    .el-input {
      width: 100%;
      margin-bottom: 8px;

      &:first-child {
        margin-right: 0;
      }
    }

    .range-separator {
      padding: 5px 0;
    }
  }
}

@media (max-width: 992px) {
  .search-card .el-col {
    margin-bottom: 15px;
  }
}
</style>