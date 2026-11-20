package com.yang.blog.mapper;

import com.yang.blog.entity.NutritionRecipeFoodEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface NutritionRecipeFoodMapper {
    //根据食谱ID查询
    List<NutritionRecipeFoodEntity> getByRecipeId(@Param("recipeId") String recipeId);
    //批量添加收藏关系
    int insertBatch(List<NutritionRecipeFoodEntity> entities);
    //批量删除收藏关系
    int deleteBatch(List<NutritionRecipeFoodEntity> entities);
    //根据食谱ID批量删除
    int delBatchByRecipeId(List<NutritionRecipeFoodEntity> entities);
    //根据食谱ID删除
    int delByRecipeId(String id);
}
