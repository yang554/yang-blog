<template>
	<view class="account-detail-page" v-if="pageReady">
		<!-- 搜索条件区域 -->
		<view class="search-section">
			<view class="section-title">
				<view class="title-text">搜索条件</view>
				<view class="section-operations">
					<!-- 折叠/展开按钮 -->
					<view class="collapse-btn" @tap="toggleSearchCollapse">
						<uni-icons :type="searchCollapsed ? 'arrowdown' : 'arrowup'" size="16" color="#666"></uni-icons>
						<text class="collapse-text">{{ searchCollapsed ? '展开' : '折叠' }}</text>
					</view>
					<view class="reset-btn" @tap="resetSearch">重置</view>
					<view class="search-btn" @tap="searchData">搜索</view>
				</view>
			</view>

			<!-- 折叠内容 -->
			<view v-if="!searchCollapsed" class="search-content">
				<scroll-view class="search-conditions" scroll-x="true" scroll-y="false">
					<view class="conditions-row" v-if="isRootUser">
						<!-- 制单人选择 -->
						<view class="condition-item">
							<view class="condition-label">制单人</view>
							<picker mode="selector" :range="creatorList" :range-key="'username'"
								@change="onCreatorChange">
								<view class="condition-value">
									<view class="value-text">{{ form.creator ? form.creator.username : '请选择制单人' }}
									</view>
									<uni-icons type="arrowdown" size="16" color="#999"></uni-icons>
								</view>
							</picker>
						</view>
					</view>

					<view class="conditions-row">
						<!-- 收支情况 -->
						<view class="condition-item">
							<view class="condition-label">收支情况</view>
							<picker mode="selector" :range="incomeExpenseOptions" @change="onIncomeExpenseChange">
								<view class="condition-value">
									<view class="value-text">{{ form.incomeExpense || '全部' }}</view>
									<uni-icons type="arrowdown" size="16" color="#999"></uni-icons>
								</view>
							</picker>
						</view>
						<!-- 流水类型 -->
						<view class="condition-item">
							<view class="condition-label">流水类型</view>
							<picker mode="selector" :range="flowTypeList" :range-key="'name'"
								@change="onFlowTypeChange">
								<view class="condition-value">
									<view class="value-text">{{ form.flowType ? form.flowType.name : '请选择流水类型' }}</view>
									<uni-icons type="arrowdown" size="16" color="#999"></uni-icons>
								</view>
							</picker>
						</view>
					</view>

					<view class="conditions-row">
						<!-- 账单日期范围 -->
						<view class="condition-item">
							<view class="condition-label">账单日期</view>
							<view class="date-range">
								<picker mode="date" :value="form.startDate" @change="onStartDateChange"
									class="date-picker">
									<view class="date-text">{{ form.startDate || '开始日期' }}</view>
								</picker>
								<text class="date-separator">至</text>
								<picker mode="date" :value="form.endDate" @change="onEndDateChange" class="date-picker">
									<view class="date-text">{{ form.endDate || '结束日期' }}</view>
								</picker>
							</view>
						</view>
					</view>

					<!-- 金额范围 -->
					<view class="conditions-row">
						<view class="condition-item full-width">
							<view class="condition-label">金额范围</view>
							<view class="amount-range">
								<view class="amount-input-group">
									<text class="currency-symbol">￥</text>
									<input class="amount-input" placeholder="最小值" v-model="queryCost.minCost"
										type="digit" @input="handleInput('min', $event)" />
								</view>
								<text class="range-separator">至</text>
								<view class="amount-input-group">
									<text class="currency-symbol">￥</text>
									<input class="amount-input" placeholder="最大值" v-model="queryCost.maxCost"
										type="digit" @input="handleInput('max', $event)" />
								</view>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
		</view>

		<!-- 明细列表区域 -->
		<view class="detail-section" v-if="pageReady">
			<view class="section-title">
				<view class="title-text">明细列表</view>
				<view class="total-count">共 {{ pagination.total }} 条记录</view>

				<!-- 视图切换按钮 -->
				<view class="view-toggle">
					<view class="toggle-btn" :class="{ active: viewMode === 'card' }" @tap="switchViewMode('card')">
						<uni-icons type="grid" size="20"></uni-icons>
					</view>
					<view class="toggle-btn" :class="{ active: viewMode === 'table' }" @tap="switchViewMode('table')">
						<uni-icons type="list" size="20"></uni-icons>
					</view>
				</view>
			</view>

			<!-- 卡片视图 -->
			<scroll-view class="card-content" scroll-y="true" :style="{ height: scrollViewHeight + 'px' }"
				v-if="viewMode === 'card' && pageReady">

				<!-- 加载状态 -->
				<view class="loading-container" v-if="pagination.loading">
					<text class="loading-text">加载中...</text>
				</view>

				<!-- 空状态 -->
				<view class="empty-container" v-else-if="pagination.currentTableData.length === 0">
					<uni-icons type="list" size="60" color="#ddd"></uni-icons>
					<text class="empty-text">暂无数据</text>
					<view class="empty-hint">尝试修改搜索条件或刷新页面</view>
				</view>

				<!-- 数据列表 -->
				<view class="list-content" v-else>
					<view class="detail-card" v-for="(item, index) in pagination.currentTableData" :key="index"
						:class="item.type === '收入' ? 'income-card' : 'expense-card'">
						<view class="card-header">
							<view class="card-title">
								<view class="expense-name">{{ item.purpose | titleText }}</view>
								<view class="flow-type">{{ item.typeTagID }}</view>
							</view>
							<view class="amount" :class="item.type === '收入' ? 'income-amount' : 'expense-amount'">
								{{ item.type === '收入' ? '+' : '-' }}{{ formatCurrency(item.cost) }}
							</view>
						</view>

						<view class="card-body">
							<view class="info-row">
								<view class="info-item combined">
									<uni-icons type="person" size="18" color="#999"></uni-icons>
									<view class="info-content">
										<view class="info-label">制单人：</view>
										<view class="info-value">{{ item.uName }}</view>
									</view>
									<view style="margin: 0 20rpx;">|</view>
									<uni-icons type="calendar" size="18" color="#999"></uni-icons>
									<view class="info-content">
										<view class="info-label">日期：</view>
										<view class="info-value">{{ item.note | dateFormat }}</view>
									</view>
								</view>
							</view>

							<view class="card-footer">
								<view class="type-badge" :class="item.type === '收入' ? 'income-badge' : 'expense-badge'">
									{{ item.type }}
								</view>
								<view class="card-action" @tap="viewDetail(item)">查看详情</view>
							</view>
						</view>
					</view>
				</view>
			</scroll-view>

			<!-- 表格视图 -->
			<view class="table-view" v-if="viewMode === 'table' && pageReady">
				<!-- 表格头部 - 横向滚动 -->
				<scroll-view class="table-header-wrapper" scroll-x="true" scroll-with-animation :show-scrollbar="true">
					<view class="table-header">
						<view class="table-cell" style="min-width: 140rpx;">账单日期</view>
						<view class="table-cell" style="min-width: 180rpx;">消费名目</view>
						<view class="table-cell" style="min-width: 120rpx;">收支类型</view>
						<view class="table-cell" style="min-width: 140rpx;">流水类型</view>
						<view class="table-cell" style="min-width: 140rpx;">金额</view>
						<view class="table-cell" style="min-width: 120rpx;">制单人</view>
						<view class="table-cell" style="min-width: 120rpx;">操作</view>
					</view>
				</scroll-view>

				<!-- 表格内容 - 横向滚动 -->
				<scroll-view class="table-content-wrapper" scroll-x="true" scroll-y="true" :scroll-top="scrollTop"
					:show-scrollbar="true" :style="{ height: scrollViewHeight + 'px' }">

					<!-- 加载状态 -->
					<view class="loading-container" v-if="pagination.loading">
						<text class="loading-text">加载中...</text>
					</view>

					<!-- 空状态 -->
					<view class="empty-container" v-else-if="pagination.currentTableData.length === 0">
						<uni-icons type="list" size="60" color="#ddd"></uni-icons>
						<text class="empty-text">暂无数据</text>
						<view class="empty-hint">尝试修改搜索条件或刷新页面</view>
					</view>

					<!-- 数据列表 -->
					<view class="table-content" v-else>
						<view class="table-row" v-for="(item, index) in pagination.currentTableData" :key="index"
							:class="{ 'even-row': index % 2 === 0 }">
							<view class="table-cell" style="min-width: 140rpx;">
								<view class="bill-date">{{ item.note | dateFormat }}</view>
							</view>
							<view class="table-cell" style="min-width: 180rpx;">
								<view class="expense-name">{{ item.purpose | titleText }}</view>
							</view>
							<view class="table-cell" style="min-width: 120rpx;">
								<view class="type-tag" :class="item.type === '收入' ? 'income-tag' : 'expense-tag'">
									{{ item.type }}
								</view>
							</view>
							<view class="table-cell" style="min-width: 140rpx;">
								<view class="flow-type">{{ item.typeTagID }}</view>
							</view>
							<view class="table-cell" style="min-width: 140rpx;">
								<view :class="item.type === '收入' ? 'income-amount' : 'expense-amount'">
									{{ formatCurrency(item.cost) }}
								</view>
							</view>
							<view class="table-cell" style="min-width: 120rpx;">
								<view class="creator">{{ item.uName }}</view>
							</view>
							<view class="table-cell" style="min-width: 120rpx;">
								<view class="view-detail" @tap="viewDetail(item)">详情</view>
							</view>
						</view>
					</view>
				</scroll-view>

				<!-- 滚动提示 -->
				<view class="scroll-hint" v-if="showScrollHint && viewMode === 'table' && pageReady">
					<uni-icons type="forward" size="20" color="#999"></uni-icons>
					<view class="hint-text">左右滑动查看完整信息</view>
				</view>
			</view>
		</view>

		<!-- 分页  -->
		<view class="pagination-container" v-if="pagination.total > pagination.pageSize">
			<view class="pagination-content">
				<button class="page-btn" :disabled="pagination.currentPage === 1" @tap="handlePrevPage">
					上一页
				</button>

				<view class="page-info">
					<text class="page-text">{{ pagination.currentPage }} / {{ pagination.totalPage }}</text>
				</view>

				<button class="page-btn" :disabled="pagination.currentPage === pagination.totalPage"
					@tap="handleNextPage">
					下一页
				</button>
			</view>
		</view>

		<!-- 详情弹窗 -->
		<uni-popup ref="detailPopup" type="center" background-color="#fff" :mask-click="true" v-if="pageReady">
			<view class="detail-popup" v-if="currentDetail">
				<view class="popup-header">
					<view class="popup-title">账单详情</view>
					<uni-icons type="close" size="24" color="#999" @tap="closeDetailPopup"></uni-icons>
				</view>

				<view class="popup-content">
					<view class="detail-row">
						<view class="detail-label">消费名目：</view>
						<view class="detail-value">{{ currentDetail.purpose }}</view>
					</view>
					<view class="detail-row">
						<view class="detail-label">收支类型：</view>
						<view class="detail-value"
							:class="currentDetail.type === '收入' ? 'income-text' : 'expense-text'">
							{{ currentDetail.type }}
						</view>
					</view>
					<view class="detail-row">
						<view class="detail-label">流水类型：</view>
						<view class="detail-value">{{ currentDetail.typeTagID }}</view>
					</view>
					<view class="detail-row">
						<view class="detail-label">金额：</view>
						<view class="detail-value"
							:class="currentDetail.type === '收入' ? 'income-text' : 'expense-text'">
							{{ currentDetail.type === '收入' ? '+' : '-' }}{{ formatCurrency(currentDetail.cost) }}
						</view>
					</view>
					<view class="detail-row">
						<view class="detail-label">账单日期：</view>
						<view class="detail-value">{{ currentDetail.note | dateFormat }}</view>
					</view>
					<view class="detail-row">
						<view class="detail-label">制单人：</view>
						<view class="detail-value">{{ currentDetail.uName }}</view>
					</view>
					<view class="detail-row" v-if="currentDetail.remark">
						<view class="detail-label">备注：</view>
						<view class="detail-value">{{ currentDetail.remark }}</view>
					</view>
				</view>

				<view class="popup-footer">
					<button class="popup-btn" @tap="closeDetailPopup">关闭</button>
				</view>
			</view>
		</uni-popup>

		<!-- 加载中状态 -->
		<view class="loading-overlay" v-if="!pageReady">
			<view class="loading-content">
				<uni-icons type="spinner-cycle" size="40" color="#2979ff" class="loading-icon"></uni-icons>
				<view class="loading-text">页面加载中...</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		lifeApi,
		userApi
	} from "@/api/index.js";
	import {
		parseDate
	} from "@/common/formatDate.js";
	export default {
		name: "DailyDetailView",
		data() {
			return {
				//是否是root用户
				isRootUser: false,
				// 页面就绪状态
				pageReady: false,

				// 搜索条件折叠状态
				searchCollapsed: true,

				// 搜索表单
				form: {
					creator: null,
					incomeExpense: '',
					flowType: null,
					startDate: '',
					endDate: ''
				},
				queryCost: {
					minCost: null,
					maxCost: null
				},
				//当前登录用户
				currentUserInfo: uni.getStorageSync(this.$constants.USER_INFO),
				// 制单人列表
				creatorList: [],

				// 收支选项
				incomeExpenseOptions: ['全部', '收入', '支出'],

				// 流水类型列表
				flowTypeList: [],

				// 视图模式：card 或 table
				viewMode: 'card',

				// 当前查看的详情
				currentDetail: null,

				// 滚动区域高度
				scrollViewHeight: 500,
				scrollTop: 0,

				// 滚动提示
				showScrollHint: true,

				// 分页相关
				pagination: {
					listData: [],
					currentPage: 1,
					pageSize: 20,
					totalPage: 1,
					currentTableData: [],
					total: 0,
					loading: true,
				},

				// 定时器
				timer: null
			}
		},

		watch: {
			'queryCost.minCost'(newVal) {
				this.validateQueryCost();
			},
			'queryCost.maxCost'(newVal) {
				this.validateQueryCost();
			},
		},
		filters: {
			titleText(value) {
				if (!value) return "";
				if (value.length > 8) {
					return value.slice(0, 8) + "...";
				}
				return value;
			},
			dateFormat(val) {
				if (!val) return "";
				else return parseDate(val);
			},
		},
		onLoad() {

		},

		onShow() {

		},

		onReady() {
			// 确保页面完全准备就绪后再执行
			setTimeout(() => {
				this.pageReady = true;
				this.calculateScrollViewHeight();
				this.loadType();
				this.loadUser();
				this.init();

				// 3秒后隐藏滚动提示
				this.timer = setTimeout(() => {
					this.showScrollHint = false;
				}, 3000);
			}, 300);
		},

		onHide() {
			// 清理定时器
			if (this.timer) {
				clearTimeout(this.timer);
				this.timer = null;
			}
		},

		onUnload() {
			// 清理定时器
			if (this.timer) {
				clearTimeout(this.timer);
				this.timer = null;
			}
		},

		onResize() {
			if (this.pageReady) {
				this.calculateScrollViewHeight();
			}
		},

		onPullDownRefresh() {
			if (!this.pageReady) {
				uni.stopPullDownRefresh();
				return;
			}

			this.init();
			setTimeout(() => {
				uni.stopPullDownRefresh();
			}, 1000);
		},

		methods: {
			// 切换搜索条件折叠状态
			toggleSearchCollapse() {
				this.searchCollapsed = !this.searchCollapsed;
				setTimeout(() => {
					this.calculateScrollViewHeight();
				}, 100);
			},

			// 计算滚动区域高度
			calculateScrollViewHeight() {
				if (!this.pageReady) return;

				const systemInfo = uni.getSystemInfoSync();
				const windowHeight = systemInfo.windowHeight;

				// 创建选择器查询搜索区域高度
				const query = uni.createSelectorQuery().in(this);
				query.select('.search-section').boundingClientRect(data => {
					const searchHeight = data ? data.height : (this.searchCollapsed ? 100 : 300);
					this.scrollViewHeight = windowHeight - searchHeight - 50;

					// 如果有分页，减去分页高度
					if (this.pagination.total > this.pagination.pageSize) {
						this.scrollViewHeight -= 100; // 分页区域大约高度
					}
				}).exec();
			},

			// 金额输入处理
			handleInput(type, e) {
				let value = e.detail ? e.detail.value : e;
				value = value.replace(/(^0+(?=\d))|[^\d.]|(?<=\..{2}).|^\.|\.+/g, (match) => {
						if (match.startsWith("0") && match !== "0") return "";
						if (match === ".") return ".";
						return "";
					}).replace(/(\d*\.?\d{0,2}).*/, "$1")
					.replace(/^\./, "0.");

				if (type === 'min') {
					this.queryCost.minCost = value;
				} else {
					this.queryCost.maxCost = value;
				}
			},

			// 验证金额范围
			validateQueryCost() {
				if (this.queryCost.minCost !== null && this.queryCost.maxCost !== null &&
					parseFloat(this.queryCost.minCost) > parseFloat(this.queryCost.maxCost)) {
					uni.showToast({
						title: '金额范围最大值不能小于最小值',
						icon: 'none',
						duration: 2000
					});
				}
			},

			async loadType() {
				// 获取流水类型
				const resType = await lifeApi._getConsumptionTypeAll();
				if (resType.data) {
					this.flowTypeList = resType.data;
				}
			},

			async loadUser() {
				const res = await userApi.getAllUsers();
				if (res) {
					this.creatorList = res;
				}
			},
			// 初始化
			async init() {
				this.pagination.loading = true;
				this.pagination.currentPage = 1;

				if (this.currentUserInfo.roles[0].name === 'root') {
					this.isRootUser = true;
					this.form.creator = '';
				} else {
					this.isRootUser = false;
					this.form.creator = this.currentUserInfo.id;
				}

				try {

					// 构建参数对象
					const params = {
						searchDate1: this.form.startDate || '',
						searchDate2: this.form.endDate || '',
						searchType: this.form.incomeExpense || '',
						searchTypeTag: this.form.flowType ? this.form.flowType.id || '' : '',
						id: this.form.creator.id ? this.form.creator.id : this.form.creator,
						queryCost: typeof this.queryCost === 'object' ?
							JSON.stringify(this.queryCost) : this.queryCost || ''
					};
					// 对参数值进行URL编码
					const encodedParams = Object.keys(params)
						.map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
						.join('&');

					// 获取账簿明细
					const resList = await lifeApi._findByFactorInfo(encodedParams);

					if (resList) {
						this.pagination.listData = resList;
						this.pagination.total = resList.length;

						this.pagination.totalPage = Math.ceil(
							this.pagination.listData.length / this.pagination.pageSize
						);
						this.pagination.totalPage =
							this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

						this.getCurrentPageData(
							this.pagination.listData,
							this.pagination.currentPage,
							this.pagination.pageSize
						);
					} else {
						this.pagination.listData = [];
						this.pagination.total = 0;
						this.pagination.currentTableData = [];
					}

				} catch (error) {
					console.error('初始化失败:', error);
				} finally {
					this.pagination.loading = false;
					this.calculateScrollViewHeight();
				}
			},

			// 获取当前页数据
			getCurrentPageData(listData, currentPage, pageSize) {
				let begin = (currentPage - 1) * pageSize;
				let end = currentPage * pageSize;
				this.pagination.currentTableData = listData.slice(begin, end);
			},

			// 日期变化处理
			onStartDateChange(e) {
				this.form.startDate = e.detail.value;
			},

			onEndDateChange(e) {
				this.form.endDate = e.detail.value;
			},

			// 切换视图模式
			switchViewMode(mode) {
				if (!this.pageReady) return;

				this.viewMode = mode;
				setTimeout(() => {
					this.calculateScrollViewHeight();
				}, 100);
			},

			// 搜索数据
			async searchData() {
				if (!this.pageReady) return;

				if (this.queryCost.minCost && this.queryCost.maxCost &&
					parseFloat(this.queryCost.minCost) > parseFloat(this.queryCost.maxCost)) {
					uni.showToast({
						title: '最小金额不能大于最大金额',
						icon: 'none',
						duration: 2000
					});
					return;
				}

				this.pagination.loading = true;
				this.pagination.currentPage = 1;

				try {
					// 构建参数对象
					const params = {
						searchDate1: this.form.startDate || '',
						searchDate2: this.form.endDate || '',
						searchType: this.form.incomeExpense || '',
						searchTypeTag: this.form.flowType ? this.form.flowType.name || '' : '',
						id: this.form.creator.id ? this.form.creator.id : this.form.creator,
						queryCost: typeof this.queryCost === 'object' ?
							JSON.stringify(this.queryCost) : this.queryCost || ''
					};
					// 对参数值进行URL编码
					const encodedParams = Object.keys(params)
						.map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
						.join('&');

					// 获取账簿明细
					const resList = await lifeApi._findByFactorInfo(encodedParams);

					if (resList) {
						this.pagination.listData = resList;
						this.pagination.total = resList.length;

						this.pagination.totalPage = Math.ceil(
							this.pagination.listData.length / this.pagination.pageSize
						);
						this.pagination.totalPage =
							this.pagination.totalPage == 0 ? 1 : this.pagination.totalPage;

						this.getCurrentPageData(
							this.pagination.listData,
							this.pagination.currentPage,
							this.pagination.pageSize
						);
					} else {
						this.pagination.listData = [];
						this.pagination.total = 0;
						this.pagination.currentTableData = [];
					}

				} catch (error) {
					console.error('搜索失败:', error);
					uni.showToast({
						title: '搜索失败',
						icon: 'none',
						duration: 2000
					});
				} finally {
					this.pagination.loading = false;
					this.calculateScrollViewHeight();
				}
			},

			// 重置搜索条件
			resetSearch() {
				if (!this.pageReady) return;

				this.form = {
					creator: null,
					incomeExpense: '',
					flowType: null,
					startDate: '',
					endDate: ''
				};
				this.queryCost = {
					minCost: null,
					maxCost: null
				};
				this.init();
			},

			// 刷新数据
			refreshData() {
				if (!this.pageReady) return;

				this.init();
			},

			// 导出数据
			exportData() {
				if (!this.pageReady) return;

				uni.showLoading({
					title: '准备导出...'
				});

				setTimeout(() => {
					uni.hideLoading();
					uni.showModal({
						title: '导出成功',
						content: `已成功导出 ${this.pagination.total} 条记录到Excel文件`,
						showCancel: false,
						confirmText: '确定'
					});
				}, 1500);
			},

			// 查看详情
			viewDetail(item) {
				if (!this.pageReady) return;

				try {
					this.currentDetail = {
						...item,
						remark: '==========='
					};

					if (this.$refs && this.$refs.detailPopup) {
						this.$refs.detailPopup.open();
					} else {
						console.warn('弹窗组件未找到，使用备用方案');
						this.showDetailModal(item);
					}
				} catch (error) {
					console.error('打开详情弹窗失败:', error);
					this.showDetailModal(item);
				}
			},

			// 备用详情显示方案
			showDetailModal(item) {
				const detailContent = `
					消费名目：${item.purpose}
					收支类型：${item.type}
					流水类型：${item.typeTagID}
					金额：${item.type === '收入' ? '+' : '-'}${this.formatCurrency(item.cost)}
					账单日期：${item.note}
					制单人：${item.uName}
					备注：
				`;

				uni.showModal({
					title: '账单详情',
					content: detailContent,
					showCancel: false,
					confirmText: '关闭'
				});
			},

			// 关闭详情弹窗
			closeDetailPopup() {
				if (this.$refs && this.$refs.detailPopup) {
					this.$refs.detailPopup.close();
				}
				this.currentDetail = null;
			},

			// 格式化货币
			formatCurrency(value) {
				if (!value) return '¥0.00';

				const num = parseFloat(value);
				if (isNaN(num)) return '¥0.00';

				return `¥${num.toLocaleString('zh-CN', {
					minimumFractionDigits: 2,
					maximumFractionDigits: 2
				})}`;
			},

			// 表单变化处理
			onCreatorChange(e) {
				if (!this.pageReady) return;

				const index = e.detail.value;
				this.form.creator = index >= 0 ? this.creatorList[index] : null;
			},

			onIncomeExpenseChange(e) {
				if (!this.pageReady) return;

				const index = e.detail.value;
				this.form.incomeExpense = index > 0 ? this.incomeExpenseOptions[index] : '';
			},

			onFlowTypeChange(e) {
				if (!this.pageReady) return;
				const index = e.detail.value;
				this.form.flowType = index >= 0 ? this.flowTypeList[index] : null;
			},

			// 上一页
			handlePrevPage() {
				if (this.pagination.currentPage > 1) {
					this.pagination.currentPage--;
					this.getCurrentPageData(
						this.pagination.listData,
						this.pagination.currentPage,
						this.pagination.pageSize
					);
				}
			},

			// 下一页
			handleNextPage() {
				if (this.pagination.currentPage < this.pagination.totalPage) {
					this.pagination.currentPage++;
					this.getCurrentPageData(
						this.pagination.listData,
						this.pagination.currentPage,
						this.pagination.pageSize
					);
				}
			}
		}
	}
</script>

<style scoped>
	/* 页面基础样式 */
	.account-detail-page {
		background-color: #f5f7fa;
		min-height: 100vh;
		padding-bottom: 20rpx;
	}

	/* 加载中状态 */
	.loading-overlay {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-color: rgba(255, 255, 255, 0.9);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 9999;
	}

	.loading-content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
	}

	.loading-icon {
		animation: rotate 1s linear infinite;
	}

	@keyframes rotate {
		0% {
			transform: rotate(0deg);
		}

		100% {
			transform: rotate(360deg);
		}
	}

	.loading-text {
		margin-top: 20rpx;
		font-size: 28rpx;
		color: #666;
	}

	/* 搜索区域样式 */
	.search-section {
		background-color: #fff;
		margin: 20rpx;
		border-radius: 16rpx;
		padding: 24rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
	}

	.section-title {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-bottom: 24rpx;
		border-bottom: 1rpx solid #f0f0f0;
		margin-bottom: 0;
	}

	.title-text {
		font-size: 32rpx;
		font-weight: 600;
		color: #333;
	}

	.section-operations {
		display: flex;
		gap: 20rpx;
		align-items: center;
	}

	/* 折叠按钮样式 */
	.collapse-btn {
		display: flex;
		align-items: center;
		gap: 8rpx;
		font-size: 26rpx;
		color: #666;
		padding: 8rpx 16rpx;
		border-radius: 8rpx;
		background-color: #f5f5f5;
	}

	.collapse-text {
		font-size: 26rpx;
	}

	.reset-btn,
	.search-btn {
		font-size: 28rpx;
		padding: 8rpx 24rpx;
		border-radius: 24rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		height: 56rpx;
		line-height: 56rpx;
	}

	.reset-btn {
		color: #666;
		background-color: #f5f5f5;
	}

	.search-btn {
		color: #fff;
		background-color: #2979ff;
	}

	/* 搜索内容区域 */
	.search-content {
		margin-top: 24rpx;
	}

	.search-conditions {
		width: 100%;
		white-space: nowrap;
		-webkit-overflow-scrolling: touch;
	}

	.conditions-row {
		display: flex;
		margin-bottom: 28rpx;
		gap: 20rpx;
	}

	.condition-item {
		flex: 1;
		min-width: 300rpx;
	}

	.condition-item.full-width {
		flex: 1 0 100%;
	}

	.condition-label {
		display: block;
		font-size: 26rpx;
		color: #666;
		margin-bottom: 12rpx;
		font-weight: 500;
	}

	.condition-value {
		height: 72rpx;
		line-height: 72rpx;
		padding: 0 24rpx;
		background-color: #f8f9fa;
		border-radius: 12rpx;
		border: 1rpx solid #e4e7ed;
		font-size: 28rpx;
		color: #333;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.value-text {
		flex: 1;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	/* 日期范围样式 */
	.date-range {
		display: flex;
		align-items: center;
		flex: 1;
	}

	.date-picker {
		flex: 1;
	}

	.date-text {
		height: 72rpx;
		border: 1rpx solid #e4e7ed;
		border-radius: 12rpx;
		padding: 0 20rpx;
		line-height: 72rpx;
		font-size: 28rpx;
		color: #333;
		text-align: center;
		background-color: #f8f9fa;
	}

	.date-separator {
		margin: 0 20rpx;
		color: #666;
		font-size: 28rpx;
	}

	/* 金额范围样式 */
	.amount-range {
		display: flex;
		align-items: center;
		flex: 1;
	}

	.amount-input-group {
		display: flex;
		align-items: center;
		flex: 1;
		height: 72rpx;
		border: 1rpx solid #e4e7ed;
		border-radius: 12rpx;
		overflow: hidden;
		background-color: #f8f9fa;
	}

	.currency-symbol {
		padding: 0 20rpx;
		background: #f0f0f0;
		height: 100%;
		line-height: 72rpx;
		color: #666;
		font-size: 28rpx;
	}

	.amount-input {
		flex: 1;
		height: 100%;
		padding: 0 20rpx;
		font-size: 28rpx;
		color: #333;
		background-color: transparent;
	}

	.range-separator {
		margin: 0 20rpx;
		color: #666;
		font-size: 28rpx;
	}

	/* 明细区域样式 */
	.detail-section {
		background-color: #fff;
		margin: 20rpx;
		border-radius: 16rpx;
		padding: 24rpx;
		box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
	}

	.total-count {
		font-size: 26rpx;
		color: #999;
		margin-left: 20rpx;
	}

	/* 视图切换 */
	.view-toggle {
		display: flex;
		align-items: center;
		gap: 12rpx;
		background-color: #f5f7fa;
		border-radius: 24rpx;
		padding: 4rpx;
		margin-left: auto;
	}

	.toggle-btn {
		width: 60rpx;
		height: 60rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		border-radius: 16rpx;
		color: #999;
		transition: all 0.3s ease;
	}

	.toggle-btn.active {
		background-color: #fff;
		color: #2979ff;
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
	}

	/* 加载状态样式 */
	.loading-container,
	.empty-container {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		height: 200rpx;
	}

	.loading-text,
	.empty-text {
		font-size: 28rpx;
		color: #666;
		margin-top: 20rpx;
	}

	.empty-hint {
		font-size: 24rpx;
		color: #ccc;
		margin-top: 12rpx;
	}

	/* 卡片视图样式 */
	.card-content {
		width: 100%;
	}

	.list-content {
		padding: 10rpx 0;
	}

	.detail-card {
		background-color: #fff;
		border-radius: 20rpx;
		padding: 32rpx;
		margin-bottom: 24rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
		border-left: 8rpx solid #2979ff;
		transition: all 0.3s ease;
	}

	.detail-card:active {
		transform: scale(0.98);
		opacity: 0.9;
	}

	.income-card {
		border-left-color: #52c41a;
	}

	.expense-card {
		border-left-color: #f5222d;
	}

	.card-header {
		display: flex;
		justify-content: space-between;
		align-items: flex-start;
		margin-bottom: 24rpx;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.card-title {
		flex: 1;
		margin-right: 20rpx;
	}

	.expense-name {
		display: block;
		font-size: 30rpx;
		font-weight: 600;
		color: #333;
		margin-bottom: 8rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.flow-type {
		font-size: 24rpx;
		color: #666;
		background-color: #f5f7fa;
		padding: 4rpx 12rpx;
		border-radius: 12rpx;
		display: inline-block;
	}

	.amount {
		font-size: 32rpx;
		font-weight: 700;
	}

	.income-amount {
		color: #52c41a;
	}

	.expense-amount {
		color: #f5222d;
	}

	.card-body {
		font-size: 26rpx;
	}

	.info-row {
		margin-bottom: 20rpx;
	}

	.info-item.combined {
		display: flex;
		align-items: center;
		justify-content: flex-start;
		color: #666;
		flex-wrap: nowrap;
		gap: 8rpx;
	}

	.info-content {
		display: flex;
		align-items: center;
		gap: 14rpx;
	}

	.info-label {
		color: #999;
		white-space: nowrap;
		font-size: 24rpx;
	}

	.info-value {
		color: #333;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.card-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-top: 20rpx;
		border-top: 1rpx solid #f0f0f0;
	}

	.type-badge {
		padding: 8rpx 24rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
		font-weight: 500;
		min-width: 80rpx;
		text-align: center;
	}

	.income-badge {
		background-color: rgba(82, 196, 26, 0.1);
		color: #52c41a;
	}

	.expense-badge {
		background-color: rgba(245, 34, 45, 0.1);
		color: #f5222d;
	}

	.card-action {
		color: #2979ff;
		font-size: 26rpx;
		padding: 8rpx 24rpx;
		border-radius: 16rpx;
		background-color: rgba(41, 121, 255, 0.1);
	}

	.card-action:active {
		background-color: rgba(41, 121, 255, 0.2);
	}

	/* 表格视图样式 */
	.table-view {
		width: 100%;
	}

	.table-header-wrapper {
		width: 100%;
		overflow-x: auto;
		background-color: #f8f9fa;
		border-radius: 12rpx 12rpx 0 0;
		-webkit-overflow-scrolling: touch;
	}

	.table-header {
		display: flex;
		min-width: 1100rpx;
		padding: 24rpx 0;
	}

	.table-header .table-cell {
		font-weight: 600;
		color: #666;
		white-space: nowrap;
		font-size: 28rpx;
		border-right: 1rpx solid #e8e8e8;
	}

	.table-header .table-cell:last-child {
		border-right: none;
	}

	.table-content-wrapper {
		width: 100%;
		overflow: auto;
		border-radius: 0 0 12rpx 12rpx;
		-webkit-overflow-scrolling: touch;
	}

	.table-content {
		min-width: 1100rpx;
	}

	.table-row {
		display: flex;
		padding: 28rpx 0;
		border-bottom: 1rpx solid #f0f0f0;
		min-width: 1100rpx;
		transition: all 0.3s ease;
	}

	.table-row:active {
		background-color: #fafbfc;
	}

	.even-row {
		background-color: #fafbfc;
	}

	.table-cell {
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 20rpx;
		font-size: 28rpx;
		color: #333;
		box-sizing: border-box;
		border-right: 1rpx solid #f0f0f0;
		overflow: hidden;
	}

	.table-cell:last-child {
		border-right: none;
	}

	.expense-name {
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		max-width: 100%;
		text-align: center;
	}

	.type-tag {
		display: inline-block;
		padding: 8rpx 20rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
		font-weight: 500;
		min-width: 80rpx;
		text-align: center;
	}

	.income-tag {
		background-color: rgba(82, 196, 26, 0.1);
		color: #52c41a;
	}

	.expense-tag {
		background-color: rgba(245, 34, 45, 0.1);
		color: #f5222d;
	}

	.flow-type,
	.bill-date,
	.creator {
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		max-width: 100%;
		text-align: center;
	}

	.income-amount {
		color: #52c41a;
		font-weight: 600;
		font-size: 28rpx;
	}

	.expense-amount {
		color: #f5222d;
		font-weight: 600;
		font-size: 28rpx;
	}

	.view-detail {
		color: #2979ff;
		font-size: 26rpx;
		padding: 8rpx 20rpx;
		background-color: rgba(41, 121, 255, 0.1);
		border-radius: 16rpx;
	}

	.view-detail:active {
		background-color: rgba(41, 121, 255, 0.2);
	}

	/* 滚动提示 */
	.scroll-hint {
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 16rpx;
		background-color: #f8f9fa;
		border-radius: 0 0 12rpx 12rpx;
		margin-top: -1rpx;
		border-top: 1rpx solid #f0f0f0;
	}

	.hint-text {
		font-size: 24rpx;
		color: #999;
		margin-left: 8rpx;
	}

	/* 分页样式 */
	.pagination-container {
		margin: 20rpx;
		padding: 20rpx;
		background: #fff;
		border-radius: 16rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
	}

	.pagination-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.page-btn {
		padding: 2rpx 40rpx;
		border-radius: 8rpx;
		background: #007AFF;
		color: #fff;
		font-size: 28rpx;
	}

	.page-btn:disabled {
		background: #ccc;
		color: #999;
	}

	.page-btn:active {
		transform: scale(0.95);
	}

	.page-info {
		.page-text {
			font-size: 28rpx;
			color: #333;
		}
	}

	/* 详情弹窗样式 */
	.detail-popup {
		width: 600rpx;
		background-color: #fff;
		border-radius: 24rpx;
		padding: 32rpx;
		box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.1);
	}

	.popup-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding-bottom: 24rpx;
		border-bottom: 1rpx solid #f0f0f0;
		margin-bottom: 32rpx;
	}

	.popup-title {
		font-size: 34rpx;
		font-weight: 600;
		color: #333;
	}

	.popup-content {
		margin-bottom: 32rpx;
	}

	.detail-row {
		display: flex;
		margin-bottom: 24rpx;
		font-size: 28rpx;
	}

	.detail-label {
		color: #666;
		min-width: 160rpx;
		font-weight: 500;
	}

	.detail-value {
		color: #333;
		flex: 1;
		word-break: break-all;
	}

	.income-text {
		color: #52c41a;
		font-weight: 600;
	}

	.expense-text {
		color: #f5222d;
		font-weight: 600;
	}

	.popup-footer {
		display: flex;
		justify-content: center;
	}

	.popup-btn {
		width: 200rpx;
		height: 70rpx;
		line-height: 70rpx;
		background-color: #2979ff;
		color: #fff;
		border-radius: 35rpx;
		font-size: 28rpx;
		border: none;
	}

	.popup-btn:active {
		background-color: #1a6aff;
	}

	/* 响应式调整 */
	@media (max-width: 750px) {
		.condition-item {
			min-width: 280rpx;
		}

		.info-row {
			flex-direction: column;
			gap: 16rpx;
		}

		.info-item.combined {
			flex-wrap: wrap;
			gap: 12rpx;
		}

		.info-value {
			max-width: 220rpx;
		}

		.detail-card {
			padding: 24rpx;
		}

		.expense-name {
			font-size: 28rpx;
		}

		.amount {
			font-size: 30rpx;
		}

		.detail-popup {
			width: 500rpx;
		}
	}

	@media (min-width: 751px) {
		.search-conditions {
			white-space: normal;
		}

		.conditions-row {
			flex-wrap: wrap;
		}

		.condition-item {
			min-width: 320rpx;
		}
	}
</style>