package com.yang.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;

import java.time.LocalDateTime;

@ApiModel(value="NutritionalRecipesEntity", description="营养食谱")
public class NutritionalRecipesEntity {
    private Long fID;    //编码
    private String fName; //食谱名
    private String fIntro; //简介
    private String fType; //食谱类型
    private String zMeal; //早餐
    private String zSnack; //早加餐
    private String aMeal; //午餐
    private String aSnack; //午加餐
    private String nMeal; //晚餐
    private String nSnack; //晚加餐
    private String fTarget; //目标人群
    private Long uID;   //创建人ID
    private String uName;   //创建人
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fCreateTime; //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fUpdateTime; //修改时间
    private String fNote;   //备注

    public Long getfID() {
        return fID;
    }

    public void setfID(Long fID) {
        this.fID = fID;
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

    public String getzMeal() {
        return zMeal;
    }

    public void setzMeal(String zMeal) {
        this.zMeal = zMeal;
    }

    public String getzSnack() {
        return zSnack;
    }

    public void setzSnack(String zSnack) {
        this.zSnack = zSnack;
    }

    public String getaMeal() {
        return aMeal;
    }

    public void setaMeal(String aMeal) {
        this.aMeal = aMeal;
    }

    public String getaSnack() {
        return aSnack;
    }

    public void setaSnack(String aSnack) {
        this.aSnack = aSnack;
    }

    public String getnMeal() {
        return nMeal;
    }

    public void setnMeal(String nMeal) {
        this.nMeal = nMeal;
    }

    public String getnSnack() {
        return nSnack;
    }

    public void setnSnack(String nSnack) {
        this.nSnack = nSnack;
    }

    public String getfTarget() {
        return fTarget;
    }

    public void setfTarget(String fTarget) {
        this.fTarget = fTarget;
    }

    public Long getuID() {
        return uID;
    }

    public void setuID(Long uID) {
        this.uID = uID;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
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
