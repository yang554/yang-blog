import {
	postRequest,
	postRequestJson,
	getRequest,
	deleteRequest,
	putRequestJson
} from '@/api/request.js'
import {
	BASE_URL,
	TIMEOUT
} from '@/api/config.js'

export const priceTrackerApi = {
	addProduct : (data) => {
	    return postRequestJson("/product/add",data) //添加商品信息
	},
	getProduct : () => {
	    return getRequest("/product/all")   //获取所有商品信息
	},
	editProduct : (data) => {
	    return putRequestJson("/product/edit",data) //编辑商品信息
	},
	delProduct : (id) => {
	    return deleteRequest(`/product/delete/${id}`) //删除商品信息(软删除)
	},
	
	addPriceRecord : (data) => {
	    return postRequestJson(`/price/add`,data) //添加商品历史价格记录
	},
	getRecordsByProductId : (id) => {
	    return getRequest(`/price/product/${id}`) //根据商品ID查询价格记录
	},
	delPriceRecord : (id) => {
	    return deleteRequest(`/price/delete/${id}`) //删除商品信息(物理删除)
	},
}