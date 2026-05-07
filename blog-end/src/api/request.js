import axios from "axios";
import router from '@/router/router.js'
import Qs from 'qs'
import { Message } from 'element-ui';

/**
 * 封装axios请求函数
 * @param {Object} config - 请求配置对象
 * @returns {Promise} - 返回axios请求的Promise对象
 */
export function request(config) {
	// 创建axios实例
	const instance1 = axios.create({
		baseURL: '/api',  // 设置基础URL，所有请求都会基于此地址
		timeout: 10000,  // 设置请求超时时间为10秒
	})
	//请求拦截和响应拦截
	instance1.interceptors.request.use(config => {
		// 从本地存储获取 Token
		// const token = JSON.parse(window.sessionStorage.getItem("ADMIN-TOKEN"))
		const token = JSON.parse(localStorage.getItem("ADMIN-TOKEN"))

		if (token) {
			config.headers['Authorization'] = `Bearer ${token}`
		}
		return config
	}, err => {
		console.log("这是request:", err);
	})
	instance1.interceptors.response.use(res => {
		res.headers['Content-Type'] = 'application/json;charset=UTF-8';
		return res;
	}, async error => {
		// 检查 error.response 是否存在
		if (error.response) {
			// 有响应
			if (error.response.status == 504 || error.response.status == 404) {
				console.log("服务器被吃了哦！")
			} else if (error.response.status == 403) {
				console.log("权限不足，请联系管理员! ")
			} else if (error.response.status == 401) {
				// 跳转到前端登录页（携带当前路径供登录后重定向）
				const redirect = encodeURIComponent(window.location.pathname);
				window.location.href = `http://localhost:8091/login?redirect=${redirect}`;
				// 清理本地 Token
				window.sessionStorage.removeItem("ADMIN-TOKEN");
				// 跳转登录页
				// router.push('/login');

				try {
					// 尝试刷新Token
					const refreshToken = localStorage.getItem('refresh_token');
					if (refreshToken) {
						const refreshResponse = await axios.post('/user/refreshToken', {}, {
							headers: {
								'Authorization': 'Bearer ' + refreshToken
							}
						});

						if (refreshResponse.data.code === 200) {
							const { accessToken } = refreshResponse.data.data;
							localStorage.setItem("ADMIN-TOKEN", accessToken);

							// 重新发送原始请求
							originalRequest.headers['Authorization'] = 'Bearer ' + accessToken;
							return axios(originalRequest);
						}
					}
				} catch (refreshError) {
					// 刷新失败，跳转到登录页
					localStorage.clear();
					window.location.href = '/login';
				}

			} else {
				// 服务器返回的错误信息
				if (error.response.data && error.response.data.msg) {
					console.log(error.response.data.msg);
				} else {
					console.log('未知错误', error);
				}
			}
		} else {
			// 没有响应的情况（网络错误、超时、请求被取消等）
			if (error.code === 'ECONNABORTED' || error.message.includes('timeout')) {
				console.log("请求超时，请检查网络或稍后重试");
			} else if (error.message === 'Network Error') {
				console.log("网络错误，请检查网络连接");
			} else {
				console.log("请求失败，请检查网络连接");
			}
		}

		// 返回一个 reject 的 Promise，避免链式调用继续执行
		return Promise.reject(error);
	});
	return instance1(config);
}

export const postKeyValueRequest = (url, params) => {
	return axios({
		method: "post",
		url: `${baseURL}${url}`,
		data: params instanceof Object ? Qs.stringify(params) : params,		//直接这样以json的形式传给服务器，这是不支持的,需要转为查询字符串如 a=xxx&b=xxx
		//定义header
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded '
		}
	})
}

//get
export const getRequest = (url, params) => {

	return request({
		method: "get",
		url: `${url}`,
		data: params
	})
}
//post
export const postRequest = (url, params) => {
	return request({
		method: "post",
		url: `${url}`,
		data: params
	})
}
//put
export const putRequest = (url, params) => {
	return request({
		method: "put",
		url: `${url}`,
		data: params
	})
}

export const postRequestJson = (url, params) => {
	return request({
		method: "post",
		url: `${url}`,
		headers: {
			'Content-Type': 'application/json',//设置请求头请求格式为JSON
		},
		data: JSON.stringify(params)
	})
}

//delete
export const deleteRequest = (url, params) => {
	return request({
		method: "delete",
		url: `${url}`,
		data: params
	})
}

export const uploadRequest = (url, params) => {
	return request({
		method: 'post',
		url: `${url}`,
		headers: {
			// 'Content-Type': 'application/json',//设置请求头请求格式为JSON
			'Content-Type': 'multipart/form-data'
		},
		data: params
	})
}