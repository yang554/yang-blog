<template>
  <div class="page2">
    <div class="dashboard-header" v-show="this.isSerachTxt">
      <h1 class="dashboard-title"></h1>
      <div class="search-container">
        <el-input placeholder="根据账户或者身份证号搜索" v-model="serachName" size="small"></el-input>
        <el-button type="success" size="small" @click="handleSearchEvent">
          <i class="el-icon-search"></i>
          搜索
        </el-button>
      </div>
    </div>

    <v-row class="content">
      <v-col span="8">
        <!-- 每年支出 -->
        <div class="list">
          <div class="left">
            <span class="title">
              <span class="title-gradient">每年支出</span>
            </span>
            <span class="angle1"></span>
            <span class="angle2"></span>
            <span class="angle3"></span>
            <span class="angle4"></span>
            <div class="chart-100">
              <expensesYear ref="expensesYear" id="expensesYear" :expenseData="this.expenseData"></expensesYear>
            </div>
          </div>
        </div>
        <!-- 每月支出 -->
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
              <expensesMonth ref="expensesMonth" id="expensesMonth"
                :expenseData="this.expenseData"></expensesMonth>
            </div>
          </div>
        </div>
      </v-col>
      <v-col span="8">
        <!-- 目标 -->
        <div class="circlePie" id="circlePie">
          <canvas id="main" width="500" height="500"></canvas>
          <canvas id="dot"></canvas>
        </div>
      </v-col>
      <v-col span="8">
        <!-- 每年收入 -->
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
              <incomeYears ref="incomeYears" id="incomeYears" :expenseData="this.expenseData"></incomeYears>
            </div>
          </div>
        </div>
        <!-- 每月收入 -->
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
              <incomeMonth ref="incomeMonth" :expenseData="this.expenseData">
              </incomeMonth>
            </div>
          </div>
        </div>
      </v-col>
    </v-row>
    <v-row class="bottom-list">
      <!-- 每日支出 -->
      <v-col span="6" class="col-6">
        <div class="list right-bottom">
          <div class="bottom bottom1">
            <span class="title">
              <span class="title-gradient">每日支出</span>
            </span>
            <span class="angle1"></span>
            <span class="angle2"></span>
            <span class="angle3"></span>
            <span class="angle4"></span>
            <div class="chart-100">
              <expensesDay ref="expensesDay" id="expensesDay" :expenseData="this.expenseData"></expensesDay>
            </div>
          </div>
        </div>
      </v-col>
      <!-- 每日收入 -->
      <v-col span="6" class="col-6">
        <div class="list right-bottom">
          <div class="bottom bottom1">
            <span class="title">
              <span class="title-gradient">每日收入</span>
            </span>
            <span class="angle1"></span>
            <span class="angle2"></span>
            <span class="angle3"></span>
            <span class="angle4"></span>
            <div class="chart-100">
              <incomeDay ref="expensesYearF" :expenseData="expenseData"></incomeDay>
            </div>
          </div>
        </div>
      </v-col>
    </v-row>
    <v-row class="bottom-list">
      <v-col span="12" class="col-12">
        <!-- 收入/支出详情占比 -->
        <div class="list right-bottom">
          <div class="bottom bottom1">
            <span class="title">
              <span class="title-gradient">{{ spanText }}详情占比</span>
            </span>
            <span class="angle1"></span>
            <span class="angle2"></span>
            <span class="angle3"></span>
            <span class="angle4"></span>
            <div class="chart-10">
              <div class="queryT">
                <span class="angle5">收支情况：</span><br />
                <el-radio v-model="expenseData.isExpenses" label="0">支出</el-radio>
                <el-radio v-model="expenseData.isExpenses" label="1">收入</el-radio>
              </div>
              <div class="queryT">
                <span class="angle5">年：</span>
                <el-select v-model="expenseData.selectYear" placeholder="请选择" style="width: 100px;" size="small">
                  <el-option v-for="item in yearOptions" :key="item.key" :value="item.value" :label="item.label" />
                </el-select>
              </div>
              <div class="queryT">
                <span class="angle5">月：</span>
                <el-select v-model="expenseData.selectMonth" placeholder="请选择" style="width: 80px;" size="small">
                  <el-option v-for="item in monthOptions" :key="item.key" :value="item.value" :label="item.label" />
                </el-select>
              </div>
              <div class="queryT">
                <span class="angle5">日：</span>
                <el-select v-model="expenseData.selectDay" placeholder="请选择" style="width: 80px;" size="small">
                  <el-option v-for="item in dayOptions" :key="item.key" :value="item.value" :label="item.label" />
                </el-select>
              </div><br />
            </div>
            <div class="chart-30">
              <expensesYearF ref="expensesYearF" :expenseData="expenseData"></expensesYearF>
            </div>
            <div class="chart-30">
              <expensesMonthF ref="expensesMonthF" :expenseData="expenseData"></expensesMonthF>
            </div>
            <div class="chart-30">
              <expensesDayF ref="expensesDayF" :expenseData="expenseData"></expensesDayF>
            </div>
          </div>
        </div>
      </v-col>
    </v-row>
    <v-row class="bottom-list">
      <v-col span="12" class="col-12">
        <!-- 收入/支出排行榜 -->
        <div class="list info-bottom">
          <div class="bottom bottom1">
            <span class="title">
              <span class="title-gradient">{{ spanText }}排行榜</span>
            </span>
            <span class="angle1"></span>
            <span class="angle2"></span>
            <span class="angle3"></span>
            <span class="angle4"></span>
            <div class="chart-33">
              <infoYears :expenseData="expenseData"></infoYears>
            </div>
            <div class="chart-33">
              <infoMonth :expenseData="expenseData"></infoMonth>
            </div>
            <div class="chart-33">
              <infoDay :expenseData="expenseData"></infoDay>
            </div>
          </div>
        </div>
      </v-col>
    </v-row>
  </div>
</template>

<script>
import expensesMonth from "./HomePageCon/expensesMonth.vue";
import incomeDay from "./HomePageCon/incomeDay.vue";
import incomeMonth from "./HomePageCon/incomeMonth.vue";
import incomeYears from "./HomePageCon/incomeYears.vue";
import expensesYear from "./HomePageCon/expensesYear.vue";
import expensesDay from "./HomePageCon/expensesDay.vue";
import expensesDayF from "./HomePageCon/expensesDayF.vue";
import expensesMonthF from "./HomePageCon/expensesMonthF.vue";
import expensesYearF from "./HomePageCon/expensesYearF.vue";
import incomeMonthF from "./HomePageCon/incomeMonthF.vue";
import incomeYearF from "./HomePageCon/incomeYearF.vue";
import infoDay from "./HomePageCon/infoDay.vue";
import infoMonth from "./HomePageCon/infoMonth.vue";
import infoYears from "./HomePageCon/infoYears.vue";

import { _getUserDepositById, _getUserByName } from "@/api/api.js";
import { getDaysInMonth, handleCrateDate, handleCreateDatePicker, parseTime } from '../../utils/calendar'

export default {
  name: "HomePageView",
  components: {
    expensesDay,
    expensesMonth,
    incomeDay,
    incomeMonth,
    incomeYears,
    expensesYear,
    expensesDayF,
    expensesMonthF,
    expensesYearF,
    incomeMonthF,
    incomeYearF,
    infoDay,
    infoMonth,
    infoYears,
  },
  data() {
    return {
      isSerachTxt: false,  //搜索框
      serachName: '',  //搜索成员姓名
      spanText: '支出',  //标题
      expenseData: {
        isExpenses: "0", //收支情况 0支出 1收入
        selectYear: new Date().getFullYear(), // 当前年,  //选中年
        selectMonth: new Date().getMonth(), //选中月
        selectDay: new Date().getDate(),   //选中日
        queryId: this.$store.state.login_user.id, //查询人ID
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
      deposit: -1,
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

      // 新增：家庭收支对比数据
      financialData: {
        year: {
          expenditure: 53158.63,
          income: 46801.25,
          difference: -6357.38
        },
        months: []
      },
      monthData: [],
      comparisonChart: null
    };
  },
  computed: {
    
  },
  watch: {
    'expenseData.selectYear': {
      handler(val) {
        this.handleGetDays(val, this.expenseData.selectMonth)
      }
    },
    'expenseData.selectMonth': {
      handler(val) {
        this.handleGetDays(this.expenseData.selectYear, val)
      }
    },
    'expenseData.selectDay': {
      handler(val) {
        this.handleGetDays(this.expenseData.selectYear, this.expenseData.selectMonth)
      }
    },
    'expenseData.isExpenses': {
      handler(val) {
        this.radioChangeBtn(val)
      }
    },
  },
  methods: {
    radioChangeBtn(val) {
      if (val === '0') {
        this.spanText = '支出'
      } else {
        this.spanText = '收入'
      }
    },
    drawDot() {
      let canvas = document.getElementById("dot");
      canvas.width = document.querySelector("#circlePie").offsetWidth;
      canvas.height = document.querySelector("#circlePie").offsetHeight;
      let ctx = canvas.getContext("2d");
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      // 将鼠标坐标添加进去，产生一个用于比对距离的点数组
      let ndots = [this.warea].concat(this.dots);
      this.dots.forEach((dot) => {
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
            if (d2 === this.warea) {
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
      this.animationFrame1 = window.requestAnimationFrame(this.drawDot);
    },
    rads(x) {
      // 弧度转换
      return (Math.PI * x) / 180;
    },
    act() {
      //清空画布
      const canvas = document.querySelector("#main");
      canvas.style.width = this.centerBox.height + "px";
      canvas.style.height = this.centerBox.height + "px";
      const context = canvas.getContext("2d");
      context.clearRect(0, 0, canvas.width, canvas.height);
      this.drawPie(this.everyPer, context);
      this.animationFrame2 = window.requestAnimationFrame(this.act);
      this.everyPer += Math.PI / 180;
      let speed = 0.07; //波浪速度，数越大速度越快
      this.xOffset += speed;
    },
    drawPie(everyPer, context) {
      context.save();
      context.fillStyle = "rgba(18,55,88,.2)";
      context.beginPath();
      context.arc(this.circle.x, this.circle.y, 245, 0, 2 * Math.PI, true);
      context.closePath();
      context.fill();
      context.restore();

      //外圆
      context.save();
      context.shadowBlur = 50;
      context.shadowColor = "#123959";
      context.fillStyle = "#080D27";
      context.beginPath();
      context.arc(this.circle.x, this.circle.y, 235, 0, 2 * Math.PI, true);
      context.closePath();
      context.fill();
      context.restore();
      for (let i = 0; i < this.title.length; i++) {
        //绘制文字。
        context.save();
        // 画文字
        this.drawCircularText(
          this.circle,
          this.title[i],
          this.rads(i * 60 - 110),
          this.rads(i * 60 - 65),
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
      context.translate(this.circle.x, this.circle.y);
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
      context.arc(this.circle.x, this.circle.y, 200, 0, 2 * Math.PI, true);
      context.closePath();
      context.fill();

      context.fillStyle = "#121535";
      context.beginPath();
      context.arc(this.circle.x, this.circle.y, 190, 0, 2 * Math.PI, true);
      context.closePath();
      context.fill();
      //内圆
      let nowRange = ((this.deposit / 1000000) * 100).toFixed(3);
      context.save();
      this.drawCircle(context);
      this.drawSin(this.xOffset, context, nowRange);
      this.drawText(context, nowRange);
      context.restore();
      for (let i = 0; i < 6; i++) {
        //绘制刻度。
        context.save();
        context.translate(this.circle.x, this.circle.y);
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
    },
    drawCircle(ctx) {
      // 画圆 中心圆
      ctx.beginPath();
      ctx.fillStyle = "#209ADF";
      ctx.arc(this.circle.x, this.circle.y, 120, 0, 2 * Math.PI);
      ctx.fill();
      ctx.beginPath();
      ctx.arc(this.circle.x, this.circle.y, 120, 0, 2 * Math.PI);
      ctx.clip();
    },
    drawSin(xOffset, ctx, nowRange) {
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
        //此处坐标(x,y)的取点，依靠公式 "振幅高*sin(x*振幅宽 + 振幅偏移量)"
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
    },
    // 中心显示文字
    drawText(ctx, nowRange) {
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
    },
    // 旋转的文字
    drawCircularText(s, string, startAngle, endAngle, n, context) {
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
    },
    handleGetDays(year, month) {
      this.dayOptions = []
      let dayCount = getDaysInMonth(year, month)
      if (this.expenseData.selectDay > dayCount) {
        this.expenseData.selectDay = dayCount
      }
      for (let i = 0; i < dayCount; i++) {
        this.dayOptions.push({
          label: `${i + 1}日`,
          value: i + 1
        })
      }
    },
    init() {
      _getUserDepositById(this.$store.state.login_user.id).then((res) => {
        if (res.data) {
          this.deposit = res.data.totalDeposit;
        } else {
          this.$message.error(res.statusText);
        }
      });

      if (this.$store.state.login_user.roles[0].name === 'admin') {
        this.isSerachTxt = true
      } else {
        this.isSerachTxt = false
      }
    },
    /*搜索*/
    handleSearchEvent() {
      if (this.serachName === '')
        this.expenseData.queryId = this.$store.state.login_user.id
      else {
        _getUserByName(this.serachName).then((res) => {
          if (res.data.data.length > 0) {
            this.expenseData.queryId = res.data.data[0].id
          }
        })
      }

    },
  },
  mounted() {
    this.init();
    this.centerBox = {
      width: document.querySelector("#circlePie").offsetWidth,
      height: document.querySelector("#circlePie").offsetHeight,
    };
    for (let i = 0; i < 200; i++) {
      // 随机200个运动的圆点
      let x = Math.random() * this.centerBox.width; // 随机的x偏移量
      let y = Math.random() * this.centerBox.height; // 随机y轴偏移量
      let xa = Math.random() * 2 - 1; // x轴运动速度
      let ya = Math.random() * 2 - 1; // y轴运动速度
      this.dots.push({
        x: x,
        y: y,
        xa: xa,
        ya: ya,
        // 两个圆点之间需要连线的距离
        max: 40,
      });
    }
    this.act();
    this.drawDot();
    this.resizeFn = this.$debounce(() => {
      // 通过捕获系统的onresize事件触发我们需要执行的事件
      this.centerBox = {
        width: document.querySelector("#circlePie").offsetWidth,
        height: document.querySelector("#circlePie").offsetHeight,
      };
      // for (let i = 1; i < 13; i++) {
      //   this.$refs["chart" + i].setChart();
      // }
    }, 500);
    window.addEventListener("resize", this.resizeFn);

    this.monthOptions = handleCreateDatePicker().months;
    this.yearOptions = handleCreateDatePicker().years;
    this.handleGetDays(this.expenseData.selectYear, this.expenseData.selectMonth)
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.resizeFn);
    window.cancelAnimationFrame(this.animationFrame1);
    window.cancelAnimationFrame(this.animationFrame2);
  },
};
</script>

<style lang="less" scoped>
@primary-color: #000000;


.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding: 0 15px;
}

.search-container {
  display: flex;
  align-items: center;
  gap: 15px;
  background: rgba(30, 41, 59, 0.7);
  backdrop-filter: blur(10px);
  padding: 10px 20px;
  border-radius: 30px;
  border: 1px solid var(--card-border);
  box-shadow: var(--glow);
  transition: var(--transition);
}

.search-container:hover {
  box-shadow: 0 0 20px rgba(67, 97, 238, 0.7);
}

.search-container .el-input {
  width: 300px;
}

.search-container .el-input__inner {
  background: rgba(15, 23, 42, 0.6);
  border: 1px solid var(--primary);
  color: var(--text-primary);
  border-radius: 20px;
  padding-left: 15px;
  transition: var(--transition);
}

.search-container .el-input__inner:focus {
  border-color: var(--primary-light);
  box-shadow: 0 0 10px rgba(76, 201, 240, 0.5);
}

.search-container .el-button {
  border-radius: 20px;
  // background: rgba(44, 71, 190, 0.7);//linear-gradient(135deg, var(--primary) 0%, var(--secondary) 100%);
  border: none;
  font-weight: 600;
  transition: var(--transition);
  box-shadow: var(--glow);
}

.search-container .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(67, 97, 238, 0.7);
}

.title {
  position: absolute;
  display: inline-block;
  color: #6eddf1;
  background: rgba(64, 158, 255, 0.2);
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
  margin: 5px;
}

.queryT {
  margin-top: 5px;
}

.page2 {
  // height: 100vh;
  padding: 14px 20px 40px;
  background: rgba(16, 32, 64, 0.6);
  overflow: hidden;

  .content {
    // height: 45%;

    .ivu-col {
      height: 100%;
    }

    .circlePie {
      height: 450px;
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
      height: 48%;

      .left,
      .right {
        background: rgba(70, 156, 241, 0.2);
      }

      .left,
      .right,
      .center {
        width: 100%;
        height: 90%;
        border: 1px solid rgba(64, 158, 255, 0.2);
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

  .info-list {
    height: 100%;

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
        border: 1px solid rgba(64, 158, 255, 0.2);
        position: relative;
      }
    }

  }

  .bottom-list {
    // height: 25%;

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
      height: 100%;
      min-height: 250px;
      width: 100%;
      padding-right: 0;
      background: rgba(64, 158, 255, 0.2);

      .bottom1 {
        width: 100%;
      }
    }

    .info-bottom {
      height: 100%;
      width: 100%;
      padding-right: 0;
      background: rgba(64, 158, 255, 0.2);

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

    .chart-33 {
      width: 33.33%;
      height: 100%;
      float: left;
      border: 1px solid #696969 !important;
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

  @media (max-width: 768px) {
    .grid-container {
      grid-template-columns: 1fr;
    }

    .center-card {
      grid-column: span 1;
    }

    .bottom-grid {
      grid-template-columns: 1fr;
    }

    .charts-container {
      flex-direction: column;
    }

    .ranking-container {
      flex-direction: column;
    }

    .search-container {
      width: 100%;
    }

    .search-container .el-input {
      width: 100%;
    }
  }
}
</style>