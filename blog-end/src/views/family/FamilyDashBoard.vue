<template>
  <div class="dashboard-view">
    <panel-group class="panel-group" />

    <div class="echarts-group">
      <div class="chart-item">
        <SexCharts style="float: left;" />
        <SexChartsDX style="float: right" />
      </div>
      <LoveChartsZX class="chart-item" />
      <LoveChartsTX class="chart-item" />
      <div class="chart-item" />
    </div>
  </div>
</template>
  
<script>
import PanelGroup from "./PanelGroup.vue"
import SexCharts from "./charts/SexCharts.vue"
import SexChartsDX from "./charts/SexChartsDX.vue"
import LoveChartsZX from "./charts/LoveChartsZX.vue"
import LoveChartsTX from "./charts/LoveChartsTX.vue"

import 'echarts-wordcloud';

export default {
  name: "DashBoardView",
  components: {
    PanelGroup,
    SexCharts,
    SexChartsDX,
    LoveChartsZX,
    LoveChartsTX
  },
  data() {
    return {

    }
  },
  methods: {

  },
  mounted() {

  },
  created() {

  }
}

</script>
  
<style lang="less" scoped>
.dashboard-view {
  position: relative;
  width: 95%;
  margin: auto;

  .github-corner {
    position: absolute;
    top: 0;
    border: 0;
    right: 0;
  }

  .panel-group {
    margin-top: 32px;
  }

  .echarts-group {
    width: 100%;
    overflow: hidden;
    margin-top: 20px;
    margin-bottom: 20px;
    display: grid;
    grid-template-columns: 50% 50%;
    grid-row-gap: 30px;

    .chart-item {
      width: auto;
      background-color: #fff;
      min-height: 300px;
      padding: 10px;
    }

    .chart-item:nth-child(even) {
      margin-left: 30px;
    }
  }
}
</style>
  