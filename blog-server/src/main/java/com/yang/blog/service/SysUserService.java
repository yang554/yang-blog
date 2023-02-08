package com.yang.blog.service;

import com.yang.blog.entity.RoleListEntity;
import com.yang.blog.entity.SysUserEntity;
import com.yang.blog.utils.RespBean;

import java.util.List;

public interface SysUserService {

    //查询所有用户
    List<SysUserEntity> findAll();
    //根据用户名查询
    SysUserEntity selectRolesByUsername(String username);
    //用户注册
    RespBean register(SysUserEntity user);
    //查询用户权限
    List<RoleListEntity> findPermsListByRoleId(Long id);
}
