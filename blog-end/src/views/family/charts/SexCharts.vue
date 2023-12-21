<template>
  <div :class="className" :style="{ height: height, width: width }" />
</template>
  
<script>
require('echarts/theme/macarons') // echarts theme
import { debounce } from '@/utils/formatDate.js'
import { _getUserCountBySex } from '@/api/api.js'


const animationDuration = 6000;

export default {
  name: "SexChartsDX",
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '50%'
    },
    height: {
      type: String,
      default: '300px'
    }
  },
  data() {
    return {
      chart: null,
      chartData: [],
    }
  },
  mounted() {
    this.initChart()
    this.__resizeHandler = debounce(() => {
      if (this.chart) {
        this.chart.resize()
      }
    }, 100)
    window.addEventListener('resize', this.__resizeHandler)
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    window.removeEventListener('resize', this.__resizeHandler)
    this.chart.dispose()
    this.chart = null
  },
  watch: {
    chartData: {
      deep: true,
      handler(val) {
        this.setChartOption(val)
      }
    }
  },
  methods: {
    setChartOption(pieData) {
      let option = {
        title: {
          text: '嫡系男女比例',
          subtext: '数量分布',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '角色占比',
            type: 'pie',
            radius: '65%',
            data: pieData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };

      this.chart.setOption(option);
    },

    initChart() {
      this.chart = this.$echarts.init(this.$el, 'macarons')

      this.setChartOption(this.chartData);
    },

    initChartData() {
      _getUserCountBySex().then(res => {
        if (res.data.status === 200) {
          this.chartData.push(...res.data.obj);
        }
      })
    },
  },
  created() {
    this.initChartData();
  }
}
</script>
  