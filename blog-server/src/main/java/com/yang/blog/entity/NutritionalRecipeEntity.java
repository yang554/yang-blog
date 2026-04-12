package com.yang.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel(value="NutritionalRecipeEntity", description="营养菜谱")
public class NutritionalRecipeEntity {
    private Long fID;    //编码
    private String fImg; //图片
    private String fName; //菜名
    private String fIntro; //简介
    private String fType; //菜系
    private String calories; //能量
    private String carbs; //碳水
    private String protein; //蛋白
    private String fat; //脂肪
    private String fBatching; //配料
    private String fSeasoning; //调料
    private String fMade; //做法
    private String fTips; //小贴士
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fCreateTime; //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fUpdateTime; //修改时间
    private String isCol; //是否收藏
    private Integer weight; //食材重量
    //食材的集合
    private List<FoodNutritionEntity> ingredients;

    public Long getfID() {
        return fID;
    }

    public void setfID(Long fID) {
        this.fID = fID;
    }

    public String getfImg() {
        return fImg;
    }

    public void setfImg(String fImg) {
        this.fImg = fImg;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getfIntro() {
        return fIntro;
    }

    public void setfIntro(String fIntro) {
        this.fIntro = fIntro;
    }

    public String getfType() {
        return fType;
    }

    public void setfType(String fType) {
        this.fType = fType;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getCarbs() {
        return carbs;
    }

    public void setCarbs(String carbs) {
        this.carbs = carbs;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getfBatching() {
        return fBatching;
    }

    public void setfBatching(String fBatching) {
        this.fBatching = fBatching;
    }

    public String getfSeasoning() {
        return fSeasoning;
    }

    public void setfSeasoning(String fSeasoning) {
        this.fSeasoning = fSeasoning;
    }

    public String getfMade() {
        return fMade;
    }

    public void setfMade(String fMade) {
        this.fMade = fMade;
    }

    public String getfTips() {
        return fTips;
    }

    public void setfTips(String fTips) {
        this.fTips = fTips;
    }

    public LocalDateTime getfCreateTime() {
        return fCreateTime;
    }

    public void setfCreateTime(LocalDateTime fCreateTime) {
        this.fCreateTime = fCreateTime;
    }

    public LocalDateTime getfUpdateTime() {
        return fUpdateTime;
    }

    public void setfUpdateTime(LocalDateTime fUpdateTime) {
        this.fUpdateTime = fUpdateTime;
    }

    public String getIsCol() {
        return isCol;
    }

    public void setIsCol(String isCol) {
        this.isCol = isCol;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public List<FoodNutritionEntity> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<FoodNutritionEntity> ingredients) {
        this.ingredients = ingredients;
    }
}
