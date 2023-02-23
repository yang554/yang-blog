package com.yang.blog.service;

import com.yang.blog.entity.TBlogEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;

public interface TBlogService {
    //分页查询获取博客
    RespBean getBlogByPage(Boolean ispublic, String isoriginal, Boolean issue, Boolean isdel);
    //添加博客
    RespBean addBlog(HashMap<String,Object> entity);

}
