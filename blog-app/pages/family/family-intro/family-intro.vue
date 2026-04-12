<template>
	<view class="family-container">
		<!-- 家族封面图 -->
		<view class="family-cover">
			<image :src="familyInfo.coverImage" mode="aspectFill" class="cover-image"></image>
			<view class="cover-overlay">
				<text class="family-surname">{{ familyInfo.surname }}</text>
				<text class="family-title">{{ familyInfo.title }}</text>
			</view>
		</view>

		<!-- 基本信息卡片 -->
		<view class="info-card">
			<view class="card-header">
				<text class="header-title">家族基本信息</text>
			</view>
			<view class="info-grid">
				<view class="info-item">
					<text class="info-label">姓氏源流</text>
					<text class="info-value">{{ familyInfo.origin }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">堂号</text>
					<text class="info-value">{{ familyInfo.hallName }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">根据地</text>
					<text class="info-value">{{ familyInfo.base }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">修订时间</text>
					<text class="info-value">{{ familyInfo.revisionDate }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">联络人</text>
					<text class="info-value">{{ familyInfo.contactPerson }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">成员数量</text>
					<text class="info-value">{{ familyInfo.memberCount }}人</text>
				</view>
			</view>
		</view>

		<!-- 家族介绍 -->
		<view class="info-card">
			<view class="card-header">
				<text class="header-title">家族介绍</text>
			</view>
			<view class="content-box">
				<text class="content-text">{{ familyInfo.introduction }}</text>
			</view>
		</view>

		<!-- 家训家规 -->
		<view class="info-card">
			<view class="card-header">
				<text class="header-title">家训家规</text>
			</view>
			<view class="rules-section">
				<view class="rules-type">
					<text class="rules-title">家训</text>
					<view class="rules-list">
						<view class="rule-item" v-for="(maxim, index) in familyInfo.maxims" :key="'maxim-' + index">
							<text class="rule-icon">•</text>
							<text class="rule-text">{{ maxim }}</text>
						</view>
					</view>
				</view>
				<view class="rules-type">
					<text class="rules-title">家规</text>
					<view class="rules-list">
						<view class="rule-item" v-for="(rule, index) in familyInfo.rules" :key="'rule-' + index">
							<text class="rule-icon">•</text>
							<text class="rule-text">{{ rule }}</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 家族迁徙 -->
		<view class="info-card" v-if="familyInfo.migrationHistory">
			<view class="card-header">
				<text class="header-title">家族迁徙史</text>
			</view>
			<view class="migration-timeline">
				<view class="timeline-item" v-for="(event, index) in familyInfo.migrationHistory"
					:key="'migration-' + index">
					<view class="timeline-dot"></view>
					<view class="timeline-content">
						<text class="timeline-time">{{ event.time }}</text>
						<text class="timeline-desc">{{ event.description }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 祖先名人 -->
		<view class="info-card" v-if="familyInfo.famousAncestors">
			<view class="card-header">
				<text class="header-title">祖先名人</text>
			</view>
			<scroll-view class="ancestor-scroll" scroll-x>
				<view class="ancestor-list">
					<view class="ancestor-item" v-for="(ancestor, index) in familyInfo.famousAncestors"
						:key="'ancestor-' + index">
						<image :src="ancestor.portrait" mode="aspectFill" class="ancestor-portrait"></image>
						<text class="ancestor-name">{{ ancestor.name }}</text>
						<text class="ancestor-dynasty">{{ ancestor.dynasty }}</text>
						<text class="ancestor-achievement">{{ ancestor.achievement }}</text>
					</view>
				</view>
			</scroll-view>
		</view>

		<!-- 操作按钮 -->
		<view class="action-buttons">
			<button class="action-btn primary" @click="viewFamilyTree">
				<text class="btn-text">查看族谱树</text>
			</button>
			<button class="action-btn edit" @click="editFamilyInfo">
				<text class="btn-text">编辑族谱</text>
			</button>
			<button class="action-btn secondary" @click="contactFamily">
				<text class="btn-text">联系家族</text>
			</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				familyInfo: {
					// 封面信息
					coverImage: '/static/logo.png',
					surname: '杨氏',
					title: '弘农堂杨氏族谱',

					// 基本信息
					origin: '源于姬姓，周朝王室后裔',
					hallName: '弘农堂',
					base: '陕西弘农郡',
					revisionDate: '2025年10月',
					contactPerson: '杨华杰',
					contactPhone: '157****84621',
					memberCount: 203,

					// 家族介绍
					introduction: '杨氏家族源于弘农郡，历史悠久，文化底蕴深厚。杨姓为中华大姓之一，历代名人辈出，素有"弘农世泽，清白传家"之美誉。家族以忠孝节义为立身之本，诗书礼仪为传家之宝，形成了独特的家族文化和优良传统。',

					// 家训家规
					maxims: [
						'忠君爱国，守法遵纪',
						'孝亲敬长，和睦宗族',
						'清白持家，勤俭节约',
						'诗书传家，礼义兴邦'
					],
					rules: [
						'晨昏定省，孝敬长辈',
						'兄弟友爱，妯娌和睦',
						'勤俭持家，杜绝奢靡',
						'诚实守信，言行一致'
					],

					// 迁徙历史
					migrationHistory: [{
							time: '隋唐时期',
							description: '从陕西弘农迁至河南洛阳'
						},
						{
							time: '宋元时期',
							description: '分支迁至江南地区'
						},
						{
							time: '明清时期',
							description: '部分族人迁往湖广、四川'
						}
					],

					// 祖先名人
					famousAncestors: [{
							name: '杨震',
							dynasty: '东汉',
							achievement: '著名清官，被誉为"关西孔子"',
							portrait: '/static/ancestor1.jpg'
						},
						{
							name: '杨坚',
							dynasty: '隋朝',
							achievement: '隋朝开国皇帝',
							portrait: '/static/ancestor2.jpg'
						},
						{
							name: '杨贵妃',
							dynasty: '唐朝',
							achievement: '中国古代四大美女之一',
							portrait: '/static/ancestor3.jpg'
						}
					]
				}
			}
		},

		methods: {
			viewFamilyTree() {
				uni.navigateTo({
					url: '/pages/family/family-source/family-source'
				})
			},
			editFamilyInfo() {
				const familyData = encodeURIComponent(JSON.stringify(this.familyInfo));
				uni.navigateTo({
					url: `/pages/family/family-edit/family-edit?familyData=${familyData}`
				});
			},
			contactFamily() {
				uni.makePhoneCall({
					phoneNumber: this.familyInfo.contactPhone
				})
			}
		}
	}
</script>

<style scoped>
	.family-container {
		background: #f5f5f5;
		min-height: 100vh;
		padding-bottom: 120rpx;
	}

	/* 封面样式 */
	.family-cover {
		position: relative;
		height: 400rpx;
		overflow: hidden;
	}

	.cover-image {
		width: 100%;
		height: 100%;
	}

	.cover-overlay {
		position: absolute;
		bottom: 0;
		left: 0;
		right: 0;
		background: linear-gradient(transparent, rgba(0, 0, 0, 0.7));
		padding: 40rpx;
		color: white;
	}

	.family-surname {
		display: block;
		font-size: 64rpx;
		font-weight: bold;
		margin-bottom: 10rpx;
	}

	.family-title {
		display: block;
		font-size: 32rpx;
		opacity: 0.9;
	}

	/* 卡片样式 */
	.info-card {
		background: white;
		margin: 20rpx;
		border-radius: 16rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
		overflow: hidden;
	}

	.card-header {
		padding: 30rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.header-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
	}

	/* 信息网格 */
	.info-grid {
		padding: 20rpx;
	}

	.info-item {
		display: flex;
		justify-content: space-between;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f8f8f8;
	}

	.info-item:last-child {
		border-bottom: none;
	}

	.info-label {
		color: #666;
		font-size: 28rpx;
	}

	.info-value {
		color: #333;
		font-size: 28rpx;
		font-weight: 500;
		text-align: right;
		flex: 1;
		margin-left: 20rpx;
	}

	/* 内容区域 */
	.content-box {
		padding: 30rpx;
	}

	.content-text {
		font-size: 30rpx;
		line-height: 1.6;
		color: #333;
	}

	/* 家训家规样式 */
	.rules-section {
		padding: 20rpx 30rpx;
	}

	.rules-type {
		margin-bottom: 40rpx;
	}

	.rules-type:last-child {
		margin-bottom: 0;
	}

	.rules-title {
		display: block;
		font-size: 32rpx;
		font-weight: bold;
		color: #e74c3c;
		margin-bottom: 20rpx;
		padding-left: 20rpx;
		border-left: 8rpx solid #e74c3c;
	}

	.rules-list {
		background: #f8f9fa;
		border-radius: 12rpx;
		padding: 20rpx;
	}

	.rule-item {
		display: flex;
		align-items: flex-start;
		padding: 15rpx 0;
	}

	.rule-icon {
		color: #e74c3c;
		margin-right: 20rpx;
		font-size: 28rpx;
	}

	.rule-text {
		flex: 1;
		font-size: 28rpx;
		line-height: 1.5;
		color: #333;
	}

	/* 时间线样式 */
	.migration-timeline {
		padding: 30rpx;
	}

	.timeline-item {
		display: flex;
		margin-bottom: 40rpx;
		position: relative;
	}

	.timeline-item:last-child {
		margin-bottom: 0;
	}

	.timeline-dot {
		width: 20rpx;
		height: 20rpx;
		background: #3498db;
		border-radius: 50%;
		margin-right: 30rpx;
		margin-top: 10rpx;
		flex-shrink: 0;
	}

	.timeline-content {
		flex: 1;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.timeline-time {
		display: block;
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}

	.timeline-desc {
		display: block;
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
	}

	/* 祖先名人样式 */
	.ancestor-scroll {
		white-space: nowrap;
		padding: 20rpx;
	}

	.ancestor-list {
		display: inline-flex;
	}

	.ancestor-item {
		display: inline-flex;
		flex-direction: column;
		align-items: center;
		margin-right: 40rpx;
		width: 200rpx;
		white-space: normal;
	}

	.ancestor-portrait {
		width: 120rpx;
		height: 120rpx;
		border-radius: 60rpx;
		margin-bottom: 20rpx;
	}

	.ancestor-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 8rpx;
		text-align: center;
	}

	.ancestor-dynasty {
		font-size: 24rpx;
		color: #e74c3c;
		margin-bottom: 8rpx;
	}

	.ancestor-achievement {
		font-size: 24rpx;
		color: #666;
		text-align: center;
		line-height: 1.4;
	}

	/* 操作按钮 */
	.action-buttons {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		background: white;
		padding: 10rpx;
		display: flex;
		gap: 30rpx;
		box-shadow: 0 -4rpx 30rpx rgba(0, 0, 0, 0.1);
	}

	.action-btn {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 15rpx;
		border-radius: 50rpx;
		font-size: 30rpx;
		border: none;
	}

	.action-btn.primary {
		background: #e74c3c;
		color: white;
	}

	.action-btn.secondary {
		background: #f8f9fa;
		color: #333;
		border: 1rpx solid #e0e0e0;
	}

	.action-btn.edit {
		background: #ffa502;
		color: white;
	}
</style>