<template>
  <div class="comment-management-container">
    <!-- 顶部导航栏 -->
    <header class="knowledge-header">
      <div class="header-container">
        <div class="header-left">
          <div class="logo" @click="goHome">
            <i class="el-icon-chat-line-round"></i>
            <h1>评论管理</h1>
          </div>
          <div class="header-nav">
            <el-menu :default-active="activeTab" mode="horizontal" @select="handleNavTagSelect"
              active-text-color="#409EFF">
              <el-menu-item index="all">全部评论</el-menu-item>
              <el-menu-item index="pending">待审核</el-menu-item>
              <el-menu-item index="approved">已通过</el-menu-item>
              <el-menu-item index="rejected">已拒绝</el-menu-item>
              <el-menu-item index="spam">垃圾评论</el-menu-item>
              <el-menu-item index="reported">被举报</el-menu-item>
            </el-menu>
          </div>
        </div>
        <div class="header-right">
          <div class="search-bar">
            <el-input v-model="searchKeyword" placeholder="搜索评论内容、用户..." @keyup.enter.native="handleSearch" size="medium">
              <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
            </el-input>
          </div>
          <el-dropdown @command="handleUserCommand">
            <span class="user-avatar">
              <el-avatar :size="36" :src="userAvatar"></el-avatar>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">
                <i class="el-icon-user"></i>个人中心
              </el-dropdown-item>
              <el-dropdown-item command="settings">
                <i class="el-icon-setting"></i>设置
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <div class="management-main">
      <!-- 左侧筛选面板 -->
      <div class="filter-sidebar">
        <div class="filter-section">
          <h3><i class="el-icon-s-operation"></i> 筛选条件</h3>

          <!-- 文章筛选 -->
          <div class="filter-group">
            <label>文章筛选</label>
            <el-select v-model="selectedArticle" placeholder="选择文章" size="small" clearable @change="applyFilters">
              <el-option v-for="article in articleList" :key="article.id" :label="article.title" :value="article.id">
              </el-option>
            </el-select>
          </div>

          <!-- 用户筛选 -->
          <div class="filter-group">
            <label>用户筛选</label>
            <el-select v-model="selectedUser" placeholder="选择用户" size="small" clearable @change="applyFilters">
              <el-option v-for="user in userList" :key="user.id" :label="user.name" :value="user.id">
              </el-option>
            </el-select>
          </div>

          <!-- 时间筛选 -->
          <div class="filter-group">
            <label>时间范围</label>
            <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期"
              end-placeholder="结束日期" size="small" @change="applyFilters">
            </el-date-picker>
          </div>

          <!-- 评论类型 -->
          <div class="filter-group">
            <label>评论类型</label>
            <el-checkbox-group v-model="selectedTypes" @change="applyFilters">
              <el-checkbox label="parent">主评论</el-checkbox>
              <el-checkbox label="reply">回复评论</el-checkbox>
            </el-checkbox-group>
          </div>

          <!-- 排序方式 -->
          <div class="filter-group">
            <label>排序方式</label>
            <el-radio-group v-model="sortBy" @change="applyFilters">
              <el-radio label="newest">最新</el-radio>
              <el-radio label="oldest">最旧</el-radio>
              <el-radio label="likes">点赞数</el-radio>
            </el-radio-group>
          </div>

          <!-- 操作按钮 -->
          <div class="filter-actions">
            <el-button type="primary" size="small" @click="applyFilters" style="width: 100%;">
              <i class="el-icon-search"></i> 应用筛选
            </el-button>
            <el-button type="default" size="small" @click="resetFilters" style="width: 100%; margin-top: 10px;">
              <i class="el-icon-refresh"></i> 重置筛选
            </el-button>
          </div>
        </div>

        <!-- 统计信息 -->
        <div class="stats-section">
          <h3><i class="el-icon-data-analysis"></i> 统计概览</h3>
          <div class="stats-grid">
            <div class="stat-item total">
              <div class="stat-value">{{ stats.total }}</div>
              <div class="stat-label">评论总数</div>
            </div>
            <div class="stat-item pending">
              <div class="stat-value">{{ stats.pending }}</div>
              <div class="stat-label">待审核</div>
            </div>
            <div class="stat-item approved">
              <div class="stat-value">{{ stats.approved }}</div>
              <div class="stat-label">已通过</div>
            </div>
            <div class="stat-item reported">
              <div class="stat-value">{{ stats.reported }}</div>
              <div class="stat-label">被举报</div>
            </div>
          </div>
        </div>

        <!-- 批量操作 -->
        <div class="batch-section">
          <h3><i class="el-icon-s-tools"></i> 批量操作</h3>
          <div class="batch-actions">
            <el-select v-model="batchAction" placeholder="选择操作" size="small" style="width: 100%; margin-bottom: 10px;">
              <el-option label="批量通过" value="approve"></el-option>
              <el-option label="批量拒绝" value="reject"></el-option>
              <el-option label="批量删除" value="delete"></el-option>
              <el-option label="批量标记为垃圾" value="spam"></el-option>
            </el-select>
            <el-button type="primary" size="small" @click="executeBatchAction" :disabled="selectedComments.length === 0"
              style="width: 100%;">
              执行操作
            </el-button>
          </div>
        </div>
      </div>

      <!-- 评论列表区域 -->
      <div class="comments-main">
        <!-- 列表头部 -->
        <div class="list-header">
          <div class="header-info">
            <h3>{{ getActiveTabTitle() }}</h3>
            <p class="list-count">
              共 {{ pagination.total }} 条评论
              <span v-if="selectedComments.length > 0">
                （已选择 {{ selectedComments.length }} 条）
              </span>
            </p>
          </div>
          <div class="header-actions">
            <el-button type="text" icon="el-icon-refresh" @click="refreshComments" size="small">
              刷新
            </el-button>
            <el-button type="text" icon="el-icon-download" @click="exportComments" size="small">
              导出
            </el-button>
          </div>
        </div>

        <!-- 评论列表 -->
        <div class="comments-container">
          <div v-if="loading" class="loading-state">
            <div class="skeleton-item" v-for="i in 5" :key="i">
              <div class="skeleton-avatar"></div>
              <div class="skeleton-content">
                <div class="skeleton-title"></div>
                <div class="skeleton-text"></div>
                <div class="skeleton-meta"></div>
              </div>
            </div>
          </div>

          <div v-else-if="filteredComments.length === 0" class="empty-state">
            <i class="el-icon-chat-line-square"></i>
            <p>暂无评论数据</p>
            <p class="empty-hint">尝试调整筛选条件或查看其他标签</p>
          </div>

          <div v-else class="comments-list">
            <!-- 全选操作 -->
            <div class="select-all">
              <el-checkbox v-model="selectAll" @change="handleSelectAll">
                全选本页评论
              </el-checkbox>
            </div>

            <!-- 评论列表 -->
            <div class="comment-card" v-for="comment in paginatedComments" :key="comment.id" :class="{
                'pending-comment': comment.status === 'pending',
                'reported-comment': comment.status === 'reported',
                'spam-comment': comment.status === 'spam'
              }">
              <div class="comment-header">
                <div class="comment-select">
                  <el-checkbox v-model="selectedComments" :label="comment.id"></el-checkbox>
                </div>
                <div class="comment-user">
                  <el-avatar :size="32" :src="comment.user.avatar" class="user-avatar"></el-avatar>
                  <div class="user-info">
                    <div class="user-name">{{ comment.user.name }}</div>
                    <div class="user-level">
                      <el-tag v-if="comment.user.level === 'admin'" size="mini" type="danger">管理员</el-tag>
                      <el-tag v-else-if="comment.user.level === 'vip'" size="mini" type="warning">VIP</el-tag>
                      <el-tag v-else size="mini" type="info">普通用户</el-tag>
                    </div>
                  </div>
                </div>
                <div class="comment-time">
                  <i class="el-icon-time"></i>
                  {{ formatTime(comment.createTime) }}
                </div>
                <div class="comment-status">
                  <el-tag size="mini" :type="getStatusType(comment.status)">
                    {{ getStatusText(comment.status) }}
                  </el-tag>
                </div>
              </div>

              <div class="comment-content">
                <div class="comment-text" v-html="formatComment(comment.content)"></div>
                <div class="comment-article" v-if="comment.article">
                  <span class="article-label">文章：</span>
                  <a href="javascript:;" @click="viewArticle(comment.article.id)" class="article-link">
                    {{ comment.article.title }}
                  </a>
                </div>
                <div class="comment-reply" v-if="comment.replyTo">
                  <div class="reply-header">
                    <i class="el-icon-chat-line-round"></i>
                    回复给：{{ comment.replyTo.user.name }}
                  </div>
                  <div class="reply-content">
                    {{ comment.replyTo.content | ellipsis(50) }}
                  </div>
                </div>
              </div>

              <div class="comment-meta">
                <div class="meta-stats">
                  <span class="stat-item">
                    <i class="el-icon-thumb"></i>
                    {{ comment.likes || 0 }}
                  </span>
                  <span class="stat-item">
                    <i class="el-icon-star-off"></i>
                    {{ comment.favorites || 0 }}
                  </span>
                  <span class="stat-item">
                    <i class="el-icon-warning-outline"></i>
                    {{ comment.reports || 0 }}
                  </span>
                </div>
                <div class="comment-ip">
                  <el-tag size="mini" type="info">{{ comment.ip || '未知IP' }}</el-tag>
                </div>
              </div>

              <div class="comment-actions">
                <div class="action-group">
                  <el-button v-if="comment.status === 'pending'" type="text" size="mini"
                    @click="approveComment(comment.id)" class="action-btn approve">
                    <i class="el-icon-check"></i> 通过
                  </el-button>
                  <el-button v-if="comment.status === 'pending'" type="text" size="mini"
                    @click="rejectComment(comment.id)" class="action-btn reject">
                    <i class="el-icon-close"></i> 拒绝
                  </el-button>
                  <el-button type="text" size="mini" @click="replyComment(comment)" class="action-btn reply">
                    <i class="el-icon-chat-line-round"></i> 回复
                  </el-button>
                  <el-button v-if="comment.status !== 'spam'" type="text" size="mini" @click="markAsSpam(comment.id)"
                    class="action-btn spam">
                    <i class="el-icon-warning-outline"></i> 垃圾
                  </el-button>
                  <el-button type="text" size="mini" @click="editComment(comment)" class="action-btn edit">
                    <i class="el-icon-edit"></i> 编辑
                  </el-button>
                  <el-popconfirm title="确定删除这条评论吗？" @confirm="deleteComment(comment.id)">
                    <el-button slot="reference" type="text" size="mini" class="action-btn delete">
                      <i class="el-icon-delete"></i> 删除
                    </el-button>
                  </el-popconfirm>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination-container" v-if="filteredComments.length > 0">
          <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :current-page="pagination.currentPage" :page-sizes="[10, 20, 50, 100]" :page-size="pagination.pageSize"
            layout="total, sizes, prev, pager, next, jumper" :total="filteredComments.length" background
            class="comment-pagination">
          </el-pagination>
        </div>
      </div>
    </div>

    <!-- 评论回复/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="commentDialogVisible" width="600px" @close="resetDialog">
      <el-form :model="commentForm" :rules="commentRules" ref="commentForm" label-width="80px">
        <el-form-item label="评论内容" prop="content">
          <el-input type="textarea" v-model="commentForm.content" :autosize="{ minRows: 4, maxRows: 8 }"
            placeholder="请输入评论内容..."></el-input>
        </el-form-item>
        <el-form-item label="回复给" v-if="commentForm.isReply">
          <div class="reply-to-info">
            <el-avatar :size="32" :src="commentForm.replyToUser.avatar"></el-avatar>
            <span class="reply-user-name">{{ commentForm.replyToUser.name }}</span>
          </div>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-radio-group v-model="commentForm.actionType">
            <el-radio label="reply">回复评论</el-radio>
            <el-radio label="edit">编辑评论</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="commentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitComment">确定</el-button>
      </span>
    </el-dialog>

    <!-- 评论详情对话框 -->
    <el-dialog title="评论详情" :visible.sync="detailDialogVisible" width="700px">
      <div class="comment-detail" v-if="currentComment">
        <div class="detail-header">
          <div class="user-info">
            <el-avatar :size="48" :src="currentComment.user.avatar"></el-avatar>
            <div class="user-detail">
              <h4>{{ currentComment.user.name }}</h4>
              <p class="user-meta">
                <span>IP: {{ currentComment.ip || '未知' }}</span>
                <span>等级: {{ currentComment.user.level }}</span>
                <span>注册时间: {{ formatTime(currentComment.user.registerTime) }}</span>
              </p>
            </div>
          </div>
          <div class="comment-meta-detail">
            <div class="meta-item">
              <span class="meta-label">评论时间:</span>
              <span class="meta-value">{{ formatTime(currentComment.createTime) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">状态:</span>
              <el-tag size="small" :type="getStatusType(currentComment.status)">
                {{ getStatusText(currentComment.status) }}
              </el-tag>
            </div>
            <div class="meta-item">
              <span class="meta-label">文章:</span>
              <a href="javascript:;" @click="viewArticle(currentComment.article.id)" class="article-link">
                {{ currentComment.article.title }}
              </a>
            </div>
          </div>
        </div>

        <div class="detail-content">
          <h4>评论内容</h4>
          <div class="content-box" v-html="formatComment(currentComment.content)"></div>
        </div>

        <div class="detail-stats">
          <h4>统计信息</h4>
          <div class="stats-grid">
            <div class="stat-item">
              <div class="stat-label">点赞数</div>
              <div class="stat-value">{{ currentComment.likes || 0 }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">收藏数</div>
              <div class="stat-value">{{ currentComment.favorites || 0 }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">举报数</div>
              <div class="stat-value">{{ currentComment.reports || 0 }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">回复数</div>
              <div class="stat-value">{{ currentComment.replies || 0 }}</div>
            </div>
          </div>
        </div>

        <div class="detail-history" v-if="currentComment.history && currentComment.history.length > 0">
          <h4>操作历史</h4>
          <el-timeline>
            <el-timeline-item v-for="(history, index) in currentComment.history" :key="index"
              :timestamp="formatTime(history.time)">
              {{ history.action }} - {{ history.operator }}
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'CommentManagement',
  data() {
    return {
      // 顶部导航
      activeTab: 'all',
      searchKeyword: '',
      userAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',

      // 筛选条件
      selectedArticle: null,
      selectedUser: null,
      dateRange: [],
      selectedTypes: ['parent', 'reply'],
      sortBy: 'newest',

      // 评论数据（写死的数据）
      comments: [
        {
          id: 1,
          content: '这篇文章写得真好，对我帮助很大！感谢作者的分享。',
          user: {
            id: 101,
            name: '张三',
            avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
            level: 'vip',
            registerTime: '2023-01-15 10:30:00'
          },
          article: {
            id: 1001,
            title: 'Vue.js 入门教程'
          },
          createTime: '2023-10-15 14:30:25',
          status: 'approved', // pending, approved, rejected, spam, reported
          likes: 12,
          favorites: 3,
          reports: 0,
          ip: '192.168.1.100',
          isReply: false,
          history: [
            { time: '2023-10-15 14:35:00', action: '审核通过', operator: '管理员' }
          ]
        },
        {
          id: 2,
          content: '我觉得这篇文章有些地方可以改进，比如第三部分的示例代码可以更详细一些。',
          user: {
            id: 102,
            name: '李四',
            avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
            level: 'normal',
            registerTime: '2023-03-20 09:15:00'
          },
          article: {
            id: 1002,
            title: 'React Hooks 使用指南'
          },
          createTime: '2023-10-16 09:15:42',
          status: 'pending',
          likes: 5,
          favorites: 1,
          reports: 0,
          ip: '192.168.1.101',
          isReply: false,
          history: []
        },
        {
          id: 3,
          content: '同意楼上的观点，作者可以考虑增加一些实际案例。',
          user: {
            id: 103,
            name: '王五',
            avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
            level: 'admin',
            registerTime: '2022-11-05 16:20:00'
          },
          article: {
            id: 1002,
            title: 'React Hooks 使用指南'
          },
          createTime: '2023-10-16 10:20:15',
          status: 'approved',
          likes: 8,
          favorites: 2,
          reports: 0,
          ip: '192.168.1.102',
          isReply: true,
          replyTo: {
            id: 2,
            user: { name: '李四' },
            content: '我觉得这篇文章有些地方可以改进...'
          },
          history: [
            { time: '2023-10-16 10:25:00', action: '审核通过', operator: '管理员' }
          ]
        },
        {
          id: 4,
          content: '这是垃圾广告，请管理员处理！',
          user: {
            id: 104,
            name: '广告账号',
            avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
            level: 'normal',
            registerTime: '2023-10-01 08:00:00'
          },
          article: {
            id: 1003,
            title: 'JavaScript 高级编程'
          },
          createTime: '2023-10-17 16:45:33',
          status: 'spam',
          likes: 0,
          favorites: 0,
          reports: 3,
          ip: '192.168.1.103',
          isReply: false,
          history: [
            { time: '2023-10-17 16:50:00', action: '标记为垃圾', operator: '系统' }
          ]
        },
        {
          id: 5,
          content: '这篇文章有错误信息，第5页的内容不正确。',
          user: {
            id: 105,
            name: '赵六',
            avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
            level: 'vip',
            registerTime: '2023-02-28 14:10:00'
          },
          article: {
            id: 1001,
            title: 'Vue.js 入门教程'
          },
          createTime: '2023-10-18 11:05:19',
          status: 'reported',
          likes: 2,
          favorites: 0,
          reports: 5,
          ip: '192.168.1.104',
          isReply: false,
          history: [
            { time: '2023-10-18 11:10:00', action: '被用户举报', operator: '系统' }
          ]
        },
        {
          id: 6,
          content: '非常详细的教程，适合初学者学习！',
          user: {
            id: 106,
            name: '孙七',
            avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
            level: 'normal',
            registerTime: '2023-05-10 13:45:00'
          },
          article: {
            id: 1004,
            title: 'TypeScript 实战应用'
          },
          createTime: '2023-10-19 08:30:55',
          status: 'approved',
          likes: 15,
          favorites: 4,
          reports: 0,
          ip: '192.168.1.105',
          isReply: false,
          history: [
            { time: '2023-10-19 08:35:00', action: '审核通过', operator: '管理员' }
          ]
        },
        {
          id: 7,
          content: '希望能看到更多关于性能优化的内容。',
          user: {
            id: 107,
            name: '周八',
            avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
            level: 'normal',
            registerTime: '2023-06-15 17:20:00'
          },
          article: {
            id: 1005,
            title: '前端性能优化技巧'
          },
          createTime: '2023-10-20 14:12:37',
          status: 'pending',
          likes: 3,
          favorites: 1,
          reports: 0,
          ip: '192.168.1.106',
          isReply: false,
          history: []
        },
        {
          id: 8,
          content: '作者提到的第3点非常实用，我已经在实际项目中应用了。',
          user: {
            id: 108,
            name: '吴九',
            avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
            level: 'vip',
            registerTime: '2023-04-22 11:05:00'
          },
          article: {
            id: 1005,
            title: '前端性能优化技巧'
          },
          createTime: '2023-10-20 15:45:22',
          status: 'approved',
          likes: 7,
          favorites: 2,
          reports: 0,
          ip: '192.168.1.107',
          isReply: true,
          replyTo: {
            id: 7,
            user: { name: '周八' },
            content: '希望能看到更多关于性能优化的内容。'
          },
          history: [
            { time: '2023-10-20 15:50:00', action: '审核通过', operator: '管理员' }
          ]
        }
      ],

      // 文章列表（筛选用）
      articleList: [
        { id: 1001, title: 'Vue.js 入门教程' },
        { id: 1002, title: 'React Hooks 使用指南' },
        { id: 1003, title: 'JavaScript 高级编程' },
        { id: 1004, title: 'TypeScript 实战应用' },
        { id: 1005, title: '前端性能优化技巧' }
      ],

      // 用户列表（筛选用）
      userList: [
        { id: 101, name: '张三' },
        { id: 102, name: '李四' },
        { id: 103, name: '王五' },
        { id: 104, name: '广告账号' },
        { id: 105, name: '赵六' },
        { id: 106, name: '孙七' },
        { id: 107, name: '周八' },
        { id: 108, name: '吴九' }
      ],

      // 分页
      pagination: {
        currentPage: 1,
        pageSize: 10,
        total: 0
      },

      // 加载状态
      loading: false,

      // 筛选后的评论
      filteredComments: [],

      // 选中的评论
      selectedComments: [],
      selectAll: false,

      // 批量操作
      batchAction: '',

      // 统计信息
      stats: {
        total: 0,
        pending: 0,
        approved: 0,
        reported: 0
      },

      // 对话框
      commentDialogVisible: false,
      detailDialogVisible: false,
      dialogTitle: '',

      // 当前操作的评论
      currentComment: null,

      // 评论表单
      commentForm: {
        content: '',
        isReply: false,
        replyToUser: null,
        actionType: 'reply'
      },

      // 表单验证规则
      commentRules: {
        content: [
          { required: true, message: '请输入评论内容', trigger: 'blur' },
          { min: 1, max: 1000, message: '评论内容长度在 1 到 1000 个字符', trigger: 'blur' }
        ]
      }
    };
  },

  computed: {
    // 分页后的评论
    paginatedComments() {
      const start = (this.pagination.currentPage - 1) * this.pagination.pageSize;
      const end = start + this.pagination.pageSize;
      return this.filteredComments.slice(start, end);
    }
  },

  created() {
    this.calculateStats();
    this.applyFilters();
  },

  filters: {
    ellipsis(value, length = 30) {
      if (!value) return '';
      if (value.length > length) {
        return value.slice(0, length) + '...';
      }
      return value;
    }
  },

  methods: {
    // 导航标签选择
    handleNavTagSelect(key) {
      this.activeTab = key;
      this.selectedComments = [];
      this.selectAll = false;
      this.applyFilters();
    },

    // 搜索处理
    handleSearch() {
      this.applyFilters();
      if (this.searchKeyword.trim()) {
        this.$message.info(`搜索关键词: ${this.searchKeyword}`);
      }
    },

    // 用户命令处理
    handleUserCommand(command) {
      switch (command) {
        case 'profile':
          this.$message.info('打开个人中心');
          break;
        case 'settings':
          this.$message.info('打开设置');
          break;
      }
    },

    // 返回首页
    goHome() {
      this.activeTab = 'all';
      this.resetFilters();
      this.$message.info('返回首页');
    },

    // 应用筛选条件
    applyFilters() {
      this.loading = true;

      // 模拟加载延迟
      setTimeout(() => {
        let filtered = [...this.comments];

        // 根据标签筛选
        if (this.activeTab !== 'all') {
          filtered = filtered.filter(comment => comment.status === this.activeTab);
        }

        // 根据文章筛选
        if (this.selectedArticle) {
          filtered = filtered.filter(comment => comment.article.id === this.selectedArticle);
        }

        // 根据用户筛选
        if (this.selectedUser) {
          filtered = filtered.filter(comment => comment.user.id === this.selectedUser);
        }

        // 根据时间筛选
        if (this.dateRange && this.dateRange.length === 2) {
          const startTime = new Date(this.dateRange[0]).getTime();
          const endTime = new Date(this.dateRange[1]).getTime();

          filtered = filtered.filter(comment => {
            const commentTime = new Date(comment.createTime).getTime();
            return commentTime >= startTime && commentTime <= endTime;
          });
        }

        // 根据类型筛选
        if (this.selectedTypes.length === 1) {
          if (this.selectedTypes[0] === 'parent') {
            filtered = filtered.filter(comment => !comment.isReply);
          } else if (this.selectedTypes[0] === 'reply') {
            filtered = filtered.filter(comment => comment.isReply);
          }
        }

        // 根据搜索关键词筛选
        if (this.searchKeyword.trim()) {
          const keyword = this.searchKeyword.toLowerCase();
          filtered = filtered.filter(comment =>
            comment.content.toLowerCase().includes(keyword) ||
            comment.user.name.toLowerCase().includes(keyword) ||
            comment.article.title.toLowerCase().includes(keyword)
          );
        }

        // 排序
        if (this.sortBy === 'newest') {
          filtered.sort((a, b) => new Date(b.createTime) - new Date(a.createTime));
        } else if (this.sortBy === 'oldest') {
          filtered.sort((a, b) => new Date(a.createTime) - new Date(b.createTime));
        } else if (this.sortBy === 'likes') {
          filtered.sort((a, b) => (b.likes || 0) - (a.likes || 0));
        }

        this.filteredComments = filtered;
        this.pagination.total = filtered.length;
        this.pagination.currentPage = 1;

        this.loading = false;
      }, 500);
    },

    // 重置筛选条件
    resetFilters() {
      this.selectedArticle = null;
      this.selectedUser = null;
      this.dateRange = [];
      this.selectedTypes = ['parent', 'reply'];
      this.sortBy = 'newest';
      this.searchKeyword = '';
      this.selectedComments = [];
      this.selectAll = false;
      this.applyFilters();
    },

    // 计算统计信息
    calculateStats() {
      this.stats.total = this.comments.length;
      this.stats.pending = this.comments.filter(c => c.status === 'pending').length;
      this.stats.approved = this.comments.filter(c => c.status === 'approved').length;
      this.stats.reported = this.comments.filter(c => c.status === 'reported').length;
    },

    // 获取当前标签标题
    getActiveTabTitle() {
      const titles = {
        all: '全部评论',
        pending: '待审核评论',
        approved: '已通过评论',
        rejected: '已拒绝评论',
        spam: '垃圾评论',
        reported: '被举报评论'
      };
      return titles[this.activeTab] || '评论管理';
    },

    // 获取状态类型
    getStatusType(status) {
      const types = {
        pending: 'warning',
        approved: 'success',
        rejected: 'danger',
        spam: 'info',
        reported: 'danger'
      };
      return types[status] || 'info';
    },

    // 获取状态文本
    getStatusText(status) {
      const texts = {
        pending: '待审核',
        approved: '已通过',
        rejected: '已拒绝',
        spam: '垃圾评论',
        reported: '被举报'
      };
      return texts[status] || '未知';
    },

    // 格式化时间
    formatTime(timeStr) {
      if (!timeStr) return '';
      const date = new Date(timeStr);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    },

    // 格式化评论内容（处理换行和链接）
    formatComment(content) {
      if (!content) return '';
      // 处理换行
      let formatted = content.replace(/\n/g, '<br>');
      // 简单处理URL（这里只是简单示例，实际可以使用更复杂的URL检测）
      formatted = formatted.replace(/(https?:\/\/[^\s]+)/g, '<a href="$1" target="_blank">$1</a>');
      return formatted;
    },

    // 刷新评论
    refreshComments() {
      this.loading = true;
      this.selectedComments = [];
      this.selectAll = false;

      // 模拟API调用延迟
      setTimeout(() => {
        this.calculateStats();
        this.applyFilters();
        this.$message.success('评论列表已刷新');
      }, 800);
    },

    // 导出评论
    exportComments() {
      this.$message.info('导出功能开发中...');
    },

    // 查看文章
    viewArticle(articleId) {
      this.$message.info(`查看文章 ID: ${articleId}`);
      // 实际项目中可以跳转到文章页面
    },

    // 全选操作
    handleSelectAll(checked) {
      if (checked) {
        this.selectedComments = this.paginatedComments.map(comment => comment.id);
      } else {
        this.selectedComments = [];
      }
    },

    // 执行批量操作
    executeBatchAction() {
      if (this.selectedComments.length === 0) {
        this.$message.warning('请先选择评论');
        return;
      }

      if (!this.batchAction) {
        this.$message.warning('请选择批量操作类型');
        return;
      }

      const actionTexts = {
        approve: '通过',
        reject: '拒绝',
        delete: '删除',
        spam: '标记为垃圾'
      };

      this.$confirm(`确定要${actionTexts[this.batchAction]}选中的 ${this.selectedComments.length} 条评论吗？`, '批量操作确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        // 模拟批量操作
        this.selectedComments.forEach(commentId => {
          const commentIndex = this.comments.findIndex(c => c.id === commentId);
          if (commentIndex !== -1) {
            if (this.batchAction === 'delete') {
              this.comments.splice(commentIndex, 1);
            } else {
              this.comments[commentIndex].status = this.batchAction === 'approve' ? 'approved' :
                this.batchAction === 'reject' ? 'rejected' :
                  this.batchAction === 'spam' ? 'spam' :
                    this.comments[commentIndex].status;

              // 添加操作历史
              this.comments[commentIndex].history.push({
                time: new Date().toISOString().replace('T', ' ').substring(0, 19),
                action: `${actionTexts[this.batchAction]}评论`,
                operator: '管理员'
              });
            }
          }
        });

        this.calculateStats();
        this.applyFilters();
        this.selectedComments = [];
        this.batchAction = '';
        this.selectAll = false;

        this.$message.success(`批量操作完成，成功${actionTexts[this.batchAction]} ${this.selectedComments.length} 条评论`);
      }).catch(() => {
        this.$message.info('已取消批量操作');
      });
    },

    // 分页处理
    handleSizeChange(val) {
      this.pagination.pageSize = val;
      this.pagination.currentPage = 1;
    },

    handleCurrentChange(val) {
      this.pagination.currentPage = val;
      this.selectedComments = [];
      this.selectAll = false;
    },

    // 单个评论操作
    approveComment(commentId) {
      const commentIndex = this.comments.findIndex(c => c.id === commentId);
      if (commentIndex !== -1) {
        this.comments[commentIndex].status = 'approved';
        this.comments[commentIndex].history.push({
          time: new Date().toISOString().replace('T', ' ').substring(0, 19),
          action: '审核通过',
          operator: '管理员'
        });

        this.calculateStats();
        this.applyFilters();
        this.$message.success('评论已通过审核');
      }
    },

    rejectComment(commentId) {
      const commentIndex = this.comments.findIndex(c => c.id === commentId);
      if (commentIndex !== -1) {
        this.comments[commentIndex].status = 'rejected';
        this.comments[commentIndex].history.push({
          time: new Date().toISOString().replace('T', ' ').substring(0, 19),
          action: '审核拒绝',
          operator: '管理员'
        });

        this.calculateStats();
        this.applyFilters();
        this.$message.success('评论已拒绝');
      }
    },

    markAsSpam(commentId) {
      const commentIndex = this.comments.findIndex(c => c.id === commentId);
      if (commentIndex !== -1) {
        this.comments[commentIndex].status = 'spam';
        this.comments[commentIndex].history.push({
          time: new Date().toISOString().replace('T', ' ').substring(0, 19),
          action: '标记为垃圾',
          operator: '管理员'
        });

        this.calculateStats();
        this.applyFilters();
        this.$message.success('评论已标记为垃圾');
      }
    },

    deleteComment(commentId) {
      const commentIndex = this.comments.findIndex(c => c.id === commentId);
      if (commentIndex !== -1) {
        this.comments.splice(commentIndex, 1);
        this.calculateStats();
        this.applyFilters();
        this.$message.success('评论已删除');
      }
    },

    // 回复评论
    replyComment(comment) {
      this.currentComment = comment;
      this.dialogTitle = '回复评论';
      this.commentForm = {
        content: '',
        isReply: true,
        replyToUser: comment.user,
        actionType: 'reply'
      };
      this.commentDialogVisible = true;
    },

    // 编辑评论
    editComment(comment) {
      this.currentComment = comment;
      this.dialogTitle = '编辑评论';
      this.commentForm = {
        content: comment.content,
        isReply: false,
        replyToUser: null,
        actionType: 'edit'
      };
      this.commentDialogVisible = true;
    },

    // 重置对话框
    resetDialog() {
      this.currentComment = null;
      this.commentForm = {
        content: '',
        isReply: false,
        replyToUser: null,
        actionType: 'reply'
      };
      if (this.$refs.commentForm) {
        this.$refs.commentForm.resetFields();
      }
    },

    // 提交评论（回复或编辑）
    submitComment() {
      this.$refs.commentForm.validate((valid) => {
        if (valid) {
          if (this.commentForm.actionType === 'reply') {
            // 模拟回复操作
            const newComment = {
              id: Date.now(),
              content: this.commentForm.content,
              user: {
                id: 999,
                name: '管理员',
                avatar: this.userAvatar,
                level: 'admin',
                registerTime: '2023-01-01 00:00:00'
              },
              article: this.currentComment.article,
              createTime: new Date().toISOString().replace('T', ' ').substring(0, 19),
              status: 'approved',
              likes: 0,
              favorites: 0,
              reports: 0,
              ip: '192.168.1.200',
              isReply: true,
              replyTo: {
                id: this.currentComment.id,
                user: { name: this.currentComment.user.name },
                content: this.currentComment.content
              },
              history: [
                { time: new Date().toISOString().replace('T', ' ').substring(0, 19), action: '管理员回复', operator: '管理员' }
              ]
            };

            this.comments.unshift(newComment);
            this.$message.success('回复已提交');
          } else if (this.commentForm.actionType === 'edit') {
            // 模拟编辑操作
            const commentIndex = this.comments.findIndex(c => c.id === this.currentComment.id);
            if (commentIndex !== -1) {
              this.comments[commentIndex].content = this.commentForm.content;
              this.comments[commentIndex].history.push({
                time: new Date().toISOString().replace('T', ' ').substring(0, 19),
                action: '内容编辑',
                operator: '管理员'
              });

              this.$message.success('评论已编辑');
            }
          }

          this.calculateStats();
          this.applyFilters();
          this.commentDialogVisible = false;
        }
      });
    },

    // 查看评论详情
    viewCommentDetail(comment) {
      this.currentComment = comment;
      this.detailDialogVisible = true;
    }
  }
};
</script>

<style scoped>
/* 使用与之前页面一致的顶部导航栏样式 */
.knowledge-header {
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
  color: white;
  padding: 0;
  box-shadow: 0 4px 20px rgba(15, 23, 42, 0.15);
  margin-bottom: 20px;
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 40px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo i {
  font-size: 28px;
  color: #60a5fa;
}

.logo h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: white;
}

/* Element UI菜单样式覆盖 */
.header-nav .el-menu {
  background: transparent;
  border-bottom: none;
}

.header-nav .el-menu-item {
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
  height: 70px;
  line-height: 70px;
  border-bottom: 3px solid transparent;
}

.header-nav .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.05);
  color: white;
}

.header-nav .el-menu-item.is-active {
  color: white;
  border-bottom-color: #60a5fa;
  background-color: rgba(255, 255, 255, 0.05);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-bar {
  width: 300px;
}

.user-avatar {
  cursor: pointer;
  transition: opacity 0.3s;
}

.user-avatar:hover {
  opacity: 0.9;
}

/* 主内容区 */
.comment-management-container {
  background-color: #f5f7fa;
  min-height: calc(100vh - 100px);
  padding: 0 0 20px 0;
}

.management-main {
  display: flex;
  gap: 20px;
}

/* 左侧筛选面板 */
.filter-sidebar {
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-section,
.stats-section,
.batch-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  padding: 20px;
}

.filter-section h3,
.stats-section h3,
.batch-section h3 {
  margin: 0 0 20px 0;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-group {
  margin-bottom: 20px;
}

.filter-group label {
  display: block;
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
  font-weight: 500;
}

.filter-actions {
  margin-top: 25px;
}

/* 统计信息 */
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.stat-item {
  text-align: center;
  padding: 15px 10px;
  border-radius: 8px;
  background-color: #f8f9fa;
  transition: transform 0.3s;
}

.stat-item:hover {
  transform: translateY(-3px);
}

.stat-item.total {
  border-top: 3px solid #409EFF;
}

.stat-item.pending {
  border-top: 3px solid #E6A23C;
}

.stat-item.approved {
  border-top: 3px solid #67C23A;
}

.stat-item.reported {
  border-top: 3px solid #F56C6C;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  line-height: 1;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

/* 评论列表区域 */
.comments-main {
  flex: 1;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
}

.list-header {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-info h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #303133;
  font-weight: 600;
}

.list-count {
  margin: 0;
  font-size: 13px;
  color: #909399;
}

.header-actions {
  display: flex;
  gap: 10px;
}

/* 评论列表容器 */
.comments-container {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.skeleton-item {
  display: flex;
  gap: 15px;
  padding: 20px;
  border-radius: 8px;
  background: #fafafa;
}

.skeleton-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f0f0f0;
  animation: pulse 1.5s ease-in-out infinite;
}

.skeleton-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.skeleton-title {
  height: 16px;
  width: 30%;
  background: #f0f0f0;
  border-radius: 4px;
  animation: pulse 1.5s ease-in-out infinite;
}

.skeleton-text {
  height: 16px;
  width: 80%;
  background: #f0f0f0;
  border-radius: 4px;
  animation: pulse 1.5s ease-in-out infinite;
}

.skeleton-meta {
  height: 12px;
  width: 50%;
  background: #f0f0f0;
  border-radius: 4px;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {

  0%,
  100% {
    opacity: 1;
  }

  50% {
    opacity: 0.5;
  }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-state i {
  font-size: 60px;
  color: #c0c4cc;
  margin-bottom: 20px;
}

.empty-state p {
  color: #909399;
  font-size: 16px;
  margin-bottom: 10px;
}

.empty-hint {
  font-size: 14px;
  color: #c0c4cc;
}

/* 评论列表 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.select-all {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 10px;
}

.comment-card {
  border-radius: 10px;
  border: 1px solid #e4e7ed;
  padding: 20px;
  transition: all 0.3s;
}

.comment-card:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.pending-comment {
  border-left: 4px solid #E6A23C;
  background-color: rgba(230, 162, 60, 0.05);
}

.reported-comment {
  border-left: 4px solid #F56C6C;
  background-color: rgba(245, 108, 108, 0.05);
}

.spam-comment {
  border-left: 4px solid #909399;
  background-color: rgba(144, 147, 153, 0.05);
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.comment-select {
  margin-right: 15px;
}

.comment-user {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  flex-shrink: 0;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.comment-time {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 5px;
  margin-right: 20px;
}

.comment-status {
  flex-shrink: 0;
}

/* 评论内容 */
.comment-content {
  margin-bottom: 15px;
}

.comment-text {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin-bottom: 15px;
}

.comment-text>>>a {
  color: #409EFF;
  text-decoration: none;
}

.comment-text>>>a:hover {
  text-decoration: underline;
}

.comment-article {
  font-size: 13px;
  color: #909399;
  margin-bottom: 10px;
}

.article-label {
  font-weight: 500;
}

.article-link {
  color: #409EFF;
  text-decoration: none;
}

.article-link:hover {
  text-decoration: underline;
}

.comment-reply {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 12px;
  margin-top: 10px;
  border-left: 3px solid #e4e7ed;
}

.reply-header {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 5px;
  margin-bottom: 8px;
}

.reply-content {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

/* 评论元数据 */
.comment-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.meta-stats {
  display: flex;
  gap: 15px;
}

.stat-item {
  font-size: 12px;
  color: #909399;
  display: flex;
  align-items: center;
  gap: 5px;
}

/* 评论操作 */
.comment-actions {
  display: flex;
  justify-content: flex-end;
}

.action-group {
  display: flex;
  gap: 10px;
}

.action-btn {
  padding: 4px 8px;
  font-size: 12px;
  transition: all 0.3s;
}

.action-btn.approve {
  color: #67C23A;
}

.action-btn.approve:hover {
  color: #67C23A;
  background-color: rgba(103, 194, 58, 0.1);
}

.action-btn.reject {
  color: #F56C6C;
}

.action-btn.reject:hover {
  color: #F56C6C;
  background-color: rgba(245, 108, 108, 0.1);
}

.action-btn.reply {
  color: #409EFF;
}

.action-btn.reply:hover {
  color: #409EFF;
  background-color: rgba(64, 158, 255, 0.1);
}

.action-btn.spam {
  color: #909399;
}

.action-btn.spam:hover {
  color: #909399;
  background-color: rgba(144, 147, 153, 0.1);
}

.action-btn.edit {
  color: #E6A23C;
}

.action-btn.edit:hover {
  color: #E6A23C;
  background-color: rgba(230, 162, 60, 0.1);
}

.action-btn.delete {
  color: #F56C6C;
}

.action-btn.delete:hover {
  color: #F56C6C;
  background-color: rgba(245, 108, 108, 0.1);
}

/* 分页 */
.pagination-container {
  padding: 20px;
  border-top: 1px solid #f0f0f0;
}

.comment-pagination {
  justify-content: center;
}

/* 对话框样式 */
.reply-to-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.reply-user-name {
  font-weight: 500;
  color: #303133;
}

/* 评论详情样式 */
.comment-detail {
  padding: 10px 0;
}

.detail-header {
  display: flex;
  gap: 20px;
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.user-info {
  display: flex;
  gap: 15px;
  flex: 1;
}

.user-detail h4 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #303133;
}

.user-meta {
  margin: 0;
  font-size: 13px;
  color: #909399;
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.comment-meta-detail {
  flex: 1;
}

.meta-item {
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.meta-label {
  font-size: 14px;
  color: #606266;
  min-width: 60px;
  margin-right: 10px;
}

.meta-value {
  font-size: 14px;
  color: #303133;
}

.detail-content {
  margin-bottom: 25px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-content h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #303133;
}

.content-box {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}

.detail-stats h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #303133;
}

.detail-stats .stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}

.detail-stats .stat-item {
  text-align: center;
  padding: 15px;
  border-radius: 8px;
  background-color: #f8f9fa;
}

.detail-stats .stat-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.detail-stats .stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
}

.detail-history {
  margin-top: 25px;
}

.detail-history h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #303133;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .management-main {
    flex-direction: column;
  }

  .filter-sidebar {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
  }

  .filter-section,
  .stats-section,
  .batch-section {
    flex: 1;
    min-width: 300px;
  }
}

@media (max-width: 768px) {
  .header-container {
    flex-direction: column;
    height: auto;
    padding: 15px;
  }

  .header-left {
    flex-direction: column;
    gap: 15px;
    width: 100%;
  }

  .header-nav {
    width: 100%;
  }

  .header-nav .el-menu {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
  }

  .header-right {
    width: 100%;
    justify-content: center;
    margin-top: 15px;
  }

  .search-bar {
    width: 100%;
  }

  .detail-header {
    flex-direction: column;
  }

  .comment-header {
    flex-wrap: wrap;
  }

  .comment-time {
    margin-right: 0;
    margin-top: 10px;
    width: 100%;
  }

  .action-group {
    flex-wrap: wrap;
  }
}
</style>