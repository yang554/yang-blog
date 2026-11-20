<template>
  <div class="barChart" ref="barChart"></div>
</template>
  
<script setup lang="ts">
import { _getConsumptionCount } from "@/api/api.js";
import { inject, reactive, onMounted, ref, markRaw } from 'vue';
import type { ECharts } from 'echarts'
const $echarts_l = inject<(el: HTMLElement) => ECharts>('$echarts_l')!

const barChart = ref(null);

const data = reactive({
  myChart: null,
  search: "month",
  consumptionType: "支出",
  getCount: 12,
  x_data: [],
  y_data: [],
});

function initData() {
  data.x_data = [];
  data.y_data = [];
  _getConsumptionCount(
    data.search,
    data.consumptionType,
    data.getCount
  ).then((res) => {
    if (res.data.length > 0) {
      res.data.forEach((item) => {
        data.x_data.push(item.consumptionTime);
        data.y_data.push(item.consumptionCost);
      });
    } else {
      // data.$message.error(res.data.msg);
    }
  });
}
function setChart() {
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
        data: data.x_data,
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
        name: "消费金额",
        type: "bar",
        itemStyle: {
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: "#50A2F6", // 0% 处的颜色
              },
              {
                offset: 0.4,
                color: "#9490F9", // 40% 处的颜色
              },
              {
                offset: 1,
                color: "#DF7DFD", // 100% 处的颜色
              },
            ],
            global: false, // 缺省为 false
          }, //背景渐变色
        },
        barWidth: 15,
        barCategoryGap: 10,
        data: data.y_data,
      },
    ],
  };
  if (!data.myChart) data.myChart = markRaw($echarts_l(barChart.value));

  data.myChart.clear();
  data.myChart.resize();
  data.myChart.setOption(option);
}

onMounted(() => {
  initData();
  setTimeout(() => {
    setChart();
  }, 500);
});
</script>
  
<style lang="less" scoped>
.barChart {
  width: 100%;
  height: 100%;
}
</style>
  