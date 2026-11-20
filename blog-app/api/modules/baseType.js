import {
	postRequest,
	getRequest,
	deleteRequest,
	postRequestJson
} from '../request.js'

export const typeApi = {
	//获取所有类型
	_typeAll: () => {
		return getRequest("/type/all");
	},
	//根据用户ID查询类型
	_getTypeByUserId: (data) => {
		return getRequest("/type/getTypeByUserId?userId=" +data);
	},
	//根据名字搜索类型
	_typeByName: (data) => {
		return getRequest("/type/getTypeByName?name=" + data);
	},
	//新建类型
	_addType: (data) => {
		return postRequestJson("/type/add", data);
	},
	//根据ID修改类型
	_updTypeById: (data) => {
		return postRequestJson("/type/updTypeById", data);
	},
	//根据ID删除类型
	_delTypeById: (data) => {
		return deleteRequest("/type/delTypeById?id=" + data);
	},
	_getUserRoleAll: () => {
		return getRequest("/type/count/type");
	}
}