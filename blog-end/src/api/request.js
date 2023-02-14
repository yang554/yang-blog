import axios from "axios";
import router from '@/router/router.js'
import Qs from 'qs'

export function request(config) {
	const instance1 = axios.create({
		// baseURL: 'http://10.224.4.16:8000/api',
		baseURL: '/api',
		timeout: 3000,
	})
	//请求拦截和响应拦截
	instance1.interceptors.request.use(config => {
		console.log(config);
		return config;
	}, err => {
		console.log(err);
	})
	instance1.interceptors.response.use(res => {
		console.log(res.data);
		return res;
	},error => {
		if(error.response.status == 504 || error.response.status == 404){
			this.$message.error({message: "服务器被吃了哦！"})
		}else if(error.response.status == 403){
			this.$message.error({message: "权限不足，请联系管理员"})
		}else if(error.response.status == 401){
			this.$message.error({message: "尚未登录"})
			router.replace('/')
		}else{
			//服务器返回的错误信息
			if(error.response.data.msg){
				this.$message.error({message: error.response.data.msg})
			}else{
				this.$message.error({message: "未知错误"})
			}
		}
		return;
	})
	//axios.create的返回值是一个promise对象！
	return instance1(config);
}

//自定义请求
let baseURL = "/api";
export const postKeyValueRequest = (url,params)=>{
	return axios({
		method:"post",
		url:`${baseURL}${url}`,
		data:params instanceof Object?Qs.stringify(params):params,		//直接这样以json的形式传给服务器，这是不支持的,需要转为查询字符串如 a=xxx&b=xxx
		//定义header
		headers:{
			'Content-Type':'application/x-www-form-urlencoded '
		}
	})
}

//get
export const getRequest = (url,params)=>{
	return axios({
		method:"get",
		url:`${baseURL}${url}`,
		data:params
	})
}
//post
export const postRequest = (url,params)=>{
	return axios({
		method:"post",
		url:`${baseURL}${url}`,
		data:params
	})
}
//delete
export const deleteRequest = (url,params)=>{
	return axios({
		method:"delete",
		url:`${baseURL}${url}`,
		data:params
	})
}