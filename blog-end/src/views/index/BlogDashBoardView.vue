<template>
  <div class="article-dashboard">
    <!-- 顶部关键指标卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon blue">
          <i class="el-icon-document"></i>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalArticles }}</div>
          <div class="stat-label">总文章数</div>
        </div>
        <div class="stat-trend up">
          <i class="el-icon-top"></i> {{ stats.weeklyGrowth }}%
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon green">
          <i class="el-icon-view"></i>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ formatNumber(stats.totalViews) }}</div>
          <div class="stat-label">总浏览量</div>
        </div>
        <div class="stat-trend up">
          <i class="el-icon-top"></i> {{ stats.viewGrowth }}%
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon orange">
          <i class="el-icon-star-on"></i>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalLikes }}</div>
          <div class="stat-label">总点赞数</div>
        </div>
        <div class="stat-trend up">
          <i class="el-icon-top"></i> {{ stats.likeGrowth }}%
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon purple">
          <i class="el-icon-chat-line-round"></i>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalComments }}</div>
          <div class="stat-label">总评论数</div>
        </div>
        <div class="stat-trend down">
          <i class="el-icon-bottom"></i> {{ stats.commentGrowth }}%
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-row">
      <!-- 近7天访问趋势 -->
      <div class="chart-card">
        <div class="chart-header">
          <span class="chart-title">
            <i class="el-icon-data-line"></i>
            近7天访问趋势
          </span>
          <el-radio-group v-model="trendType" size="small">
            <el-radio-button label="views">浏览量</el-radio-button>
            <el-radio-button label="visitors">访客数</el-radio-button>
          </el-radio-group>
        </div>
        <div class="chart-container">
          <div ref="trendChart" class="chart"></div>
        </div>
      </div>

      <!-- 文章分类分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <span class="chart-title">
            <i class="el-icon-pie-chart"></i>
            文章分类分布
          </span>
          <span class="chart-sub">按文章数量</span>
        </div>
        <div class="chart-container">
          <div ref="categoryChart" class="chart"></div>
        </div>
      </div>
    </div>

    <div class="charts-row">
      <!-- 热门文章排行榜 -->
      <div class="chart-card hot-articles">
        <div class="chart-header">
          <span class="chart-title">
            <i class="el-icon-medal"></i>
            热门文章 TOP10
          </span>
          <el-button type="text" size="small" @click="viewAllArticles">
            查看全部 <i class="el-icon-arrow-right"></i>
          </el-button>
        </div>
        <div class="hot-list">
          <div v-for="(article, index) in hotArticles" :key="article.id" class="hot-item">
            <div class="hot-rank" :class="getRankClass(index)">
              {{ index + 1 }}
            </div>
            <div class="hot-info">
              <div class="hot-title">{{ article.title }}</div>
              <div class="hot-meta">
                <span><i class="el-icon-view"></i> {{ formatNumber(article.views) }}</span>
                <span><i class="el-icon-star-on"></i> {{ article.likes }}</span>
                <span><i class="el-icon-chat-line-round"></i> {{ article.comments }}</span>
              </div>
            </div>
            <div class="hot-category">
              <el-tag size="mini" :type="getCategoryType(article.category)">
                {{ article.category }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 最近更新 & 待办 -->
      <div class="right-panel">
        <!-- 最近更新 -->
        <div class="chart-card recent-updates">
          <div class="chart-header">
            <span class="chart-title">
              <i class="el-icon-time"></i>
              最近更新
            </span>
            <span class="chart-sub">最新5篇</span>
          </div>
          <div class="update-list">
            <div v-for="article in recentArticles" :key="article.id" class="update-item">
              <div class="update-title">{{ article.title }}</div>
              <div class="update-time">{{ formatDate(article.updateTime) }}</div>
            </div>
          </div>
        </div>

        <!-- 待办事项 -->
        <div class="chart-card todo-list">
          <div class="chart-header">
            <span class="chart-title">
              <i class="el-icon-edit-outline"></i>
              待办事项
            </span>
            <el-button type="text" size="small" @click="addTodo">
              + 新建
            </el-button>
          </div>
          <div class="todo-items">
            <div v-for="todo in todos" :key="todo.id" class="todo-item">
              <el-checkbox v-model="todo.done" @change="updateTodo(todo)">
                {{ todo.content }}
              </el-checkbox>
              <span class="todo-time">{{ todo.deadline }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'ArticleDashboard',
  data() {
    return {
      trendType: 'views',
      stats: {
        totalArticles: 156,
        weeklyGrowth: 12,
        totalViews: 258000,
        viewGrowth: 8,
        totalLikes: 15680,
        likeGrowth: 5,
        totalComments: 3420,
        commentGrowth: -2
      },
      hotArticles: [
        { id: 1, title: 'Vue3 响应式原理深度解析', views: 12500, likes: 456, comments: 89, category: '前端技术' },
        { id: 2, title: '2024 年最佳后端框架对比', views: 9800, likes: 234, comments: 56, category: '后端技术' },
        { id: 3, title: 'AI 时代程序员如何保持竞争力', views: 8700, likes: 567, comments: 123, category: '行业观察' },
        { id: 4, title: 'Git 高级技巧与团队协作', views: 7600, likes: 189, comments: 34, category: '开发工具' },
        { id: 5, title: '微服务架构设计模式', views: 6500, likes: 234, comments: 67, category: '架构设计' },
        { id: 6, title: '前端性能优化完全指南', views: 5900, likes: 178, comments: 45, category: '前端技术' },
        { id: 7, title: 'Docker 与 K8s 实战', views: 5200, likes: 145, comments: 32, category: 'DevOps' },
        { id: 8, title: '数据库索引优化最佳实践', views: 4800, likes: 123, comments: 28, category: '数据库' },
        { id: 9, title: 'TypeScript 泛型详解', views: 4200, likes: 98, comments: 21, category: '前端技术' },
        { id: 10, title: '软件测试自动化实践', views: 3800, likes: 87, comments: 19, category: '质量保障' }
      ],
      recentArticles: [
        { id: 1, title: 'Vue3 响应式原理深度解析', updateTime: '2024-01-15 14:30' },
        { id: 2, title: '2024 年最佳后端框架对比', updateTime: '2024-01-14 10:20' },
        { id: 3, title: 'AI 时代程序员如何保持竞争力', updateTime: '2024-01-13 16:45' },
        { id: 4, title: 'Git 高级技巧与团队协作', updateTime: '2024-01-12 09:15' },
        { id: 5, title: '微服务架构设计模式', updateTime: '2024-01-11 11:00' }
      ],
      todos: [
        { id: 1, content: '完成 Vue3 响应式原理文章', done: false, deadline: '今日' },
        { id: 2, content: '审核待发布文章', done: false, deadline: '明日' },
        { id: 3, content: '优化移动端适配样式', done: true, deadline: '昨日' },
        { id: 4, content: '回复读者评论', done: false, deadline: '本周' }
      ],
      charts: {
        trend: null,
        category: null
      },
      categoryData: [
        { name: '前端技术', value: 45, color: '#5470c6' },
        { name: '后端技术', value: 32, color: '#fac858' },
        { name: '架构设计', value: 28, color: '#ee6666' },
        { name: '行业观察', value: 18, color: '#73c0de' },
        { name: '开发工具', value: 15, color: '#3ba272' },
        { name: '数据库', value: 12, color: '#fc8452' },
        { name: 'DevOps', value: 6, color: '#9a60b4' }
      ]
    }
  },
  mounted() {
    this.initCharts()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    Object.values(this.charts).forEach(chart => {
      chart && chart.dispose()
    })
  },
  methods: {
    initCharts() {
      this.initTrendChart()
      this.initCategoryChart()
    },
    initTrendChart() {
      const chart = echarts.init(this.$refs.trendChart)
      this.charts.trend = chart
      
      const option = {
        tooltip: { trigger: 'axis' },
        grid: { top: 30, left: 50, right: 30, bottom: 20, containLabel: true },
        xAxis: {
          type: 'category',
          data: ['01-07', '01-08', '01-09', '01-10', '01-11', '01-12', '01-13'],
          axisLabel: { fontSize: 11 }
        },
        yAxis: { type: 'value', name: this.trendType === 'views' ? '浏览量' : '访客数' },
        series: [{
          data: [3200, 3800, 4200, 5800, 6200, 7100, 8500],
          type: 'line',
          smooth: true,
          symbol: 'circle',
          symbolSize: 8,
          lineStyle: { width: 3, color: '#5470c6' },
          areaStyle: { opacity: 0.1, color: '#5470c6' },
          itemStyle: { color: '#5470c6' }
        }]
      }
      chart.setOption(option)
      
      this.$watch('trendType', () => {
        const newOption = { ...option }
        newOption.yAxis.name = this.trendType === 'views' ? '浏览量' : '访客数'
        chart.setOption(newOption)
      })
    },
    initCategoryChart() {
      const chart = echarts.init(this.$refs.categoryChart)
      this.charts.category = chart
      
      const option = {
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', left: 'left', top: 'center' },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 8, borderColor: '#fff', borderWidth: 2 },
          label: { show: true, formatter: '{b}: {d}%' },
          emphasis: { scale: true },
          data: this.categoryData.map(item => ({ name: item.name, value: item.value, itemStyle: { color: item.color } }))
        }]
      }
      chart.setOption(option)
    },
    formatNumber(num) {
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + 'w'
      }
      return num.toString()
    },
    formatDate(dateStr) {
      const date = new Date(dateStr)
      return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${date.getMinutes()}`
    },
    getRankClass(index) {
      if (index === 0) return 'rank-1'
      if (index === 1) return 'rank-2'
      if (index === 2) return 'rank-3'
      return ''
    },
    getCategoryType(category) {
      const typeMap = {
        '前端技术': 'primary',
        '后端技术': 'success',
        '架构设计': 'warning',
        '行业观察': 'info'
      }
      return typeMap[category] || ''
    },
    viewAllArticles() {
      this.$router && this.$router.push('/articles')
      this.$message && this.$message.info('跳转到文章管理')
    },
    addTodo() {
      this.$message && this.$message.info('新建待办功能开发中')
    },
    updateTodo(todo) {
      this.$message && this.$message.success(`待办 "${todo.content}" 已${todo.done ? '完成' : '重新打开'}`)
    },
    handleResize() {
      Object.values(this.charts).forEach(chart => {
        chart && chart.resize()
      })
    }
  }
}
</script>

<style lang="less" scoped>
.article-dashboard {
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;

  // 统计卡片行
  .stats-row {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 20px;

    @media (max-width: 1200px) {
      grid-template-columns: repeat(2, 1fr);
    }
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }

    .stat-card {
      background: #fff;
      border-radius: 12px;
      padding: 20px;
      display: flex;
      align-items: center;
      position: relative;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      transition: all 0.3s;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
      }

      .stat-icon {
        width: 54px;
        height: 54px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 28px;
        margin-right: 16px;
        
        &.blue { background: #e8f4ff; color: #409eff; }
        &.green { background: #e8f9f0; color: #67c23a; }
        &.orange { background: #fff3e0; color: #e6a23c; }
        &.purple { background: #f4e8ff; color: #9b59b6; }
      }

      .stat-info {
        flex: 1;
        
        .stat-value {
          font-size: 28px;
          font-weight: 700;
          color: #1f2f3d;
          line-height: 1.2;
        }
        
        .stat-label {
          font-size: 14px;
          color: #909399;
          margin-top: 4px;
        }
      }

      .stat-trend {
        font-size: 12px;
        padding: 4px 8px;
        border-radius: 20px;
        
        &.up {
          background: #e8f9f0;
          color: #67c23a;
        }
        
        &.down {
          background: #ffe8e8;
          color: #f56c6c;
        }
        
        i {
          font-size: 10px;
        }
      }
    }
  }

  // 图表行
  .charts-row {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    margin-bottom: 20px;

    @media (max-width: 1200px) {
      grid-template-columns: 1fr;
    }
  }

  // 图表卡片
  .chart-card {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    
    .chart-header {
      padding: 16px 20px;
      border-bottom: 1px solid #f0f2f5;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .chart-title {
        font-size: 16px;
        font-weight: 600;
        color: #1f2f3d;
        
        i {
          margin-right: 8px;
          color: #409eff;
        }
      }
      
      .chart-sub {
        font-size: 12px;
        color: #909399;
      }
    }
    
    .chart-container {
      padding: 20px;
      
      .chart {
        height: 320px;
        width: 100%;
      }
    }
  }

  // 热门文章列表
  .hot-articles {
    .hot-list {
      max-height: 500px;
      overflow-y: auto;
      
      .hot-item {
        display: flex;
        align-items: center;
        padding: 14px 20px;
        border-bottom: 1px solid #f5f7fa;
        transition: background 0.2s;
        
        &:hover {
          background: #fafbfc;
        }
        
        .hot-rank {
          width: 28px;
          height: 28px;
          border-radius: 6px;
          background: #f5f7fa;
          display: flex;
          align-items: center;
          justify-content: center;
          font-weight: 600;
          margin-right: 16px;
          
          &.rank-1 {
            background: linear-gradient(135deg, #ffd700, #ffb347);
            color: #fff;
          }
          &.rank-2 {
            background: linear-gradient(135deg, #c0c0c0, #a8a8a8);
            color: #fff;
          }
          &.rank-3 {
            background: linear-gradient(135deg, #cd7f32, #b87333);
            color: #fff;
          }
        }
        
        .hot-info {
          flex: 1;
          
          .hot-title {
            font-weight: 500;
            margin-bottom: 6px;
            color: #1f2f3d;
          }
          
          .hot-meta {
            font-size: 12px;
            color: #909399;
            
            span {
              margin-right: 16px;
              
              i {
                margin-right: 4px;
              }
            }
          }
        }
        
        .hot-category {
          margin-left: 12px;
        }
      }
    }
  }

  // 右侧面板
  .right-panel {
    display: flex;
    flex-direction: column;
    gap: 20px;
    
    .recent-updates {
      .update-list {
        .update-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 14px 20px;
          border-bottom: 1px solid #f5f7fa;
          
          .update-title {
            font-size: 14px;
            color: #1f2f3d;
          }
          
          .update-time {
            font-size: 12px;
            color: #909399;
          }
        }
      }
    }
    
    .todo-list {
      .todo-items {
        .todo-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 14px 20px;
          border-bottom: 1px solid #f5f7fa;
          
          .todo-time {
            font-size: 12px;
            color: #e6a23c;
          }
        }
      }
    }
  }
}

// 滚动条美化
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}
::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
  &:hover {
    background: #a8a8a8;
  }
}
</style>