import {
	postRequest,
	postRequestJson,
	getRequest,
	deleteRequest
} from '@/api/request.js'
import {
	BASE_URL,
	TIMEOUT
} from '@/api/config.js'

export const userApi = {
	// 获取app版本信息
	_getLatestVersionInfo: (data) => {
	  return getRequest("/app/list?"+data)
	},
	// 登录
	login: (data) => {
		return postRequestJson("/user/login", data)
	},

	// 注册
	register: (data) => {
		return postRequest("/user/register", data)
	},

	// 查找用户
	findUser: (data) => {
		return postRequest("/user/findUser", data)
	},

	// 获取所有用户
	getAllUsers: () => {
		return getRequest("/user/all")
	},

	// 添加用户
	addUser: (data) => {
		return postRequest("/user/addUser", data)
	},

	// 根据用户名查询用户
	getUserByName: (name) => {
		return getRequest("/user/selectUserByName", {
			name
		})
	},

	// 根据用户名模糊查询
	getUserByNameLike: (name) => {
		return getRequest("/user/getUserByName", {
			name
		})
	},

	// 编辑用户
	editUser: (data) => {
		return postRequestJson("/user/editUser", data)
	},

	// 重置密码
	resetPassword: (data) => {
		return postRequest("/user/resetUserPwd", data)
	},

	// 删除用户
	deleteUser: (id) => {
		return deleteRequest("/user/delUser", {
			id
		})
	},

	// 获取用户角色统计
	getUserRoleStats: () => {
		return getRequest("/user/count/role")
	},

	// 获取验证码 - 返回 ArrayBuffer
	getCaptcha: () => {
		return new Promise((resolve, reject) => {
			uni.request({
				url: BASE_URL + '/user/getCaptcha',
				method: 'GET',
				responseType: 'arraybuffer', // 关键：接收二进制数据
				header: {
					'Content-Type': 'application/octet-stream'
				},
				success: (res) => {
					if (res.statusCode === 200) {
						// 转换 ArrayBuffer 为 Base64
						const base64 = arrayBufferToBase64(res.data)
						const imageUrl = `data:image/gif;base64,${base64}`

						// 尝试从响应头获取 captchaKey 信息
						const captchaKey = res.header['X-Captcha-Key']
						const captchaExpire = res.header['X-Captcha-Expire']

						resolve({
							status: 200,
							data: {
								imageUrl: imageUrl,
								captchaKey: captchaKey,
								captchaExpire: captchaExpire,
								rawData: res.data
							}
						})
					} else {
						reject(new Error(`HTTP ${res.statusCode}`))
					}
				},
				fail: (err) => {
					reject(err)
				}
			})
		})
	},

	// 带刷新参数的验证码获取
	getCaptchaWithRefresh: (refresh = true) => {
		return userApi.getCaptcha(`?refresh=${refresh}`)
	},
	
	//修改头像
	upload: () => {
		
	}
}

// ArrayBuffer 转 Base64
function arrayBufferToBase64(buffer) {
	if (typeof uni !== 'undefined' && uni.arrayBufferToBase64) {
		return uni.arrayBufferToBase64(buffer)
	}

	// 兼容 H5 环境
	if (typeof btoa !== 'undefined') {
		let binary = ''
		const bytes = new Uint8Array(buffer)
		const len = bytes.byteLength
		for (let i = 0; i < len; i++) {
			binary += String.fromCharCode(bytes[i])
		}
		return btoa(binary)
	}

	return buffer
}
