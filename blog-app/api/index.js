// api/index.js
export * from './modules/user.js'
export * from './modules/blog.js'
export * from './modules/family.js'

// 导出其他模块...
export { userApi } from './modules/user.js'
export { blogApi } from './modules/blog.js'
export { familyApi } from './modules/family.js'
export { lifeApi } from './modules/life.js'
export { auntApi } from './modules/aunt.js'
export { prizeApi } from './modules/prize.js'
export { appApi } from './modules/app.js'
export { tagApi } from './modules/baseTag.js'
export { typeApi } from './modules/baseType.js'
export { foodApi } from './modules/food.js'
export { recipeApi } from './modules/recipe.js'
export { recipesApi } from './modules/recipes.js'
export { dietPlanApi } from './modules/dietPlan.js'

// 导出请求方法
export { default as request } from './request.js'
export { getRequest, postRequest, deleteRequest, uploadRequest, postRequestJson } from './request.js'

export const appVersion = {
  // 获取所有族谱数据
  _getLatestVersionInfo: () => {
    return getRequest("/app/version")
  }
}