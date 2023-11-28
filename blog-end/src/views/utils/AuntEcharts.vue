<template>
    <div class="echart" id="mychart" :style="myChartStyle"></div>
  </template>
   
  <script>
   
  export default {
    data() {
      return {
        xData: ['2023/10/12~10/23','2023/10/12~10/23','2023/10/12~10/23','2023/10/12~10/23'], //横坐标
        yData: ['25'], //数据
        myChartStyle: { float: "left", width: "70%", height: "550px" } //图表样式
      };
    },
    mounted() {
      this.initEcharts();
    },
    methods: {
      initEcharts() {
        // 基本柱状图
        const option = {
          xAxis: {
            data: this.xData
          },
          yAxis: {},
          series: [
            {
              type: "bar", //形状为柱状图
              data: this.yData
            }
          ]
        };
        
        const myChart = this.$echarts.init(document.getElementById("mychart"));
        myChart.setOption(option);
        //随着屏幕大小调节图表
        window.addEventListener("resize", () => {
          myChart.resize();
        });
      }
    },
    created() {
        // this.init();
    }
  };
  </script>