<template>
  <div class="barChart"></div>
</template>
  
<script>
import { _getConsumptionCount } from "@/api/api.js";
export default {
  name: "",
  props: {
    expenseData: Object
  },
  data() {
    return {
      myChart: null,
      search: "month",
      consumptionType: "支出",
      getCount: 12,
      x_data: [],
      y_data: [],
      expensesMonth_data:[],
    };
  },
  watch: {
    y_data: {
      deep: true,
      handler(val) {
        this.setChart(val);
      },
    },
    'expenseData.queryId': {
      handler(val) {
        this.initData();
      },
    },
  },
  methods: {
    initData() {
      this.x_data = [];
      this.y_data = [];
      this.expensesMonth_data = [];
      _getConsumptionCount(
        this.search,
        this.consumptionType,
        this.getCount,
        this.expenseData.queryId
      ).then((res) => {
        if (res.data.length > 0) {
          res.data.forEach((item) => {
            this.x_data.push(item.consumptionTime);
            this.y_data.push(item.consumptionCost);
            const t = {}
            this.$set(t, 'record_time', item.consumptionTime);
            this.$set(t, 'cost', item.consumptionCost);
            this.$set(t, 'type', '支出');
            this.expensesMonth_data.push(t)
          });
          this.$emit('month_data', this.expensesMonth_data);
        } else {
          // this.$message.error(res.data.msg);
        }
      });
    },
    setChart() {
      let option = {
        tooltip: {
          trigger: "axis",
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效
            type: "shadow", // 默认为直线，可选为：'line' | 'shadow'
          },
          backgroundColor: "#11367a",
          textStyle: {
            color: "#6dd0e3",
            fontSize: 16,
          },
        },
        grid: {
          top: "12%",
          bottom: "12%",
          left: "10%",
          right: "10%",
          containLabel: false,
        },
        xAxis: [
          {
            name: "(月份)",
            nameGap: -5,
            nameTextStyle: {
              color: "#61B9C8",
              fontSize: 12,
              align: "button",
              padding: [0, 0, 0, 14],
            },
            type: "category",
            boundaryGap: true,
            axisLine: {
              symbol: ["none", "arrow"],
              symbolSize: [6, 6],
              symbolOffset: [0, 10],
              lineStyle: {
                color: "#122C49",
              },
            },
            axisTick: { show: false },
            axisLabel: {
              color: "#61B9C8",
              fontSize: 10,
              interval: 0,
              formatter: function (value, index) {
                let date = new Date(value);
                let month = date.getMonth() + 1; // 月份从0开始计算
                let year = date.getFullYear();
                return `${month}月`; // 显示为“月日”格式
              }
            },
            data: this.x_data,
          },
        ],
        yAxis: [
          {
            type: "value",
            scale: true,
            min: 0,
            interval: 1000,
            axisLine: {
              symbol: ["none", "arrow"],
              symbolSize: [6, 6],
              lineStyle: {
                color: "#122C49",
              },
            },
            axisLabel: {
              color: "#61B9C8",
              showMaxLabel: false,
              fontSize: 10,
            },
            splitLine: {
              show: false,
            },
            name: "(金额)",
            nameGap: -5,
            nameTextStyle: {
              color: "#61B9C8",
              fontSize: 12,
              align: "right",
              padding: [0, 4, 0, 0],
            },
          },
        ],
        series: [
          {
            name: "支出金额",
            type: "line",
            color: 'rgba(231, 76, 60, 0.8)',
            barWidth: 15,
            barCategoryGap: 10,
            data: this.y_data,
          },
        ],
      };
      if (!this.myChart) this.myChart = this.$echarts_l(this.$el);

      this.myChart.clear();
      this.myChart.resize();
      this.myChart.setOption(option);
    },
  },
  mounted() {
    // this.setChart();
  },
  created() {
    this.initData();
    setTimeout(() => {
      this.setChart();
    }, 500);
  },
};
</script>
  
<style lang="less" scoped>
.barChart {
  width: 100%;
  height: 100%;
}
</style>
  