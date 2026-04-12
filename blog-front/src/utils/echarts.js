import * as echarts from 'echarts'

export const initSvgChart = (el) => {
    // return echarts
  return echarts.init(el, null, { renderer: 'svg' })
}