<template>
  <div id="app" class="food-safety">
    <!-- 监控仪表板 -->
    <div class="monitoring-dashboard">
      <div class="dashboard-container">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="6">
            <div class="metric-card">
              <div class="metric-icon inspection">
                <i class="el-icon-s-check"></i>
              </div>
              <div class="metric-content">
                <div class="metric-value">{{ todayInspections }}</div>
                <div class="metric-label">今日检测批次</div>
                <div class="metric-trend">
                  <span class="trend-up">↑ {{ inspectionTrend }}%</span>
                  <span>较昨日</span>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="6">
            <div class="metric-card">
              <div class="metric-icon qualified">
                <i class="el-icon-success"></i>
              </div>
              <div class="metric-content">
                <div class="metric-value">{{ qualifiedRate }}%</div>
                <div class="metric-label">合格率</div>
                <div class="metric-trend">
                  <span :class="qualifiedTrend >= 0 ? 'trend-up' : 'trend-down'">
                    {{ qualifiedTrend >= 0 ? '↑' : '↓' }} {{ Math.abs(qualifiedTrend) }}%
                  </span>
                  <span>较上周</span>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="6">
            <div class="metric-card">
              <div class="metric-icon violation">
                <i class="el-icon-warning-outline"></i>
              </div>
              <div class="metric-content">
                <div class="metric-value">{{ violationCount }}</div>
                <div class="metric-label">违规事件</div>
                <div class="metric-trend">
                  <span :class="violationTrend >= 0 ? 'trend-down' : 'trend-up'">
                    {{ violationTrend >= 0 ? '↑' : '↓' }} {{ Math.abs(violationTrend) }}%
                  </span>
                  <span>较上周</span>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :xs="24" :sm="6">
            <div class="metric-card">
              <div class="metric-icon recall">
                <i class="el-icon-remove-outline"></i>
              </div>
              <div class="metric-content">
                <div class="metric-value">{{ recallCount }}</div>
                <div class="metric-label">召回批次</div>
                <div class="metric-trend">
                  <span :class="recallTrend >= 0 ? 'trend-down' : 'trend-up'">
                    {{ recallTrend >= 0 ? '↑' : '↓' }} {{ Math.abs(recallTrend) }}%
                  </span>
                  <span>较上月</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>

    <!-- 主内容区 -->
    <main class="safety-main">
      <div class="main-container">
        <!-- 左侧边栏 -->
        <aside class="safety-sidebar">
          <!-- 检测分类 -->
          <div class="detection-category">
            <h3 class="sidebar-title">
              <i class="el-icon-menu"></i> 检测分类
            </h3>
            <div class="category-list">
              <div v-for="category in detectionCategories" :key="category.id" class="category-item"
                :class="{ active: activeCategory === category.id }" @click="selectCategory(category)">
                <div class="category-icon">
                  <i :class="category.icon"></i>
                </div>
                <div class="category-content">
                  <div class="category-name">{{ category.name }}</div>
                  <div class="category-count">{{ category.count }}项</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 快速检测 -->
          <div class="quick-detection">
            <h3 class="sidebar-title">
              <i class="el-icon-alarm-clock"></i> 快速检测
            </h3>
            <div class="detection-list">
              <div v-for="item in quickDetectionItems" :key="item.id" class="detection-item"
                @click="startQuickDetection(item)">
                <div class="detection-icon">
                  <i :class="item.icon"></i>
                </div>
                <div class="detection-content">
                  <div class="detection-name">{{ item.name }}</div>
                  <div class="detection-time">{{ item.time }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 安全标准 -->
          <div class="safety-standards">
            <h3 class="sidebar-title">
              <i class="el-icon-document"></i> 安全标准
            </h3>
            <div class="standards-list">
              <div v-for="standard in safetyStandards" :key="standard.id" class="standard-item"
                @click="viewStandard(standard)">
                <div class="standard-code">{{ standard.code }}</div>
                <div class="standard-name">{{ standard.name }}</div>
                <div class="standard-date">{{ standard.date }}</div>
              </div>
            </div>
            <div class="view-all">
              <el-button type="text" @click="viewAllStandards">
                查看全部标准 <i class="el-icon-arrow-right"></i>
              </el-button>
            </div>
          </div>
        </aside>

        <!-- 主要内容 -->
        <div class="safety-content">
          <!-- 风险地图 -->
          <div class="risk-map-section">
            <div class="section-header">
              <h3 class="section-title">
                <i class="el-icon-map-location"></i> 食品安全风险地图
              </h3>
              <div class="map-controls">
                <el-select v-model="mapRegion" placeholder="选择区域" size="small">
                  <el-option label="全国" value="china"></el-option>
                  <el-option label="华北" value="north"></el-option>
                  <el-option label="华东" value="east"></el-option>
                  <el-option label="华南" value="south"></el-option>
                  <el-option label="华中" value="central"></el-option>
                  <el-option label="西南" value="southwest"></el-option>
                  <el-option label="西北" value="northwest"></el-option>
                </el-select>
                <el-select v-model="mapRiskType" placeholder="风险类型" size="small">
                  <el-option label="微生物污染" value="microbial"></el-option>
                  <el-option label="农药残留" value="pesticide"></el-option>
                  <el-option label="重金属超标" value="heavy-metal"></el-option>
                  <el-option label="非法添加" value="illegal-additive"></el-option>
                  <el-option label="其他风险" value="other"></el-option>
                </el-select>
              </div>
            </div>
            <div class="map-container">
              <div id="safetyMap" style="height: 300px;"></div>
            </div>
            <div class="map-legend">
              <div class="legend-item">
                <div class="legend-color low"></div>
                <span>低风险</span>
              </div>
              <div class="legend-item">
                <div class="legend-color medium"></div>
                <span>中风险</span>
              </div>
              <div class="legend-item">
                <div class="legend-color high"></div>
                <span>高风险</span>
              </div>
            </div>
          </div>

          <!-- 违规通报 -->
          <div class="violation-section">
            <div class="section-header">
              <h3 class="section-title">
                <i class="el-icon-warning"></i> 最新违规通报
              </h3>
              <el-button type="text" size="small" @click="viewAllViolations">
                查看全部 <i class="el-icon-arrow-right"></i>
              </el-button>
            </div>
            <div class="violation-list">
              <el-table :data="violationData" style="width: 100%" max-height="300">
                <el-table-column prop="product" label="问题产品" width="180">
                  <template slot-scope="scope">
                    <div class="product-info">
                      <div class="product-name">{{ scope.row.product }}</div>
                      <div class="product-brand">{{ scope.row.brand }}</div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="enterprise" label="生产企业" width="150"></el-table-column>
                <el-table-column prop="issue" label="问题描述" width="200">
                  <template slot-scope="scope">
                    <el-tooltip :content="scope.row.issue" placement="top">
                      <span class="issue-text">{{ scope.row.issue }}</span>
                    </el-tooltip>
                  </template>
                </el-table-column>
                <el-table-column prop="riskLevel" label="风险等级" width="100">
                  <template slot-scope="scope">
                    <el-tag size="small" :type="getRiskLevelType(scope.row.riskLevel)">
                      {{ scope.row.riskLevel }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="date" label="通报日期" width="120"></el-table-column>
                <el-table-column label="操作" width="100">
                  <template slot-scope="scope">
                    <el-button type="text" size="small" @click="viewViolationDetail(scope.row)">
                      详情
                    </el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>

          <!-- 检测新闻 -->
          <div class="news-section">
            <div class="section-header">
              <h3 class="section-title">
                <i class="el-icon-news"></i> 食品安全动态
              </h3>
              <el-radio-group v-model="newsType" size="small">
                <el-radio-button label="all">全部</el-radio-button>
                <el-radio-button label="policy">政策法规</el-radio-button>
                <el-radio-button label="inspection">抽检信息</el-radio-button>
                <el-radio-button label="research">科研进展</el-radio-button>
              </el-radio-group>
            </div>
            <div class="news-list">
              <div v-for="news in filteredNews" :key="news.id" class="news-item" @click="viewNewsDetail(news)">
                <div class="news-image">
                  <img :src="news.image" :alt="news.title" />
                  <div class="news-tag">{{ news.tag }}</div>
                </div>
                <div class="news-content">
                  <h4 class="news-title">{{ news.title }}</h4>
                  <p class="news-summary">{{ news.summary }}</p>
                  <div class="news-meta">
                    <span class="meta-source">{{ news.source }}</span>
                    <span class="meta-date">{{ news.date }}</span>
                    <span class="meta-views">
                      <i class="el-icon-view"></i> {{ news.views }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧边栏 -->
        <aside class="safety-tools">
          <!-- 食品追溯查询 -->
          <div class="traceability-section">
            <h3 class="tools-title">
              <i class="el-icon-search"></i> 食品追溯查询
            </h3>
            <div class="traceability-tool">
              <el-input v-model="traceCode" placeholder="请输入追溯码" size="medium" style="margin-bottom: 10px;">
                <template slot="prepend">
                  <i class="el-icon-s-grid"></i>
                </template>
                <el-button slot="append" icon="el-icon-search" @click="queryTrace"></el-button>
              </el-input>
              <div class="traceability-tips">
                <p>支持以下类型追溯码：</p>
                <ul>
                  <li>• 商品条形码 (13位)</li>
                  <li>• 生产批号 (企业自定)</li>
                  <li>• 监管码 (20位)</li>
                </ul>
              </div>
            </div>
            <div v-if="traceResult" class="traceability-result">
              <div class="result-header">
                <h4>追溯结果</h4>
                <el-tag size="small" :type="traceResult.safetyStatus === '合格' ? 'success' : 'danger'">
                  {{ traceResult.safetyStatus }}
                </el-tag>
              </div>
              <div class="result-content">
                <div class="result-item">
                  <span class="item-label">产品名称:</span>
                  <span class="item-value">{{ traceResult.productName }}</span>
                </div>
                <div class="result-item">
                  <span class="item-label">生产企业:</span>
                  <span class="item-value">{{ traceResult.manufacturer }}</span>
                </div>
                <div class="result-item">
                  <span class="item-label">生产日期:</span>
                  <span class="item-value">{{ traceResult.productionDate }}</span>
                </div>
                <div class="result-item">
                  <span class="item-label">检测结果:</span>
                  <span class="item-value">{{ traceResult.inspectionResult }}</span>
                </div>
                <div class="result-item">
                  <span class="item-label">流通记录:</span>
                  <span class="item-value">{{ traceResult.circulation }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 检测工具 -->
          <div class="detection-tools">
            <h3 class="tools-title">
              <i class="el-icon-s-tools"></i> 检测工具
            </h3>
            <div class="tools-list">
              <div v-for="tool in detectionTools" :key="tool.id" class="tool-item" @click="openTool(tool)">
                <div class="tool-icon">
                  <i :class="tool.icon"></i>
                </div>
                <div class="tool-content">
                  <div class="tool-name">{{ tool.name }}</div>
                  <div class="tool-desc">{{ tool.description }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 预警信息 -->
          <div class="warning-section">
            <h3 class="tools-title">
              <i class="el-icon-bell"></i> 实时预警
            </h3>
            <div class="warning-list">
              <div v-for="warning in realTimeWarnings" :key="warning.id" class="warning-item" :class="warning.level">
                <div class="warning-icon">
                  <i :class="warning.icon"></i>
                </div>
                <div class="warning-content">
                  <div class="warning-title">{{ warning.title }}</div>
                  <div class="warning-desc">{{ warning.description }}</div>
                  <div class="warning-time">{{ warning.time }}</div>
                </div>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </main>

    <!-- 风险预警对话框 -->
    <el-dialog title="食品安全风险预警" :visible.sync="warningDialogVisible" width="600px">
      <div class="warning-dialog">
        <div class="warning-summary">
          <div class="summary-item">
            <div class="summary-label">高风险预警</div>
            <div class="summary-value danger">{{ highRiskCount }}</div>
          </div>
          <div class="summary-item">
            <div class="summary-label">中风险预警</div>
            <div class="summary-value warning">{{ mediumRiskCount }}</div>
          </div>
          <div class="summary-item">
            <div class="summary-label">低风险预警</div>
            <div class="summary-value info">{{ lowRiskCount }}</div>
          </div>
        </div>
        <div class="warning-details">
          <h4>预警详情</h4>
          <el-table :data="warningDetails" style="width: 100%" max-height="300">
            <el-table-column prop="type" label="预警类型" width="120">
              <template slot-scope="scope">
                <el-tag size="small"
                  :type="scope.row.level === 'high' ? 'danger' : scope.row.level === 'medium' ? 'warning' : 'info'">
                  {{ scope.row.type }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="预警描述"></el-table-column>
            <el-table-column prop="time" label="发布时间" width="140"></el-table-column>
            <el-table-column label="操作" width="80">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="viewWarningDetail(scope.row)">
                  处理
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="warningDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="exportWarnings">导出报告</el-button>
      </span>
    </el-dialog>

    <!-- 检测详情对话框 -->
    <el-dialog :title="detectionDetail.title" :visible.sync="detectionDialogVisible" width="800px">
      <div class="detection-detail">
        <div class="detail-header">
          <div class="detail-meta">
            <div class="meta-item">
              <span class="meta-label">检测编号:</span>
              <span class="meta-value">{{ detectionDetail.code }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">检测日期:</span>
              <span class="meta-value">{{ detectionDetail.date }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">检测状态:</span>
              <el-tag size="small" :type="detectionDetail.status === '合格' ? 'success' : 'danger'">
                {{ detectionDetail.status }}
              </el-tag>
            </div>
          </div>
        </div>
        <div class="detail-body">
          <el-tabs v-model="detailActiveTab">
            <el-tab-pane label="检测项目" name="items">
              <div class="detection-items">
                <el-table :data="detectionDetail.items" style="width: 100%">
                  <el-table-column prop="name" label="检测项目" width="180"></el-table-column>
                  <el-table-column prop="result" label="检测结果" width="120"></el-table-column>
                  <el-table-column prop="standard" label="标准限值" width="120"></el-table-column>
                  <el-table-column prop="unit" label="单位" width="80"></el-table-column>
                  <el-table-column label="状态" width="100">
                    <template slot-scope="scope">
                      <el-tag size="small" :type="scope.row.status === '合格' ? 'success' : 'danger'">
                        {{ scope.row.status }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="风险提示" width="200">
                    <template slot-scope="scope">
                      <span :class="scope.row.status === '合格' ? 'text-success' : 'text-danger'">
                        {{ scope.row.riskTip }}
                      </span>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </el-tab-pane>
            <el-tab-pane label="样品信息" name="sample">
              <div class="sample-info">
                <div class="info-item">
                  <span class="info-label">样品名称:</span>
                  <span class="info-value">{{ detectionDetail.sampleName }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">生产企业:</span>
                  <span class="info-value">{{ detectionDetail.manufacturer }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">生产日期:</span>
                  <span class="info-value">{{ detectionDetail.productionDate }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">采样地点:</span>
                  <span class="info-value">{{ detectionDetail.samplingLocation }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">检测机构:</span>
                  <span class="info-value">{{ detectionDetail.inspectionAgency }}</span>
                </div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="检测报告" name="report">
              <div class="detection-report">
                <div class="report-summary">
                  <h4>检测结论</h4>
                  <p>{{ detectionDetail.conclusion }}</p>
                </div>
                <div class="report-actions">
                  <el-button type="primary" icon="el-icon-download">
                    下载完整报告
                  </el-button>
                  <el-button type="warning" icon="el-icon-printer">
                    打印报告
                  </el-button>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="detectionDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="shareDetection">分享报告</el-button>
      </span>
    </el-dialog>

    <!-- 底部信息 -->
    <footer class="safety-footer">
      <div class="footer-container">
        <div class="footer-content">
          <div class="footer-section">
            <h4>食品安全监控系统</h4>
            <p>国家市场监督管理总局食品安全监督管理司</p>
            <p>全国食品安全投诉举报热线: 12315</p>
          </div>
          <div class="footer-section">
            <h4>相关链接</h4>
            <ul>
              <li><a href="#">国家食品安全标准平台</a></li>
              <li><a href="#">食品安全风险监测中心</a></li>
              <li><a href="#">食品追溯信息平台</a></li>
              <li><a href="#">食品安全法律法规库</a></li>
            </ul>
          </div>
          <div class="footer-section">
            <h4>技术支持</h4>
            <p>数据更新时间: {{ dataUpdateTime }}</p>
            <p>技术支持: 食品安全信息技术有限公司</p>
            <p>服务热线: 400-123-4567</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>© 2023 食品安全监控系统. 本系统数据仅供参考，具体以官方发布为准。</p>
        </div>
      </div>
    </footer>
  </div>
</template>
<script>
import * as echarts from 'echarts';

export default {
  name: 'FoodSafety',
  data() {
    return {
      // 系统状态
      systemStatus: 'normal',
      systemStatusText: '正常',
      lastUpdateTime: this.formatTime(new Date()),
      warningCount: 8,

      // 快速搜索
      quickSearch: '',
      searchSuggestions: [
        { value: '三文鱼' },
        { value: '乳制品' },
        { value: '蔬菜农药残留' },
        { value: '食品添加剂' },
        { value: '重金属检测' },
        { value: '食品微生物' },
        { value: '转基因食品' },
        { value: '食品追溯' }
      ],

      // 监控指标
      todayInspections: 1245,
      inspectionTrend: 12.5,
      qualifiedRate: 98.7,
      qualifiedTrend: 0.3,
      violationCount: 23,
      violationTrend: -5.2,
      recallCount: 8,
      recallTrend: 15.3,

      // 左侧分类
      activeCategory: 1,
      detectionCategories: [
        { id: 1, name: '微生物检测', icon: 'el-icon-microphone', count: 56 },
        { id: 2, name: '农药残留', icon: 'el-icon-coin', count: 42 },
        { id: 3, name: '重金属检测', icon: 'el-icon-heavy-rain', count: 38 },
        { id: 4, name: '食品添加剂', icon: 'el-icon-sugar', count: 67 },
        { id: 5, name: '非法添加物', icon: 'el-icon-warning', count: 29 },
        { id: 6, name: '转基因检测', icon: 'el-icon-c-scale-to-original', count: 18 },
        { id: 7, name: '营养成分', icon: 'el-icon-food', count: 35 },
        { id: 8, name: '过敏原检测', icon: 'el-icon-first-aid-kit', count: 22 }
      ],

      // 快速检测
      quickDetectionItems: [
        { id: 1, name: '农残快速检测', icon: 'el-icon-sunny', time: '15分钟' },
        { id: 2, name: '微生物快速检测', icon: 'el-icon-mic', time: '30分钟' },
        { id: 3, name: '重金属快速检测', icon: 'el-icon-heavy-rain', time: '20分钟' },
        { id: 4, name: '添加剂检测', icon: 'el-icon-sugar', time: '25分钟' }
      ],

      // 安全标准
      safetyStandards: [
        { id: 1, code: 'GB 2760-2014', name: '食品添加剂使用标准', date: '2014-12-24' },
        { id: 2, code: 'GB 2761-2017', name: '食品中真菌毒素限量', date: '2017-03-17' },
        { id: 3, code: 'GB 2762-2017', name: '食品中污染物限量', date: '2017-03-17' },
        { id: 4, code: 'GB 2763-2021', name: '食品中农药最大残留限量', date: '2021-03-18' }
      ],

      // 风险地图
      mapRegion: 'china',
      mapRiskType: 'microbial',

      // 违规通报
      violationData: [
        {
          id: 1,
          product: '花生油',
          brand: 'XX品牌',
          enterprise: 'XX粮油有限公司',
          issue: '黄曲霉毒素B1超标',
          riskLevel: '高风险',
          date: '2023-10-15'
        },
        {
          id: 2,
          product: '婴幼儿奶粉',
          brand: 'YY品牌',
          enterprise: 'YY乳业有限公司',
          issue: '阪崎肠杆菌检测阳性',
          riskLevel: '高风险',
          date: '2023-10-14'
        },
        {
          id: 3,
          product: '鲜猪肉',
          brand: 'ZZ养殖场',
          enterprise: 'ZZ肉类加工厂',
          issue: '莱克多巴胺残留超标',
          riskLevel: '中风险',
          date: '2023-10-13'
        },
        {
          id: 4,
          product: '茶叶',
          brand: 'AA茶厂',
          enterprise: 'AA茶叶有限公司',
          issue: '农药残留超标',
          riskLevel: '中风险',
          date: '2023-10-12'
        },
        {
          id: 5,
          product: '蜂蜜',
          brand: 'BB蜂业',
          enterprise: 'BB食品有限公司',
          issue: '蔗糖含量超标',
          riskLevel: '低风险',
          date: '2023-10-11'
        }
      ],

      // 检测新闻
      newsType: 'all',
      safetyNews: [
        {
          id: 1,
          title: '国家发布新版食品安全抽检实施细则',
          summary: '新版细则对食品抽检的抽样、检验、判定等环节提出了更高要求',
          tag: '政策法规',
          image: 'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400',
          source: '国家市场监督管理总局',
          date: '2023-10-15',
          views: 1560,
          type: 'policy'
        },
        {
          id: 2,
          title: '第三季度全国食品安全抽检合格率达98.7%',
          summary: '市场监管总局发布第三季度食品安全监督抽检情况分析',
          tag: '抽检信息',
          image: 'https://images.unsplash.com/photo-1484723091739-30a097e8f929?w=400',
          source: '食品安全监管司',
          date: '2023-10-14',
          views: 2340,
          type: 'inspection'
        },
        {
          id: 3,
          title: '新型食品安全快速检测技术研究取得突破',
          summary: '科研团队成功研发出30分钟检测多种农残的新技术',
          tag: '科研进展',
          image: 'https://images.unsplash.com/photo-1585435557343-3b092031d041?w=400',
          source: '食品安全研究所',
          date: '2023-10-13',
          views: 1890,
          type: 'research'
        }
      ],

      // 食品追溯
      traceCode: '',
      traceResult: null,

      // 检测工具
      detectionTools: [
        { id: 1, name: '农药残留计算器', icon: 'el-icon-cpu', description: '计算农药残留是否超标' },
        { id: 2, name: '食品添加剂查询', icon: 'el-icon-search', description: '查询添加剂使用标准' },
        { id: 3, name: '风险评估工具', icon: 'el-icon-pie-chart', description: '食品安全风险评估' },
        { id: 4, name: '检测报告生成', icon: 'el-icon-document', description: '自动生成检测报告' }
      ],

      // 实时预警
      realTimeWarnings: [
        {
          id: 1,
          title: '某品牌奶粉检出阪崎肠杆菌',
          description: '紧急召回相关批次产品',
          time: '10:30',
          level: 'high',
          icon: 'el-icon-warning'
        },
        {
          id: 2,
          title: '蔬菜农残超标预警',
          description: '部分地区蔬菜样本农残超标',
          time: '09:15',
          level: 'medium',
          icon: 'el-icon-warning-outline'
        },
        {
          id: 3,
          title: '冷链食品新冠病毒监测',
          description: '加强进口冷链食品检测',
          time: '昨天 16:20',
          level: 'low',
          icon: 'el-icon-info'
        }
      ],

      // 风险预警对话框
      warningDialogVisible: false,
      highRiskCount: 3,
      mediumRiskCount: 4,
      lowRiskCount: 1,
      warningDetails: [
        { id: 1, type: '微生物污染', description: '某品牌酸奶检出致病菌', level: 'high', time: '2023-10-15 10:30' },
        { id: 2, type: '农药残留', description: '某地蔬菜农残超标', level: 'medium', time: '2023-10-15 09:15' },
        { id: 3, type: '非法添加', description: '某保健品检出非法添加物', level: 'high', time: '2023-10-14 16:45' },
        { id: 4, type: '重金属超标', description: '某海产品重金属超标', level: 'medium', time: '2023-10-14 14:20' }
      ],

      // 检测详情对话框
      detectionDialogVisible: false,
      detailActiveTab: 'items',
      detectionDetail: {
        title: '食品检测报告',
        code: 'JC20231015001',
        date: '2023-10-15',
        status: '不合格',
        items: [
          { name: '黄曲霉毒素B1', result: '15.8', standard: '≤10.0', unit: 'μg/kg', status: '不合格', riskTip: '超标58%，存在致癌风险' },
          { name: '苯并[a]芘', result: '3.2', standard: '≤5.0', unit: 'μg/kg', status: '合格', riskTip: '符合标准要求' },
          { name: '酸价', result: '1.5', standard: '≤3.0', unit: 'mg/g', status: '合格', riskTip: '符合标准要求' },
          { name: '过氧化值', result: '0.12', standard: '≤0.25', unit: 'g/100g', status: '合格', riskTip: '符合标准要求' }
        ],
        sampleName: '花生油',
        manufacturer: 'XX粮油有限公司',
        productionDate: '2023-09-10',
        samplingLocation: 'XX超市',
        inspectionAgency: '国家食品质量安全监督检验中心',
        conclusion: '该批次花生油中黄曲霉毒素B1超标，不符合GB 2761-2017《食品中真菌毒素限量》标准要求，判定为不合格产品。'
      },

      // 底部信息
      dataUpdateTime: this.formatTime(new Date()),

      // 图表实例
      safetyMap: null
    };
  },

  computed: {
    // 过滤新闻
    filteredNews() {
      if (this.newsType === 'all') return this.safetyNews;
      return this.safetyNews.filter(news => news.type === this.newsType);
    }
  },

  mounted() {
    // 初始化地图
    this.$nextTick(() => {
      this.initSafetyMap();
    });

    // 模拟实时数据更新
    this.startRealTimeUpdates();

    // 模拟加载追溯结果（演示用）
    setTimeout(() => {
      this.loadDemoTraceResult();
    }, 1000);
  },

  methods: {
    // 格式化时间
    formatTime(date) {
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      const hour = date.getHours().toString().padStart(2, '0');
      const minute = date.getMinutes().toString().padStart(2, '0');
      return `${year}-${month}-${day} ${hour}:${minute}`;
    },

    // 返回首页
    goHome() {
      this.activeCategory = 1;
      this.mapRegion = 'china';
      this.mapRiskType = 'microbial';
    },

    // 搜索建议
    querySearch(queryString, cb) {
      const suggestions = this.searchSuggestions;
      const results = queryString
        ? suggestions.filter(item => item.value.toLowerCase().includes(queryString.toLowerCase()))
        : suggestions.slice(0, 5);
      cb(results);
    },

    // 处理搜索选择
    handleSearchSelect(item) {
      this.$message.info(`搜索: ${item.value}`);
      // 这里实际项目中应该调用搜索API
    },

    // 处理用户命令
    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$message.info('打开用户信息');
          break;
        case 'reports':
          this.$message.info('查看检测报告');
          break;
        case 'settings':
          this.$message.info('打开系统设置');
          break;
      }
    },

    // 选择分类
    selectCategory(category) {
      this.activeCategory = category.id;
      this.$message.info(`选择分类: ${category.name}`);
    },

    // 开始快速检测
    startQuickDetection(item) {
      this.$message.info(`开始${item.name}`);
      // 这里实际项目中应该打开快速检测界面
    },

    // 查看标准
    viewStandard(standard) {
      this.$message.info(`查看标准: ${standard.code} ${standard.name}`);
    },

    // 查看全部标准
    viewAllStandards() {
      this.$message.info('查看全部食品安全标准');
    },

    // 查看全部违规
    viewAllViolations() {
      this.$message.info('查看全部违规通报');
    },

    // 查看违规详情
    viewViolationDetail(row) {
      this.$message.info(`查看违规详情: ${row.product}`);
      // 这里可以打开详情对话框或跳转到详情页
    },

    // 查看新闻详情
    viewNewsDetail(news) {
      this.$message.info(`查看新闻详情: ${news.title}`);
    },

    // 获取风险等级类型
    getRiskLevelType(level) {
      switch (level) {
        case '高风险': return 'danger';
        case '中风险': return 'warning';
        case '低风险': return 'info';
        default: return '';
      }
    },

    // 初始化安全地图
    initSafetyMap() {
      const chartDom = document.getElementById('safetyMap');
      if (!chartDom) return;

      this.safetyMap = echarts.init(chartDom);

      // 模拟地图数据
      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'item',
          formatter: function (params) {
            return `${params.name}<br/>风险等级: ${params.value}<br/>风险点: ${params.data.riskCount || 0}个`;
          },
          backgroundColor: 'rgba(15, 23, 42, 0.9)',
          borderColor: '#0f172a',
          textStyle: { color: '#fff' }
        },
        visualMap: {
          min: 0,
          max: 100,
          left: 'left',
          top: 'bottom',
          text: ['高', '低'],
          calculable: true,
          inRange: {
            color: ['#10b981', '#f59e0b', '#ef4444']
          },
          textStyle: {
            color: '#475569'
          }
        },
        series: [{
          type: 'map',
          map: 'china',
          roam: false,
          zoom: 1.2,
          label: {
            show: true,
            color: '#0f172a',
            fontSize: 10
          },
          emphasis: {
            label: {
              color: '#fff',
              backgroundColor: '#0f172a',
              padding: [4, 6],
              borderRadius: 4
            },
            itemStyle: {
              areaColor: '#3b82f6'
            }
          },
          data: [
            { name: '北京', value: 25, riskCount: 3 },
            { name: '天津', value: 30, riskCount: 2 },
            { name: '河北', value: 45, riskCount: 5 },
            { name: '山西', value: 20, riskCount: 1 },
            { name: '内蒙古', value: 15, riskCount: 0 },
            { name: '辽宁', value: 35, riskCount: 4 },
            { name: '吉林', value: 20, riskCount: 2 },
            { name: '黑龙江', value: 25, riskCount: 3 },
            { name: '上海', value: 40, riskCount: 4 },
            { name: '江苏', value: 55, riskCount: 7 },
            { name: '浙江', value: 50, riskCount: 6 },
            { name: '安徽', value: 35, riskCount: 3 },
            { name: '福建', value: 30, riskCount: 2 },
            { name: '江西', value: 25, riskCount: 2 },
            { name: '山东', value: 60, riskCount: 8 },
            { name: '河南', value: 50, riskCount: 6 },
            { name: '湖北', value: 35, riskCount: 4 },
            { name: '湖南', value: 40, riskCount: 5 },
            { name: '广东', value: 65, riskCount: 9 },
            { name: '广西', value: 30, riskCount: 3 },
            { name: '海南', value: 20, riskCount: 1 },
            { name: '重庆', value: 25, riskCount: 2 },
            { name: '四川', value: 45, riskCount: 5 },
            { name: '贵州', value: 20, riskCount: 1 },
            { name: '云南', value: 25, riskCount: 2 },
            { name: '西藏', value: 10, riskCount: 0 },
            { name: '陕西', value: 30, riskCount: 3 },
            { name: '甘肃', value: 20, riskCount: 1 },
            { name: '青海', value: 15, riskCount: 0 },
            { name: '宁夏', value: 20, riskCount: 1 },
            { name: '新疆', value: 25, riskCount: 2 },
            { name: '台湾', value: 30, riskCount: 2 },
            { name: '香港', value: 40, riskCount: 3 },
            { name: '澳门', value: 25, riskCount: 1 }
          ]
        }]
      };

      this.safetyMap.setOption(option);

      // 响应窗口大小变化
      window.addEventListener('resize', () => {
        if (this.safetyMap) {
          this.safetyMap.resize();
        }
      });
    },

    // 查询追溯
    queryTrace() {
      if (!this.traceCode.trim()) {
        this.$message.warning('请输入追溯码');
        return;
      }

      // 模拟查询结果
      this.traceResult = {
        productName: 'XX牌花生油',
        manufacturer: 'XX粮油有限公司',
        productionDate: '2023-09-10',
        inspectionResult: '黄曲霉毒素B1超标（15.8μg/kg，标准≤10.0μg/kg）',
        safetyStatus: '不合格',
        circulation: '生产→仓储→分销→零售'
      };

      this.$message.success('追溯查询完成');
    },

    // 加载演示追溯结果
    loadDemoTraceResult() {
      this.traceCode = '6901234567890';
    },

    // 打开工具
    openTool(tool) {
      this.$message.info(`打开工具: ${tool.name}`);
    },

    // 显示风险预警
    showWarnings() {
      this.warningDialogVisible = true;
    },

    // 查看预警详情
    viewWarningDetail(warning) {
      this.$message.info(`处理预警: ${warning.description}`);
    },

    // 导出预警报告
    exportWarnings() {
      this.$message.success('预警报告导出成功');
    },

    // 分享检测报告
    shareDetection() {
      this.$message.success('检测报告已复制分享链接');
    },

    // 模拟实时数据更新
    startRealTimeUpdates() {
      setInterval(() => {
        // 更新时间
        this.lastUpdateTime = this.formatTime(new Date());
        this.dataUpdateTime = this.formatTime(new Date());

        // 模拟数据波动
        this.todayInspections += Math.floor(Math.random() * 10) - 3;
        this.todayInspections = Math.max(1000, this.todayInspections);

        // 更新警告计数
        const newWarningCount = Math.max(0, this.warningCount + Math.floor(Math.random() * 3) - 1);
        this.warningCount = newWarningCount;

        // 更新系统状态
        if (newWarningCount > 10) {
          this.systemStatus = 'warning';
          this.systemStatusText = '预警';
        } else if (newWarningCount > 5) {
          this.systemStatus = 'normal';
          this.systemStatusText = '正常';
        } else {
          this.systemStatus = 'good';
          this.systemStatusText = '良好';
        }

      }, 30000); // 30秒更新一次
    }
  },

  beforeDestroy() {
    // 清理图表实例
    if (this.safetyMap) {
      this.safetyMap.dispose();
    }
  }
};
</script>
<style scoped>
.food-safety {
  font-family: 'Helvetica Neue', Arial, sans-serif;
  color: #333;
  background-color: #f8fafc;
  min-height: 100vh;
}

/* 监控仪表板 */
.monitoring-dashboard {
  margin: 20px auto;
  padding: 0 20px;
}

.dashboard-container {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
  border: 1px solid #e2e8f0;
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  border-radius: 10px;
  background: #f8fafc;
  border-left: 4px solid #0f172a;
  transition: transform 0.3s;
}

.metric-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.metric-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.metric-icon.inspection {
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
}

.metric-icon.qualified {
  background: linear-gradient(135deg, #10b981, #34d399);
}

.metric-icon.violation {
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
}

.metric-icon.recall {
  background: linear-gradient(135deg, #ef4444, #f87171);
}

.metric-content {
  flex: 1;
}

.metric-value {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
  margin-bottom: 5px;
}

.metric-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
}

.metric-trend {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.trend-up {
  color: #10b981;
  font-weight: 500;
}

.trend-down {
  color: #ef4444;
  font-weight: 500;
}

/* 主内容区 */
.safety-main {
  margin: 20px auto;
  padding: 0 20px;
}

.main-container {
  display: flex;
  gap: 20px;
}

/* 左侧边栏 */
.safety-sidebar {
  width: 280px;
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

.detection-category,
.quick-detection,
.safety-standards {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
  margin-bottom: 20px;
  border: 1px solid #e2e8f0;
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

.detection-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detection-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e2e8f0;
}

.detection-item:hover {
  border-color: #cbd5e1;
  background-color: #f8fafc;
}

.detection-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f1f5f9;
  border-radius: 8px;
}

.detection-icon i {
  font-size: 18px;
  color: #3b82f6;
}

.detection-content {
  flex: 1;
}

.detection-name {
  font-weight: 500;
  color: #0f172a;
  margin-bottom: 4px;
}

.detection-time {
  font-size: 12px;
  color: #94a3b8;
}

.standards-list {
  margin-bottom: 15px;
}

.standard-item {
  padding: 12px 0;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  transition: background-color 0.3s;
}

.standard-item:hover {
  background-color: #f8fafc;
}

.standard-item:last-child {
  border-bottom: none;
}

.standard-code {
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 4px;
}

.standard-name {
  font-size: 14px;
  color: #475569;
  margin-bottom: 4px;
}

.standard-date {
  font-size: 12px;
  color: #94a3b8;
}

.view-all {
  text-align: center;
  padding-top: 10px;
  border-top: 1px solid #f1f5f9;
}

/* 主要内容区域 */
.safety-content {
  flex: 1;
  min-width: 0;
}

.risk-map-section,
.violation-section,
.news-section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
  margin-bottom: 20px;
  border: 1px solid #e2e8f0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  color: #0f172a;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.map-controls {
  display: flex;
  gap: 10px;
}

.map-container {
  height: 300px;
  margin-bottom: 15px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e2e8f0;
}

.map-legend {
  display: flex;
  justify-content: center;
  gap: 30px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-color {
  width: 20px;
  height: 20px;
  border-radius: 4px;
}

.legend-color.low {
  background-color: #10b981;
}

.legend-color.medium {
  background-color: #f59e0b;
}

.legend-color.high {
  background-color: #ef4444;
}

.issue-text {
  display: inline-block;
  max-width: 180px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-info {
  display: flex;
  flex-direction: column;
}

.product-name {
  font-weight: 500;
  color: #0f172a;
}

.product-brand {
  font-size: 12px;
  color: #94a3b8;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.news-item {
  display: flex;
  gap: 20px;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s;
}

.news-item:hover {
  border-color: #cbd5e1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.news-image {
  position: relative;
  width: 120px;
  height: 90px;
  flex-shrink: 0;
  border-radius: 6px;
  overflow: hidden;
}

.news-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.news-tag {
  position: absolute;
  top: 8px;
  left: 8px;
  background-color: rgba(15, 23, 42, 0.8);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.news-content {
  flex: 1;
  min-width: 0;
}

.news-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0 0 8px;
  line-height: 1.4;
}

.news-summary {
  font-size: 14px;
  color: #64748b;
  line-height: 1.5;
  margin-bottom: 10px;
}

.news-meta {
  display: flex;
  gap: 15px;
  font-size: 13px;
  color: #94a3b8;
}

/* 右侧边栏 */
.safety-tools {
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

.traceability-section,
.detection-tools,
.warning-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.08);
  margin-bottom: 20px;
  border: 1px solid #e2e8f0;
}

.traceability-tips {
  margin-top: 10px;
  padding: 12px;
  background-color: #f8fafc;
  border-radius: 8px;
}

.traceability-tips p {
  margin: 0 0 8px;
  font-size: 14px;
  color: #475569;
  font-weight: 500;
}

.traceability-tips ul {
  margin: 0;
  padding-left: 20px;
}

.traceability-tips li {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 4px;
}

.traceability-result {
  margin-top: 20px;
  padding: 15px;
  background-color: #f8fafc;
  border-radius: 8px;
  border-left: 4px solid #3b82f6;
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.result-header h4 {
  margin: 0;
  font-size: 16px;
  color: #0f172a;
}

.result-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.result-item {
  display: flex;
}

.item-label {
  width: 80px;
  font-size: 14px;
  color: #64748b;
  flex-shrink: 0;
}

.item-value {
  flex: 1;
  font-size: 14px;
  color: #0f172a;
  font-weight: 500;
}

.tools-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tool-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s;
}

.tool-item:hover {
  border-color: #cbd5e1;
  background-color: #f8fafc;
}

.tool-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f1f5f9;
  border-radius: 8px;
}

.tool-icon i {
  font-size: 18px;
  color: #3b82f6;
}

.tool-content {
  flex: 1;
}

.tool-name {
  font-weight: 500;
  color: #0f172a;
  margin-bottom: 4px;
}

.tool-desc {
  font-size: 12px;
  color: #94a3b8;
}

.warning-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.warning-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  border-left: 4px solid #3b82f6;
}

.warning-item.high {
  background-color: rgba(239, 68, 68, 0.05);
  border-left-color: #ef4444;
}

.warning-item.medium {
  background-color: rgba(245, 158, 11, 0.05);
  border-left-color: #f59e0b;
}

.warning-item.low {
  background-color: rgba(59, 130, 246, 0.05);
  border-left-color: #3b82f6;
}

.warning-icon {
  width: 36px;
  height: 36px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: white;
  border-radius: 50%;
}

.warning-item.high .warning-icon {
  color: #ef4444;
}

.warning-item.medium .warning-icon {
  color: #f59e0b;
}

.warning-item.low .warning-icon {
  color: #3b82f6;
}

.warning-content {
  flex: 1;
}

.warning-title {
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 4px;
}

.warning-desc {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 4px;
}

.warning-time {
  font-size: 12px;
  color: #94a3b8;
}

/* 风险预警对话框 */
.warning-dialog {
  padding: 10px 0;
}

.warning-summary {
  display: flex;
  justify-content: space-around;
  margin-bottom: 30px;
  padding: 20px;
  background-color: #f8fafc;
  border-radius: 8px;
}

.summary-item {
  text-align: center;
}

.summary-label {
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
}

.summary-value {
  font-size: 32px;
  font-weight: 700;
}

.summary-value.danger {
  color: #ef4444;
}

.summary-value.warning {
  color: #f59e0b;
}

.summary-value.info {
  color: #3b82f6;
}

.warning-details h4 {
  margin: 0 0 15px;
  font-size: 16px;
  color: #0f172a;
}

/* 检测详情对话框 */
.detection-detail {
  padding: 10px 0;
}

.detail-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e2e8f0;
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-label {
  font-size: 14px;
  color: #64748b;
}

.meta-value {
  font-size: 14px;
  color: #0f172a;
  font-weight: 500;
}

.text-success {
  color: #10b981;
}

.text-danger {
  color: #ef4444;
}

.sample-info {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 15px;
  background-color: #f8fafc;
  border-radius: 8px;
}

.info-item {
  display: flex;
}

.info-label {
  width: 100px;
  font-size: 14px;
  color: #64748b;
  flex-shrink: 0;
}

.info-value {
  flex: 1;
  font-size: 14px;
  color: #0f172a;
  font-weight: 500;
}

.detection-report {
  padding: 20px;
}

.report-summary {
  margin-bottom: 20px;
}

.report-summary h4 {
  margin: 0 0 10px;
  font-size: 16px;
  color: #0f172a;
}

.report-summary p {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
}

.report-actions {
  display: flex;
  gap: 15px;
}

/* 底部信息 */
.safety-footer {
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
  margin: 0 0 10px;
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

  .safety-sidebar,
  .safety-tools {
    width: 100%;
  }

  .safety-sidebar {
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
    gap: 15px;
  }

  .header-left {
    flex-direction: column;
    gap: 15px;
    width: 100%;
  }

  .safety-status {
    width: 100%;
    justify-content: space-between;
  }

  .header-right {
    width: 100%;
    justify-content: space-between;
  }

  .quick-search {
    width: 200px;
  }

  .monitoring-dashboard .el-col {
    margin-bottom: 15px;
  }

  .news-item {
    flex-direction: column;
  }

  .news-image {
    width: 100%;
    height: 150px;
  }

  .warning-summary {
    flex-direction: column;
    gap: 20px;
  }
}
</style>