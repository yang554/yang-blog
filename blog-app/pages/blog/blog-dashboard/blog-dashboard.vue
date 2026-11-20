<template>
	<view>
		<address-picker v-model="addressCodes" @change="onAddressChange" />
		<text>选中的code：{{ addressCodes }}</text>
	</view>
</template>

<script>
	import AddressPicker from '@/components/address-picker/address-picker.vue'

	export default {
		data() {
			return {
				addressCodes: [] // 例如 ['110000', '110100', '110101']
			}
		},
		methods: {
			onAddressChange(result) {
				console.log('省市区对象', result)
			}
		}
	}
</script>