package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="u_consumption_type类", description="消费类型实体类")
public class UConsumptionTypeEntity {
    @ApiModelProperty(value = "编码")
    private Long id;
    @ApiModelProperty(value = "标签名称")
    private String name;
    @ApiModelProperty(value = "关键词")
    private String  keywords;
    @ApiModelProperty(value = "标签颜色")
    private String color;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
