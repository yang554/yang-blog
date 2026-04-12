package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="t_family_tree", description="族谱")
public class TFamilyTreeEntity {
    @ApiModelProperty(value = "编码")
    private Long id;        //编码
    @ApiModelProperty(value = "名称")
    private String name;    //名称
    @ApiModelProperty(value = "卷")
    private String generation;  //卷
    @ApiModelProperty(value = "封面名")
    private String displayName; //封面名
    @ApiModelProperty(value = "创建人ID")
    private Long createID;  //创建人ID
    @ApiModelProperty(value = "说明")
    private String remarks; //说明
    @ApiModelProperty(value = "备注")
    private String note;    //备注

    @ApiModelProperty(value = "人口")
    private String totalMembers; //人口
    @ApiModelProperty(value = "最大代数")
    private String maxGeneration;    //最大代数

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

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getCreateID() {
        return createID;
    }

    public void setCreateID(Long createID) {
        this.createID = createID;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(String totalMembers) {
        this.totalMembers = totalMembers;
    }

    public String getMaxGeneration() {
        return maxGeneration;
    }

    public void setMaxGeneration(String maxGeneration) {
        this.maxGeneration = maxGeneration;
    }
}
