<template>
  <div>
    <h1>女生的姨妈周期可视化页面</h1>
    <div>
      <label for="start-date">姨妈开始日期：</label>
      <input type="date" id="start-date" v-model="startDate" @change="calculateNextPeriodDate">
    </div>
    <div>
      <label for="cycle-length">周期长度（天）：</label>
      <input type="number" id="cycle-length" v-model="cycleLength" @change="calculateNextPeriodDate">
    </div>
    <div>
      <h2>下次姨妈来临情况：</h2>
      <p>日期：{{ nextPeriodDate }}</p>
      <p>日历：</p>
      <calendar :events="events"></calendar>
    </div>
  </div>
</template>

<script>
import Calendar from 'vue-calendar-component';

export default {
  components: {
    Calendar
  },
  data() {
    return {
      startDate: '',
      cycleLength: '',
      nextPeriodDate: '',
      events: []
    };
  },
  methods: {
    calculateNextPeriodDate() {
      // 根据姨妈开始日期和周期长度计算下次姨妈来临的日期
      // 这里只是一个简单的示例，你可以根据实际需求进行计算
      const startDate = new Date(this.startDate);
      const cycleLength = parseInt(this.cycleLength);
      const nextPeriodDate = new Date(startDate.getTime() + cycleLength * 24 * 60 * 60 * 1000);

      // 更新下次姨妈来临的日期和日历事件
      this.nextPeriodDate = nextPeriodDate.toDateString();
      this.events = [{
        date: nextPeriodDate,
        title: '姨妈来临'
      }];
    }
  }
};
</script>