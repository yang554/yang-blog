package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

import java.math.BigDecimal;
import java.util.List;

@ApiModel(value="DailyFlowInfoEntity", description="每日流水明细")
public class DailyFlowInfoEntity {
    private Long id;    //编码
    private String purpose; //用途
    private String type; //类型（收入或者支出）
    private Double cost; //费用
    private String note; //备注
    private Long fID;    //外键ID
    private String typeTagID;
    private String uName;   //制单人

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getfID() {
        return fID;
    }

    public void setfID(Long fID) {
        this.fID = fID;
    }

    public String getTypeTagID() {
        return typeTagID;
    }

    public void setTypeTagID(String typeTagID) {
        this.typeTagID = typeTagID;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}
