// 存储键名
export const STORAGE_KEYS = {
  TOKEN: 'ADMIN-TOKEN',
  USER_INFO: 'userInfo',
  CURRENT_CAPTCHA_KEY: 'current_captcha_key',
  THEME: 'theme',
  LANGUAGE: 'language',
  REMEMBER_ME:'rememberMe'
}

// API 状态码
export const API_CODES = {
  SUCCESS: 200,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  SERVER_ERROR: 500,
}

// 页面路径
export const PAGE_PATHS = {
  LOGIN: '/pages/login/login',	//登录页
  INDEX: '/pages/index/index',	//首页
  PROFILE: '/pages/profile/profile',	//
  AUNT_DASHBOARD: '/pages/aunt-dashboard/aunt-dashboard',	//经期看板
  BLOG_LIST: '/pages/all-blog/all-blog',	//博客列表
  BLOG_WRITE: '/pages/write/write',	//发布博客
  BLOG_EDIT: '/pages/edit-blog/edit-blog',	//编辑博客
  CLASS_BASIC_FEE: '/pages/basic-fee/basic-fee',	//课时费
  FAMILY_SOURCE: '/pages/family-source/family-source',	//族谱
  DAILY_FLOW_ADD: '/pages/add-daily-flow/add-daily-flow',	//添加账单
  DAILY_CONSUMPTION_TYPE: '/pages/consumption-type/consumption-type',	//流水类型
  DAILY_FLOW: '/pages/daily-flow/daily-flow',	//账单
  WEALTH_REPORT: '/pages/wealth-report/wealth-report',	//财富报表
  USER_DEPOSIT: '/pages/user-deposit/user-deposit',	//用户资产
  NUTRITIONAL_RECIPE: '/pages/nutritional-recipe/nutritional-recipe',	//菜谱
  NUTRITION_FOOD: '/pages/food-nutrition/food-nutrition',	//食物营养
  PRIZES: '/pages/prizes/prizes',	//抽奖
}