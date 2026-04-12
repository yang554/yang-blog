import {
	postRequest,
	postRequestJson,
	getRequest,
	deleteRequest,
	request
} from '@/api/request.js'

export const lifeApi = {
	// 获取原始数据
	_getRawData: (data) => {
		return getRequest("/DailyFlowController/getRawData?" + data)
	},
	//获取用户记录年份
	_getAvailableYears: (userId) => {
		return getRequest(`/DailyFlowController/years/${userId}`)
	},
	//根据消费名称获取历史消费来源
	_getMatchSource: (data) => {
	    return getRequest(`/consumption/getMatchSource?purpose=${data}`)
	},
	//获取所有消费来源
	_getConsumptionTypeAll: () => {
	    return getRequest("/consumption/getConsumptionTypeAll");
	},
	//添加流水明细
	_insertBatch: (data) => {
	    return postRequestJson("/DailyFlowInfoController/insertBatch", data);
	},
	//添加or修改账单
	_operationDailyFlow: (data) => {
	    return postRequestJson("/DailyFlowController/operationDailyFlow", data);
	},
	//获取登录用户账单
	_getDailyFlowAll: (data) => {
	    return getRequest(`/DailyFlowController/findAllById?id=${data}`);
	},
	//条件查询账单
	_findByFactor: (data) => {
	    return getRequest(`/DailyFlowController/findByFactor?${data}`)
	},
	//删除账单
	_delFlow: (data) => {
	    return deleteRequest(`/DailyFlowController/delFlow?id=${data}`);
	},
	//根据用户ID或者账户查询账单
	_getUserByName: (data) => {
	    return getRequest(`/user/selectUserByName?name=${data}`);
	},
	//根据单号查找账单明细
	_getByFactor: (data) => {
	    return getRequest(`/DailyFlowInfoController/findByFactor?fID=${data}`);
	},
	//根据ID查询用户资产
	_getUserDepositById: (data) => {
	    return getRequest(`/UserDeposit/getUserDepositById?id=${data}`);
	},
	//获取日账单数据
	_getConsumptionCountDInfo: (type, time, queryId) => {
	    return getRequest(`/DailyFlowController/getConsumptionCountDInfo?consumptionType=${type}&consumptionTime=${time}&queryId=${queryId}`)
	},
	//获取月账单数据
	_getConsumptionCountMInfo: (type, time, queryId) => {
	    return getRequest(`/DailyFlowController/getConsumptionCountMInfo?consumptionType=${type}&consumptionTime=${time}&queryId=${queryId}`)
	},
	//获取年账单数据
	_getConsumptionCountYInfo: (type, time, queryId) => {
	    return getRequest(`/DailyFlowController/getConsumptionCountYInfo?consumptionType=${type}&consumptionTime=${time}&queryId=${queryId}`)
	},
	//获取流水类型
	_getConsumptionTypeAll: () => {
	    return getRequest("/consumption/getConsumptionTypeAll");
	},
	//根据流水类型搜索
	_getConsumptionTypeByName: (data) => {
	    return getRequest(`/consumption/getConsumptionTypeByName?name=${data}`);
	},
	//添加流水类型
	_addConsumptionType: (data) => {
	    return postRequestJson("/consumption/addConsumptionType", data);
	},
	//根据修改流水类型
	_updConsumptionTypeById: (data) => {
	    return postRequestJson("/consumption/updConsumptionTypeById", data);
	},
	//根据流水类型ID删除
	_delConsumptionType: (data) => {
	    return deleteRequest(`/consumption/delConsumptionType?id=${data}`);
	},
	//条件查询账簿明细
	_findByFactorInfo: (data) => {
	    return getRequest("/DailyFlowInfoController/findByFactorInfo?" + data)
	}
}