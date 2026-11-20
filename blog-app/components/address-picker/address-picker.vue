<template>
	<view class="address-picker">
		<view class="picker-trigger" @click="open">
			<view class="trigger-text" :class="{ placeholder: !displayText }">
				{{ displayText || placeholder }}
			</view>
			<view class="trigger-icon">▼</view>
		</view>

		<view v-if="visible" class="picker-mask" @click="close">
			<view class="picker-container" @click.stop>
				<view class="picker-header">
					<text class="cancel-btn" @click="close">取消</text>
					<text class="title">选择地址</text>
					<text class="confirm-btn" @click="handleConfirm">确定</text>
				</view>

				<picker-view class="picker-view" :value="pickerIndex" @change="onPickerChange"
					:indicator-style="indicatorStyle">
					<picker-view-column>
						<view class="picker-item" v-for="(item, idx) in provinceList" :key="item.value">
							{{ item.label }}
						</view>
					</picker-view-column>

					<picker-view-column>
						<view class="picker-item" v-for="(item, idx) in cityList" :key="item.value">
							{{ item.label }}
						</view>
					</picker-view-column>

					<picker-view-column>
						<view class="picker-item" v-for="(item, idx) in districtList" :key="item.value">
							{{ item.label }}
						</view>
					</picker-view-column>
				</picker-view>
			</view>
		</view>
	</view>
</template>

<script>
	import rawData from '@/static/json/china-area-data.json' // 请修改为实际路径

	export default {
		name: 'AddressPicker',
		props: {
			value: {
				type: [Array, String],
				default: () => []
			},
			placeholder: {
				type: String,
				default: '请选择省市区'
			}
		},
		data() {
			return {
				visible: false,
				provinceList: [],
				cityList: [],
				districtList: [],
				selectedProvince: null,
				selectedCity: null,
				selectedDistrict: null,
				pickerIndex: [0, 0, 0],
				indicatorStyle: 'height: 40px;'
			}
		},
		computed: {
			displayText() {
				const parts = []
				if (this.selectedProvince) parts.push(this.selectedProvince.label)
				if (this.selectedCity) parts.push(this.selectedCity.label)
				if (this.selectedDistrict && this.selectedDistrict.label) parts.push(this.selectedDistrict.label)
				return parts.join(' ')
			}
		},
		watch: {
			value: {
				handler(newVal) {
					this.setValueByCodes(newVal)
				},
				immediate: true
			}
		},
		created() {
			this.buildData()
			this.setValueByCodes(this.value)
		},
		methods: {
			buildData() {
				const provinceObject = rawData['86']
				if (!provinceObject) {
					console.error('数据错误：缺少根节点 86')
					return
				}

				const provinces = []
				for (const provinceCode in provinceObject) {
					const province = {
						value: provinceCode,
						label: provinceObject[provinceCode]
					}
					// 添加市
					const cityObj = rawData[provinceCode]
					if (cityObj) {
						const cities = []
						for (const cityCode in cityObj) {
							const city = {
								value: cityCode,
								label: cityObj[cityCode]
							}
							// 添加区县
							const districtObj = rawData[cityCode]
							if (districtObj) {
								const districts = []
								for (const districtCode in districtObj) {
									districts.push({
										value: districtCode,
										label: districtObj[districtCode]
									})
								}
								if (districts.length) {
									city.children = districts
								}
							}
							cities.push(city)
						}
						if (cities.length) {
							province.children = cities
						}
					}
					provinces.push(province)
				}
				this.provinceList = provinces
			},

			setValueByCodes(codes) {
				// 处理6位数字字符串
				if (typeof codes === 'string' && /^\d{6}$/.test(codes)) {
					const provinceCode = codes.slice(0, 2) + '0000'
					const cityCode = codes.slice(0, 4) + '00'
					const districtCode = codes
					codes = [provinceCode, cityCode, districtCode]
				}

				let provinceCode = '',
					cityCode = '',
					districtCode = ''
				if (Array.isArray(codes)) {
					provinceCode = codes[0] || ''
					cityCode = codes[1] || ''
					districtCode = codes[2] || ''
				} else if (typeof codes === 'string' && codes.includes(',')) {
					const parts = codes.split(',')
					provinceCode = parts[0] || ''
					cityCode = parts[1] || ''
					districtCode = parts[2] || ''
				} else if (typeof codes === 'string' && codes) {
					provinceCode = codes
				}

				if (!provinceCode) {
					return
				}

				const province = this.provinceList.find(p => p.value === provinceCode)
				if (!province) {
					return
				}
				this.selectedProvince = province
				this.cityList = province.children || []

				let city = null
				if (cityCode && this.cityList.length) {
					city = this.cityList.find(c => c.value === cityCode)
				}
				if (!city && this.cityList.length) {
					city = this.cityList[0]
				}
				if (city) {
					this.selectedCity = city
					this.districtList = city.children || []
					let district = null
					if (districtCode && this.districtList.length) {
						district = this.districtList.find(d => d.value === districtCode)
					}
					if (!district && this.districtList.length) {
						district = this.districtList[0]
					}
					this.selectedDistrict = district
				} else {
					this.selectedCity = null
					this.districtList = []
					this.selectedDistrict = null
				}
				this.updatePickerIndex()
			},

			updatePickerIndex() {
				const provinceIdx = this.selectedProvince ?
					this.provinceList.findIndex(p => p.value === this.selectedProvince.value) :
					0
				const cityIdx = this.selectedCity && this.cityList.length ?
					this.cityList.findIndex(c => c.value === this.selectedCity.value) :
					0
				const districtIdx = this.selectedDistrict && this.districtList.length ?
					this.districtList.findIndex(d => d.value === this.selectedDistrict.value) :
					0
				this.pickerIndex = [
					provinceIdx >= 0 ? provinceIdx : 0,
					cityIdx >= 0 ? cityIdx : 0,
					districtIdx >= 0 ? districtIdx : 0
				]
			},

			onProvinceChange(province) {
				this.selectedProvince = province
				this.cityList = province.children || []
				if (this.cityList.length) {
					this.onCityChange(this.cityList[0])
				} else {
					this.selectedCity = null
					this.districtList = []
					this.selectedDistrict = null
				}
			},

			onCityChange(city) {
				this.selectedCity = city
				this.districtList = city.children || []
				if (this.districtList.length) {
					this.selectedDistrict = this.districtList[0]
				} else {
					this.selectedDistrict = null
				}
			},

			onPickerChange(e) {
				const [provinceIdx, cityIdx, districtIdx] = e.detail.value
				this.pickerIndex = [provinceIdx, cityIdx, districtIdx]

				const newProvince = this.provinceList[provinceIdx]
				if (!newProvince) return

				if (!this.selectedProvince || this.selectedProvince.value !== newProvince.value) {
					this.onProvinceChange(newProvince)
					this.$nextTick(() => {
						this.pickerIndex = [provinceIdx, 0, 0]
					})
					return
				}

				const newCity = this.cityList[cityIdx]
				if (newCity && (!this.selectedCity || this.selectedCity.value !== newCity.value)) {
					this.onCityChange(newCity)
					this.$nextTick(() => {
						this.pickerIndex = [provinceIdx, cityIdx, 0]
					})
					return
				}

				const newDistrict = this.districtList[districtIdx]
				if (newDistrict && (!this.selectedDistrict || this.selectedDistrict.value !== newDistrict.value)) {
					this.selectedDistrict = newDistrict
				}
			},

			handleConfirm() {
				const result = {
					province: this.selectedProvince ? {
						...this.selectedProvince
					} : null,
					city: this.selectedCity ? {
						...this.selectedCity
					} : null,
					district: this.selectedDistrict ? {
						...this.selectedDistrict
					} : null
				}
				const codes = []
				let code = ''
				if (result.province) {
					codes.push(result.province.value)
					code = result.province.value
				}
				if (result.city){
					codes.push(result.city.value)
					code = result.city.value
				} 
				if (result.district){
					codes.push(result.district.value)
					code = result.district.value
				} 
				this.$emit('input', codes)
				this.$emit('change', result,this.displayText,code)
				this.$emit('confirm', {
					result,
					codes
				})
				this.close()
			},

			open() {
				// 打开时同步外部值，确保显示正确
				this.setValueByCodes(this.value)
				this.visible = true
			},

			close() {
				this.visible = false
				// 恢复为外部传入的值，撤销本次未确认的改动
				this.setValueByCodes(this.value)
				this.$emit('cancel')
			}
		}
	}
</script>

<style lang="scss" scoped>
	/* 样式与之前相同，保持不变 */
	.address-picker {
		width: 100%;

		.picker-trigger {
			display: flex;
			justify-content: space-between;
			align-items: center;
			background-color: #f5f5f5;
			border-radius: 8rpx;
			padding: 20rpx 24rpx;
			font-size: 28rpx;
			color: #333;
			box-sizing: border-box;

			.trigger-text {
				flex: 1;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;

				&.placeholder {
					color: #999;
				}
			}

			.trigger-icon {
				margin-left: 16rpx;
				font-size: 24rpx;
				color: #999;
				transform: scaleY(0.8);
			}
		}

		.picker-mask {
			position: fixed;
			top: 0;
			left: 0;
			right: 0;
			bottom: 0;
			background-color: rgba(0, 0, 0, 0.5);
			z-index: 999;

			.picker-container {
				position: absolute;
				bottom: 0;
				left: 0;
				right: 0;
				background-color: #fff;
				border-top-left-radius: 24rpx;
				border-top-right-radius: 24rpx;
				display: flex;
				flex-direction: column;
				max-height: 60vh;

				.picker-header {
					display: flex;
					justify-content: space-between;
					align-items: center;
					padding: 24rpx 32rpx;
					border-bottom: 1rpx solid #eee;

					.cancel-btn,
					.confirm-btn {
						font-size: 32rpx;
						color: #007aff;
					}

					.cancel-btn {
						color: #999;
					}

					.title {
						font-size: 32rpx;
						font-weight: 500;
						color: #333;
					}
				}

				.picker-view {
					height: 400rpx;
					width: 100%;

					.picker-item {
						height: 40px;
						line-height: 40px;
						font-size: 32rpx;
						color: #333;
						text-align: center;
						white-space: nowrap;
						overflow: hidden;
						text-overflow: ellipsis;
					}
				}
			}
		}
	}
</style>