package com.yang.blog.service;

import com.yang.blog.entity.SysRoleEntity;
import com.yang.blog.utils.RespBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SysRoleService {
    //查询所有角色权限
    List<SysRoleEntity> getRoleAll();

    List<Map<String,Integer>> getUserRoleAll();

    RespBean addRole(String RoleName);

    RespBean updRole(HashMap<String,Object> roleEntities);

    RespBean delRole(String RoleId);
}
