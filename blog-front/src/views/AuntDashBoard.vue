<template>
  <div class="dashboard-view">
    <div style="margin-top: 5px;">
      <el-col :xs="24" :sm="24" :lg="24" class="card-panel-col">
        <el-row>
          <el-col :span="3">
            <el-input placeholder="按用户名搜索" v-model="data.uName">
              <template #prefix>
                <el-icon class="el-input__icon">
                  <search />
                </el-icon>
              </template>
            </el-input>
          </el-col>
          <el-col :span="1">
            <el-button type="success" @click="selectByNameAllFun">
              <i class="el-icon-search"></i>
              搜索
            </el-button>
          </el-col>
        </el-row>
      </el-col>
    </div>
    <div v-show="data.isShowContent">
      <div class="panel-group">
        <el-row :gutter="40" class="panel-group">
          <el-col :xs="24" :lg="8" class="card-panel-col">
            <div class="card-panel">
              <el-descriptions class="margin-top" title="上次记录" :column="2" border>
                <el-descriptions-item label="姓名">{{ data.auntForm.name }}</el-descriptions-item>
                <el-descriptions-item label="周期长度">{{ data.auntForm.cycle }}</el-descriptions-item>
                <el-descriptions-item label="开始日期">{{ data.auntForm.startDate }}</el-descriptions-item>
                <el-descriptions-item label="结束日期">{{ data.auntForm.endDate }}</el-descriptions-item>
                <el-descriptions-item label="持续时间">{{ data.auntForm.duration }}</el-descriptions-item>
                <el-descriptions-item label="血量">{{ data.auntForm.bloodVolume }}ml</el-descriptions-item>
              </el-descriptions>
            </div>
          </el-col>
          <el-col :xs="24" :lg="8" class="card-panel-col">
            <div class="card-panel">
              <el-descriptions class="margin-top" title="系统推测下次经期情况" :column="2" border>
                <el-descriptions-item label="姓名">{{ data.auntForm.name }}</el-descriptions-item>
                <el-descriptions-item label="周期长度">{{ data.nextAuntForm.cycleLength }}天</el-descriptions-item>
                <el-descriptions-item label="开始日期">{{ data.nextAuntForm.nextPeriodStartDate }}</el-descriptions-item>
                <el-descriptions-item label="结束日期">{{ data.nextAuntForm.nextPeriodEndDate }}</el-descriptions-item>
                <el-descriptions-item label="持续时间">{{ data.nextAuntForm.periodLength }}天</el-descriptions-item>
                <el-descriptions-item label="距离开始还有">{{ data.nextAuntForm.daysUntilNextPeriod }}天</el-descriptions-item>
              </el-descriptions>
            </div>
          </el-col>
          <el-col :xs="24" :lg="8" class="card-panel-col">
            <div class="card-panel">
              <el-descriptions class="margin-top" title="本次实际记录" :column="2" border>
                <el-descriptions-item label="姓名">{{ data.auntForm.name }}</el-descriptions-item>
                <el-descriptions-item label="周期长度">{{ data.realAuntForm.cycle }}</el-descriptions-item>
                <el-descriptions-item label="开始日期">{{ data.realAuntForm.startDate }}</el-descriptions-item>
                <el-descriptions-item label="结束日期">{{ data.realAuntForm.endDate }}</el-descriptions-item>
                <el-descriptions-item label="持续时间">{{ data.realAuntForm.duration }}</el-descriptions-item>
                <el-descriptions-item label="血量">{{ data.realAuntForm.bloodVolume }}ml</el-descriptions-item>
              </el-descriptions>
            </div>
          </el-col>
        </el-row>
      </div>
      <div class="echarts-group">
        <div class="chart-item">
          <div class="echart" id="mychart" style="float: left; width: 70%;height: 550px;"></div>
          <div class="select">
            <el-form inline>
              <el-form-item>
                <el-select
                  v-model="data.curYear"
                  placeholder="请选择"
                  style="width: 100px;"
                  size="small"
                >
                  <el-option
                    v-for="item in data.yearOptions"
                    :key="item.key"
                    :value="item.value"
                    :label="item.label"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-select
                  v-model="data.curMonth"
                  placeholder="请选择"
                  style="width: 80px;"
                  size="small"
                >
                  <el-option
                    v-for="item in data.monthOptions"
                    :key="item.key"
                    :value="item.value"
                    :label="item.label"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleQuickChange('prev')" size="small">上一月</el-button>
                <el-button type="primary" @click="handleQuickChange('next')" size="small">下一月</el-button>
                <el-button type="primary" @click="handleQuickChange('cur')" size="small">今</el-button>
              </el-form-item>
              <el-form-item>
                <div
                  style="width:80px;height:30px;background-color: #1ecc0e;margin-left: 15px;align-items: center;justify-content: center;display: flex;"
                >经后期</div>
                <div
                  style="width:80px;height:30px;background-color: #f73131;margin-left: 15px;align-items: center;justify-content: center;display: flex;"
                >月经期</div>
                <div
                  style="width:80px;height:30px;background-color: #277c20;margin-left: 15px;align-items: center;justify-content: center;display: flex;"
                >经前期</div>
                <div
                  style="width:80px;height:30px;background-color: #c548ff;margin-left: 15px;align-items: center;justify-content: center;display: flex;"
                >排卵期</div>
              </el-form-item>
            </el-form>
          </div>
          <table class="calendar-table" :style="{ width:props.width }">
            <thead>
              <tr>
                <th v-for="(item, i) in data.weeks" :key="i">{{ item }}</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(dates, i) in data.res" :key="i" :style="{ height: props.tbodyHeight }">
                <td
                  v-for="(item, index) in dates"
                  :key="index"
                  :class="{ notCurMonth: !item.isCurMonth,yellow:item.isYellow,greens:item.isGreens, currentDay: item.isCurrentDay, selectDay: item.isSelected, curDay: item.isCurDay, rangeSelectd: item.isRangeSelected, weekend: item.isWeekend , green:item.isGreen }"
                  @click="handleItemClick(item, i, index)"
                >
                  <span class="span-day">{{ item.day }}</span>
                  <span class="span-day span-txt">{{ item.nl }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
    <div v-show="data.isEmpty">
      <el-empty description="暂无该用户数据"></el-empty>
    </div>
  </div>
</template>
    
<script setup lang="ts">
import {
  _getUserAll,
  _getAuntNameAll,
  _getAuntByNameAll,
  _addAunt,
  _updAunt
} from "@/api/api.js";
import {
  getDaysInMonth,
  handleCrateDate,
  handleCreateDatePicker,
  parseTime
} from "@/utils/calendar.js";
import {
  ref,
  watch,
  onMounted,
  defineProps,
  reactive,
  nextTick,
  computed
} from "vue";
import "echarts-wordcloud";
import * as echarts from "echarts";
import { ElMessage, ElLoading } from "element-plus";

const props = defineProps({
  selectMode: {
    type: String,
    default: "click"
  },
  startOfWeek: {
    type: Number,
    default: 1
  },
  canSelect: {
    type: Boolean,
    default: false
  },
  width: {
    type: String,
    default: "28.5%"
  },
  tbodyHeight: {
    type: String,
    default: "60px"
  }
});
const data = reactive({
  isEmpty: true, //空状态
  dialogTableVisible: false,
  dialogFormVisible: false,
  isShowContent: false, //主体内容显\隐
  monthOptions: [],
  yearOptions: [],
  weeks: ["日", "一", "二", "三", "四", "五", "六"],
  curYear: ref(new Date().getFullYear()), // 当前年
  curMonth: ref(new Date().getMonth()), // 当前月
  curDay: ref(new Date().getDate()), // 当前日
  days: 0, // 当前月总共天数
  curDate: parseTime(new Date().getTime()), // 当前日期 yyyy-MM-dd 格式，用来匹配是否是当前日期
  prevDays: [], // 非当前月的上一月展示的日期
  rearDays: [], // 非当前月的下一月展示的日期
  curDays: [], // 当前月的日期
  curIndex_i: 1, //当前日期i坐标
  curIndex_j: 1, //当前日期j坐标
  chkIndex_i: -1, //选中日期i坐标
  chkIndex_j: -1, //选中日期j坐标
  showDays: [], // 总共展示的42个日期
  res: [], // 二维数组
  selectedDates: "", // 选中的日期
  selectedMode: false, // true表示点击， false表示滑动
  moveIndex: [], // 两个，第一个是起始，第二个是结束
  canMove: false, // 当moveIndex数组有一个值时，可以触发滑动
  auntForm: {
    name: "",
    startDate: "", //开始日期
    endDate: "", //结束日期
    duration: "", //持续时间
    cycle: "", //周期长度
    bloodVolume: "", //姨妈血量
    uStatus: "0"
  },
  realAuntForm: {
    id: "",
    name: "",
    startDate: "", //开始日期
    endDate: "", //结束日期
    duration: "", //持续时间
    cycle: "", //周期长度
    bloodVolume: "", //姨妈血量
    symptom: "", //症状
    note: "", //备注
    uStatus: ""
  },
  nextAuntForm: {
    daysUntilNextPeriod: "", //距离下次还剩的时间
    cycleLength: "", //周期
    periodLength: "", //持续时间
    nextPeriodEndDate: "", //下次结束时间
    nextPeriodStartDate: "" //下次开始时间
  },
  uNameAll: [], //所有用户
  uName: "柳莉莉", //查询的用户
  value: new Date(),

  xData: [], //横坐标
  yData: [], //数据
  myChartStyle: { float: "left", width: "70%", height: "550px" } //图表样式
});

watch(
  () => data.curMonth,
  () => {
    handleGetDays(data.curYear, data.curMonth, props.startOfWeek);
  }
);

watch(
  () => data.curYear,
  () => {
    handleGetDays(data.curYear, data.curMonth, props.startOfWeek);
  }
);

// function init() {
//   //获取所有登记用户名字
//   // _getAuntNameAll().then(res => {
//   //   data.uNameAll = res.data.obj;
//   // });

//   selectByNameAllFun();
// }
/*根据名字查询所有记录*/
function selectByNameAllFun() {
  if (data.uName === "") {
    ElMessage.error("请输入需要搜索的人员姓名");
    return;
  }
  data.xData = [];
  data.yData = [];
  _getAuntByNameAll(data.uName, 0).then(res => {
    //历史记录
    if (res.data == "") {
      data.isEmpty = true;
      data.isShowContent = false;
      ElMessage({
        message: "未查到该人员",
        type: "warning"
      });
    } else {
      data.isEmpty = false;
      data.isShowContent = true;
      res.data.auntList.forEach(element => {
        data.xData.push(element.startDate + "~" + element.endDate);
        data.yData.push(element.bloodVolume);
      });
      data.nextAuntForm.daysUntilNextPeriod = res.data.daysUntilNextPeriod;
      data.nextAuntForm.nextPeriodEndDate = res.data.nextPeriodEndDate;
      data.nextAuntForm.nextPeriodStartDate = res.data.nextPeriodStartDate;
      data.nextAuntForm.cycleLength = res.data.cycleLength;
      data.nextAuntForm.periodLength = res.data.periodLength;

      data.auntForm.name = res.data.auntList[0].uName;
      data.auntForm.startDate = res.data.auntList[0].startDate;
      data.auntForm.endDate = res.data.auntList[0].endDate;
      data.auntForm.duration = res.data.auntList[0].duration;
      data.auntForm.cycle = res.data.auntList[0].cycle;
      data.auntForm.bloodVolume = res.data.auntList[0].bloodVolume;
    }
  });

  _getAuntByNameAll(data.uName, 1).then(res => {
    //实际记录
    if (res.data === "") {
      data.realAuntForm.name = data.uName;
    } else {
      data.realAuntForm.id = res.data.auntList[0].id;
      data.realAuntForm.name = data.uName;
      data.realAuntForm.startDate = res.data.auntList[0].startDate;
      data.realAuntForm.endDate = res.data.auntList[0].endDate;
      data.realAuntForm.duration = res.data.auntList[0].duration;
      data.realAuntForm.cycle = res.data.auntList[0].cycle;
      data.realAuntForm.bloodVolume = res.data.auntList[0].bloodVolume;
      data.realAuntForm.symptom = res.data.auntList[0].symptom;
      data.realAuntForm.note = res.data.auntList[0].note;
    }
  });
  setTimeout(() => {
    initEcharts();
  }, 100);
}
/*获取天数*/
function handleGetDays(year, month, startOfWeek) {
  data.showDays = [];
  data.days = getDaysInMonth(year, month);
  let firstDayOfWeek = new Date(`${year}-${month + 1}-01`).getDay();
  // 处理周起始日
  const obj = {
    0: "日",
    1: "一",
    2: "二",
    3: "三",
    4: "四",
    5: "五",
    6: "六"
  };
  const firstDayInCN = obj[firstDayOfWeek];
  const index = data.weeks.indexOf(firstDayInCN);

  if (firstDayOfWeek === 0) {
    // 星期天为0 星期一为1 ，以此类推
    firstDayOfWeek = 7;
  }
  data.prevDays = handleCrateDate(year, month, 1, index + 1, "prev");
  data.rearDays = handleCrateDate(
    year,
    month,
    1,
    35 - data.days - index,
    "rear"
  );
  if (data.rearDays.length == 0) {
    data.rearDays = handleCrateDate(
      year,
      month,
      1,
      42 - data.days - index,
      "rear"
    );
  }
  data.curDays = handleCrateDate(year, month, 1, data.days);
  data.showDays.unshift(...data.prevDays);
  data.showDays.push(...data.curDays);
  data.showDays.push(...data.rearDays);
  data.res = handleFormatDates(data.showDays);
}
/*传入长度42的原数组，最终转换成二维数组，格式化*/
function handleFormatDates(arr, size = 7) {
  const arr2 = [];
  for (let i = 0; i < size; i++) {
    const temp = arr.slice(i * size, i * size + size);
    for (let j = 0; j < temp.length; j++) {
      //获取当前日期的坐标
      if (temp[j].date === data.curDate) {
        data.curIndex_i = i;
        data.curIndex_j = j;
      }
    }
    arr2.push(temp);
  }
  arr2[data.curIndex_i][data.curIndex_j].isCurrentDay = true;
  return arr2;
}

/*打印周期表头*/
function handleTableHead(start) {
  const sliceDates = data.weeks.splice(start - 1);
  data.weeks.unshift(...sliceDates);
}

/*日期点击事件*/
function handleItemClick(item, i, j) {
  const chkText = data.res[i][j].flag;
  data.selectedDates = data.res[i][j].date;
  if (chkText === "prev") {
    handleQuickChange("prev");
  } else if (chkText === "rear") {
    handleQuickChange("next");
  } else {
    if (data.chkIndex_i == -1) {
      data.chkIndex_i = data.curIndex_i;
      data.chkIndex_j = data.curIndex_j;
    }
    data.res[data.chkIndex_i][data.chkIndex_j].isSelected = false;
    data.res[data.curIndex_i][data.curIndex_j].isCurrentDay = false;
    let ddd = 1;
    nextTick(() => {
      for (let a = 0; a < 7; a++) {
        const temp = data.res[a];
        for (let b = 0; b < temp.length; b++) {
          //获取当前日期的坐标
          if (data.res[a][b].month === new Date().getMonth() + 1) {
            data.res[data.curIndex_i][data.curIndex_j].isCurDay = true;
          } else {
            data.res[data.curIndex_i][data.curIndex_j].isCurDay = false;
          }
        }
      }
      if (
        i == data.curIndex_i &&
        j == data.curIndex_j &&
        data.selectedDates === data.curDate
      ) {
        data.res[data.curIndex_i][data.curIndex_j].isCurrentDay = true;
        data.res[data.curIndex_i][data.curIndex_j].isCurDay = false;
      } else {
        data.res[i][j].isSelected = true;
      }
    });
    data.chkIndex_i = i;
    data.chkIndex_j = j;
  }
}

const nextDate = computed(() => {
  const ddd = new Date(data.nextAuntForm.nextPeriodEndDate);
  const num = 604800000;
  const c = ddd.getTime() + num;
  return new Date(c);
});

const pLDate = computed(() => {
  const ddd = new Date(nextDate.value);
  ddd.setDate(ddd.getDate() + 7);
  return ddd.toISOString();
});

const JQDate = computed(() => {
  const ddd = new Date(pLDate.value);
  ddd.setDate(ddd.getDate() + 7);
  return ddd.toISOString();
});

const oldDate = computed(() => {
  const ddd = new Date(data.auntForm.endDate);
  const num = 604800000;
  const c = ddd.getTime() + num;
  return new Date(c);
});

const oldPLDate = computed(() => {
  const ddd = new Date(oldDate.value);
  ddd.setDate(ddd.getDate() + 7);
  return ddd.toISOString();
});

const oldJQDate = computed(() => {
  const ddd = new Date(oldPLDate.value);
  ddd.setDate(ddd.getDate() + 7);
  return ddd.toISOString();
});

/*手动点击月份按钮事件*/
function handleQuickChange(type) {
  const ddd = new Date(data.nextAuntForm.nextPeriodStartDate);
  const dd = new Date(nextDate.value);
  const ttt = new Date(data.nextAuntForm.nextPeriodEndDate);
  const yyy = new Date(pLDate.value);
  const ggg = new Date(JQDate.value);

  const aaa = new Date(data.auntForm.startDate);
  const aa = new Date(oldDate.value);
  const bbb = new Date(data.auntForm.endDate);
  const yy = new Date(oldPLDate.value);
  const gg = new Date(oldJQDate.value);

  let flag1 = false; //月经期
  let flag2 = false; //经后期
  let flag3 = false; //排卵期
  let flag4 = false;  //经前期
  if (type === "prev") {
    data.curMonth--;
    if (data.curMonth === -1) {
      data.curMonth = 11;
      data.curYear -= 1;
    }
    nextTick(() => {
      for (let i = 0; i < 7; i++) {
        const temp = data.res[i];
        for (let j = 0; j < temp.length; j++) {
          const year = data.res[i][j].year;
          const month = data.res[i][j].month;
          const day = data.res[i][j].day;
          //经前期
          if (
            (year === yy.getFullYear() &&
              month === yy.getMonth() &&
              day === yy.getDate() + 1) ||
            (year === yyy.getFullYear() &&
              month === yyy.getMonth() &&
              day === yyy.getDate() + 1)
          ) {
            flag4 = true;
          }
          if (flag4) {
            data.res[i][j].isGreens = true;
          }
          if (
            (year === gg.getFullYear() &&
              month === gg.getMonth() &&
              day === gg.getDate()) ||
            (year === ggg.getFullYear() &&
              month === ggg.getMonth() &&
              day === ggg.getDate())
          ) {
            flag4 = false;
          }
          //排卵期
          if (
            (year === dd.getFullYear() &&
              month === dd.getMonth() &&
              day === dd.getDate() + 1) ||
            (year === aa.getFullYear() &&
              month === aa.getMonth() &&
              day === aa.getDate() + 1)
          ) {
            flag3 = true;
          }
          if (flag3) {
            data.res[i][j].isYellow = true;
          }
          if (
            (year === yy.getFullYear() &&
              month === yy.getMonth() &&
              day === yy.getDate()) ||
            (year === yyy.getFullYear() &&
              month === yyy.getMonth() &&
              day === yyy.getDate())
          ) {
            flag3 = false;
          }
          //经后期
          if (
            (year === ttt.getFullYear() &&
              month === ttt.getMonth() &&
              day === ttt.getDate() + 1) ||
            (year === bbb.getFullYear() &&
              month === bbb.getMonth() &&
              day === bbb.getDate() + 1)
          ) {
            flag2 = true;
          }
          if (flag2) {
            data.res[i][j].isGreen = true;
          }
          if (
            (year === dd.getFullYear() &&
              month === dd.getMonth() &&
              day === dd.getDate()) ||
            (year === aa.getFullYear() &&
              month === aa.getMonth() &&
              day === aa.getDate())
          ) {
            flag2 = false;
          }
          //月经期
          if (
            (year === ddd.getFullYear() &&
              month === ddd.getMonth() &&
              day === ddd.getDate()) ||
            (year === aaa.getFullYear() &&
              month === aaa.getMonth() &&
              day === aaa.getDate())
          ) {
            flag1 = true;
          }
          if (flag1) {
            data.res[i][j].isWeekend = true;
          }
          if (
            (year === ttt.getFullYear() &&
              month === ttt.getMonth() &&
              day === ttt.getDate()) ||
            (year === bbb.getFullYear() &&
              month === bbb.getMonth() &&
              day === bbb.getDate())
          ) {
            flag1 = false;
          }
        }
      }
    });
  } else if (type === "next") {
    data.curMonth++;
    if (data.curMonth === 12) {
      data.curMonth = 0;
      data.curYear += 1;
    }
    nextTick(() => {
      for (let i = 0; i < 7; i++) {
        const temp = data.res[i];
        for (let j = 0; j < temp.length; j++) {
          const year = data.res[i][j].year;
          const month = data.res[i][j].month;
          const day = data.res[i][j].day;
          //经前期
          if (
            (year === yy.getFullYear() &&
              month === yy.getMonth() &&
              day === yy.getDate() + 1) ||
            (year === yyy.getFullYear() &&
              month === yyy.getMonth() &&
              day === yyy.getDate() + 1)
          ) {
            flag4 = true;
          }
          if (flag4) {
            data.res[i][j].isGreens = true;
          }
          if (
            (year === gg.getFullYear() &&
              month === gg.getMonth() &&
              day === gg.getDate()) ||
            (year === ggg.getFullYear() &&
              month === ggg.getMonth() &&
              day === ggg.getDate())
          ) {
            flag4 = false;
          }
          //排卵期
          if (
            (year === dd.getFullYear() &&
              month === dd.getMonth() &&
              day === dd.getDate() + 1) ||
            (year === aa.getFullYear() &&
              month === aa.getMonth() &&
              day === aa.getDate() + 1)
          ) {
            flag3 = true;
          }
          if (flag3) {
            data.res[i][j].isYellow = true;
          }
          if (
            (year === yy.getFullYear() &&
              month === yy.getMonth() &&
              day === yy.getDate()) ||
            (year === yyy.getFullYear() &&
              month === yyy.getMonth() &&
              day === yyy.getDate())
          ) {
            flag3 = false;
          }
          //经后期
          if (
            (year === ttt.getFullYear() &&
              month === ttt.getMonth() &&
              day === ttt.getDate() + 1) ||
            (year === bbb.getFullYear() &&
              month === bbb.getMonth() &&
              day === bbb.getDate() + 1)
          ) {
            flag2 = true;
          }
          if (flag2) {
            data.res[i][j].isGreen = true;
          }
          if (
            (year === dd.getFullYear() &&
              month === dd.getMonth() &&
              day === dd.getDate()) ||
            (year === aa.getFullYear() &&
              month === aa.getMonth() &&
              day === aa.getDate())
          ) {
            flag2 = false;
          }
          //月经期
          if (
            (year === ddd.getFullYear() &&
              month === ddd.getMonth() &&
              day === ddd.getDate()) ||
            (year === aaa.getFullYear() &&
              month === aaa.getMonth() &&
              day === aaa.getDate())
          ) {
            flag1 = true;
          }
          if (flag1) {
            data.res[i][j].isWeekend = true;
          }
          if (
            (year === ttt.getFullYear() &&
              month === ttt.getMonth() &&
              day === ttt.getDate()) ||
            (year === bbb.getFullYear() &&
              month === bbb.getMonth() &&
              day === bbb.getDate())
          ) {
            flag1 = false;
          }
        }
      }
    });
  } else {
    data.curYear = ref(new Date().getFullYear()); // 当前年
    data.curMonth = ref(new Date().getMonth()); // 当前月
    data.curDay = ref(new Date().getDate()); // 当前日

    nextTick(() => {
      for (let i = 0; i < 7; i++) {
        const temp = data.res[i];
        for (let j = 0; j < temp.length; j++) {
          const year = data.res[i][j].year;
          const month = data.res[i][j].month;
          const day = data.res[i][j].day;
          //经前期
          if (
            (year === yy.getFullYear() &&
              month === yy.getMonth() &&
              day === yy.getDate() + 1) ||
            (year === yyy.getFullYear() &&
              month === yyy.getMonth() &&
              day === yyy.getDate() + 1)
          ) {
            flag4 = true;
          }
          if (flag4) {
            data.res[i][j].isGreens = true;
          }
          if (
            (year === gg.getFullYear() &&
              month === gg.getMonth() &&
              day === gg.getDate()) ||
            (year === ggg.getFullYear() &&
              month === ggg.getMonth() &&
              day === ggg.getDate())
          ) {
            flag4 = false;
          }
          //排卵期
          if (
            (year === dd.getFullYear() &&
              month === dd.getMonth() &&
              day === dd.getDate() + 1) ||
            (year === aa.getFullYear() &&
              month === aa.getMonth() &&
              day === aa.getDate() + 1)
          ) {
            flag3 = true;
          }
          if (flag3) {
            data.res[i][j].isYellow = true;
          }
          if (
            (year === yy.getFullYear() &&
              month === yy.getMonth() &&
              day === yy.getDate()) ||
            (year === yyy.getFullYear() &&
              month === yyy.getMonth() &&
              day === yyy.getDate())
          ) {
            flag3 = false;
          }
          //经后期
          if (
            (year === ttt.getFullYear() &&
              month === ttt.getMonth() &&
              day === ttt.getDate() + 1) ||
            (year === bbb.getFullYear() &&
              month === bbb.getMonth() &&
              day === bbb.getDate() + 1)
          ) {
            flag2 = true;
          }
          if (flag2) {
            data.res[i][j].isGreen = true;
          }
          if (
            (year === dd.getFullYear() &&
              month === dd.getMonth() &&
              day === dd.getDate()) ||
            (year === aa.getFullYear() &&
              month === aa.getMonth() &&
              day === aa.getDate())
          ) {
            flag2 = false;
          }
          //月经期
          if (
            (year === ddd.getFullYear() &&
              month === ddd.getMonth() &&
              day === ddd.getDate()) ||
            (year === aaa.getFullYear() &&
              month === aaa.getMonth() &&
              day === aaa.getDate())
          ) {
            flag1 = true;
          }
          if (flag1) {
            data.res[i][j].isWeekend = true;
          }
          if (
            (year === ttt.getFullYear() &&
              month === ttt.getMonth() &&
              day === ttt.getDate()) ||
            (year === bbb.getFullYear() &&
              month === bbb.getMonth() &&
              day === bbb.getDate())
          ) {
            flag1 = false;
          }
        }
      }
    });
  }
}

/*柱状图*/
function initEcharts() {
  if (data.isEmpty) return;
  // 基本柱状图
  const option = {
    title: {
      text: "月例-流血量",
      left: "center"
    },
    tooltip: {
      trigger: "item"
    },
    xAxis: {
      data: data.xData
    },
    yAxis: {},
    series: [
      {
        name: "日期范围",
        type: "bar", //形状为柱状图
        data: data.yData,
        tooltip: {
          valueFormatter: function(value) {
            return value + " ml";
          }
        }
      }
    ]
  };

  setTimeout(() => {
    const myChart = echarts.init(document.getElementById("mychart"));
    myChart.setOption(option);
  }, 500);
}

onMounted(() => {
  selectByNameAllFun();
  data.monthOptions = handleCreateDatePicker().months;
  data.yearOptions = handleCreateDatePicker().years;

  if (localStorage.selectedDates)
    data.selectedDates = JSON.parse(localStorage.selectedDates);

  // init();
  data.weeks.unshift(...data.weeks.splice(props.startOfWeek - 1));
  handleGetDays(data.curYear, data.curMonth, props.startOfWeek);
  data.selectedMode = props.selectMode === "click";
});
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
    grid-template-columns: 100% 50%;
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

.el-descriptions__body th {
  width: 80px;
}

.panel-group {
  margin-top: 18px;

  .card-panel-col {
    margin-bottom: 12px;
  }

  .card-panel {
    float: left;
    height: 195px;
    width: 100%;

    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 4px 4px 40px rgba(0, 0, 0, 0.05);
    border-color: rgba(0, 0, 0, 0.05);
  }
}

.echarts {
  // display: flex;
  // align-items: center;
  // justify-content: center;
  // flex-direction: column;
  float: left;
}

.span-day {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
}

.span-txt {
  font-size: 12px;
}

.calendar {
  // display: flex;
  // align-items: center;
  justify-content: center;
  flex-direction: column;
  float: right;
}

.calendar-table {
  // table-layout: fixed;
  border-collapse: collapse;
  transition: 0.3s;

  thead tr {
    height: 50px;
  }

  tbody tr {
    &:first-child td {
      border-top: 1px solid #08a8a0;
    }

    td {
      cursor: pointer;
      border-right: 1px solid #08a8a0;
      border-bottom: 1px solid #08a8a0;

      &:first-child {
        border-left: 1px solid #08a8a0;
      }
    }
  }
}

.notCurMonth {
  color: #111111;
  background-color: #edeef0;
}

.currentDay {
  //当前日期颜色
  color: #fff;
  background-color: #409eff;
}

.selectDay {
  //选中日期颜色
  color: rgb(0, 0, 0);
  background-color: #409eff;
}

.rangeSelectd {
  //默认日期颜色
  color: #606266;
  background-color: #dee2e9;
}

.weekend {
  color: #fa0707;
  //background-color: #b64545;
}

.green {
  color: #1ecc0e;
}

.yellow {
  color: #c548ff;
}

.greens {
  color: #277c20;
}
.curDay {
  color: #409eff;
  background-color: #ffffff;
}
</style>
    