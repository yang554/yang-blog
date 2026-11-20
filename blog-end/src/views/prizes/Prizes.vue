<template>
  <div class="container">
    <div class="centered-div">
      <LuckyWheel ref="myLuckyWheel" width="650px" height="650px" :prizes="myLuckyWheel.prizes"
        :blocks="myLuckyWheel.blocks" :buttons="myLuckyWheel.buttons" @start="startLuckyWheel" @end="endLuckyWheel" />
    </div>
    <div class="record-container">
      <!-- 标题与统计 -->
      <div class="record-header">
        <h2>我的抽奖记录</h2>
        <div class="stats">
          <span class="stat-item">
            <span class="stat-number">{{ total }}</span>次参与
          </span>
          <span class="stat-item">
            <span class="stat-number">{{ winCount }}</span>次中奖
          </span>
        </div>
      </div>

      <!-- 记录列表 -->
      <div class="scroll-wrapper">
        <div class="record-list" ref="scrollContainer">
          <div v-for="(item, index) in records" :key="index" class="record-item"
            :class="[item.status, { 'new-item': item.isNew }]" @mouseenter="hoverIndex = index"
            @mouseleave="hoverIndex = -1">
            <!-- 时间轴圆点 -->
            <div class="timeline-dot"></div>

            <!-- 记录内容 -->
            <div class="record-content">
              <div class="record-main">
                <span class="prize-name">{{ item.name }}</span>
                <span class="record-time">{{ item.createTime | formatTime(item.createTime) }}</span>
              </div>
              <div class="record-meta">
                <span class="status-tag">{{ statusMap[item.status] }}</span>
                <transition name="fade">
                  <button v-if="item.status === 'unclaimed' && hoverIndex === index" class="claim-btn"
                    @click="claimPrize(index)">
                    立即领取
                  </button>
                </transition>
              </div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script>
import { _getPrizeAll, _prizeDram } from "@/api/api.js";
import { formatDate, getTime } from '@/utils/formatDate'
export default {
  data() {
    return {
      colors: ['#ec7aad', '#78bc29', '#f08300', '#fef200', '#02a0e9','#e40011'],
      isScrolling: false,
      hoverIndex: -1,
      records: [],
      statusMap: {
        claimed: "已领取",
        unclaimed: "待领取",
        none: "未中奖"
      },
      // 大转盘
      myLuckyWheel: {
        blocks: [{ padding: "31px", background: "#fff", imgs: [{ src: require('@/assets/images/zp.png'),width:"650px",rotate: true}] }],
        prizes: [],
        buttons: [
          {
            radius: "20%",
            background: "#fdd100",
            // pointer: true,
            // fonts: [{ text: "幸运大转盘", top: "-10px" }],
            imgs: [{ src: require('@/assets/images/zzheng.png'),width:"50px",top:"-180px"}]
          },
        ],
      },

    };
  },
  filters: {
    formatTime(val) {
      return formatDate(val);
    },
  },
  watch: {
    records: {
      handler() {
        this.scrollToBottom();
      },
      deep: true
    }
  },
  created() {
    this.init();
  },
  methods: {
    getRandomColor(index) {
      const colorIndex = index % this.colors.length; // 循环取颜色索引
      return this.colors[colorIndex];
    },
    scrollToBottom() {
      if (this.isScrolling) return
      this.isScrolling = true
      this.$nextTick(() => {
        setTimeout(() => {
          const container = this.$refs.scrollContainer;
          if (!container) {
            console.error('滚动容器未找到！');
            return;
          }
          const tmp = container.offsetHeight;
          container.scrollTop = tmp
          console.log("tmp",tmp,"container.scrollTop",container.scrollTop)
          setTimeout(() => {
            this.isScrolling = false
          }, 300)
        }, 300);
      })
    },
    claimPrize(index) {
      this.records[index].status = 'claimed'
      this.$set(this.records, index, { ...this.records[index] })
    },
    //初始化
    init() {
      _getPrizeAll().then((res) => {
        if (res.status != 200) return
        const prizes = []
        res.data.forEach((item, index) => {
          prizes.push({
            index: item.id,
            title: item.name,
            background: this.getRandomColor(index),
            fonts: [{ text: item.name, top: '10%' }],
          })
        })
        this.myLuckyWheel.prizes = prizes
      })
    },
    // 根据后端返回的 prize_id 找到奖品索引
    findPrizeIndex(prizeID) {
      return this.myLuckyWheel.prizes.findIndex(item => {
        if (item.index === prizeID) return item.index
      });
    },
    // 大转盘
    startLuckyWheel() {
      this.$refs.myLuckyWheel.play();
      _prizeDram().then((res) => {
        if (res.status != 200) return
        setTimeout(() => {
          const index = this.findPrizeIndex(res.data.data.id)
          this.$refs.myLuckyWheel.stop(index);
        }, 300);
      });

    },
    endLuckyWheel(prize) {
      if (prize.title === "谢谢参与") {
        this.$message.success('好运将至莫急躁，美好自会来到')
      } else {
        this.$message.error(' 恭喜你, 中奖啦, 拥抱所有好运，获得' + prize.title)
      }
      const aa = {
        id: '' + getTime("start"),
        name: prize.title,
        createTime: new Date(),
        status: "unclaimed",
        isNew: false
      }
      this.records.push(aa)
    },
  },
  computed: {
    total() {
      return this.records.length
    },
    winCount() {
      return this.records.filter(item => item.status !== 'none').length
    }
  },
  mounted() {
    this.scrollToBottom() // 初始化时滚动到底部
  }
};
</script>

<style scoped>
/* .container {} */

.centered-div {
  float: left;
  margin-left: 8rem;
}

.record-container {
  background: #eeeeee;
  float: right;
  width: 40%;
  height: 650px;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  padding: 2rem;
  display: flex;
  flex-direction: column;
}

.scroll-wrapper {
  flex: 1;
  overflow: hidden;
  position: relative;
  height: calc(100% - 10px);
  overflow-y: auto;
  padding-right: 12px;
  min-height: 0;
}

.record-header {
  border-bottom: 2px solid #f0f2f5;
  padding-bottom: 1rem;
  margin-bottom: 1.5rem;
}

h2 {
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
  font-size: 1.5rem;
}

.stats {
  display: flex;
  gap: 2rem;
}

.stat-item {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.stat-number {
  font-weight: 700;
  color: #2c3e50;
  font-size: 1.2rem;
}

.record-list {
  position: absolute;
  min-height: 100%;
  width: 100%;
  height: 100%;
  padding-left: 24px;
  padding-right: 12px;
  overflow-y: auto;
  scroll-behavior: smooth;
  transition: scroll-top 0.5s ease-in-out;
}

/* 滚动条美化 */
.scroll-wrapper::-webkit-scrollbar {
  width: 8px;
  background: rgba(245, 245, 245, 0.8);
}

.scroll-wrapper::-webkit-scrollbar-thumb {
  background: #617df2;
  border-radius: 4px;
  border: 2px solid transparent;
  background-clip: content-box;
}

.scroll-wrapper::-webkit-scrollbar-thumb:hover {
  background: #4055b5;
}

.record-item {
  position: relative;
  padding: 1.5rem 0;
  border-bottom: 1px solid #f0f2f5;
  transition: all 0.3s ease;
}

.record-item:last-child {
  border-bottom: none;
}

.timeline-dot {
  position: absolute;
  left: -31px;
  top: 50%;
  transform: translateY(-50%);
  width: 12px;
  height: 12px;
  background: #3498db;
  border-radius: 50%;
  border: 2px solid white;
  box-shadow: 0 2px 6px rgba(52, 152, 219, 0.3);
}

.record-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-main {
  flex: 1;
}

.prize-name {
  font-weight: 500;
  color: #2c3e50;
  display: block;
  margin-bottom: 0.3rem;
}

.record-time {
  font-size: 0.85rem;
  color: #95a5a6;
}

.status-tag {
  font-size: 0.8rem;
  padding: 4px 8px;
  border-radius: 12px;
  margin-right: 1rem;
}

/* 状态颜色 */
.claimed .status-tag {
  background: #e8f5e9;
  color: #2ecc71;
}

.unclaimed .status-tag {
  background: #fff3e0;
  color: #f39c12;
}

.none .status-tag {
  background: #f5f6fa;
  color: #95a5a6;
}

.claim-btn {
  background: #3498db;
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s;
}

.claim-btn:hover {
  background: #2980b9;
  transform: translateY(-1px);
}

/* 动画效果 */
.new-item {
  animation: slideIn 0.5s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(20px);
  }

  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

/* 移动端 */
@media (max-width: 600px) {
  .record-container {
    margin: 1rem;
    padding: 1rem;
  }

  .record-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .record-meta {
    margin-top: 0.5rem;
    width: 100%;
    display: flex;
    justify-content: space-between;
  }
}
</style>