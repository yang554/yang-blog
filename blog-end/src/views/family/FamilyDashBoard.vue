<template>
  <div class="clan-dashboard">
    <!-- 宗祠匾额 + 统计概览 -->
    <div class="dashboard-header">
      <div class="clan-plaque">
        <h1>📊 宗 譜 大 屏</h1>
        <p>—— 一览无余 · 世代永传 ——</p>
      </div>

      <div class="header-tools">
        <div class="family-selector">
          <el-select v-model="currentFamilyId" placeholder="选择族譜" @change="handleFamilyChange" class="family-select">
            <el-option v-for="item in familyList" :key="item.id" :label="item.name" :value="item.id">
              <span class="family-option">
                {{ item.name + '-' + item.displayName }}
                <span class="family-meta">({{ item.total_members }}人 · {{ item.max_generation }}代)</span>
              </span>
            </el-option>
          </el-select>
        </div>

        <button class="btn-refresh" @click="refreshData">
          <i class="el-icon-refresh"></i> 更 新
        </button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-group">
      <div class="stat-card" v-for="stat in overallStats" :key="stat.label">
        <div class="stat-icon" :style="{ background: stat.bgColor }">
          <i :class="stat.icon"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-value">{{ stat.value }}</div>
          <div class="stat-unit">{{ stat.unit }}</div>
        </div>
      </div>
    </div>

    <!-- 第一行：人口结构与世代分布 -->
    <div class="charts-row">
      <div class="chart-card">
        <div class="card-header">
          <span class="card-title">👥 性 別 比 例</span>
        </div>
        <div class="chart-content" ref="genderChart"></div>
      </div>

      <div class="chart-card">
        <div class="card-header">
          <span class="card-title">📊 世 代 分 布</span>
        </div>
        <div class="chart-content" ref="generationChart"></div>
      </div>
    </div>

    <!-- 第二行：年龄结构与姓氏排行 -->
    <div class="charts-row">
      <div class="chart-card">
        <div class="card-header">
          <span class="card-title">🎂 年 龄 结 构</span>
          <div class="card-actions">
          </div>
        </div>
        <div class="chart-content" ref="ageChart"></div>
      </div>

      <div class="chart-card">
        <div class="card-header">
          <span class="card-title">📝 姓 氏 排 行</span>
          <div class="card-actions">
            <el-switch v-model="showSurnameDetail" active-text="详情" inactive-text="词云" @change="updateSurnameChart">
            </el-switch>
          </div>
        </div>
        <div class="chart-content" ref="surnameChart"></div>
      </div>
    </div>

    <!-- 第三行：婚姻状况与家族树概览 -->
    <div class="charts-row">
      <div class="chart-card">
        <div class="card-header">
          <span class="card-title">💑 婚 姻 状 况</span>
        </div>
        <div class="chart-content" ref="marriageChart"></div>
      </div>

      <div class="chart-card">
        <div class="card-header">
          <span class="card-title">🌳 家 族 树 概 揽</span>
          <div class="card-actions">
            <button class="btn-view" @click="viewFamilyTree">
              查看完整族譜 <i class="el-icon-arrow-right"></i>
            </button>
          </div>
        </div>
        <div class="chart-content" ref="treeChart"></div>
      </div>
    </div>

    <!-- 第四行：人口增长趋势与寿命分析 -->
    <div class="charts-row">
      <div class="chart-card">
        <div class="card-header">
          <span class="card-title">📈 人 口 增 长 趋 势</span>
        </div>
        <div class="chart-content" ref="trendChart"></div>
      </div>

      <div class="chart-card">
        <div class="card-header">
          <span class="card-title">⏳ 寿 命 分 析</span>
          <div class="card-actions">
            <el-radio-group v-model="lifespanChartType" size="small" @change="changeLifespanChart">
              <el-radio-button label="average">寿命分析</el-radio-button>
              <el-radio-button label="top10">最长寿TOP10</el-radio-button>
            </el-radio-group>
          </div>
        </div>
        <div class="chart-content" ref="lifespanChart"></div>
      </div>
    </div>

    <!-- 第五行：地域分布 -->
    <div class="charts-row full-width">
      <div class="chart-card full-width">
        <div class="card-header">
          <span class="card-title">📍 族 人 地 域 分 布</span>
        </div>
        <div class="family-tree-container">
          <div class="tree-content" ref="locationChart"></div>
          <!-- <div class="important-nodes">
            <div class="node-list-title">✨ 重 要 節 點</div>
            <div class="node-list">
              <div v-for="node in importantNodes" :key="node.id" class="node-item" @click="gotoMemberDetail(node)">
                <div class="node-avatar">
                  <img :src="node.avatar" :alt="node.name">
                </div>
                <div class="node-info">
                  <div class="node-name">{{ node.name }}</div>
                  <div class="node-meta">第{{ node.generation }}代 · {{ node.age }}歲</div>
                </div>
                <span class="node-tag" :class="node.type">{{ node.role }}</span>
              </div>
            </div>
          </div> -->
        </div>
      </div>
    </div>



    <!-- 族谱对比选择器 -->
    <!-- <div class="compare-section" v-if="familyList.length > 1">
      <div class="compare-title">
        <span class="title-icon">⚖️</span>
        <span>族 譜 对 比</span>
      </div>
      <el-select v-model="compareFamilyId" placeholder="选择对比族譜" clearable @change="handleCompareChange">
        <el-option v-for="item in familyList.filter(f => f.id !== currentFamilyId)" :key="item.id" :label="item.name"
          :value="item.id">
        </el-option>
      </el-select>
    </div> -->

    <!-- 对比分析面板 -->
    <div v-if="compareFamilyId" class="compare-panel">
      <div class="panel-header">
        <h3>⚖️ 对 比 分 析</h3>
        <button class="close-btn" @click="clearCompare">✕</button>
      </div>
      <div class="compare-grid">
        <div class="compare-item" v-for="item in compareItems" :key="item.label">
          <div class="compare-label">{{ item.label }}</div>
          <div class="compare-values">
            <div class="value current">
              <span class="number">{{ item.current }}</span>
              <span class="unit">{{ item.unit }}</span>
            </div>
            <div class="vs">VS</div>
            <div class="value compare">
              <span class="number">{{ item.compare }}</span>
              <span class="unit">{{ item.unit }}</span>
            </div>
          </div>
          <div class="compare-diff" :class="item.diff > 0 ? 'up' : 'down'">
            <i :class="item.diff > 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
            {{ Math.abs(item.diff) }}{{ item.unit }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
_getFamilyTree
import * as echarts from 'echarts'
import 'echarts-wordcloud'
import { _getFamilyTree, _getUserCountByLoveAndSex, _getSourceAll } from "@/api/api.js";
import chinaJson from "@/assets/json/china-province.json";

export default {
  name: "ClanDashboard",
  data() {
    return {
      isLoading: false, // 添加加载状态
      // 族谱列表
      familyList: [],
      currentFamilyId: '',
      compareFamilyId: null,

      // 统计数据
      overallStats: [],

      // 图表实例
      charts: {},

      // UI状态
      showSurnameDetail: false,

      // 重要节点
      importantNodes: [],

      // 对比数据
      compareItems: [],

      //世代分布数据
      generation_x: [],
      generation_y_female: [],
      generation_y_male: [],

      //年龄分布数据
      ageData: [],

      //姓氏分布数据
      surnameData: [],

      //婚姻状况数据
      married: 0, //已婚
      unmarried: 0, //未婚
      widowed: 0, //丧偶
      remarried: 0, //再婚

      //人口地域分布数据
      locationData: [],

      //人口增长趋势数据
      trendData: {
        years: [],           // 年份数组
        birthData: [],       // 出生人数
        deathData: [],       // 死亡人数
        cumulativeData: [],  // 累计人口
      },

      //寿命分析数据
      lifespanData: {
        average: 0,           // 平均寿命
        maleAvg: 0,           // 男性平均寿命
        femaleAvg: 0,         // 女性平均寿命
        maxAge: 0,            // 最高寿命
        maxAgeName: '',       // 最高寿命者姓名
        minAge: 0,            // 最低寿命
        minAgeName: '',       // 最低寿命者姓名
        ageDistribution: [],  // 寿命分布（按年龄段）
        topLongevous: [],     // 最长寿TOP10
      },
      // 寿命分析图表类型
      lifespanChartType: 'average', // average, distribution, top10

    }
  },

  mounted() {
    this.getFamilyTree();
    this.initCharts()

    window.addEventListener('resize', this.handleResize)
  },

  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    Object.values(this.charts).forEach(chart => chart && chart.dispose())
  },

  methods: {
    //获取族谱大屏数据
    async getFamilyTree() {
      const res = await _getFamilyTree();
      if (res.data.length > 0) {
        this.familyList = res.data;
        this.currentFamilyId = res.data[0].id

        await this.loadDashboardData(this.currentFamilyId);
      }

    },

    //统计族中成员年龄分布
    getAgeData(members) {
      this.ageData = [];
      // 年龄分段配置
      const ageRanges = [
        { label: '0-10岁', min: 0, max: 10 },
        { label: '11-20岁', min: 11, max: 20 },
        { label: '21-30岁', min: 21, max: 30 },
        { label: '31-40岁', min: 31, max: 40 },
        { label: '41-50岁', min: 41, max: 50 },
        { label: '51-60岁', min: 51, max: 60 },
        { label: '61-70岁', min: 61, max: 70 },
        { label: '71-80岁', min: 71, max: 80 },
        { label: '80岁以上', min: 81, max: 200 }
      ]
      // 初始化数据结构
      const distribution = ageRanges.map(range => ({
        age_range: range.label,
        alive_count: 0,
        deceased_count: 0
      }))

      // 遍历每个成员
      members.forEach(member => {
        // 计算年龄
        const age = this.calculateAge(
          member.userBirthDay,
          member.isSurvival,
          member.userDieDay
        )

        // 跳过无效年龄
        if (isNaN(age) || age < 0) return

        // 找到所属年龄段
        const rangeIndex = ageRanges.findIndex(r => age >= r.min && age <= r.max)
        if (rangeIndex === -1) return

        // 根据存活状态累加
        if (member.isSurvival === '1') {
          distribution[rangeIndex].alive_count++
        } else {
          distribution[rangeIndex].deceased_count++
        }
      })

      this.ageData = distribution;
    },

    //统计族中姓氏
    getSurnameData(members) {
      this.surnameData = [];
      const count = {}

      // 统计
      members.forEach(m => {
        if (!m.userName) return
        const surname = m.userName[0]
        count[surname] = (count[surname] || 0) + 1
      })

      // 转数组并排序
      this.surnameData = Object.entries(count)
        .map(([name, value]) => ({ name, value }))
        .sort((a, b) => b.value - a.value)
    },

    //统计族中婚姻状态
    getMarriageData(members) {
      this.married = 0;
      this.unmarried = 0;
      this.remarried = 0;
      this.widowed = 0;

      const processedIds = new Set()

      members.forEach(member => {
        // 跳过已处理的配偶
        if (processedIds.has(member.userId)) return

        const spouseList = member.list || []
        if (spouseList.length === 0) {
          this.unmarried++
          return
        }

        // 检查是否有存活配偶
        const hasLivingSpouse = spouseList.some(s => s.isSurvival === '1')

        if (spouseList.length > 1) {
          this.remarried++
        } else if (hasLivingSpouse) {
          this.married++
        } else {
          this.widowed++
        }

        // 标记配偶
        spouseList.forEach(s => s.userId && processedIds.add(s.userId))
      })
    },

    // 统计族中世代分布数据
    getGenerationData(data) {
      this.generation_x = [];
      this.generation_y_male = [];
      this.generation_y_female = [];

      const groupedData = {};
      data.forEach(item => {
        if (!groupedData[item.id]) {
          groupedData[item.id] = { generation: item.id, male: 0, female: 0 };
        }
        if (item.name === '男') groupedData[item.id].male = item.value;
        if (item.name === '女') groupedData[item.id].female = item.value;
      });

      Object.keys(groupedData).sort((a, b) => a - b).forEach(key => {
        this.generation_x.push(`第${key}代`);
        this.generation_y_male.push(groupedData[key].male);
        this.generation_y_female.push(groupedData[key].female);
      });
    },

    //统计族中人员地域分布数据
    getLocationData(members) {
      this.locationData = [];

      // 使用 Map 来统计，避免重复计算
      const provinceStats = new Map();

      members.forEach(member => {
        let provinceName = null;

        // 优先使用地址编码
        if (member.userAddressOldCode && member.userAddressOldCode.length >= 2) {
          const provinceCode = member.userAddressOldCode.substring(0, 2);
          provinceName = this.getProvinceNameByCode(provinceCode);
        }

        // 如果没有编码，使用现居地编码
        if (!provinceName && member.userAddressNewCode && member.userAddressNewCode.length >= 2) {
          const provinceCode = member.userAddressNewCode.substring(0, 2);
          provinceName = this.getProvinceNameByCode(provinceCode);
        }

        // 如果都没有编码，回退到文本解析
        if (!provinceName) {
          // 优先使用户籍地
          if (member.userAddressOld) {
            provinceName = this.extractCityName(member.userAddressOld);
          }
          // 如果没有户籍地，使用现居地
          if (!provinceName && member.userAddressNew) {
            provinceName = this.extractCityName(member.userAddressNew);
          }
        }

        // 如果解析到省份名称，累加计数
        if (provinceName) {
          provinceStats.set(provinceName, (provinceStats.get(provinceName) || 0) + 1);
        } else {
          // 统计无法解析的地址
          console.log('无法解析地址:', member.userName, member.userAddressOld || member.userAddressNew);
        }
      });

      // 转换为ECharts需要的格式
      this.locationData = Array.from(provinceStats.entries()).map(([name, value]) => ({
        name: name,
        value: value
      }));

      // 按人数降序排序
      this.locationData.sort((a, b) => b.value - a.value);
    },

    // 提取城市名称
    extractCityName(address) {
      if (!address) return null;

      // 中国行政区划映射表（按优先级排序）
      const regionMap = [
        // 1. 自治区（完整名称优先）
        { pattern: '新疆维吾尔自治区', name: '新疆维吾尔自治区', type: '自治区' },
        { pattern: '广西壮族自治区', name: '广西壮族自治区', type: '自治区' },
        { pattern: '内蒙古自治区', name: '内蒙古自治区', type: '自治区' },
        { pattern: '西藏自治区', name: '西藏自治区', type: '自治区' },
        { pattern: '宁夏回族自治区', name: '宁夏回族自治区', type: '自治区' },

        // 2. 特别行政区
        { pattern: '香港特别行政区', name: '香港特别行政区', type: '特别行政区' },
        { pattern: '澳门特别行政区', name: '澳门特别行政区', type: '特别行政区' },
        { pattern: '香港', name: '香港特别行政区', type: '特别行政区' },
        { pattern: '澳门', name: '澳门特别行政区', type: '特别行政区' },

        // 3. 直辖市（完整名称）
        { pattern: '北京市', name: '北京市', type: '直辖市' },
        { pattern: '上海市', name: '上海市', type: '直辖市' },
        { pattern: '天津市', name: '天津市', type: '直辖市' },
        { pattern: '重庆市', name: '重庆市', type: '直辖市' },

        // 4. 自治区简称
        { pattern: '新疆', name: '新疆维吾尔自治区', type: '自治区' },
        { pattern: '广西', name: '广西壮族自治区', type: '自治区' },
        { pattern: '内蒙古', name: '内蒙古自治区', type: '自治区' },
        { pattern: '西藏', name: '西藏自治区', type: '自治区' },
        { pattern: '宁夏', name: '宁夏回族自治区', type: '自治区' },

        // 5. 直辖市简称
        { pattern: '北京', name: '北京市', type: '直辖市' },
        { pattern: '上海', name: '上海市', type: '直辖市' },
        { pattern: '天津', name: '天津市', type: '直辖市' },
        { pattern: '重庆', name: '重庆市', type: '直辖市' },

        // 6. 普通省份（包含"省"字的优先）
        { pattern: '黑龙江省', name: '黑龙江省', type: '省' },
        { pattern: '吉林省', name: '吉林省', type: '省' },
        { pattern: '辽宁省', name: '辽宁省', type: '省' },
        { pattern: '河北省', name: '河北省', type: '省' },
        { pattern: '山西省', name: '山西省', type: '省' },
        { pattern: '江苏省', name: '江苏省', type: '省' },
        { pattern: '浙江省', name: '浙江省', type: '省' },
        { pattern: '安徽省', name: '安徽省', type: '省' },
        { pattern: '福建省', name: '福建省', type: '省' },
        { pattern: '江西省', name: '江西省', type: '省' },
        { pattern: '山东省', name: '山东省', type: '省' },
        { pattern: '河南省', name: '河南省', type: '省' },
        { pattern: '湖北省', name: '湖北省', type: '省' },
        { pattern: '湖南省', name: '湖南省', type: '省' },
        { pattern: '广东省', name: '广东省', type: '省' },
        { pattern: '海南省', name: '海南省', type: '省' },
        { pattern: '四川省', name: '四川省', type: '省' },
        { pattern: '贵州省', name: '贵州省', type: '省' },
        { pattern: '云南省', name: '云南省', type: '省' },
        { pattern: '陕西省', name: '陕西省', type: '省' },
        { pattern: '甘肃省', name: '甘肃省', type: '省' },
        { pattern: '青海省', name: '青海省', type: '省' },
        { pattern: '台湾省', name: '台湾省', type: '省' },

        // 7. 普通省份简称
        { pattern: '黑龙江', name: '黑龙江省', type: '省' },
        { pattern: '吉林', name: '吉林省', type: '省' },
        { pattern: '辽宁', name: '辽宁省', type: '省' },
        { pattern: '河北', name: '河北省', type: '省' },
        { pattern: '山西', name: '山西省', type: '省' },
        { pattern: '江苏', name: '江苏省', type: '省' },
        { pattern: '浙江', name: '浙江省', type: '省' },
        { pattern: '安徽', name: '安徽省', type: '省' },
        { pattern: '福建', name: '福建省', type: '省' },
        { pattern: '江西', name: '江西省', type: '省' },
        { pattern: '山东', name: '山东省', type: '省' },
        { pattern: '河南', name: '河南省', type: '省' },
        { pattern: '湖北', name: '湖北省', type: '省' },
        { pattern: '湖南', name: '湖南省', type: '省' },
        { pattern: '广东', name: '广东省', type: '省' },
        { pattern: '海南', name: '海南省', type: '省' },
        { pattern: '四川', name: '四川省', type: '省' },
        { pattern: '贵州', name: '贵州省', type: '省' },
        { pattern: '云南', name: '云南省', type: '省' },
        { pattern: '陕西', name: '陕西省', type: '省' },
        { pattern: '甘肃', name: '甘肃省', type: '省' },
        { pattern: '青海', name: '青海省', type: '省' },
        { pattern: '台湾', name: '台湾省', type: '省' },
      ];

      // 按模式长度降序排序（长的优先匹配）
      const sortedMap = [...regionMap].sort((a, b) =>
        b.pattern.length - a.pattern.length
      );

      // 遍历匹配
      for (const region of sortedMap) {
        if (address.includes(region.pattern)) {
          console.log(`匹配到: ${region.pattern} -> ${region.name}`);
          return region.name;
        }
      }

      // 备用方案：正则匹配常见格式
      const patterns = [
        /([^省市]+[省市])/,           // 匹配 XX省 或 XX市
        /([^自治区]+自治区)/,         // 匹配 XX自治区
        /([^特别行政区]+特别行政区)/,  // 匹配 XX特别行政区
        /([^地区]+地区)/              // 匹配 XX地区
      ];

      for (const pattern of patterns) {
        const match = address.match(pattern);
        if (match) {
          return match[1];
        }
      }

      return null;
    },

    // 根据省级编码获取省份名称
    getProvinceNameByCode(code) {
      const provinceMap = {
        '11': '北京市', '12': '天津市', '13': '河北省', '14': '山西省', '15': '内蒙古自治区',
        '21': '辽宁省', '22': '吉林省', '23': '黑龙江省',
        '31': '上海市', '32': '江苏省', '33': '浙江省', '34': '安徽省', '35': '福建省',
        '36': '江西省', '37': '山东省',
        '41': '河南省', '42': '湖北省', '43': '湖南省', '44': '广东省', '45': '广西壮族自治区',
        '46': '海南省',
        '50': '重庆市', '51': '四川省', '52': '贵州省', '53': '云南省', '54': '西藏自治区',
        '61': '陕西省', '62': '甘肃省', '63': '青海省', '64': '宁夏回族自治区', '65': '新疆维吾尔自治区',
        '71': '台湾省', '81': '香港特别行政区', '82': '澳门特别行政区'
      };
      return provinceMap[code] || null;
    },

    //统计族中人口数据
    getTrendData(members) {
      const birthStats = {};
      const deathStats = {};

      // 找出所有成员中的最小和最大出生年份
      let minYear = Infinity;
      let maxYear = -Infinity;

      members.forEach(member => {
        if (member.userBirthDay) {
          const birthYear = new Date(member.userBirthDay).getFullYear();
          if (!isNaN(birthYear)) {
            minYear = Math.min(minYear, birthYear);
            maxYear = Math.max(maxYear, birthYear);

            // 统计出生人数
            birthStats[birthYear] = (birthStats[birthYear] || 0) + 1;
          }
        }

        // 处理死亡年份（已故成员）
        if (member.isSurvival === '0' && member.userDieDay) {
          const deathYear = new Date(member.userDieDay).getFullYear();
          if (!isNaN(deathYear)) {
            deathStats[deathYear] = (deathStats[deathYear] || 0) + 1;
          }
        }
      });

      // 如果没有数据，返回空数组
      if (minYear === Infinity || maxYear === -Infinity) {
        this.trendData = {
          years: [],
          birthData: [],
          deathData: []
        };
        return;
      }

      // 根据数据量自动决定时间间隔
      const yearSpan = maxYear - minYear;
      let interval = 1; // 默认1年

      if (yearSpan > 100) {
        interval = 10; // 超过100年，按10年统计
      } else if (yearSpan > 50) {
        interval = 5;  // 50-100年，按5年统计
      } else if (yearSpan > 20) {
        interval = 2;  // 20-50年，按2年统计
      }

      // 生成年份数组（从最小年份到最大年份，按间隔取）
      const trendYears = [];
      const birthData = [];
      const deathData = [];

      // 计算起始年份（向下取整到间隔的倍数）
      const startYear = Math.floor(minYear / interval) * interval;

      for (let year = startYear; year <= maxYear; year += interval) {
        trendYears.push(year);

        // 计算该年份区间内的出生人数
        let birthCount = 0;
        for (let y = year; y < year + interval && y <= maxYear; y++) {
          birthCount += birthStats[y] || 0;
        }
        birthData.push(birthCount);

        // 计算该年份区间内的死亡人数
        let deathCount = 0;
        for (let y = year; y < year + interval && y <= maxYear; y++) {
          deathCount += deathStats[y] || 0;
        }
        deathData.push(deathCount);
      }

      this.trendData = {
        years: trendYears,
        birthData: birthData,
        deathData: deathData,
        // 保存原始范围信息（可选）
        range: {
          min: minYear,
          max: maxYear,
          total: members.length
        }
      };
    },

    // 统计寿命分析数据
    getLifespanData(members) {
      // 只统计已故成员
      const deceasedMembers = members.filter(m => m.isSurvival === '0' && m.userBirthDay && m.userDieDay);

      if (deceasedMembers.length === 0) {
        // 如果没有已故成员，显示默认数据
        this.lifespanData = {
          average: 0,
          maleAvg: 0,
          femaleAvg: 0,
          maxAge: 0,
          maxAgeName: '无数据',
          minAge: 0,
          minAgeName: '无数据',
          ageDistribution: [],
          topLongevous: []
        };
        return;
      }

      // 计算每个已故成员的寿命
      const ages = [];
      const maleAges = [];
      const femaleAges = [];
      const ageDetails = [];

      deceasedMembers.forEach(member => {
        const age = this.calculateAge(member.userBirthDay, '0', member.userDieDay);
        if (isNaN(age) || age < 0) return;

        ages.push(age);
        ageDetails.push({
          name: member.userName,
          age: age,
          sex: member.userSex,
          birthDay: member.userBirthDay,
          dieDay: member.userDieDay
        });

        if (member.userSex === '男') {
          maleAges.push(age);
        } else if (member.userSex === '女') {
          femaleAges.push(age);
        }
      });

      // 按年龄排序
      ageDetails.sort((a, b) => b.age - a.age);

      // 计算平均寿命
      const average = ages.length > 0 ? (ages.reduce((a, b) => a + b, 0) / ages.length).toFixed(1) : 0;
      const maleAvg = maleAges.length > 0 ? (maleAges.reduce((a, b) => a + b, 0) / maleAges.length).toFixed(1) : 0;
      const femaleAvg = femaleAges.length > 0 ? (femaleAges.reduce((a, b) => a + b, 0) / femaleAges.length).toFixed(1) : 0;

      // 最高/最低寿命
      const maxAge = ages.length > 0 ? Math.max(...ages) : 0;
      const minAge = ages.length > 0 ? Math.min(...ages) : 0;
      const maxAgeMember = ageDetails.find(d => d.age === maxAge);
      const minAgeMember = ageDetails.find(d => d.age === minAge);

      // 最长寿TOP10
      const topLongevous = ageDetails.slice(0, 10).map(d => ({
        name: d.name,
        age: d.age,
        sex: d.sex
      }));

      this.lifespanData = {
        average: parseFloat(average),
        maleAvg: parseFloat(maleAvg),
        femaleAvg: parseFloat(femaleAvg),
        maxAge: maxAge,
        maxAgeName: maxAgeMember ? maxAgeMember.name : '未知',
        minAge: minAge,
        minAgeName: minAgeMember ? minAgeMember.name : '未知',
        topLongevous: topLongevous,
        totalDeceased: ages.length,
        rawData: ageDetails
      };
    },

    // 初始化图表
    initCharts() {
      //注册中国地图
      echarts.registerMap('china', chinaJson)

      const chartRefs = [
        'genderChart', 'generationChart', 'ageChart', 'surnameChart',
        'marriageChart', 'locationChart', 'treeChart', 'trendChart', 'lifespanChart'
      ]
      chartRefs.forEach(ref => {
        if (this.$refs[ref]) {
          this.charts[ref] = echarts.init(this.$refs[ref])
        }
      })
    },

    // 窗口大小变化
    handleResize() {
      Object.values(this.charts).forEach(chart => chart && chart.resize())
    },

    // 加载数据
    async loadDashboardData(familyId) {
      this.isLoading = true;

      try {
        const [loveSexRes, sourceRes] = await Promise.all([
          _getUserCountByLoveAndSex(familyId),
          _getSourceAll(familyId)
        ]);

        // 处理世代分布数据
        if (loveSexRes.data.data) {
          this.getGenerationData(loveSexRes.data.data);
        }

        // 处理成员数据
        if (sourceRes.data) {
          this.getAgeData(sourceRes.data);
          this.getSurnameData(sourceRes.data);
          this.getMarriageData(sourceRes.data);
          this.getLocationData(sourceRes.data);
          this.getTrendData(sourceRes.data);
          this.getLifespanData(sourceRes.data);
        }

        // 确保图表已初始化*
        if (Object.keys(this.charts).length === 0) {
          this.initCharts();
        }

        // 更新所有图表
        this.$nextTick(() => {
          this.updateAllCharts();
        });

      } catch (error) {
        console.error('加载数据失败', error);
        this.$message.error('数据加载失败');
      } finally {
        this.isLoading = false;
      }

      // 重要节点数据
      this.importantNodes = [
        {
          id: 1,
          name: '李元祖',
          generation: 1,
          age: 98,
          avatar: 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' viewBox=\'0 0 100 100\'%3E%3Ccircle cx=\'50\' cy=\'50\' r=\'50\' fill=\'%23b59569\'/%3E%3Ctext x=\'50\' y=\'70\' font-size=\'40\' text-anchor=\'middle\' fill=\'%23fff\'%3E祖%3C/text%3E%3C/svg%3E',
          role: '始祖',
          type: 'success'
        },
        {
          id: 2,
          name: '李德明',
          generation: 3,
          age: 85,
          avatar: 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' viewBox=\'0 0 100 100\'%3E%3Ccircle cx=\'50\' cy=\'50\' r=\'50\' fill=\'%239e7a56\'/%3E%3Ctext x=\'50\' y=\'70\' font-size=\'40\' text-anchor=\'middle\' fill=\'%23fff\'%3E族%3C/text%3E%3C/svg%3E',
          role: '族長',
          type: 'primary'
        }
      ]


    },

    // 统计卡片
    updateOverallStats() {
      const currentFamily = this.familyList.find(f => f.id === this.currentFamilyId) || {};
      this.overallStats = [
        {
          icon: 'el-icon-user',
          label: '总人数',
          value: currentFamily.total_members || 0,
          unit: '人',
          bgColor: '#b59569'
        },
        {
          icon: 'el-icon-s-operation',
          label: '总代数',
          value: currentFamily.max_generation || 0,
          unit: '代',
          bgColor: '#9e7a56'
        },
        {
          icon: 'el-icon-sunny',
          label: '在世',
          value: currentFamily.alive_count || 0,
          unit: '人',
          bgColor: '#b59569'
        },
        {
          icon: 'el-icon-moon',
          label: '亡故',
          value: currentFamily.deceased_count || 0,
          unit: '人',
          bgColor: '#9e7a56'
        }
      ];
    },

    // 性别比例图
    updateGenderChart() {
      const option = {
        tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
        legend: { orient: 'horizontal', bottom: 10, textStyle: { color: '#5d3e28' } },
        series: [{
          type: 'pie',
          radius: ['45%', '70%'],
          center: ['50%', '45%'],
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          data: [
            { value: this.familyList.find(f => f.id === this.currentFamilyId)?.male_count || 0, name: '男性', itemStyle: { color: '#6a76cb' } },
            { value: this.familyList.find(f => f.id === this.currentFamilyId)?.female_count || 0, name: '女性', itemStyle: { color: '#ec4ec3' } }
          ]
        }]
      }
      this.charts.genderChart?.setOption(option)
    },

    // 世代分布图
    updateGenerationChart() {
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'value',
          axisLabel: { color: '#5d3e28' },
          axisLine: { lineStyle: { color: '#b48c5c' } }
        },
        yAxis: {
          type: 'category',
          data: this.generation_x,
          axisLabel: { color: '#5d3e28' }
        },
        series: [{
          name: '男性',
          type: 'bar',
          data: this.generation_y_male,
          itemStyle: {
            color: '#6a76cb'
          },
          label: { show: true, position: 'right', color: '#5d3e28' }
        },
        {
          name: '女性',
          type: 'bar',
          data: this.generation_y_female,
          itemStyle: {
            color: '#ec4ec3'
          },
          label: { show: true, position: 'right', color: '#5d3e28' }
        }]
      }
      this.charts.generationChart?.setOption(option)
    },

    // 年龄结构图
    updateAgeChart() {
      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: this.ageData.map(item => item.age_range),
          axisLabel: { color: '#5d3e28' },
          axisLine: { lineStyle: { color: '#b48c5c' } }
        },
        yAxis: {
          type: 'value',
          axisLabel: { color: '#5d3e28' }
        },
        series: [{
          name: '在世',
          type: 'bar',
          data: this.ageData.map(item => item.alive_count),
          itemStyle: {
            color: '#67C23A',
            borderRadius: [4, 4, 0, 0]
          },
          label: { show: true, position: 'top', color: '#5d3e28' }
        },
        {
          name: '亡故',
          type: 'bar',
          data: this.ageData.map(item => item.deceased_count),
          itemStyle: {
            color: '#909399',
            borderRadius: [4, 4, 0, 0]
          },
          label: { show: true, position: 'top', color: '#5d3e28' }
        }]
      }
      this.charts.ageChart?.setOption(option)
    },

    // 姓氏排行图
    updateSurnameChart() {
      if (!this.charts.surnameChart) return

      this.charts.surnameChart.clear();

      if (this.showSurnameDetail) {
        const option = {
          tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
          grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
          xAxis: {
            type: 'value',
            axisLabel: { color: '#5d3e28' },
            axisLine: { lineStyle: { color: '#b48c5c' } }
          },
          yAxis: {
            type: 'category',
            data: this.surnameData.map(d => d.name),
            axisLabel: { color: '#5d3e28' }
          },
          series: [{
            name: '人数',
            type: 'bar',
            data: this.surnameData.map(d => d.value),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#b59569' },
                { offset: 1, color: '#9e7a56' }
              ])
            },
            label: { show: true, position: 'right', color: '#5d3e28' }
          }]
        }
        this.charts.surnameChart?.setOption(option)
      } else {
        const option = {
          series: [{
            type: 'wordCloud',
            shape: 'circle',
            sizeRange: [20, 50],
            rotationRange: [-30, 30],
            gridSize: 18,
            textStyle: { color: () => `#${Math.floor(Math.random() * 16777215).toString(16)}` },
            data: this.surnameData.map(d => ({ name: d.name, value: d.value }))
          }]
        }
        this.charts.surnameChart?.setOption(option)
      }
    },

    // 婚姻状况图
    updateMarriageChart() {
      const option = {
        tooltip: { trigger: 'item', formatter: '{b}: {c}人 ({d}%)' },
        legend: { orient: 'vertical', right: 10, top: 'center', textStyle: { color: '#5d3e28' } },
        series: [{
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['35%', '50%'],
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          data: [
            { name: '已婚', value: this.married, itemStyle: { color: '#67C23A' } },
            { name: '未婚', value: this.unmarried, itemStyle: { color: '#909399' } },
            { name: '丧偶', value: this.widowed, itemStyle: { color: '#E6A23C' } },
            { name: '再婚', value: this.remarried, itemStyle: { color: '#409EFF' } }
          ]
        }]
      }
      this.charts.marriageChart?.setOption(option)
    },

    // 地域分布图
    updateLocationChart() {
      const values = this.locationData.map(item => item.value);
      const maxValue = Math.max(...values, 1);

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: (params) => {
            return `${params.name}: ${params.value || 0}人`;
          }
        },
        visualMap: {
          min: 0, max: maxValue,
          left: 'left', top: 'bottom',
          calculable: true,
          inRange: { color: ['#b3d9ff', '#7ec8e0', '#4da6cf', '#2c7ab1', '#6b4f8c', '#aa4e6c', '#d45d5d']},
          textStyle: {
            color: '#5d3e28'
          }
        },
        series: [{
          name: '族人地域分布',
          type: 'map',
          map: 'china',
          zoom: 1.2,
          roam: true,
          itemStyle: {
            areaColor: '#f5e6d3',
            borderColor: '#b48c5c',
            borderWidth: 1
          },
          emphasis: {
            areaColor: '#b59569',
            label: { show: true, color: '#fff' }
          },
          data: this.locationData
        }]
      }
      this.charts.locationChart?.setOption(option)
    },

    // 家族树概览
    updateTreeChart() {
      const option = {
        tooltip: { trigger: 'item', formatter: '{b}' },
        series: [{
          type: 'tree',
          data: [{
            name: '始祖',
            children: [
              {
                name: '二世', children: [
                  { name: '三世', children: [{ name: '四世' }, { name: '四世' }] },
                  { name: '三世', children: [{ name: '四世' }] }
                ]
              },
              { name: '二世', children: [{ name: '三世', children: [{ name: '四世' }] }] }
            ]
          }],
          orient: 'vertical',
          initialTreeDepth: 3,
          lineStyle: { color: '#b48c5c', width: 1.5 },
          label: { position: 'left', color: '#5d3e28', fontSize: 12 }
        }]
      }
      this.charts.treeChart?.setOption(option)
    },

    // 人口增长趋势
    updateTrendChart() {
      const option = {
        tooltip: { trigger: 'axis' },
        legend: { data: ['出生人数', '死亡人数'], bottom: 1, textStyle: { color: '#5d3e28' } },
        grid: { left: '3%', right: '4%', bottom: '15%', top: '3%' },
        xAxis: {
          type: 'category',
          data: this.trendData.years,
          axisLabel: { color: '#5d3e28' },
          axisLine: { lineStyle: { color: '#b48c5c' } }
        },
        yAxis: {
          type: 'value',
          axisLabel: { color: '#5d3e28' }
        },
        series: [
          {
            name: '出生人数',
            type: 'line',
            smooth: true,
            data: this.trendData.birthData,
            lineStyle: { color: '#6a76cb', width: 3 },
            areaStyle: { color: 'rgba(106, 118, 203, 0.1)' }
          },
          {
            name: '死亡人数',
            type: 'line',
            smooth: true,
            data: this.trendData.deathData,
            lineStyle: { color: '#b3523e', width: 3 },
            areaStyle: { color: 'rgba(179, 82, 62, 0.1)' }
          }
        ]
      }
      this.charts.trendChart?.setOption(option)
    },

    // 寿命分析
    updateLifespanChart() {
      const option = {
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: {
          type: 'category',
          data: ['平均寿命', '男性', '女性', '最长寿', '最短命'],
          axisLabel: { color: '#5d3e28' },
          axisLine: { lineStyle: { color: '#b48c5c' } }
        },
        yAxis: {
          type: 'value',
          name: '岁',
          axisLabel: { color: '#5d3e28' }
        },
        series: [{
          type: 'bar',
          data: [this.lifespanData.average, this.lifespanData.maleAvg, this.lifespanData.femaleAvg, this.lifespanData.maxAge, this.lifespanData.minAge],
          itemStyle: {
            color: function (params) {
              const colors = ['#b59569', '#6a76cb', '#ec4ec3', '#b3523e', '#909399']
              return colors[params.dataIndex]
            },
            borderRadius: [4, 4, 0, 0]
          },
          label: { show: true, position: 'top', color: '#5d3e28', formatter: '{c}岁' }
        }]
      }
      this.charts.lifespanChart?.setOption(option)
    },

    // 最长寿TOP10
    updateTopLongevous() {
      const topData = this.lifespanData.topLongevous;
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: function (params) {
            const data = topData[params[0].dataIndex];
            return `${data.name}<br/>年龄: ${data.age}岁 (${data.sex})`;
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'value',
          axisLabel: { color: '#5d3e28' },
          axisLine: { lineStyle: { color: '#b48c5c' } }
        },
        yAxis: {
          type: 'category',
          data: topData.map(d => d.name),
          axisLabel: { color: '#5d3e28' }
        },
        series: [{
          name: '寿命',
          type: 'bar',
          data: topData.map(d => d.age),
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
              { offset: 0, color: '#b3523e' },
              { offset: 1, color: '#b59569' }
            ]),
            borderRadius: [0, 4, 4, 0]
          },
          label: {
            show: true,
            position: 'right',
            color: '#5d3e28',
            formatter: '{c}岁'
          }
        }]
      };

      this.charts.lifespanChart?.setOption(option);
    },

    // 切换族谱
    handleFamilyChange() {
      this.loadDashboardData(this.currentFamilyId)
    },

    // 刷新数据
    refreshData() {
      this.loadDashboardData(this.currentFamilyId)
      this.$message.success('数据已更新')
    },

    //切换寿命分析与前十排行
    changeLifespanChart() {
      if (!this.charts.lifespanChart) return;
      switch (this.lifespanChartType) {
        case 'average':
          this.updateLifespanChart();
          break;
        case 'top10':
          this.updateTopLongevous();
          break;
        default:
          this.updateLifespanChart();
      }
    },

    // 切换对比族谱
    handleCompareChange(val) {
      if (val) {
        this.compareItems = [
          { label: '總人數', current: 446, compare: 389, unit: '人', diff: 57 },
          { label: '總代數', current: 8, compare: 7, unit: '代', diff: 1 },
          { label: '男性比例', current: 55, compare: 52, unit: '%', diff: 3 },
          { label: '平均壽命', current: 72, compare: 68, unit: '歲', diff: 4 }
        ]
      } else {
        this.clearCompare()
      }
    },

    // 清除对比
    clearCompare() {
      this.compareFamilyId = null
      this.compareItems = []
    },

    // 查看完整族谱
    viewFamilyTree() {
      this.$router.push({
        path: `/home/family/FamilySource/${this.currentFamilyId}`
      })
    },

    // 跳转到成员详情
    gotoMemberDetail(member) {
      this.$message.info(`查看成員：${member.name}`)
    },

    // 年龄计算函数
    calculateAge(birthday, isSurvival, dieDay) {
      const birthDate = new Date(birthday).getTime()
      const today = new Date().getTime()

      if (isSurvival === '1') {
        // 存活成员：当前日期 - 出生日期
        return Math.floor((today - birthDate) / (1000 * 60 * 60 * 24 * 365))
      } else {
        // 已故成员：死亡日期 - 出生日期
        const dieDate = new Date(dieDay).getTime()
        return Math.floor((dieDate - birthDate) / (1000 * 60 * 60 * 24 * 365))
      }
    },

    // 更新所有图表
    updateAllCharts() {
      this.updateOverallStats()
      this.updateGenderChart()
      this.updateGenerationChart()
      this.updateAgeChart()
      this.updateSurnameChart()
      this.updateMarriageChart()
      this.updateLocationChart()
      this.updateTreeChart()
      this.updateTrendChart()
      this.updateLifespanChart()
    },

  }
}
</script>

<style scoped>
.clan-dashboard {
  background: #e8dccc;
  min-height: 100vh;
  padding: 30px 20px 40px;
  font-family: '华文楷体', 'KaiTi', '宋体', serif;
  box-sizing: border-box;
}

/* 头部区域 */
.dashboard-header {
  max-width: 1400px;
  margin: 0 auto 30px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 20px;
}

.clan-plaque {
  text-align: left;
}

.clan-plaque h1 {
  font-size: 2.8rem;
  color: #5d3e28;
  margin: 0;
  letter-spacing: 6px;
  font-weight: normal;
  border-bottom: 3px solid #b48c5c;
  padding-bottom: 10px;
  text-shadow: 2px 2px 0 #d4b48c;
}

.clan-plaque p {
  font-size: 1.1rem;
  color: #7b5f42;
  margin: 8px 0 0;
  font-style: italic;
}

/* 头部工具栏 */
.header-tools {
  display: flex;
  gap: 15px;
  align-items: center;
}

.family-select {
  width: 300px;
}

::v-deep .family-select .el-input__inner {
  background: #fff9ed;
  border: 2px solid #c9a97a;
  border-radius: 40px;
  color: #5d3e28;
  font-family: inherit;
  height: 44px;
}

.family-option {
  display: flex;
  align-items: center;
  gap: 5px;
}

.family-meta {
  color: #8b6f4b;
  font-size: 0.85rem;
}

.btn-refresh {
  background: #9e7a56;
  border: none;
  padding: 10px 20px;
  border-radius: 40px;
  font-size: 1rem;
  font-weight: bold;
  color: #fdf3e0;
  cursor: pointer;
  box-shadow: 0 4px 0 #5d3f26, inset 0 1px 6px #f7dbb6;
  border: 1px solid #c9a16e;
  font-family: inherit;
  display: flex;
  align-items: center;
  gap: 5px;
}

.btn-refresh:hover {
  background: #b28d63;
  transform: translateY(-2px);
  box-shadow: 0 6px 0 #5d3f26;
}

/* 统计卡片组 */
.stats-group {
  max-width: 1400px;
  margin: 0 auto 30px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  background: #f2e5d2;
  border-radius: 30px 30px 20px 20px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 8px 0 #a77c52, 0 0 0 2px #bb9a70, 0 0 0 4px #876e4a;
  border: 1px solid #e5c69b;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 0 #4d3722, inset 0 1px 5px #fff0d0;
}

.stat-icon i {
  font-size: 30px;
  color: white;
}

.stat-content {
  flex: 1;
  position: relative;
}

.stat-label {
  font-size: 1rem;
  color: #5d3e28;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 2rem;
  font-weight: bold;
  color: #382915;
  display: inline-block;
  margin-right: 5px;
}

.stat-unit {
  font-size: 1rem;
  color: #8b6f4b;
  display: inline-block;
}

/* 图表行 */
.charts-row {
  max-width: 1400px;
  margin: 0 auto 30px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.charts-row.full-width {
  grid-template-columns: 1fr;
}

/* 图表卡片 */
.chart-card {
  background: #f2e5d2;
  border-radius: 30px 30px 20px 20px;
  padding: 20px;
  box-shadow: 0 8px 0 #a77c52, 0 0 0 2px #bb9a70, 0 0 0 4px #876e4a;
  border: 1px solid #e5c69b;
}

.chart-card.full-width {
  grid-column: 1 / -1;
}

.card-header {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #b48c5c;
  flex-wrap: wrap;
}

.card-title {
  font-size: 1.4rem;
  font-weight: bold;
  color: #5d3e28;
}

.card-subtitle {
  font-size: 0.9rem;
  color: #8b6f4b;
  font-style: italic;
}

.card-actions {
  margin-left: auto;
  display: flex;
  gap: 10px;
}

::v-deep .card-actions .el-radio-button__inner {
  background: #ecdbba;
  border: 1px solid #b48b5c;
  color: #5d3e28;
  font-family: inherit;
  font-size: 0.85rem;
  padding: 4px 12px;
}

::v-deep .card-actions .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  background: #b59569;
  color: white;
  border-color: #5d3e28;
}

::v-deep .card-actions .el-switch__label {
  color: #5d3e28;
  font-size: 0.85rem;
}

::v-deep .card-actions .el-switch__core {
  border-color: #b48b5c;
  background: #ecdbba;
}

.btn-view {
  background: none;
  border: 1px solid #b48b5c;
  padding: 4px 12px;
  border-radius: 20px;
  color: #5d3e28;
  cursor: pointer;
  font-family: inherit;
  font-size: 0.9rem;
}

.btn-view:hover {
  background: #b59569;
  color: white;
}

.chart-content {
  width: 100%;
  height: 280px;
}

/* 家族树容器 */
.family-tree-container {
  display: flex;
  gap: 20px;
}

.tree-content {
  flex: 1;
  height: 400px;
}

.important-nodes {
  width: 300px;
  background: #ecdbba;
  border-radius: 20px;
  padding: 15px;
  border: 1px solid #b48b5c;
  box-shadow: inset 0 1px 8px #fff0d0, 0 4px 0 #a77c52;
}

.node-list-title {
  font-size: 1.1rem;
  font-weight: bold;
  color: #5d3e28;
  margin-bottom: 15px;
  padding-bottom: 8px;
  border-bottom: 1px solid #b48c5c;
}

.node-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.node-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  background: #f2e5d2;
  border-radius: 40px;
  cursor: pointer;
  transition: 0.2s;
  border: 1px solid #b48b5c;
}

.node-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 0 #a77c52;
}

.node-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #b59569;
}

.node-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.node-info {
  flex: 1;
}

.node-name {
  font-weight: bold;
  color: #5d3e28;
  font-size: 1rem;
}

.node-meta {
  font-size: 0.8rem;
  color: #8b6f4b;
}

.node-tag {
  padding: 2px 8px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: bold;
  background: #b59569;
  color: white;
  border: 1px solid #5d3e28;
}

.node-tag.success {
  background: #10b981;
}

.node-tag.primary {
  background: #6a76cb;
}

.node-tag.warning {
  background: #b3523e;
}

/* 对比选择器 */
.compare-section {
  max-width: 1400px;
  margin: 40px auto 20px;
  text-align: center;
}

.compare-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 15px;
  color: #5d3e28;
  font-size: 1.2rem;
  font-weight: bold;
}

.compare-title .title-icon {
  font-size: 1.5rem;
}

::v-deep .compare-section .el-select {
  width: 300px;
}

/* 对比面板 */
.compare-panel {
  max-width: 1400px;
  margin: 20px auto 0;
  background: #f2e5d2;
  border-radius: 30px 30px 20px 20px;
  padding: 20px;
  box-shadow: 0 8px 0 #a77c52, 0 0 0 2px #bb9a70, 0 0 0 4px #876e4a;
  border: 1px solid #e5c69b;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #b48c5c;
}

.panel-header h3 {
  margin: 0;
  color: #5d3e28;
  font-size: 1.3rem;
  display: flex;
  align-items: center;
  gap: 5px;
}

.panel-header .close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #b3523e;
}

.compare-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.compare-item {
  background: #ecdbba;
  border-radius: 20px;
  padding: 15px;
  border: 1px solid #b48b5c;
}

.compare-label {
  font-size: 1rem;
  color: #5d3e28;
  font-weight: bold;
  margin-bottom: 10px;
  text-align: center;
}

.compare-values {
  display: flex;
  align-items: center;
  justify-content: space-around;
  margin-bottom: 10px;
}

.value {
  text-align: center;
}

.value .number {
  font-size: 1.8rem;
  font-weight: bold;
  color: #5d3e28;
}

.value .unit {
  font-size: 0.9rem;
  color: #8b6f4b;
  margin-left: 2px;
}

.vs {
  color: #b3523e;
  font-weight: bold;
  font-size: 1.2rem;
}

.compare-diff {
  text-align: center;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.compare-diff.up {
  color: #10b981;
}

.compare-diff.down {
  color: #b3523e;
}

/* 响应式 */
@media (max-width: 1200px) {
  .stats-group {
    grid-template-columns: repeat(2, 1fr);
  }

  .charts-row {
    grid-template-columns: 1fr;
  }

  .family-tree-container {
    flex-direction: column;
  }

  .important-nodes {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .clan-plaque h1 {
    font-size: 2rem;
  }

  .stats-group {
    grid-template-columns: 1fr;
  }

  .header-tools {
    width: 100%;
  }

  .family-select {
    width: 100%;
  }
}
</style>