<template>
  <div class="user-list-view">
    <div class="summary-cards">
      <div class="summary-card">
        <div class="card-icon" style="background: #e6f7ff;">
          <i class="el-icon-toilet-paper" style="color: #1890ff;"></i>
        </div>
        <div class="card-content">
          <div class="card-title">流水号</div>
          <div class="card-value">{{ dailyFlowEntity.id }}</div>
        </div>
      </div>

      <div class="summary-card">
        <div class="card-icon" style="background: #f6ffed;">
          <i class="el-icon-sort" style="color: #52c41a;"></i>
        </div>
        <div class="card-content">
          <div class="card-title">收支类型</div>
          <div class="card-value">{{ dailyFlowEntity.type }}</div>
        </div>
      </div>

      <div class="summary-card">
        <div class="card-icon" style="background: #fff7e6;">
          <i class="el-icon-coin" style="color: #fa8c16;"></i>
        </div>
        <div class="card-content">
          <div class="card-title">总金额</div>
          <div class="card-value">￥{{ dailyFlowEntity.cost }}</div>
        </div>
      </div>

      <div class="summary-card">
        <div class="card-icon" style="background: #f9f0ff;">
          <i class="el-icon-date" style="color: #722ed1;"></i>
        </div>
        <div class="card-content">
          <div class="card-title">账单日期</div>
          <div class="card-value">
            <el-date-picker v-model="dailyFlowEntity.recordTime" type="date" placeholder="选择日期" size="small"  @change="handleDateChange(dailyFlowEntity.recordTime)"
              style="width: 150px;" :picker-options="pickerOptions"></el-date-picker>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="search-bar  table-header">
        <div class="search-item">
          <span class="search-label">操作人：</span>
          <span class="search-value">{{ dailyFlowEntity.uName || '未记录' }}</span>
        </div>

        <div class="search-item">
          <span class="search-label">创建时间：</span>
          <span class="search-value">{{ dailyFlowEntity.fCreateTime || '未记录' }}</span>
        </div>

        <div class="search-item">
          <span class="search-label">更新时间：</span>
          <span class="search-value">{{ dailyFlowEntity.fUpdateTime || '未记录' }}</span>
        </div>
        <div class="card-actions">
          <el-button type="warning" size="small" @click="handleReset">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
          <el-button type="primary" size="small" @click="handleAddBtn">
            <i class="el-icon-plus"></i> 添加行
          </el-button>
          <el-button type="success" size="small" @click="handleSaveBtn">
            <i class="el-icon-set-up"></i> 保存
          </el-button>
          <el-button size="small" type="info" @click="$router.push('/home/life/DailyFlowView')">
            <i class="el-icon-close"></i>
            返回
          </el-button>
        </div>
      </div>

      <el-table :data="pagination.currentTableData" style="width: 100%" border :max-height="screenHeight"
        v-loading="pagination.loading">

        <el-table-column label="编码" width="150" align="center">
          <template slot-scope="scope">
            {{ scope.row.id | tableTile }}
          </template>
        </el-table-column>

        <el-table-column label="费用名目" align="center">
          <template slot-scope="scope">
            <div style="position: relative;">
              <el-input v-model="scope.row.purpose" placeholder="输入费用名称"
                @input="getMatchSource(scope.$index, scope.row)">
              </el-input>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="金额(￥)" align="center" width="150">
          <template slot-scope="scope">
            <el-input v-model="scope.row.cost" type="text" @input="handleInput(scope.$index, scope.row)"
              placeholder="0.00">
            </el-input>
          </template>
        </el-table-column>

        <el-table-column label="收支类型" align="center" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === '收入' ? 'success' : 'danger'">
              {{ scope.row.type }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="费用来源" align="center">
          <template slot-scope="scope">
            <el-select v-model="scope.row.typeTagID" placeholder="选择费用来源" filterable style="width: 100%">
              <el-option v-for="(item, index) in tagList" :key="index" :label="item.name" :value="item.name">
              </el-option>
            </el-select>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" align="center">
          <template slot-scope="scope">
            <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">
              <i class="el-icon-delete"></i>
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
  _operationDailyFlow,
  _getNutritionalRecipeByFactor,
  _delNutritionalRecipe,
  _insertBatch,
  _getByFactor,
  _getConsumptionTypeAll,
  _getMatchSource
} from "@/api/api.js";
import { getTime, parseTime } from "../../utils/formatDate";
export default {
  name: "NutritionalRecipe",
  data() {
    return {
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
        shortcuts: [
          {
            text: "今天",
            onClick(picker) {
              picker.$emit("pick", new Date());
            }
          },
          {
            text: "昨天",
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24);
              picker.$emit("pick", date);
            }
          },
          {
            text: "一周前",
            onClick(picker) {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", date);
            }
          }
        ]
      },
      screenHeight: 950,
      pagination: {
        listData: [], //总data
        currentPage: 1, //当前页
        pageSizes: [5, 10, 15, 20],
        pageSize: 10, //每页显示条数
        totalPage: 1, //总页数
        currentTableData: [], //当前页data
        total: 0, //总数量
        loading: false //table加载样式是否显示
      },
      uID: "", //用户ID
      isOperation: "",
      tagList: [], //标签列表
      dailyFlowInfoType: "", //消费类型
      dailyFlowEntity: {
        id: "",
        type: "",
        cost: "",
        recordTime: "",
        note: "",
        made: "",
        uName: "",
        fCreateTime: "",
        fUpdateTime: "",
        op: ""
      },
      showSuggestions: false,
      filteredSuggestions: [],
      selectedSuggestionIndex: 0,
      currentRowIndex: -1,
      matchedCategories: []
    };
  },
  filters: {
    tableTile(value) {
      if (!value) return "";
      if (value.length > 19) {
        return value.slice(0, 19) + "...";
      }
      return value;
    }
  },
  computed: {
    totalSum: function () {
      return this.pagination.listData.reduce((sum, row) => {
        this.dailyFlowEntity.cost = parseFloat(sum + parseFloat(row.cost)).toFixed(2);
        return sum + parseFloat(row.cost);
      }, 0);
    }
  },
  mounted() {
    this.updateScreenHeight(); // 组件挂载时获取屏幕高度
    window.addEventListener("resize", this.updateScreenHeight); // 监听窗口大小变化
    // 监听Tab键事件
    window.addEventListener("keyup", this.handleKeyUp);
  },
  beforeDestroy() {
    // 移除监听
    window.removeEventListener("keyup", this.handleKeyUp);
    window.removeEventListener("resize", this.updateScreenHeight); // 组件销毁前移除事件监听
  },
  methods: {
    updateScreenHeight() {
      this.screenHeight = window.innerHeight - 300; // 更新屏幕高度
    },
    handleChange() {
      this.totalSum;
      this.dailyFlowEntity.type = this.dailyFlowInfoType
    },
    handleInput(index, row) {
      let value = row.cost;
      value = value.replace(/(^0+(?=\d))|[^\d.]|(?<=\..{2}).|^\.|\.+/g, (match) => { //deepseek给出表达式
        if (match.startsWith("0") && match !== "0") return ""; // 去除前导零（如00→0）
        if (match === ".") return ".";                        // 允许单个小数点
        return "";                                            // 过滤非法字符
      }).replace(/(\d*\.?\d{0,2}).*/, "$1")                   // 截断小数到两位
        .replace(/^\./, "0.");                                 // 处理以小数点开头的情况（如.12→0.12）
      this.$set(row, "cost", value); // 确保响应式更新
      this.handleChange();
    },
    handleDateChange(val) {
      this.dailyFlowEntity.recordTime = parseTime(val);
    },
    //初始化
    init() {
      if (this.dailyFlowEntity.id) {
        this.dailyFlowEntity.op = "upd";
        this.isOperation = true;
        _getByFactor(this.dailyFlowEntity.id).then(res => {
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
      } else {
        this.isOperation = false;
        this.dailyFlowEntity.id = getTime("start");
        this.dailyFlowEntity.made = this.$store.state.login_user.id;
        this.dailyFlowEntity.op = "add";
        this.dailyFlowEntity.recordTime = parseTime(Date.now());
      }
      //获取所有消费类型
      _getConsumptionTypeAll().then(res => {
        if (res.data.status === 200) {
          //每次请求时清空tableData数据
          this.tagList.splice(0);
          this.tagList.push(...res.data.data);
        }
      });
    },
    getMatchSource: _.debounce(function (index, row) {
      _getMatchSource(row.purpose).then(res => {
        if(res.data.data[0]  != undefined){
          row.typeTagID = res.data.data[0]
          row.note = ''
        }else{
          row.note = 'x'
        }
      })
    }, 300)
    ,
    //分页
    getCurrentPageData(listData, currentPage, pageSize) {
      let begin = (currentPage - 1) * pageSize;
      let end = currentPage * pageSize;
      this.pagination.currentTableData = listData.slice(begin, end);
    },
    //表格操作-删除
    handleDelete(index, row) {
      this.pagination.listData = this.pagination.listData.filter(
        item => item.id !== row.id
      );
      this.pagination.total = this.pagination.listData.length;

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
      if (
        this.pagination.currentTableData.length == 0 &&
        this.pagination.listData.length > 0
      ) {
        this.pagination.currentPage = this.pagination.currentPage - 1;
        this.getCurrentPageData(
          this.pagination.listData,
          this.pagination.currentPage,
          this.pagination.pageSize
        );
      }
      this.handleChange();
    },
    handleReset() {
      this.init();
    },
    /*键盘按钮事件*/
    handleKeyUp(event) {
      if (event.key === "Enter") {
        // 新增一行数据
        this.handleAddBtn();
        // event.preventDefault(); // 防止默认行为，即在输入框中移动
      }
    },
    handleAddBtn() {
      if (this.pagination.pageSize == this.pagination.currentTableData.length) {
        this.pagination.currentPage = this.pagination.currentPage + 1;
      }
      const newRow = {
        id: getTime("start"),
        cost: 0,
        type: this.dailyFlowInfoType,
        fID: this.dailyFlowEntity.id,
        typeTagID: '',
        note:''
      };
      this.pagination.listData.push(newRow);

      this.pagination.total = this.pagination.listData.length;
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
    },
    handleSaveBtn() {
      _insertBatch(this.pagination.listData).then(res => {
        if (res.data.status === 200) {
          _operationDailyFlow(this.dailyFlowEntity).then(res => {
            if (res.data.status === 200) {
              this.$notify.success(res.data.msg);
              this.$router.push("/home/life/DailyFlowView");
            }
          });
        }
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
    }
  },
  created() {
    if (this.$route.params.type != undefined) {
      this.dailyFlowInfoType = this.$route.params.type
      this.dailyFlowEntity.type = this.$route.params.type

    } else if (this.$route.params.data != undefined) {
      this.dailyFlowEntity = JSON.parse(this.$route.params.data);
      this.dailyFlowInfoType = this.dailyFlowEntity.type;
    }

    this.init();
  }
};
</script>
    
<style  lang="less" scoped>
.span-txt {
  font-size: 14px;
  color: rgb(221, 221, 221);
}

.span-txt_1 {
  font-size: 14px;
  color: rgb(214, 214, 214);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px 25px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.header-title {
  color: #2c3e50;
  font-size: 22px;
  font-weight: 600;
}

.header-title i {
  color: #5d78ff;
  margin-right: 10px;
}

.summary-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 5px;
}

.summary-card {
  background: white;
  border-radius: 12px;
  padding: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
}

.card-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 22px;
}

.card-content {
  flex: 1;
}

.card-title {
  color: #7d8fa9;
  font-size: 14px;
  margin-bottom: 5px;
}

.card-value {
  color: #2c3e50;
  font-size: 22px;
  font-weight: 600;
}

.card-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.main-content {
  background: white;
  border-radius: 12px;
  padding: 15px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.07);
}

.search-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.search-item {
  display: flex;
  align-items: center;
  min-width: 220px;
}

.search-label {
  color: #5d6d7e;
  font-size: 14px;
  margin-right: 10px;
  white-space: nowrap;
}

.search-value {
  font-weight: 500;
  color: #2c3e50;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.table-title {
  font-size: 18px;
  color: #2c3e50;
  font-weight: 600;
}

.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table th {
  background-color: #f8f9fc !important;
  color: #5d6d7e;
  font-weight: 600;
}

.el-table td {
  padding: 12px 0;
}

.el-input__inner {
  border-radius: 6px !important;
}

.el-tag {
  border-radius: 4px;
}

.pagination-container {
  padding: 20px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #ebeef5;
}

.suggestion-box {
  position: absolute;
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 2000;
  max-height: 200px;
  overflow-y: auto;
  width: 300px;
}

.suggestion-item {
  padding: 10px 15px;
  cursor: pointer;
  transition: background 0.2s;
}

.suggestion-item:hover {
  background: #f5f7fa;
}

.suggestion-item.selected {
  background: #ecf5ff;
  color: #409eff;
}

.match-highlight {
  color: #5d78ff;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}

.el-button {
  border-radius: 6px;
  padding: 10px 20px;
  font-weight: 500;
  transition: all 0.3s;
}

.el-button--primary {
  background: linear-gradient(to right, #5d78ff, #6a5cff);
  border: none;
}

.el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(93, 120, 255, 0.3);
}

.success-message {
  text-align: center;
  padding: 30px;
}

.success-message i {
  font-size: 48px;
  color: #67c23a;
  margin-bottom: 15px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .search-bar {
    flex-direction: column;
  }

  .summary-cards {
    grid-template-columns: 1fr;
  }

  .action-buttons {
    flex-direction: column;
  }

  .action-buttons .el-button {
    width: 100%;
  }
}
</style>
    