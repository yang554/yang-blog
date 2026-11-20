import { request, postKeyValueRequest, getRequest, postRequest, deleteRequest, uploadRequest, postRequestJson ,putRequest} from "./request.js"
import { getToken } from '@/utils/auth'
// ArrayBuffer 转 Base64
function arrayBufferToBase64(buffer) {
    if (typeof uni !== 'undefined' && uni.arrayBufferToBase64) {
        return uni.arrayBufferToBase64(buffer)
    }

    // 兼容 H5 环境
    if (typeof btoa !== 'undefined') {
        let binary = ''
        const bytes = new Uint8Array(buffer)
        const len = bytes.byteLength
        for (let i = 0; i < len; i++) {
            binary += String.fromCharCode(bytes[i])
        }
        return btoa(binary)
    }

    return buffer
}

//------------------获取验证码
export const _getCaptchaImg = (data) => {
    return new Promise((resolve, reject) => {
        request({
            url: '/user/getCaptcha',
            method: 'GET',
            responseType: 'arraybuffer', //------------------ 关键：接收二进制数据
            headers: {
                'Content-Type': 'application/octet-stream'
            }
        })
            .then(res => {
                if (res.status === 200) {
                    //------------------ 转换 ArrayBuffer 为 Base64
                    const base64 = arrayBufferToBase64(res.data)
                    const imageUrl = `data:image/gif;base64,${base64}`

                    //------------------ 从响应头获取 captchaKey 信息
                    const captchaKey = res.headers['x-captcha-key']
                    const captchaExpire = res.headers['x-captcha-expire']

                    resolve({
                        status: 200,
                        data: {
                            imageUrl: imageUrl,
                            captchaKey: captchaKey,
                            captchaExpire: captchaExpire
                        }
                    })
                } else {
                    reject(new Error(`HTTP ${res.status}`))
                }
            })
            .catch(err => {
                reject(err)
            })
    })
}
//------------------登录
export const _userLogin = (data) => {
    return postRequest("/user/login", data);
}
//------------------注销
export const _userLogout = () => {
    return postRequest("/user/logout");
}
//------------------注册
export const _userRegister = (data) => {
    return postRequest("/user/register", data);
}
//------------------分类专栏
export const _typeAll = () => {
    return getRequest("/type/all");
}
export const _typeByName = (data) => {
    return getRequest("/type/getTypeByName?name=" + data);
}
export const _addType = (data) => {
    return postRequest("/type/add", data);
}
export const _updTypeById = (data) => {
    return postRequest("/type/updTypeById", data);
}
export const _delTypeById = (data) => {
    return deleteRequest("/type/delTypeById?id=" + data);
}
export const _getUserRoleAll = () => {
    return getRequest("/type/count/type");
}
//------------------云标签管理
export const _getTagAll = () => {
    return getRequest("/tag/all");
}
export const _getTagByName = (data) => {
    return request("/tag/getTagByName?name=" + data);
}
export const _addTag = (data) => {
    return postRequest("/tag/addTag", data);
}
export const _updTagById = (data) => {
    return postRequest("/tag/updTagById", data);
}
export const _delTagById = (data) => {
    return deleteRequest("/tag/delTagById?id=" + data);
}
//------------------文件上传管理
export const _uploadImgs = (data) => {
    return uploadRequest("/file/uploadAvatar", data)
}
//------------------权限管理
export const _getRoleAll = () => {
    return getRequest("/role/all");
}
export const _getCountByRole = () => {
    return getRequest("/user/count/role");
}
export const _addRole = (data) => {
    return postRequest("/role/addRole", data);
}
export const _updRole = (data) => {
    return postRequest("/role/updRole", data);
}
export const _delRole = (data) => {
    return deleteRequest("/role/delRole?id=" + data);
}
//------------------博客管理
export const _addBlog = (data) => {
    return postRequest("/blog/addBlog", data);
}
export const _getBlog = (data) => {
    return getRequest(data);
}
export const _getBlogById = (data) => {
    return getRequest("/blog/getBlogByID?bID=" + data)
}
export const _delBlog = (data) => {
    return deleteRequest("/blog/delBlog?bID=" + data);
}
export const _delBlogH = (id, isdel) => {
    return deleteRequest("/blog/delBlogH?bID=" + id + "&isdel=" + isdel);
}
export const _saveBlog = (data) => {
    return postRequest("/blog/saveBlog", data);
}
export const _addBlogBrowseCount = (id) => {
    return postRequest("/blog/addBlogBrowseCount?bID=" + id);
}
export const _addBlogLikesOrCollectionCount = (bID, uID, typeOp) => {
    return postRequest("/blog/like/toggle?bID=" + bID + "&uID=" + uID + "&type=" + typeOp);
}
export const _getBlogByTile = (data) => {
    return getRequest(data);
}
//------------------用户管理
export const _findUser = (data) => {
    return postRequest("/user/findUser", data);
}
export const _getUserAll = () => {
    return getRequest("/user/all");
}
export const _addUser = (data) => {
    return postRequest("/user/addUser", data);
}
export const _getUserByName = (data) => {
    return getRequest("/user/selectUserByName?name=" + data);
}
export const _getUserByNameLike = (data) => {
    return getRequest("/user/getUserByName?name=" + data);
}
export const _editUserById = (data) => {
    return postRequest("/user/editUser", data);
}
export const _resetUserPwd = (data) => {
    return postRequest("/user/resetUserPwd", data);
}
export const _delUserById = (data) => {
    return deleteRequest("/user/delUser?id=" + data);
}
//------------------用户经期管理
export const _getByALL = () => {
    return getRequest("/aunt/getByALL")
}
export const _getAuntNameAll = () => {
    return getRequest("/aunt/all")
}
export const _selectByName = (data) => {
    return getRequest("/aunt/selectByName?uName=" + data)
}
export const _getAuntByNameAll = (uName, status) => {
    return getRequest("/aunt/selectByNameAll?uName=" + uName + "&uStatus=" + status)
}
export const _addAunt = (data) => {
    return postRequest("/aunt/addAunt", data)
}
export const _updAunt = (data) => {
    return postRequest("/aunt/updAunt", data)
}
export const _delAunt = (data) => {
    return deleteRequest("/aunt/delAunt?id=" + data);
}
export const _getEventAll = (data) => {
    return getRequest(data)
}
export const _addEvent = (data) => {
    return postRequest("/event/addEvent", data)
}
export const _updEvent = (data) => {
    return postRequest("/event/updEvent", data)
}
export const _delEventById = (data) => {
    return deleteRequest("/event/delAunt?id=" + data);
}
//------------------ -- 族谱管理  ---
export const _getFamilyTree = () => { //查询所有族谱的统计信息
    return getRequest("/TFamilyTreeController/getFamilyTree")
}
export const _getAll = () => { //查询所有族谱
    return getRequest("/TFamilyTreeController/getAll")
}
export const _operationData = (data) => {  //修改/添加族谱
    return postRequest("/TFamilyTreeController/operationData",data)
}
export const _delTree = (data) => { //删除族谱
    return deleteRequest("/TFamilyTreeController/delTree?id=" + data);
}
export const _getSourceAll = (pTree) => { //根据族谱ID获取所有成员
    return getRequest("/source/getSourceAll?pTree="+pTree)
}
export const _getSourceCount = () => {
    return getRequest("/source/getSourceCount")
}
export const _getUserCountBySexDX = () => {
    return getRequest("/source/getUserCountBySexDX")
}
export const _getSourceCountByDX = () => {
    return getRequest("/source/getSourceCountByDX?search=" + '杨')
}
export const _getUserCountBySex = () => {
    return getRequest("/source/getUserCountBySex")
}
export const _getUserCountByLove = (pTree) => {  //根据族谱ID代系获取人口数量
    return getRequest("/source/getUserCountByLove?pTree="+pTree)
}
export const _getUserCountByLoveAndSex = (pTree) => {    //根据族谱ID代系获取男女数量
    return getRequest("/source/getUserCountByLoveAndSex?pTree="+pTree)
}
export const _addSource = (data) => {
    return postRequest("/source/addSource", data);
}
export const _addParents = (data) => {
    return postRequestJson("/source/addParents", data);
}
export const _saveSource = (data) => {
    return postRequest("/source/saveSource", data);
}
export const _delSource = (data) => {
    return postRequest("/source/delSource", data);
}
//------------------营养管理-体成分分析
export const _getNutritionAll = () => {
    return getRequest("/nutrition/all")
}
export const _getNutritionById = (data) => {
    return getRequest("/nutrition/findById?id=" + data)
}
export const _getNutritionByUser = (data) => {
    return getRequest("/nutrition/findByUser?uID=+" + data)
}
export const _operationNBC = (data) => {
    return postRequest("/nutrition/operationNBC", data);
}
export const _delNutritionById = (data) => {
    return deleteRequest("/nutrition/delNBC?id=" + data);
}
//------------------营养管理-食物营养
export const _getFoodNutritionAll = () => {
    return getRequest("/foodNutrition/all")
}
export const _getFoodNutritionByFactor = (data) => {
    return getRequest("/foodNutrition/findByFactor?param=" + data)
}
export const _insertBatchFood = (data) => {
    return postRequest("/foodNutrition/insertBatch", data)
}
export const _operationFoodNutrition = (data) => {
    return postRequest("/foodNutrition/operationFood", data);
}
export const _delFoodNutritionById = (data) => {
    return deleteRequest("/foodNutrition/delFood?id=" + data);
}
//------------------菜谱&食材关系
export const _getByRecipeId = (recipeId) => {
    return getRequest("/RecipeFood/getByRecipeId?recipeId=" + recipeId)
}
//------------------营养管理-食物菜谱
export const _getNutritionalRecipeAll = () => {
    return getRequest("/NutritionalRecipe/all")
}
export const _getAllByUserCol = (uID) => {
    return getRequest("/NutritionalRecipe/findAllByUserCol?uID=" + uID)
}
export const _getFoodType = () => {
    return getRequest("/NutritionalRecipe/getFoodType")
}
export const _getNutritionalRecipeByFactor = (name, type) => {
    return getRequest("/NutritionalRecipe/findByFactor?name=" + name + "&type=" + type)
}
export const _getNutritionalRecipeById = (data) => {
    return getRequest("/NutritionalRecipe/findById?id=" + data)
}
export const _addFoodLikesOrCollectionCount = (fID, uID, typeOp) => {
    return postRequest("/food/likeOrCol/toggle?fID=" + fID + "&uID=" + uID + "&type=" + typeOp);
}
export const _operationNutritionalRecipe = (data) => {
    return postRequest("/NutritionalRecipe/operationRecipe", data);
}
export const _delNutritionalRecipe = (data) => {
    return deleteRequest("/NutritionalRecipe/delRecipe?id=" + data);
}
//------------------营养管理-营养食谱
export const _getNutritionalRecipesAll = () => {
    return getRequest("/NutritionalRecipes/all")
}
export const _getNutritionalRecipesByFactor = (name, type, target) => {
    return getRequest("/NutritionalRecipes/findByFactor?name=" + name + "&type=" + type + "&target=" + target)
}
export const _getNutritionalRecipesById = (data) => {
    return getRequest("/NutritionalRecipes/findById?id=" + data)
}
export const _operationNutritionalRecipes = (data) => {
    return postRequest("/NutritionalRecipes/operationRecipes", data);
}
export const _delNutritionalRecipes = (data) => {
    return deleteRequest("/NutritionalRecipes/delRecipes?id=" + data);
}
//------------------饮食计划
export const _getDietPlanByUid = (createID, createDate) => {
    return getRequest("/dietPlan/getDietPlanByUid?createID=" + createID + "&createDate=" + createDate)
}
export const _getDietPlanByDateScope = (createID, startDate,endDate) => {
    return getRequest(`/dietPlan/getDietPlanByDateScope?createID=${createID}&startDate=${startDate}&endDate=${endDate}`)
}
export const _operationDietPlan = (data) => {
    return postRequest("/dietPlan/operationDietPlan", data);
}
//------------------人生轨迹-每日流水
export const _getDailyFlowAll = (data) => {
    return getRequest("/DailyFlowController/findAllById?id=" + data);
}
export const _getDailyFlowInfoAll = () => {
    return getRequest("/DailyFlowInfoController/all");
}
export const _findByFactor = (data) => {
    return getRequest("/DailyFlowController/findByFactor?" + data)
}
export const _findByFactorInfo = (data) => {
    return getRequest("/DailyFlowInfoController/findByFactorInfo?" + data)
}
export const _getConsumptionCount = (search, consumptionType, getCount, queryId) => {
    return getRequest("/DailyFlowController/getConsumptionCount?search=" + search + "&consumptionType=" + consumptionType + "&getCount=" + getCount + "&queryId=" + queryId)
}
export const _getConsumptionCountDInfo = (consumptionType, time, queryId) => {
    return getRequest("/DailyFlowController/getConsumptionCountDInfo?consumptionType=" + consumptionType + "&consumptionTime=" + time + "&queryId=" + queryId)
}
export const _getConsumptionCountMInfo = (consumptionType, time, queryId) => {
    return getRequest("/DailyFlowController/getConsumptionCountMInfo?consumptionType=" + consumptionType + "&consumptionTime=" + time + "&queryId=" + queryId)
}
export const _getConsumptionCountYInfo = (consumptionType, time, queryId) => {
    return getRequest("/DailyFlowController/getConsumptionCountYInfo?consumptionType=" + consumptionType + "&consumptionTime=" + time + "&queryId=" + queryId)
}
export const _getRawData = (data) => {
    return getRequest("/DailyFlowController/getRawData?" + data)
}
export const _getAvailableYears = (userId) => {
    return getRequest(`/DailyFlowController/years/${userId}`)
}
export const _operationDailyFlow = (data) => {
    return postRequest("/DailyFlowController/operationDailyFlow", data);
}
export const _getByFactor = (data) => {
    return getRequest("/DailyFlowInfoController/findByFactor?fID=" + data);
}
export const _insertBatch = (data) => {
    return postRequest("/DailyFlowInfoController/insertBatch", data);
}
export const _delFlow = (data) => {
    return deleteRequest("/DailyFlowController/delFlow?id=" + data);
}
//------------------流水类型
export const _getConsumptionTypeAll = () => {
    return getRequest("/consumption/getConsumptionTypeAll");
}
export const _getMatchSource = (data) => {
    return getRequest("/consumption/getMatchSource?purpose=" + data)
}
export const _getConsumptionTypeByName = (data) => {
    return request("/consumption/getConsumptionTypeByName?name=" + data);
}
export const _addConsumptionType = (data) => {
    return postRequest("/consumption/addConsumptionType", data);
}
export const _updConsumptionTypeById = (data) => {
    return postRequest("/consumption/updConsumptionTypeById", data);
}
export const _delConsumptionType = (data) => {
    return deleteRequest("/consumption/delConsumptionType?id=" + data);
}
//------------------存款金额
export const _updUserDepositById = (data) => {
    return postRequest("/UserDeposit/updUserDepositById", data);
}
export const _getUserDeposit = () => {
    return getRequest("/UserDeposit/findAll");
}
export const _getUserDepositById = (data) => {
    return getRequest("/UserDeposit/getUserDepositById?id=" + data);
}
export const _getUserDepositByName = (name) => {
    return getRequest("/UserDeposit/getUserDepositByName?name=" + name);
}
//------------------抽奖系统
export const _getPrizeAll = () => {
    return getRequest("/prize/all");
}
export const _operationPrize = (data) => {
    return postRequest("/prize/operationPrize", data);
}
export const _prizeDram = () => {
    return getRequest("/prize/dram");
}
//------------------app管理
export const _uploadWgtFile = (data) => {
    return uploadRequest("/app/upload",data)
}
export const _getVersionLis = (data) =>  {
    return getRequest("/app/list?"+data);
}
export const _updateVersion = (data) => {
    return postRequest("/app/update", data);
}
export const _deleteVersion = (data) =>  {
    return deleteRequest("/app/delete?id=" + data);
}

//------------------其他功能---------------------
export const addProduct = (data) => {
    return postRequest("/product/add",data) //添加商品信息
}
export const getProduct = () => {
    return getRequest("/product/all")   //获取所有商品信息
}
export const editProduct = (data) => {
    return putRequest("/product/edit",data) //编辑商品信息
}
export const delProduct = (id) => {
    return deleteRequest(`/product/delete/${id}`) //删除商品信息(软删除)
}

export const addPriceRecord = (data) => {
    return postRequest(`/price/add`,data) //添加商品历史价格记录
}
export const getRecordsByProductId = (id) => {
    return getRequest(`/price/product/${id}`) //根据商品ID查询价格记录
}
export const delPriceRecord = (id) => {
    return deleteRequest(`/price/delete/${id}`) //删除商品信息(物理删除)
}