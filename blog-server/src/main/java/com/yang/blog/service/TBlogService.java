package com.yang.blog.service;

import com.yang.blog.entity.TBlogEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;

public interface TBlogService {
    //分页查询获取博客
    RespBean getBlogByPage(int page,int size ,Boolean ispublic, String isoriginal, Boolean issue, Boolean isdel,String title,String uID,String isLiked,String isCollection,String authorID,String type);
    //根据博客标题模糊查询
    RespBean getBlogByPageTitle(Boolean ispublic, String isoriginal, Boolean issue, Boolean isdel,String title,String uID,String isLiked,String isCollection,String type);
    //条件查询
    RespBean getBlogByParams(String title, String uID, List<String> tags, String type);
    //根据用户查询自己文章
    RespBean getBlogByUser(String uID);
    //根据博客ID查询
    RespBean getBlogByID(String bID);
    //添加博客
    RespBean addBlog(HashMap<String,Object> entity);
    //保存博客
    RespBean saveBlog(HashMap<String,Object> entity);
    //添加文章浏览量
    RespBean addBlogBrowseCount(String bID);
    //保存博客封面图片
    RespBean saveBlogCover(String cover,String id);
    //删除博客(永久)
    RespBean delBlog(String bID);
    //删除博客回收站\还原
    RespBean delBlogH(String bID,Boolean isdel);

    void syncClicksToDB();//每5分钟执行一次,添加博客文章点击量
}
