<template>
	<view>
		<movable-area :style="{
				'width': `${width}px`,
				'height': `${height}px`,
				'marginLeft': `-${width / 2 + 180}px`,
				'marginTop': rotateState ? `-${height / 2 + 180 - viewHeight }px` : `-${height / 2 + 180}px`,
			}">
			<!-- 具体参数释义请查看标签官方文档 -->
			<movable-view :scale-value="scaleValue" :x="movableX" :y="movableY" :scale="scale" :disabled="disabled"
				:friction="friction" :direction="direction" inertia out-of-bounds @change="changeView"
				@scale="changeScale">
				<view :class="[ rotateState ? 'rotated-view' : '' ]">
					<view v-for="item in list" :key="item.id">
						<view class="item_box">
							<!-- 始祖节点 -->
							<view class="member_item">
								<!-- 信息卡片 -->
								<view class="member_card" :class="getGenderClass(item)" @click="itemClick(item)">
									<!-- 头像 可根据数据的性别或者其他自定义字段扩展差异化头像 isLife：是否建在，死亡成员灰度渲染 -->
									<view class="avatar">
										<image :src="item.avatar || getGenderClass(item) === 'male' ? require('@/static/defaultImg.png') : require('@/static/defaultW.png')"
											:style="{ 'filter': !item.isLife ? 'grayscale(100%)' : 'grayscale(0%)'}">
										</image>
									</view>
									<!-- 关系 -->
									<view class="level">
										<view class="level_label">{{ item.relation }}</view>
									</view>
									<!-- 姓名 -->
									<view class="name">{{ item.name }}</view>
									<!-- 是否建在标签 -->
									<view class="die_tag" v-if="!item.isLife || item.isLife == '0'">去世</view>
								</view>
								<!-- 下级出发线 -->
								<view class="go_line" v-if="item.children && item.children.length"></view>
								<!-- 折叠按钮 -->
								<view class="fold_btn" v-if="item.children && item.children.length"
									@tap="foldItem(item)">
									<!-- #ifdef MP-WEIXIN -->
									<image src="@/static/add.png" class="icon" v-if="item.fold"></image>
									<image src="@/static/minus.png" class="icon" v-else></image>
									<!-- #endif -->
									<!-- #ifndef MP-WEIXIN -->
									<image src="./static/add.png" class="icon" v-if="item.fold"></image>
									<image src="./static/minus.png" class="icon" v-else></image>
									<!-- #endif -->
								</view>
							</view>
							<!-- 夫妻连线 -->
							<view class="spouse_connector" v-if="item.spouse && item.spouse.length"></view>

							<!-- 始祖配偶节点 -->
							<view v-if="item.spouse && item.spouse.length" class="member_item">
								<view class="member_card" :class="getGenderClass(sItem)" v-for="sItem in item.spouse"
									:key="sItem.key" @tap="() => { itemClick(sItem) }">
									<view class="avatar">
										<image
											:src="sItem.avatar || getGenderClass(sItem) === 'male' ? require('@/static/defaultImg.png') : require('@/static/defaultW.png')"
											mode="aspectFill"
											:style="{ 'filter': !sItem.isLife || sItem.isLife == '0' ? 'grayscale(100%)' : 'grayscale(0%)' }">
										</image>
									</view>
									<view class="level">
										<view class="level_label">{{ sItem.relation }}</view>
									</view>
									<view class="name">{{ sItem.name }}</view>
									<view class="die_tag" v-if="!sItem.isLife || sItem.isLife == '0'">去世</view>
								</view>
							</view>
						</view>
						<!-- 后代节点 -->
						<view v-if="item.children && item.children.length && !item.fold">
							<Item :itemData="item.children" @check="itemClick"></Item>
						</view>
					</view>
				</view>
			</movable-view>
		</movable-area>
	</view>
</template>

<script>
	import Item from "./components/item.vue"
	export default {
		components: {
			Item
		},
		props: {
			width: { // 拖动容器宽度 建议大于2倍的设备可视宽度
				type: Number,
				default: 2000
			},
			height: { // 拖动容器高度 建议大于2倍的设备可视高度
				type: Number,
				default: 2000
			},
			direction: { // 拖动支持方向，同movable-view原文档
				type: String,
				default: 'all'
			},
			friction: { // 摩擦系数，同movable-view原文档
				type: Number,
				default: 4
			},
			disabled: { // 是否禁用
				type: Boolean,
				default: false
			},
			scale: { // 是否支持双指缩放
				type: Boolean,
				default: true
			},
			list: { // 节点列表数据
				type: Array
			},
		},
		data() {
			return {
				movableX: 1200, // 拖动容器X偏移值
				movableY: 1200, // 拖动容器Y偏移值
				scaleValue: 0.9, // 缩放值
				rotateState: false, // 旋转状态
				viewHeight: 0, // 设备视口高度
			}
		},
		watch: {},
		computed: {},
		methods: {
			// 根据性别获取样式类
			getGenderClass(item) {
				return item.gender;
			},
			// 视角位置变化
			changeView(e) {
				if (e.detail.source == 'friction') {
					this.movableX = e.detail.x
					this.movableY = e.detail.y
				}
			},
			// 视角缩放变化
			changeScale(e) {
				if (e.detail.source == 'friction') {
					this.scaleValue = e.detail.scale
				}
			},
			// 视角复位
			refreshView() {
				this.movableX = (this.width / 2) + 200
				this.movableY = (this.height / 2) + 200
				this.scaleValue = 0.9
			},
			// 视角旋转
			rotateView() {
				this.rotateState = !this.rotateState
			},
			// 折叠
			foldItem(item) {
				this.$set(item, 'fold', item.fold ? false : true)
			},
			// 点击项
			itemClick(item) {
				this.$emit('check', item)
			},
		},
		created() {
			// 初始化偏移位置
			this.movableX = (this.width / 2) + 200
			this.movableY = (this.height / 2) + 200
			// 获取设备视口高度
			this.$nextTick(() => {
				const res = uni.getSystemInfoSync();
				// #ifdef APP-PLUS
				this.viewHeight = res.windowHeight - 100;
				// #endif
				// #ifdef H5
				this.viewHeight = res.windowHeight - 50;
				// #endif
				// #ifdef MP-WEIXIN
				this.viewHeight = res.windowHeight - 100;
				// #endif
			})
		}
	}
</script>

<style scoped lang="scss">
	.rotated-view {
		transform: rotate(-90deg);
		transform-origin: left top;
	}

	.item_box {
		display: flex;
	}

	.member_card {
		width: 170rpx;
		padding: 35rpx 0 25rpx;
		border: 1px solid #dcdcdc;
		border-radius: 10rpx;
		margin: 0 10rpx;
		position: relative;
		background-color: #FFF;
		box-shadow: 0 5rpx 10rpx rgba(0, 0, 0, 0.1);

	}

	/* 男性样式 - 蓝色边框 */
	.member_card.male {
		border-color: #1890ff;
		box-shadow: 0 5rpx 15rpx rgba(24, 144, 255, 0.2);
	}

	/* 女性样式 - 粉色边框 */
	.member_card.female {
		border-color: #ff4d94;
		box-shadow: 0 5rpx 15rpx rgba(255, 77, 148, 0.2);
	}

	.die_tag {
		position: absolute;
		top: 0;
		left: 0;
		background-color: #dcdcdc;
		color: #666;
		width: 80rpx;
		height: 45rpx;
		line-height: 45rpx;
		border-radius: 5rpx;
		text-align: center;
		font-size: 26rpx;
	}

	.avatar {
		width: 120rpx;
		height: 120rpx;
		margin: 14rpx auto 0;
		border-radius: 100rpx;
		overflow: hidden;
		border: 1px solid #f1f1f1;

		image {
			width: 100%;
			height: 100%;
		}
	}

	.level {
		position: relative;
		z-index: 924;
		margin-top: -15rpx;
		display: flex;
		justify-content: center;

		.level_label {
			color: #333;
			font-size: 24rpx;
			padding: 0 20rpx;
			background-color: #F4F0ED;
			border-radius: 100rpx;
		}
	}

	.name {
		text-align: center;
		font-size: 26rpx;
		color: #333;
		margin: 15rpx 0 10rpx;
	}

	.go_line {
		height: 40rpx;
		width: 2rpx;
		background-color: #ff0000;
		margin: 0 auto;
	}

	.fold_btn {
		width: 50rpx;
		margin: 0 auto;
		background-color: #FFF;
		border-radius: 100rpx;
		position: relative;
		z-index: 999;

		.icon {
			width: 50rpx;
			height: 50rpx;
		}
	}
</style>