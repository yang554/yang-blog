package com.yang.blog.mapper;

import com.yang.blog.entity.NutritionalRecipeEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface NutritionalRecipeMapper {
    //查询所有记录
    List<NutritionalRecipeEntity> findAll();
    //条件查询
    List<NutritionalRecipeEntity> findByFactor(@Param("name")String name, @Param("type")String type);
    //根据ID查询
    NutritionalRecipeEntity findById(String id);
    //添加菜谱
    int addRecipe(NutritionalRecipeEntity entity);
    //修改菜谱
    int updRecipe(NutritionalRecipeEntity entity);
    //删除菜谱
    int delRecipe(String id);
}
