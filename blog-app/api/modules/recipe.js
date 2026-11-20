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

export const recipeApi = {
	// 获取所有食谱
	_getNutritionalRecipeAll: () => {
	    return getRequest("/NutritionalRecipe/all")
	},
	_getAllByUserCol: (uID) => {
	    return getRequest("/NutritionalRecipe/findAllByUserCol?uID=" + uID)
	},
	_getFoodType: () => {
	    return getRequest("/NutritionalRecipe/getFoodType")
	},
	_getNutritionalRecipeByFactor: (name, type) => {
	    return getRequest("/NutritionalRecipe/findByFactor?name=" + name + "&type=" + type)
	},
	// 根据食谱ID查询
	_getNutritionalRecipeById: (data) => {
	    return getRequest("/NutritionalRecipe/findById?id=" + data)
	},
	// 根据食谱ID查询食材清单
	_getByRecipeId: (recipeId) => {
	    return getRequest("/RecipeFood/getByRecipeId?recipeId=" + recipeId)
	},
	_addFoodLikesOrCollectionCount: (fID, uID, typeOp) => {
	    return postRequestJson("/food/likeOrCol/toggle?fID=" + fID + "&uID=" + uID + "&type=" + typeOp);
	},
	_operationNutritionalRecipe: (data) => {
	    return postRequestJson("/NutritionalRecipe/operationRecipe", data);
	},
	_delNutritionalRecipe: (data) => {
	    return deleteRequest("/NutritionalRecipe/delRecipe?id=" + data);
	}
}