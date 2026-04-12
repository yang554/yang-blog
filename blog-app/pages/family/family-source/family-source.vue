<template>
	<view class="family-container">
		<!-- 顶部控制栏 -->
		<view class="control-bar">
			<view class="search-section">
				<input v-model="searchName" placeholder="查找家族成员" class="search-input" @confirm="searchByName" />
				<button @click="searchByName" class="action-btn">搜索</button>
			</view>
		</view>

		<!-- 族谱树 -->
		<view class="genealogy-panel">
			<fgtree ref="familyTree" :list="treeData" @check="handleNodeClick" class="family-tree">
			</fgtree>
		</view>

		<!-- 悬浮按钮组 -->
		<view class="floating-buttons">
			<view class="floating-btn" @click="zoomToFit">
				<image src="@/static/icon/fuwei.png" mode="aspectFit" class="btn-icon" />
			</view>
			<view class="floating-btn" @click="toggleOrientation">
				<image :src="rotationIcon" mode="aspectFit" class="btn-icon" />
			</view>
		</view>

		<!-- 操作弹出层 -->
		<view class="action-popup" v-if="showActionPopup" @tap="closePopup">
			<view class="popup-content" @tap.stop>
				<view class="popup-header">
					<text class="popup-title"></text>
					<text class="popup-close" @tap="closePopup">×</text>
				</view>

				<view class="action-grid">
					<!-- 添加父母亲 -->
					<view class="action-row">
						<view class="action-item" @tap="addParent">
							<image src="@/static/icon/037-parents.png" mode="aspectFit" class="action-icon" />
							<text class="action-text">添加父母</text>
						</view>
					</view>

					<!-- 添加兄弟姐妹、自己、配偶 -->
					<view class="action-row">
						<view class="action-item" @tap="addSibling">
							<image src="@/static/icon/019-brother.png" mode="aspectFit" class="action-icon" />
							<text class="action-text">添加兄弟姐妹</text>
						</view>
						<view class="action-item" @tap="viewDetail">
							<text class="action-text">点击查看资料</text>
							<view class="avatar">
								<image
									:src="selectedNode.rawData.userImgUrl || selectedNode.gender === 'male' ? require('@/static/defaultImg.png') : require('@/static/defaultW.png')"
									mode="aspectFill"
									:style="{ 'filter': !selectedNode.isLife || selectedNode.isLife == '0' ? 'grayscale(100%)' : 'grayscale(0%)' }">
								</image>
							</view>
							<text class="action-text">{{ selectedNode.name }}</text>
						</view>
						<view class="action-item" @tap="addSpouse">
							<image src="@/static/icon/fuqi.png" mode="aspectFit" class="action-icon" />
							<text class="action-text">添加配偶</text>
						</view>
					</view>

					<!-- 添加后代 -->
					<view class="action-row">
						<view class="action-item" @tap="addSon">
							<image src="@/static/icon/017-son.png" mode="aspectFit" class="action-icon" />
							<text class="action-text">添加儿子</text>
						</view>
						<view class="action-item" @tap="addDaughter">
							<image src="@/static/icon/044-daughter.png" mode="aspectFit" class="action-icon" />
							<text class="action-text">添加女儿</text>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		familyApi
	} from "@/api/index.js";

	import {
		parseDate
	} from "@/common/formatDate.js"

	import fgtree from "@/components/fg-tree/index.vue"
	export default {
		components: {
			fgtree
		},
		data() {
			return {
				currentUserInfo: uni.getStorageSync(this.$constants.USER_INFO), //当前登录用户
				parentTreeId: '', //族谱ID
				treeData: [],
				searchName: '',
				jsonarray: [],
				showActionPopup: false, // 控制弹出层显示
				selectedNode: null, // 当前选中的节点
				isRotated: false, // 旋转状态
			};
		},

		computed: {
			// 旋转图标
			rotationIcon() {
				return this.isRotated ? require('@/static/icon/zxz.png') : require('@/static/icon/fxz.png');
			},
		},

		onLoad(options) {
			if (options.pTree) {
				this.parentTreeId = options.pTree;
			}
			this.loadFamilyData();
		},

		methods: {
			async loadFamilyData() {
				try {
					const res = await familyApi._getSourceAll(this.parentTreeId);
					if (res && Array.isArray(res)) {
						this.jsonarray = res;
						this.treeData = this.convertToTreeData(this.jsonarray);
					} else {
						console.error('API返回数据格式不正确:', res);
						uni.showToast({
							title: '数据格式错误',
							icon: 'none'
						});
					}
				} catch (error) {
					console.error('加载族谱数据失败:', error);
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					});
				}
			},

			convertToTreeData(originalData) {
				const nodeMap = new Map();

				// 创建所有节点
				originalData.forEach(item => {
					const node = {
						id: item.userId,
						name: item.userName,
						relation: this.getRelation(item),
						isLife: item.isSurvival === '1',
						gender: item.userSex === '男' ? 'male' : 'female',
						sex: item.userSex,
						love: item.userLove,
						pName: item.pName,
						mName: item.mName,
						mateNames: item.list,
						age: this.calculateAge(item.isSurvival  === '1', item.userBirthDay, item.userDieDay),
						spouse: [],
						children: [],
						rawData: item,
						type: 'Y',
						dieDay: parseDate(item.userDieDay) ? parseDate(new Date(item.userDieDay)) : null,
						dAge: this.calcDieAge(item.userDieDay) | null,
						birthDate: parseDate(item.userBirthDay) ? parseDate(new Date(item.userBirthDay)) :
							null // 添加生日字段
					};
					nodeMap.set(item.userId, node);
				});

				// 建立配偶关系
				originalData.forEach(item => {
					const node = nodeMap.get(item.userId);

					// 处理配偶关系
					if (item.userMateId && item.userMateId.trim()) {
						const mateIds = item.userMateId.split(',').filter(id => id && id.trim() && id !== ',');
						mateIds.forEach(mateId => {
							const spouseNode = nodeMap.get(mateId);
							if (spouseNode && !node.spouse.some(s => s.id === spouseNode.id)) {
								node.spouse.push({
									id: spouseNode.id,
									name: spouseNode.name,
									relation: this.getSpouseRelation(spouseNode),
									isLife: spouseNode.isLife,
									gender: spouseNode.gender,
									age: this.calculateAge(spouseNode.isLife,spouseNode.birthDate,spouseNode.dieDay),
									mateNames: item.mateName,
									dieDay: parseDate(spouseNode.dieDay) ? parseDate(new Date(
										spouseNode.dieDay)) : null,
									dAge: this.calcDieAge(spouseNode.dieDay) | null,
									rawData: spouseNode.rawData,
									birthDate: parseDate(spouseNode.birthDate) ? parseDate(
										new Date(spouseNode.birthDate)) : null
								});
							}
						});
					}
				});

				// 建立亲子关系
				originalData.forEach(item => {
					const node = nodeMap.get(item.userId);

					// 查找父亲节点
					if (item.userPId) {
						const parentNode = nodeMap.get(item.userPId);
						if (parentNode && !parentNode.children.some(child => child.id === node.id)) {
							parentNode.children.push(node);
						}
					}

					// 查找母亲节点
					if (item.userMID) {
						const parentNode = nodeMap.get(item.userMID);
						if (parentNode && !parentNode.children.some(child => child.id === node.id)) {
							parentNode.children.push(node);
						}
					}
				});

				// 计算长幼顺序
				nodeMap.forEach(node => {
					if (node.children.length > 0) {
						// 对子女按生日排序
						node.children.sort((a, b) => {
							if (!a.birthDate && !b.birthDate) return 0;
							if (!a.birthDate) return 1;
							if (!b.birthDate) return -1;
							return a.birthDate - b.birthDate;
						});

						// 计算长幼顺序
						this.calculateBirthOrder(node.children);
					}
				});

				// 找到根节点
				const rootNode = this.findRootNode(nodeMap);
				return rootNode;
			},

			// 计算长幼顺序
			calculateBirthOrder(children) {
				let sonCount = 0;
				let daughterCount = 0;

				children.forEach(child => {
					if (child.gender === 'male') {
						sonCount++;
						child.relation = this.getSonBirthOrder(sonCount);
					} else {
						daughterCount++;
						child.relation = this.getDaughterBirthOrder(daughterCount);
					}
				});
			},

			// 获取儿子的长幼顺序
			getSonBirthOrder(order) {
				switch (order) {
					case 1:
						return '长子';
					case 2:
						return '次子';
					case 3:
						return '三子';
					case 4:
						return '四子';
					case 5:
						return '五子';
					case 6:
						return '六子';
					case 7:
						return '七子';
					case 8:
						return '八子';
					case 9:
						return '九子';
					default:
						return `${order}子`;
				}
			},

			// 获取女儿的长幼顺序
			getDaughterBirthOrder(order) {
				switch (order) {
					case 1:
						return '长女';
					case 2:
						return '次女';
					case 3:
						return '三女';
					case 4:
						return '四女';
					case 5:
						return '五女';
					case 6:
						return '六女';
					case 7:
						return '七女';
					case 8:
						return '八女';
					case 9:
						return '九女';
					default:
						return `${order}女`;
				}
			},

			// 根据数据计算关系
			getRelation(item) {
				switch (item.userLove) {
					case '1':
						return '始祖';
					case '2':
						return '第二代';
					case '3':
						return '第三代';
					case '4':
						return '第四代';
					default:
						return '成员';
				}
			},

			// 获取配偶关系
			getSpouseRelation(spouseNode) {
				return spouseNode.gender === 'male' ? '丈夫' : '妻子';
			},

			// 找到根节点
			findRootNode(nodeMap) {
				const potentialRoots = Array.from(nodeMap.values()).filter(node => {
					const originalNode = node.rawData;
					// 没有父母ID
					if (!originalNode.userPId && !originalNode.userMID) {
						// 如果有配偶，且配偶有父母，则不是根节点
						if (node.spouse.length > 0) {
							const hasSpouseWithParents = node.spouse.some(spouse => {
								const spouseOriginal = spouse.rawData;
								return spouseOriginal.userPId || spouseOriginal.userMID;
							});

							// 如果配偶有父母，则不是根节点
							// 如果配偶也没有父母，则可能是根节点
							return !hasSpouseWithParents;
						}
						// 没有配偶，可能是根节点
						return true;
					}
					return false;
				});

				// 如果找到了多个潜在根节点，尝试找到最合适的
				if (potentialRoots.length > 0) {
					// 优先选择有子女的节点作为根节点
					const rootWithChildren = potentialRoots.find(node => node.children.length > 0);
					if (rootWithChildren) {
						return [rootWithChildren];
					}

					// 如果没有有子女的节点，选择第一个
					return [potentialRoots[0]];
				}

				// 如果没有找到合适的根节点，返回空数组
				console.warn('未找到合适的根节点');
				return [];
			},
			// 计算死亡距今年数
			calcDieAge(dieDay) {
				if (!dieDay) return '未知';
				const birth = new Date(dieDay);
				const now = new Date();
				let age = now.getFullYear() - birth.getFullYear();
				const monthDiff = now.getMonth() - birth.getMonth();
				if (monthDiff < 0 || (monthDiff === 0 && now.getDate() < birth.getDate())) {
					age--;
				}
				return age;
			},
			// 计算年龄
			calculateAge(isLife, birthDay, dieDay) {
				// 参数验证
				if (!birthDay) return '未知';

				try {
					const birth = new Date(birthDay);
					if (isNaN(birth.getTime())) return '未知';

					let endDate;
					if (isLife) {
						endDate = new Date();
					} else {
						if (!dieDay) return '未知';
						endDate = new Date(dieDay);
						if (isNaN(endDate.getTime())) return '未知';
					}

					// 确保结束日期不小于出生日期
					if (endDate < birth) return '未知';

					let age = endDate.getFullYear() - birth.getFullYear();
					const monthDiff = endDate.getMonth() - birth.getMonth();

					if (monthDiff < 0 || (monthDiff === 0 && endDate.getDate() < birth.getDate())) {
						age--;
					}

					// 年龄不能为负数
					return age < 0 ? 0 : age;

				} catch (error) {
					console.error('计算年龄出错:', error);
					return '未知';
				}
			},

			// 节点点击事件
			handleNodeClick(node) {
				this.selectedNode = node;
				this.showActionPopup = true;
			},
			// 关闭弹出层
			closePopup() {
				this.showActionPopup = false;
				this.selectedNode = null;
			},

			// 添加父母
			addParent() {
				
				
				if (!this.showSpouseSelection()) {
					return
				};

				if (this.selectedNode.rawData.userMID === null && this.selectedNode.rawData.userPId === null) {
					uni.showToast({
						title: '暂未开放',
						icon: 'none'
					});
					return
					uni.showToast({
						title: `为 ${this.selectedNode.name} 添加父母`,
						icon: 'none'
					});

					const memberStr = encodeURIComponent(JSON.stringify(this.selectedNode));
					uni.navigateTo({
						url: `/pages/family/add-member/add-member?type=parent&member=${memberStr}`
					});
					this.closePopup();
				} else {
					uni.showToast({
						title: ` ${this.selectedNode.name} 已有父母`,
						icon: 'none'
					});
				}

			},

			// 添加兄弟姐妹
			addSibling() {
				if (!this.showSpouseSelection()) {
					return
				};
				if (this.selectedNode.rawData.userPId === null && this.selectedNode.rawData.userPId === null) {
					uni.showToast({
						title: ` ${this.selectedNode.name} 已是始祖，无法添加`,
						icon: 'none'
					});
					return
				}

				uni.showToast({
					title: `为 ${this.selectedNode.name} 添加兄弟姐妹`,
					icon: 'none'
				});

				const memberStr = encodeURIComponent(JSON.stringify(this.selectedNode));
				uni.navigateTo({
					url: `/pages/family/add-member/add-member?type=sibling&member=${memberStr}`
				});

				this.closePopup();
			},

			// 查看详情
			viewDetail() {
				if (!this.selectedNode) return;
				const nodeStr = encodeURIComponent(JSON.stringify(this.selectedNode));
				uni.navigateTo({
					url: `/pages/family/member-detail/member-detail?node=${nodeStr}`
				});
				this.closePopup();
			},

			// 添加配偶
			addSpouse() {
				if (!this.showSpouseSelection()) {
					return
				};
				if (this.selectedNode.spouse.length > 0) {
					uni.showToast({
						title: ` ${this.selectedNode.name} 已有配偶`,
						icon: 'none'
					});
					return
				}
				uni.showToast({
					title: `为 ${this.selectedNode.name} 添加配偶`,
					icon: 'none'
				});

				const memberStr = encodeURIComponent(JSON.stringify(this.selectedNode));
				uni.navigateTo({
					url: `/pages/family/add-member/add-member?type=spouse&member=${memberStr}`
				});

				this.closePopup();
			},

			// 添加儿子
			addSon() {
				if (!this.showSpouseSelection()) {
					return
				};
				uni.showToast({
					title: `为 ${this.selectedNode.name} 添加儿子`,
					icon: 'none'
				});

				this.addChild('male');
			},

			// 添加女儿
			addDaughter() {
				if (!this.showSpouseSelection()) {
					return
				};
				uni.showToast({
					title: `为 ${this.selectedNode.name} 添加女儿`,
					icon: 'none'
				});

				this.addChild('female');
			},

			// 添加子女的通用方法
			addChild(gender) {
				if (this.selectedNode.spouse.length === 0) {
					uni.showToast({
						title: `${this.selectedNode.name} 还没成家，请先添加配偶`,
						icon: 'none'
					});

					return
				}
				const memberStr = encodeURIComponent(JSON.stringify(this.selectedNode));
				uni.navigateTo({
					url: `/pages/family/add-member/add-member?type=child&member=${memberStr}&gender=${gender}`
				});
				this.closePopup();
			},
			showSpouseSelection() {
				if (this.selectedNode.type === "Y") {
					return true
				} else {
					uni.showToast({
						title: `${this.selectedNode.name} 没权限添加`,
						icon: 'none'
					});
					return false
				}
			},
			// 查找家族成员
			searchByName() {
				if (!this.searchName.trim()) {
					uni.showToast({
						title: '请输入搜索内容',
						icon: 'none'
					});
					return;
				}

				// 在树中搜索
				const found = this.searchInTree(this.treeData, this.searchName);
				if (found) {
					const nodeStr = encodeURIComponent(JSON.stringify(found));
					uni.navigateTo({
						url: `/pages/family/member-detail/member-detail?node=${nodeStr}`
					});
				} else {
					uni.showToast({
						title: '未找到该成员',
						icon: 'none'
					});
				}
			},

			searchInTree(nodes, name) {
				for (const node of nodes) {
					if (node.name && node.name.includes(name)) {
						return node;
					}
					if (node.children && node.children.length > 0) {
						const found = this.searchInTree(node.children, name);
						if (found) return found;
					}
				}
				return null;
			},
			// 复位
			zoomToFit() {
				if (this.$refs.familyTree) {
					this.$refs.familyTree.refreshView();
				}
			},

			// 切换横竖屏
			toggleOrientation() {
				if (this.$refs.familyTree) {
					this.$refs.familyTree.rotateView();
					this.isRotated = !this.isRotated;
				}
			}
		}
	};
</script>

<style scoped>
	.family-container {
		padding: 20rpx;
		height: 100vh;
		background: #fff;
		display: flex;
		flex-direction: column;
		position: relative;
	}

	.control-bar {
		margin-bottom: 20rpx;
		padding: 20rpx;
		border-radius: 10rpx;
		flex-shrink: 0;
		border: 1rpx solid rgba(30, 41, 59, 0.5);
	}

	.search-section {
		display: flex;
		align-items: center;
	}

	.search-input {
		flex: 1;
		background: rgba(255, 255, 255, 0.1);
		border: 1rpx solid rgba(56, 189, 248, 0.2);
		border-radius: 30rpx;
		padding: 20rpx 30rpx;
		color: #000;
		margin-right: 20rpx;
		font-size: 28rpx;
	}

	.search-input::placeholder {
		color: #94a3b8;
	}

	.action-btn {
		background: #55aaff;
		color: white;
		border: none;
		border-radius: 30rpx;
		padding: 10rpx 30rpx;
		margin-left: 10rpx;
		font-size: 25rpx;
	}

	.genealogy-panel {
		flex: 1;
		background: #fff;
		border-radius: 12rpx;
		border: 1rpx solid rgba(30, 41, 59, 0.5);
		overflow: hidden;
	}

	.family-tree {
		background: #fff;
		height: 100%;
	}

	/* 悬浮按钮组样式 */
	.floating-buttons {
		position: fixed;
		right: 30rpx;
		top: 50%;
		transform: translateY(-50%);
		display: flex;
		flex-direction: column;
		gap: 30rpx;
		z-index: 998;
	}

	.floating-btn {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		width: 60rpx;
		height: 60rpx;
		background: #fff;
		border-radius: 60rpx;
		box-shadow: 0 8rpx 25rpx rgba(85, 170, 255, 0.4);
		transition: all 0.3s ease;
	}

	.floating-btn:active {
		background: #fff;
		;
		transform: scale(0.95);
		box-shadow: 0 4rpx 15rpx rgba(85, 170, 255, 0.6);
	}

	.btn-icon {
		width: 40rpx;
		height: 40rpx;
	}

	/* 操作弹出层样式 */
	.action-popup {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		display: flex;
		align-items: center;
		justify-content: center;
		z-index: 999;
	}

	.popup-content {
		background: white;
		border-radius: 20rpx;
		width: 90%;
		max-width: 600rpx;
		padding: 40rpx 30rpx;
		box-shadow: 0 10rpx 40rpx rgba(0, 0, 0, 0.2);
	}

	.popup-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 40rpx;
		padding-bottom: 20rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.popup-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
	}

	.popup-close {
		font-size: 48rpx;
		color: #999;
		width: 60rpx;
		height: 60rpx;
		text-align: center;
		line-height: 60rpx;
	}

	.action-grid {
		display: flex;
		flex-direction: column;
		gap: 30rpx;
	}

	.action-row {
		display: flex;
		justify-content: space-around;
		gap: 20rpx;
	}

	.action-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 30rpx 20rpx;
		border-radius: 16rpx;
		background: #f8f9fa;
		flex: 1;
		min-width: 0;
		transition: all 0.3s ease;
	}

	.avatar {
		width: 120rpx;
		height: 120rpx;
		margin: 14rpx auto 0;
		border-radius: 100rpx;
		overflow: hidden;
		border: 1px solid #f1f1f1;
	}

	.avatar image {
		width: 100%;
		height: 100%;
	}

	.action-item:active {
		background: #e9ecef;
		transform: scale(0.95);
	}

	.action-icon {
		font-size: 60rpx;
		margin-bottom: 20rpx;
		height: 90rpx;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.action-text {
		font-size: 24rpx;
		color: #333;
		text-align: center;
		line-height: 1.4;
	}

	/* 针对中间行有3个元素的情况 */
	.action-row:nth-child(2) .action-item {
		flex: 1;
	}

</style>