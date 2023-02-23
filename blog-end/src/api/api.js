import { request, postKeyValueRequest, getRequest, postRequest, deleteRequest } from "./request.js"

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
//博客管理
export const _addBlog = (data) => {
    return postRequest("/blog/addBlog",data);
}
export const _getBlog = (data) => {
    return getRequest(data);
}