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
    return getRequest("/type/getTypeByName/" + data);
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