package com.yang.blog.service.impl;

import com.yang.blog.entity.NutritionRecipeFoodEntity;
import com.yang.blog.mapper.NutritionRecipeFoodMapper;
import com.yang.blog.service.NutritionRecipeFoodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class NutritionRecipeFoodServiceImpl implements NutritionRecipeFoodService {

    @Autowired
    private NutritionRecipeFoodMapper foodMapper;

    @Override
    public List<NutritionRecipeFoodEntity> getByRecipeId(String recipeId) {
        return foodMapper.getByRecipeId(recipeId);
    }
}
