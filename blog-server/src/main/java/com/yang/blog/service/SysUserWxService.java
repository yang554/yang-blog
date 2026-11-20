package com.yang.blog.service;

import com.yang.blog.entity.SysUserWxEntity;

public interface SysUserWxService {
    //根据用户名模糊查询
    SysUserWxEntity findByOpenid(String openid);
    //用户注册
    SysUserWxEntity register(String openid);
}
