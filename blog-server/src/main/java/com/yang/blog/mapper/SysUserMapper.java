package com.yang.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yang.blog.entity.SysUserEntity;
import com.yang.blog.utils.RespBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface SysUserMapper extends BaseMapper {
    //查询所有用户信息
    List<SysUserEntity> findAll();
    //根据用户名查询
    SysUserEntity selectRolesByUsername(String username);
    //用户注册
    int register(SysUserEntity user);
}
