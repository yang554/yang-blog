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
      search: "year",
      consumptionType: "支出",
      getCount: 6,
      x_data: [],
      y_data: [],
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
          });
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
            name: "(年)",
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
              fontSize: 12,
              interval: 0,
            },
            data: this.x_data,
          },
        ],
        yAxis: [
          {
            type: "value",
            scale: true,
            min: 0,
            interval: 5000,
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
              fontSize: 12,
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
            barWidth: 20,
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
  