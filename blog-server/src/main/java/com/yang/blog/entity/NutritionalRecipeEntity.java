package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

import java.time.LocalDateTime;

@ApiModel(value="NutritionalRecipeEntity", description="营养菜谱")
public class NutritionalRecipeEntity {
    private Long fID;    //编码
    private String fImg; //图片
    private String fName; //菜名
    private String fIntro; //简介
    private String fType; //菜系
    private String fBatching; //配料
    private String fSeasoning; //调料
    private String fMade; //做法
    private String fTips; //小贴士
    private LocalDateTime fCreateTime; //创建时间
    private LocalDateTime fUpdateTime; //修改时间

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
}
