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

export const recipesApi = {
	// 获取所有食谱
	_getNutritionalRecipesAll: () => {
	    return getRequest("/NutritionalRecipes/all")
	},
	_getNutritionalRecipesByFactor: (name, type, target) => {
	    return getRequest("/NutritionalRecipes/findByFactor?name=" + name + "&type=" + type + "&target=" + target)
	},
	_getNutritionalRecipesById: (data) => {
	    return getRequest("/NutritionalRecipes/findById?id=" + data)
	},
	_operationNutritionalRecipes: (data) => {
	    return postRequest("/NutritionalRecipes/operationRecipes", data);
	},
	_delNutritionalRecipes: (data) => {
	    return deleteRequest("/NutritionalRecipes/delRecipes?id=" + data);
	}
}