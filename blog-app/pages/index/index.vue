<template>
	<view class="home-container">
		<!-- 背景渐变 -->
		<view class="background-gradient"></view>

		<!-- 自定义顶部导航栏组件 -->
		<custom-navbar :statusBarHeight="statusBarHeight" title="首页" />

		<!-- 主要内容区域 -->
		<view class="content-wrapper" :style="{ 
			paddingTop: (statusBarHeight + 70) + 'px',
			height: `calc(100vh - ${statusBarHeight + 15 }px`
		}">
			<scroll-view class="main-content" scroll-y :refresher-enabled="true" @refresherrefresh="onRefresh"
				:show-scrollbar="false" refresher-background="transparent" :style="{ height: '100%' }">

				<!-- 用户信息卡片 -->
				<view class="user-card glass-card">
					<!-- 用户信息内容保持不变 -->
					<view class="user-basic">
						<view class="avatar-section">
							<view class="avatar-wrapper">
								<image class="user-avatar"
									:src="getFullAvatarUrl(userInfo.avatar) || '/static/images/default-avatar.png'"
									mode="aspectFill"></image>
								<view class="online-dot"></view>
								<view class="avatar-glow"></view>
							</view>
						</view>
						<view class="user-info">
							<text class="user-name">{{ userInfo.nickname || userInfo.username || '用户' }}</text>
							<text class="user-role">{{ formatRoles(userInfo.roles) }}</text>
							<view class="user-meta">
								<text class="user-id">ID: {{ userInfo.id || '--' }}</text>
								<text class="user-welcome">欢迎回来！</text>
							</view>
						</view>
					</view>
				</view>

				<!-- 快捷操作 -->
				<view class="quick-actions-section glass-card">
					<view class="section-header">
						<text class="section-title">快捷操作</text>
					</view>
					<view class="actions-grid">
						<view class="action-item"
							@click="navigateTo('/pages/life/add-daily-flow/add-daily-flow?type=income')">
							<view class="action-icon">
								<image src="@/static/icon/jishouru.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">记收入</text>
						</view>
						<view class="action-item"
							@click="navigateTo('/pages/life/add-daily-flow/add-daily-flow?type=expense')">
							<view class="action-icon">
								<image src="@/static/icon/jizhichu.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">记支出</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/blog/write/write')">
							<view class="action-icon">
								<image src="@/static/icon/xieboke.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">写笔记</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/aunt/aunt-dashboard/aunt-dashboard')">
							<view class="action-icon">
								<image src="@/static/icon/yuejing.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">经期预测</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/other/priceTracker/priceTracker')">
							<view class="action-icon">
								<image src="@/static/icon/zhishikuchaxun.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">商品记录</text>
						</view>
					</view>
				</view>

				<!-- 功能模块-财富 -->
				<view class="quick-actions-section glass-card">
					<view class="section-header">
						<text class="section-title">资产</text>
					</view>
					<view class="actions-grid">
						<view class="action-item" @click="navigateTo('/pages/life/wealth-report/wealth-report')">
							<view class="action-icon">
								<image src="@/static/icon/caiwubaogao.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">收支报表</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/life/daily-flow/daily-flow')">
							<view class="action-icon">
								<image src="@/static/icon/zhangbu.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">家庭账簿</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/life/daily-detail/daily-detail')">
							<view class="action-icon">
								<image src="@/static/icon/shouzhibaobiao.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">账簿明细</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/life/flow-analysis/flow-analysis')">
							<view class="action-icon">
								<image src="@/static/icon/liushuifenxi.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">流水分析</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/life/consumption-type/consumption-type')">
							<view class="action-icon">
								<image src="@/static/icon/leixing.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">消费类型</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/life/user-deposit/user-deposit')">
							<view class="action-icon">
								<image src="@/static/icon/ziyuan.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">用户资产</text>
						</view>
					</view>
				</view>
				<!-- 功能模块-营养管理 -->
				<view class="quick-actions-section glass-card">
					<view class="section-header">
						<text class="section-title">营养科学</text>
					</view>
					<view class="actions-grid">
						<view class="action-item" @click="navigateTo('/pages/nutrition/food-nutrition/food-nutrition')">
							<view class="action-icon">
								<image src="@/static/icon/yingyangguanli.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">食物营养</text>
						</view>
						<view class="action-item"
							@click="navigateTo('/pages/nutrition/nutritional-recipe/nutritional-recipe')">
							<view class="action-icon">
								<image src="@/static/icon/shipu1.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">营养食谱</text>
						</view>
						<view class="action-item"
							@click="navigateTo('/pages/nutrition/nutritional-recipes/nutritional-recipes')">
							<view class="action-icon">
								<image src="@/static/icon/zhishiku.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">营养方案</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/nutrition/order-food/order-food')">
							<view class="action-icon">
								<image src="@/static/icon/shipu.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">点餐</text>
						</view>
					</view>
				</view>
				<!-- 功能模块-知识库 -->
				<view class="quick-actions-section glass-card">
					<view class="section-header">
						<text class="section-title">知识库</text>
					</view>
					<view class="actions-grid">
						<view class="action-item" @click="navigateTo('/pages/blog/knowledge-base/knowledge-base')">
							<view class="action-icon">
								<image src="@/static/icon/zhishiku2.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">知识库</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/blog/blog-dashboard/blog-dashboard')">
							<view class="action-icon">
								<image src="@/static/icon/a-leixingtianjiaBImoban.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">知识看板</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/blog/knowledge-tag/knowledge-tag')">
							<view class="action-icon">
								<image src="@/static/icon/biaoqian1.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">知识标签</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/blog/knowledge-type/knowledge-type')">
							<view class="action-icon">
								<image src="@/static/icon/4leixing.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">知识类型</text>
						</view>
					</view>
				</view>
				<!-- 功能模块-族谱 -->
				<view class="quick-actions-section glass-card">
					<view class="section-header">
						<text class="section-title">族谱</text>
					</view>
					<view class="actions-grid">
						<view class="action-item" @click="navigateTo('/pages/family/family-tree/family-tree')">
							<view class="action-icon">
								<image src="@/static/icon/zupufengmian.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">族谱</text>
						</view>
						<!-- <view class="action-item" @click="navigateTo('/pages/family/family-intro/family-intro')">
							<view class="action-icon">
								<image src="@/static/icon/tubiao-jingshu-02.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">族谱简介[test]</text>
						</view> -->
						<!-- <view class="action-item" @click="navigateTo('/pages/blog/blog-dashboard/blog-dashboard')">
							<view class="action-icon">
								<image src="@/static/icon/zupufengmian.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">测试用例</text>
						</view> -->
					</view>
				</view>
				<!-- 功能模块-经期 -->
				<view class="quick-actions-section glass-card">
					<view class="section-header">
						<text class="section-title">经期</text>
					</view>
					<view class="actions-grid">
						<view class="action-item" @click="navigateTo('/pages/aunt/aunt-dashboard/aunt-dashboard')">
							<view class="action-icon">
								<image src="@/static/icon/yuejing.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">经期预测</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/aunt/aunt-list/aunt-list')">
							<view class="action-icon">
								<image src="@/static/icon/jingqizhushou.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">历史记录</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/aunt/aunt-add/aunt-add')">
							<view class="action-icon">
								<image src="@/static/icon/jingqijilu-01.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">经期记录</text>
						</view>
					</view>
				</view>

				<!-- 功能模块-课时费 -->
				<view class="quick-actions-section glass-card">
					<view class="section-header">
						<text class="section-title">课时费</text>
					</view>
					<view class="actions-grid">
						<view class="action-item" @click="navigateTo('/pages/class/class-fee-rule/class-fee-rule')">
							<view class="action-icon">
								<image src="@/static/icon/weifaguizeguanli.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">课时费规则</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/class/basic-fee/basic-fee')">
							<view class="action-icon">
								<image src="@/static/icon/gerenkeshi.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">课时费计算</text>
						</view>
					</view>
				</view>

				<!-- 功能模块-奖品 -->
				<view class="quick-actions-section glass-card">
					<view class="section-header">
						<text class="section-title">奖品</text>
					</view>
					<view class="actions-grid">
						<view class="action-item" @click="navigateTo('/pages/prize/prize-setting/prize-setting')">
							<view class="action-icon">
								<image src="@/static/icon/jiangpinguanli1.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">奖品设置</text>
						</view>
						<view class="action-item" @click="navigateTo('/pages/prize/prizes/prizes')">
							<view class="action-icon">
								<image src="@/static/icon/zhuanpanshezhi.png" mode="aspectFit" class="icon" />
							</view>
							<text class="action-label">幸运转盘</text>
						</view>
					</view>
				</view>

				<!-- 最近动态 -->
				<view class="recent-activities glass-card" v-if="recentActivities.length < 0">
					<view class="section-header">
						<text class="section-title">最近动态</text>
						<text class="section-more" @click="navigateTo('/pages/activity/all-activities')">查看全部</text>
					</view>
					<view class="activities-list">
						<view class="activity-item" v-for="activity in recentActivities" :key="activity.id"
							@click="handleActivityClick(activity)">
							<view class="activity-icon" :class="activity.type">
								<text :class="getActivityIcon(activity.type)"></text>
							</view>
							<view class="activity-content">
								<text class="activity-title">{{ activity.title }}</text>
								<text class="activity-time">{{ formatTime(activity.time) }}</text>
							</view>
							<view class="activity-arrow">
								<text class="iconfont icon-arrow-right"></text>
							</view>
						</view>
					</view>
				</view>

				<!-- 空状态 -->
				<!-- <view class="empty-state glass-card" v-else >
					<view class="empty-icon">
						<text class="iconfont icon-wushuju"></text>
					</view>
					<text class="empty-text">暂无动态记录</text>
					<text class="empty-desc">开始使用系统功能后，动态将显示在这里</text>
				</view> -->

				<!-- 底部提示 -->
				<view class="footer-tips">
					<text class="tip-text">当前版本 {{this.currentVersion.version}} · 最后更新
						{{ formatDate(lastUpdateTime) }}</text>
				</view>

				<!-- 安全区域占位 -->
				<view class="safe-area-bottom"></view>
			</scroll-view>
		</view>

		<!-- 自定义底部导航栏组件 -->
		<custom-tabbar :currentTab="0" />

		<!-- 所有模块弹窗 -->
		<view class="popup-mask" v-if="showModulesPopup" @tap="closeModulesPopup">
			<view class="all-modules-popup glass-popup" @tap.stop>
				<view class="popup-header">
					<text class="popup-title">所有功能</text>
					<text class="popup-close" @tap="closeModulesPopup">
						<text class="iconfont icon-guanbi"></text>
					</text>
				</view>
				<scroll-view class="modules-list" scroll-y>
					<view class="module-list-item" v-for="module in allModules" :key="module.path"
						@tap="navigateToModule(module.path)">
						<view class="module-list-icon" :class="module.iconClass">
							<text :class="module.icon"></text>
						</view>
						<view class="module-list-content">
							<text class="module-list-name">{{ module.name }}</text>
							<text class="module-list-desc">{{ module.desc }}</text>
						</view>
						<view class="module-list-arrow">
							<text class="iconfont icon-arrow-right"></text>
						</view>
					</view>
				</scroll-view>
			</view>
		</view>

	</view>
</template>

<script>
	import CustomNavbar from '@/components/custom-navbar/custom-navbar.vue'
	import CustomTabbar from '@/components/custom-tabbar/custom-tabbar.vue'
	import {
		IMG_URL
	} from '@/api/config.js'

	export default {
		components: {
			CustomNavbar,
			CustomTabbar
		},
		data() {
			return {
				statusBarHeight: 0,
				currentVersion: '1.0.0',
				userInfo: {},
				recentActivities: [],
				showModulesPopup: false,
				lastUpdateTime: new Date(),
				currentTab: 0,
				unreadNotifications: 3,
				// tab徽章数据
				tabBadges: {
					1: 3, // 财富tab显示3个未读
					2: 5 // 博客tab显示5个未读
				},
				allModules: [{
						name: '财富报表',
						path: '/pages/life/wealth-report/wealth-report',
						icon: 'iconfont icon-baobiao',
						iconClass: 'wealth',
						desc: '查看财务分析报表'
					},
					{
						name: '用户资产',
						path: '/pages/life/user-deposit/user-deposit',
						icon: 'iconfont icon-zichan',
						iconClass: 'wealth',
						desc: '管理个人资产'
					},
					{
						name: '流水类型',
						path: '/pages/life/consumption-type/consumption-type',
						icon: 'iconfont icon-leixing',
						iconClass: 'wealth',
						desc: '配置收支分类'
					},
					{
						name: '博客看板',
						path: '/pages/blog/blog-dashboard/blog-dashboard',
						icon: 'iconfont icon-kanban',
						iconClass: 'blog',
						desc: '博客数据统计'
					},
					{
						name: '文章管理',
						path: '/pages/blog/all-blog/all-blog',
						icon: 'iconfont icon-wenzhang',
						iconClass: 'blog',
						desc: '管理所有文章'
					},
					{
						name: '用户列表',
						path: '/pages/user/user-list/user-list',
						icon: 'iconfont icon-liebiao',
						iconClass: 'user',
						desc: '查看系统用户'
					},
					{
						name: '权限管理',
						path: '/pages/user/role/role',
						icon: 'iconfont icon-quanxian',
						iconClass: 'user',
						desc: '配置用户权限'
					},
					{
						name: '经期看板',
						path: '/pages/aunt/aunt-dashboard/aunt-dashboard',
						icon: 'iconfont icon-kanban1',
						iconClass: 'health',
						desc: '健康数据统计'
					}
				]
			}
		},

		onLoad() {
			this.getStatusBarHeight();
			this.loadUserInfo();
			this.loadRecentActivities();
		},
		onReady() {
			uni.hideTabBar();
		},
		onShow() {
			this.refreshData();
			this.loadUserInfo();
		},

		onPullDownRefresh() {
			this.onRefresh();
		},

		methods: {
			// 获取状态栏高度
			getStatusBarHeight() {
				const systemInfo = uni.getSystemInfoSync();
				this.statusBarHeight = systemInfo.statusBarHeight;
			},
			// 处理通知点击
			handleNotification() {
				// 可以在这里添加额外的逻辑
			},
			// 图片资源路径处理
			getFullAvatarUrl(avatarPath) {
				if (!avatarPath) return '/static/defaultImg.png';

				if (avatarPath.startsWith('http')) {
					return avatarPath;
				}

				if (avatarPath.startsWith('/')) {
					return IMG_URL + avatarPath;
				}

				return avatarPath;
			},
			// 加载用户信息
			async loadUserInfo() {
				try {
					const token = uni.getStorageSync(this.$constants.TOKEN);
					const userInfo = uni.getStorageSync(this.$constants.USER_INFO) || {};
					userInfo.avatar = userInfo.avatar
					this.userInfo = userInfo;
					this.currentVersion = await this.getCurrentVersion();
					if (!token || !userInfo) {
						// 如果没有登录信息，跳转到登录页
						setTimeout(() => {
							uni.reLaunch({
								url: this.$PATHS.LOGIN
							});
						}, 1000);
					}
				} catch (error) {
					console.error('加载用户信息失败:', error);
				}
			},

			// 加载最近动态
			async loadRecentActivities() {
				try {
					// 模拟数据
					this.recentActivities = [{
							id: 1,
							type: 'blog',
							title: '发布了新文章《Vue3最佳实践》',
							time: new Date(Date.now() - 2 * 60 * 60 * 1000)
						},
						{
							id: 2,
							type: 'wealth',
							title: '新增一笔收入记录',
							time: new Date(Date.now() - 5 * 60 * 60 * 1000)
						},
						{
							id: 3,
							type: 'health',
							title: '完成了健康记录',
							time: new Date(Date.now() - 24 * 60 * 60 * 1000)
						}
					];
				} catch (error) {
					console.error('加载最近动态失败:', error);
				}
			},

			// 检查权限
			hasPermission(roles) {
				if (!roles || roles.length === 0) return true;
				const userRoles = this.userInfo.roles || [];
				const roleNames = userRoles.map(role => typeof role === 'object' ? role.name : role);
				return roles.some(role => roleNames.includes(role));
			},

			// 格式化角色显示
			formatRoles(roles) {
				if (!roles || !Array.isArray(roles)) return '普通用户';
				const roleMap = {
					'admin': '管理员',
					'user_manager': '用户管理员',
					'blog_manager': '博客管理员',
					'user': '普通用户',
					'visitor': '访客'
				};
				return roles.map(role => {
					const roleName = typeof role === 'object' ? role.name : role;
					return roleMap[roleName] || roleName;
				}).join('、');
			},

			// 格式化时间
			formatTime(time) {
				if (!time) return '';
				const now = new Date();
				const diff = now - new Date(time);
				const minutes = Math.floor(diff / (1000 * 60));
				const hours = Math.floor(diff / (1000 * 60 * 60));
				const days = Math.floor(hours / 24);

				if (minutes < 1) return '刚刚';
				if (minutes < 60) return `${minutes}分钟前`;
				if (hours < 24) return `${hours}小时前`;
				if (days < 30) return `${days}天前`;

				return new Date(time).toLocaleDateString();
			},

			// 格式化日期
			formatDate(date) {
				if (!date) return '';
				const d = new Date(date);
				return `${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`;
			},

			// 格式化数字
			formatNumber(num) {
				return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
			},

			// 获取活动图标
			getActivityIcon(type) {
				const iconMap = {
					blog: 'iconfont icon-boke',
					wealth: 'iconfont icon-caifu',
					health: 'iconfont icon-jiankang'
				};
				return iconMap[type] || 'iconfont icon-tongzhi';
			},

			// 打开模块弹窗
			openModulesPopup() {
				this.showModulesPopup = true;
			},

			// 关闭模块弹窗
			closeModulesPopup() {
				this.showModulesPopup = false;
			},

			// 导航到模块
			navigateToModule(path) {
				this.closeModulesPopup();
				this.navigateTo(path);
			},

			// 通用导航
			navigateTo(url) {
				uni.navigateTo({
					url: url
				});
			},

			// 处理活动点击
			handleActivityClick(activity) {
				switch (activity.type) {
					case 'blog':
						this.navigateTo('/pages/blog/all-blog/all-blog');
						break;
					case 'wealth':
						this.navigateTo('/pages/life/daily-flow/daily-flow');
						break;
					case 'health':
						this.navigateTo('/pages/aunt/aunt-list/aunt-list');
						break;
				}
			},

			// 刷新数据
			refreshData() {
				this.loadRecentActivities();
			},

			// 下拉刷新
			onRefresh() {
				this.refreshData();
				setTimeout(() => {
					uni.stopPullDownRefresh();
					uni.showToast({
						title: '数据已刷新',
						icon: 'success'
					});
				}, 1000);
			},
			// 获取当前App版本信息
			getCurrentVersion() {
				return new Promise((resolve, reject) => {
					plus.runtime.getProperty(plus.runtime.appid, (inf) => {
						resolve({
							version: inf.version,
							versionCode: parseInt(inf.versionCode),
							name: inf.name
						});
					}, reject);
				});
			}

		}
	}
</script>

<style scoped lang="scss">
	@import "@/scss/variables.scss";

	.home-container {
		min-height: 100vh;
		background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
		position: relative;
	}

	.background-gradient {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		height: 100%;
		background: linear-gradient(135deg, $primary-color 0%, $accent-color 100%);
		z-index: 1;
	}

	/* 玻璃态效果卡片 */
	.glass-card {
		background: rgba(255, 255, 255, 0.9);
		backdrop-filter: blur(20rpx);
		border-radius: 24rpx;
		border: 1rpx solid rgba(255, 255, 255, 0.3);
		box-shadow: $shadow-light;
	}

	.glass-popup {
		background: rgba(255, 255, 255, 0.95);
		backdrop-filter: blur(30rpx);
		border: 1rpx solid rgba(255, 255, 255, 0.4);
	}

	/* 内容包装器 */
	.content-wrapper {
		position: relative;
		z-index: 2;
		box-sizing: border-box;
		overflow: hidden;
	}

	/* 主要内容区域 */
	.main-content {
		/* 高度设置为100%填充父容器 */
		box-sizing: border-box;
		overflow: hidden;
		background: transparent;
		padding: 0 30rpx;
	}

	/* 用户信息卡片 */
	.user-card {
		padding: 40rpx;
		margin-bottom: 30rpx;

		.user-basic {
			display: flex;
			align-items: center;
			margin-bottom: 40rpx;

			.avatar-section {
				position: relative;
				margin-right: 30rpx;

				.avatar-wrapper {
					position: relative;

					.user-avatar {
						width: 120rpx;
						height: 120rpx;
						border-radius: 50%;
						border: 4rpx solid rgba(255, 255, 255, 0.8);
						box-shadow: $shadow-medium;
						position: relative;
						z-index: 2;
					}

					.online-dot {
						position: absolute;
						bottom: 8rpx;
						right: 8rpx;
						width: 24rpx;
						height: 24rpx;
						background: $success-color;
						border: 3rpx solid $bg-white;
						border-radius: 50%;
						z-index: 3;
						box-shadow: 0 2rpx 8rpx rgba(82, 196, 26, 0.4);
					}

					.avatar-glow {
						position: absolute;
						top: 0rpx;
						left: 0rpx;
						right: 0rpx;
						bottom: 9rpx;
						background: linear-gradient(135deg, $primary-color, $accent-color);
						border-radius: 50%;
						z-index: 1;
						animation: glow 1s ease-in-out infinite alternate;
					}
				}
			}

			.user-info {
				flex: 1;

				.user-name {
					display: block;
					font-size: 36rpx;
					font-weight: bold;
					color: $text-primary;
					margin-bottom: 8rpx;
				}

				.user-role {
					display: block;
					font-size: 26rpx;
					color: $primary-color;
					margin-bottom: 12rpx;
					font-weight: 500;
				}

				.user-meta {
					display: flex;
					justify-content: space-between;
					align-items: center;

					.user-id {
						font-size: 24rpx;
						color: $text-light;
					}

					.user-welcome {
						font-size: 24rpx;
						color: $text-secondary;
					}
				}
			}
		}

		.user-stats {
			display: grid;
			grid-template-columns: repeat(3, 1fr);
			gap: 20rpx;
			border-top: 1rpx solid rgba(0, 0, 0, 0.06);
			padding-top: 30rpx;

			.stat-item {
				text-align: center;
				padding: 20rpx 10rpx;
				border-radius: 16rpx;
				transition: all 0.3s ease;

				&:active {
					background: rgba($primary-color, 0.1);
					transform: scale(0.95);
				}

				.stat-number {
					display: block;
					font-size: 32rpx;
					font-weight: bold;
					color: $text-primary;
					margin-bottom: 8rpx;

					&.positive {
						color: $success-color;
					}

					&.negative {
						color: $error-color;
					}
				}

				.stat-label {
					font-size: 24rpx;
					color: $text-secondary;
				}
			}
		}
	}

	/* 快捷操作区域 */
	.quick-actions-section {
		padding: 30rpx;
		margin-bottom: 30rpx;

		.section-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 30rpx;

			.section-title {
				font-size: 32rpx;
				font-weight: bold;
				color: $text-primary;
			}

			.section-more {
				font-size: 26rpx;
				color: $primary-color;
				padding: 12rpx 24rpx;
				border-radius: 20rpx;
				transition: all 0.3s ease;

				&:active {
					background: rgba($primary-color, 0.1);
				}
			}
		}

		.actions-grid {
			display: grid;
			grid-template-columns: repeat(4, 1fr);
			gap: 20rpx;

			.action-item {
				text-align: center;
				padding: 20rpx 10rpx;
				border-radius: 20rpx;
				transition: all 0.3s ease;

				&:active {
					transform: scale(0.95);
					background: rgba(0, 0, 0, 0.03);
				}

				.action-icon {
					display: flex;
					align-items: center;
					justify-content: center;

					&.income {
						background: linear-gradient(135deg, $success-color, #aed197);
					}

					&.expense {
						background: linear-gradient(135deg, $error-color, #ff7875);
					}

					&.blog {
						background: linear-gradient(135deg, $info-color, #40a9ff);
					}

					&.health {
						background: linear-gradient(135deg, $accent-color, #9254de);
					}
				}

				.action-label {
					font-size: 24rpx;
					color: $text-primary;
					font-weight: 500;
				}
			}
		}
	}

	/* 功能模块区域 */
	.modules-section {
		padding: 30rpx;
		margin-bottom: 30rpx;

		.section-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 30rpx;

			.section-title {
				font-size: 32rpx;
				font-weight: bold;
				color: $text-primary;
			}

			.section-more {
				font-size: 26rpx;
				color: $primary-color;
				padding: 12rpx 24rpx;
				border-radius: 20rpx;
				transition: all 0.3s ease;

				&:active {
					background: rgba($primary-color, 0.1);
				}
			}
		}

		.main-modules-grid {
			display: grid;
			grid-template-columns: 1fr;
			gap: 24rpx;
			margin-bottom: 30rpx;

			.module-card.large {
				background: linear-gradient(135deg, rgba($primary-color, 0.05), rgba($accent-color, 0.03));
				border-radius: 20rpx;
				padding: 30rpx;
				display: flex;
				align-items: center;
				position: relative;
				transition: all 0.3s ease;
				border: 1rpx solid rgba($primary-color, 0.1);

				&:active {
					transform: scale(0.98);
					background: linear-gradient(135deg, rgba($primary-color, 0.08), rgba($accent-color, 0.05));
					box-shadow: $shadow-light;
				}

				.module-icon {
					width: 80rpx;
					height: 80rpx;
					border-radius: 20rpx;
					display: flex;
					align-items: center;
					justify-content: center;
					margin-right: 24rpx;
					font-size: 36rpx;
					color: #fff;
					box-shadow: $shadow-medium;

					&.wealth {
						background: linear-gradient(135deg, $warning-color, #ffc53d);
					}

					&.blog {
						background: linear-gradient(135deg, $info-color, #40a9ff);
					}
				}

				.module-content {
					flex: 1;

					.module-name {
						display: block;
						font-size: 32rpx;
						font-weight: bold;
						color: $text-primary;
						margin-bottom: 8rpx;
					}

					.module-desc {
						font-size: 24rpx;
						color: $text-secondary;
					}
				}

				.module-badge {
					position: absolute;
					top: 20rpx;
					right: 20rpx;
					background: $error-color;
					color: #fff;
					font-size: 20rpx;
					padding: 4rpx 12rpx;
					border-radius: 20rpx;
					min-width: 32rpx;
					text-align: center;
					box-shadow: 0 2rpx 8rpx rgba(255, 77, 79, 0.3);
				}

				.module-arrow {
					.iconfont {
						font-size: 28rpx;
						color: $text-light;
					}
				}
			}
		}

		.secondary-modules-grid {
			display: grid;
			grid-template-columns: repeat(2, 1fr);
			gap: 24rpx;

			.module-card.small {
				background: rgba($primary-color, 0.03);
				border-radius: 20rpx;
				padding: 40rpx 24rpx;
				text-align: center;
				transition: all 0.3s ease;
				border: 1rpx solid rgba($primary-color, 0.05);

				&:active {
					transform: scale(0.95);
					background: rgba($primary-color, 0.08);
					box-shadow: $shadow-light;
				}

				.module-icon {
					width: 80rpx;
					height: 80rpx;
					border-radius: 20rpx;
					display: flex;
					align-items: center;
					justify-content: center;
					margin: 0 auto 20rpx;
					font-size: 36rpx;
					color: #fff;
					box-shadow: $shadow-medium;

					&.user {
						background: linear-gradient(135deg, $success-color, #73d13d);
					}

					&.health {
						background: linear-gradient(135deg, $accent-color, #9254de);
					}

					&.nutrition {
						background: linear-gradient(135deg, #fa541c, #ff7a45);
					}

					&.prize {
						background: linear-gradient(135deg, #eb2f96, #f759ab);
					}
				}

				.module-name {
					font-size: 26rpx;
					color: $text-primary;
					font-weight: 500;
				}
			}
		}
	}

	/* 最近动态 */
	.recent-activities {
		padding: 30rpx;
		margin-bottom: 30rpx;

		.section-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 30rpx;

			.section-title {
				font-size: 32rpx;
				font-weight: bold;
				color: $text-primary;
			}

			.section-more {
				font-size: 26rpx;
				color: $primary-color;
				padding: 12rpx 24rpx;
				border-radius: 20rpx;
				transition: all 0.3s ease;

				&:active {
					background: rgba($primary-color, 0.1);
				}
			}
		}

		.activities-list {
			.activity-item {
				display: flex;
				align-items: center;
				padding: 24rpx 0;
				border-bottom: 1rpx solid rgba(0, 0, 0, 0.06);
				transition: all 0.3s ease;

				&:active {
					background: rgba(0, 0, 0, 0.03);
					border-radius: 16rpx;
					transform: scale(0.98);
				}

				&:last-child {
					border-bottom: none;
				}

				.activity-icon {
					width: 80rpx;
					height: 80rpx;
					border-radius: 50%;
					display: flex;
					align-items: center;
					justify-content: center;
					margin-right: 24rpx;
					font-size: 32rpx;
					color: #fff;
					box-shadow: $shadow-medium;

					&.blog {
						background: $info-color;
					}

					&.wealth {
						background: $warning-color;
					}

					&.health {
						background: $success-color;
					}
				}

				.activity-content {
					flex: 1;

					.activity-title {
						display: block;
						font-size: 28rpx;
						color: $text-primary;
						margin-bottom: 8rpx;
					}

					.activity-time {
						font-size: 24rpx;
						color: $text-light;
					}
				}

				.activity-arrow {
					.iconfont {
						font-size: 28rpx;
						color: $text-light;
					}
				}
			}
		}
	}

	/* 空状态 */
	.empty-state {
		padding: 80rpx 40rpx;
		text-align: center;
		margin-bottom: 30rpx;

		.empty-icon {
			width: 120rpx;
			height: 120rpx;
			border-radius: 50%;
			background: rgba($primary-color, 0.1);
			display: flex;
			align-items: center;
			justify-content: center;
			margin: 0 auto 30rpx;

			.iconfont {
				font-size: 50rpx;
				color: rgba($primary-color, 0.5);
			}
		}

		.empty-text {
			display: block;
			font-size: 32rpx;
			color: $text-secondary;
			margin-bottom: 16rpx;
		}

		.empty-desc {
			font-size: 26rpx;
			color: $text-light;
		}
	}

	/* 底部提示 */
	.footer-tips {
		text-align: center;
		padding: 10rpx 0;

		.tip-text {
			font-size: 24rpx;
			color: $text-light;
		}
	}

	/* 安全区域 */
	.safe-area-bottom {
		height: env(safe-area-inset-bottom);
		width: 100%;
	}

	/* 弹窗遮罩层 */
	.popup-mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: flex-end;
		z-index: 9999;
	}

	/* 所有模块弹窗 */
	.all-modules-popup {
		border-radius: 40rpx 40rpx 0 0;
		max-height: 80vh;
		width: 100%;

		.popup-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 40rpx 30rpx 20rpx;
			border-bottom: 1rpx solid rgba(0, 0, 0, 0.06);

			.popup-title {
				font-size: 32rpx;
				font-weight: bold;
				color: $text-primary;
			}

			.popup-close {
				width: 60rpx;
				height: 60rpx;
				border-radius: 50%;
				display: flex;
				align-items: center;
				justify-content: center;
				background: rgba(0, 0, 0, 0.05);
				transition: all 0.3s ease;

				&:active {
					background: rgba(0, 0, 0, 0.1);
				}

				.iconfont {
					font-size: 28rpx;
					color: $text-secondary;
				}
			}
		}

		.modules-list {
			max-height: 60vh;
			padding: 20rpx 0;

			.module-list-item {
				display: flex;
				align-items: center;
				padding: 30rpx;
				border-bottom: 1rpx solid rgba(0, 0, 0, 0.06);
				transition: all 0.3s ease;

				&:active {
					background: rgba(0, 0, 0, 0.03);
				}

				.module-list-icon {
					width: 80rpx;
					height: 80rpx;
					border-radius: 20rpx;
					display: flex;
					align-items: center;
					justify-content: center;
					margin-right: 24rpx;
					font-size: 32rpx;
					color: #fff;
					box-shadow: $shadow-medium;

					&.wealth {
						background: $warning-color;
					}

					&.blog {
						background: $info-color;
					}

					&.user {
						background: $success-color;
					}

					&.health {
						background: $accent-color;
					}
				}

				.module-list-content {
					flex: 1;

					.module-list-name {
						display: block;
						font-size: 30rpx;
						color: $text-primary;
						margin-bottom: 8rpx;
					}

					.module-list-desc {
						font-size: 24rpx;
						color: $text-light;
					}
				}

				.module-list-arrow {
					.iconfont {
						font-size: 28rpx;
						color: $text-light;
					}
				}
			}
		}
	}

	/* 快捷操作弹窗 */
	.quick-actions-popup {
		border-radius: 40rpx 40rpx 0 0;
		max-height: 50vh;
		width: 100%;

		.popup-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			padding: 40rpx 30rpx 20rpx;
			border-bottom: 1rpx solid rgba(0, 0, 0, 0.06);

			.popup-title {
				font-size: 32rpx;
				font-weight: bold;
				color: $text-primary;
			}

			.popup-close {
				width: 60rpx;
				height: 60rpx;
				border-radius: 50%;
				display: flex;
				align-items: center;
				justify-content: center;
				background: rgba(0, 0, 0, 0.05);
				transition: all 0.3s ease;

				&:active {
					background: rgba(0, 0, 0, 0.1);
				}

				.iconfont {
					font-size: 28rpx;
					color: $text-secondary;
				}
			}
		}

		.quick-actions-grid {
			display: grid;
			grid-template-columns: repeat(2, 1fr);
			gap: 30rpx;
			padding: 40rpx 30rpx;

			.quick-action-item {
				text-align: center;
				padding: 40rpx 20rpx;
				border-radius: 20rpx;
				background: rgba($primary-color, 0.03);
				transition: all 0.3s ease;

				&:active {
					transform: scale(0.95);
					background: rgba($primary-color, 0.08);
				}

				.quick-action-icon {
					width: 80rpx;
					height: 80rpx;
					border-radius: 20rpx;
					display: flex;
					align-items: center;
					justify-content: center;
					margin: 0 auto 20rpx;
					font-size: 36rpx;
					color: #fff;
					box-shadow: $shadow-medium;

					&.family {
						background: linear-gradient(135deg, #FF6B6B, #FF8E53);
					}

					&.class {
						background: linear-gradient(135deg, #4ECDC4, #44A08D);
					}

					&.food {
						background: linear-gradient(135deg, #FFD93D, #FF9C3D);
					}

					&.report {
						background: linear-gradient(135deg, #A166AB, #5073B8);
					}
				}

				.quick-action-label {
					font-size: 24rpx;
					color: $text-primary;
					font-weight: 500;
				}
			}
		}
	}

	// 动画定义
	@keyframes glow {
		0% {
			opacity: 0.6;
			transform: scale(1);
		}

		100% {
			opacity: 0.8;
			transform: scale(1.05);
		}
	}
</style>