// api/modules/family.js
import {
	postRequest,
	getRequest,
	deleteRequest,
	postRequestJson
} from '../request.js'

export const familyApi = {
	//查询所有族谱
	_getAll: () => {
		return getRequest("/TFamilyTreeController/getAll")
	},
	 //修改/添加族谱
	_operationData:(data) => { 
	    return postRequestJson("/TFamilyTreeController/operationData",data)
	},
	//删除族谱
	_delTree: (data) => { 
	    return deleteRequest("/TFamilyTreeController/delTree?id=" + data);
	},
	//根据族谱ID获取所有成员
	_getSourceAll: (pTree) => {
		return getRequest("/source/getSourceAll?pTree="+pTree)
	},

	// 获取族谱统计
	_getSourceCount: () => {
		return getRequest("/source/getSourceCount")
	},

	// 获取性别统计
	_getUserCountBySex: () => {
		return getRequest("/source/getUserCountBySex")
	},

	// 获取爱情统计
	_getUserCountByLove: () => {
		return getRequest("/source/getUserCountByLove")
	},

	// 添加族谱数据
	_addSource: (data) => {
		return postRequestJson("/source/addSource", data)
	},

	// 添加父母关系 (JSON格式)
	_addParents: (data) => {
		return postRequestJson("/source/addParents", data)
	},

	// 保存族谱数据
	_saveSource: (data) => {
		return postRequestJson("/source/saveSource", data)
	},

	// 删除族谱数据
	_deleteSource: (data) => {
		return postRequestJson("/source/delSource", data)
	}
}