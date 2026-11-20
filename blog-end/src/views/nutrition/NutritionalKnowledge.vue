<template>
    <div id="app" class="nutrition-knowledge">
        <!-- 顶部导航 -->
        <header class="knowledge-header">
            <div class="header-container">
                <div class="header-left">
                    <div class="logo" @click="goHome">
                        <i class="el-icon-reading"></i>
                        <h1>营养知识库</h1>
                    </div>
                    <div class="header-nav">
                        <el-menu :default-active="activeNav" mode="horizontal" @select="handleNavSelect"
                            active-text-color="#1FAAEB">
                            <el-menu-item index="home">首页</el-menu-item>
                            <el-menu-item index="articles">营养文章</el-menu-item>
                            <el-menu-item index="videos">营养视频</el-menu-item>
                            <el-menu-item index="recipes">健康食谱</el-menu-item>
                            <el-menu-item index="tools">营养工具</el-menu-item>
                        </el-menu>
                    </div>
                </div>
                <div class="header-right">
                    <div class="search-bar">
                        <el-input v-model="searchKeyword" placeholder="搜索营养知识..." @keyup.enter="handleSearch" size="medium">
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
                            <el-dropdown-item command="bookmarks">
                                <i class="el-icon-collection"></i>我的收藏
                            </el-dropdown-item>
                            <el-dropdown-item command="history">
                                <i class="el-icon-time"></i>浏览历史
                            </el-dropdown-item>
                            <el-dropdown-item command="settings" divided>
                                <i class="el-icon-setting"></i>设置
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown>
                </div>
            </div>
        </header>
        <!-- 主内容区 -->
        <main class="knowledge-main">
            <div class="main-container">
                <!-- 左侧边栏 -->
                <aside class="knowledge-sidebar">
                    <!-- 分类导航 -->
                    <div class="category-section">
                        <h3 class="sidebar-title">
                            <i class="el-icon-menu"></i> 知识分类
                        </h3>
                        <div class="category-list">
                            <div v-for="category in categories" :key="category.id" class="category-item"
                                :class="{ active: currentCategory && currentCategory.id === category.id }"
                                @click="selectCategory(category)">
                                <div class="category-icon">
                                    <i :class="category.icon"></i>
                                </div>
                                <div class="category-content">
                                    <div class="category-name">{{ category.name }}</div>
                                    <div class="category-count">{{ category.count }}篇</div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 热门标签 -->
                    <div class="tags-section">
                        <h3 class="sidebar-title">
                            <i class="el-icon-collection-tag"></i> 热门标签
                        </h3>
                        <div class="tags-list">
                            <el-tag v-for="tag in hotTags" :key="tag.id" :type="tag.type" size="medium" class="tag-item"
                                @click="selectTag(tag)">
                                {{ tag.name }}
                            </el-tag>
                        </div>
                    </div>

                    <!-- 今日推荐 -->
                    <div class="recommend-section">
                        <h3 class="sidebar-title">
                            <i class="el-icon-star-on"></i> 今日推荐
                        </h3>
                        <div class="recommend-list">
                            <div v-for="item in dailyRecommendations" :key="item.id" class="recommend-item"
                                @click="viewContent(item)">
                                <div class="recommend-image">
                                    <img :src="item.image" :alt="item.title" />
                                    <div class="recommend-type" :class="item.type">
                                        {{ item.type === 'article' ? '文章' : item.type === 'video' ? '视频' : '食谱' }}
                                    </div>
                                </div>
                                <div class="recommend-content">
                                    <h4 class="recommend-title">{{ item.title }}</h4>
                                    <div class="recommend-meta">
                                        <span class="meta-item">
                                            <i class="el-icon-view"></i> {{ item.views }}
                                        </span>
                                        <span class="meta-item">
                                            <i class="el-icon-time"></i> {{ item.duration || item.readTime }}
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </aside>

                <!-- 主要内容 -->
                <div class="knowledge-content">
                    <!-- 首页特别内容 -->
                    <div v-if="activeNav === 'home'" class="home-content">
                        <!-- 轮播图 -->
                        <div class="carousel-section">
                            <el-carousel height="360px" :interval="5000" arrow="always">
                                <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
                                    <div class="carousel-item" :style="{ backgroundImage: `url(${item.image})` }">
                                        <div class="carousel-overlay">
                                            <div class="carousel-content">
                                                <span class="carousel-category">{{ item.category }}</span>
                                                <h3 class="carousel-title">{{ item.title }}</h3>
                                                <p class="carousel-desc">{{ item.description }}</p>
                                                <el-button type="primary" size="medium" @click="viewContent(item)">
                                                    立即阅读
                                                </el-button>
                                            </div>
                                        </div>
                                    </div>
                                </el-carousel-item>
                            </el-carousel>
                        </div>

                        <!-- 特色专题 -->
                        <div class="featured-section">
                            <div class="section-header">
                                <h2 class="section-title">
                                    <i class="el-icon-s-operation"></i> 特色专题
                                </h2>
                                <el-button type="text" @click="viewAllTopics">查看全部专题</el-button>
                            </div>
                            <div class="topics-grid">
                                <div v-for="topic in featuredTopics" :key="topic.id" class="topic-card"
                                    @click="viewTopic(topic)">
                                    <div class="topic-image">
                                        <img :src="topic.image" :alt="topic.title" />
                                    </div>
                                    <div class="topic-content">
                                        <h4 class="topic-title">{{ topic.title }}</h4>
                                        <p class="topic-desc">{{ topic.description }}</p>
                                        <div class="topic-meta">
                                            <span>{{ topic.articleCount }}篇文章</span>
                                            <span>{{ topic.followers }}人关注</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 最新内容 -->
                        <div class="latest-section">
                            <div class="section-header">
                                <h2 class="section-title">
                                    <i class="el-icon-refresh"></i> 最新内容
                                </h2>
                                <el-radio-group v-model="latestFilter" size="small">
                                    <el-radio-button label="all">全部</el-radio-button>
                                    <el-radio-button label="article">文章</el-radio-button>
                                    <el-radio-button label="video">视频</el-radio-button>
                                    <el-radio-button label="recipe">食谱</el-radio-button>
                                </el-radio-group>
                            </div>
                            <div class="latest-content">
                                <div class="content-tabs">
                                    <el-tabs v-model="latestActiveTab">
                                        <el-tab-pane label="推荐" name="recommend">
                                            <div class="content-list">
                                                <div v-for="item in latestContent.recommend" :key="item.id"
                                                    class="content-card" @click="viewContent(item)">
                                                    <div class="content-image">
                                                        <img :src="item.image" :alt="item.title" />
                                                        <div class="content-type">{{ getContentType(item.type) }}</div>
                                                    </div>
                                                    <div class="content-info">
                                                        <h4 class="content-title">{{ item.title }}</h4>
                                                        <p class="content-excerpt">{{ item.excerpt }}</p>
                                                        <div class="content-meta">
                                                            <span class="meta-author">
                                                                <el-avatar :size="24" :src="item.author.avatar"></el-avatar>
                                                                <span>{{ item.author.name }}</span>
                                                            </span>
                                                            <span class="meta-date">{{ item.publishDate }}</span>
                                                            <span class="meta-stats">
                                                                <i class="el-icon-view"></i> {{ item.views }}
                                                                <i class="el-icon-chat-dot-round"></i> {{ item.comments }}
                                                            </span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </el-tab-pane>
                                        <el-tab-pane label="热门" name="hot">
                                            <div class="content-list">
                                                <div v-for="item in latestContent.hot" :key="item.id" class="content-card"
                                                    @click="viewContent(item)">
                                                    <div class="content-image">
                                                        <img :src="item.image" :alt="item.title" />
                                                        <div class="content-type">{{ getContentType(item.type) }}</div>
                                                    </div>
                                                    <div class="content-info">
                                                        <h4 class="content-title">{{ item.title }}</h4>
                                                        <p class="content-excerpt">{{ item.excerpt }}</p>
                                                        <div class="content-meta">
                                                            <span class="meta-author">
                                                                <el-avatar :size="24" :src="item.author.avatar"></el-avatar>
                                                                <span>{{ item.author.name }}</span>
                                                            </span>
                                                            <span class="meta-date">{{ item.publishDate }}</span>
                                                            <span class="meta-stats">
                                                                <i class="el-icon-view"></i> {{ item.views }}
                                                                <i class="el-icon-chat-dot-round"></i> {{ item.comments }}
                                                            </span>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </el-tab-pane>
                                    </el-tabs>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 非首页内容 -->
                    <div v-else class="category-content">
                        <!-- 内容头部 -->
                        <div class="content-header">
                            <h2 class="content-title">{{ navTitles[activeNav] }}</h2>
                            <div class="content-filter">
                                <el-select v-model="contentSort" placeholder="排序方式" size="medium">
                                    <el-option label="最新发布" value="newest"></el-option>
                                    <el-option label="最多浏览" value="views"></el-option>
                                    <el-option label="最多收藏" value="favorites"></el-option>
                                </el-select>
                                <el-button type="primary" icon="el-icon-refresh" size="medium" @click="refreshContent">
                                    刷新
                                </el-button>
                            </div>
                        </div>

                        <!-- 内容列表 -->
                        <div class="content-list-grid">
                            <div v-for="item in categoryContent" :key="item.id" class="content-card-large"
                                @click="viewContent(item)">
                                <div class="card-image">
                                    <img :src="item.image" :alt="item.title" />
                                    <div class="card-type">{{ getContentType(item.type) }}</div>
                                    <div class="card-actions">
                                        <el-button type="text" icon="el-icon-star-off" class="favorite-btn"
                                            @click.stop="toggleFavorite(item)"></el-button>
                                    </div>
                                </div>
                                <div class="card-content">
                                    <h4 class="card-title">{{ item.title }}</h4>
                                    <p class="card-excerpt">{{ item.excerpt }}</p>
                                    <div class="card-meta">
                                        <div class="meta-left">
                                            <el-avatar :size="28" :src="item.author.avatar"></el-avatar>
                                            <div class="author-info">
                                                <div class="author-name">{{ item.author.name }}</div>
                                                <div class="author-title">{{ item.author.title }}</div>
                                            </div>
                                        </div>
                                        <div class="meta-right">
                                            <span class="meta-item">
                                                <i class="el-icon-view"></i> {{ item.views }}
                                            </span>
                                            <span class="meta-item">
                                                <i class="el-icon-chat-dot-round"></i> {{ item.comments }}
                                            </span>
                                            <span class="meta-item">
                                                <i class="el-icon-time"></i> {{ item.readTime }}
                                            </span>
                                        </div>
                                    </div>
                                    <div class="card-tags">
                                        <el-tag v-for="tag in item.tags" :key="tag" size="mini" type="info">
                                            {{ tag }}
                                        </el-tag>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 分页 -->
                        <div class="pagination-section">
                            <el-pagination :current-page="currentPage" :page-size="pageSize" :total="totalContent"
                                layout="prev, pager, next, jumper" @current-change="handlePageChange">
                            </el-pagination>
                        </div>
                    </div>
                </div>

                <!-- 右侧边栏 -->
                <aside class="knowledge-tools">
                    <!-- 营养计算器 -->
                    <div class="calculator-section">
                        <h3 class="tools-title">
                            <i class="el-icon-cpu"></i> 营养计算器
                        </h3>
                        <div class="calculator-tool">
                            <el-tabs v-model="calculatorActiveTab">
                                <el-tab-pane label="BMI计算" name="bmi">
                                    <div class="calculator-form">
                                        <el-form :model="bmiForm" label-width="80px">
                                            <el-form-item label="身高(cm)">
                                                <el-input-number v-model="bmiForm.height" :min="100" :max="250"
                                                    controls-position="right" style="width: 100%;" />
                                            </el-form-item>
                                            <el-form-item label="体重(kg)">
                                                <el-input-number v-model="bmiForm.weight" :min="30" :max="200"
                                                    controls-position="right" style="width: 100%;" />
                                            </el-form-item>
                                            <el-form-item>
                                                <el-button type="primary" @click="calculateBMI" style="width: 100%;">
                                                    计算BMI
                                                </el-button>
                                            </el-form-item>
                                        </el-form>
                                        <div v-if="bmiResult" class="calculator-result">
                                            <div class="result-title">您的BMI指数</div>
                                            <div class="result-value">{{ bmiResult.value }}</div>
                                            <div class="result-status" :class="bmiResult.status">
                                                {{ bmiResult.message }}
                                            </div>
                                            <div class="result-desc">{{ bmiResult.description }}</div>
                                        </div>
                                    </div>
                                </el-tab-pane>
                                <el-tab-pane label="热量需求" name="calorie">
                                    <div class="calculator-form">
                                        <el-form :model="calorieForm" label-width="100px">
                                            <el-form-item label="性别">
                                                <el-radio-group v-model="calorieForm.gender">
                                                    <el-radio label="male">男</el-radio>
                                                    <el-radio label="female">女</el-radio>
                                                </el-radio-group>
                                            </el-form-item>
                                            <el-form-item label="年龄">
                                                <el-input-number v-model="calorieForm.age" :min="18" :max="80"
                                                    controls-position="right" style="width: 100%;" />
                                            </el-form-item>
                                            <el-form-item label="活动水平">
                                                <el-select v-model="calorieForm.activity" placeholder="请选择">
                                                    <el-option label="久坐不动" value="sedentary"></el-option>
                                                    <el-option label="轻度活动" value="light"></el-option>
                                                    <el-option label="中度活动" value="moderate"></el-option>
                                                    <el-option label="高度活动" value="active"></el-option>
                                                    <el-option label="非常活跃" value="very-active"></el-option>
                                                </el-select>
                                            </el-form-item>
                                            <el-form-item>
                                                <el-button type="primary" @click="calculateCalorie" style="width: 100%;">
                                                    计算热量需求
                                                </el-button>
                                            </el-form-item>
                                        </el-form>
                                        <div v-if="calorieResult" class="calculator-result">
                                            <div class="result-title">每日热量需求</div>
                                            <div class="result-value">{{ calorieResult }} kcal</div>
                                            <div class="result-desc">维持当前体重所需的热量</div>
                                        </div>
                                    </div>
                                </el-tab-pane>
                            </el-tabs>
                        </div>
                    </div>

                    <!-- 专家问答 -->
                    <div class="qa-section">
                        <h3 class="tools-title">
                            <i class="el-icon-question"></i> 专家问答
                        </h3>
                        <div class="qa-list">
                            <div v-for="qa in expertQA" :key="qa.id" class="qa-item" @click="viewQA(qa)">
                                <div class="qa-question">
                                    <span class="qa-tag">{{ qa.tag }}</span>
                                    {{ qa.question }}
                                </div>
                                <div class="qa-expert">
                                    <el-avatar :size="32" :src="qa.expert.avatar"></el-avatar>
                                    <div class="expert-info">
                                        <div class="expert-name">{{ qa.expert.name }}</div>
                                        <div class="expert-title">{{ qa.expert.title }}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="qa-ask">
                            <el-button type="primary" plain size="small" @click="askQuestion" style="width: 100%;">
                                <i class="el-icon-edit"></i> 向专家提问
                            </el-button>
                        </div>
                    </div>

                    <!-- 营养小贴士 -->
                    <div class="tips-section">
                        <h3 class="tools-title">
                            <i class="el-icon-lightbulb"></i> 营养小贴士
                        </h3>
                        <div class="tips-list">
                            <div v-for="(tip, index) in nutritionTips" :key="index" class="tip-item">
                                <div class="tip-index">{{ index + 1 }}</div>
                                <div class="tip-content">{{ tip }}</div>
                            </div>
                        </div>
                    </div>
                </aside>
            </div>
        </main>

        <!-- 内容详情对话框 -->
        <el-dialog :title="currentContent.title" :visible.sync="contentDialogVisible" width="80%" top="5vh"
            custom-class="content-dialog">
            <div v-if="currentContent.type === 'article'" class="article-content">
                <div class="article-header">
                    <div class="article-meta">
                        <el-avatar :size="40" :src="currentContent.author.avatar"></el-avatar>
                        <div class="meta-info">
                            <div class="author-name">{{ currentContent.author.name }}</div>
                            <div class="publish-info">
                                <span>{{ currentContent.publishDate }}</span>
                                <span>阅读 {{ currentContent.views }}</span>
                                <span>收藏 {{ currentContent.favorites }}</span>
                            </div>
                        </div>
                    </div>
                    <div class="article-actions">
                        <el-button type="text" :icon="currentContent.isFavorite ? 'el-icon-star-on' : 'el-icon-star-off'"
                            @click="toggleFavorite(currentContent)">
                            {{ currentContent.favorites }}
                        </el-button>
                        <el-button type="text" icon="el-icon-share">分享</el-button>
                    </div>
                </div>
                <div class="article-body">
                    <h2>{{ currentContent.title }}</h2>
                    <div class="article-tags">
                        <el-tag v-for="tag in currentContent.tags" :key="tag" size="small" type="info">
                            {{ tag }}
                        </el-tag>
                    </div>
                    <div class="article-content" v-html="currentContent.content"></div>
                </div>
            </div>
            <div v-else-if="currentContent.type === 'video'" class="video-content">
                <div class="video-player">
                    <video :src="currentContent.videoUrl" controls style="width: 100%;"></video>
                </div>
                <div class="video-info">
                    <h3>{{ currentContent.title }}</h3>
                    <div class="video-meta">
                        <span>{{ currentContent.duration }}</span>
                        <span>播放 {{ currentContent.views }}</span>
                        <span>{{ currentContent.publishDate }}</span>
                    </div>
                    <div class="video-description">{{ currentContent.description }}</div>
                </div>
            </div>
            <div v-else class="recipe-content">
                <div class="recipe-header">
                    <h3>{{ currentContent.title }}</h3>
                    <div class="recipe-meta">
                        <div class="meta-item">
                            <i class="el-icon-time"></i> 准备时间: {{ currentContent.prepTime }}
                        </div>
                        <div class="meta-item">
                            <i class="el-icon-knife-fork"></i> 份量: {{ currentContent.servings }}
                        </div>
                        <div class="meta-item">
                            <i class="el-icon-fire"></i> 热量: {{ currentContent.calories }} kcal/份
                        </div>
                    </div>
                </div>
                <div class="recipe-body">
                    <div class="recipe-ingredients">
                        <h4>食材</h4>
                        <ul>
                            <li v-for="ingredient in currentContent.ingredients" :key="ingredient">
                                {{ ingredient }}
                            </li>
                        </ul>
                    </div>
                    <div class="recipe-steps">
                        <h4>步骤</h4>
                        <div v-for="(step, index) in currentContent.steps" :key="index" class="step-item">
                            <div class="step-number">{{ index + 1 }}</div>
                            <div class="step-content">{{ step }}</div>
                        </div>
                    </div>
                </div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button @click="contentDialogVisible = false">关闭</el-button>
                <el-button type="primary" @click="shareContent">分享</el-button>
            </span>
        </el-dialog>

        <!-- 底部 -->
        <footer class="knowledge-footer">
            <div class="footer-container">
                <div class="footer-content">
                    <div class="footer-section">
                        <h4>营养知识库</h4>
                        <p>提供专业、科学的营养知识，帮助您建立健康的生活方式。</p>
                    </div>
                    <div class="footer-section">
                        <h4>快速链接</h4>
                        <ul>
                            <li><a href="#">关于我们</a></li>
                            <li><a href="#">联系我们</a></li>
                            <li><a href="#">隐私政策</a></li>
                            <li><a href="#">使用条款</a></li>
                        </ul>
                    </div>
                    <div class="footer-section">
                        <h4>关注我们</h4>
                        <div class="social-links">
                            <i class="el-icon-weibo"></i>
                            <i class="el-icon-wechat"></i>
                            <i class="el-icon-phone"></i>
                        </div>
                    </div>
                </div>
                <div class="footer-bottom">
                    <p>© 2023 营养知识库. 保留所有权利. 数据仅供参考，如有健康问题请咨询专业医生或营养师。</p>
                </div>
            </div>
        </footer>
    </div>
</template>
<script>
export default {
    name: 'NutritionKnowledge',
    data() {
        return {
            // 顶部导航
            activeNav: 'home',
            navTitles: {
                home: '首页',
                articles: '营养文章',
                videos: '营养视频',
                recipes: '健康食谱',
                tools: '营养工具'
            },
            searchKeyword: '',
            userAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',

            // 面包屑导航
            currentCategory: null,

            // 左侧分类
            categories: [
                { id: 1, name: '基础营养学', icon: 'el-icon-reading', count: 156 },
                { id: 2, name: '减肥健身', icon: 'el-icon-run', count: 89 },
                { id: 3, name: '疾病营养', icon: 'el-icon-first-aid-kit', count: 72 },
                { id: 4, name: '母婴营养', icon: 'el-icon-baby', count: 45 },
                { id: 5, name: '老年营养', icon: 'el-icon-user', count: 38 },
                { id: 6, name: '运动营养', icon: 'el-icon-basketball', count: 67 },
                { id: 7, name: '素食营养', icon: 'el-icon-grape', count: 34 },
                { id: 8, name: '饮食文化', icon: 'el-icon-food', count: 52 }
            ],

            // 热门标签
            hotTags: [
                { id: 1, name: '蛋白质', type: '' },
                { id: 2, name: '维生素', type: 'success' },
                { id: 3, name: '膳食纤维', type: 'info' },
                { id: 4, name: '减脂', type: 'warning' },
                { id: 5, name: '增肌', type: '' },
                { id: 6, name: '血糖控制', type: 'success' },
                { id: 7, name: '心脏健康', type: 'danger' },
                { id: 8, name: '肠道健康', type: 'info' }
            ],

            // 今日推荐
            dailyRecommendations: [
                {
                    id: 1,
                    title: '蛋白质摄入指南',
                    type: 'article',
                    image: 'https://images.unsplash.com/photo-1490818387583-1baba5e638af?w=400',
                    views: 1250,
                    readTime: '5分钟'
                },
                {
                    id: 2,
                    title: '地中海饮食食谱',
                    type: 'recipe',
                    image: 'https://images.unsplash.com/photo-1499028344343-cd173ffc68a9?w-400',
                    views: 890,
                    duration: '30分钟'
                },
                {
                    id: 3,
                    title: '维生素D的重要性',
                    type: 'video',
                    image: 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400',
                    views: 1560,
                    duration: '12分钟'
                }
            ],

            // 首页轮播图
            carouselItems: [
                {
                    id: 1,
                    title: '2023年最新膳食指南',
                    description: '了解最新的营养科学研究和饮食建议',
                    category: '基础营养',
                    image: 'https://images.unsplash.com/photo-1490818387583-1baba5e638af?w=1200'
                },
                {
                    id: 2,
                    title: '健康减肥的科学方法',
                    description: '避免常见减肥误区，科学减重不反弹',
                    category: '减肥健身',
                    image: 'https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=1200'
                },
                {
                    id: 3,
                    title: '糖尿病患者的营养管理',
                    description: '通过合理饮食控制血糖，提高生活质量',
                    category: '疾病营养',
                    image: 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=1200'
                }
            ],

            // 特色专题
            featuredTopics: [
                {
                    id: 1,
                    title: '地中海饮食全解析',
                    description: '探索世界上最健康的饮食模式',
                    image: 'https://images.unsplash.com/photo-1499028344343-cd173ffc68a9?w=400',
                    articleCount: 24,
                    followers: 1560
                },
                {
                    id: 2,
                    title: '健身增肌营养指南',
                    description: '科学增肌，合理营养搭配',
                    image: 'https://images.unsplash.com/photo-1534367507877-0edd93bd013b?w=400',
                    articleCount: 18,
                    followers: 2340
                },
                {
                    id: 3,
                    title: '孕期营养全攻略',
                    description: '准妈妈必备的营养知识',
                    image: 'https://images.unsplash.com/photo-1512295767273-ac109ac3acfa?w=400',
                    articleCount: 15,
                    followers: 890
                }
            ],

            // 最新内容筛选
            latestFilter: 'all',
            latestActiveTab: 'recommend',

            // 最新内容数据
            latestContent: {
                recommend: [
                    {
                        id: 1,
                        title: 'Omega-3脂肪酸的全面指南',
                        excerpt: '了解Omega-3脂肪酸的来源、作用和推荐摄入量',
                        type: 'article',
                        image: 'https://images.unsplash.com/photo-1551183053-bf91a1d81141?w-400',
                        author: {
                            name: '张营养师',
                            avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
                            title: '注册营养师'
                        },
                        publishDate: '2023-10-15',
                        views: 2350,
                        comments: 89,
                        readTime: '8分钟'
                    },
                    {
                        id: 2,
                        title: '10分钟健康早餐',
                        excerpt: '快速准备营养均衡的早餐，开启活力一天',
                        type: 'video',
                        image: 'https://images.unsplash.com/photo-1482049016688-2d3e1b311543?w-400',
                        author: {
                            name: '李厨师',
                            avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                            title: '健康烹饪专家'
                        },
                        publishDate: '2023-10-14',
                        views: 1560,
                        comments: 45,
                        duration: '10分钟'
                    },
                    {
                        id: 3,
                        title: '低GI晚餐食谱',
                        excerpt: '控制血糖的晚餐选择，适合糖尿病患者',
                        type: 'recipe',
                        image: 'https://images.unsplash.com/photo-1473093295043-cdd812d0e601?w-400',
                        author: {
                            name: '王医生',
                            avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
                            title: '内分泌科医生'
                        },
                        publishDate: '2023-10-13',
                        views: 1890,
                        comments: 67,
                        prepTime: '25分钟'
                    }
                ],
                hot: [
                    {
                        id: 4,
                        title: '间歇性断食的利弊',
                        excerpt: '深入分析间歇性断食的科学依据和注意事项',
                        type: 'article',
                        image: 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w-400',
                        author: {
                            name: '赵研究员',
                            avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                            title: '营养学博士'
                        },
                        publishDate: '2023-10-12',
                        views: 3120,
                        comments: 124,
                        readTime: '12分钟'
                    },
                    {
                        id: 5,
                        title: '运动后营养补充',
                        excerpt: '运动后如何正确补充营养促进恢复',
                        type: 'video',
                        image: 'https://images.unsplash.com/photo-1534367507877-0edd93bd013b?w-400',
                        author: {
                            name: '刘教练',
                            avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
                            title: '运动营养师'
                        },
                        publishDate: '2023-10-11',
                        views: 2780,
                        comments: 89,
                        duration: '15分钟'
                    }
                ]
            },

            // 分类内容
            categoryContent: [],
            contentSort: 'newest',
            currentPage: 1,
            pageSize: 6,
            totalContent: 50,

            // 右侧工具
            calculatorActiveTab: 'bmi',
            bmiForm: {
                height: 170,
                weight: 65
            },
            bmiResult: null,
            calorieForm: {
                gender: 'male',
                age: 30,
                activity: 'moderate'
            },
            calorieResult: null,

            // 专家问答
            expertQA: [
                {
                    id: 1,
                    question: '如何增加蛋白质摄入而不增加太多热量？',
                    tag: '蛋白质',
                    expert: {
                        name: '张营养师',
                        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
                        title: '注册营养师'
                    }
                },
                {
                    id: 2,
                    question: '素食者如何确保摄入足够的铁质？',
                    tag: '素食营养',
                    expert: {
                        name: '李医生',
                        avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
                        title: '营养科主任'
                    }
                },
                {
                    id: 3,
                    question: '运动前吃什么能提高表现？',
                    tag: '运动营养',
                    expert: {
                        name: '王教练',
                        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
                        title: '运动营养专家'
                    }
                }
            ],

            // 营养小贴士
            nutritionTips: [
                '每天至少喝8杯水，保持身体水分充足',
                '多吃色彩鲜艳的蔬菜水果，获取多种抗氧化剂',
                '选择全谷物代替精制谷物',
                '适量摄入健康脂肪，如坚果和橄榄油',
                '控制添加糖的摄入，减少加工食品'
            ],

            // 内容详情
            contentDialogVisible: false,
            currentContent: {},

            // 模拟文章内容
            mockArticleContent: `
          <h3>什么是Omega-3脂肪酸？</h3>
          <p>Omega-3脂肪酸是一类对人体健康至关重要的多不饱和脂肪酸。它们被称为"必需脂肪酸"，因为人体无法自行合成，必须从食物中获取。</p>
          
          <h3>主要类型</h3>
          <p>Omega-3脂肪酸主要有三种类型：</p>
          <ul>
            <li><strong>ALA（α-亚麻酸）</strong>：主要存在于植物性食物中，如亚麻籽、奇亚籽和核桃</li>
            <li><strong>EPA（二十碳五烯酸）</strong>：主要存在于鱼类和海藻中</li>
            <li><strong>DHA（二十二碳六烯酸）</strong>：主要存在于鱼类和海藻中，对大脑健康特别重要</li>
          </ul>
          
          <h3>健康益处</h3>
          <p>Omega-3脂肪酸具有多种健康益处：</p>
          <ol>
            <li>支持心脏健康，降低心血管疾病风险</li>
            <li>促进大脑发育和功能</li>
            <li>减少炎症反应</li>
            <li>支持眼睛健康</li>
            <li>可能改善情绪和心理健康</li>
          </ol>
          
          <h3>推荐摄入量</h3>
          <p>大多数健康组织建议每周至少吃两次鱼类，特别是富含脂肪的鱼类，如三文鱼、鲭鱼和沙丁鱼。</p>
        `
        };
    },

    mounted() {
        // 初始化分类内容
        this.loadCategoryContent();

        // 模拟加载数据
        this.generateMockContent();
    },

    methods: {
        // 导航处理
        handleNavSelect(key) {
            this.activeNav = key;
            this.currentCategory = null;
            this.currentPage = 1;
            this.loadCategoryContent();
        },

        goHome() {
            this.activeNav = 'home';
            this.currentCategory = null;
        },

        // 分类选择
        selectCategory(category) {
            this.currentCategory = category;
            this.activeNav = 'articles';
            this.loadCategoryContent();
        },

        // 标签选择
        selectTag(tag) {
            this.searchKeyword = tag.name;
            this.handleSearch();
        },

        // 搜索处理
        handleSearch() {
            if (!this.searchKeyword.trim()) {
                this.$message.warning('请输入搜索关键词');
                return;
            }

            this.$message.info(`搜索: ${this.searchKeyword}`);
            // 这里实际项目中应该调用搜索API
            this.loadCategoryContent();
        },

        // 加载分类内容
        loadCategoryContent() {
            // 模拟加载分类内容
            this.generateMockContent();
        },

        // 生成模拟内容
        generateMockContent() {
            const mockData = [];
            const contentTypes = ['article', 'video', 'recipe'];
            const titles = [
                '蛋白质摄入全指南',
                '地中海饮食的7个要点',
                '运动后的营养补充策略',
                '控制血糖的饮食建议',
                '维生素D与骨骼健康',
                '肠道健康与益生菌',
                '减脂期的饮食安排',
                '高血压患者的低钠饮食'
            ];

            const authors = [
                { name: '张营养师', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', title: '注册营养师' },
                { name: '李医生', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png', title: '营养科主任' },
                { name: '王研究员', avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png', title: '营养学博士' },
                { name: '刘教练', avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png', title: '运动营养师' }
            ];

            const tags = ['蛋白质', '减脂', '健康饮食', '维生素', '膳食纤维', '运动营养', '心脏健康'];

            for (let i = 0; i < this.pageSize; i++) {
                const type = contentTypes[Math.floor(Math.random() * contentTypes.length)];
                const title = titles[Math.floor(Math.random() * titles.length)] + ` (${i + 1})`;
                const author = authors[Math.floor(Math.random() * authors.length)];

                // 随机生成标签
                const contentTags = [];
                const tagCount = Math.floor(Math.random() * 3) + 1;
                for (let j = 0; j < tagCount; j++) {
                    const randomTag = tags[Math.floor(Math.random() * tags.length)];
                    if (!contentTags.includes(randomTag)) {
                        contentTags.push(randomTag);
                    }
                }

                mockData.push({
                    id: i + 1,
                    title: title,
                    excerpt: '这是一段关于营养知识的摘要内容，介绍了相关的重要信息和知识点...',
                    type: type,
                    image: `https://images.unsplash.com/photo-${1551183050 + i}?w=400`,
                    author: author,
                    publishDate: `2023-10-${15 - i}`,
                    views: Math.floor(Math.random() * 2000) + 500,
                    comments: Math.floor(Math.random() * 100) + 10,
                    readTime: `${Math.floor(Math.random() * 10) + 5}分钟`,
                    tags: contentTags,
                    isFavorite: Math.random() > 0.5
                });
            }

            this.categoryContent = mockData;
            this.totalContent = 50;
        },

        // 查看内容
        viewContent(content) {
            this.currentContent = {
                ...content,
                content: this.mockArticleContent,
                videoUrl: 'https://example.com/sample-video.mp4',
                description: '这是一个关于营养知识的详细视频介绍...',
                ingredients: ['鸡胸肉 200g', '西兰花 150g', '胡萝卜 100g', '橄榄油 1汤匙'],
                steps: ['准备所有食材', '将鸡胸肉切成小块', '蔬菜洗净切好', '用橄榄油炒至熟透'],
                prepTime: '20分钟',
                servings: '2人份',
                calories: 350,
                favorites: Math.floor(Math.random() * 200) + 50
            };
            this.contentDialogVisible = true;
        },

        // 查看专题
        viewTopic(topic) {
            this.$message.info(`查看专题: ${topic.title}`);
            // 实际项目中应跳转到专题页面
        },

        // 查看全部专题
        viewAllTopics() {
            this.$message.info('查看全部专题');
        },

        // 刷新内容
        refreshContent() {
            this.loadCategoryContent();
            this.$message.success('内容已刷新');
        },

        // 分页处理
        handlePageChange(page) {
            this.currentPage = page;
            this.loadCategoryContent();
        },

        // 获取内容类型
        getContentType(type) {
            const typeMap = {
                article: '文章',
                video: '视频',
                recipe: '食谱'
            };
            return typeMap[type] || '未知';
        },

        // 切换收藏
        toggleFavorite(item) {
            item.isFavorite = !item.isFavorite;
            const action = item.isFavorite ? '收藏' : '取消收藏';
            this.$message.success(`${action}成功: ${item.title}`);
        },

        // 用户命令处理
        handleUserCommand(command) {
            switch (command) {
                case 'profile':
                    this.$message.info('打开个人中心');
                    break;
                case 'bookmarks':
                    this.$message.info('查看我的收藏');
                    break;
                case 'history':
                    this.$message.info('查看浏览历史');
                    break;
                case 'settings':
                    this.$message.info('打开设置');
                    break;
            }
        },

        // 计算BMI
        calculateBMI() {
            const heightM = this.bmiForm.height / 100;
            const bmi = (this.bmiForm.weight / (heightM * heightM)).toFixed(1);

            let status = 'normal';
            let message = '';
            let description = '';

            if (bmi < 18.5) {
                status = 'underweight';
                message = '体重过轻';
                description = '建议适当增加营养摄入，增加肌肉量';
            } else if (bmi >= 18.5 && bmi < 24) {
                status = 'normal';
                message = '正常体重';
                description = '继续保持健康的饮食和运动习惯';
            } else if (bmi >= 24 && bmi < 28) {
                status = 'overweight';
                message = '超重';
                description = '建议控制饮食，增加运动量';
            } else {
                status = 'obese';
                message = '肥胖';
                description = '建议咨询医生或营养师，制定减重计划';
            }

            this.bmiResult = {
                value: bmi,
                status: status,
                message: message,
                description: description
            };

            this.$message.success(`BMI计算完成: ${bmi}`);
        },

        // 计算热量需求
        calculateCalorie() {
            // 基础代谢率计算公式（ Mifflin-St Jeor 方程）
            let bmr;
            if (this.calorieForm.gender === 'male') {
                bmr = 10 * 70 + 6.25 * 170 - 5 * this.calorieForm.age + 5;
            } else {
                bmr = 10 * 70 + 6.25 * 170 - 5 * this.calorieForm.age - 161;
            }

            // 活动系数
            const activityFactors = {
                sedentary: 1.2,      // 久坐
                light: 1.375,        // 轻度活动
                moderate: 1.55,      // 中度活动
                active: 1.725,       // 高度活动
                'very-active': 1.9   // 非常活跃
            };

            const tdee = Math.round(bmr * activityFactors[this.calorieForm.activity]);
            this.calorieResult = tdee;

            this.$message.success(`每日热量需求: ${tdee} kcal`);
        },

        // 查看问答
        viewQA(qa) {
            this.$message.info(`查看问答: ${qa.question}`);
        },

        // 提问
        askQuestion() {
            this.$message.info('打开提问窗口');
        },

        // 分享内容
        shareContent() {
            this.$message.success('内容已复制到剪贴板，可以分享了');
            this.contentDialogVisible = false;
        }
    }
};
</script>
<style scoped>
.nutrition-knowledge {
    font-family: 'Helvetica Neue', Arial, sans-serif;
    color: #333;
    background-color: #f8fafc;
    min-height: 100vh;
}

/* 顶部导航 */
.knowledge-header {
    background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
    color: white;
    padding: 0;
    box-shadow: 0 4px 20px rgba(15, 23, 42, 0.15);
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
.knowledge-main {
    margin: 20px auto;
    padding: 0 20px;
}

.main-container {
    display: flex;
    gap: 20px;
}

/* 左侧边栏 */
.knowledge-sidebar {
    width: 260px;
    flex-shrink: 0;
}

.sidebar-title {
    font-size: 16px;
    color: #0f172a;
    margin: 0 0 15px;
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
}

.sidebar-title i {
    color: #3b82f6;
}

.category-section {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
    margin-bottom: 20px;
}

.category-list {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.category-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;
}

.category-item:hover {
    background-color: #f1f5f9;
}

.category-item.active {
    background-color: rgba(15, 23, 42, 0.05);
    border-left: 3px solid #0f172a;
}

.category-icon {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f1f5f9;
    border-radius: 8px;
}

.category-icon i {
    font-size: 18px;
    color: #475569;
}

.category-item.active .category-icon {
    background-color: rgba(15, 23, 42, 0.1);
}

.category-content {
    flex: 1;
}

.category-name {
    font-weight: 500;
    color: #0f172a;
    margin-bottom: 4px;
}

.category-count {
    font-size: 12px;
    color: #94a3b8;
}

.tags-section {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
    margin-bottom: 20px;
}

.tags-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.tag-item {
    cursor: pointer;
    transition: transform 0.3s;
}

.tag-item:hover {
    transform: translateY(-2px);
}

.recommend-section {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
}

.recommend-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.recommend-item {
    cursor: pointer;
    transition: transform 0.3s;
}

.recommend-item:hover {
    transform: translateY(-3px);
}

.recommend-image {
    position: relative;
    border-radius: 8px;
    overflow: hidden;
    height: 120px;
    margin-bottom: 10px;
}

.recommend-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.recommend-type {
    position: absolute;
    top: 8px;
    right: 8px;
    background-color: rgba(15, 23, 42, 0.8);
    color: white;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
}

.recommend-content h4 {
    margin: 0 0 8px;
    font-size: 14px;
    color: #0f172a;
    font-weight: 600;
    line-height: 1.4;
}

.recommend-meta {
    display: flex;
    gap: 12px;
    font-size: 12px;
    color: #94a3b8;
}

/* 主要内容区域 */
.knowledge-content {
    flex: 1;
    min-width: 0;
    /* 防止flex item溢出 */
}

/* 首页内容 */
.home-content {
    display: flex;
    flex-direction: column;
    gap: 30px;
}

/* 轮播图 */
.carousel-section {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 8px 25px rgba(15, 23, 42, 0.15);
}

.carousel-item {
    height: 360px;
    background-size: cover;
    background-position: center;
    position: relative;
}

.carousel-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(90deg, rgba(15, 23, 42, 0.8) 0%, rgba(15, 23, 42, 0.4) 100%);
    display: flex;
    align-items: center;
    padding: 0 60px;
}

.carousel-content {
    max-width: 600px;
    color: white;
}

.carousel-category {
    display: inline-block;
    background-color: rgba(59, 130, 246, 0.8);
    color: white;
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 14px;
    margin-bottom: 15px;
}

.carousel-title {
    font-size: 32px;
    font-weight: 700;
    margin: 0 0 15px;
    line-height: 1.3;
}

.carousel-desc {
    font-size: 16px;
    opacity: 0.9;
    margin-bottom: 25px;
    line-height: 1.5;
}

/* 特色专题 */
.featured-section {
    background: white;
    border-radius: 12px;
    padding: 25px;
    box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 25px;
}

.section-title {
    font-size: 20px;
    color: #0f172a;
    margin: 0;
    display: flex;
    align-items: center;
    gap: 10px;
}

.topics-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
}

.topic-card {
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    cursor: pointer;
    transition: transform 0.3s, box-shadow 0.3s;
}

.topic-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12);
}

.topic-image {
    height: 160px;
    overflow: hidden;
}

.topic-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.topic-content {
    padding: 20px;
}

.topic-title {
    font-size: 16px;
    font-weight: 600;
    color: #0f172a;
    margin: 0 0 10px;
}

.topic-desc {
    font-size: 14px;
    color: #64748b;
    margin-bottom: 15px;
    line-height: 1.5;
}

.topic-meta {
    display: flex;
    justify-content: space-between;
    font-size: 13px;
    color: #94a3b8;
}

/* 最新内容 */
.latest-section {
    background: white;
    border-radius: 12px;
    padding: 25px;
    box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
}

.latest-content {
    margin-top: 20px;
}

.content-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.content-card {
    display: flex;
    gap: 20px;
    padding: 20px;
    border-radius: 10px;
    border: 1px solid #e2e8f0;
    cursor: pointer;
    transition: all 0.3s;
}

.content-card:hover {
    border-color: #cbd5e1;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.content-image {
    position: relative;
    width: 200px;
    height: 120px;
    flex-shrink: 0;
    border-radius: 8px;
    overflow: hidden;
}

.content-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.content-type {
    position: absolute;
    bottom: 8px;
    left: 8px;
    background-color: rgba(15, 23, 42, 0.8);
    color: white;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
}

.content-info {
    flex: 1;
    min-width: 0;
}

.content-title {
    font-size: 18px;
    font-weight: 600;
    color: #0f172a;
    margin: 0 0 10px;
}

.content-excerpt {
    font-size: 14px;
    color: #64748b;
    line-height: 1.5;
    margin-bottom: 15px;
}

.content-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 13px;
    color: #94a3b8;
}

.meta-author {
    display: flex;
    align-items: center;
    gap: 8px;
}

.meta-stats {
    display: flex;
    gap: 12px;
}

/* 分类内容 */
.category-content {
    background: white;
    border-radius: 12px;
    padding: 25px;
    box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
}

.content-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 25px;
}

.content-title {
    font-size: 22px;
    color: #0f172a;
    margin: 0;
}

.content-filter {
    display: flex;
    gap: 15px;
    align-items: center;
}

.content-list-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 25px;
    margin-bottom: 30px;
}

.content-card-large {
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    cursor: pointer;
    transition: transform 0.3s, box-shadow 0.3s;
}

.content-card-large:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
}

.card-image {
    position: relative;
    height: 200px;
    overflow: hidden;
}

.card-image img {
    width: 100%;
    height: 100%;
    object-fit: cover;
}

.card-type {
    position: absolute;
    top: 15px;
    left: 15px;
    background-color: rgba(15, 23, 42, 0.8);
    color: white;
    padding: 6px 12px;
    border-radius: 4px;
    font-size: 14px;
}

.card-actions {
    position: absolute;
    top: 15px;
    right: 15px;
}

.favorite-btn {
    color: #f59e0b;
    font-size: 18px;
}

.card-content {
    padding: 20px;
}

.card-title {
    font-size: 18px;
    font-weight: 600;
    color: #0f172a;
    margin: 0 0 12px;
    line-height: 1.4;
}

.card-excerpt {
    font-size: 14px;
    color: #64748b;
    line-height: 1.5;
    margin-bottom: 15px;
}

.card-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}

.meta-left {
    display: flex;
    align-items: center;
    gap: 10px;
}

.author-info {
    display: flex;
    flex-direction: column;
}

.author-name {
    font-weight: 500;
    font-size: 14px;
    color: #0f172a;
}

.author-title {
    font-size: 12px;
    color: #94a3b8;
}

.meta-right {
    display: flex;
    gap: 15px;
    font-size: 13px;
    color: #94a3b8;
}

.card-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
}

.pagination-section {
    display: flex;
    justify-content: center;
    padding-top: 20px;
    border-top: 1px solid #e2e8f0;
}

/* 右侧工具 */
.knowledge-tools {
    width: 320px;
    flex-shrink: 0;
}

.tools-title {
    font-size: 16px;
    color: #0f172a;
    margin: 0 0 15px;
    display: flex;
    align-items: center;
    gap: 8px;
    font-weight: 600;
}

.calculator-section {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
    margin-bottom: 20px;
}

.calculator-form {
    margin-top: 15px;
}

.calculator-result {
    margin-top: 20px;
    padding: 15px;
    background-color: #f8fafc;
    border-radius: 8px;
    text-align: center;
}

.result-title {
    font-size: 14px;
    color: #64748b;
    margin-bottom: 8px;
}

.result-value {
    font-size: 28px;
    font-weight: 700;
    color: #0f172a;
    margin-bottom: 8px;
}

.result-status {
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 8px;
}

.result-status.normal {
    color: #10b981;
}

.result-status.underweight {
    color: #3b82f6;
}

.result-status.overweight {
    color: #f59e0b;
}

.result-status.obese {
    color: #ef4444;
}

.result-desc {
    font-size: 13px;
    color: #64748b;
    line-height: 1.4;
}

.qa-section {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
    margin-bottom: 20px;
}

.qa-list {
    margin-bottom: 15px;
}

.qa-item {
    padding: 15px 0;
    border-bottom: 1px solid #f1f5f9;
    cursor: pointer;
}

.qa-item:last-child {
    border-bottom: none;
}

.qa-question {
    font-size: 14px;
    color: #0f172a;
    line-height: 1.4;
    margin-bottom: 12px;
}

.qa-tag {
    display: inline-block;
    background-color: #f1f5f9;
    color: #475569;
    padding: 2px 8px;
    border-radius: 4px;
    font-size: 12px;
    margin-right: 8px;
}

.qa-expert {
    display: flex;
    align-items: center;
    gap: 10px;
}

.expert-info {
    display: flex;
    flex-direction: column;
}

.expert-name {
    font-size: 14px;
    font-weight: 500;
    color: #0f172a;
}

.expert-title {
    font-size: 12px;
    color: #94a3b8;
}

.tips-section {
    background: white;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
}

.tips-list {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.tip-item {
    display: flex;
    gap: 12px;
}

.tip-index {
    width: 24px;
    height: 24px;
    flex-shrink: 0;
    background-color: #f1f5f9;
    color: #0f172a;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: 600;
}

.tip-content {
    flex: 1;
    font-size: 14px;
    color: #475569;
    line-height: 1.5;
}

/* 底部 */
.knowledge-footer {
    background-color: #0f172a;
    color: white;
    margin-top: 50px;
    padding: 40px 0 20px;
}

.footer-container {
    margin: 0 auto;
    padding: 0 20px;
}

.footer-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 40px;
    margin-bottom: 30px;
}

.footer-section h4 {
    font-size: 18px;
    margin: 0 0 15px;
    color: white;
}

.footer-section p {
    color: #cbd5e1;
    line-height: 1.6;
    margin: 0;
}

.footer-section ul {
    list-style: none;
    padding: 0;
    margin: 0;
}

.footer-section ul li {
    margin-bottom: 10px;
}

.footer-section ul li a {
    color: #cbd5e1;
    text-decoration: none;
    transition: color 0.3s;
}

.footer-section ul li a:hover {
    color: white;
}

.social-links {
    display: flex;
    gap: 15px;
    font-size: 24px;
}

.social-links i {
    cursor: pointer;
    transition: opacity 0.3s;
}

.social-links i:hover {
    opacity: 0.8;
}

.footer-bottom {
    text-align: center;
    padding-top: 20px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.footer-bottom p {
    color: #94a3b8;
    font-size: 14px;
    margin: 0;
}

/* 响应式调整 */
@media (max-width: 1200px) {
    .main-container {
        flex-direction: column;
    }

    .knowledge-sidebar,
    .knowledge-tools {
        width: 100%;
    }

    .knowledge-sidebar {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 20px;
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
    }

    .header-right {
        width: 100%;
        justify-content: center;
        margin-top: 15px;
    }

    .search-bar {
        width: 100%;
    }

    .content-card {
        flex-direction: column;
    }

    .content-image {
        width: 100%;
    }

    .content-list-grid {
        grid-template-columns: 1fr;
    }

    .carousel-overlay {
        padding: 0 20px;
    }

    .carousel-title {
        font-size: 24px;
    }

    .topics-grid {
        grid-template-columns: 1fr;
    }
}
</style>