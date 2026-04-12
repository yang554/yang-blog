export default {
	size: {
		type: [String, Number],
		default: 300
	},
	prizeList: {
		type: Array
	},
	turns: {
		type: Number,
		default: 10
	},
	duration: {
		type: Number,
	    default: 3
	},
	styleOpt: {
		type: Object,
		default: () => ({
			// 每一块扇形的背景色,默认值,可通过父组件来改变
			// $primary-1 $primary-2 
			prizeBgColors: ['#fff0a3', '#fffce6'],
			// 每一块扇形的外边框颜色,默认值,可通过父组件来改变
			// primary-4
			borderColor: '#ffd752',
		})
	},
	customStyle: {
		type: String,
	},
	dialStyle: {
		type: String,
	},
	pointerStyle: {
		type: String,
		default: `width: 30%`
	}
}