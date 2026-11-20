package com.yang.blog.mapper;

import com.yang.blog.entity.SysUserWxEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface SysUserWxMapper {
    //根据用户名模糊查询
    SysUserWxEntity findByOpenid(String openid);
    //用户注册
    int register(SysUserWxEntity user);
}
