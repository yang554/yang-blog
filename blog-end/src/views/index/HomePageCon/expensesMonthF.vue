<template>
  <div class="pieChart"></div>
</template>

<script>
import { _getConsumptionCountMInfo } from "@/api/api.js";
export default {
  name: "",
  props: {
    expenseData: Object
  },
  data() {
    return {
      myChart: null,
      chartData: [],
      title: [],
      titleNmae: "",
      titleNmae1: "",
    };
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.setChart(val);
      },
    },
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
  methods: {
    initData() {
      let time = this.expenseData.selectYear + '-' + (this.expenseData.selectMonth + 1) + '-' + this.expenseData.selectDay
      this.chartData = [];
      this.title = [];
      if(this.expenseData.isExpenses == '0'){
        this.titleNmae = '【'+this.expenseData.selectYear+'年'+(this.expenseData.selectMonth + 1)+'月支出结构】';
        this.titleNmae1 = '支出占比';
      }else{
        this.titleNmae = '【'+this.expenseData.selectYear+'年'+(this.expenseData.selectMonth + 1)+'月收入结构】';
        this.titleNmae1 = '收入占比';
      }
      _getConsumptionCountMInfo(this.expenseData.isExpenses, time,this.expenseData.queryId).then((res) => {
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
            text: this.titleNmae,
            right: "center",
            bottom: "1%",
            textStyle: {
              color: "#61B9C8",
              fontSize: 14,
            },
          },
        ],
        tooltip: {
          trigger: "item",
          backgroundColor: "#11367a",
          textStyle: {
            color: "#6dd0e3",
            fontSize: 16,
          },
          formatter: "{a} <br/>{b} : {c}元 ({d}%)",
        },
        series: [
          {
            name: this.titleNmae1,
            type: "pie",
            radius: "65%",
            center: ["50%", "50%"],
            data: this.chartData,
            label: {
              fontSize: 12,
              color: "#00CCFF",
            },
            labelLine: {
              length: 10,
              length2: 5,
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
