package com.yang.blog.controller;

import com.yang.blog.entity.NutritionRecipeFoodEntity;
import com.yang.blog.service.NutritionRecipeFoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/RecipeFood")
public class NutritionRecipeFoodController {
    @Autowired
    private NutritionRecipeFoodService service;

    //根据ID获取记录
    @GetMapping("/getByRecipeId")
    public List<NutritionRecipeFoodEntity> getByRecipeId(@RequestParam("recipeId") String recipeId){
        return service.getByRecipeId(recipeId);
    }
}
