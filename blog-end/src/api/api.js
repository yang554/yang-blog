import { request, postKeyValueRequest, getRequest, postRequest, deleteRequest ,uploadRequest,postRequestJson} from "./request.js"

//登录
export const _userLogin = (data) => {
    return postRequest("/user/login", data);
}
//注册
export const _userRegister = (data) => {
    return postRequest("/user/register", data);
}
//分类专栏
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
//云标签管理
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
//文件上传管理
export const _uploadImgs = (data) => {
    return uploadRequest("/file/uploadAvatar",data)
}
//权限管理
export const _getRoleAll = () => {
    return getRequest("/role/all");
}
export const _getCountByRole = ()=> {
    return getRequest("/user/count/role");
}
export const _addRole = (data) => {
    return postRequest("/role/addRole",data);
}
export const _updRole = (data) => {
    return postRequest("/role/updRole",data);
}
export const _delRole = (data) => {
    return deleteRequest("/role/delRole?id="+data);
}
//博客管理
export const _addBlog = (data) => {
    return postRequest("/blog/addBlog",data);
}
export const _getBlog = (data) => {
    return getRequest(data);
}
export const _getBlogById = (data) => {
    return getRequest("/blog/getBlogByID?bID="+data)
}
export const _delBlog = (data) => {
    return deleteRequest("/blog/delBlog?bID=" + data);
}
export const _delBlogH = (id,isdel) => {
    return deleteRequest("/blog/delBlogH?bID=" + id+"&isdel="+isdel);
}
export const _saveBlog = (data) => {
    return postRequest("/blog/saveBlog",data);
}
export const _getBlogByTile = (data) => {
    return getRequest(data);
}
//用户管理
export const _findUser = (data) => {
    return postRequest("/user/findUser",data);
}
export const _getUserAll = () => {
    return getRequest("/user/all");
}
export const _addUser = (data) => {
    return postRequest("/user/addUser",data);
}
export const _getUserByName = (data) => {
    return getRequest("/user/selectUserByName?name="+data);
}
export const _getUserByNameLike = (data) => {
    return getRequest("/user/getUserByName?name="+data);
}
export const _editUserById = (data) => {
    return postRequest("/user/editUser",data);
}
export const _resetUserPwd = (data) => {
    return postRequest("/user/resetUserPwd",data);
}
export const _delUserById = (data) => {
    return deleteRequest("/user/delUser?id="+data);
}
//用户经期管理
export const _getByALL = () => {
    return getRequest("/aunt/getByALL")
}
export const _getAuntNameAll = () => {
    return getRequest("/aunt/all")
}
export const _selectByName = (data) => {
    return getRequest("/aunt/selectByName?uName="+data)
}
export const _getAuntByNameAll = (uName,status) => {
    return getRequest("/aunt/selectByNameAll?uName="+uName+"&uStatus="+status)
}
export const _addAunt = (data) => {
    return postRequest("/aunt/addAunt",data)
}
export const _updAunt = (data) => {
    return postRequest("/aunt/updAunt",data)
}
export const _delAunt = (data) => {
    return deleteRequest("/aunt/delAunt?id="+data);
}
export const _getEventAll = (data) => {
    return getRequest(data)
} 
export const _addEvent = (data) => {
    return postRequest("/event/addEvent",data)
}
export const _updEvent = (data) => {
    return postRequest("/event/updEvent",data)
}
export const _delEventById = (data) => {
    return deleteRequest("/event/delAunt?id="+data);
}
//族谱管理
export const _getSourceAll = () => {
    return getRequest("/source/getSourceAll")
}
export const _getSourceCount = () => {
    return getRequest("/source/getSourceCount")
}
export const _getUserCountBySexDX = () => {
    return getRequest("/source/getUserCountBySexDX")
}
export const _getSourceCountByDX = () => {
    return getRequest("/source/getSourceCountByDX?search="+'杨')
}
export const _getUserCountBySex = () => {
    return getRequest("/source/getUserCountBySex")
}
export const _getUserCountByLove = () => {
    return getRequest("/source/getUserCountByLove")
}
export const _getUserCountByLoveAndSex = () => {
    return getRequest("/source/getUserCountByLoveAndSex")
}
export const _addSource = (data) => {
    return postRequest("/source/addSource",data);
}
export const _addParents = (data) => {
    return postRequestJson("/source/addParents",data);
}
export const _saveSource = (data) => {
    return postRequestJson("/source/saveSource",data);
}
export const _delSource = (data) => {
    return postRequest("/source/delSource",data);
}
//营养管理-体成分分析
export const _getNutritionAll = () => {
    return getRequest("/nutrition/all")
}
export const _getNutritionById = (data) => {
    return getRequest("/nutrition/findById?id="+data)
}
export const _getNutritionByUser = (data) => {
    return getRequest("/nutrition/findByUser?uID=+"+data)
}
export const _operationNBC = (data) => {
    return postRequest("/nutrition/operationNBC",data);
}
export const _delNutritionById = (data) => {
    return deleteRequest("/nutrition/delNBC?id="+data);
}
//营养管理-食物营养
export const _getFoodNutritionAll = () => {
    return getRequest("/foodNutrition/all")
}
export const _getFoodNutritionByFactor = (data) => {
    return getRequest("/foodNutrition/findByFactor?param="+data)
}
export const _operationFoodNutrition = (data) => {
    return postRequest("/foodNutrition/operationFood",data);
}
export const _delFoodNutritionById = (data) => {
    return deleteRequest("/foodNutrition/delFood?id="+data);
}
//营养管理-食物菜谱
export const _getNutritionalRecipeAll = () => {
    return getRequest("/NutritionalRecipe/all")
}
export const _getNutritionalRecipeByFactor = (name,type) => {
    return getRequest("/NutritionalRecipe/findByFactor?name="+name+"&type="+type)
}
export const _getNutritionalRecipeById = (data) => {
    return getRequest("/NutritionalRecipe/findById?id="+data)
}
export const _operationNutritionalRecipe = (data) => {
    return postRequest("/NutritionalRecipe/operationRecipe",data);
}
export const _delNutritionalRecipe = (data) => {
    return deleteRequest("/NutritionalRecipe/delRecipe?id="+data);
}