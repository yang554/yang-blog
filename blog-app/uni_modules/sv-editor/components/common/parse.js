/**
 * 富文本解析工具
 * @author sonve
 * @version 1.0.0
 * @date 2024-12-04
 */

import config from './config.js'

/**
 * 将含有封面占位图形式的视频富文本转换成正常视频的富文本
 * @param {String} richText 要进行处理的富文本字符串
 * @returns {String} 返回处理结果
 */
export function parseHtmlWithVideo(richText) {
	// 正则表达式匹配<img>标签及其属性
	const imgRegex = /<img\s+([^>]+)>/gi;
	// 正则表达式匹配data-custom属性中的url值
	const customUrlRegex = /\bdata-custom="[^"]*url=([^&"]+)/i;

	return richText.replace(imgRegex, (match, attrs) => {
		// 查找data-custom属性中的url值
		const urlMatch = attrs.match(customUrlRegex);
		if (urlMatch) {
			// 获取data-custom中的url
			const videoUrl = urlMatch[1];

			// 解析出所有属性
			const attrArray = attrs.split(/\s+/).filter(attr => attr.trim() !== '');

			// 过滤掉src属性和data-custom属性
			const newAttrs = attrArray.filter(attr => !attr.startsWith('src=') && !attr.startsWith('data-custom='))
				.join(' ');

			// 构建新的video标签，保留原有的其他属性，但去除src和data-custom
			return `<video controls ${newAttrs}><source src="${videoUrl}" /></video>`;
		}
		// 如果没有匹配到data-custom中的url，则保持原样
		return match;
	});
}

/**
 * 带有视频的富文本逆向转换
 * @description 可自定义处理封面
 * @param {Promise} richText 要转换的富文本
 * @param {Function<Promise>} customCallback 自定义处理封面回调，需要return封面图片资源，自带参数为视频地址
 * @returns {Promise} 转换后的富文本 注意异步处理
 */
export async function replaceVideoWithImageRender(richText, customCallback) {
console.log(richText)
	// 正则表达式用于匹配 <video> 标签以及其内部的 <source> 标签
	const videoRegex = /<video\s+([^>]+)>(.*?)<\/video>/gi;

	// 找到所有的 <video> 标签
	const matches = [];
	let match;
	while ((match = videoRegex.exec(richText)) !== null) {
		matches.push(match);
	}

	// 并行处理每个 <video> 标签，生成对应的缩略图
	const replacements = await Promise.all(
		matches.map(async (match) => {
			const [fullMatch, attributes, content] = match;

			// 匹配 <source> 标签中的 src 属性
			const sourceRegex = /<source\s+[^>]*src="([^">]+)"/i;
			const matchSource = content.match(sourceRegex);

			let videoUrl = '';
			if (matchSource && matchSource.length > 1) {
				videoUrl = matchSource[1];
			}

			// 生成视频封面图
			let thumbnailRes
			if (customCallback) thumbnailRes = await customCallback(videoUrl) // 自定义封面处理
			if (!thumbnailRes) thumbnailRes = config.video_thumbnail // 无效值则默认封面处理

			// 过滤掉不需要的属性，例如 controls
			const filteredAttributes = attributes
				.split(/\s+/)
				.filter(attr => !attr.startsWith('controls'))
				.join(' ');

			// 构建新的 img 标签，继承 video 的属性（除了 controls）并添加 data-custom 属性
			const imgTag = `<img ${filteredAttributes} src="${thumbnailRes}" data-custom="url=${videoUrl}" />`;

			return { fullMatch, imgTag };
		}));

	// 使用 replacements 替换原始的 <video> 标签
	let result = richText;
	for (const { fullMatch, imgTag } of replacements) {
		result = result.replace(fullMatch, imgTag);
	}

	return result;
}

/**
 * 解析出富文本中的图片和视频
 * @param {String} richText 要解析的富文本
 * @returns {Array} 图片和视频数组
 */
export function parseImagesAndVideos(richText) {
	// 创建一个空数组用于存储图片和视频信息
	const result = [];

	// 正则表达式匹配 <img> 标签及其属性
	const imgRegex = /<img\s+[^>]*>/gi;
	// 匹配属性名和值的正则表达式，改进后的版本可以处理属性名中包含连字符的情况
	const attrRegex = /(\w+(-\w+)*)\s*=\s*(?:"([^"]*)"|'([^']*)'|([^\s"'<>]+))/gi;

	// 找到所有的 <img> 标签
	const matches = richText.match(imgRegex);
	// 如果没有找到任何 <img> 标签，返回空数组
	if (!matches) return [];

	// 遍历所有的 <img> 标签
	matches.forEach(match => {
		// 创建一个对象用于存储单个图片或视频的信息
		const ivInfo = {};
		// 使用正则表达式匹配每个 <img> 标签的属性
		let attrsMatch;
		while ((attrsMatch = attrRegex.exec(match)) !== null) {
			// 属性名
			const name = attrsMatch[1].toLowerCase();
			// 属性值可能存在于第三、第四或第五个捕获组中
			let value = attrsMatch[3] || attrsMatch[4] || attrsMatch[5] || '';

			// 去除属性值两端可能存在的引号
			if (value.startsWith('"') && value.endsWith('"') || value.startsWith("'") && value.endsWith("'")) {
				value = value.substring(1, value.length - 1);
			}

			// 将属性名和值添加到 ivInfo 对象中
			ivInfo[name] = value;
		}
		// 将单个图片或视频信息添加到数组中
		result.push(ivInfo);
	});

	// 返回包含所有图片和视频信息的数组
	return result;
}

/**
 * 解析出富文本中的图片
 * @param {String} richText 要解析的富文本
 * @returns {Array} 图片数组
 */
export function parseImages(richText) {
	let result = []
	const ivList = parseImagesAndVideos(richText)
	ivList.forEach(item => {
		if (!item['data-custom'] || !item['data-custom'].startsWith('url')) {
			result.push(item)
		}
	})
	return result
}

/**
 * 解析出富文本中的视频
 * @param {String} richText 要解析的富文本
 * @returns {Array} 视频数组
 */
export function parseVideos(richText) {
	let result = []
	const ivList = parseImagesAndVideos(richText)
	ivList.forEach(item => {
		if (item['data-custom'] && item['data-custom'].startsWith('url')) {
			result.push(item)
		}
	})
	return result
}