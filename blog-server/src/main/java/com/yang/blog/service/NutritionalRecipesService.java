package com.yang.blog.service;

import com.yang.blog.entity.NutritionalRecipesEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;

public interface NutritionalRecipesService {
    //查询所有记录
    List<NutritionalRecipesEntity> findAll();
    //条件查询
    List<NutritionalRecipesEntity> findByFactor(String name, String type,String target);
    //根据ID查询
    RespBean findById(String id);
    //添加菜谱
    RespBean operationRecipes(HashMap<String, Object> hashMap);
    //删除菜谱
    RespBean delRecipes(String id);
}
