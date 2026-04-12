import { postRequest, getRequest, deleteRequest,postRequestJson } from '../request.js'

export const blogApi = {
  // 添加博客
  addBlog: (data) => {
    return postRequestJson("/blog/addBlog", data)
  },
  
  // 获取博客
  getBlog: (url) => {
    return getRequest(url)
  },
  
  // 根据条件获取博客
  getBlogParams: (url) => {
    return getRequest(url)
  },
  
  // 根据ID获取博客
  getBlogById: (id) => {
    return getRequest("/blog/getBlogByID", { bID: id })
  },
  
  // 删除博客
  deleteBlog: (id) => {
    return deleteRequest("/blog/delBlog", { bID: id })
  },
  
  // 逻辑删除博客
  deleteBlogH: (id, isdel) => {
    return deleteRequest("/blog/delBlogH", { bID: id, isdel: isdel })
  },
  
  // 保存博客
  saveBlog: (data) => {
    return postRequestJson("/blog/saveBlog", data)
  },
  
  // 增加浏览数
  addBrowseCount: (id) => {
    return postRequest("/blog/addBlogBrowseCount", { bID: id })
  },
  
  // 点赞/收藏
  toggleLikeOrCollection: (blogId, userId, type) => {
    return postRequest("/blog/like/toggle", { 
      bID: blogId, 
      uID: userId, 
      type: type 
    })
  },
  
  // 根据标题搜索博客
  getBlogByTitle: (url) => {
    return getRequest(url)
  }
}