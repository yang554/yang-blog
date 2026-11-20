<template>
  <div class="user-list-view">
    <!-- 搜索区域 - 优化布局适配 -->
    <div class="search-container">
      <el-row :gutter="20">
        <!-- 账簿人搜索 -->
        <el-col :xs="24" :sm="12" :md="8" :lg="4" v-show="isSerachTxt">
          <div class="search-item">
            <span class="search-label">账簿人：</span>
            <el-input placeholder="账户/身份证号" v-model="searchName" size="small" clearable></el-input>
          </div>
        </el-col>

        <!-- 收支情况 -->
        <el-col :xs="24" :sm="12" :md="8" :lg="3">
          <div class="search-item">
            <span class="search-label">收支情况：</span>
            <el-select v-model="searchType" placeholder="全部" size="small" clearable>
              <el-option label="收入" value="收入"></el-option>
              <el-option label="支出" value="支出"></el-option>
            </el-select>
          </div>
        </el-col>

        <!-- 流水类型 -->
        <el-col :xs="24" :sm="12" :md="8" :lg="3">
          <div class="search-item">
            <span class="search-label">流水类型：</span>
            <el-select v-model="searchTypeTag" placeholder="全部" size="small" clearable>
              <el-option v-for="(item, index) in tagList" :key="index" :label="item.name" :value="item.name"></el-option>
            </el-select>
          </div>
        </el-col>

        <!-- 账单日期 -->
        <el-col :xs="24" :sm="24" :md="16" :lg="5">
          <div class="search-item">
            <span class="search-label">账单日期：</span>
            <el-date-picker v-model="searchDate" type="daterange" range-separator="~" start-placeholder="开始日期"
              end-placeholder="结束日期" size="small" :picker-options="pickerOptions" @change="dateChange"
              style="width: 100%"></el-date-picker>
          </div>
        </el-col>

        <!-- 金额范围 -->
        <el-col :xs="24" :sm="24" :md="16" :lg="6">
          <div class="search-item">
            <span class="search-label">金额范围：</span>
            <div class="amount-range">
              <el-input v-model="queryCost.minCost" placeholder="最小值" size="small"
                @input="handleInput('min', queryCost.minCost)" clearable>
                <template slot="prepend">¥</template>
              </el-input>
              <span class="range-separator">至</span>
              <el-input v-model="queryCost.maxCost" placeholder="最大值" size="small"
                @input="handleInput('max', queryCost.maxCost)" clearable>
                <template slot="prepend">¥</template>
              </el-input>
            </div>
          </div>
        </el-col>

        <!-- 操作按钮 -->
        <el-col :xs="24" :sm="24" :md="8" :lg="3">
          <el-button type="primary" size="small" icon="el-icon-search" @click="handleSearchEvent">搜索</el-button>
          <el-button size="small" icon="el-icon-refresh" @click="handleReset" style="margin-left: 10px">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 表格区域 -->
    <div class="table-container">
      <el-table v-loading="pagination.loading" :data="pagination.currentTableData" border stripe style="width: 100%;"
        :max-height="screenHeight" :header-cell-style="{
            background: '#f0f7ff',
            'text-align': 'center',
            color: '#333',
            fontWeight: 'bold'
          }">
        <el-table-column label="名目" align="center" prop="purpose">
          <template slot-scope="scope">
            <span class="purpose-text">{{ scope.row.purpose }}</span>
          </template>
        </el-table-column>
        <el-table-column label="收支类型" align="center" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === '收入' ? 'success' : 'danger'" size="small" effect="plain">
              {{ scope.row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="流水类型" align="center">
          <template slot-scope="scope">
            <el-tag type="info" size="small">
              {{ getTagName(scope.row.typeTagID) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="金额(￥)" align="center" width="120">
          <template slot-scope="scope">
            <span :class="scope.row.type === '收入' ? 'income-color' : 'expense-color'">
              {{ scope.row.type === '收入' ? '+' : '-' }}¥{{ scope.row.cost }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="账单日期" align="center">
          <template slot-scope="scope">
            <span class="date-text">{{ scope.row.note }}</span>
          </template>
        </el-table-column>
        <el-table-column label="制单人" align="center" width="120" prop="uName">
          <template slot-scope="scope">
            <span class="user-name">{{ scope.row.uName }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页优化 -->
    <div class="pagination-container">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
        :current-page="pagination.currentPage" :page-sizes="pagination.pageSizes" :page-size="pagination.pageSize"
        layout="total, sizes, prev, pager, next, jumper" :total="pagination.total" background></el-pagination>
    </div>
  </div>
</template>

<script>
import { _getDailyFlowInfoAll, _findByFactorInfo, _getConsumptionTypeAll, _getUserByName } from "@/api/api.js";
import { getTime, parseDate } from "../../utils/formatDate";

export default {
  name: "DailyFlowView",
  data() {
    return {
      isSerachTxt: false,
      screenHeight: window.innerHeight - 320,
      tableData: [],
      pagination: {
        listData: null,
        currentPage: 1,
        pageSizes: [10, 20, 30, 50],
        pageSize: 20,
        totalPage: 1,
        currentTableData: [],
        total: 0,
        loading: true,
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
        shortcuts: [{
          text: '今天',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一周',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: '最近一月',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setMonth(start.getMonth() - 1);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
      searchType: "",
      searchTypeTag: "",
      searchDate: "",
      searchDate1: "",
      searchDate2: "",
      searchName: "",
      searchNameID: this.$store.state.login_user.id,
      tagList: [],
      queryCost: {
        minCost: null,
        maxCost: null
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
  mounted() {
    this.updateScreenHeight();
    window.addEventListener("resize", this.updateScreenHeight);
  },
  methods: {
    updateScreenHeight() {
      this.screenHeight = window.innerHeight - 320;
    },

    // 获取标签名称
    getTagName(id) {
      const tag = this.tagList.find(item => item.id === id);
      return tag ? tag.name : id;
    },

    validateQueryCost() {
      if (this.queryCost.minCost && this.queryCost.maxCost &&
        parseFloat(this.queryCost.minCost) > parseFloat(this.queryCost.maxCost)) {
        this.$message.warning('金额范围的最大值不能小于最小值！');
      }
    },

    handleInput(type, value) {
      // 优化金额输入处理
      value = value.replace(/[^\d.]/g, '')
        .replace(/\.{2,}/g, '.')
        .replace(/^\./g, '')
        .replace(/(\d*\.\d{0,2}).*/, '$1');

      if (type === 'min') {
        this.queryCost.minCost = value;
      } else {
        this.queryCost.maxCost = value;
      }
    },

    init() {
      if (this.$store.state.login_user.roles[0].name === 'root') {
        this.isSerachTxt = true;
        this.searchNameID = '';
      } else {
        this.isSerachTxt = false;
        this.searchNameID = this.$store.state.login_user.id;
      }

      this.fetchData();

      _getConsumptionTypeAll().then(res => {
        if (res.data.status === 200) {
          this.tagList = res.data.data || [];
        }
      });
    },

    getCurrentPageData() {
      const { listData, currentPage, pageSize } = this.pagination;
      if (!listData) return;

      const begin = (currentPage - 1) * pageSize;
      const end = currentPage * pageSize;
      this.pagination.currentTableData = listData.slice(begin, end);
    },

    dateChange(val) {
      if (val && val.length === 2) {
        this.searchDate1 = parseDate(val[0]);
        this.searchDate2 = parseDate(val[1]);
      } else {
        this.searchDate1 = "";
        this.searchDate2 = "";
      }
    },

    handleSearchEvent() {
      if (this.searchName) {
        _getUserByName(this.searchName).then((res) => {
          if (res.data.data && res.data.data.length > 0) {
            this.searchNameID = res.data.data[0].id;
          } else {
            this.searchNameID = '';
          }
          this.fetchData();
        });
      } else {
        this.searchNameID = this.$store.state.login_user.id;
        this.fetchData();
      }
    },

    fetchData() {
      const params = {
        searchDate1: this.searchDate1 || '',
        searchDate2: this.searchDate2 || '',
        searchType: this.searchType || '',
        searchTypeTag: this.searchTypeTag,
        id: this.searchNameID || '',
        queryCost: typeof this.queryCost === 'object'
          ? JSON.stringify(this.queryCost)  // 对象转字符串
          : this.queryCost || ''
      };

      // 对参数值进行URL编码
      const encodedParams = Object.keys(params).map(key =>
        `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`
      ).join('&');
      _findByFactorInfo(encodedParams).then((res) => {
        if (res.data) {
          this.pagination.listData = res.data;
          this.pagination.total = res.data.length;
          this.pagination.currentPage = 1;
          this.getCurrentPageData();
          this.pagination.loading = false;
        }
      });
    },

    handleReset() {
      this.searchDate = "";
      this.searchDate1 = "";
      this.searchDate2 = "";
      this.searchType = "";
      this.searchTypeTag = "";
      this.queryCost.minCost = "";
      this.queryCost.maxCost = "";
      this.searchName = "";

      if (this.$store.state.login_user.roles[0].name === 'admin') {
        this.searchNameID = '';
      } else {
        this.searchNameID = this.$store.state.login_user.id;
      }

      this.init();
    },

    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.getCurrentPageData();
    },

    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.getCurrentPageData();
    },
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.updateScreenHeight);
  },
  created() {
    // 根据角色初始化searchNameID
    if (this.$store.state.login_user.roles[0].name === 'admin') {
      this.searchNameID = '';
    } else {
      this.searchNameID = this.$store.state.login_user.id;
    }
    this.init();
  },
};
</script>

<style lang="less" scoped>
@import '@/assets/table-container.css';

.income-color {
  color: #18a058;
  font-weight: 600;
}

.expense-color {
  color: #d03050;
  font-weight: 600;
}

.purpose-text {
  font-weight: 500;
  color: #1d2129;
}

.date-text {
  color: #4e5969;
  font-size: 13px;
}

.user-name {
  color: #34495e;
  font-weight: 500;
}

// 响应式适配
@media (max-width: 768px) {
  .search-container {
    padding: 12px;

    .search-item {
      flex-direction: column;
      align-items: flex-start;
      margin-bottom: 16px;

      .search-label {
        text-align: left;
        margin-bottom: 6px;
        padding-right: 0;
      }

      .el-select,
      .el-input,
      .el-date-editor {
        width: 100%;
      }
    }

    .action-buttons {
      justify-content: center;
      padding-top: 10px;
    }
  }

  .amount-range {
    flex-direction: column;
    gap: 10px !important;

    .range-separator {
      text-align: center;
      width: 100%;
    }
  }
}
</style>