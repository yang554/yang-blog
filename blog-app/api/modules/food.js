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

export const foodApi = {
	// 获取所有食物信息
	_getFoodNutritionAll: () => {
	    return getRequest("/foodNutrition/all")
	},
	// 根据食物名称搜索
	_getFoodNutritionByFactor: (data) => {
	    return getRequest("/foodNutrition/findByFactor?param=" + data)
	},
	// 批量插入添加
	_insertBatchFood: (data) => {
	    return postRequestJson("/foodNutrition/insertBatch", data)
	},
	// 编辑食物信息
	_operationFoodNutrition: (data) => {
	    return postRequestJson("/foodNutrition/operationFood", data);
	},
	// 删除
	_delFoodNutritionById: (data) => {
	    return deleteRequest("/foodNutrition/delFood?id=" + data);
	},
}