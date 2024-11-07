package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

import java.time.LocalDateTime;

@ApiModel(value="FoodNutritionEntity", description="食物营养成分")
public class FoodNutritionEntity {
    private Long fID;    //编码
    private String fImg; //图片
    private String fType; //类别
    private String fName; //名称
    private String fEdibleSection; //可食部
    private String fEnergy; //能量
    private String fCarbonWater; //碳水
    private String fFat; //脂肪
    private String fProtein; //蛋白质
    private String fDietaryFiber; //膳食纤维
    private String fVitaminA; //维A
    private String fVitaminC; //维C
    private String fVitaminD; //维D
    private String fVitaminE; //维E
    private String fVitaminK; //维K
    private String fVitaminB1; //维B1
    private String fVitaminB2; //维B2
    private String fVitaminB3; //维B3
    private String fVitaminB6; //维B6
    private String fVitaminB9; //维B9
    private String fVitaminB12; //维B12
    private String fK; //钾
    private String fNa; //钠
    private String fCa; //钙
    private String fZn; //锌
    private String fSe; //硒
    private String fFe; //铁
    private String fRole; //作用
    private LocalDateTime fCreateTime; //创建时间
    private LocalDateTime fUpdateTime; //修改时间
    private String fNote; //备注

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

    public String getfType() {
        return fType;
    }

    public void setfType(String fType) {
        this.fType = fType;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getfEdibleSection() {
        return fEdibleSection;
    }

    public void setfEdibleSection(String fEdibleSection) {
        this.fEdibleSection = fEdibleSection;
    }

    public String getfEnergy() {
        return fEnergy;
    }

    public void setfEnergy(String fEnergy) {
        this.fEnergy = fEnergy;
    }

    public String getfCarbonWater() {
        return fCarbonWater;
    }

    public void setfCarbonWater(String fCarbonWater) {
        this.fCarbonWater = fCarbonWater;
    }

    public String getfFat() {
        return fFat;
    }

    public void setfFat(String fFat) {
        this.fFat = fFat;
    }

    public String getfProtein() {
        return fProtein;
    }

    public void setfProtein(String fProtein) {
        this.fProtein = fProtein;
    }

    public String getfDietaryFiber() {
        return fDietaryFiber;
    }

    public void setfDietaryFiber(String fDietaryFiber) {
        this.fDietaryFiber = fDietaryFiber;
    }

    public String getfVitaminA() {
        return fVitaminA;
    }

    public void setfVitaminA(String fVitaminA) {
        this.fVitaminA = fVitaminA;
    }

    public String getfVitaminC() {
        return fVitaminC;
    }

    public void setfVitaminC(String fVitaminC) {
        this.fVitaminC = fVitaminC;
    }

    public String getfVitaminD() {
        return fVitaminD;
    }

    public void setfVitaminD(String fVitaminD) {
        this.fVitaminD = fVitaminD;
    }

    public String getfVitaminE() {
        return fVitaminE;
    }

    public void setfVitaminE(String fVitaminE) {
        this.fVitaminE = fVitaminE;
    }

    public String getfVitaminK() {
        return fVitaminK;
    }

    public void setfVitaminK(String fVitaminK) {
        this.fVitaminK = fVitaminK;
    }

    public String getfVitaminB1() {
        return fVitaminB1;
    }

    public void setfVitaminB1(String fVitaminB1) {
        this.fVitaminB1 = fVitaminB1;
    }

    public String getfVitaminB2() {
        return fVitaminB2;
    }

    public void setfVitaminB2(String fVitaminB2) {
        this.fVitaminB2 = fVitaminB2;
    }

    public String getfVitaminB3() {
        return fVitaminB3;
    }

    public void setfVitaminB3(String fVitaminB3) {
        this.fVitaminB3 = fVitaminB3;
    }

    public String getfVitaminB6() {
        return fVitaminB6;
    }

    public void setfVitaminB6(String fVitaminB6) {
        this.fVitaminB6 = fVitaminB6;
    }

    public String getfVitaminB9() {
        return fVitaminB9;
    }

    public void setfVitaminB9(String fVitaminB9) {
        this.fVitaminB9 = fVitaminB9;
    }

    public String getfVitaminB12() {
        return fVitaminB12;
    }

    public void setfVitaminB12(String fVitaminB12) {
        this.fVitaminB12 = fVitaminB12;
    }

    public String getfK() {
        return fK;
    }

    public void setfK(String fK) {
        this.fK = fK;
    }

    public String getfNa() {
        return fNa;
    }

    public void setfNa(String fNa) {
        this.fNa = fNa;
    }

    public String getfCa() {
        return fCa;
    }

    public void setfCa(String fCa) {
        this.fCa = fCa;
    }

    public String getfZn() {
        return fZn;
    }

    public void setfZn(String fZn) {
        this.fZn = fZn;
    }

    public String getfSe() {
        return fSe;
    }

    public void setfSe(String fSe) {
        this.fSe = fSe;
    }

    public String getfFe() {
        return fFe;
    }

    public void setfFe(String fFe) {
        this.fFe = fFe;
    }

    public String getfRole() {
        return fRole;
    }

    public void setfRole(String fRole) {
        this.fRole = fRole;
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

    public String getfNote() {
        return fNote;
    }

    public void setfNote(String fNote) {
        this.fNote = fNote;
    }
}
