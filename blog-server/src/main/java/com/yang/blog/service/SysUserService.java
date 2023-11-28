package com.yang.blog.service;

import com.yang.blog.entity.RoleListEntity;
import com.yang.blog.entity.SysRoleEntity;
import com.yang.blog.entity.SysUserEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;

public interface SysUserService {
    //查询用户是否存在
    boolean findUser(String name);
    //查询所有用户
    List<SysUserEntity> findAll();
    //根据用户名查询
    SysUserEntity selectRolesByUsername(String username);
    //用户注册
    RespBean register(SysUserEntity user);
    //添加用户
    RespBean addUser(SysUserEntity user);
    //查询用户权限
    List<RoleListEntity> findPermsListByRoleId(Long id);
    //查询所有权限
    List<SysRoleEntity> getRoleAll();
    //编辑用户
    RespBean editUser(HashMap<String,Object> userEntity);
    //保存头像
    RespBean saveAvatar(String avatar,String id);
    //删除用户
    RespBean delUser(String id);
    //用户分类
    RespBean getUserRoleAll();
}
