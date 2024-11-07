package com.yang.blog.mapper;

import com.yang.blog.entity.SysUserRoleEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface SysUserRoleMapper {
    int add(SysUserRoleEntity roleListEntity);
    int upd(SysUserRoleEntity roleListEntity);
    int del(String id);
    int delUserRole(String userID);
}
