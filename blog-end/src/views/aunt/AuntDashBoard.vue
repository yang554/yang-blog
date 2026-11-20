<template>
  <div class="dashboard-container">
    <!-- 用户选择区域 -->
    <el-card class="user-select-card" shadow="hover">
      <div class="user-select-group">
        <div class="user-select-input">
          <el-select v-model="uID" filterable placeholder="请选择用户" size="medium" class="user-select"
            popper-class="tech-select-dropdown">
            <el-option v-for="(item, index) in uNameAll" :key="index" :label="item.uName" :value="item.uID">
              <div class="user-option">
                <i class="el-icon-user"></i>
                <span>{{ item.uName }}</span>
              </div>
            </el-option>
          </el-select>
        </div>
        <el-button type="primary" size="medium" @click="selectByNameAllFun" class="search-btn">
          <i class="el-icon-search"></i> 加载数据
        </el-button>
      </div>
    </el-card>

    <!-- 数据面板区域 -->
    <div class="data-panels">
      <el-row :gutter="20">
        <!-- 上次记录面板 -->
        <el-col :xs="24" :lg="8">
          <el-card class="data-panel tech-panel" shadow="hover">
            <div class="panel-header">
              <i class="el-icon-time"></i>
              <h3>上次记录</h3>
            </div>
            <el-descriptions class="tech-descriptions" :column="1" border>
              <el-descriptions-item label="姓名">{{ auntForm.name }}</el-descriptions-item>
              <el-descriptions-item label="周期长度">{{ auntForm.cycle }}</el-descriptions-item>
              <el-descriptions-item label="开始日期">{{ auntForm.startDate }}</el-descriptions-item>
              <el-descriptions-item label="结束日期">{{ auntForm.endDate }}</el-descriptions-item>
              <el-descriptions-item label="持续时间">{{ auntForm.duration }}</el-descriptions-item>
              <el-descriptions-item label="血量">{{ auntForm.bloodVolume }}ml</el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>

        <!-- 预测信息面板 -->
        <el-col :xs="24" :lg="8">
          <el-card class="data-panel tech-panel" shadow="hover">
            <div class="panel-header">
              <i class="el-icon-odometer"></i>
              <h3>{{ this.nextAuntForm.msg }}</h3>
            </div>
            <el-descriptions class="tech-descriptions" :column="1" border>
              <el-descriptions-item label="姓名">{{ auntForm.name }}</el-descriptions-item>
              <el-descriptions-item label="周期长度">{{ nextAuntForm.cycleLength }}天</el-descriptions-item>
              <el-descriptions-item label="开始日期">{{ nextAuntForm.nextPeriodStartDate }}</el-descriptions-item>
              <el-descriptions-item label="结束日期">{{ nextAuntForm.nextPeriodEndDate }}</el-descriptions-item>
              <el-descriptions-item label="持续时间">{{ nextAuntForm.periodLength }}天</el-descriptions-item>
              <el-descriptions-item label="距离开始还有">
                <span class="highlight">{{ nextAuntForm.daysUntilNextPeriod }}天</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>

        <!-- 本次记录面板 -->
        <el-col :xs="24" :lg="8">
          <el-card class="data-panel tech-panel" shadow="hover">
            <div class="panel-header">
              <i class="el-icon-edit"></i>
              <h3>本次实际记录</h3>
              <el-button type="primary" @click="dialogFunction" size="small" class="edit-btn">
                <i class="el-icon-edit"></i> 编辑
              </el-button>
            </div>
            <el-descriptions class="tech-descriptions" :column="1" border>
              <el-descriptions-item label="姓名">{{ auntForm.name}}</el-descriptions-item>
              <el-descriptions-item label="周期长度">{{ realAuntForm.cycle }}</el-descriptions-item>
              <el-descriptions-item label="开始日期">{{ realAuntForm.startDate }}</el-descriptions-item>
              <el-descriptions-item label="结束日期">{{ realAuntForm.endDate }}</el-descriptions-item>
              <el-descriptions-item label="持续时间">{{ realAuntForm.duration }}</el-descriptions-item>
              <el-descriptions-item label="血量">{{ realAuntForm.bloodVolume }}ml</el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表和日历区域 -->
    <div class="visualization-section">
      <el-card class="chart-card tech-panel" shadow="hover">
        <div class="chart-header">
          <h3><i class="el-icon-data-line"></i> 月例-流血量分析</h3>
          <div class="chart-controls">
            <el-form inline>
              <el-form-item>
                <el-select v-model="curYear" placeholder="年份" style="width: 100px;" size="small" class="tech-select">
                  <el-option v-for="item in yearOptions" :key="item.key" :value="item.value" :label="item.label" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-select v-model="curMonth" placeholder="月份" style="width: 100px;" size="small" class="tech-select">
                  <el-option v-for="item in monthOptions" :key="item.key" :value="item.value" :label="item.label" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button-group>
                  <el-button type="primary" @click="handleQuickChange('prev')" size="small" class="tech-btn">
                    <i class="el-icon-arrow-left"></i> 上一月
                  </el-button>
                  <el-button type="primary" @click="handleQuickChange('next')" size="small" class="tech-btn">
                    下一月 <i class="el-icon-arrow-right"></i>
                  </el-button>
                  <el-button type="success" @click="handleCreateEvent()" size="small" class="tech-btn">
                    <i class="el-icon-plus"></i> 创建事件
                  </el-button>
                </el-button-group>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <div class="visualization-content">
          <!-- 图表区域 -->
          <div class="chart-container">
            <div class="echart" v-show="isShowContent" id="mychart"></div>
            <div class="echart" v-show="this.isEmpty">
              <el-empty description="暂无该用户数据"></el-empty>
            </div>
          </div>
          <!-- 日历区域 -->
          <div class="calendar-container">
            <div class="calendar-wrapper">
              <div class="calendar-header">
                <div class="calendar-title">{{ calendarTitle }}</div>
                <div class="calendar-actions">
                  <el-button circle size="mini" @click="handleQuickChange('prev')">
                    <i class="el-icon-arrow-left"></i>
                  </el-button>
                  <el-button circle size="mini" @click="handleQuickChange('today')">
                    今
                  </el-button>
                  <el-button circle size="mini" @click="handleQuickChange('next')">
                    <i class="el-icon-arrow-right"></i>
                  </el-button>
                </div>
              </div>

              <div class="weekdays">
                <div v-for="(item, i) in weeks" :key="i" class="weekday" :class="{ weekend: i === 0 || i === 6 }">
                  {{ item }}
                </div>
              </div>

              <div class="days-grid">
                <div v-for="(item, index) in showDays" :key="index" class="day-cell" :class="{
                    'prev-month': item.flag === 'prev',
                    'next-month': item.flag === 'rear',
                    'current-day': item.isCurrentDay,
                    'selected-day': item.isSelected,
                    'today': item.isToday,
                    'weekend': item.isWeekend,
                    'has-event': item.hasEvent
                  }" @click="handleItemClick(item, Math.floor(index / 7), index % 7)">
                  <div class="date-info">
                    <span class="day-number">
                      {{ item.day }}
                    </span>
                    <span v-if="item.isToday" class="today-badge">今</span>
                  </div>
                  <div class="lunar-day">{{ item.nl }}</div>
                  <!-- <div v-if="item.hasEvent" class="event-indicator">
                    <i class="el-icon-collection"></i>
                  </div> -->
                </div>
              </div>

              <div class="calendar-footer">
                <div class="legend">
                  <div class="legend-item">
                    <div class="legend-icon today"></div>
                    <span>今日</span>
                  </div>
                  <div class="legend-item">
                    <div class="legend-icon weekend"></div>
                    <span>周末</span>
                  </div>
                  <div class="legend-item">
                    <div class="legend-icon event"></div>
                    <span>有记录</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog :title="'记录编辑 - ' + realAuntForm.name" @click.native.stop :visible.sync="dialogFormVisible" width="800px"
      class="tech-dialog">
      <el-form v-model="realAuntForm" label-position="top">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="realAuntForm.name" autocomplete="off" disabled></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="周期长度">
              <el-input v-model="realAuntForm.cycle" autocomplete="off" disabled></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="开始日期">
              <el-date-picker v-model="realAuntForm.startDate" type="date" :picker-options="pickerOptions"
                placeholder="选择开始时间" @change="auntDatetChange(realAuntForm.startDate, 'st')" class="tech-date-picker">
              </el-date-picker>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="结束日期">
              <el-date-picker v-model="realAuntForm.endDate" type="date" :picker-options="pickerOptions"
                placeholder="选择结束时间" @change="auntDatetChange(realAuntForm.endDate, 'ed')" class="tech-date-picker">
              </el-date-picker>
            </el-form-item>
          </el-col>

          <el-col :span="4">
            <el-form-item label="持续时间">
              <el-input v-model="realAuntForm.duration" autocomplete="off" disabled></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="4">
            <el-form-item label="血量(ml)">
              <el-input v-model="realAuntForm.bloodVolume" autocomplete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="症状">
              <el-input type="textarea" :rows="4" placeholder="记录每个月姨妈期间出现的症状，如腹痛、乳房胀痛、情绪波动等"
                v-model="realAuntForm.symptom" class="tech-textarea">
              </el-input>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="备注">
              <el-input type="textarea" :rows="4" placeholder="可以记录一些额外的信息，如服用的药物、健康状况等。" v-model="realAuntForm.note"
                class="tech-textarea">
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" class="tech-btn-cancel">取 消</el-button>
        <el-button type="primary" @click="handleEditAunt" class="tech-btn-save">保 存</el-button>
      </div>
    </el-dialog>
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
        id: '',
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
        uID: '',
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
        nextPeriodStartDate: '',  //下次开始时间
        msg: '',   //系统建议
      },
      uNameAll: [],    //所有用户
      uName: '',   //查询的用户名称
      uID: '', //查询的用户ID
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
  computed: {
    calendarTitle() {
      const months = ['一月', '二月', '三月', '四月', '五月', '六月',
        '七月', '八月', '九月', '十月', '十一月', '十二月'];
      return `${this.curYear}年 ${months[this.curMonth]}`;
    }
  },
  methods: {
    init() {
      //获取所有登记用户名字
      _getAuntNameAll().then(res => {
        this.uNameAll = res.data.data
        if (this.$store.state.login_user.roles[0].name === 'admin' && this.uNameAll != null) {
          this.uName = this.uNameAll[0].uName
          this.uID = this.uNameAll[0].uID
        }
        for (let i = 0; i < this.uNameAll.length; i++) {
          const element = this.uNameAll[i];
          this.uNameAll.forEach(key => {
            if (key.uID == this.$store.state.login_user.id) {
              this.uName = key.uName
              this.uID = key.uID
            }
          })
        }
        this.selectByNameAllFun()
      })
    },
    //根据名字查询所有记录
    selectByNameAllFun() {
      if (this.uID === "") {
        const num = 1000 * 3600 * 24 * 35
        const d = Date.now() - num
        this.$notify.error({
          title: '错误',
          message: '请选择查询用户'
        });
        return
      }
      this.xData = []
      this.yData = []
      _getAuntByNameAll(this.uID, 0).then(res => { //历史记录
        if (res.data == "") {
          this.isEmpty = true
        } else {
          this.isEmpty = false
          res.data.auntList.forEach(element => {
            this.xData.push(element.startDate + "~" + element.endDate)
            this.yData.push(element.bloodVolume)
          });
          this.nextAuntForm.daysUntilNextPeriod = res.data.daysUntilNextPeriod
          this.nextAuntForm.nextPeriodEndDate = res.data.nextPeriodEndDate
          this.nextAuntForm.nextPeriodStartDate = res.data.nextPeriodStartDate
          this.nextAuntForm.cycleLength = res.data.cycleLength
          this.nextAuntForm.periodLength = res.data.periodLength
          this.nextAuntForm.msg = '根据' + res.data.msg + '预测下次经期情况'


          this.auntForm.name = res.data.auntList[0].uName
          this.auntForm.startDate = res.data.auntList[0].startDate
          this.auntForm.endDate = res.data.auntList[0].endDate
          this.auntForm.duration = res.data.auntList[0].duration
          this.auntForm.cycle = res.data.auntList[0].cycle
          this.auntForm.bloodVolume = res.data.auntList[0].bloodVolume

          this.isShowContent = true
        }

      })

      _getAuntByNameAll(this.uID, 1).then(res => { //实际记录
        if (res.data === "") {
          this.realAuntForm.id = ''
          this.realAuntForm.uID = this.uID
          this.realAuntForm.name = this.auntForm.name
          this.realAuntForm.startDate = ''
          this.realAuntForm.endDate = ''
          this.realAuntForm.duration = ''
          this.realAuntForm.cycle = ''
          this.realAuntForm.bloodVolume = ''
          this.realAuntForm.symptom = ''
          this.realAuntForm.note = ''
        } else {
          this.realAuntForm.id = res.data.auntList[0].id
          this.realAuntForm.name = res.data.auntList[0].uName
          this.realAuntForm.startDate = res.data.auntList[0].startDate
          this.realAuntForm.endDate = res.data.auntList[0].endDate
          this.realAuntForm.duration = res.data.auntList[0].duration
          this.realAuntForm.cycle = res.data.auntList[0].cycle
          this.realAuntForm.bloodVolume = res.data.auntList[0].bloodVolume
          this.realAuntForm.symptom = res.data.auntList[0].symptom
          this.realAuntForm.note = res.data.auntList[0].note
        }

      })

      setTimeout(() => {
        this.initEcharts();
      }, 100)
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
      // 标记今天
      const today = parseTime(new Date().getTime());
      this.curDays.forEach(day => {
        day.isToday = day.date === today;
        // 模拟事件标记 - 实际应用中应根据数据设置
        // day.hasEvent = Math.random() > 0.7;
      });

      this.showDays.unshift(...this.prevDays)
      this.showDays.push(...this.curDays)
      this.showDays.push(...this.rearDays)

      // 标记周末
      this.showDays.forEach((day, idx) => {
        const dayOfWeek = idx % 7;
        day.isWeekend = dayOfWeek === 0 || dayOfWeek === 6;
      });
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
          // 保持选中状态
          if (temp[j].date === this.selectedDates) {
            temp[j].isSelected = true;
            this.chkIndex_i = i;
            this.chkIndex_j = j;
          }
        }
        arr2.push(temp)
      }
      // 确保今天高亮
      if (arr2[this.curIndex_i] && arr2[this.curIndex_i][this.curIndex_j]) {
        arr2[this.curIndex_i][this.curIndex_j].isCurrentDay = true;
      }
      if (this.chkIndex_i == -1) {
        this.chkIndex_i = this.curIndex_i
        this.chkIndex_j = this.curIndex_j
      }
      arr2[this.chkIndex_i][this.chkIndex_j].isSelected = true;

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
      const aa = { "name": this.uID, "date": day }
      this.$router.push("/home/aunt/CreateEven/" + JSON.stringify(aa))
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
      } else if (type === 'next') {
        this.curMonth++
        if (this.curMonth === 12) {
          this.curMonth = 0
          this.curYear += 1
        }
      } else {
        const today = new Date();
        this.curYear = today.getFullYear();
        this.curMonth = today.getMonth();
        this.chkIndex_i = this.curIndex_i
        this.chkIndex_j = this.curIndex_j
      }
      // 重新生成日历数据，会保持选中的日期
      this.handleGetDays(this.curYear, this.curMonth, this.startOfWeek);
    },
    initEcharts() {
      if (this.isEmpty) return
      // 基本柱状图
      const option = {
        title: {
          text: '月例-流血量',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        xAxis: {
          data: this.xData
        },
        yAxis: {},
        series: [
          {
            name: '日期范围',
            type: "bar", //形状为柱状图
            data: this.yData,
            tooltip: {
              valueFormatter: function (value) {
                return value + ' ml';
              }
            },
          }
        ]
      };

      setTimeout(() => {
        const myChart = this.$echarts.init(document.getElementById("mychart"));
        myChart.setOption(option);
      }, 500)
    },
    // 当前记录时间控件改变事件
    auntDatetChange(val, flag) {
      if (val === '') return
      if (flag === 'st') {
        const sd = Date.parse(val)
        const lastDate = new Date(this.auntForm.startDate)
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
      this.$store.state.date = new Date().getTime()
      // this.$router.go(0);
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

<style lang="scss">
.dashboard-container {
  // max-width: 1600px;
  margin: 0 auto;
  padding: 5px;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background: linear-gradient(135deg, #0f1a30 0%, #1a2a4f 100%);
  color: #e0f0ff;
  min-height: 100vh;
}

.user-select-card {
  background: rgba(16, 32, 64, 0.6);
  border: 1px solid rgba(64, 158, 255, 0.2);
  border-radius: 12px;
  margin-bottom: 25px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);

  .el-card__body {
    padding: 20px;
  }
}

.user-select-group {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-select {
  flex: 1;

  .el-input__inner {
    background: rgba(10, 25, 50, 0.7);
    border: 1px solid rgba(64, 158, 255, 0.3);
    color: #0084ff;
    border-radius: 8px;
    height: 42px;
    transition: all 0.3s;

    &:focus {
      border-color: #409eff;
      box-shadow: 0 0 10px rgba(64, 158, 255, 0.5);
    }
  }
}

.search-btn {
  background: linear-gradient(135deg, #409eff, #337ecc);
  border: none;
  border-radius: 8px;
  padding: 10px 25px;
  font-weight: 500;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(64, 158, 255, 0.3);

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);
  }
}

.data-panels {
  margin-bottom: 30px;
}

.tech-panel {
  background: rgba(16, 32, 64, 0.6);
  border: 1px solid rgba(64, 158, 255, 0.2);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  transition: all 0.4s ease;
  height: 100%;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 40px rgba(64, 158, 255, 0.25);
  }

  .el-card__body {
    padding: 20px;
  }
}

.panel-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(64, 158, 255, 0.2);

  i {
    font-size: 24px;
    color: #409eff;
    margin-right: 12px;
  }

  h3 {
    font-size: 1.3rem;
    font-weight: 500;
    color: rgb(153, 153, 153);
    flex: 1;
  }
}

.tech-descriptions {
  .el-descriptions__header {
    margin-bottom: 15px;
  }

  .el-descriptions__title {
    color: #a0c4ff;
    font-weight: 400;
  }

  .el-descriptions__body {
    background: transparent;

    th {
      background: rgba(10, 25, 50, 0.7);
      color: #a0c4ff;
      border: 1px solid rgba(64, 158, 255, 0.2);
    }

    td {
      background: rgba(20, 40, 80, 0.4);
      color: #e0f0ff;
      border: 1px solid rgba(64, 158, 255, 0.2);
    }
  }
}

.highlight {
  color: #67e0e6;
  font-weight: bold;
  text-shadow: 0 0 8px rgba(103, 224, 230, 0.6);
}

.visualization-section {
  margin-top: 30px;
}

.chart-card {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid rgba(92, 128, 255, 0.15);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);

  .el-card__body {
    padding: 0;
  }
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid rgba(64, 158, 255, 0.2);

  h3 {
    font-size: 1.3rem;
    font-weight: 500;
    color: #fff;
    display: flex;
    align-items: center;
    gap: 10px;

    i {
      color: #ff7b89;
      font-size: 1.4rem;
    }
  }
}

.chart-controls {
  .el-form-item {
    margin-bottom: 0;
    margin-right: 10px;
  }
}

.tech-select {
  .el-input__inner {
    background: rgba(10, 25, 50, 0.7);
    border: 1px solid rgba(64, 158, 255, 0.3);
    color: #e0f0ff;
    border-radius: 6px;
    transition: all 0.3s;

    &:focus {
      border-color: #409eff;
      box-shadow: 0 0 8px rgba(64, 158, 255, 0.5);
    }
  }
}

.tech-btn {
  background: rgba(10, 25, 50, 0.7);
  border: 1px solid rgba(64, 158, 255, 0.3);
  color: #e0f0ff;
  border-radius: 6px;
  transition: all 0.3s;

  &:hover {
    background: rgba(20, 40, 80, 0.9);
    border-color: #409eff;
    transform: translateY(-2px);
  }
}

.visualization-content {
  display: flex;
  height: 550px;
}

.chart-container {
  flex: 1;
  padding: 20px;

  .echart {
    width: 100%;
    height: 100%;
    background: rgba(10, 25, 50, 0.3);
    border-radius: 8px;
    border: 1px solid rgba(64, 158, 255, 0.2);
  }
}

.calendar-container {
  width: 30%;
  padding: 20px;
}

.calendar-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: rgba(20, 28, 50, 0.6);
  border-radius: 12px;
  border: 1px solid rgba(92, 128, 255, 0.1);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
  padding: 15px;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(92, 128, 255, 0.15);
}

.calendar-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #e6f0ff;
  letter-spacing: 0.5px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.calendar-actions {
  display: flex;
  gap: 8px;

  .el-button {
    width: 32px;
    height: 32px;
    padding: 0;
    background: rgba(15, 23, 42, 0.7);
    border: 1px solid rgba(92, 128, 255, 0.2);
    color: #a0b9ff;
    border-radius: 50%;
    transition: all 0.3s;

    &:hover {
      background: rgba(30, 45, 80, 0.8);
      border-color: #5c80ff;
      color: #d6e4ff;
      transform: translateY(-2px);
      box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
    }
  }
}

.weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 10px;
  text-align: center;
  font-weight: 500;
}

.weekday {
  font-size: 13px;
  color: #8a9cc9;
  padding: 8px 0;

  &.weekend {
    color: #ff7b89;
  }
}

.days-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-gap: 6px;
  flex: 1;
}

.day-cell {
  position: relative;
  border-radius: 10px;
  padding: 10px 5px;
  min-height: 75px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  cursor: pointer;
  transition: all 0.2s ease;
  background: rgba(25, 33, 55, 0.5);
  border: 1px solid rgba(92, 128, 255, 0.1);

  &:hover {
    background: rgba(40, 55, 90, 0.4);
    transform: translateY(-2px);
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
    border-color: rgba(92, 128, 255, 0.3);
  }

  .date-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
  }

  .day-number {
    font-size: 15px;
    font-weight: 500;
    color: #d6e4ff;
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    z-index: 2;
  }

  .today-badge {
    font-size: 10px;
    background: #ff7b89;
    color: white;
    padding: 2px 6px;
    border-radius: 10px;
    font-weight: 500;
    position: absolute;
    top: -6px;
    right: -5px;
    z-index: 3;
  }

  .lunar-day {
    font-size: 11px;
    color: #8a9cc9;
    text-align: center;
    margin-top: 4px;
  }

  .event-indicator {
    position: absolute;
    bottom: 6px;
    right: 6px;
    width: 20px;
    height: 20px;
    background: #ff7b89;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2;

    i {
      color: white;
      font-size: 10px;
    }
  }
}

/* 日期状态样式 */
.prev-month {

  .day-number,
  .lunar-day {
    color: #4a5a7a !important;
    opacity: 0.5;
  }

  &:hover {

    .day-number,
    .lunar-day {
      color: #6a7ca0 !important;
    }
  }
}

.next-month {

  .day-number,
  .lunar-day {
    color: #4a5a7a !important;
    opacity: 0.5;
  }

  &:hover {

    .day-number,
    .lunar-day {
      color: #6a7ca0 !important;
    }
  }
}

.weekend {
  .day-number {
    color: #ff7b89;
  }
}

.today {
  background: linear-gradient(135deg, rgba(255, 123, 137, 0.25), rgba(220, 70, 90, 0.25));
  border-color: rgba(255, 123, 137, 0.3);

  .day-number {
    background: #ff7b89;
    color: white !important;
    font-weight: 600;
  }

  .lunar-day {
    color: #ffb0b9 !important;
  }
}

.selected-day {
  border: 2px solid #5c80ff;
  box-shadow: 0 0 0 2px rgba(92, 128, 255, 0.2);
}

.has-event {
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 8px;
    right: 8px;
    width: 6px;
    height: 6px;
    background: #ff7b89;
    border-radius: 50%;
    z-index: 1;
  }
}

.calendar-footer {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid rgba(92, 128, 255, 0.1);
}

.legend {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #8a9cc9;

  .legend-icon {
    width: 14px;
    height: 14px;
    border-radius: 4px;

    &.today {
      background: #ff7b89;
      border-radius: 50%;
    }

    &.weekend {
      background: #ff7b89;
      opacity: 0.7;
    }

    &.event {
      background: #ff7b89;
    }
  }
}


.tech-calendar {
  height: 100%;
  background: rgba(10, 25, 50, 0.3);
  border-radius: 8px;
  border: 1px solid rgba(64, 158, 255, 0.2);
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.25);
}

.calendar-table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
  font-size: 14px;

  thead tr {
    height: 40px;
    background: rgba(20, 40, 80, 0.7);

    th {
      color: #a0c4ff;
      font-weight: 400;
      border: 1px solid rgba(64, 158, 255, 0.2);
    }
  }

  tbody tr {
    td {
      height: 70px;
      position: relative;
      border: 1px solid rgba(64, 158, 255, 0.1);
      transition: all 0.3s;
      cursor: pointer;

      &:hover {
        background: rgba(64, 158, 255, 0.15);
      }

      .day-number {
        display: block;
        font-size: 16px;
        font-weight: 500;
        margin-bottom: 3px;
      }

      .lunar-day {
        display: block;
        font-size: 12px;
        color: #a0c4ff;
      }
    }
  }
}

/* 日历样式 */
.notCurMonth {
  color: #4a6580;
}

.currentDay {
  background: linear-gradient(135deg, rgba(64, 158, 255, 0.7), rgba(58, 119, 255, 0.7));
  color: #fff !important;

  .lunar-day {
    color: #e0f0ff !important;
  }
}

.selectDay {
  background: rgba(103, 224, 230, 0.2);
  color: #67e0e6 !important;

  .lunar-day {
    color: #67e0e6 !important;
  }
}

.rangeSelectd {
  background: rgba(103, 224, 230, 0.1);
}

.weekend {
  color: #ff7b89;

  .lunar-day {
    color: #ff7b89;
  }
}

.curDay {
  color: #67e0e6;
}

.tech-dialog {
  border-radius: 12px;
  overflow: hidden;
  background: rgba(16, 32, 64, 0.9);
  border: 1px solid rgba(64, 158, 255, 0.3);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);

  .el-dialog__header {
    background: linear-gradient(90deg, rgba(20, 40, 80, 0.9), rgba(10, 25, 50, 0.9));
    padding: 20px;
    border-bottom: 1px solid rgba(64, 158, 255, 0.2);

    .el-dialog__title {
      color: #fff;
      font-size: 1.4rem;
      font-weight: 500;
    }
  }

  .el-dialog__body {
    padding: 30px;
    background: rgba(10, 25, 50, 0.7);
  }

  .el-form-item__label {
    color: #a0c4ff;
  }

  .el-input__inner,
  .el-textarea__inner {
    background: rgba(20, 40, 80, 0.4);
    border: 1px solid rgba(64, 158, 255, 0.2);
    color: #e0f0ff;
    border-radius: 6px;

    &:focus {
      border-color: #409eff;
      box-shadow: 0 0 8px rgba(64, 158, 255, 0.5);
    }
  }
}

.tech-btn-cancel {
  background: rgba(20, 40, 80, 0.4);
  border: 1px solid rgba(64, 158, 255, 0.2);
  color: #e0f0ff;
  border-radius: 6px;

  &:hover {
    background: rgba(30, 60, 120, 0.6);
    border-color: #409eff;
  }
}

.tech-btn-save {
  background: linear-gradient(135deg, #409eff, #337ecc);
  border: none;
  border-radius: 6px;
  color: #fff;

  &:hover {
    background: linear-gradient(135deg, #337ecc, #2b6cb0);
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(64, 158, 255, 0.4);
  }
}

@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .visualization-content {
    flex-direction: column;
    height: auto;
  }

  .calendar-container {
    width: 100%;
    margin-top: 20px;
    border-left: none;
    border-top: 1px solid rgba(92, 128, 255, 0.1);
  }
}

@media (max-width: 768px) {
  .chart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .chart-controls {
    width: 100%;
    flex-wrap: wrap;
  }

  .days-grid {
    grid-gap: 4px;
  }

  .day-cell {
    min-height: 65px;
    padding: 8px 3px;

    .day-number {
      font-size: 13px;
      width: 20px;
      height: 20px;
    }

    .lunar-day {
      font-size: 10px;
    }

    .today-badge {
      font-size: 9px;
      padding: 1px 4px;
    }
  }

  .legend {
    flex-wrap: wrap;
    justify-content: flex-start;
    gap: 12px;
  }
}
</style>