<template>
  <div v-if="isMounted" class="pieChart" ref="pieChart"></div>
  <div v-else class="loading-placeholder">图表加载中...</div>
</template>

<script setup lang="ts">
import { _getConsumptionCountYInfo } from "@/api/api.js";
import { inject, reactive, onMounted, ref, markRaw, watch, watchEffect, onUnmounted, nextTick } from 'vue';
// import * as echarts from "echarts";
import type { ECharts } from 'echarts'
const $echarts_l = inject<(el: HTMLElement) => ECharts>('$echarts_l')!

const pieChart = ref(null);
const isMounted = ref(false);

const props = defineProps(['expenseData']);

const data = reactive({
  myChart: null,
  chartData: [],
  title: [],
  titleNmae: "",
  titleNmae1: "",
});


watch(
  () => data.chartData,
  () => {
    setChart();
  }
);
watch(
  () => ({
    isExpenses: props.expenseData.isExpenses,
    selectYear: props.expenseData.selectYear
  }),
  () => {
    if (isMounted.value) {
      initData()
      nextTick(setChart) // 确保 DOM 更新后执行
    }
  },
  { deep: true }
);
function initData() {
  try {
    const { isExpenses, selectYear, selectMonth, selectDay } = props.expenseData;
    const time = `${selectYear}-${selectMonth + 1}-${selectDay}`;

    // 清空数据时保留响应性
    data.chartData.splice(0, data.chartData.length)
    data.title.splice(0, data.title.length)

    data.titleNmae = `【${selectYear}年${isExpenses === '0' ? '支出' : '收入'}结构】`;
    data.titleNmae1 = isExpenses === '0' ? '支出占比' : '收入占比';

    // 确保组件仍处于挂载状态
    if (!isMounted.value) return

    _getConsumptionCountYInfo(isExpenses, time).then((res) => {
      if (res.data.length > 0) {

        data.chartData = res.data.map(item => ({
          value: item.consumptionCost,
          name: item.consumptionType
        }));

        data.title = res.data.map(item => ({
          name: item.consumptionType,
          icon: "circle"
        }));
      }
    });
  } catch (e) {
    console.error('数据加载失败:', e)
  }
}
function setChart() {
  // 确保容器存在且已挂载
  if (!pieChart.value || !isMounted.value) return
  // 销毁旧实例（增加容错判断）
  if (data.myChart && typeof data.myChart.dispose === 'function') {
    data.myChart.dispose()
    data.myChart = null
  }
  // 创建新实例前重置容器
  pieChart.value.innerHTML = ''
  data.myChart = markRaw($echarts_l(pieChart.value))

  let option = {
    title: [
      {
        text: data.titleNmae,
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
        name: data.titleNmae1,
        type: "pie",
        radius: "65%",
        center: ["50%", "50%"],
        data: data.chartData,
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
            shadowColor: "rgba(0, 0, 0, 0.1)",
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

  if (!data.myChart) data.myChart = markRaw($echarts_l(pieChart.value));


  data.myChart.clear();
  data.myChart.resize();
  // 添加渲染容错
  try {
    data.myChart.setOption(option)
  } catch (e) {
    console.error('图表渲染失败:', e)
  }
}
onMounted(() => {
  isMounted.value = true
  initData();
  setTimeout(() => {
    setChart();
  }, 500);
});
onUnmounted(() => {
  isMounted.value = false
})
</script>

<style lang="less" scoped>
.loading-placeholder {
  height: 100%;
  display: grid;
  place-items: center;
  color: #888;
}

.pieChart {
  height: 100%;
  width: 100%;
  padding: 0 20px;
}
</style>
