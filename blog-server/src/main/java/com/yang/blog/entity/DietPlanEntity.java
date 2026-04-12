package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel(value="DietPlanEntity", description="饮食计划")
public class DietPlanEntity {
    private Long id;    //编码
    private String energy; //能量
    private String fat; //脂肪
    private String carbonWater; //碳水
    private String protein; //蛋白质
    private String createDate;    //创建日期
    private String createID; //创建人ID
    private String note;    //备注
    private String op ; //操作
    private List<DietPlanInfoEntity> planInfoEntities; //饮食计划详细

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateID() {
        return createID;
    }

    public void setCreateID(String createID) {
        this.createID = createID;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public List<DietPlanInfoEntity> getPlanInfoEntities() {
        return planInfoEntities;
    }

    public void setPlanInfoEntities(List<DietPlanInfoEntity> planInfoEntities) {
        this.planInfoEntities = planInfoEntities;
    }
}
