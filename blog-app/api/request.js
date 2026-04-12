import {
	BASE_URL,
	TIMEOUT
} from '@/api/config.js'
import { STORAGE_KEYS ,PAGE_PATHS} from '@/common/constants.js'

// 基础请求方法
export const request = (config) => {
	return new Promise((resolve, reject) => {
		// 确保 URL 是完整的 http URL
		let url = config.url
		// 如果传入的 URL 不是以 http 开头，添加 BASE_URL
		if (!url.startsWith('http')) {
			url = BASE_URL + url
		}
		// 请求拦截 - 添加 token
		const token = uni.getStorageSync(STORAGE_KEYS.TOKEN)
		if (token) {
			config.header = config.header || {}
			config.header['Authorization'] = `Bearer ${token}`
		}

		// 设置默认超时时间
		config.timeout = config.timeout || TIMEOUT
		uni.request({
			url: url,
			method: config.method || 'GET',
			data: config.data,
			header: config.header,
			timeout: config.timeout || 10000,
			success: (res) => {
				// 响应拦截
				if (res.statusCode === 200) {
					resolve(res.data)
				} else {
					handleHttpError(res.statusCode, res.data)
					reject(res)
				}
			},
			fail: (err) => {
				uni.showToast({
					title: '网络连接失败，请检查网络',
					icon: 'none'
				})
				reject(err)
			}
		})
	})
}

// 处理 HTTP 错误
const handleHttpError = (statusCode, data) => {
	const errorMap = {
		400: '请求参数错误',
		401: '未授权，请重新登录',
		403: '权限不足，请联系管理员',
		404: '请求地址不存在',
		500: '服务器内部错误',
		502: '网关错误',
		503: '服务不可用',
		504: '服务器连接超时'
	}

	const message = data?.msg || errorMap[statusCode] || '网络请求失败'

	uni.showToast({
		title: message,
		icon: 'none',
		duration: 3000
	})

	// 如果是 401，清除 token 并跳转到登录页
	if (statusCode === 401) {
		uni.removeStorageSync(STORAGE_KEYS.TOKEN)
		setTimeout(() => {
			uni.reLaunch({
				url: PAGE_PATHS.LOGIN
			})
		}, 1500)
	}
}

// GET 请求
export const getRequest = (url, params = {}) => {
	let queryString = ''
	if (Object.keys(params).length > 0) {
		queryString = '?' + Object.keys(params).map(key =>
			`${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`
		).join('&')
	}

	return request({
		method: 'GET',
		url: url + queryString
	})
}

// POST 请求 (表单格式)
export const postRequest = (url, data = {}) => {
	return request({
		method: 'POST',
		url: url,
		data: data,
		header: {
			'Content-Type': 'application/x-www-form-urlencoded'
		}
	})
}

// POST 请求 (JSON 格式)
export const postRequestJson = (url, data = {}) => {
	return request({
		method: 'POST',
		url: url,
		data: data,
		header: {
			'Content-Type': 'application/json'
		}
	})
}

// DELETE 请求
export const deleteRequest = (url, params = {}) => {
	let queryString = ''
	if (Object.keys(params).length > 0) {
		queryString = '?' + Object.keys(params).map(key =>
			`${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`
		).join('&')
	}

	return request({
		method: 'DELETE',
		url: url + queryString
	})
}

// 文件上传
export const uploadRequest = (url, filePath, formData = {}) => {
	return new Promise((resolve, reject) => {
		const token = uni.getStorageSync('ADMIN-TOKEN')

		uni.uploadFile({
			url: BASE_URL + url,
			filePath: filePath,
			name: 'file',
			formData: formData,
			header: {
				'Authorization': token ? `Bearer ${token}` : ''
			},
			success: (res) => {
				if (res.statusCode === 200) {
					try {
						const data = typeof res.data === 'string' ? JSON.parse(res.data) : res
							.data
						resolve(data)
					} catch (e) {
						resolve(res.data)
					}
				} else {
					handleHttpError(res.statusCode, res.data)
					reject(res)
				}
			},
			fail: (err) => {
				uni.showToast({
					title: '上传失败，请重试',
					icon: 'none'
				})
				reject(err)
			}
		})
	})
}

export default request
