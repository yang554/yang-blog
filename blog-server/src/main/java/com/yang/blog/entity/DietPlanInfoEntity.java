package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

@ApiModel(value="DietPlanInfoEntity", description="饮食计划详细")
public class DietPlanInfoEntity {
    private Long id;    //编码
    private String time; //餐时【早餐、午餐、晚餐】
    private String foodName; //菜名
    private String energy; //能量
    private String fat; //脂肪
    private String carbonWater; //碳水
    private String protein; //蛋白质
    private String planID; //计划ID
    private String createTime;    //创建时间
    private String note;    //备注

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getCarbonWater() {
        return carbonWater;
    }

    public void setCarbonWater(String carbonWater) {
        this.carbonWater = carbonWater;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getPlanID() {
        return planID;
    }

    public void setPlanID(String planID) {
        this.planID = planID;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
