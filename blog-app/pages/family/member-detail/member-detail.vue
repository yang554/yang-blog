<template>
	<view class="container">
		<!-- 顶部头像和信息区域 -->
		<view class="header"
			:style="{ 'filter': !memberData.isLife || memberData.isLife == '0' ? 'grayscale(100%)' : 'grayscale(0%)' }">
			<view class="avatar-section">
				<image
					:src="memberData.rawData.userImgUrl || memberData.gender === 'male' ? require('@/static/defaultImg.png') : require('@/static/defaultW.png')"
					class="avatar" mode="aspectFill" />
				<view class="name-section">
					<text class="name">{{ memberData.name }}</text>
					<view class="tags">
						<text class="tag gender">{{ genderText }}</text>
						<text class="tag relation">{{ memberData.relation }}</text>
						<text class="tag status">{{ statusText }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 基本信息卡片 -->
		<view class="card">
			<view class="card-header">
				<text class="card-title">基本信息</text>
			</view>
			<view class="card-content">
				<view class="info-item">
					<text class="label">姓名</text>
					<text class="value">{{ memberData.name }}</text>
				</view>
				<view class="info-item">
					<text class="label">性别</text>
					<text class="value">{{ genderText }}</text>
				</view>
				<view class="info-item">
					<text class="label">年龄</text>
					<text class="value">{{ memberData.age }}岁</text>
				</view>
				<view class="info-item">
					<text class="label">生辰</text>
					<text class="value">{{ formatDate(memberData.birthDate) }}</text>
				</view>
				<view class="info-item" v-if="!memberData.isLife">
					<text class="label">忌日</text>
					<text class="value">{{ formatDate(memberData.dieDay) }}</text>
				</view>
				<!-- <view class="info-item" v-if="!memberData.isLife">
					<text class="label">距今</text>
					<text class="value">已故{{ memberData.dAge }}年</text>
				</view> -->
				<view class="info-item">
					<text class="label">父亲</text>
					<text class="value">{{ memberData.pName || '未填写' }}</text>
				</view>
				<view class="info-item">
					<text class="label">母亲</text>
					<text class="value">{{ memberData.mName || '未填写' }}</text>
				</view>
			</view>
		</view>

		<!-- 联系信息卡片 -->
		<view class="card">
			<view class="card-header">
				<text class="card-title">联系信息</text>
			</view>
			<view class="card-content">
				<view class="info-item" v-if="memberData.rawData.userPhone">
					<text class="label">手机号码</text>
					<text class="value">{{ memberData.rawData.userPhone }}</text>
				</view>
				<view class="info-item" v-if="memberData.rawData.userEmil">
					<text class="label">邮箱</text>
					<text class="value">{{ memberData.rawData.userEmil }}</text>
				</view>
				<view class="info-item">
					<text class="label">籍贯</text>
					<text class="value">{{ memberData.rawData.userAddressOld || '未填写' }}</text>
				</view>
				<view class="info-item">
					<text class="label">现居地</text>
					<text class="value">{{ memberData.rawData.userAddressNew || '未填写' }}</text>
				</view>
			</view>
		</view>

		<!-- 座右铭卡片 -->
		<view class="card" v-if="memberData.rawData.userNote">
			<view class="card-header">
				<text class="card-title">{{ !memberData.isLife ? '备注' : '座右铭'}}</text>
			</view>
			<view class="card-content">
				<text class="motto">{{ memberData.rawData.userNote }}</text>
			</view>
		</view>

		<!-- 配偶信息 -->
		<view class="card" v-if="memberData.spouse && memberData.spouse.length > 0">
			<view class="card-header">
				<text class="card-title">配偶信息</text>
			</view>
			<view class="card-content">
				<view class="spouse-item" v-for="spouse in memberData.spouse" :key="spouse.id"
					@click="viewSpouseDetail(spouse)">
					<image :src="spouse.rawData.userImgUrl || '/static/default-avatar.png'" class="spouse-avatar"
						mode="aspectFill" />
					<view class="spouse-info">
						<text class="spouse-name">{{ spouse.name }}</text>
						<text class="spouse-relation">{{ spouse.relation }}</text>
					</view>
					<view class="spouse-detail">
						<text class="spouse-age">{{ spouse.age }}岁</text>
						<text class="spouse-status">{{ spouse.isLife ? '在世' : '已故' }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 子女信息 -->
		<view class="card" v-if="memberData.children && memberData.children.length > 0">
			<view class="card-header">
				<text class="card-title">子女信息 ({{ memberData.children.length }}位)</text>
			</view>
			<view class="card-content">
				<view class="children-item" v-for="child in memberData.children" :key="child.id"
					@click="viewChildDetail(child)">
					<view class="child-main">
						<image :src="child.rawData.userImgUrl || '/static/default-avatar.png'" class="child-avatar"
							mode="aspectFill" />
						<view class="child-info">
							<text class="child-name">{{ child.name }}</text>
							<view class="child-details">
								<text class="child-relation">{{ child.relation }}</text>
								<text class="child-gender">{{ child.sex }}</text>
								<text class="child-age" v-if="child.age">{{ child.age }}岁</text>
							</view>
						</view>
					</view>
					<view class="child-status">
						<text :class="['status-dot', child.isLife ? 'living' : 'deceased']"></text>
						<text class="status-text">{{ child.isLife ? '在世' : '已故' }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 操作按钮 -->
		<view class="action-buttons">
			<button class="btn btn-primary" @click="editMember">编辑信息</button>
			<button class="btn btn-secondary" @click="shareMember">分享</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				memberData: {}
			}
		},
		onLoad(options) {
			if (options.node) {
				// 解析节点数据
				const nodeData = JSON.parse(decodeURIComponent(options.node));
				this.memberData = nodeData;
			}
			this.setupEventListeners();
		},
		onUnload() {
			// 页面卸载时移除监听
			this.removeEventListeners();
		},
		onBackPress(options) {
			const pages = getCurrentPages();
			const prevPage = pages[pages.length - 2];
			if (prevPage) {
				// 直接调用上一页的方法
				if (prevPage.$vm && prevPage.$vm.loadFamilyData) {
					prevPage.$vm.loadFamilyData();
				} else if (prevPage.loadFamilyData) {
					prevPage.loadFamilyData();
				}
			}
			
			
		},
		computed: {
			genderText() {
				return this.memberData.sex || (this.memberData.gender === 'male' ? '男' : '女')
			},
			statusText() {
				let text = this.memberData.isLife ? '在世' : '已故';
				if(!this.memberData.isLife){
					text += `${this.memberData.dAge}年`
				}
				
				return text;
			}
		},
		methods: {
			// 设置事件监听
			setupEventListeners() {
				uni.$on('memberDataUpdated', this.handleMemberDataUpdated);
			},

			// 移除事件监听
			removeEventListeners() {
				uni.$off('memberDataUpdated', this.handleMemberDataUpdated);
			},
			// 处理成员数据更新事件
			handleMemberDataUpdated(event) {
				// 如果更新的是当前成员，重新加载数据
				if (event.data.userId === this.memberData.id) {
					uni.showToast({
						title: '信息已更新',
						icon: 'success'
					});

					this.memberData.name = event.data.userName;
					this.memberData.isLife = event.data.isSurvival === '是' ? true : false;
					this.memberData.rawData.userAddressNew = event.data.userAddressNew;
					this.memberData.rawData.userAddressOld = event.data.userAddressOld;
					this.memberData.birthDate = event.data.userBirthDay;
					this.memberData.dieDay = event.data.userDieDay;
					this.memberData.rawData.userEmil = event.data.userEmil;
					this.memberData.rawData.userNote = event.data.userNote;
					this.memberData.rawData.userPhone = event.data.userPhone;
					this.memberData.sex = event.data.userSex;

				}
			},
			formatDate(dateString) {
				if (!dateString) return '未填写'
				const date = new Date(dateString)
				return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
			},
			viewSpouseDetail(spouse) {
				uni.navigateTo({
					url: `/pages/member-detail/member-detail?id=${spouse.id}`
				})
			},
			viewChildDetail(child) {
				uni.navigateTo({
					url: `/pages/member-detail/member-detail?id=${child.id}`
				})
			},
			editMember() {
				const memberStr = encodeURIComponent(JSON.stringify(this.memberData));
				uni.navigateTo({
					url: `/pages/family/edit-member/edit-member?node=${memberStr}`
				});
			},
			shareMember() {
				uni.showToast({
					title: '分享功能开发中',
					icon: 'none'
				})
			}
		}
	}
</script>

<style scoped>
	.container {
		padding: 20rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	/* 头部样式 */
	.header {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		border-radius: 20rpx;
		padding: 40rpx;
		margin-bottom: 30rpx;
		color: white;
	}

	.avatar-section {
		display: flex;
		align-items: center;
	}

	.avatar {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		border: 4rpx solid rgba(255, 255, 255, 0.3);
		margin-right: 30rpx;
	}

	.name-section {
		flex: 1;
	}

	.name {
		font-size: 48rpx;
		font-weight: bold;
		display: block;
		margin-bottom: 20rpx;
	}

	.tags {
		display: flex;
		flex-wrap: wrap;
		gap: 15rpx;
	}

	.tag {
		padding: 8rpx 20rpx;
		border-radius: 30rpx;
		font-size: 24rpx;
		background: rgba(255, 255, 255, 0.2);
	}

	/* 卡片样式 */
	.card {
		background: white;
		border-radius: 20rpx;
		margin-bottom: 30rpx;
		overflow: hidden;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
	}

	.card-header {
		padding: 30rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.card-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.card-content {
		padding: 30rpx;
	}

	/* 信息项样式 */
	.info-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f8f8f8;
	}

	.info-item:last-child {
		border-bottom: none;
	}

	.label {
		color: #666;
		font-size: 28rpx;
	}

	.value {
		color: #333;
		font-size: 28rpx;
		font-weight: 500;
		text-align: right;
	}

	/* 座右铭样式 */
	.motto {
		font-size: 28rpx;
		color: #666;
		font-style: italic;
		line-height: 1.6;
	}

	/* 配偶样式 */
	.spouse-item {
		display: flex;
		align-items: center;
		padding: 20rpx 0;
		border-bottom: 1rpx solid #f8f8f8;
	}

	.spouse-item:last-child {
		border-bottom: none;
	}

	.spouse-avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		margin-right: 20rpx;
	}

	.spouse-info {
		flex: 1;
	}

	.spouse-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		display: block;
	}

	.spouse-relation {
		font-size: 24rpx;
		color: #666;
	}

	.spouse-detail {
		text-align: right;
	}

	.spouse-age {
		font-size: 28rpx;
		color: #333;
		display: block;
	}

	.spouse-status {
		font-size: 24rpx;
		color: #666;
	}

	/* 子女样式 */
	.children-item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 25rpx 0;
		border-bottom: 1rpx solid #f8f8f8;
	}

	.children-item:last-child {
		border-bottom: none;
	}

	.child-main {
		display: flex;
		align-items: center;
		flex: 1;
	}

	.child-avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		margin-right: 20rpx;
	}

	.child-info {
		flex: 1;
	}

	.child-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 10rpx;
	}

	.child-details {
		display: flex;
		gap: 20rpx;
	}

	.child-relation,
	.child-gender,
	.child-age {
		font-size: 24rpx;
		color: #666;
	}

	.child-status {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.status-dot {
		width: 16rpx;
		height: 16rpx;
		border-radius: 50%;
	}

	.status-dot.living {
		background-color: #67C23A;
	}

	.status-dot.deceased {
		background-color: #909399;
	}

	.status-text {
		font-size: 24rpx;
		color: #666;
	}

	/* 操作按钮样式 */
	.action-buttons {
		display: flex;
		gap: 20rpx;
		padding: 30rpx 0;
	}

	.btn {
		flex: 1;
		height: 80rpx;
		border-radius: 40rpx;
		font-size: 28rpx;
		border: none;
	}

	.btn-primary {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		color: white;
	}

	.btn-secondary {
		background: #f0f0f0;
		color: #333;
	}
</style>