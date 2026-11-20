<template>
	<view class="app-container">
		<!-- ===== 顶部导航 ===== -->
		<view class="page-header">
			<view class="header-left">
				<text class="back-btn" @click="goBack"></text>
				<text class="app-title">{{ product ? product.name : '价格详情' }}</text>
			</view>
			<view class="header-right">
				<button class="btn-add" @click="openAddPriceModal">＋</button>
			</view>
		</view>

		<!-- ===== 🎯 最划算推荐卡片 ===== -->
		<view class="best-recommendation" v-if="bestSpec">
			<view class="recommendation-badge">🏆 最划算推荐</view>
			<view class="recommendation-content">
				<text class="recommendation-name"  v-if="bestSpec.brand !== null">品牌：{{ bestSpec.brand }}</text>
				<text class="recommendation-name"  v-if="bestSpec.specName !== null">规格：{{ bestSpec.specName }}</text>
				<text class="recommendation-price" v-if="bestSpec.unitPrice !== null">
					单价：¥{{ bestSpec.unitPrice.toFixed(4) }} / {{ bestSpec.baseUnit }}
				</text>
				<text class="recommendation-price" v-else style="color:#94a3b8;font-size:14px;">
					不可比（总价最低）
				</text>
				<text class="recommendation-save" v-if="savePercent !== null">
					比次优规格便宜 {{ savePercent }}%
				</text>
			</view>
		</view>

		<!-- ===== 📊 规格价格对比表格 ===== -->
		<view class="compare-card" v-if="specStats.length > 0">
			<view class="compare-header" @click="toggleCompare">
				<text class="compare-title">📊 规格价格对比</text>
				<view class="compare-header-right">
					<text class="compare-base-unit" @click.stop="toggleBaseUnit">
						基准单位：{{ priceBaseUnit }}
					</text>
					<text class="compare-toggle">{{ showCompare ? '收起' : '展开' }}</text>
				</view>
			</view>

			<view v-if="showCompare" class="compare-body">
				<!-- 表格头部 -->
				<view class="compare-table">
					<view class="compare-table-header">
						<view class="compare-cell compare-cell-label">参数</view>
						<view class="compare-cell" v-for="(spec, idx) in sortedSpecs" :key="spec.specName"
							:class="{ 'best-col': spec.isBest }">
							<text v-if="spec.isBest" class="best-tag">⭐</text>
						</view>
					</view>

					<!-- 行：品牌 -->
					<view class="compare-table-row">
						<view class="compare-cell compare-cell-label">品牌</view>
						<view class="compare-cell" v-for="spec in sortedSpecs" :key="spec.specName"
							:class="{ 'best-col': spec.isBest }">
							{{ spec.brand }}
						</view>
					</view>

					<!-- 行：总价 -->
					<view class="compare-table-row">
						<view class="compare-cell compare-cell-label">总价</view>
						<view class="compare-cell" v-for="spec in sortedSpecs" :key="spec.specName"
							:class="{ 'best-col': spec.isBest }">
							¥{{ spec.minPrice.toFixed(2) }}
						</view>
					</view>

					<!-- 行：单位价格（只有所有规格都有unitPrice时才显示） -->
					<view class="compare-table-row" v-if="hasUnitPrice">
						<view class="compare-cell compare-cell-label">单位价格</view>
						<view class="compare-cell highlight-price" v-for="spec in sortedSpecs" :key="spec.specName"
							:class="{ 'best-col': spec.isBest, 'best-price-value': spec.isBest }">
							<template v-if="spec.unitPrice !== null">
								¥{{ spec.unitPrice.toFixed(4) }} / {{ spec.baseUnit }}
							</template>
							<template v-else>—</template>
						</view>
					</view>

					<!-- 行：规格详情 -->
					<view class="compare-table-row">
						<view class="compare-cell compare-cell-label">规格</view>
						<view class="compare-cell" v-for="spec in sortedSpecs" :key="spec.specName"
							:class="{ 'best-col': spec.isBest }">
							{{ spec.quantity }}{{ spec.unit }}
						</view>
					</view>

					<!-- 行：价格范围 -->
					<view class="compare-table-row">
						<view class="compare-cell compare-cell-label">价格范围</view>
						<view class="compare-cell" v-for="spec in sortedSpecs" :key="spec.specName"
							:class="{ 'best-col': spec.isBest }">
							¥{{ spec.minPrice.toFixed(2) }} ~ ¥{{ spec.maxPrice.toFixed(2) }}
						</view>
					</view>

					<!-- 行：记录数 -->
					<view class="compare-table-row" v-if="sortedSpecs.some(s => s.recordCount > 0)">
						<view class="compare-cell compare-cell-label">记录数</view>
						<view class="compare-cell" v-for="spec in sortedSpecs" :key="spec.specName"
							:class="{ 'best-col': spec.isBest }">
							{{ spec.recordCount }} 条
						</view>
					</view>

					<!-- 行：价格趋势（简单箭头） -->
					<view class="compare-table-row" v-if="sortedSpecs.some(s => s.trend)">
						<view class="compare-cell compare-cell-label">趋势</view>
						<view class="compare-cell" v-for="spec in sortedSpecs" :key="spec.specName"
							:class="{ 'best-col': spec.isBest }">
							<text :class="['trend-icon', spec.trend.direction]">
								{{ spec.trend.direction === 'up' ? '↗' : spec.trend.direction === 'down' ? '↘' : '→' }}
							</text>
							<text class="trend-percent" v-if="spec.trend && spec.trend.changePercent">
								{{ spec.trend.changePercent }}%
							</text>
						</view>
					</view>
				</view>

				<view v-if="specStats.length === 1" class="compare-hint">
					<text>只有一种规格，添加更多规格进行价格对比</text>
				</view>
			</view>
		</view>

		<!-- ===== 规格快速切换 ===== -->
		<view class="spec-switcher" v-if="specStats.length > 1">
			<scroll-view scroll-x class="spec-switcher-scroll" show-scrollbar="false">
				<view v-for="spec in specStats" :key="spec.specName" class="spec-switcher-item"
					:class="{ active: selectedSpecName === spec.specName }" @click="selectSpec(spec.specName)"
					:ref="'spec_' + spec.specName">
					<text class="spec-switcher-name">{{ spec.specName }}</text>
					<view class="spec-switcher-price-wrap">
						<text class="spec-switcher-price">¥{{ spec.latestPrice.toFixed(2) }}</text>
						<text v-if="spec.trend" :class="['spec-switcher-trend', spec.trend.direction]">
							{{ spec.trend.direction === 'up' ? '↑' : spec.trend.direction === 'down' ? '↓' : '—' }}
						</text>
					</view>
					<text v-if="spec.isBest" class="spec-switcher-badge">⭐</text>
				</view>
			</scroll-view>
		</view>

		<!-- ===== 价格历史 ===== -->
		<view class="detail-history">
			<view class="history-header">
				<view class="history-header-left">
					<text class="history-title">📜 价格记录</text>
					<text class="history-count">{{ product ? product.prices.length : 0 }}</text>
				</view>
				<view class="history-header-right">
					<picker mode="selector" :range="filterOptions" :value="filterIndex" @change="onFilterChange">
						<view class="filter-sort">{{ filterOptions[filterIndex] || '全部' }}</view>
					</picker>
				</view>
			</view>

			<view class="filter-bar">
				<view class="filter-group">
					<text class="filter-label">从</text>
					<picker mode="date" :value="dateFrom" @change="onDateFromChange">
						<view class="filter-date">{{ dateFrom || '选择' }}</view>
					</picker>
					<text class="filter-label">至</text>
					<picker mode="date" :value="dateTo" @change="onDateToChange">
						<view class="filter-date">{{ dateTo || '选择' }}</view>
					</picker>
				</view>
				<text v-if="dateFrom || dateTo" class="filter-clear" @click="clearDateFilter">清除</text>
			</view>

			<scroll-view scroll-y class="price-scroll">
				<view v-if="filteredPrices.length > 0">
					<view v-for="(item, idx) in filteredPrices" :key="item.id || idx" class="price-item">
						<view class="price-info">
							<text class="price-date">{{ formatDate(item.recordDate) }}</text>
							<text class="price-spec">{{ item.brand || '品牌' }}</text>
							<text class="price-spec">{{ item.specName || '默认' }}</text>
							<text class="price-value">¥{{ item.price.toFixed(2) }}</text>
							<text :class="['price-change', getChangeClass(item, idx)]">
								{{ getChangeText(item, idx) }}
							</text>
						</view>
						<view class="price-actions">
							<!-- 新增编辑按钮 -->
							<uni-icons type="compose" @click.stop="openEditPriceModal(item)" size="18" color="#94a3b8"
								class="price-edit" />
							<text class="price-delete" @click.stop="deletePriceItem(item)">✕</text>
						</view>
					</view>
				</view>
				<view v-else class="price-empty">
					<text class="empty-icon">📭</text>
					<text class="empty-text">{{ dateFrom || dateTo ? '没有匹配的记录' : '暂无价格记录' }}</text>
					<text v-if="!dateFrom && !dateTo" class="empty-hint">点击右上角 ＋ 添加第一条记录</text>
				</view>
			</scroll-view>
		</view>

		<!-- ===== 弹窗：添加/编辑价格 ===== -->
		<view v-if="showPriceModal" class="modal-mask" @click.self="closePriceModal">
			<view class="modal-box modal-small">
				<view class="modal-header">
					<text class="modal-title">{{ isEditPriceMode ? '编辑价格' : '记录价格' }}</text>
					<text class="modal-close" @click="closePriceModal">✕</text>
				</view>
				<view class="modal-body">
					<view class="price-product-badge" v-if="product">
						<text>{{ product.name }}</text>
					</view>
					<view class="form-group">
						<text class="form-label">品牌</text>
						<input type="text" placeholder="" v-model="newPrice.brand" class="form-input" />
					</view>
					<view class="form-group">
						<text class="form-label">日期 <text class="required">*</text></text>
						<picker mode="date" :value="newPrice.recordDate" :end="todayDate"
							@change="onNewPriceDateChange">
							<view class="form-input date-picker">{{ newPrice.recordDate || '请选择日期' }}</view>
						</picker>
					</view>
					<view class="form-group">
						<text class="form-label">价格 (¥) <text class="required">*</text></text>
						<input type="digit" v-model="newPrice.price" placeholder="0.00" class="form-input" />
					</view>
					<!-- 规格名称输入 + 实时解析 -->
					<view class="form-group">
						<text class="form-label">规格名称 <text class="required">*</text></text>
						<view class="spec-input-wrapper" :class="parseStatus">
							<input type="text" v-model="newPrice.specName" class="form-input spec-input"
								@input="onSpecNameInput" @focus="onSpecNameFocus" @blur="onSpecNameBlur" />
							<!-- 状态图标 -->
							<text class="parse-status-icon" v-if="parseStatus === 'success'">✅</text>
							<text class="parse-status-icon" v-else-if="parseStatus === 'partial'">⚠️</text>
							<text class="parse-status-icon" v-else-if="parseStatus === 'error'">❌</text>
						</view>

						<!-- 历史规格联想（保留原有） -->
						<view v-if="showSpecSuggestions && specSuggestions.length" class="spec-suggestions">
							<view v-for="sug in specSuggestions" :key="sug" class="spec-suggestion-item"
								@mousedown.prevent="applySpecSuggestion(sug)"
								@touchstart.prevent="applySpecSuggestion(sug)">
								{{ sug }}
							</view>
						</view>

						<!-- 增强的解析结果展示 -->
						<view v-if="parseResult" class="parse-result" :class="parseStatus">
							<view class="parse-result-main">
								<text
									class="parse-icon">{{ parseStatus === 'success' ? '✅' : parseStatus === 'partial' ? '⚠️' : '❌' }}</text>
								<text class="parse-text">{{ parseResult.message }}</text>
							</view>
							<view v-if="parseResult.detail" class="parse-result-detail">
								<text>📐 解析详情：{{ parseResult.detail }}</text>
							</view>
						</view>

						<text class="form-hint">💡 支持格式：200ml×24盒、24盒*200ml、500ml、24盒、10个×5包</text>
					</view>
					<view class="form-row">
						<view class="form-group">
							<text class="form-label">总数量 <text class="required">*</text></text>
							<input type="number" v-model="newPrice.quantity" placeholder="自动计算" class="form-input" />
						</view>
						<view class="form-group">
							<text class="form-label">单位 <text class="required">*</text></text>
							<picker mode="selector" :range="unitOptions" :value="newPrice.unitIndex"
								@change="onNewPriceUnitChange">
								<view class="form-input">{{ newPrice.unit || '请选择' }}</view>
							</picker>
						</view>
					</view>
					<view class="form-group">
						<text class="form-label">货源</text>
						<input type="text" v-model="newPrice.supply" placeholder="" class="form-input" />
					</view>
				</view>
				<view class="modal-footer">
					<button class="btn-cancel" @click="closePriceModal">取消</button>
					<button class="btn-save" @click="savePrice">{{ isEditPriceMode ? '更新' : '添加' }}</button>
				</view>
			</view>
		</view>

		<!-- ===== Toast ===== -->
		<view v-if="toastMessage" class="toast" :class="toastType">
			<text>{{ toastMessage }}</text>
		</view>
	</view>
</template>

<script>
	import {
		priceTrackerApi
	} from "@/api/index.js";

	export default {
		name: 'PriceTrackerInfo',
		data() {
			return {
				parseStatus: 'idle', // 'idle' | 'success' | 'partial' | 'error'
				productId: null,
				product: null,

				unitOptions: ['ml', 'L', 'g', 'kg', '个', '包', '盒', '瓶', '罐'],
				priceBaseUnit: 'auto',
				baseUnitOptions: ['auto', 'ml', '100ml', 'L', 'g', '100g', 'kg', '个', '件', '片'],

				// 筛选
				filterSpec: '',
				filterOptions: ['全部规格'],
				filterIndex: 0,
				dateFrom: '',
				dateTo: '',

				// 对比展开
				showCompare: true,

				// 当前选中的规格
				selectedSpecName: '',

				// 弹窗
				showPriceModal: false,
				isEditPriceMode: false, // 新增：是否编辑模式
				editingPriceId: null, // 新增：编辑中的记录ID
				newPrice: {
					recordDate: '',
					price: null,
					quantity: null,
					unit: '',
					unitIndex: 0,
					specName: '',
					brand: '',
					supply: '',
				},
				parseResult: null,
				showSpecSuggestions: false,
				specSuggestions: [],

				toastMessage: '',
				toastType: 'toast-info',
				toastTimer: null
			};
		},
		computed: {
			// ----- 规格统计 (保持不变) -----
			specStats() {
				if (!this.product || !this.product.prices || this.product.prices.length === 0) {
					return [];
				}

				const specMap = {};
				const unitTypes = new Set();

				this.product.prices.forEach(p => {
					const key = p.specName || '默认';
					if (!specMap[key]) {
						specMap[key] = {
							specName: key,
							quantity: p.quantity || 0,
							unit: p.unit || '',
							prices: [],
							minPrice: null,
							minDate: null,
							maxPrice: null,
							maxDate: null,
							latestPrice: 0,
							latestDate: null,
							unitPrice: null,
							baseUnit: '',
							recordCount: 0,
							trend: null,
							brand: p.brand || '',
							supply: p.supply || '',
						};
					}
					const spec = specMap[key];
					const price = p.price;
					const recordDate = p.recordDate;

					spec.prices.push(price);
					spec.recordCount = (spec.recordCount || 0) + 1;

					if (spec.minPrice === null || price < spec.minPrice) {
						spec.minPrice = price;
						spec.minDate = recordDate;
					}
					if (spec.maxPrice === 0 || price > spec.maxPrice) {
						spec.maxPrice = price;
						spec.maxDate = recordDate;
					}
					const d = new Date(recordDate);
					if (!spec.latestDate || d > new Date(spec.latestDate)) {
						spec.latestPrice = price;
						spec.latestDate = recordDate;
						spec.quantity = p.quantity || 0;
						spec.unit = p.unit || '';
					}
					if (p.unit) unitTypes.add(p.unit);
				});

				const weightUnits = ['g', 'kg', 'G', 'KG', 'Kg'];
				const capacityUnits = ['ml', 'l', 'ML', 'L'];
				const countUnits = ['个', '包', '盒', '瓶', '罐', '片', '桶', '箱', '只'];

				let hasWeight = false,
					hasCapacity = false,
					hasCount = false;
				unitTypes.forEach(u => {
					if (weightUnits.includes(u)) hasWeight = true;
					if (capacityUnits.includes(u)) hasCapacity = true;
					if (countUnits.includes(u)) hasCount = true;
				});

				let finalBaseUnit = this.priceBaseUnit;
				if (this.priceBaseUnit === 'auto') {
					if (hasWeight) finalBaseUnit = '100g';
					else if (hasCapacity) finalBaseUnit = '100ml';
					else if (hasCount) finalBaseUnit = '个';
					else finalBaseUnit = '个';
				}
				this.priceBaseUnit = finalBaseUnit;

				const result = [];
				for (const key in specMap) {
					const spec = specMap[key];
					if (spec.quantity > 0 && spec.unit) {
						spec.unitPrice = this.calcUnitPriceByBase(spec.minPrice, spec.quantity, spec.unit, finalBaseUnit);
						spec.baseUnit = finalBaseUnit;
					} else {
						spec.unitPrice = null;
						spec.baseUnit = finalBaseUnit;
					}
					const prices = spec.prices;
					if (prices.length > 0) {
						spec.minPrice = Math.min(...prices);
						spec.maxPrice = Math.max(...prices);
					}
					spec.recordCount = prices.length;
					if (prices.length >= 2) {
						const sorted = [...prices].sort((a, b) => a - b);
						const first = sorted[0],
							last = sorted[sorted.length - 1];
						const diff = last - first;
						const percent = first !== 0 ? (diff / first * 100) : 0;
						spec.trend = {
							direction: diff > 0 ? 'up' : diff < 0 ? 'down' : 'flat',
							changePercent: percent.toFixed(1)
						};
					} else {
						spec.trend = {
							direction: 'flat',
							changePercent: '0'
						};
					}
					result.push(spec);
				}
				this.currentBaseUnit = finalBaseUnit;
				return result;
			},

			sortedSpecs() {
				const withPrice = this.specStats.filter(s => s.unitPrice !== null && s.unitPrice > 0);
				const withoutPrice = this.specStats.filter(s => s.unitPrice === null || s.unitPrice <= 0);
				withPrice.sort((a, b) => a.unitPrice - b.unitPrice);
				const result = withPrice.concat(withoutPrice);
				if (result.length > 0 && result[0].unitPrice !== null) {
					result[0].isBest = true;
				}
				return result;
			},

			bestSpec() {
				return this.sortedSpecs.length ? this.sortedSpecs[0] : null;
			},

			savePercent() {
				if (!this.bestSpec) return null;
				if (this.sortedSpecs.length < 2) return null;
				const second = this.sortedSpecs[1];
				if (!second || !second.unitPrice) return null;
				if (!this.bestSpec.unitPrice || this.bestSpec.unitPrice === 0) return null;
				const diff = second.unitPrice - this.bestSpec.unitPrice;
				if (this.bestSpec.unitPrice === 0) return null;
				return ((diff / this.bestSpec.unitPrice) * 100).toFixed(1);
			},

			hasUnitPrice() {
				return this.sortedSpecs.some(s => s.unitPrice !== null && s.unitPrice > 0);
			},

			selectedSpec() {
				if (!this.selectedSpecName || this.specStats.length === 0) {
					return this.bestSpec || null;
				}
				return this.specStats.find(s => s.specName === this.selectedSpecName) || this.bestSpec || null;
			},

			specNameList() {
				const names = ['全部规格'];
				this.specStats.forEach(s => names.push(s.specName));
				return names;
			},

			filteredPrices() {
				if (!this.product || !this.product.prices) return [];
				let prices = [...this.product.prices];
				if (this.filterSpec && this.filterSpec !== '全部规格') {
					prices = prices.filter(p => (p.specName || '默认') === this.filterSpec);
				}
				if (this.dateFrom) {
					prices = prices.filter(p => new Date(p.recordDate) >= new Date(this.dateFrom));
				}
				if (this.dateTo) {
					prices = prices.filter(p => new Date(p.recordDate) <= new Date(this.dateTo));
				}
				prices.sort((a, b) => new Date(b.recordDate) - new Date(a.recordDate));
				return prices;
			},

			todayDate() {
				const d = new Date();
				return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
			},
		},
		watch: {
			specNameList(newList) {
				this.filterOptions = newList;
			},
			bestSpec(newBest) {
				if (newBest && !this.selectedSpecName) {
					this.selectedSpecName = newBest.specName;
				}
			},
			filterSpec(newVal) {
				if (newVal && newVal !== '全部规格') {
					this.selectedSpecName = newVal;
				}
			}
		},
		onLoad(options) {
			if (options.id) {
				this.productId = options.id;
				this.loadProductDetail();
			}
		},
		methods: {
			// ----- 辅助函数（保持不变）-----
			getBaseFactor(unit, baseUnit) {
				if (!unit || !baseUnit) return 1;
				const mlMap = {
					'100ml': 100,
					'ml': 1,
					'L': 1000,
					'ML': 1,
					'l': 1000
				};
				const gMap = {
					'100g': 100,
					'g': 1,
					'kg': 1000,
					'G': 1,
					'KG': 1000,
					'Kg': 1000
				};
				const countMap = {
					'个': 1,
					'包': 1,
					'盒': 1,
					'瓶': 1,
					'罐': 1,
					'箱': 1,
					'桶': 1,
					'片': 1
				};
				let type = 'count',
					value = 1;
				if (unit in mlMap) {
					type = 'volume';
					value = mlMap[unit];
				} else if (unit in gMap) {
					type = 'weight';
					value = gMap[unit];
				} else if (unit in countMap) {
					type = 'count';
					value = countMap[unit];
				}
				let baseType = 'count',
					baseValue = 1;
				if (baseUnit in mlMap) {
					baseType = 'volume';
					baseValue = mlMap[baseUnit];
				} else if (baseUnit in gMap) {
					baseType = 'weight';
					baseValue = gMap[baseUnit];
				} else if (baseUnit in countMap) {
					baseType = 'count';
					baseValue = countMap[baseUnit];
				}
				if (type !== baseType) return null;
				return value / baseValue;
			},

			calcUnitPriceByBase(price, quantity, unit, baseUnit) {
				if (!price || !quantity || !unit || !baseUnit) return null;
				const factor = this.getBaseFactor(unit, baseUnit);
				if (factor === null || factor === 0) return null;
				return price / (quantity * factor);
			},

			toggleBaseUnit() {
				const options = ['auto', 'ml', '100ml', 'L', 'g', '100g', 'kg', '个', '件'];
				const idx = options.indexOf(this.priceBaseUnit);
				const next = (idx + 1) % options.length;
				this.priceBaseUnit = options[next];
				this.$forceUpdate();
			},

			// ----- 规格解析（保持不变）-----
			parseSpecName(specName) {
				if (!specName || !specName.trim()) {
					return {
						success: false,
						message: '请输入规格名称'
					};
				}
				let text = specName.trim();
				text = text.replace(/[×xX*Ｘ]/g, '*');
				text = text.replace(/\s+/g, ' ').trim();

				const compoundRegex = /^(\d+)\s*([a-zA-Z\u4e00-\u9fa5]+)\s*\*\s*(\d+)\s*([a-zA-Z\u4e00-\u9fa5]+)$/;
				const compoundMatch = text.match(compoundRegex);
				if (compoundMatch) {
					let num1 = parseInt(compoundMatch[1]);
					let unit1 = compoundMatch[2];
					let num2 = parseInt(compoundMatch[3]);
					let unit2 = compoundMatch[4];
					const baseUnits = ['ml', 'l', 'g', 'kg', '个', '片', '粒', '颗', '只'];
					const packUnits = ['包', '盒', '瓶', '罐', '箱', '袋', '桶', '听'];
					let baseNum, baseUnit, packNum, packUnit;
					if (baseUnits.includes(unit1) && packUnits.includes(unit2)) {
						baseNum = num1;
						baseUnit = unit1;
						packNum = num2;
						packUnit = unit2;
					} else if (baseUnits.includes(unit2) && packUnits.includes(unit1)) {
						baseNum = num2;
						baseUnit = unit2;
						packNum = num1;
						packUnit = unit1;
					} else if (packUnits.includes(unit1) && packUnits.includes(unit2)) {
						baseNum = num1;
						baseUnit = unit1;
						packNum = num2;
						packUnit = unit2;
					} else {
						baseNum = num1;
						baseUnit = unit1;
						packNum = num2;
						packUnit = unit2;
					}
					const totalQuantity = baseNum * packNum;
					const finalUnit = baseUnit;
					const unitType = this.getUnitType(finalUnit);
					return {
						success: true,
						quantity: totalQuantity,
						unit: finalUnit,
						unitType: unitType,
						message: `✅ 解析成功：${baseNum}${baseUnit} × ${packNum}${packUnit} = ${totalQuantity}${finalUnit}`,
						detail: `基准单位：${finalUnit}（${unitType === 'volume' ? '容量' : unitType === 'weight' ? '重量' : '数量'}）`
					};
				}
				const simpleRegex = /^(\d+)\s*([a-zA-Z\u4e00-\u9fa5]+)$/;
				const simpleMatch = text.match(simpleRegex);
				if (simpleMatch) {
					const num = parseInt(simpleMatch[1]);
					const unit = simpleMatch[2];
					const unitType = this.getUnitType(unit);
					return {
						success: true,
						quantity: num,
						unit: unit,
						unitType: unitType,
						message: `✅ 解析成功：${num}${unit}`,
						detail: `单位类型：${unitType === 'volume' ? '容量' : unitType === 'weight' ? '重量' : '数量'}`
					};
				}
				const numOnlyRegex = /^(\d+)$/;
				const numMatch = text.match(numOnlyRegex);
				if (numMatch) {
					return {
						success: false,
						message: '⚠️ 仅识别到数量，请补充单位（如 ml、g、个、盒 等）',
						partial: true,
						quantity: parseInt(numMatch[1]),
						unit: null,
						unitType: null
					};
				}
				return {
					success: false,
					message: '❌ 无法识别格式，请参照提示输入（如 200ml×24盒）'
				};
			},

			getUnitType(unit) {
				const volume = ['ml', 'l', '升', '毫升'];
				const weight = ['g', 'kg', '克', '千克', '斤', '两'];
				const count = ['个', '包', '盒', '瓶', '罐', '箱', '袋', '桶', '听', '片', '粒', '颗', '只', '条', '根'];
				const u = unit.toLowerCase();
				if (volume.includes(u) || volume.includes(unit)) return 'volume';
				if (weight.includes(u) || weight.includes(unit)) return 'weight';
				if (count.includes(unit)) return 'count';
				return 'unknown';
			},

			onSpecNameInput() {
				const raw = this.newPrice.specName;
				if (!raw || !raw.trim()) {
					this.parseResult = null;
					this.parseStatus = 'idle';
					return;
				}
				const result = this.parseSpecName(raw);
				if (result.success) {
					this.parseStatus = 'success';
					this.parseResult = result;
					this.newPrice.quantity = result.quantity;
					const unitIndex = this.unitOptions.indexOf(result.unit);
					if (unitIndex !== -1) {
						this.newPrice.unitIndex = unitIndex;
						this.newPrice.unit = result.unit;
					} else {
						this.newPrice.unit = result.unit;
					}
				} else if (result.partial) {
					this.parseStatus = 'partial';
					this.parseResult = result;
					this.newPrice.quantity = result.quantity;
				} else {
					this.parseStatus = 'error';
					this.parseResult = result;
				}
				this.updateSpecSuggestions();
			},

			onSpecNameFocus() {
				this.showSpecSuggestions = true;
				this.updateSpecSuggestions();
			},

			onSpecNameBlur() {
				setTimeout(() => {
					this.showSpecSuggestions = false;
				}, 300);
			},

			updateSpecSuggestions() {
				const input = this.newPrice.specName.trim().toLowerCase();
				if (!input) {
					const all = this.product.prices.map(p => p.specName).filter(Boolean);
					this.specSuggestions = [...new Set(all)];
					return;
				}
				const all = this.product.prices.map(p => p.specName).filter(Boolean);
				const matched = all.filter(name => name.toLowerCase().includes(input));
				this.specSuggestions = [...new Set(matched)];
			},

			applySpecSuggestion(sug) {
				this.newPrice.specName = sug;
				this.showSpecSuggestions = false;
				this.onSpecNameInput();
			},

			// ===== 选择规格 =====
			selectSpec(specName) {
				this.selectedSpecName = specName;
				this.filterSpec = specName;
				const index = this.filterOptions.indexOf(specName);
				if (index !== -1) this.filterIndex = index;
				this.$nextTick(() => {
					// 滚动效果可选
				});
			},

			toggleCompare() {
				this.showCompare = !this.showCompare;
			},

			formatDate(dateStr) {
				if (!dateStr) return '';
				const d = new Date(dateStr);
				return `${d.getFullYear()}/${d.getMonth()+1}/${d.getDate()}`;
			},

			getChangeClass(item, idx) {
				const prices = this.filteredPrices;
				if (idx === prices.length - 1) return 'flat';
				const prev = prices[idx + 1];
				if (!prev) return 'flat';
				if (item.price > prev.price) return 'up';
				if (item.price < prev.price) return 'down';
				return 'flat';
			},

			getChangeText(item, idx) {
				const prices = this.filteredPrices;
				if (idx === prices.length - 1) return '--';
				const prev = prices[idx + 1];
				if (!prev) return '--';
				const change = ((item.price - prev.price) / prev.price * 100).toFixed(1);
				if (item.price > prev.price) return '+' + change + '%';
				if (item.price < prev.price) return change + '%';
				return '0%';
			},

			showToast(message, type = 'toast-info') {
				if (this.toastTimer) clearTimeout(this.toastTimer);
				this.toastMessage = message;
				this.toastType = type;
				this.toastTimer = setTimeout(() => {
					this.toastMessage = '';
				}, 3000);
			},

			goBack() {
				uni.navigateBack();
			},

			loadProductDetail() {
				const _this = this;
				this.product = {
					id: this.productId,
					name: '加载中...',
					prices: []
				};
				priceTrackerApi.getProductById(this.productId).then(productRes => {
					if (productRes.data) {
						_this.product = {
							...productRes.data,
							prices: []
						};
						return priceTrackerApi.getRecordsByProductId(_this.productId);
					} else {
						_this.showToast('商品不存在', 'toast-error');
						return Promise.reject('商品不存在');
					}
				}).then(priceRes => {
					if (priceRes && priceRes.data) {
						_this.product.prices = priceRes.data.map(item => ({
							id: item.id,
							productId: item.productId,
							price: item.price,
							quantity: item.quantity,
							specName: item.specName,
							unit: item.unit,
							brand: item.brand,
							supply: item.supply,
							recordDate: item.recordDate,
							createdAt: item.createdAt
						}));
					}
					_this.$nextTick(() => {
						if (_this.bestSpec) {
							_this.selectedSpecName = _this.bestSpec.specName;
							_this.filterSpec = _this.bestSpec.specName;
							const idx = _this.filterOptions.indexOf(_this.bestSpec.specName);
							if (idx !== -1) _this.filterIndex = idx;
						}
					});
				}).catch(err => {
					console.error("加载失败:", err);
					_this.showToast('加载失败', 'toast-error');
				});
			},

			// ===== 筛选=====
			onDateFromChange(e) {
				this.dateFrom = e.detail.value;
			},
			onDateToChange(e) {
				this.dateTo = e.detail.value;
			},
			onFilterChange(e) {
				this.filterIndex = e.detail.value;
				this.filterSpec = this.filterOptions[this.filterIndex];
				if (this.filterSpec && this.filterSpec !== '全部规格') {
					this.selectedSpecName = this.filterSpec;
				}
			},
			clearDateFilter() {
				this.dateFrom = '';
				this.dateTo = '';
			},

			// ===== 弹窗操作 =====
			onNewPriceDateChange(e) {
				this.newPrice.recordDate = e.detail.value;
			},
			onNewPriceUnitChange(e) {
				this.newPrice.unit = this.unitOptions[e.detail.value];
				this.newPrice.unitIndex = e.detail.value;
			},

			// ---- 打开新增 ----
			openAddPriceModal() {
				if (!this.product) {
					this.showToast('商品不存在', 'toast-error');
					return;
				}
				this.isEditPriceMode = false;
				this.editingPriceId = null;
				this.newPrice = {
					recordDate: this.todayDate,
					price: null,
					quantity: null,
					unit: '',
					unitIndex: 0,
					specName: '',
					brand: '',
					supply: '',
				};
				this.parseResult = null;
				this.showSpecSuggestions = false;
				this.showPriceModal = true;
			},

			// ---- 打开编辑 ----
			openEditPriceModal(item) {
				if (!this.product) {
					this.showToast('商品不存在', 'toast-error');
					return;
				}
				this.isEditPriceMode = true;
				this.editingPriceId = item.id;
				// 填充数据
				this.newPrice = {
					recordDate: item.recordDate || this.todayDate,
					price: item.price !== undefined ? item.price : null,
					quantity: item.quantity || null,
					unit: item.unit || '',
					unitIndex: this.unitOptions.indexOf(item.unit) !== -1 ? this.unitOptions.indexOf(item.unit) : 0,
					specName: item.specName || '',
					brand: item.brand || '',
					supply: item.supply || '',
				};
				// 触发解析以显示结果
				this.parseResult = null;
				this.parseStatus = 'idle';
				if (this.newPrice.specName) {
					this.onSpecNameInput();
				}
				this.showSpecSuggestions = false;
				this.showPriceModal = true;
			},

			// ---- 保存（新增/编辑合并） ----
			savePrice() {
				const _this = this;
				if (!this.newPrice.recordDate) {
					this.showToast('请选择日期', 'toast-error');
					return;
				}
				if (!this.newPrice.price || this.newPrice.price <= 0) {
					this.showToast('请输入正确的价格', 'toast-error');
					return;
				}
				if (!this.newPrice.specName || !this.newPrice.specName.trim()) {
					this.showToast('请输入规格名称', 'toast-error');
					return;
				}
				if (!this.newPrice.quantity || this.newPrice.quantity <= 0) {
					this.showToast('请输入正确的数量', 'toast-error');
					return;
				}
				if (!this.newPrice.unit) {
					this.showToast('请选择单位', 'toast-error');
					return;
				}

				const payload = {
					productId: this.product.id,
					price: parseFloat(this.newPrice.price),
					recordDate: this.newPrice.recordDate,
					specName: this.newPrice.specName.trim(),
					quantity: parseInt(this.newPrice.quantity),
					unit: this.newPrice.unit,
					brand: this.newPrice.brand,
					supply: this.newPrice.supply,
				};

				if (this.isEditPriceMode) {
					// ---- 编辑模式 ----
					payload.id = this.editingPriceId;
					priceTrackerApi.editPriceRecord(payload).then(res => {
						if (res.status === 200) {
							// 更新本地数据
							const idx = _this.product.prices.findIndex(p => p.id === _this.editingPriceId);
							if (idx !== -1) {
								const updated = {
									..._this.product.prices[idx],
									price: payload.price,
									recordDate: payload.recordDate,
									specName: payload.specName,
									quantity: payload.quantity,
									unit: payload.unit,
									brand: payload.brand,
									supply: payload.supply,
								};
								_this.$set(_this.product.prices, idx, updated);
							}
							_this.showToast('价格更新成功', 'toast-success');
							_this.closePriceModal();
						} else {
							_this.showToast(res.data ? res.data.msg || '更新失败' : '更新失败', 'toast-error');
						}
					}).catch(err => {
						console.error("更新失败:", err);
						_this.showToast('更新失败', 'toast-error');
					});
				} else {
					// ---- 新增模式 ----
					priceTrackerApi.addPriceRecord(payload).then(res => {
						if (res.status === 200) {
							const newRecord = {
								id: res.data.id ? res.data.id : Date.now(),
								productId: payload.productId,
								price: payload.price,
								quantity: payload.quantity,
								specName: payload.specName,
								unit: payload.unit,
								brand: payload.brand,
								supply: payload.supply,
								recordDate: payload.recordDate
							};
							_this.product.prices.push(newRecord);
							_this.showToast('价格添加成功', 'toast-success');
							_this.closePriceModal();
						} else {
							_this.showToast(res.data ? res.data.msg || '添加失败' : '添加失败', 'toast-error');
						}
					}).catch(err => {
						console.error("添加失败:", err);
						_this.showToast('添加失败', 'toast-error');
					});
				}
			},

			// ---- 删除 ----
			deletePriceItem(priceItem) {
				const _this = this;
				uni.showModal({
					title: '提示',
					content: `确定删除 ${this.formatDate(priceItem.recordDate)} (${priceItem.specName || '默认'}) 的价格记录 ¥${priceItem.price} 吗？`,
					success(res) {
						if (res.confirm) {
							priceTrackerApi.delPriceRecord(priceItem.id).then(result => {
								if (result.status === 200) {
									const idx = _this.product.prices.findIndex(p => p.id === priceItem.id);
									if (idx !== -1) _this.product.prices.splice(idx, 1);
									_this.showToast('删除成功', 'toast-success');
								} else {
									_this.showToast(result.data ? result.data.msg || '删除失败' : '删除失败',
										'toast-error');
								}
							}).catch(err => {
								console.error("删除失败:", err);
								_this.showToast('删除失败', 'toast-error');
							});
						}
					}
				});
			},

			// ---- 关闭弹窗 ----
			closePriceModal() {
				this.showPriceModal = false;
				this.isEditPriceMode = false;
				this.editingPriceId = null;
				this.parseResult = null;
				this.showSpecSuggestions = false;
			}
		}
	};
</script>

<style scoped>
	/* ---- 推荐卡片 ---- */
	.best-recommendation {
		background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
		border-radius: 14px;
		padding: 12px 16px;
		margin-bottom: 12px;
		display: flex;
		align-items: center;
		gap: 12px;
		border: 1px solid #fcd34d;
		box-shadow: 0 2px 8px rgba(245, 158, 11, 0.15);
	}

	.recommendation-badge {
		font-size: 14px;
		font-weight: 700;
		color: #92400e;
		background: #fff7ed;
		padding: 4px 12px;
		border-radius: 20px;
		white-space: nowrap;
	}

	.recommendation-content {
		display: flex;
		align-items: center;
		flex-wrap: wrap;
		gap: 8px 16px;
	}

	.recommendation-name {
		font-weight: 600;
		color: #78350f;
		font-size: 15px;
	}

	.recommendation-price {
		font-weight: 700;
		color: #b45309;
		font-size: 18px;
	}

	.recommendation-save {
		font-size: 13px;
		color: #92400e;
		background: #fff7ed;
		padding: 2px 10px;
		border-radius: 12px;
	}

	/* ---- 对比表格 ---- */
	.compare-card {
		background: #ffffff;
		border-radius: 14px;
		border: 1px solid #eef2f6;
		margin-bottom: 12px;
		overflow: hidden;
	}

	.compare-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 10px 14px;
		cursor: pointer;
		background: #fafbfc;
		border-bottom: 1px solid #eef2f6;
	}

	.compare-title {
		font-weight: 600;
		font-size: 14px;
		color: #0f172a;
	}

	.compare-header-right {
		display: flex;
		align-items: center;
		gap: 12px;
	}

	.compare-base-unit {
		font-size: 12px;
		color: #4f7df3;
		background: #eef2ff;
		padding: 2px 10px;
		border-radius: 12px;
		cursor: pointer;
	}

	.compare-toggle {
		font-size: 12px;
		color: #94a3b8;
	}

	.compare-body {
		padding: 6px 8px 8px;
		overflow-x: auto;
	}

	.compare-table {
		display: table;
		width: 100%;
		border-collapse: collapse;
		font-size: 13px;
		min-width: 500px;
	}

	.compare-table-header,
	.compare-table-row {
		display: table-row;
	}

	.compare-table-header .compare-cell {
		font-weight: 600;
		color: #475569;
		background: #f8fafc;
		border-bottom: 2px solid #e2e8f0;
		padding: 8px 6px;
	}

	.compare-cell {
		display: table-cell;
		padding: 6px 6px;
		text-align: center;
		border-bottom: 1px solid #f1f5f9;
		vertical-align: middle;
		min-width: 60px;
	}

	.compare-cell-label {
		font-weight: 600;
		color: #64748b;
		text-align: left;
		background: #fafbfc;
		padding-left: 10px;
		min-width: 70px;
		white-space: nowrap;
	}

	.best-col {
		background: #fffbeb !important;
		border-left: 2px solid #fcd34d;
		border-right: 2px solid #fcd34d;
	}

	.best-col:first-child {
		border-left: none;
	}

	.best-col:last-child {
		border-right: none;
	}

	.best-price-value {
		color: #b45309;
		font-weight: 700;
	}

	.spec-name {
		font-weight: 500;
	}

	.best-tag {
		font-size: 16px;
		margin-left: 4px;
	}

	.highlight-price {
		font-weight: 600;
		color: #0f172a;
	}

	.trend-icon {
		font-size: 18px;
	}

	.trend-icon.up {
		color: #059669;
	}

	.trend-icon.down {
		color: #dc2626;
	}

	.trend-icon.flat {
		color: #94a3b8;
	}

	.trend-percent {
		font-size: 11px;
		margin-left: 2px;
		color: #64748b;
	}

	/* ---- 规格切换增强 ---- */
	.spec-switcher {
		background: #ffffff;
		border-radius: 14px;
		border: 1px solid #eef2f6;
		padding: 8px 0;
		margin-bottom: 12px;
	}

	.spec-switcher-scroll {
		white-space: nowrap;
		padding: 0 12px;
	}

	.spec-switcher-item {
		display: inline-flex;
		flex-direction: column;
		align-items: center;
		padding: 6px 14px;
		margin-right: 8px;
		border-radius: 10px;
		background: #f8fafc;
		border: 2px solid transparent;
		transition: all 0.2s;
		min-width: 70px;
		position: relative;
		cursor: pointer;
	}

	.spec-switcher-item.active {
		background: #eef2ff;
		border-color: #4f7df3;
	}

	.spec-switcher-item:active {
		transform: scale(0.95);
	}

	.spec-switcher-name {
		font-size: 13px;
		font-weight: 600;
		color: #0f172a;
	}

	.spec-switcher-price-wrap {
		display: flex;
		align-items: center;
		gap: 4px;
	}

	.spec-switcher-price {
		font-size: 14px;
		font-weight: 700;
		color: #4f7df3;
	}

	.spec-switcher-trend {
		font-size: 14px;
		font-weight: 700;
	}

	.spec-switcher-trend.up {
		color: #059669;
	}

	.spec-switcher-trend.down {
		color: #dc2626;
	}

	.spec-switcher-trend.flat {
		color: #94a3b8;
	}

	.spec-switcher-badge {
		font-size: 12px;
		position: absolute;
		top: -4px;
		right: -4px;
	}

	/* ---- 弹窗增强：规格联想 ---- */
	.spec-suggestions {
		background: #ffffff;
		border: 1px solid #e2e8f0;
		border-radius: 8px;
		margin-top: 4px;
		max-height: 120px;
		overflow-y: auto;
		box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
		z-index: 10;
	}

	.spec-suggestion-item {
		padding: 6px 12px;
		font-size: 13px;
		cursor: pointer;
		color: #0f172a;
		border-bottom: 1px solid #f1f5f9;
	}

	.spec-suggestion-item:last-child {
		border-bottom: none;
	}

	.spec-suggestion-item:active {
		background: #eef2ff;
	}

	.spec-history-hint {
		margin-top: 8px;
		padding: 8px 12px;
		background: #f1f5f9;
		border-radius: 8px;
		display: flex;
		justify-content: space-between;
		font-size: 13px;
		color: #475569;
	}

	.hint-trend.up {
		color: #059669;
	}

	.hint-trend.down {
		color: #dc2626;
	}

	.hint-trend.flat {
		color: #94a3b8;
	}


	/* ===== 解析结果 ===== */
	.parse-result {
		background: #d1fae5;
		border-radius: 8px;
		padding: 8px 12px;
		margin-top: 4px;
	}

	.parse-text {
		font-size: 13px;
		color: #065f46;
	}

	.form-hint {
		font-size: 12px;
		color: #94a3b8;
		margin-top: 4px;
		display: block;
	}

	/* ===== 规格快速切换 ===== */
	.spec-switcher {
		background: #ffffff;
		border-radius: 14px;
		border: 1px solid #eef2f6;
		padding: 8px 0;
		margin-bottom: 12px;
	}

	.spec-switcher-scroll {
		white-space: nowrap;
		padding: 0 12px;
	}

	.spec-switcher-item {
		display: inline-flex;
		flex-direction: column;
		align-items: center;
		padding: 6px 14px;
		margin-right: 8px;
		border-radius: 10px;
		background: #f8fafc;
		border: 2px solid transparent;
		transition: all 0.2s;
		min-width: 70px;
		position: relative;
		cursor: pointer;
	}

	.spec-switcher-item.active {
		background: #eef2ff;
		border-color: #4f7df3;
	}

	.spec-switcher-item:active {
		transform: scale(0.95);
	}

	.spec-switcher-name {
		font-size: 13px;
		font-weight: 600;
		color: #0f172a;
	}

	.spec-switcher-price {
		font-size: 14px;
		font-weight: 700;
		color: #4f7df3;
	}

	.spec-switcher-badge {
		font-size: 12px;
		position: absolute;
		top: -4px;
		right: -4px;
	}

	/* 其他样式保持不变 */
	.app-container {
		min-height: 100vh;
		background: #f0f4f8;
		padding: 12px 16px 20px;
		font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
		position: relative;
	}

	/* ===== Toast ===== */
	.toast {
		position: fixed;
		top: 16px;
		left: 50%;
		transform: translateX(-50%);
		padding: 8px 20px;
		border-radius: 12px;
		background: #1e293b;
		color: #ffffff;
		font-size: 13px;
		font-weight: 500;
		z-index: 9999;
		box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
		animation: slideDown 0.3s ease;
	}

	.toast-success {
		background: #10b981;
	}

	.toast-error {
		background: #ef4444;
	}

	.toast-info {
		background: #4f7df3;
	}

	@keyframes slideDown {
		from {
			opacity: 0;
			transform: translateX(-50%) translateY(-20px);
		}

		to {
			opacity: 1;
			transform: translateX(-50%) translateY(0);
		}
	}

	/* ===== 顶部导航 ===== */
	.page-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		flex-wrap: wrap;
		gap: 8px 12px;
		background: #ffffff;
		padding: 8px 14px;
		border-radius: 14px;
		box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
		margin-bottom: 12px;
	}

	.header-left {
		display: flex;
		align-items: center;
		gap: 6px;
	}

	.back-btn {
		font-size: 28px;
		line-height: 1;
		cursor: pointer;
		color: #0f172a;
		padding: 0 2px;
	}

	.app-title {
		font-weight: 700;
		font-size: 16px;
		color: #0f172a;
		max-width: 140px;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.header-right {
		display: flex;
		align-items: center;
		gap: 6px;
	}

	.btn-add {
		background: #4f7df3;
		color: #ffffff;
		border: none;
		width: 32px;
		height: 32px;
		border-radius: 50%;
		font-size: 20px;
		font-weight: 600;
		cursor: pointer;
		transition: background 0.2s;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.btn-add:active {
		background: #3b66d0;
		transform: scale(0.92);
	}

	/* ===== 规格对比卡片 ===== */
	.compare-card {
		background: #ffffff;
		border-radius: 14px;
		border: 1px solid #eef2f6;
		margin-bottom: 12px;
		overflow: hidden;
	}

	.compare-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 10px 14px;
		cursor: pointer;
		background: #fafbfc;
		border-bottom: 1px solid #eef2f6;
	}

	.compare-title {
		font-weight: 600;
		font-size: 14px;
		color: #0f172a;
	}

	.compare-toggle {
		font-size: 12px;
		color: #94a3b8;
	}

	.compare-body {
		padding: 10px 14px;
	}

	.compare-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 8px 0;
		border-bottom: 1px solid #f1f5f9;
	}

	.compare-item:last-child {
		border-bottom: none;
	}

	.compare-item.best {
		background: #fffbeb;
		margin: 0 -8px;
		padding: 8px 8px;
		border-radius: 8px;
		border-color: #fcd34d;
	}

	.compare-spec-info {
		display: flex;
		align-items: center;
		gap: 8px;
		flex-wrap: wrap;
	}

	.compare-spec-name {
		font-size: 14px;
		font-weight: 600;
		color: #0f172a;
	}

	.compare-spec-detail {
		font-size: 12px;
		color: #94a3b8;
	}

	.compare-badge {
		font-size: 10px;
		background: #f59e0b;
		color: #ffffff;
		padding: 1px 8px;
		border-radius: 12px;
	}

	.compare-price-row {
		display: flex;
		align-items: center;
		gap: 8px;
	}

	.compare-price {
		font-size: 16px;
		font-weight: 700;
		color: #4f7df3;
	}

	.compare-unit-price {
		font-size: 12px;
		color: #94a3b8;
	}

	.compare-hint {
		text-align: center;
		padding: 12px 0;
		color: #94a3b8;
		font-size: 13px;
	}

	/* ===== 历史价格 ===== */
	.detail-history {
		background: #ffffff;
		border-radius: 14px;
		border: 1px solid #eef2f6;
		overflow: hidden;
		display: flex;
		flex-direction: column;
	}

	.history-header {
		padding: 10px 14px;
		background: #fafbfc;
		border-bottom: 1px solid #eef2f6;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.history-header-left {
		display: flex;
		align-items: center;
		gap: 8px;
	}

	.history-title {
		font-weight: 600;
		font-size: 15px;
		color: #0f172a;
	}

	.history-count {
		font-size: 12px;
		color: #64748b;
		background: #eef2f6;
		padding: 0 10px;
		border-radius: 20px;
	}

	.filter-sort {
		padding: 2px 12px;
		border: 1px solid #e2e8f0;
		border-radius: 16px;
		font-size: 12px;
		color: #475569;
		background: #ffffff;
		cursor: pointer;
	}

	.filter-bar {
		background: #f8fafc;
		padding: 8px 14px;
		border-radius: 12px;
		margin: 8px 14px 4px;
		display: flex;
		flex-wrap: wrap;
		gap: 6px 12px;
		align-items: center;
	}

	.filter-group {
		display: flex;
		align-items: center;
		gap: 4px;
		flex-wrap: wrap;
	}

	.filter-label {
		font-size: 12px;
		color: #64748b;
	}

	.filter-date {
		padding: 2px 10px;
		border: 1px solid #e2e8f0;
		border-radius: 16px;
		font-size: 12px;
		background: #ffffff;
		cursor: pointer;
		min-width: 70px;
		color: #475569;
	}

	.filter-clear {
		font-size: 12px;
		color: #4f7df3;
		cursor: pointer;
	}

	.price-scroll {
		flex: 1;
		max-height: 340px;
		overflow-y: auto;
		padding: 4px 14px 8px;
	}

	.price-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 8px 28px 0px 0px;
		border-bottom: 1px solid #f1f5f9;
	}

	.price-item:last-child {
		border-bottom: none;
	}

	.price-info {
		display: flex;
		align-items: center;
		gap: 12px;
		flex: 1;
		flex-wrap: wrap;
	}

	.price-date {
		font-size: 13px;
		color: #64748b;
		min-width: 70px;
	}

	.price-spec {
		font-size: 12px;
		color: #94a3b8;
		background: #f1f5f9;
		padding: 1px 8px;
		border-radius: 12px;
	}

	.price-value {
		font-size: 15px;
		font-weight: 700;
		color: #0f172a;
		min-width: 60px;
	}

	.price-change {
		font-size: 12px;
		font-weight: 600;
		min-width: 45px;
	}

	.price-change.up {
		color: #059669;
	}

	.price-change.down {
		color: #dc2626;
	}

	.price-change.flat {
		color: #94a3b8;
	}

	.price-delete {
		font-size: 16px;
		color: #cbd5e1;
		cursor: pointer;
		padding: 4px;
		transition: color 0.2s;
	}

	.price-delete:hover {
		color: #ef4444;
	}

	.price-empty {
		text-align: center;
		padding: 30px 16px;
		color: #94a3b8;
		display: flex;
		flex-direction: column;
		gap: 4px;
	}

	.empty-icon {
		font-size: 32px;
	}

	.empty-text {
		font-size: 14px;
	}

	.empty-hint {
		font-size: 12px;
		color: #cbd5e1;
	}

	/* ===== 弹窗 ===== */
	.modal-mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(15, 23, 42, 0.5);
		backdrop-filter: blur(4px);
		display: flex;
		justify-content: center;
		align-items: center;
		z-index: 1000;
	}

	.modal-box {
		background: #ffffff;
		border-radius: 24px;
		width: 90%;
		max-width: 440px;
		max-height: 85vh;
		display: flex;
		flex-direction: column;
		box-shadow: 0 30px 80px rgba(0, 0, 0, 0.15);
		animation: scaleIn 0.3s ease;
	}

	@keyframes scaleIn {
		from {
			opacity: 0;
			transform: scale(0.95);
		}

		to {
			opacity: 1;
			transform: scale(1);
		}
	}

	.modal-header {
		padding: 14px 18px;
		border-bottom: 1px solid #f1f5f9;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.modal-title {
		font-size: 17px;
		font-weight: 700;
		color: #0f172a;
	}

	.modal-close {
		font-size: 24px;
		color: #94a3b8;
		cursor: pointer;
		line-height: 1;
	}

	.modal-body {
		padding: 16px 18px;
		overflow-y: auto;
		flex: 1;
	}

	.modal-footer {
		padding: 12px 18px;
		border-top: 1px solid #f1f5f9;
		display: flex;
		justify-content: flex-end;
		gap: 10px;
	}

	.form-group {
		margin-bottom: 12px;
		margin-right: 24px;
	}

	.form-label {
		font-size: 13px;
		font-weight: 500;
		color: #475569;
		display: block;
		margin-bottom: 3px;
	}

	.required {
		color: #ef4444;
	}

	.form-input {
		width: 100%;
		padding: 8px 14px;
		border: 1px solid #e2e8f0;
		border-radius: 10px;
		font-size: 14px;
		background: #f8fafc;
		outline: none;
		transition: border 0.2s;
	}

	.form-input:focus {
		border-color: #4f7df3;
		background: #ffffff;
	}

	.form-row {
		display: grid;
		grid-template-columns: 1fr 1fr;
		gap: 10px;
	}

	.date-picker {
		color: #64748b;
	}

	.price-product-badge {
		background: #eef2ff;
		padding: 4px 14px;
		border-radius: 20px;
		text-align: center;
		font-weight: 600;
		font-size: 20px;
		color: #4f7df3;
		margin-bottom: 14px;
	}

	.btn-cancel {
		background: #f1f5f9;
		border: none;
		padding: 6px 18px;
		border-radius: 20px;
		font-size: 14px;
		font-weight: 500;
		cursor: pointer;
	}

	.btn-cancel:hover {
		background: #e2e8f0;
	}

	.btn-save {
		background: #4f7df3;
		color: #ffffff;
		border: none;
		padding: 6px 24px;
		border-radius: 20px;
		font-size: 14px;
		font-weight: 600;
		cursor: pointer;
	}

	.btn-save:hover {
		background: #3b66d0;
	}

	/* 规格输入框状态 */
	.spec-input-wrapper {
		position: relative;
		display: flex;
		align-items: center;
	}

	.spec-input {
		flex: 1;
		padding-right: 36px;
	}

	.spec-input-wrapper.success .spec-input {
		border-color: #10b981;
		background: #f0fdf4;
	}

	.spec-input-wrapper.partial .spec-input {
		border-color: #f59e0b;
		background: #fffbeb;
	}

	.spec-input-wrapper.error .spec-input {
		border-color: #ef4444;
		background: #fef2f2;
	}

	.parse-status-icon {
		position: absolute;
		right: 12px;
		font-size: 18px;
	}

	.parse-result {
		margin-top: 6px;
		padding: 8px 12px;
		border-radius: 8px;
		font-size: 13px;
	}

	.parse-result.success {
		background: #d1fae5;
		color: #065f46;
	}

	.parse-result.partial {
		background: #fef3c7;
		color: #92400e;
	}

	.parse-result.error {
		background: #fee2e2;
		color: #991b1b;
	}

	.parse-result-main {
		display: flex;
		align-items: center;
		gap: 6px;
	}

	.parse-result-detail {
		margin-top: 4px;
		font-size: 12px;
		opacity: 0.8;
	}

	.parse-icon {
		font-size: 16px;
	}

	/* 价格操作按钮布局 */
	.price-actions {
		display: flex;
		align-items: center;
		gap: 8px;
		flex-shrink: 0;
	}

	.price-edit {
		cursor: pointer;
		padding: 4px;
		opacity: 0.6;
		transition: opacity 0.2s;
	}

	.price-edit:hover {
		opacity: 1;
	}

	.price-delete {
		font-size: 16px;
		color: #cbd5e1;
		cursor: pointer;
		padding: 4px;
		transition: color 0.2s;
	}

	.price-delete:hover {
		color: #ef4444;
	}

	/* ===== 响应式 ===== */
	@media (max-width: 700px) {
		.app-container {
			padding: 8px 10px 16px;
		}

		.page-header {
			flex-direction: row;
			padding: 8px 12px;
		}

		.app-title {
			max-width: 120px;
			font-size: 15px;
		}

		.spec-detail-grid {
			grid-template-columns: repeat(2, 1fr);
			gap: 6px;
		}

		.spec-switcher-item {
			padding: 4px 10px;
			min-width: 60px;
		}

		.spec-switcher-name {
			font-size: 12px;
		}

		.spec-switcher-price {
			font-size: 12px;
		}

		.filter-bar {
			flex-direction: column;
			align-items: stretch;
			gap: 4px;
		}

		.filter-group {
			flex-wrap: wrap;
		}

		.price-info {
			gap: 6px;
		}

		.price-date {
			min-width: 55px;
			font-size: 12px;
		}

		.price-value {
			font-size: 14px;
			min-width: 50px;
		}

		.price-change {
			font-size: 11px;
			min-width: 35px;
		}

		.compare-item {
			flex-direction: column;
			align-items: flex-start;
			gap: 4px;
		}

		.form-row {
			grid-template-columns: 1fr;
		}
	}

	@media (max-width: 420px) {
		.spec-detail-grid {
			grid-template-columns: repeat(2, 1fr);
		}

		.price-info {
			gap: 4px;
		}

		.price-date {
			min-width: 45px;
			font-size: 11px;
		}

		.price-value {
			font-size: 13px;
			min-width: 45px;
		}
	}
</style>