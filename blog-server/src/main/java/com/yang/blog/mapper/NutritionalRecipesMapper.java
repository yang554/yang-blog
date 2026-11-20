package com.yang.blog.mapper;

import com.yang.blog.entity.NutritionalRecipesEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface NutritionalRecipesMapper {
    //查询所有记录
    List<NutritionalRecipesEntity> findAll();
    //条件查询
    List<NutritionalRecipesEntity> findByFactor(@Param("name") String name, @Param("type") String type, @Param("target") String target);
    //根据ID查询
    NutritionalRecipesEntity findById(String id);
    //添加食谱
    int addRecipes(NutritionalRecipesEntity entity);
    //修改食谱
    int updRecipes(NutritionalRecipesEntity entity);
    //删除食谱
    int delRecipes(String id);
}
