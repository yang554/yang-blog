package com.yang.blog.mapper;

import com.yang.blog.entity.SysRoleEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SysRoleMapper {
    //查询所有角色权限
    List<SysRoleEntity> getRoleAll();

    List<Map<String,Integer>> getUserRoleAll();

    int addRole(String RoleName);

    int updRole(SysRoleEntity roleEntities);

    int delRole(String RoleId);
}
