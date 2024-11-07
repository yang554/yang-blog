package com.yang.blog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value="sys-user类", description="用户实体类")
public class SysUserEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "用户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private long id;
    @ApiModelProperty(value = "账号")
    private String username;
    @ApiModelProperty(value = "密码")
    private String password;
    @ApiModelProperty(value = "昵称")
    private String nickname;
    @ApiModelProperty(value = "头像")
    private String avatar;
    @ApiModelProperty(value = "电子邮箱")
    private String email;
    @ApiModelProperty(value = "联系电话")
    private String phone;
    @ApiModelProperty(value = "地址")
    private String address;
    @ApiModelProperty(value = "用户说明")
    private String description;
    @ApiModelProperty(value = "加密")
    private String salt;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "修改时间")
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "备注")
    private String note;
    @ApiModelProperty(value = "备用字段1")
    private String ext01;
    @ApiModelProperty(value = "备用字段2")
    private String ext02;
    //定义角色的集合
    @TableField(exist = false)
    private List<SysRoleEntity> roles;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getExt01() {
        return ext01;
    }

    public void setExt01(String etx01) {
        this.ext01 = etx01;
    }

    public String getExt02() {
        return ext02;
    }

    public void setExt02(String etx02) {
        this.ext02 = etx02;
    }

    public List<SysRoleEntity> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRoleEntity> roles) {
        this.roles = roles;
    }
}
