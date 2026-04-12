<template>
	<view class="container">
		<!-- 大转盘区域 -->
		<view class="wheel-container">
			<view class="wheel-wrapper">
				<l-dialer dialStyle="borderColor: '#00ff00'" size="350" :prizeList="mockPrizes" @click="onClick"
					@done="onDone" ref="dialer">
					<image class="wheel-bg" slot="border" src="@/static/zp.png" />
				</l-dialer>
			</view>
		</view>

		<!-- 记录区域 -->
		<view class="record-container">
			<!-- 标题与统计 -->
			<view class="record-header">
				<view class="record-meta">
					<text class="record-title">我的抽奖记录</text>
					<button class="btn warning" @tap="clearAll">清空记录</button>
				</view>
				<view class="stats">
					<view class="stat-item">
						<text class="stat-number">{{ total }}</text>
						<text class="stat-label">次参与</text>
					</view>
					<view class="stat-item">
						<text class="stat-number">{{ winCount }}</text>
						<text class="stat-label">次中奖</text>
					</view>
				</view>
			</view>

			<!-- 记录列表 -->
			<scroll-view class="scroll-wrapper" scroll-y="true" :scroll-top="scrollTop" @scroll="onScroll">
				<view class="record-list">
					<view v-for="(item, index) in records" :key="item.id" class="record-item"
						:class="[item.status, { 'new-item': item.isNew }]" @touchstart="hoverIndex = index"
						@touchend="hoverIndex = -1">
						<!-- 时间轴圆点 -->
						<view class="timeline-dot"></view>

						<!-- 记录内容 v-if="item.status === 'unclaimed' && hoverIndex === index"-->
						<view class="record-content">
							<view class="record-main">
								<text class="prize-name">{{ item.name }}</text>
								<text class="record-time">{{ formatTime(item.createTime) }}</text>
							</view>
							<view class="record-meta">
								<view class="button-container" v-if="item.status === 'unclaimed'" >
									<button class="claim-btn" @tap="claimPrize(index)">{{ statusMap[item.status] }}</button>
								</view>
								<text class="status-tag" v-else>{{ statusMap[item.status] }}</text>
							</view>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script>
	import ldialer from '@/uni_modules/lime-dialer/components/l-dialer/l-dialer.vue'
	export default {
		components: {
			ldialer
		},
		data() {
			return {
				hoverIndex: -1,
				scrollTop: 0,
				records: [],
				statusMap: {
					claimed: "已领取",
					unclaimed: "待领取",
					none: "未中奖"
				},
				mockPrizes: [],
			};
		},
		computed: {
			total() {
				return this.records.length;
			},
			winCount() {
				return this.records.filter(item => item.status !== 'none').length;
			}
		},
		onLoad() {
			this.init();
		},
		onReady() {
			this.scrollToBottom();
		},
		methods: {
			// 清空列表
			clearAll() {
				if (this.records.length === 0) {
					uni.showToast({
						title: '记录已为空',
						icon: 'none'
					});
					return;
				}

				uni.showModal({
					title: '确认清空',
					content: '确定要清空所有抽奖记录吗？此操作不可恢复',
					success: (res) => {
						if (res.confirm) {
							this.records = [];
							this.saveRecords()
							uni.showToast({
								title: '已清空',
								icon: 'success'
							});
						}
					}
				});
			},
			// 根据权重计算中奖索引
			getPrizeIndexByWeight() {
				// 计算总权重
				const totalWeight = this.mockPrizes.reduce((sum, item) => sum + Number(item.weight), 0);

				// 生成随机数
				let random = Math.random() * totalWeight;

				// 根据权重选择奖品
				for (let i = 0; i < this.mockPrizes.length; i++) {
					random -= Number(this.mockPrizes[i].weight);
					if (random <= 0) {
						return i;
					}
				}

				// 默认返回最后一个
				return this.mockPrizes.length - 1;
			},
			onDone(index) {
				const prize = this.mockPrizes[index]
				this.endLuckyWheel(prize)
			},
			onClick() {
				// 根据权重获取中奖索引
				const prizeIndex = this.getPrizeIndexByWeight();
				this.$refs.dialer.run(prizeIndex)
			},

			// 生成不重复且相邻不相似的随机颜色
			generateDistinctColors(count) {
				const colors = [];
				const usedColors = new Set();

				// 基础颜色集 - 这些颜色在色相环上分布均匀
				const baseColors = [
					'#FF6B6B', '#4ECDC4', '#45B7D1', '#96CEB4', '#FFEAA7',
					'#DDA0DD', '#98D8C8', '#F7DC6F', '#BB8FCE', '#85C1E9',
					'#F8C471', '#82E0AA', '#F1948A', '#85C1E9', '#D7BDE2',
					'#F9E79F', '#A9DFBF', '#E8DAEF', '#FAD7A0', '#AED6F1'
				];

				// 如果需要的颜色数量不超过基础颜色集，直接使用并打乱
				if (count <= baseColors.length) {
					const shuffled = [...baseColors].sort(() => Math.random() - 0.5);
					return shuffled.slice(0, count);
				}

				// 如果需要更多颜色，使用HSL颜色模型生成
				for (let i = 0; i < count; i++) {
					let color;
					let attempts = 0;
					const maxAttempts = 100;

					do {
						// 使用HSL确保颜色分布均匀
						const hue = (i * 137.5 + Math.random() * 30) % 360; // 137.5度是黄金角度，确保颜色分布均匀
						const saturation = 70 + Math.random() * 20; // 70-90% 饱和度
						const lightness = 50 + Math.random() * 20; // 50-70% 亮度

						color = this.hslToHex(hue, saturation, lightness);
						attempts++;

						// 确保颜色不重复且与相邻颜色不相似
						const isSimilarToAdjacent = colors.length > 0 &&
							this.colorDistance(color, colors[colors.length - 1]) < 150;

						if (attempts > maxAttempts || (!usedColors.has(color) && !isSimilarToAdjacent)) {
							break;
						}
					} while (true);

					usedColors.add(color);
					colors.push(color);
				}

				return colors;
			},

			// 计算两个颜色之间的欧几里得距离
			colorDistance(color1, color2) {
				const rgb1 = this.hexToRgb(color1);
				const rgb2 = this.hexToRgb(color2);

				return Math.sqrt(
					Math.pow(rgb1.r - rgb2.r, 2) +
					Math.pow(rgb1.g - rgb2.g, 2) +
					Math.pow(rgb1.b - rgb2.b, 2)
				);
			},

			// HSL转Hex
			hslToHex(h, s, l) {
				h /= 360;
				s /= 100;
				l /= 100;
				let r, g, b;
				if (s === 0) {
					r = g = b = l;
				} else {
					const hue2rgb = (p, q, t) => {
						if (t < 0) t += 1;
						if (t > 1) t -= 1;
						if (t < 1 / 6) return p + (q - p) * 6 * t;
						if (t < 1 / 2) return q;
						if (t < 2 / 3) return p + (q - p) * (2 / 3 - t) * 6;
						return p;
					};
					const q = l < 0.5 ? l * (1 + s) : l + s - l * s;
					const p = 2 * l - q;
					r = hue2rgb(p, q, h + 1 / 3);
					g = hue2rgb(p, q, h);
					b = hue2rgb(p, q, h - 1 / 3);
				}
				const toHex = x => {
					const hex = Math.round(x * 255).toString(16);
					return hex.length === 1 ? '0' + hex : hex;
				};
				return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
			},

			// Hex转RGB
			hexToRgb(hex) {
				const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
				return result ? {
					r: parseInt(result[1], 16),
					g: parseInt(result[2], 16),
					b: parseInt(result[3], 16)
				} : {
					r: 0,
					g: 0,
					b: 0
				};
			},

			// 滚动到底部
			scrollToBottom() {
				this.$nextTick(() => {
					setTimeout(() => {
						this.scrollTop = 99999;
					}, 100);
				});
			},

			onScroll(e) {
				// 可以在这里处理滚动事件
			},

			// 领取奖品 
			claimPrize(index) {
				this.records[index].status = 'claimed';
				this.$set(this.records, index, {
					...this.records[index]
				});

				uni.showToast({
					title: '领取成功',
					icon: 'success'
				});
			},

			// 初始化
			init() {
				// 获取奖品
				const data = uni.getStorageSync('prizeConfig');
				if (data) {
					this.mockPrizes = JSON.parse(data);

					// 为每个奖品生成不重复且相邻不相似的随机颜色
					const colors = this.generateDistinctColors(this.mockPrizes.length);

					// 为每个奖品分配颜色
					this.mockPrizes = this.mockPrizes.map((item, index) => {
						return {
							...item,
							Color: colors[index]
						};
					});
				}

				// 加载历史记录
				this.loadRecords();
			},

			// 加载记录
			loadRecords() {
				try {
					const records = uni.getStorageSync('lotteryRecords');
					if (records) {
						this.records = JSON.parse(records);
					}
				} catch (e) {
					console.error('加载记录失败:', e);
				}
			},

			// 保存记录
			saveRecords() {
				try {
					uni.setStorageSync('lotteryRecords', JSON.stringify(this.records));
				} catch (e) {
					console.error('保存记录失败:', e);
				}
			},

			// 格式化时间
			formatTime(timestamp) {
				if (!timestamp) return '';
				const date = new Date(timestamp);
				const year = date.getFullYear();
				const month = (date.getMonth() + 1).toString().padStart(2, '0');
				const day = date.getDate().toString().padStart(2, '0');
				const hour = date.getHours().toString().padStart(2, '0');
				const minute = date.getMinutes().toString().padStart(2, '0');
				return `${year}-${month}-${day} ${hour}:${minute}`;
			},

			// 抽奖结束
			endLuckyWheel(prize) {
				const newRecord = {
					id: Date.now().toString(),
					name: prize.name,
					createTime: new Date(),
					status: prize.title === "谢谢参与" ? "none" : "unclaimed",
					isNew: true
				};

				this.records.push(newRecord);
				this.saveRecords();

				// 移除新项目标记
				setTimeout(() => {
					const index = this.records.findIndex(item => item.id === newRecord.id);
					if (index !== -1) {
						this.records[index].isNew = false;
						this.$set(this.records, index, {
							...this.records[index]
						});
					}
				}, 1000);

				// 滚动到底部显示新记录
				this.scrollToBottom();
			}
		}
	};
</script>

<style lang="scss" scoped>
	.container {
		display: flex;
		flex-direction: column;
		min-height: 100vh;
		background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
		padding: 20rpx;
	}

	.wheel-container {
		display: flex;
		justify-content: center;
		margin-bottom: 40rpx;
	}

	.wheel-wrapper {
		width: 650rpx;
		height: 650rpx;
		position: relative;
		transform-origin: center center;
	}

	.wheel-bg {
		width: 100%;
		height: 100%;
	}

	.record-container {
		flex: 1;
		background: #eeeeee;
		border-radius: 20rpx;
		box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.1);
		padding: 40rpx;
		display: flex;
		flex-direction: column;
	}

	.record-header {
		border-bottom: 2rpx solid #f0f2f5;
		padding-bottom: 20rpx;
		margin-bottom: 30rpx;
	}

	.record-title {
		color: #2c3e50;
		font-size: 36rpx;
		font-weight: bold;
		margin-bottom: 20rpx;
		display: block;
	}

	.btn {
		// margin: 0 20rpx;
		border-radius: 18rpx;
		color: #fff;
		font-size: 25rpx;
		text-align: center;
		
		&:active {
			transform: scale(0.95);
		}
	}

	.warning {
		background-color: #FF9500;
	}

	.stats {
		display: flex;
		gap: 40rpx;
	}

	.stat-item {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.stat-number {
		font-weight: 700;
		color: #2c3e50;
		font-size: 32rpx;
	}

	.stat-label {
		color: #7f8c8d;
		font-size: 28rpx;
	}

	.scroll-wrapper {
		flex: 1;
		overflow: hidden;
	}

	.record-list {
		padding-left: 48rpx;
		padding-right: 24rpx;
	}

	.record-item {
		position: relative;
		padding: 30rpx 0;
		border-bottom: 1rpx solid #f0f2f5;
		transition: all 0.3s ease;
	}

	.record-item:last-child {
		border-bottom: none;
	}

	.timeline-dot {
		position: absolute;
		left: -36rpx;
		top: 50%;
		transform: translateY(-50%);
		width: 24rpx;
		height: 24rpx;
		background: #3498db;
		border-radius: 50%;
		border: 2rpx solid white;
		box-shadow: 0 2rpx 6rpx rgba(52, 152, 219, 0.3);
	}

	.record-content {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.record-main {
		flex: 1;
	}

	.prize-name {
		font-weight: 500;
		color: #2c3e50;
		display: block;
		margin-bottom: 6rpx;
		font-size: 30rpx;
	}

	.record-time {
		font-size: 24rpx;
		color: #95a5a6;
	}

	.record-meta {
		display: flex;
		align-items: center;
		gap: 20rpx;
	}

	.status-tag {
		font-size: 24rpx;
		padding: 8rpx 16rpx;
		border-radius: 24rpx;
	}

	/* 状态颜色 */
	.claimed .status-tag {
		background: #e8f5e9;
		color: #2ecc71;
	}

	.unclaimed .status-tag {
		background: #fff3e0;
		color: #f39c12;
	}

	.none .status-tag {
		background: #f5f6fa;
		color: #95a5a6;
	}

	.button-container {
		display: flex;
		align-items: center;
	}

	.claim-btn {
		background: #3498db;
		color: white;
		border: none;
		padding: 12rpx 32rpx;
		border-radius: 40rpx;
		font-size: 24rpx;
		transition: all 0.3s;
	}

	.claim-btn:active {
		background: #2980b9;
		transform: translateY(-1px);
	}

	@keyframes slideIn {
		from {
			opacity: 0;
			transform: translateX(40rpx);
		}

		to {
			opacity: 1;
			transform: translateX(0);
		}
	}

	/* 平板和桌面端布局 */
	@media (min-width: 768px) {
		.container {
			flex-direction: row;
			padding: 40rpx;
		}

		.wheel-container {
			flex: 1;
			margin-bottom: 0;
			margin-right: 40rpx;
			align-items: center;
			justify-content: center;
		}

		.wheel-wrapper {
			width: 500rpx;
			height: 500rpx;
		}

		.record-container {
			width: 40%;
			flex: none;
		}
	}

	/* 桌面端大屏幕布局 */
	@media (min-width: 1200px) {
		.wheel-wrapper {
			width: 600rpx;
			height: 600rpx;
		}

		.record-container {
			width: 35%;
		}
	}
</style>