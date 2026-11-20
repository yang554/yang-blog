<template>
	<view class="member_container">
		<view v-for="(item, index) in itemData" :key="item.id">
			<!-- 顶部横线线条 -->
			<view class="top_line" :style="{ width: index == itemData.length - 1 ? '0' : '100%' }"></view>
			<view class="item_box">
				<!-- 成员节点 -->
				<view class="member_item">
					<!-- 子级入口竖线 -->
					<view class="top_x_line"></view>
					<view class="member_card" :class="getGenderClass(item)" @tap="itemClick(item)">
						<view class="flex flex-center">
							<!-- 头像  -->
							<view class="avatar">
								<image :src="item.avatar || getGenderClass(item) === 'male' ? require('@/static/defaultImg.png') : require('@/static/defaultW.png')" mode="aspectFill"
									:style="{ 'filter': !item.isLife || item.isLife == '0' ? 'grayscale(100%)' : 'grayscale(0%)' }">
								</image>
							</view>
						</view>
						<view class="level">
							<view class="level_label">{{ item.relation }}</view>
						</view>
						<view class="name">{{ item.name }}</view>
						<view class="die_tag" v-if="!item.isLife || item.isLife == '0'">去世</view>
					</view>
					<!-- 下级出发线 -->
					<view class="go_line" v-if="item.children && item.children.length"></view>
					<!-- 折叠按钮 -->
					<view class="fold_btn" v-if="item.children && item.children.length" @tap="foldItem(item)">
						<!-- #ifdef MP-WEIXIN -->
						<image src="@/static/add.png" class="icon" v-if="item.fold"></image>
						<image src="@/static/minus.png" class="icon" v-else></image>
						<!-- #endif -->
						<!-- #ifndef MP-WEIXIN -->
						<image src="../static/add.png" class="icon" v-if="item.fold"></image>
						<image src="../static/minus.png" class="icon" v-else></image>
						<!-- #endif -->
					</view>
				</view>

				<!-- 夫妻连线 -->
				<view class="spouse_connector" v-if="item.spouse && item.spouse.length"></view>

				<!-- 成员配偶节点 -->
				<view v-if="item.spouse && item.spouse.length" class="member_item">
					<view class="top_x_line_no_bg"></view>
					<view class="member_card" :class="getGenderClass(sItem)" v-for="sItem in item.spouse"
						:key="sItem.id" @tap="() => { itemClick(sItem) }">
						<view class="flex flex-center">
							<view class="avatar">
								<image :src="sItem.avatar || getGenderClass(sItem) === 'male' ? require('@/static/defaultImg.png') : require('@/static/defaultW.png')" mode="aspectFill"
									:style="{ 'filter': !sItem.isLife || sItem.isLife == '0' ? 'grayscale(100%)' : 'grayscale(0%)', }">
								</image>
							</view>
						</view>
						<view class="level">
							<view class="level_label">{{ sItem.relation }}</view>
						</view>
						<view class="name">{{ sItem.name }}</view>
						<view class="die_tag" v-if="!sItem.isLife || sItem.isLife == '0'">去世</view>
					</view>
				</view>
			</view>
			<!-- 下级节点 -->
			<view v-if="item.children && item.children.length && !item.fold">
				<Item :itemData="item.children" @check="itemClick"></Item>
			</view>
		</view>
	</view>
</template>

<script>
	import Item from "./item.vue"
	export default {
		name: 'Item',
		components: {
			Item
		},
		props: {
			itemData: {
				type: Array
			},
		},
		data() {
			return {}
		},
		watch: {},
		computed: {},
		methods: {
			foldItem(item) {
				this.$set(item, 'fold', item.fold ? false : true)
				this.$forceUpdate()
			},
			itemClick(item) {
				this.$emit('check', item)
			},
			// 根据性别获取样式类
			getGenderClass(item) {
				return item.gender;
			}
		},
		mounted() {}
	}
</script>

<style scoped lang="less">
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

	.top_line {
		height: 2rpx;
		background-color: #ff0000;
		margin-left: 95rpx;
		width: 100%;
		margin-top: -28rpx;
		border-top-right-radius: 10rpx;
	}

	.member_container {
		display: flex;
	}

	.item_box {
		display: flex;
		position: relative;
	}

	.top_x_line {
		height: 90rpx;
		width: 2rpx;
		background-color: #ff0000;
		margin: 0 auto;
	}

	.top_x_line_no_bg {
		height: 90rpx;
		width: 2rpx;
		margin: 0 auto;
	}

	/* 成员卡片基础样式 */
	.member_card {
		min-width: 170rpx;
		padding: 35rpx 0 25rpx;
		border: 3rpx solid #dcdcdc;
		border-radius: 10rpx;
		margin: 0 10rpx;
		position: relative;
		background-color: #FFF;
		box-shadow: 0 5rpx 10rpx rgba(0, 0, 0, 0.1);
		transition: all 0.3s ease;
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

	/* 夫妻连线 */
	.spouse_connector {
		position: absolute;
		top: 50%;
		left: 50%;
		width: 20rpx;
		height: 2rpx;
		background: linear-gradient(90deg, #1890ff 0%, #ff4d94 100%);
		transform: translate(-50%, -50%);
		z-index: 1;
	}

	.go_line {
		height: 90rpx;
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