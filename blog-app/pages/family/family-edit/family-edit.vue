<template>
	<view class="edit-container">
		<scroll-view class="edit-scroll" scroll-y>
			<!-- 封面编辑 -->
			<view class="edit-section">
				<view class="section-header">
					<text class="section-title">封面信息</text>
				</view>
				<view class="cover-edit">
					<view class="cover-preview" @click="changeCover">
						<image :src="editInfo.coverImage" mode="aspectFill" class="cover-image"></image>
						<view class="cover-overlay">
							<text class="cover-edit-text">点击更换封面</text>
						</view>
					</view>
					<view class="cover-inputs">
						<view class="input-group">
							<text class="input-label">姓氏</text>
							<input v-model="editInfo.surname" class="input-field" placeholder="请输入姓氏" />
						</view>
						<view class="input-group">
							<text class="input-label">族谱标题</text>
							<input v-model="editInfo.title" class="input-field" placeholder="请输入族谱标题" />
						</view>
					</view>
				</view>
			</view>

			<!-- 基本信息编辑 -->
			<view class="edit-section">
				<view class="section-header">
					<text class="section-title">基本信息</text>
				</view>
				<view class="form-grid">
					<view class="input-group">
						<text class="input-label">姓氏源流</text>
						<input v-model="editInfo.origin" class="input-field" placeholder="请输入姓氏源流" />
					</view>
					<view class="input-group">
						<text class="input-label">堂号</text>
						<input v-model="editInfo.hallName" class="input-field" placeholder="请输入堂号" />
					</view>
					<view class="input-group">
						<text class="input-label">根据地</text>
						<input v-model="editInfo.base" class="input-field" placeholder="请输入根据地" />
					</view>
					<view class="input-group">
						<text class="input-label">修订时间</text>
						<input v-model="editInfo.revisionDate" class="input-field" placeholder="请输入修订时间" />
					</view>
					<view class="input-group">
						<text class="input-label">联络人</text>
						<input v-model="editInfo.contactPerson" class="input-field" placeholder="请输入联络人姓名" />
					</view>
					<view class="input-group">
						<text class="input-label">联系电话</text>
						<input v-model="editInfo.contactPhone" class="input-field" placeholder="请输入联系电话" />
					</view>
					<view class="input-group">
						<text class="input-label">成员数量</text>
						<input v-model="editInfo.memberCount" type="number" class="input-field" placeholder="请输入成员数量" />
					</view>
				</view>
			</view>

			<!-- 家族介绍编辑 -->
			<view class="edit-section">
				<view class="section-header">
					<text class="section-title">家族介绍</text>
				</view>
				<view class="textarea-group">
					<textarea v-model="editInfo.introduction" class="textarea-field" placeholder="请输入家族介绍..."
						maxlength="500" :show-confirm-bar="false"></textarea>
					<text class="word-count">{{ editInfo.introduction.length }}/500</text>
				</view>
			</view>

			<!-- 家训编辑 -->
			<view class="edit-section">
				<view class="section-header">
					<text class="section-title">家训</text>
					<view class="section-action" @click="addMaxim">
						<text class="action-icon">+</text>
						<text class="action-text">添加</text>
					</view>
				</view>
				<view class="list-editor">
					<view class="list-item" v-for="(maxim, index) in editInfo.maxims" :key="index">
						<input v-model="editInfo.maxims[index]" class="list-input" placeholder="请输入家训内容" />
						<view class="item-actions">
							<text class="action-btn" @click="moveUp(index, 'maxims')" v-if="index > 0">↑</text>
							<text class="action-btn" @click="moveDown(index, 'maxims')"
								v-if="index < editInfo.maxims.length - 1">↓</text>
							<text class="action-btn delete" @click="removeItem(index, 'maxims')">删除</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 家规编辑 -->
			<view class="edit-section">
				<view class="section-header">
					<text class="section-title">家规</text>
					<view class="section-action" @click="addRule">
						<text class="action-icon">+</text>
						<text class="action-text">添加</text>
					</view>
				</view>
				<view class="list-editor">
					<view class="list-item" v-for="(rule, index) in editInfo.rules" :key="index">
						<input v-model="editInfo.rules[index]" class="list-input" placeholder="请输入家规内容" />
						<view class="item-actions">
							<text class="action-btn" @click="moveUp(index, 'rules')" v-if="index > 0">↑</text>
							<text class="action-btn" @click="moveDown(index, 'rules')"
								v-if="index < editInfo.rules.length - 1">↓</text>
							<text class="action-btn delete" @click="removeItem(index, 'rules')">删除</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 迁徙历史编辑 -->
			<view class="edit-section">
				<view class="section-header">
					<text class="section-title">迁徙历史</text>
					<view class="section-action" @click="addMigration">
						<text class="action-icon">+</text>
						<text class="action-text">添加</text>
					</view>
				</view>
				<view class="migration-editor">
					<view class="migration-item" v-for="(migration, index) in editInfo.migrationHistory" :key="index">
						<view class="migration-inputs">
							<input v-model="migration.time" class="migration-time" placeholder="时间（如：明朝洪武年间）" />
							<textarea v-model="migration.description" class="migration-desc" placeholder="迁徙描述"
								:show-confirm-bar="false"></textarea>
						</view>
						<view class="item-actions">
							<text class="action-btn" @click="moveUp(index, 'migrationHistory')"
								v-if="index > 0">↑</text>
							<text class="action-btn" @click="moveDown(index, 'migrationHistory')"
								v-if="index < editInfo.migrationHistory.length - 1">↓</text>
							<text class="action-btn delete" @click="removeItem(index, 'migrationHistory')">删除</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 祖先名人编辑 -->
			<view class="edit-section">
				<view class="section-header">
					<text class="section-title">祖先名人</text>
					<view class="section-action" @click="addAncestor">
						<text class="action-icon">+</text>
						<text class="action-text">添加</text>
					</view>
				</view>
				<view class="ancestor-editor">
					<view class="ancestor-item" v-for="(ancestor, index) in editInfo.famousAncestors" :key="index">
						<view class="ancestor-portrait-edit" @click="changeAncestorPortrait(index)">
							<image :src="ancestor.portrait" mode="aspectFill" class="portrait-image"></image>
							<view class="portrait-overlay">
								<text class="portrait-edit-text">更换头像</text>
							</view>
						</view>
						<view class="ancestor-inputs">
							<input v-model="ancestor.name" class="ancestor-input" placeholder="姓名" />
							<input v-model="ancestor.dynasty" class="ancestor-input" placeholder="朝代" />
							<input v-model="ancestor.achievement" class="ancestor-input" placeholder="主要成就" />
						</view>
						<view class="item-actions">
							<text class="action-btn" @click="moveUp(index, 'famousAncestors')" v-if="index > 0">↑</text>
							<text class="action-btn" @click="moveDown(index, 'famousAncestors')"
								v-if="index < editInfo.famousAncestors.length - 1">↓</text>
							<text class="action-btn delete" @click="removeItem(index, 'famousAncestors')">删除</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 底部操作按钮 -->
			<view class="bottom-actions">
				<button class="save-btn primary" @click="saveFamilyInfo">保存修改</button>
				<button class="save-btn secondary" @click="resetForm">重置</button>
				<button class="save-btn danger" @click="goBack">取消</button>
			</view>
		</scroll-view>

		<!-- 加载提示 -->
		<view class="loading-mask" v-if="loading">
			<view class="loading-content">
				<text class="loading-text">保存中...</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				loading: false,
				editInfo: {
					coverImage: '/static/family-cover.jpg',
					surname: '杨氏',
					title: '弘农堂杨氏族谱',
					origin: '源于姬姓，周朝王室后裔',
					hallName: '弘农堂',
					base: '陕西弘农郡',
					revisionDate: '2025年10月',
					contactPerson: '杨明远',
					contactPhone: '139****9999',
					memberCount: 203,
					introduction: '杨氏家族源于弘农郡，历史悠久，文化底蕴深厚。杨姓为中华大姓之一，历代名人辈出，素有"弘农世泽，清白传家"之美誉。家族以忠孝节义为立身之本，诗书礼仪为传家之宝，形成了独特的家族文化和优良传统。',
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
				},
				originalInfo: {}
			}
		},

		onLoad(options) {
			// 如果有传入的数据，则加载编辑数据
			if (options.familyData) {
				try {
					const familyData = JSON.parse(decodeURIComponent(options.familyData));
					this.editInfo = familyData;
					console.log('接收到家族数据:', this.editInfo);
				} catch (e) {
					console.error('解析家族数据失败:', e);
					uni.showToast({
						title: '数据加载失败',
						icon: 'none'
					});
				}
			}

			// 保存原始数据用于重置
			this.originalInfo = JSON.parse(JSON.stringify(this.editInfo));
		},

		methods: {
			// 返回上一页
			goBack() {
				uni.navigateBack();
			},

			// 保存家族信息
			async saveFamilyInfo() {
				// 简单验证
				if (!this.editInfo.surname || !this.editInfo.title) {
					uni.showToast({
						title: '请填写姓氏和族谱标题',
						icon: 'none'
					});
					return;
				}

				this.loading = true;

				try {
					// 模拟保存操作
					await new Promise(resolve => setTimeout(resolve, 1000));

					// 这里实际应该调用API保存数据
					// await this.$api.family.updateFamilyInfo(this.editInfo);

					uni.showToast({
						title: '保存成功',
						icon: 'success'
					});

					// 保存成功后返回
					setTimeout(() => {
						// 通过事件总线或全局状态管理通知列表页面更新
						uni.$emit('familyInfoUpdated', this.editInfo);
						this.goBack();
					}, 1500);

				} catch (error) {
					console.error('保存失败:', error);
					uni.showToast({
						title: '保存失败，请重试',
						icon: 'none'
					});
				} finally {
					this.loading = false;
				}
			},

			// 重置表单
			resetForm() {
				uni.showModal({
					title: '确认重置',
					content: '确定要重置所有修改吗？',
					success: (res) => {
						if (res.confirm) {
							this.editInfo = JSON.parse(JSON.stringify(this.originalInfo));
							uni.showToast({
								title: '已重置',
								icon: 'success'
							});
						}
					}
				});
			},

			// 更换封面
			changeCover() {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						this.editInfo.coverImage = res.tempFilePaths[0];
					}
				});
			},

			// 更换祖先头像
			changeAncestorPortrait(index) {
				uni.chooseImage({
					count: 1,
					sizeType: ['compressed'],
					sourceType: ['album', 'camera'],
					success: (res) => {
						this.$set(this.editInfo.famousAncestors[index], 'portrait', res.tempFilePaths[0]);
					}
				});
			},

			// 添加家训
			addMaxim() {
				this.editInfo.maxims.push('');
			},

			// 添加家规
			addRule() {
				this.editInfo.rules.push('');
			},

			// 添加迁徙历史
			addMigration() {
				this.editInfo.migrationHistory.push({
					time: '',
					description: ''
				});
			},

			// 添加祖先名人
			addAncestor() {
				this.editInfo.famousAncestors.push({
					name: '',
					dynasty: '',
					achievement: '',
					portrait: '/static/default-ancestor.jpg'
				});
			},

			// 删除项目
			removeItem(index, type) {
				uni.showModal({
					title: '确认删除',
					content: '确定要删除此项吗？',
					success: (res) => {
						if (res.confirm) {
							this.editInfo[type].splice(index, 1);
						}
					}
				});
			},

			// 上移项目
			moveUp(index, type) {
				if (index > 0) {
					const array = [...this.editInfo[type]]; // 创建新数组
					// 交换元素
					[array[index], array[index - 1]] = [array[index - 1], array[index]];
					// 重新赋值触发响应式更新
					this.$set(this.editInfo, type, array);
				}
			},

			// 下移项目
			moveDown(index, type) {
				const array = [...this.editInfo[type]]; // 创建新数组
				if (index < array.length - 1) {
					// 交换元素
					[array[index], array[index + 1]] = [array[index + 1], array[index]];
					// 重新赋值触发响应式更新
					this.$set(this.editInfo, type, array);
				}
			}
		}
	}
</script>

<style scoped>
	.edit-container {
		background: #f5f5f5;
		min-height: 100vh;
	}

	/* 滚动区域 */
	.edit-scroll {
		height: calc(100vh - 130rpx);
	}

	/* 编辑区块 */
	.edit-section {
		background: white;
		margin: 20rpx;
		border-radius: 16rpx;
		overflow: hidden;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 30rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.section-action {
		display: flex;
		align-items: center;
		padding: 10rpx 20rpx;
		background: #007AFF;
		border-radius: 30rpx;
	}

	.action-icon {
		color: white;
		font-size: 24rpx;
		margin-right: 8rpx;
	}

	.action-text {
		color: white;
		font-size: 24rpx;
	}

	/* 封面编辑 */
	.cover-edit {
		padding: 30rpx;
	}

	.cover-preview {
		position: relative;
		height: 300rpx;
		border-radius: 16rpx;
		overflow: hidden;
		margin-bottom: 30rpx;
	}

	.cover-image {
		width: 100%;
		height: 100%;
	}

	.cover-overlay {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.cover-edit-text {
		color: white;
		font-size: 28rpx;
	}

	.cover-inputs {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	/* 表单样式 */
	.form-grid {
		padding: 20rpx 30rpx;
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.input-group {
		display: flex;
		flex-direction: column;
	}

	.input-label {
		font-size: 28rpx;
		color: #333;
		margin-bottom: 15rpx;
		font-weight: 500;
	}

	.input-field {
		padding: 20rpx;
		border: 1rpx solid #e0e0e0;
		border-radius: 8rpx;
		font-size: 28rpx;
		background: #f8f9fa;
	}

	.textarea-group {
		padding: 30rpx;
		position: relative;
	}

	.textarea-field {
		width: 100%;
		height: 200rpx;
		padding: 20rpx;
		border: 1rpx solid #e0e0e0;
		border-radius: 8rpx;
		font-size: 28rpx;
		background: #f8f9fa;
		box-sizing: border-box;
	}

	.word-count {
		position: absolute;
		bottom: 40rpx;
		right: 40rpx;
		font-size: 24rpx;
		color: #999;
	}

	/* 列表编辑器 */
	.list-editor {
		padding: 20rpx 30rpx;
	}

	.list-item {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
		padding: 20rpx;
		background: #f8f9fa;
		border-radius: 8rpx;
	}

	.list-input {
		flex: 1;
		padding: 15rpx;
		font-size: 28rpx;
		background: transparent;
	}

	.item-actions {
		display: flex;
		align-items: center;
		margin-left: 20rpx;
	}

	.action-btn {
		padding: 10rpx 15rpx;
		background: #e0e0e0;
		border-radius: 6rpx;
		font-size: 24rpx;
		margin-left: 10rpx;
	}

	.action-btn.delete {
		background: #ff4757;
		color: white;
	}

	/* 迁徙历史编辑器 */
	.migration-editor {
		padding: 20rpx 30rpx;
	}

	.migration-item {
		margin-bottom: 30rpx;
		padding: 20rpx;
		background: #f8f9fa;
		border-radius: 8rpx;
	}

	.migration-inputs {
		margin-bottom: 15rpx;
	}

	.migration-time {
		width: 100%;
		padding: 15rpx;
		font-size: 28rpx;
		background: white;
		border-radius: 6rpx;
		margin-bottom: 15rpx;
	}

	.migration-desc {
		width: 100%;
		height: 120rpx;
		padding: 15rpx;
		font-size: 28rpx;
		background: white;
		border-radius: 6rpx;
		box-sizing: border-box;
	}

	/* 祖先名人编辑器 */
	.ancestor-editor {
		padding: 20rpx 30rpx;
	}

	.ancestor-item {
		display: flex;
		align-items: flex-start;
		margin-bottom: 30rpx;
		padding: 20rpx;
		background: #f8f9fa;
		border-radius: 8rpx;
	}

	.ancestor-portrait-edit {
		position: relative;
		width: 120rpx;
		height: 120rpx;
		border-radius: 60rpx;
		overflow: hidden;
		margin-right: 20rpx;
		flex-shrink: 0;
	}

	.portrait-image {
		width: 100%;
		height: 100%;
	}

	.portrait-overlay {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.portrait-edit-text {
		color: white;
		font-size: 20rpx;
		text-align: center;
	}

	.ancestor-inputs {
		flex: 1;
		display: flex;
		flex-direction: column;
		gap: 10rpx;
	}

	.ancestor-input {
		padding: 15rpx;
		font-size: 28rpx;
		background: white;
		border-radius: 6rpx;
	}

	/* 底部操作按钮 */
	.bottom-actions {
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

	.save-btn {
		padding: 15rpx 60rpx;
		border-radius: 50rpx;
		font-size: 30rpx;
		border: none;
	}

	.save-btn.primary {
		background: #007AFF;
		color: white;
	}

	.save-btn.secondary {
		background: #f8f9fa;
		color: #333;
		border: 1rpx solid #e0e0e0;
	}

	.save-btn.danger {
		background: #ff4757;
		color: white;
	}

	/* 加载遮罩 */
	.loading-mask {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 1000;
	}

	.loading-content {
		background: white;
		padding: 40rpx 60rpx;
		border-radius: 16rpx;
	}

	.loading-text {
		font-size: 28rpx;
		color: #333;
	}
</style>