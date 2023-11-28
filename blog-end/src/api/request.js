import axios from "axios";
import router from '@/router/router.js'
import Qs from 'qs'

export function request(config) {
	const instance1 = axios.create({
		baseURL: '/api',
		timeout: 3000,
	})
	//请求拦截和响应拦截
	instance1.interceptors.request.use(config => {
		return config;
	}, err => {
		console.log(err);
	})
	instance1.interceptors.response.use(res => {
		res.headers['Content-Type'] = 'application/json;charset=UTF-8';
		return res;
	}, error => {
		if (error.response.status == 504 || error.response.status == 404) {
			this.$message.error({ message: "服务器被吃了哦！" })
		} else if (error.response.status == 403) {
			this.$message.error({ message: "权限不足，请联系管理员" })
		} else if (error.response.status == 401) {
			this.$message.error({ message: "尚未登录" })
			router.replace('/')
		} else {
			//服务器返回的错误信息
			if (error.response.data.msg) {
				this.$message.error({ message: error.response.data.msg })
			} else {
				this.$message.error({ message: "未知错误" })
			}
		}
		return;
	})
	return instance1(config);
}

//自定义请求
let baseURL = "/api";
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
	return axios({
		method: "get",
		url: `${baseURL}${url}`,
		data: params
	})
}
//post
export const postRequest = (url, params) => {
	return axios({
		method: "post",
		url: `${baseURL}${url}`,
		data: params
	})
}
//delete
export const deleteRequest = (url, params) => {
	return axios({
		method: "delete",
		url: `${baseURL}${url}`,
		data: params
	})
}

export const uploadRequest = (url, params) => {
	return axios({
		method: 'post',
		url: `${baseURL}${url}`,
		headers: {
			// 'Content-Type': 'application/json',//设置请求头请求格式为JSON
			'Content-Type': 'multipart/form-data'
			// 'access_token': this.token //设置token 其中K名要和后端协调好
		},
		data: params
	})
}