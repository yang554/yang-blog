package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="UConsumptionCountEntity类", description="消费统计实体类")
public class UConsumptionCountEntity {
    @ApiModelProperty(value = "统计日期")
    private String consumptionTime;
    @ApiModelProperty(value = "消费情况")
    private String consumptionType;
    @ApiModelProperty(value = "消费金额")
    private String consumptionCost;

    public String getConsumptionTime() {
        return consumptionTime;
    }

    public void setConsumptionTime(String consumptionTime) {
        this.consumptionTime = consumptionTime;
    }

    public String getConsumptionType() {
        return consumptionType;
    }

    public void setConsumptionType(String consumptionType) {
        this.consumptionType = consumptionType;
    }

    public String getConsumptionCost() {
        return consumptionCost;
    }

    public void setConsumptionCost(String consumptionCost) {
        this.consumptionCost = consumptionCost;
    }
}
