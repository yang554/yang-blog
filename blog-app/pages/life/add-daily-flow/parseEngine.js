import {
	lifeApi
} from '@/api/index.js'

// ========== 中文数字转数字（支持万、亿） ==========
function chineseToNumber(str) {
	const map = {
		'零': 0,
		'一': 1,
		'二': 2,
		'两': 2,
		'三': 3,
		'四': 4,
		'五': 5,
		'六': 6,
		'七': 7,
		'八': 8,
		'九': 9,
		'十': 10,
		'百': 100,
		'千': 1000,
		'万': 10000,
		'亿': 100000000
	};
	let result = 0;
	let temp = 0;
	for (let i = 0; i < str.length; i++) {
		const char = str[i];
		const val = map[char];
		if (val === undefined) continue;
		if (val >= 10) {
			if (temp === 0) temp = 1;
			result += temp * val;
			temp = 0;
		} else {
			temp = val;
		}
	}
	result += temp;
	return result;
}

// ========== 核心金额提取 ==========
function extractAmount(text) {
	// 1. 优先提取纯数字
	const numMatch = text.match(/(\d+\.?\d*)/);
	if (numMatch) {
		const v = parseFloat(numMatch[1]);
		if (!isNaN(v) && v > 0) return v;
	}

	let rest = text;
	let integer = 0;
	let dime = 0;
	let cent = 0;

	// 2. 提取“块/元”前的数字
	const yuanMatch = rest.match(/([零一二三四五六七八九十百千万亿两]+)\s*(块|元)/);
	if (yuanMatch) {
		integer = chineseToNumber(yuanMatch[1]);
		rest = rest.substring(yuanMatch.index + yuanMatch[0].length);
	}

	// 3. 提取“毛/角”前的数字
	const jiaoMatch = rest.match(/([零一二三四五六七八九]+)\s*(毛|角)/);
	if (jiaoMatch) {
		dime = chineseToNumber(jiaoMatch[1]);
		rest = rest.substring(jiaoMatch.index + jiaoMatch[0].length);
	} else {
		// 如果“块”后面直接跟单个数字（如“17块二”），视为“角”
		const singleMatch = rest.match(/^\s*([零一二三四五六七八九])\s*(?![毛角分])/);
		if (singleMatch) {
			dime = chineseToNumber(singleMatch[1]);
			rest = rest.substring(singleMatch.index + singleMatch[0].length);
		}
	}

	// 4. 提取“分”前的数字
	const fenMatch = rest.match(/([零一二三四五六七八九]+)\s*分/);
	if (fenMatch) {
		cent = chineseToNumber(fenMatch[1]);
	} else {
		// 剩余文本中第一个单个数字（如“五毛二”中的“二”）
		const singleMatch = rest.match(/^\s*([零一二三四五六七八九])\s*(?![毛角分])/);
		if (singleMatch) {
			cent = chineseToNumber(singleMatch[1]);
		}
	}

	if (integer === 0 && dime === 0 && cent === 0) return null;
	return integer + dime / 10 + cent / 100;
}

// ========== 提取费用名目（去掉金额部分） ==========
function extractPurpose(text) {
	// 1. 去掉金额数字和单位
	let purpose = text
		// 去掉 “数字”
		.replace(/\d+\.?\d*\s*/g, '')
		// 去掉 "数字+块/元"（如 "5500元"、"17块"）
		.replace(/\d+\.?\d*\s*[块元]/g, '')
		// 去掉 "中文数字+块/元"（如 "十七块"）
		.replace(/[零一二三四五六七八九十百千万亿两]+\s*[块元]/g, '')
		// 去掉 "毛/角" 部分
		.replace(/[零一二三四五六七八九]+\s*[毛角]/g, '')
		// 去掉 "分" 部分
		.replace(/[零一二三四五六七八九]+\s*分/g, '')
		// 去掉单独的数字（如 "二" 在末尾）
		.replace(/[零一二三四五六七八九]/g, '')
		// 去掉末尾的标点符号（如 "5500。" 中的 "。"）
		.replace(/[，,。.!！?？、]+$/, '')
		.trim();

	// 2. 如果处理后为空，尝试提取第一个非金额词
	if (!purpose) {
		// 提取第一个中文词汇（2个字符以上）
		const match = text.match(/^([\u4e00-\u9fa5]{2,})/);
		if (match) {
			purpose = match[1];
		} else {
			// 实在提取不到，返回原文本（去掉标点和数字）
			purpose = text.replace(/[，,。.!！?？、；;：:""''（）()【】\[\]\d]/g, '').trim() || text;
		}
	}

	return purpose;
}

// ========== 对外解析函数 ==========
export const parseVoiceText = async (text) => { 
	const amount = extractAmount(text);
	const purpose = extractPurpose(text);
	
	let category = '其他';

	//账目类型
	if(purpose){
		const res = await lifeApi._getMatchSource(purpose)
		category = res.data[0]
	}
	

	// 账户映射
	const accountMap = {
		'微信': '微信',
		'支付宝': '支付宝',
		'支': '支付宝',
		'微': '微信',
		'现金': '现金',
		'卡': '信用卡'
	};
	let account = '现金';
	Object.keys(accountMap).forEach(key => {
		if (text.includes(key)) account = accountMap[key];
	});

	return {
		amount,
		category,
		account,
		purpose,
		rawText: text
	};
};