// @ts-nocheck
export interface DialerProps {
	/**
	 * 组件尺寸（像素单位）
	 * @default 300
	 */
	size : string | number;

	/**
	 * 奖项列表
	 * @default []
	 */
	prizeList : UTSJSONObject[];

	/**
	 * 默认抽奖次数
	 * @default 10
	 */
	turns : number;

	/**
	 * 单次抽奖持续时间（秒）
	 * @default 3
	 */
	duration : number;

	/**
	 * 样式配置对象
	 * @default { prizeBgColors: ['#fff0a3', '#fffce6'], borderColor: '#ffd752' }
	 */
	styleOpt : UTSJSONObject

	/**
	 * 自定义组件样式类名
	 */
	customStyle ?: string;

	/**
	 * 转盘容器样式类名
	 */
	dialStyle ?: string;

	/**
	 * 指针样式（内联样式字符串）
	 */
	pointerStyle ?: string;
}