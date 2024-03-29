<template>
    <div :class="className" :style="{ height: height, width: width }" />
</template>
  
<script>
require('echarts/theme/macarons') // echarts theme
import { debounce } from '@/utils/formatDate.js'
import { _getUserRoleAll } from '@/api/api.js'

const animationDuration = 6000;

export default {
    name: "WordcloudChartTag",
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
        setChartOption(data) {
            let image1 = "data:image/png;base64,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"
            let maskImage = new Image();
            maskImage.src = image1;

            let option = {
                title: {
                    text: '博客类型',
                    subtext: '词云图',
                    left: 'left'
                },
                series: [{
                    type: 'wordCloud',

                    shape: 'diamond ',

                    keepAspect: false,

                    maskImage: maskImage,

                    left: 'center',
                    top: 'center',
                    width: '90%',
                    height: '90%',
                    right: null,
                    bottom: null,

                    // Text size range which the value in data will be mapped to.
                    // Default to have minimum 12px and maximum 60px size.
                    sizeRange: [10, 50],

                    rotationRange: [-90, 90],
                    rotationStep: 45,

                    gridSize: 8,

                    drawOutOfBound: false,

                    layoutAnimation: true,

                    // Global text style
                    textStyle: {
                        fontFamily: 'sans-serif',
                        fontWeight: 'bold',
                        // Color can be a callback function or a color string
                        color: function () {
                            // Random color
                            return 'rgb(' + [
                                Math.round(Math.random() * 160),
                                Math.round(Math.random() * 160),
                                Math.round(Math.random() * 160)
                            ].join(',') + ')';
                        }
                    },
                    emphasis: {
                        focus: 'self',

                        textStyle: {
                            textShadowBlur: 10,
                            textShadowColor: '#333'
                        }
                    },

                    data: data
                }]
            };

            this.chart.setOption(option);
        },

        initChart() {
            this.chart = this.$echarts.init(this.$el, 'macarons')

            this.setChartOption(this.chartData);
        },

        initChartData() {
            //博客type --> 词云图
            _getUserRoleAll().then(res => {
                // console.log(res);
                if (res.data.status === 200) {
                    //去除掉词云图中关键字
                    let keys = [
                        "+",
                        "-",
                        "*",
                        "/",
                        "[",
                        "]",
                        "【",
                        "】",
                        "~",
                        "！",
                        "@",
                        "#",
                        "$",
                        "%",
                        "^",
                        "&",
                        "(",
                        ")",
                        "=",
                        "0",
                        "1",
                        "2",
                        "3",
                        "4",
                        "5",
                        "6",
                        "7",
                        "8",
                        "9",
                        ".",
                        "<",
                        ">",
                        "?",
                        "`",
                        ":",
                        ";",
                        "---",
                        "《",
                        "》",
                        "<",
                        ">",
                    ];
                    res.data.obj.forEach((item, index) => {
                        if ((keys.indexOf(item.name) === -1) && (item.name.length > 1)) {
                            this.chartData.push(item);
                        }
                    });
                }
            });
        },
    },
    created() {
        this.initChartData();
    }
}
</script>
  