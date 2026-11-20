<template>
    <div class="blog-management-container">
        <!-- 顶部导航栏（与营养知识页面相同） -->
        <header class="knowledge-header">
            <div class="header-container">
                <div class="header-left">
                    <div class="logo">
                        <i class="el-icon-notebook-2"></i>
                        <h1>文章管理</h1>
                    </div>
                    <div class="header-nav">
                        <el-menu :default-active="activeTab" mode="horizontal" @select="handleNavTagSelect"
                            active-text-color="#409EFF">
                            <el-menu-item index="8">文章大厅</el-menu-item>
                            <el-menu-item index="0">已发布</el-menu-item>
                            <el-menu-item index="1">原创</el-menu-item>
                            <el-menu-item index="2">转载</el-menu-item>
                            <el-menu-item index="3">草稿</el-menu-item>
                            <el-menu-item index="4">私密</el-menu-item>
                            <el-menu-item index="5">回收站</el-menu-item>
                            <el-menu-item index="6">点赞</el-menu-item>
                            <el-menu-item index="7">收藏</el-menu-item>
                        </el-menu>
                    </div>
                </div>
                <div class="header-right">
                    <div class="search-bar">
                        <el-input v-model="inputSearchKeyWord" placeholder="搜索博客标题..." @keyup.enter.native="searchByTitle"
                            size="medium">
                            <el-button slot="append" icon="el-icon-search" @click="searchByTitle"></el-button>
                        </el-input>
                    </div>
                </div>
            </div>
        </header>

        <!-- 主内容区 -->
        <div class="management-main">
            <!-- 左侧分类导航 -->
            <div class="category-sidebar">
                <div class="sidebar-header">
                    <h3><i class="el-icon-menu"></i> 文章分类</h3>
                </div>
                <div class="category-list">
                    <div class="category-item" :class="{ active: selectedCategoryId === null }"
                        @click="selectCategory(null)">
                        <div class="category-icon" style="backgroundColor: #909399">
                            <i class="el-icon-document"></i>
                        </div>
                        <div class="category-info">
                            <span class="category-name">全部文章</span>
                            <span class="article-count">{{ blogCount }}篇</span>
                        </div>
                    </div>
                    <div class="category-item" v-for="category in categories" :key="category.id" :class="{
                            active: selectedCategoryId === category.id,
                            'has-draft': category.draftCount > 0
                        }" @click="selectCategory(category.id)">
                        <div class="category-icon" style="backgroundColor: #909399">
                            <i class="el-icon-reading"></i>
                        </div>
                        <div class="category-info">
                            <span class="category-name">{{ category.name }}</span>
                            <span class="article-count">{{ category.count }}篇</span>
                        </div>
                        <div v-if="category.draftCount > 0" class="draft-badge">
                            {{ category.draftCount }}
                        </div>
                    </div>
                </div>
                <div class="category-actions">
                    <el-button type="text" size="small" icon="el-icon-setting" @click="$router.push('/home/sortblog')">
                        管理分类
                    </el-button>
                </div>
            </div>

            <!-- 中间博客列表 -->
            <div class="blog-list-main">
                <!-- 列表头部信息 -->
                <div class="list-header">
                    <div class="header-info">
                        <h3 v-if="selectedCategoryName">{{ selectedCategoryName }}</h3>
                        <h3 v-else>全部文章</h3>
                        <p class="list-count">共 {{ pagination.totalBlogs }} 篇博客</p>
                    </div>
                    <div class="header-actions">
                        <el-button type="primary" icon="el-icon-plus" @click="$router.push('/home/blog/write')"
                            class="create-btn">
                            写文章
                        </el-button>
                    </div>
                </div>

                <!-- 博客列表 -->
                <div class="blog-list-container">
                    <div v-if="pagination.loading" class="loading-state">
                        <div class="skeleton-item" v-for="i in 5" :key="i">
                            <div class="skeleton-cover"></div>
                            <div class="skeleton-content">
                                <div class="skeleton-title"></div>
                                <div class="skeleton-desc"></div>
                                <div class="skeleton-meta"></div>
                            </div>
                        </div>
                    </div>

                    <div v-else-if="pagination.currentTableData.length === 0" class="empty-state">
                        <i class="el-icon-document"></i>
                        <p>暂无博客内容</p>
                        <el-button type="primary" icon="el-icon-plus" @click="$router.push('/home/blog/write')"
                            class="empty-action">
                            开始写第一篇文章
                        </el-button>
                    </div>

                    <div v-else class="blog-list">
                        <div class="blog-card" v-for="(item, index) in pagination.currentTableData" :key="item.bid" :class="{
                                'draft-blog': !item.issue,
                                'deleted-blog': item.isdel,
                                'private-blog': item.ispublic == '0'
                            }">
                            <!-- 博客封面 -->
                            <div class="blog-cover" @click="handlePreviewContent(index, item)">
                                <el-image :src="item.cover || '/static/defaultImg.png'" fit="cover" class="cover-image">
                                    <div slot="error" class="cover-error">
                                        <i class="el-icon-picture-outline"></i>
                                    </div>
                                </el-image>
                                <div class="cover-overlay">
                                    <span v-if="!item.issue" class="status-badge draft">草稿</span>
                                    <span v-if="item.isdel" class="status-badge deleted">已删除</span>
                                    <span v-if="item.ispublic == '0'" class="status-badge private">私密</span>
                                </div>
                            </div>

                            <!-- 博客内容 -->
                            <div class="blog-content">
                                <div class="blog-header">
                                    <h4 class="blog-title" @click="handlePreviewContent(index, item)">
                                        {{ item.title | ellipsis }}
                                    </h4>
                                    <div class="blog-meta">
                                        <span class="meta-item">
                                            <i class="el-icon-user"></i>
                                            {{ item.uName }}
                                        </span>
                                        <span class="meta-item">
                                            <i class="el-icon-time"></i>
                                            {{ item.createtime | fortTime }}
                                        </span>
                                        <span class="meta-item">
                                            <i class="el-icon-view"></i>
                                            {{ item.browsecount }}
                                        </span>
                                    </div>
                                </div>

                                <p class="blog-desc" @click="handlePreviewContent(index, item)">
                                    {{ item.description | ellipsisDis }}
                                </p>

                                <!-- 博客标签和分类 -->
                                <div class="blog-tags">
                                    <el-tag size="mini" v-if="item.isoriginal"
                                        :type="item.isoriginal === '原创' ? 'success' : 'info'" class="original-tag">
                                        {{ item.isoriginal }}
                                    </el-tag>
                                    <el-tag size="mini" type="info" class="category-tag">
                                        {{ item.tName }}
                                    </el-tag>
                                    <el-tag v-for="(tag, tagIndex) in item.blogTags" :key="tagIndex" size="mini"
                                        :type="tag.color || 'primary'" class="tag-item">
                                        {{ tag.name }}
                                    </el-tag>
                                </div>

                                <!-- 博客统计 -->
                                <div class="blog-stats">
                                    <div class="stat-item">
                                        <el-button type="text" size="mini" @click="handleLikeCount(index, item, 'like')"
                                            :class="{ liked: item.is_liked }">
                                            <i :class="item.is_liked ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
                                            {{ item.likes }}
                                        </el-button>
                                    </div>
                                    <div class="stat-item">
                                        <el-button type="text" size="mini"
                                            @click="handleCollectionCount(index, item, 'collection')"
                                            :class="{ collected: item.is_collection }">
                                            <i
                                                :class="item.is_collection ? 'el-icon-folder-checked' : 'el-icon-folder'"></i>
                                            {{ item.collection }}
                                        </el-button>
                                    </div>
                                    <div class="stat-item">
                                        <span class="stat-text">
                                            <i class="el-icon-chat-line-round"></i>
                                            {{ item.comments || 0 }}
                                        </span>
                                    </div>
                                </div>

                                <!-- 操作按钮 -->
                                <div class="blog-actions">
                                    <div class="action-group">
                                        <el-button v-if="hasEditPermission && !item.isDelete" type="text" size="mini"
                                            @click="$router.push('/home/editblog/' + item.bid)" class="action-btn edit">
                                            <i class="el-icon-edit"></i> 编辑
                                        </el-button>
                                        <el-popconfirm v-if="!item.isDelete" @confirm="handleDelete(index, item.bid)"
                                            title="确定删除这篇博客吗？">
                                            <el-button slot="reference" type="text" size="mini" class="action-btn delete">
                                                <i class="el-icon-delete"></i> 删除
                                            </el-button>
                                        </el-popconfirm>
                                        <el-button type="text" size="mini" @click="handlePreviewContent(index, item)"
                                            class="action-btn preview">
                                            <i class="el-icon-view"></i> 预览
                                        </el-button>
                                        <el-button v-if="item.isdel" type="text" size="mini" @click="recoveryBlog(item.bid)"
                                            class="action-btn recovery">
                                            <i class="el-icon-refresh-left"></i> 还原
                                        </el-button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 分页 -->
                <div class="pagination-container" v-if="pagination.totalBlogs > 0">
                    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
                        :current-page="pagination.currentPage" :page-sizes="pagination.pageSizes"
                        :page-size="pagination.pageSize" layout="total, sizes, prev, pager, next, jumper"
                        :total="pagination.totalBlogs" background class="blog-pagination">
                    </el-pagination>
                </div>
            </div>

            <!-- 右侧统计信息 -->
            <div class="statistics-sidebar">
                <!-- 统计卡片 -->
                <div class="stat-card">
                    <div class="stat-header">
                        <h4><i class="el-icon-data-analysis"></i> 统计概览</h4>
                    </div>
                    <div class="stat-content">
                        <div class="stat-item total">
                            <div class="stat-icon">
                                <i class="el-icon-document"></i>
                            </div>
                            <div class="stat-info">
                                <div class="stat-value">{{ pagination.totalBlogs }}</div>
                                <div class="stat-label">文章总数</div>
                            </div>
                        </div>
                        <div class="stat-item published">
                            <div class="stat-icon">
                                <i class="el-icon-s-promotion"></i>
                            </div>
                            <div class="stat-info">
                                <div class="stat-value">{{ publishedCount }}</div>
                                <div class="stat-label">已发布</div>
                            </div>
                        </div>
                        <div class="stat-item draft">
                            <div class="stat-icon">
                                <i class="el-icon-edit-outline"></i>
                            </div>
                            <div class="stat-info">
                                <div class="stat-value">{{ draftCount }}</div>
                                <div class="stat-label">草稿</div>
                            </div>
                        </div>
                        <div class="stat-item views">
                            <div class="stat-icon">
                                <i class="el-icon-view"></i>
                            </div>
                            <div class="stat-info">
                                <div class="stat-value">{{ totalViews }}</div>
                                <div class="stat-label">总浏览</div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 最近编辑 -->
                <div class="recent-card">
                    <div class="recent-header">
                        <h4><i class="el-icon-time"></i> 最近编辑</h4>
                    </div>
                    <div class="recent-list">
                        <div v-for="item in recentBlogs" :key="item.bid" class="recent-item"
                            @click="$router.push('/home/editblog/' + item.bid)">
                            <div class="recent-title">{{ item.title | ellipsisShort }}</div>
                            <div class="recent-time">{{ item.updtime | fortTime }}</div>
                        </div>
                        <div v-if="recentBlogs.length === 0" class="recent-empty">
                            暂无最近编辑的文章
                        </div>
                    </div>
                </div>

                <!-- 快速操作 -->
                <div class="quick-actions">
                    <h4><i class="el-icon-s-operation"></i> 快速操作</h4>
                    <div class="action-buttons">
                        <el-button type="primary" icon="el-icon-upload2" @click="importBlog" class="quick-btn">
                            导入文章
                        </el-button>
                        <el-button type="warning" icon="el-icon-download" @click="exportBlogs" class="quick-btn">
                            导出文章
                        </el-button>
                    </div>
                </div>
            </div>
        </div>

        <!-- 预览对话框 -->
        <el-dialog title="博客预览" :visible.sync="previewBlogDialogVisible" width="80%" top="5vh" class="preview-dialog">
            <div class="preview-header">
                <h3>{{ currentBlog.title }}</h3>
                <div class="preview-meta">
                    <el-tag size="small" type="info">{{ currentBlog.tName }}</el-tag>
                    <span><i class="el-icon-user"></i> {{ currentBlog.uName }}</span>
                    <span><i class="el-icon-time"></i> {{ currentBlog.createtime | fortTime }}</span>
                    <span><i class="el-icon-view"></i> {{ currentBlog.browsecount }}</span>
                </div>
            </div>
            <mavon-editor :value="currentBlog.content" defaultOpen="preview" :boxShadow="false" style="height: 65vh"
                :editable="false" :subfield="false" :toolbarsFlag="false">
            </mavon-editor>
            <span slot="footer" class="dialog-footer">
                <el-button @click="previewBlogDialogVisible = false">关闭</el-button>
                <el-button type="primary" @click="shareBlog">分享</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import { _typeAll, _getBlog, _delBlog, _delBlogH, _getBlogByTile, _addBlogBrowseCount, _addBlogLikesOrCollectionCount } from "@/api/api.js";
import { formatDate } from '@/utils/formatDate'

export default {
    name: "BlogView",
    data() {
        return {
            screenHeight: 950,
            baseUrl: "",
            uID: this.$store.state.login_user.id,
            uRole: ['admin', 'blog_manager'],
            inputSearchKeyWord: "",
            activeName: "first",
            tabindex: "0",
            pagination: {
                listData: null,
                currentPage: 1,
                pageSizes: [5, 10, 15, 20],
                pageSize: 5,
                totalPage: 1,
                currentTableData: [],
                totalBlogs: 0,
                loading: false,
            },
            blogCount:0,//文章数量
            activeTab: this.$store.state.blogActiveMenuIndex || "8",
            currentBlog: {},
            previewBlogDialogVisible: false,

            // 新增数据
            selectedCategoryId: null,
            selectedCategoryName: null,
            listSort: "newest",

            // 文章分类
            categories: [],

            // 统计信息
            publishedCount: 0,
            draftCount: 0,
            totalViews: 0,

            // 最近编辑
            recentBlogs: []
        }
    },
    computed: {
        hasEditPermission() {
            return this.uRole.includes(this.$store.state.login_user.roles[0].name);
        }
    },
    created() {
        this.initBlogs('');
        this.calculateStats();
        this.loadRecentBlogs();
    },
    mounted() {
        this.updateScreenHeight();
        window.addEventListener("resize", this.updateScreenHeight);
    },
    filters: {
        fortTime(val) {
            return formatDate(val);
        },
        ellipsis(value) {
            if (!value) return '';
            if (value.length > 30) {
                return value.slice(0, 30) + '...'
            }
            return value
        },
        ellipsisDis(value) {
            if (!value) return '';
            if (value.length > 100) {
                return value.slice(0, 100) + '...'
            }
            return value
        },
        ellipsisShort(value) {
            if (!value) return '';
            if (value.length > 20) {
                return value.slice(0, 20) + '...'
            }
            return value
        }
    },
    methods: {
        updateScreenHeight() {
            this.screenHeight = window.innerHeight - 300;
        },
        getCurrentPageData(listData, currentPage, pageSize) {
            let begin = (currentPage - 1) * pageSize;
            let end = currentPage * pageSize;
            this.pagination.currentTableData = listData.slice(begin, end);
        },
        initBlogs(type) {
            this.baseUrl = `/blog/getBlogByPage?1=1&page=1&size=5&uID=${this.uID}&title=${this.inputSearchKeyWord}`;
            let queryParams = "";
            switch (this.activeTab) {
                case "0": // 已发布
                    queryParams = "&ispublic=1&issue=1&isdel=0&authorID=" + this.uID;
                    break;
                case "1": // 原创
                    queryParams = "&isoriginal=原创&issue=1&isdel=0&authorID=" + this.uID;
                    break;
                case "2": // 转载
                    queryParams = "&isoriginal=转载&issue=1&isdel=0&authorID=" + this.uID;
                    break;
                case "3": // 草稿
                    queryParams = "&issue=0&isdel=0&authorID=" + this.uID;
                    break;
                case "4": // 私密
                    queryParams = "&ispublic=0&issue=1&isdel=0&authorID=" + this.uID;
                    break;
                case "5": // 回收站
                    queryParams = "&isdel=1&authorID=" + this.uID;
                    break;
                case "6": // 点赞
                    queryParams = "&isLiked=1";
                    break;
                case "7": // 收藏
                    queryParams = "&isCollection=1";
                    break;
                case "8": // 文章大厅
                    queryParams = "&ispublic=1&issue=1&isdel=0";
                    break;
            }

            // 添加类型筛选
            if (this.selectedCategoryId !== null) {
                queryParams += "&type=" + this.selectedCategoryId;
            }

            this.baseUrl += queryParams;

            this.pagination.loading = true;

            _getBlog(this.baseUrl).then(res => {
                if (res.data.data) {
                    this.pagination.totalBlogs = Number(res.data.data.length);

                    this.pagination.listData = res.data.data;
                    this.pagination.totalPage = Math.ceil(this.pagination.listData.length / this.pagination.pageSize);
                    this.pagination.totalPage = this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

                    this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize);

                    // 计算统计信息
                    this.calculateStats();
                    // 更新最近编辑
                    this.loadRecentBlogs();

                    if (type != 'category') {
                        this.blogCount = Number(res.data.data.length)
                        this.categories = []
                        this.pagination.listData.forEach(item => {
                            const count = this.categories.findIndex(c => c.id === item.typeid);
                            if (count > -1) {
                                this.categories[count].count = this.categories[count].count + 1;
                            } else {
                                let a = {
                                    id: item.typeid,
                                    name: item.tName,
                                    count: 1
                                }
                                this.categories.push(a)
                            }

                        });
                    }

                } else {
                    this.$message.warning(res.data.msg)
                }
                this.pagination.loading = false;

            }).catch(() => {
                this.pagination.loading = false;
            })
        },
        // 计算统计信息
        calculateStats() {
            if (!this.pagination.listData) return;

            this.publishedCount = this.pagination.listData.filter(blog =>
                blog.issue && !blog.isdel && blog.ispublic == '1'
            ).length;

            this.draftCount = this.pagination.listData.filter(blog =>
                !blog.issue
            ).length;

            this.totalViews = this.pagination.listData.reduce((sum, blog) =>
                sum + (parseInt(blog.browsecount) || 0), 0
            );
        },
        // 加载最近编辑的文章
        loadRecentBlogs() {
            if (!this.pagination.listData) {
                this.recentBlogs = [];
                return;
            }

            // 按更新时间排序，取前5条
            this.recentBlogs = [...this.pagination.listData]
                .sort((a, b) => new Date(b.updtime || b.createtime) - new Date(a.updtime || a.createtime))
                .slice(0, 5);
        },
        // 选择分类
        selectCategory(categoryId) {
            this.selectedCategoryId = categoryId;
            this.selectedCategoryName = categoryId ?
                this.categories.find(c => c.id === categoryId)?.name : null;
            this.pagination.currentPage = 1;
            this.initBlogs('category');
        },
        // 导入博客
        importBlog() {
            this.$message.info('导入功能开发中...');
        },
        // 导出博客
        exportBlogs() {
            this.$message.info('导出功能开发中...');
        },
        // 分享博客
        shareBlog() {
            this.$message.info('分享功能开发中...');
        },
        //搜索文章
        searchByTitle() {
            this.initBlogs('');
        },
        handleDelete(index, bid) {
            if (this.activeTab === '5') {
                _delBlog(bid).then(res => {
                    this.$message.success("彻底删除成功!");
                    this.initBlogs('');
                })
            } else {
                _delBlogH(bid, "1").then(res => {
                    this.$message.success("删除成功!");
                    this.initBlogs('');
                })
            }
        },
        handlePreviewContent(index, row) {
            this.currentBlog = row;
            this.previewBlogDialogVisible = true;
            _addBlogBrowseCount(row.bid).then(res => {
                if (res.data.status === 200) {
                    row.browsecount = res.data.data;
                }
            })
        },
        handleLikeCount(index, row, typeOp) {
            _addBlogLikesOrCollectionCount(row.bid, this.$store.state.login_user.id, typeOp).then(res => {
                if (res.data.data) {
                    row.is_liked = 1;
                    row.likes = Number(row.likes) + 1;
                } else {
                    row.is_liked = 0;
                    row.likes = Math.max(0, Number(row.likes) - 1);
                }
            })
        },
        handleCollectionCount(index, row, typeOp) {
            _addBlogLikesOrCollectionCount(row.bid, this.$store.state.login_user.id, typeOp).then(res => {
                if (res.data.data) {
                    row.is_collection = 1;
                    row.collection = Number(row.collection) + 1;
                } else {
                    row.is_collection = 0;
                    row.collection = Math.max(0, Number(row.collection) - 1);
                }
            })
        },
        handleNavTagSelect(key) {
            this.activeTab = key;
            this.$store.commit('setActiveMenuIndex', key);
            this.inputSearchKeyWord = "";
            this.selectedCategoryId = null;
            this.initBlogs('');
        },
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
        handleSizeChange(val) {
            this.pagination.pageSize = val;
            this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize);
        },
        handleCurrentChange(val) {
            this.pagination.currentPage = val;
            this.getCurrentPageData(this.pagination.listData, this.pagination.currentPage, this.pagination.pageSize);
        },
        recoveryBlog(val) {
            _delBlogH(val, "0").then(res => {
                this.$message.success("还原成功!");
                this.initBlogs('');
            })
        }
    },
    beforeDestroy() {
        window.removeEventListener("resize", this.updateScreenHeight);
    }
}
</script>

<style scoped>
/* 顶部导航栏样式（与营养知识页面完全一致） */
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

/* 主内容区 */
.blog-management-container {
    background-color: #f5f7fa;
    min-height: calc(100vh - 100px);
    padding: 0 0 20px 0;
}

.management-main {
    display: flex;
    gap: 20px;
}

/* 左侧分类导航 */
.category-sidebar {
    width: 260px;
    flex-shrink: 0;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    display: flex;
    flex-direction: column;
}

.sidebar-header {
    padding: 20px 20px 15px;
    border-bottom: 1px solid #f0f0f0;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.sidebar-header h3 {
    margin: 0;
    font-size: 16px;
    color: #303133;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 8px;
}

.category-list {
    flex: 1;
    padding: 15px 0;
    overflow-y: auto;
}

.category-item {
    display: flex;
    align-items: center;
    padding: 12px 20px;
    cursor: pointer;
    transition: all 0.3s;
    border-left: 3px solid transparent;
    position: relative;
}

.category-item:hover {
    background-color: #f5f7fa;
}

.category-item.active {
    background-color: #ecf5ff;
    border-left-color: #409EFF;
}

.category-item.has-draft::before {
    content: '';
    position: absolute;
    right: 20px;
    top: 50%;
    width: 8px;
    height: 8px;
    background: #E6A23C;
    border-radius: 50%;
    transform: translateY(-50%);
}

.category-icon {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    margin-right: 12px;
    color: white;
}

.category-icon i {
    font-size: 18px;
}

.category-info {
    flex: 1;
}

.category-name {
    display: block;
    font-size: 14px;
    color: #303133;
    font-weight: 500;
    margin-bottom: 4px;
}

.category-item.active .category-name {
    color: #409EFF;
}

.article-count {
    font-size: 12px;
    color: #909399;
}

.draft-badge {
    background-color: #E6A23C;
    color: white;
    font-size: 11px;
    padding: 2px 6px;
    border-radius: 10px;
    min-width: 20px;
    text-align: center;
}

.category-actions {
    padding: 15px 20px;
    border-top: 1px solid #f0f0f0;
}

/* 中间博客列表 */
.blog-list-main {
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
    align-items: center;
    gap: 15px;
}

.create-btn {
    padding: 10px 20px;
    font-weight: 500;
}

/* 博客列表容器 */
.blog-list-container {
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
    gap: 20px;
    padding: 20px;
    border-radius: 8px;
    background: #fafafa;
}

.skeleton-cover {
    width: 240px;
    height: 160px;
    background: #f0f0f0;
    border-radius: 6px;
    animation: pulse 1.5s ease-in-out infinite;
}

.skeleton-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.skeleton-title {
    height: 24px;
    width: 60%;
    background: #f0f0f0;
    border-radius: 4px;
    animation: pulse 1.5s ease-in-out infinite;
}

.skeleton-desc {
    height: 16px;
    width: 80%;
    background: #f0f0f0;
    border-radius: 4px;
    animation: pulse 1.5s ease-in-out infinite;
}

.skeleton-meta {
    height: 12px;
    width: 40%;
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
}

.empty-state i {
    font-size: 60px;
    color: #c0c4cc;
    margin-bottom: 20px;
}

.empty-state p {
    color: #909399;
    font-size: 16px;
    margin-bottom: 25px;
}

.empty-action {
    padding: 12px 24px;
}

/* 博客列表 */
.blog-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.blog-card {
    display: flex;
    gap: 20px;
    padding: 20px;
    border-radius: 10px;
    border: 1px solid #e4e7ed;
    transition: all 0.3s;
}

.blog-card:hover {
    border-color: #cbd5e1;
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
}

.draft-blog {
    border-left: 4px solid #E6A23C;
}

.deleted-blog {
    border-left: 4px solid #F56C6C;
}

.private-blog {
    border-left: 4px solid #67C23A;
}

.blog-cover {
    position: relative;
    width: 240px;
    height: 100%;
    flex-shrink: 0;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
}

.cover-image {
    width: 100%;
    height: 100%;
    transition: transform 0.5s ease;
}

.cover-error {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    color: #c0c4cc;
    font-size: 32px;
}

.blog-cover:hover .cover-image {
    transform: scale(1.05);
}

.cover-overlay {
    position: absolute;
    top: 10px;
    right: 10px;
    display: flex;
    gap: 5px;
}

.status-badge {
    padding: 4px 10px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 500;
    color: white;
}

.status-badge.draft {
    background: rgba(230, 162, 60, 0.9);
}

.status-badge.deleted {
    background: rgba(245, 108, 108, 0.9);
}

.status-badge.private {
    background: rgba(103, 194, 58, 0.9);
}

/* 博客内容 */
.blog-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    min-width: 0;
}

.blog-header {
    margin-bottom: 12px;
}

.blog-title {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 10px 0;
    cursor: pointer;
    transition: color 0.3s;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.blog-title:hover {
    color: #409EFF;
}

.blog-meta {
    display: flex;
    gap: 15px;
    font-size: 13px;
    color: #909399;
}

.meta-item {
    display: flex;
    align-items: center;
    gap: 5px;
}

.blog-desc {
    flex: 1;
    font-size: 14px;
    color: #606266;
    line-height: 1.6;
    margin: 0 0 15px 0;
    cursor: pointer;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
}

.blog-desc:hover {
    color: #409EFF;
}

.blog-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-bottom: 15px;
}

.original-tag,
.category-tag,
.tag-item {
    margin-right: 5px;
}

.blog-stats {
    display: flex;
    gap: 15px;
    margin-bottom: 15px;
}

.stat-item .el-button {
    color: #909399;
    padding: 0;
}

.stat-item .el-button:hover,
.stat-item .el-button.liked,
.stat-item .el-button.collected {
    color: #409EFF;
}

.stat-item .el-button.liked {
    color: #E6A23C;
}

.stat-item .el-button.collected {
    color: #F56C6C;
}

.stat-text {
    display: flex;
    align-items: center;
    gap: 5px;
    font-size: 13px;
    color: #909399;
}

.blog-actions {
    border-top: 1px solid #f0f0f0;
    padding-top: 15px;
}

.action-group {
    display: flex;
    gap: 15px;
}

.action-btn {
    padding: 6px 0;
    font-size: 13px;
    transition: all 0.3s;
}

.action-btn.edit {
    color: #409EFF;
}

.action-btn.edit:hover {
    color: #409EFF;
    font-weight: 500;
}

.action-btn.delete {
    color: #F56C6C;
}

.action-btn.delete:hover {
    color: #F56C6C;
    font-weight: 500;
}

.action-btn.preview {
    color: #67C23A;
}

.action-btn.preview:hover {
    color: #67C23A;
    font-weight: 500;
}

.action-btn.recovery {
    color: #E6A23C;
}

.action-btn.recovery:hover {
    color: #E6A23C;
    font-weight: 500;
}

/* 分页 */
.pagination-container {
    padding: 20px;
    border-top: 1px solid #f0f0f0;
}

.blog-pagination {
    justify-content: center;
}

/* 右侧统计信息 */
.statistics-sidebar {
    width: 280px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
    gap: 20px;
}

.stat-card,
.recent-card,
.quick-actions {
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.stat-header,
.recent-header {
    padding: 15px 20px;
    border-bottom: 1px solid #f0f0f0;
}

.stat-header h4,
.recent-header h4 {
    margin: 0;
    font-size: 15px;
    color: #303133;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 8px;
}

.stat-content {
    padding: 20px;
}

.stat-item {
    display: flex;
    align-items: center;
    gap: 15px;
    padding: 15px 0;
}

.stat-item:not(:last-child) {
    border-bottom: 1px solid #f5f5f5;
}

.stat-icon {
    width: 40px;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    color: white;
}

.stat-item.total .stat-icon {
    background: linear-gradient(135deg, #409EFF, #79bbff);
}

.stat-item.published .stat-icon {
    background: linear-gradient(135deg, #67C23A, #95d475);
}

.stat-item.draft .stat-icon {
    background: linear-gradient(135deg, #E6A23C, #eebe77);
}

.stat-item.views .stat-icon {
    background: linear-gradient(135deg, #F56C6C, #f89898);
}

.stat-icon i {
    font-size: 20px;
}

.stat-info {
    flex: 1;
}

.stat-value {
    font-size: 24px;
    font-weight: 700;
    color: #303133;
    line-height: 1;
    margin-bottom: 5px;
}

.stat-label {
    font-size: 13px;
    color: #909399;
}

/* 最近编辑 */
.recent-list {
    padding: 15px 0;
}

.recent-item {
    padding: 10px 20px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.recent-item:hover {
    background-color: #f5f7fa;
}

.recent-title {
    font-size: 14px;
    color: #303133;
    margin-bottom: 4px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.recent-time {
    font-size: 12px;
    color: #909399;
}

.recent-empty {
    padding: 20px;
    text-align: center;
    color: #909399;
    font-size: 14px;
}

/* 快速操作 */
.quick-actions {
    padding: 20px;
}

.quick-actions h4 {
    margin: 0 0 15px 0;
    font-size: 15px;
    color: #303133;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 8px;
}

.action-buttons {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.quick-btn {
    width: 100%;
    justify-content: flex-start;
    padding: 12px 20px;
}

/* 预览对话框 */
.preview-dialog ::v-deep .el-dialog__header {
    padding: 20px;
    border-bottom: 1px solid #e4e7ed;
}

.preview-dialog ::v-deep .el-dialog__body {
    padding: 0;
}

.preview-header {
    padding: 20px;
    border-bottom: 1px solid #e4e7ed;
}

.preview-header h3 {
    margin: 0 0 15px 0;
    color: #303133;
    font-size: 20px;
    font-weight: 600;
}

.preview-meta {
    display: flex;
    align-items: center;
    gap: 15px;
    font-size: 14px;
    color: #909399;
}

.preview-meta span {
    display: flex;
    align-items: center;
    gap: 5px;
}

/* 响应式调整 */
@media (max-width: 1200px) {
    .management-main {
        flex-direction: column;
    }

    .category-sidebar,
    .statistics-sidebar {
        width: 100%;
    }

    .category-sidebar {
        order: 1;
    }

    .blog-list-main {
        order: 2;
    }

    .statistics-sidebar {
        order: 3;
        flex-direction: row;
        flex-wrap: wrap;
    }

    .stat-card,
    .recent-card,
    .quick-actions {
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

    .blog-card {
        flex-direction: column;
    }

    .blog-cover {
        width: 100%;
        height: 200px;
    }
}
</style>