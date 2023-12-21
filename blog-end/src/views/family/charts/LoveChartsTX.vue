<template>
  <!-- <div :class="className" :style="{ height: height, width: width }" /> -->
  <div class="echart" id="mychart" style="float: left;"></div>
</template>
  
<script>
require('echarts/theme/macarons') // echarts theme
import 'echarts-wordcloud';
import { debounce } from '@/utils/formatDate.js'
import { _getUserCountByLove, _getUserCountByLoveAndSex } from '@/api/api.js'

const animationDuration = 6000;

export default {
  name: "MyBarChart",
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    }
  },
  data() {
    return {
      chart: null,
      index_X: [],
      value_n: [],
      value_v: [],
    }
  },
  mounted() {
    this.__resizeHandler = debounce(() => {
      if (this.chart) {
        this.chart.resize()
      }
    }, 100)
    window.addEventListener('resize', this.__resizeHandler)
    this.initEcharts()
  },
  methods: {
    initEcharts() {
      let option = {
        title: {
          text: '家族代系',
          subtext: '男女人数统计',
          left: 'left'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: [
          {
            type: 'category',
            data: this.index_X
          }
        ],
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '男',
            type: 'bar',
            tooltip: {
              valueFormatter: function (value) {
                return value + ' 人';
              }
            },
            data: this.value_n
          },
          {
            name: '女',
            type: 'bar',
            tooltip: {
              valueFormatter: function (value) {
                return value + ' 人';
              }
            },
            data: this.value_v
          },
        ]
      };
      setTimeout(() => {
        const myChart = this.$echarts.init(document.getElementById("mychart"));
        myChart.setOption(option);
      }, 1000)
    },
    initChartData() {
      _getUserCountByLoveAndSex().then(res => {
        if (res.data.status === 200) {
          const data = res.data.obj
          for (let i = 0; i < data.length; i++) {
            if (data[i].name === '男') {
              this.value_n.push(data[i].value)
            } else {
              this.value_v.push(data[i].value)
            }
          }
        }
      })
      _getUserCountByLove().then(res => {
        const data = res.data.obj
        if (res.data.status === 200) {
          for (let i = 0; i < data.length; i++) {

            this.index_X.push('第'+data[i].name+'代')
          }
        }
      })
    },
  },
  created() {
    this.initChartData();
  }
}
</script>
  