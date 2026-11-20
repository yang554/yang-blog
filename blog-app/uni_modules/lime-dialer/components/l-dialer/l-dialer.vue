<template>
	<view class="l-dialer" :style="getStyle">
		<view class="l-dialer__inner" :style="getDialStyle">
			<view class="l-dialer__inner-border" v-if="$slots.border">
				<slot name="border"/>
			</view>
			<view class="l-dialer__inner-wrap" :style="styleOpt.borderColor && (' border: 1rpx solid ' + styleOpt.borderColor)">
				<view class="l-dialer__inner-item" v-for="(item, index) in prizeList" :key="index" :style="[getRotateAngle(index)]">
					<view class="l-dialer__inner-content" :style="[getCorrectAngle(index)]">
						<slot v-if="$slots.prize" name="prize" :item="item" :even="index % 2"></slot>
						<block v-else>
							<view class="l-dialer__inner-name" :style="[{fontSize: styleOpt.fontSize, color: styleOpt.color}]">{{ item.name }}</view>
							<image class="l-dialer__inner-img" :src="item.img"></image>
						</block>
					</view>
				</view>
			</view>
		</view>
		<view class="l-dialer__pointer" :style="pointerStyle" >
			<slot v-if="$slots && $slots.pointer" name="pointer"/>
			<image
				v-else
				:class="!isTurnIng ? 'heart': ''"
				src="/uni_modules/lime-dialer/static/turnable_btn.png" 
				style="width: 100%"
				mode="widthFix" 
				@tap="$emit('click')"
			/>
		</view>
	</view>
</template>
<script>
	// @ts-nocheck
/**
 * Dialer 转盘抽奖组件
 * @description 用于实现大转盘抽奖效果的交互组件
 * @tutorial https://ext.dcloud.net.cn/plugin?name=lime-dialer
 * 
 * @property {(String|Number)} size 组件整体尺寸（支持px/rpx单位）
 * @property {Object[]} prizeList 奖项配置数组
 * @property {Number} turns 默认旋转圈数
 * @property {Number} duration 单次抽奖持续时间（单位：秒）
 * @property {Object} styleOpt 样式配置对象
 * @property {String} customStyle 自定义组件根节点样式类名
 * @property {String} dialStyle 转盘容器样式类名
 * @property {String} pointerStyle 指针内联样式（支持CSS字符串）
 * @event {Function} click 点击触发
 * @event {Function} done 完成时触发
 */
import dialerProps from './props';
export default {
	emits: ['click', 'done'],
	props: dialerProps,
	data() {
		return {
			// 开始转动的角度
			startRotateDegree: 0,
			// 设置指针默认指向的位置,现在是默认指向2个扇形之间的边线上
			rotateAngle: 0,
			rotateTransition: '',
			isTurnIng: false,
		};
	},
	computed: {
		getStyleOpt() {
			const style = {
				// 每一块扇形的背景色,默认值,可通过父组件来改变
				prizeBgColors: ['#fff0a3', '#fffce6'],
				// 每一块扇形的外边框颜色,默认值,可通过父组件来改变
				borderColor: '#ffd752',
			}
			return Object.assign(style, this.styleOpt)
		},
		getRotateAngle() {
			return index => {
				const style = {
					transform: `rotate(${(360 / this.prizeList.length) * index}deg) skewX(0deg) skewY(${360 / this.prizeList.length - 90}deg)`,
					backgroundColor: `${this.getStyleOpt.prizeBgColors[index % this.getStyleOpt.prizeBgColors.length]}`,
					border: `${this.getStyleOpt.borderColor && '1rpx solid ' + this.getStyleOpt.borderColor }`
				}
				if(this.prizeList.length == 2) {
					style['transform'] = index == 0 ? 0 : `rotate(270deg)` //`rotate(${(360 / this.prizeList.length) * index}deg)`
					style['top']  = 0
				} 
				return style
				// return {
				// 	transform: `rotate(${(360 / this.prizeList.length) * index}deg) skewX(0deg) skewY(${360 / this.prizeList.length - 90}deg)`,
				// 	backgroundColor: `${this.styleOpt.prizeBgColors[index % this.styleOpt.prizeBgColors.length]}`,
				// 	border: `${this.styleOpt.borderColor && '1rpx solid ' + this.styleOpt.borderColor }`
				// }
			};
		},
		getCorrectAngle() {
			return index => {
				const style = {
					transform: `skewY(${90 - 360 / this.prizeList.length}deg) skewX(0deg) rotate(${180 / this.prizeList.length}deg)`
				}
				if(this.prizeList.length == 2){
					if(index == 0) {
						style['transform'] = `rotate(90deg)` 
						style['bottom'] = 0
					} else {
						style['transform'] = `rotate(0deg)` 
						style['left'] = 0
						style['bottom'] = '-50%'
					}
					
				}
				return style
			};
		},
		getStyle() {
			let { size, customStyle } = this;
			//addUnit(size)//
			size = /\d$/.test(size) ? size + 'px' : size;
			return `width: ${size}; height: ${size}; ${customStyle}`;
		},
		getDialStyle() {
			return `
				padding: ${this.getStyleOpt.padding};
				transform: ${this.rotateAngle};
				transition: ${this.rotateTransition};
				${this.dialStyle}
			`;
		}
		
	},
	methods: {
		// 转动起来
		run(index) {
			if(this.isTurnIng) return
			const duration = this.duration;
			const length = this.prizeList.length
			
			const rotateAngle = this.startRotateDegree + this.turns * 360 + 360 - (180 / length + (360 / length) * index) - (this.startRotateDegree % 360);
			this.startRotateDegree = rotateAngle;
			this.rotateAngle = `rotate(${rotateAngle}deg)`;
			this.rotateTransition = `transform ${duration}s cubic-bezier(0.250, 0.460, 0.455, 0.995)`;
			this.isTurnIng = true
			// sleep(duration * 1000 + 500).then(() => {
			// 	this.$emit('done', index);
			// 	this.isTurnIng = false
			// })
			setTimeout(() => {
				this.$emit('done', index);
				this.isTurnIng = false
			}, duration * 1000 + 500);
		}
	}
};
</script>
<style lang="scss" scoped>
@import './index';
</style>
