<template>
  <div class="pieChart"></div>
</template>

<script>
import { _getConsumptionCountMInfo } from "@/api/api.js";
export default {
  name: "",
  data() {
    return {
      myChart: null,
      chartData: [],
      title: [],
    };
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.setChart(val);
      },
    },
  },
  methods: {
    initData() {
      this.chartData = [];
      this.title = [];
      _getConsumptionCountMInfo("收入").then((res) => {
        if (res.data.length > 0) {
          res.data.forEach((item) => {
            this.title.push({ name: item.consumptionType, icon: "circle" });
            this.chartData.push({
              value: item.consumptionCost,
              name: item.consumptionType,
            });
          });
          // console.log(this.data)
        } else {
          // this.$message.error(res.statusText);
        }
      });
    },
    setChart() {
      let option = {
        title: [
          {
            text: "【收入结构】",
            right: "center",
            bottom: "6%",
            textStyle: {
              color: "#61B9C8",
              fontSize: 12,
            },
          },
        ],
        tooltip: {
          trigger: "item",
          formatter: "{a} <br/>{b} : {c}元 ({d}%)",
        },
        // legend: {
        //   data: this.title,
        //   left: "8%",
        //   top: "10%",
        //   itemWidth: 7,
        //   itemHeight: 7,
        //   textStyle: {
        //     color: "#00CCFF",
        //     fontSize: 10,
        //   },
        // },
        series: [
          {
            name: "收入",
            type: "pie",
            radius: "70%",
            center: ["50%", "50%"],
            data: this.chartData,
            label: {
              fontSize: 8,
              color: "#00CCFF",
            },
            labelLine: {
              length: 10,
              length2: 10,
              lineStyle: {
                color: "#3F3F5C",
              },
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: "rgba(0, 0, 0, 0.5)",
            },
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
    this.initData();
    this.setChart();
  },
};
</script>

<style lang="less" scoped>
.pieChart {
  height: 100%;
  width: 100%;
  padding: 0 20px;
}
</style>
