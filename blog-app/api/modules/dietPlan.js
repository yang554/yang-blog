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

export const dietPlanApi = {
	//饮食计划
	_getDietPlanByUid: (createID, createDate) => {
		return getRequest("/dietPlan/getDietPlanByUid?createID=" + createID + "&createDate=" + createDate)
	},
	_getDietPlanByDateScope: (createID, startDate, endDate) => {
		return getRequest(
			`/dietPlan/getDietPlanByDateScope?createID=${createID}&startDate=${startDate}&endDate=${endDate}`
			)
	},
	_operationDietPlan: (data) => {
		return postRequestJson("/dietPlan/operationDietPlan", data);
	}
}