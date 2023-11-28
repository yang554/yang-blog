import { request, postKeyValueRequest, getRequest, postRequest, deleteRequest ,uploadRequest} from "./request.js"

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
    return getRequest("/user/getRoleAll");
}
export const _getCountByRole = ()=> {
    return getRequest("/user/count/role");
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
export const _delBlogH = (data) => {
    return deleteRequest("/blog/delBlogH?bID=" + data);
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
export const _editUserById = (data) => {
    return postRequest("/user/editUser",data);
}
export const _delUserById = (data) => {
    return deleteRequest("/user/delUser?id="+data);
}
//用户经期管理
export const _getAuntNameAll = () => {
    return getRequest("/aunt/all")
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
export const _addEvent = (data) => {
    return postRequest("/event/addEvent",data)
}