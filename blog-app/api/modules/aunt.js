import {
	postRequest,
	postRequestJson,
	getRequest,
	deleteRequest
} from '@/api/request.js'

export const auntApi = {
	// 获取所有登记用户
	_getAuntNameAll: () => {
	    return getRequest("/aunt/all")
	},
	//根据名字查询所有记录
	_getAuntByNameAll: (uName, status) => {
	    return getRequest(`/aunt/selectByNameAll?uName=${uName}&uStatus=${status}`)
	},
	//添加经期记录
	_addAunt: (data) => {
	    return postRequestJson("/aunt/addAunt", data)
	},
	//修改经期记录
	_updAunt: (data) => {
	    return postRequestJson("/aunt/updAunt", data)
	},
	//删除经期记录
	_delAunt: (data) => {
	    return deleteRequest("/aunt/delAunt?id=" + data);
	},
	//获取所有经期记录
	_getByALL: () => {
	    return getRequest("/aunt/getByALL")
	},
	//根据名字或者账户查询记录
	_selectByName: (data) => {
	    return getRequest("/aunt/selectByName?uName=" + data)
	}
}