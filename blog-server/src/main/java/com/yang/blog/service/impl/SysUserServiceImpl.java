package com.yang.blog.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yang.blog.entity.RoleListEntity;
import com.yang.blog.entity.SysUserEntity;
import com.yang.blog.mapper.SysUserMapper;
import com.yang.blog.service.SysUserService;
import com.yang.blog.utils.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.logging.Logger;

@Service
@Slf4j
public class SysUserServiceImpl implements SysUserService {
    Logger log = Logger.getLogger(String.valueOf(SysUserServiceImpl.class));
    @Autowired
    private SysUserMapper userMapper;
    @Override
    public List<SysUserEntity> findAll() {
        return userMapper.findAll();
    }

    @Override
    public SysUserEntity selectRolesByUsername(String username) {
        return userMapper.selectRolesByUsername(username);
    }

    @Override
    public RespBean register(SysUserEntity user) {
        //1、先检查用户名是否已存在
        if(!ObjectUtils.isEmpty(this.selectRolesByUsername(user.getUsername()))){
            return RespBean.warn("用户名已存在请重新输入");
        }
        //2、产生随机盐
        String ss = RandomUtil.randomString(6);
        log.info("随机字符串为==="+ss);
        user.setSalt(ss);
        Md5Hash md5Hash = new Md5Hash(user.getPassword(), ss, 1024);
        user.setPassword(md5Hash.toHex());

        int insert = userMapper.register(user);
        System.out.println(user);
        if(insert >=0){
            return RespBean.ok("用户创建成功username="+user.getUsername(),user);
        }else{
            return RespBean.error("创建失败！");
        }
    }

    @Override
    public List<RoleListEntity> findPermsListByRoleId(Long id) {
        return null;
    }


}
