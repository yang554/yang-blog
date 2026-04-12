package com.yang.blog.service;

import com.yang.blog.entity.NutritionRecipeFoodEntity;

import java.util.List;

public interface NutritionRecipeFoodService {
    //根据用户收藏查询所有记录
    List<NutritionRecipeFoodEntity> getByRecipeId(String recipeId);
}
