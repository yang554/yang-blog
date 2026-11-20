<template>
  <div class="barChart" ref="barChart"></div>
</template>
  
<script setup lang="ts">
import { _getConsumptionCount } from "@/api/api.js";
import { inject, reactive, onMounted, ref, markRaw } from 'vue';
// import * as echarts from "echarts";
import type { ECharts } from 'echarts'
const $echarts_l = inject < (el: HTMLElement) => ECharts > ('$echarts_l')!

const barChart = ref(null);

const data = reactive({
  myChart: null,
  search: "year",
  consumptionType: "支出",
  getCount: 6,
  x_data: [],
  y_data: [],
});
function initData() {
  data.x_data = [];
  data.x_data_1 = [];
  data.y_data = [];
  _getConsumptionCount(
    data.search,
    data.consumptionType,
    data.getCount
  ).then((res) => {
    if (res.data.length > 0) {
      res.data.forEach((item) => {
        data.x_data_1.push(item.consumptionTime.substr(-2).toString().replace(/^0+/, ''));
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
        name: "(年)",
        nameGap: -5,
        nameTextStyle: {
          color: "#61B9C8",
          fontSize: 12,
          align: "button",
          padding: [0, 0, 0, 14],
        },
        type: "category",
        boundaryGap: false,
        axisLine: {
          symbol: ["none", "arrow"],
          symbolSize: [6, 6],
          symbolOffset: [0, 10],
          lineStyle: {
            color: "#122C49",
          },
        },
        axisTick: {
          show: false,
        },
        axisLabel: {
          color: "#61B9C8",
          fontSize: 12,
          interval: 0,
        },
        data: data.x_data,
      },
    ],
    yAxis: [
      {
        name: "(金额)",
        nameGap: -5,
        nameTextStyle: {
          color: "#61B9C8",
          fontSize: 12,
          align: "right",
          padding: [0, 4, 0, 0],
        },
        type: "value",
        scale: true,
        min: 0,
        interval: 500,
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
        barWidth: 20,
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
  