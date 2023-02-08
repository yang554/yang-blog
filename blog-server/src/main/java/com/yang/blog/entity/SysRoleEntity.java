package com.yang.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="sys-role类", description="用户权限实体类")
public class SysRoleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色id")
    @JsonSerialize(using = ToStringSerializer.class)        //序列化时转化为字符串
    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "角色名")
    private String name;

    @ApiModelProperty(value = "角色中文名")
    private String nameZh;

    //定义权限的集合
    @TableField(exist = false)
    private List<RoleListEntity> perms;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }

    public List<RoleListEntity> getPerms() {
        return perms;
    }

    public void setPerms(List<RoleListEntity> perms) {
        this.perms = perms;
    }
}
