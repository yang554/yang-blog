package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel(value="NutritionRecipeFoodEntity", description="营养菜谱")
public class NutritionRecipeFoodEntity {
    private Long ID;    //编码
    private Long foodId;    //食材编码
    private Long recipeId;    //菜谱编码
    private Integer weight;    //重量
    private String createTime;    //创建时间

    //食材信息
    private FoodNutritionEntity ingredient;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public FoodNutritionEntity getIngredient() {
        return ingredient;
    }

    public void setIngredient(FoodNutritionEntity ingredient) {
        this.ingredient = ingredient;
    }
}
