<template>
    <div class="page2">
        <el-row class="content">
            <el-col :span="8">
                <div class="list">
                    <div class="left">
                        <span class="title">
                            <span class="title-gradient">每日支出</span>
                        </span>
                        <span class="angle1"></span>
                        <span class="angle2"></span>
                        <span class="angle3"></span>
                        <span class="angle4"></span>
                        <div class="chart-100">
                            <ExpensesDay ref="expensesDay"></ExpensesDay>
                        </div>
                    </div>
                </div>
                <div class="list">
                    <div class="left">
                        <span class="title">
                            <span class="title-gradient">每月支出</span>
                        </span>
                        <span class="angle1"></span>
                        <span class="angle2"></span>
                        <span class="angle3"></span>
                        <span class="angle4"></span>
                        <div class="chart-100">
                            <ExpensesMonth ref="expensesMonth"></ExpensesMonth>
                        </div>
                    </div>
                </div>
            </el-col>
            <el-col :span="8">
                <div class="circlePie" id="circlePie">
                    <canvas id="main" width="550" height="550"></canvas>
                    <canvas id="dot"></canvas>
                </div>
            </el-col>
            <el-col :span="8">
                <div class="list">
                    <div class="right">
                        <span class="title">
                            <span class="title-gradient">每月收入</span>
                        </span>
                        <span class="angle1"></span>
                        <span class="angle2"></span>
                        <span class="angle3"></span>
                        <span class="angle4"></span>
                        <div class="chart-100">
                            <IncomeMonth ref="incomeMonth"></IncomeMonth>
                        </div>
                    </div>
                </div>
                <div class="list">
                    <div class="right">
                        <span class="title">
                            <span class="title-gradient">每年收入</span>
                        </span>
                        <span class="angle1"></span>
                        <span class="angle2"></span>
                        <span class="angle3"></span>
                        <span class="angle4"></span>
                        <div class="chart-100">
                            <IncomeYears ref="incomeYears"></IncomeYears>
                        </div>
                    </div>
                </div>
            </el-col>
        </el-row>
        <el-row class="bottom-list">
            <el-col :span="8" class="col-4">
                <div class="list right-bottom">
                    <div class="bottom bottom1">
                        <span class="title">
                            <span class="title-gradient">每年支出</span>
                        </span>
                        <span class="angle1"></span>
                        <span class="angle2"></span>
                        <span class="angle3"></span>
                        <span class="angle4"></span>
                        <div class="chart-100">
                            <ExpensesYear ref="expensesYear"></ExpensesYear>
                        </div>
                    </div>
                </div>
            </el-col>
            <el-col :span="16" class="col-8">
                <div class="list right-bottom">
                    <div class="bottom bottom1">
                        <span class="title">
                            <span class="title-gradient">收入/支出详情占比</span>
                        </span>
                        <span class="angle1"></span>
                        <span class="angle2"></span>
                        <span class="angle3"></span>
                        <span class="angle4"></span>
                        <div class="chart-10">
                            <div class="queryT">
                                <span class="angle5">收支情况：</span><br />
                                <el-radio v-model="data.expenseData.isExpenses" label="0">支出</el-radio>
                                <el-radio v-model="data.expenseData.isExpenses" label="1">收入</el-radio>
                            </div>
                            <div class="queryT">
                                <span class="angle5">年：</span>
                                <el-select v-model="data.expenseData.selectYear" placeholder="请选择" style="width: 100px;"
                                    size="small">
                                    <el-option v-for="item in data.yearOptions" :key="item.key" :value="item.value"
                                        :label="item.label" />
                                </el-select>
                            </div>
                            <div class="queryT">
                                <span class="angle5">月：</span>
                                <el-select v-model="data.expenseData.selectMonth" placeholder="请选择" style="width: 80px;"
                                    size="small">
                                    <el-option v-for="item in data.monthOptions" :key="item.key" :value="item.value"
                                        :label="item.label" />
                                </el-select>
                            </div>
                            <div class="queryT">
                                <span class="angle5">日：</span>
                                <el-select v-model="data.expenseData.selectDay" placeholder="请选择" style="width: 80px;"
                                    size="small">
                                    <el-option v-for="item in data.dayOptions" :key="item.key" :value="item.value"
                                        :label="item.label" />
                                </el-select>
                            </div>
                        </div>
                        <div class="chart-30">
                            <ExpensesYearF ref="expensesYearF" :expense-data="data.expenseData"></ExpensesYearF>
                        </div>
                        <div class="chart-30">
                            <ExpensesMonthF ref="expensesMonthF" :expenseData="data.expenseData"></ExpensesMonthF>
                        </div>
                        <div class="chart-30">
                            <ExpensesDayF ref="expensesDayF" :expenseData="data.expenseData"></ExpensesDayF>
                        </div>
                    </div>
                </div>
            </el-col>
        </el-row>
    </div>
</template>
  
<script setup lang="ts">
import {
    ref,
    onBeforeUnmount,
    onMounted,
    onUnmounted,
    defineComponent,
    reactive,
    nextTick,
    watch,
    computed
} from "vue";
import ExpensesMonth from "@/components/RetirementPlan/ExpensesMonth.vue";
import IncomeMonth from "@/components/RetirementPlan/IncomeMonth.vue";
import IncomeYears from "@/components/RetirementPlan/IncomeYears.vue";
import ExpensesYear from "@/components/RetirementPlan/ExpensesYear.vue";
import ExpensesDay from "@/components/RetirementPlan/ExpensesDay.vue";
import ExpensesDayF from "@/components/RetirementPlan/ExpensesDayF.vue";
import ExpensesMonthF from "@/components/RetirementPlan/ExpensesMonthF.vue";
import ExpensesYearF from "@/components/RetirementPlan/ExpensesYearF.vue";

import { _getUserDepositById } from "@/api/api.js";
import { getDaysInMonth, handleCrateDate, handleCreateDatePicker, parseTime } from '@/utils/calendar';
const debounce = (fn, delay = 300) => {
    let timer;
    return (...args) => {
        clearTimeout(timer)
        timer = setTimeout(() => fn(...args), delay)
    }
}
const data = reactive({
    expenseData: {
        isExpenses: "0", //收支情况 0支出 1收入
        selectYear: new Date().getFullYear(), // 当前年,  //选中年
        selectMonth: new Date().getMonth(), //选中月
        selectDay: new Date().getDate(),   //选中日
    },
    yearOptions: [],
    monthOptions: [],
    dayOptions: [],
    everyPer: 0,
    xOffset: 0,
    circle: {
        x: 250,
        y: 250,
        radius: 218,
    },
    deposit: 1,
    title: [
        "福寿康宁-",
        "锦绣前程-",
        "金玉满堂-",
        "花好月圆-",
        "心旷神怡-",
        "高山流水-",
    ],
    warea: { x: 250, y: 250, max: 700 },
    dots: [],
    resizeFn: null,
    animationFrame1: null,
    animationFrame2: null,
    centerBox: {
        width: 0,
        height: 0,
    },
});
const debouncedUpdate = debounce(() => {
  handleGetDays(data.expenseData.selectYear, data.expenseData.selectMonth);
}, 300);
watch(
    () => ({
        year: data.expenseData.selectYear,
        month: data.expenseData.selectMonth,
        day: data.expenseData.selectDay
    }),
      debouncedUpdate,
    { deep: true }
);


// 修改 expenseData 时使用
const updateExpenseData = (newValues: Partial<ExpenseData>) => {
    Object.assign(data.expenseData, newValues);
};

function init() {

}
function drawDot() {
    let canvas = document.getElementById("dot");
    canvas.width = document.querySelector("#circlePie").offsetWidth;
    canvas.height = document.querySelector("#circlePie").offsetHeight;
    let ctx = canvas.getContext("2d");
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    // 将鼠标坐标添加进去，产生一个用于比对距离的点数组
    let ndots = [data.warea].concat(data.dots);
    data.dots.forEach((dot) => {
        // 粒子位移
        dot.x += dot.xa;
        dot.y += dot.ya;
        // 遇到边界将加速度反向
        dot.xa *= dot.x > canvas.width || dot.x < 0 ? -1 : 1;
        dot.ya *= dot.y > canvas.height || dot.y < 0 ? -1 : 1;
        // 绘制点
        ctx.fillStyle = "#ffffff";
        ctx.beginPath();
        ctx.arc(dot.x - 0.5, dot.y - 0.5, 2, 0, 2 * Math.PI, true);
        ctx.closePath();
        ctx.fill();
        // 循环比对粒子间的距离
        for (let i = 0; i < ndots.length; i++) {
            let d2 = ndots[i];
            if (dot === d2 || d2.x === null || d2.y === null) continue;
            let xc = dot.x - d2.x;
            let yc = dot.y - d2.y;
            // 两个粒子之间的距离
            let dis = Math.sqrt(xc * xc + yc * yc);
            // 距离比
            let ratio;
            // 如果两个粒子之间的距离小于粒子对象的max值，则在两个粒子间画线
            if (dis < d2.max) {
                // 计算距离比
                ratio = (d2.max - dis) / d2.max;
                // 画线
                ctx.beginPath();
                ctx.lineWidth = ratio / 2;
                if (d2 === data.warea) {
                    ctx.strokeStyle = "rgba(255,255,255,0)";
                } else {
                    // 距离变大 连线颜色变浅
                    ctx.strokeStyle = "rgba(255,255,255," + (ratio + 0.2) + ")";
                }
                ctx.moveTo(dot.x, dot.y);
                ctx.lineTo(d2.x, d2.y);
                ctx.stroke();
            }
        }
        // 将已经计算过的粒子从数组中删除
        ndots.splice(ndots.indexOf(dot), 1);
    });
    data.animationFrame1 = window.requestAnimationFrame(drawDot);
}
function rads(x) {
    // 弧度转换
    return (Math.PI * x) / 180;
}
function act() {
    //清空画布
    const canvas = document.querySelector("#main");
    canvas.style.width = data.centerBox.height + "px";
    canvas.style.height = data.centerBox.height + "px";
    const context = canvas.getContext("2d");
    context.clearRect(0, 0, canvas.width, canvas.height);
    drawPie(data.everyPer, context);
    data.animationFrame2 = window.requestAnimationFrame(act);
    data.everyPer += Math.PI / 180;
    let speed = 0.07; //波浪速度，数越大速度越快
    data.xOffset += speed;
}
function drawPie(everyPer, context) {
    context.save();
    context.fillStyle = "rgba(18,55,88,.2)";
    context.beginPath();
    context.arc(data.circle.x, data.circle.y, 245, 0, 2 * Math.PI, true);
    context.closePath();
    context.fill();
    context.restore();

    //外圆
    context.save();
    context.shadowBlur = 50;
    context.shadowColor = "#123959";
    context.fillStyle = "#080D27";
    context.beginPath();
    context.arc(data.circle.x, data.circle.y, 235, 0, 2 * Math.PI, true);
    context.closePath();
    context.fill();
    context.restore();
    for (let i = 0; i < data.title.length; i++) {
        //绘制文字。
        context.save();
        // 画文字
        drawCircularText(
            data.circle,
            data.title[i],
            rads(i * 60 - 110),
            rads(i * 60 - 65),
            i,
            context
        );
        context.restore();
    }
    // 旋转小球
    let x = 240 * Math.cos(everyPer);
    let y = 240 * Math.sin(everyPer);
    context.save();
    context.fillStyle = "rgb(56,252,253)";
    context.shadowBlur = 80;
    context.shadowColor = "#39E9EE";
    context.translate(data.circle.x, data.circle.y);
    context.beginPath();
    context.arc(x, y, 5, 0, 2 * Math.PI);
    context.arc(-x, -y, 5, 0, 2 * Math.PI);
    context.closePath();
    context.fill();
    context.restore();
    //
    context.save();
    context.fillStyle = "#153776";
    context.beginPath();
    context.arc(data.circle.x, data.circle.y, 200, 0, 2 * Math.PI, true);
    context.closePath();
    context.fill();

    context.fillStyle = "#121535";
    context.beginPath();
    context.arc(data.circle.x, data.circle.y, 190, 0, 2 * Math.PI, true);
    context.closePath();
    context.fill();
    //内圆
    let nowRange = ((540000 / 1000000) * 100).toFixed(3);
    context.save();
    drawCircle(context);
    drawSin(data.xOffset, context, nowRange);
    drawText(context, nowRange);
    context.restore();
    for (let i = 0; i < 6; i++) {
        //绘制刻度。
        context.save();
        context.translate(data.circle.x, data.circle.y);
        context.rotate(-Math.PI / 2 + Math.PI / 6 + (i * Math.PI) / 3); //旋转坐标轴。坐标轴x的正方形从 向上开始算起
        context.beginPath();
        context.moveTo(190, 0);
        context.lineTo(200, 0);
        context.lineWidth = 4;
        context.strokeStyle = "#0A122D";
        context.stroke();
        context.closePath();
        context.restore();
    }
}
function drawCircle(ctx) {
    // 画圆 中心圆
    ctx.beginPath();
    ctx.fillStyle = "#209ADF";
    ctx.arc(data.circle.x, data.circle.y, 120, 0, 2 * Math.PI);
    ctx.fill();
    ctx.beginPath();
    ctx.arc(data.circle.x, data.circle.y, 120, 0, 2 * Math.PI);
    ctx.clip();
}
function drawSin(xOffset, ctx, nowRange) {
    //画sin 曲线函数
    let mW = 240;
    let mH = 240;
    let sX = 0;
    let axisLength = mW; //轴长
    let waveWidth = 0.04; //波浪宽度,数越小越宽
    let waveHeight = 12; //波浪高度,数越大越高
    ctx.save();
    ctx.translate(130, 130);
    let points = []; //用于存放绘制Sin曲线的点
    ctx.beginPath();
    //在整个轴长上取点
    for (let x = sX; x < sX + axisLength; x += 20 / axisLength) {
        //此处坐标(x,y)的取点，依靠公式 “振幅高*sin(x*振幅宽 + 振幅偏移量)”
        let y = -Math.sin((sX + x) * waveWidth + xOffset);
        let dY = mH * (1 - nowRange / 100);
        points.push([x, dY, dY + y * waveHeight]);
        ctx.lineTo(x, dY + y * waveHeight);
    }
    //封闭路径
    ctx.lineTo(axisLength, mH);
    ctx.lineTo(sX, mH);
    ctx.lineTo(points[0][0], points[0][1]);
    ctx.fillStyle = "#2C50B1";
    ctx.fill();

    ctx.restore();
}
// 中心显示文字
function drawText(ctx, nowRange) {
    ctx.save();
    ctx.translate(130, 130);
    let size = 50;
    ctx.font = size + "px Microsoft Yahei";
    ctx.textAlign = "center";
    ctx.fillStyle = "#95EFFF";
    ctx.fillText(nowRange + "%", 120, 120 - size / 2);
    ctx.restore();
    ctx.save();
    size = 25;
    ctx.translate(130, 130);
    ctx.font = size + "px Microsoft Yahei";
    ctx.textAlign = "center";
    ctx.fillStyle = "#95EFFF";
    ctx.fillText("距离百万目标", 120, 120 + size);
    ctx.restore();
}
// 旋转的文字
function drawCircularText(s, string, startAngle, endAngle, n, context) {
    let radius = s.radius, // 文字环绕的中心圆半径
        angleDecrement, // 一个文字所占的角度
        angle = parseFloat(startAngle), // 文字的起始角度
        index = 0, // 文字的索引值
        character; // 当前要画的文字
    let arr = string.split("-");
    context.save();
    context.fillStyle = "#fff";
    context.font = "16px 微软雅黑 ";
    context.textAlign = "center";
    context.textBaseline = "middle";
    if (n < 2 || n === 5) {
        // 上三个不需要反转的文字
        while (index < string.length) {
            character = string.charAt(index);
            if (arr[0].indexOf(character) >= 0) {
                if (arr[0].length > 6) {
                    angleDecrement = (startAngle - endAngle) / (string.length - 3);
                } else {
                    angleDecrement = (startAngle - endAngle) / (string.length - 1);
                }
            } else {
                angleDecrement = (startAngle - endAngle) / (string.length + 6);
            }
            context.save();
            context.beginPath();
            context.translate(
                s.x + Math.cos(angle) * radius,
                s.y + Math.sin(angle) * radius
            );
            context.rotate(Math.PI / 2 + angle);
            context.fillText(character, 0, 0);
            angle -= angleDecrement;
            index++;
            context.restore();
        }
    } else {
        // 下面三个需要反转的文字
        while (index < string.length) {
            character = string.split("").reverse().join("").charAt(index); // 字符串反转
            if (arr[1].indexOf(character) >= 0) {
                angleDecrement = (startAngle - endAngle) / (string.length + 6);
            } else {
                if (arr[0].length > 6) {
                    angleDecrement = (startAngle - endAngle) / (string.length - 3);
                } else {
                    angleDecrement = (startAngle - endAngle) / (string.length - 1);
                }
            }
            context.save();
            context.beginPath();
            context.translate(
                s.x + Math.cos(angle) * radius,
                s.y + Math.sin(angle) * radius
            );
            context.rotate(-Math.PI / 2 + angle); // 旋转文字
            context.fillText(character, 0, 0);
            angle -= angleDecrement;
            index++;
            context.restore();
        }
    }
    context.restore();
}
// function getUserDepositById() {
//     _getUserDepositById(this.$store.state.login_user.id).then((res) => {
//         if (res.status == 200) {
//             this.deposit = res.data.totalDeposit;
//             console.log(this.deposit)
//         } else {
//             this.$message.error(res.statusText);
//         }
//     });
// }
function handleGetDays(year, month) {
    data.dayOptions = []
    let dayCount = getDaysInMonth(year, month)
    if (data.expenseData.selectDay > dayCount) {
        data.expenseData.selectDay = dayCount
    }
    for (let i = 0; i < dayCount; i++) {
        data.dayOptions.push({
            label: `${i + 1}日`,
            value: i + 1
        })
    }
}

onMounted(() => {
    // data.getUserDepositById();
    data.centerBox = {
        width: document.querySelector("#circlePie").offsetWidth,
        height: document.querySelector("#circlePie").offsetHeight,
    };
    for (let i = 0; i < 200; i++) {
        // 随机200个运动的圆点
        let x = Math.random() * data.centerBox.width; // 随机的x偏移量
        let y = Math.random() * data.centerBox.height; // 随机y轴偏移量
        let xa = Math.random() * 2 - 1; // x轴运动速度
        let ya = Math.random() * 2 - 1; // y轴运动速度
        data.dots.push({
            x: x,
            y: y,
            xa: xa,
            ya: ya,
            // 两个圆点之间需要连线的距离
            max: 40,
        });
    }
    act();
    drawDot();
    data.resizeFn = debounce(() => {
        // 通过捕获系统的onresize事件触发我们需要执行的事件
        data.centerBox = {
            width: document.querySelector("#circlePie").offsetWidth,
            height: document.querySelector("#circlePie").offsetHeight,
        };
    }, 500);

    window.addEventListener("resize", data.resizeFn);

    data.monthOptions = handleCreateDatePicker().months;
    data.yearOptions = handleCreateDatePicker().years;
    handleGetDays(data.expenseData.selectYear, data.expenseData.selectMonth)
});

onBeforeUnmount(() => {
    window.removeEventListener("resize", data.resizeFn);
    window.cancelAnimationFrame(data.animationFrame1);
    window.cancelAnimationFrame(data.animationFrame2);
});
</script>
  
<style lang="less" scoped>
@primary-color: #000000;

.title {
    position: absolute;
    display: inline-block;
    color: #6eddf1;
    background: #1e1e2f;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 17px;

    .title-gradient {
        display: inline-block;
        padding: 5px 14px;
        background: radial-gradient(55% 55% ellipse, #2d2d44 60%, #1e1e2f; );
    }
}

.angle1 {
    display: inline-block;
    position: absolute;
    width: 10px;
    height: 10px;
    top: 0;
    left: 0;
    border-top: 1px solid #1c5ab3;
    border-left: 1px solid #1c5ab3;
}

.angle2 {
    display: inline-block;
    position: absolute;
    width: 10px;
    height: 10px;
    top: 0;
    right: 0;
    border-top: 1px solid #1c5ab3;
    border-right: 1px solid #1c5ab3;
}

.angle3 {
    display: inline-block;
    position: absolute;
    width: 10px;
    height: 10px;
    bottom: 0;
    left: 0;
    border-bottom: 1px solid #1c5ab3;
    border-left: 1px solid #1c5ab3;
}

.angle4 {
    display: inline-block;
    position: absolute;
    width: 10px;
    height: 10px;
    bottom: 0;
    right: 0;
    border-bottom: 1px solid #1c5ab3;
    border-right: 1px solid #1c5ab3;
}

.angle5 {
    font-size: 14px;
    color: #61B9C8;
    margin: 10px;
}

.queryT {
    margin-top: 10px;
}

.page2 {
    height: 100%;
    width: 100%;
    padding: 14px 20px 20px;
    background: #1e1e2f;
    overflow: hidden;

    .content {
        height: 65%;

        .ivu-col {
            height: 100%;
        }

        .circlePie {
            height: 100%;
            padding: 0 0 40px;
            text-align: center;
            position: relative;

            canvas {
                position: absolute;
                left: 50%;
                top: 0;
                transform: translate(-50%, 0);
            }

            #dot {
                background: rgba(0, 0, 0, 0);
            }
        }

        .list {
            height: 50%;

            .left,
            .right {
                background: #2d2d44;
            }

            .left,
            .right,
            .center {
                width: 100%;
                height: 90%;
                border: 1px solid #696969;
                position: relative;

                #left1,
                #left2,
                #left3,
                #right1,
                #right2,
                #right3,
                #center2 {
                    height: 100%;
                }

                .chart-68 {
                    width: 68%;
                    height: 100%;
                    float: left;
                }

                .chart-100 {
                    width: 100%;
                    height: 100%;
                    float: left;
                }

                .chart-32 {
                    width: 32%;
                    height: 100%;
                    float: left;
                }
            }
        }
    }

    .bottom-list {
        height: 35%;

        .ivu-col {
            height: 100%;
        }

        .list {
            height: 100%;
            width: 33.3333%;
            padding-right: 1.5%;
            float: left;

            .bottom {
                width: 100%;
                height: 100%;
                border: 1px solid #696969;
                position: relative;
            }
        }

        .right-bottom {
            width: 100%;
            padding-right: 0;
            background: #2d2d44;

            .bottom1 {
                width: 100%;
            }
        }

        .chart-68 {
            width: 68%;
            height: 100%;
            float: left;
        }

        .chart-100 {
            width: 100%;
            height: 100%;
            float: left;
        }

        .chart-32 {
            width: 32%;
            height: 100%;
            float: left;
        }

        .chart-30 {
            width: 28%;
            height: 100%;
            float: left;
        }

        .chart-10 {
            width: 15%;
            height: 100%;
            float: left;
        }
    }
}
</style>
  