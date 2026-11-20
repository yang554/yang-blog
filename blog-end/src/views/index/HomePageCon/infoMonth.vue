<template>
  <div class="pieChart">
    <el-card class="property-card">
      <div slot="header" class="text">
        <span>{{ titleNmae }}</span>
      </div>
      <div v-for="(item,index) in chartData" :key="index" class="text item">
        {{ item.name +'&ensp;&ensp;&ensp;&ensp;&ensp;&ensp;￥'+ item.value }}
      </div>
    </el-card>
    <div v-show="this.isEmpty"><el-empty description="暂无数据"></el-empty></div>
  </div>
</template>
<script>
import { _getConsumptionCountMInfo } from "@/api/api.js";
export default {
  props: {
    expenseData: Object
  },
  watch: {
    'expenseData.isExpenses': {
      handler(val) {
        this.initData();
      },
    },
    'expenseData.selectYear': {
      handler(val) {
        this.initData();
      },
    },
    'expenseData.selectMonth': {
      handler(val) {
        this.initData();
      },
    },
    'expenseData.queryId': {
      handler(val) {
        this.initData();
      },
    },
  },
  data() {
    return {
      chartDataTop10:[],
      chartData: [],
      titleNmae: "",
      isEmpty: false,
    };
  },
  methods: {
    initData() {
      let time = this.expenseData.selectYear + '-' + (this.expenseData.selectMonth + 1) + '-' + this.expenseData.selectDay
      this.chartData = [];
      if(this.expenseData.isExpenses == '0'){
        this.titleNmae =this.expenseData.selectYear+'年'+(this.expenseData.selectMonth + 1)+'月支出排行';
      }else{
        this.titleNmae = this.expenseData.selectYear+'年'+(this.expenseData.selectMonth + 1)+'月收入排行';
      }
      _getConsumptionCountMInfo(this.expenseData.isExpenses, time,this.expenseData.queryId).then((res) => {
        if (res.data.length > 0) {
          res.data.forEach((item) => {
            this.chartData.push({
              value: item.consumptionCost,
              name: item.consumptionType,
            });
          });
          this.isEmpty = false
        } else {
          this.isEmpty = true
        }
      });
      // this.chartDataTop10 = allData.slice(0, 10);
    },
  },
  mounted() {
    this.initData();
  },
}
</script>
<style>
.property-card{
  background-color: rgba(25, 54, 83, 0.2) !important;
  border: none !important;
}

.text {
  padding: 5px 0;
  font-size: 14px;
  color: #6eddf1;
}

.item {
  border-bottom: 1px solid #696969 !important;
}

</style>