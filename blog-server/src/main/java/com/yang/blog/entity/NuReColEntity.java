package com.yang.blog.entity;

import io.swagger.annotations.ApiModel;

@ApiModel(value="nutritional_recipe_collection", description="食谱收藏表")
public class NuReColEntity {
    private static final long serialVersionUID = 1L;
    private Long Id;
    private Long uID;
    private Long fID;
    private String createTime;

    public NuReColEntity(Long id, Long fID, Long uID) {
        Id = id;
        this.fID = fID;
        this.uID = uID;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public Long getuID() {
        return uID;
    }

    public void setuID(Long uID) {
        this.uID = uID;
    }

    public Long getfID() {
        return fID;
    }

    public void setfID(Long fID) {
        this.fID = fID;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
