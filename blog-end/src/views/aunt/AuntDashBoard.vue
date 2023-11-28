<template>
  <div class="dashboard-view">

    <div class="panel-group">
      <el-row :gutter="40" class="panel-group">
        <el-col :xs="24" :sm="24" :lg="24" class="card-panel-col">
          <el-row style="margin-top: 10px;margin-bottom: 10px;">
            <el-col :span="8">
              <el-select size="small" v-model="uName" filterable placeholder="请选择用户">
                <el-option v-for="item in uNameAll" :label="item.uName" :value="item.uName">
                </el-option>
              </el-select>
              <el-button type="primary" size="small" @click="selectByNameAllFun">
                <i class="el-icon-search"></i>
                搜索
              </el-button>
            </el-col>
          </el-row>
        </el-col>
        <el-col :xs="24" :lg="8" class="card-panel-col">
          <div class="card-panel">
            <el-descriptions class="margin-top" title="上次记录" :column="2" border>
              <el-descriptions-item label="姓名">{{ auntForm.name }}</el-descriptions-item>
              <el-descriptions-item label="周期长度">{{ auntForm.cycle }}</el-descriptions-item>
              <el-descriptions-item label="开始日期">{{ auntForm.startDate }}</el-descriptions-item>
              <el-descriptions-item label="结束日期">{{ auntForm.endDate }}</el-descriptions-item>
              <el-descriptions-item label="持续时间">{{ auntForm.duration }}</el-descriptions-item>
              <el-descriptions-item label="血量">{{ auntForm.bloodVolume }}ml</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-col>
        <el-col :xs="24" :lg="8" class="card-panel-col">
          <div class="card-panel">
            <el-descriptions class="margin-top" title="系统推测下次经期情况" :column="2" border>
              <el-descriptions-item label="姓名">{{ auntForm.name }}</el-descriptions-item>
              <el-descriptions-item label="周期长度">{{ nextAuntForm.cycleLength }}天</el-descriptions-item>
              <el-descriptions-item label="开始日期">{{ nextAuntForm.nextPeriodStartDate }}</el-descriptions-item>
              <el-descriptions-item label="结束日期">{{ nextAuntForm.nextPeriodEndDate }}</el-descriptions-item>
              <el-descriptions-item label="持续时间">{{ nextAuntForm.periodLength }}天</el-descriptions-item>
              <el-descriptions-item label="距离开始还有">{{ nextAuntForm.daysUntilNextPeriod }}天</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-col>
        <el-col :xs="24" :lg="8" class="card-panel-col">
          <div class="card-panel">
            <el-descriptions class="margin-top" title="本次实际记录" :column="2" border>
              <template slot="extra">
                <el-button type="primary" @click="dialogFunction" size="small">编辑</el-button>
              </template>
              <el-descriptions-item label="姓名">{{ auntForm.name }}</el-descriptions-item>
              <el-descriptions-item label="周期长度">{{ realAuntForm.cycle }}</el-descriptions-item>
              <el-descriptions-item label="开始日期">{{ realAuntForm.startDate }}</el-descriptions-item>
              <el-descriptions-item label="结束日期">{{ realAuntForm.endDate }}</el-descriptions-item>
              <el-descriptions-item label="持续时间">{{ realAuntForm.duration }}</el-descriptions-item>
              <el-descriptions-item label="血量">{{ realAuntForm.bloodVolume }}ml</el-descriptions-item>
            </el-descriptions>
            <el-dialog title="本次实际记录" :visible.sync="dialogFormVisible">
              <el-form v-model="realAuntForm">
                <el-form-item label="姓名" label-width="120px">
                  <el-input v-model="realAuntForm.name" autocomplete="off" disabled style="width: 50%;">{{
                    auntForm.name }}</el-input>
                </el-form-item>
                <el-form-item label="周期长度" label-width="120px">
                  <el-input class="el-form-item__label" v-model="realAuntForm.cycle" autocomplete="off" disabled
                    style="width: 10%;"></el-input>
                  <label class="el-form-item__label">持续时间</label>
                  <el-input class="el-form-item__label" v-model="realAuntForm.duration" autocomplete="off" disabled
                    style="width: 10%;"></el-input>
                  <label class="el-form-item__label">血量</label>
                  <el-input class="el-form-item__label" v-model="realAuntForm.bloodVolume" autocomplete="off"
                    style="width: 10%;"></el-input>
                </el-form-item>
                <el-form-item label="开始日期" label-width="120px">
                  <el-date-picker class="el-form-item__label" @change="auntDatetChange(realAuntForm.startDate, 'st')"
                    v-model="realAuntForm.startDate" type="date" :picker-options="pickerOptions" placeholder="选择开始时间">
                  </el-date-picker>
                  <label class="el-form-item__label">结束日期</label>
                  <el-date-picker class="el-form-item__label" @change="auntDatetChange(realAuntForm.endDate, 'ed')"
                    v-model="realAuntForm.endDate" type="date" :picker-options="pickerOptions" placeholder="选择结束时间">
                  </el-date-picker>
                </el-form-item>
                <el-form-item label="症状" label-width="120px">
                  <el-input class="el-form-item__label" type="textarea" :rows="5"
                    placeholder="记录每个月姨妈期间出现的症状，如腹痛、乳房胀痛、情绪波动等" v-model="realAuntForm.symptom"
                    style="width: 40%;"></el-input>
                  <label class="el-form-item__label">备注</label>
                  <el-input class="el-form-item__label" type="textarea" placeholder="可以记录一些额外的信息，如服用的药物、健康状况等。" :rows="5"
                    v-model="realAuntForm.note" style="width: 40%;"></el-input>
                </el-form-item>
              </el-form>
              <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleEditAunt">保 存</el-button>
              </div>
            </el-dialog>
          </div>
        </el-col>
        <el-col :xs="24" :lg="24" class="card-panel-col">
          <div v-show="this.isEmpty"><el-empty description="暂无该用户数据"></el-empty></div>
        </el-col>
      </el-row>
    </div>

    <div class="echarts-group" v-show="isShowContent">

      <div class="chart-item">
        <div class="echart" id="mychart" style="float: left; width: 70%;height: 550px;"></div>
        <div class="select">
          <el-form inline>
            <el-form-item>
              <el-select v-model="curYear" placeholder="请选择" style="width: 100px;" size="small">
                <el-option v-for="item in yearOptions" :key="item.key" :value="item.value" :label="item.label" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-select v-model="curMonth" placeholder="请选择" style="width: 80px;" size="small">
                <el-option v-for="item in monthOptions" :key="item.key" :value="item.value" :label="item.label" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleQuickChange('prev')" size="small">上一月</el-button>
              <el-button type="primary" @click="handleQuickChange('next')" size="small">下一月</el-button>
              <el-button type="primary" @click="handleCreateEvent()" size="small">创建</el-button>
            </el-form-item>
          </el-form>
        </div>

        <table class="calendar-table" :style="{ width }">
          <thead>
            <tr>
              <th v-for="(item, i) in weeks" :key="i">{{ item }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(dates, i) in res" :key="i" :style="{ height: tbodyHeight }">
              <td v-for="(item, index) in dates" :key="index"
                :class="{ notCurMonth: !item.isCurMonth, currentDay: item.isCurrentDay, selectDay: item.isSelected, curDay: item.isCurDay, rangeSelectd: item.isRangeSelected, weekend: item.isWeekend }"
                @click="handleItemClick(item, i, index)" @mouseover="handleItemMove(item, i, index)">
                <!-- <span>{{ item.date.split('-').slice(1).join('-') }}</span> -->
                <span class="span-day">{{ item.day }}</span>
                <span class="span-day span-txt">{{ item.nl }}</span>
                <!-- <slot :data="item" /> -->
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
    
<script>
import { _getUserAll, _getAuntNameAll, _getAuntByNameAll, _addAunt, _updAunt } from "@/api/api.js";
import { getDaysInMonth, handleCrateDate, handleCreateDatePicker, parseTime } from '../../utils/calendar'

import 'echarts-wordcloud';

export default {
  name: "AuntDashBoard",
  components: {
  },
  props: {
    'selectMode': {
      type: String,
      default: 'click'
    },
    'startOfWeek': {
      type: Number,
      default: 1
    },
    canSelect: {
      type: Boolean,
      default: false
    },
    width: {
      type: String,
      default: '28.5%'
    },
    tbodyHeight: {
      type: String,
      default: '60px'
    }
  },
  data() {
    return {
      isEmpty: false,  //空状态
      dialogTableVisible: false,
      dialogFormVisible: false,
      isShowContent: false,  //主体内容显\隐
      monthOptions: [],
      yearOptions: [],
      weeks: ['日', '一', '二', '三', '四', '五', '六'],
      curYear: new Date().getFullYear(), // 当前年
      curMonth: new Date().getMonth(), // 当前月
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
        name: '',
        startDate: '',   //开始日期
        endDate: '',     //结束日期
        duration: '',    //持续时间
        cycle: '',      //周期长度
        bloodVolume: '', //姨妈血量
        uStatus: '0'
      },
      realAuntForm: {
        id: '',
        name: '',
        startDate: '',   //开始日期
        endDate: '',     //结束日期
        duration: '',    //持续时间
        cycle: '',      //周期长度
        bloodVolume: '', //姨妈血量
        symptom: '', //症状
        note: '', //备注
        uStatus: ''
      },
      nextAuntForm: {
        daysUntilNextPeriod: '', //距离下次还剩的时间
        cycleLength: '',     //周期
        periodLength: '',    //持续时间
        nextPeriodEndDate: '',   //下次结束时间
        nextPeriodStartDate: ''  //下次开始时间
      },
      uNameAll: [],    //所有用户
      uName: '',   //查询的用户
      value: new Date(),

      xData: [], //横坐标
      yData: [], //数据
      myChartStyle: { float: "left", width: "70%", height: "550px" }, //图表样式
      pickerOptions: { //限制日期控件选择
        disabledDate(time) {

          const num = 1000 * 3600 * 24 * 35
          const d = Date.now() - num
          return time.getTime() > Date.now() || time.getTime() < d;
        },
      },
    }
  },
  watch: {
    curMonth: {
      handler(val) {
        this.handleGetDays(this.curYear, val, this.startOfWeek)
      }
    },
    curYear: {
      handler(val) {
        this.handleGetDays(val, this.curMonth, this.startOfWeek)
      }
    }
  },
  methods: {
    init() {
      //获取所有登记用户名字
      _getAuntNameAll().then(res => {
        this.uNameAll = res.data.obj
      })
      this.uName = this.$store.state.login_user.username

      this.selectByNameAllFun()
    },
    //根据名字查询所有记录
    selectByNameAllFun() {
      if (this.uName === "") {
        const num = 1000 * 3600 * 24 * 35
        const d = Date.now() - num
        console.log("35天时间" + num)
        console.log("当前：" + Date.now())
        console.log("差值：" + d)
        console.log(parseTime(d))
        this.$notify.error({
          title: '错误',
          message: '请选择查询用户'
        });
        return
      }
      this.xData = []
      this.yData = []
      _getAuntByNameAll(this.uName, 0).then(res => { //历史记录
        if (res.data == "") {
          this.isEmpty = true
        } else {
          this.isEmpty = false
          res.data.auntList.forEach(element => {
            this.xData.push(element.startDate + "~" + element.endDate)
            this.yData.push(element.bloodVolume)
          });
          this.initEcharts();

          this.nextAuntForm.daysUntilNextPeriod = res.data.daysUntilNextPeriod
          this.nextAuntForm.nextPeriodEndDate = res.data.nextPeriodEndDate
          this.nextAuntForm.nextPeriodStartDate = res.data.nextPeriodStartDate
          this.nextAuntForm.cycleLength = res.data.cycleLength
          this.nextAuntForm.periodLength = res.data.periodLength


          this.auntForm.name = res.data.auntList[0].uName
          this.auntForm.startDate = res.data.auntList[0].startDate
          this.auntForm.endDate = res.data.auntList[0].endDate
          this.auntForm.duration = res.data.auntList[0].duration
          this.auntForm.cycle = res.data.auntList[0].cycle
          this.auntForm.bloodVolume = res.data.auntList[0].bloodVolume

          this.isShowContent = true
        }

      })

      _getAuntByNameAll(this.uName, 1).then(res => { //实际记录
        if (res.data === "") {
          this.realAuntForm.name = this.uName
        } else {
          this.realAuntForm.id = res.data.auntList[0].id
          this.realAuntForm.name = this.uName
          this.realAuntForm.startDate = res.data.auntList[0].startDate
          this.realAuntForm.endDate = res.data.auntList[0].endDate
          this.realAuntForm.duration = res.data.auntList[0].duration
          this.realAuntForm.cycle = res.data.auntList[0].cycle
          this.realAuntForm.bloodVolume = res.data.auntList[0].bloodVolume
        }

      })
    },
    //获取天数
    handleGetDays(year, month, startOfWeek) {
      this.showDays = []
      this.days = getDaysInMonth(year, month)
      let firstDayOfWeek = new Date(`${year}-${month + 1}-01`).getDay()
      // 处理周起始日
      const obj = {
        0: '日',
        1: '一',
        2: '二',
        3: '三',
        4: '四',
        5: '五',
        6: '六'
      }
      const firstDayInCN = obj[firstDayOfWeek]
      const index = this.weeks.indexOf(firstDayInCN)

      if (firstDayOfWeek === 0) { // 星期天为0 星期一为1 ，以此类推
        firstDayOfWeek = 7
      }
      this.prevDays = handleCrateDate(year, month, 1, index + 1, 'prev')
      this.rearDays = handleCrateDate(year, month, 1, 35 - this.days - (index), 'rear')
      if (this.rearDays.length == 0) {
        this.rearDays = handleCrateDate(year, month, 1, 42 - this.days - (index), 'rear')
      }
      this.curDays = handleCrateDate(year, month, 1, this.days)
      this.showDays.unshift(...this.prevDays)
      this.showDays.push(...this.curDays)
      this.showDays.push(...this.rearDays)
      this.res = this.handleFormatDates(this.showDays)
    },
    // 传入长度42的原数组，最终转换成二维数组，格式化
    handleFormatDates(arr, size = 7) {
      const arr2 = []
      for (let i = 0; i < size; i++) {
        const temp = arr.slice(i * size, i * size + size)
        for (let j = 0; j < temp.length; j++) {//获取当前日期的坐标
          if (temp[j].date === this.curDate) {
            this.curIndex_i = i
            this.curIndex_j = j
          }
        }
        arr2.push(temp)
      }
      arr2[this.curIndex_i][this.curIndex_j].isCurrentDay = true
      return arr2
    },
    handleTableHead(start) {
      const sliceDates = this.weeks.splice(start - 1)
      this.weeks.unshift(...sliceDates)
    },
    //创建日期事件
    handleCreateEvent() {
      const date = new Date().getDate
      if (this.chkIndex_i == -1) {
        this.chkIndex_i = this.curIndex_i
        this.chkIndex_j = this.curIndex_j
      }
      const day = this.res[this.chkIndex_i][this.chkIndex_j].date
      const aa = {"name":this.uName,"date":day}
      this.$router.push("/home/aunt/CreateEven/"+JSON.stringify(aa))
    },
    //日期点击事件
    handleItemClick(item, i, j) {
      const chkText = this.res[i][j].flag
      this.selectedDates = this.res[i][j].date
      if (chkText === 'prev') {
        this.handleQuickChange('prev')
      } else if (chkText === 'rear') {
        this.handleQuickChange('next')
      } else {
        if (this.chkIndex_i == -1) {
          this.chkIndex_i = this.curIndex_i
          this.chkIndex_j = this.curIndex_j
        }
        this.res[this.chkIndex_i][this.chkIndex_j].isSelected = false
        this.res[this.curIndex_i][this.curIndex_j].isCurrentDay = false
        let ddd = 1
        this.$nextTick(() => {
          for (let a = 0; a < 7; a++) {
            const temp = this.res[a]
            for (let b = 0; b < temp.length; b++) {//获取当前日期的坐标
              if (this.res[a][b].month === (new Date().getMonth() + 1)) {
                this.res[this.curIndex_i][this.curIndex_j].isCurDay = true
              } else {
                this.res[this.curIndex_i][this.curIndex_j].isCurDay = false
              }
            }
          }
          if (i == this.curIndex_i && j == this.curIndex_j && this.selectedDates === this.curDate) {
            this.res[this.curIndex_i][this.curIndex_j].isCurrentDay = true
            this.res[this.curIndex_i][this.curIndex_j].isCurDay = false
          } else {
            this.res[i][j].isSelected = true
          }

        })
        this.chkIndex_i = i
        this.chkIndex_j = j
      }
      // if (this.selectedMode) {
      // this.$nextTick(() => {
      //   // this.$set(this.res[i][j], 'isSelected', )
      //   this.res[i][j].isSelected = !this.res[i][j].isSelected
      //   if (this.res[i][j].isSelected) {
      //     this.selectedDates.push(this.res[i][j].date)
      //     this.selectedDates = Array.from(new Set(this.selectedDates))
      //   } else {
      //     this.selectedDates.splice(this.selectedDates.indexOf(item.date), 1)
      //   }
      //   // this.$emit('dateSelected', this.selectedDates)
      //   console.log(this.selectedDates)
      // })
      // }
    },
    handleItemMove(data, i, j) {
      if (this.canMove && !this.selectedMode) {
        const index = i * 7 + j
        this.showDays.forEach(item => {
          item.isSelected = false
          item.isRangeSelected = false
        })
        // 让第一个日期和最后一个日期显示蓝色高亮
        this.showDays[index].isSelected = true
        this.showDays[this.moveIndex[0]].isSelected = true

        // 不同情况的判断，当用户的鼠标滑动进日期的索引小于起始日期的索引，要做if else处理
        if (this.moveIndex[0] < index) {
          for (let i = this.moveIndex[0] + 1; i < index; i++) {
            this.showDays[i].isRangeSelected = true
          }
        } else {
          for (let i = index + 1; i < this.moveIndex[0]; i++) {
            this.showDays[i].isRangeSelected = true
          }
        }
      }
    },
    handleQuickChange(type) {//手动点击月份按钮事件
      if (type === 'prev') {
        this.curMonth--
        if (this.curMonth === -1) {
          this.curMonth = 11
          this.curYear -= 1
        }
        this.$nextTick(() => {
          for (let i = 0; i < 7; i++) {
            const temp = this.res[i]
            for (let j = 0; j < temp.length; j++) {//获取当前日期的坐标
              if (temp[j].date === this.selectedDates) {
                this.chkIndex_i = i
                this.chkIndex_j = j
                this.res[i][j].isSelected = true
              } else {
                this.res[i][j].isSelected = false
              }
            }
            this.res[this.curIndex_i][this.curIndex_j].isCurrentDay = false
            this.res[this.curIndex_i][this.curIndex_j].isCurDay = false
          }
        })
      } else if (type === 'next') {
        this.curMonth++
        if (this.curMonth === 12) {
          this.curMonth = 0
          this.curYear += 1
        }
        this.$nextTick(() => {
          for (let i = 0; i < 7; i++) {
            const temp = this.res[i]
            for (let j = 0; j < temp.length; j++) {//获取当前日期的坐标
              if (temp[j].date === this.selectedDates) {
                this.chkIndex_i = i
                this.chkIndex_j = j
                this.res[i][j].isSelected = true
              } else {
                this.res[i][j].isSelected = false
              }
            }
            this.res[this.curIndex_i][this.curIndex_j].isCurrentDay = false
            this.res[this.curIndex_i][this.curIndex_j].isCurDay = false
          }
        })
      } else {

      }
    },
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

      setTimeout(() => {
        const myChart = this.$echarts.init(document.getElementById("mychart"));
        myChart.setOption(option);
      }, 1000)

      // const myChart = this.$echarts.init(document.getElementById("mychart"));
      // myChart.setOption(option);
      //随着屏幕大小调节图表
      // window.addEventListener("resize", () => {
      //   myChart.resize();
      // });
    },
    // 当前记录时间控件改变事件
    auntDatetChange(val, flag) {
      if (val === '') return
      if (flag === 'st') {
        const sd = Date.parse(val)
        const lastDate = new Date(this.auntForm.endDate)
        const last = Date.parse(lastDate)
        const num = sd - last
        this.realAuntForm.cycle = Math.floor(num / (1000 * 3600 * 24)) + "天"
        this.realAuntForm.startDate = parseTime(val)
      } else {
        if (this.realAuntForm.startDate === '') {
          this.$notify.error({
            title: '错误',
            message: '请选择开始时间'
          });
          this.realAuntForm.endDate = ''
          return
        }
        const sd = Date.parse(this.realAuntForm.startDate)
        this.realAuntForm.endDate = parseTime(new Date(val))
        const ed = Date.parse(this.realAuntForm.endDate)
        const num = ed - sd
        this.realAuntForm.duration = Math.floor(num / (1000 * 3600 * 24)) + "天"
      }
    },
    // 编辑当前记录
    dialogFunction() {
      if (this.auntForm.name === '') {
        this.$notify.error({
          title: '错误',
          message: '暂无该用户数据'
        });
        return
      }
      this.dialogFormVisible = true
    },
    // 保存当前记录
    handleEditAunt() {
      if (this.realAuntForm.id === '') {
        this.realAuntForm.uStatus = '1'
        _addAunt(this.realAuntForm).then(res => {
          this.$message.success("编辑成功!");
          this.dialogFormVisible = false
        })
      } else {
        _updAunt(this.realAuntForm).then(res => {
          this.$message.success("编辑成功!");
          this.dialogFormVisible = false
        })
      }
    }
  },
  mounted() {
    this.monthOptions = handleCreateDatePicker().months;
    this.yearOptions = handleCreateDatePicker().years;

    if (localStorage.selectedDates) this.selectedDates = JSON.parse(localStorage.selectedDates);
  },
  created() {
    this.init();
    this.weeks.unshift(...this.weeks.splice(this.startOfWeek - 1))
    this.handleGetDays(this.curYear, this.curMonth, this.startOfWeek)
    this.selectedMode = this.selectMode === 'click'
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
    box-shadow: 4px 4px 40px rgba(0, 0, 0, .05);
    border-color: rgba(0, 0, 0, .05);
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
  transition: .3s;

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
  color: #C0C4CC;
}

.currentDay {
  //当前日期颜色
  color: #fff;
  background-color: #409EFF;
}

.selectDay {
  //选中日期颜色
  color: rgb(0, 0, 0);
  background-color: #b2b5b9;
}

.rangeSelectd {
  //默认日期颜色
  color: #606266;
  background-color: #dee2e9;
}

.weekend {
  color: #F73131;
}

.curDay {
  color: #409EFF;
  background-color: #ffffff;
}
</style>
    