/**
 * 防抖函数
 * @param {Function} func 要执行的函数
 * @param {number} wait 等待时间
 * @param {boolean} immediate 是否立即执行
 * @returns {Function}
 */
export function debounce(func, wait = 300, immediate = false) {
	let timeout;

	return function executedFunction(...args) {
		const context = this;
		const later = function() {
			timeout = null;
			if (!immediate) func.apply(context, args);
		};

		const callNow = immediate && !timeout;
		clearTimeout(timeout);
		timeout = setTimeout(later, wait);

		if (callNow) func.apply(context, args);
	};
}

/**
 * 节流函数
 * @param {Function} func 要执行的函数
 * @param {number} limit 限制时间
 * @returns {Function}
 */
export function throttle(func, limit = 300) {
	let inThrottle;

	return function(...args) {
		const context = this;
		if (!inThrottle) {
			func.apply(context, args);
			inThrottle = true;
			setTimeout(() => inThrottle = false, limit);
		}
	};
}

/**
 * 智能节流（根据函数执行时间动态调整）
 * @param {Function} func 要执行的函数
 * @param {Object} options 配置选项
 * @returns {Function}
 */
export function smartThrottle(func, options = {}) {
	const {
		limit = 300,
			maxWait = 1000,
			trailing = true,
			leading = true
	} = options;

	let lastExec = 0;
	let timer;
	let lastArgs;

	return function executedFunction(...args) {
		const now = Date.now();
		const remaining = limit - (now - lastExec);

		if (remaining <= 0) {
			// 可以立即执行
			lastExec = now;
			if (leading) func.apply(this, args);
		} else if (trailing) {
			// 记录最后一次参数
			lastArgs = args;

			// 清除之前的定时器
			if (timer) clearTimeout(timer);

			// 设置新的定时器
			timer = setTimeout(() => {
				lastExec = Date.now();
				func.apply(this, lastArgs);
			}, Math.min(remaining, maxWait));
		}
	};
}