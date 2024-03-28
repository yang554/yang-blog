package com.yang.blog.service;

import com.yang.blog.entity.TBlogEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;

public interface TBlogService {
    //分页查询获取博客
    RespBean getBlogByPage(Boolean ispublic, String isoriginal, Boolean issue, Boolean isdel);
    //根据博客标题模糊查询
    RespBean getBlogByPageTitle(Boolean ispublic, String isoriginal, Boolean issue, Boolean isdel,String title);
    //根据博客ID查询
    RespBean getBlogByID(String bID);
    //添加博客
    RespBean addBlog(HashMap<String,Object> entity);
    //保存博客
    RespBean saveBlog(HashMap<String,Object> entity);
    //保存博客封面图片
    RespBean saveBlogCover(String cover,String id);
    //删除博客(永久)
    RespBean delBlog(String bID);
    //删除博客回收站\还原
    RespBean delBlogH(String bID,Boolean isdel);
}
