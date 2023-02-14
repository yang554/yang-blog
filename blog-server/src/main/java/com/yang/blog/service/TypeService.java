package com.yang.blog.service;

import com.yang.blog.entity.TypeEntity;
import com.yang.blog.utils.RespBean;

public interface TypeService {
    //查询所有分类
    RespBean getTypeAll();
    //分页查询
    RespBean getTypeByPage(Integer currentPage,Integer pageSize);
    //根据分类ID查询
    RespBean getTypeById(String id);
    //根据分类名称查询
    RespBean getTypeByName(String name);
    //添加分类
    RespBean addType(String name);
    //编辑分类
    RespBean updTypeById(TypeEntity entity);
    //删除分类
    RespBean delTypeById(String id);
    //批量删除分类
    RespBean delType();
}
